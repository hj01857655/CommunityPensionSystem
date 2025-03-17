<template>
  <div class="data-table">
    <el-table
      v-bind="$attrs"
      v-loading="loading"
      :data="data"
      border
      stripe
      :height="height"
      :max-height="maxHeight"
      @selection-change="handleSelectionChange"
    >
      <el-table-column v-if="showSelection" type="selection" width="55" align="center" />
      <el-table-column v-if="showIndex" type="index" label="序号" width="80" align="center" />
      
      <slot></slot>
      
      <el-table-column v-if="showActions" label="操作" :width="actionsWidth" fixed="right" align="center">
        <template #default="scope">
          <slot name="actions" :row="scope.row" :index="scope.$index">
            <el-button v-if="showViewAction" type="success" size="small" @click="handleAction('view', scope.row)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button v-if="showEditAction" type="primary" size="small" @click="handleAction('edit', scope.row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button v-if="showDeleteAction" type="danger" size="small" @click="handleAction('delete', scope.row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </slot>
        </template>
      </el-table-column>
    </el-table>
    
    <el-pagination
      v-if="showPagination"
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :page-sizes="pageSizes"
      :layout="paginationLayout"
      :total="total"
      class="pagination"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { View, Edit, Delete } from '@element-plus/icons-vue';

const props = defineProps({
  data: {
    type: Array,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  },
  showSelection: {
    type: Boolean,
    default: false
  },
  showIndex: {
    type: Boolean,
    default: true
  },
  showActions: {
    type: Boolean,
    default: true
  },
  actionsWidth: {
    type: [String, Number],
    default: 220
  },
  showViewAction: {
    type: Boolean,
    default: true
  },
  showEditAction: {
    type: Boolean,
    default: true
  },
  showDeleteAction: {
    type: Boolean,
    default: true
  },
  showPagination: {
    type: Boolean,
    default: true
  },
  page: {
    type: Number,
    default: 1
  },
  size: {
    type: Number,
    default: 10
  },
  total: {
    type: Number,
    default: 0
  },
  pageSizes: {
    type: Array,
    default: () => [10, 20, 50, 100]
  },
  paginationLayout: {
    type: String,
    default: 'total, sizes, prev, pager, next, jumper'
  },
  height: {
    type: [String, Number],
    default: null
  },
  maxHeight: {
    type: [String, Number],
    default: null
  }
});

const emit = defineEmits([
  'selection-change', 
  'page-change', 
  'size-change', 
  'action',
  'view',
  'edit',
  'delete'
]);

const currentPage = ref(props.page);
const pageSize = ref(props.size);

watch(() => props.page, (newVal) => {
  currentPage.value = newVal;
});

watch(() => props.size, (newVal) => {
  pageSize.value = newVal;
});

const handleSelectionChange = (selection) => {
  emit('selection-change', selection);
};

const handleSizeChange = (size) => {
  pageSize.value = size;
  emit('size-change', size);
  emit('page-change', { page: currentPage.value, size });
};

const handleCurrentChange = (page) => {
  currentPage.value = page;
  emit('page-change', { page, size: pageSize.value });
};

const handleAction = (action, row) => {
  emit('action', { action, row });
  emit(action, row);
};
</script>

<style scoped>
.data-table {
  width: 100%;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 