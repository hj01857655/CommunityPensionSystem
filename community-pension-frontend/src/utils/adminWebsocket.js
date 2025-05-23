/**
 * 后台管理系统WebSocket客户端
 * 用于连接WebSocket服务器，接收紧急呼叫和系统通知
 */
import { ElNotification } from 'element-plus';
// 检查是否有通知存储模块，如果没有就不使用

// WebSocket连接
let socket;
// 消息处理回调
let messageHandlers = {
  onEmergency: null,
  onSystem: null,
  onConnected: null,
  onDisconnected: null,
  onError: null
};
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

/**
 * 初始化WebSocket连接
 * @param {boolean} force 是否强制初始化，即使已经初始化过
 * @param {number} delay 延迟初始化的时间（毫秒）
 */
export function initWebSocket(token, force = false, delay = 0) {
  if (initialized && !force) {
    console.log('WebSocket已初始化');
    return;
  }
  
  // 如果需要延迟初始化
  if (delay > 0) {
    console.log(`将在${delay/1000}秒后初始化WebSocket`);
    setTimeout(() => initWebSocket(token, force, 0), delay);
    return;
  }

  // 尝试从多个来源获取token
  if (!token) {
    // 获取后台管理员token（使用sessionStorage）
    token = sessionStorage.getItem('admin-access-token');
    
    // 如果没有获取到，尝试从local storage获取用户ID
    if (!token) {
      const userInfo = sessionStorage.getItem('userInfo');
      if (userInfo) {
        try {
          const userObj = JSON.parse(userInfo);
          if (userObj && userObj.userId) {
            console.log('从UserInfo获取到用户ID:', userObj.userId);
            // 直接使用用户ID作为token
            token = userObj.userId.toString();
          }
        } catch (error) {
          console.warn('解析userInfo失败:', error);
        }
      }
    }
  }
  
  if (!token) {
    console.warn('无法获取管理员token或用户ID，无法初始化WebSocket');
    return;
  }
  
  // 移除Bearer前缀，因为WebSocket连接不需要
  if (token.startsWith('Bearer ')) {
    token = token.substring(7);
  }
  
  // 动态获取后端地址，而不是硬编码
  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
  
  // 从当前页面URL获取主机和端口
  let host = window.location.hostname;
  let port = '9000'; // 默认后端端口
  
  // 如果是开发环境，使用localhost:9000
  if (host === 'localhost' && window.location.port === '5173') {
    host = 'localhost';
  } else if (process.env.NODE_ENV === 'production') {
    // 生产环境可能使用相同的主机，但端口不同
    // 可以根据实际部署情况调整
    port = '9000';
  }
  
  const wsUrl = `${protocol}//${host}:${port}/websocket/${token}`;
  console.log('连接到后端 WebSocket 服务器:', wsUrl);
  console.log('token 信息:', token.substring(0, 10) + '...');
  console.log('token 长度:', token.length);
  console.log('正在初始化 WebSocket 连接...');
  
  
  try {
    console.log('正在创建WebSocket对象...');
    socket = new WebSocket(wsUrl);
    console.log('WebSocket对象创建成功，等待连接...');
    
    // 添加连接超时处理
    const connectionTimeout = setTimeout(() => {
      if (socket.readyState !== WebSocket.OPEN) {
        console.error('WebSocket连接超时');
        socket.close();
        tryReconnect();
      }
    }, 5000); // 5秒连接超时
    
    // 连接打开事件
    socket.onopen = function(event) {
      clearTimeout(connectionTimeout); // 清除连接超时定时器
      console.log('%cWebSocket连接成功!', 'color: green; font-weight: bold');
      console.log('连接详情:', event);
      reconnectCount = 0;
      startHeartbeat();
      initialized = true;
      
      // 触发连接成功回调
      if (messageHandlers.onConnected) {
        messageHandlers.onConnected();
      }
      
      // 发送一个测试消息
      try {
        const testMsg = {
          type: 'CONNECT',
          timestamp: new Date().toISOString(),
          content: '管理员客户端连接成功'
        };
        socket.send(JSON.stringify(testMsg));
        console.log('发送测试消息成功');
      } catch (e) {
        console.error('发送测试消息失败:', e);
      }
    };
    
    // 接收消息事件
    socket.onmessage = function(event) {
      console.log('%c收到WebSocket消息!', 'color: blue; font-weight: bold');
      try {
        const message = JSON.parse(event.data);
        console.log('消息内容:', message);
        
        // 处理不同类型的消息
        handleMessage(message);
      } catch (e) {
        console.error('解析消息失败:', e, event.data);
      }
    };
    
    // 连接关闭事件
    socket.onclose = function(event) {
      clearTimeout(connectionTimeout); // 清除连接超时定时器
      console.warn('%cWebSocket连接关闭!', 'color: orange; font-weight: bold', event);
      
      // 输出调试信息
      console.warn('关闭详情:', {
        code: event.code,
        reason: event.reason,
        wasClean: event.wasClean,
        type: event.type
      });
      
      // 分析关闭原因
      let closeReason = '';
      let shouldReconnect = true; // 默认应该重连
      let reconnectDelay = reconnectCount * 2000; // 指数退避重连延迟
      
      switch(event.code) {
        case 1000:
          closeReason = '正常关闭';
          // 即使是正常关闭也尝试重连，但使用较短的延迟
          reconnectDelay = 1000;
          break;
        case 1001:
          closeReason = '端点离开';
          break;
        case 1002:
          closeReason = '协议错误';
          break;
        case 1003:
          closeReason = '收到不可接受的数据';
          break;
        case 1006:
          closeReason = '连接异常关闭(可能是token验证失败)';
          // 如果是认证问题，可能需要重新获取token
          // 尝试使用新token重连
          setTimeout(() => {
            const newToken = sessionStorage.getItem('admin-access-token');
            if (newToken && newToken !== token) {
              console.log('检测到新token，尝试使用新token重连');
              initWebSocket(newToken, true);
              return;
            }
          }, 1000);
          break;
        case 1007:
          closeReason = '数据格式不一致';
          break;
        case 1008:
          closeReason = '违反策略';
          break;
        case 1009:
          closeReason = '消息过大';
          break;
        case 1010:
          closeReason = '客户端期望协商扩展';
          break;
        case 1011:
          closeReason = '服务器遇到意外情况';
          break;
        case 1015:
          closeReason = 'TLS握手失败';
          break;
        default:
          closeReason = `未知原因(代码: ${event.code})`;
      }
      console.warn('关闭原因:', closeReason);
      
      // 清除心跳检测
      clearInterval(heartbeatTimer);
      
      // 如果应该重连且未超过最大重连次数
      if (shouldReconnect && reconnectCount < MAX_RECONNECT) {
        reconnectCount++;
        console.log(`将在 ${reconnectDelay/1000} 秒后尝试重新连接，这是第 ${reconnectCount} 次尝试...`);
        
        // 使用延迟重连，避免频繁重连导致服务器负载过高
        setTimeout(() => {
          console.log(`正在尝试第 ${reconnectCount} 次重连...`);
          initWebSocket(token, true); // 强制重新初始化
        }, reconnectDelay);
      } else if (!shouldReconnect) {
        console.log('根据关闭原因决定不再重连');
        initialized = false;
        
        // 触发断开连接回调
        if (messageHandlers.onDisconnected) {
          messageHandlers.onDisconnected(event);
        }
      } else {
        console.error('超过最大重连次数，不再尝试重连');
        initialized = false;
        
        // 触发断开连接回调
        if (messageHandlers.onDisconnected) {
          messageHandlers.onDisconnected(event);
        }
      }
    };
    
    // 连接错误事件
    socket.onerror = function(error) {
      clearTimeout(connectionTimeout); // 清除连接超时定时器
      console.error('%cWebSocket连接错误!', 'color: red; font-weight: bold', error);
      
      // 输出更多调试信息
      console.error('错误详情:', {
        readyState: socket.readyState,
        url: socket.url,
        protocol: socket.protocol,
        error: error
      });
      
      // 测试后端可用性
      testBackendAvailability();
      
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
 * 测试后端可用性
 * 通过发送一个简单的HTTP请求来检查后端服务是否可用
 */
function testBackendAvailability() {
  console.log('正在测试后端服务可用性...');
  
  // 发送一个简单的GET请求到后端的健康检查接口
  fetch('/api/health')
    .then(response => {
      console.log('后端服务状态码:', response.status);
      if (response.ok) {
        console.log('后端服务可用，WebSocket连接失败可能是由于其他原因');
      } else {
        console.error('后端服务返回非200状态码，可能不可用');
      }
    })
    .catch(error => {
      console.error('后端服务请求失败，可能不可用:', error);
    });
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
  
  reconnectTimer = setTimeout(() => {
    console.log(`正在进行第${reconnectCount}次重连...`);
    
    // 获取后台管理员token（使用sessionStorage）
    let token = sessionStorage.getItem('admin-access-token');
    
    // 如果没有获取到，尝试从用户信息中获取用户ID
    if (!token) {
      const userInfo = sessionStorage.getItem('userInfo');
      if (userInfo) {
        try {
          const userObj = JSON.parse(userInfo);
          if (userObj && userObj.userId) {
            console.log('从用户信息中获取到用户ID:', userObj.userId);
            token = userObj.userId.toString();
          }
        } catch (error) {
          console.warn('解析用户信息失败:', error);
        }
      }
    }
    
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

// 存储已处理的紧急消息类型
// 用于过滤不同类型的紧急消息，比如“紧急呼叫警报”和“紧急呼叫通知”
let lastEmergencyTime = 0;
const EMERGENCY_COOLDOWN = 5000; // 5秒内只处理一条紧急消息

/**
 * 处理接收到的消息
 * @param {Object} message 消息对象
 */
function handleMessage(message) {
  // 根据消息类型处理
  switch (message.type) {
    case 'EMERGENCY':
      // 时间限制去重逻辑
      const now = Date.now();
      if (now - lastEmergencyTime < EMERGENCY_COOLDOWN) {
        console.log('在冷却时间内收到紧急消息，忽略:', message);
        return;
      }
      
      // 更新最后一次紧急消息时间
      lastEmergencyTime = now;
      
      // 处理紧急消息
      handleEmergencyMessage(message);
      break;
    case 'SYSTEM':
      handleSystemMessage(message);
      break;
    case 'HEARTBEAT_RESPONSE':
      console.log('收到心跳响应');
      break;
    default:
      console.log('收到未知类型消息:', message);
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
  
  // 在控制台记录紧急消息，便于调试
  console.warn('紧急呼叫警报！', {
    title: message.title || '紧急通知',
    content: message.content || '',
    time: new Date().toLocaleString()
  });
  
  // 触发紧急消息回调
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
  
  // 显示通知
  showSystemNotification(message);
  
  // 在控制台记录系统消息
  console.info('系统通知:', {
    title: message.title || '系统通知',
    content: message.content || '',
    time: new Date().toLocaleString()
  });
  
  // 触发系统消息回调
  if (messageHandlers.onSystem) {
    messageHandlers.onSystem(message);
  }
}

/**
 * 播放警报声
 * 使用Web Audio API尝试绕过浏览器的自动播放限制
 */
function playAlertSound() {
  try {
    // 定时闪烁页面标题
    let originalTitle = document.title;
    let flashTitle = '【紧急警报】' + originalTitle;
    let titleFlashing = true;
    
    // 存储闪烁定时器以便在关闭弹窗时停止
    window._emergencyTitleInterval = setInterval(() => {
      document.title = titleFlashing ? flashTitle : originalTitle;
      titleFlashing = !titleFlashing;
    }, 500);
    
    // 使用HTML5 Audio API播放警报声
    // 浏览器安全策略要求音频播放必须由用户交互触发
    try {
      // 如果已经创建了音频元素，直接使用
      if (!window._emergencyAudio) {
        // 创建音频元素
        const audio = new Audio('/static/sounds/emergency-alert.mp3');
        audio.volume = 0.8;
        audio.loop = true;
        window._emergencyAudio = audio;
        
        // 预加载音频
        audio.load();
        
        // 添加音频播放错误处理
        audio.onerror = (e) => {
          console.error('警报声播放错误:', e);
        };
      }
      
      // 尝试播放音频
      // 使用Promise包装play()方法，因为它返回Promise
      const playPromise = window._emergencyAudio.play();
      
      // 处理自动播放策略限制
      if (playPromise !== undefined) {
        playPromise.catch(error => {
          // 自动播放被阻止，显示一个播放按钮
          console.warn('由于浏览器安全策略，无法自动播放警报声，请点击弹窗上的按钮手动播放', error);
          
          // 将音频播放按钮添加到弹窗中（在showEmergencyAlert函数中处理）
          window._needManualPlayAudio = true;
        });
      }
      
      // 10秒后停止循环
      setTimeout(() => {
        if (window._emergencyAudio && !window._emergencyAudio.paused) {
          window._emergencyAudio.loop = false;
        }
      }, 10000);
    } catch (error) {
      console.error('创建警报声播放器失败:', error);
    }
    
    // 10秒后自动停止标题闪烁
    setTimeout(function() {
      if (window._emergencyTitleInterval) {
        clearInterval(window._emergencyTitleInterval);
        document.title = originalTitle;
      }
    }, 10000);
    
    // 使用浏览器通知API（如果支持）
    if ('Notification' in window && Notification.permission === 'granted') {
      new Notification('紧急呼叫警报', {
        body: '收到紧急呼叫，请立即查看！',
        icon: '/favicon.ico'
      });
    }
  } catch (error) {
    console.error('创建警报失败:', error);
  }
}

/**
 * 使用传统Audio元素的后备方案
 */
function fallbackToTraditionalAudio() {
  try {
    const audio = new Audio('/static/sounds/emergency-alert.mp3');
    audio.volume = 0.8;
    audio.loop = true;
    const playPromise = audio.play();
    window._emergencyAudio = audio;
    
    if (playPromise !== undefined) {
      playPromise.catch(error => {
        console.warn('传统Audio播放失败，可能由于浏览器限制:', error);
      });
    }
  } catch (error) {
    console.error('创建传统Audio元素失败:', error);
  }
}

/**
 * 用户交互触发的警报声播放
 * 这个函数在用户点击按钮后调用，可以绕过浏览器的自动播放限制
 */
function playAlertSoundWithUserGesture() {
  try {
    // 先停止之前的音频（如果有）
    stopAlertSound();
    
    // 使用Web Audio API播放警报声
    if (window.AudioContext || window.webkitAudioContext) {
      // 创建Audio Context
      const AudioContext = window.AudioContext || window.webkitAudioContext;
      const audioContext = new AudioContext();
      window._emergencyAudioContext = audioContext;
      
      // 加载警报声文件
      fetch('/static/sounds/emergency-alert.mp3')
        .then(response => response.arrayBuffer())
        .then(arrayBuffer => audioContext.decodeAudioData(arrayBuffer))
        .then(audioBuffer => {
          // 创建音频源
          const source = audioContext.createBufferSource();
          source.buffer = audioBuffer;
          
          // 创建音量控制
          const gainNode = audioContext.createGain();
          gainNode.gain.value = 0.8; // 设置音量
          
          // 连接音频节点
          source.connect(gainNode);
          gainNode.connect(audioContext.destination);
          
          // 设置循环
          source.loop = true;
          
          // 开始播放
          source.start(0);
          
          // 存储音频源以便停止
          window._emergencyAudioSource = source;
          window._emergencyAudioGain = gainNode;
          
          console.log('用户交互触发的警报声播放成功');
        })
        .catch(error => {
          console.error('加载警报声文件失败:', error);
          // 如果Web Audio API失败，尝试使用传统Audio元素
          playTraditionalAudioWithUserGesture();
        });
    } else {
      // 浏览器不支持Web Audio API，尝试使用传统Audio元素
      playTraditionalAudioWithUserGesture();
    }
  } catch (error) {
    console.error('创建警报失败:', error);
    // 尝试使用传统Audio元素
    playTraditionalAudioWithUserGesture();
  }
}

/**
 * 用户交互触发的传统Audio元素播放
 */
function playTraditionalAudioWithUserGesture() {
  try {
    const audio = new Audio('/static/sounds/emergency-alert.mp3');
    audio.volume = 0.8;
    audio.loop = true;
    const playPromise = audio.play();
    window._emergencyAudio = audio;
    
    if (playPromise !== undefined) {
      playPromise.then(() => {
        console.log('传统Audio播放成功');
      }).catch(error => {
        console.warn('传统Audio播放失败，即使有用户交互:', error);
      });
    }
  } catch (error) {
    console.error('创建传统Audio元素失败:', error);
  }
}

/**
 * 停止警报提示
 */
function stopAlertSound() {
  // 停止传统Audio元素（如果存在）
  if (window._emergencyAudio) {
    window._emergencyAudio.pause();
    window._emergencyAudio.currentTime = 0;
    window._emergencyAudio = null;
  }
  
  // 停止Web Audio API音频（如果存在）
  if (window._emergencyAudioSource) {
    try {
      window._emergencyAudioSource.stop();
      window._emergencyAudioSource.disconnect();
      window._emergencyAudioSource = null;
    } catch (error) {
      console.warn('停止Web Audio源失败:', error);
    }
  }
  
  // 关闭Audio Context（如果存在）
  if (window._emergencyAudioContext && window._emergencyAudioContext.state !== 'closed') {
    try {
      window._emergencyAudioContext.close();
    } catch (error) {
      console.warn('关闭Audio Context失败:', error);
    }
    window._emergencyAudioContext = null;
  }
  
  // 释放音量控制节点
  if (window._emergencyAudioGain) {
    try {
      window._emergencyAudioGain.disconnect();
    } catch (error) {
      console.warn('断开音量节点失败:', error);
    }
    window._emergencyAudioGain = null;
  }
  
  // 停止标题闪烁
  if (window._emergencyTitleInterval) {
    clearInterval(window._emergencyTitleInterval);
    window._emergencyTitleInterval = null;
    document.title = document.title.replace(/【紧急警报】/g, '');
  }
}

/**
 * 格式化日期时间
 * @param {string} dateTimeStr 日期时间字符串
 * @returns {string} 格式化后的日期时间
 */
function formatDateTime(dateTimeStr) {
  if (!dateTimeStr) return new Date().toLocaleString();
  try {
    return new Date(dateTimeStr).toLocaleString();
  } catch (e) {
    return dateTimeStr;
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
  
  // 准备弹窗内容
  let footerContent = `<button class="handle-btn">立即处理</button>`;
  
  // 如果需要手动播放音频，添加播放按钮
  if (window._needManualPlayAudio) {
    footerContent = `<button class="play-sound-btn">播放警报声</button>` + footerContent;
  }
  
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
          ${footerContent}
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
          display: flex;
          justify-content: flex-end;
          gap: 10px;
      }
      
      .play-sound-btn {
          padding: 8px 15px;
          background-color: #ff9800;
          color: #fff;
          border: none;
          border-radius: 3px;
          cursor: pointer;
      }
      
      .play-sound-btn:hover {
          background-color: #e68a00;
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
  
  // 如果有播放按钮，添加点击事件
  const playSoundBtn = alertDiv.querySelector('.play-sound-btn');
  if (playSoundBtn) {
    playSoundBtn.addEventListener('click', function() {
      // 用户交互触发的音频播放
      if (window._emergencyAudio) {
        window._emergencyAudio.play()
          .then(() => {
            console.log('用户交互触发的音频播放成功');
            // 播放成功后隐藏按钮
            playSoundBtn.style.display = 'none';
          })
          .catch(error => {
            console.error('即使有用户交互也无法播放音频:', error);
          });
      }
    });
  }
  
  const handleBtn = alertDiv.querySelector('.handle-btn');
  handleBtn.addEventListener('click', function() {
    // 停止警报声
    stopAlertSound();
    // 跳转到紧急呼叫处理页面
    window.location.href = '/admin/emergency-calls';
    // 移除弹窗
    document.body.removeChild(alertDiv);
  });
  
  // 尝试自动播放警报声
  playAlertSound();
}

/**
 * 显示系统通知
 * @param {Object} message 系统消息对象
 */
function showSystemNotification(message) {
  ElNotification({
    title: message.title || '系统通知',
    message: message.content || '',
    type: 'info',
    duration: 5000
  });
}

/**
 * 关闭WebSocket连接
 */
export function closeWebSocket() {
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

/**
 * 检查WebSocket是否已连接
 * @returns {boolean} 是否已连接
 */
export function isWebSocketConnected() {
  return socket && socket.readyState === WebSocket.OPEN;
}

/**
 * 设置消息处理器
 * @param {Object} handlers 消息处理器对象
 */
export function setMessageHandlers(handlers) {
  messageHandlers = { ...messageHandlers, ...handlers };
  console.log('设置消息处理器:', messageHandlers);
}

/**
 * 发送消息到WebSocket服务器
 * @param {string} type 消息类型
 * @param {Object} data 消息数据
 * @returns {boolean} 是否发送成功
 */
function sendMessage(type, data) {
  if (!socket || socket.readyState !== WebSocket.OPEN) {
    console.error('WebSocket未连接，无法发送消息');
    return false;
  }

  try {
    const message = {
      type: type,
      data: data,
      timestamp: new Date().toISOString()
    };
    
    socket.send(JSON.stringify(message));
    console.log('消息已发送:', message);
    return true;
  } catch (error) {
    console.error('发送消息失败:', error);
    return false;
  }
}

// 导出WebSocket客户端API
export default {
  init: initWebSocket,
  close: closeWebSocket,
  setMessageHandlers: setMessageHandlers,
  isConnected: isWebSocketConnected,
  testConnection: testSimpleConnection,
  sendMessage: sendMessage,
  // 添加错误处理回调
  onError: function(callback) {
    if (typeof callback === 'function') {
      messageHandlers.onError = callback;
    }
  },
  // 添加连接关闭回调
  onClose: function(callback) {
    if (typeof callback === 'function') {
      messageHandlers.onDisconnected = callback;
    }
  }
}

/**
 * 测试简单的WebSocket连接，不带Token验证
 * 用于测试后端WebSocket服务是否正常
 */
export function testSimpleConnection() {
  try {
    // 尝试连接一个简单的WebSocket，不带Token
    console.log('尝试简单WebSocket连接测试...');
    
    // 使用echo测试服务
    const testSocket = new WebSocket('wss://echo.websocket.org');
    
    testSocket.onopen = function() {
      console.log('%c测试WebSocket连接成功!', 'color: green; font-weight: bold');
      
      // 发送测试消息
      testSocket.send('WebSocket测试消息');
    };
    
    testSocket.onmessage = function(event) {
      console.log('%c收到测试回显消息:', 'color: blue; font-weight: bold', event.data);
      
      // 测试成功，关闭连接
      testSocket.close();
    };
    
    testSocket.onerror = function(error) {
      console.error('%c测试WebSocket连接错误!', 'color: red; font-weight: bold', error);
    };
    
    testSocket.onclose = function() {
      console.log('测试WebSocket连接关闭');
    };
    
    return true;
  } catch (error) {
    console.error('测试WebSocket连接失败:', error);
    return false;
  }
}
