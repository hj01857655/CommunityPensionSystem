<!-- 标签页 -->
<template>
  <div class="tags-view-container">
    <scroll-pane ref="scrollPane" class="tags-view-wrapper" @scroll="handleScroll">
      <router-link
        v-for="tag in visitedViews"
        :key="tag.path"
        :class="isActive(tag) ? 'active' : ''"
        :to="{ path: tag.path, query: tag.query, fullPath: tag.fullPath }"
        tag="span"
        class="tags-view-item"
        @click.middle="!isAffix(tag) && closeSelectedTag(tag)"
        @contextmenu.prevent="openMenu($event, tag)"
      >
        {{ tag.title }}
        <el-icon v-if="!isAffix(tag)" class="close-icon" @click.prevent.stop="closeSelectedTag(tag)">
          <Close />
        </el-icon>
      </router-link>
    </scroll-pane>
    <ul v-show="visible" :style="{left:left+'px',top:top+'px'}" class="contextmenu">
      <li @click="refreshSelectedTag(selectedTag)"><el-icon><Refresh /></el-icon> 刷新页面</li>
      <li v-if="!isAffix(selectedTag)" @click="closeSelectedTag(selectedTag)"><el-icon><Close /></el-icon> 关闭当前</li>
      <li @click="closeOthersTags"><el-icon><CircleClose /></el-icon> 关闭其他</li>
      <li v-if="!isFirstView()" @click="closeLeftTags"><el-icon><ArrowLeft /></el-icon> 关闭左侧</li>
      <li v-if="!isLastView()" @click="closeRightTags"><el-icon><ArrowRight /></el-icon> 关闭右侧</li>
      <li @click="closeAllTags"><el-icon><CircleClose /></el-icon> 全部关闭</li>
    </ul>
  </div>
</template>

<script setup>
import {onBeforeUnmount, onMounted, ref, watch} from 'vue';
import {useRoute, useRouter} from 'vue-router';
import ScrollPane from './ScrollPane.vue';
import {ArrowLeft, ArrowRight, CircleClose, Close, Refresh} from '@element-plus/icons-vue';

const props = defineProps({
  visitedViews: {
    type: Array,
    required: true
  }
});

const emit = defineEmits([
  'remove-tab',
  'close-others',
  'close-left',
  'close-right',
  'close-all',
  'refresh',
  'add-tab'
]);

const route = useRoute();
const router = useRouter();
const scrollPane = ref(null);
const visible = ref(false);
const top = ref(0);
const left = ref(0);
const selectedTag = ref({});

// 判断是否是当前激活的标签
const isActive = (tag) => {
  return tag.path === route.path || 
    (tag.meta?.affix && route.matched.some(r => r.path === tag.path));
};

// 判断是否是固定标签
const isAffix = (tag) => {
  return (tag.meta && tag.meta.affix) || tag.path === '/admin/home';
};

// 判断是否是第一个标签
const isFirstView = () => {
  try {
    return selectedTag.value.path === '/admin/home' || selectedTag.value.path === props.visitedViews[1]?.path;
  } catch (err) {
    return false;
  }
};

// 判断是否是最后一个标签
const isLastView = () => {
  try {
    return selectedTag.value.path === props.visitedViews[props.visitedViews.length - 1]?.path;
  } catch (err) {
    return false;
  }
};

// 关闭选中的标签
const closeSelectedTag = (view) => {
  // 如果是固定标签或首页，不允许关闭
  if (isAffix(view) || view.path === '/admin/home') {
    return;
  }
  emit('remove-tab', view);
};

// 刷新选中的标签
const refreshSelectedTag = (view) => {
  emit('refresh', view);
};

// 关闭其他标签
const closeOthersTags = () => {
  router.push(selectedTag.value.path);
  emit('close-others', selectedTag.value);
};

// 关闭左侧标签
const closeLeftTags = () => {
  emit('close-left', selectedTag.value);
};

// 关闭右侧标签
const closeRightTags = () => {
  emit('close-right', selectedTag.value);
};

// 关闭所有标签
const closeAllTags = () => {
  emit('close-all');
};

// 打开右键菜单
const openMenu = (e, tag) => {
  const menuMinWidth = 105;
  const offsetLeft = e.clientX;
  const offsetWidth = e.target.offsetWidth;
  const maxLeft = window.innerWidth - menuMinWidth;
  const left = offsetLeft + offsetWidth > maxLeft ? maxLeft : offsetLeft;
  
  top.value = e.clientY;
  left.value = left;
  visible.value = true;
  selectedTag.value = tag;
};

// 关闭右键菜单
const closeMenu = () => {
  visible.value = false;
};

// 处理滚动
const handleScroll = () => {
  closeMenu();
};

// 监听点击事件，关闭右键菜单
document.addEventListener('click', closeMenu);

// 组件卸载时移除事件监听
onBeforeUnmount(() => {
  document.removeEventListener('click', closeMenu);
});

// 监听路由变化
watch(() => route.path, (newPath) => {
  try {
    // 通用路由处理逻辑
    const hasTag = props.visitedViews.some(v => v.path === newPath);

    if (!hasTag && route.meta?.showInTab !== false && !route.meta?.isFirstLevelMenu) {
      emit('add-tab', {
        path: newPath,
        title: route.meta?.title || '未命名标签',
        meta: {affix: route.meta?.affix}
      });
    }

    // 滚动到当前激活的标签
    if (scrollPane.value) {
      scrollPane.value.moveToTarget(route);
    }
    
    // 安全更新激活的标签状态
    const matchedTag = props.visitedViews.find(v => v.path === newPath);
    if (matchedTag) {
      selectedTag.value = matchedTag;
    } else if (props.visitedViews.length > 0) {
      selectedTag.value = props.visitedViews[0];
    }
  } catch (error) {
    console.error('路由监听异常:', error);
  }
}, {immediate: true});

// 监听右键菜单显示状态
watch(visible, (value) => {
  if (value) {
    document.body.addEventListener('click', closeMenu);
  } else {
    document.body.removeEventListener('click', closeMenu);
  }
});

// 组件挂载时初始化
onMounted(() => {
  try {
    // 初始化首页标签
    if (!Array.isArray(props.visitedViews)) {
      console.error('visitedViews 未正确初始化');
      return;
    }
    
    const hasHome = props.visitedViews.some(v => v?.path === '/admin/home');
    if (!hasHome) {
      const homeTab = { path: '/admin/home', title: '首页', meta: { affix: true } };
      emit('add-tab', homeTab);
      router.push(homeTab.path).catch(() => {});
    } else {
      const homeTag = props.visitedViews.find(v => v?.path === '/admin/home');
      if (homeTag && !isActive(homeTag)) {
        router.push(homeTag.path).catch(() => {});
      }
    }
  } catch (error) {
    console.error('初始化异常:', error);
  }
});
</script>

<style lang="scss" scoped>
.tags-view-container {
  height: 34px;
  width: 100%;
  background: #fff;
  border-bottom: 1px solid #d8dce5;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, .12), 0 0 3px 0 rgba(0, 0, 0, .04);

  .tags-view-wrapper {
    .tags-view-item {
      display: inline-block;
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

      &:first-of-type {
        margin-left: 15px;
      }

      &:last-of-type {
        margin-right: 15px;
      }

      &.active {
        background-color: #42b983;
        color: #fff;
        border-color: #42b983;

        &::before {
          content: '';
          background: #fff;
          display: inline-block;
          width: 8px;
          height: 8px;
          border-radius: 50%;
          position: relative;
          margin-right: 4px;
        }
      }

      .close-icon {
        width: 16px;
        height: 16px;
        vertical-align: 2px;
        border-radius: 50%;
        text-align: center;
        transition: all .3s cubic-bezier(.645, .045, .355, 1);
        transform-origin: 100% 50%;

        &:before {
          transform: scale(.6);
          display: inline-block;
          vertical-align: -3px;
        }

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
    box-shadow: 2px 2px 3px 0 rgba(0, 0, 0, .3);
    border: 1px solid #e4e7ed;

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