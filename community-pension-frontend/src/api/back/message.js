/**
 * 后台消息中心API
 */
import request from '@/utils/axios';

/**
 * 获取消息列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getMessages(params) {
  return request({
    url: '/api/message/list',
    method: 'get',
    params
  });
}

/**
 * 获取消息详情
 * @param {number} id 消息ID
 * @returns {Promise}
 */
export function getMessage(id) {
  return request({
    url: `/api/message/${id}`,
    method: 'get'
  });
}

/**
 * 标记消息为已读
 * @param {number} id 消息ID
 * @returns {Promise}
 */
export function markMessageRead(id) {
  return request({
    url: `/api/message/mark-read/${id}`,
    method: 'post'
  });
}

/**
 * 标记所有消息为已读
 * @returns {Promise}
 */
export function markAllMessagesRead() {
  return request({
    url: '/api/message/mark-read-all',
    method: 'post'
  });
}

/**
 * 删除消息
 * @param {number} id 消息ID
 * @returns {Promise}
 */
export function deleteMessage(id) {
  return request({
    url: `/api/message/${id}`,
    method: 'delete'
  });
}

/**
 * 清空所有消息
 * @returns {Promise}
 */
export function clearAllMessages() {
  return request({
    url: '/api/message/clear-all',
    method: 'delete'
  });
}

/**
 * 发送消息
 * @param {Object} data 消息数据
 * @returns {Promise}
 */
export function sendMessage(data) {
  return request({
    url: '/api/message/send',
    method: 'post',
    data
  });
}
