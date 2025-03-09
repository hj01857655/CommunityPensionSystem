import axios from '@/utils/axios';
import { ElMessage } from 'element-plus';
// 后台登录
export const adminLogin = async (loginData) => {
    try {
        const response = await axios.post('/api/users/adminLogin', loginData);
        // console.log("admin.js:response", response)
        if(response.status === 200&&response.data){
            return response.data;
        }
        if(response.status === 401){
            return {code:401,message:"用户名或密码错误"}
        }
        return {code:500,message:"登录失败，请稍后重试"}
    } catch (error) {
        console.log(error)
        const status = error.response?.status;
        const message = status === 401 ? '用户名或密码错误' : '登录失败，请稍后重试';
        console.log(message)
        return { success: false, error: message };
    }
}

// 获取管理员信息
export const getAdminInfo = async (token) => {
    try {
        const response = await axios.get('/api/users/userInfo', {
            headers: {
                Authorization: `${token}`,
            },
        });
        return { success: true, data: response.data };
    } catch (error) {
        console.error('Get user info error:', error);
        const status = error.response?.status;
        const message = status === 401 ? '用户未登录' : '获取用户信息失败，请稍后重试';
        console.error(message)
        return { success: false, error: message };
    }
};
