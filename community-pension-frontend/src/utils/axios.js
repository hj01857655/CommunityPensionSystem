import axios from 'axios';
import { ElMessage } from 'element-plus';
import router from '@/router';

// 创建axios实例
const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Token管理
const TokenManager = {
  // 前台用户token
  user: {
    get: () => localStorage.getItem('token'),
    set: (token) => localStorage.setItem('token', token),
    remove: () => localStorage.removeItem('token')
  },
  // 后台管理员token
  admin: {
    get: () => localStorage.getItem('admin-token'),
    set: (token) => {
      localStorage.setItem('admin-token', token);
      localStorage.setItem('isLoginIn', 'true');
    },
    remove: () => {
      localStorage.removeItem('admin-token');
      localStorage.removeItem('isLoginIn');
    }
  },
  // 根据当前路径判断使用哪种token
  getCurrentToken: () => {
    const path = window.location.pathname;
    // 如果路径包含admin，则使用admin token
    if (path.includes('/admin')) {
      return TokenManager.admin.get();
    }
    // 否则使用普通用户token
    return TokenManager.user.get();
  },
  // 清除所有token和用户信息
  clearAll: () => {
    localStorage.clear();
  }
};

// 刷新Token的函数
async function refreshToken() {
  const token = TokenManager.getCurrentToken();
  if (!token) return null;
  
  try {
    const response = await axios.post('/api/users/refreshToken', { token });
    if (response.data && response.data.data && response.data.data.token) {
      const newToken = response.data.data.token;
      // 根据当前路径决定存储token的位置
      if (window.location.pathname.includes('/admin')) {
        TokenManager.admin.set(newToken);
      } else {
        TokenManager.user.set(newToken);
      }
      return newToken;
    }
    return null;
  } catch (error) {
    console.error('刷新Token失败:', error);
    handleAuthError();
    return null;
  }
}

// 处理认证错误，根据当前路径重定向
function handleAuthError() {
  TokenManager.clearAll();
  const path = window.location.pathname;
  
  if (path.includes('/admin')) {
    ElMessage.error('登录已过期，请重新登录');
    router.push('/admin/login');
  } else {
    ElMessage.error('登录已过期，请重新登录');
    router.push('/login');
  }
}

// 请求拦截器
axiosInstance.interceptors.request.use(
  (config) => {
    const token = TokenManager.getCurrentToken();
    if (token) {
      config.headers['Authorization'] = `${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
axiosInstance.interceptors.response.use(
  (response) => {
    // 如果响应成功但业务状态码不是200
    if (response.data && response.data.code !== 200) {
      // 如果是401未授权，处理登录过期
      if (response.data.code === 401) {
        handleAuthError();
        return Promise.reject(new Error('登录已过期，请重新登录'));
      }
      
      // 其他业务错误，显示错误信息
      if (response.data.msg) {
        ElMessage.error(response.data.msg);
      }
    }
    return response;
  },
  async (error) => {
    const originalRequest = error.config;
    
    // 如果是401错误且没有重试过
    if (error.response && error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      // 尝试刷新token
      const newToken = await refreshToken();
      if (newToken) {
        // 更新原始请求的Authorization头
        originalRequest.headers['Authorization'] = `${newToken}`;
        // 重新发送原始请求
        return axiosInstance(originalRequest);
      }
    }
    
    // 处理其他错误
    if (error.response) {
      switch (error.response.status) {
        case 400:
          ElMessage.error('请求参数错误');
          break;
        case 403:
          ElMessage.error('没有权限访问该资源');
          break;
        case 404:
          ElMessage.error('请求的资源不存在');
          break;
        case 500:
          ElMessage.error('服务器内部错误');
          break;
        default:
          ElMessage.error(`请求失败: ${error.message}`);
      }
    } else if (error.request) {
      // 请求发送但没有收到响应
      ElMessage.error('网络异常，请检查您的网络连接');
    } else {
      // 请求配置出错
      ElMessage.error(`请求错误: ${error.message}`);
    }
    
    return Promise.reject(error);
  }
);

// 请求方法封装
const http = {
  get(url, params, config = {}) {
    return axiosInstance.get(url, { params, ...config });
  },
  post(url, data, config = {}) {
    return axiosInstance.post(url, data, config);
  },
  put(url, data, config = {}) {
    return axiosInstance.put(url, data, config);
  },
  delete(url, config = {}) {
    return axiosInstance.delete(url, config);
  },
  patch(url, data, config = {}) {
    return axiosInstance.patch(url, data, config);
  }
};

export { TokenManager };
export default axiosInstance;
