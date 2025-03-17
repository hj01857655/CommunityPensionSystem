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
        <el-table-column label="老人姓名" width="120">
          <template #default="scope">
            <a href="javascript:void(0);" @click="handleViewElder(scope.row.elderId)">
              {{ getElderName(scope.row.elderId) }}
            </a>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="360" fixed="right">
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
              <el-button
                v-if="scope.row.elderId"
                type="warning"
                size="small"
                :loading="unbinding"
                @click="handleUnbind(scope.row)"
              >
                <el-icon><Close /></el-icon>
                解绑
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
      <!-- 查看亲属详情 -->
      <el-dialog v-model="viewKinDialogVisible" title="查看亲属详情" width="50%">
        <!-- 家属信息以不可编辑的表单形式展示 -->
        <el-form :model="selectedKin || {}" label-width="100px" class="kin-form">
          <el-form-item label="亲属姓名">
            <el-input v-model="selectedKin.name" disabled />
          </el-form-item>
          <el-form-item label="关系">
            <el-input v-model="selectedKin.relationship" disabled />
          </el-form-item>
          <el-form-item label="老人姓名">
            <el-input v-model="elderName" disabled />
          </el-form-item>
        </el-form>
      
        <!-- 绑定的老人信息以卡片形式展示 -->
        <el-card v-if="elderDetails" class="elder-details-card">
          <div v-if="elderDetails.name"><span>老人姓名:</span> {{ elderDetails.name }}</div>
          <div v-if="elderDetails.gender"><span>性别:</span> {{ elderDetails.gender === 'male' ? '男' : '女' }}</div>
          <div v-if="elderDetails.birthday"><span>生日:</span> {{ elderDetails.birthday }}</div>
          <div v-if="elderDetails.age"><span>年龄:</span> {{ elderDetails.age }}</div>
          <div v-if="elderDetails.idCard"><span>身份证:</span> {{ elderDetails.idCard }}</div>
          <div v-if="elderDetails.emergencyContactName"><span>紧急联系人:</span> {{ elderDetails.emergencyContactName }}</div>
          <div v-if="elderDetails.emergencyContactPhone"><span>紧急联系电话:</span> {{ elderDetails.emergencyContactPhone }}</div>
          <div v-if="elderDetails.healthCondition"><span>健康状况:</span> {{ elderDetails.healthCondition }}</div>
          <div v-if="elderDetails.remark"><span>备注:</span> {{ elderDetails.remark }}</div>
        </el-card>
        <div v-else-if="selectedKin && selectedKin.elderId" class="no-elder-info">
          <el-empty description="正在加载老人信息..." v-if="loading"></el-empty>
          <el-empty description="未能获取到老人信息" v-else></el-empty>
        </div>
        <div v-else class="no-elder-info">
          <el-empty description="该亲属未绑定老人"></el-empty>
        </div>
        <template #footer>
          <el-button @click="viewKinDialogVisible = false">关闭</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Plus, Edit, Delete, View, Link, Close } from '@element-plus/icons-vue';
import { useKinStore } from '@/stores/back/kinStore';
import { useElderStore } from '@/stores/back/elderStore';

// 初始化store
const kinStore = useKinStore();//亲属管理store
const elderStore = useElderStore();//老人管理store

// 状态
const binding = ref(false);
const unbinding = ref(false);

// 表单引用
const editKinFormRef = ref(null);

// 表单数据
const editKinForm = ref({
  id: null,
  name: '',
  relationship: '',
  elder_id: null
});

// 获取亲属列表数据
const fetchKinList = async () => {
  await kinStore.fetchKins();
};

// 在组件挂载时获取数据
onMounted(async () => {
  await fetchKinList();
  await elderStore.fetchElders(); // 获取老人列表用于关联显示
  await elderStore.fetchUnboundElders(); // 获取未绑定家属的老人列表
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
  return elder ? elder.name : '无';
};

// 从store获取状态
const loading = computed(() => kinStore.loading);
const searchQuery = ref('');
const currentPage = computed(() => kinStore.currentPage);
const pageSize = computed(() => kinStore.pageSize);
const total = computed(() => kinStore.total);

// 表单引用
const addKinFormRef = ref(null);


// 对话框显示状态
const addKinDialogVisible = ref(false); // 添加亲属对话框
const editKinDialogVisible = ref(false); // 编辑亲属对话框
const viewKinDialogVisible = ref(false); // 查看亲属详情对话框

// 计算亲属列表
const filteredKins = computed(() => kinStore.kinList);

// 初始化选中的亲属信息
const selectedKin = ref({
  name: '',
  relationship: '',
  elderId: null
});

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
const handleEdit = async (row) => {
  editKinDialogVisible.value = true;
  editKinForm.value = {
    id: row.id,
    name: row.name,
    relationship: row.relationship,
    elder_id: row.elderId
  };

  // 获取老人姓名
  const elder = elderStore.elderList.find(elder => elder.id === row.elderId);
  if (elder) {
    editKinForm.value.elderName = elder.name;
  }
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
  selectedKin.value = row; // 保存当前选中的亲属信息

  if (row.elderId) {
    try {
      const elderDetailsData = await kinStore.fetchElderByElderId(row.elderId);
      if (elderDetailsData) {
        elderDetails.value = elderDetailsData; // 更新视图绑定
        console.log('查看亲属详情', elderDetailsData);
      } else {
        ElMessage.error('未能获取到老人信息');
      }
    } catch (error) {
      console.error('查看亲属详情出错:', error);
      ElMessage.error('查看亲属详情失败');
    }
  } else {
    elderDetails.value = null; // 没有elderId时，老人信息为空
  }

  viewKinDialogVisible.value = true; // 显示查看详情弹出框
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

// 老人详细信息
const elderDetails = ref(null);

// 计算属性，用于获取老人姓名
const elderName = computed(() => {
  return getElderName(selectedKin.value?.elderId);
});

// 查看老人信息
const handleViewElder = async (elderId) => {
  if (!elderId) {
    ElMessage.error('无效的老人ID');
    return;
  }

  try {
    const elderDetailsData = await kinStore.fetchElderByElderId(elderId);
    if (elderDetailsData) {
      elderDetails.value = elderDetailsData; // 更新视图绑定
      console.log('查看老人详情', elderDetailsData);
      viewKinDialogVisible.value = true; // 显示查看详情弹出框
    } else {
      ElMessage.error('未能获取到老人信息');
    }
  } catch (error) {
    console.error('查看老人详情出错:', error);
    ElMessage.error('查看老人详情失败');
  }
};

// 绑定老人
const handleBind = async (row) => {
  binding.value = true;
  try {
    const success = await kinStore.bindElder(row.id, row.elderId);
    if (success) {
      ElMessage.success('绑定成功');
      fetchKinList(); // 重新获取列表
    } else {
      ElMessage.error('绑定失败');
    }
  } catch (error) {
    console.error('绑定出错:', error);
    ElMessage.error('绑定失败');
  } finally {
    binding.value = false;
  }
};

// 解绑老人
const handleUnbind = async (row) => {
  unbinding.value = true;
  try {
    const success = await kinStore.unbindElder(row.id);
    if (success) {
      ElMessage.success('解绑成功');
      fetchKinList(); // 重新获取列表
    } else {
      ElMessage.error('解绑失败');
    }
  } catch (error) {
    console.error('解绑出错:', error);
    ElMessage.error('解绑失败');
  } finally {
    unbinding.value = false;
  }
};

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
  console.log('编辑亲属提交', editKinForm.value);
  if (!editKinFormRef.value) return;
  editKinFormRef.value.validate(async (valid) => {
    if (valid) {
      const success = await kinStore.updateKin(editKinForm.value);
      if (success) {
        editKinDialogVisible.value = false;
        ElMessage.success('更新成功');
      } else {
        ElMessage.error('更新失败');
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
  margin: 16px 0;
  border-radius: 4px;
  overflow-x: auto;
}

.el-dialog {
  border-radius: 8px;
}

.el-dialog__body {
  padding: 20px 40px;
  background-color: #f9f9f9;
}

.el-dialog__footer {
  padding: 10px 20px 20px;
  text-align: right;
}

.el-card {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.el-card div {
  margin-bottom: 10px;
  font-size: 14px;
  color: #606266;
}

.el-card div span {
  font-weight: bold;
  color: #303133;
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
  overflow-x: auto;
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

.kin-details-card {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f0f9ff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.elder-details-card {
  margin-top: 20px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.kin-form {
  margin-bottom: 20px;
}

.kin-table a {
  color: #409eff;
  text-decoration: none;
  cursor: pointer;
}

.kin-table a:hover {
  text-decoration: underline;
}
</style>
