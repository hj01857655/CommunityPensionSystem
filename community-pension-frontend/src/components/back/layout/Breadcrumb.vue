<template>
  <el-breadcrumb class="app-breadcrumb" separator="/">
    <transition-group name="breadcrumb">
      <el-breadcrumb-item v-for="(item, index) in levelList" :key="`${item.path}-${index}`">
        <span v-if="item.redirect === 'noRedirect' || index == levelList.length - 1" class="no-redirect">{{ item.meta.title }}</span>
        <a v-else @click.prevent="handleLink(item)">{{ item.meta.title }}</a>
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();
const levelList = ref(null);

// 获取面包屑数据
const getBreadcrumb = () => {
  let matched = route.matched.filter(item => item.meta && item.meta.title);
  
  // 移除"管理后台"这一级，因为它与"首页"重复
  matched = matched.filter(item => item.path !== '/admin');
  
  // 判断是否为首页
  if (!isDashboard(matched[0])) {
    matched = [{ path: "/admin/home", meta: { title: "首页" } }].concat(matched);
  }
  
  levelList.value = matched;
};

// 判断是否为首页
const isDashboard = (route) => {
  const path = route && route.path;
  if (!path) {
    return false;
  }
  return path === '/admin/home';
};

// 处理链接点击
const handleLink = (item) => {
  const { redirect, path } = item;
  if (redirect) {
    router.push(redirect);
    return;
  }
  router.push(path);
};

// 监听路由变化
watch(() => route.path, (path) => {
  // 如果是重定向页面，不更新面包屑
  if (path.startsWith('/redirect/')) {
    return;
  }
  getBreadcrumb();
}, { immediate: true });

// 初始化
onMounted(() => {
  getBreadcrumb();
});
</script>

<style lang="scss" scoped>
.app-breadcrumb.el-breadcrumb {
  display: inline-block;
  font-size: 14px;
  line-height: 50px;
  margin-left: 8px;

  .no-redirect {
    color: #97a8be;
    cursor: text;
  }
}

/* 面包屑动画 */
.breadcrumb-enter-active,
.breadcrumb-leave-active {
  transition: all .5s;
}

.breadcrumb-enter-from,
.breadcrumb-leave-active {
  opacity: 0;
  transform: translateX(20px);
}

.breadcrumb-leave-active {
  position: absolute;
}
</style> 