import axios, { TokenManager } from '@/utils/axios';

// 管理员登录
export const adminLogin = async (data) => {
    try {
        console.log('发送登录请求:', {
            url: '/api/auth/adminLogin',
            data: {
                username: data.username,
                password: data.password,
                roleId: data.roleId
            }
        });
        const response = await axios.post('/api/auth/adminLogin', {
            username: data.username,
            password: data.password,
            roleId: data.roleId
        });

        console.log('登录响应:', response);

        if (response.code === 200) {
            if (response.data && response.data.accessToken && response.data.refreshToken) {
                // 设置token
                TokenManager.admin.set(response.data.accessToken, response.data.refreshToken);
                // console.log(TokenManager.admin.getAccessToken());
                // console.log(TokenManager.admin.getRefreshToken());
                return response;
            } else {
                throw new Error('登录响应数据格式错误');
            }
        } else {
            throw new Error(response.message || '登录失败');
        }
    } catch (error) {
        console.error('登录错误:', {
            error: error.message,
            response: error.response?.data
        });
        throw error;
    }
};

// 管理员退出
export const adminLogout = async () => {
    try {
        // 获取当前的访问令牌和刷新令牌
        const accessToken = TokenManager.admin.getAccessToken();
        const refreshToken = TokenManager.admin.getRefreshToken();

        if (!accessToken) {
            console.warn('退出时未找到访问令牌');
            // clearLocalStorage();
            return { code: 200, message: '退出成功' };
        }

        const response = await axios.post('/api/auth/logout', {}, {
            headers: {
                'Authorization': accessToken,
                'Refresh-Token': refreshToken || ''
            }
        });

        // 无论接口调用是否成功，都清除本地存储
        clearLocalStorage();
        return response;
    } catch (error) {
        console.error('退出错误:', error);
        // 即使发生错误，也清除本地存储
        // clearLocalStorage();
        return { code: 200, message: '退出成功' };
    }
};

// 获取管理员信息
export const getAdminInfo = async (userId) => {
    try {
        if (!userId) {
            throw new Error('用户ID不能为空');
        }

        const response = await axios.get(`/api/system/user/${userId}`);
        
        // 验证响应格式
        if (!response) {
            throw new Error('服务器响应为空');
        }

        // 验证响应状态
        if (response.code === 200) {
            // 验证响应数据完整性
            if (!response.data || !response.data.user) {
                throw new Error('响应数据格式不正确');
            }
            return response;
        } else if (response.code === 401) {
            throw new Error('登录已过期，请重新登录');
        } else if (response.code === 403) {
            throw new Error('没有权限访问该资源');
        } else {
            throw new Error(response.message || '获取用户信息失败');
        }
    } catch (error) {
        console.error('获取用户信息错误:', error);
        
        // 处理网络错误
        if (error.isAxiosError) {
            if (!error.response) {
                throw new Error('网络连接失败，请检查网络设置');
            }
            // 处理特定的 HTTP 状态码
            switch (error.response.status) {
                case 401:
                    throw new Error('登录已过期，请重新登录');
                case 403:
                    throw new Error('没有权限访问该资源');
                case 404:
                    throw new Error('用户信息不存在');
                case 500:
                    throw new Error('服务器内部错误，请稍后重试');
                default:
                    throw new Error('获取用户信息失败，请稍后重试');
            }
        }
        
        throw error;
    }
};

// 刷新管理员token
export const refreshAdminToken = async () => {
    try {
        const response = await axios.post('/api/auth/refresh', {}, {
            headers: {
                'Refresh-Token': TokenManager.admin.getRefreshToken()
            }
        });
        
        if (response.code === 200) {
            return response;
        } else {
            throw new Error(response.message || '刷新token失败');
        }
    } catch (error) {
        console.error("刷新token错误", error);
        throw error;
    }
};

// 清除会话存储的函数
function clearLocalStorage() {
    sessionStorage.removeItem("adminInfo");
    sessionStorage.removeItem("roleId");
    sessionStorage.removeItem("isAdminLoggedIn");
    TokenManager.admin.clear();
}

// 前台用户登录
export const userLogin = async (data) => {
    try {
        console.log('发送登录请求:', {
            url: '/user/login',
            data: {
                username: data.username,
                password: data.password,
                roleId: data.roleId
            }
        });

        const response = await axios.post('/user/login', {
            username: data.username,
            password: data.password,
            roleId: data.roleId
        });

        console.log('登录响应:', response);

        if (response.code === 200) {
            if (response.data && response.data.accessToken && response.data.refreshToken) {
                TokenManager.user.set(response.data.accessToken, response.data.refreshToken);
                return response;
            } else {
                throw new Error('登录响应数据格式错误');
            }
        } else {
            throw new Error(response.message || '登录失败');
        }
    } catch (error) {
        console.error('登录错误:', {
            error: error.message,
            response: error.response?.data
        });
        throw error;
    }
};

// 登录方法
export const login = async (username, password) => {
    return await axios({
        url: '/api/auth/login',
        method: 'post',
        data: {
            username,
            password
        }
    });
};

// 获取用户详细信息
export const getInfo = async (userId) => {
    return await axios({
        url: '/api/system/user/' + userId,
        method: 'get'
    });
};

// 退出方法
export const logout = async () => {
    return await axios({
        url: '/api/auth/logout',
        method: 'post'
    });
};

// 获取验证码
export const getCodeImg = async () => {
    return await axios({
        url: '/api/auth/captcha',
        method: 'get'
    });
};