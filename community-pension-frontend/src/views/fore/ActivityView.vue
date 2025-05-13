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
              <el-col v-for="activity in activityList" :key="activity.id" :span="8">
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
                      @click="handleRegister(activity)"
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
                  </div>
                </el-card>
              </el-col>
            </el-row>
            <!-- 无数据状态 -->
            <el-empty v-if="!loading && activityList.length === 0" description="暂无活动"/>
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
              <el-table-column label="活动名称" min-width="150">
                <template #default="{ row }">
                  <div class="activity-title">
                    <span>{{ row.title }}</span>
                    <el-tag size="small" effect="plain">{{ row.typeName }}</el-tag>
                  </div>
                </template>
              </el-table-column>

              <!-- 活动时间列 -->
              <el-table-column label="活动时间" min-width="180">
                <template #default="{ row }">
                  <div class="time-info">
                    <el-icon><Calendar /></el-icon>
                    {{ formatDateTime(row.startTime, 'MM-DD HH:mm') }} - {{ formatDateTime(row.endTime, 'HH:mm') }}
                  </div>
                </template>
              </el-table-column>

              <!-- 活动地点列 -->
              <el-table-column label="活动地点" min-width="120">
                <template #default="{ row }">
                  <div class="location-info">
                    <el-icon><Location /></el-icon>
                    {{ row.location }}
                  </div>
                </template>
              </el-table-column>

              <!-- 活动状态列 -->
              <el-table-column label="活动状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getStatusType(row.status)" effect="plain" class="activity-status-tag">
                    {{ getStatusText(row.status) }}
                  </el-tag>
                </template>
              </el-table-column>

              <!-- 报名状态列 -->
              <el-table-column label="报名状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getParticipateStatusType(row.participateStatus)" effect="plain" class="participate-status-tag">
                    {{ getParticipateStatusText(row.participateStatus) }}
                  </el-tag>
                </template>
              </el-table-column>

              <!-- 操作列 -->
              <el-table-column align="center" label="操作" width="120">
                <template #default="{ row }">
                  <el-button
                    link
                    size="small"
                    type="danger"
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
  import { useActivityStore } from '@/stores/fore/activityStore'
import { useUserStore } from '@/stores/fore/userStore'
import { Calendar, Location, User } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, onBeforeUnmount, onMounted, onUnmounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

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

  // 添加活动列表的计算属性
  const activityList = computed(() => activityStore.activities);

  // Tab页管理
  const activeTab = ref('activities') // 当前激活的标签页，默认为活动列表

  // 监听标签页切换
  watch(activeTab, (newTab) => {
    console.log('标签页切换:', newTab);
    if (newTab === 'myActivities') {
      fetchUserActivities();
    }
  });

  // 从store中获取状态和方法
  const {
    loading, // 加载状态
    getStatusText, // 获取活动状态文本
    getStatusType, // 获取活动状态类型（用于标签颜色）
    canJoin, // 判断活动是否可以报名
    getButtonText, // 获取按钮文本
    fetchActivities, // 获取活动列表方法
    registerActivityAction, // 活动报名方法
    getUserRegisteredActivitiesAction, // 获取用户已报名活动方法
    cancelActivityRegistrationAction,
    fetchUserRegisteredActivities // 添加从 store 获取用户活动列表的方法
  } = activityStore

  // 添加加载状态追踪变量
  const dataLoadAttempted = ref(false);
  const dataLoadTriggerCount = ref(0);

  // 修改 ensureDataLoaded 函数，接受一个强制刷新参数
  const ensureDataLoaded = async (source = 'unknown', forceReload = false) => {
    dataLoadTriggerCount.value++;
    console.log(`[活动数据] 加载触发源: ${source}, 次数: ${dataLoadTriggerCount.value}, 强制刷新: ${forceReload}`);

    // 如果正在加载，等待加载完成
    if (loading.value) {
      console.log('[活动数据] 数据正在加载中，等待加载完成');
      // 等待当前加载完成
      await new Promise(resolve => {
        const unwatch = watch(() => loading.value, (newValue) => {
          if (!newValue) {
            unwatch();
            resolve();
          }
        });
      });
      return activityList.value;
    }

    // 第一次加载、强制刷新、或强制重载
    if (!dataLoadAttempted.value || source === 'force' || forceReload) {
      console.log('[活动数据] 开始获取活动列表数据...');
      dataLoadAttempted.value = true;

      try {
        // 调用 store 中的方法，强制刷新
        const result = await fetchActivities(true);
        console.log(`[活动数据] 加载完成，当前活动数量: ${activityList.value.length}`, result);
        return result;
      } catch (err) {
        console.error('[活动数据] 加载失败:', err);
        // 加载失败后允许再次尝试
        dataLoadAttempted.value = false;
        ElMessage.error('加载活动列表失败，请刷新页面重试');
        return [];
      }
    } else {
      console.log('[活动数据] 数据已加载，跳过本次加载, 当前数据量:', activityList.value.length);
      return activityList.value;
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
    return types[status] || 'info'  // 将默认值从 'default' 改为 'info'
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
    // 打印当前获取到的用户信息
    const userInfo = userStore.userInfo || JSON.parse(localStorage.getItem('userInfo') || '{}');
    console.log('【报名前用户信息】', userInfo);
    try {
      // 直接调用store的报名方法，让其内部处理老人ID获取
      console.log('准备报名活动:', activity);

      const success = await registerActivityAction(activity);
      console.log('报名活动结果:', success);

      // 无论在哪个标签页，报名成功后都刷新活动列表
      fetchActivities();
      
      if (activeTab.value === 'myActivities') {
        // 如果在"我的活动"标签页，报名成功后刷新列表
        fetchUserActivities();
      }
    } catch (error) {
      console.error('报名活动过程中发生错误:', error);
      
      // 无论报名成功还是失败，都刷新活动列表
      fetchActivities();
      
      // 如果是因为"已报名"等业务错误，也刷新我的活动列表以更新状态
      if (error.response?.status === 409 || error.response?.status === 500) {
        fetchUserActivities();
      }
      
      // 优先使用后端返回的标准错误结构
      const errorMsg = error.response?.data?.message || 
                       error.response?.data?.msg || 
                       error.message || 
                       '报名活动过程中发生错误，请稍后重试';
      
      ElMessage.error(errorMsg);
      console.log('----活动报名调试信息结束：发生错误----');
    }
  }

  /**
   * 获取用户报名的活动列表
   */
  const fetchUserActivities = async () => {
    console.log('开始获取用户活动报名列表...');
    myActivitiesLoading.value = true;
    emptyText.value = '加载中...';

    try {
      // 获取用户信息
      const userInfo = userStore.userInfo || JSON.parse(localStorage.getItem('userInfo') || '{}');
      console.log('当前用户信息:', userInfo);
      
      // 先检查是否有用户信息
      if (!userInfo || !userInfo.userId) {
        console.warn('未检测到有效的用户信息，跳过获取用户活动列表');
        myActivitiesList.value = [];
        total.value = 0;
        emptyText.value = '请先登录';
        return;
      }

      // 通过 store 获取用户报名的活动列表
      const result = await fetchUserRegisteredActivities({
        pageNum: currentPage.value,
        pageSize: pageSize.value
      });

      console.log('获取到的活动报名列表原始数据:', result);

      // 处理返回结果
      if (result && result.records) {
        // 先获取所有活动的详情
        const activityDetails = await Promise.all(
          result.records.map(async (record) => {
            try {
              const detailResponse = await activityStore.getActivityDetailAction(record.activityId);
              return {
                activityId: record.activityId,
                detail: detailResponse || {}
              };
            } catch (error) {
              console.error(`获取活动${record.activityId}详情失败:`, error);
              return {
                activityId: record.activityId,
                detail: {}
              };
            }
          })
        );

        // 创建活动详情的映射
        const detailMap = activityDetails.reduce((map, item) => {
          map[item.activityId] = item.detail;
          return map;
        }, {});

        // 确保每条记录都包含必要的字段
        myActivitiesList.value = result.records.map(record => {
          console.log('处理单条记录:', record);
          // 获取对应的活动详情
          const activityDetail = detailMap[record.activityId] || {};
          return {
            ...record,
            // 活动基本信息
            title: record.activityTitle || '未知活动',
            // 报名状态
            participateStatus: record.status ?? 0,
            // 活动时间和地点从详情中获取
            startTime: activityDetail.startTime || record.registerTime || '',
            endTime: activityDetail.endTime || record.registerTime || '',
            // 其他信息
            typeName: record.registerTypeName || '其他',
            location: activityDetail.location || '地点待定',
            status: record.status ?? 0,
            // 扩展信息
            registerInfo: `${record.elderName} (${record.registerTypeName})`,
            registerTimeFormatted: formatDateTime(record.registerTime, 'YYYY-MM-DD HH:mm')
          };
        });
        total.value = result.total || 0;
        emptyText.value = myActivitiesList.value.length === 0 ? '暂无报名记录' : '';
        console.log('处理后的活动报名列表:', myActivitiesList.value);
      } else {
        console.warn('返回数据格式不正确:', result);
        myActivitiesList.value = [];
        total.value = 0;
        emptyText.value = '暂无报名记录';
      }
    } catch (error) {
      console.error('获取用户活动报名列表失败:', error);
      myActivitiesList.value = [];
      total.value = 0;
      
      // 判断错误类型并提供更明确的错误信息
      if (error.message && error.message.includes('无法确定老人身份')) {
        emptyText.value = '无法确定老人身份，请确认您是老人或已绑定老人';
        ElMessage.warning(emptyText.value);
      } else if (error.response?.status === 401) {
        emptyText.value = '请先登录后查看';
        ElMessage.warning('请先登录后查看您的报名活动');
      } else {
        emptyText.value = '加载失败，请刷新重试';
        ElMessage.error('获取报名记录失败，请稍后重试');
      }
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
      async (newPath, oldPath) => {
        console.log('[活动页面] 路由变化:', {
          newPath,
          oldPath,
          initialPath: initialRoutePath.value,
          currentBasePath: routeBasePath.value
        });

      if (newPath.includes('/activity') || newPath.includes('/home/activity')) {
        // 强制刷新数据
        await ensureDataLoaded('routeChange', true);
      }
    },
      {immediate: true} // 添加immediate: true，确保首次进入时也触发
  );

  // 修改 onMounted 钩子
  onMounted(async () => {
    console.log('[活动页面] 组件已挂载');

    // 记录初始路径
    initialRoutePath.value = routeBasePath.value;

    // 每次组件挂载时重置数据加载标记
    dataLoadAttempted.value = false;

    // 添加事件监听器
    window.addEventListener('refresh-activity-data', handleRefreshEvent);
    window.addEventListener('activity-data-reset', handleResetEvent);

    // 确保先加载公共活动列表，这不依赖用户登录状态
    await ensureDataLoaded('onMounted', true);

    // 检查URL中是否有tab参数
    const tabParam = route.query.tab;
    if (tabParam === 'myActivities') {
      // URL中指定了my-activities标签页，先设置标签页
      activeTab.value = 'myActivities';
      // 不需要在这里调用fetchUserActivities，因为watch会处理
    } else {
      // 如果不是myActivities标签，且用户已登录，则加载用户活动列表
      try {
        const userInfo = userStore.userInfo || JSON.parse(localStorage.getItem('userInfo') || '{}');
        if (userInfo && userInfo.userId) {
          await fetchUserActivities();
        } else {
          console.log('用户未登录，跳过加载用户活动列表');
        }
      } catch (err) {
        console.error('加载用户活动列表失败', err);
      }
    }
  });

  // 修改 onBeforeUnmount 钩子，确保移除所有事件监听器
  onBeforeUnmount(() => {
    window.removeEventListener('refresh-activity-data', handleRefreshEvent);
    window.removeEventListener('activity-data-reset', handleResetEvent);
  });

  // 修改 onUnmounted 钩子，确保移除所有事件监听器
  onUnmounted(() => {
  });

  // 判断当前活动是否已报名
  const isRegistered = (activityId) => {
    // 从两个数据源检查是否已报名
    const fromMyActivities = myActivitiesList.value?.some(item => item.id === activityId);
    return fromMyActivities;
  };

  // 判断当前活动报名状态（返回报名状态码，未报名返回null）
  const getActivityRegisterStatus = (activityId) => {
    // 优先从我的活动报名列表中找
    if (Array.isArray(myActivitiesList.value)) {
      const record = myActivitiesList.value.find(item => item.id === activityId);
      if (record) {
        return record.participateStatus;
      }
    }
    
    return null;
  };
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
    line-clamp: 2;
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

  /* 暗色主题样式 */
  :root.dark .activity-container {
    background-color: #1f1f1f;
    color: #fff;
  }

  :root.dark .activity-tabs {
    background-color: #2a2a2a;
    border-color: #333;
  }

  :root.dark :deep(.el-tabs__item) {
    color: #aaa;
  }

  :root.dark :deep(.el-tabs__item.is-active) {
    color: #66b1ff;
  }

  :root.dark :deep(.el-tabs__active-bar) {
    background-color: #66b1ff;
  }

  :root.dark .activity-card {
    background-color: #2a2a2a;
    border-color: #333;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.2);
  }

  :root.dark .activity-card:hover {
    box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.3);
  }

  :root.dark .activity-title {
    color: #eee;
  }

  :root.dark .activity-description {
    color: #bbb;
  }

  :root.dark .activity-info {
    color: #999;
  }

  :root.dark .activity-tag {
    background-color: #3a3a3a;
    color: #bbb;
  }

  :root.dark .activity-status {
    background-color: #333;
  }

  :root.dark .activity-status.enrolled {
    background-color: #1d3712;
    color: #85ce61;
  }

  :root.dark .activity-status.available {
    background-color: #213d50;
    color: #79bbff;
  }

  :root.dark .activity-status.full {
    background-color: #3d2c14;
    color: #ebb563;
  }

  :root.dark .activity-status.passed {
    background-color: #2d2d30;
    color: #a6a9ad;
  }

  :root.dark .activity-detail-content {
    background-color: #2a2a2a;
    border-color: #333;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.2);
  }

  :root.dark .detail-section-title {
    color: #eee;
    border-bottom-color: #333;
  }

  :root.dark .activity-attendees {
    background-color: #2a2a2a;
    border-color: #333;
  }

  :root.dark .attendee-item {
    background-color: #333;
    color: #eee;
  }

  :root.dark .attendee-avatar {
    border-color: #444;
  }

  :root.dark .empty-text {
    color: #999;
  }

  :root.dark :deep(.el-pagination) {
    --el-pagination-button-bg-color: #2a2a2a;
    --el-pagination-button-color: #aaa;
    --el-pagination-button-disabled-bg-color: #222;
    --el-pagination-button-disabled-color: #666;
    --el-pagination-hover-color: #66b1ff;
  }

  :root.dark :deep(.el-pagination .el-pager li) {
    background-color: #2a2a2a;
    color: #aaa;
  }

  :root.dark :deep(.el-pagination .el-pager li:hover) {
    color: #66b1ff;
  }

  :root.dark :deep(.el-pagination .el-pager li.is-active) {
    background-color: #66b1ff;
    color: #fff;
  }

  :root.dark :deep(.el-dialog) {
    background-color: #2a2a2a;
    border-color: #333;
  }

  :root.dark :deep(.el-dialog__title) {
    color: #eee;
  }

  :root.dark :deep(.el-form-item__label) {
    color: #bbb;
  }

  :root.dark :deep(.el-input__wrapper) {
    background-color: #333;
    box-shadow: 0 0 0 1px #444 inset;
  }

  :root.dark :deep(.el-input__inner) {
    color: #eee;
  }

  :root.dark :deep(.el-textarea__inner) {
    background-color: #333;
    border-color: #444;
    color: #eee;
  }

  :root.dark :deep(.el-button--primary) {
    --el-button-hover-bg-color: #4a88c7;
    --el-button-hover-border-color: #4a88c7;
  }

  :root.dark :deep(.el-button--default) {
    --el-button-bg-color: #333;
    --el-button-border-color: #444;
    --el-button-hover-bg-color: #444;
    --el-button-hover-border-color: #555;
    --el-button-active-bg-color: #3a3a3a;
    --el-button-active-border-color: #444;
    color: #bbb;
  }

  .activity-status-tag {
    cursor: default;
    pointer-events: none;
    opacity: 0.85;
    font-weight: normal;
  }

  .participate-status-tag {
    cursor: default;
    pointer-events: none;
    opacity: 0.85;
    font-weight: normal;
  }
  </style>