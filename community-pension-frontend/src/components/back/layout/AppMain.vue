<template>
  <div class="main-container">
    <div class="content-container">
      <router-view v-slot="{ Component }">
        <transition mode="out-in" name="fade">
          <keep-alive :include="cachedViews">
            <component :is="Component" :key="$route.path"/>
          </keep-alive>
        </transition>
      </router-view>
    </div>
  </div>
</template>

<script setup>
import {computed} from 'vue';
import {useTagsViewStore} from '@/stores/back/tagsViewStore';

const tagsViewStore = useTagsViewStore();

// 计算缓存的视图
const cachedViews = computed(() => {
  return tagsViewStore.cachedViews || [];
});
</script>

<style scoped>
.main-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.content-container {
  flex: 1;
  overflow: auto;
  padding: 20px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>