import axios from '@/utils/axios';
import { ElMessage } from 'element-plus';

// 获取token
export const getTokens = () => {
    return localStorage.getItem('admin-token');
}
// 设置token
export const setToken = (newToken) => {
    localStorage.setItem('admin-token', newToken);
    localStorage.setItem('isLoginIn', true)
}


//刷新token
export const refreshToken = async () => {
    const response = await axios.get('/api/users/refreshToken');
    console.log(response)
    if(response.status === 200&&response.data){
        setTokens(response.data.token);
    }
}

// 管理员登录
export const adminLogin = async (loginData) => {
    try {
        const response = await axios.post('/api/users/adminLogin', loginData);
        if(response.status === 200&&response.data){
            const result = response.data;
            localStorage.clear()
            if(result.token){
                console.log("admin.js setToken:",result.token)
                setToken(result.token)
            }
            if(result.user){
                console.log("admin.jssetAdminInfo:",result.user)
                localStorage.setItem('adminInfo', JSON.stringify(result.user));
            }
            return result;
        }else{
            return {status:500,data:{},message:"登录失败，请稍后重试"};
        }
    } catch (error) {
        console.log(error)
        const status = error.response?.status;
        const message = status === 401 ? '用户名或密码错误' : '登录失败，请稍后重试';
        console.log(message)
        return { status:500,data:{},message:message };
    }
}

// 退出登录
export const logouts = () => {
    if(getTokens()){
        localStorage.removeItem('admin-token');
        localStorage.removeItem('isLoginIn')
        localStorage.removeItem('adminInfo')
    }else{
        localStorage.clear();
    }
}