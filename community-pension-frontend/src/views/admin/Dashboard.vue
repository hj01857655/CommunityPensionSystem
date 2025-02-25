<template>
    <div>
      <h1>管理员仪表盘</h1>
      <p>欢迎，{{ userName }}！您的角色是 {{ userRole }}</p>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  
  const userName = ref(localStorage.getItem('userName') || '管理员');
  const userRole = ref(localStorage.getItem('userRole') || 'admin');
  </script>
  
  <style scoped>
  h1 {
    color: #2c3e50;
  }
  </style>