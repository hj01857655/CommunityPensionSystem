/**
 * 后台管理系统通知存储
 * 用于管理系统通知、紧急呼叫等通知
 */
import { defineStore } from 'pinia';
import { ref } from 'vue';
import { initWebSocket, closeWebSocket } from '@/utils/adminWebsocket';

export const useNotificationStore = defineStore('backNotification', () => {
  // 通知列表
  const notifications = ref([]);
  // 未读通知数量
  const unreadCount = ref(0);
  // WebSocket连接状态
  const wsConnected = ref(false);

  /**
   * 添加通知
   * @param {Object} notification 通知对象
   */
  function addNotification(notification) {
    notifications.value.unshift(notification);
    if (!notification.read) {
      unreadCount.value++;
    }
  }

  /**
   * 标记通知为已读
   * @param {number} id 通知ID
   */
  function markAsRead(id) {
    const notification = notifications.value.find(n => n.id === id);
    if (notification && !notification.read) {
      notification.read = true;
      unreadCount.value--;
    }
  }

  /**
   * 标记所有通知为已读
   */
  function markAllAsRead() {
    notifications.value.forEach(n => {
      if (!n.read) {
        n.read = true;
      }
    });
    unreadCount.value = 0;
  }

  /**
   * 清除所有通知
   */
  function clearAll() {
    notifications.value = [];
    unreadCount.value = 0;
  }

  /**
   * 初始化WebSocket连接
   */
  function initWsConnection() {
    // 关闭现有连接
    closeWebSocket();
    // 延迟1秒初始化新连接
    setTimeout(() => {
      initWebSocket(true);
      wsConnected.value = true;
    }, 1000);
  }

  /**
   * 关闭WebSocket连接
   */
  function closeWsConnection() {
    closeWebSocket();
    wsConnected.value = false;
  }

  return {
    notifications,
    unreadCount,
    wsConnected,
    addNotification,
    markAsRead,
    markAllAsRead,
    clearAll,
    initWsConnection,
    closeWsConnection
  };
});

export default useNotificationStore;
