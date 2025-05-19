<template>
  <div class="activity-view-container">
    <div class="activity-view">
      <el-card class="content-card" shadow="hover">
        <!-- 活动标签页切换 -->
        <el-tabs v-model="activeTab">
          <!-- 社区活动标签页 -->
          <el-tab-pane label="社区活动" name="activities">
            <!-- 搜索和筛选区域 -->
            <div class="search-filter-container">
              <el-input
                v-model="searchQuery"
                placeholder="搜索活动名称"
                prefix-icon="Search"
                clearable
                @input="handleSearch"
                class="search-input"
              />
              <el-select v-model="filterType" placeholder="活动类型" clearable @change="handleFilter" class="filter-select">
                <el-option v-for="type in activityTypes" :key="type.value" :label="type.label" :value="type.value" />
              </el-select>
              <el-select v-model="filterStatus" placeholder="活动状态" clearable @change="handleFilter" class="filter-select">
                <el-option v-for="(text, status) in STATUS_MAP" :key="status" :label="text" :value="Number(status)" />
              </el-select>
              <el-button 
                type="primary" 
                :icon="Refresh" 
                circle 
                @click="handleRefresh"
                :loading="refreshing"
                class="refresh-button"
              />
            </div>
            
            <!-- 活动类型快捷过滤 -->
            <div class="quick-filter-container">
              <el-button 
                size="small" 
                class="quick-filter-btn" 
                :type="filterType === '' ? 'primary' : 'default'"
                @click="quickFilterByType('')"
              >
                全部
              </el-button>
              <el-button 
                v-for="type in activityTypes" 
                :key="type.value"
                :type="filterType === type.value ? 'primary' : 'default'"
                size="small"
                class="quick-filter-btn"
                @click="quickFilterByType(type.value)"
              >
                {{ type.label }}
              </el-button>
            </div>
            
            <!-- 加载状态显示 -->
            <div v-if="loading" class="loading-container">
              <el-row :gutter="20">
                <el-col v-for="i in 6" :key="i" :xs="24" :sm="12" :md="8" :lg="8">
                  <div class="skeleton-card" style="width: 100%; height: 250px; padding: 20px;">
                    <el-skeleton animated>
                      <template #template>
                        <el-skeleton-item variant="h3" style="width: 50%" />
                        <el-skeleton-item variant="text" style="margin-top: 16px; width: 80%" />
                        <el-skeleton-item variant="text" style="margin-top: 16px; width: 60%" />
                        <el-skeleton-item variant="text" style="margin-top: 16px; width: 40%" />
                        <div style="margin-top: 30px; display: flex; justify-content: space-between;">
                          <el-skeleton-item variant="text" style="width: 30%" />
                          <el-skeleton-item variant="button" style="width: 20%" />
                        </div>
                      </template>
                    </el-skeleton>
                  </div>
                </el-col>
              </el-row>
            </div>
            
            <!-- 活动列表展示 -->
            <template v-else>
              <el-empty v-if="filteredActivities.length === 0" description="暂无符合条件的活动" />
              <el-row v-else :gutter="20">
                <el-col v-for="(activity, index) in filteredActivities" 
                  :key="activity.id" 
                  :xs="24" 
                  :sm="12" 
                  :md="8" 
                  :lg="8"
                  class="activity-col"
                  :style="{ animationDelay: index * 0.1 + 's' }"
                >
                  <el-card shadow="hover" class="activity-card" @click="viewActivityDetail(activity)">
                    <!-- 活动标题和类型 -->
                    <div class="activity-header">
                      <h4>{{ activity.title }}</h4>
                      <el-tag size="small" effect="plain">{{ activity.typeName }}</el-tag>
                    </div>
                    <!-- 活动时间 -->
                    <p class="time">
                      <el-icon><Calendar /></el-icon>
                      {{ formatDateTime(activity.startTime, 'MM-DD HH:mm') }} - {{ formatDateTime(activity.endTime, 'HH:mm') }}
                    </p>
                    <!-- 活动地点 -->
                    <p class="location">
                      <el-icon><Location /></el-icon>
                      {{ activity.location }}
                    </p>
                    <!-- 活动描述 -->
                    <p class="description">{{ activity.description }}</p>
                    <!-- 活动信息（参与人数和状态） -->
                    <div class="activity-info">
                      <div class="participants">
                        <el-icon><User /></el-icon>
                        <span>{{ activity.currentRegistrations || 0 }}/{{ activity.maxRegistrations }}人</span>
                      </div>
                      <el-tag :type="getStatusType(activity.status)" effect="plain" class="activity-status-tag">
                        {{ getStatusText(activity.status) }}
                      </el-tag>
                    </div>
                    <!-- 操作按钮 -->
                    <div class="actions">
                      <el-button
                        v-if="canJoin(activity.status)"
                        :disabled="[0,1,4].includes(getActivityRegisterStatus(activity.id))"
                        type="primary"
                        size="small"
                        :loading="activity.loading"
                        @click.stop="handleRegister(activity, $event)"
                      >
                        <template v-if="[0,1,4].includes(getActivityRegisterStatus(activity.id))">
                          已报名
                        </template>
                        <template v-else>
                          {{ getButtonText(activity.status) }}
                        </template>
                      </el-button>
                      <el-tooltip
                        v-if="[0,1,4].includes(getActivityRegisterStatus(activity.id))"
                        content="您已报名该活动"
                        placement="top"
                      >
                        <span style="color: #f56c6c;">已报名</span>
                      </el-tooltip>

                      <!-- 签到按钮 -->
                      <el-button
                        v-if="activity.status === 2 && [1].includes(getActivityRegisterStatus(activity.id))"
                        type="success"
                        size="small"
                        :loading="activity.checkingIn"
                        :disabled="activity.isCheckedIn"
                        @click.stop="handleCheckIn(activity, $event)"
                      >
                        {{ activity.isCheckedIn ? '已签到' : '签到' }}
                      </el-button>
                    </div>
                  </el-card>
                </el-col>
              </el-row>
              
              <!-- 分页控件 -->
              <div class="pagination-container" v-if="totalActivities > activityPageSize">
                <el-pagination
                  v-model:current-page="activityCurrentPage"
                  v-model:page-size="activityPageSize"
                  :page-sizes="[9, 18, 27, 36]"
                  layout="total, sizes, prev, pager, next, jumper"
                  :total="totalActivities"
                  @size-change="handleActivitySizeChange"
                  @current-change="handleActivityPageChange"
                />
              </div>
            </template>
          </el-tab-pane>

          <!-- 我的活动报名标签页 -->
          <el-tab-pane label="我的活动报名" name="myActivities">
            <!-- 加载状态显示 -->
            <div v-if="myActivitiesLoading" class="loading-container">
              <el-row :gutter="20">
                <el-col v-for="i in 3" :key="i" :xs="24" :sm="12" :md="8" :lg="8">
                  <div class="skeleton-card" style="width: 100%; height: 200px; padding: 20px; margin-bottom: 20px;">
                    <el-skeleton animated>
                      <template #template>
                        <el-skeleton-item variant="h3" style="width: 50%; margin-bottom: 15px;" />
                        <el-skeleton-item variant="text" style="width: 80%; margin-bottom: 15px;" />
                        <el-skeleton-item variant="text" style="width: 60%; margin-bottom: 15px;" />
                        <div style="display: flex; justify-content: space-between; margin-top: 20px;">
                          <el-skeleton-item variant="button" style="width: 80px;" />
                          <el-skeleton-item variant="button" style="width: 80px;" />
                        </div>
                      </template>
                    </el-skeleton>
                  </div>
                </el-col>
              </el-row>
            </div>
            
            <!-- 数据为空时的提示 -->
            <el-empty v-else-if="myActivitiesList.length === 0" :description="emptyText">
              <template #extra>
                <el-button v-if="emptyText === '请先登录'" type="primary" @click="$router.push('/login')">
                  去登录
                </el-button>
                <el-button v-else-if="emptyText.includes('加载失败')" type="primary" @click="fetchUserActivities()">
                  重新加载
                </el-button>
                <el-button v-else type="primary" @click="activeTab = 'activities'">
                  去浏览活动
                </el-button>
              </template>
            </el-empty>
            
            <!-- 活动报名列表卡片式布局 -->
            <div v-else class="my-activities-container">
              <el-card 
                v-for="(item, index) in myActivitiesList" 
                :key="item.id" 
                class="registration-card"
                style="margin-bottom: 15px; background-color: #fff;"
              >
                <!-- 卡片内容 -->
                <div style="padding: 10px;">
                  <h3>{{ item.activityTitle }}</h3>
                  <p>报名时间: {{ formatDateTime(item.registerTime, 'YYYY-MM-DD HH:mm') }}</p>
                  <p>状态: {{ getParticipateStatusText(item.status) }}</p>
                  <div style="display: flex; justify-content: space-between; margin-top: 15px;">
                    <el-button type="primary" size="small" @click="viewActivityDetail({ id: item.activityId })">查看详情</el-button>
                    <div>
                      <!-- 签到按钮 - 仅在活动进行中且报名状态为"已通过"时显示 -->
                      <el-button 
                        v-if="item.activityStatus === 2 && item.status === 1 && !item.isCheckedIn" 
                        type="success" 
                        size="small" 
                        :loading="item.checkingIn"
                        @click="handleCheckInFromMyList(item)"
                      >
                        签到
                      </el-button>
                      <!-- 已签到标记 -->
                      <el-tag v-else-if="item.status === 4" type="success">已签到</el-tag>
                      <!-- 取消报名按钮 -->
                      <el-button 
                        type="danger" 
                        size="small" 
                        :disabled="!canCancel(item.activityStatus, item.status)" 
                        @click="handleCancel(item)"
                      >
                        取消报名
                      </el-button>
                    </div>
                  </div>
                </div>
              </el-card>
            </div>

            <!-- 分页组件 -->
            <div class="pagination-container" v-if="total > myActivityPageSize">
              <el-pagination
                v-model:current-page="myActivityCurrentPage"
                v-model:page-size="myActivityPageSize"
                :page-sizes="[5, 10, 20, 50]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="total"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
              />
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>

    <!-- 滚动到顶部按钮 -->
    <el-backtop :right="20" :bottom="90" />
    
    <!-- 悬浮操作按钮 -->
    
  </div>
</template>

<script setup>
import { useActivityStore } from '@/stores/fore/activityStore'
import { useUserStore } from '@/stores/fore/userStore'
import { Calendar, Location, Refresh, User } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

// 使用路由
const router = useRouter()
const route = useRoute()

// 使用活动和用户store
const activityStore = useActivityStore()
const userStore = useUserStore()

// 活动列表数据
const activityList = computed(() => activityStore.activities)
const loading = computed(() => activityStore.loading)

// 标签页切换
const activeTab = ref('activities')

// 搜索和筛选相关变量
const searchQuery = ref('')
const filterType = ref('')
const filterStatus = ref('')
const activityTypes = ref([
  { value: 1, label: '文化娱乐' },
  { value: 2, label: '健康讲座' },
  { value: 3, label: '体育健身' },
  { value: 4, label: '志愿服务' },
  { value: 5, label: '其他活动' }
])
const STATUS_MAP = {
  0: '筹备中',
  1: '报名中',
  2: '进行中',
  3: '已结束',
  4: '已取消'
}

// 分页相关变量
const activityCurrentPage = ref(1)
const activityPageSize = ref(9)
const totalActivities = ref(0)

// 过滤后的活动列表
const filteredActivities = computed(() => {
  if (!activityList.value || activityList.value.length === 0) return []
  
  let result = [...activityList.value]
  
  // 按名称搜索
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(activity => 
      activity.title && activity.title.toLowerCase().includes(query)
    )
  }
  
  // 按类型筛选
  if (filterType.value) {
    result = result.filter(activity => activity.type === filterType.value)
  }
  
  // 按状态筛选
  if (filterStatus.value !== '' && filterStatus.value !== null && filterStatus.value !== undefined) {
    result = result.filter(activity => activity.status === filterStatus.value)
  }
  
  // 更新总数
  totalActivities.value = result.length
  
  // 分页处理
  const start = (activityCurrentPage.value - 1) * activityPageSize.value
  const end = start + activityPageSize.value
  return result.slice(start, end)
})

// 搜索处理函数
const handleSearch = () => {
  activityCurrentPage.value = 1 // 重置到第一页
}

// 刷新功能
const refreshing = ref(false)

const handleRefresh = async () => {
  refreshing.value = true
  try {
    // 调用获取活动列表的函数，强制刷新
    await fetchActivities(true)
    ElMessage.success('刷新成功')
  } catch (error) {
    console.error('刷新失败:', error)
    ElMessage.error('刷新失败，请稍后重试')
  } finally {
    refreshing.value = false
  }
}

// 筛选处理函数
const handleFilter = () => {
  activityCurrentPage.value = 1 // 重置到第一页
}

// 快速过滤类型
const quickFilterByType = (typeValue) => {
  filterType.value = typeValue;
  activityCurrentPage.value = 1; // 重置到第一页
}

// 分页大小变化处理
const handleActivitySizeChange = (size) => {
  activityPageSize.value = size
  activityCurrentPage.value = 1
}

// 页码变化处理
const handleActivityPageChange = (page) => {
  activityCurrentPage.value = page
}

// 监听标签页切换
watch(activeTab, (newTab) => {
  if (newTab === 'myActivities') {
    // 重置页码，确保从第一页开始
    myActivityCurrentPage.value = 1;
    fetchUserActivities();
  }
});

// 获取活动状态类型
const getStatusType = (status) => {
  const statusMap = {
    0: 'info',     // 筹备中
    1: 'success',  // 报名中
    2: 'primary',  // 进行中
    3: 'warning',  // 已结束
    4: 'danger'    // 已取消
  }
  return statusMap[status] || 'info'
}

// 获取活动状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '筹备中',
    1: '报名中',
    2: '进行中',
    3: '已结束',
    4: '已取消'
  }
  return statusMap[status] || '未知'
}

// 获取活动报名按钮文本
const getButtonText = (status) => {
  const buttonTextMap = {
    0: '即将开始',
    1: '立即报名',
    2: '进行中',
    3: '已结束',
    4: '已取消'
  }
  return buttonTextMap[status] || '查看详情'
}

// 判断是否可以报名
const canJoin = (status) => {
  return status === 1 // 只有报名中的活动可以报名
}

// 数据加载状态跟踪
const dataLoadAttempted = ref(false)

// 获取活动列表
const fetchActivities = async (forceReload = false) => {
  // 如果已经在加载中，避免重复请求
  if (loading.value) {
    return activityList.value;
  }

  // 检查本地缓存
  const cacheKey = 'activity_list_cache';
  const cacheExpiry = 'activity_list_cache_expiry';
  const cacheTime = 5 * 60 * 1000; // 5分钟缓存
  
  const now = new Date().getTime();
  const cachedData = localStorage.getItem(cacheKey);
  const expiryTime = localStorage.getItem(cacheExpiry);
  
  // 如果不是强制刷新，且缓存未过期，使用缓存数据
  if (!forceReload && cachedData && expiryTime && now < parseInt(expiryTime)) {
    try {
      const parsedData = JSON.parse(cachedData);
      if (parsedData && parsedData.length > 0) {
        console.log('使用本地缓存的活动列表数据');
        // 更新本地数据，不使用setActivities
        activities.value = parsedData;
        return parsedData;
      }
    } catch (e) {
      console.error('解析缓存数据出错:', e);
    }
  }

  // 第一次加载或强制刷新
  if (!dataLoadAttempted.value || forceReload) {
    dataLoadAttempted.value = true;

    try {
      // 调用 store 中的方法，强制刷新
      console.log('调用 activityStore.fetchActivities...');
      const result = await activityStore.fetchActivities(true);
      console.log('activityStore.fetchActivities 结果:', result);
      
      // 如果结果为空，可能是store中的数据尚未初始化
      if (!result || result.length === 0) {
        console.warn('活动列表为空，请检查store中的数据初始化逻辑');
      } else {
        // 缓存结果
        localStorage.setItem(cacheKey, JSON.stringify(result));
        localStorage.setItem(cacheExpiry, (now + cacheTime).toString());
      }
      
      return result;
    } catch (err) {
      console.error('加载活动列表失败:', err);
      // 加载失败后允许再次尝试
      dataLoadAttempted.value = false;
      ElMessage.error('加载活动列表失败，请刷新页面重试');
      return [];
    }
  } else {
    return activityList.value;
  }
};

// 我的活动报名列表数据
const myActivitiesLoading = ref(false) // 我的活动报名列表加载状态
const myActivitiesList = ref([]) // 我的活动报名列表数据
const myActivityCurrentPage = ref(1) // 当前页码
const myActivityPageSize = ref(10) // 每页显示条数
const total = ref(0) // 总记录数
const emptyText = ref('暂无报名记录') // 无数据时显示的文本

/**
 * 获取报名状态文字
 * @param {number} status - 报名状态码
 * @returns {string} 报名状态文本
 */
const getParticipateStatusText = (status) => {
  const statusTexts = {
    0: '待审核',
    1: '已通过',
    2: '已拒绝',
    3: '已取消',
    4: '已签到'
  };
  // 尝试使用状态码获取文本，如果找不到则尝试使用statusName，最后返回未知状态
  return statusTexts[status] || `未知状态(${status})`;
}

/**
 * 获取报名状态类型（用于标签颜色）
 * @param {number} status - 报名状态码
 * @returns {string} 报名状态对应的类型
 */
const getParticipateStatusType = (status) => {
  const types = {
    0: 'info',     // 待审核 - 灰色
    1: 'success',  // 已通过 - 绿色
    2: 'danger',   // 已拒绝 - 红色
    3: 'warning',  // 已取消 - 黄色
    4: 'primary'   // 已签到 - 蓝色
  }
  return types[status] || 'info'
}

/**
 * 判断是否可以取消报名
 * @param {number} activityStatus - 活动状态码
 * @param {number} participateStatus - 报名状态码
 * @returns {boolean} 是否可以取消报名
 */
const canCancel = (activityStatus, participateStatus) => {
  // 使用传入的参数或者从row中获取
  const actStatus = activityStatus?.activityStatus || activityStatus;
  const partStatus = participateStatus?.status || participateStatus;
  
  // 只有活动状态为"筹备中"或"报名中"，且报名状态为"待审核"或"已通过"时才能取消
  // 已签到的报名不能取消
  return (actStatus === 0 || actStatus === 1) &&
         (partStatus === 0 || partStatus === 1) &&
         partStatus !== 4;
}

/**
 * 格式化日期时间
 * @param {string|Date} date - 日期时间
 * @param {string} format - 格式化模板
 * @returns {string} 格式化后的日期时间字符串
 */
const formatDateTime = (date, format = 'YYYY-MM-DD HH:mm:ss') => {
  if (!date) return ''

  const d = new Date(date)
  if (isNaN(d.getTime())) return ''

  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')

  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
}

/**
 * 获取活动报名状态
 * @param {number|string} activityId - 活动ID
 * @returns {number|null} 报名状态码，未报名返回null
 */
const getActivityRegisterStatus = (activityId) => {
  // 优先从我的活动报名列表中找
  if (Array.isArray(myActivitiesList.value) && myActivitiesList.value.length > 0) {
    // 尝试多种可能的ID字段
    const record = myActivitiesList.value.find(item => 
      item.activityId === activityId || 
      String(item.activityId) === String(activityId)
    );
    
    if (record) {
      return record.status || 0;
    }
  }
  
  return null;
};

/**
 * 处理活动报名
 * @param {Object} activity - 活动对象
 */
const handleRegister = async (activity, event) => {
  // 阻止事件冒泡，防止触发卡片点击事件
  event?.stopPropagation();

  console.log('点击报名按钮 - 活动信息:', {
    id: activity.id,
    title: activity.title,
    status: activity.status
  });

  // 如果已经在处理中，不重复处理
  if (activity.loading) {
    console.log('报名请求正在处理中，请勿重复点击');
    return;
  }

  // 获取用户信息
  const userInfo = userStore.userInfo || JSON.parse(localStorage.getItem('userInfo') || '{}');

  if (!userInfo || !userInfo.userId) {
    console.warn('用户未登录，无法报名');
    ElMessage.warning('请先登录后再报名活动');
    return;
  }
  
  console.log('用户信息:', {
    userId: userInfo.userId,
    username: userInfo.username,
    roles: userInfo.roles,
    roleId: userInfo.roleId || localStorage.getItem('roleId')
  });

  // 如果已经报名，则不继续
  const registerStatus = getActivityRegisterStatus(activity.id);
  if ([0, 1, 4].includes(registerStatus)) {
    console.log('用户已报名，无需重复报名, 状态:', registerStatus);
    ElMessage.info('您已经报名了此活动');
    return;
  }

  try {
    // 设置加载状态
    activity.loading = true;
    
    console.log('调用报名API前...');
    
    // 确保活动ID是数字类型
    const activityToRegister = {
      ...activity,
      id: Number(activity.id)
    };

    // 调用store的报名方法
    const result = await activityStore.registerActivityAction(activityToRegister);
    
    console.log('报名API调用结果:', result);
    
    if (result) {
      ElMessage.success('活动报名成功');
      
      // 报名成功后，立即刷新用户活动列表
      await fetchUserActivities();
      
      // 然后刷新活动列表
      await fetchActivities(true);
    } else {
      console.warn('报名API返回失败');
      ElMessage.error('报名失败，请稍后重试');
    }
  } catch (error) {
    console.error('报名过程中发生错误:', error);
    
    // 提取更详细的错误信息
    let errorMessage = '报名失败，请稍后重试';
    
    if (typeof error === 'object') {
      if (error.message) {
        errorMessage = error.message;
      } else if (error.response && error.response.data) {
        const responseData = error.response.data;
        errorMessage = responseData.message || responseData.msg || errorMessage;
      }
    }
    
    ElMessage.error(errorMessage);
  } finally {
    // 无论成功失败，都重置loading状态
    activity.loading = false;
  }
};

/**
 * 获取用户报名的活动列表
 */
const fetchUserActivities = async () => {
  if (!userStore.isLoggedIn) {
    emptyText.value = '请先登录';
    myActivitiesList.value = [];
    myActivitiesLoading.value = false;
    return;
  }

  // 设置加载状态
  myActivitiesLoading.value = true;
  emptyText.value = '';

  try {
    // 通过 store 获取用户报名的活动列表
    const result = await activityStore.fetchUserRegisteredActivities({
      pageNum: myActivityCurrentPage.value,
      pageSize: myActivityPageSize.value
    });

    // 处理返回结果
    if (result && result.records) {
      // 将API返回的数据赋值给列表
      myActivitiesList.value = result.records;
      
      // 尝试补充活动详情信息
      if (myActivitiesList.value.length > 0) {
        const activityDetailsPromises = myActivitiesList.value.map(async (item) => {
          try {
            // 获取活动详情
            const activityDetail = await activityStore.getActivityDetailAction(item.activityId);
            if (activityDetail) {
              // 找到对应的列表项并补充详情
              const index = myActivitiesList.value.findIndex(i => i.id === item.id);
              if (index !== -1) {
                // 补充地点信息
                myActivitiesList.value[index].location = activityDetail.location || '暂无地点信息';
                // 补充其他可能缺失的信息
                if (!myActivitiesList.value[index].activityStartTime) {
                  myActivitiesList.value[index].activityStartTime = activityDetail.startTime;
                }
                if (!myActivitiesList.value[index].activityEndTime) {
                  myActivitiesList.value[index].activityEndTime = activityDetail.endTime;
                }
                // 补充活动状态
                myActivitiesList.value[index].activityStatus = activityDetail.status;
                
                // 如果活动正在进行中且用户报名已通过，获取签到状态
                if (activityDetail.status === 2 && myActivitiesList.value[index].status === 1) {
                  try {
                    const checkInStatus = await activityStore.getCheckInStatusAction(item.activityId);
                    // 更新签到状态
                    if (checkInStatus && checkInStatus.isCheckedIn) {
                      myActivitiesList.value[index].isCheckedIn = true;
                      myActivitiesList.value[index].status = 4; // 更新为已签到状态
                    } else {
                      myActivitiesList.value[index].isCheckedIn = false;
                    }
                  } catch (error) {
                    // 获取签到状态失败，默认为未签到
                    myActivitiesList.value[index].isCheckedIn = false;
                  }
                }
                
                // 添加加载完成标记
                myActivitiesList.value[index].detailLoaded = true;
              }
            }
          } catch (err) {
            // 错误处理
          }
        });
        
        // 等待所有活动详情获取完成
        await Promise.allSettled(activityDetailsPromises);
      }
      
      total.value = result.total || 0;
      emptyText.value = result.records.length === 0 ? '暂无报名记录' : '';
    } else {
      myActivitiesList.value = [];
      total.value = 0;
      emptyText.value = '暂无报名记录';
    }
  } catch (error) {
    myActivitiesList.value = [];
    total.value = 0;
    emptyText.value = '加载失败，请刷新重试';
    ElMessage.error('获取活动报名列表失败，请刷新重试');
  } finally {
    myActivitiesLoading.value = false;
  }
}

/**
 * 处理分页大小变更
 * @param {number} size - 新的页大小
 */
const handleSizeChange = (size) => {
  myActivityPageSize.value = size;
  fetchUserActivities();
}

/**
 * 处理页码变更
 * @param {number} page - 新的页码
 */
const handleCurrentChange = (page) => {
  myActivityCurrentPage.value = page;
  fetchUserActivities();
}

/**
 * 处理取消报名
 * @param {Object} activity - 活动对象
 */
const handleCancel = async (activity) => {
  const userInfo = userStore.userInfo || JSON.parse(localStorage.getItem('userInfo') || '{}');
  if (!userInfo || !userInfo.userId) {
    ElMessage.warning('请先登录');
    return;
  }

  // 获取老人ID
  const elderId = userInfo.elderInfo?.id || userInfo.userId;

  try {
    // 显示确认对话框
    await ElMessageBox.confirm(`确认取消报名活动"${activity.activityTitle || activity.title}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });

    // 调用API取消报名
    const success = await activityStore.cancelActivityRegistrationAction(activity.id, elderId);
    if (success) {
      // 刷新列表
      ElMessage.success('取消报名成功');
      fetchUserActivities();
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消报名失败:', error);
      ElMessage.error('取消报名失败');
    }
  }
}

/**
 * 跳转到活动详情页
 * @param {Object} activity - 活动对象
 */
const viewActivityDetail = (activity) => {
  router.push(`/home/activity/${activity.id}`);
}

// 活动签到相关
const handleCheckIn = async (activity, event) => {
  // 阻止事件冒泡，防止触发卡片点击事件
  event?.stopPropagation();

  try {
    // 设置签到中状态
    activity.checkingIn = true;

    // 调用签到API
    const success = await activityStore.selfCheckInAction(activity);

    if (success) {
      ElMessage.success('签到成功');
      activity.isCheckedIn = true;

      // 刷新我的活动列表
      await fetchActivities(true);
    }
  } catch (error) {
    ElMessage.error(error.message || '签到失败，请稍后重试');
  } finally {
    activity.checkingIn = false;
  }
}

// 从我的活动列表跳转到签到
const handleCheckInFromMyList = async (item) => {
  if (item.activityStatus !== 2) {
    ElMessage.warning('活动尚未开始，无法签到');
    return;
  }
  
  if (item.isCheckedIn || item.status === 4) {
    ElMessage.info('您已经完成签到');
    return;
  }
  
  try {
    // 设置签到中状态
    item.checkingIn = true;
    
    // 构造签到所需的活动对象
    const activity = {
      id: item.activityId
    };
    
    // 调用签到API
    const success = await activityStore.selfCheckInAction(activity);
    
    if (success) {
      ElMessage.success('签到成功');
      item.isCheckedIn = true;
      item.status = 4; // 更新状态为已签到
      
      // 刷新列表数据
      await fetchUserActivities();
    }
  } catch (error) {
    ElMessage.error(error.message || '签到失败，请稍后重试');
  } finally {
    item.checkingIn = false;
  }
};

/**
 * 强制刷新我的活动报名列表
 */
const forceRefreshMyActivities = async () => {
  try {
    // 清空列表，确保重新加载
    myActivitiesList.value = [];
    // 重新加载数据
    await fetchUserActivities();
    ElMessage.success('刷新成功');
  } catch (error) {
    ElMessage.error('刷新失败，请稍后重试');
  }
}

// 组件挂载时初始化数据
onMounted(async () => {
  try {
    // 获取活动列表
    const result = await fetchActivities(true);
  } catch (err) {
    ElMessage.error('加载活动列表失败，请刷新页面重试');
  }
  
  // 如果用户已登录，获取用户活动报名列表
  const userInfo = userStore.userInfo || JSON.parse(localStorage.getItem('userInfo') || '{}');
  if (userInfo && userInfo.userId) {
    await fetchUserActivities();
    
    // 检查URL中是否有tab参数
    const tabParam = route.query.tab;
    if (tabParam === 'myActivities') {
      // URL中指定了my-activities标签页，设置标签页
      activeTab.value = 'myActivities';
    }
  }
});
</script>

<style scoped>
/* 基础样式 */
.activity-view-container {
  padding: 20px;
}

.content-card {
  margin-bottom: 20px;
}

.loading-container {
  padding: 20px;
}

/* 搜索过滤样式 */
.search-filter-container {
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: center;
}

.search-input {
  min-width: 200px;
  max-width: 300px;
  margin-right: 10px;
}

.filter-select {
  min-width: 120px;
  margin-right: 10px;
}

.refresh-button {
  transition: all 0.3s;
}

.refresh-button:hover {
  transform: rotate(180deg);
}

@media (max-width: 768px) {
  .search-filter-container {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-input, .filter-select {
    width: 100%;
    max-width: none;
    margin-right: 0;
    margin-bottom: 10px;
  }
}

/* 骨架屏卡片样式 */
.skeleton-card {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  transition: all 0.3s;
  animation: pulse 1.5s infinite alternate;
}

/* 快速过滤按钮组样式 */
.quick-filter-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
  padding: 8px 0;
  border-bottom: 1px solid #f2f2f2;
}

.quick-filter-btn {
  transition: all 0.3s ease;
}

.quick-filter-btn:not(.el-button--primary):hover {
  color: #409eff;
  border-color: #c6e2ff;
  background-color: #ecf5ff;
}

@keyframes pulse {
  0% {
    opacity: 0.6;
  }
  100% {
    opacity: 1;
  }
}

:root.dark .skeleton-card {
  background-color: #1a1a1a;
  border-color: #333;
}

/* 活动卡片样式 */
.activity-card {
  height: 100%;
  margin-bottom: 20px;
  transition: all 0.4s ease;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  border-radius: 12px;
  border-top: 3px solid #f0f0f0;
}

.activity-col {
  animation: fadeInUp 0.5s ease-out forwards;
  opacity: 0;
  transform: translateY(20px);
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.activity-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15);
  border-top: 3px solid #67c23a;
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.activity-header h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.time, .location {
  display: flex;
  align-items: center;
  margin: 8px 0;
  font-size: 14px;
  color: #666;
}

.time .el-icon, .location .el-icon {
  margin-right: 5px;
  font-size: 16px;
}

.description {
  margin: 10px 0;
  font-size: 14px;
  color: #666;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.activity-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.participants {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #666;
}

.participants .el-icon {
  margin-right: 5px;
}

.activity-status-tag {
  font-size: 12px;
}

.actions {
  margin-top: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 表格样式 */
.activity-title {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.time-info, .location-info {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #666;
}

.participate-status-tag {
  font-size: 12px;
}

/* 分页样式 */
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 评价表单样式 */
.rating-container {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.evaluation-form {
  width: 100%;
}

/* 暗色主题样式 */
:root.dark .activity-card {
  background-color: #1a1a1a;
  border-color: #333;
}

:root.dark .activity-header h4 {
  color: #fff;
}

:root.dark .time,
:root.dark .location,
:root.dark .description {
  color: #bbb;
}

:root.dark .activity-status {
  background-color: #333;
  color: #fff;
}

:root.dark .participants {
  color: #bbb;
}

/* 悬浮操作按钮样式 */
.fab-container {
  display: none;
}

.fab-button {
  display: none;
}

/* 我的活动报名标签页样式 */
.my-activities-container {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  width: 100%;
  position: relative;
  padding: 10px;
  /* 确保容器有最小高度 */
  min-height: 200px;
  background-color: rgba(0, 0, 255, 0.05);
}

.registration-card {
  width: 100%;
  padding: 16px;
  margin-bottom: 15px;
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.4s ease;
  animation: fadeInUp 0.5s ease-out forwards !important;
  opacity: 0;
  border-left: 4px solid #e0e0e0;
  position: relative;
  overflow: hidden;
  /* 确保卡片可见 */
  background-color: #ffffff;
  z-index: 1;
  display: block;
}

@media (min-width: 768px) {
  .registration-card {
    width: calc(50% - 20px);
  }
}

@media (min-width: 1200px) {
  .registration-card {
    width: calc(33.33% - 20px);
  }
}

.registration-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.12);
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.registration-card.data-loaded {
  animation: highlightPulse 2s forwards;
}

.registration-header {
  display: flex;
  flex-direction: column;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px dashed #ebeef5;
}

@media (min-width: 576px) {
  .registration-header {
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
  }
}

.activity-title-section {
  display: flex;
  flex-direction: column;
  margin-bottom: 8px;
}

@media (min-width: 576px) {
  .activity-title-section {
    margin-bottom: 0;
  }
}

.activity-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 5px;
}

.activity-status-tags {
  display: flex;
  gap: 5px;
}

.registration-time {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #666;
}

.registration-time .el-icon {
  margin-right: 5px;
}

.registration-body {
  margin-top: 10px;
  margin-bottom: 16px;
}

.registration-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

@media (min-width: 768px) {
  .registration-info {
    flex-direction: row;
    flex-wrap: wrap;
  }
  
  .info-item {
    width: calc(50% - 5px);
  }
}

.info-item {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #666;
}

.info-item .el-icon {
  margin-right: 5px;
  color: #409eff;
}

.check-in-status {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
}

.registration-footer {
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding-top: 12px;
  border-top: 1px dashed #ebeef5;
}

.status-pending {
  border-left: 4px solid #e6a23c;
  background: linear-gradient(to right, rgba(230, 162, 60, 0.05), transparent 20%);
}

.status-approved {
  border-left: 4px solid #67c23a;
  background: linear-gradient(to right, rgba(103, 194, 58, 0.05), transparent 20%);
}

@keyframes highlightPulse {
  0% {
    border-color: #67c23a;
    box-shadow: 0 0 5px rgba(103, 194, 58, 0.3);
  }
  50% {
    border-color: #67c23a;
    box-shadow: 0 0 15px rgba(103, 194, 58, 0.8);
  }
  100% {
    border-color: #e0e0e0;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  }
}

/* 热门活动轮播图样式 */
.featured-activities {
  margin-bottom: 30px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 16px;
  position: relative;
  padding-left: 12px;
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 18px;
  background: linear-gradient(to bottom, #67c23a, #409eff);
  border-radius: 2px;
}

.featured-activity-card {
  height: 100%;
  border-radius: 8px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
  padding: 20px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.featured-activity-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}

.featured-activity-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 10px;
  color: #303133;
}

.featured-activity-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-size: 14px;
  color: #606266;
}

.featured-activity-info p {
  margin: 0;
  display: flex;
  align-items: center;
}

.featured-activity-info .el-icon {
  margin-right: 5px;
}
</style>