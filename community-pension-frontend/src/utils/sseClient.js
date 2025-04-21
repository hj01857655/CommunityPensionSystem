/**
 * SSE客户端服务
 */
import { ElMessage, ElNotification } from 'element-plus';
import axios from '@/utils/axios';
import { TokenManager } from '@/utils/axios';

class SseClient {
  constructor() {
    this.eventSource = null;
    this.connected = false;
    this.reconnectAttempts = 0;
    this.maxReconnectAttempts = 5;
    this.reconnectInterval = 3000; // 3秒
    this.listeners = new Map();
    this.clientType = null; // 'admin' 或 'user'
    this.userStore = null;
    this.connectTimeout = null;
  }

  /**
   * 初始化用户store
   * @param {Object} store - Pinia store实例
   */
  initStore(store) {
    this.userStore = store;
  }

  /**
   * 连接SSE服务器
   * @param {string} type 客户端类型：'admin' 或 'user'
   */
  async connect(type = 'user') {
    if (this.eventSource) {
      console.log('SSE已连接或正在连接中');
      return;
    }

    // 直接从本地存储获取所有认证信息
    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
    const accessToken = localStorage.getItem('user-access-token');
    const refreshToken = localStorage.getItem('user-refresh-token');
    const userInfoStr = localStorage.getItem('userInfo');
    
    console.log('SSE连接 - 本地存储状态:', {
      isLoggedIn,
      hasAccessToken: !!accessToken,
      hasRefreshToken: !!refreshToken,
      userInfoStr
    });

    let localUserInfo;
    try {
      localUserInfo = JSON.parse(userInfoStr || '{}');
      console.log('SSE连接 - 解析后的用户信息:', localUserInfo);
    } catch (error) {
      console.error('解析userInfo失败:', error);
      localUserInfo = {};
    }
    
    // 尝试从不同字段获取用户ID
    const userId = localUserInfo?.userId || localUserInfo?.id;
    console.log('SSE连接 - 用户ID:', userId);

    // 检查用户是否已登录
    if (!isLoggedIn) {
      console.warn('SSE连接失败：用户未登录');
      return;
    }

    if (!accessToken) {
      console.warn('SSE连接失败：访问令牌不存在');
      return;
    }

    if (!refreshToken) {
      console.warn('SSE连接失败：刷新令牌不存在');
      return;
    }

    if (!userId) {
      console.warn('SSE连接失败：无法获取用户ID');
      return;
    }

    this.clientType = type;

    try {
      console.log('SSE连接 - 开始建立连接，使用用户ID:', userId);

      // 使用封装的 axios 实例发起 SSE 请求
      const response = await axios({
        url: `/api/notifications/sse`,
        method: 'GET',
        params: {
          userId: userId
        },
        headers: {
          'Accept': 'text/event-stream',
          'Cache-Control': 'no-cache'
        },
        responseType: 'text',
        timeout: 30000, // 设置30秒超时
        onDownloadProgress: (progressEvent) => {
          const event = progressEvent.event;
          if (event.type === 'message') {
            try {
              const data = JSON.parse(event.data);
              console.log(`收到SSE(${type})通知:`, data);
              
              // 添加客户端类型标识
              data.clientType = type;
              
              // 显示通知
              this.showNotification(data);
              
              // 触发消息事件
              this.dispatchEvent('notification', data);
            } catch (error) {
              console.error('解析SSE消息失败:', error);
            }
          } else if (event.type === 'open') {
            console.log(`SSE(${type})连接成功`);
            this.connected = true;
            this.reconnectAttempts = 0;
            this.dispatchEvent('connect', { connected: true, type });
          } else if (event.type === 'error') {
            this.handleError(event);
          }
        }
      });

      // 保存请求对象，用于后续关闭连接
      this.eventSource = response;
      
    } catch (error) {
      console.error(`创建SSE(${type})连接失败:`, error);
      this.handleError(error);
    }
  }

  /**
   * 处理连接错误
   * @param {Error} error 错误对象
   */
  handleError(error) {
    console.error(`SSE(${this.clientType})连接错误:`, error);
    this.connected = false;
    
    if (this.eventSource) {
      this.eventSource.abort(); // 使用 axios 的 abort 方法
      this.eventSource = null;
    }
    
    // 触发错误事件
    this.dispatchEvent('error', { error, type: this.clientType });
    
    // 尝试重新连接
    this.reconnect();
  }

  /**
   * 显示通知
   * @param {Object} notification 通知对象
   */
  showNotification(notification) {
    if (!notification || !notification.title) {
      return;
    }

    // 根据通知类型设置通知类型
    let type = 'info';
    switch (notification.type) {
      case 'SYSTEM':
        type = 'info';
        break;
      case 'HEALTH':
        type = 'warning';
        break;
      case 'SERVICE':
        type = 'success';
        break;
      case 'ACTIVITY':
        type = 'success';
        break;
      case 'EMERGENCY':
        type = 'error';
        break;
      default:
        type = 'info';
    }

    // 显示通知
    ElNotification({
      title: notification.title,
      message: notification.content,
      type: type,
      duration: 5000,
      onClick: () => {
        // 如果有链接，点击通知时跳转
        if (notification.link) {
          window.location.href = notification.link;
        }
      }
    });
  }

  /**
   * 重新连接
   */
  reconnect() {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.warn(`SSE重连失败，已达到最大重试次数(${this.maxReconnectAttempts})`);
      ElMessage.error('通知连接失败，请刷新页面重试');
      return;
    }

    this.reconnectAttempts++;
    console.log(`SSE尝试重连(${this.reconnectAttempts}/${this.maxReconnectAttempts})...`);

    // 使用指数退避算法增加重试间隔
    const delay = Math.min(1000 * Math.pow(2, this.reconnectAttempts), 30000);
    
    setTimeout(() => {
      this.connect(this.clientType);
    }, delay);
  }

  /**
   * 断开SSE连接
   */
  disconnect() {
    if (this.connectTimeout) {
      clearTimeout(this.connectTimeout);
      this.connectTimeout = null;
    }
    if (this.eventSource) {
      this.eventSource.close();
      this.eventSource = null;
      this.connected = false;
      console.log('SSE连接已断开');
    }
  }

  /**
   * 添加事件监听器
   * @param {string} event 事件名称
   * @param {Function} callback 回调函数
   */
  addEventListener(event, callback) {
    if (!this.listeners.has(event)) {
      this.listeners.set(event, []);
    }
    
    this.listeners.get(event).push(callback);
  }

  /**
   * 移除事件监听器
   * @param {string} event 事件名称
   * @param {Function} callback 回调函数
   */
  removeEventListener(event, callback) {
    if (!this.listeners.has(event)) {
      return;
    }
    
    const callbacks = this.listeners.get(event);
    const index = callbacks.indexOf(callback);
    
    if (index !== -1) {
      callbacks.splice(index, 1);
    }
  }

  /**
   * 触发事件
   * @param {string} event 事件名称
   * @param {*} data 事件数据
   */
  dispatchEvent(event, data) {
    if (!this.listeners.has(event)) {
      return;
    }
    
    const callbacks = this.listeners.get(event);
    callbacks.forEach(callback => {
      try {
        callback(data);
      } catch (error) {
        console.error(`执行${event}事件回调函数失败:`, error);
      }
    });
  }

  /**
   * 检查是否已连接
   * @returns {boolean} 是否已连接
   */
  isConnected() {
    return this.connected;
  }
}

// 创建SSE客户端实例
const sseClient = new SseClient();

// 修改为命名导出
export { sseClient };