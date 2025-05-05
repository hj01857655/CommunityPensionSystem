<template>
  <router-view></router-view>
</template>

<script setup>
import { useUserStore as backUserStore } from '@/stores/back/userStore';
import { useUserStore as foreUserStore } from '@/stores/fore/userStore';
import { computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';

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

// 在应用启动时初始化主题
onMounted(() => {
  // 从 localStorage 获取主题设置
  const savedTheme = localStorage.getItem('fore-theme');
  if (savedTheme === 'dark') {
    document.documentElement.classList.add('dark');
  } else {
    document.documentElement.classList.remove('dark');
  }
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