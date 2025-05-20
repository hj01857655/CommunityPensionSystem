<template>
  <div class="dashboard-container">
    <!-- 统计数据卡片 -->
    <el-row :gutter="20">
      <el-col :span="6" v-for="(item, index) in statistics" :key="index">
        <el-card shadow="hover" class="stats-card">
          <div class="card-header">
            <el-icon :class="item.icon"><component :is="item.icon" /></el-icon>
            <span>{{ item.title }}</span>
          </div>
          <div class="card-content">
            <count-to
              :start-val="0"
              :end-val="item.value"
              :duration="2000"
              class="card-value"
            />
            <div class="card-footer">
              <span :class="['trend', item.trend > 0 ? 'up' : 'down']">
                {{ Math.abs(item.trend) }}%
                <el-icon><component :is="item.trend > 0 ? 'ArrowUp' : 'ArrowDown'" /></el-icon>
              </span>
              <span class="label">较上周</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <!-- 用户趋势图 -->
      <el-col :span="16">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-header">
              <span>用户活跃趋势</span>
              <el-radio-group v-model="timeRange" size="small" @change="handleTimeRangeChange">
                <el-radio-button value="week">本周</el-radio-button>
                <el-radio-button value="month">本月</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="chart-content" ref="trendChartRef"></div>
        </el-card>
      </el-col>

      <!-- 活动分布图 -->
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-header">
              <span>活动类型分布</span>
            </div>
          </template>
          <div class="chart-content" ref="pieChartRef"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最新活动列表 -->
    <el-card class="activity-card">
      <template #header>
        <div class="activity-header">
          <span>最新活动</span>
          <el-button type="primary" link @click="showMoreActivities">查看更多</el-button>
        </div>
      </template>
      <el-table :data="latestActivities" style="width: 100%">
        <el-table-column prop="name" label="活动名称" />
        <el-table-column prop="date" label="开始时间" width="180" />
        <el-table-column prop="participants" label="参与人数" width="120" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 底部区域：通知和预警 -->
    <el-row :gutter="20" class="bottom-row">
      <!-- 最新通知公告 -->
      <el-col :span="12">
        <el-card class="notification-card">
          <template #header>
            <div class="notification-header">
              <span>最新通知公告</span>
              <el-button type="primary" link @click="showMoreNotifications">查看更多</el-button>
            </div>
          </template>
          <el-empty v-if="!notifications.length" description="暂无通知" />
          <el-table v-else :data="notifications" style="width: 100%">
            <el-table-column prop="title" label="标题" show-overflow-tooltip />
            <el-table-column prop="type" label="类型" width="100" />
            <el-table-column prop="date" label="发布时间" width="180">
              <template #default="{ row }">
                {{ formatDateTime(row.date) }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getNotificationStatusType(row.status)">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 健康预警 -->
      <el-col :span="12">
        <el-card class="warning-card">
          <template #header>
            <div class="warning-header">
              <span>健康预警</span>
              <el-button type="primary" link @click="showMoreWarnings">查看更多</el-button>
            </div>
          </template>
          <el-empty v-if="!warnings.length" description="暂无预警" />
          <el-table v-else :data="warnings" style="width: 100%">
            <el-table-column prop="userName" label="老人姓名" width="120" />
            <el-table-column prop="warningType" label="预警类型" width="120" />
            <el-table-column prop="warningMessage" label="预警内容" show-overflow-tooltip />
            <el-table-column prop="warningTime" label="预警时间" width="180">
              <template #default="{ row }">
                {{ formatDateTime(row.warningTime) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 更多活动对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="活动列表"
      width="80%"
    >
      <el-table :data="allActivities" style="width: 100%">
        <el-table-column prop="name" label="活动名称" />
        <el-table-column prop="date" label="开始时间" width="180" />
        <el-table-column prop="participants" label="参与人数" width="120" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 更多通知对话框 -->
    <el-dialog
      v-model="notificationDialogVisible"
      title="通知公告列表"
      width="80%"
    >
      <el-table :data="allNotifications" style="width: 100%">
        <el-table-column prop="title" label="标题" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="100" />
        <el-table-column prop="date" label="发布时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.date) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getNotificationStatusType(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="notificationDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 更多预警对话框 -->
    <el-dialog
      v-model="warningDialogVisible"
      title="健康预警列表"
      width="80%"
    >
      <el-table :data="allWarnings" style="width: 100%">
        <el-table-column prop="userName" label="老人姓名" width="120" />
        <el-table-column prop="warningType" label="预警类型" width="120" />
        <el-table-column prop="warningMessage" label="预警内容" show-overflow-tooltip />
        <el-table-column prop="warningTime" label="预警时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.warningTime) }}
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="warningDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {
  getActivityTypeData,
  getOverviewStatistics,
  getRecentActivities,
  getRecentNotifications,
  getStatisticsData,
  getUserTrendData,
  getWarningData
} from '@/api/back/dashboard'
// WebSocket已在main.js中初始化
import CountTo from '@/components/common/utils/CountTo/index.vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { nextTick, onMounted, onUnmounted, ref } from 'vue'

// 引用图表DOM
const trendChartRef = ref(null)
const pieChartRef = ref(null)

// 图表实例
let trendChart = null
let pieChart = null

// 时间范围选择
const timeRange = ref('week')

// 数据状态
const statistics = ref([])
const latestActivities = ref([])
const allActivities = ref([])
const dialogVisible = ref(false)
const loading = ref(false)
const notifications = ref([])
const allNotifications = ref([])
const notificationDialogVisible = ref(false)
const warnings = ref([])
const allWarnings = ref([])
const warningDialogVisible = ref(false)

// 定时刷新
let refreshTimer = null

// 初始化趋势图
const initTrendChart = () => {
  if (!trendChartRef.value) return
  
  trendChart = echarts.init(trendChartRef.value, null, {
    renderer: 'canvas',
    useDirtyRect: true,
    locale: 'ZH',
    devicePixelRatio: window.devicePixelRatio,
    useCoarsePointer: true,
    useCoarsePointerMove: true,
    throttleType: 'debounce',
    throttleDelay: 100,
    event: {
      wheel: { passive: true },
      mousewheel: { passive: true }
    }
  })
  updateTrendChart()
}

// 更新趋势图数据
const updateTrendChart = async () => {
  try {
    // 使用API获取趋势数据
    const response = await getUserTrendData(timeRange.value)
    if (response.code === 200 && response.data) {
      const { dates, newUsers, activeUsers } = response.data
      
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['新增用户', '活跃用户']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: dates
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '新增用户',
            type: 'line',
            data: newUsers,
            smooth: true,
            areaStyle: {
              opacity: 0.1
            }
          },
          {
            name: '活跃用户',
            type: 'line',
            data: activeUsers,
            smooth: true,
            areaStyle: {
              opacity: 0.1
            }
          }
        ]
      }
      
      trendChart?.setOption(option)
    }
  } catch (error) {
    console.error('获取用户趋势数据失败:', error)
    ElMessage.error('获取用户趋势数据失败')
    
    // 如果API失败，尝试使用备选方案(例如缓存的数据)或显示错误状态
  }
}

// 初始化饼图
const initPieChart = async () => {
  if (!pieChartRef.value) return
  
  pieChart = echarts.init(pieChartRef.value, null, {
    renderer: 'canvas',
    useDirtyRect: true,
    locale: 'ZH',
    devicePixelRatio: window.devicePixelRatio,
    useCoarsePointer: true,
    useCoarsePointerMove: true,
    throttleType: 'debounce',
    throttleDelay: 100,
    event: {
      wheel: { passive: true },
      mousewheel: { passive: true }
    }
  })
  
  try {
    // 使用API获取活动类型数据
    const response = await getActivityTypeData()
    if (response.code === 200 && response.data) {
      const data = response.data
      
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '活动类型',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '20',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: data
          }
        ]
      }
      
      pieChart?.setOption(option)
    }
  } catch (error) {
    console.error('获取活动类型数据失败:', error)
    ElMessage.error('获取活动类型数据失败')
    
    // 如果API失败，尝试显示错误状态
  }
}

// 刷新所有数据
const refreshData = async () => {
  loading.value = true
  
  try {
    // 尝试使用主要统计数据API
    const statsResponse = await getStatisticsData()
    if (statsResponse.code === 200 && statsResponse.data) {
      statistics.value = statsResponse.data
    } else {
      // 如果主要API失败，尝试使用备选API
      try {
        const overviewResponse = await getOverviewStatistics()
        if (overviewResponse) {
          // 转换为列表格式，以适应UI展示
          statistics.value = Object.entries(overviewResponse).map(([key, value]) => {
            // 根据键名设置不同的图标
            let icon = 'User'
            if (key.includes('activity')) icon = 'List'
            else if (key.includes('active')) icon = 'Timer'
            else if (key.includes('ongoing')) icon = 'Bell'
            
            return {
              title: key.replace(/([A-Z])/g, ' $1').replace(/^./, str => str.toUpperCase()),
              value: typeof value === 'object' ? value.count : value,
              trend: typeof value === 'object' ? value.trend || 0 : 0,
              icon
            }
          }).filter(item => item.title && item.value !== undefined)
        }
      } catch (fallbackError) {
        console.error('备选统计API也失败了:', fallbackError)
      }
    }
    
    // 最新活动
    const activitiesResponse = await getRecentActivities(3)
    if (activitiesResponse.code === 200 && activitiesResponse.data) {
      latestActivities.value = activitiesResponse.data
    }
    
    // 最新通知
    const notificationsResponse = await getRecentNotifications(5)
    if (notificationsResponse.code === 200 && notificationsResponse.data) {
      notifications.value = notificationsResponse.data
    }
    
    // 健康预警
    const warningsResponse = await getWarningData(5)
    if (warningsResponse.code === 200 && warningsResponse.data) {
      warnings.value = warningsResponse.data
    }
    
    // 更新趋势图
    await updateTrendChart()
  } catch (error) {
    console.error('刷新仪表盘数据失败:', error)
    ElMessage.error('刷新仪表盘数据失败')
  } finally {
    loading.value = false
  }
}

// 处理时间范围变化
const handleTimeRangeChange = () => {
  updateTrendChart()
}

// 获取状态对应的类型
const getStatusType = (status) => {
  const types = {
    '未开始': 'info',
    '进行中': 'success',
    '已结束': 'warning'
  }
  return types[status] || 'info'
}

// 显示更多活动
const showMoreActivities = async () => {
  try {
    const response = await getRecentActivities(10)
    if (response.code === 200 && response.data) {
      allActivities.value = response.data
      dialogVisible.value = true
    }
  } catch (error) {
    console.error('获取更多活动数据失败:', error)
    ElMessage.error('获取更多活动数据失败')
  }
}

// 显示更多通知
const showMoreNotifications = async () => {
  try {
    const response = await getRecentNotifications(10)
    if (response.code === 200 && response.data) {
      allNotifications.value = response.data
      notificationDialogVisible.value = true
    }
  } catch (error) {
    console.error('获取更多通知数据失败:', error)
    ElMessage.error('获取更多通知数据失败')
  }
}

// 显示更多预警
const showMoreWarnings = async () => {
  try {
    const response = await getWarningData(10)
    if (response.code === 200 && response.data) {
      allWarnings.value = response.data
      warningDialogVisible.value = true
    }
  } catch (error) {
    console.error('获取更多预警数据失败:', error)
    ElMessage.error('获取更多预警数据失败')
  }
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '';
  const date = new Date(dateTime);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
}

// 获取通知状态对应的类型
const getNotificationStatusType = (status) => {
  const types = {
    '草稿': 'info',
    '已发布': 'success',
    '已撤回': 'danger'
  }
  return types[status] || 'info'
}

// 监听窗口大小变化
const handleResize = () => {
  if (trendChart) {
    trendChart.resize({
      animation: {
        duration: 300,
        easing: 'cubicOut'
      }
    })
  }
  if (pieChart) {
    pieChart.resize({
      animation: {
        duration: 300,
        easing: 'cubicOut'
      }
    })
  }
}

import AdminWebSocketClient from '@/utils/adminWebsocket';

onMounted(() => {
  // 初始化数据
  refreshData()
  
  // 初始化图表
  nextTick(() => {
    initTrendChart()
    initPieChart()
  })
  
  // 添加窗口大小变化监听
  window.addEventListener('resize', handleResize, {
    passive: true
  })
  
  // 设置定时刷新
  refreshTimer = setInterval(refreshData, 60000) // 每分钟刷新一次
  
  // 确保 WebSocket 连接初始化
  const token = sessionStorage.getItem('admin-access-token');
  if (token) {
    console.log('在后台首页初始化 WebSocket 连接');
    // 强制初始化 WebSocket，确保连接已建立
    AdminWebSocketClient.init(token, true);
  } else {
    console.warn('无法获取管理员 token，无法初始化 WebSocket');
  }
})

onUnmounted(() => {
  // 清理定时器
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
  
  // 移除窗口大小变化监听
  window.removeEventListener('resize', handleResize)
  
  // 销毁图表实例
  trendChart?.dispose()
  pieChart?.dispose()
  
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.stats-card {
  margin-bottom: 20px;
  .card-header {
    display: flex;
    align-items: center;
    font-size: 14px;
    color: #606266;
    margin-bottom: 10px;
    .el-icon {
      margin-right: 8px;
      font-size: 20px;
      color: #409EFF;
    }
  }
  .card-content {
    text-align: center;
  }
  .card-value {
    font-size: 24px;
    font-weight: bold;
    color: #409EFF;
  }
  .card-footer {
    margin-top: 8px;
    font-size: 12px;
    .trend {
      &.up {
        color: #67C23A;
      }
      &.down {
        color: #F56C6C;
      }
    }
    .label {
      color: #909399;
      margin-left: 8px;
    }
  }
}

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  .chart-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .chart-content {
    height: 300px;
  }
}

.activity-card {
  .activity-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}

.bottom-row {
  margin-top: 20px;
}

.notification-card,
.warning-card {
  .notification-header,
  .warning-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>