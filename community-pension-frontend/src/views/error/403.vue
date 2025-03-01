<template>
  <div class="error-container">
    <el-result
      title="403"
      sub-title="抱歉，您没有访问该页面的权限"
    >
      <template #icon>
        <el-image src="@assets/error/403.jpg" />
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
const goHome = () =>{
  if(localStorage.getItem('role') === 'admin'){
    router.push('/admin/dashboard')
  }else{
    router.push('/')
  }
}
</script> 