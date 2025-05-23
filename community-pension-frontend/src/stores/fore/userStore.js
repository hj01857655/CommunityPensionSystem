import {
  bindElderKinRelation,
  getKinListByElderId,
  getUnboundElders,
  unbindElderKinRelation,
  userLogin,
  updateUserInfo
} from '@/api/fore/user';
import { getHealthRecords as getHealthRecordsApi } from '@/api/fore/health';
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

  // 获取用户角色
  const userRole = computed(() => {
    // 获取角色，优先使用roleId
    const role = Number(roleId.value) || Number(localStorage.getItem('roleId')) || null;
    console.log('当前用户角色信息:', {
      storeRoleId: roleId.value,
      localStorageRoleId: localStorage.getItem('roleId'),
      calculatedRole: role,
      roleType: typeof role
    });
    
    if (!role) {
      console.warn('用户角色未定义，可能未正确加载用户信息');
    }
    return role;
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
        allergy: user.allergy || '',
        medicalHistory: user.medicalHistory || '',
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
      const userInfoStr = localStorage.getItem('userInfo');
      // console.log('从localStorage获取的原始用户信息:', userInfoStr);
      
      if (userInfoStr) {
        const parsedUserInfo = JSON.parse(userInfoStr);
        // console.log('解析后的用户信息:', parsedUserInfo);
        
        // 确保角色ID存在并设置到store中
        if (parsedUserInfo.roleId) {
          roleId.value = Number(parsedUserInfo.roleId);
          // console.log('设置roleId到store:', roleId.value);
        }
        
        // 确保用户信息设置到store中
        userInfo.value = parsedUserInfo;
        isLoggedIn.value = true;
        
        // 确保角色设置正确
        if (parsedUserInfo.roles && Array.isArray(parsedUserInfo.roles)) {
          roles.value = parsedUserInfo.roles;
        } else if (parsedUserInfo.roleId) {
          // 根据roleId推断角色
          roles.value = parsedUserInfo.roleId === 1 ? ['elder'] : ['kin'];
        }
        
        return {
          code: 200,
          data: parsedUserInfo
        };
      }

      // 从API获取用户信息（如果本地没有）
      const response = await axios.get('/user/info');
      if (response.code === 200 && response.data) {
        const userData = response.data;
        // 确保角色ID存在
        if (!userData.roleId && userData.roleIdList && userData.roleIdList.length > 0) {
          userData.roleId = userData.roleIdList[0];
        }
        
        // 存储到本地和store
        localStorage.setItem('userInfo', JSON.stringify(userData));
        userInfo.value = userData;
        roleId.value = Number(userData.roleId);
        isLoggedIn.value = true;
        
        return {
          code: 200,
          data: userData
        };
      }
      
      return {
        code: 404,
        message: '未找到用户信息'
      };
    } catch (error) {
      console.error('获取用户信息失败:', error);
      return {
        code: 500,
        message: '获取用户信息失败'
      };
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

  /**
   * 更新用户信息
   * @param {Object} userData - 用户数据
   * @returns {Promise<boolean>} - 是否更新成功
   */
  const handleUpdateUserInfo = async (userData) => {
    try {
      loading.value = true;
      // 确保userData中包含userId
      if (!userData.userId) {
        if (userData.id) {
          userData.userId = userData.id;
        } else if (userInfo.value && userInfo.value.userId) {
          userData.userId = userInfo.value.userId;
        } else if (userInfo.value && userInfo.value.id) {
          userData.userId = userInfo.value.id;
        }
      }
      
      // 创建一个只包含必要字段的数据对象
      const updateData = {
        userId: userData.userId,
        name: userData.name,
        gender: userData.gender,
        birthday: userData.birthday,
        idCard: userData.idCard,
        phone: userData.phone,
        address: userData.address,
        email: userData.email,
        emergencyContactName: userData.emergencyContactName,
        emergencyContactPhone: userData.emergencyContactPhone,
        healthCondition: userData.healthCondition,
        allergy: userData.allergy,
        medicalHistory: userData.medicalHistory,
        avatar: userData.avatar
      };
      
      console.log('userStore中处理后的用户数据:', updateData);
      
      if (!updateData.userId) {
        console.error('无法获取用户ID，更新失败');
        return false;
      }
      
      const response = await updateUserInfo(updateData);
      if (response.code === 200) {
        // 更新store中的用户信息
        userInfo.value = response.data;
        
        // 根据角色更新特定信息
        if (roleId.value === 1) { // 老人
          elderInfo.value = { ...elderInfo.value, ...response.data };
          localStorage.setItem('elderInfo', JSON.stringify(elderInfo.value));
        } else if (roleId.value === 2) { // 家属
          kinInfo.value = { ...kinInfo.value, ...response.data };
          localStorage.setItem('kinInfo', JSON.stringify(kinInfo.value));
        }
        
        return true;
      }
      return false;
    } catch (error) {
      console.error('更新用户信息失败:', error);
      return false;
    } finally {
      loading.value = false;
    }
  };

  const getElderInfo = async (elderId) => {
    try {
      // 如果已经有老人信息，直接返回
      if (elderInfo.value && elderInfo.value.id === elderId) {
        return { code: 200, data: elderInfo.value };
      }
      
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
      // 使用正确的API路径 - 从health.js中获取
      const response = await getHealthRecordsApi(elderId);
      
      // 如果成功获取健康记录
      if (response.code === 200 && response.data) {
        return response;
      }
      
      // 如果未获取到数据，返回空对象
      return {
        code: 200,
        data: {
          allergy: '',
          medicalHistory: '',
          healthCondition: ''
        },
        msg: '未找到健康信息'
      };
    } catch (error) {
      console.error('获取健康记录失败:', error);
      // 返回默认值而非null，确保前端不会因API错误而崩溃
      return {
        code: 500,
        data: {
          allergy: '',
          medicalHistory: '',
          healthCondition: ''
        },
        msg: '获取健康信息失败'
      };
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
    userRole,
    
    // 方法
    setUserInfo,
    login,
    getUserInfo,
    logout,
    handleUpdateUserInfo,
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
