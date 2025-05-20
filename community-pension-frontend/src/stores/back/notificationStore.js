/**
 * 后台管理系统通知存储
 * 用于管理系统通知、紧急呼叫等通知
 */
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import AdminWebSocketClient from '@/utils/adminWebsocket';
import { getNotifications, markNotificationAsRead, markAllNotificationsAsRead, deleteNotification, clearAllNotifications } from '@/api/back/notification';
import { ElMessage } from 'element-plus';

export const useNotificationStore = defineStore('backNotification', () => {
  // 通知列表
  const notifications = ref([]);
  // 未读通知数量
  const unreadCount = ref(0);
  // WebSocket连接状态
  const wsConnected = ref(false);
  // 加载状态
  const loading = ref(false);
  // 分页信息
  const pagination = ref({
    current: 1,
    size: 10,
    total: 0
  });

  /**
   * 获取通知列表
   * @param {Object} params 查询参数
   */
  async function fetchNotifications(params = {}) {
    try {
      loading.value = true;
      const { current = pagination.value.current, size = pagination.value.size, type = 'all' } = params;
      
      const res = await getNotifications({
        current,
        size,
        type
      });
      
      notifications.value = res.data.records || [];
      pagination.value = {
        current: res.data.current,
        size: res.data.size,
        total: res.data.total
      };
      
      // 计算未读通知数量
      unreadCount.value = notifications.value.filter(n => !n.read).length;
      return res;
    } catch (error) {
      ElMessage.error(`获取通知列表失败: ${error.message}`);
      return Promise.reject(error);
    } finally {
      loading.value = false;
    }
  }
  
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
  async function markAsRead(id) {
    try {
      await markNotificationAsRead(id);
      
      // 更新本地数据
      const notification = notifications.value.find(n => n.id === id);
      if (notification && !notification.read) {
        notification.read = true;
        unreadCount.value = Math.max(0, unreadCount.value - 1);
      }
      
      ElMessage.success('已标记为已读');
      return true;
    } catch (error) {
      ElMessage.error(`标记已读失败: ${error.message}`);
      return Promise.reject(error);
    }
  }

  /**
   * 标记所有通知为已读
   */
  async function markAllAsRead() {
    try {
      await markAllNotificationsAsRead();
      
      // 更新本地数据
      notifications.value.forEach(n => {
        if (!n.read) {
          n.read = true;
        }
      });
      unreadCount.value = 0;
      
      ElMessage.success('所有通知已标记为已读');
      return true;
    } catch (error) {
      ElMessage.error(`标记所有通知已读失败: ${error.message}`);
      return Promise.reject(error);
    }
  }

  /**
   * 清除所有通知
   */
  async function clearAll() {
    try {
      await clearAllNotifications();
      
      // 更新本地数据
      notifications.value = [];
      unreadCount.value = 0;
      
      ElMessage.success('所有通知已清除');
      return true;
    } catch (error) {
      ElMessage.error(`清除通知失败: ${error.message}`);
      return Promise.reject(error);
    }
  }

  /**
   * 初始化WebSocket连接
   */
  function initWsConnection() {
    // 关闭现有连接
    AdminWebSocketClient.close();
    // 延迟1秒初始化新连接
    setTimeout(() => {
      const token = sessionStorage.getItem('admin-access-token');
      AdminWebSocketClient.init(token, true);
      wsConnected.value = true;
    }, 1000);
  }

  /**
   * 关闭WebSocket连接
   */
  function closeWsConnection() {
    AdminWebSocketClient.close();
    wsConnected.value = false;
  }

  return {
    notifications,
    unreadCount,
    wsConnected,
    loading,
    pagination,
    fetchNotifications,
    addNotification,
    markAsRead,
    markAllAsRead,
    clearAll,
    initWsConnection,
    closeWsConnection
  };
});

export default useNotificationStore;
