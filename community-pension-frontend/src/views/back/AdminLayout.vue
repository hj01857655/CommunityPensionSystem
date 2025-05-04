<template>
  <el-container class="admin-layout">
    <el-aside :width="isCollapse ? '64px' : '200px'" class="sidebar-container">
      <Sidebar :is-collapse="isCollapse" />
    </el-aside>
    <el-container>
      <el-header height="60px" class="header-container">
        <Header :is-collapse="isCollapse" @toggle-sidebar="toggleSidebar" />
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
import {onMounted, ref} from 'vue';
import {useRouter} from 'vue-router';
import {useAdminStore} from '@/stores/back/adminStore';
import {useTagsViewStore} from '@/stores/tagsView';
import Sidebar from '@/components/back/layout/Sidebar.vue'; //侧边栏
import Header from '@/components/back/layout/Header.vue'; //头部
import AppMain from '@/components/back/layout/AppMain.vue';
import TagsView from '@/components/back/layout/TagsView.vue';
import {ElMessage} from 'element-plus';

const router = useRouter();
const adminStore = useAdminStore();
const tagsViewStore = useTagsViewStore();
const isCollapse = ref(false);

const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value;
};

// TagsView 相关方法
const removeTab = (view) => {
  tagsViewStore.removeVisitedView(view);
};

const closeOthersTags = (view) => {
  tagsViewStore.closeOthersTags(view);
};

const closeLeftTags = (view) => {
  tagsViewStore.closeLeftTags(view);
};

const closeRightTags = (view) => {
  tagsViewStore.closeRightTags(view);
};

const closeAllTags = () => {
  tagsViewStore.closeAllTags();
  router.push('/admin/home');
};

const refreshTag = (view) => {
  tagsViewStore.refreshTag(view);
};

const addTab = (view) => {
  tagsViewStore.addVisitedView(view);
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
        userId = parsedUserInfo?.userId;
      } catch (error) {
        console.error('解析用户信息失败:', error);
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

    // 初始化标签视图
    tagsViewStore.initHomeTab();
  } catch (error) {
    console.error('获取用户信息失败:', error);
    ElMessage.error(error.message || '获取用户信息失败，请重新登录');
    // 清除登录状态
    await adminStore.logout();
    router.push('/login');
  }
};

onMounted(() => {
  getUserInfo();
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