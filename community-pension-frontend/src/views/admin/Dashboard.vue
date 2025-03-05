<template>
    <div class="dashboard">
      <!-- 统计卡片 -->
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon user-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-title">老人总数</div>
              <div class="stat-value">{{ stats.elderCount }}</div>
              <div class="stat-desc">较上月 <span class="up">+5.2%</span></div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon service-icon">
              <el-icon><Service /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-title">服务预约</div>
              <div class="stat-value">{{ stats.serviceCount }}</div>
              <div class="stat-desc">较上月 <span class="up">+3.8%</span></div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon activity-icon">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-title">活动数量</div>
              <div class="stat-value">{{ stats.activityCount }}</div>
              <div class="stat-desc">较上月 <span class="down">-1.2%</span></div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon notice-icon">
              <el-icon><Bell /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-title">通知公告</div>
              <div class="stat-value">{{ stats.noticeCount }}</div>
              <div class="stat-desc">较上月 <span class="up">+12.5%</span></div>
            </div>
          </el-card>
        </el-col>
      </el-row>
  
      <!-- 图表区域 -->
      <el-row :gutter="20" class="chart-row">
        <el-col :xs="24" :lg="16">
          <el-card shadow="hover" class="chart-card">
            <template #header>
              <div class="chart-header">
                <span>服务预约趋势</span>
                <el-radio-group v-model="timeRange" size="small">
                  <el-radio-button value="week">本周</el-radio-button>
                  <el-radio-button value="month">本月</el-radio-button>
                  <el-radio-button value="year">全年</el-radio-button>
                </el-radio-group>
              </div>
            </template>
            <div ref="serviceChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="8">
          <el-card shadow="hover" class="chart-card">
            <template #header>
              <div class="chart-header">
                <span>老人年龄分布</span>
              </div>
            </template>
            <div ref="ageChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
  
      <!-- 最近活动和通知 -->
      <el-row :gutter="20" class="data-row">
        <el-col :xs="24" :md="12">
          <el-card shadow="hover" class="data-card">
            <template #header>
              <div class="data-header">
                <span>最近活动</span>
                <el-button type="primary" link>查看全部</el-button>
              </div>
            </template>
            <el-table :data="recentActivities" style="width: 100%" :show-header="false">
              <el-table-column width="60">
                <template #default="scope">
                  <el-avatar :size="40" :icon="Calendar" class="activity-avatar" />
                </template>
              </el-table-column>
              <el-table-column>
                <template #default="scope">
                  <div class="activity-info">
                    <div class="activity-title">{{ scope.row.title }}</div>
                    <div class="activity-time">{{ scope.row.time }}</div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column width="100" align="right">
                <template #default="scope">
                  <el-tag :type="scope.row.status === '进行中' ? 'success' : 'info'">
                    {{ scope.row.status }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
        <el-col :xs="24" :md="12">
          <el-card shadow="hover" class="data-card">
            <template #header>
              <div class="data-header">
                <span>最新通知</span>
                <el-button type="primary" link>查看全部</el-button>
              </div>
            </template>
            <el-table :data="recentNotices" style="width: 100%" :show-header="false">
              <el-table-column width="60">
                <template #default="scope">
                  <el-avatar :size="40" :icon="Bell" class="notice-avatar" />
                </template>
              </el-table-column>
              <el-table-column>
                <template #default="scope">
                  <div class="notice-info">
                    <div class="notice-title">{{ scope.row.title }}</div>
                    <div class="notice-time">{{ scope.row.time }}</div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column width="100" align="right">
                <template #default="scope">
                  <el-tag :type="scope.row.type === '重要' ? 'danger' : 'info'">
                    {{ scope.row.type }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted, onBeforeUnmount } from 'vue'
  import { User, Service, Calendar, Bell } from '@element-plus/icons-vue'
  import * as echarts from 'echarts'
  
  // 统计数据
  const stats = ref({
    elderCount: 256,
    serviceCount: 128,
    activityCount: 45,
    noticeCount: 32
  })
  
  // 时间范围选择
  const timeRange = ref('month')
  
  // 图表引用
  const serviceChartRef = ref(null)
  const ageChartRef = ref(null)
  let serviceChart = null
  let ageChart = null
  
  // 最近活动数据
  const recentActivities = ref([
    { title: '健康讲座', time: '2025-02-28 14:00', status: '未开始' },
    { title: '太极班', time: '2025-02-26 09:00', status: '进行中' },
    { title: '棋牌比赛', time: '2025-02-25 14:00', status: '已结束' },
    { title: '春节联欢会', time: '2025-02-20 18:00', status: '已结束' }
  ])
  
  // 最新通知数据
  const recentNotices = ref([
    { title: '关于开展全民健康体检的通知', time: '2025-02-23', type: '重要' },
    { title: '春节期间社区服务安排', time: '2025-02-20', type: '普通' },
    { title: '社区文化活动中心装修通知', time: '2025-02-19', type: '普通' },
    { title: '关于加强社区防疫通知书', time: '2025-02-18', type: '重要' }
  ])
  
  // 初始化服务预约趋势图表
  const initServiceChart = () => {
    if (!serviceChartRef.value) return
    
    serviceChart = echarts.init(serviceChartRef.value)
    
    const option = {
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['送餐服务', '健康体检', '康复护理', '日间照料']
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
        data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '送餐服务',
          type: 'line',
          stack: 'Total',
          data: [12, 13, 10, 13, 9, 23, 21],
          smooth: true
        },
        {
          name: '健康体检',
          type: 'line',
          stack: 'Total',
          data: [22, 18, 19, 23, 29, 33, 31],
          smooth: true
        },
        {
          name: '康复护理',
          type: 'line',
          stack: 'Total',
          data: [15, 13, 11, 13, 12, 13, 10],
          smooth: true
        },
        {
          name: '日间照料',
          type: 'line',
          stack: 'Total',
          data: [8, 9, 9, 8, 10, 12, 14],
          smooth: true
        }
      ]
    }
    
    serviceChart.setOption(option)
  }
  
  // 初始化老人年龄分布图表
  const initAgeChart = () => {
    if (!ageChartRef.value) return
    
    ageChart = echarts.init(ageChartRef.value)
    
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 10,
        data: ['60-65岁', '66-70岁', '71-75岁', '76-80岁', '80岁以上']
      },
      series: [
        {
          name: '年龄分布',
          type: 'pie',
          radius: ['50%', '70%'],
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
              fontSize: 16,
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: [
            { value: 78, name: '60-65岁' },
            { value: 65, name: '66-70岁' },
            { value: 48, name: '71-75岁' },
            { value: 35, name: '76-80岁' },
            { value: 30, name: '80岁以上' }
          ]
        }
      ]
    }
    
    ageChart.setOption(option)
  }
  
  // 监听窗口大小变化，调整图表大小
  const handleResize = () => {
    serviceChart && serviceChart.resize()
    ageChart && ageChart.resize()
  }
  
  onMounted(() => {
    // 初始化图表
    initServiceChart()
    initAgeChart()
    
    // 添加窗口大小变化监听
    window.addEventListener('resize', handleResize)
  })
  
  onBeforeUnmount(() => {
    // 移除窗口大小变化监听
    window.removeEventListener('resize', handleResize)
    
    // 销毁图表实例
    serviceChart && serviceChart.dispose()
    ageChart && ageChart.dispose()
  })
  </script>
  
  <style scoped>
  .dashboard {
    padding: 10px;
  }
  
  .stat-card {
    margin-bottom: 20px;
    display: flex;
    align-items: center;
    padding: 10px;
  }
  
  .stat-icon {
    width: 60px;
    height: 60px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 15px;
  }
  
  .stat-icon .el-icon {
    font-size: 30px;
    color: white;
  }
  
  .user-icon {
    background-color: #409EFF;
  }
  
  .service-icon {
    background-color: #67C23A;
  }
  
  .activity-icon {
    background-color: #E6A23C;
  }
  
  .notice-icon {
    background-color: #F56C6C;
  }
  
  .stat-content {
    flex: 1;
  }
  
  .stat-title {
    font-size: 14px;
    color: #909399;
  }
  
  .stat-value {
    font-size: 24px;
    font-weight: bold;
    margin: 5px 0;
  }
  
  .stat-desc {
    font-size: 12px;
    color: #909399;
  }
  
  .up {
    color: #67C23A;
  }
  
  .down {
    color: #F56C6C;
  }
  
  .chart-row {
    margin-bottom: 20px;
  }
  
  .chart-card {
    margin-bottom: 20px;
  }
  
  .chart-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .chart-container {
    height: 350px;
  }
  
  .data-row {
    margin-bottom: 20px;
  }
  
  .data-card {
    margin-bottom: 20px;
  }
  
  .data-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .activity-avatar, .notice-avatar {
    background-color: #f0f2f5;
  }
  
  .activity-info, .notice-info {
    display: flex;
    flex-direction: column;
  }
  
  .activity-title, .notice-title {
    font-weight: bold;
    margin-bottom: 5px;
  }
  
  .activity-time, .notice-time {
    font-size: 12px;
    color: #909399;
  }
  </style>