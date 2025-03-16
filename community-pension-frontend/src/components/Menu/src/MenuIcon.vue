<!-- 菜单图标组件 -->
<template>
  <component :is="iconComponent" v-if="iconComponent" />
  <i v-else :class="icon" />
</template>

<script setup>
import { computed } from 'vue'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const props = defineProps({
  icon: {
    type: String,
    required: true
  }
})

// 解析图标组件
const iconComponent = computed(() => {
  // 如果是 Element Plus 图标
  if (ElementPlusIconsVue[props.icon]) {
    return ElementPlusIconsVue[props.icon]
  }
  // 如果是自定义类名图标
  return null
})
</script>

<style scoped>
i {
  margin-right: 5px;
  width: 24px;
  text-align: center;
  font-size: 18px;
  vertical-align: middle;
}
</style> 