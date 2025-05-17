import { adminLogin, adminLogout, getAdminInfo, refreshAdminToken } from '@/api/back/login';
import { getAvatarUrl } from '@/utils/avatarUtils';
import { TokenManager } from '@/utils/axios';
import { ElMessage } from 'element-plus';
import { defineStore } from 'pinia';
import { computed, reactive, ref } from 'vue';

export const useAdminStore = defineStore('admin', () => {
  // state
  const userInfo = ref({});
  const roleId = ref('');
  const roles = ref([]);
  const permissions = ref([]);
  const name = ref('');
  const avatar = ref('');
  const token = ref(TokenManager.admin.getAccessToken() || '');
  const refreshToken = ref(TokenManager.admin.getRefreshToken() || '');
  const isCollapse = ref(false);
  const lastLoginTime = ref(null);
  const menuList = ref([]);
  const settings = reactive({
    theme: 'light',
    language: 'zh-CN',
    showTags: true
  });

  // getters
  const getAccessToken = computed(() => token.value || TokenManager.admin.getAccessToken());
  const getRefreshToken = computed(() => refreshToken.value || TokenManager.admin.getRefreshToken());
  const isLoggedIn = computed(() => !!userInfo.value);
  const userRoles = computed(() => roles.value);
  const userPermissions = computed(() => permissions.value);
  const userAvatar = computed(() => getAvatarUrl(avatar.value));
  const hasToken = computed(() => !!token.value);
  const username = computed(() => userInfo.value?.username || '');
  const roleNames = computed(() => {
    const roleMap = {
      1: '老人',
      2: '家属',
      3: '社区工作人员',
      4: '管理员'
    };
    return roles.value.map(roleId => roleMap[roleId]);
  });
  const roleIds = computed(() => {
    const roleMap = {
      '老人': 1,
      '家属': 2,
      '社区工作人员': 3,
      '管理员': 4
    };
    return roles.value.map(roleName => roleMap[roleName] || 4);
  });

  // actions
  function setUserInfo(user) {
    if (user) {
      userInfo.value = user;
      name.value = user.name;
      avatar.value = user.avatar || '';
      roles.value = [user.roleId];
      roleId.value = user.roleId;
      lastLoginTime.value = new Date().toISOString();
      sessionStorage.setItem('userInfo', JSON.stringify(userInfo.value));
    }
  }

  function setToken(accessToken, refreshTokenVal) {
    if (accessToken && refreshTokenVal) {
      TokenManager.admin.set(accessToken, refreshTokenVal);
      token.value = accessToken;
      refreshToken.value = refreshTokenVal;
    }
  }

  function setRoles(rolesVal) {
    roles.value = Array.isArray(rolesVal) ? rolesVal : [rolesVal];
  }

  function setPermissions(permissionsVal) {
    permissions.value = Array.isArray(permissionsVal) ? permissionsVal : [];
  }

  function setMenuList(menuListVal) {
    menuList.value = menuListVal;
  }

  function toggleSideBar() {
    isCollapse.value = !isCollapse.value;
  }

  function updateSettings(settingsVal) {
    Object.assign(settings, settingsVal);
  }

  function resetState() {
    userInfo.value = null;
    roles.value = [];
    permissions.value = [];
    name.value = '';
    avatar.value = '';
    token.value = '';
    refreshToken.value = '';
    menuList.value = [];
    lastLoginTime.value = null;
    TokenManager.admin.clear();
  }

  async function initApp() {
    try {
      if (getAccessToken.value) {
        await getInfo();
      }
    } catch (error) {
      resetState();
      throw error;
    }
  }

  async function login(loginData) {
    try {
      const { username: uname, password, roleId: rid } = loginData;
      const parsedRoleId = parseInt(rid, 10);
      if (isNaN(parsedRoleId)) {
        throw new Error('无效的角色ID');
      }
      const response = await adminLogin({
        username: uname.trim(),
        password: password,
        roleId: parsedRoleId
      });
      if (response.code === 200 && response.data) {
        const { user, accessToken, refreshToken: refreshTokenRes } = response.data;
        setUserInfo(user);
        setToken(accessToken, refreshTokenRes);
        const roleName = user.roleId === 4 ? 'admin' : 'staff';
        sessionStorage.setItem('userRole', roleName);
        const userInfoWithRoles = {
          ...user,
          roles: [roleName]
        };
        sessionStorage.setItem('admin-user-info', JSON.stringify(userInfoWithRoles));
        return response;
      }
      throw new Error(response.message || '登录失败');
    } catch (error) {
      console.error('登录失败:', error);
      ElMessage.error(error.message || '登录失败，请稍后重试');
      throw error;
    }
  }

  async function getInfo(userId) {
    try {
      if (!getAccessToken.value) {
        throw new Error('未登录或登录已过期');
      }
      if (!userId) {
        throw new Error('用户ID不能为空');
      }
      const cachedUserInfo = sessionStorage.getItem('userInfo');
      if (cachedUserInfo) {
        const parsedUserInfo = JSON.parse(cachedUserInfo);
        if (parsedUserInfo.userId === userId) {
          setUserInfo(parsedUserInfo);
          return { code: 200, data: { user: parsedUserInfo } };
        }
      }
      const response = await getAdminInfo(userId);
      if (response.code === 200 && response.data) {
        const { user, roles: rolesRes } = response.data;
        if (!user || !user.userId) {
          throw new Error('获取的用户信息不完整');
        }
        setUserInfo(user);
        if (rolesRes) {
          setRoles(rolesRes);
        }
        sessionStorage.setItem('userInfo', JSON.stringify(user));
        return response;
      }
      throw new Error(response.message || '获取用户信息失败');
    } catch (error) {
      console.error('获取用户信息失败:', error);
      if (error.response?.status === 401) {
        ElMessage.error('登录已过期，请重新登录');
        resetState();
        return { code: 401, message: '登录已过期' };
      } else if (error.message.includes('网络错误')) {
        ElMessage.error('网络连接失败，请检查网络设置');
        return { code: -1, message: '网络连接失败' };
      } else {
        ElMessage.error(error.message || '获取用户信息失败');
        if (error.message.includes('未登录') || error.message.includes('过期')) {
          resetState();
        }
        throw error;
      }
    }
  }

  async function updateUserInfo(userInfoVal) {
    try {
      userInfo.value = { ...userInfo.value, ...userInfoVal };
      return true;
    } catch (error) {
      console.error('更新用户信息失败:', error);
      ElMessage.error(error.message || '更新用户信息失败');
      return false;
    }
  }

  async function refreshAccessToken() {
    try {
      const response = await refreshAdminToken();
      if (response.code === 200 && response.data) {
        const { accessToken: accessTokenRes, refreshToken: refreshTokenRes } = response.data;
        setToken(accessTokenRes, refreshTokenRes);
        return true;
      }
      return false;
    } catch (error) {
      console.error('刷新token失败:', error);
      return false;
    }
  }

  async function logout() {
    try {
      await adminLogout();
      TokenManager.admin.clear();
      sessionStorage.clear();
      resetState();
      window.location.href = '/admin/login';
      return true;
    } catch (error) {
      console.error('退出登录失败:', error);
      resetState();
      throw error;
    }
  }

  function hasPermission(permission) {
    return permissions.value.includes(permission);
  }

  function hasRole(role) {
    return roles.value.includes(role);
  }

  return {
    // state
    userInfo,
    roleId,
    roles,
    permissions,
    name,
    avatar,
    token,
    refreshToken,
    isCollapse,
    lastLoginTime,
    menuList,
    settings,
    // getters
    getAccessToken,
    getRefreshToken,
    isLoggedIn,
    userRoles,
    userPermissions,
    userAvatar,
    hasToken,
    username,
    roleNames,
    roleIds,
    // actions
    setUserInfo,
    setToken,
    setRoles,
    setPermissions,
    setMenuList,
    toggleSideBar,
    updateSettings,
    resetState,
    initApp,
    login,
    getInfo,
    updateUserInfo,
    refreshAccessToken,
    logout,
    hasPermission,
    hasRole
  };
});