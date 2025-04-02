<!--这是标签页的滚动组件-->
<template>
  <el-scrollbar ref="scrollContainer" :vertical="false" class="scroll-container" @wheel.prevent="handleScroll">
    <slot />
  </el-scrollbar>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue';

const props = defineProps({
  speed: {
    type: Number,
    default: 20
  }
});

const tagAndTagSpacing = 4; // 标签之间的间距

const scrollContainer = ref(null);
const scrollWrapper = ref(null);
const left = ref(0);

// 计算滚动包装器
const scrollWrapperComputed = computed(() => {
  return scrollContainer.value?.$refs.wrap;
});

// 处理滚动事件
const handleScroll = (e) => {
  const eventDelta = e.wheelDelta || -e.deltaY * 40;
  const $scrollWrapper = scrollWrapperComputed.value;
  if ($scrollWrapper) {
    $scrollWrapper.scrollLeft = $scrollWrapper.scrollLeft + eventDelta / 4;
  }
};

// 触发滚动事件
const emitScroll = () => {
  scrollContainer.value?.$emit('scroll');
};

// 移动到目标标签
const moveToTarget = (currentTag) => {
  if (!currentTag) return;
  
  const $container = scrollContainer.value?.$el;
  if (!$container) return;
  
  const $containerWidth = $container.offsetWidth;
  const $scrollWrapper = scrollWrapperComputed.value;
  if (!$scrollWrapper) return;
  
  const tagList = scrollContainer.value?.$parent?.$refs.tag;
  if (!tagList || tagList.length === 0) return;

  const firstTag = tagList[0];
  const lastTag = tagList[tagList.length - 1];

  if (firstTag === currentTag) {
    $scrollWrapper.scrollLeft = 0;
  } else if (lastTag === currentTag) {
    $scrollWrapper.scrollLeft = $scrollWrapper.scrollWidth - $containerWidth;
  } else {
    // 找到前一个和后一个标签
    const currentIndex = tagList.findIndex(item => item === currentTag);
    if (currentIndex === -1) return;
    
    const prevTag = tagList[currentIndex - 1];
    const nextTag = tagList[currentIndex + 1];
    
    if (prevTag && nextTag) {
      const prevTagLeft = prevTag.offsetLeft;
      const nextTagLeft = nextTag.offsetLeft;
      const nextTagRight = nextTagLeft + nextTag.offsetWidth;
      
      if (nextTagRight > $scrollWrapper.scrollLeft + $containerWidth) {
        $scrollWrapper.scrollLeft = nextTagRight - $containerWidth + tagAndTagSpacing;
      } else if (prevTagLeft < $scrollWrapper.scrollLeft) {
        $scrollWrapper.scrollLeft = prevTagLeft - tagAndTagSpacing;
      }
    }
  }
};

// 组件挂载时添加滚动事件监听
onMounted(() => {
  const $scrollWrapper = scrollWrapperComputed.value;
  if ($scrollWrapper) {
    $scrollWrapper.addEventListener('scroll', emitScroll, true);
  }
});

// 组件卸载时移除滚动事件监听
onBeforeUnmount(() => {
  const $scrollWrapper = scrollWrapperComputed.value;
  if ($scrollWrapper) {
    $scrollWrapper.removeEventListener('scroll', emitScroll);
  }
});

// 暴露方法给父组件
defineExpose({
  moveToTarget
});
</script>

<style lang="scss" scoped>
.scroll-container {
  white-space: nowrap;
  position: relative;
  overflow: hidden;
  width: 100%;
  
  :deep(.el-scrollbar__bar) {
    bottom: 0px;
  }
  
  :deep(.el-scrollbar__wrap) {
    height: 49px;
  }
}
</style> 