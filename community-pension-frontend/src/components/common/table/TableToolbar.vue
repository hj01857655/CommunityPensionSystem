<template>
  <div class="table-toolbar">
    <div class="left-buttons">
      <slot name="left">
        <el-button v-if="showAdd" type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增
        </el-button>
        <el-button v-if="showBatchDelete && selectedRows.length > 0" type="danger" @click="handleBatchDelete">
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
        <slot name="leftButtons"></slot>
      </slot>
    </div>
    <div class="right-buttons">
      <slot name="right">
        <el-button v-if="showRefresh" @click="handleRefresh">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
        <el-button v-if="showExport" @click="handleExport">
          <el-icon><Download /></el-icon>
          导出
        </el-button>
        <el-tooltip content="列设置" placement="top" v-if="showColumnSetting">
          <el-button @click="handleColumnSetting">
            <el-icon><Setting /></el-icon>
          </el-button>
        </el-tooltip>
        <slot name="rightButtons"></slot>
      </slot>
    </div>
  </div>
</template>

<script setup>
import { Plus, Delete, Refresh, Download, Setting } from '@element-plus/icons-vue';

const props = defineProps({
  // 是否显示新增按钮
  showAdd: {
    type: Boolean,
    default: true
  },
  // 是否显示批量删除按钮
  showBatchDelete: {
    type: Boolean,
    default: true
  },
  // 是否显示刷新按钮
  showRefresh: {
    type: Boolean,
    default: true
  },
  // 是否显示导出按钮
  showExport: {
    type: Boolean,
    default: false
  },
  // 是否显示列设置按钮
  showColumnSetting: {
    type: Boolean,
    default: false
  },
  // 选中的行数据
  selectedRows: {
    type: Array,
    default: () => []
  }
});

const emit = defineEmits(['add', 'batch-delete', 'refresh', 'export', 'column-setting']);

// 处理新增
const handleAdd = () => {
  emit('add');
};

// 处理批量删除
const handleBatchDelete = () => {
  emit('batch-delete', props.selectedRows);
};

// 处理刷新
const handleRefresh = () => {
  emit('refresh');
};

// 处理导出
const handleExport = () => {
  emit('export');
};

// 处理列设置
const handleColumnSetting = () => {
  emit('column-setting');
};
</script>

<style scoped>
.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.left-buttons, .right-buttons {
  display: flex;
  gap: 8px;
}
</style>
