<template>
    <div class="activity-view">
      <el-card class="content-card" shadow="hover">
        <!-- 活动标签页切换 -->
        <el-tabs v-model="activeTab">
          <!-- 社区活动标签页 -->
          <el-tab-pane label="社区活动" name="activities">
            <!-- 加载状态显示 -->
            <div v-if="loading" class="loading-container">
              <el-skeleton :rows="3" animated />
            </div>
            <!-- 活动列表展示 -->
            <el-row v-else :gutter="20">
              <el-col :span="8" v-for="activity in activities" :key="activity.id">
                <el-card shadow="hover" class="activity-card">
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
                    <el-tag :type="getStatusType(activity.status)" effect="dark">
                      {{ getStatusText(activity.status) }}
                    </el-tag>
                  </div>
                  <!-- 操作按钮 -->
                  <div class="actions">
                    <el-button 
                      :type="canJoin(activity.status) ? 'primary' : ''"
                      :link="!canJoin(activity.status)"
                      size="small"
                      :loading="activity.loading"
                      :disabled="!canJoin(activity.status)"
                      @click="handleRegister(activity)"
                    >
                      {{ getButtonText(activity.status) }}
                    </el-button>
                  </div>
                </el-card>
              </el-col>
            </el-row>
            <!-- 无数据状态 -->
            <el-empty v-if="!loading && activities.length === 0" description="暂无活动" />
          </el-tab-pane>
          
          <!-- 我的活动报名标签页 -->
          <el-tab-pane label="我的活动报名" name="myActivities">
            <!-- 加载状态显示 -->
            <div v-if="myActivitiesLoading" class="loading-container">
              <el-skeleton :rows="3" animated />
            </div>
            <!-- 活动报名列表表格 -->
            <el-table
              v-else
              :data="myActivitiesList"
              style="width: 100%"
              border
              stripe
              :empty-text="emptyText"
            >
              <!-- 活动名称列 -->
              <el-table-column prop="title" label="活动名称" min-width="150">
                <template #default="{ row }">
                  <div class="activity-title">
                    <span>{{ row.title }}</span>
                    <el-tag size="small" effect="plain">{{ row.typeName }}</el-tag>
                  </div>
                </template>
              </el-table-column>
              
              <!-- 活动时间列 -->
              <el-table-column prop="time" label="活动时间" min-width="180">
                <template #default="{ row }">
                  <div class="time-info">
                    <el-icon><Calendar /></el-icon>
                    {{ formatDateTime(row.startTime, 'MM-DD HH:mm') }} - {{ formatDateTime(row.endTime, 'HH:mm') }}
                  </div>
                </template>
              </el-table-column>
              
              <!-- 活动地点列 -->
              <el-table-column prop="location" label="活动地点" min-width="120">
                <template #default="{ row }">
                  <div class="location-info">
                    <el-icon><Location /></el-icon>
                    {{ row.location }}
                  </div>
                </template>
              </el-table-column>
              
              <!-- 活动状态列 -->
              <el-table-column prop="status" label="活动状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getStatusType(row.status)" effect="dark">
                    {{ getStatusText(row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              
              <!-- 报名状态列 -->
              <el-table-column prop="participateStatus" label="报名状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getParticipateStatusType(row.participateStatus)" effect="dark">
                    {{ getParticipateStatusText(row.participateStatus) }}
                  </el-tag>
                </template>
              </el-table-column>
              
              <!-- 操作列 -->
              <el-table-column prop="actions" label="操作" width="120" align="center">
                <template #default="{ row }">
                  <el-button 
                    link
                    type="danger" 
                    size="small" 
                    :disabled="!canCancel(row.status, row.participateStatus)"
                    @click="handleCancel(row)"
                  >
                    取消报名
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            
            <!-- 分页组件 -->
            <div class="pagination-container" v-if="total > 0">
              <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
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
  </template>
  
  <script setup>
  import { ref, computed, onMounted, watch, nextTick, onBeforeUnmount } from 'vue'
  import { useRouter, useRoute } from 'vue-router'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { Calendar, Location, User, List, Plus } from '@element-plus/icons-vue'
  import { useActivityStore } from '@/stores/fore/activityStore'
  import { useUserStore } from '@/stores/fore/useUserStore'
  
  // 使用路由
  const router = useRouter()
  const route = useRoute()
  
  // 添加路由路径计算属性
  const routeBasePath = computed(() => {
    const basePath = route.path.split('/').slice(0, 3).join('/');
    return basePath;
  });
  
  // 添加初始路由路径标记
  const initialRoutePath = ref('');
  
  // 使用store
  const activityStore = useActivityStore() // 活动相关的状态管理
  const userStore = useUserStore() // 用户认证相关的状态管理
  
  // Tab页管理
  const activeTab = ref('activities') // 当前激活的标签页，默认为活动列表
  
  // 从store中获取状态和方法
  const { 
    activities, // 活动列表数据
    loading, // 加载状态
    getStatusText, // 获取活动状态文本
    getStatusType, // 获取活动状态类型（用于标签颜色）
    canJoin, // 判断活动是否可以报名
    getButtonText, // 获取按钮文本
    fetchActivities, // 获取活动列表方法
    registerActivityAction, // 活动报名方法
    getUserRegisteredActivitiesAction, // 获取用户已报名活动方法
    cancelActivityRegistrationAction // 取消报名方法
  } = activityStore
  
  // 添加加载状态追踪变量
  const dataLoadAttempted = ref(false);
  const dataLoadTriggerCount = ref(0);
  
  // 修改 ensureDataLoaded 函数，接受一个强制刷新参数
  const ensureDataLoaded = async (source = 'unknown', forceReload = false) => {
    dataLoadTriggerCount.value++;
    console.log(`[活动数据] 加载触发源: ${source}, 次数: ${dataLoadTriggerCount.value}, 强制刷新: ${forceReload}`);
    
    // 第一次加载、强制刷新、或强制重载
    if (!dataLoadAttempted.value || source === 'force' || forceReload) {
      console.log('[活动数据] 开始获取活动列表数据...');
      dataLoadAttempted.value = true;
      
      // 延迟100ms，确保组件和路由状态已稳定
      await new Promise(resolve => setTimeout(resolve, 100));
      
      try {
        // 调用 store 中的方法，强制刷新
        const result = await fetchActivities(true);
        console.log(`[活动数据] 加载完成，当前活动数量: ${activities.length}`, result);
        return result; // 返回结果方便调用者使用
      } catch (err) {
        console.error('[活动数据] 加载失败:', err);
        // 加载失败后允许再次尝试
        dataLoadAttempted.value = false;
        return []; // 返回空数组表示失败
      }
    } else {
      console.log('[活动数据] 数据已加载，跳过本次加载, 当前数据量:', activities.length);
      return activities; // 返回当前缓存数据
    }
  };
  
  // 我的活动报名列表数据
  const myActivitiesLoading = ref(false) // 我的活动报名列表加载状态
  const myActivitiesList = ref([]) // 我的活动报名列表数据
  const currentPage = ref(1) // 当前页码
  const pageSize = ref(10) // 每页显示条数
  const total = ref(0) // 总记录数
  const emptyText = ref('暂无报名记录') // 无数据时显示的文本
  
  // 报名状态映射
  const PARTICIPATE_STATUS_MAP = {
    0: '待审核',
    1: '已通过',
    2: '已拒绝',
    3: '已取消'
  }
  
  /**
   * 获取报名状态文字
   * @param {number} status - 报名状态码
   * @returns {string} 报名状态文本
   */
  const getParticipateStatusText = (status) => {
    return PARTICIPATE_STATUS_MAP[status] || '未知状态'
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
      3: 'warning'   // 已取消 - 黄色
    }
    return types[status] || 'default'
  }
  
  /**
   * 判断是否可以取消报名
   * @param {number} activityStatus - 活动状态码
   * @param {number} participateStatus - 报名状态码
   * @returns {boolean} 是否可以取消报名
   */
  const canCancel = (activityStatus, participateStatus) => {
    // 只有活动状态为"筹备中"或"报名中"，且报名状态为"待审核"或"已通过"时才能取消
    return (activityStatus === 0 || activityStatus === 1) && 
           (participateStatus === 0 || participateStatus === 1)
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
   * 处理活动报名
   * @param {Object} activity - 活动对象
   */
  const handleRegister = async (activity) => {
    console.log('----活动报名调试信息开始----');
    // 检查用户是否登录
    const userInfo = userStore.userInfo || JSON.parse(localStorage.getItem('userInfo') || '{}');
    const isLoggedIn = userStore.isLoggedIn || localStorage.getItem('isLoggedIn') === 'true';
    
    console.log('当前用户信息:', userInfo);
    console.log('登录状态:', isLoggedIn);
    console.log('本地存储role:', localStorage.getItem('role'));
    console.log('本地存储roleId:', localStorage.getItem('roleId'));
    console.log('本地存储userId:', localStorage.getItem('userId'));
    
    if (!isLoggedIn && !userInfo.id && !userInfo.userId) {
      console.log('----活动报名调试信息结束：用户未登录----');
      ElMessage.warning('请先登录后再报名活动');
      return;
    }
    
    // 获取报名活动所需的老人ID
    try {
      // 直接调用store的报名方法，让其内部处理老人ID获取
      console.log('准备报名活动:', activity);
      
      // 为报名活动添加额外信息，帮助调试
      const activityWithDebugInfo = {
        ...activity,
        _debug: {
          userInfo: userInfo,
          localStorageRole: localStorage.getItem('role'),
          localStorageUserId: localStorage.getItem('userId')
        }
      };
      
      const success = await registerActivityAction(activityWithDebugInfo);
      console.log('报名活动结果:', success);
      
      if (success && activeTab.value === 'myActivities') {
        // 如果在"我的活动"标签页，报名成功后刷新列表
        fetchUserActivities();
      }
      console.log('----活动报名调试信息结束：操作完成----');
    } catch (error) {
      console.error('报名活动过程中发生错误:', error);
      ElMessage.error('报名活动过程中发生错误，请稍后重试');
      console.log('----活动报名调试信息结束：发生错误----');
    }
  }
  
  /**
   * 获取用户报名的活动列表
   */
  const fetchUserActivities = async () => {
    myActivitiesLoading.value = true;
    emptyText.value = '加载中...';
    
    try {
      // 获取用户信息
      const userInfo = userStore.userInfo || JSON.parse(localStorage.getItem('userInfo') || '{}');
      
      // 确定老人ID
      let elderId = null;
      
      // 从不同来源尝试获取老人ID
      // 1. 如果用户是老人，直接使用用户ID
      if (userInfo.roles && userInfo.roles.includes('elder')) {
        elderId = userInfo.userId || userInfo.id;
      } 
      // 2. 如果是家属且绑定了老人
      else if (userInfo.roles && userInfo.roles.includes('kin') && userInfo.bindElder) {
        elderId = userInfo.bindElder.id;
      }
      // 3. 根据本地存储的角色判断
      else {
        const role = localStorage.getItem('role');
        if (role === '1') { // 老人角色
          elderId = userInfo.userId || userInfo.id || localStorage.getItem('userId');
        } else if (role === '2') { // 家属角色
          const bindElderStr = localStorage.getItem('bindElder');
          if (bindElderStr) {
            try {
              const bindElder = JSON.parse(bindElderStr);
              elderId = bindElder.id;
            } catch (error) {
              console.error('解析bindElder失败:', error);
            }
          }
        }
      }
      
      console.log('用户信息:', userInfo);
      console.log('确定的老人ID:', elderId);
      
      // 检查是否获取到老人ID
      if (!elderId) {
        emptyText.value = '无法确定老人身份，请确认您是老人或已绑定老人';
        myActivitiesLoading.value = false;
        return;
      }
      
      // 设置查询参数
      const params = {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        userId: elderId // 使用老人ID作为查询参数（API使用userId参数，但实际上是老人ID）
      };
      
      console.log('请求参数:', params); // 添加日志，方便调试
      
      // 调用API获取数据
      const result = await getUserRegisteredActivitiesAction(params);
      
      // 处理返回结果
      if (result && result.records) {
        myActivitiesList.value = result.records;
        total.value = result.total;
        emptyText.value = '暂无报名记录';
      } else {
        myActivitiesList.value = [];
        total.value = 0;
        emptyText.value = '暂无报名记录';
      }
    } catch (error) {
      console.error('获取用户活动报名列表失败:', error);
      ElMessage.error('获取报名记录失败，请稍后重试');
      emptyText.value = '加载失败，请刷新重试';
      myActivitiesList.value = [];
      total.value = 0;
    } finally {
      myActivitiesLoading.value = false;
    }
  }
  
  /**
   * 处理取消报名
   * @param {Object} activity - 活动对象
   */
  const handleCancel = (activity) => {
    // 显示确认对话框
    ElMessageBox.confirm(
      `确定要取消报名"${activity.title}"活动吗？`,
      '取消报名',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(async () => {
      try {
        // 获取用户信息，确定老人ID
        const userInfo = userStore.userInfo || JSON.parse(localStorage.getItem('userInfo') || '{}');
        let elderId = null;
        
        // 确定老人ID的逻辑
        if (userInfo.roles && userInfo.roles.includes('elder')) {
          elderId = userInfo.userId || userInfo.id;
        } else if (userInfo.roles && userInfo.roles.includes('kin') && userInfo.bindElder) {
          elderId = userInfo.bindElder.id;
        } else {
          // 从localStorage获取角色和ID
          const role = localStorage.getItem('role');
          if (role === '1') { // 老人角色
            elderId = userInfo.userId || userInfo.id || localStorage.getItem('userId');
          } else if (role === '2') { // 家属角色
            const bindElderStr = localStorage.getItem('bindElder');
            if (bindElderStr) {
              const bindElder = JSON.parse(bindElderStr);
              elderId = bindElder.id;
            }
          }
        }
        
        if (!elderId) {
          ElMessage.warning('无法确定老人身份，请确认您是老人或已绑定老人');
          return;
        }
        
        // 调用API取消报名
        const success = await cancelActivityRegistrationAction(activity.id, elderId);
        if (success) {
          // 刷新列表
          fetchUserActivities();
          
          // 如果活动已经在首页中显示，可能也需要刷新首页活动列表
          if (activeTab.value === 'myActivities') {
            fetchActivities();
          }
        }
      } catch (error) {
        console.error('取消报名失败:', error);
        ElMessage.error('取消报名失败，请稍后重试');
      }
    }).catch(() => {
      // 用户点击取消按钮，不做任何操作
    });
  }
  
  /**
   * 处理每页显示条数变化
   * @param {number} size - 每页显示条数
   */
  const handleSizeChange = (size) => {
    pageSize.value = size
    fetchUserActivities()
  }
  
  /**
   * 处理页码变化
   * @param {number} page - 当前页码
   */
  const handleCurrentChange = (page) => {
    currentPage.value = page
    fetchUserActivities()
  }
  
  // 添加数据重置事件处理函数
  const handleResetEvent = () => {
    console.log('[活动页面] 收到重置事件');
    dataLoadAttempted.value = false;
  };
  
  // 修改刷新事件处理函数
  const handleRefreshEvent = (event) => {
    console.log('[活动页面] 收到刷新事件', event.detail);
    // 检查事件是否有详细信息
    const forceRefresh = event.detail?.forceRefresh || false;
    const source = event.detail?.source || 'unknown';
    ensureDataLoaded(`refreshEvent:${source}`, forceRefresh);
  };
  
  // 修改路由监听器
  watch(
    () => route.path,
    async (newPath) => {
      if (newPath.includes('/activity') || newPath.includes('/home/activity')) {
        console.log('[活动页面] 检测到活动页面路由变化:', newPath);
        
        // 判断是否是首次加载或路由真正变化
        const isNewNavigation = initialRoutePath.value !== routeBasePath.value;
        
        await nextTick();
        ensureDataLoaded('routeChange', isNewNavigation);
        
        // 更新初始路径
        initialRoutePath.value = routeBasePath.value;
      }
    },
    { immediate: true }
  );
  
  // 修改 onMounted 钩子
  onMounted(() => {
    console.log('[活动页面] 组件已挂载');
    
    // 记录初始路径
    initialRoutePath.value = routeBasePath.value;
    
    // 每次组件挂载时重置数据加载标记
    dataLoadAttempted.value = false;
    
    // 添加事件监听器
    window.addEventListener('refresh-activity-data', handleRefreshEvent);
    window.addEventListener('activity-data-reset', handleResetEvent);
    
    // 检查URL中是否有tab参数，用于直接定位到指定标签页
    const tabParam = route.query.tab;
    if (tabParam === 'myActivities') {
      // URL中指定了my-activities标签页
      activeTab.value = 'myActivities';
      fetchUserActivities();
    } else {
      // 默认加载活动列表，使用nextTick确保组件已渲染
      nextTick(() => {
        // 强制刷新数据，不考虑缓存
        ensureDataLoaded('onMounted', true);
      });
    }
  });
  
  // 修改 onBeforeUnmount 钩子，确保移除所有事件监听器
  onBeforeUnmount(() => {
    window.removeEventListener('refresh-activity-data', handleRefreshEvent);
    window.removeEventListener('activity-data-reset', handleResetEvent);
  });
  </script>
  
  <style scoped>
  /* 活动视图容器样式 */
  .activity-view {
    width: 100%;
  }
  
  /* 内容卡片样式 */
  .content-card {
    margin-bottom: 20px;
  }
  
  /* 活动卡片样式 */
  .activity-card {
    margin-bottom: 20px;
    transition: all 0.3s ease;
  }
  
  /* 活动卡片悬停效果 */
  .activity-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
  }
  
  /* 活动标题区域样式 */
  .activity-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
  }
  
  /* 活动标题样式 */
  h4 {
    margin: 0;
    color: #2c3e50;
    font-size: 18px;
    flex: 1;
    margin-right: 12px;
  }
  
  /* 时间和地点信息样式 */
  .time, .location, .time-info, .location-info {
    display: flex;
    align-items: center;
    gap: 8px;
    color: #666;
    margin: 8px 0;
  }
  
  /* 活动描述样式 */
  .description {
    margin: 12px 0;
    color: #666;
    font-size: 14px;
    line-height: 1.5;
    height: 42px;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }
  
  /* 活动信息区域样式 */
  .activity-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 12px 0;
  }
  
  /* 参与人数和活动标题样式 */
  .participants, .activity-title {
    display: flex;
    align-items: center;
    gap: 4px;
    color: #666;
    font-size: 14px;
  }
  
  /* 操作按钮区域样式 */
  .actions {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }
  
  /* 加载中容器样式 */
  .loading-container {
    padding: 20px;
  }
  
  /* 分页容器样式 */
  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
  
  /* 骨架屏样式 */
  :deep(.el-skeleton) {
    padding: 20px;
    background: #fff;
    border-radius: 4px;
    margin-bottom: 20px;
  }
  
  /* 标签样式 */
  :deep(.el-tag) {
    margin-left: 8px;
  }
  
  /* 表格样式 */
  :deep(.el-table) {
    border-radius: 4px;
  }
  </style>