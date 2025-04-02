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
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import CountTo from '@/components/CountTo/index.vue'
import {
  User, Timer, List, Bell,
  ArrowUp, ArrowDown
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { 
  getTrendData, 
  getActivityTypes, 
  getStatistics,
  getActivities 
} from '@/mock/dashboard'

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
const updateTrendChart = () => {
  const { dates, newUsers, activeUsers } = getTrendData(timeRange.value)
  
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

// 初始化饼图
const initPieChart = () => {
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
  const data = getActivityTypes()
  
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

// 刷新所有数据
const refreshData = () => {
  statistics.value = getStatistics()
  latestActivities.value = getActivities(3)
  updateTrendChart()
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
const showMoreActivities = () => {
  allActivities.value = getActivities(10)
  dialogVisible.value = true
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
</style>