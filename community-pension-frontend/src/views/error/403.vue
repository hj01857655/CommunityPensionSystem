<template>
  <ErrorPage type="403" @error-click="goHome" />
</template>

<script setup>
import { useRouter } from 'vue-router'
import ErrorPage from '@/components/common/error/ErrorPage.vue'

const router = useRouter()

const goHome = () =>{
  if(localStorage.getItem('role') === 'admin'){
    router.push('/admin/home')
  }else{
    router.push('/')
  }
}
</script> 