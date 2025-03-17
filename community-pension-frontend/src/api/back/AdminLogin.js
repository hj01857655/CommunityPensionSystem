import axios from '@/utils/axios';
import { TokenManager } from '@/utils/axios';

export const adminLogin = async (data) => {
    try {
        const response = await axios.post('/api/users/adminLogin', {
            username: data.username,
            password: data.password,
            roleId: data.roleId
        });

        if (response.code === 200) {
            if (response.data && response.data.accessToken && response.data.refreshToken) {
                TokenManager.admin.set(response.data.accessToken, response.data.refreshToken);
            }
        }

        return response;
    } catch (error) {
        console.error('登录错误:', error);
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
            clearLocalStorage();
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
        clearLocalStorage();
        return { code: 200, message: '退出成功' };
    }
}

// 清除会话存储的函数
function clearLocalStorage() {
    sessionStorage.removeItem("adminInfo");
    sessionStorage.removeItem("roleId");
    sessionStorage.removeItem("isAdminLoggedIn");
    TokenManager.admin.clear();
}

// 刷新管理员token
export const refreshAdminToken = async () => {
    try {
        const response = await axios.post('/api/auth/refresh', {}, {
            headers: {
                'Refresh-Token': TokenManager.admin.getRefreshToken()
            }
        });
        return response;
    } catch (error) {
        console.error("刷新token错误", error);
        throw error;
    }
}