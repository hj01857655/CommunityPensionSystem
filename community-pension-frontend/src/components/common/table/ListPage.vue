<template>
  <div class="list-page-container">
    <!-- 搜索表单 -->
    <SearchForm
      v-if="showSearch"
      v-model="queryParams"
      :auto-search="autoSearch"
      @search="handleSearch"
      @reset="handleReset"
    >
      <slot name="search"></slot>
      <template #buttons>
        <slot name="searchButtons"></slot>
      </template>
    </SearchForm>

    <!-- 表格工具栏 -->
    <TableToolbar
      v-if="showToolbar"
      :show-add="showAdd"
      :show-batch-delete="showBatchDelete"
      :show-refresh="showRefresh"
      :show-export="showExport"
      :show-column-setting="showColumnSetting"
      :selected-rows="selectedRows"
      @add="handleAdd"
      @batch-delete="handleBatchDelete"
      @refresh="handleRefresh"
      @export="handleExport"
      @column-setting="handleColumnSetting"
    >
      <template #left>
        <slot name="toolbarLeft"></slot>
      </template>
      <template #leftButtons>
        <slot name="toolbarLeftButtons"></slot>
      </template>
      <template #right>
        <slot name="toolbarRight"></slot>
      </template>
      <template #rightButtons>
        <slot name="toolbarRightButtons"></slot>
      </template>
    </TableToolbar>

    <!-- 数据表格 -->
    <DataTable
      :data="tableData"
      :loading="loading"
      :border="tableBorder"
      :stripe="tableStripe"
      :height="tableHeight"
      :max-height="tableMaxHeight"
      :row-key="rowKey"
      :show-header="showHeader"
      :highlight-current-row="highlightCurrentRow"
      :row-class-name="rowClassName"
      :show-selection="showSelection"
      :show-index="showIndex"
      :show-operation="showOperation"
      :operation-width="operationWidth"
      :show-view="showView"
      :show-edit="showEdit"
      :show-delete="showDelete"
      :tree-props="treeProps"
      :default-expand-all="defaultExpandAll"
      @selection-change="handleSelectionChange"
      @row-click="handleRowClick"
      @view="handleView"
      @edit="handleEdit"
      @delete="handleDelete"
    >
      <slot></slot>
      <template #operation>
        <slot name="operation"></slot>
      </template>
      <template #extraOperation="scope">
        <slot name="extraOperation" :row="scope.row"></slot>
      </template>
    </DataTable>

    <!-- 分页组件 -->
    <Pagination
      v-if="showPagination"
      v-model:page="page"
      v-model:limit="limit"
      :total="total"
      :page-sizes="pageSizes"
      :layout="paginationLayout"
      :background="paginationBackground"
      :auto-scroll="autoScroll"
      :hidden="paginationHidden"
      @pagination="handlePagination"
    />

    <!-- 其他插槽 -->
    <slot name="footer"></slot>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue';
import DataTable from './DataTable.vue';
import Pagination from './Pagination.vue';
import SearchForm from './SearchForm.vue';
import TableToolbar from './TableToolbar.vue';

const props = defineProps({
  // 是否显示搜索表单
  showSearch: {
    type: Boolean,
    default: true
  },
  // 是否在表单值变化时自动搜索
  autoSearch: {
    type: Boolean,
    default: true
  },
  // 是否显示工具栏
  showToolbar: {
    type: Boolean,
    default: true
  },
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
  // 表格数据
  data: {
    type: Array,
    default: () => []
  },
  // 加载状态
  loading: {
    type: Boolean,
    default: false
  },
  // 是否显示表格边框
  tableBorder: {
    type: Boolean,
    default: true
  },
  // 是否显示斑马纹
  tableStripe: {
    type: Boolean,
    default: true
  },
  // 表格高度
  tableHeight: {
    type: [String, Number],
    default: null
  },
  // 表格最大高度
  tableMaxHeight: {
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
  },
  // 是否显示分页
  showPagination: {
    type: Boolean,
    default: true
  },
  // 分页大小选项
  pageSizes: {
    type: Array,
    default: () => [10, 20, 30, 50]
  },
  // 分页布局
  paginationLayout: {
    type: String,
    default: 'total, sizes, prev, pager, next, jumper'
  },
  // 分页背景
  paginationBackground: {
    type: Boolean,
    default: true
  },
  // 是否自动滚动
  autoScroll: {
    type: Boolean,
    default: true
  },
  // 是否隐藏分页
  paginationHidden: {
    type: Boolean,
    default: false
  },
  // 是否自动加载数据
  autoLoad: {
    type: Boolean,
    default: true
  },
  // 查询参数
  queryParams: {
    type: Object,
    default: () => ({})
  },
  // 初始页码
  initialPage: {
    type: Number,
    default: 1
  },
  // 初始每页条数
  initialLimit: {
    type: Number,
    default: 10
  },
  // 总条数
  total: {
    type: Number,
    default: 0
  }
});

const emit = defineEmits([
  'update:queryParams',
  'search',
  'reset',
  'add',
  'batch-delete',
  'refresh',
  'export',
  'column-setting',
  'selection-change',
  'row-click',
  'view',
  'edit',
  'delete',
  'pagination'
]);

// 表格数据
const tableData = computed(() => props.data);

// 查询参数
const queryParams = reactive({ ...props.queryParams });

// 监听 queryParams 变化
watch(() => props.queryParams, (newVal) => {
  Object.keys(queryParams).forEach(key => {
    delete queryParams[key];
  });
  Object.assign(queryParams, newVal);
}, { deep: true });

// 监听本地 queryParams 变化
watch(queryParams, (newVal) => {
  emit('update:queryParams', { ...newVal });
}, { deep: true });

// 分页相关
const page = ref(props.initialPage);
const limit = ref(props.initialLimit);

// 选中的行数据
const selectedRows = ref([]);

// 生命周期钩子
onMounted(() => {
  if (props.autoLoad) {
    handleSearch();
  }
});

// 处理搜索
const handleSearch = () => {
  page.value = 1;
  emit('search', {
    ...queryParams,
    page: page.value,
    limit: limit.value
  });
};

// 处理重置
const handleReset = () => {
  emit('reset');
};

// 处理新增
const handleAdd = () => {
  emit('add');
};

// 处理批量删除
const handleBatchDelete = (rows) => {
  emit('batch-delete', rows);
};

// 处理刷新
const handleRefresh = () => {
  emit('refresh');
  handleSearch();
};

// 处理导出
const handleExport = () => {
  emit('export', {
    ...queryParams,
    page: page.value,
    limit: limit.value
  });
};

// 处理列设置
const handleColumnSetting = () => {
  emit('column-setting');
};

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection;
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

// 处理分页
const handlePagination = ({ page: newPage, limit: newLimit }) => {
  emit('pagination', {
    page: newPage,
    limit: newLimit,
    ...queryParams
  });
  emit('search', {
    ...queryParams,
    page: newPage,
    limit: newLimit
  });
};
</script>

<style scoped>
.list-page-container {
  width: 100%;
}
</style>
