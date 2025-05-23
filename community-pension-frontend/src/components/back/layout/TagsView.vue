<!-- 标签页 -->
<template>
  <div class="tags-view-container">
    <el-scrollbar wrap-class="tags-view-wrapper" :native="false" :noresize="false" class="custom-scrollbar">
      <router-link
        v-for="tag in visitedViews"
        ref="tagRefs"
        :key="tag.path"
        :to="{ path: tag.path, query: tag.query, fullPath: tag.fullPath }"
        :class="isActive(tag) ? 'active' : ''"
        :data-path="tag.path"
        class="tags-view-item"
        @click="handleTagClick(tag)"
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
import { useTagsViewStore } from '@/stores/back/tagsViewStore';
import { ElScrollbar } from 'element-plus';
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();
const tagsViewStore = useTagsViewStore();
const tagRefs = ref([]);
const visible = ref(false);
const top = ref(0);
const left = ref(0);
const selectedTag = ref({});

// 直接使用 computed 从 store 获取 visitedViews
const visitedViews = computed(() => tagsViewStore.visitedViews);

// 监听路由变化，尝试添加新标签到 store
watch(
  () => route.fullPath, // 监听 fullPath 以处理 query 参数变化
  (newFullPath) => {
    if (route.name) { // 确保路由有 name
      tagsViewStore.addView({ ...route }); // 传递整个 route 对象给 store action
    }
    // 添加后滚动到当前标签
    moveToCurrentTag();
  },
  { immediate: true } // 立即执行一次以添加初始标签
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
  // 首页标签始终固定，不允许关闭
  if (!tag) return false;

  // 判断是否是首页标签或有affix标记
  return tag?.meta?.affix || isHomePath(tag.path);
}

// 处理标签点击事件
function handleTagClick(tag) {
  // 确保标签被正确激活
  tagsViewStore.updateVisitedView(tag);

  // 如果是当前活动标签，则刷新当前页面
  if (isActive(tag) && tag.path === route.path) {
    // 如果已经是当前标签，则刷新当前页面
    refreshSelectedTag(tag);
  } else {
    // 添加点击动画效果
    const tagEl = event.currentTarget;
    if (tagEl) {
      tagEl.classList.add('tag-click-effect');
      setTimeout(() => {
        tagEl.classList.remove('tag-click-effect');
      }, 300);
    }
  }
}

// 组件挂载时初始化
onMounted(() => {
  document.addEventListener('click', closeMenu);
  // 确保初始时滚动到活动标签
  nextTick(moveToCurrentTag);
});

// 组件卸载时移除事件监听
onBeforeUnmount(() => {
  document.removeEventListener('click', closeMenu);
});

// 检查标签是否激活
function isActive(tag) {
  if (!tag) return false;

  // 使用规范化路径进行比较
  const tagPath = normalizePath(tag.path);
  const routePath = normalizePath(route.path);

  // 直接比较规范化后的路径
  return tagPath === routePath;
}

// 关闭选定的标签 - 调用 store action
function closeSelectedTag(tag) {
  // 如果是固定标签，不允许关闭
  if (isAffix(tag)) {
    return;
  }

  // 检查是否是当前活动标签
  const currentIsActive = isActive(tag);
  let nextTag = null;

  // 如果关闭的是当前活动标签，需要确定下一个要导航到的标签
  if (currentIsActive) {
    // 获取标签在数组中的位置
    const tagIndex = visitedViews.value.findIndex(v => v.path === tag.path);

    // 首先尝试导航到最后一个标签（排除当前标签）
    const lastTag = visitedViews.value[visitedViews.value.length - 1];
    if (lastTag && lastTag.path !== tag.path) {
      nextTag = lastTag;
    }
    // 如果最后一个标签就是当前标签，则尝试前一个标签
    else if (tagIndex > 0) {
      nextTag = visitedViews.value[tagIndex - 1];
    }
    // 如果没有前一个，尝试导航到后一个标签
    else if (visitedViews.value.length > tagIndex + 1) {
      nextTag = visitedViews.value[tagIndex + 1];
    }

    // 如果上述标签都不存在，则导航到首页
    if (!nextTag) {
      // 尝试找到首页标签
      const homeTag = visitedViews.value.find(v => isHomePath(v.path));
      nextTag = homeTag || { path: '/admin/home', fullPath: '/admin/home' }; // 确保总有后备
    }
  }

  // 调用 store action 删除标签
  tagsViewStore.delView(tag).then(() => {
    // 删除成功后，如果是关闭当前活动标签，则导航到下一个标签
    if (currentIsActive && nextTag) {
      // 使用更可靠的导航方式，确保有 fullPath 和 path 的后备
      const targetPath = nextTag.fullPath || nextTag.path;
      router.push(targetPath).catch(err => {
        console.error('导航失败:', err);
        // 如果导航失败，尝试导航到首页
        router.push('/admin/home');
      });
    }
  });
}

// 刷新选定的标签 - 调用 store action
function refreshSelectedTag(tag) {
  // 先移除缓存，确保组件会重新渲染
  tagsViewStore.delCachedView(tag).then(() => {
    // 使用 nextTick 确保 DOM 更新后再执行刷新操作
    nextTick(() => {
      // 如果当前标签就是要刷新的标签
      if (tag.path === route.path || tag.fullPath === route.fullPath) {
        // 查找首页标签作为中转页
        const homeTag = tagsViewStore.visitedViews.find(v => isHomePath(v.path));

        // 如果有首页标签且不是当前标签，通过首页中转刷新
        if (homeTag && homeTag.path !== tag.path) {
          // 先导航到首页
          router.push(homeTag.path).then(() => {
            // 然后导航回原标签
            nextTick(() => {
              router.push(tag.fullPath || tag.path);
            });
          }).catch(err => {
            console.error('刷新标签失败:', err);
          });
        } else {
          // 如果没有首页标签或当前就是首页，直接刷新页面
          window.location.reload();
        }
      } else {
        // 如果刷新的不是当前标签，先标记为已刷新
        tagsViewStore.refreshTag(tag);
      }
    });
  });
  closeMenu(); // 关闭右键菜单
}

// 关闭其他标签 - 调用 store action
function closeOthersTags() {
  if (!selectedTag.value || !selectedTag.value.path) return;
  // 确保传递的是有效的 tag 对象
  const tagToKeep = visitedViews.value.find(v => v.path === selectedTag.value.path);
  if (tagToKeep) {
    tagsViewStore.delOthersViews(tagToKeep).then(() => {
      if (!isActive(tagToKeep)) { // 如果关闭后当前选中的不是活动标签，导航到它
         router.push(tagToKeep.fullPath || tagToKeep.path);
      }
    });
  }
  closeMenu();
}

// 关闭所有标签 - 调用 store action
function closeAllTags() {
  // 先关闭菜单
  closeMenu();

  // 调用 store action 删除所有视图
  tagsViewStore.delAllViews().then(({ visitedViews: remainingViews }) => {
    // 确保标签列表中只有一个首页标签
    const homeTag = remainingViews.find(tag => isHomePath(tag.path));

    // 如果当前活动路由不在剩余标签中，导航到首页
    if (!remainingViews.some(v => isActive(v))) {
      // 使用 nextTick 确保 DOM 更新后再导航
      nextTick(() => {
        if (homeTag) {
          // 如果有首页标签，导航到首页
          router.push(homeTag.fullPath || homeTag.path).catch(err => {
            console.error('导航失败:', err);
            router.push('/admin/home');
          });
        } else if (remainingViews.length > 0) {
          // 如果没有首页标签但有其他标签，导航到第一个标签
          router.push(remainingViews[0].fullPath || remainingViews[0].path);
        } else {
          // 如果没有任何标签（理论上不应发生），导航到首页
          router.push('/admin/home');
        }
      });
    }
  });
}

// 关闭左侧标签 - 调用 store action
function closeLeftTags() {
  if (!selectedTag.value || !selectedTag.value.path) return;
  const tagReference = visitedViews.value.find(v => v.path === selectedTag.value.path);
  if (tagReference) {
     tagsViewStore.delLeftViews(tagReference).then(() => {
       if (!isActive(tagReference)) { // 如果关闭后当前选中的不是活动标签，导航到它
         router.push(tagReference.fullPath || tagReference.path);
       }
     });
  }
  closeMenu();
}

// 关闭右侧标签 - 调用 store action
function closeRightTags() {
  if (!selectedTag.value || !selectedTag.value.path) return;
  const tagReference = visitedViews.value.find(v => v.path === selectedTag.value.path);
  if (tagReference) {
     tagsViewStore.delRightViews(tagReference).then(() => {
       if (!isActive(tagReference)) { // 如果关闭后当前选中的不是活动标签，导航到它
         router.push(tagReference.fullPath || tagReference.path);
       }
     });
  }
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

// 工具函数：规范化路径（移除尾部斜杠）
function normalizePath(path) {
  return path?.replace(/\/$/, '') || '';
}

// 工具函数：判断是否是首页路径
function isHomePath(path) {
  const normalizedPath = normalizePath(path);
  return normalizedPath === '/admin/home' || normalizedPath === '/dashboard';
}

// 滚动到当前激活的标签
function moveToCurrentTag() {
  nextTick(() => {
    // 查找当前激活的标签元素
    const activeTag = tagRefs.value?.find(tag => {
      const tagPath = tag.$el.getAttribute('data-path');
      return tagPath === route.path;
    });

    if (activeTag && activeTag.$el) {
      // 获取标签元素和滚动容器
      const tagElement = activeTag.$el;
      const scrollContainer = document.querySelector('.tags-view-wrapper');

      if (scrollContainer && tagElement) {
        // 计算滚动位置，使标签居中显示
        const containerWidth = scrollContainer.offsetWidth;
        const tagWidth = tagElement.offsetWidth;
        const tagOffsetLeft = tagElement.offsetLeft;

        // 计算滚动位置，使标签尽量居中
        const scrollLeft = tagOffsetLeft - (containerWidth / 2) + (tagWidth / 2);

        // 使用平滑滚动效果
        scrollContainer.scrollTo({
          left: Math.max(0, scrollLeft),
          behavior: 'smooth'
        });
      }
    }
  });
}
</script>

<style lang="scss" scoped>
.tags-view-container {
  height: 40px;
  width: 100%;
  background: linear-gradient(to right, #f8f9fa, #ffffff);
  border-bottom: 1px solid #e4e7ed;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);

  .custom-scrollbar {
    :deep(.el-scrollbar__bar.is-horizontal) {
      height: 4px;
      bottom: 0;
      opacity: 0.2;
      transition: opacity 0.3s;

      &:hover {
        opacity: 0.8;
      }
    }
  }

  .tags-view-wrapper {
    .tags-view-item {
      display: inline-flex;
      align-items: center;
      position: relative;
      cursor: pointer;
      height: 28px;
      line-height: 28px;
      border: 1px solid #e4e7ed;
      color: #606266;
      background: #fff;
      padding: 0 10px;
      font-size: 13px;
      margin-left: 6px;
      margin-top: 5px;
      text-decoration: none;
      overflow: hidden;
      white-space: nowrap;
      max-width: 200px;
      text-overflow: ellipsis;
      border-radius: 4px;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

      &.tag-click-effect {
        animation: tagPulse 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      }

      @keyframes tagPulse {
        0% { transform: scale(1); }
        50% { transform: scale(0.95); }
        100% { transform: scale(1); }
      }

      &:hover:not(.active) {
        color: #409eff;
        border-color: #c6e2ff;
        background-color: #f0f7ff;
      }

      &:first-of-type {
        margin-left: 15px;
      }

      &:last-of-type {
        margin-right: 15px;
      }

      &.active {
        background: linear-gradient(135deg, #409eff, #66b1ff);
        color: #fff;
        border-color: #409eff;
        box-shadow: 0 3px 10px rgba(64, 158, 255, 0.35);
        transform: translateY(-1px);
        font-weight: 500;
        padding-left: 12px;
        padding-right: 12px;
        position: relative;
        overflow: visible;

        &::before {
          content: '';
          position: absolute;
          bottom: -1px;
          left: 50%;
          transform: translateX(-50%);
          width: 80%;
          height: 2px;
          background: rgba(255, 255, 255, 0.8);
          border-radius: 2px;
        }

        &::after {
          content: '';
          position: absolute;
          bottom: -4px;
          left: 50%;
          transform: translateX(-50%);
          width: 6px;
          height: 6px;
          background: #fff;
          border-radius: 50%;
          box-shadow: 0 0 6px rgba(0, 0, 0, 0.1);
        }
      }

      .el-icon-close {
        margin-left: 6px;
        width: 18px;
        height: 18px;
        line-height: 18px;
        text-align: center;
        border-radius: 50%;
        transition: all .3s;
        background-color: rgba(0, 0, 0, 0.1);
        color: #666;
        font-weight: bold;
        display: flex;
        align-items: center;
        justify-content: center;

        &:hover {
          background-color: #f56c6c;
          color: #fff;
        }

        &::after {
          content: '×';
          font-size: 14px;
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
    padding: 6px 0;
    border-radius: 6px;
    font-size: 13px;
    font-weight: 400;
    color: #333;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
    border: 1px solid #ebeef5;
    min-width: 120px;
    animation: menuFadeIn 0.2s ease-out;

    @keyframes menuFadeIn {
      from {
        opacity: 0;
        transform: translateY(10px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }

    li {
      margin: 0;
      padding: 8px 16px;
      cursor: pointer;
      transition: all 0.2s;
      display: flex;
      align-items: center;
      position: relative;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 0;
        bottom: 0;
        width: 0;
        background: #409eff;
        transition: width 0.2s;
      }

      &:hover {
        background: #f5f7fa;
        color: #409eff;

        &::before {
          width: 3px;
        }
      }
    }
  }
}
</style>