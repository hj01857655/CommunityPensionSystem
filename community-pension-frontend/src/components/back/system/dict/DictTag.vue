<template>
  <el-tag :type="type">{{ label }}</el-tag>
</template>

<script setup>
import { computed } from 'vue';

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

// 这里应该从字典数据中获取对应的标签和类型
// 暂时使用模拟数据
const label = computed(() => {
  const dictMap = {
    sys_normal_disable: {
      '0': '正常',
      '1': '停用'
    },
    sys_show_hide: {
      '0': '显示',
      '1': '隐藏'
    }
  };
  return dictMap[props.dictType]?.[props.value] || props.value;
});

const type = computed(() => {
  if (props.dictType === 'sys_normal_disable') {
    return props.value === '0' ? 'success' : 'danger';
  }
  if (props.dictType === 'sys_show_hide') {
    return props.value === '0' ? 'success' : 'info';
  }
  return '';
});
</script> 