<template>
    <div class="dashboard">
        <el-card class="content-card" shadow="hover">
            <h1>{{ dashboard.title }}</h1>
            <p>{{ dashboard.description }}</p>
            <el-row :gutter="20" class="dashboard-grid">
                <!-- Health Card -->
                <el-col :span="12" :md="6">
                    <home-card title="健康监测" icon="FirstAidKit" class="health-card" :moreRouteName="'HealthView'" @more="handleMenuSelect">
                        <div v-if="isLoggedIn">
                            <div v-if="healthData">
                                <div class="data-item" v-for="(value, key) in displayHealthData" :key="key">
                                    <span class="label">{{ healthLabels[key] }}</span>
                                    <span class="value">{{ formatHealthValue(key, value) }}</span>
                                </div>
                                <div class="update-time">
                                    更新时间: {{ formatDateTime(healthData.recordTime) }}
                                </div>
                            </div>
                            <div v-else>
                                <el-empty description="暂无健康数据" />
                            </div>
                        </div>
                        <div v-else>
                            <el-empty description="请登录以查看健康监测数据" />
                        </div>
                    </home-card>
                </el-col>

                <!-- Services Card -->
                <el-col :span="12" :md="6">
                    <home-card title="服务预约" icon="Calendar" class="service-card" :moreRouteName="'ServiceView'" @more="handleMenuSelect">
                        <div v-if="isLoggedIn">
                            <el-empty v-if="!services.length" description="暂无预约服务" />
                            <div v-else class="service-list">
                                <div v-for="service in services" :key="service.id" class="service-item">
                                    <span>{{ service.name }}</span>
                                    <el-tag :type="getServiceStatusType(service.status)">
                                        {{ service.status }}
                                    </el-tag>
                                </div>
                            </div>
                        </div>
                        <div v-else>
                            <el-empty description="请登录以查看服务预约" />
                        </div>
                    </home-card>
                </el-col>

                <!-- Activities Card -->
                <el-col :span="12" :md="6">
                    <home-card title="社区活动" icon="Flag" class="activity-card" :moreRouteName="'ActivityView'" @more="handleMenuSelect">
                        <el-empty v-if="!activities.length" description="暂无活动" />
                        <div v-else class="activity-list">
                            <div v-for="activity in activities" :key="activity.id" class="activity-item">
                                <div class="activity-info">
                                    <span class="activity-name">{{ activity.name }}</span>
                                    <span class="activity-date">{{ formatDate(activity.date) }}</span>
                                </div>
                            </div>
                        </div>
                    </home-card>
                </el-col>

                <!-- Notices Card -->
                <el-col :span="12" :md="6">
                    <home-card title="通知公告" icon="Bell" class="notice-card" :moreRouteName="'NoticeView'" @more="handleMenuSelect">
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

                <!-- Weather Card -->
                <el-col :span="12" :md="6">
                    <home-card title="当前天气" icon="Cloudy" class="weather-card">
                        <div v-if="weatherData">
                            <p>温度: {{ weatherData.temperature }}°C</p>
                            <p>天气: {{ weatherData.weather }}</p>
                            <p>湿度: {{ weatherData.humidity }}%</p>
                        </div>
                        <div v-else>
                            <el-skeleton :rows="3" animated />
                        </div>
                    </home-card>
                </el-col>

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
    </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { getHealthData } from '@/api/fore/health';
import HomeCard from '@/components/front/HomeCard.vue';
import { Phone } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { formatDateTime, formatDate } from '@/utils/date';

const props = defineProps({
    isLoggedIn: {
        type: Boolean,
        required: true
    }
});

const router = useRouter();

const dashboard = ref({
    title: '社区养老系统首页',
    description: '欢迎来到社区养老系统，您可以在这里查看和管理您的服务预约、健康档案和社区活动。',
});

// 健康数据相关
const healthData = ref(null);

// 健康指标标签
const healthLabels = {
    bloodPressure: '血压',
    heartRate: '心率',
    bloodSugar: '血糖',
    temperature: '体温',
    height: '身高',
    weight: '体重'
};

// 计算要显示的健康数据
const displayHealthData = computed(() => {
    if (!healthData.value) return {};
    return {
        bloodPressure: healthData.value.bloodPressure,
        heartRate: healthData.value.heartRate,
        bloodSugar: healthData.value.bloodSugar,
        temperature: healthData.value.temperature,
        height: healthData.value.height,
        weight: healthData.value.weight
    };
});

// 格式化健康数据显示
const formatHealthValue = (key, value) => {
    if (value === null || value === undefined) return '暂无数据';
    
    const units = {
        bloodPressure: 'mmHg',
        heartRate: '次/分',
        bloodSugar: 'mmol/L',
        temperature: '℃',
        height: 'cm',
        weight: 'kg'
    };
    
    return `${value} ${units[key] || ''}`;
};

// 获取健康数据
const fetchHealthData = async () => {
    if (!props.isLoggedIn) return;
    
    try {
        // 从本地存储获取老人ID
        const userInfo = localStorage.getItem('userInfo');
        if (!userInfo) {
            console.warn('未找到老人信息');
            return;
        }

        const elder = JSON.parse(userInfo);
        const response = await getHealthData(elder.id);
        
        if (response.code === 200 && response.data) {
            healthData.value = response.data;
        } else if (response.code === 404) {
            console.warn('暂无健康数据');
            healthData.value = null;
        }
    } catch (error) {
        console.error('获取健康数据失败:', error);
        ElMessage.error('获取健康数据失败');
    }
};

// 导航到健康档案页面
const navigateTo = (path) => {
    router.push(`/${path}`);
};

// 监听登录状态变化
watch(() => props.isLoggedIn, (newValue) => {
    if (newValue) {
        fetchHealthData();
    } else {
        healthData.value = null;
    }
});

const services = ref([]);
const activities = ref([]);
const recentNotices = ref([
    { date: '2025-02-23', title: '社区活动：健康讲座将于本周六举行' },
    { date: '2025-02-20', title: '系统维护通知：2月25日凌晨停机2小时' },
    // 更多通知公告
]);
const emergencyContact = ref('张三');
const emergencyPhone = ref('123-456-7890');

// 天气数据
const weatherData = ref({
  temperature: 25,
  weather: '晴',
  humidity: 65,
  windDirection: '东南风',
  windSpeed: '3级',
  airQuality: '优',
  updateTime: new Date().toLocaleString()
});

onMounted(() => {
    if (props.isLoggedIn) {
        fetchHealthData();
    }
});

const getServiceStatusType = (status) => {
    return status === '已完成' ? 'success' : 'warning';
};

const viewNoticeDetail = (notice) => {
    console.log(notice);
    // 查看通知详情的逻辑
};

const handleEmergencyCall = () => {
    console.log('紧急呼叫');
    // 紧急呼叫的逻辑
};

// 处理菜单选择
const handleMenuSelect = (routeName) => {
    if (!props.isLoggedIn && routeName !== 'ActivityView' && routeName !== 'NoticeView') {
        ElMessage.warning('请先登录以访问此功能');
        return;
    }
    router.push({ name: routeName });
};
</script>

<style scoped>
.dashboard {
    width: 100%;
    height: 100%;
    background-color: #f0f2f5;
    padding: 20px;
}

.content-card {
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    background: linear-gradient(135deg, #ffffff 0%, #f9f9f9 100%);
}

.info-card {
    text-align: center;
    transition: transform 0.3s ease;
}

.info-card:hover {
    transform: translateY(-5px);
}

.info-card h3 {
    margin-bottom: 10px;
    color: #2c3e50;
    font-weight: 600;
}

.info-card p {
    margin-bottom: 20px;
    color: #606266;
}

.data-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
    padding: 8px;
    background: rgba(0, 0, 0, 0.02);
    border-radius: 4px;
}

.data-item .label {
    color: #666;
    font-size: 14px;
}

.data-item .value {
    font-weight: 600;
    color: #2c3e50;
}

.update-time {
    margin-top: 12px;
    font-size: 12px;
    color: #999;
    text-align: right;
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

.weather-card {
    border-left: 4px solid var(--el-color-info);
}

.emergency-card {
    border-left: 4px solid var(--el-color-danger);
}

/* 添加一些动画效果 */
.data-item {
    transition: all 0.3s ease;
}

.data-item:hover {
    background: rgba(64, 158, 255, 0.1);
}
</style>
