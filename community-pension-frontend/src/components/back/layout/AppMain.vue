<template>
  <div class="main-container">
    <TagsView 
      :visited-views="tagsViewStore.visitedViews" 
      @remove-tab="removeTab"
      @close-others="closeOthersTags"
      @close-left="closeLeftTags"
      @close-right="closeRightTags"
      @close-all="closeAllTags"
      @refresh="refreshTag"
    />
    <div class="content-container">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <keep-alive :include="tagsViewStore.cachedViews">
            <component :is="Component" :key="$route.path" />
          </keep-alive>
        </transition>
      </router-view>
    </div>
  </div>
</template>

<script setup>
import { watch, onMounted, onBeforeUnmount } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useTagsViewStore } from '@/stores/tagsView';
import TagsView from './TagsView.vue';

const router = useRouter();
const route = useRoute();
const tagsViewStore = useTagsViewStore();

// 移除标签
const removeTab = (view) => {
  // 如果是首页，不允许关闭
  if (view.path === '/admin/home') {
    return;
  }
  tagsViewStore.removeVisitedView(view);
  tagsViewStore.removeCachedView(view);
  // 如果关闭的是当前标签，自动切换到首页
  if (view.path === route.path) {
    router.push('/admin/home');
  }
};

// 关闭其他标签
const closeOthersTags = (view) => {
  tagsViewStore.closeOthersTags(view);
  // 自动跳转到最后一个有效标签页
  if (!visitedViews.some(v => v.path === route.path)) {
    const lastView = visitedViews.slice(-1)[0] || { path: '/admin/home' };
    router.push(lastView.path);
  }
};

// 关闭左侧标签
const closeLeftTags = (view) => {
  tagsViewStore.closeLeftTags(view);
  // 自动跳转到最后一个有效标签页
  if (!visitedViews.some(v => v.path === route.path)) {
    const lastView = visitedViews.slice(-1)[0] || { path: '/admin/home' };
    router.push(lastView.path);
  }
};

// 关闭右侧标签
const closeRightTags = (view) => {
  tagsViewStore.closeRightTags(view);
  // 自动跳转到最后一个有效标签页
  if (!visitedViews.some(v => v.path === route.path)) {
    const lastView = visitedViews.slice(-1)[0] || { path: '/admin/home' };
    router.push(lastView.path);
  }
};

// 关闭所有标签
const closeAllTags = () => {
  tagsViewStore.closeAllTags();
  // 关闭所有标签后自动跳转到首页
  if (!tagsViewStore.visitedViews.length) {
    router.push('/admin/home');
  }
};

// 刷新标签
const refreshTag = (view) => {
  tagsViewStore.refreshTag(view);
};

// 监听路由变化
watch(
  () => route.path,
  (newPath) => {
    // 如果是首页相关路径
    if (['/admin', '/admin/home', '/admin/index'].includes(newPath)) {
      // 如果当前没有首页标签，初始化它
      if (!tagsViewStore.visitedViews.some(v => v.path === '/admin/home')) {
        tagsViewStore.initDashboardTab();
      }
      // 如果当前路径不是 /admin/home，重定向到 /admin/home
      if (newPath !== '/admin/home') {
        router.replace('/admin/home');
      }
      return;
    }
    
    // 检查是否已存在该标签
    const isExist = tagsViewStore.visitedViews.some(v => v.path === route.path);
    if (!isExist) {
      tagsViewStore.addVisitedView(route);
      tagsViewStore.addCachedView(route);
    }
  },
  { immediate: true }
);

// 组件挂载时初始化首页标签
onMounted(() => {
  // 如果当前没有首页标签，初始化它
  if (!tagsViewStore.visitedViews.some(v => v.path === '/admin/home')) {
    tagsViewStore.initDashboardTab();
  }
});
</script>

<style scoped>
.main-container {
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  min-height: calc(100vh - 120px);
}

.content-container {
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