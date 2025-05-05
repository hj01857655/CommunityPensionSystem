import {
  bindElderKinRelation,
  getKinListByElderId,
  getUnboundElders,
  unbindElderKinRelation,
  userLogin
} from '@/api/fore/user';
import { getAvatarUrl } from '@/utils/avatarUtils'; // 导入头像工具函数
import axios, { TokenManager } from '@/utils/axios';
import { ElMessage } from 'element-plus';
import { defineStore } from 'pinia';
import { computed, ref } from 'vue';

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
  const loading = ref(false); // 添加loading状态用于登录等操作

  // 计算属性
  const avatarUrl = computed(() => {
    return getAvatarUrl(userInfo.value?.avatar);
  });

  // 方法定义
  // 保存token到存储和TokenManager
  const saveToken = (token, refreshToken) => {
    if (token && refreshToken) {
      TokenManager.user.set(token, refreshToken);
    }
  };

  const setUserInfo = (user) => {
    if (user) {
      userInfo.value = user;
      isLoggedIn.value = true;
      
      // 确保roleId存在且为数字类型
      roleId.value = user.roleId ? Number(user.roleId) : null;
      
      // 确保用户有角色
      let userRoles = [];
      if (user.roles && Array.isArray(user.roles) && user.roles.length > 0) {
        userRoles = user.roles;
      } else if (user.role) {
        userRoles = [user.role];
      } else if (user.roleId) {
        // 根据角色ID设置默认角色
        userRoles = user.roleId === 1 ? ['elder'] : ['kin'];
      }
      roles.value = userRoles;
      
      // 确保有userId
      if (!user.userId && user.id) {
        user.userId = user.id;
      }
      
      // 使用 localStorage 存储前台用户基本信息
      const userToStore = {
        userId: user.userId || user.id,
        username: user.username,
        name: user.name || user.username,
        avatar: user.avatar,
        roleId: user.roleId,
        roles: userRoles,
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
      };
      
      console.log('保存到localStorage的用户信息:', userToStore);
      localStorage.setItem("userInfo", JSON.stringify(userToStore));
      localStorage.setItem("isLoggedIn", "true");
      
      // 安全地转换roleId为字符串
      if (user.roleId !== undefined && user.roleId !== null) {
        localStorage.setItem("roleId", String(user.roleId));
      }
      
      localStorage.setItem("roles", JSON.stringify(userRoles));
    }
  };

  const login = async (loginData) => {
    try {
      loading.value = true;
      console.log('正在发送登录请求，数据:', loginData);
      const response = await userLogin(loginData);
      console.log('登录响应:', response);
      
      // 登录成功
      if (response && response.code === 200) {
        // 根据API返回的数据结构解析
        let token, refreshToken, userData;
        
        // 处理可能的不同响应格式
        if (response.data && typeof response.data === 'object') {
          // 直接从data中获取各种可能的返回格式
          token = response.data.token || response.data.accessToken;
          refreshToken = response.data.refreshToken;
          
          // 检查用户数据位置
          if (response.data.user) {
            userData = response.data.user;
          } else if (response.data.userInfo) {
            userData = response.data.userInfo;
          } else if (response.data.userId || response.data.username) {
            // 如果直接在data中包含用户信息
            userData = response.data;
          } else {
            // 创建基本用户数据
            userData = {
              userId: loginData.username,
              username: loginData.username,
              roleId: loginData.roleId
            };
          }
          
          console.log('解析的数据:', { 
            token: token ? '已获取' : '未获取', 
            refreshToken: refreshToken ? '已获取' : '未获取', 
            userData: userData 
          });
          
          // 保存token (如有)
          if (token && refreshToken) {
            saveToken(token, refreshToken);
            console.log('Token已保存');
          } else {
            console.warn('登录成功但未返回有效Token');
          }
          
          // 确保userData包含基本必需字段
          if (!userData.roleId && loginData.roleId) {
            userData.roleId = loginData.roleId;
          }
          
          if (!userData.username && loginData.username) {
            userData.username = loginData.username;
          }
          
          // 设置用户信息
          try {
            setUserInfo(userData);
            console.log('用户信息已保存:', userData);
          } catch (err) {
            console.error('设置用户信息时发生错误:', err);
            // 尝试使用最基本的用户信息
            const basicUserInfo = {
              userId: loginData.username,
              username: loginData.username,
              roleId: Number(loginData.roleId),
              roles: loginData.roleId === 1 ? ['elder'] : ['kin']
            };
            setUserInfo(basicUserInfo);
            console.log('使用基本信息设置用户:', basicUserInfo);
          }
          
          loading.value = false;
          return true;
        } else {
          console.error('登录成功但返回的数据格式不正确:', response);
          loading.value = false;
          return false;
        }
      } else {
        console.error('登录失败:', response?.message || '未知错误');
        loading.value = false;
        return false;
      }
    } catch (error) {
      console.error('登录过程中发生错误:', error);
      loading.value = false;
      throw error;
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
    loading,
    
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
