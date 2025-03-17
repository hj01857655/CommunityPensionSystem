<template>
  <div class="kin-management">
    <el-card shadow="hover" class="main-card">
      <template #header>
        <div class="card-header">
          <h3>亲属管理</h3>
          <div class="header-actions">
            <el-input
              v-model="searchQuery"
              placeholder="搜索姓名/身份证号/电话"
              class="search-input"
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button type="success" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              添加亲属信息
            </el-button>
          </div>
        </div>
      </template>
      <!-- 家属表格 -->
      <el-table :data="filteredKins" style="width: 100%" v-loading="loading" border class="kin-table">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="relationship" label="关系" width="120" />
        <el-table-column prop="elderName" label="老人姓名" width="120">
          <template #default="scope">
            {{ getElderName(scope.row.elder_id) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="scope">
            <div style="display: flex; gap: 8px;">
              <el-button type="primary" size="small" @click="handleView(scope.row)">
                <el-icon><View /></el-icon>
                查看
              </el-button>
              <el-button type="primary" size="small" @click="handleEdit(scope.row)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button type="danger" size="small" @click="handleDelete(scope.row)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 30, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
      <!-- 添加亲属 -->
      <el-dialog v-model="addKinDialogVisible" title="添加亲属" width="50%">
        <el-form :model="addKinForm" :rules="addKinRules" ref="addKinFormRef" label-width="120px">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="addKinForm.name" />
          </el-form-item>
          <el-form-item label="关系" prop="relationship">
            <el-input v-model="addKinForm.relationship" />
          </el-form-item>
          <el-form-item label="老人姓名" prop="elderName">
            <el-select v-model="addKinForm.elder_id" placeholder="请选择老人">
              <el-option
                v-for="elder in elderStore.elderList"
                :key="elder.id"
                :label="elder.name"
                :value="elder.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleAddKin">添加</el-button>
            <el-button @click="addKinDialogVisible = false">取消</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
      <!-- 编辑亲属 -->
      <el-dialog v-model="editKinDialogVisible" title="编辑亲属" width="50%">
        <el-form :model="editKinForm" :rules="editKinRules" ref="editKinFormRef" label-width="120px">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="editKinForm.name" />
          </el-form-item>
          <el-form-item label="关系" prop="relationship">
            <el-input v-model="editKinForm.relationship" />
          </el-form-item>
          <el-form-item label="老人姓名" prop="elderName">
            <el-select v-model="editKinForm.elder_id" placeholder="请选择老人">
              <el-option
                v-for="elder in elderStore.elderList"
                :key="elder.id"
                :label="elder.name"
                :value="elder.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSave">保存</el-button>
            <el-button @click="editKinDialogVisible = false">取消</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Plus, Edit, Delete, View } from '@element-plus/icons-vue';
import { useKinStore } from '@/stores/back/kinStore';
import { useElderStore } from '@/stores/back/elderStore';

// 初始化store
const kinStore = useKinStore();
const elderStore = useElderStore();

// 获取亲属列表数据
const fetchKinList = async () => {
  await kinStore.fetchKins();
};

// 在组件挂载时获取数据
onMounted(async () => {
  await fetchKinList();
  await elderStore.fetchElders(); // 获取老人列表用于关联显示
});

// 监听分页和搜索条件变化
watch(() => kinStore.currentPage, fetchKinList);
watch(() => kinStore.pageSize, fetchKinList);
watch(() => kinStore.searchQuery, (newVal) => {
  if (newVal !== searchQuery.value) {
    searchQuery.value = newVal;
  }
});

const handleCurrentChange = (val) => {
  kinStore.currentPage = val;
};

const handleSizeChange = (val) => {
  kinStore.pageSize = val;
};

// 获取老人姓名
const getElderName = (elderId) => {
  const elder = elderStore.elderList.find(elder => elder.id === elderId);
  return elder ? elder.name : '未知';
};

// 从store获取状态
const loading = computed(() => kinStore.loading);
const searchQuery = ref('');
const currentPage = computed(() => kinStore.currentPage);
const pageSize = computed(() => kinStore.pageSize);
const total = computed(() => kinStore.total);

// 对话框显示状态
const addKinDialogVisible = ref(false); // 添加亲属对话框
const editKinDialogVisible = ref(false); // 编辑亲属对话框

// 计算亲属列表
const filteredKins = computed(() => kinStore.kinList);

// 添加亲属
const handleAdd = () => {
  addKinDialogVisible.value = true;
  addKinForm.value = {
    name: '',
    relationship: '',
    elder_id: null
  };
};

// 编辑亲属
const handleEdit = (row) => {
  editKinDialogVisible.value = true;
  editKinForm.value = {
    id: row.id,
    name: row.name,
    relationship: row.relationship,
    elder_id: row.elder_id
  };
};

// 删除亲属
const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该亲属吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const success = await kinStore.deleteKin(row.id);
    if (!success) {
      ElMessage.error('删除亲属失败');
    }
  }).catch(() => {
    ElMessage.info('已取消删除');
  });
};

// 查看亲属详情
const handleView = async (row) => {
  if (!row.elderId) {
    ElMessage.error('无效的老人ID');
    return;
  }
  
  try {
    const response = await kinStore.fetchKinsByElderId(row.elderId);
    console.log('查看亲属详情', response);
    if (response && response.code === 200) {
      console.log('查看亲属详情', response.data);
      ElMessage.info('查看亲属详情成功');
    } else {
      ElMessage.error(response.message || '查看亲属详情失败');
    }
  } catch (error) {
    console.error('查看亲属详情出错:', error);
    ElMessage.error('查看亲属详情失败');
  }
};

// 搜索
const handleSearch = () => {
  searchQuery.value = searchQuery.value.trim();
  kinStore.searchQuery = searchQuery.value;
  kinStore.currentPage = 1; // 重置到第一页
  fetchKinList();
};

// 添加亲属表单
const addKinForm = ref({
  name: '',
  relationship: '',
  elder_id: null
});

// 编辑亲属表单
const editKinForm = ref({
  name: '',
  relationship: '',
  elder_id: null
});

// 表单验证规则
const addKinRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  relationship: [
    { required: true, message: '请输入与老人的关系', trigger: 'blur' }
  ],
  elder_id: [
    { required: true, message: '请选择老人', trigger: 'change' }
  ]
};

// 编辑表单验证规则
const editKinRules = {
  ...addKinRules
};

// 添加亲属提交
const handleAddKin = () => {
  if (!addKinFormRef.value) return;
  addKinFormRef.value.validate(async (valid) => {
    if (valid) {
      const success = await kinStore.addKin(addKinForm.value);
      if (success) {
        addKinDialogVisible.value = false;
      }
    } else {
      return false;
    }
  });
};

// 编辑亲属提交
const handleSave = () => {
  if (!editKinFormRef.value) return;
  editKinFormRef.value.validate(async (valid) => {
    if (valid) {
      const success = await kinStore.updateKin(editKinForm.value);
      if (success) {
        editKinDialogVisible.value = false;
      }
    } else {
      return false;
    }
  });
};
</script>

<style scoped>
.kin-management {
  padding: 10px;
}

.main-card {
  margin-bottom: 20px;
  transition: all 0.3s;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.main-card:hover {
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.card-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-input {
  width: 280px;
  transition: all 0.3s;
}

.search-input:focus-within {
  width: 320px;
}

.filter-tags {
  margin: 16px 0;
}

.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  padding: 8px 0;
}

@media screen and (max-width: 768px) {
  .header-actions {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .search-input {
    width: 100%;
  }
  
  .header-actions .el-button {
    margin-top: 8px;
  }
  
  .filter-tags {
    overflow-x: auto;
    white-space: nowrap;
    padding-bottom: 8px;
  }
}

.el-table {
  margin: 15px 0;
  border-radius: 4px;
}

.el-dialog {
  border-radius: 8px;
}

.el-dialog__body {
  padding: 20px 40px;
}

.el-dialog__footer {
  padding: 10px 20px 20px;
  text-align: right;
}

.el-form-item {
  margin-bottom: 22px;
}

.el-form-item__label {
  font-weight: 500;
  color: #606266;
}

.el-form-item__content {
  margin-left: 0;
}

.el-form-item__error {
  margin-left: 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.form-item-group {
  margin-bottom: 20px;
}

.form-item-group .el-form-item {
  margin-bottom: 12px;
}

.drawer-content {
  padding: 20px;
}

.drawer-footer {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px;
  background-color: #fff;
  text-align: right;
  border-top: 1px solid #e4e7ed;
}

.el-button {
  padding: 8px 16px;
}

.el-button [class*='el-icon'] + span {
  margin-left: 6px;
}

.kin-table {
  margin: 16px 0;
  border-radius: 4px;
  overflow: hidden;
}

.kin-table :deep(th) {
  background-color: #f5f7fa !important;
  color: #606266;
  font-weight: 600;
}

.kin-table :deep(.el-table__row) {
  cursor: pointer;
  transition: all 0.2s;
}

.kin-table :deep(.el-table__row:hover) {
  background-color: #ecf5ff !important;
}
</style>
