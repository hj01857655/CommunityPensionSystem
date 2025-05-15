// 引入请求库
import request from '@/utils/axios';

/**
 * 获取后台首页统计数据（列表形式）
 * @returns {Promise} 统计数据Promise
 */
export function getStatisticsData() {
  return request({
    url: '/api/dashboard/stats',
    method: 'get'
  });
}

/**
 * 获取后台首页统计数据（对象形式）
 * @returns {Promise} 统计数据Promise
 */
export function getOverviewStatistics() {
  return request({
    url: '/api/dashboard/overview',
    method: 'get'
  });
}

/**
 * 获取用户趋势数据
 * @param {String} type - 时间范围类型，week: 本周, month: 本月
 * @returns {Promise} 用户趋势数据Promise
 */
export function getUserTrendData(type = 'week') {
  return request({
    url: '/api/dashboard/user-trend',
    method: 'get',
    params: { type }
  });
}

/**
 * 获取活动类型分布数据
 * @returns {Promise} 活动类型分布数据Promise
 */
export function getActivityTypeData() {
  return request({
    url: '/api/dashboard/activity-types',
    method: 'get'
  });
}

/**
 * 获取最新活动列表
 * @param {Number} limit - 获取活动的数量限制
 * @returns {Promise} 活动列表数据Promise
 */
export function getRecentActivities(limit = 3) {
  return request({
    url: '/api/dashboard/recent-activities',
    method: 'get',
    params: { limit }
  });
}

/**
 * 获取最新通知公告
 * @param {number} limit 返回条数
 */
export function getRecentNotifications(limit) {
  return request({
    url: '/api/dashboard/recent-notifications',
    method: 'get',
    params: { limit }
  });
}

/**
 * 获取最新健康预警
 * @param {number} limit 返回条数
 */
export function getWarningData(limit) {
  return request({
    url: '/api/dashboard/warnings',
    method: 'get',
    params: { limit }
  });
}

/**
 * 获取仪表盘所有数据（单个接口获取全部）
 * @returns {Promise} 所有仪表盘数据Promise
 */
export function getAllDashboardData() {
  return request({
    url: '/api/dashboard/all',
    method: 'get'
  });
}

// 作为备选的数据获取方法，使用组合API请求
// 如果后端没有提供单一的仪表盘数据API，可以使用这个函数组合多个API调用
export async function getDashboardDataComposite() {
  try {
    // 使用Promise.all并行请求所有数据
    const [statsResponse, activityTypesResponse, activitiesResponse] = await Promise.all([
      getStatisticsData(),
      getActivityTypeData(),
      getRecentActivities(3)
    ]);
    
    return {
      success: true,
      stats: statsResponse.data,
      activityTypes: activityTypesResponse.data,
      activities: activitiesResponse.data
    };
  } catch (error) {
    console.error("获取仪表盘数据失败:", error);
    return {
      success: false,
      error: error.message || "获取数据失败"
    };
  }
} 