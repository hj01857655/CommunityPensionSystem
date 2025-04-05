import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { 
  userLogin, 
  getUnboundElders, 
  getKinListByElderId,
  bindElderKinRelation,
  unbindElderKinRelation
} from '@/api/fore/user';
import { TokenManager } from '@/utils/axios';
import { getAvatarUrl } from '@/utils/avatarUtils';  // 导入头像工具函数
import axios from '@/utils/axios';
import { ElMessage } from 'element-plus';
import { getHealthRecords } from '@/api/fore/health';

export const useUserStore = defineStore('user', () => {
  // 状态定义
  const userInfo = ref(null);
  const elderInfo = ref(null);
  const kinInfo = ref(null);
  const isLoggedIn = ref(false);
  const roleId = ref(null);
  const roles = ref([]);

  // 计算属性
  const avatarUrl = computed(() => {
    return getAvatarUrl(userInfo.value?.avatar);
  });

  // 方法定义
  const setUserInfo = (user) => {
    if (user) {
      userInfo.value = user;
      isLoggedIn.value = true;
      roleId.value = user.roleId;
      roles.value = [user.role];
      
      // 保存到本地存储
      localStorage.setItem("userInfo", JSON.stringify(user));
      localStorage.setItem("userId", user.id);
      localStorage.setItem("username", user.username);
      localStorage.setItem("name", user.name);
      localStorage.setItem("roleId", user.roleIds[0]);
      localStorage.setItem("role", user.roleIds[0]);
      localStorage.setItem("roleName", user.roleNames[0]);
      localStorage.setItem("isLoggedIn", "true");

      // 如果是家属，保存关联的老人信息
      if (user.role === 'kin' && user.bindElder) {
        localStorage.setItem("bindElder", JSON.stringify(user.bindElder));
      }
    }
  };

  const login = async (loginForm) => {
    try {
      const response = await userLogin(loginForm);
      
      if (response.code === 200) {
        console.log("useUserStore登录成功响应", response);
        setUserInfo(response.data.user);

        // 保存token
        if (response.data.accessToken) {
          TokenManager.user.set(response.data.accessToken, response.data.refreshToken);
        } else {
          console.error('登录响应中没有token');
          ElMessage.error('登录失败：未获取到token');
          return false;
        }

        return true;
      } else {
        ElMessage.error(response.message || '登录失败');
        return false;
      }
    } catch (error) {
      console.error('登录错误:', error);
      ElMessage.error('登录过程中发生错误，请稍后再试');
      return false;
    }
  };

  const getUserInfo = async () => {
    try {
      // 如果已经有用户信息，直接返回
      if (userInfo.value) {
        return { code: 200, data: userInfo.value };
      }
      
      // 从本地存储获取用户信息
      const userInfoStr = localStorage.getItem('userInfo');
      if (!userInfoStr) {
        return { code: 401, message: '用户未登录' };
      }
      
      try {
        const parsedUserInfo = JSON.parse(userInfoStr);
        userInfo.value = parsedUserInfo;
        
        // 如果是老人，获取老人信息
        if (roleId.value === 1) {
          const elderInfoStr = localStorage.getItem('elderInfo');
          if (elderInfoStr) {
            elderInfo.value = JSON.parse(elderInfoStr);
            Object.assign(userInfo.value, elderInfo.value);
          }
        }
        
        // 如果是家属，获取家属信息
        if (roleId.value === 2) {
          const kinInfoStr = localStorage.getItem('kinInfo');
          if (kinInfoStr) {
            kinInfo.value = JSON.parse(kinInfoStr);
            Object.assign(userInfo.value, kinInfo.value);
          }
        }
        
        return { code: 200, data: userInfo.value };
      } catch (error) {
        console.error('解析用户信息失败:', error);
        return { code: 500, message: '获取用户信息失败' };
      }
    } catch (error) {
      console.error('获取用户信息失败:', error);
      return { code: 500, message: '获取用户信息失败' };
    }
  };

  const logout = async () => {
    // 清除状态
    userInfo.value = null;
    elderInfo.value = null;
    kinInfo.value = null;
    isLoggedIn.value = false;
    roleId.value = null;
    roles.value = [];
    
    // 清除本地存储
    localStorage.removeItem('userInfo');
    localStorage.removeItem('elderInfo');
    localStorage.removeItem('kinInfo');
    localStorage.removeItem('isLoggedIn');
    localStorage.removeItem('roleId');
    
    // 清除token
    TokenManager.user.clear();
    
    return true;
  };

  const updateUserInfo = async (userData) => {
    try {
      // 此处可以添加API调用来更新后端数据
      const response = await updateUserInfo(userData);
      
      // 更新本地存储
      userInfo.value = { ...userInfo.value, ...userData };
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value));
      
      // 根据角色更新特定信息
      if (roleId.value === 1) { // 老人
        elderInfo.value = { ...elderInfo.value, ...userData };
        localStorage.setItem('elderInfo', JSON.stringify(elderInfo.value));
      } else if (roleId.value === 2) { // 家属
        kinInfo.value = { ...kinInfo.value, ...userData };
        localStorage.setItem('kinInfo', JSON.stringify(kinInfo.value));
      }
      
      return true;
    } catch (error) {
      console.error('更新用户信息失败:', error);
      return false;
    }
  };

  const getElderInfo = async (elderId) => {
    try {
      // 如果已经有老人信息，直接返回
      if (elderInfo.value && elderInfo.value.id === elderId) {
        return { code: 200, data: elderInfo.value };
      }
      
      // 此处可以添加API调用来获取老人详细信息
      // const response = await fetchElderInfo(elderId);
      
      // 模拟返回数据
      return { 
        code: 200, 
        data: { 
          id: elderId,
          name: '老人姓名'
        } 
      };
    } catch (error) {
      console.error('获取老人信息失败:', error);
      return null;
    }
  };

  const fetchUnboundElders = async () => {
    try {
      const response = await getUnboundElders();
      if (response.code === 200) {
        return response.data;
      } else {
        throw new Error(response.message || '获取未绑定老人列表失败');
      }
    } catch (error) {
      console.error('获取未绑定老人列表失败:', error);
      throw error;
    }
  };

  const fetchKinListByElderId = async (elderId) => {
    try {
      const response = await getKinListByElderId(elderId);
      if (response.code === 200) {
        return response.data;
      } else {
        throw new Error(response.message || '获取家属列表失败');
      }
    } catch (error) {
      console.error('获取家属列表失败:', error);
      throw error;
    }
  };

  const bindElderKin = async (elderId, kinId, relationType) => {
    try {
      const response = await bindElderKinRelation(elderId, kinId, relationType);
      if (response.code === 200) {
        ElMessage.success('绑定成功');
        return true;
      }
      return false;
    } catch (error) {
      console.error('绑定老人家属关系失败:', error);
      return false;
    }
  };

  const unbindElderKin = async (elderId, kinId) => {
    try {
      const response = await unbindElderKinRelation(elderId, kinId);
      if (response.code === 200) {
        ElMessage.success('解绑成功');
        return true;
      }
      return false;
    } catch (error) {
      console.error('解绑老人家属关系失败:', error);
      return false;
    }
  };

  const getHealthRecords = async (elderId) => {
    try {
      const response = await getHealthRecords(elderId);
      if (response.code === 200) {
        return response;
      }
      return null;
    } catch (error) {
      console.error('获取健康记录失败:', error);
      return null;
    }
  };

  return {
    // 状态
    userInfo,
    elderInfo,
    kinInfo,
    isLoggedIn,
    roleId,
    roles,
    
    // 计算属性
    avatarUrl,
    
    // 方法
    setUserInfo,
    login,
    getUserInfo,
    logout,
    updateUserInfo,
    getElderInfo,
    fetchUnboundElders,
    fetchKinListByElderId,
    bindElderKin,
    unbindElderKin,
    getHealthRecords
  };
});
export default useUserStore;