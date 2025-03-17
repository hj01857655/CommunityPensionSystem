import axios from '@/utils/axios';

/**
 * 用户信息管理相关接口
 */

/**
 * 分页查询用户列表
 * @param {Object} params - 查询参数
 * @param {number} params.current - 页码
 * @param {number} params.size - 每页数量
 * @param {string} [params.username] - 用户名（可选）
 * @returns {Promise<{code: number, data: Object, message: string}>}
 */
export const getUserList = async (params) => {
    try {
        const response = await axios.get('/api/users', { params });
        console.log('获取用户列表成功:', response.data);
        return response;
    } catch (error) {
        console.error('获取用户列表失败:', error);
        throw error;
    }
};

// 添加用户
export const addUser = (userData) => {
    return axios.post('/api/users/add', userData);
}

// 更新用户
export const updateUser = (userData) => {
    return axios.put(`/api/users/${userData.id}`, userData);
}

// 删除用户
export const deleteUser = (userId) => {
    return axios.delete(`/api/users/${userId}`);
}

// 重置密码
export const resetPassword = (userId) => {
    return axios.put(`/api/users/resetPassword?userId=${userId}`);
}

// 获取所有用户（用于管理界面）
export const getAllUsers = () => {
    return axios.get('/api/users/list').then(response => {
        return {
            success: response.code === 200,
            data: response.data,
            message: response.message
        };
    });
}

// 获取用户列表（带分页）
export const getUserListWithPagination = (params) => {
    return axios.get('/api/users/list', { params });
}
