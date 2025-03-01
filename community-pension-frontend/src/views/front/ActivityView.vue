<template>
    <div class="activity-view">
      <el-card class="content-card" shadow="hover">
        <h3>社区活动</h3>
        <el-row :gutter="20">
          <el-col :span="8" v-for="activity in activities" :key="activity.id">
            <el-card shadow="hover" class="activity-card">
              <h4>{{ activity.title }}</h4>
              <p class="time">
                <el-icon><Calendar /></el-icon>
                {{ activity.time }}
              </p>
              <p class="location">
                <el-icon><Location /></el-icon>
                {{ activity.location }}
              </p>
              <p class="description">{{ activity.description }}</p>
              <div class="actions">
                <el-tag :type="getStatusType(activity.status)">
                  {{ activity.status }}
                </el-tag>
                <el-button 
                  type="primary" 
                  size="small"
                  :disabled="activity.status !== '未报名'"
                  @click="handleJoin(activity)"
                >
                  {{ activity.status === '未报名' ? '报名' : '已报名' }}
                </el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-card>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue'
  import { Calendar, Location } from '@element-plus/icons-vue'
  import { ElMessage } from 'element-plus'
  
  const activities = ref([
    {
      id: 1,
      title: '健康讲座',
      time: '2025-02-28 14:00',
      location: '社区活动中心',
      description: '关于老年人健康生活的专业讲座',
      status: '未报名'
    },
    {
      id: 2,
      title: '太极班',
      time: '2025-03-01 09:00',
      location: '社区广场',
      description: '由专业教练指导的太极拳课程',
      status: '已报名'
    },
    {
      id: 3,
      title: '棋牌比赛',
      time: '2025-03-05 14:00',
      location: '棋牌室',
      description: '象棋、麻将等多种棋牌比赛',
      status: '未报名'
    }
  ])
  
  const getStatusType = (status) => {
    const types = {
      '未报名': 'info',
      '已报名': 'success',
      '已结束': 'danger'
    }
    return types[status] || 'default'
  }
  
  const handleJoin = (activity) => {
    activity.status = '已报名'
    ElMessage.success(`成功报名：${activity.title}`)
  }
  </script>
  
  <style scoped>
  .activity-view {
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
  
  .activity-card {
    margin-bottom: 20px;
    transition: all 0.3s ease;
  }
  
  .activity-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
  }
  
  h4 {
    margin: 0 0 12px 0;
    color: #2c3e50;
    font-size: 18px;
  }
  
  .time, .location {
    display: flex;
    align-items: center;
    gap: 8px;
    color: #666;
    margin: 8px 0;
  }
  
  .description {
    margin: 12px 0;
    color: #666;
    font-size: 14px;
  }
  
  .actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 16px;
  }
  </style>