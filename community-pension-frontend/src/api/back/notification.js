/**
 * 后台通知中心API
 */
import request from '@/utils/request';

/**
 * 获取通知列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getNotifications(params) {
  return request({
    url: '/admin/notifications',
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
    url: `/admin/notifications/${id}`,
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
    url: `/admin/notifications/${id}/read`,
    method: 'put'
  });
}

/**
 * 标记所有通知为已读
 * @returns {Promise}
 */
export function markAllNotificationsAsRead() {
  return request({
    url: '/admin/notifications/read-all',
    method: 'put'
  });
}

/**
 * 删除通知
 * @param {number} id 通知ID
 * @returns {Promise}
 */
export function deleteNotification(id) {
  return request({
    url: `/admin/notifications/${id}`,
    method: 'delete'
  });
}

/**
 * 清空所有通知
 * @returns {Promise}
 */
export function clearAllNotifications() {
  return request({
    url: '/admin/notifications/clear-all',
    method: 'delete'
  });
}
