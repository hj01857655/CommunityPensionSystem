/**
 * 后台管理系统消息存储
 * 用于管理系统消息、用户消息等
 */
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import AdminWebSocketClient from '@/utils/adminWebsocket';
import { ElMessage } from 'element-plus';

export const useMessageStore = defineStore('backMessage', () => {
  // 消息列表
  const messages = ref([]);
  // 未读消息数量
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
   * 获取消息列表
   * @param {Object} params 查询参数
   */
  async function fetchMessages(params = {}) {
    try {
      loading.value = true;
      const { current = pagination.value.current, size = pagination.value.size, type = 'all' } = params;

      // 从WebSocket获取消息，这里我们模拟一些数据
      // 实际应用中，可能需要发送一个特定的消息类型到服务器，请求消息列表
      const mockMessages = Array.from({ length: 15 }, (_, index) => ({
        id: index + 1,
        sender: `用户 ${index + 1}`,
        avatar: '',
        content: `这是一条测试消息，ID: ${index + 1}`,
        time: new Date(Date.now() - index * 86400000).toLocaleString(),
        read: type === 'read' ? true : (type === 'unread' ? false : (index % 2 === 0))
      }));

      messages.value = mockMessages;
      pagination.value = {
        current,
        size,
        total: 50 // 模拟总数
      };

      // 计算未读消息数量
      unreadCount.value = messages.value.filter(m => !m.read).length;

      return { data: { records: mockMessages, current, size, total: 50 } };
    } catch (error) {
      ElMessage.error(`获取消息列表失败: ${error.message}`);
      return Promise.reject(error);
    } finally {
      loading.value = false;
    }
  }

  /**
   * 添加消息
   * @param {Object} message 消息对象
   */
  function addMessage(message) {
    messages.value.unshift(message);
    if (!message.read) {
      unreadCount.value++;
    }

    // 显示消息通知
    ElMessage({
      type: 'info',
      message: `新消息: ${message.content}`,
      duration: 3000
    });
  }

  /**
   * 标记消息为已读
   * @param {number} id 消息ID
   */
  async function markAsRead(id) {
    try {
      // 发送WebSocket消息标记已读
      AdminWebSocketClient.sendMessage('markMessageRead', { id });

      // 更新本地数据
      const message = messages.value.find(m => m.id === id);
      if (message && !message.read) {
        message.read = true;
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
   * 标记所有消息为已读
   */
  async function markAllAsRead() {
    try {
      // 发送WebSocket消息标记所有已读
      AdminWebSocketClient.sendMessage('markAllMessagesRead', {});

      // 更新本地数据
      messages.value.forEach(m => {
        if (!m.read) {
          m.read = true;
        }
      });
      unreadCount.value = 0;

      ElMessage.success('所有消息已标记为已读');
      return true;
    } catch (error) {
      ElMessage.error(`标记所有消息已读失败: ${error.message}`);
      return Promise.reject(error);
    }
  }

  /**
   * 发送消息
   * @param {Object} message 消息对象
   */
  async function sendUserMessage(message) {
    try {
      AdminWebSocketClient.sendMessage('chatMessage', message);

      ElMessage.success('消息已发送');
      return true;
    } catch (error) {
      ElMessage.error(`发送消息失败: ${error.message}`);
      return Promise.reject(error);
    }
  }

  /**
   * 清除所有消息
   */
  async function clearAll() {
    try {
      // 发送WebSocket消息清空所有消息
      AdminWebSocketClient.sendMessage('clearAllMessages', {});

      // 更新本地数据
      messages.value = [];
      unreadCount.value = 0;

      ElMessage.success('所有消息已清除');
      return true;
    } catch (error) {
      ElMessage.error(`清除消息失败: ${error.message}`);
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

  // 处理接收到的WebSocket消息
  function handleWebSocketMessage(message) {
    if (message.type === 'chatMessage') {
      // 添加新消息
      addMessage({
        id: message.data.id || Date.now(),
        sender: message.data.sender || '系统',
        avatar: message.data.avatar || '',
        content: message.data.content,
        time: message.data.time || new Date().toLocaleString(),
        read: false
      });
    }
  }

  return {
    messages,
    unreadCount,
    wsConnected,
    loading,
    pagination,
    fetchMessages,
    addMessage,
    markAsRead,
    markAllAsRead,
    sendUserMessage,
    clearAll,
    initWsConnection,
    closeWsConnection,
    handleWebSocketMessage
  };
});

export default useMessageStore;
