<template>
  <router-view></router-view>
</template>

<script setup>
import { onMounted } from 'vue';

// 在应用启动时初始化主题
onMounted(() => {
  // 从localStorage获取主题设置
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