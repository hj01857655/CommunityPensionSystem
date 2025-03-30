import axios from '@/utils/axios';
import { TokenManager } from '@/utils/axios';

/**
 * 用户登录
 * @param {Object} data - 登录参数
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @returns {Promise<{code: number, data: Object, message: string}>}
 */
export const userLogin = (data) => {
  return axios.post('/api/auth/login', data);
};

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
        localStorage.removeItem("roleId");
        localStorage.removeItem("isLoggedIn");
        TokenManager.user.clear();
        
        throw error;
    }
}

/**
 * 获取未绑定的老人列表
 * @returns {Promise<{code: number, data: Array, message: string}>}
 */
export const getUnboundElders = () => {
  return axios.get('/api/user/elder/unbound');
};

/**
 * 获取老人的家属列表
 * @param {number} elderId - 老人ID
 * @returns {Promise<{code: number, data: Array, message: string}>}
 */
export const getKinListByElderId = (elderId) => {
  return axios.get(`/api/user/kin/list/${elderId}`);
};

/**
 * 绑定老人和家属关系
 * @param {number} elderId - 老人ID
 * @param {number} kinId - 家属ID
 * @param {string} relationType - 关系类型
 * @returns {Promise<{code: number, message: string}>}
 */
export const bindElderKinRelation = (elderId, kinId, relationType) => {
  return axios.post('/api/user/elder-kin/bind', { elderId, kinId, relationType });
};

/**
 * 解绑老人和家属关系
 * @param {number} elderId - 老人ID
 * @param {number} kinId - 家属ID
 * @returns {Promise<{code: number, message: string}>}
 */
export const unbindElderKinRelation = (elderId, kinId) => {
  return axios.post('/api/user/elder-kin/unbind', { elderId, kinId });
};

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

