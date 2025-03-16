<!-- 分页组件 -->
<template>
  <div class="pagination-footer">
    <el-pagination
      :current-page="currentPage"
      :page-size="pageSize"
      :page-sizes="[10, 20, 30, 50]"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      @update:current-page="handleCurrentChange"
      @update:page-size="handleSizeChange"
    />
  </div>
</template>

<script setup>
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
});

const emit = defineEmits(['size-change', 'current-change', 'update:current-page', 'update:page-size']);

const handleSizeChange = (val) => {
  emit('size-change', val);
  emit('update:page-size', val);
};

const handleCurrentChange = (val) => {
  emit('current-change', val);
  emit('update:current-page', val);
};
</script>

<style scoped>
.pagination-footer {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>