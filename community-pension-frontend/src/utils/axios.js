import axios from 'axios';
import ElMessage from 'element-plus';
import router from '@/router';

const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json'
  }
});
// 存储 Token 的函数
function setToken(token) {
  localStorage.setItem('token', token);
}

// 获取 Token 的函数
function getToken() {
  return localStorage.getItem('token');
}
// 刷新 Token 的函数
async function refreshToken() {
  const token = getToken();
  if (token) {
    try {
      const response = await axiosInstance.post('/api/users/refreshToken', { token });
      const newToken = response.data.data.token;
      setToken(newToken);
      return newToken;
    } catch (error) {
      console.log('刷新 Token 失败，请重新登录');
      localStorage.clear();
      router.push("/admin/login")
      return null;
    }
  }
  return null;
}

// 请求拦截器
axiosInstance.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `${token}`;
  }else{

  }
  return config;
}, (error) => {
  return Promise.reject(error);
});

// 响应拦截器
axiosInstance.interceptors.response.use((response) => {
  if (response.data.code === 401) {
    ElMessage.error('登录已过期，请重新登录');
    localStorage.clear();
    router.push("/admin/login")
  }
  return response;
}, async (error) => {
  const originalRequest = error.config;
  if (error.response && error.response.status === 401 && !originalRequest._retry) {
    originalRequest._retry = true;
    const newToken = await refreshToken();
    if (newToken) {
      originalRequest.headers.Authorization = `${newToken}`;
      return axiosInstance(originalRequest);
    } else {
      // 处理刷新失败的情况，例如重新登录
      console.log('刷新 Token 失败，请重新登录');
      localStorage.clear();
      router.push("/admin/login")
    }
  }
  return Promise.reject(error);
});

export default axiosInstance;
