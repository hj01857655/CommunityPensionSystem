<template>
  <div class="app-container">
    <!-- 搜索条件 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="服务名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入服务名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleSearch"
        />
      </el-form-item>
      <el-form-item label="服务类别" prop="category">
        <el-select
          v-model="queryParams.category"
          placeholder="请选择服务类别"
          clearable
          style="width: 200px"
        >
          <el-option label="护理服务" value="护理服务" />
          <el-option label="餐饮服务" value="餐饮服务" />
          <el-option label="医疗服务" value="医疗服务" />
          <el-option label="家政服务" value="家政服务" />
          <el-option label="生活服务" value="生活服务" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain :icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain :icon="Edit" :disabled="single" @click="handleEdit">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain :icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 表格 -->
    <el-table
      v-loading="loading"
      :data="filteredServices"
      @selection-change="handleSelectionChange"
      border
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column prop="name" label="服务名称" min-width="150" align="center" />
      <el-table-column prop="category" label="类别" width="120" align="center">
        <template #default="scope">
          <el-tag :type="getCategoryType(scope.row.category)">
            {{ scope.row.category }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" min-width="200" align="center" />
      <el-table-column prop="duration" label="时长" width="100" align="center">
        <template #default="scope">
          {{ scope.row.duration }} 分钟
        </template>
      </el-table-column>
      <el-table-column prop="price" label="价格" width="100" align="center">
        <template #default="scope">
          {{ scope.row.price }} 元
        </template>
      </el-table-column>
      <el-table-column prop="is_active" label="状态" width="100" align="center">
        <template #default="scope">
          <el-switch
            v-model="scope.row.is_active"
            :active-value="1"
            :inactive-value="0"
            @change="handleStatusChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button type="primary" link :icon="Edit" @click="handleEdit(scope.row)">修改</el-button>
          <el-button type="primary" link :icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <pagination
      v-if="totalItems > 0"
      :total="totalItems"
      v-model:page="currentPage"
      v-model:limit="pageSize"
      @pagination="getList"
    />

    <!-- 服务表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增服务' : '编辑服务'"
      width="600px"
      append-to-body
    >
      <el-form
        ref="serviceFormRef"
        :model="serviceForm"
        :rules="serviceRules"
        label-width="100px"
      >
        <el-form-item label="服务名称" prop="name">
          <el-input v-model="serviceForm.name" placeholder="请输入服务名称" />
        </el-form-item>
        <el-form-item label="类别" prop="category">
          <el-select v-model="serviceForm.category" placeholder="请选择服务类别">
            <el-option label="护理服务" value="护理服务" />
            <el-option label="餐饮服务" value="餐饮服务" />
            <el-option label="医疗服务" value="医疗服务" />
            <el-option label="家政服务" value="家政服务" />
            <el-option label="生活服务" value="生活服务" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="serviceForm.description" type="textarea" :rows="3" placeholder="请输入服务描述" />
        </el-form-item>
        <el-form-item label="时长" prop="duration">
          <el-input-number v-model="serviceForm.duration" :min="0" placeholder="请输入服务时长" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="serviceForm.price" :min="0" placeholder="请输入服务价格" />
        </el-form-item>
        <el-form-item label="状态" prop="is_active">
          <el-switch v-model="serviceForm.is_active" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="dialogVisible = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Delete, Edit, Plus, Refresh } from '@element-plus/icons-vue'
import Pagination from '@/components/common/Pagination.vue'
import RightToolbar from '@/components/RightToolbar/index.vue'
import { useServiceStore } from '@/stores/back/service'

const serviceStore = useServiceStore()

// 服务列表数据（静态数据）
const services = ref([
  { id: 1, name: '基础护理', category: '护理服务', description: '提供基础的日常生活照料', duration: 60, price: 100.00, is_active: 1 },
  { id: 2, name: '营养餐配送', category: '餐饮服务', description: '提供营养均衡的餐点配送', duration: 30, price: 50.00, is_active: 1 },
  { id: 3, name: '健康咨询', category: '医疗服务', description: '提供专业的健康咨询服务', duration: 45, price: 80.00, is_active: 1 },
  { id: 4, name: '家庭清洁', category: '家政服务', description: '提供家庭清洁服务', duration: 90, price: 120.00, is_active: 1 },
  { id: 5, name: '陪同外出', category: '生活服务', description: '陪同老人外出活动', duration: 120, price: 150.00, is_active: 1 },
  { id: 6, name: '心理辅导', category: '医疗服务', description: '提供心理健康辅导', duration: 60, price: 100.00, is_active: 1 },
  { id: 7, name: '康复训练', category: '医疗服务', description: '提供专业的康复训练', duration: 90, price: 150.00, is_active: 1 },
  { id: 8, name: '定制膳食', category: '餐饮服务', description: '根据老人需求定制营养餐点', duration: 45, price: 80.00, is_active: 1 },
  { id: 9, name: '居家安全检查', category: '家政服务', description: '提供居家安全检查服务', duration: 60, price: 100.00, is_active: 1 },
  { id: 10, name: '远程医疗咨询', category: '医疗服务', description: '提供远程医疗咨询服务', duration: 30, price: 60.00, is_active: 1 }
])

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const searchQuery = ref('')
const dialogVisible = ref(false)
const dialogType = ref('add')

// 表单相关
const serviceFormRef = ref(null)
const serviceForm = ref({
  name: '',
  category: '',
  description: '',
  duration: 0,
  price: 0,
  is_active: 1
})

// 表单验证规则
const serviceRules = {
  name: [{ required: true, message: '请输入服务名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择服务类别', trigger: 'change' }],
  description: [{ required: true, message: '请输入服务描述', trigger: 'blur' }],
  duration: [{ required: true, message: '请输入服务时长', trigger: 'blur' }],
  price: [{ required: true, message: '请输入服务价格', trigger: 'blur' }]
}

// 过滤后的服务列表
const filteredServices = computed(() => {
  if (!services.value || !searchQuery.value) {
    return services.value || []
  }
  
  const query = searchQuery.value.toLowerCase()
  return services.value.filter(service =>
    service.name.toLowerCase().includes(query) ||
    service.category.toLowerCase().includes(query)
  )
})

// 总服务数
const totalItems = computed(() => {
  return filteredServices.value ? filteredServices.value.length : 0
})

// 获取类别标签类型
const getCategoryType = (category) => {
  const typeMap = {
    '护理服务': 'success',
    '餐饮服务': 'info',
    '医疗服务': 'warning',
    '家政服务': 'primary',
    '生活服务': 'danger'
  }
  return typeMap[category] || 'info'
}

// 搜索服务
const handleSearch = () => {
  currentPage.value = 1
}

// 新增服务
const handleAdd = () => {
  dialogType.value = 'add'
  serviceForm.value = {
    name: '',
    category: '',
    description: '',
    duration: 0,
    price: 0,
    is_active: 1
  }
  dialogVisible.value = true
}

// 编辑服务
const handleEdit = (row) => {
  dialogType.value = 'edit'
  serviceForm.value = { ...row }
  dialogVisible.value = true
}

// 删除服务
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除服务 "${row.name}" 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 这里应该调用删除服务的API
    ElMessage.success('删除成功')
    services.value = services.value.filter(service => service.id !== row.id)
  })
}

// 提交表单
const submitForm = () => {
  serviceFormRef.value.validate((valid) => {
    if (valid) {
      if (dialogType.value === 'add') {
        // 添加服务
        const newService = {
          id: services.value.length + 1,
          ...serviceForm.value
        }
        services.value.push(newService)
        ElMessage.success('添加服务成功')
      } else {
        // 编辑服务
        const index = services.value.findIndex(service => service.id === serviceForm.value.id)
        if (index !== -1) {
          services.value[index] = { ...serviceForm.value }
          ElMessage.success('更新服务成功')
        }
      }
      dialogVisible.value = false
    }
  })
}

// 处理服务状态切换
const handleStatusChange = (row) => {
  // 这里应该调用更新服务状态的API
  ElMessage.success(`服务 "${row.name}" 状态已更新`)
}

// 分页大小变化
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

// 当前页变化
const handleCurrentChange = (val) => {
  currentPage.value = val
}

const queryParams = ref({
  name: '',
  category: ''
})

// 重置
const handleReset = () => {
  queryParams.value = {
    name: '',
    category: ''
  }
  handleSearch()
}

// 显示搜索条件
const showSearch = ref(true)

// 非单个禁用
const single = ref(true)
// 非多个禁用
const multiple = ref(true)

// 多选框选中数据
const handleSelectionChange = (selection) => {
  single.value = selection.length !== 1
  multiple.value = !selection.length
}
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.mb8 {
  margin-bottom: 8px;
}

.small-padding {
  padding-left: 5px;
  padding-right: 5px;
}

.fixed-width .el-button--small {
  padding: 7px 10px;
  min-width: 60px;
}
</style>