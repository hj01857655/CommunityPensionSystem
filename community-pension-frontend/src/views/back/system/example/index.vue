<template>
  <div class="app-container">
    <ListPage 
      page-key="system/example"
      @search="handleSearch"
      @reset="handleReset"
      @selection-change="handleSelectionChange"
      @page-change="handlePageChange"
      @limit-change="handleLimitChange"
      ref="listPageRef"
    >
      <!-- 工具栏左侧按�?-->
      <template #toolbar-left>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增</el-button>
        <el-button type="danger" :icon="Delete" :disabled="!selectedRows.length" @click="handleBatchDelete">批量删除</el-button>
      </template>
      
      <!-- 工具栏右侧按�?-->
      <template #toolbar-right>
        <el-button :icon="Refresh" circle @click="refreshTable" />
      </template>
      
      <!-- 表格�?-->
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column prop="name" label="名称" min-width="120" show-overflow-tooltip />
      <el-table-column prop="code" label="编码" min-width="120" show-overflow-tooltip />
      <el-table-column prop="type" label="类型" width="100">
        <template #default="{ row }">
          <el-tag :type="row.type === '1' ? 'success' : row.type === '2' ? 'warning' : 'info'">
            {{ getTypeName(row.type) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状�? width="100">
        <template #default="{ row }">
          <el-switch
            v-model="row.status"
            :active-value="'1'"
            :inactive-value="'0'"
            @change="handleStatusChange(row)"
          />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160" show-overflow-tooltip />
      <el-table-column label="操作" width="180" align="center" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
          <el-button type="primary" link :icon="View" @click="handleView(row)">查看</el-button>
          <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </ListPage>
    
    <!-- 新增/编辑对话�?-->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
      append-to-body
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名�? />
        </el-form-item>
        <el-form-item label="编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入编�? />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型" style="width: 100%">
            <el-option label="类型一" value="1" />
            <el-option label="类型�? value="2" />
            <el-option label="类型�? value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状�? prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="1">启用</el-radio>
            <el-radio label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备�? />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">�?�?/el-button>
          <el-button type="primary" @click="submitForm">�?�?/el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, Delete, Edit, View, Refresh } from '@element-plus/icons-vue';
import ListPage from '@/components/common/table/ListPage.vue';

// 页面状�?const listPageRef = ref(null);
const selectedRows = ref([]);
const tableData = ref([]);
const pagination = reactive({
  page: 1,
  limit: 10,
  total: 0
});

// 搜索条件
const searchParams = reactive({});

// 对话框状�?const dialogVisible = ref(false);
const dialogType = ref('add'); // add, edit, view
const dialogTitle = computed(() => {
  return dialogType.value === 'add' ? '新增示例' : 
         dialogType.value === 'edit' ? '编辑示例' : '查看示例';
});

// 表单数据
const formRef = ref(null);
const form = reactive({
  id: '',
  name: '',
  code: '',
  type: '1',
  status: '1',
  remark: ''
});

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入名�?, trigger: 'blur' }],
  code: [{ required: true, message: '请输入编�?, trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择状�?, trigger: 'change' }]
};

// 获取类型名称
const getTypeName = (type) => {
  const typeMap = {
    '1': '类型一',
    '2': '类型�?,
    '3': '类型�?
  };
  return typeMap[type] || '未知类型';
};

// 加载数据
const loadData = () => {
  // 模拟API请求
  setTimeout(() => {
    // 生成模拟数据
    const mockData = Array.from({ length: 20 }, (_, index) => ({
      id: index + 1,
      name: `示例名称${index + 1}`,
      code: `CODE${String(index + 1).padStart(3, '0')}`,
      type: String(Math.floor(Math.random() * 3) + 1),
      status: Math.random() > 0.3 ? '1' : '0',
      createTime: new Date().toLocaleString(),
      remark: `这是示例数据${index + 1}的备注信息`
    }));
    
    // 计算分页
    const start = (pagination.page - 1) * pagination.limit;
    const end = start + pagination.limit;
    const filteredData = mockData.filter(item => {
      // 根据搜索条件过滤
      if (searchParams.name && !item.name.includes(searchParams.name)) return false;
      if (searchParams.code && !item.code.includes(searchParams.code)) return false;
      if (searchParams.type && item.type !== searchParams.type) return false;
      if (searchParams.status && item.status !== searchParams.status) return false;
      return true;
    });
    
    tableData.value = filteredData.slice(start, end);
    pagination.total = filteredData.length;
    
    // 更新ListPage组件
    if (listPageRef.value) {
      listPageRef.value.setTableData(tableData.value);
      listPageRef.value.setPagination({
        page: pagination.page,
        limit: pagination.limit,
        total: pagination.total
      });
    }
  }, 300);
};

// 搜索处理
const handleSearch = (params) => {
  Object.assign(searchParams, params);
  pagination.page = 1;
  loadData();
};

// 重置处理
const handleReset = () => {
  Object.keys(searchParams).forEach(key => {
    searchParams[key] = '';
  });
  pagination.page = 1;
  loadData();
};

// 选择行变�?const handleSelectionChange = (selection) => {
  selectedRows.value = selection;
};

// 页码变化
const handlePageChange = (page) => {
  pagination.page = page;
  loadData();
};

// 每页条数变化
const handleLimitChange = (limit) => {
  pagination.limit = limit;
  pagination.page = 1;
  loadData();
};

// 刷新表格
const refreshTable = () => {
  loadData();
};

// 新增
const handleAdd = () => {
  dialogType.value = 'add';
  dialogVisible.value = true;
  // 重置表单
  Object.keys(form).forEach(key => {
    form[key] = key === 'type' || key === 'status' ? '1' : '';
  });
};

// 编辑
const handleEdit = (row) => {
  dialogType.value = 'edit';
  dialogVisible.value = true;
  // 填充表单
  Object.keys(form).forEach(key => {
    form[key] = row[key];
  });
};

// 查看
const handleView = (row) => {
  dialogType.value = 'view';
  dialogVisible.value = true;
  // 填充表单
  Object.keys(form).forEach(key => {
    form[key] = row[key];
  });
};

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除"${row.name}"吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功');
    loadData();
  }).catch(() => {});
};

// 批量删除
const handleBatchDelete = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请至少选择一条记�?);
    return;
  }
  
  ElMessageBox.confirm(`确认删除选中�?{selectedRows.value.length}条记录吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('批量删除成功');
    loadData();
  }).catch(() => {});
};

// 状态变�?const handleStatusChange = (row) => {
  const statusText = row.status === '1' ? '启用' : '禁用';
  ElMessage.success(`�?{statusText}�?{row.name}`);
};

// 提交表单
const submitForm = () => {
  if (dialogType.value === 'view') {
    dialogVisible.value = false;
    return;
  }
  
  formRef.value.validate((valid) => {
    if (valid) {
      ElMessage.success(dialogType.value === 'add' ? '新增成功' : '修改成功');
      dialogVisible.value = false;
      loadData();
    }
  });
};

// 初始�?onMounted(() => {
  // 初始加载数据
  loadData();
});
</script>

<style scoped>
.app-container {
  padding: 20px;
}
</style>

