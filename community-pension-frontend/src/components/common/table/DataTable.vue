<template>
  <div class="data-table-container">
    <el-table
      v-loading="loading"
      :data="data"
      :border="border"
      :stripe="stripe"
      :height="height"
      :max-height="maxHeight"
      :row-key="rowKey"
      :show-header="showHeader"
      :highlight-current-row="highlightCurrentRow"
      :row-class-name="rowClassName"
      :tree-props="treeProps"
      :default-expand-all="defaultExpandAll"
      @selection-change="handleSelectionChange"
      @row-click="handleRowClick"
    >
      <el-table-column v-if="showSelection" type="selection" width="55" align="center" />
      <el-table-column v-if="showIndex" type="index" width="60" align="center" label="序号" />
      <slot></slot>
      <template v-if="showOperation && !$slots.operation">
        <el-table-column label="操作" align="center" :width="operationWidth" fixed="right">
          <template #default="scope">
            <el-button v-if="showView" type="primary" link @click.stop="handleView(scope.row)">查看</el-button>
            <el-button v-if="showEdit" type="primary" link @click.stop="handleEdit(scope.row)">编辑</el-button>
            <el-button v-if="showDelete" type="danger" link @click.stop="handleDelete(scope.row)">删除</el-button>
            <slot name="extraOperation" :row="scope.row"></slot>
          </template>
        </el-table-column>
      </template>
      <slot name="operation"></slot>
      <template #empty>
        <div class="empty-data">
          <el-empty description="暂无数据" />
        </div>
      </template>
    </el-table>
  </div>
</template>

<script setup>
defineProps({
  // 表格数据
  data: {
    type: Array,
    required: true
  },
  // 加载状态
  loading: {
    type: Boolean,
    default: false
  },
  // 是否显示边框
  border: {
    type: Boolean,
    default: true
  },
  // 是否显示斑马纹
  stripe: {
    type: Boolean,
    default: true
  },
  // 表格高度
  height: {
    type: [String, Number],
    default: null
  },
  // 表格最大高度
  maxHeight: {
    type: [String, Number],
    default: null
  },
  // 行数据的Key
  rowKey: {
    type: String,
    default: 'id'
  },
  // 是否显示表头
  showHeader: {
    type: Boolean,
    default: true
  },
  // 是否高亮当前行
  highlightCurrentRow: {
    type: Boolean,
    default: false
  },
  // 行的 className
  rowClassName: {
    type: [Function, String],
    default: ''
  },
  // 是否显示选择列
  showSelection: {
    type: Boolean,
    default: false
  },
  // 是否显示序号列
  showIndex: {
    type: Boolean,
    default: false
  },
  // 是否显示操作列
  showOperation: {
    type: Boolean,
    default: true
  },
  // 操作列宽度
  operationWidth: {
    type: [String, Number],
    default: 150
  },
  // 是否显示查看按钮
  showView: {
    type: Boolean,
    default: true
  },
  // 是否显示编辑按钮
  showEdit: {
    type: Boolean,
    default: true
  },
  // 是否显示删除按钮
  showDelete: {
    type: Boolean,
    default: true
  },
  // 树形数据配置
  treeProps: {
    type: Object,
    default: () => ({
      children: 'children',
      hasChildren: 'hasChildren'
    })
  },
  // 是否默认展开所有树形节点
  defaultExpandAll: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['selection-change', 'row-click', 'view', 'edit', 'delete']);

// 处理选择变化
const handleSelectionChange = (selection) => {
  emit('selection-change', selection);
};

// 处理行点击
const handleRowClick = (row, column, event) => {
  emit('row-click', row, column, event);
};

// 处理查看
const handleView = (row) => {
  emit('view', row);
};

// 处理编辑
const handleEdit = (row) => {
  emit('edit', row);
};

// 处理删除
const handleDelete = (row) => {
  emit('delete', row);
};
</script>

<style scoped>
.data-table-container {
  width: 100%;
}

.empty-data {
  padding: 30px 0;
}
</style>
