import axios from "axios";
import { jwtDecode } from 'jwt-decode';

const instance = axios.create({
    baseURL: "/",  // 使用代理路径
    timeout: 5000,
    headers: {
        'Content-Type': 'application/json;charset=UTF-8',
        'Accept': 'application/json'
    }
});
// 统一处理token格式
const formatToken = (token) => {
    return token.startsWith('Bearer ') ? token : `Bearer ${token}`;
};

// 存储类型配置
const storageConfig = {
    // 前台使用本地存储，后台使用会话存储
    user: 'localStorage',
    admin: 'sessionStorage',
    
    // 获取存储对象
    getStorage(type) {
        return type === 'sessionStorage' ? sessionStorage : localStorage;
    }
};

// Token管理器
export const TokenManager = {
    // 用户相关的token管理（使用本地存储）
    user: {
        getAccessToken: () => storageConfig.getStorage(storageConfig.user).getItem("user-access-token"),
        getRefreshToken: () => storageConfig.getStorage(storageConfig.user).getItem("user-refresh-token"),
        set: (accessToken, refreshToken) => {
            const formattedAccessToken = formatToken(accessToken);
            storageConfig.getStorage(storageConfig.user).setItem("user-access-token", formattedAccessToken);
            if (refreshToken) {
                const formattedRefreshToken = formatToken(refreshToken);
                storageConfig.getStorage(storageConfig.user).setItem("user-refresh-token", formattedRefreshToken);
            }
        },
        clear: () => {
            storageConfig.getStorage(storageConfig.user).removeItem("user-access-token");
            storageConfig.getStorage(storageConfig.user).removeItem("user-refresh-token");
            storageConfig.getStorage(storageConfig.user).clear();
        }
    },
    // 管理员相关的token管理（使用会话存储）
    admin: {
        getAccessToken: () => storageConfig.getStorage(storageConfig.admin).getItem("admin-access-token"),
        getRefreshToken: () => storageConfig.getStorage(storageConfig.admin).getItem("admin-refresh-token"),
        set: (accessToken, refreshToken) => {
            const formattedAccessToken = formatToken(accessToken);
            storageConfig.getStorage(storageConfig.admin).setItem("admin-access-token", formattedAccessToken);
            if (refreshToken) {
                const formattedRefreshToken = formatToken(refreshToken);
                storageConfig.getStorage(storageConfig.admin).setItem("admin-refresh-token", formattedRefreshToken);
            }
        },
        clear: () => {
            storageConfig.getStorage(storageConfig.admin).removeItem("admin-access-token");
            storageConfig.getStorage(storageConfig.admin).removeItem("admin-refresh-token");
            storageConfig.getStorage(storageConfig.admin).clear();
        }
    }
};

// 导出存储配置
export { storageConfig };

// 判断token是否过期
const isTokenExpired = (token) => {
    if (!token) return true;
    try {
        const tokenValue = token.startsWith('Bearer ') ? token.substring(7) : token;
        if (!tokenValue.includes('.') || tokenValue.split('.').length !== 3) {
            return true;
        }
        const decoded = jwtDecode(tokenValue);
        return decoded.exp < Date.now() / 1000;
    } catch (error) {
        return true;
    }
};

// 刷新用户token
const refreshUserToken = async () => {
    const refreshToken = TokenManager.user.getRefreshToken();
    const response = await axios.post('/api/auth/refresh', {}, {
        headers: {
            'Refresh-Token': refreshToken
        }
    });
    return response.data.accessToken;
};

// 刷新管理员token
const refreshAdminToken = async () => {
    try {
        const refreshToken = TokenManager.admin.getRefreshToken();
        if (!refreshToken) {
            throw new Error('刷新令牌不存在');
        }
        
        const response = await axios.post('/api/auth/refresh', {}, {
            headers: {
                'Refresh-Token': refreshToken
            }
        });
        
        if(response.status==200 && response.data.code==200){
            const {accessToken, refreshToken: newRefreshToken} = response.data;
            // 更新本地存储的 token
            TokenManager.admin.set(accessToken, newRefreshToken);
            return accessToken;
        }else{
            throw new Error(response.data.message || '刷新令牌失败');
        }
    } catch (error) {
        throw new Error('刷新令牌过程中发生错误，请重新登录');
    }
};

// 用于存储等待刷新token的回调函数
let refreshSubscribers = [];
// 标记是否正在刷新token
let isRefreshing = false;

// 添加请求拦截器
instance.interceptors.request.use(
    config => {
        // 登录接口不需要token
        const loginPaths = ['/api/auth/login', '/api/auth/adminLogin'];  // 更新登录路径
        if (loginPaths.some(path => config.url === path)) {
            return config;
        }
        
        // 如果是FormData类型的请求(文件上传)，不要修改Content-Type
        if (config.data instanceof FormData) {
            // 删除默认的Content-Type，让浏览器自动添加包含boundary的正确值
            delete config.headers['Content-Type'];
        }
        
        // 根据当前路径确定是用户还是管理员
        const isAdmin = window.location.pathname.includes('/admin/');
        const token = isAdmin ? 
            TokenManager.admin.getAccessToken() : 
            TokenManager.user.getAccessToken();
        
        if (token) {
            config.headers['Authorization'] = formatToken(token);
        }
        return config;
    },
    async error => {
        return Promise.reject(error);
    }
);

// 响应拦截器
instance.interceptors.response.use(
    async response => {
        // 处理二进制数据
        if (response.config.responseType === 'blob') {
            return response;
        }

        // 处理成功响应
        if (response.status === 200) {
            const responseData = response.data;
            
            // 处理token刷新
            if (responseData.data) {
                const { accessToken, refreshToken } = responseData.data;
                if (accessToken && refreshToken) {
                    const isAdminRequest = window.location.pathname.includes('/admin/');
                    TokenManager[isAdminRequest ? 'admin' : 'user'].set(accessToken, refreshToken);
                }
            }

            // 统一返回格式
            return {
                code: responseData.code || response.status,
                data: responseData.data || responseData,
                message: responseData.message || 'success'
            };
        }

        // 其他情况返回原始响应
        return response;
    },
    async error => {
        const { response, config } = error;
        
        // 网络连接错误处理
        if (!response) {
            return Promise.reject(new Error('网络连接失败，请检查网络后重试'));
        }

        // 提取后端返回的错误信息
        let errorMessage = '请求失败';
        if (response.data) {
            if (typeof response.data === 'string') {
                try {
                    const parsedError = JSON.parse(response.data);
                    errorMessage = parsedError.message || parsedError.msg || parsedError.error || response.data;
                } catch (e) {
                    errorMessage = response.data;
                }
            } else if (response.data.message || response.data.msg || response.data.error) {
                errorMessage = response.data.message || response.data.msg || response.data.error;
            }
        }

        // 统一错误处理
        const errorHandler = {
            400: () => '请求参数错误',
            401: async () => {
                if (!config.url.includes('/api/auth/')) {
                    const isAdmin = window.location.pathname.includes('/admin/');
                    const refreshToken = isAdmin ? TokenManager.admin.getRefreshToken() : TokenManager.user.getRefreshToken();
                    
                    if (!isRefreshing) {
                        isRefreshing = true;
                        try {
                            const newToken = isAdmin ? await refreshAdminToken() : await refreshUserToken();
                            TokenManager[isAdmin ? 'admin' : 'user'].set(newToken);
                            isRefreshing = false;
                            refreshSubscribers.forEach(cb => cb(newToken));
                            refreshSubscribers = [];
                            // 重试当前请求
                            config.headers.Authorization = formatToken(newToken);
                            return instance(config);
                        } catch (e) {
                            TokenManager[isAdmin ? 'admin' : 'user'].clear();
                            window.location.href = isAdmin ? '/admin/login' : '/login';
                        }
                    }
                    
                    return new Promise(resolve => {
                        refreshSubscribers.push(token => {
                            config.headers.Authorization = formatToken(token);
                            resolve(instance(config));
                        });
                    });
                }
                
                return '登录状态已过期，请重新登录';
            },
            403: () => '您没有权限访问该资源',
            404: () => '请求的资源不存在',
            500: () => {
                // 针对活动状态API的特殊处理
                if (config.url.includes('/api/activity/register/status/')) {
                    // 这可能是因为用户未注册该活动，我们返回一个特定的错误
                    return { isSpecialError: true, type: 'NOT_REGISTERED', message: '未找到报名记录' };
                }
                
                // 应用相关的特殊错误处理
                if (errorMessage.includes('不存在') || errorMessage.includes('未找到')) {
                    return { isSpecialError: true, type: 'NOT_FOUND', message: errorMessage };
                }
                
                return errorMessage || '服务器繁忙，请稍后重试';
            },
            502: () => '网关错误',
            503: () => '服务不可用',
            504: () => '网关超时',
        };

        // 处理错误
        const handler = errorHandler[response.status];
        const errMsg = handler ? await handler() : errorMessage || `未知错误(${response.status})`;
        
        // 如果是特殊处理的错误，直接返回
        if (errMsg && typeof errMsg === 'object' && errMsg.isSpecialError) {
            return Promise.reject(new Error(errMsg.message));
        }
        
        // 一般错误直接返回错误消息
        return Promise.reject(new Error(errMsg));
    }
);

export default instance;