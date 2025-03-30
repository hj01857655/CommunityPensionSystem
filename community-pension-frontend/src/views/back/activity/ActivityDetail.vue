<template>
  <div class="activity-detail">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>活动详情</span>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="活动标题">{{ activity.title }}</el-descriptions-item>
        <el-descriptions-item label="活动类型">{{ getActivityTypeName(activity.type) }}</el-descriptions-item>
        <el-descriptions-item label="活动状态">
          <el-tag :type="getStatusType(activity.status)">
            {{ getStatusText(activity.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="活动时间">
          {{ activity.startTime }} 至 {{ activity.endTime }}
        </el-descriptions-item>
        <el-descriptions-item label="活动地点">{{ activity.location }}</el-descriptions-item>
        <el-descriptions-item label="参与人数">
          {{ activity.currentParticipants }}/{{ activity.maxParticipants }}
        </el-descriptions-item>
        <el-descriptions-item label="活动描述" :span="2">
          {{ activity.description }}
        </el-descriptions-item>
      </el-descriptions>

      <!-- 活动统计 -->
      <div class="stats-section">
        <h3>活动统计</h3>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card shadow="hover">
              <template #header>
                <div class="stats-header">
                  <span>报名人数</span>
                  <el-icon><User /></el-icon>
                </div>
              </template>
              <div class="stats-value">{{ stats.totalParticipants || 0 }}</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover">
              <template #header>
                <div class="stats-header">
                  <span>签到人数</span>
                  <el-icon><Check /></el-icon>
                </div>
              </template>
              <div class="stats-value">{{ stats.totalCheckins || 0 }}</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover">
              <template #header>
                <div class="stats-header">
                  <span>请假人数</span>
                  <el-icon><Close /></el-icon>
                </div>
              </template>
              <div class="stats-value">{{ stats.totalAbsences || 0 }}</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover">
              <template #header>
                <div class="stats-header">
                  <span>参与率</span>
                  <el-icon><DataLine /></el-icon>
                </div>
              </template>
              <div class="stats-value">{{ stats.participationRate || '0%' }}</div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 参与记录 -->
      <div class="participants-section">
        <h3>参与记录</h3>
        <el-table :data="participants" border style="width: 100%">
          <el-table-column prop="name" label="姓名" width="120" />
          <el-table-column prop="phone" label="联系电话" width="150" />
          <el-table-column prop="registrationTime" label="报名时间" width="180" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getParticipantStatusType(row.status)">
                {{ getParticipantStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="checkinTime" label="签到时间" width="180" />
          <el-table-column prop="remark" label="备注" />
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useActivityStore } from '@/stores/back/activityStore'
import { User, Check, Close, DataLine } from '@element-plus/icons-vue'

const route = useRoute()
const activityStore = useActivityStore()
const activity = ref({})
const stats = ref({})
const participants = ref([])

// 获取活动类型名称
const getActivityTypeName = (type) => {
  const typeMap = {
    '1': '文化娱乐',
    '2': '健康讲座',
    '3': '志愿服务',
    '4': '节日庆祝',
    '5': '其他活动'
  }
  return typeMap[type] || '未知类型'
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    '0': 'info',
    '1': 'success',
    '2': 'warning',
    '3': 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    '0': '未开始',
    '1': '进行中',
    '2': '已结束',
    '3': '已取消'
  }
  return textMap[status] || '未知状态'
}

// 获取参与状态类型
const getParticipantStatusType = (status) => {
  const typeMap = {
    '0': 'info',    // 已报名
    '1': 'success', // 已签到
    '2': 'warning', // 已请假
    '3': 'danger'   // 已取消
  }
  return typeMap[status] || 'info'
}

// 获取参与状态文本
const getParticipantStatusText = (status) => {
  const textMap = {
    '0': '已报名',
    '1': '已签到',
    '2': '已请假',
    '3': '已取消'
  }
  return textMap[status] || '未知状态'
}

// 获取活动详情
const fetchActivityDetail = async () => {
  const id = route.params.id
  if (!id) return
  
  try {
    const data = await activityStore.fetchActivityDetail(id)
    activity.value = data
    
    // 获取活动统计
    const statsData = await activityStore.fetchActivityStats(id)
    stats.value = statsData
    
    // TODO: 获取参与记录
    // participants.value = await getActivityParticipants(id)
  } catch (error) {
    console.error('获取活动详情失败:', error)
  }
}

onMounted(() => {
  fetchActivityDetail()
})
</script>

<style scoped>
.activity-detail {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stats-section {
  margin-top: 30px;
}

.stats-section h3,
.participants-section h3 {
  margin-bottom: 20px;
}

.stats-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stats-value {
  font-size: 24px;
  font-weight: bold;
  text-align: center;
  color: #409EFF;
}

.participants-section {
  margin-top: 30px;
}
</style> 