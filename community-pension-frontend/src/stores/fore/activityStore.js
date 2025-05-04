import {defineStore} from 'pinia'
import {ref, watch} from 'vue'
import {
  cancelActivityRegistration,
  getActivityDetail,
  getActivityList,
  getActivityStatus,
  getUserRegisteredActivities,
  registerActivity
} from '@/api/fore/activity'
import {ElMessage} from 'element-plus'
import {useUserStore} from '@/stores/fore/userStore'

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
      // 添加调试日志，打印完整的本地存储信息
      console.log('------获取老人ID调试信息开始------');
      console.log('userStore.userInfo:', userStore.userInfo);
      if (localStorage.getItem('userInfo')) {
        console.log('localStorage.userInfo:', JSON.parse(localStorage.getItem('userInfo')));
      }
      console.log('localStorage.userId:', localStorage.getItem('userId'));
      console.log('localStorage.role:', localStorage.getItem('role'));
      console.log('localStorage.roleName:', localStorage.getItem('roleName'));
      
      // 首先尝试从userStore获取用户信息
      if (userStore.userInfo) {
        console.log('从userStore中获取用户信息:', userStore.userInfo);
        
        // 处理roles可能是数组或字符串的情况
        const isElder = Array.isArray(userStore.userInfo.roles) 
          ? userStore.userInfo.roles.includes('elder') 
          : userStore.userInfo.roles === 'elder' || userStore.userInfo.role === 'elder' || userStore.userInfo.roleId === 1;
        
        // 如果用户是老人，直接使用userId或id作为老人ID
        if (isElder) {
          // 尝试从可能的位置获取老人ID
          const elderId = userStore.userInfo.userId || userStore.userInfo.id;
          console.log('用户是老人，使用ID作为老人ID:', elderId);
          console.log('------获取老人ID调试信息结束------');
          return elderId;
        }
        
        // 如果用户是家属，并且绑定了老人，使用绑定的老人ID
        const isKin = Array.isArray(userStore.userInfo.roles) 
          ? userStore.userInfo.roles.includes('kin') 
          : userStore.userInfo.roles === 'kin' || userStore.userInfo.role === 'kin' || userStore.userInfo.roleId === 2;
        
        if (isKin && userStore.userInfo.bindElder) {
          console.log('用户是家属，使用绑定的老人ID:', userStore.userInfo.bindElder.id);
          console.log('------获取老人ID调试信息结束------');
          return userStore.userInfo.bindElder.id;
        }
      }
      
      // 如果从store中获取失败，尝试从localStorage获取
      const userInfoStr = localStorage.getItem('userInfo');
      const localRole = localStorage.getItem('role');
      
      if (userInfoStr) {
        try {
          const userInfo = JSON.parse(userInfoStr);
          console.log('从localStorage解析的userInfo:', userInfo);
          
          // 从userInfo中获取老人ID（可能存储在userId或id字段中）
          const elderId = userInfo.userId || userInfo.id;
          
          // 检查用户角色，确认是否是老人 - 处理多种可能情况
          const isElderFromUserInfo = 
            // 检查roles数组
            (userInfo.roles && Array.isArray(userInfo.roles) && userInfo.roles.includes('elder')) ||
            // 检查roles字符串
            userInfo.roles === 'elder' ||
            // 检查单一role字段
            userInfo.role === 'elder' ||
            // 检查roleId字段
            userInfo.roleId === 1 ||
            // 检查localStorage中的role
            localRole === '1' || localRole === 1;
          
          if (isElderFromUserInfo && elderId) {
            console.log('从localStorage确认用户是老人，ID:', elderId);
            console.log('------获取老人ID调试信息结束------');
            return elderId;
          }
          
          // 如果用户是家属，并且绑定了老人，使用绑定的老人ID
          const isKinFromUserInfo = 
            // 检查roles数组
            (userInfo.roles && Array.isArray(userInfo.roles) && userInfo.roles.includes('kin')) ||
            // 检查roles字符串
            userInfo.roles === 'kin' ||
            // 检查单一role字段
            userInfo.role === 'kin' ||
            // 检查roleId字段
            userInfo.roleId === 2 ||
            // 检查localStorage中的role
            localRole === '2' || localRole === 2;
          
          if (isKinFromUserInfo && userInfo.bindElder) {
            console.log('从localStorage确认用户是家属，绑定老人ID:', userInfo.bindElder.id);
            console.log('------获取老人ID调试信息结束------');
            return userInfo.bindElder.id;
          }
        } catch (error) {
          console.error('解析localStorage中的userInfo失败:', error);
        }
      }
      
      // 基于本地存储的role和userId直接判断
      const userId = localStorage.getItem('userId');
      
      // 如果localStorage中直接有userId且角色是老人
      if ((localRole === '1' || localRole === 1) && userId) {
        console.log('从localStorage直接确认用户是老人, userId:', userId);
        console.log('------获取老人ID调试信息结束------');
        return userId;
      } else if (userId && !localRole) {
        // 如果有userId但没有明确角色信息，假定为老人（针对某些特殊情况）
        console.log('从localStorage获取到userId但无角色信息，假定为老人:', userId);
        console.log('------获取老人ID调试信息结束------');
        return userId;
      }
      
      // 尝试根据存储在userInfo中的userId
      if (userInfoStr) {
        try {
          const userInfo = JSON.parse(userInfoStr);
          if (userInfo.userId) {
            console.log('从localStorage.userInfo中获取userId:', userInfo.userId);
            console.log('------获取老人ID调试信息结束------');
            return userInfo.userId;
          }
        } catch (error) {
          console.error('再次解析userInfo失败:', error);
        }
      }
      
      // 尝试获取绑定的老人信息
      const bindElderStr = localStorage.getItem('bindElder');
      if (bindElderStr) {
        try {
          const bindElder = JSON.parse(bindElderStr);
          if (bindElder && bindElder.id) {
            console.log('从localStorage.bindElder获取老人ID:', bindElder.id);
            console.log('------获取老人ID调试信息结束------');
            return bindElder.id;
          }
        } catch (error) {
          console.error('解析bindElder数据失败:', error);
        }
      }
      
      console.warn('无法确定老人ID，用户可能不是老人或未绑定老人');
      console.log('------获取老人ID调试信息结束------');
      return null;
    } catch (error) {
      console.error('获取老人ID时出错:', error);
      console.log('------获取老人ID调试信息结束------');
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
    console.log('----活动报名Action开始执行----');
    // 检查调试信息
    const debugInfo = activity._debug;
    if (debugInfo) {
      console.log('收到活动报名调试信息:', debugInfo);
    }
    
    // 检查活动是否可以报名，或者是否正在加载
    if (activity.loading || !canJoin(activity.status)) {
      console.log('活动不可报名状态:', {
        loading: activity.loading,
        status: activity.status,
        canJoin: canJoin(activity.status)
      });
      console.log('----活动报名Action结束：活动不可报名----');
      return false;
    }
    
    // 尝试获取老人ID - 普通逻辑
    let elderId = getElderId();
    
    // 如果普通方式无法获取，尝试从调试信息或本地存储直接获取
    if (!elderId && debugInfo) {
      // 1. 尝试从userInfo中获取
      if (debugInfo.userInfo) {
        elderId = debugInfo.userInfo.userId || debugInfo.userInfo.id;
        if (elderId) {
          console.log('从调试信息userInfo获取elderId:', elderId);
        }
      }
      
      // 2. 尝试从localStorage获取
      if (!elderId && debugInfo.localStorageUserId) {
        elderId = debugInfo.localStorageUserId;
        console.log('从调试信息localStorage.userId获取elderId:', elderId);
      }
    }
    
    // 最后尝试的特殊处理：直接从信息中提取userInfo.userId作为老人ID
    if (!elderId) {
      try {
        const userInfoStr = localStorage.getItem('userInfo');
        if (userInfoStr) {
          const userInfo = JSON.parse(userInfoStr);
          if (userInfo.userId) {
            elderId = userInfo.userId;
            console.log('特殊处理：直接使用userInfo.userId作为elderId:', elderId);
          }
        }
      } catch (error) {
        console.error('尝试获取userInfo.userId失败:', error);
      }
    }
    
    // 如果依然无法获取老人ID，显示错误并返回
    if (!elderId) {
      ElMessage.warning('无法确定老人身份，请确认您是老人或已绑定老人');
      console.log('----活动报名Action结束：无法获取老人ID----');
      return false;
    }
    
    activity.loading = true; // 设置特定活动的加载状态
    try {
      // 调用API进行活动报名，传入老人数据
      console.log(`开始报名活动[${activity.id}]，老人ID:`, elderId);
      await registerActivity(activity.id, { elderId });
      ElMessage.success(`成功报名：${activity.title}`); // 显示成功消息
      // 重新获取活动列表以更新状态
      await fetchActivities();
      console.log('----活动报名Action结束：报名成功----');
      return true; // 报名成功
    } catch (error) {
      console.error('活动报名失败:', error);
      // 显示详细的错误信息，帮助调试
      if (error.response) {
        console.error('错误响应数据:', error.response.data);
        console.error('错误响应状态:', error.response.status);
        ElMessage.error(`报名失败：${error.response.data?.msg || error.message || '未知错误'}`);
      } else {
        ElMessage.error(error.message || '活动报名失败，请稍后重试'); // 显示错误消息
      }
      console.log('----活动报名Action结束：报名失败----');
      return false; // 报名失败
    } finally {
      activity.loading = false; // 重置加载状态
    }
  }
  
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