import axios from '@/utils/axios';

/**
 * 活动管理相关接口
 */

/**
 * 获取活动列表
 * @param {Object} [params] - 查询参数
 * @param {number} [params.status] - 活动状态（可选）
 * @returns {Promise<{code: number, data: Array<Object>, msg: string}>}
 */
export const getActivityList = (params = {}) => {
  return axios.get('/api/activity/list', { params })
}

/**
 * 活动报名
 * @param {number} activityId - 活动ID
 * @param {Object} [elderData={}] - 老人相关数据，必须包含elderId
 * @returns {Promise<{code: number, msg: string}>}
 */
export const registerActivity = (activityId, elderData = {}) => {
  console.log('API registerActivity 被调用 - 参数:', { activityId, elderData });
  
  // 提取elderId参数
  const elderId = elderData.elderId;
  
  if (!elderId) {
    console.error('报名活动缺少必要参数: elderId');
    return Promise.reject({ message: '报名活动需要提供老人ID' });
  }
  
  if (!activityId) {
    console.error('报名活动缺少必要参数: activityId');
    return Promise.reject({ message: '报名活动需要提供活动ID' });
  }
  
  try {
    // 确保activityId是数字
    const numericActivityId = Number(activityId);
    if (isNaN(numericActivityId)) {
      console.error('活动ID不是有效数字:', activityId);
      return Promise.reject({ message: '活动ID不是有效数字' });
    }
    
    // 确保elderId是数字
    const numericElderId = Number(elderId);
    if (isNaN(numericElderId)) {
      console.error('老人ID不是有效数字:', elderId);
      return Promise.reject({ message: '老人ID不是有效数字' });
    }
    
    console.log('发起报名请求, URL:', `/api/activity/register/${numericActivityId}`, '参数:', { elderId: numericElderId });
    
    // 使用正确的请求方式 - 后端使用@RequestParam，需要将参数放在URL查询参数中
    return axios.post(`/api/activity/register/${numericActivityId}`, null, {
      params: {
        elderId: numericElderId,
        // 不需要提供其他可选参数，使用默认值
        registerType: 0  // 使用默认的报名类型
      }
    })
    .then(response => {
      console.log('报名API响应成功:', response);
      return response;
    })
    .catch(error => {
      console.error('报名API请求失败:', error);
      
      if (error.response) {
        console.error('错误状态码:', error.response.status);
        console.error('错误响应数据:', error.response.data);
        
        // 提取错误消息
        const errorMsg = error.response.data && (error.response.data.msg || error.response.data.message)
          ? (error.response.data.msg || error.response.data.message)
          : '报名失败，请稍后重试';
          
        return Promise.reject({ message: errorMsg });
      }
      
      return Promise.reject(error);
    });
  } catch (error) {
    console.error('报名过程中捕获到错误:', error);
    return Promise.reject(error);
  }
}

/**
 * 获取活动详情
 * @param {number} activityId - 活动ID
 * @returns {Promise<{code: number, data: Object, msg: string}>}
 */
export const getActivityDetail = (activityId) => {
  return axios.get(`/api/activity/${activityId}`)
}

/**
 * 获取用户活动报名状态
 * @param {number} activityId - 活动ID
 * @param {number} [elderId] - 老人ID，可选，如果不提供会尝试从store或localStorage获取
 * @returns {Promise<{code: number, data: {status: number, registerId: number}, msg: string}>}
 */
export const getActivityStatus = (activityId, elderId) => {
  console.log('调用getActivityStatus API - 参数:', { activityId, elderId });
  
  // 确保activityId是数字类型
  const numericActivityId = Number(activityId);
  if (isNaN(numericActivityId)) {
    console.error('活动ID不是有效数字:', activityId);
    return Promise.reject({ message: '活动ID不是有效数字' });
  }
  
  // 确保elderId是数字类型
  const numericElderId = elderId ? Number(elderId) : null;
  if (elderId && isNaN(numericElderId)) {
    console.error('老人ID不是有效数字:', elderId);
    return Promise.reject({ message: '老人ID不是有效数字' });
  }
  
  // 修正API路径为正确的端点，尝试几种可能的路径格式
  let requestConfig = {
    params: numericElderId ? { elderId: numericElderId } : {}
  };
  
  // 使用完整的调试日志
  console.log('发起查询报名状态请求, URL:', `/api/activity/register/status/${numericActivityId}`, '参数:', requestConfig);
  
  return axios.get(`/api/activity/register/status/${numericActivityId}`, requestConfig)
    .then(response => {
      console.log('查询报名状态API响应成功:', response);
      return response;
    })
    .catch(error => {
      console.error('查询报名状态API请求失败:', error);
      
      if (error.response) {
        // 如果是404，表示未报名，返回友好结果而不是抛出错误
        if (error.response.status === 404) {
          console.log('报名状态API返回404，表示未报名');
          return {
            code: 200,
            data: {
              status: -1,
              isRegistered: false
            },
            msg: '未报名'
          };
        }
        
        console.error('错误状态码:', error.response.status);
        console.error('错误响应数据:', error.response.data);
      }
      
      throw error;
    });
}

/**
 * 获取用户已报名的活动列表
 * @param {Object} [params] - 查询参数
 * @param {number} [params.pageNum=1] - 页码
 * @param {number} [params.pageSize=10] - 每页大小
 * @returns {Promise<{code: number, data: {records: Array<Object>, total: number}, msg: string}>}
 */
export const getUserRegisteredActivities = (params = {}) => {
  return axios.get('/api/activity/register/user/list', { 
    params: {
      pageNum: params.pageNum || 1,
      pageSize: params.pageSize || 10,
      ...params
    }
  })
}

/**
 * 取消活动报名
 * @param {number} registerId - 报名记录ID
 * @param {number} elderId - 老人ID
 * @returns {Promise<{code: number, msg: string}>}
 */
export const cancelActivityRegistration = (registerId, elderId) => {
  return axios.post(`/api/activity/register/cancel/${registerId}`, null, {
    params: { elderId }
  })
}

/**
 * 用户自助签到
 * @param {number} activityId - 活动ID
 * @param {number} elderId - 老人ID
 * @param {Object} [options] - 签到选项
 * @param {string} [options.location] - 签到位置
 * @returns {Promise<{code: number, msg: string}>}
 */
export const selfCheckIn = (activityId, elderId, options = {}) => {
  // 首先，获取报名ID
  return axios.get(`/api/activity/check-in/register-id`, {
    params: {
      activityId,
      elderId
    }
  }).then(res => {
    if (res.code === 200 && res.data) {
      const registerId = res.data;
      // 然后，使用报名ID进行签到
      return axios.post(`/api/activity/check-in`, {
        registerId: registerId,
        checkInUserId: elderId,
        isProxyCheckIn: 0 // 自助签到，非代签
      });
    } else {
      throw new Error('未找到报名记录');
    }
  });
}

/**
 * 查询用户签到状态
 * @param {number} activityId - 活动ID
 * @param {number} elderId - 老人ID
 * @returns {Promise<{code: number, data: {isCheckedIn: boolean, checkInTime: string}, msg: string}>}
 */
export const getCheckInStatus = (activityId, elderId) => {
  return axios.get(`/api/activity/check-in/check`, {
    params: {
      activityId,
      elderId
    }
  }).then(res => {
    if (res.code === 200) {
      // 转换返回格式以保持兼容性
      return {
        code: 200,
        data: {
          isCheckedIn: res.data,  // 后端直接返回布尔值
          checkInTime: new Date().toISOString() // 由于后端没有提供具体签到时间，使用当前时间代替
        },
        msg: '获取成功'
      };
    }
    return res;
  });
}