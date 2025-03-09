import axios from '@/utils/axios';
import { ElMessage } from 'element-plus';
const getTokens = () => {
    return localStorage.getItem('admin-token');
}
// 设置token
export const setToken = (newToken) => {
    localStorage.setItem('admin-token', newToken);
}
// 清除token
export const clearToken = () => {
    if(getTokens()){
        localStorage.removeItem('admin-token');
    }else{
        localStorage.clear();
    }
}
//刷新token
export const refreshToken = async () => {
    const response = await axios.get('/api/users/refreshToken');
    console.log(response)
    if(response.status === 200&&response.data){
        setTokens(response.data.token);
    }
}
// 后台登录
export const adminLogin = async (loginData) => {
    try {
        const response = await axios.post('/api/users/adminLogin', loginData);
        if(response.status === 200&&response.data){
            return response.data;
        }
        if(response.status === 401){
            ElMessage.error("用户未登录")
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


