import {
  cancelActivityRegistration,
  getActivityDetail,
  getActivityList,
  getActivityStatus,
  getCheckInStatus,
  getUserRegisteredActivities,
  registerActivity,
  selfCheckIn
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
      // 获取用户信息，优先从store中获取，再从localStorage获取
      const userInfo = userStore.userInfo || JSON.parse(localStorage.getItem('userInfo') || '{}');
      const roleId = userStore.roleId || localStorage.getItem('roleId') || userInfo.roleId;
      
      // 调试信息
      console.log('获取老人ID - 用户信息:', {
        userInfo: userInfo,
        storeRoleId: userStore.roleId,
        localStorageRoleId: localStorage.getItem('roleId'),
        roleId: roleId
      });
      
      // 情况1: 如果用户是老人角色(roleId == 1)，直接返回用户ID
      if (roleId == 1 || (userInfo.roles && userInfo.roles.includes('elder'))) {
        const elderId = userInfo.userId || userInfo.id;
        console.log('用户是老人，返回老人ID:', elderId);
        return elderId;
      }
      
      // 情况2: 如果用户是家属角色(roleId == 2)且绑定了老人，返回绑定的老人ID
      if (roleId == 2 || (userInfo.roles && userInfo.roles.includes('kin'))) {
        if (userInfo.bindElder) {
          const elderId = userInfo.bindElder.id || userInfo.bindElder.userId;
          console.log('用户是家属且已绑定老人，返回绑定老人ID:', elderId);
          return elderId;
        }
        
        // 尝试从localStorage获取绑定老人信息
        const bindElderStr = localStorage.getItem('bindElder');
        if (bindElderStr) {
          try {
            const bindElder = JSON.parse(bindElderStr);
            const elderId = bindElder.id || bindElder.userId;
            console.log('从localStorage获取到绑定老人ID:', elderId);
            return elderId;
          } catch (e) {
            console.error('解析绑定老人信息失败:', e);
          }
        }
        
        // 如果没有绑定老人，可能需要提示用户先绑定老人
        console.warn('家属用户未绑定老人');
        return null;
      }
      
      console.warn('无法确定用户角色或获取老人ID');
      return null;
    } catch (error) {
      console.error('获取老人ID时发生错误:', error);
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
    console.log('fetchActivities 被调用，forceRefresh:', forceRefresh);
    // 如果已有数据且不是强制刷新，直接返回缓存数据
    if (activities.value.length > 0 && !forceRefresh) {
      console.log('使用缓存的活动数据:', activities.value.length, '条记录');
      return activities.value;
    }

    // 如果正在加载中，等待加载完成
    if (loading.value) {
      console.log('活动数据正在加载中，等待完成...');
      return new Promise(resolve => {
        const unwatch = watch(() => loading.value, (newValue) => {
          if (!newValue) {
            unwatch();
            console.log('加载完成，返回活动数据:', activities.value.length, '条记录');
            resolve(activities.value);
          }
        });
      });
    }

    try {
      console.log('开始加载活动数据...');
      loading.value = true;

      // 调用API获取活动列表
      const response = await getActivityList();
      console.log('活动列表API响应:', response);

      if (response && response.code === 200) {
        // 检查response.data是否存在
        if (response.data) {
          let activityData;
          
          // 处理不同的响应格式
          if (typeof response.data === 'object') {
            // 如果是对象，可能有分页信息
            if (response.data.records && Array.isArray(response.data.records)) {
              activityData = response.data.records;
              console.log('从分页数据中提取活动列表:', activityData.length, '条记录');
            } else if (Array.isArray(response.data)) {
              // 直接是数组
              activityData = response.data;
              console.log('活动数据是数组:', activityData.length, '条记录');
            } else {
              // 其他情况，尝试转换为数组
              activityData = [response.data];
              console.log('活动数据是单个对象，转换为数组');
            }
          } else if (Array.isArray(response.data)) {
            // 直接是数组
            activityData = response.data;
            console.log('活动数据是数组:', activityData.length, '条记录');
          } else {
            // 未知格式，使用空数组
            activityData = [];
            console.warn('未知的活动数据格式:', response.data);
          }
          
          // 更新状态
          if (activityData && activityData.length > 0) {
            activities.value = activityData.map(activity => ({
              ...activity,
              loading: false // 添加loading状态用于报名操作
            }));
            console.log('活动数据更新成功:', activities.value.length, '条记录');
          } else {
            console.warn('活动数据为空');
            activities.value = [];
          }
        } else {
          console.warn('响应中没有data字段');
          activities.value = [];
        }
      } else {
        console.error('获取活动列表失败:', response?.message || '未知错误');
        ElMessage.error(response?.message || '获取活动列表失败');
      }
      return activities.value;
    } catch (error) {
      console.error('获取活动列表异常:', error);
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
    console.log('registerActivityAction - 收到活动对象:', activity);
    
    // 确保activity是一个有效对象
    if (!activity || typeof activity !== 'object') {
      console.error('报名失败: 无效的活动对象', activity);
      ElMessage.error('报名失败: 无效的活动对象');
      return false;
    }
    
    // 确保活动ID存在且为数字类型
    if (!activity.id) {
      console.error('报名失败: 活动ID不存在', activity);
      ElMessage.error('报名失败: 活动ID不存在');
      return false;
    }
    
    const activityId = Number(activity.id);
    if (isNaN(activityId)) {
      console.error('报名失败: 活动ID不是有效数字', activity.id);
      ElMessage.error('报名失败: 活动ID不是有效数字');
      return false;
    }
    
    // 检查活动是否可以报名
    if (!canJoin(activity.status)) {
      console.warn('当前活动状态不可报名:', activity.status);
      ElMessage.warning('当前活动状态不可报名');
      return false;
    }
    
    // 获取老人ID
    let elderId = getElderId();
    console.log('获取到的老人ID:', elderId);
    
    // 如果不能获取到有效的老人ID，则无法报名
    if (!elderId) {
      console.error('无法获取老人ID，报名失败');
      ElMessage.warning('当前用户不是老人身份或未获取到老人信息，无法报名活动');
      return false;
    }
    
    // 创建一个新的loading状态变量，避免直接修改传入的activity对象
    let isLoading = false;
    
    try {
      console.log('开始调用报名API, 活动ID:', activityId, '老人ID:', elderId);
      isLoading = true;
      
      // 调用API进行活动报名，直接传递elderId
      const result = await registerActivity(activityId, { elderId });
      console.log('报名API返回完整结果:', result);
      
      // 检查结果格式
      if (!result) {
        console.error('API返回无效结果');
        ElMessage.error('报名失败：服务器返回无效结果');
        return false;
      }
      
      if (result.code === 200) {
        console.log('报名成功，服务器返回:', result.data);
        ElMessage.success(result.message || result.msg || '活动报名成功');
        
        // 刷新活动列表
        await fetchActivities(true);
        
        return true;
      } else {
        console.error('报名失败, API返回错误:', result);
        
        // 提取并显示错误信息
        const errorMsg = result.message || result.msg || '活动报名失败，请稍后重试';
        console.error('错误消息:', errorMsg);
        
        ElMessage.error(errorMsg);
        return false;
      }
    } catch (error) {
      console.error('报名过程中发生错误:', error);
      console.error('错误类型:', typeof error);
      
      // 获取更详细的错误消息
      let errorMessage = '报名失败，请稍后重试';
      
      if (error && error.message) {
        errorMessage = error.message;
      } else if (error && error.response && error.response.data) {
        if (error.response.data.msg) {
          errorMessage = error.response.data.msg;
        } else if (error.response.data.message) {
          errorMessage = error.response.data.message;
        }
      }
      
      console.error('最终错误消息:', errorMessage);
      ElMessage.error(errorMessage);
      
      return false;
    } finally {
      isLoading = false;
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
      // 获取老人ID
      const elderId = getElderId();
      
      if (!elderId) {
        console.warn('无法获取老人ID，无法查询报名状态');
        return null;
      }
      
      console.log('查询活动报名状态, 活动ID:', activityId, '老人ID:', elderId);
      
      // 传递elderId参数给API调用
      const response = await getActivityStatus(activityId, elderId);
      console.log('查询报名状态API返回:', response);
      
      if (response && response.code === 200) {
        // 检查响应数据格式
        if (response.data) {
          const statusData = {
            status: response.data.status,
            registerId: response.data.registerId || response.data.id,
            registerTime: response.data.registerTime || response.data.createdAt,
            isRegistered: true
          };
          console.log('解析到的报名状态数据:', statusData);
          return statusData;
        } else {
          console.warn('API返回成功但没有数据');
          return { status: -1, isRegistered: false };
        }
      } else {
        const msg = response?.msg || response?.message || '未知错误';
        console.warn('获取报名状态失败:', msg);
        
        // 如果是因为未登录等常见错误，可以显示提示，但用更友好的方式
        if (response?.code === 401) {
          // 不在这里显示登录提示，避免过多干扰
          console.warn('用户未登录，无法查询报名状态');
        } else if (response?.code === 403) {
          // 用户无权限查看此活动的报名状态
          console.warn('用户无权限查看此活动的报名状态');
        } else if (msg.includes('不存在') || msg.includes('未找到')) {
          // 可能是活动不存在或报名记录不存在
          console.log('未找到报名记录，用户未报名');
          return { status: -1, isRegistered: false };
        } else {
          console.warn('其他错误原因:', msg);
        }
        
        return { status: -1, isRegistered: false };
      }
    } catch (error) {
      console.error('查询报名状态时发生错误:', error);
      
      // 特殊处理500错误等情况
      const errorMsg = error.message || '未知错误';
      const httpStatus = error.response?.status;
      
      // HTTP状态为404或错误消息包含"未报名"等关键词时，视为用户未报名
      if (httpStatus === 404 || 
          errorMsg.includes('未报名') || 
          errorMsg.includes('不存在') ||
          errorMsg.includes('未找到报名记录')) {
        console.log('根据错误信息判断用户未报名');
        return { status: -1, isRegistered: false };
      }
      
      // 对于500错误，我们需要特殊处理
      if (httpStatus === 500) {
        console.warn('服务器内部错误(500)，无法获取报名状态');
        return { status: -1, isRegistered: false };
      }
      
      return { status: -1, isRegistered: false };
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
      
      const res = await cancelActivityRegistration(registerId, elderId);
      if (res.code === 200) {
        ElMessage.success('取消报名成功');
        return true; // 取消成功
      }
      ElMessage.error(res.msg || '取消报名失败');
      return false; // 取消失败
    } catch (error) {
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
                        // 解析bindElder失败
                    }
                }
            }
        }

        // 检查是否获取到老人ID
        if (!elderId) {
            throw new Error('无法确定老人身份，请确认您是老人或已绑定老人');
        }

        // 设置查询参数
        const queryParams = {
            ...params,
            userId: elderId  // 使用userId作为参数名
        };

        // 调用API获取数据
        return await getUserRegisteredActivitiesAction(queryParams);
    }
  
  // 初始化函数 - 在store创建时自动调用一次获取活动列表
  const initStore = async () => {
    console.log('活动Store初始化中...');
    try {
      await fetchActivities();
      console.log('活动Store初始化完成，活动数量:', activities.value.length);
    } catch (error) {
      console.error('活动Store初始化失败:', error);
    }
  };
  
  // 在store创建后立即初始化
  initStore();
  
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
    fetchUserRegisteredActivities,
    initStore,
    
    // 新增签到相关方法
    /**
     * 用户自助签到
     * @param {Object} activity - 活动对象
     * @returns {Promise<boolean>} 签到是否成功
     */
    selfCheckInAction: async (activity) => {
      try {
        // 获取老人ID
        const elderId = getElderId();
        if (!elderId) {
          ElMessage.warning('无法确定老人身份，请确认您是老人或已绑定老人');
          return false;
        }
        
        const res = await selfCheckIn(activity.id, elderId);
        if (res.code === 200) {
          ElMessage.success('签到成功');
          // 刷新活动列表
          await fetchActivities(true);
          return true;
        } else {
          ElMessage.error(res.msg || '签到失败');
          return false;
        }
      } catch (error) {
        ElMessage.error(error.message || '签到失败，请稍后重试');
        return false;
      }
    },
    
    /**
     * 获取用户签到状态
     * @param {number} activityId - 活动ID
     * @returns {Promise<Object|null>} 签到状态对象
     */
    getCheckInStatusAction: async (activityId) => {
      try {
        // 获取老人ID
        const elderId = getElderId();
        if (!elderId) {
          return null;
        }
        
        const res = await getCheckInStatus(activityId, elderId);
        if (res.code === 200) {
          return res.data;
        }
        return null;
      } catch (error) {
        // 如果是404等特定错误，说明用户未签到
        if (error.response && error.response.status === 404) {
          return { isCheckedIn: false };
        }
        return null;
      }
    }
  }
}) 