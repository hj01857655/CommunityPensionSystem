<template>
  <el-container class="home-container">
    <!-- Header -->
    <el-header class="header">
      <div class="header-content">
        <h1 class="logo">社区养老系统前台门户</h1>
        <el-menu :default-active="activeIndex" mode="horizontal" class="nav-menu" @select="handleMenuSelect">
          <el-menu-item v-for="(item, index) in menuItems" :key="item.index" :index="item.index">
            {{ item.label }}
          </el-menu-item>
        </el-menu>

        <!-- 搜索框 -->
        <div class="search-box">
          <el-input
            v-model="searchQuery"
            placeholder="搜索服务、活动、通知等"
            class="global-search"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>

        <!-- 通知组件 -->
        <el-popover
          ref="notificationPopoverRef"
          v-model:visible="notificationPopoverVisible"
          placement="bottom"
          :width="400"
          trigger="click"
          class="notification-popover"
        >
          <template #reference>
            <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge">
              <el-icon class="notification-icon"><Bell /></el-icon>
            </el-badge>
          </template>

          <div class="notification-container">
            <div class="notification-header">
              <span>通知中心</span>
              <el-button link @click="markAllAsRead">全部已读</el-button>
            </div>

            <el-tabs v-model="activeNotificationTab">
              <el-tab-pane label="全部" name="all">
                <div class="notification-list">
                  <div v-for="notice in notifications" :key="notice.id"
                       class="notification-item"
                    :class="{ 'unread': !notice.read }"
                    @click="handleNotificationClick(notice)">
                    <div class="notification-content">
                      <div class="notification-title">{{ notice.title }}</div>
                      <div class="notification-message">{{ notice.content || notice.message }}</div>
                    </div>
                    <div class="notification-time">{{ formatDateTime(notice.publishTime || notice.createTime) }}</div>
                  </div>
                </div>
              </el-tab-pane>
              <el-tab-pane label="未读" name="unread">
                <div class="notification-list">
                  <div v-for="notice in unreadNotifications" :key="notice.id"
                    class="notification-item"
                    @click="handleNotificationClick(notice)">
                    <div class="notification-content">
                      <div class="notification-title">{{ notice.title }}</div>
                      <div class="notification-message">{{ notice.content || notice.message }}</div>
                    </div>
                    <div class="notification-time">{{ formatDateTime(notice.publishTime || notice.createTime) }}</div>
                  </div>
                </div>
              </el-tab-pane>
            </el-tabs>

            <div class="notification-footer">
              <el-button link @click="handleViewAllNotices">查看全部通知</el-button>
            </div>
          </div>
        </el-popover>

        <el-dropdown @command="handleCommand" class="user-dropdown">
          <span class="user-info">
            <el-avatar :size="48" :src="avatarUrl" />
            <span class="username">{{ formData?.name || '访客' }}</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人信息</el-dropdown-item>
              <el-dropdown-item command="changePassword">修改密码</el-dropdown-item>

              <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
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
import { getNoticeList, markNoticeAsRead } from '@/api/fore/notice';
import { useNoticeStore } from '@/stores/fore/noticeStore';
import { useUserStore } from '@/stores/fore/userStore';
import { getAvatarUrl } from '@/utils/avatarUtils';
import { formatDateTime } from '@/utils/date';
import DashBoard from '@/views/fore/DashBoard.vue';
import { Bell, Search } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { computed, defineAsyncComponent, onBeforeUnmount, onMounted, ref, watch } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const userStore = useUserStore();
const noticeStore = useNoticeStore();

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

  // 检查本地存储中的主题设置并应用
  const savedTheme = localStorage.getItem('fore-theme')
  if (savedTheme === 'dark' && !document.documentElement.classList.contains('dark')) {
    document.documentElement.classList.add('dark')
  } else if (savedTheme !== 'dark' && document.documentElement.classList.contains('dark')) {
    document.documentElement.classList.remove('dark')
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
  { index: 'activity', label: '社区活动' }
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
const handleMenuSelect = async (index) => {
  if (!isLoggedIn.value && index !== 'activity' && index !== 'notice') {
    ElMessage.warning('请先登录以访问此功能');
    return;
  }

  console.log('[导航] 菜单选择:', index);
  activeIndex.value = index;

  // 如果是活动页面，先发送活动数据重置事件
  if(index === 'activity') {
    console.log('[导航] 发送活动数据重置事件');
    // 重置活动视图的数据加载状态
    window.dispatchEvent(new CustomEvent('activity-data-reset'));
  }

  // 使用路由导航
  if(index === 'home'){
    await router.push(`/home`);
  }else{
    await router.push(`/home/${index}`);
  }

  // 如果是社区活动页面，确保等待路由更新后触发数据刷新
  if(index === 'activity') {
    console.log('[导航] 等待路由更新...');

    // 使用延迟确保组件已加载
    setTimeout(() => {
      console.log('[导航] 发送活动数据刷新事件');
      window.dispatchEvent(new CustomEvent('refresh-activity-data', {
        detail: {forceRefresh: true, source: 'navigation'}
      }));
    }, 300); // 延长延迟以确保组件完全挂载
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
  switch (command) {
    case 'profile':
      activeIndex.value = 'profile';
      router.push('/home/profile');
      break;
    case 'changePassword':
      activeIndex.value = command;
      break;
    case 'logout':
      ElMessage.success('退出登录成功');
      localStorage.clear();
      router.push('/login');
      break;
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

// 通知相关
const activeNotificationTab = ref('all')
const notifications = ref([])
const unreadCount = computed(() => {
  return notifications.value.filter(notice => !notice.read).length
})

const unreadNotifications = computed(() => {
  return notifications.value.filter(notice => !notice.read)
})

// 获取通知列表
const fetchNotifications = async () => {
  try {
    // 调用后端API获取通知列表
    const res = await getNoticeList({ current: 1, size: 10 });
    if (res.code === 200) {
      notifications.value = res.data.records || [];
    } else {
      console.error('获取通知列表失败:', res.message);
      notifications.value = []; // 清空通知列表
    }
  } catch (error) {
    console.error('获取通知列表失败:', error);
    notifications.value = []; // 清空通知列表
  }
};

// 标记通知为已读
const markAsRead = async (noticeId) => {
  try {
    await markNoticeAsRead(noticeId);
    const notice = notifications.value.find(n => n.id === noticeId);
    if (notice) {
      notice.read = true;
    }
  } catch (error) {
    console.error('标记通知已读失败:', error);
  }
};

// 标记所有通知为已读
const markAllAsRead = async () => {
  try {
    notifications.value.forEach(notice => {
      notice.read = true
    })
  } catch (error) {
    console.error('标记所有通知已读失败:', error)
  }
}

// 通知弹窗的ref
const notificationPopoverRef = ref(null);
// 控制通知弹窗显示状态
const notificationPopoverVisible = ref(false);

// 处理通知点击
const handleNotificationClick = async (notice) => {
  if (!notice.read) {
    await markAsRead(notice.id);
  }
  
  // 关闭弹窗
  notificationPopoverVisible.value = false;
  
  // 导航到通知详情页面
  router.push({
    name: 'NoticeDetailView',
    params: { id: notice.id }
  });
};

// 查看全部通知
const handleViewAllNotices = () => {
  // 关闭弹窗
  notificationPopoverVisible.value = false;
  
  // 导航到通知列表页面
  router.push('/home/notice');
};

// 搜索相关
const searchQuery = ref('')
const handleSearch = () => {
  if (!searchQuery.value.trim()) return
  // TODO: 实现全局搜索
  console.log('搜索关键词:', searchQuery.value)
}
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

.notification-badge {
  cursor: pointer;
  margin-right: 20px;
}

.notification-icon {
  font-size: 20px;
  color: #606266;
}

.notification-container {
  padding: 0;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  border-bottom: 1px solid #EBEEF5;
}

.notification-list {
  max-height: 400px;
  overflow-y: auto;
}

.notification-item {
  padding: 12px 15px;
  border-bottom: 1px solid #EBEEF5;
  cursor: pointer;
  transition: background-color 0.3s;
}

.notification-item:hover {
  background-color: #F5F7FA;
}

.notification-item.unread {
  background-color: #F0F9FF;
}

.notification-content {
  margin-bottom: 5px;
}

.notification-title {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.notification-message {
  color: #606266;
  font-size: 13px;
}

.notification-time {
  font-size: 12px;
  color: #909399;
}

.notification-footer {
  padding: 10px 15px;
  text-align: center;
  border-top: 1px solid #EBEEF5;
}

.search-box {
  margin: 0 20px;
  width: 250px;
}

.global-search {
  width: 100%;
}

.tool-group {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-right: 20px;
}

.tool-icon {
  font-size: 20px;
  color: #606266;
  cursor: pointer;
  transition: color 0.3s;
}

.tool-icon:hover {
  color: #409eff;
}

.help-menu {
  padding: 10px 0;
}

.help-item {
  display: flex;
  align-items: center;
  padding: 10px 15px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.help-item:hover {
  background-color: #F5F7FA;
}

.help-item .el-icon {
  margin-right: 10px;
  font-size: 16px;
}

@media (max-width: 768px) {
  :deep(.el-breadcrumb__item) {
    font-size: 12px;
  }
}

:deep(.el-avatar) {
  border: 1px solid #EBEEF5;
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
}

:deep(.el-dropdown-menu__item .el-icon) {
  font-size: 16px;
  margin-right: 0;
}
</style>