<template>
  <el-scrollbar
    ref="scrollContainer"
    :vertical="false"
    class="scroll-container"
    @wheel.prevent="handleScroll"
  >
    <slot />
  </el-scrollbar>
</template>

<script setup>
import useTagsViewStore from '@/store/modules/tagsView'

const tagAndTagSpacing = ref(4);
const { proxy } = getCurrentInstance();

const scrollWrapper = computed(() => proxy.$refs.scrollContainer?.$refs.wrapRef);

onMounted(() => {
  if (scrollWrapper.value) {
    scrollWrapper.value.addEventListener('scroll', emitScroll, true);
  }
});

onBeforeUnmount(() => {
  if (scrollWrapper.value) {
    scrollWrapper.value.removeEventListener('scroll', emitScroll, true);
  }
});

function handleScroll(e) {
  if (!scrollWrapper.value) return;
  
  const eventDelta = e.wheelDelta || -e.deltaY * 40;
  const $scrollWrapper = scrollWrapper.value;
  $scrollWrapper.scrollLeft = $scrollWrapper.scrollLeft + eventDelta / 4;
}

const emits = defineEmits(['scroll']);
const emitScroll = () => {
  emits('scroll');
};

const tagsViewStore = useTagsViewStore();
const visitedViews = computed(() => tagsViewStore.visitedViews);

function moveToTarget(currentTag) {
  if (!currentTag || !scrollWrapper.value) return;
  
  const $container = proxy.$refs.scrollContainer?.$el;
  if (!$container) return;
  
  const $containerWidth = $container.offsetWidth;
  const $scrollWrapper = scrollWrapper.value;

  const firstTag = visitedViews.value[0];
  const lastTag = visitedViews.value[visitedViews.value.length - 1];

  if (firstTag === currentTag) {
    $scrollWrapper.scrollLeft = 0;
    return;
  }
  
  if (lastTag === currentTag) {
    $scrollWrapper.scrollLeft = $scrollWrapper.scrollWidth - $containerWidth;
    return;
  }

  const tagListDom = document.getElementsByClassName('tags-view-item');
  const currentIndex = visitedViews.value.findIndex(item => item === currentTag);
  
  if (currentIndex === -1) return;
  
  let prevTag = null;
  let nextTag = null;
  
  for (const k in tagListDom) {
    if (k !== 'length' && Object.hasOwnProperty.call(tagListDom, k)) {
      const dom = tagListDom[k];
      if (dom.dataset.path === visitedViews.value[currentIndex - 1]?.path) {
        prevTag = dom;
      }
      if (dom.dataset.path === visitedViews.value[currentIndex + 1]?.path) {
        nextTag = dom;
      }
    }
  }

  if (!prevTag || !nextTag) return;

  const afterNextTagOffsetLeft = nextTag.offsetLeft + nextTag.offsetWidth + tagAndTagSpacing.value;
  const beforePrevTagOffsetLeft = prevTag.offsetLeft - tagAndTagSpacing.value;

  if (afterNextTagOffsetLeft > $scrollWrapper.scrollLeft + $containerWidth) {
    $scrollWrapper.scrollLeft = afterNextTagOffsetLeft - $containerWidth;
  } else if (beforePrevTagOffsetLeft < $scrollWrapper.scrollLeft) {
    $scrollWrapper.scrollLeft = beforePrevTagOffsetLeft;
  }
}

defineExpose({
  moveToTarget,
});
</script>

<style lang='scss' scoped>
.scroll-container {
  white-space: nowrap;
  position: relative;
  overflow: hidden;
  width: 100%;
  :deep(.el-scrollbar__bar) {
    bottom: 0px;
  }
  :deep(.el-scrollbar__wrap) {
    height: 39px;
  }
}
</style>