import {
  cancelActivityRegistration,
  getActivityDetail,
  getActivityList,
  getActivityStatus,
  getUserRegisteredActivities,
  registerActivity
} from '@/api/fore/activity'
import { useUserStore } from '@/stores/fore/userStore'
import { ElMessage } from 'element-plus'
import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

/**
 * 活动管理状态库
 * 负责管理社区活动的状态和相关操作
 */
export const useActivityStore = defineStore('activity', () => {
  // 引入用户状态
  const userStore = useUserStore()
  
  // 状态定义
  const activities = ref([]) // 活动列表数据
  const loading = ref(false) // 加载状态
  
  /**
   * 活动状态码映射表
   * 用于将状态码转换为对应的文本描述
   */
  const STATUS_MAP = {
    0: '筹备中',
    1: '报名中',
    2: '进行中',
    3: '已结束',
    4: '已取消'
  }
  
  // 计算属性和工具方法
  /**
   * 获取活动状态文本
   * @param {number} status - 活动状态码
   * @returns {string} 状态文本描述
   */
  const getStatusText = (status) => {
    return STATUS_MAP[status] || '未知状态'
  }
  
  /**
   * 获取活动状态对应的类型（用于标签颜色）
   * @param {number} status - 活动状态码
   * @returns {string} 对应的类型
   */
  const getStatusType = (status) => {
    const types = {
      0: 'info',    // 筹备中 - 灰色
      1: 'warning', // 报名中 - 黄色
      2: 'success', // 进行中 - 绿色
      3: 'danger',  // 已结束 - 红色
      4: 'info'     // 已取消 - 灰色
    }
    return types[status] || 'default'
  }
  
  /**
   * 判断活动是否可以报名
   * @param {number} status - 活动状态码
   * @returns {boolean} 是否可以报名
   */
  const canJoin = (status) => {
    return status === 1 // 只有报名中状态可以报名
  }
  
  /**
   * 根据活动状态获取按钮文本
   * @param {number} status - 活动状态码
   * @returns {string} 按钮文本
   */
  const getButtonText = (status) => {
    switch (status) {
      case 0: return '未开始'
      case 1: return '报名'
      case 2: return '进行中'
      case 3: return '已结束'
      case 4: return '已取消'
      default: return '未知'
    }
  }
  
  /**
   * 获取老人ID的辅助函数
   * @returns {string|null} 老人ID或null
   */
  const getElderId = () => {
    try {
      // 获取用户信息和角色判断
      const userInfo = userStore.userInfo;
      const userRole = localStorage.getItem('role');
      
      // 如果有用户信息且角色是老人，返回用户ID
      if (userInfo && userInfo.userId && userRole === 'elder') {
        return userInfo.userId;
      }
      
      // 尝试从localStorage获取userId
      const userId = localStorage.getItem('userId');
      if (userId && userRole === 'elder') {
        return userId;
      }
      
      return null;
    } catch (error) {
      return null;
    }
  };
  
  // Action 方法
  /**
   * 获取活动列表
   * 从服务器获取活动列表数据并更新状态
   * @param {boolean} forceRefresh - 是否强制刷新数据，忽略缓存
   * @returns {Promise<Array>} 活动列表数据
   */
  const fetchActivities = async (forceRefresh = false) => {
    // 日志输出当前状态
      console.log('[活动Store] 获取活动列表:', {
      currentCount: activities.value.length,
      isLoading: loading.value,
      forceRefresh: forceRefresh
    });

      // 如果已有数据且不是强制刷新，直接返回缓存数据
      if (activities.value.length > 0 && !forceRefresh) {
          console.log('[活动Store] 使用缓存数据');
      return activities.value;
    }

      // 如果正在加载中，等待加载完成
      if (loading.value) {
          console.log('[活动Store] 正在加载中，等待加载完成');
          return new Promise(resolve => {
              const unwatch = watch(() => loading.value, (newValue) => {
                  if (!newValue) {
                      unwatch();
                      resolve(activities.value);
                  }
              });
          });
      }

    try {
        loading.value = true;
        console.log('[活动Store] 开始请求活动列表数据');

        // 调用API获取活动列表
        const response = await getActivityList();
        console.log('[活动Store] 获取活动列表响应:', response);

        if (response?.code === 200) {
            // 检查response.data是否是对象，并且包含records属性
            if (response.data && typeof response.data === 'object') {
                const activityData = response.data.records || response.data;
                if (Array.isArray(activityData)) {
                    // 更新状态
                    activities.value = activityData.map(activity => ({
                        ...activity,
                        loading: false // 添加loading状态用于报名操作
                    }));
                    console.log('[活动Store] 活动列表数据更新成功，数量:', activities.value.length);
                    return activities.value;
                }
            }
            console.warn('[活动Store] 活动列表数据格式异常:', response.data);
      } else {
            console.warn('[活动Store] 获取活动列表失败，响应码:', response?.code);
            ElMessage.error(response?.message || '获取活动列表失败');
      }
      return activities.value;
    } catch (error) {
        console.error('[活动Store] 获取活动列表失败:', error);
      ElMessage.error('获取活动列表失败，请稍后重试');
        return activities.value;
    } finally {
        loading.value = false;
    }
  }
  
  /**
   * 活动报名
   * 发起活动报名请求并处理结果
   * @param {Object} activity - 活动对象
   * @returns {boolean} 是否报名成功
   */
  const registerActivityAction = async (activity) => {
    // 检查活动是否可以报名，或者是否正在加载
    if (activity.loading || !canJoin(activity.status)) {
      return false;
    }
    
    // 获取老人ID
    let elderId = getElderId();
    
    // 如果不能获取到有效的老人ID，则无法报名
    if (!elderId) {
      ElMessage.warning('当前用户不是老人身份或未获取到老人信息，无法报名活动');
      return false;
    }
    
    // 标记活动正在加载
    activity.loading = true;
    
    try {
      // 调用API进行活动报名
      const result = await registerActivity(activity.id, elderId);
      
      if (result.code === 0) {
        ElMessage.success('活动报名成功');
        
        // 刷新活动列表
        await fetchActivities();
        
        activity.loading = false;
        return true;
      } else {
        ElMessage.error(result.message || '活动报名失败');
        activity.loading = false;
        return false;
      }
    } catch (error) {
      ElMessage.error(error.message || '活动报名过程中发生错误');
      activity.loading = false;
      return false;
    }
  };
  
  /**
   * 获取活动详情
   * @param {number} activityId - 活动ID
   * @returns {Object|null} 活动详情对象，失败时返回null
   */
  const getActivityDetailAction = async (activityId) => {
    try {
      const response = await getActivityDetail(activityId) // 调用API获取详情
      return response.data // 返回数据
    } catch (error) {
      console.error('获取活动详情失败:', error)
      ElMessage.error('获取活动详情失败，请稍后重试')
      return null
    }
  }
  
  /**
   * 获取用户对特定活动的报名状态
   * @param {number} activityId - 活动ID
   * @returns {Object|null} 状态对象，失败时返回null
   */
  const getActivityStatusAction = async (activityId) => {
    try {
      const response = await getActivityStatus(activityId)
      return response.data
    } catch (error) {
      console.error('获取活动状态失败:', error)
      ElMessage.error('获取活动状态失败，请稍后重试')
      return null
    }
  }
  
  /**
   * 获取用户已报名的活动列表
   * @param {Object} params - 查询参数
   * @param {number} [params.pageNum] - 页码
   * @param {number} [params.pageSize] - 每页大小
   * @returns {Object} 分页数据对象，包含records和total
   */
  const getUserRegisteredActivitiesAction = async (params = {}) => {
    try {
      const res = await getUserRegisteredActivities(params)
      if (res.code === 200) {
        return res.data // 返回数据
      }
      return { records: [], total: 0 } // 失败时返回空数据
    } catch (error) {
      console.error('获取用户已报名活动列表失败', error)
      return { records: [], total: 0 } // 发生错误时返回空数据
    }
  }
  
  /**
   * 取消活动报名
   * @param {number} registerId - 报名记录ID
   * @param {number} [elderId] - 老人ID，如果未提供，会尝试获取
   * @returns {boolean} 是否取消成功
   */
  const cancelActivityRegistrationAction = async (registerId, elderId) => {
    try {
      // 如果未提供elderId，尝试获取
      if (!elderId) {
        elderId = getElderId();
        if (!elderId) {
          ElMessage.warning('无法确定老人身份，请确认您是老人或已绑定老人');
          return false;
        }
      }
      
      console.log(`开始取消活动报名[${registerId}]，老人ID:`, elderId);
      const res = await cancelActivityRegistration(registerId, elderId);
      if (res.code === 200) {
        ElMessage.success('取消报名成功');
        return true; // 取消成功
      }
      ElMessage.error(res.msg || '取消报名失败');
      return false; // 取消失败
    } catch (error) {
      console.error('取消活动报名失败', error);
      ElMessage.error('取消报名失败，请稍后重试');
      return false; // 发生错误时返回失败
    }
  }

    /**
     * 获取用户报名的活动列表
     * @param {Object} params - 分页参数
     * @returns {Promise} 活动列表数据
     */
    const fetchUserRegisteredActivities = async (params) => {
        const userStore = useUserStore();
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

        console.log('[活动Store] 用户信息:', userInfo);
        console.log('[活动Store] 确定的老人ID:', elderId);

        // 检查是否获取到老人ID
        if (!elderId) {
            throw new Error('无法确定老人身份，请确认您是老人或已绑定老人');
        }

        // 设置查询参数
        const queryParams = {
            ...params,
            userId: elderId // 使用老人ID作为查询参数
        };

        console.log('[活动Store] 请求参数:', queryParams);

        // 调用API获取数据
        return await getUserRegisteredActivitiesAction(queryParams);
    }
  
  // 返回状态和方法给组件使用
  return {
    // 状态
    activities,
    loading,
    
    // 计算属性和工具方法
    getStatusText,
    getStatusType,
    canJoin,
    getButtonText,
    
    // Actions
    fetchActivities,
    registerActivityAction,
    getActivityDetailAction,
    getActivityStatusAction,
    getUserRegisteredActivitiesAction,
      cancelActivityRegistrationAction,
      fetchUserRegisteredActivities
  }
}) 