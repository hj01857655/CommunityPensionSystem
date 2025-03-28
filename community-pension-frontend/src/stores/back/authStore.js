import { defineStore } from 'pinia';
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { login, logout, refreshToken, invalidateToken } from '@/api/back/system/auth';
import { TokenManager } from '@/utils/axios';
import { useUserStore } from './userStore';

export const useAuthStore = defineStore('auth', () => {
    const token = ref(TokenManager.user.getAccessToken());
    const userStore = useUserStore();

    /**
     * 登录
     * @param {*} loginData 
     * @returns 
     */
    const handleLogin = async (loginData) => {
        try {
            const res = await login(loginData);
            if (res.code === 200) {
                const { token } = res.data;
                TokenManager.user.set(token);
                ElMessage.success('登录成功');
                return res;
            }
            ElMessage.error(res.msg || '登录失败');
            return null;
        } catch (error) {
            console.error('登录失败:', error);
            throw error;
        }
    };

    // 退出登录
    const handleLogout = async () => {
        try {
            const res = await logout();
            if (res.code === 200) {
                TokenManager.user.remove();
                userStore.resetState();
                ElMessage.success('退出成功');
                return res;
            }
            ElMessage.error(res.msg || '退出失败');
            return null;
        } catch (error) {
            console.error('退出失败:', error);
            throw error;
        }
    };

    /**
     * 刷新访问令牌
     * @param {string} refreshToken - 刷新令牌
     * @returns {Promise<{code: number, data: {accessToken: string, refreshToken: string}, msg: string}>}
     */
    const handleRefreshToken = async (refreshToken) => {
        try {
            const res = await refreshToken(refreshToken);
            if (res.code === 200) {
                const { accessToken, refreshToken: newRefreshToken } = res.data;
                TokenManager.user.set(accessToken);
                return res;
            }
            ElMessage.error(res.msg || '刷新令牌失败');
            return null;
        } catch (error) {
            console.error('刷新令牌失败:', error);
            throw error;
        }
    };

    /**
     * 使令牌失效
     * @param {string} accessToken - 访问令牌
     * @param {string} [refreshToken] - 刷新令牌（可选）
     * @returns {Promise<{code: number, msg: string}>}
     */
    const handleInvalidateToken = async (accessToken, refreshToken) => {
        try {
            const res = await invalidateToken(accessToken, refreshToken);
            if (res.code === 200) {
                ElMessage.success('令牌已失效');
                return res;
            }
            ElMessage.error(res.msg || '使令牌失效失败');
            return null;
        } catch (error) {
            console.error('使令牌失效失败:', error);
            throw error;
        }
    };

    return {
        token,
        handleLogin,
        handleLogout,
        handleRefreshToken,
        handleInvalidateToken
    };
}); 