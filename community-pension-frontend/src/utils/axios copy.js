import axios from "axios";
import { jwtDecode } from 'jwt-decode';

const axiosInstance = axios.create({
    baseURL: "http://localhost:9000",
    headers: {
        'Content-Type': 'application/json'
    }
});

const TokenManager = {
    // 用户相关的token管理
    user: {
        getAccessToken: () => {
            const token = localStorage.getItem("user-access-token");
            return token;
        },
        getRefreshToken: () => {
            const token = localStorage.getItem("user-refresh-token");
            return token;
        },
        set: (accessToken, refreshToken) => {
            // 确保accessToken带有Bearer前缀
            const formattedAccessToken = accessToken.startsWith('Bearer ') ? accessToken : `Bearer ${accessToken}`;
            localStorage.setItem("user-access-token", formattedAccessToken);
            if (refreshToken) {
                // 确保refreshToken带有Bearer前缀
                const formattedRefreshToken = refreshToken.startsWith('Bearer ') ? refreshToken : `Bearer ${refreshToken}`;
                localStorage.setItem("user-refresh-token", formattedRefreshToken);
            }
        },
        clear: () => {
            localStorage.removeItem("user-access-token");
            localStorage.removeItem("user-refresh-token");
        }
    },
    // 管理员相关的token管理
    admin: {
        getAccessToken: () => {
            const token = localStorage.getItem("admin-access-token");
            return token;
        },
        getRefreshToken: () => {
            const token = localStorage.getItem("admin-refresh-token");
            return token;
        },
        set: (accessToken, refreshToken) => {
            // 确保accessToken带有Bearer前缀
            const formattedAccessToken = accessToken.startsWith('Bearer ') ? accessToken : `Bearer ${accessToken}`;
            localStorage.setItem("admin-access-token", formattedAccessToken);
            if (refreshToken) {
                // 确保refreshToken带有Bearer前缀
                const formattedRefreshToken = refreshToken.startsWith('Bearer ') ? refreshToken : `Bearer ${refreshToken}`;
                localStorage.setItem("admin-refresh-token", formattedRefreshToken);
            }
        },
        clear: () => {
            localStorage.removeItem("admin-access-token");
            localStorage.removeItem("admin-refresh-token");
        }
    }
};

// 判断token是否过期
const isTokenExpired = (token) => {
    if (!token) return true;
    try {
        // 如果token带有Bearer前缀，去掉前缀
        const tokenValue = token.startsWith('Bearer ') ? token.substring(7) : token;
        
        // 检查token格式是否正确
        if (!tokenValue.includes('.') || tokenValue.split('.').length !== 3) {
            console.error('Invalid token format');
            return true;
        }
        const decoded = jwtDecode(tokenValue);
        if (decoded.exp < Date.now() / 1000) {
            console.warn("Token 已过期,请重新登录", new Date(decoded.exp * 1000).toLocaleString())
        }
        return decoded.exp < Date.now() / 1000;
    } catch (error) {
        console.error('Token decode error:', error);
        return true;
    }
};

// 用于存储正在进行的刷新token的Promise
let userRefreshTokenPromise = null;
let adminRefreshTokenPromise = null;

// 刷新用户token
const refreshUserToken = async () => {
    try {
        // 如果已经有一个刷新请求在进行中，返回该Promise
        if (userRefreshTokenPromise) {
            return userRefreshTokenPromise;
        }

        // 创建新的刷新请求
        userRefreshTokenPromise = axios.post('/api/auth/refresh', null, {
            baseURL: "http://localhost:9000",
            headers: {
                'Refresh-Token': TokenManager.user.getRefreshToken()
            }
        });

        const response = await userRefreshTokenPromise;
        userRefreshTokenPromise = null;

        if (response.data.code === 200 && response.data.data) {
            const { accessToken, refreshToken } = response.data.data;
            TokenManager.user.set(accessToken, refreshToken);
            return accessToken;
        } else {
            throw new Error('刷新令牌失败');
        }
    } catch (error) {
        userRefreshTokenPromise = null;
        console.error('刷新用户令牌失败:', error);
        TokenManager.user.clear();
        // 重定向到登录页面
        window.location.href = '/login';
        throw error;
    }
};

// 刷新管理员token
const refreshAdminToken = async () => {
    try {
        // 如果已经有一个刷新请求在进行中，返回该Promise
        if (adminRefreshTokenPromise) {
            return adminRefreshTokenPromise;
        }

        // 创建新的刷新请求
        adminRefreshTokenPromise = axios.post('/api/auth/refresh', null, {
            baseURL: "http://localhost:9000",
            headers: {
                'Refresh-Token': TokenManager.admin.getRefreshToken()
            }
        });

        const response = await adminRefreshTokenPromise;
        adminRefreshTokenPromise = null;

        if (response.data.code === 200 && response.data.data) {
            const { accessToken, refreshToken } = response.data.data;
            TokenManager.admin.set(accessToken, refreshToken);
            return accessToken;
        } else {
            throw new Error('刷新令牌失败');
        }
    } catch (error) {
        adminRefreshTokenPromise = null;
        console.error('刷新管理员令牌失败:', error);
        TokenManager.admin.clear();
        // 重定向到管理员登录页面
        window.location.href = '/admin/login';
        throw error;
    }
};

// 请求拦截器
axiosInstance.interceptors.request.use(async (config) => {
    console.log("axios.js 请求拦截器config:", config)
    // 根据当前浏览器的location.pathname判断是用户还是管理员请求
    const isAdminRequest = window.location.pathname.includes('/admin/');
    let accessToken;
    // 管理员请求
    if (isAdminRequest) {
        accessToken = TokenManager.admin.getAccessToken();

        console.log(isTokenExpired(accessToken))
        // 如果没有access token或已过期，且有refresh token，尝试刷新
        if ((!accessToken || isTokenExpired(accessToken)) && TokenManager.admin.getRefreshToken()) {
            try {
                accessToken = await refreshAdminToken();
            } catch (error) {
                throw error;
            }
        }
    } else {
        // 用户请求
        accessToken = TokenManager.user.getAccessToken();
        // 如果没有access token或已过期，且有refresh token，尝试刷新
        if ((!accessToken || isTokenExpired(accessToken)) &&
            (TokenManager.user.getRefreshToken())) {
            try {
                accessToken = await refreshUserToken();
            } catch (error) {
                throw error;
            }
        }
    }

    if (accessToken) {
        // 如果accessToken已经带有Bearer前缀，直接使用，否则添加前缀
        config.headers.Authorization = accessToken.startsWith('Bearer ') ? accessToken : `Bearer ${accessToken}`;
    }

    // 添加刷新令牌到请求头
    const refreshToken = isAdminRequest ? TokenManager.admin.getRefreshToken() : TokenManager.user.getRefreshToken();
    if (refreshToken) {
        config.headers['Refresh-Token'] = refreshToken;
    }
    return config;
}, async (error) => {
    console.log("axios.js 请求错误:", error.config.url, error)
    return Promise.reject(error);
});

// 响应拦截器
axiosInstance.interceptors.response.use(
    async (response) => {
        console.log("axios.js 响应拦截器response:", response)
        // 检查响应头中是否有新的访问令牌
        const newAccessToken = response.headers['Authorization'] || response.headers['new-access-token'];
        if (newAccessToken) {
            // 根据当前请求判断是用户还是管理员
            const isAdminRequest = window.location.pathname.includes('/admin/');
            if (isAdminRequest) {
                TokenManager.admin.set(newAccessToken);
            } else {
                TokenManager.user.set(newAccessToken);
            }
        }
        return response;
    },
    async (error) => {
        console.log("axios.js 响应错误:", error.config.url, error)
        const originalRequest = error.config;
        // 如果是401错误且未尝试过刷新token
        if (error.response?.status === 401 && !originalRequest._retry) {
            console.log(error.response)
            originalRequest._retry = true;

            try {
                // 根据当前浏览器的location.pathname判断是用户还是管理员请求
                const isAdminRequest = window.location.pathname.includes('/admin/');

                let accessToken;
                if (isAdminRequest) {
                    accessToken = await refreshAdminToken();
                } else {
                    if (TokenManager.user.getRefreshToken()) {
                        accessToken = await refreshUserToken();
                    } else {
                        // 如果refresh token也没有，则重定向到登录页面
                        window.location.href = '/login';
                    }
                }

                originalRequest.headers.Authorization = `Bearer ${accessToken}`;
                return axiosInstance(originalRequest);
            } catch (refreshError) {
                return Promise.reject(refreshError);
            }
        }

        return Promise.reject(error);
    }
);

export { TokenManager };
export default axiosInstance;
