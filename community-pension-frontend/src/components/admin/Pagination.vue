<template>
  <div class="pagination-container">
    <el-pagination
      v-bind="$attrs"
      @current-change="$emit('update:current-page', $event)"
      @size-change="$emit('update:page-size', $event)"
    />
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';
import { ElPagination } from 'element-plus';
import { computed } from 'vue';
const props = defineProps({
  currentPage: {
    type: Number,
    required: true
  },
  pageSize: {
    type: Number,
    required: true
  },
  total: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['update:current-page', 'update:page-size'])
</script> 