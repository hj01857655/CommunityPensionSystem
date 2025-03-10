import axios from '@/utils/axios';
import { ElMessage } from 'element-plus';

// 获取token
export const getTokens = () => {
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

export const adminLogin = async (loginData) => {
    try {
        const response = await axios.post('/api/users/adminLogin', loginData);
        /**
         *  {
         *      config: {
         *          headers: {
         *              Authorization: "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGVJZCI6NCwiaWF0IjoxNzQxNTM4NzEyLCJleHAiOjE3NDE1NDIzMTJ9.Q0nJ9tYzqsnow9006yCtu3NneX8EHwkUXx1Exm7zxVI"
         *          }
         *      },
         *      data: {
         *          token: "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGVJZCI6NCwiaWF0IjoxNzQxNTM4NzEyLCJleHAiOjE3NDE1NDIzMTJ9.Q0nJ9tYzqsnow9006yCtu3NneX8EHwkUXx1Exm7zxVI",
         *          user: {
         *              id: 4,
         *              username: "admin",
         *              password: "123456",
         *              roleId: 4,
         *              status: 1,
         *              createTime: "2025-03-09 10:00:00",
         *              updateTime: "2025-03-09 10:00:00",
         *          }
         *      },
         *      headers: {
         *          Authorization: "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGVJZCI6NCwiaWF0IjoxNzQxNTM4NzEyLCJleHAiOjE3NDE1NDIzMTJ9.Q0nJ9tYzqsnow9006yCtu3NneX8EHwkUXx1Exm7zxVI"
         *      },
         *      status: 200,
         *      statusText: "OK"
         *  }
         */
        if(response.status === 200&&response.data){
            const result = response.data;
            return result;
        }else{
            console.log(response)
            return {status:500,data:{},message:"登录失败，请稍后重试"};
        }
    } catch (error) {
        console.log(error)
        console.log(error)
        const status = error.response?.status;
        const message = status === 401 ? '用户名或密码错误' : '登录失败，请稍后重试';
        console.log(message)
        return { status:500,data:{},message:message };
    }
}



