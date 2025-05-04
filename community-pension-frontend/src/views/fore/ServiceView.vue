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
            <el-table-column prop="serviceName" label="服务名称" />
            <el-table-column prop="serviceTypeName" label="服务类别" />
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
            <el-table-column prop="serviceName" label="服务名称" min-width="120" />
            <el-table-column prop="serviceTypeName" label="服务类别" min-width="100" />
            <el-table-column label="服务信息" min-width="150">
              <template #default="{ row }">
                <div>{{ row.price }} 元</div>
                <div>{{ row.duration }} 分钟</div>
              </template>
            </el-table-column>
            <el-table-column prop="scheduleTime" label="预约时间" min-width="160">
              <template #default="{ row }">
                {{ formatDateTime(row.scheduleTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="applyReason" label="预约备注" min-width="150" show-overflow-tooltip />
            <el-table-column label="服务人员" min-width="120">
              <template #default="{ row }">
                <span v-if="row.status >= 1">{{ row.staffName || '未分配' }}</span>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" min-width="120">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
                <div v-if="row.statusUpdateTime" class="status-time">
                  {{ formatDateTime(row.statusUpdateTime) }}
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作" min-width="150" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="viewAppointmentDetail(row)">
                  详情
                </el-button>
                <el-button v-if="row.status === 0 || row.status === 1" type="danger" size="small"
                  @click="handleCancel(row)">
                  取消
                </el-button>
                <el-button v-if="row.status === 2 && !row.evaluated" type="success" size="small"
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
          <span>{{ currentService.serviceName }}</span>
        </el-form-item>
        <el-form-item label="服务类别">
          <span>{{ currentService.serviceTypeName }}</span>
          <input type="hidden" v-model="bookingForm.serviceTypeId" />
        </el-form-item>
        <el-form-item label="价格">
          <span>{{ currentService.price }} 元</span>
        </el-form-item>
        <el-form-item label="时长">
          <span>{{ currentService.duration }} 分钟</span>
        </el-form-item>
        <el-form-item label="预约日期" prop="appointmentDate">
          <el-date-picker
              v-model="bookingForm.appointmentDate"
              type="date"
            placeholder="选择日期"
            :disabled-date="disabledDate"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            :formatter="formatDateWithTip"
          />
        </el-form-item>
        <el-form-item label="预约时间" prop="appointmentTime">
          <el-time-picker v-model="bookingForm.appointmentTime" format="HH:mm" placeholder="选择时间"
            :disabled="!bookingForm.appointmentDate"
            :disabled-hours="() => disabledTime(bookingForm.appointmentDate).hours"
            :disabled-minutes="() => disabledTime(bookingForm.appointmentDate).minutes" />
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
        <el-descriptions-item label="服务名称">{{ currentService.serviceName }}</el-descriptions-item>
        <el-descriptions-item label="服务类别">{{ currentService.serviceTypeName }}</el-descriptions-item>
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

    <!-- 预约详情对话框 -->
    <el-dialog v-model="appointmentDetailVisible" title="预约详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="服务名称">{{ currentAppointment.serviceName }}</el-descriptions-item>
        <el-descriptions-item label="服务类别">{{ currentAppointment.serviceTypeName }}</el-descriptions-item>
        <el-descriptions-item label="价格">{{ currentAppointment.price }} 元</el-descriptions-item>
        <el-descriptions-item label="时长">{{ currentAppointment.duration }} 分钟</el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ formatDateTime(currentAppointment.scheduleTime) }}</el-descriptions-item>
        <el-descriptions-item label="预约状态">
          <el-tag :type="getStatusType(currentAppointment.status)">
            {{ getStatusText(currentAppointment.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="预约备注" :span="2">{{ currentAppointment.applyReason }}</el-descriptions-item>
        <el-descriptions-item label="服务人员" v-if="currentAppointment.status >= 1">
          <span v-if="currentAppointment.staffName">{{ currentAppointment.staffName }}</span>
          <span v-else class="text-muted">暂未指派</span>
        </el-descriptions-item>
        <el-descriptions-item label="状态更新时间" v-if="currentAppointment.statusUpdateTime">
          {{ formatDateTime(currentAppointment.statusUpdateTime) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import {computed, onMounted, reactive, ref, watch} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {formatDateTime} from '@/utils/date'
import {Search} from '@element-plus/icons-vue'
import useServiceStore from '@/stores/fore/serviceStore'
import useUserStore from '@/stores/fore/userStore'
import {checkHoliday} from '@/api/fore/holiday'

const serviceStore = useServiceStore()
const userStore = useUserStore()

const activeTab = ref('list')

// 服务列表相关
const loading = computed(() => serviceStore.loading)
const services = computed(() => serviceStore.serviceList)
const searchQuery = ref('')
const categoryFilter = ref('')
const categoryOptions = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const totalServices = computed(() => serviceStore.total)

// 过滤后的服务列表
const filteredServices = computed(() => {
  return services.value
})

// 我的预约相关
const loadingAppointments = computed(() => serviceStore.loading)
const myAppointments = computed(() => {
  console.log('计算属性 myAppointments 被调用，当前值:', serviceStore.myAppointments)
  return serviceStore.myAppointments
})
const statusFilter = ref('')
const dateRange = ref([])
const appointmentPage = ref(1)
const appointmentPageSize = ref(10)
const totalAppointments = computed(() => {
  console.log('计算属性 totalAppointments 被调用，当前值:', serviceStore.appointmentTotal)
  return serviceStore.appointmentTotal
})

// 状态处理方法
const getStatusType = (status) => {
  switch (status) {
    case 0: // 待审核
      return 'warning'
    case 1: // 已派单
      return 'primary'
    case 2: // 服务中
      return 'success'
    case 3: // 已完成
      return 'info'
    case 4: // 已取消
      return 'info'
    case 5: // 已拒绝
      return 'danger'
    default:
      return 'info'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 0:
      return '待审核'
    case 1:
      return '已派单'
    case 2:
      return '服务中'
    case 3:
      return '已完成'
    case 4:
      return '已取消'
    case 5:
      return '已拒绝'
    default:
      return '未知状态'
  }
}

// 状态选项
const statusOptions = [
  { value: 0, label: '待审核' },
  { value: 1, label: '已派单' },
  { value: 2, label: '服务中' },
  { value: 3, label: '已完成' },
  { value: 4, label: '已取消' },
  { value: 5, label: '已拒绝' }
]

// 服务预约对话框相关
const bookingDialogVisible = ref(false)
const currentService = ref({})
const bookingFormRef = ref(null)
const submitting = ref(false)

const bookingForm = reactive({
  appointmentDate: null,
  appointmentTime: null,
  remark: '',
  serviceTypeId: null
})

// 在 script setup 部分添加
const holidayCache = ref(new Map())

// 修改日期格式化函数
const formatDateWithTip = (date) => {
  const dateObj = typeof date === 'string' ? new Date(date) : date
  const formattedDate = formatDateTime(dateObj)
  const tip = holidayCache.value.get(dateObj.getTime()) || ''
  return `${formattedDate} ${tip}`
}

// 修改日期禁用函数
const disabledDate = (time) => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return time.getTime() < today.getTime()
}

// 修改获取日期提示函数
const getDateTip = async (date) => {
  const dateObj = typeof date === 'string' ? new Date(date) : date
  const dateKey = dateObj.getTime()
  if (holidayCache.value.has(dateKey)) {
    return holidayCache.value.get(dateKey)
  }

  try {
    const isHoliday = await checkHoliday(dateObj)
    const tip = isHoliday ? '节假日' : ''
    holidayCache.value.set(dateKey, tip)
    return tip
  } catch (error) {
    console.error('获取节假日信息失败:', error)
    return ''
  }
}

// 监听日期变化，预加载节假日信息
watch(() => bookingForm.appointmentDate, async (newDate) => {
  if (newDate) {
    await getDateTip(newDate)
  }
})

// 修改禁用时间函数
const disabledTime = (date) => {
  if (!date) return { hours: [], minutes: [] }

  const dateObj = typeof date === 'string' ? new Date(date) : date
  const hours = []
  const minutes = []

  // 获取当前时间
  const now = new Date()

  // 如果是今天，则禁用当前时间之前的时间
  if (dateObj.getDate() === now.getDate() &&
      dateObj.getMonth() === now.getMonth() &&
      dateObj.getFullYear() === now.getFullYear()) {
    for (let i = 0; i <= now.getHours(); i++) {
      hours.push(i)
    }
    for (let i = 0; i <= now.getMinutes(); i++) {
      minutes.push(i)
    }
  }

  // 禁用非工作时间（8:00-18:00）
  for (let i = 0; i < 8; i++) {
    hours.push(i)
  }
  for (let i = 18; i < 24; i++) {
    hours.push(i)
  }

  return {
    hours,
    minutes
  }
}

// 获取服务列表
const fetchServices = async () => {
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value,
      serviceName: searchQuery.value
    }
    await serviceStore.fetchServiceList(params)
  } catch (error) {
    console.error('获取服务列表失败:', error)
    ElMessage.error('获取服务列表失败')
  }
}

// 获取用户信息
const getUserInfo = () => {
  // 优先从 store 获取
  if (userStore.userInfo?.userId) {
    return userStore.userInfo
  }

  // 如果 store 中没有，尝试从本地存储获取
  const storedUserInfo = localStorage.getItem('userInfo')
  if (storedUserInfo) {
    try {
      const userInfo = JSON.parse(storedUserInfo)
      userStore.setUserInfo(userInfo)
      return userInfo
    } catch (error) {
      console.error('解析用户信息失败:', error)
    }
  }

  return null
}

// 获取我的预约列表
const fetchMyAppointments = async () => {
  try {
    const userInfo = getUserInfo()
    if (!userInfo?.userId) {
      ElMessage.warning('请先登录')
      return
    }
    const params = {
      current: appointmentPage.value,
      size: appointmentPageSize.value,
      userId: userInfo.userId,
      status: statusFilter.value,
      startTime: dateRange.value?.[0],
      endTime: dateRange.value?.[1]
    }
    console.log('获取预约列表参数:', params)
    await serviceStore.fetchMyAppointments(params)
    console.log('预约列表数据:', serviceStore.myAppointments)
  } catch (error) {
    console.error('获取预约列表失败:', error)
    ElMessage.error('获取预约列表失败')
  }
}

const handleSearch = () => {
  currentPage.value = 1
  appointmentPage.value = 1
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
  ],
  remark: [
    { max: 200, message: '备注不能超过200个字符', trigger: 'blur' }
  ]
}

// 服务详情对话框相关
const detailDialogVisible = ref(false)

// 打开预约对话框
const openBookingDialog = (service) => {
  const userInfo = getUserInfo()
  if (!userInfo?.userId) {
    ElMessage.warning('请先登录后再进行预约')
    return
  }

  // 确保service对象包含必要的字段
  const requiredFields = ['serviceId', 'serviceName', 'serviceType', 'serviceTypeName', 'price', 'duration']
  const missingFields = requiredFields.filter(field => !service?.[field])

  if (missingFields.length > 0) {
    console.error('服务信息不完整，缺少字段:', missingFields)
    console.error('当前服务信息:', service)
    ElMessage.error('服务信息不完整，请刷新页面重试')
    return
  }

  // 打印服务信息以便调试
  console.log('打开预约对话框，服务信息:', service)

  // 将serviceType映射为serviceTypeId
  const serviceTypeMap = {
    'medical': 1,
    'cleaning': 2,
    'repair': 3
  }

  currentService.value = {
    ...service,
    serviceTypeId: serviceTypeMap[service.serviceType]
  }

  bookingForm.appointmentDate = null
  bookingForm.appointmentTime = null
  bookingForm.remark = ''
  bookingForm.serviceTypeId = serviceTypeMap[service.serviceType]
  bookingDialogVisible.value = true
}

// 打开服务详情对话框
const viewServiceDetail = (service) => {
  currentService.value = service
  detailDialogVisible.value = true
}

// 提交预约
const submitBooking = async () => {
  if (!bookingFormRef.value) return

  try {
    const userInfo = getUserInfo()
    if (!userInfo?.userId) {
      ElMessage.warning('请先登录后再进行预约')
      return
    }

    await bookingFormRef.value.validate()

    // 验证currentService
    if (!currentService.value || !currentService.value.serviceId || !currentService.value.serviceType) {
      console.error('服务信息不完整:', currentService.value)
      ElMessage.error('服务信息不完整，请刷新页面重试')
      return
    }

    submitting.value = true

    // 构建预约时间
    const appointmentTime = new Date(bookingForm.appointmentDate)
    const timeDate = new Date(bookingForm.appointmentTime)
    appointmentTime.setHours(timeDate.getHours())
    appointmentTime.setMinutes(timeDate.getMinutes())

    // 检查时间是否在服务时间内（8:00-18:00）
    const hours = appointmentTime.getHours()
    if (hours < 8 || hours >= 18) {
      ElMessage.warning('预约时间必须在8:00-18:00之间')
      return
    }

    // 检查是否是未来时间
    const now = new Date()
    if (appointmentTime <= now) {
      ElMessage.warning('预约时间必须大于当前时间')
      return
    }

    // 确保applyReason满足长度要求
    const applyReason = bookingForm.remark?.trim() || '服务预约'
    if (applyReason.length < 5) {
      ElMessage.warning('备注信息至少需要5个字符')
      return
    }
    if (applyReason.length > 500) {
      ElMessage.warning('备注信息不能超过500个字符')
      return
    }

    // 构建请求数据
    const data = {
      userId: Number(userInfo.userId),
      serviceItemId: Number(currentService.value.serviceId),
      serviceTypeId: Number(bookingForm.serviceTypeId),
      scheduleTime: new Date(appointmentTime.getTime() + 8 * 60 * 60 * 1000).toISOString().split('.')[0], // 转换为东八区时间
      applyReason: applyReason
    }

    // 添加日志输出
    console.log('提交预约数据:', data)

    await serviceStore.createServiceAppointment(data)
    ElMessage.success('预约成功')
    bookingDialogVisible.value = false

    // 切换到我的预约标签页并刷新列表
    activeTab.value = 'my'
    fetchMyAppointments()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('预约失败:', error)
      ElMessage.error(error.message || '预约失败')
    }
  } finally {
    submitting.value = false
  }
}

// 取消预约
const handleCancel = async (appointment) => {
  try {
    // 检查预约状态
    if (appointment.status !== 0 && appointment.status !== 1) {
      ElMessage.warning('只有待审核或已派单状态的预约可以取消')
      return
    }

    await ElMessageBox.confirm('确定要取消该预约吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await serviceStore.cancelServiceAppointment(appointment.id)
    ElMessage.success('取消预约成功')
    fetchMyAppointments()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消预约失败:', error)
      ElMessage.error('取消预约失败')
    }
  }
}

// 评价对话框相关
const evaluationDialogVisible = ref(false)
const currentAppointment = ref({})
const evaluationFormRef = ref(null)
const submittingEvaluation = ref(false)

const evaluationForm = reactive({
  rating: 0,
  content: ''
})

const evaluationRules = {
  rating: [
    { required: true, message: '请选择评分', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入评价内容', trigger: 'blur' },
    { min: 5, max: 500, message: '评价内容长度在 5 到 500 个字符', trigger: 'blur' }
  ]
}

// 打开评价对话框
const openEvaluationDialog = (appointment) => {
  currentAppointment.value = appointment
  evaluationForm.rating = 0
  evaluationForm.content = ''
  evaluationDialogVisible.value = true
}

// 提交评价
const submitEvaluation = async () => {
  if (!evaluationFormRef.value) return

  try {
    await evaluationFormRef.value.validate()

    submittingEvaluation.value = true
    await serviceStore.handleEvaluateService(currentAppointment.value.id, evaluationForm)
    ElMessage.success('评价成功')
    evaluationDialogVisible.value = false
    fetchMyAppointments()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('评价失败:', error)
      ElMessage.error('评价失败')
    }
  } finally {
    submittingEvaluation.value = false
  }
}

// 监听预约列表数据变化
watch(() => serviceStore.myAppointments, (newVal) => {
  console.log('预约列表数据发生变化:', newVal)
}, { deep: true })

// 监听总数变化
watch(() => serviceStore.appointmentTotal, (newVal) => {
  console.log('预约总数发生变化:', newVal)
})

// 组件挂载时获取数据
onMounted(async () => {
  // 从本地存储恢复用户信息
  const storedUserInfo = localStorage.getItem('userInfo')
  if (storedUserInfo) {
    try {
      const userInfo = JSON.parse(storedUserInfo)
      userStore.setUserInfo(userInfo)
    } catch (error) {
      console.error('解析用户信息失败:', error)
    }
  }

  await fetchServices()
})

// 监听标签页切换
watch(() => activeTab.value, (newTab) => {
  if (newTab === 'my') {
    fetchMyAppointments()
  }
})

// 预约详情对话框相关
const appointmentDetailVisible = ref(false)

// 查看预约详情
const viewAppointmentDetail = (appointment) => {
  currentAppointment.value = appointment
  appointmentDetailVisible.value = true
}
</script>

<style scoped>
.service-view {
  padding: 20px;
}

.content-card {
  margin-bottom: 20px;
}

.content-card h3 {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 500;
}

.filter-container {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
  align-items: center;
}

.search-input {
  width: 200px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

:deep(.el-rate) {
  display: inline-block;
  vertical-align: middle;
}

:deep(.el-rate__text) {
  margin-left: 10px;
  color: #666;
}

.status-time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.text-muted {
  color: #909399;
  font-style: italic;
}
</style>