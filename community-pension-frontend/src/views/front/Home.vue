<template>
  <div class="home-container">
    <el-container>
      <!-- Header -->
      <el-header class="header">
        <div class="header-content">
          <h1 class="logo">社区养老系统</h1>
          <el-menu :default-active="activeIndex" mode="horizontal" class="nav-menu" @select="handleMenuSelect">
            <el-menu-item v-for="(item, index) in menuItems" :key="item.index" :index="item.index">
              {{ item.label }}
            </el-menu-item>
          </el-menu>
          <el-dropdown @command="handleCommand" class="user-dropdown">
            <span class="user-info">
              <el-avatar :size="48" :src="userStore.elderInfo?.avatar || defaultAvatar" />
              <span class="username">{{ userStore.elderInfo.name || '访客' }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="changePassword">修改密码</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- Main Content -->
      <el-main class="main">
        <el-breadcrumb v-if="activeIndex !== 'home'" separator="/">
          <el-breadcrumb-item>首页</el-breadcrumb-item>
          <el-breadcrumb-item>{{ breadcrumbMap[activeIndex] }}</el-breadcrumb-item>
        </el-breadcrumb>

        <el-row :gutter="20">
          <!-- Dashboard -->
          <template v-if="activeIndex === 'home'">
            <el-col :span="24">
              <el-card class="welcome-card" shadow="hover">
                <h2 class="welcome-title">
                  <el-icon>
                    <User />
                  </el-icon>
                  欢迎您，{{ userStore.elderInfo.name || '访客' }}！
                </h2>

                <el-row :gutter="20" class="dashboard-grid">
                  <!-- Health Card -->
                  <el-col :span="12" :md="6">
                    <home-card title="健康监测" icon="FirstAidKit" class="health-card" @more="activeIndex = 'health'">
                      <div v-loading="healthLoading">
                        <div class="data-item">
                          <span class="label">血压</span>
                          <span class="value">{{ healthData.bloodPressure || '--' }}</span>
                        </div>
                        <div class="data-item">
                          <span class="label">心率</span>
                          <span class="value">{{ healthData.heartRate || '--' }} bpm</span>
                        </div>
                        <div class="data-item">
                          <span class="label">血糖</span>
                          <span class="value">{{ healthData.bloodSugar || '--' }} mmol/L</span>
                        </div>
                      </div>
                    </home-card>

                  </el-col>

                  <!-- Services Card -->
                  <el-col :span="12" :md="6">
                    <home-card title="服务预约" icon="Calendar" class="service-card" @more="activeIndex = 'service'">
                      <el-empty v-if="!services.length" description="暂无预约服务" />
                      <div v-else class="service-list">
                        <div v-for="service in services" :key="service.id" class="service-item">
                          <span>{{ service.name }}</span>
                          <el-tag :type="getServiceStatusType(service.status)">
                            {{ service.status }}
                          </el-tag>
                        </div>
                      </div>
                    </home-card>
                  </el-col>

                  <!-- Activities Card -->
                  <el-col :span="12" :md="6">
                    <home-card title="社区活动" icon="Flag" class="activity-card" @more="activeIndex = 'activity'">
                      <el-empty v-if="!activities.length" description="暂无活动" />
                      <div v-else class="activity-list">
                        <div v-for="activity in activities" :key="activity.id" class="activity-item">
                          <div class="activity-info">
                            <span class="activity-name">{{ activity.name }}</span>
                            <span class="activity-date">{{ activity.date }}</span>
                          </div>
                        </div>
                      </div>
                    </home-card>
                  </el-col>

                  <!-- Notices Card -->
                  <el-col :span="12" :md="6">
                    <home-card title="通知公告" icon="Bell" class="notice-card" @more="activeIndex = 'notice'">
                      <div class="notice-list">
                        <div v-for="notice in recentNotices" :key="notice.date" class="notice-item"
                          @click="viewNoticeDetail(notice)">
                          <el-tooltip :content="notice.title" placement="top" :show-after="1000">
                            <span class="notice-title">{{ notice.title }}</span>
                          </el-tooltip>
                          <span class="notice-date">{{ formatDate(notice.date) }}</span>
                        </div>
                      </div>
                    </home-card>
                  </el-col>
                  <!-- Notice Detail -->

                  <!-- Emergency Card -->
                  <el-col :span="12" :md="6">
                    <home-card title="紧急求助" icon="Warning" class="emergency-card" :show-more="false">
                      <div class="emergency-content">
                        <el-button type="danger" size="large" :icon="Phone" @click="handleEmergencyCall">
                          紧急呼叫
                        </el-button>
                        <div class="emergency-info">
                          <p>紧急联系人：{{ emergencyContact }}</p>
                          <p>联系电话：{{ emergencyPhone }}</p>
                        </div>
                      </div>
                    </home-card>
                  </el-col>

                </el-row>
              </el-card>
            </el-col>
          </template>

          <!-- 其他内容 -->
          <component :is="currentComponent" v-if="activeIndex !== 'home'" v-model:active-index="activeIndex" />
        </el-row>
      </el-main>

      <!-- Footer -->
      <el-footer class="footer">
        <div class="footer-content">
          <span>© 2025 社区养老系统</span>
          <el-link href="/admin" target="_blank" class="admin-link">后台管理</el-link>
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, defineAsyncComponent } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { useHealthData } from '@/composables/useHealthData'
import HomeCard from '@components/front/HomeCard.vue';
import dayjs from 'dayjs'
import {
  User,
  Phone
} from '@element-plus/icons-vue';


const selectedNotice = ref(null)
// 查看通知公告详情
const viewNoticeDetail = (notice) => {
  selectedNotice.value = notice;
  activeIndex.value = 'noticeDetail'
};
// 面包屑导航
const breadcrumbMap = {
  home: '首页',
  service: '服务预约',
  health: '健康档案',
  activity: '社区活动',
  notice: '通知公告',
  profile: '个人信息',
  changePassword: '修改密码',
  noticeDetail: '通知详情'
};
// Store and composables
const userStore = useUserStore()
const router = useRouter()
const { healthData, loading: healthLoading, fetchHealthData } = useHealthData()
onMounted(() => {
  if(!userStore.userInfo){
    router.push('/login')
  }else{

  }
})
// Constants
const defaultAvatar = localStorage.getItem('userInfo') ? `@/assets/avatar/${JSON.parse(localStorage.getItem('userInfo')).avatar}` : 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
const menuItems = [
  { index: 'home', label: '首页' },
  { index: 'service', label: '服务预约' },
  { index: 'health', label: '健康档案' },
  { index: 'activity', label: '社区活动' },
  { index: 'notice', label: '通知公告' }
]

// Reactive state
const activeIndex = ref('home')
const services = ref([
  { id: 1, name: '送餐服务', status: '已预约' },
  { id: 2, name: '康复护理', status: '待确认' }
])
const activities = ref([
  { id: 1, name: '健康讲座', date: '明天 14:00' },
  { id: 2, name: '棋牌比赛', date: '后天 15:00' }
])
const emergencyContact = ref('张医生')
const emergencyPhone = ref('120')
//通知公告预览
const noticePreviews = ref([
  { date: '2025-02-23', title: '社区活动：健康讲座将于本周六举行' },
  { date: '2025-02-20', title: '系统维护通知：2月25日凌晨停机2小时' },
  // 更多通知公告
]);
// get 4 recent notices
const recentNotices = computed(() => noticePreviews.value.slice(0, 4))
// get current component
const currentComponent = computed(() => {
  const componentMap = {
    service: defineAsyncComponent(() => import('@views/front/ServiceView.vue')),
    health: defineAsyncComponent(() => import('@views/front/HealthView.vue')),
    activity: defineAsyncComponent(() => import('@views/front/ActivityView.vue')),
    notice: defineAsyncComponent(() => import('@views/front/NoticeView.vue')),
    profile: defineAsyncComponent(() => import('@views/front/ProfileView.vue')),
    noticeDetail: defineAsyncComponent(() => import('@views/front/NoticeDetailView.vue'))
  }
  return componentMap[activeIndex.value]
})

// Methods
const handleMenuSelect = (index) => {
  activeIndex.value = index
}

const handleCommand = async (command) => {
  if (command === 'profile' || command === 'changePassword') {
    if (!userStore.isAuthenticated) {
      ElMessage.warning('请先登录')
      router.push('/login')
      return
    }
    activeIndex.value = command
  } else if (command === 'logout') {
    userStore.logout()
    ElMessage.success('退出登录成功')
    router.push('/login')
  }
}

const handleEmergencyCall = () => {
  ElMessage.success(`正在拨打紧急联系电话：${emergencyPhone.value}`)
  // 实际的紧急呼叫逻辑
}

const getServiceStatusType = (status) => {
  const statusMap = {
    '已预约': 'success',
    '待确认': 'warning',
    '已取消': 'info'
  }
  return statusMap[status] || 'info'
}

const formatDate = (date) => {
  return dayjs(date).format('MM-DD')
}

// Lifecycle
onMounted(async () => {
  if (userStore.isAuthenticated) {
    try {
      await userStore.getElderInfo
      await fetchHealthData(userStore.roleId)
    } catch (error) {
      console.error('初始化数据失败:', error)
    }
  }
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  display: flex;
  flex-direction: column;
}

.header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  padding: 0 10px;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin-left: 8px;
  color: #606266;
  font-size: 15px;
}

.avatar {
  border-radius: 50%;
  margin-right: 8px;
}

.dashboard-grid {
  margin-top: 20px;
}

.data-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding: 8px;
  background: rgba(0, 0, 0, 0.02);
  border-radius: 4px;
}

.data-item .label {
  color: #666;
}

.data-item .value {
  font-weight: 600;
  color: #2c3e50;
}

.service-list,
.activity-list,
.notice-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.service-item,
.activity-item,
.notice-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px;
  background: rgba(0, 0, 0, 0.02);
  border-radius: 4px;
  transition: all 0.3s ease;
}

.service-item:hover,
.activity-item:hover,
.notice-item:hover {
  background: rgba(0, 0, 0, 0.05);
}

.notice-title {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 8px;
}

.notice-date {
  color: #999;
  font-size: 0.9em;
}

.emergency-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.emergency-info {
  text-align: center;
  color: #666;
}

/* Card variations */
.health-card {
  border-left: 4px solid var(--el-color-success);
}

.service-card {
  border-left: 4px solid var(--el-color-primary);
}

.activity-card {
  border-left: 4px solid var(--el-color-warning);
}

.notice-card {
  border-left: 4px solid var(--el-color-info);
}

.emergency-card {
  border-left: 4px solid var(--el-color-danger);
}

.footer {
  background-color: #f5f5f5;
  padding: 10px 20px;
  text-align: center;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.admin-link {
  color: #409eff;
  font-weight: bold;
}

.admin-link:hover {
  color: #66b1ff;
}
</style>