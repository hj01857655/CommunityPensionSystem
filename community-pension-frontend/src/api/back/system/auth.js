import axios from '@/utils/axios';

/**
 * 管理员登录
 * @param {Object} data
 * @returns {Promise}
 */
export const login = (data) => {
    return axios({
        url: '/system/auth/adminLogin',
        method: 'post',
        data
    });
};

/**
 * 退出登录
 * @returns {Promise}
 */
export const logout = () => {
    return axios({
        url: '/api/system/auth/adminLogout',
        method: 'post'
    });
};

/**
 * 刷新访问令牌
 * @param {string} refreshToken - 刷新令牌
 * @returns {Promise<{code: number, data: {accessToken: string, refreshToken: string}, msg: string}>}
 */
export const refreshToken = (refreshToken) => {
    return axios({
        url: '/api/system/auth/refresh',
        method: 'post',
        headers: {
            'Refresh-Token': refreshToken
        }
    });
};

/**
 * 使令牌失效
 * @param {string} accessToken - 访问令牌
 * @param {string} [refreshToken] - 刷新令牌（可选）
 * @returns {Promise<{code: number, msg: string}>}
 */
export const invalidateToken = (accessToken, refreshToken) => {
    return axios({
        url: '/api/system/auth/invalidate',
        method: 'post',
        headers: {
            'Authorization': accessToken,
            ...(refreshToken && { 'Refresh-Token': refreshToken })
        }
    });
};