<template>
  <router-view></router-view>
</template>

<script setup>
import {computed, onMounted, onUnmounted, watch} from 'vue';
import {useRoute} from 'vue-router';
import {useUserStore as foreUserStore} from '@/stores/fore/userStore';
import {useUserStore as backUserStore} from '@/stores/back/userStore';
import {sseClient} from '@/utils/sseClient';

const route = useRoute();

// 判断当前是否为后台路由
const isBackend = computed(() => route.path.startsWith('/admin'));

// 获取当前应该使用的 store
const currentUserStore = computed(() => {
  if (isBackend.value) {
    return backUserStore;
  } else {
    // 默认使用前台 store
    return foreUserStore;
  }
});

// 初始化 SSE 客户端
sseClient.initStore(currentUserStore);

// 监听用户登录状态变化
watch(() => {
  // 直接从本地存储获取登录状态
  const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
  console.log('App - 检查登录状态:', {isLoggedIn});
  return isLoggedIn;
}, (isLoggedIn) => {
  if (isLoggedIn) {
    // 直接从本地存储获取所有认证信息
    const accessToken = localStorage.getItem('user-access-token');
    const refreshToken = localStorage.getItem('user-refresh-token');
    const userInfoStr = localStorage.getItem('userInfo');

    console.log('App - 本地存储状态:', {
      isLoggedIn,
      hasAccessToken: !!accessToken,
      hasRefreshToken: !!refreshToken,
      userInfoStr
    });

    let localUserInfo;
    try {
      localUserInfo = JSON.parse(userInfoStr || '{}');
      console.log('App - 解析后的用户信息:', localUserInfo);
    } catch (error) {
      console.error('App - 解析userInfo失败:', error);
      localUserInfo = {};
    }

    // 尝试从不同字段获取用户ID
    const userId = localUserInfo?.userId || localUserInfo?.id;
    console.log('App - 用户ID:', userId);

    if (!accessToken) {
      console.warn('App - 无法建立SSE连接：访问令牌不存在');
      return;
    }

    if (!refreshToken) {
      console.warn('App - 无法建立SSE连接：刷新令牌不存在');
      return;
    }

    if (!userId) {
      console.warn('App - 无法建立SSE连接：无法获取用户ID');
      return;
    }

    console.log(`App - 开始连接SSE，当前模式: ${isBackend.value ? '后台' : '前台'}`);
    const clientType = isBackend.value ? 'admin' : 'user';
    sseClient.connect(clientType);
  } else {
    console.log('App - 用户未登录，断开SSE连接');
    sseClient.disconnect();
  }
}, {immediate: true});

// 监听路由变化，处理前后台切换
watch(() => route.path, (newPath) => {
  console.log(`App - 路由变化: ${newPath}, 当前模式: ${isBackend.value ? '后台' : '前台'}`);

  // 直接从本地存储获取所有认证信息
  const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
  const accessToken = localStorage.getItem('user-access-token');
  const refreshToken = localStorage.getItem('user-refresh-token');
  const userInfoStr = localStorage.getItem('userInfo');

  console.log('App - 路由变化时的本地存储状态:', {
    isLoggedIn,
    hasAccessToken: !!accessToken,
    hasRefreshToken: !!refreshToken,
    userInfoStr
  });

  let localUserInfo;
  try {
    localUserInfo = JSON.parse(userInfoStr || '{}');
    console.log('App - 路由变化时的用户信息:', localUserInfo);
  } catch (error) {
    console.error('App - 解析userInfo失败:', error);
    localUserInfo = {};
  }

  // 尝试从不同字段获取用户ID
  const userId = localUserInfo?.userId || localUserInfo?.id;
  console.log('App - 路由变化时的用户ID:', userId);

  if (!isLoggedIn) {
    console.log('App - 路由变化：用户未登录');
    return;
  }

  if (!accessToken) {
    console.warn('App - 路由变化：访问令牌不存在');
    return;
  }

  if (!refreshToken) {
    console.warn('App - 路由变化：刷新令牌不存在');
    return;
  }

  if (!userId) {
    console.warn('App - 路由变化：无法获取用户ID');
    return;
  }

  if (!sseClient.isConnected()) {
    console.log('App - 路由变化：SSE未连接，尝试重新连接');
    const clientType = isBackend.value ? 'admin' : 'user';
    sseClient.connect(clientType);
  }
});

// 组件卸载时断开 SSE 连接
onUnmounted(() => {
  console.log('App组件卸载，断开SSE连接');
  sseClient.disconnect();
});

// 在应用启动时初始化主题
onMounted(() => {
  // 从 localStorage 获取主题设置
  const savedTheme = localStorage.getItem('fore-theme');
  if (savedTheme === 'dark') {
    document.documentElement.classList.add('dark');
  } else {
    document.documentElement.classList.remove('dark');
  }

  console.log(`应用启动，当前模式: ${isBackend.value ? '后台' : '前台'}`);
});
</script>

<style>
#app {
  font-family: Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

/* 暗色主题基础样式 */
:root.dark #app {
  color: #fff;
}
</style>