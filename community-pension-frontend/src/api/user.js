import axios from '@/utils/axios';
import { ElMessage } from 'element-plus';

// 用户
export const login = async (loginData) => {
    try {
        const response = await axios.post('/api/users/login', {
            username: loginData.username,
            password: loginData.password,
            roleId: loginData.roleId
        });
        const { token, user } = response.data.data;
        console.log(token, user)
        //将 token 设置到本地
        localStorage.setItem('token', token);
        //存储前删除user中的password
        const { password, ...userInfo } = user;
        localStorage.setItem('userInfo', JSON.stringify(userInfo));
        return { success: true, data: { token, userInfo } };
    } catch (error) {
        console.error('Login error:', error);
        const status = error.response?.status;
        const message = status === 401 ? '用户名或密码错误' : '登录失败，请稍后重试';
        ElMessage.error(message);
        return { success: false, error: message };
    }
};

//后台管理
export const loginAdmin = async (loginData) => {
    try {
        //先校验loginData中roleId
        if (loginData.roleId !== 3 && loginData.roleId !== 4) {
            ElMessage.error('角色ID错误');
            return { success: false, error: '角色ID错误' };
        }
        const response = await axios.post('/api/users/adminLogin', loginData);
        return { success: true, data: response.data };
    } catch (error) {
        console.error('Login error:', error);
        const status = error.response?.status;
        const message = status === 401 ? '用户名或密码错误' : '登录失败，请稍后重试';
        ElMessage.error(message);
        return { success: false, error: message };
    }
}
//重置密码
export const resetPassword = async (userId) => {
    try {
        const token = localStorage.getItem('token');
        const response = await axios.put(`/api/users/resetPassword`, null, {
            headers: {
                Authorization: `${token}`
            },
            params: {
                userId: userId
            }
        });
        ElMessage.success('重置成功');
        return { success: true, data: response.data };
    } catch (error) {
        console.error('Reset password error:', error);
        ElMessage.error('重置失败，请稍后重试');
        return { success: false, error: error.message };
    }
};
//获取所有用户
export const getAllUsers = async () => {
    try {
        const token = localStorage.getItem('token');
        const response = await axios.get('/api/users', {
            headers: {
                Authorization: `${token}`,
            },
        });
        return { success: true, data: response.data };
    } catch (error) {
        console.error('Get all users error:', error);
        ElMessage.error('获取用户失败，请稍后重试');
        return { success: false, error: error.message };
    }
};
//获取用户
export const getUser = async (userId) => {
    try {
        const response = await axios.get(`/api/users/${userId}`);
        return { success: true, data: response.data };
    } catch (error) {
        console.error('Get user error:', error);
        ElMessage.error('获取用户失败，请稍后重试');
        return { success: false, error: error.message };
    }
};

//addUser
export const addUser = async (user) => {
    try {
        const token = localStorage.getItem('token');
        const response = await axios.post('/api/users/add', user, {
            headers: {
                Authorization: `${token}`,
            },
        });
        if (response.data.code === 401) {
            ElMessage.error(response.data.msg);
            return { success: false, error: response.data.msg };
        }
        ElMessage.success('添加成功');
        return { success: true, data: response.data };
    } catch (error) {
        console.error('Add user error:', error);
        ElMessage.error('添加失败，请稍后重试');
        return { success: false, error: error.message };
    }
};

//deleteUser
export const deleteUser = async (id) => {
    try {
        const response = await axios.delete(`/api/users/delete/${id}`);
        ElMessage.success('删除成功');
        return { success: true, data: response.data };
    } catch (error) {
        console.error('Delete user error:', error);
        ElMessage.error('删除失败，请稍后重试');
        return { success: false, error: error.message };
    }
};

//updateUser
export const updateUser = async (user) => {
    try {
        const token = localStorage.getItem('token');
        const response = await axios.put(`/api/users/update`,
            user,
            {
                headers: {
                    Authorization: `${token}`,
                },
            }
        );
        ElMessage.success('更新成功');
        return { success: true, data: response.data };
    } catch (error) {
        console.error('Update user error:', error);
        ElMessage.error('更新失败，请稍后重试');
        return { success: false, error: error.message };
    }
};
//获取用户信息
export const getUserInfo = async () => {
    try {
        const token = localStorage.getItem('token');
        const response = await axios.get('/api/users/userInfo', {
            headers: {
                Authorization: `${token}`,
            }
        });
        return { success: true, data: response.data };
    } catch (error) {
        console.error('Get user info error:', error);
        ElMessage.error('获取用户信息失败，请稍后重试');
        return { success: false, error: error.message };
    }
};


