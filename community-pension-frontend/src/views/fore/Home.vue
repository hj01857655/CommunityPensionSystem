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

      <el-row :gutter="20">
        <!-- Dashboard -->
        <template v-if="activeIndex === 'home'">
          <DashBoard :is-logged-in="isLoggedIn" />
        </template>

        <!-- 其他组件 -->
        <component :is="currentComponent" v-if="activeIndex !== 'home'" v-model:active-index="activeIndex" />
      </el-row>
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
import { ref, computed, watch, defineAsyncComponent, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import DashBoard from '@/views/fore/DashBoard.vue';
import { useUserStore } from '@/stores/fore/useUserStore';
const userStore = useUserStore();
const router = useRouter();
const formData = ref({});
const isLoggedIn = ref(false);

// 使用store中的avatarUrl
const avatarUrl = computed(() => userStore.avatarUrl);

onMounted(() => {
  if (!userStore.isLoggedIn || !localStorage.getItem("isLoggedIn")) {
    router.push('/login');
  } else {
    formData.value = userStore.userInfo;
    isLoggedIn.value = true;
  }
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
};

// 用户操作
const handleCommand = async (command) => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录以访问此功能');
    return;
  }
  if (command === 'profile' || command === 'changePassword') {
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