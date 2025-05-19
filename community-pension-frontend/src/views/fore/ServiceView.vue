<template>
  <div class="service-view">
    <el-card class="content-card" shadow="hover">
      <h3>服务预约</h3>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="服务列表" name="list">
          <div class="filter-container">
            <el-input v-model="searchQuery" class="search-input" clearable placeholder="搜索服务名称"
                      @input="handleSearch">
              <template #prefix>
                <el-icon>
                  <Search/>
                </el-icon>
              </template>
            </el-input>
            <el-select v-model="categoryFilter" class="category-select" clearable placeholder="服务类别" @change="handleSearch">
              <el-option v-for="item in categoryOptions" :key="item" :label="item" :value="item"/>
            </el-select>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </div>

          <el-table v-loading="loading" :data="filteredServices" style="width: 100%">
            <el-table-column label="服务名称" min-width="140" prop="serviceName">
              <template #default="{ row }">
                <div class="service-name">
                  <el-icon><Service /></el-icon>
                  <span>{{ row.serviceName }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="服务类别" min-width="120" prop="serviceTypeName">
              <template #default="{ row }">
                <el-tag size="small" :type="getServiceTypeTagType(row.serviceType)">{{ row.serviceTypeName }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="服务信息" min-width="150">
              <template #default="{ row }">
                <div class="service-info">
                  <div><span class="price-tag">{{ row.price ? `${row.price} 元` : '价格未设置' }}</span></div>
                  <div><span class="duration-tag">{{ row.duration ? `${row.duration} 分钟` : '时长未设置' }}</span></div>
                </div>
              </template>
            </el-table-column>
            <el-table-column fixed="right" label="操作" min-width="150">
              <template #default="{ row }">
                <div class="action-buttons">
                  <el-button size="small" type="primary" @click="openBookingDialog(row)">
                    <el-icon><Calendar /></el-icon> 预约
                  </el-button>
                  <el-button size="small" type="info" @click="viewServiceDetail(row)">
                    <el-icon><InfoFilled /></el-icon> 详情
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination v-if="totalServices > 0" :current-page="currentPage" :page-size="pageSize" :total="totalServices"
                         class="pagination" layout="total, prev, pager, next" @current-change="handlePageChange"/>
        </el-tab-pane>

        <el-tab-pane label="我的预约" name="my">
          <div class="filter-container">
            <el-select v-model="statusFilter" class="status-filter" clearable placeholder="预约状态"
                       @change="fetchMyAppointments">
              <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value"/>
            </el-select>
            <el-date-picker v-model="dateRange" class="date-range" end-placeholder="结束日期" range-separator="至"
                            start-placeholder="开始日期" type="daterange" @change="fetchMyAppointments"/>
            <el-button circle class="refresh-button" icon="Refresh" title="刷新预约列表" type="primary"
                       @click="fetchMyAppointments"></el-button>
          </div>

          <el-table v-loading="loadingAppointments" :data="myAppointments" style="width: 100%">
            <template #empty>
              <el-empty description="暂无预约数据"/>
            </template>
            <el-table-column label="服务名称" min-width="120" prop="serviceName">
              <template #default="{ row }">
                <div class="service-name">{{ row.serviceName }}</div>
              </template>
            </el-table-column>
            <el-table-column label="服务类别" min-width="100" prop="serviceTypeName">
              <template #default="{ row }">
                <el-tag size="small" type="info">{{ row.serviceTypeName || '未分类' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="服务信息" min-width="150">
              <template #default="{ row }">
                <div class="service-info">
                  <div><span class="price-tag">{{ row.price || 0 }} 元</span></div>
                  <div><span class="duration-tag">{{ row.duration || 0 }} 分钟</span></div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="预约时间" min-width="160" prop="scheduleTime">
              <template #default="{ row }">
                <div class="time-info">
                  <el-icon>
                    <Calendar/>
                  </el-icon>
                  <span>{{ formatDateTime(row.scheduleTime || row.appointmentTime) }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="预约备注" min-width="150" prop="applyReason" show-overflow-tooltip>
              <template #default="{ row }">
                <span class="remark">{{ row.applyReason || '无' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="服务进度" min-width="120">
              <template #default="{ row }">
                <span class="service-progress">
                  <el-icon><Timer/></el-icon>
                  <template v-if="row.status === 0">等待审核</template>
                  <template v-else-if="row.status === 1">等待服务开始</template>
                  <template v-else-if="row.status === 2">服务进行中</template>
                  <template v-else-if="row.status === 3">服务已结束</template>
                  <template v-else-if="row.status === 4">已取消</template>
                  <template v-else-if="row.status === 5">已拒绝</template>
                  <template v-else>{{ row.statusName || '未知状态' }}</template>
                </span>
              </template>
            </el-table-column>
            <el-table-column label="状态" min-width="120">
              <template #default="{ row }">
                <div class="status-container">
                  <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
                  <div v-if="row.statusUpdateTime" class="status-time">
                    {{ formatDateTime(row.statusUpdateTime) }}
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column fixed="right" label="操作" min-width="150">
              <template #default="{ row }">
                <div class="action-buttons">
                  <el-button size="small" type="primary" @click="viewAppointmentDetail(row)">
                    详情
                  </el-button>
                  <el-button v-if="row.status === 0 || row.status === 1" size="small" type="danger"
                             @click="handleCancel(row)">
                    取消
                  </el-button>
                  <el-button v-if="row.status === 3 && !row.evaluated" size="small" type="success"
                             @click="openEvaluationDialog(row)">
                    评价
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination v-if="totalAppointments > 0" :current-page="appointmentPage" :page-size="appointmentPageSize"
                         :total="totalAppointments" class="pagination" layout="total, prev, pager, next"
                         @current-change="handleAppointmentPageChange"/>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 服务预约对话框 -->
    <el-dialog v-model="bookingDialogVisible" title="服务预约" width="500px">
      <el-form ref="bookingFormRef" :model="bookingForm" :rules="bookingRules" label-width="100px">
        <el-form-item label="服务名称">
          <span>{{ currentService.serviceName }}</span>
        </el-form-item>
        <el-form-item label="服务类别">
          <span>{{ currentService.serviceTypeName }}</span>
          <input v-model="bookingForm.serviceTypeId" type="hidden"/>
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
              :disabled-date="disabledDate"
              :formatter="formatDateWithTip"
              format="YYYY-MM-DD"
              placeholder="选择日期"
              type="date"
              value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="预约时间" prop="appointmentTime">
          <el-time-picker v-model="bookingForm.appointmentTime" :disabled="!bookingForm.appointmentDate" :disabled-hours="() => disabledTime(bookingForm.appointmentDate).hours"
                          :disabled-minutes="() => disabledTime(bookingForm.appointmentDate).minutes"
                          format="HH:mm"
                          placeholder="选择时间"/>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="bookingForm.remark" :rows="3" placeholder="请输入申请原因" type="textarea"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="bookingDialogVisible = false">取消</el-button>
          <el-button :loading="submitting" type="primary" @click="submitBooking">确认预约</el-button>
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
      <el-form ref="evaluationFormRef" :model="evaluationForm" :rules="evaluationRules" label-width="100px">
        <el-form-item label="服务名称">
          <span>{{ currentAppointment.serviceName }}</span>
        </el-form-item>
        <el-form-item label="评分" prop="rating">
          <el-rate v-model="evaluationForm.rating" :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                   :texts="['很差', '较差', '一般', '较好', '很好']" show-text/>
        </el-form-item>
        <el-form-item label="评价内容" prop="content">
          <el-input v-model="evaluationForm.content" :rows="4" placeholder="请输入您的评价内容" type="textarea"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="evaluationDialogVisible = false">取消</el-button>
          <el-button :loading="submittingEvaluation" type="primary" @click="submitEvaluation">提交评价</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 预约详情对话框 -->
    <el-dialog v-model="appointmentDetailVisible" title="预约详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="服务名称">{{ currentAppointment.serviceName }}</el-descriptions-item>
        <el-descriptions-item label="服务类别">{{ currentAppointment.serviceTypeName }}</el-descriptions-item>
        <el-descriptions-item label="价格">
          <template v-if="currentAppointment.price !== null && currentAppointment.price !== undefined">
            {{ currentAppointment.price }} 元
          </template>
          <template v-else-if="currentAppointment.actualFee !== null && currentAppointment.actualFee !== undefined">
            {{ currentAppointment.actualFee }} 元
          </template>
          <template v-else>
            <span class="text-muted">待定</span>
          </template>
        </el-descriptions-item>
        <el-descriptions-item label="时长">
          <template v-if="currentAppointment.duration !== null && currentAppointment.duration !== undefined">
            {{ currentAppointment.duration }} 分钟
          </template>
          <template
              v-else-if="currentAppointment.actualDuration !== null && currentAppointment.actualDuration !== undefined">
            {{ currentAppointment.actualDuration }} 分钟
          </template>
          <template v-else>
            <span class="text-muted">待定</span>
          </template>
        </el-descriptions-item>
        <el-descriptions-item label="预约时间">
          {{ formatDateTime(currentAppointment.scheduleTime || currentAppointment.appointmentTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="预约状态">
          <el-tag :type="getStatusType(currentAppointment.status)">
            {{ getStatusText(currentAppointment.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item :span="2" label="预约备注">{{
            currentAppointment.applyReason || '无'
          }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentAppointment.status >= 1" label="服务人员">
          <span v-if="currentAppointment.staffName">{{ currentAppointment.staffName }}</span>
          <span v-else class="text-muted">暂未指派</span>
        </el-descriptions-item>
        <el-descriptions-item v-if="currentAppointment.statusUpdateTime" label="状态更新时间">
          {{ formatDateTime(currentAppointment.statusUpdateTime) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import {computed, onBeforeUnmount, onMounted, reactive, ref, watch} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {formatDateTime} from '@/utils/date'
import {Calendar, Search, User} from '@element-plus/icons-vue'
import useServiceStore from '@/stores/fore/serviceStore'
import useUserStore from '@/stores/fore/userStore'
import {checkHoliday} from '@/api/fore/holiday'
import {useRoute, useRouter} from 'vue-router'

const serviceStore = useServiceStore()
const userStore = useUserStore()
const router = useRouter()
const route = useRoute()

// 从URL参数或localStorage恢复activeTab
const initActiveTab = () => {
  const tabParam = route.query.tab;
  const savedTab = localStorage.getItem('serviceActiveTab');

  if (tabParam === 'my' || tabParam === 'list') {
    return tabParam;
  } else if (savedTab === 'my' || savedTab === 'list') {
    return savedTab;
  }
  return 'list';  // 默认值
};

const activeTab = ref(initActiveTab())
const debugMode = ref(process.env.NODE_ENV === 'development')  // 只在开发环境开启调试模式

// 本地状态存储预约列表数据，不再使用计算属性
const localAppointments = ref([]);
const localTotal = ref(0);

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
  let result = services.value;
  
  // 根据搜索关键词筛选
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(service => 
      service.serviceName && service.serviceName.toLowerCase().includes(query)
    );
  }
  
  // 根据服务类别筛选（前端筛选，不依赖后端）
  if (categoryFilter.value) {
    result = result.filter(service => 
      service.serviceTypeName === categoryFilter.value
    );
  }
  
  return result;
})

// 我的预约相关
const loadingAppointments = computed(() => serviceStore.loading)
const myAppointments = computed(() => {
  return serviceStore.myAppointments
})
const statusFilter = ref('')
const dateRange = ref([])
const appointmentPage = ref(1)
const appointmentPageSize = ref(10)
const totalAppointments = computed(() => {
  return serviceStore.appointmentTotal
})

// 计算预约列表是否为空
const isEmptyAppointments = computed(() => {
  const isEmpty = !myAppointments.value || myAppointments.value.length === 0;
  return isEmpty;
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
  {value: 0, label: '待审核'},
  {value: 1, label: '已派单'},
  {value: 2, label: '服务中'},
  {value: 3, label: '已完成'},
  {value: 4, label: '已取消'},
  {value: 5, label: '已拒绝'}
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
  serviceTypeId: null,
  serviceItemId: null  // 添加serviceItemId字段，确保在表单中保存服务项目ID
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
  if (!date) return {hours: [], minutes: []}

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
    // 显示加载状态
    const loadingMessage = ElMessage({
      message: '正在加载服务数据...',
      type: 'info',
      duration: 0
    });

    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      serviceName: searchQuery.value || undefined
      // 移除serviceType参数，改用前端筛选
    };

    if (debugMode.value) {
      console.log('请求参数:', params);
    }

    await serviceStore.fetchServiceList(params);
    // 提取服务类别选项
    const categories = new Set();
    serviceStore.serviceList.forEach(service => {
      if (service.serviceTypeName) {
        categories.add(service.serviceTypeName);
      }
    });
    categoryOptions.value = Array.from(categories);
    
    if (debugMode.value) {
      console.log('服务列表:', services.value);
    }

    // 关闭加载消息
    loadingMessage.close();
  } catch (error) {
    ElMessage.error(error.message || '获取服务列表失败');
    console.error('获取服务列表失败:', error);
  }
};

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
  const isLoggedIn = await checkLoginStatus();
  if (!isLoggedIn) return;

  try {
    // 获取用户ID
    const userInfo = getUserInfo();
    if (!userInfo || !userInfo.userId) {
      ElMessage.warning('获取用户信息失败，请重新登录');
      router.push('/login');
      return;
    }

    // 显示加载状态
    const loadingMessage = ElMessage({
      message: '正在加载预约数据...',
      type: 'info',
      duration: 0
    });

    const params = {
      userId: userInfo.userId,
      pageNum: appointmentPage.value,
      pageSize: appointmentPageSize.value,
      status: statusFilter.value || undefined,
      startTime: dateRange.value ? dateRange.value[0] : undefined,
      endTime: dateRange.value ? dateRange.value[1] : undefined
    };

    try {
      const result = await serviceStore.fetchMyAppointments(params);
      // 检查结果 
      if (result && Array.isArray(result) && result.length > 0) {
        // 如果store中没有数据但API返回了数据，尝试主动更新store
        if (serviceStore.myAppointments.length === 0) {
          // 确保store.myAppointments是响应式数组
          if (Array.isArray(serviceStore.myAppointments)) {
            serviceStore.myAppointments.length = 0;
            result.forEach(item => serviceStore.myAppointments.push(item));
          }
        }
      }
    } catch (error) {
      ElMessage.error(error.message || '获取预约列表失败');
    } finally {
      // 关闭加载消息
      loadingMessage.close();
    }
  } catch (error) {
    ElMessage.error(error.message || '获取预约列表失败');
  }
};

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
    {required: true, message: '请选择预约日期', trigger: 'change'}
  ],
  appointmentTime: [
    {required: true, message: '请选择预约时间', trigger: 'change'}
  ],
  remark: [
    {required: true, message: '请输入申请原因', trigger: 'blur'},
    {min: 5, max: 500, message: '申请原因长度必须在5-500个字符之间', trigger: 'blur'}
  ]
}

// 服务详情对话框相关
const detailDialogVisible = ref(false)

// 打开预约对话框
const openBookingDialog = async (service) => {
  const isLoggedIn = await checkLoginStatus();
  if (!isLoggedIn) return;

  // 确保service对象有效
  if (!service) {
    ElMessage.error('服务项目信息不完整，请刷新页面后重试');
    return;
  }

  // 获取服务项目ID，使用正确的字段名serviceId
  const serviceId = service.id || service.serviceId;
  
  if (!serviceId) {
    ElMessage.error('服务项目ID不存在，请刷新页面后重试');
    if (debugMode.value) {
      console.error('服务项目信息不完整:', service);
    }
    return;
  }

  currentService.value = service;
  
  // 重置表单数据
  Object.assign(bookingForm, {
    appointmentDate: null,
    appointmentTime: null,
    remark: '',
    serviceTypeId: service.serviceType || null,
    serviceItemId: serviceId  // 将服务项目ID保存到表单中
  });
  
  if (debugMode.value) {
    console.log('打开预约对话框，当前服务:', service);
    console.log('服务项目ID:', serviceId);
    console.log('表单数据:', bookingForm);
  }
  
  bookingDialogVisible.value = true;
};

// 打开服务详情对话框
const viewServiceDetail = (service) => {
  currentService.value = service
  detailDialogVisible.value = true
}

// 提交预约
const submitBooking = async () => {
  const isLoggedIn = await checkLoginStatus();
  if (!isLoggedIn) return;

  if (!bookingFormRef.value) return;
  
  await bookingFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        submitting.value = true;
        
        // 优先使用表单中的serviceItemId
        const serviceItemId = bookingForm.serviceItemId;
        
        // 检查服务项目是否存在
        if (!serviceItemId) {
          ElMessage.error('服务项目信息不完整，请重新选择服务');
          return;
        }
        
        // 安全地获取日期和时间值
        const dateStr = bookingForm.appointmentDate;
        const timeObj = bookingForm.appointmentTime;
        
        if (!dateStr || !timeObj) {
          ElMessage.error('请选择预约日期和时间');
          return;
        }
        
        const timeStr = formatTime(timeObj);
        const scheduleTime = `${dateStr}T${timeStr}`;
        
        // 检查是否是未来时间
        const scheduleDateTime = new Date(scheduleTime);
        const now = new Date();
        
        // 添加至少提前1小时预约的限制
        const oneHourLater = new Date(now.getTime() + 60 * 60 * 1000);
        
        if (scheduleDateTime <= now) {
          ElMessage.error('预约时间必须大于当前时间');
          return;
        }
        
        if (scheduleDateTime <= oneHourLater) {
          ElMessage.error('预约时间必须至少提前1小时');
          return;
        }
        
        // 确保申请原因符合要求
        if (!bookingForm.remark || bookingForm.remark.length < 5) {
          ElMessage.error('申请原因长度必须在5-500个字符之间');
          return;
        }
        
        const params = {
          serviceItemId: serviceItemId,
          userId: userStore.userInfo.userId,
          scheduleTime: scheduleTime,
          applyReason: bookingForm.remark
        };
        
        if (debugMode.value) {
          console.log('提交预约参数:', params);
          console.log('当前服务信息:', currentService.value);
          console.log('表单数据:', bookingForm);
        }
        
        await serviceStore.createServiceAppointment(params);
        ElMessage.success('预约提交成功');
        bookingDialogVisible.value = false;
        // 重新获取预约列表
        if (activeTab.value === 'my') {
          fetchMyAppointments();
        }
      } catch (error) {
        console.error('预约提交失败:', error);
        // 增强错误处理，提供更详细的错误信息
        if (error.response && error.response.data && error.response.data.message) {
          ElMessage.error(error.response.data.message);
        } else {
          ElMessage.error(error.message || '预约提交失败，请稍后重试或联系客服');
        }
      } finally {
        submitting.value = false;
      }
    }
  });
};

// 格式化时间为HH:MM:SS格式
const formatTime = (time) => {
  if (!time) return '00:00:00';
  if (typeof time === 'string') return time;
  
  const hours = time.getHours().toString().padStart(2, '0');
  const minutes = time.getMinutes().toString().padStart(2, '0');
  return `${hours}:${minutes}:00`;
};

// 取消预约
const handleCancel = async (appointment) => {
  const isLoggedIn = await checkLoginStatus();
  if (!isLoggedIn) return;

  try {
    await ElMessageBox.confirm('确认要取消该预约吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });

    await serviceStore.cancelServiceAppointment(appointment.id);
    ElMessage.success('预约取消成功');
    fetchMyAppointments();
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消预约失败:', error);
      ElMessage.error(error.message || '取消预约失败');
    }
  }
};

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
    {required: true, message: '请选择评分', trigger: 'change'}
  ],
  content: [
    {required: true, message: '请输入评价内容', trigger: 'blur'},
    {min: 5, max: 500, message: '评价内容长度在 5 到 500 个字符', trigger: 'blur'}
  ]
}

// 打开评价对话框
const openEvaluationDialog = async (appointment) => {
  const isLoggedIn = await checkLoginStatus();
  if (!isLoggedIn) return;

  currentAppointment.value = appointment;
  evaluationDialogVisible.value = true;
};

// 提交评价
const submitEvaluation = async () => {
  const isLoggedIn = await checkLoginStatus();
  if (!isLoggedIn) return;

  if (!evaluationFormRef.value) return;

  await evaluationFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const params = {
          ...evaluationForm.value,
          appointmentId: currentAppointment.value.id
        };

        await serviceStore.handleEvaluateService(params.appointmentId, params);
        ElMessage.success('评价提交成功');
        evaluationDialogVisible.value = false;
        // 重新获取预约列表
        fetchMyAppointments();
      } catch (error) {
        console.error('评价提交失败:', error);
        ElMessage.error(error.message || '评价提交失败');
      }
    }
  });
};

// 监听预约列表数据变化
watch(() => serviceStore.myAppointments, (newVal) => {
  // 监听预约列表数据变化
}, {deep: true})

// 监听总数变化
watch(() => serviceStore.appointmentTotal, (newVal) => {
  // 监听预约总数变化
})

// 监听标签页切换，并保存到localStorage和URL
watch(() => activeTab.value, (newTab) => {
  // 保存当前活动标签到localStorage
  localStorage.setItem('serviceActiveTab', newTab);

  // 更新URL参数，但不触发路由跳转
  const query = {...route.query, tab: newTab};
  router.replace({query});

  if (newTab === 'my') {
    fetchMyAppointments();
  } else if (newTab === 'list') {
    fetchServices();
  }
});

// 监听路由变化，如果tab参数变化则更新activeTab
watch(() => route.query.tab, (newTab) => {
  if (newTab && (newTab === 'my' || newTab === 'list') && newTab !== activeTab.value) {
    activeTab.value = newTab;
  }
}, {immediate: true});

// 组件卸载时保存状态
onBeforeUnmount(() => {
  // 保存当前活动标签到localStorage
  localStorage.setItem('serviceActiveTab', activeTab.value);
});

// 预约详情对话框相关
const appointmentDetailVisible = ref(false)

// 查看预约详情
const viewAppointmentDetail = (appointment) => {
  // 创建一个新对象以避免修改原始数据
  currentAppointment.value = {
    ...appointment,
    // 不需要设置默认值，让模板中的条件判断来处理
  };

  appointmentDetailVisible.value = true;
}

// 重新加载数据
const reloadAppointments = () => {
  fetchMyAppointments();
};

// 检查登录状态
const checkLoginStatus = async () => {
  try {
    // 检查是否有token，使用正确的key
    const token = localStorage.getItem('user-access-token');
    if (!token) {
      ElMessage.warning('请先登录后再访问服务预约');
      router.push('/login');
      return false;
    }

    // 从本地存储获取用户信息
    const localUserInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
    if (!localUserInfo || !localUserInfo.userId) {
      ElMessage.warning('请先登录后再访问服务预约');
      router.push('/login');
      return false;
    }

    // 更新store中的用户信息
    if (!userStore.userInfo) {
      userStore.setUserInfo(localUserInfo);
    }

    return true;
  } catch (error) {
    console.error('检查登录状态失败:', error);
    ElMessage.error('登录状态验证失败，请重新登录');
    router.push('/login');
    return false;
  }
}

// 组件挂载时获取数据
onMounted(async () => {
  // 从本地存储恢复用户信息
  const storedUserInfo = localStorage.getItem('userInfo');
  if (storedUserInfo) {
    try {
      const userInfo = JSON.parse(storedUserInfo);
      userStore.setUserInfo(userInfo);
    } catch (error) {
      // 用户信息解析失败
    }
  }

  // 从localStorage恢复活动标签页状态
  const savedTab = localStorage.getItem('serviceActiveTab');
  if (savedTab) {
    activeTab.value = savedTab;
  }

  // 根据当前标签页加载相应的数据
  if (activeTab.value === 'my') {
    fetchMyAppointments();
  } else {
    await fetchServices();
    
    // 从服务列表中提取唯一的服务类别
    const categories = new Set();
    services.value.forEach(service => {
      if (service.serviceTypeName) {
        categories.add(service.serviceTypeName);
      }
    });
    categoryOptions.value = Array.from(categories);
  }

  // 添加事件监听，防止外部刷新影响标签页状态
  window.addEventListener('refresh-dashboard-data', handleRefreshEvent);
});

// 组件卸载时清理事件监听器
onBeforeUnmount(() => {
  // 保存当前活动标签到localStorage
  localStorage.setItem('serviceActiveTab', activeTab.value);

  // 移除事件监听器
  window.removeEventListener('refresh-dashboard-data', handleRefreshEvent);
});

// 处理外部刷新事件，确保不会重置标签页
const handleRefreshEvent = (event) => {
  // 如果是在"我的预约"标签页，则刷新预约数据而不是切换标签页
  if (activeTab.value === 'my') {
    // 延迟执行以避免与主刷新冲突
    setTimeout(() => {
      fetchMyAppointments();
    }, 500);

    // 阻止事件进一步传播，避免影响其他组件
    event.stopPropagation();
  }
};

// 获取服务类型标签颜色
const getServiceTypeTagType = (serviceType) => {
  switch (serviceType) {
    case 1:
      return 'success';
    case 2:
      return 'warning';
    case 3:
      return 'danger';
    default:
      return 'info';
  }
}
</script>

<style scoped>
.service-view {
  padding: 20px;
}

.content-card {
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.content-card h3 {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 500;
  color: #303133;
}

.filter-container {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

.search-input {
  width: 200px;
}

.category-select {
  width: 150px;
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

/* 新增样式 */
.service-name {
  font-weight: 500;
  color: #303133;
}

.price-tag {
  font-weight: bold;
  color: #e6a23c;
}

.duration-tag {
  color: #409eff;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

/* 表格样式优化 */
:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table__header) {
  background-color: #f5f7fa;
}

:deep(.el-table__row:hover) {
  background-color: #f0f7ff !important;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .filter-container {
    flex-direction: column;
    align-items: stretch;
  }

  .search-input {
    width: 100%;
  }

  .action-buttons {
    flex-direction: column;
  }
}

/* 我的预约标签页样式 */
.status-filter {
  width: 150px;
}

.date-range {
  width: 350px;
}

.refresh-button {
  margin-left: auto;
}

.service-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.time-info {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #606266;
}

.time-info .el-icon {
  font-size: 16px;
  color: #409eff;
}

.remark {
  color: #606266;
  font-size: 13px;
}

.staff-name {
  display: flex;
  align-items: center;
  gap: 5px;
}

.staff-name .el-icon {
  color: #67c23a;
}

.status-container {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

/* 状态更新时间 */
.status-time {
  font-size: 12px;
  color: #909399;
}
</style>