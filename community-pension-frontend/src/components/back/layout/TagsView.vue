<!-- 标签页 -->
<template>
  <div class="tags-view-container">
    <el-scrollbar wrap-class="tags-view-wrapper" :native="false" :noresize="false">
      <router-link
        v-for="tag in visitedViews"
        ref="tagRefs"
        :key="tag.path"
        :to="{ path: tag.path, query: tag.query, fullPath: tag.fullPath }"
        :class="isActive(tag) ? 'active' : ''"
        :data-path="tag.path"
        class="tags-view-item"
        @click.middle="closeSelectedTag(tag)"
        @contextmenu.prevent="openMenu(tag, $event)"
      >
        {{ (tag.meta && tag.meta.title) || getDefaultTitle(tag.path) }}
        <span v-if="!isAffix(tag)" class="el-icon-close" @click.prevent.stop="closeSelectedTag(tag)" />
      </router-link>
    </el-scrollbar>
    <ul v-show="visible" :style="{ left: left + 'px', top: top + 'px' }" class="contextmenu">
      <li @click="refreshSelectedTag(selectedTag)">刷新</li>
      <li v-if="!isAffix(selectedTag)" @click="closeSelectedTag(selectedTag)">关闭</li>
      <li @click="closeOthersTags">关闭其他</li>
      <li @click="closeAllTags">关闭所有</li>
      <li @click="closeLeftTags">关闭左侧</li>
      <li @click="closeRightTags">关闭右侧</li>
    </ul>
  </div>
</template>

<script setup>
import { useTagsViewStore } from '@/stores/tagsView';
import { ElScrollbar } from 'element-plus';
import { nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';

// 定义props，但使用store作为主要数据源
const props = defineProps({
  visitedViews: {
    type: Array,
    required: true
  }
});

const emit = defineEmits([
  'add-tab',
  'remove-tab',
  'close-others',
  'close-all',
  'close-left',
  'close-right',
  'refresh'
]);

const route = useRoute();
const router = useRouter();
const tagsViewStore = useTagsViewStore();
const tagRefs = ref([]);
const visible = ref(false);
const top = ref(0);
const left = ref(0);
const selectedTag = ref({});

// 使用computed引用store中的数据，确保始终使用最新状态
const visitedViews = ref(tagsViewStore.visitedViews);
// 同步store变化到本地
watch(() => tagsViewStore.visitedViews, (newViews) => {
  visitedViews.value = newViews;
}, { deep: true });

// 监听路由变化，初始化当前标签
watch(
  () => route.path,
  (newPath) => {
    try {
      // 规范化路径，移除末尾斜杠
      const normalizedPath = newPath.replace(/\/$/, '');
      
      // 在添加之前先检查是否已存在重复标签
      const existingTags = visitedViews.value.filter(v => 
        v.path.replace(/\/$/, '') === normalizedPath ||
        v.path.replace(/\/$/, '') + '/' === normalizedPath + '/'
      );
      
      if (existingTags.length > 0) {
        // 如果发现重复标签，则清理多余的
        if (existingTags.length > 1) {
          // 保留第一个，删除其余的
          for (let i = 1; i < existingTags.length; i++) {
            emit('remove-tab', existingTags[i]);
          }
        }
        return;
      }
      
      // 不添加首页和特殊页面
      if (normalizedPath !== '/admin/home' && route.meta?.showInTab !== false && !route.meta?.isFirstLevelMenu) {
        emit('add-tab', {
          path: normalizedPath, // 使用规范化的路径
          name: route.name,
          meta: { 
            ...route.meta,
            title: route.meta?.title || getDefaultTitle(normalizedPath)
          }
        });
      }
    } catch (error) {
      // 错误处理
    }
  },
  { immediate: true }
);

// 获取默认标题的函数
function getDefaultTitle(path) {
  try {
    const parts = path.split('/');
    if (parts.length > 0) {
      const lastPart = parts[parts.length - 1];
      return lastPart.charAt(0).toUpperCase() + lastPart.slice(1);
    }
  } catch (e) {
    // 错误处理
  }
  return '未命名标签';
}

// 判断标签是否为固定标签
function isAffix(tag) {
  return tag && ((tag.meta && tag.meta.affix) || tag.path === '/admin/home');
}

// 组件挂载时初始化
onMounted(() => {
  try {
    // 首先清理可能存在的重复标签
    cleanTags();
    
    // 初始化首页标签
    const homeView = {
      path: '/admin/home', // 确保使用规范化的路径
      name: 'AdminHome',
      meta: { 
        title: '首页',
        affix: true 
      }
    };
    
    // 检查首页标签是否存在（包括可能末尾有斜杠的路径）
    const homeTags = visitedViews.value.filter(v => 
      v.path.replace(/\/$/, '') === '/admin/home'
    );
    
    if (homeTags.length === 0) {
      // 没有首页标签，添加一个
      emit('add-tab', homeView);
    } else if (homeTags.length > 1) {
      // 有多个首页标签，清理多余的，只保留一个
      // 优先保留 affixed 的标签
      const affixedHomeTag = homeTags.find(tag => tag.meta?.affix);
      const tagToKeep = affixedHomeTag || homeTags[0];
      
      homeTags.forEach(tag => {
        if (tag !== tagToKeep) {
          emit('remove-tab', tag);
        }
      });
    }
    
    // 初始化当前路由标签（如果不是首页）
    const currentPath = route.path.replace(/\/$/, ''); // 规范化当前路径
    
    if (currentPath !== '/admin/home' && route.meta?.showInTab !== false && !route.meta?.isFirstLevelMenu) {
      // 检查当前路径标签是否存在
      const currentTags = visitedViews.value.filter(v => 
        v.path.replace(/\/$/, '') === currentPath
      );
      
      if (currentTags.length === 0) {
        // 没有当前页面的标签，添加一个
        emit('add-tab', {
          path: currentPath,
          name: route.name,
          meta: { 
            ...route.meta,
            title: route.meta?.title || getDefaultTitle(currentPath)
          }
        });
      } else if (currentTags.length > 1) {
        // 有多个相同路径标签，清理多余的
        for (let i = 1; i < currentTags.length; i++) {
          emit('remove-tab', currentTags[i]);
        }
      }
    }
  } catch (error) {
    // 错误处理
  }
  
  // 监听点击事件以关闭上下文菜单
  document.addEventListener('click', closeMenu);
});

// 组件卸载时移除事件监听
onBeforeUnmount(() => {
  document.removeEventListener('click', closeMenu);
});

// 检查标签是否激活
function isActive(tag) {
  // 规范化当前路径，处理末尾斜杠问题
  const currentPath = route.path.replace(/\/$/, '');
  const tagPath = tag.path.replace(/\/$/, '');
  return currentPath === tagPath;
}

// 关闭选定的标签 - 改进版
function closeSelectedTag(tag) {
  try {
    // 固定标签不能关闭
    if (isAffix(tag)) {
      return;
    }

    // 如果关闭的是当前活动标签，需要先找出下一个要导航的标签
    let nextTag = null;
    if (isActive(tag)) {
      // 在移除前确定下一个导航目标
      const tagIndex = visitedViews.value.findIndex(v => v.path === tag.path);
      
      // 优先选择左侧标签
      if (tagIndex > 0) {
        nextTag = { ...visitedViews.value[tagIndex - 1] };
      } 
      // 如果没有左侧标签，选择右侧标签
      else if (visitedViews.value.length > 1 && tagIndex + 1 < visitedViews.value.length) {
        nextTag = { ...visitedViews.value[tagIndex + 1] };
      } 
      // 如果没有其他标签，导航到首页
      else {
        nextTag = { path: '/admin/home', meta: { title: '首页' } };
      }
    }
    
    // 触发关闭事件到父组件
    emit('remove-tab', tag);
    
    // 如果是当前标签，在nextTick后导航到下一个标签
    if (isActive(tag) && nextTag) {
      // 使用nextTick确保DOM更新后再进行导航
      nextTick(() => {
        router.push(nextTag.path);
      });
    }
  } catch (error) {
    // 出错时导航到安全的首页
    router.push('/admin/home');
  }
}

// 刷新选定的标签
function refreshSelectedTag(tag) {
  emit('refresh', tag);
}

// 关闭其他标签
function closeOthersTags() {
  emit('close-others', selectedTag.value);
  closeMenu();
}

// 关闭所有标签
function closeAllTags() {
  emit('close-all');
  router.push('/admin/home');
  closeMenu();
}

// 关闭左侧标签
function closeLeftTags() {
  emit('close-left', selectedTag.value);
  closeMenu();
}

// 关闭右侧标签
function closeRightTags() {
  emit('close-right', selectedTag.value);
  closeMenu();
}

// 打开上下文菜单
function openMenu(tag, e) {
  try {
    const menuMinWidth = 105;
    const offsetLeft = e.clientX;
    const offsetWidth = window.innerWidth;
    const maxLeft = offsetWidth - menuMinWidth;
    const menuTop = e.clientY;

    if (offsetLeft > maxLeft) {
      left.value = maxLeft;
    } else {
      left.value = offsetLeft;
    }
    top.value = menuTop;

    selectedTag.value = tag;
    visible.value = true;
  } catch (error) {
    visible.value = false;
  }
}

// 关闭上下文菜单
function closeMenu() {
  visible.value = false;
}

// 清理标签函数
function cleanTags() {
  // 查找重复标签
  const normalizedPaths = new Map();
  const duplicates = [];
  
  visitedViews.value.forEach(tag => {
    const normalizedPath = tag.path.replace(/\/$/, '');
    
    if (normalizedPaths.has(normalizedPath)) {
      duplicates.push({
        original: normalizedPaths.get(normalizedPath),
        duplicate: tag
      });
    } else {
      normalizedPaths.set(normalizedPath, tag);
    }
  });
  
  // 清理重复标签
  if (duplicates.length > 0) {
    duplicates.forEach(pair => {
      emit('remove-tab', pair.duplicate);
    });
  }
}
</script>

<style lang="scss" scoped>
.tags-view-container {
  height: 34px;
  width: 100%;
  background: #fff;
  border-bottom: 1px solid #d8dce5;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.12), 0 0 3px 0 rgba(0, 0, 0, 0.04);

  .tags-view-wrapper {
    .tags-view-item {
      display: inline-flex;
      align-items: center;
      position: relative;
      cursor: pointer;
      height: 26px;
      line-height: 26px;
      border: 1px solid #d8dce5;
      color: #495060;
      background: #fff;
      padding: 0 8px;
      font-size: 12px;
      margin-left: 5px;
      margin-top: 4px;
      text-decoration: none;
      overflow: hidden;
      white-space: nowrap;
      max-width: 180px;
      text-overflow: ellipsis;

      &:first-of-type {
        margin-left: 15px;
      }

      &:last-of-type {
        margin-right: 15px;
      }

      &.active {
        background-color: #409eff;
        color: #fff;
        border-color: #409eff;

        &::before {
          content: '';
          background: #fff;
          display: inline-block;
          width: 8px;
          height: 8px;
          border-radius: 50%;
          position: relative;
          margin-right: 2px;
        }
      }
      
      .el-icon-close {
        margin-left: 5px;
        width: 16px;
        height: 16px;
        line-height: 16px;
        text-align: center;
        border-radius: 50%;
        transition: all .3s;
        
        &:hover {
          background-color: #b4bccc;
          color: #fff;
        }
      }
    }
  }

  .contextmenu {
    margin: 0;
    background: #fff;
    z-index: 3000;
    position: absolute;
    list-style-type: none;
    padding: 5px 0;
    border-radius: 4px;
    font-size: 12px;
    font-weight: 400;
    color: #333;
    box-shadow: 2px 2px 3px 0 rgba(0, 0, 0, 0.1);

    li {
      margin: 0;
      padding: 7px 16px;
      cursor: pointer;

      &:hover {
        background: #eee;
      }
    }
  }
}
</style>