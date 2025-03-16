<!-- CRUD表格组件 -->
<template>
  <div class="crud-table">
    <el-table
      :data="data"
      style="width: 100%"
      v-loading="loading"
      border
      stripe
    >
      <el-table-column
        v-for="column in columns"
        :key="column.prop"
        v-bind="column"
      >
        <template #default="scope" v-if="column.type === 'status'">
          <el-tag :type="scope.row[column.prop] ? 'success' : 'danger'">
            {{ scope.row[column.prop] ? '启用' : '禁用' }}
          </el-tag>
        </template>
        <template #default="scope" v-else-if="column.type === 'operation'">
          <el-button-group>
            <el-button
              type="primary"
              size="small"
              @click="handleEdit(scope.row)"
            >
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDelete(scope.row)"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </el-button-group>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { Edit, Delete } from '@element-plus/icons-vue';

defineProps({
  data: {
    type: Array,
    required: true
  },
  columns: {
    type: Array,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['edit', 'delete']);

const handleEdit = (row) => {
  emit('edit', row);
};

const handleDelete = (row) => {
  emit('delete', row);
};
</script>

<style scoped>
.crud-table {
  margin: 20px 0;
}
</style> 