<template>
    <div class="service-management">
      <el-card shadow="hover" class="table-card">
        <template #header>
          <div class="card-header">
            <h3>服务项目管理</h3>
            <div class="header-actions">
              <el-input
                v-model="searchQuery"
                placeholder="搜索服务名称"
                class="search-input"
                clearable
                @clear="handleSearch"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button type="success" @click="handleAdd">添加服务</el-button>
            </div>
          </div>
        </template>
        <el-table
          :data="filteredServices"
          style="width: 100%"
          v-loading="loading"
          border
        >
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="服务名称" min-width="150" />
          <el-table-column prop="category" label="类别" width="120">
            <template #default="scope">
              <el-tag :type="getCategoryType(scope.row.category)">
                {{ scope.row.category }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="描述" min-width="200" />
          <el-table-column prop="duration" label="时长" width="100">
            <template #default="scope">
              {{ scope.row.duration }} 分钟
            </template>
          </el-table-column>
          <el-table-column prop="price" label="价格" width="100">
            <template #default="scope">
              {{ scope.row.price }} 元
            </template>
          </el-table-column>
          <el-table-column prop="is_active" label="状态" width="100">
            <template #default="scope">
              <el-switch
                v-model="scope.row.is_active"
                :active-value="1"
                :inactive-value="0"
                @change="handleStatusChange(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <Pagination
          v-if="serviceStore.serviceItemTotal > 0"
          :total="serviceStore.serviceItemTotal"
          v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize"
          @pagination="getList"
        />
      </el-card>
      
      <!-- 服务表单对话框 -->
      <el-dialog
        v-model="dialogVisible"
        :title="dialogType === 'add' ? '新增服务' : '编辑服务'"
        width="600px"
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
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitForm">确定</el-button>
          </span>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <script setup>
  import { ref, computed, reactive, onMounted } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { Search } from '@element-plus/icons-vue'
  import Pagination from '@/components/common/Pagination.vue'
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
  </script>
  
  <style scoped>
  .service-management {
    padding: 10px;
  }
  
  .table-card {
    margin-bottom: 20px;
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .card-header h3 {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
  }
  
  .header-actions {
    display: flex;
    align-items: center;
    gap: 10px;
  }
  
  .search-input {
    width: 250px;
  }
  
  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
  </style>