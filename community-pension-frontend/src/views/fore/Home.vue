<template>
  <el-container class="home-container">
    <!-- Header -->
    <el-header class="header">
      <div class="header-content">
        <h1 class="logo">社区养老系统</h1>
        <el-menu :default-active="activeIndex" mode="horizontal" class="nav-menu" @select="handleMenuSelect">
          <el-menu-item v-for="(item, index) in menuItems" :key="item.index" :index="item.index">
            {{ item.label }}
          </el-menu-item>
        </el-menu>
        <el-dropdown @command="handleCommand" class="user-dropdown">
          <span class="user-info">
            <el-avatar :size="48" :src="avatarUrl" />
            <span class="username">{{ formData?.name || '访客' }}</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人信息</el-dropdown-item>
              <el-dropdown-item command="changePassword">修改密码</el-dropdown-item>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <!-- Main Content -->
    <el-main class="main">
      <el-breadcrumb v-if="activeIndex !== 'home'" separator="/">
        <el-breadcrumb-item>首页</el-breadcrumb-item>
        <el-breadcrumb-item>{{ breadcrumbMap[activeIndex] }}</el-breadcrumb-item>
      </el-breadcrumb>

      <!-- Dashboard -->
      <template v-if="activeIndex === 'home'">
        <DashBoard :is-logged-in="isLoggedIn" />
      </template>

      <!-- 子路由内容 -->
      <router-view v-else class="router-view">
        <template #default="{ Component }">
          <component :is="Component" :is-logged-in="isLoggedIn" />
        </template>
      </router-view>
    </el-main>

    <!-- Footer -->
    <el-footer class="footer">
      <div class="footer-content" style="justify-content: center;">
        <span>© 2025 社区养老系统</span>
        <span style="margin-left: 20px;">联系我们：1234567890</span>
      </div>
    </el-footer>
  </el-container>
</template>

<script setup>
import { ref, computed, watch, defineAsyncComponent, onMounted, onBeforeUnmount } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import DashBoard from '@/views/fore/DashBoard.vue';
import { useUserStore } from '@/stores/fore/useUserStore';
import { getAvatarUrl } from '@/utils/avatarUtils';

const router = useRouter();
const userStore = useUserStore();

// 获取登录状态
const isLoggedIn = computed(() => {
  // 从本地缓存获取
  const localLoginState = localStorage.getItem("isLoggedIn") === "true";
  // 从store获取
  const storeLoginState = userStore.isLoggedIn;
  
  // 如果本地缓存和store状态不一致，同步到store
  if (localLoginState !== storeLoginState) {
    userStore.isLoggedIn = localLoginState;
  }
  
  return localLoginState;
});

// 获取用户信息
const formData = computed(() => {
  // 从本地存储获取用户信息
  const userInfo = localStorage.getItem('userInfo');
  if (userInfo) {
    return JSON.parse(userInfo);
  }
  return {};
});

// 获取头像URL
const avatarUrl = computed(() => {
  // 从本地存储获取头像
  const avatar = formData.value?.avatar;
  if (avatar) {
    return getAvatarUrl(avatar);
  }
  return getAvatarUrl('/avatar/default.jpg');
});

// 处理来自DashBoard的菜单更新事件
const handleUpdateActiveIndex = (event) => {
  if (event.detail && event.detail.index) {
    activeIndex.value = event.detail.index;
  }
};

onMounted(() => {
  if (!isLoggedIn.value) {
    router.push('/login');
  }
  
  // 添加自定义事件监听器
  window.addEventListener('update-active-index', handleUpdateActiveIndex);
});

onBeforeUnmount(() => {
  // 移除事件监听器以防止内存泄漏
  window.removeEventListener('update-active-index', handleUpdateActiveIndex);
});

const selectedNotice = ref(null);
// 查看通知公告详情
const viewNoticeDetail = (notice) => {
  selectedNotice.value = notice;
  activeIndex.value = 'noticeDetail';
};

// 面包屑导航
const breadcrumbMap = {
  home: '首页',
  service: '服务预约',
  health: '健康档案',
  activity: '社区活动',
  notice: '通知公告',
  profile: '个人信息',
  noticeDetail: '通知详情'
};

const activeIndex = ref('home');
const menuItems = [
  { index: 'home', label: '首页' },
  { index: 'service', label: '服务预约' },
  { index: 'health', label: '健康档案' },
  { index: 'activity', label: '社区活动' },
  { index: 'notice', label: '通知公告' }
];

// 定义组件
const ServiceView = defineAsyncComponent(() => import('@/views/fore/ServiceView.vue'));
const HealthView = defineAsyncComponent(() => import('@/views/fore/HealthView.vue'));
const ActivityView = defineAsyncComponent(() => import('@/views/fore/ActivityView.vue'));
const NoticeView = defineAsyncComponent(() => import('@/views/fore/NoticeView.vue'));
const ProfileView = defineAsyncComponent(() => import('@/views/fore/ProfileView.vue'));
const NoticeDetailView = defineAsyncComponent(() => import('@/views/fore/NoticeDetailView.vue'));

// 当前组件
const currentComponent = computed(() => {
  const componentMap = {
    home: DashBoard, // 首页
    service: ServiceView, // 服务预约
    health: HealthView, // 健康档案
    activity: ActivityView, // 社区活动
    notice: NoticeView, // 通知公告
    profile: ProfileView, // 个人信息
    noticeDetail: NoticeDetailView // 通知详情
  };
  return componentMap[activeIndex.value];
});

// 菜单选择
const handleMenuSelect = (index) => {
  if (!isLoggedIn.value && index !== 'activity' && index !== 'notice') {
    ElMessage.warning('请先登录以访问此功能');
    return;
  }
  activeIndex.value = index;
  // 使用路由导航
  if(index === 'home'){
    router.push(`/home`);
  }else{
    router.push(`/home/${index}`);
  }
};

// 监听路由变化，更新activeIndex
watch(() => router.currentRoute.value.path, (newPath) => {
  const pathParts = newPath.split('/');
  if (pathParts.length >= 3) {
    activeIndex.value = pathParts[2];
  }
}, { immediate: true });

// 用户操作
const handleCommand = async (command) => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录以访问此功能');
    return;
  }
  if (command === 'profile') {
    activeIndex.value = 'profile';
    router.push('/home/profile');
  } else if (command === 'changePassword') {
    activeIndex.value = command;
  } else if (command === 'logout') {
    ElMessage.success('退出登录成功');
    localStorage.clear();
    router.push('/login');
  }
};

// 紧急呼叫
const handleEmergencyCall = () => {
  ElMessage.success(`正在拨打紧急联系电话：${emergencyPhone.value}`);
};

// 服务状态类型
const getServiceStatusType = (status) => {
  const statusMap = {
    '已预约': 'success',
    '待确认': 'warning',
    '已取消': 'info'
  };
  return statusMap[status] || 'info';
};

</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  display: flex;
  flex-direction: column;
}

.header {
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  z-index: 10;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  padding: 0 20px;
}

.logo {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.nav-menu {
  flex: 1;
  margin-left: 20px;
}

.user-dropdown {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin-left: 10px;
  font-weight: bold;
  color: #333;
}

.main {
  flex: 1;
  padding: 20px;
  background-color: #f0f2f5;
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: auto;
}

:deep(.el-main) {
  padding: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.router-view {
  flex: 1;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.footer {
  background-color: #f5f5f5;
  padding: 10px 20px;
  text-align: center;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.admin-link {
  color: #409eff;
  font-weight: bold;
}

.admin-link:hover {
  color: #66b1ff;
}
</style>