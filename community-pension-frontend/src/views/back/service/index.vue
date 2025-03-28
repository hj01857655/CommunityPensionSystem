<template>
  <div class="app-container">
    <router-view v-slot="{ Component }">
      <transition name="fade-transform" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted } from 'vue';
import { useServiceStore } from '@/stores/back/service';

const serviceStore = useServiceStore();

onMounted(() => {
  // 组件挂载时可以进行一些初始化操作
});

onUnmounted(() => {
  // 组件卸载时重置store状态
  serviceStore.resetState();
});
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.5s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>