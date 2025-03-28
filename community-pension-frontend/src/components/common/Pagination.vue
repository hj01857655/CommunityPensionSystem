<template>
  <div class="pagination-container">
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  total: {
    type: Number,
    required: true
  },
  page: {
    type: Number,
    default: 1
  },
  limit: {
    type: Number,
    default: 10
  }
});

const emit = defineEmits(['update:page', 'update:limit', 'pagination']);

const currentPage = computed({
  get: () => props.page,
  set: (val) => emit('update:page', val)
});

const pageSize = computed({
  get: () => props.limit,
  set: (val) => emit('update:limit', val)
});

const handleSizeChange = (val) => {
  emit('pagination', { page: 1, limit: val });
};

const handleCurrentChange = (val) => {
  emit('pagination', { page: val, limit: props.limit });
};
</script>

<style scoped>
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 