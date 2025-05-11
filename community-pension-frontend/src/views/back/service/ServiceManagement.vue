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
      <el-table-column prop="serviceId" label="ID" width="80" align="center" />
      <el-table-column prop="serviceName" label="服务名称" min-width="150" align="center" />
      <el-table-column prop="serviceType" label="类别" width="120" align="center">
        <template #default="scope">
          <el-tag :type="getCategoryType(scope.row.serviceType)">
            {{ getServiceTypeName(scope.row.serviceType) }}
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
      <el-table-column prop="status" label="状态" width="100" align="center">
        <template #default="scope">
          <el-switch
            :model-value="scope.row.status === '1'"
            @change="(val) => handleStatusChange(scope.row, val ? '1' : '0')"
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
        <el-form-item label="服务名称" prop="serviceName">
          <el-input v-model="serviceForm.serviceName" placeholder="请输入服务名称" />
        </el-form-item>
        <el-form-item label="类别" prop="serviceType">
          <el-select v-model="serviceForm.serviceType" placeholder="请选择服务类别">
            <el-option label="医疗服务" value="medical" />
            <el-option label="保洁服务" value="cleaning" />
            <el-option label="维修服务" value="repair" />
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
        <el-form-item label="状态" prop="status">
          <el-switch v-model="serviceForm.status" :active-value="1" :inactive-value="0" />
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
import RightToolbar from '@/components/common/base/RightToolbar/index.vue'
import { useServiceItemStore } from '@/stores/back/service'

const serviceStore = useServiceItemStore()

// 服务列表数据和分页信息
const services = ref([])
const totalItems = ref(0)
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const searchQuery = ref('')
const dialogVisible = ref(false)
const dialogType = ref('add')

// 表单相关
const serviceFormRef = ref(null)
const serviceForm = ref({
  serviceId: '',
  serviceName: '',
  serviceType: '',
  description: '',
  duration: 0,
  price: 0,
  status: '1'
})

// 表单验证规则
const serviceRules = {
  serviceName: [{ required: true, message: '请输入服务名称', trigger: 'blur' }],
  serviceType: [{ required: true, message: '请选择服务类别', trigger: 'change' }],
  description: [{ required: true, message: '请输入服务描述', trigger: 'blur' }],
  duration: [{ required: true, message: '请输入服务时长', trigger: 'blur' }],
  price: [{ required: true, message: '请输入服务价格', trigger: 'blur' }]
}

// 过滤后的服务列表
const filteredServices = computed(() => {
  if (!services.value) {
    return []
  }
  
  if (!searchQuery.value) {
    return services.value
  }
  
  const query = searchQuery.value.toLowerCase()
  return services.value.filter(service =>
    service.serviceName?.toLowerCase().includes(query) ||
    service.serviceType?.toLowerCase().includes(query)
  )
})

// 获取服务类型名称
const getServiceTypeName = (type) => {
  const typeMap = {
    'medical': '医疗服务',
    'cleaning': '保洁服务',
    'repair': '维修服务'
  }
  return typeMap[type] || type
}

// 获取服务类型编码
const getServiceTypeCode = (name) => {
  const typeMap = {
    '医疗服务': 'medical',
    '保洁服务': 'cleaning',
    '维修服务': 'repair'
  }
  return typeMap[name] || name
}

// 获取类别标签类型
const getCategoryType = (type) => {
  const typeMap = {
    'medical': 'danger',  // 医疗服务使用红色
    'cleaning': 'info',   // 保洁服务使用蓝色
    'repair': 'warning'   // 维修服务使用黄色
  }
  return typeMap[type] || 'info'
}

// 查询参数
const queryParams = reactive({
  name: '',
  category: ''
})

// 显示搜索条件
const showSearch = ref(true)

// 选中的服务
const selectedServices = ref([])
const single = computed(() => selectedServices.value.length !== 1)
const multiple = computed(() => selectedServices.value.length === 0)

// 获取服务列表
const getList = async () => {
  loading.value = true
  try {
    const res = await serviceStore.getServiceItemList({
      page: currentPage.value,
      limit: pageSize.value,
      serviceName: queryParams.name,
      serviceType: queryParams.category
    })
    if (res) {
      services.value = res.records
      totalItems.value = res.total
    }
  } catch (error) {
    console.error('获取服务列表失败:', error)
    ElMessage.error('获取服务列表失败')
  } finally {
    loading.value = false
  }
}

// 重置查询
const handleReset = () => {
  queryParams.name = ''
  queryParams.category = ''
  handleSearch()
}

// 表格选择
const handleSelectionChange = (selection) => {
  selectedServices.value = selection
}

// 编辑服务
const handleEdit = (row) => {
  dialogType.value = 'edit'
  serviceForm.value = {
    serviceId: row.serviceId,
    serviceName: row.serviceName,
    serviceType: row.serviceType,
    description: row.description,
    duration: row.duration,
    price: row.price,
    status: row.status
  }
  dialogVisible.value = true
}

// 删除服务
const handleDelete = (row) => {
  const ids = row ? [row.serviceId] : selectedServices.value.map(item => item.serviceId)
  ElMessageBox.confirm('确认要删除选中的服务吗？', '警告', {
    type: 'warning'
  }).then(async () => {
    try {
      if (ids.length === 1) {
        await serviceStore.deleteServiceItem(ids[0])
      } else {
        await serviceStore.batchDeleteServiceItem(ids)
      }
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除服务失败:', error)
      ElMessage.error('删除服务失败')
    }
  })
}

// 状态变更
const handleStatusChange = async (row, newStatus) => {
  try {
    await serviceStore.updateServiceItemStatus(row.serviceId, newStatus)
    ElMessage.success('状态更新成功')
    // 更新成功后重新获取列表
    await getList()
  } catch (error) {
    console.error('更新服务状态失败:', error)
    ElMessage.error('更新服务状态失败')
  }
}

// 提交表单
const submitForm = async () => {
  if (!serviceFormRef.value) return
  
  await serviceFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (dialogType.value === 'add') {
          await serviceStore.addServiceItem(serviceForm.value)
          ElMessage.success('新增成功')
        } else {
          await serviceStore.updateServiceItem(serviceForm.value)
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
        getList()
      } catch (error) {
        console.error('保存服务失败:', error)
        ElMessage.error('保存服务失败')
      }
    }
  })
}

// 页面加载时获取数据
onMounted(() => {
  getList()
})

// 新增服务
const handleAdd = () => {
  dialogType.value = 'add'
  serviceForm.value = {
    serviceId: '',
    serviceName: '',
    serviceType: '',
    description: '',
    duration: 0,
    price: 0,
    status: '1'
  }
  dialogVisible.value = true
}

// 搜索服务
const handleSearch = () => {
  currentPage.value = 1
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