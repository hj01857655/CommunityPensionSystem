<template>
  <el-config-provider>
    <router-view />
  </el-config-provider>
</template>

<script setup>
import { useUserStore as backUserStore } from '@/stores/back/userStore';
import { useUserStore as foreUserStore } from '@/stores/fore/userStore';
import { computed, provide, ref, watchEffect } from 'vue';
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

// 全局深色模式变量
const isDark = ref(localStorage.getItem('fore-theme') === 'dark');

// 监听切换，自动同步 html 的 .dark class 和 localStorage
watchEffect(() => {
  if (isDark.value) {
    document.documentElement.classList.add('dark');
    localStorage.setItem('fore-theme', 'dark');
  } else {
    document.documentElement.classList.remove('dark');
    localStorage.setItem('fore-theme', 'light');
  }
});

provide('isDark', isDark);
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