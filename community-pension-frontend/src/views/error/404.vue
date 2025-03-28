<template>
  <ErrorPage type="404" @error-click="goHome" />
</template>

<script setup>
import { useRouter } from 'vue-router'
import ErrorPage from '@/components/common/error/ErrorPage.vue'

const router = useRouter()

const goHome = () => {
  if(localStorage.getItem('role') === 'admin'){
    router.push('/admin/home')
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