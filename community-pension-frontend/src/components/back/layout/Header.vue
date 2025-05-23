<template>
  <div class="header">
    <div class="left">
      <el-icon class="toggle-sidebar" @click="toggleSidebar">
        <component :is="isCollapse ? 'Expand' : 'Fold'"/>
      </el-icon>
      <breadcrumb/>
    </div>
    <div class="right">
      <div class="header-tools">
        <!-- 添加系统时间显示 -->
        <div class="system-time">
          <el-icon>
            <Clock/>
          </el-icon>
          <span>{{ currentTime }}</span>
        </div>

        <el-input
            v-model="searchQuery"
            class="search-input"
            placeholder="快速搜索..."
            size="small"
            @keyup.enter="performSearch"
        >
          <template #prefix>
            <el-icon>
              <Search/>
            </el-icon>
          </template>
        </el-input>
        <el-badge :value="unreadNotificationsCount" class="tool-icon" trigger="click">
          <el-dropdown placement="bottom-end" @command="handleNotificationCommand">
            <el-icon>
              <Bell/>
            </el-icon>
            <template #dropdown>
              <el-dropdown-menu class="notification-dropdown">
                <div class="dropdown-header">
                  <h3>通知中心</h3>
                  <el-button link @click="markAllNotificationsRead">全部标记已读</el-button>
                </div>
                <div v-if="unreadNotifications.length > 0" class="notification-list">
                  <el-dropdown-item v-for="notification in unreadNotifications.slice(0, 5)" :key="notification.id"
                                    :command="'view-' + notification.id" class="notification-item">
                    <div class="notification-title">{{ notification.title }}</div>
                    <div class="notification-content">{{ notification.content }}</div>
                    <div class="notification-time">{{ notification.time }}</div>
                  </el-dropdown-item>
                </div>
                <div v-else class="no-notification">
                  <el-empty :image-size="80" description="暂无未读通知"></el-empty>
                </div>
                <el-dropdown-item class="view-all" command="view-all-notifications">
                  查看全部通知
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </el-badge>
        <el-badge :value="unreadMessagesCount" class="tool-icon" trigger="click">
          <el-dropdown placement="bottom-end" @command="handleMessageCommand">
            <el-icon>
              <Message/>
            </el-icon>
            <template #dropdown>
              <el-dropdown-menu class="message-dropdown">
                <div class="dropdown-header">
                  <h3>消息中心</h3>
                  <el-button link @click="markAllMessagesRead">全部标记已读</el-button>
                </div>
                <div v-if="unreadMessages.length > 0" class="message-list">
                  <el-dropdown-item v-for="message in unreadMessages.slice(0, 5)" :key="message.id"
                                    :command="'view-msg-' + message.id" class="message-item">
                    <el-avatar :size="30" :src="message.avatar" class="message-avatar"/>
                    <div class="message-info">
                      <div class="message-sender">{{ message.sender }}</div>
                      <div class="message-content">{{ message.content }}</div>
                      <div class="message-time">{{ message.time }}</div>
                    </div>
                  </el-dropdown-item>
                </div>
                <div v-else class="no-message">
                  <el-empty :image-size="80" description="暂无未读消息"></el-empty>
                </div>
                <el-dropdown-item class="view-all" command="view-all-messages">
                  查看全部消息
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </el-badge>
        <el-icon class="tool-icon" @click="toggleFullscreen">
          <component :is="isFullscreen ? 'Close' : 'FullScreen'"/>
        </el-icon>
        <!-- 全局配置按钮 -->
        <el-icon class="tool-icon" @click="openSettingDrawer">
          <Setting/>
        </el-icon>
      </div>
      <el-dropdown trigger="click" @command="handleCommand">
        <span class="user-info">
          <el-avatar :size="32" :src="getAvatarUrl(userInfo.avatar)"/>
          <span class="username">{{ userInfo.username || userInfo.userName }}</span>
          <el-icon><ArrowDown/></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">个人信息</el-dropdown-item>
            <el-dropdown-item command="password">修改密码</el-dropdown-item>
            <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
  <!-- 全局设置抽屉 -->
  <el-drawer
      v-model="settingDrawerVisible"
      :before-close="handleCloseSettingDrawer"
      :destroy-on-close="false"
      direction="rtl"
      size="300px"
      title="系统设置"
  >
    <el-tabs v-model="activeSettingTab" class="setting-tabs">
      <el-tab-pane label="主题设置" name="theme">
        <div class="setting-section">
          <h3 class="setting-title">主题模式</h3>
          <div class="theme-switch">
            <div
                :class="{ active: currentTheme === 'light' }"
                class="theme-item"
                @click="toggleTheme"
            >
              <el-icon>
                <Sunny/>
              </el-icon>
              <span>浅色模式</span>
            </div>
            <div
                :class="{ active: currentTheme === 'dark' }"
                class="theme-item"
                @click="toggleTheme"
            >
              <el-icon>
                <Moon/>
              </el-icon>
              <span>深色模式</span>
            </div>
          </div>
        </div>

        <div class="setting-section">
          <h3 class="setting-title">系统主题色</h3>
          <div class="theme-colors">
            <div
                v-for="color in themeColors"
                :key="color.value"
                :class="{ active: currentPrimaryColor === color.value }"
                :style="{ backgroundColor: color.value }"
                class="color-item"
                @click="changePrimaryColor(color.value)"
            ></div>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="界面设置" name="interface">
        <div class="setting-section">
          <h3 class="setting-title">导航模式</h3>
          <div class="nav-mode-options">
            <el-radio-group v-model="navMode" @change="changeNavMode">
              <el-radio :value="'side'">侧边菜单模式</el-radio>
              <el-radio :value="'top'">顶部菜单模式</el-radio>
            </el-radio-group>
          </div>
        </div>

        <div class="setting-section">
          <h3 class="setting-title">系统布局</h3>
          <div class="layout-item">
            <span>开启标签页</span>
            <el-switch v-model="showTags" @change="toggleTags"/>
          </div>
          <div class="layout-item">
            <span>固定Header</span>
            <el-switch v-model="fixedHeader" @change="toggleFixedHeader"/>
          </div>
          <div class="layout-item">
            <span>侧边菜单折叠</span>
            <el-switch v-model="sidebarCollapsed" @change="toggleSidebarCollapse"/>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="通知设置" name="notification">
        <div class="setting-section">
          <h3 class="setting-title">通知提醒</h3>
          <div class="layout-item">
            <span>系统通知</span>
            <el-switch v-model="systemNotification" @change="toggleSystemNotification"/>
          </div>
          <div class="layout-item">
            <span>消息提醒</span>
            <el-switch v-model="messageNotification" @change="toggleMessageNotification"/>
          </div>
          <div class="layout-item">
            <span>声音提醒</span>
            <el-switch v-model="soundNotification" @change="toggleSoundNotification"/>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <div class="setting-footer">
      <el-button type="primary" @click="saveSettings">保存设置</el-button>
      <el-button @click="resetSettings">恢复默认</el-button>
    </div>
  </el-drawer>
</template>

<script setup>
import {computed, onMounted, onUnmounted, ref, watch} from 'vue';
import {useRouter} from 'vue-router';
import {useNotificationStore} from '@/stores/back/notificationStore';
import {useMessageStore} from '@/stores/back/messageStore';
import {useAdminStore} from '@/stores/back/adminStore';
import Breadcrumb from './Breadcrumb.vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import {getAvatarUrl} from '@/utils/avatarUtils';
import {ArrowDown, Bell, Clock, Message, Moon, Search, Setting, Sunny} from '@element-plus/icons-vue';

const props = defineProps({
  isCollapse: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['toggle-sidebar', 'change-nav-mode', 'toggle-tags', 'toggle-fixed-header']);
const router = useRouter();
const adminStore = useAdminStore();

const userInfo = computed(() => adminStore.userInfo || {});

// 新增功能相关数据
const searchQuery = ref('');
// 主题状态 - 从localStorage读取，默认为light
const currentTheme = ref(localStorage.getItem('theme') || 'light');
const isFullscreen = ref(false); // 全屏状态

// 当前时间
const currentTime = ref(formatDateTime(new Date()));

// 每秒更新时间
const timer = setInterval(() => {
  currentTime.value = formatDateTime(new Date());
}, 1000);

onUnmounted(() => {
  // 清除计时器，防止内存泄漏
  clearInterval(timer);
});

// 格式化日期时间
function formatDateTime(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');
  const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
  const weekDay = weekDays[date.getDay()];

  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds} ${weekDay}`;
}

// 使用通知中心和消息中心的Store
const notificationStore = useNotificationStore();
const messageStore = useMessageStore();

// 获取通知中心数据
const unreadNotifications = computed(() => notificationStore.notifications.filter(notification => !notification.read));
const unreadNotificationsCount = computed(() => notificationStore.unreadCount);

// 获取消息中心数据
const unreadMessages = computed(() => messageStore.messages.filter(message => !message.read));
const unreadMessagesCount = computed(() => messageStore.unreadCount);

const toggleSidebar = () => {
  emit('toggle-sidebar');
};

// 搜索功能
const performSearch = () => {
  if (searchQuery.value.trim()) {
    ElMessage.info(`搜索: ${searchQuery.value}`);
    // 这里可以添加实际的搜索逻辑，比如跳转到搜索结果页面
    router.push({path: '/admin/search', query: {q: searchQuery.value}});
  }
};

// 通知中心功能
const handleNotificationCommand = (command) => {
  // 先检查是否是查看全部通知的命令
  if (command === 'all' || command === 'view-all-notifications') {
    router.push('/admin/notifications');
    return;
  }
  
  // 处理查看单条通知的命令
  if (command.startsWith('view-')) {
    // 提取ID部分，忽略 'view-' 前缀
    const parts = command.split('-');
    // 如果是 view-123 格式，那么ID在索引位置1
    // 如果是 view-notification-123 格式，那么ID在索引位置2
    const idStr = parts.length > 2 ? parts[2] : parts[1];
    const id = parseInt(idStr);
    
    // 确保 id 是有效数字
    if (!isNaN(id)) {
      notificationStore.markAsRead(id);
      // 跳转到通知详情页
      router.push(`/admin/notifications/${id}`);
    } else {
      console.error('无效的通知 ID:', idStr);
    }
  }
};

const markAllNotificationsRead = () => {
  notificationStore.markAllAsRead();
};

// 消息中心功能
const handleMessageCommand = (command) => {
  // 先检查是否是查看全部消息的命令
  if (command === 'all' || command === 'view-all-messages') {
    router.push('/admin/messages');
    return;
  }
  
  // 处理查看单条消息的命令
  if (command.startsWith('view-')) {
    // 提取ID部分，忽略 'view-' 前缀
    const parts = command.split('-');
    // 如果是 view-123 格式，那么ID在索引位置1
    // 如果是 view-msg-123 格式，那么ID在索引位置2
    const idStr = parts.length > 2 ? parts[2] : parts[1];
    const id = parseInt(idStr);
    
    // 确保 id 是有效数字
    if (!isNaN(id)) {
      messageStore.markAsRead(id);
      // 跳转到消息详情页
      router.push(`/admin/messages/${id}`);
    } else {
      console.error('无效的消息 ID:', idStr);
    }
  }
};

const markAllMessagesRead = () => {
  messageStore.markAllAsRead();
};

// 主题切换
const toggleTheme = () => {
  // 切换主题
  currentTheme.value = currentTheme.value === 'light' ? 'dark' : 'light';
  
  // 更新DOM
  document.documentElement.setAttribute('data-theme', currentTheme.value);
  
  // 应用Element Plus暗黑模式
  if (currentTheme.value === 'dark') {
    document.documentElement.classList.add('dark');
  } else {
    document.documentElement.classList.remove('dark');
  }
  
  // 保存到localStorage
  localStorage.setItem('theme', currentTheme.value);
  
  ElMessage.success(`已切换到${currentTheme.value === 'dark' ? '暗黑' : '明亮'}模式`);
};

// 全屏切换
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen().then(() => {
      isFullscreen.value = true;
    }).catch(err => {
      ElMessage.error(`进入全屏失败: ${err.message}`);
    });
  } else {
    if (document.exitFullscreen) {
      document.exitFullscreen().then(() => {
        isFullscreen.value = false;
      }).catch(err => {
        ElMessage.error(`退出全屏失败: ${err.message}`);
      });
    }
  }
};

// 全局设置抽屉相关
const settingDrawerVisible = ref(false);
const activeSettingTab = ref('theme');

// 主题设置
const currentPrimaryColor = ref(localStorage.getItem('primaryColor') || '#409EFF');
const themeColors = [
  {name: '拂晓蓝', value: '#409EFF'},
  {name: '薄暮红', value: '#F56C6C'},
  {name: '葱茏绿', value: '#67C23A'},
  {name: '金盏花黄', value: '#E6A23C'},
  {name: '酱紫', value: '#909399'},
  {name: '明青', value: '#13C2C2'}
];

// 界面设置
const navMode = ref(localStorage.getItem('navMode') || 'side');
const showTags = ref(localStorage.getItem('showTags') !== 'false');
const fixedHeader = ref(localStorage.getItem('fixedHeader') !== 'false');
const sidebarCollapsed = ref(props.isCollapse); // 使用本地变量跟踪侧边栏折叠状态

// 监听props.isCollapse的变化，同步更新本地变量
watch(() => props.isCollapse, (newValue) => {
  sidebarCollapsed.value = newValue;
});

// 通知设置
const systemNotification = ref(localStorage.getItem('systemNotification') !== 'false');
const messageNotification = ref(localStorage.getItem('messageNotification') !== 'false');
const soundNotification = ref(localStorage.getItem('soundNotification') !== 'false');

// 打开设置抽屉
const openSettingDrawer = () => {
  settingDrawerVisible.value = true;
};

// 关闭设置抽屉
const handleCloseSettingDrawer = () => {
  settingDrawerVisible.value = false;
};

// 更改主题色
const changePrimaryColor = (color) => {
  currentPrimaryColor.value = color;
  document.documentElement.style.setProperty('--el-color-primary', color);
  localStorage.setItem('primaryColor', color);
};

// 更改导航模式
const changeNavMode = (mode) => {
  navMode.value = mode;
  localStorage.setItem('navMode', mode);
  // 这里需要通知父组件更改导航模式
  emit('change-nav-mode', mode);
};

// 切换标签页显示
const toggleTags = (value) => {
  showTags.value = value;
  localStorage.setItem('showTags', value);
  // 这里需要通知父组件更改标签页显示
  emit('toggle-tags', value);
};

// 切换固定Header
const toggleFixedHeader = (value) => {
  fixedHeader.value = value;
  localStorage.setItem('fixedHeader', value);
  // 这里需要通知父组件更改Header固定状态
  emit('toggle-fixed-header', value);
};

// 切换侧边菜单折叠
const toggleSidebarCollapse = (value) => {
  sidebarCollapsed.value = value;
  // 通知父组件
  emit('toggle-sidebar');
};

// 切换系统通知
const toggleSystemNotification = (value) => {
  systemNotification.value = value;
  localStorage.setItem('systemNotification', value);
};

// 切换消息提醒
const toggleMessageNotification = (value) => {
  messageNotification.value = value;
  localStorage.setItem('messageNotification', value);
};

// 切换声音提醒
const toggleSoundNotification = (value) => {
  soundNotification.value = value;
  localStorage.setItem('soundNotification', value);
};

// 保存设置
const saveSettings = () => {
  ElMessage.success('设置保存成功');
  settingDrawerVisible.value = false;
};

// 重置设置
const resetSettings = () => {
  // 重置主题设置
  currentTheme.value = 'light';
  currentPrimaryColor.value = '#409EFF';
  document.documentElement.setAttribute('data-theme', 'light');
  document.documentElement.style.setProperty('--el-color-primary', '#409EFF');

  // 重置界面设置
  navMode.value = 'side';
  showTags.value = true;
  fixedHeader.value = true;
  sidebarCollapsed.value = false;

  // 重置通知设置
  systemNotification.value = true;
  messageNotification.value = true;
  soundNotification.value = false;

  // 保存到localStorage
  localStorage.setItem('theme', 'light');
  localStorage.setItem('primaryColor', '#409EFF');
  localStorage.setItem('navMode', 'side');
  localStorage.setItem('showTags', 'true');
  localStorage.setItem('fixedHeader', 'true');
  localStorage.setItem('systemNotification', 'true');
  localStorage.setItem('messageNotification', 'true');
  localStorage.setItem('soundNotification', 'false');

  // 通知父组件
  emit('change-nav-mode', 'side');
  emit('toggle-tags', true);
  emit('toggle-fixed-header', true);
  emit('toggle-sidebar');

  ElMessage.success('已恢复默认设置');
};

// 在组件挂载时初始化主题
onMounted(() => {
  // 初始化主题
  document.documentElement.setAttribute('data-theme', currentTheme.value);
  
  // 初始化通知和消息数据
  notificationStore.fetchNotifications();
  messageStore.fetchMessages();
  document.documentElement.style.setProperty('--el-color-primary', currentPrimaryColor.value);
  
  // 应用Element Plus暗黑模式
  if (currentTheme.value === 'dark') {
    document.documentElement.classList.add('dark');
  } else {
    document.documentElement.classList.remove('dark');
  }
});

const handleGlobalConfig = (command) => {
  switch (command) {
    case 'system':
      // 跳转到系统设置页面
      router.push({path: '/admin/system'});
      break;
    case 'notification':
      // 跳转到通知设置页面
      router.push({path: '/admin/notification'});
      break;
    case 'appearance':
      // 跳转到界面设置页面
      router.push({path: '/admin/appearance'});
      break;
    case 'security':
      // 跳转到安全设置页面
      router.push({path: '/admin/security'});
      break;
  }
};

const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      // 使用完整路径并避免使用重定向路由
      if (router.currentRoute.value.path !== '/admin/system/user/profile' ||
          router.currentRoute.value.query.tab !== 'info') {
        router.push({
          path: '/admin/system/user/profile',
          query: {tab: 'info'},
          replace: true // 使用replace避免在历史记录中创建新条目
        });
      }
      break;
    case 'password':
      // 使用完整路径并避免使用重定向路由
      if (router.currentRoute.value.path !== '/admin/system/user/profile' ||
          router.currentRoute.value.query.tab !== 'password') {
        router.push({
          path: '/admin/system/user/profile',
          query: {tab: 'password'},
          replace: true // 使用replace避免在历史记录中创建新条目
        });
      }
      break;
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });
        await adminStore.logout();
        router.push('/login');
      } catch (error) {
        // 用户取消操作
      }
      break;
  }
};
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  padding: 0 20px;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.left {
  display: flex;
  align-items: center;
}

.toggle-sidebar {
  font-size: 20px;
  cursor: pointer;
  margin-right: 16px;
}

.right {
  display: flex;
  align-items: center;
}

.header-tools {
  display: flex;
  align-items: center;
  margin-right: 20px;
}

.system-time {
  font-size: 14px;
  margin-right: 16px;
  display: flex;
  align-items: center;
}

.search-input {
  width: 200px;
  margin-right: 16px;
}

.tool-icon {
  font-size: 20px;
  margin: 0 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin: 0 10px;
}

/* 通知和消息下拉样式 */
.notification-dropdown, .message-dropdown {
  width: 360px;
  max-height: 500px;
  overflow-y: auto;
  padding: 0;
}

.dropdown-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #ebeef5;
}

.dropdown-header h3 {
  margin: 0;
  font-size: 16px;
}

.notification-list, .message-list {
  max-height: 400px;
  overflow-y: auto;
}

.notification-item, .message-item {
  padding: 12px 16px;
  border-bottom: 1px solid #ebeef5;
}

.notification-item:hover, .message-item:hover {
  background-color: #f5f7fa;
}

.notification-title {
  font-weight: 500;
  margin-bottom: 4px;
}

.notification-content, .message-content {
  font-size: 13px;
  color: #606266;
  margin-bottom: 4px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notification-time, .message-time {
  font-size: 12px;
  color: #909399;
}

.message-item {
  display: flex;
  align-items: flex-start;
}

.message-avatar {
  margin-right: 12px;
}

.message-info {
  flex: 1;
  overflow: hidden;
}

.message-sender {
  font-weight: 500;
  margin-bottom: 4px;
}

.no-notification, .no-message {
  padding: 20px;
  text-align: center;
}

.view-all {
  text-align: center;
  padding: 12px 16px;
  border-top: 1px solid #ebeef5;
  font-weight: 500;
  color: #409EFF;
}

/* 全局设置抽屉样式 */
.setting-tabs {
  padding: 0 20px;
}

.setting-section {
  padding: 20px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  margin-bottom: 10px;
}

.setting-title {
  font-size: 16px;
  margin-bottom: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.theme-switch {
  display: flex;
  align-items: center;
  justify-content: space-around;
  margin-bottom: 20px;
}

.theme-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 12px;
  width: 100px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid var(--el-border-color-lighter);
}

.theme-item:hover {
  transform: scale(1.05);
}

.theme-item.active {
  color: var(--el-color-primary);
  border-color: var(--el-color-primary);
  background-color: var(--el-color-primary-light-9);
}

.theme-item .el-icon {
  font-size: 24px;
  margin-bottom: 8px;
}

.theme-colors {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  margin-bottom: 20px;
}

.color-item {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  margin: 0 10px 10px 0;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.color-item:hover {
  transform: scale(1.1);
}

.color-item.active {
  border: 2px solid #fff;
  box-shadow: 0 0 0 2px var(--el-color-primary);
}

.nav-mode-options {
  margin-bottom: 20px;
}

.layout-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px dashed var(--el-border-color-lighter);
}

.layout-item:last-child {
  border-bottom: none;
}

.layout-item span {
  color: var(--el-text-color-regular);
}

.setting-footer {
  padding: 20px;
  border-top: 1px solid var(--el-border-color-lighter);
  text-align: center;
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: var(--el-bg-color);
}
</style>