<template>
  <div class="error-container">
    <el-result
      title="404"
      sub-title="抱歉，您访问的页面不存在"
    >
      <template #icon>
        <el-image src="@/assets/error/404.jpg" />
      </template>
      <template #extra>
        <el-button type="primary" @click="goHome">返回首页</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
const goHome = () => {
  if(localStorage.getItem('role') === 'admin'){
    router.push('/admin/analysis/dashboard')
  }else{
    router.push('/')
  }
}
</script>

<style scoped>
.error-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}
</style> 