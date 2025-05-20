/**
 * WebSocket客户端
 * 用于连接WebSocket服务器，接收和处理消息
 */

// WebSocket连接
let socket;
// 重连次数
let reconnectCount = 0;
// 最大重连次数
const MAX_RECONNECT = 5;
// 重连间隔（毫秒）
const RECONNECT_INTERVAL = 3000;
// 心跳间隔（毫秒）
const HEARTBEAT_INTERVAL = 30000;
// 心跳定时器
let heartbeatTimer;
// 重连定时器
let reconnectTimer;
// 是否已初始化
let initialized = false;
// 消息处理回调
let messageHandlers = {};

/**
 * 获取当前用户信息
 * @returns {Object|null} 用户信息对象或null
 */
function getUserInfo() {
    try {
        // 从 localStorage 获取用户信息
        const userInfoStr = localStorage.getItem('userInfo');
        if (userInfoStr) {
            return JSON.parse(userInfoStr);
        }
        return null;
    } catch (error) {
        console.error('获取用户信息失败:', error);
        return null;
    }
}

/**
 * 初始化WebSocket连接
 * @param {string} token 用户令牌
 */
function initWebSocket(token) {
  // 检查是否已经初始化
  if (initialized) {
    console.log('WebSocket已经初始化，不需要再次初始化');
    return;
  }
  
  // 检查token是否有效
  if (!token) {
    console.error('WebSocket初始化失败：token不能为空');
    return;
  }
  
  // 处理token，移除Bearer前缀
  if (token.startsWith('Bearer ')) {
    token = token.substring(7);
    console.log('移除Bearer前缀后的token长度:', token.length);
  }

  // 直接连接到后端服务器，不依赖Vite代理
  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
  const wsUrl = `${protocol}//localhost:9000/websocket/${token}`;
  console.log('直接连接到后端服务器:', wsUrl);
  console.log('完整token信息:', token);
  
  try {
    socket = new WebSocket(wsUrl);
    
    // 连接打开事件
    socket.onopen = function() {
      console.log('%cWebSocket连接成功!', 'color: green; font-weight: bold');
      reconnectCount = 0;
      startHeartbeat();
      initialized = true;
      
      // 触发连接成功回调
      if (messageHandlers.onConnected) {
        messageHandlers.onConnected();
      }
    };
    
    // 接收消息事件
    socket.onmessage = function(event) {
      const message = JSON.parse(event.data);
      console.log('收到WebSocket消息:', message);
      
      // 处理不同类型的消息
      handleMessage(message);
    };
    
    // 连接关闭事件
    socket.onclose = function(event) {
      console.warn('%cWebSocket连接关闭!', 'color: orange; font-weight: bold', event);
      
      // 输出更多调试信息
      console.warn('关闭详情:', {
        code: event.code,
        reason: event.reason,
        wasClean: event.wasClean,
        type: event.type
      });
      
      stopHeartbeat();
      initialized = false;
      
      // 触发连接关闭回调
      if (messageHandlers.onDisconnected) {
        messageHandlers.onDisconnected(event);
      }
      
      // 尝试重连
      tryReconnect();
    };
    
    // 连接错误事件
    socket.onerror = function(error) {
      console.error('%cWebSocket连接错误!', 'color: red; font-weight: bold', error);
      
      // 输出更多调试信息
      console.error('错误详情:', {
        readyState: socket.readyState,
        url: socket.url,
        protocol: socket.protocol,
        error: error
      });
      
      // 触发连接错误回调
      if (messageHandlers.onError) {
        messageHandlers.onError(error);
      }
    };
  } catch (error) {
    console.error('初始化WebSocket失败:', error);
    tryReconnect();
  }

}

/**
 * 尝试重连
 */
function tryReconnect() {
    if (reconnectCount >= MAX_RECONNECT) {
        console.log('已达到最大重连次数，停止重连');
        return;
    }
    
    reconnectCount++;
    console.log(`WebSocket将在${RECONNECT_INTERVAL/1000}秒后尝试第${reconnectCount}次重连`);
    
    if (reconnectTimer) {
        clearTimeout(reconnectTimer);
    }
    
    reconnectTimer = setTimeout(function() {
        console.log(`正在进行第${reconnectCount}次重连...`);
        // 从本地存储获取user-access-token
        let token = localStorage.getItem('user-access-token');
        if (!token) {
            console.warn('无法获取token，重连失败');
            return;
        }
        // 移除Bearer前缀
        if (token.startsWith('Bearer ')) {
            token = token.substring(7);
        }
        initWebSocket(token);
    }, RECONNECT_INTERVAL);
}

/**
 * 开始心跳检测
 */
function startHeartbeat() {
    if (heartbeatTimer) {
        clearInterval(heartbeatTimer);
    }
    
    heartbeatTimer = setInterval(function() {
        if (socket && socket.readyState === WebSocket.OPEN) {
            // 发送心跳消息
            const heartbeatMsg = {
                type: 'HEARTBEAT',
                timestamp: new Date().toISOString()
            };
            socket.send(JSON.stringify(heartbeatMsg));
            console.log('发送心跳消息');
        }
    }, HEARTBEAT_INTERVAL);
}

/**
 * 停止心跳检测
 */
function stopHeartbeat() {
    if (heartbeatTimer) {
        clearInterval(heartbeatTimer);
        heartbeatTimer = null;
    }
}

/**
 * 处理接收到的消息
 * @param {Object} message 消息对象
 */
function handleMessage(message) {
    // 根据消息类型处理
    switch (message.type) {
        case 'EMERGENCY':
            // 检查当前用户是否为管理员
            const userInfo = getUserInfo();
            if (userInfo && userInfo.roleId === 4) { // 角色ID 4为管理员
                handleEmergencyMessage(message);
            } else {
                console.log('收到紧急消息，但当前用户非管理员，忽略');
            }
            break;
        case 'SYSTEM':
            handleSystemMessage(message);
            break;
        case 'HEARTBEAT':
            // 心跳响应，不需要特殊处理
            console.log('收到心跳响应');
            break;
        default:
            console.log('收到未知类型消息:', message);
            // 如果有通用消息处理器，调用它
            if (messageHandlers.onMessage) {
                messageHandlers.onMessage(message);
            }
    }
}

/**
 * 处理紧急消息
 * @param {Object} message 紧急消息对象
 */
function handleEmergencyMessage(message) {
    console.log('收到紧急消息:', message);
    
    // 播放警报声
    playAlertSound();
    
    // 显示紧急弹窗
    showEmergencyAlert(message);
    
    // 如果有紧急消息处理器，调用它
    if (messageHandlers.onEmergency) {
        messageHandlers.onEmergency(message);
    }
}

/**
 * 处理系统消息
 * @param {Object} message 系统消息对象
 */
function handleSystemMessage(message) {
    console.log('收到系统消息:', message);
    
    // 显示系统通知
    showSystemNotification(message);
    
    // 如果有系统消息处理器，调用它
    if (messageHandlers.onSystem) {
        messageHandlers.onSystem(message);
    }
}

/**
 * 播放警报声
 */
function playAlertSound() {
    try {
        // 修改路径以适应Vue项目的公共资源结构
        const audio = new Audio('/static/sounds/emergency-alert.mp3');
        // 设置音量和循环
        audio.volume = 0.8;
        audio.loop = true;
        
        // 播放警报声
        const playPromise = audio.play();
        
        // 存储音频对象以便在关闭弹窗时停止
        window._emergencyAudio = audio;
        
        // 处理播放失败的情况
        if (playPromise !== undefined) {
            playPromise.catch(error => {
                console.error('播放警报声失败:', error);
            });
        }
    } catch (error) {
        console.error('创建音频对象失败:', error);
    }
}

/**
 * 停止警报声
 */
function stopAlertSound() {
    try {
        if (window._emergencyAudio) {
            window._emergencyAudio.pause();
            window._emergencyAudio.currentTime = 0;
            window._emergencyAudio = null;
            console.log('警报声已停止');
        }
    } catch (error) {
        console.error('停止警报声失败:', error);
    }
}

/**
 * 显示紧急弹窗
 * @param {Object} message 紧急消息对象
 */
function showEmergencyAlert(message) {
    // 创建弹窗元素
    const alertDiv = document.createElement('div');
    alertDiv.className = 'emergency-alert';
    alertDiv.innerHTML = `
        <div class="emergency-alert-header">
            <h3>${message.title || '紧急呼叫警报'}</h3>
            <button class="close-btn">&times;</button>
        </div>
        <div class="emergency-alert-body">
            <p>${message.content || '收到紧急呼叫！'}</p>
            <p class="emergency-time">时间: ${formatDateTime(message.timestamp)}</p>
        </div>
        <div class="emergency-alert-footer">
            <button class="handle-btn">立即处理</button>
        </div>
    `;
    
    // 添加样式
    const style = document.createElement('style');
    style.textContent = `
        .emergency-alert {
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            width: 400px;
            background-color: #fff;
            border: 2px solid #ff0000;
            border-radius: 5px;
            box-shadow: 0 0 20px rgba(255, 0, 0, 0.5);
            z-index: 9999;
            animation: blink 1s infinite;
        }
        
        @keyframes blink {
            0% { box-shadow: 0 0 20px rgba(255, 0, 0, 0.5); }
            50% { box-shadow: 0 0 30px rgba(255, 0, 0, 0.8); }
            100% { box-shadow: 0 0 20px rgba(255, 0, 0, 0.5); }
        }
        
        .emergency-alert-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 10px 15px;
            background-color: #ff0000;
            color: #fff;
        }
        
        .emergency-alert-header h3 {
            margin: 0;
            font-size: 18px;
        }
        
        .close-btn {
            background: none;
            border: none;
            color: #fff;
            font-size: 24px;
            cursor: pointer;
        }
        
        .emergency-alert-body {
            padding: 15px;
            max-height: 200px;
            overflow-y: auto;
        }
        
        .emergency-alert-body p {
            margin: 0 0 10px;
            font-size: 16px;
        }
        
        .emergency-time {
            font-size: 14px;
            color: #666;
        }
        
        .emergency-alert-footer {
            padding: 10px 15px;
            text-align: right;
            border-top: 1px solid #eee;
        }
        
        .handle-btn {
            padding: 8px 15px;
            background-color: #ff0000;
            color: #fff;
            border: none;
            border-radius: 3px;
            cursor: pointer;
        }
        
        .handle-btn:hover {
            background-color: #cc0000;
        }
    `;
    
    // 添加到文档
    document.head.appendChild(style);
    document.body.appendChild(alertDiv);
    
    // 添加事件监听
    const closeBtn = alertDiv.querySelector('.close-btn');
    closeBtn.addEventListener('click', function() {
        // 停止警报声
        stopAlertSound();
        // 移除弹窗
        document.body.removeChild(alertDiv);
    });
    
    const handleBtn = alertDiv.querySelector('.handle-btn');
    handleBtn.addEventListener('click', function() {
        // 停止警报声
        stopAlertSound();
        // 跳转到紧急呼叫处理页面
        window.location.href = '/admin/emergency-calls';
        // 移除弹窗
        document.body.removeChild(alertDiv);
    });
    
    // 10秒后自动停止循环警报声，但不关闭弹窗
    setTimeout(function() {
        if (window._emergencyAudio && !window._emergencyAudio.paused) {
            window._emergencyAudio.loop = false;
        }
    }, 10000);
}

/**
 * 显示系统通知
 * @param {Object} message 系统消息对象
 */
function showSystemNotification(message) {
    // 使用浏览器通知API
    if ('Notification' in window) {
        // 检查通知权限
        if (Notification.permission === 'granted') {
            createNotification(message);
        } else if (Notification.permission !== 'denied') {
            // 请求权限
            Notification.requestPermission().then(function(permission) {
                if (permission === 'granted') {
                    createNotification(message);
                }
            });
        }
    }
}

/**
 * 创建浏览器通知
 * @param {Object} message 消息对象
 */
function createNotification(message) {
    const notification = new Notification(message.title || '系统通知', {
        body: message.content || '',
        icon: '/static/images/logo.png'
    });
    
    // 点击通知时的行为
    notification.onclick = function() {
        window.focus();
        if (message.link) {
            window.location.href = message.link;
        }
        notification.close();
    };
    
    // 自动关闭
    setTimeout(function() {
        notification.close();
    }, 10000);
}

/**
 * 格式化日期时间
 * @param {string} dateTimeStr 日期时间字符串
 * @returns {string} 格式化后的日期时间
 */
function formatDateTime(dateTimeStr) {
    if (!dateTimeStr) {
        return '未知时间';
    }
    
    const date = new Date(dateTimeStr);
    
    if (isNaN(date.getTime())) {
        return dateTimeStr;
    }
    
    return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
}

/**
 * 设置消息处理器
 * @param {Object} handlers 消息处理器对象
 */
function setMessageHandlers(handlers) {
    messageHandlers = { ...messageHandlers, ...handlers };
}

/**
 * 关闭WebSocket连接
 */
function closeWebSocket() {
    if (socket) {
        socket.close();
        console.log('WebSocket连接已手动关闭');
    }
    
    stopHeartbeat();
    
    if (reconnectTimer) {
        clearTimeout(reconnectTimer);
        reconnectTimer = null;
    }
    
    initialized = false;
}

// 导出WebSocket客户端API
export default {
    init: initWebSocket,
    close: closeWebSocket,
    setMessageHandlers: setMessageHandlers
};
