<template>
  <div class="dashboard">
    <!-- User Activity Analysis -->
    <el-card shadow="hover" class="chart-card">
      <template #header>
        <div class="chart-header">
          <span>用户活跃度分析</span>
        </div>
      </template>
      <div ref="userActivityChartRef" class="chart-container"></div>
    </el-card>

    <!-- Service Appointment Statistics -->
    <el-card shadow="hover" class="chart-card">
      <template #header>
        <div class="chart-header">
          <span>服务预约统计</span>
        </div>
      </template>
      <div ref="serviceAppointmentChartRef" class="chart-container"></div>
    </el-card>

    <!-- Health Data Trend Analysis -->
    <el-card shadow="hover" class="chart-card">
      <template #header>
        <div class="chart-header">
          <span>健康数据趋势分析</span>
        </div>
      </template>
      <div ref="healthDataChartRef" class="chart-container"></div>
    </el-card>

    <!-- Activity Participation Analysis -->
    <el-card shadow="hover" class="chart-card">
      <template #header>
        <div class="chart-header">
          <span>活动参与情况分析</span>
        </div>
      </template>
      <div ref="activityParticipationChartRef" class="chart-container"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';

// Chart references
const userActivityChartRef = ref(null);
const serviceAppointmentChartRef = ref(null);
const healthDataChartRef = ref(null);
const activityParticipationChartRef = ref(null);

onMounted(() => {
  // Initialize charts
  initUserActivityChart();
  initServiceAppointmentChart();
  initHealthDataChart();
  initActivityParticipationChart();
});

const initUserActivityChart = () => {
  if (!userActivityChartRef.value) return;
  const chart = echarts.init(userActivityChartRef.value);
  const option = {
    title: {
      text: '用户活跃度分析',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },    
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '活跃度',
        type: 'line',
        data: [120, 132, 101, 134, 90, 230, 210],
        smooth: true
      }
    ]
  };
  chart.setOption(option);
};
const initServiceAppointmentChart = () => {
  if (!serviceAppointmentChartRef.value) return;
  const chart = echarts.init(serviceAppointmentChartRef.value);
  const option = {
    title: {
      text: '服务预约统计',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '预约数量',
        type: 'line',
        data: [120, 132, 101, 134, 90, 230, 210],
        smooth: true
      }
    ]
  };
  chart.setOption(option);
};
const initHealthDataChart = () => {
  if (!healthDataChartRef.value) return;
  const chart = echarts.init(healthDataChartRef.value);
  const option = {
    title: {
      text: '健康数据趋势分析',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value' 
    },
    series: [
      {
        name: '健康数据',
        type: 'line',
        data: [120, 132, 101, 134, 90, 230, 210],
        smooth: true
      }
    ]
  };
  chart.setOption(option);
};
const initActivityParticipationChart = () => {
  if (!activityParticipationChartRef.value) return;
  const chart = echarts.init(activityParticipationChartRef.value);
  const option = {
    title: {
      text: '活动参与情况分析',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },    
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '参与人数',
        type: 'line',
        data: [120, 132, 101, 134, 90, 230, 210],
        smooth: true
      }
    ]
  };
  chart.setOption(option);
};

</script>

<style scoped>
.dashboard {
  padding: 10px;
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
</style>