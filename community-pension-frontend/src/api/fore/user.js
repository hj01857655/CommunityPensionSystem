import axios from '@/utils/axios';
import { TokenManager } from '@/utils/axios';

// 用户登录
export const userLogin = async (data) => {
    try {
        const response = await axios.post('/api/users/userLogin', {
            username: data.username,
            password: data.password,
            roleId: data.roleId
        });
        
        // 响应拦截器已经处理了基本的错误，这里只需要返回数据
        return response;
    } catch (error) {
        console.error('登录错误:', error);
        throw error; // 直接抛出错误，让上层处理
    }
}

// 更新用户信息
export const updateUserInfo = async (data) => {
    try {
        const response = await axios.put(`/api/users/${data.id}`, data);
        if (response.code === 200) {
            // 更新本地存储
            localStorage.setItem("userInfo", JSON.stringify(response.data));
            if (data.roleId === 1) { // 老人
                localStorage.setItem("elderInfo", JSON.stringify(response.data));
            } else if (data.roleId === 2) { // 家属
                localStorage.setItem("kinInfo", JSON.stringify(response.data));
            }
        }
        return response;
    } catch (error) {
        console.error('更新用户信息错误:', error);
        throw error;
    }
}

// 用户退出
export const userLogout = async (data) => {
    try {
        const response = await axios.post('/api/users/userLogout', {
            username: data.username,
            roleId: data.roleId
        });

        if (response.code === 200) {
            // 清除本地存储的用户信息和token
            localStorage.removeItem("userInfo");
            localStorage.removeItem("elderInfo");
            localStorage.removeItem("kinInfo");
            localStorage.removeItem("roleId");
            localStorage.removeItem("isLoggedIn");
            
            // 清除token
            TokenManager.user.clear();
        }
        
        return response;
    } catch (error) {
        console.error('退出错误:', error);
        // 即使发生错误，也要清除本地存储
        localStorage.removeItem("userInfo");
        localStorage.removeItem("elderInfo");
        localStorage.removeItem("kinInfo");
        localStorage.removeItem("roleId");
        localStorage.removeItem("isLoggedIn");
        TokenManager.user.clear();
        
        throw error;
    }
}

