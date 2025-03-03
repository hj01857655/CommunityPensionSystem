<template>
  <div class="home-container">
    <el-container>
      <el-header class="header">
        <div class="header-content">
          <h1 class="logo">社区养老系统</h1>
          <el-menu
            :default-active="activeIndex"
            mode="horizontal"
            class="nav-menu"
            @select="handleMenuSelect"
          >
            <el-menu-item index="home">首页</el-menu-item>
            <el-menu-item index="service">服务预约</el-menu-item>
            <el-menu-item index="health">健康档案</el-menu-item>
            <el-menu-item index="activity">社区活动</el-menu-item>
            <el-menu-item index="notice">通知公告</el-menu-item>
          </el-menu>
          <el-dropdown @command="handleCommand" class="user-dropdown">
            <span class="user-info">
              <el-avatar :size="48" :src="userAvatar" />
              <span class="username">{{ userName }}</span>
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

      <el-main class="main">
        <el-row :gutter="20">
          <el-col :span="24" v-if="activeIndex === 'home'">
            <el-card class="content-card welcome-card" shadow="hover">
              <h3 class="welcome-title">
                <i class="el-icon-user"></i> 欢迎您，{{ userName }}！（{{ roleText }}）
              </h3>
              <el-row :gutter="20" class="preview-grid">
                <el-col :span="12" :md="6">
                  <el-card shadow="hover" class="preview-card health-card">
                    <template #header>
                      <div class="card-header">
                        <span><i class="el-icon-first-aid-kit"></i> 健康监测</span>
                        <el-button type="text" class="more-btn" @click="activeIndex = 'health'">
                          更多 <i class="el-icon-arrow-right"></i>
                        </el-button>
                      </div>
                    </template>
                    <p>血压：{{ healthData.bloodPressure }}</p>
                    <p>心率：{{ healthData.heartRate }}</p>
                    <p>血糖：{{ healthData.bloodSugar }}</p>
                  </el-card>
                </el-col>
                <el-col :span="12" :md="6">
                  <el-card shadow="hover" class="preview-card service-card">
                    <template #header>
                      <div class="card-header">
                        <span><i class="el-icon-date"></i> 服务预约</span>
                        <el-button type="text" class="more-btn" @click="activeIndex = 'service'">
                          更多 <i class="el-icon-arrow-right"></i>
                        </el-button>
                      </div>
                    </template>
                    <p v-for="service in services" :key="service.id">{{ service.name }}：{{ service.status }}</p>
                    <el-button type="primary" @click="activeIndex = 'service'">预约服务</el-button>
                  </el-card>
                </el-col>
                <el-col :span="12" :md="6">
                  <el-card shadow="hover" class="preview-card activity-card">
                    <template #header>
                      <div class="card-header">
                        <span><i class="el-icon-s-flag"></i> 社区活动</span>
                        <el-button type="text" class="more-btn" @click="activeIndex = 'activity'">
                          更多 <i class="el-icon-arrow-right"></i>
                        </el-button>
                      </div>
                    </template>
                    <p v-for="activity in activities" :key="activity.id">{{ activity.name }}：{{ activity.date }}</p>
                    <el-button type="primary" @click="activeIndex = 'activity'">社区活动</el-button>
                  </el-card>
                </el-col>
                <el-col :span="12" :md="6">
                  <el-card shadow="hover" class="preview-card notice-card">
                    <template #header>
                      <div class="card-header">
                        <span><i class="el-icon-bell"></i> 紧急求助</span>
                      </div>
                    </template>
                    <el-button type="danger" size="large" @click="emergencyCall">紧急呼叫</el-button>
                    <p>紧急联系人：{{ emergencyContact }}</p>
                  </el-card>
                </el-col>
              </el-row>
              <el-card class="content-card notice-card">
                <h3>通知公告</h3>
                <el-timeline>
                  <el-timeline-item
                    v-for="item in noticePreviews"
                    :key="item.date"
                    :timestamp="item.date"
                    placement="top"
                    size="small"
                  >
                    {{ item.content }}
                  </el-timeline-item>
                </el-timeline>
              </el-card>
            </el-card>
          </el-col>

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
            <el-dialog title="服务详情" v-model="dialogVisible" width="30%" :before-close="handleCloseDialog">
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

          <el-col :span="24" v-if="activeIndex === 'health'">
            <el-card class="content-card" shadow="hover" v-if="isAuthenticated">
              <h3>健康档案</h3>
              <el-form :model="healthForm" :rules="healthRules" ref="healthFormRef" label-width="100px">
                <el-form-item label="身高" prop="height">
                  <el-input v-model="healthForm.height" placeholder="请输入身高 (cm)" />
                </el-form-item>
                <el-form-item label="体重" prop="weight">
                  <el-input v-model="healthForm.weight" placeholder="请输入体重 (kg)" />
                </el-form-item>
                <el-form-item label="血压" prop="bloodPressure">
                  <el-input v-model="healthForm.bloodPressure" placeholder="请输入血压 (mmHg)" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="saveHealth">保存</el-button>
                  <el-button @click="resetHealth">重置</el-button>
                </el-form-item>
              </el-form>
            </el-card>
            <el-card class="content-card" shadow="hover" v-else>
              <h3>健康档案</h3>
              <p>请登录后查看和管理健康档案。</p>
              <el-button type="primary" @click="router.push('/login')">去登录</el-button>
            </el-card>
          </el-col>

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

          <el-col :span="24" v-if="activeIndex === 'profile'">
            <el-card class="content-card" shadow="hover" v-if="isAuthenticated">
              <h3>个人信息</h3>
              <el-divider><span>个人档案</span></el-divider>
              <el-descriptions v-if="general" :column="2">
                <el-descriptions-item label="姓名">{{ general.name }}</el-descriptions-item>
                <el-descriptions-item label="账号">{{ general.account }}</el-descriptions-item>
                <el-descriptions-item label="头像">
                  <img :src="general.avatar" alt="头像" class="avatar" />
                </el-descriptions-item>
                <el-descriptions-item label="年龄">{{ general.age }}</el-descriptions-item>
                <el-descriptions-item label="性别">{{ general.sex === 1 ? '男' : '女' }}</el-descriptions-item>
                <el-descriptions-item label="电话">{{ general.phone }}</el-descriptions-item>
                <el-descriptions-item label="地址">{{ general.address }}</el-descriptions-item>
                <el-descriptions-item label="紧急联系人电话">{{ general.contacts }}</el-descriptions-item>
                <el-descriptions-item label="注册时间">{{ formatDateTime(general.ctime) }}</el-descriptions-item>
                <el-descriptions-item label="更新时间">{{ formatDateTime(general.utime) }}</el-descriptions-item>
              </el-descriptions>
              <el-divider><span>健康档案</span></el-divider>
              <el-descriptions v-if="health" :column="2">
                <el-descriptions-item label="健康档案ID">{{ health.id }}</el-descriptions-item>
                <el-descriptions-item label="健康信息">{{ health.health }}</el-descriptions-item>
                <el-descriptions-item label="病史详情">{{ health.medical }}</el-descriptions-item>
                <el-descriptions-item label="出生日期">{{ formatDateTime(health.dob) }}</el-descriptions-item>
              </el-descriptions>
              <div class="button-group">
                <el-button type="primary" @click="openEditGeneralDialog">编辑个人信息</el-button>
                <el-button type="primary" @click="openEditHealthDialog">编辑健康档案</el-button>
              </div>
            </el-card>
            <el-card class="content-card" shadow="hover" v-else>
              <h3>个人信息</h3>
              <p>请登录后查看和编辑个人信息。</p>
              <el-button type="primary" @click="router.push('/login')">去登录</el-button>
            </el-card>

            <el-dialog v-model="showEditGeneralDialog" title="编辑用户信息" width="30%">
              <el-form :model="editFormData">
                <el-form-item label="用户ID">
                  <el-input v-model="editFormData.id" disabled />
                </el-form-item>
                <el-form-item label="姓名">
                  <el-input v-model="editFormData.name" />
                </el-form-item>
                <el-form-item label="头像" prop="avatar">
                  <el-upload
                    class="avatar-uploader"
                    action="http://localhost:3000/file"
                    :show-file-list="false"
                    :on-success="success"
                  >
                    <img v-if="editFormData.avatar" :src="editFormData.avatar" class="avatar" />
                    <i v-else class="el-icon-plus avatar-uploader-icon" />
                  </el-upload>
                </el-form-item>
                <el-form-item label="年龄">
                  <el-input v-model="editFormData.age" type="number" />
                </el-form-item>
                <el-form-item label="性别">
                  <el-radio-group v-model="editFormData.sex">
                    <el-radio label="1">男</el-radio>
                    <el-radio label="0">女</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="地址">
                  <el-input v-model="editFormData.address" />
                </el-form-item>
                <el-form-item label="电话号码">
                  <el-input v-model="editFormData.phone" />
                </el-form-item>
                <el-form-item label="紧急联系人">
                  <el-input v-model="editFormData.contacts" />
                </el-form-item>
              </el-form>
              <template #footer>
                <span class="dialog-footer">
                  <el-button @click="showEditGeneralDialog = false">取消</el-button>
                  <el-button type="primary" @click="submitForm">保存</el-button>
                </span>
              </template>
            </el-dialog>

            <el-dialog v-model="showEditHealthDialog" title="编辑健康档案信息" width="30%">
              <el-form :model="healthData">
                <el-form-item label="出生日期">
                  <el-date-picker v-model="healthData.dob" type="date" placeholder="选择日期" />
                </el-form-item>
                <el-form-item label="健康信息">
                  <el-input v-model="healthData.health" />
                </el-form-item>
                <el-form-item label="病史详情">
                  <el-input v-model="healthData.medical" />
                </el-form-item>
              </el-form>
              <template #footer>
                <span class="dialog-footer">
                  <el-button @click="showEditHealthDialog = false">取消</el-button>
                  <el-button type="primary" @click="submitHealthData">保存</el-button>
                </span>
              </template>
            </el-dialog>

          </el-col>

          <el-col :span="24" v-if="activeIndex === 'changePassword'">
            <el-card class="content-card" shadow="hover">
              <h3>修改密码</h3>
              <el-form class="change-password-form">
                <el-form-item label="旧密码">
                  <el-input v-model="oldPassword" type="password"></el-input>
                </el-form-item>
                <el-form-item label="新密码">
                  <el-input v-model="newPassword" type="password"></el-input>
                </el-form-item>
                <el-form-item label="确认新密码">
                  <el-input v-model="confirmPassword" type="password"></el-input>
                </el-form-item>
                <el-form-item>
                  <div class="button-group">
                    <el-button type="primary" @click="changePassword">确认</el-button>
                    <el-button @click="activeIndex = 'home'">取消</el-button>
                  </div>
                </el-form-item>
              </el-form>
            </el-card>
          </el-col>

          <el-col :span="24" v-if="activeIndex === 'notice' && !selectedNotice.content">
            <el-card class="content-card" shadow="hover">
              <h3>通知公告</h3>
              <el-row>
                <el-col :span="24" v-for="(item, index) in paginatedNotices" :key="index" class="notice-item" @click="viewNoticeDetail(item)">
                  <span class="notice-title">{{ item.content }}</span>
                  <span class="notice-date">{{ item.date }}</span>
                </el-col>
              </el-row>
              <div class="button-group">
                <el-button type="text" @click="showMoreNotices">更多</el-button>
              </div>
            </el-card>
          </el-col>

          <el-col :span="24" v-if="activeIndex === 'notice' && selectedNotice.content">
            <el-card class="content-card" shadow="hover">
              <h3>通知详情</h3>
              <p>{{ selectedNotice.content }}</p>
              <el-button type="text" @click="selectedNotice = {}">返回</el-button>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import axios from 'axios';
import dayjs from 'dayjs';

const userName = ref(localStorage.getItem('userName') || '访客');
const userAvatar = ref('https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png');
const userRole = ref(localStorage.getItem('userRole') || 'guest');
const roleText = ref(computeRoleText(userRole.value));
const isAuthenticated = ref(!!localStorage.getItem('token'));
const router = useRouter();

const activeIndex = ref('home');

const breadcrumbMap = {
  home: '首页',
  service: '服务预约',
  health: '健康档案',
  activity: '社区活动',
  notice: '通知公告',
  profile: '个人信息',
};

const serviceTab = ref('list');
const serviceData = ref([
  { date: '2025-02-25', service: '健康体检', status: '未预约', description: '全面体检，包括血压、血糖等项目' },
  { date: '2025-02-26', service: '康复护理', status: '已预约', description: '专业护理，帮助恢复身体机能' },
]);
const dialogVisible = ref(false);
const selectedService = ref({});

const healthForm = ref({
  height: '165',
  weight: '60',
  bloodPressure: '120/80',
});
const healthFormRef = ref();
const healthRules = ref({
  height: [{ required: true, message: '请输入身高', trigger: 'blur' }],
  weight: [{ required: true, message: '请输入体重', trigger: 'blur' }],
  bloodPressure: [{ required: true, message: '请输入血压', trigger: 'blur' }],
});

const activityData = ref([
  { id: 1, title: '健康讲座', time: '2025-02-28 14:00', status: '未报名' },
  { id: 2, title: '文艺汇演', time: '2025-03-01 15:00', status: '已报名' },
  { id: 3, title: '户外踏青', time: '2025-03-05 09:00', status: '已签到' },
]);

const general = ref(null);
const editFormData = ref({});
const health = ref(null);
const showEditGeneralDialog = ref(false);
const showEditHealthDialog = ref(false);


const servicePreviews = ref([
  { date: '2025-02-25', content: '健康体检服务预约已确认，请准时前往社区中心' },
  { date: '2025-02-26', content: '康复护理服务预约成功，工作人员将联系您' },
]);

const healthPreviews = ref([
  { date: '2025-02-24', content: '您的健康档案更新：身高165cm，体重60kg' },
  { date: '2025-02-23', content: '血压记录：120/80 mmHg，健康状况正常' },
]);

const activityPreviews = ref([
  { date: '2025-02-28', content: '健康讲座活动报名开始，时间：14:00' },
  { date: '2025-03-01', content: '文艺汇演活动已报名，请准时参加' },
]);

const noticePreviews = ref([
  { date: '2025-02-23', content: '社区活动：健康讲座将于本周六举行' },
  { date: '2025-02-20', content: '系统维护通知：2月25日凌晨停机2小时' },
]);

const healthData = ref({ bloodPressure: '120/80', heartRate: '75', bloodSugar: '5.5' });
const services = ref([{ id: 1, name: '送餐', status: '已预约' }, { id: 2, name: '护理', status: '待确认' }]);
const activities = ref([{ id: 1, name: '健康讲座', date: '明天' }, { id: 2, name: '棋牌比赛', date: '后天' }]);
const emergencyContact = ref('110');
const weather = ref('晴，25°C');
const notice = ref('明天上午9点有健康讲座');

const oldPassword = ref('');
const newPassword = ref('');
const confirmPassword = ref('');

const notices = ref([
  { date: '2025-02-23', content: '社区活动：健康讲座将于本周六举行' },
  { date: '2025-02-20', content: '系统维护通知：2月25日凌晨停机2小时' },
  // 模拟更多通知公告
  { date: '2025-02-19', content: '新冠疫苗接种通知' },
  { date: '2025-02-18', content: '社区清洁活动' },
  { date: '2025-02-17', content: '春节联欢晚会报名开始' },
  { date: '2025-02-16', content: '社区义诊活动' },
  { date: '2025-02-15', content: '消防演习通知' },
  { date: '2025-02-14', content: '社区安全讲座' },
  { date: '2025-02-13', content: '社区运动会报名' },
  { date: '2025-02-12', content: '社区图书馆开放时间调整' },
]);

const paginatedNotices = ref(notices.value.slice(0, 5));
const selectedNotice = ref({});

function computeRoleText(role) {
  const roleMap = {
    elder: '老人',
    family: '老人家属',
    staff: '社区工作人员',
    admin: '管理员',
    guest: '访客',
  };
  return roleMap[role] || '未知角色';
}

const handleMenuSelect = (index) => {
  activeIndex.value = index;
};

const handleCommand = (command) => {
  if (command === 'profile') {
    if (!isAuthenticated.value) {
      ElMessage.warning('请先登录');
      router.push('/login');
    } else {
      activeIndex.value = 'profile';
    }
  } else if (command === 'changePassword') {
    activeIndex.value = 'changePassword';
  } else if (command === 'logout') {
    ElMessage.success('退出登录');
    localStorage.removeItem('token');
    localStorage.removeItem('userRole');
    localStorage.removeItem('userName');
    localStorage.removeItem('userId');
    localStorage.removeItem('rememberedPassword');
    isAuthenticated.value = false;
    userName.value = '访客';
    userRole.value = 'guest';
    roleText.value = computeRoleText('guest');
    router.push('/login');
  }
};

const bookService = (row) => {
  row.status = '已预约';
  ElMessage.success(`${row.service} 预约成功`);
};

const showServiceDetail = (row) => {
  selectedService.value = { ...row };
  dialogVisible.value = true;
};

const handleCloseDialog = () => {
  dialogVisible.value = false;
};

const saveHealth = () => {
  healthFormRef.value.validate((valid) => {
    if (valid) {
      ElMessage.success('健康档案保存成功');
    }
  });
};
const resetHealth = () => {
  healthFormRef.value.resetFields();
};

const joinActivity = (item) => {
  if (!isAuthenticated.value) {
    ElMessage.warning('请先登录');
    router.push('/login');
    return;
  }
  if (item.status === '未报名') {
    item.status = '已报名';
    ElMessage.success(`${item.title} 报名成功`);
  } else if (item.status === '已报名') {
    item.status = '已签到';
    ElMessage.success(`${item.title} 签到成功`);
  }
};

const formatDateTime = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm:ss') : '';
};

const formatDOB = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD') : '';
};

const fetchUserData = async () => {
  const id = localStorage.getItem('userId');
  try {
    const res = await axios.get('http://localhost:3000/general/id', { params: { id } });
    general.value = res.data.data;
    userName.value = general.value.name || userName.value;
  } catch (error) {
    ElMessage.error('获取用户信息失败');
  }
};

const fetchHealthData = async () => {
  const generalId = localStorage.getItem('userId');
  try {
    const res = await axios.get('http://localhost:3000/getHealth', { params: { generalId } });
    health.value = res.data.data;
  } catch (error) {
    ElMessage.error('获取健康档案失败');
  }
};

const submitForm = async () => {
  try {
    const response = await axios.post('http://localhost:3000/generalUpdate', editFormData.value);
    if (response.data.code === 1) {
      ElMessage.success(response.data.msg || '保存成功');
      await fetchUserData();
      showEditGeneralDialog.value = false;
    } else {
      ElMessage.error(response.data.msg || '保存失败');
    }
  } catch (error) {
    ElMessage.error('请求失败: ' + (error.response?.data?.msg || error.message));
  }
};

const submitHealthData = async () => {
  const userId = localStorage.getItem('userId');
  const data = { ...healthData.value, generalId: userId, dob: formatDOB(healthData.value.dob) };
  try {
    const response = await axios.post('http://localhost:3000/updateHealth', data);
    if (response.data.code === 1) {
      ElMessage.success(response.data.msg || '健康档案更新成功');
      await fetchHealthData();
      showEditHealthDialog.value = false;
    } else {
      ElMessage.error(response.data.msg || '更新失败');
    }
  } catch (error) {
    ElMessage.error('请求失败: ' + (error.response?.data?.msg || error.message));
  }
};

const openEditGeneralDialog = () => {
  editFormData.value = { ...general.value };
  showEditGeneralDialog.value = true;
};

const openEditHealthDialog = () => {
  healthData.value = { ...health.value };
  showEditHealthDialog.value = true;
};

const success = (res) => {
  editFormData.value.avatar = res.data;
};

const emergencyCall = () => {
  //实现紧急呼叫逻辑
  ElMessage.success('紧急呼叫');
};

const changePassword = () => {
  if (newPassword.value !== confirmPassword.value) {
    ElMessage.error('新密码和确认密码不一致');
    return;
  }
  // 调用API进行密码修改
  ElMessage.success('密码修改成功');
  activeIndex.value = 'home';
};

const showMoreNotices = () => {
  paginatedNotices.value = notices.value;
};

const viewNoticeDetail = (notice) => {
  selectedNotice.value = notice;
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
  background: url('@/assets/login.png') no-repeat center center;
  background-size: cover;
  display: flex;
  flex-direction: column;
}

.header {
  background: rgba(255, 255, 255, 0.95);
  padding: 0;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(5px);
  height: 60px;
  line-height: 60px;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  padding: 0 20px;
}

.logo {
  font-size: 22px;
  color: #2c3e50;
  font-weight: 600;
  margin: 0;
}

.nav-menu {
  flex: 1;
  margin-left: 40px;
  background: transparent;
  border-bottom: none;
}

.nav-menu .el-menu-item {
  font-size: 16px;
  color: #606266;
}

.nav-menu .el-menu-item:hover,
.nav-menu .el-menu-item.is-active {
  color: #409eff;
  border-bottom: 2px solid #409eff;
}

.user-dropdown {
  margin-left: 20px;
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

.change-password-form {
  max-width: 400px;
  margin: 0 auto;
}

.welcome-card {
  background: rgba(255, 255, 255, 0.98);
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.welcome-title {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 24px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.preview-grid {
  display: flex;
  flex-wrap: wrap;
}

.preview-card {
  min-height: 220px;
  border-radius: 8px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.preview-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
}

.service-card {
  border-left: 4px solid #409eff;
}

.health-card {
  border-left: 4px solid #67c23a;
}

.activity-card {
  border-left: 4px solid #e6a23c;
}

.notice-card {
  border-left: 4px solid #f56c6c;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: bold;
  color: #2c3e50;
}

.card-header span {
  display: flex;
  align-items: center;
  gap: 6px;
}

.more-btn {
  padding: 0;
  font-size: 14px;
  color: #409eff;
}

.more-btn:hover {
  color: #66b1ff;
}

.el-timeline {
  margin: 10px 0;
  padding-left: 0;
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

.button-group {
  display: flex;
  justify-content: center;
  gap: 10px;
}

.avatar {
  width: 100px;
  height: 100px;
  display: block;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.notice-item {
  display: flex;
  justify-content: space-between;
  padding: 10px;
  border-bottom: 1px solid #e0e0e0;
  cursor: pointer;
}

.notice-item:hover {
  background-color: #f5f5f5;
}

.notice-title {
  flex: 1;
  color: #333;
}

.notice-date {
  color: #999;
}
</style>