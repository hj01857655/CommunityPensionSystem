<template>
  <el-container class="admin-layout">
    <el-aside :width="isCollapse ? '64px' : '200px'" class="sidebar-container">
      <Sidebar :is-collapse="isCollapse" />
    </el-aside>
    <el-container>
      <el-header height="60px" class="header-container">
        <Header 
          :is-collapse="isCollapse" 
          @toggle-sidebar="toggleSidebar"
          @change-nav-mode="handleNavModeChange"
          @toggle-tags="handleTagsToggle"
          @toggle-fixed-header="handleFixedHeaderToggle"
        />
      </el-header>
      <div class="main-container">
        <TagsView
            :visited-views="tagsViewStore.visitedViews"
            @refresh="refreshTag"
            @remove-tab="removeTab"
            @close-others="closeOthersTags"
            @close-left="closeLeftTags"
            @close-right="closeRightTags"
            @close-all="closeAllTags"
            @add-tab="addTab"
        />
        <el-main>
          <AppMain/>
        </el-main>
      </div>
    </el-container>
  </el-container>
</template>

<script setup>
import AppMain from '@/components/back/layout/AppMain.vue';
import Header from '@/components/back/layout/Header.vue'; //头部
import Sidebar from '@/components/back/layout/Sidebar.vue'; //侧边栏
import TagsView from '@/components/back/layout/TagsView.vue';
import { useAdminStore } from '@/stores/back/adminStore';
import { useTagsViewStore } from '@/stores/back/tagsViewStore';
import { ElMessage } from 'element-plus';
import { onMounted, ref, watch } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const adminStore = useAdminStore();
const tagsViewStore = useTagsViewStore();
const isCollapse = ref(false);

// 布局相关状态
const navMode = ref(localStorage.getItem('navMode') || 'side');
const showTagsView = ref(localStorage.getItem('showTags') !== 'false');
const fixedHeader = ref(localStorage.getItem('fixedHeader') !== 'false');

const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value;
};

// 处理导航模式变更
const handleNavModeChange = (mode) => {
  navMode.value = mode;
  // 这里可以添加导航模式变更的逻辑
  ElMessage.success(`导航模式已切换为: ${mode === 'side' ? '侧边菜单模式' : '顶部菜单模式'}`);
};

// 处理标签页显示切换
const handleTagsToggle = (value) => {
  showTagsView.value = value;
  // 这里可以添加标签页显示切换的逻辑
  ElMessage.success(`标签页显示已${value ? '开启' : '关闭'}`);
};

// 处理固定Header切换
const handleFixedHeaderToggle = (value) => {
  fixedHeader.value = value;
  // 这里可以添加固定Header切换的逻辑
  document.body.classList.toggle('fixed-header', value);
  ElMessage.success(`固定Header已${value ? '开启' : '关闭'}`);
};

// 监听导航模式变化，应用相应的样式
watch(navMode, (newMode) => {
  document.body.classList.toggle('top-nav-mode', newMode === 'top');
  document.body.classList.toggle('side-nav-mode', newMode === 'side');
});

// 在组件挂载时初始化布局样式
onMounted(() => {
  // 应用导航模式样式
  document.body.classList.toggle('top-nav-mode', navMode.value === 'top');
  document.body.classList.toggle('side-nav-mode', navMode.value === 'side');
  
  // 应用固定Header样式
  document.body.classList.toggle('fixed-header', fixedHeader.value);
});

// TagsView 相关方法
const removeTab = (view) => {
  try {
    tagsViewStore.removeVisitedView(view);
  } catch (error) {
    // 错误处理
  }
};

const closeOthersTags = (view) => {
  try {
    tagsViewStore.closeOthersTags(view);
  } catch (error) {
    // 错误处理
  }
};

const closeLeftTags = (view) => {
  try {
    tagsViewStore.closeLeftTags(view);
  } catch (error) {
    // 错误处理
  }
};

const closeRightTags = (view) => {
  try {
    tagsViewStore.closeRightTags(view);
  } catch (error) {
    // 错误处理
  }
};

const closeAllTags = () => {
  try {
    tagsViewStore.closeAllTags();
    router.push('/admin/home');
  } catch (error) {
    // 错误处理
    router.push('/admin/home');
  }
};

const refreshTag = (view) => {
  try {
    tagsViewStore.refreshTag(view);
  } catch (error) {
    // 错误处理
  }
};

const addTab = (view) => {
  if (!view || typeof view !== 'object' || !view.path) {
    return;
  }
  
  // 规范化路径
  const normalizedPath = view.path.replace(/\/$/, '');
  
  // 更严格地检查是否已存在标签（考虑到路径末尾可能有斜杠的情况）
  const existingViews = tagsViewStore.visitedViews.filter(v => 
    v.path.replace(/\/$/, '') === normalizedPath ||
    v.path.replace(/\/$/, '') + '/' === normalizedPath + '/'
  );
  
  if (existingViews.length > 0) {
    // 如果发现同一路径有多个标签，清理多余的
    if (existingViews.length > 1) {
      // 保留第一个，删除其余的
      for (let i = 1; i < existingViews.length; i++) {
        tagsViewStore.removeVisitedView(existingViews[i]);
      }
    }
    return;
  }
  
  // 修正view对象的path为规范化路径
  view.path = normalizedPath;
  
  // 添加视图
  tagsViewStore.addView(view);
};

// 清理重复标签的函数
const cleanupDuplicateTags = () => {
  try {
    // 首先主动调用Store中的清理方法
    const storeCleanupCount = tagsViewStore.cleanupDuplicateTags();
    
    // 再次检查，确保不同形式的路径也被处理
    const normalizedPathMap = new Map();
    const duplicates = [];
    
    // 遍历所有标签，找出重复项
    tagsViewStore.visitedViews.forEach(tag => {
      // 规范化路径（移除末尾斜杠）
      const normalizedPath = tag.path.replace(/\/$/, '');
      
      // 检查是否已存在相同规范化路径的标签
      if (normalizedPathMap.has(normalizedPath)) {
        // 记录重复项，准备删除
        duplicates.push(tag);
      } else {
        // 记录已存在的标签路径
        normalizedPathMap.set(normalizedPath, tag);
      }
    });
    
    // 移除重复标签
    duplicates.forEach(tag => {
      tagsViewStore.removeVisitedView(tag);
    });
    
    return storeCleanupCount + duplicates.length;
  } catch (error) {
    // 错误处理
    return 0;
  }
};

const getUserInfo = async () => {
  try {
    // 检查是否已登录
    const token = sessionStorage.getItem('admin-access-token');
    if (!token) {
      ElMessage.error('请先登录');
      router.push('/login');
      return;
    }

    // 检查是否已有用户信息
    const userInfo = adminStore.userInfo;
    if (userInfo && userInfo.userId) {
      return;
    }

    // 从 sessionStorage 获取用户信息
    const userInfoStr = sessionStorage.getItem('userInfo');
    let userId;
    
    if (userInfoStr) {
      try {
        const parsedUserInfo = JSON.parse(userInfoStr);
        userId = parsedUserInfo.userId;
      } catch (e) {
        throw new Error('解析用户信息失败');
      }
    }

    if (!userId) {
      throw new Error('未找到用户信息');
    }

    // 获取用户信息
    await adminStore.getInfo(userId);
    
    // 验证获取到的用户信息
    if (!adminStore.userInfo || !adminStore.userInfo.userId) {
      throw new Error('获取用户信息失败');
    }

    // 初始化标签视图 - 使用正确的方法添加首页标签
    const homeView = {
      path: '/admin/home',
      name: 'AdminHome',
      meta: { 
        title: '首页',
        affix: true 
      }
    };
    
    // 清理可能存在的重复标签
    cleanupDuplicateTags();
    
    // 检查首页标签是否存在
    const hasHomeTag = tagsViewStore.visitedViews.some(v => 
      v.path === '/admin/home' || v.path === '/admin/home/'
    );
    
    if (!hasHomeTag) {
      tagsViewStore.addView(homeView);
    }
  } catch (error) {
    ElMessage.error(error.message || '获取用户信息失败，请重新登录');
    // 清除登录状态
    await adminStore.logout();
    router.push('/login');
  }
};

// 初始化首页标签
const initHomeTab = () => {
  // 检查是否已存在首页标签
  const homeRoute = {
    path: '/admin/home',
    meta: {
      title: '首页',
      affix: true // 固定标签不可关闭
    }
  };
  
  // 添加首页标签
  addTab(homeRoute);
  
  // 清理可能存在的重复标签
  cleanupDuplicateTags();
};

onMounted(() => {
  getUserInfo();
  // 初始化首页标签
  initHomeTab();
});
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

.sidebar-container {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
}

.header-container {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
  flex: 1;
}
</style>