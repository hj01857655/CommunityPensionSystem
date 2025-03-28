<template>
  <div class="activity-type">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="title">活动类型管理</span>
            <el-input
              v-model="searchQuery"
              placeholder="请输入类型名称搜索"
              class="search-input"
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增活动类型
          </el-button>
        </div>
      </template>

      <el-table
        :data="tableData"
        style="width: 100%"
        v-loading="loading"
        border
        stripe
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="name" label="类型名称" min-width="120" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status ? 'success' : 'danger'">
              {{ row.status ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button-group>
              <el-button type="primary" size="small" @click="handleEdit(row)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button type="danger" size="small" @click="handleDelete(row)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增活动类型' : '编辑活动类型'"
      width="500px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="类型名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入类型名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述信息"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="form.status"
            :active-value="true"
            :inactive-value="false"
            inline-prompt
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue'
import {
  getActivityTypeList,
  addActivityType,
  updateActivityType,
  deleteActivityType
} from '@/api/back/activity/type'

// 表格数据
const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchQuery = ref('')

// 表单相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref()
const form = ref({
  name: '',
  description: '',
  status: true
})

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '请输入类型名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入描述信息', trigger: 'blur' },
    { max: 200, message: '长度不能超过 200 个字符', trigger: 'blur' }
  ]
}

// 获取活动类型列表
const getActivityTypes = async () => {
  loading.value = true
  try {
    const response = await getActivityTypeList({
      page: currentPage.value,
      size: pageSize.value,
      query: searchQuery.value
    })
    if (response.code === 200) {
      tableData.value = response.data.content
      total.value = response.data.totalElements
    }
  } catch (error) {
    console.error('获取活动类型列表失败:', error)
    ElMessage.error(error.message || '获取活动类型列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getActivityTypes()
}

// 新增活动类型
const handleAdd = () => {
  dialogType.value = 'add'
  form.value = {
    name: '',
    description: '',
    status: true
  }
  dialogVisible.value = true
}

// 编辑活动类型
const handleEdit = (row) => {
  dialogType.value = 'edit'
  form.value = { ...row }
  dialogVisible.value = true
}

// 删除活动类型
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确认删除活动类型"${row.name}"吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await deleteActivityType(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      getActivityTypes()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除活动类型失败:', error)
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    const submitFunc = dialogType.value === 'add' ? addActivityType : updateActivityType
    const response = await submitFunc(form.value)
    
    if (response.code === 200) {
      ElMessage.success(dialogType.value === 'add' ? '新增成功' : '编辑成功')
      dialogVisible.value = false
      getActivityTypes()
    }
  } catch (error) {
    console.error('提交表单失败:', error)
    ElMessage.error(error.message || '操作失败')
  }
}

// 分页相关
const handleSizeChange = (val) => {
  pageSize.value = val
  getActivityTypes()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  getActivityTypes()
}

// 初始化
onMounted(() => {
  getActivityTypes()
})
</script>

<style scoped>
.activity-type {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.title {
  font-size: 16px;
  font-weight: bold;
}

.search-input {
  width: 200px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

:deep(.el-button-group) {
  .el-button + .el-button {
    margin-left: -1px;
  }
}

.operation-buttons {
  display: flex;
  gap: 5px;
}
</style>