import axios from '@/utils/axios';
import { TokenManager } from '@/utils/axios';

// 用户登录
export const userLogin = async (data) => {
    try {
        const response = await axios.post('/api/auth/login', {
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
        const response = await axios.put(`/api/system/user/${data.userId}`, data);
        if (response.code === 200) {
            // 更新本地存储
            localStorage.setItem("userInfo", JSON.stringify(response.data));
            
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
        const response = await axios.post('/api/auth/logout', {
            username: data.username,
            roleId: data.roleId
        });

        if (response.code === 200) {
            // 清除本地存储的用户信息和token
            localStorage.removeItem("userInfo");
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

// 获取未绑定家属的老人列表
export const getUnboundElders = async () => {
  try {
    const response = await axios.get('/api/user/unbound/elders');
    return response;
  } catch (error) {
    console.error('获取未绑定家属老人列表失败:', error);
    throw error;
  }
}

// 绑定老人和家属关系
export const bindElderKinRelation = async (elderId, kinId, relationType) => {
  try {
    const response = await axios.post('/api/user/bind-relation', null, {
      params: { elderId, kinId, relationType }
    });
    return response;
  } catch (error) {
    console.error('绑定老人家属关系失败:', error);
    throw error;
  }
}

// 解绑老人和家属关系
export const unbindElderKinRelation = async (elderId, kinId) => {
  try {
    const response = await axios.post('/api/user/unbind-relation', null, {
      params: { elderId, kinId }
    });
    return response;
  } catch (error) {
    console.error('解绑老人家属关系失败:', error);
    throw error;
  }
}

// 获取老人的家属ID列表
export const getKinIdsByElderId = async (elderId) => {
  try {
    const response = await axios.get(`/api/user/kin-ids/${elderId}`);
    return response;
  } catch (error) {
    console.error('获取老人家属ID列表失败:', error);
    throw error;
  }
}

