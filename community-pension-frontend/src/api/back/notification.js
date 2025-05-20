/**
 * 后台通知中心API
 */
import request from '@/utils/axios';

/**
 * 获取通知列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getNotifications(params) {
  return request({
    url: '/api/notice/list',
    method: 'get',
    params
  });
}

/**
 * 获取通知详情
 * @param {number} id 通知ID
 * @returns {Promise}
 */
export function getNotificationDetail(id) {
  return request({
    url: `/api/notice/${id}`,
    method: 'get'
  });
}

/**
 * 标记通知为已读
 * @param {number} id 通知ID
 * @returns {Promise}
 */
export function markNotificationAsRead(id) {
  return request({
    url: `/api/notice/mark-read/${id}`,
    method: 'post'
  });
}

/**
 * 标记所有通知为已读
 * @returns {Promise}
 */
export function markAllNotificationsAsRead() {
  return request({
    url: '/api/notice/mark-read-all',
    method: 'get'
  });
}

/**
 * 删除通知
 * @param {number} id 通知ID
 * @returns {Promise}
 */
export function deleteNotification(id) {
  return request({
    url: `/api/notice/${id}`,
    method: 'delete'
  });
}

/**
 * 清空所有通知
 * @returns {Promise}
 */
export function clearAllNotifications() {
  return request({
    url: '/api/notice/clear-all',
    method: 'delete'
  });
}
