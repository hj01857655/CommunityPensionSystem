import axios, { TokenManager } from '@/utils/axios';
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


