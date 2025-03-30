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
const TokenManager = {
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
            console.error('Invalid token format');
            return true;
        }
        const decoded = jwtDecode(tokenValue);
        if (decoded.exp < Date.now() / 1000) {
            console.warn("Token 已过期,请重新登录", new Date(decoded.exp * 1000).toLocaleString());
        }
        return decoded.exp < Date.now() / 1000;
    } catch (error) {
        console.error('Token decode error:', error);
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
            return response.data.accessToken;
        }else{
            throw new Error(response.data.message || '刷新令牌失败');
        }
    } catch (error) {
        console.error('刷新令牌错误:', error);
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
        // console.log(config)
        // 登录接口不需要token
        const loginPaths = ['/api/auth/login', '/api/auth/adminLogin'];  // 更新登录路径
        if (loginPaths.some(path => config.url === path)) {
            console.log('登录请求，跳过token验证:', {
                url: config.url,
                method: config.method,
                data: config.data
            });
            return config;
        }
        
        console.log('发送请求:', {
            url: config.url,
            method: config.method,
            params: config.params,
            data: config.data,
            headers: config.headers
        });
        
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
        console.error('请求拦截器错误:', error);
        return Promise.reject(error);
    }
);

// 响应拦截器
instance.interceptors.response.use(
    async response => {
        // 调试日志
        // console.log("响应拦截器", response);
        console.log("收到响应:", {
            url: response.config.url,
            status: response.status,
            data: response.data
        });
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
        console.error("详细响应拦截器错误:", error);
        console.error("错误响应:", error.response);
        
        const { response, config } = error;
        
        // 网络连接错误处理
        if (!response) {
            console.error('网络连接异常');
            return Promise.reject(new Error('网络连接失败，请检查网络后重试'));
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
                return '认证失败，请重新登录';
            },
            403: () => '权限不足，请联系管理员',
            404: () => `资源不存在: ${config.url}`,
            500: () => '服务器繁忙，请稍后重试',
            default: () => response.data?.message || '未知错误'
        };

        const message = await (errorHandler[response.status]?.() || errorHandler.default());
        return Promise.reject(new Error(message));
    }
);

// 导出token管理器
export { TokenManager };

export default instance;