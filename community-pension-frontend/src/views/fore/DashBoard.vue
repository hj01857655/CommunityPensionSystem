<template>
    <div class="dashboard">
        <el-card class="content-card" shadow="hover">
            <div class="dashboard-header">
                <div>
                    <h1>{{ dashboard.title }}</h1>
                    <p>{{ dashboard.description }}</p>
                </div>
                <el-button 
                    type="primary" 
                    :icon="Refresh" 
                    circle 
                    @click="refreshData" 
                    title="刷新数据"
                    class="refresh-button">
                </el-button>
            </div>
            <el-row :gutter="20" class="dashboard-grid">
                <!-- Health Card -->
                <el-col :span="24" :md="8">
                    <home-card title="健康监测" icon="FirstAidKit" class="health-card" :moreRouteName="'HealthView'" @more="handleMenuSelect">
                        <div v-if="isLoggedIn">
                            <div v-if="healthData">
                                <div v-for="(value, key) in displayHealthData" 
                                     :key="key" 
                                     class="data-item"
                                     :class="{ 'data-abnormal': getHealthValueType(key, value) === 'abnormal' }">
                                    <span class="label">{{ healthLabels[key] }}</span>
                                    <span class="value" :class="{ 'value-abnormal': getHealthValueType(key, value) === 'abnormal' }">
                                        {{ formatHealthValue(key, value) }}
                                        <el-tooltip v-if="getHealthValueType(key, value) === 'abnormal'" 
                                                   :content="`正常范围: ${healthRanges[key].min}-${healthRanges[key].max}${healthRanges[key].unit}`" 
                                                   placement="top">
                                            <el-icon class="warning-icon"><Warning /></el-icon>
                                        </el-tooltip>
                                    </span>
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
                <el-col :span="24" :md="8">
                    <home-card title="服务预约" icon="Calendar" class="service-card" :moreRouteName="'ServiceView'" @more="handleMenuSelect">
                        <div v-if="isLoggedIn">
                            <div v-if="servicesLoading" class="loading-container">
                                <el-skeleton :rows="3" animated />
                            </div>
                            <el-empty v-else-if="!services.length" description="暂无预约服务" />
                            <div v-else class="service-list">
                                <div v-for="service in services" :key="service.id" 
                                     class="service-item" 
                                     :class="{'service-upcoming': service.isUpcoming}">
                                    <div class="service-info">
                                        <div class="service-name">{{ service.name }}</div>
                                        <div class="service-time" :class="{'service-time-soon': service.isUpcoming}">
                                            {{ getServiceTimeDisplay(service.scheduleTime) }}
                                        </div>
                                    </div>
                                    <div class="service-actions">
                                        <el-tag :type="getServiceStatusType(service.status)">
                                            {{ service.status }}
                                        </el-tag>
                                        <el-button 
                                            v-if="service.status !== '已完成' && service.status !== '已取消' && service.status !== '已拒绝'"
                                            type="danger" 
                                            size="small" 
                                            link
                                            @click="cancelService(service.id)">
                                            取消
                                        </el-button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div v-else>
                            <el-empty description="请登录以查看服务预约" />
                        </div>
                    </home-card>
                </el-col>

                <!-- Activities Card -->
                <el-col :span="24" :md="8">
                    <home-card title="社区活动" icon="Flag" class="activity-card" :moreRouteName="'ActivityView'" @more="handleMenuSelect">
                        <div v-if="activitiesLoading" class="loading-container">
                            <el-skeleton :rows="3" animated />
                        </div>
                        <el-empty v-else-if="activities.length === 0" description="暂无活动" />
                        <div v-else class="activity-list">
                            <div v-for="activity in activities" :key="activity.id" 
                                 class="activity-item"
                                 :class="{'activity-coming-soon': activity.isComingSoon}">
                                <div class="activity-info">
                                    <div class="activity-header">
                                        <span class="activity-name">{{ activity.name }}</span>
                                        <el-tag :type="getActivityStatusType(activity.status)" size="small">
                                            {{ getActivityStatusText(activity.status) }}
                                        </el-tag>
                                    </div>
                                    <div class="activity-time">
                                        <el-icon><Clock /></el-icon> {{ formatActivityDate(activity.date) }}
                                    </div>
                                    <div class="activity-location">
                                        <el-icon><Location /></el-icon> {{ getActivityInfoDisplay(activity) }}
                                    </div>
                                </div>
                                <div v-if="activity.isComingSoon" class="activity-badge">
                                    即将开始
                                </div>
                                <el-button 
                                    v-if="activity.status === 0 && isLoggedIn"
                                    type="primary" 
                                    size="small" 
                                    link
                                    @click="registerActivity(activity.id)">
                                    报名
                                </el-button>
                            </div>
                        </div>
                    </home-card>
                </el-col>

                <!-- Notices Card -->
                <el-col :span="24" :md="8">
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
                <el-col :span="24" :md="8">
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
                <el-col :span="24" :md="8">
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
import { ref, onMounted, computed, watch, onUnmounted, onBeforeUnmount } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { getHealthData } from '@/api/fore/health';
import HomeCard from '@/components/front/HomeCard.vue';
import { Phone, Warning, Refresh, Clock, Location } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { formatDateTime, formatDate } from '@/utils/date';
import { getMyAppointments, cancelAppointment } from '@/api/fore/service';
import { getActivityList, getActivityDetail, registerActivity as apiRegisterActivity } from '@/api/fore/activity';
import { useUserStore } from '@/stores/fore/useUserStore';

const router = useRouter();
const userStore = useUserStore();

const props = defineProps({
    isLoggedIn: {
        type: Boolean,
        required: true
    }
});

const dashboard = ref({
    title: '社区养老系统首页',
    description: '欢迎来到社区养老系统，您可以在这里查看和管理您的服务预约、健康档案和社区活动。',
});

// 健康数据相关
const healthData = ref(null);
const healthDataLoading = ref(false); // 添加健康数据加载状态
const healthDataRetryCount = ref(0); // 添加重试次数计数
const MAX_RETRY_COUNT = 3; // 最大重试次数

// 健康指标标签
const healthLabels = {
    bloodPressure: '血压',
    heartRate: '心率',
    bloodSugar: '血糖',
    temperature: '体温',
    height: '身高',
    weight: '体重'
};

// 健康指标正常范围配置
const healthRanges = {
    bloodPressure: { min: '90/60', max: '140/90', unit: 'mmHg' },
    heartRate: { min: 60, max: 100, unit: '次/分' },
    bloodSugar: { min: 3.9, max: 6.1, unit: 'mmol/L' },
    temperature: { min: 36.3, max: 37.2, unit: '℃' },
    height: { min: 0, max: 0, unit: 'cm' }, // 身高没有异常范围
    weight: { min: 0, max: 0, unit: 'kg' }  // 体重也没有统一的异常范围
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

// 检查健康指标是否异常
const isAbnormalValue = (key, value) => {
    if (!value || !healthRanges[key]) return false;
    
    // 血压特殊处理，格式为 "收缩压/舒张压"
    if (key === 'bloodPressure' && value.includes('/')) {
        const [systolic, diastolic] = value.split('/').map(v => parseInt(v.trim(), 10));
        const [minSys, minDia] = healthRanges[key].min.split('/').map(v => parseInt(v.trim(), 10));
        const [maxSys, maxDia] = healthRanges[key].max.split('/').map(v => parseInt(v.trim(), 10));
        
        return systolic < minSys || systolic > maxSys || diastolic < minDia || diastolic > maxDia;
    }
    
    // 其他数值型指标
    const numValue = parseFloat(value);
    return numValue < healthRanges[key].min || numValue > healthRanges[key].max;
};

// 获取健康指标状态类型
const getHealthValueType = (key, value) => {
    if (!value || !healthRanges[key] || (key === 'height' || key === 'weight')) return '';
    return isAbnormalValue(key, value) ? 'abnormal' : 'normal';
};

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

// 获取用户ID的工具函数
const getUserId = () => {
    try {
        // 首先尝试从userStore获取用户信息
        if (userStore.userInfo && (userStore.userInfo.id || userStore.userInfo.userId || userStore.userInfo.elderId)) {
            const userIdFromStore = userStore.userInfo.id || userStore.userInfo.userId || userStore.userInfo.elderId;
            console.log('从userStore获取到用户ID:', userIdFromStore);
            return userIdFromStore;
        }
        
        // 如果从store中获取失败，尝试从localStorage获取
        const userInfoStr = localStorage.getItem('userInfo');
        if (!userInfoStr) {
            console.warn('无法获取用户信息，localStorage中不存在userInfo');
            return null;
        }
        
        const userInfo = JSON.parse(userInfoStr);
        console.log('从localStorage解析的用户信息:', userInfo);
        
        // 尝试从各种可能的字段中获取用户ID
        const userId = userInfo.id || userInfo.userId || userInfo.elderId;
        
        if (!userId) {
            console.warn('用户信息中无法找到有效ID:', userInfo);
            // 尝试从其他可能的存储位置获取
            const userId = localStorage.getItem('userId');
            if (userId) {
                console.log('从localStorage.userId获取到用户ID:', userId);
                return userId;
            }
            return null;
        }
        
        return userId;
    } catch (error) {
        console.error('获取用户ID时出错:', error);
        return null;
    }
};

// 获取健康数据
const fetchHealthData = async () => {
    if (!props.isLoggedIn || healthDataLoading.value) return; // 如果已经在加载中，则不重复请求
    
    // 如果重试次数已达上限，则不再尝试
    if (healthDataRetryCount.value >= MAX_RETRY_COUNT) {
        console.warn(`健康数据请求已达最大重试次数 ${MAX_RETRY_COUNT}，不再尝试`);
        return;
    }
    
    healthDataLoading.value = true;
    
    // 设置超时控制
    const timeoutPromise = new Promise((_, reject) => {
        setTimeout(() => reject(new Error('请求超时')), 10000); // 10秒超时
    });
    
    try {
        // 使用工具函数获取用户ID
        const elderId = getUserId();
        if (!elderId) {
            console.error('无法获取有效的用户ID');
            healthDataLoading.value = false;
            ElMessage.warning('获取用户信息失败，请尝试重新登录');
            return;
        }
        
        console.log('健康数据 - 使用elderId:', elderId);
        
        // 使用Promise.race实现请求超时控制
        const response = await Promise.race([
            getHealthData(elderId),
            timeoutPromise
        ]);
        
        // 成功获取数据，重置重试计数
        healthDataRetryCount.value = 0;
        
        if (response.code === 200 && response.data) {
            healthData.value = response.data;
        } else if (response.code === 404) {
            console.warn('暂无健康数据');
            healthData.value = null;
        } else {
            console.warn('获取健康数据失败:', response);
            healthDataRetryCount.value++;
        }
    } catch (error) {
        console.error('获取健康数据失败:', error);
        healthDataRetryCount.value++;
        
        // 记录重试次数并在控制台显示
        console.warn(`健康数据请求失败，当前重试次数: ${healthDataRetryCount.value}/${MAX_RETRY_COUNT}`);
    } finally {
        healthDataLoading.value = false;
    }
};

const services = ref([]);
const servicesLoading = ref(false);
const servicesRetryCount = ref(0); // 添加服务请求重试计数

const activities = ref([]);
const activitiesLoading = ref(false);
const activitiesRetryCount = ref(0); // 添加活动请求重试计数

// 创建超时控制Promise
const createTimeoutPromise = (milliseconds = 10000) => {
    return new Promise((_, reject) => {
        setTimeout(() => reject(new Error(`请求超时(${milliseconds}ms)`)), milliseconds);
    });
};

// 获取最近的服务预约
const fetchRecentServices = async () => {
    if (!props.isLoggedIn || servicesLoading.value) return;
    
    // 重试次数检查
    if (servicesRetryCount.value >= MAX_RETRY_COUNT) {
        console.warn(`服务请求已达最大重试次数 ${MAX_RETRY_COUNT}，不再尝试`);
        return;
    }
    
    servicesLoading.value = true;
    
    try {
        // 使用工具函数获取用户ID
        const userId = getUserId();
        if (!userId) {
            console.error('无法获取有效的用户ID');
            servicesLoading.value = false;
            services.value = [];
            ElMessage.warning('获取用户信息失败，请尝试重新登录');
            return;
        }
        
        console.log('服务预约 - 使用userId:', userId);
        
        // 调用API获取用户的服务预约列表，添加超时控制
        const response = await Promise.race([
            getMyAppointments({ 
                userId: userId,
                size: 10 // 获取多一些记录，以便筛选后仍有足够数据
            }),
            createTimeoutPromise()
        ]);
        
        // 成功获取数据，重置重试计数
        servicesRetryCount.value = 0;
        
        if (response.code === 200 && response.data) {
            // 根据API返回数据结构调整数据处理
            const appointmentData = Array.isArray(response.data) ? response.data : 
                (response.data.records ? response.data.records : []);
                
            // 获取最近的预约并格式化数据
            services.value = appointmentData
                // 过滤掉已取消和已拒绝的预约
                .filter(service => service.status !== 4 && service.status !== 5)
                // 按预约时间排序
                .sort((a, b) => new Date(a.scheduleTime || a.appointmentTime) - new Date(b.scheduleTime || b.appointmentTime))
                // 只取最近的3条
                .slice(0, 3)
                .map(service => ({
                    id: service.id,
                    name: service.serviceItemName || service.serviceName,
                    status: service.statusText || getOrderStatusText(service.status),
                    scheduleTime: service.scheduleTime || service.appointmentTime,
                    isUpcoming: isUpcomingService(service.scheduleTime || service.appointmentTime)
                }));
        } else {
            console.warn('获取服务预约返回数据异常:', response);
            services.value = [];
            servicesRetryCount.value++;
        }
    } catch (error) {
        console.error('获取服务预约失败:', error);
        servicesRetryCount.value++;
        console.warn(`服务请求失败，当前重试次数: ${servicesRetryCount.value}/${MAX_RETRY_COUNT}`);
        services.value = [];
    } finally {
        servicesLoading.value = false;
    }
};

// 判断一个服务是否即将到期（24小时内）
const isUpcomingService = (scheduleTime) => {
    if (!scheduleTime) return false;
    
    const now = new Date();
    const serviceTime = new Date(scheduleTime);
    const hoursDiff = (serviceTime - now) / (1000 * 60 * 60);
    
    // 如果预约时间在24小时内，标记为即将到期
    return hoursDiff > 0 && hoursDiff <= 24;
};

// 获取预约时间的友好显示
const getServiceTimeDisplay = (scheduleTime) => {
    if (!scheduleTime) return '时间未定';
    
    const now = new Date();
    const serviceTime = new Date(scheduleTime);
    const hoursDiff = (serviceTime - now) / (1000 * 60 * 60);
    const daysDiff = Math.floor(hoursDiff / 24);
    
    if (hoursDiff < 0) {
        return `已过期 (${formatDate(scheduleTime)})`;
    } else if (hoursDiff < 1) {
        return `即将开始 (不到1小时)`;
    } else if (hoursDiff < 24) {
        return `即将开始 (${Math.floor(hoursDiff)}小时后)`;
    } else {
        return `${daysDiff}天后 (${formatDate(scheduleTime)})`;
    }
};

// 获取服务订单状态文本
const getOrderStatusText = (status) => {
    // 服务订单状态映射: 0-待审核 1-已审核 2-服务中 3-已完成 4-已取消 5-已拒绝
    const statusMap = {
        0: '待审核',
        1: '已审核',
        2: '服务中',
        3: '已完成',
        4: '已取消',
        5: '已拒绝'
    };
    return statusMap[status] || '未知状态';
};

// 获取服务状态对应的类型（用于Tag组件）
const getServiceStatusType = (status) => {
    // 状态与Tag类型映射关系
    const statusTypeMap = {
        '待审核': 'info',
        '已审核': 'success',
        '服务中': 'warning',
        '已完成': 'success', 
        '已取消': 'danger',
        '已拒绝': 'danger'
    };
    return statusTypeMap[status] || 'info';
};

// 获取活动状态对应的类型（用于Tag组件）
const getActivityStatusType = (status) => {
    // 活动状态与Tag类型映射: 0-未开始，1-进行中，2-已结束
    const statusMap = {
        0: 'info',
        1: 'success',
        2: 'danger'
    };
    return statusMap[status] || 'info';
};

// 获取活动状态文本
const getActivityStatusText = (status) => {
    // 活动状态文本映射: 0-未开始，1-进行中，2-已结束
    const statusMap = {
        0: '未开始',
        1: '进行中',
        2: '已结束'
    };
    return statusMap[status] || '未知状态';
};

// 获取最近的活动
const fetchRecentActivities = async () => {
    if (activitiesLoading.value) return;
    
    // 重试次数检查
    if (activitiesRetryCount.value >= MAX_RETRY_COUNT) {
        console.warn(`活动请求已达最大重试次数 ${MAX_RETRY_COUNT}，不再尝试`);
        return;
    }
    
    activitiesLoading.value = true;
    
    try {
        // 调用API获取最近的活动，添加超时控制
        const response = await Promise.race([
            getActivityList({ pageSize: 5 }),
            createTimeoutPromise()
        ]);
        
        // 成功获取活动列表，重置重试计数
        activitiesRetryCount.value = 0;
        
        if (response.code === 200 && response.data && response.data.records) {
            // 获取活动详情并格式化数据，每个活动详情也添加超时控制
            const activityPromises = response.data.records
                .slice(0, 3) // 先只取前3条
                .map(async activity => {
                    // 获取活动详情以获取更多信息
                    try {
                        const detailResponse = await Promise.race([
                            getActivityDetail(activity.id),
                            createTimeoutPromise(5000) // 活动详情5秒超时
                        ]);
                        
                        if (detailResponse.code === 200 && detailResponse.data) {
                            const detail = detailResponse.data;
                            return {
                                id: activity.id,
                                name: activity.title,
                                status: activity.status,
                                date: activity.startTime,
                                endTime: activity.endTime,
                                location: detail.location || '地点待定',
                                maxParticipants: detail.maxParticipants || 0,
                                currentParticipants: detail.currentParticipants || 0,
                                isComingSoon: isActivityComingSoon(activity.startTime)
                            };
                        }
                        return null;
                    } catch (error) {
                        console.error(`获取活动详情失败: ${activity.id}`, error);
                        // 返回基本信息，没有额外的详情
                        return {
                            id: activity.id,
                            name: activity.title,
                            status: activity.status,
                            date: activity.startTime,
                            endTime: activity.endTime,
                            location: '地点待定',
                            isComingSoon: isActivityComingSoon(activity.startTime)
                        };
                    }
                });
                
            // 使用Promise.allSettled代替Promise.all，确保部分失败不影响整体
            const results = await Promise.allSettled(activityPromises);
            activities.value = results
                .filter(result => result.status === 'fulfilled' && result.value !== null)
                .map(result => result.value);
        } else {
            console.warn('获取活动列表返回数据异常:', response);
            activities.value = [];
            activitiesRetryCount.value++;
        }
    } catch (error) {
        console.error('获取活动列表失败:', error);
        activitiesRetryCount.value++;
        console.warn(`活动请求失败，当前重试次数: ${activitiesRetryCount.value}/${MAX_RETRY_COUNT}`);
        activities.value = [];
    } finally {
        activitiesLoading.value = false;
    }
};

// 判断活动是否即将开始（48小时内开始的活动）
const isActivityComingSoon = (startTime) => {
    if (!startTime) return false;
    
    const now = new Date();
    const activityTime = new Date(startTime);
    const hoursDiff = (activityTime - now) / (1000 * 60 * 60);
    
    // 如果活动在48小时内开始且尚未开始
    return hoursDiff > 0 && hoursDiff <= 48;
};

// 获取活动地点和名额显示
const getActivityInfoDisplay = (activity) => {
    let result = activity.location || '地点待定';
    
    if (activity.maxParticipants > 0) {
        const remaining = activity.maxParticipants - (activity.currentParticipants || 0);
        result += ` | 剩余名额: ${remaining > 0 ? remaining : '已满'}`;
    }
    
    return result;
};

// 导航到健康档案页面
const navigateTo = (path) => {
    router.push(`/${path}`);
};

// 登录状态变化监听
const loginStateChangeTimer = ref(null);
watch(() => props.isLoggedIn, async (newValue, oldValue) => {
    // 使用防抖处理登录状态变化，以防止短时间内多次触发
    if (loginStateChangeTimer.value) {
        clearTimeout(loginStateChangeTimer.value);
    }
    
    loginStateChangeTimer.value = setTimeout(async () => {
        console.log('登录状态变化:', oldValue, '->', newValue);
        
        if (newValue !== oldValue) {
            // 登录状态发生变化，重新初始化数据
            await initializeData();
        }
    }, 500);
});

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

// 定时刷新相关设置
const refreshInterval = ref(300000); // 默认5分钟刷新一次
const refreshTimer = ref(null);

// 手动刷新数据
const refreshData = async () => {
    // 如果已经有请求在进行中，则不重复发起请求
    if (healthDataLoading.value || servicesLoading.value || activitiesLoading.value) {
        console.log('有请求正在进行中，请稍后再刷新');
        return;
    }
    
    try {
        // 显示刷新中的提示
        const loadingInstance = ElMessage({
            message: '数据刷新中...',
            type: 'info',
            duration: 0
        });
        
        if (props.isLoggedIn) {
            const promises = [];
            // 仅当重试次数未达上限时才发起请求
            if (healthDataRetryCount.value < MAX_RETRY_COUNT) {
                promises.push(fetchHealthData());
            }
            if (servicesRetryCount.value < MAX_RETRY_COUNT) {
                promises.push(fetchRecentServices());
            }
            
            // 使用allSettled确保部分失败不影响整体
            await Promise.allSettled(promises);
        }
        
        // 不管是否登录都刷新活动数据
        if (activitiesRetryCount.value < MAX_RETRY_COUNT) {
            await fetchRecentActivities();
        }
        
        // 关闭加载提示
        loadingInstance.close();
        
        // 打印API请求情况报告
        printApiStatusReport();
    } catch (error) {
        console.error('刷新数据失败:', error);
        ElMessage.error('刷新数据失败，请稍后再试');
    }
};

// 打印API请求状态报告
const printApiStatusReport = () => {
    console.group('API请求状态报告');
    console.log(`健康数据: ${healthDataLoading.value ? '加载中' : '空闲'}, 重试次数: ${healthDataRetryCount.value}/${MAX_RETRY_COUNT}`);
    console.log(`服务数据: ${servicesLoading.value ? '加载中' : '空闲'}, 重试次数: ${servicesRetryCount.value}/${MAX_RETRY_COUNT}`);
    console.log(`活动数据: ${activitiesLoading.value ? '加载中' : '空闲'}, 重试次数: ${activitiesRetryCount.value}/${MAX_RETRY_COUNT}`);
    console.groupEnd();
};

// 设置自动刷新定时器
const setupAutoRefresh = () => {
    // 清除已有定时器
    if (refreshTimer.value) {
        clearInterval(refreshTimer.value);
    }
    
    // 设置新定时器 - 只有在没有任何请求正在进行中且重试次数未达上限时才执行刷新
    refreshTimer.value = setInterval(() => {
        const hasActiveRequests = healthDataLoading.value || servicesLoading.value || activitiesLoading.value;
        const hasMaxRetries = 
            healthDataRetryCount.value >= MAX_RETRY_COUNT && 
            servicesRetryCount.value >= MAX_RETRY_COUNT && 
            activitiesRetryCount.value >= MAX_RETRY_COUNT;
            
        if (!hasActiveRequests && !hasMaxRetries) {
            console.log(`执行定时刷新 (${new Date().toLocaleTimeString()})`);
            refreshData();
        } else if (hasMaxRetries) {
            console.warn('所有API请求均已达到最大重试次数，跳过定时刷新');
        } else {
            console.log('有请求正在进行中，跳过本次定时刷新');
        }
    }, refreshInterval.value);
};

// 组件卸载时清除所有定时器和状态
onUnmounted(() => {
    console.log('Dashboard组件卸载');
    
    // 清除刷新定时器
    if (refreshTimer.value) {
        clearInterval(refreshTimer.value);
        refreshTimer.value = null;
    }
    
    // 清除登录状态变化定时器
    if (loginStateChangeTimer.value) {
        clearTimeout(loginStateChangeTimer.value);
        loginStateChangeTimer.value = null;
    }
    
    // 重置所有loading状态和重试计数
    healthDataLoading.value = false;
    healthDataRetryCount.value = 0;
    servicesLoading.value = false;
    servicesRetryCount.value = 0;
    activitiesLoading.value = false;
    activitiesRetryCount.value = 0;
});

// 工具函数：主动初始化数据
const initializeData = async () => {
    // 检查登录状态条件
    if (!props.isLoggedIn) {
        console.warn('用户未登录，只加载不需要登录的数据');
        // 重置需要登录才能查看的数据
        healthData.value = null;
        services.value = [];
        // 加载不需要登录的数据
        await fetchRecentActivities();
        return;
    }

    try {
        // 先主动获取用户信息
        const userInfoResponse = await userStore.getUserInfo();
        
        // 检查用户信息响应
        if (userInfoResponse.code !== 200 || !userInfoResponse.data) {
            console.error('获取用户信息失败:', userInfoResponse);
            ElMessage.error('获取用户信息失败，请尝试重新登录');
            return;
        }
        
        console.log('初始化 - 成功获取用户信息:', userInfoResponse.data);
        
        // 验证用户ID
        const userId = getUserId();
        if (!userId) {
            console.error('无法获取有效的用户ID');
            ElMessage.error('用户ID无效，请重新登录');
            return;
        }
        
        // 显示加载中消息
        const loadingMessage = ElMessage({
            message: '正在加载数据...',
            type: 'info',
            duration: 0
        });
        
        try {
            // 并行获取所有数据
            await Promise.allSettled([
                fetchHealthData(),
                fetchRecentServices(),
                fetchRecentActivities()
            ]);
            
            // 检查是否有数据
            if (!healthData.value) {
                console.warn('未获取到健康数据');
            }
            
            if (services.value.length === 0) {
                console.warn('未获取到服务预约数据');
            }
            
            if (activities.value.length === 0) {
                console.warn('未获取到活动数据');
            }
        } finally {
            // 关闭加载消息
            loadingMessage.close();
        }
    } catch (error) {
        console.error('初始化数据失败:', error);
        ElMessage.error('初始化数据出错，请刷新页面重试');
    }
};

// 修改onMounted钩子
onMounted(async () => {
    console.log('Dashboard组件挂载，登录状态:', props.isLoggedIn);
    // 初始化数据
    await initializeData();
    // 设置自动刷新
    setupAutoRefresh();
    
    // 监听主题变化事件
    window.addEventListener('fore-theme-changed', handleThemeChange);
});

onBeforeUnmount(() => {
    // 移除主题变化事件监听
    window.removeEventListener('fore-theme-changed', handleThemeChange);
});

// 处理主题变化
const handleThemeChange = () => {
    // 此处可以添加主题变化时需要执行的特定操作
    console.log('[仪表盘] 接收到主题变化事件');
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

// 取消服务预约
const cancelService = async (serviceId) => {
    if (!props.isLoggedIn) {
        ElMessage.warning('请先登录');
        return;
    }
    
    try {
        // 调用取消预约的API
        const response = await cancelAppointment(serviceId, '用户从首页取消');
        
        if (response.code === 200) {
            ElMessage.success('取消预约成功');
            // 刷新服务列表
            await fetchRecentServices();
        } else {
            ElMessage.error(response.msg || '取消预约失败');
        }
    } catch (error) {
        console.error('取消预约失败:', error);
        ElMessage.error('取消预约失败');
    }
};

// 活动报名
const registerActivity = async (activityId) => {
    if (!props.isLoggedIn) {
        ElMessage.warning('请先登录');
        return;
    }
    
    try {
        // 调用活动报名的API
        const response = await apiRegisterActivity(activityId);
        
        if (response.code === 200) {
            ElMessage.success('活动报名成功');
            // 刷新活动列表
            await fetchRecentActivities();
        } else {
            ElMessage.error(response.msg || '活动报名失败');
        }
    } catch (error) {
        console.error('活动报名失败:', error);
        ElMessage.error('活动报名失败');
    }
};

// 格式化活动日期（添加安全处理）
const formatActivityDate = (date) => {
    if (!date) return '日期待定';
    
    try {
        return formatDate(date);
    } catch (error) {
        console.error('格式化活动日期出错:', error, date);
        // 返回原始日期字符串或默认值
        return String(date) || '日期有误';
    }
};
</script>

<style scoped>
/* 基础布局样式 */
.dashboard {
    width: 100%;
    height: 100%;
    background-color: #f0f2f5;
    padding: 20px;
    overflow-x: hidden;
}

:root.dark .dashboard {
    background-color: #1f1f1f;
    color: #fff;
}

.content-card {
    width: 100%;
    margin-bottom: 20px;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    overflow: hidden;
}

:root.dark .content-card {
    background-color: #2a2a2a;
    color: #eee;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.3);
}

/* 网格布局优化 */
.dashboard-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
    margin-top: 20px;
}

/* 响应式布局优化 */
@media (max-width: 1366px) {
    .dashboard-grid {
        grid-template-columns: repeat(3, 1fr);
        gap: 16px;
    }
    
    .dashboard {
        padding: 16px;
    }
}

@media (max-width: 768px) {
    .dashboard-grid {
        grid-template-columns: 1fr;
        gap: 12px;
    }
    
    .dashboard {
        padding: 12px;
    }
    
    .content-card {
        margin-bottom: 12px;
    }
}

/* 通用卡片内容样式 */
:deep(.el-card__body) {
    padding: 20px;
}

:deep(.el-empty) {
    padding: 32px;
}

:deep(.el-skeleton) {
    padding: 20px;
}

/* 健康卡片样式优化 */
.health-card {
    .data-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 12px;
        margin-bottom: 10px;
        border-radius: 4px;
        background-color: #f8f9fa;
        
        &:last-child {
            margin-bottom: 0;
        }
        
        .label {
            color: #606266;
            font-size: 14px;
            min-width: 60px;
        }
        
        .value {
            font-weight: 500;
            color: #303133;
            text-align: right;
            flex: 1;
            margin-left: 16px;
        }
        
        &.data-abnormal {
            background-color: #fff3f3;
        }
    }
}

:root.dark .health-card .data-item {
    background-color: #333;
    
    .label {
        color: #aaa;
    }
    
    .value {
        color: #eee;
    }
    
    &.data-abnormal {
        background-color: #3d1d1d;
    }
}

/* 服务预约卡片样式优化 */
.service-card {
    .service-list {
        display: flex;
        flex-direction: column;
        gap: 12px;
    }
    
    .service-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 12px;
        background-color: #f8f9fa;
        border-radius: 4px;
        
        .service-info {
            flex: 1;
            min-width: 0;
            margin-right: 16px;
            
            .service-name {
                font-weight: 500;
                margin-bottom: 6px;
                color: #303133;
                font-size: 14px;
            }
            
            .service-time {
                font-size: 12px;
                color: #909399;
            }
        }
        
        .service-actions {
            display: flex;
            align-items: center;
            gap: 12px;
            flex-shrink: 0;
        }
    }
}

:root.dark .service-card {
    .service-item {
        background-color: #333;
        
        .service-info {
            .service-name {
                color: #eee;
            }
            
            .service-time {
                color: #888;
            }
        }
    }
}

/* 活动卡片样式优化 */
.activity-card {
    .activity-list {
        display: flex;
        flex-direction: column;
        gap: 12px;
    }
    
    .activity-item {
        padding: 12px;
        background-color: #f8f9fa;
        border-radius: 4px;
        position: relative;
        
        .activity-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 8px;
            
            .activity-name {
                font-weight: 500;
                color: #303133;
                flex: 1;
                margin-right: 16px;
                font-size: 14px;
            }
        }
        
        .activity-time,
        .activity-location {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 12px;
            color: #909399;
            margin-top: 6px;
            
            .el-icon {
                font-size: 14px;
            }
        }
    }
}

:root.dark .activity-card {
    .activity-item {
        background-color: #333;
        
        .activity-header {
            .activity-name {
                color: #eee;
            }
        }
        
        .activity-time,
        .activity-location {
            color: #888;
        }
    }
}
</style>
