<template>
  <div class="home-container">
    <HeaderNavbar />
    <el-container>
      <!-- 主体内容 -->
      <el-main class="main">
        <!-- 面包屑导航 -->
        <el-breadcrumb separator="/" class="breadcrumb">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item v-if="activeIndex !== 'home'">
            {{ breadcrumbMap[activeIndex] }}
          </el-breadcrumb-item>
        </el-breadcrumb>

        <el-row :gutter="20">
          <!-- 首页 -->
          <el-col :span="24" v-if="activeIndex === 'home'">
            <el-card class="content-card" shadow="hover">
              <h3>欢迎您，{{ userName }}！（{{ roleText }}）</h3>
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-card shadow="hover" class="card">
                    <template #header>
                      <div class="card-header">
                        <span style="font-size: 16px; font-weight: bold;">预约通知</span>
                        <el-button
                          type="primary"
                          style="float: right;"
                          @click="activeIndex = 'service'"
                        >
                          更多 +
                        </el-button>
                      </div>
                    </template>
                    <el-timeline>
                      <el-timeline-item timestamp="2025-02-25" placement="top">
                        您的健康体检预约已确认，请准时前往。
                      </el-timeline-item>
                      <el-timeline-item timestamp="2025-02-24" placement="top">
                        康复护理预约成功，工作人员将联系您。
                      </el-timeline-item>
                    </el-timeline>
                  </el-card>
                </el-col>
                <el-col :span="12">
                  <el-card shadow="hover" class="card">
                    <template #header>
                      <div class="card-header">
                        <span style="font-size: 16px; font-weight: bold;">社区活动</span>
                        <el-button
                          type="primary"
                          style="float: right;"
                          @click="activeIndex = 'activity'"
                        >
                          更多 +
                        </el-button>
                      </div>
                    </template>
                    <el-timeline>
                      <el-timeline-item
                        v-for="item in activityPreviews"
                        :key="item.date"
                        :timestamp="item.date"
                        placement="top"
                      >
                        {{ item.content }}
                      </el-timeline-item>
                    </el-timeline>
                  </el-card>
                </el-col>
                <el-col :span="12">
                  <el-card shadow="hover" class="card">
                    <template #header>
                      <div class="card-header">
                        <span style="font-size: 16px; font-weight: bold;">通知公告</span>
                        <el-button
                          type="primary"
                          style="float: right;"
                          @click="activeIndex = 'notice'"
                        >
                          更多 +
                        </el-button>
                      </div>
                    </template>
                    <el-timeline>
                      <el-timeline-item
                        v-for="item in noticePreviews"
                        :key="item.date"
                        :timestamp="item.date"
                        placement="top"
                      >
                        {{ item.content }}
                      </el-timeline-item>
                    </el-timeline>
                  </el-card>
                </el-col>
              </el-row>
            </el-card>
          </el-col>

          <!-- 修改密码 -->
          <el-col :span="24" v-if="activeIndex === 'changePassword'">
            <ChangePasswordForm />
          </el-col>

          <!-- 服务预约（需登录） -->
          <el-col :span="24" v-if="activeIndex === 'service'">
            <el-card class="content-card" shadow="hover" v-if="isAuthenticated">
              <h3>服务预约</h3>
              <el-tabs v-model="serviceTab">
                <el-tab-pane label="服务列表" name="list">
                  <el-table
                    :data="serviceData"
                    style="width: 100%"
                    @row-click="showServiceDetail"
                    class="clickable-table"
                  >
                    <el-table-column prop="date" label="日期" width="180" />
                    <el-table-column prop="service" label="服务项目" width="180" />
                    <el-table-column prop="status" label="状态" />
                    <el-table-column label="操作" width="120">
                      <template #default="scope">
                        <el-button
                          type="primary"
                          size="small"
                          :disabled="scope.row.status === '已预约'"
                          @click.stop="bookService(scope.row)"
                        >
                          预约
                        </el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                </el-tab-pane>
                <el-tab-pane label="预约通知" name="notification">
                  <el-timeline>
                    <el-timeline-item timestamp="2025-02-25" placement="top">
                      您的健康体检预约已确认，请准时前往。
                    </el-timeline-item>
                    <el-timeline-item timestamp="2025-02-24" placement="top">
                      康复护理预约成功，工作人员将联系您。
                    </el-timeline-item>
                  </el-timeline>
                </el-tab-pane>
              </el-tabs>
            </el-card>
            <el-card class="content-card" shadow="hover" v-else>
              <h3>服务预约</h3>
              <p>请登录后查看和管理服务预约。</p>
              <el-button type="primary" @click="router.push('/login')">去登录</el-button>
            </el-card>
            <el-dialog
              title="服务详情"
              v-model="dialogVisible"
              width="30%"
              :before-close="handleCloseDialog"
            >
              <el-descriptions :column="1" border>
                <el-descriptions-item label="服务项目">
                  {{ selectedService.service }}
                </el-descriptions-item>
                <el-descriptions-item label="日期">
                  {{ selectedService.date }}
                </el-descriptions-item>
                <el-descriptions-item label="状态">
                  {{ selectedService.status }}
                </el-descriptions-item>
                <el-descriptions-item label="描述">
                  {{ selectedService.description || '暂无详细描述' }}
                </el-descriptions-item>
              </el-descriptions>
              <template #footer>
                <el-button @click="dialogVisible = false">关闭</el-button>
              </template>
            </el-dialog>
          </el-col>

          <!-- 社区活动（浏览无需登录，操作需登录） -->
          <el-col :span="24" v-if="activeIndex === 'activity'">
            <el-card class="content-card" shadow="hover">
              <h3>社区活动</h3>
              <el-row :gutter="20">
                <el-col :span="8" v-for="item in activityData" :key="item.id">
                  <el-card shadow="hover">
                    <h4>{{ item.title }}</h4>
                    <p>时间：{{ item.time }}</p>
                    <p>状态：{{ item.status }}</p>
                    <el-button
                      type="success"
                      size="small"
                      @click="joinActivity(item)"
                      :disabled="!isAuthenticated || item.status === '已报名' || item.status === '已签到'"
                    >
                      {{ item.status === '未报名' ? '报名' : '签到' }}
                    </el-button>
                  </el-card>
                </el-col>
              </el-row>
              <p v-if="!isAuthenticated" class="login-prompt">
                请登录后进行报名或签到操作。
                <el-button type="text" @click="router.push('/login')">去登录</el-button>
              </p>
            </el-card>
          </el-col>

          <!-- 通知公告（无需登录） -->
          <el-col :span="24" v-if="activeIndex === 'notice'">
            <el-card class="content-card" shadow="hover">
              <h3>通知公告</h3>
              <el-timeline>
                <el-timeline-item timestamp="2025-02-23" placement="top">
                  社区活动：健康讲座将于本周六举行
                </el-timeline-item>
                <el-timeline-item timestamp="2025-02-20" placement="top">
                  系统维护通知：2月25日凌晨停机2小时
                </el-timeline-item>
              </el-timeline>
            </el-card>
          </el-col>

          <!-- 个人信息（需登录，优化后） -->
          <el-col :span="24" v-if="activeIndex === 'profile'">
            <el-card class="content-card" shadow="hover" v-if="isAuthenticated">
              <h3>我的个人中心</h3>
              <el-tabs v-model="profileTab" type="border-card" class="profile-tabs">
                <el-tab-pane label="基本信息" name="basic">
                  <el-descriptions :column="1" border v-if="general">
                    <el-descriptions-item label="姓名">{{ general.name }}</el-descriptions-item>
                    <el-descriptions-item label="性别">{{ general.gender }}</el-descriptions-item>
                    <el-descriptions-item label="年龄">{{ general.age }}</el-descriptions-item>
                    <el-descriptions-item label="联系方式">{{ general.contact }}</el-descriptions-item>
                    <el-descriptions-item label="地址">{{ general.address }}</el-descriptions-item>
                  </el-descriptions>
                  <p v-else class="loading-text">加载中...</p>
                  <el-button
                    type="primary"
                    style="margin-top: 20px;"
                    @click="openEditGeneralDialog"
                    :disabled="!general"
                  >
                    编辑基本信息
                  </el-button>
                </el-tab-pane>
                <el-tab-pane label="健康档案" name="health">
                  <el-descriptions :column="1" border v-if="health">
                    <el-descriptions-item label="身高">{{ health.height }} cm</el-descriptions-item>
                    <el-descriptions-item label="体重">{{ health.weight }} kg</el-descriptions-item>
                    <el-descriptions-item label="血压">{{ health.bloodPressure }} mmHg</el-descriptions-item>
                  </el-descriptions>
                  <p v-else class="loading-text">加载中...</p>
                  <el-button
                    type="primary"
                    style="margin-top: 20px;"
                    @click="openEditHealthDialog"
                    :disabled="!health"
                  >
                    编辑健康档案
                  </el-button>
                </el-tab-pane>
                <el-tab-pane label="服务记录" name="services">
                  <el-timeline>
                    <el-timeline-item
                      v-for="item in serviceHistory"
                      :key="item.date"
                      :timestamp="item.date"
                      placement="top"
                    >
                      {{ item.content }}
                    </el-timeline-item>
                  </el-timeline>
                  <el-button
                    type="primary"
                    style="margin-top: 20px;"
                    @click="viewAllServices"
                  >
                    查看完整服务记录
                  </el-button>
                </el-tab-pane>
                <el-tab-pane label="活动记录" name="activities">
                  <el-timeline>
                    <el-timeline-item
                      v-for="item in activityHistory"
                      :key="item.date"
                      :timestamp="item.date"
                      placement="top"
                    >
                      {{ item.content }}
                    </el-timeline-item>
                  </el-timeline>
                  <el-button
                    type="primary"
                    style="margin-top: 20px;"
                    @click="viewAllActivities"
                  >
                    查看完整活动记录
                  </el-button>
                </el-tab-pane>
              </el-tabs>
            </el-card>
            <el-card class="content-card" shadow="hover" v-else>
              <h3>我的个人中心</h3>
              <p>请登录后查看和管理个人信息。</p>
              <el-button type="primary" @click="router.push('/login')">去登录</el-button>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import HeaderNavbar from '@/components/HeaderNavbar.vue';
import ChangePasswordForm from '@/components/ChangePasswordForm.vue';
import { ElMessage } from 'element-plus';

const activeIndex = ref('home');
const userName = ref(localStorage.getItem('userName') || '用户');
const roleText = ref('老人');
const isAuthenticated = ref(!!localStorage.getItem('token'));
const serviceTab = ref('list');
const profileTab = ref('basic');
const dialogVisible = ref(false);
const selectedService = ref({});
const general = ref(null);
const health = ref(null);
const serviceHistory = ref([]);
const activityHistory = ref([]);
const activityPreviews = ref([]);
const noticePreviews = ref([]);
const router = useRouter();

const breadcrumbMap = {
  service: '服务预约',
  activity: '社区活动',
  notice: '通知公告',
  profile: '个人信息',
  changePassword: '修改密码'
};

const handleMenuSelect = (index) => {
  if (index === 'home' || index === 'service' || index === 'activity' || index === 'notice') {
    if (!isAuthenticated.value) {
      ElMessage.warning('请先登录');
    } else {
      activeIndex.value = index;
    }
  }
};

const handleCommand = (command) => {
  if (command === 'profile') {
    activeIndex.value = 'profile';
  } else if (command === 'changePassword') {
    activeIndex.value = 'changePassword';
  } else if (command === 'logout') {
    ElMessage.success('退出登录');
    localStorage.removeItem('token');
    localStorage.removeItem('userRole');
    localStorage.removeItem('userName');
    router.push('/login');
  }
};

const fetchUserData = async () => {
  try {
    const response = await axios.get('http://localhost:3000/userData');
    general.value = response.data.general;
    health.value = response.data.health;
  } catch (error) {
    ElMessage.error('请求失败: ' + (error.response?.data?.msg || error.message));
  }
};

const fetchHealthData = async () => {
  try {
    const response = await axios.get('http://localhost:3000/healthData');
    health.value = response.data;
  } catch (error) {
    ElMessage.error('请求失败: ' + (error.response?.data?.msg || error.message));
  }
};

const openEditGeneralDialog = () => {
  if (!general.value) {
    ElMessage.warning('请等待用户信息加载完成');
    return;
  }
  editFormData.value = { ...general.value };
  showEditGeneralDialog.value = true;
};

const openEditHealthDialog = () => {
  if (!health.value) {
    ElMessage.warning('请等待健康档案加载完成');
    return;
  }
  healthData.value = { ...health.value };
  showEditHealthDialog.value = true;
};

const handleAvatarUpload = (res) => {
  editFormData.value.avatar = res.data;
  ElMessage.success('头像上传成功');
};

const viewAllServices = () => {
  ElMessage.info('跳转到完整服务记录页面');
};

const viewAllActivities = () => {
  ElMessage.info('跳转到完整活动记录页面');
};

onMounted(() => {
  if (isAuthenticated.value) {
    fetchUserData();
    fetchHealthData();
  }
});
</script>

<style scoped>
.home-container {
  height: 100vh;
  background: url('@/assets/login.jpg') no-repeat center center;
  background-size: cover;
  display: flex;
  flex-direction: column;
}

.main {
  flex: 1;
  padding: 20px;
  background: rgba(245, 245, 245, 0.8);
  overflow-y: auto;
}

.breadcrumb {
  margin-bottom: 20px;
}

.content-card {
  margin-bottom: 20px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.95);
  padding: 20px;
}

.content-card h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 20px;
  font-weight: 600;
}

.content-card h4 {
  margin: 0 0 10px 0;
  color: #2c3e50;
}

.content-card p {
  margin: 8px 0;
  color: #606266;
}

.card {
  margin-bottom: 20px;
  min-height: 200px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-timeline {
  margin: 10px 0;
  padding-left: 0;
}

.el-button {
  margin: 5px 0;
}

.login-prompt {
  margin-top: 10px;
  color: #606266;
}

.login-prompt .el-button {
  padding: 0;
  margin-left: 5px;
}

.clickable-table :deep(.el-table__row) {
  cursor: pointer;
}

.clickable-table :deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}

.avatar {
  width: 100px;
  height: 100px;
  display: block;
  border-radius: 50%;
}

.avatar-uploader {
  display: inline-block;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
  cursor: pointer;
}

.avatar-uploader-icon:hover {
  border-color: #409eff;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.profile-tabs {
  margin-top: 20px;
}

.profile-tabs .el-tabs__content {
  padding: 20px;
}

.profile-tabs .el-descriptions {
  margin-bottom: 20px;
}

.loading-text {
  color: #606266;
  text-align: center;
  margin: 20px 0;
}
</style> 