<template>
  <div class="service-view">
    <el-card class="content-card" shadow="hover">
      <h3>服务预约</h3>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="服务列表" name="list">
          <div class="filter-container">
            <el-input v-model="searchQuery" placeholder="搜索服务名称" class="search-input" clearable @clear="handleSearch">
              <template #prefix>
                <el-icon>
                  <Search />
                </el-icon>
              </template>
            </el-input>
            <el-select v-model="categoryFilter" placeholder="服务类别" clearable @change="handleSearch">
              <el-option v-for="item in categoryOptions" :key="item" :label="item" :value="item" />
            </el-select>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </div>

          <el-table :data="filteredServices" style="width: 100%" v-loading="loading">
            <el-table-column prop="name" label="服务名称" />
            <el-table-column prop="category" label="服务类别" />
            <el-table-column prop="price" label="价格">
              <template #default="{ row }">
                {{ row.price }} 元
              </template>
            </el-table-column>
            <el-table-column prop="duration" label="时长">
              <template #default="{ row }">
                {{ row.duration }} 分钟
              </template>
            </el-table-column>
            <el-table-column label="操作">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="openBookingDialog(row)">
                  预约
                </el-button>
                <el-button type="info" size="small" @click="viewServiceDetail(row)">
                  详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination v-if="totalServices > 0" class="pagination" :current-page="currentPage" :page-size="pageSize"
            :total="totalServices" layout="total, prev, pager, next" @current-change="handlePageChange" />
        </el-tab-pane>

        <el-tab-pane label="我的预约" name="my">
          <div class="filter-container">
            <el-select v-model="statusFilter" placeholder="预约状态" clearable @change="fetchMyAppointments">
              <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
            <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期"
              end-placeholder="结束日期" @change="fetchMyAppointments" />
          </div>

          <el-table :data="myAppointments" style="width: 100%" v-loading="loadingAppointments">
            <el-table-column prop="serviceName" label="服务名称" />
            <el-table-column prop="appointmentTime" label="预约时间">
              <template #default="{ row }">
                {{ formatDateTime(row.appointmentTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作">
              <template #default="{ row }">
                <el-button v-if="row.status === '待确认' || row.status === '已确认'" type="danger" size="small"
                  @click="handleCancel(row)">
                  取消
                </el-button>
                <el-button v-if="row.status === '已完成' && !row.evaluated" type="success" size="small"
                  @click="openEvaluationDialog(row)">
                  评价
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination v-if="totalAppointments > 0" class="pagination" :current-page="appointmentPage"
            :page-size="appointmentPageSize" :total="totalAppointments" layout="total, prev, pager, next"
            @current-change="handleAppointmentPageChange" />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 服务预约对话框 -->
    <el-dialog v-model="bookingDialogVisible" title="服务预约" width="500px">
      <el-form :model="bookingForm" :rules="bookingRules" ref="bookingFormRef" label-width="100px">
        <el-form-item label="服务名称">
          <span>{{ currentService.name }}</span>
        </el-form-item>
        <el-form-item label="服务类别">
          <span>{{ currentService.category }}</span>
        </el-form-item>
        <el-form-item label="价格">
          <span>{{ currentService.price }} 元</span>
        </el-form-item>
        <el-form-item label="时长">
          <span>{{ currentService.duration }} 分钟</span>
        </el-form-item>
        <el-form-item label="预约日期" prop="appointmentDate">
          <el-date-picker v-model="bookingForm.appointmentDate" type="date" placeholder="选择日期"
            :disabled-date="disabledDate" />
        </el-form-item>
        <el-form-item label="预约时间" prop="appointmentTime">
          <el-time-picker v-model="bookingForm.appointmentTime" format="HH:mm" placeholder="选择时间"
            :disabled="!bookingForm.appointmentDate" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="bookingForm.remark" type="textarea" :rows="3" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="bookingDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitBooking" :loading="submitting">确认预约</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 服务详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="服务详情" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="服务名称">{{ currentService.name }}</el-descriptions-item>
        <el-descriptions-item label="服务类别">{{ currentService.category }}</el-descriptions-item>
        <el-descriptions-item label="价格">{{ currentService.price }} 元</el-descriptions-item>
        <el-descriptions-item label="时长">{{ currentService.duration }} 分钟</el-descriptions-item>
        <el-descriptions-item label="服务描述">{{ currentService.description }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 服务评价对话框 -->
    <el-dialog v-model="evaluationDialogVisible" title="服务评价" width="500px">
      <el-form :model="evaluationForm" :rules="evaluationRules" ref="evaluationFormRef" label-width="100px">
        <el-form-item label="服务名称">
          <span>{{ currentAppointment.serviceName }}</span>
        </el-form-item>
        <el-form-item label="评分" prop="rating">
          <el-rate v-model="evaluationForm.rating" :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
            :texts="['很差', '较差', '一般', '较好', '很好']" show-text />
        </el-form-item>
        <el-form-item label="评价内容" prop="content">
          <el-input v-model="evaluationForm.content" type="textarea" :rows="4" placeholder="请输入您的评价内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="evaluationDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEvaluation" :loading="submittingEvaluation">提交评价</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatDate } from '@/utils/date'
import { Search } from '@element-plus/icons-vue'
import { getServiceList, getServiceDetail, createServiceAppointment, getMyServiceAppointments, cancelServiceAppointment, evaluateService } from '@/api/fore/service'

const activeTab = ref('list')

// 服务列表相关
const loading = ref(false)
const services = ref([])
const searchQuery = ref('')
const categoryFilter = ref('')
const categoryOptions = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const totalServices = ref(0)

// 我的预约相关
const loadingAppointments = ref(false)
const myAppointments = ref([])
const statusFilter = ref('')
const dateRange = ref([])
const appointmentPage = ref(1)
const appointmentPageSize = ref(10)
const totalAppointments = ref(0)

// 状态选项
const statusOptions = [
  { value: '待确认', label: '待确认' },
  { value: '已确认', label: '已确认' },
  { value: '已完成', label: '已完成' },
  { value: '已取消', label: '已取消' }
]

// 服务预约对话框相关
const bookingDialogVisible = ref(false)
const currentService = ref({})
const bookingFormRef = ref(null)
const submitting = ref(false)

const bookingForm = reactive({
  appointmentDate: null,
  appointmentTime: null,
  remark: ''
})

// 禁用日期（今天之前的日期不可选）
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7 // 禁用今天之前的日期
}

const getStatusType = (status) => {
  return status === '待确认' ? 'warning' : status === '已确认' ? 'success' : status === '已完成' ? 'info' : 'danger'
}

const formatDateTime = (date) => {
  return formatDate(date, 'YYYY-MM-DD HH:mm')
}

const handleSearch = () => {
  fetchServices()
  fetchMyAppointments()
}

// 分页处理函数
const handlePageChange = (val) => {
  currentPage.value = val
  fetchServices()
}

// 预约分页处理函数
const handleAppointmentPageChange = (val) => {
  appointmentPage.value = val
  fetchMyAppointments()
}
const bookingRules = {
  appointmentDate: [
    { required: true, message: '请选择预约日期', trigger: 'change' }
  ],
  appointmentTime: [
    { required: true, message: '请选择预约时间', trigger: 'change' }
  ]
}

// 服务详情对话框相关
const detailDialogVisible = ref(false)

// 服务评价对话框相关
const evaluationDialogVisible = ref(false)
const currentAppointment = ref({})
const evaluationFormRef = ref(null)
const submittingEvaluation = ref(false)

const evaluationForm = reactive({
  rating: 5,
  content: ''
})

const evaluationRules = {
  rating: [
    { required: true, message: '请选择评分', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入评价内容', trigger: 'blur' }
  ]
}

// 计算过滤后的服务列表
const filteredServices = computed(() => {
  let result = services.value

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(service =>
      service.name.toLowerCase().includes(query) ||
      service.description.toLowerCase().includes(query)
    )
  }

  if (categoryFilter.value) {
    result = result.filter(service => service.category === categoryFilter.value)
  }

  return result
})

// 获取服务列表
const fetchServices = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }

    const response = await getServiceList(params)
    if (response.code === 200) {
      services.value = response.data.records || []
      totalServices.value = response.data.total || 0

      // 提取所有服务类别
      const categories = new Set(services.value.map(service => service.category))
      categoryOptions.value = Array.from(categories)
    }
  } catch (error) {
    console.error('获取服务列表失败:', error)
    ElMessage.error('获取服务列表失败')
  } finally {
    loading.value = false
  }
}

// 获取我的预约列表
const fetchMyAppointments = async () => {
  if (!localStorage.getItem('elderInfo')) {
    ElMessage.warning('请先登录')
    return
  }

  loadingAppointments.value = true
  try {
    const params = {
      page: appointmentPage.value,
      size: appointmentPageSize.value,
      status: statusFilter.value || undefined
    }

    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = formatDate(dateRange.value[0])
      params.endDate = formatDate(dateRange.value[1])
    }

    const response = await getMyServiceAppointments(params)
    if (response.code === 200) {
      myAppointments.value = response.data.records || []
      totalAppointments.value = response.data.total || 0
    }
  } catch (error) {
    console.error('获取预约列表失败:', error)
    ElMessage.error('获取预约列表失败')
  } finally {
    loadingAppointments.value = false
  }
}

// 打开服务预约对话框
const openBookingDialog = (service) => {
  if (!localStorage.getItem('elderInfo')) {
    ElMessage.warning('请先登录以预约服务')
    return
  }

  currentService.value = service
  bookingForm.appointmentDate = null
  bookingForm.appointmentTime = null
  bookingForm.remark = ''
  bookingDialogVisible.value = true
}

// 查看服务详情
const viewServiceDetail = async (service) => {
  try {
    const response = await getServiceDetail(service.id)
    if (response.code === 200) {
      currentService.value = response.data
      detailDialogVisible.value = true
    }
  } catch (error) {
    console.error('获取服务详情失败:', error)
    ElMessage.error('获取服务详情失败')
  }
}

// 提交服务预约
const submitBooking = async () => {
  await bookingFormRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      // 合并日期和时间
      const appointmentDateTime = new Date(bookingForm.appointmentDate)
      const timeObj = new Date(bookingForm.appointmentTime)
      appointmentDateTime.setHours(timeObj.getHours())
      appointmentDateTime.setMinutes(timeObj.getMinutes())

      const elderInfo = JSON.parse(localStorage.getItem('elderInfo'))

      const data = {
        serviceId: currentService.value.id,
        elderId: elderInfo.id,
        appointmentTime: appointmentDateTime.toISOString(),
        remark: bookingForm.remark
      }

      const response = await createServiceAppointment(data)
      if (response.code === 200) {
        ElMessage.success('预约成功')
        bookingDialogVisible.value = false

        // 如果当前是在"我的预约"页面，则刷新列表
        if (activeTab.value === 'my') {
          fetchMyAppointments()
        }
      }
    } catch (error) {
      console.error('预约失败:', error)
      ElMessage.error('预约失败')
    } finally {
      submittingEvaluation.value = false
    }
  })
}

// 取消预约
const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消"${row.serviceName}"预约吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // 调用取消预约API
    const response = await cancelServiceAppointment(row.id)
    if (response.code === 200) {
      ElMessage.success('预约已取消')
      // 更新本地状态
      const index = myAppointments.value.findIndex(item => item.id === row.id)
      if (index !== -1) {
        myAppointments.value[index].status = '已取消'
      }
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消预约失败:', error)
      ElMessage.error('取消预约失败')
    }
  }
  if (activeTab.value === 'my') {
    fetchMyAppointments()
  }
}

// 打开评价对话框
const openEvaluationDialog = (appointment) => {
  currentAppointment.value = appointment
  evaluationForm.rating = 5
  evaluationForm.content = ''
  evaluationDialogVisible.value = true
}

// 提交服务评价
const submitEvaluation = async () => {
  await evaluationFormRef.value.validate(async (valid) => {
    if (!valid) return

    submittingEvaluation.value = true
    try {
      const response = await evaluateService(currentAppointment.value.id, evaluationForm)
      if (response.code === 200) {
        ElMessage.success('评价提交成功')
        evaluationDialogVisible.value = false
        
        // 更新本地状态
        const index = myAppointments.value.findIndex(item => item.id === currentAppointment.value.id)
        if (index !== -1) {
          myAppointments.value[index].evaluated = true
        }
        
        // 刷新预约列表
        fetchMyAppointments()
      }
    } catch (error) {
      console.error('评价提交失败:', error)
      ElMessage.error('评价提交失败')
    } finally {
      submittingEvaluation.value = false
    }
  })
}
// 组件挂载时获取数据
onMounted(() => {
  fetchServices()
  fetchMyAppointments()
})
</script>
<style scoped>
.service-view {
  width: 100%;
}

.content-card {
  margin-bottom: 20px;
}

h3 {
  margin-bottom: 20px;
  color: #2c3e50;
  font-weight: 600;
}

.filter-container {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  gap: 15px;
}

.search-input {
  width: 220px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  width: 100%;
}

.el-table {
  margin-bottom: 10px;
}
</style>