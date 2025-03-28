<template>
  <el-tag :type="tagType">{{ label }}</el-tag>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  options: {
    type: Array,
    default: () => []
  },
  value: {
    type: [String, Number],
    default: ''
  }
});

const label = computed(() => {
  const option = props.options.find(item => item.value === props.value);
  return option ? option.label : '';
});

const tagType = computed(() => {
  const option = props.options.find(item => item.value === props.value);
  return option ? option.tagType || '' : '';
});
</script> 