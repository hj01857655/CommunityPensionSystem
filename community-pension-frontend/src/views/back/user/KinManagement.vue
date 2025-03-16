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
      <!--家属表格 -->
      <el-table :data="filteredKins" style="width: 100%" v-loading="loading" border class="kin-table">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="phone" label="电话" width="120" />
        <el-table-column prop="address" label="地址" width="180" />
        <el-table-column prop="relationship" label="关系" width="120" />
        <!-- 新增的老人信息列 -->
        <el-table-column prop="elderName" label="老人姓名" width="120">
          <template #default="scope">
            {{ getElderName(scope.row.elder_id) }}
          </template>
        </el-table-column>
        <!--备注-->
        <el-table-column prop="remarks" label="备注" width="180" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="updateTime" label="更新时间" width="180" />
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
      <el-pagination v-model="currentPage" :page-size="pageSize" :total="total" @size-change="handleSizeChange"
        @current-change="handleCurrentChange" />
      <!-- 添加亲属 -->
      <el-dialog v-model="addKinDialogVisible" title="添加亲属" width="50%">
        <el-form :model="addKinForm" :rules="addKinRules" ref="addKinFormRef" label-width="120px">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="addKinForm.name" />
          </el-form-item>
          <el-form-item label="电话" prop="phone">
            <el-input v-model="addKinForm.phone" />
          </el-form-item>
          <el-form-item label="地址" prop="address">
            <el-input v-model="addKinForm.address" />
          </el-form-item>
          <el-form-item label="关系" prop="relationship">
            <el-input v-model="addKinForm.relationship" />
          </el-form-item>
          <el-form-item label="老人姓名" prop="elderName">
            <el-input v-model="addKinForm.elderName" />
          </el-form-item>
          <el-form-item label="备注" prop="remarks">
            <el-input v-model="addKinForm.remarks" />
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
          <el-form-item label="电话" prop="phone">
            <el-input v-model="editKinForm.phone" />
          </el-form-item>
          <el-form-item label="地址" prop="address">
            <el-input v-model="editKinForm.address" />
          </el-form-item>
          <el-form-item label="关系" prop="relationship">
            <el-input v-model="editKinForm.relationship" />
          </el-form-item>
          <el-form-item label="老人姓名" prop="elderName">
            <el-input v-model="editKinForm.elderName" />
          </el-form-item>
          <el-form-item label="备注" prop="remarks">
            <el-input v-model="editKinForm.remarks" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleEditKin">编辑</el-button>
            <el-button @click="editKinDialogVisible = false">取消</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete, View } from '@element-plus/icons-vue'

// 亲属列表数据 
const kins = ref([])

// 获取亲属列表数据
const fetchKinList = async () => {
  loading.value = true
  try {
    const res = await getAllKins()
    if (res.success) {
      kins.value = res.data
      total.value = res.data.length
    } else {
      console.error('获取亲属列表失败')
    }
  } catch (error) {
    console.error('获取亲属列表失败：' + error.message)
  } finally {
    loading.value = false
  }
}

// 在组件挂载时获取数据
onMounted(() => {
  fetchKinList()
})
const handleCurrentChange = (val) => {
  currentPage.value = val
}
const handleSizeChange = (val) => {
  pageSize.value = val
}
const elders = ref([
  {
    id: 1,
    name: '张大爷',
    phone: '12345678901',
  },
  {
    id: 2,
    name: '李大爷',
    phone: '12345678902',
  },
  {
    id: 3,
    name: '吴大爷',
    phone: '12345678903',
  }
])

// 获取老人姓名
const getElderName = (elderId) => {
  const elder = elders.value.find(elder => elder.id === elderId)
  return elder ? elder.name : '未知'
}

// 定义 loading 状态
const loading = ref(false)

// 搜索查询
const searchQuery = ref('')

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0) // 添加total变量

// 对话框显示状态
const addKinDialogVisible = ref(false) // 添加亲属对话框
const editKinDialogVisible = ref(false) // 编辑亲属对话框

// 计算亲属列表
const filteredKins = computed(() => {
  return kins.value.filter(kin =>
    kin.name.includes(searchQuery.value) ||
    kin.phone.includes(searchQuery.value) ||
    kin.email.includes(searchQuery.value)
  )
})

// 添加亲属
const handleAdd = () => {
  addKinDialogVisible.value = true
  addKinForm.value = {
    name: '',
    phone: '',
    address: '',
    relationship: '',
    elderName: '',
    remarks: ''
  }
}

// 编辑亲属
const handleEdit = (row) => {
  editKinDialogVisible.value = true
  editKinForm.value = {
    name: row.name,
    phone: row.phone,
    address: row.address,
    relationship: row.relationship,
    elderName: row.elderName,
    remarks: row.remarks
  }
}

// 删除亲属
const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该亲属吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteKin(row.id)
      if (res.success) {
        ElMessage.success('删除成功')
        fetchKinList() // 刷新列表
      } else {
        console.error('删除失败')
      }
    } catch (error) {
      console.error('删除失败：' + error.message)
    }
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

// 查看亲属详情
const handleView = (row) => {
  console.log('查看亲属详情', row)
  // 这里可以实现查看详情的逻辑，例如打开一个抽屉或对话框
  ElMessage.info('查看亲属详情功能开发中')
}

// 搜索
const handleSearch = () => {
  searchQuery.value = searchQuery.value.trim()
  if (!searchQuery.value) {
    ElMessage.warning('请输入搜索内容')
    return
  } 
  console.log('搜索亲属', searchQuery.value)
}

// 分页大小变化
const handlePageSizeChange = (size) => {
  if (size < 1 || size > 100) {
    ElMessage.warning('每页显示数量应在1-100之间')
    return
  }
  pageSize.value = size
}

// 分页大小变化
const handleCurrentPageChange = (page) => {
  if (page < 1) {
    ElMessage.warning('页码不能小于1')
    return
  }
  currentPage.value = page
}
// 添加亲属表单
const addKinForm = ref({
  name: '',
  phone: '',
  address: '',
  relationship: '',
  elderName: '',
  remarks: ''
})

// 编辑亲属表单
const editKinForm = ref({
  name: '',
  phone: '',
  address: '',
  relationship: '',
  elderName: '',
  remarks: ''
})

// 表单验证规则
const addKinRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入电话号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入地址', trigger: 'blur' },
    { min: 5, max: 100, message: '长度在 5 到 100 个字符', trigger: 'blur' }
  ],
  relationship: [
    { required: true, message: '请输入与老人的关系', trigger: 'blur' }
  ],
  elderName: [
    { required: true, message: '请输入老人姓名', trigger: 'blur' }
  ]
}

// 编辑表单验证规则
const editKinRules = {
  ...addKinRules
}

// 添加亲属提交
const handleAddKin = () => {
  if (!addKinFormRef.value) return
  addKinFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await addKin(addKinForm.value)
        if (res.success) {
          ElMessage.success('添加成功')
          addKinDialogVisible.value = false
          fetchKinList() // 刷新列表
        } else {
          console.error('添加失败')
        }
      } catch (error) {
        console.error('添加失败：' + error.message)
      }
    } else {
      return false
    }
  })
}

// 编辑亲属提交
const handleEditKin = () => {
  if (!editKinFormRef.value) return
  editKinFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await updateKin(editKinForm.value)
        if (res.success) {
          ElMessage.success('更新成功')
          editKinDialogVisible.value = false
          fetchKinList() // 刷新列表
        } else {
          console.error('更新失败')
        }
      } catch (error) {
        console.error('更新失败：' + error.message)
      }
    } else {
      return false
    }
  })
}
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

/* 响应式调整 */
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
