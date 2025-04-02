<template>
  <el-tag :type="type">{{ label }}</el-tag>
</template>

<script setup>
import { computed } from 'vue';
import { useDict } from '@/utils/dict'

const props = defineProps({
  value: {
    type: [String, Number],
    required: true
  },
  dictType: {
    type: String,
    required: true
  }
});

// 使用字典数据
const dictData = useDict(props.dictType)

// 获取标签
const label = computed(() => {
  const item = dictData.value?.find(item => item.value === props.value)
  return item?.label || props.value
});

// 获取标签类型
const type = computed(() => {
  const item = dictData.value?.find(item => item.value === props.value)
  return item?.elTagType || ''
});
</script> 