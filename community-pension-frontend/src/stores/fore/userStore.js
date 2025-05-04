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

/**
 * 前台用户状态管理
 * 用于管理普通用户（老人、家属）的状态
 * @module stores/fore/foregroundUserStore
 */

/**
 * 前台用户 Store
 * @typedef {Object} ForegroundUserStore
 * @property {Ref<Object|null>} userInfo - 用户信息
 * @property {Ref<Object|null>} elderInfo - 老人信息
 * @property {Ref<Object|null>} kinInfo - 家属信息
 * @property {Ref<boolean>} isLoggedIn - 登录状态
 * @property {Ref<number|null>} roleId - 角色ID
 * @property {Ref<Array>} roles - 角色列表
 */
export const useUserStore = defineStore('foreground-user', () => {
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
      roles.value = user.roles || [user.role];
      
      // 使用 localStorage 存储前台用户基本信息
      localStorage.setItem("userInfo", JSON.stringify({
        userId: user.userId,
        username: user.username,
        name: user.name,
        avatar: user.avatar,
        roleId: user.roleId,
        roles: user.roles || [user.role],
        permissions: user.permissions || [],
        phone: user.phone,
        email: user.email,
        gender: user.gender,
        isActive: user.isActive,
        // 基本信息
        birthday: user.birthday,
        idCard: user.idCard,
        address: user.address,
        emergencyContactName: user.emergencyContactName,
        emergencyContactPhone: user.emergencyContactPhone,
        healthCondition: user.healthCondition,
        // 绑定ID列表
        kinIds: user.kinIds || [],
        elderIds: user.elderIds || []
      }));
      localStorage.setItem("isLoggedIn", "true");
      localStorage.setItem("roleId", user.roleId);
      localStorage.setItem("roles", JSON.stringify(user.roles || [user.role]));
    }
  };

  const login = async (loginForm) => {
    try {
      const response = await userLogin(loginForm);
      
      if (response.code === 200) {
        console.log("前台登录成功响应", response);
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
        console.log("前台登录失败响应", response);
        ElMessage.error('登录失败');
        return false;
      }
    } catch (error) {
      console.error('登录错误:', error.message);
      ElMessage.error(error.message || '登录失败');
      return false;
    }
  };

  const getUserInfo = async () => {
    try {
      // 从本地存储获取用户信息
      const userInfo = localStorage.getItem('userInfo')
      if (userInfo) {
        const parsedUserInfo = JSON.parse(userInfo)
        // 确保角色ID存在
        if (!parsedUserInfo.roleId && parsedUserInfo.roleIds && parsedUserInfo.roleIds.length > 0) {
          parsedUserInfo.roleId = parsedUserInfo.roleIds[0]
        }
        return parsedUserInfo
      }

      // 从API获取用户信息
      const response = await axios.get('/user/info')
      if (response.code === 200 && response.data) {
        const userData = response.data
        // 确保角色ID存在
        if (!userData.roleId && userData.roleIds && userData.roleIds.length > 0) {
          userData.roleId = userData.roleIds[0]
        }
        // 存储到本地
        localStorage.setItem('userInfo', JSON.stringify(userData))
        return userData
      }
      return null
    } catch (error) {
      console.error('获取用户信息失败:', error)
      return null
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
    
    // 清除 localStorage 存储
    localStorage.removeItem('userInfo');
    localStorage.removeItem('isLoggedIn');
    localStorage.removeItem('roleId');
    localStorage.removeItem('roles');
    
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
      localStorage.setItem('fore-userInfo', JSON.stringify(userInfo.value));
      
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
      console.log('获取家属列表响应:', response);
      
      if (response && response.code === 200) {
        return response.data;
      } else if (response && Array.isArray(response)) {
        return response;
      } else if (response && response.data && Array.isArray(response.data)) {
        return response.data;
      } else {
        throw new Error(response?.message || '获取家属列表失败');
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
      const response = await axios.get('/api/health-records/getHealthRecords', {
        params: { elderId }
      });
      if (response.data.code === 200) {
        return response.data;
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

// 为了向后兼容，保留原来的导出名称
/** @deprecated 请使用 useUserStore 替代 */
export const useForegroundUserStore = useUserStore;

// 默认导出
export default useUserStore;
