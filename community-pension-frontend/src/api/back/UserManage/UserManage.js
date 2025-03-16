import axios from '@/utils/axios';

// 获取用户列表
export const getUserList = () => {
    return axios.get('/api/users/list');
}

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
