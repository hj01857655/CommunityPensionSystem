//axios拦截

import axios from 'axios';
import router from '@/router';

// 创建axios实例
const instance = axios.create({
    // 获取当前域名
    baseURL: process.env.VUE_APP_API_BASE_URL,
    timeout: 10000,
});

// 请求拦截器
instance.interceptors.request.use(config => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
}, error => {
    return Promise.reject(error);
});

// 响应拦截器
instance.interceptors.response.use(response => {
    return response;
}, error => {
    if (error.response && error.response.status === 401) {
        // 处理未授权错误，例如重定向到登录页面
        localStorage.removeItem('token');
        router.push('/login');
        console.error('未授权，请重新登录');
    }
    return Promise.reject(error);
});

export default instance;
