import { defineStore } from 'pinia';
import { adminLogin, adminLogout, refreshAdminToken, getAdminInfo } from '@/api/back/login';
import { TokenManager } from '@/utils/axios';
import { ElMessage } from 'element-plus';
import { getAvatarUrl } from '@/utils/avatarUtils';
export const useAdminStore = defineStore('admin', {
  // 状态
  state: () => ({
    userInfo: {},
    roleId: '',
    roles: [],
    permissions: [],
    name: '',
    avatar: '',
    token: TokenManager.admin.getAccessToken() || '',
    refreshToken: TokenManager.admin.getRefreshToken() || '',
    isCollapse: false,        // 侧边栏折叠状态
    lastLoginTime: null,      // 最后登录时间
    menuList: [],            // 用户菜单列表
    settings: {              // 用户设置
      theme: 'light',
      language: 'zh-CN',
      showTags: true
    }
  }),

  // 计算属性
  getters: {
    // 获取token
    getAccessToken: (state) => state.token || TokenManager.admin.getAccessToken(),
    // 获取刷新token
    getRefreshToken: (state) => state.refreshToken || TokenManager.admin.getRefreshToken(),
    // 是否已登录
    isLoggedIn: (state) => !!state.userInfo,
    // 获取用户角色
    userRoles: (state) => state.roles,
    // 获取用户权限
    userPermissions: (state) => state.permissions,
    // 获取用户头像
    userAvatar: (state) => getAvatarUrl(state.avatar),
    // 判断是否有token
    hasToken: (state) => !!state.token,
    // 获取用户名
    username: (state) => state.userInfo?.username || '',
    // 获取角色名称列表
    roleNames: (state) => {
      const roleMap = {
        1: '老人',
        2: '家属',
        3: '社区工作人员',
        4: '管理员'
      };
      return state.roles.map(roleId => roleMap[roleId] );
    },
    // 获取角色ID
    roleIds: (state) => {
      const roleMap = {
        '老人': 1,
        '家属': 2,
        '社区工作人员': 3,
        '管理员': 4
      };
      return state.roles.map(roleName => roleMap[roleName] || 4);
    }
  },

  // 方法
  actions: {
    // 设置用户信息
    setUserInfo(user) {
      if (user) {
        this.userInfo = user;//用户信息
        this.name = user.name;//用户名
        this.avatar = user.avatar || '';//用户头像
        this.roles = [user.roleId];//用户角色
        this.roleId = user.roleId;//用户角色ID
        this.lastLoginTime = new Date().toISOString();//最后登录时间
        sessionStorage.setItem('userInfo', JSON.stringify(this.userInfo));
      } else {
        // this.resetState();
      }
    },

    // 设置token
    setToken(accessToken, refreshToken) {
      if (accessToken && refreshToken) {
        TokenManager.admin.set(accessToken, refreshToken);
        this.token = accessToken;
        this.refreshToken = refreshToken;
      }
    },

    // 设置角色
    setRoles(roles) {
      this.roles = Array.isArray(roles) ? roles : [roles];
    },

    // 设置权限
    setPermissions(permissions) {
      this.permissions = Array.isArray(permissions) ? permissions : [];
    },

    // 设置菜单
    setMenuList(menuList) {
      this.menuList = menuList;
    },

    // 切换侧边栏状态
    toggleSideBar() {
      this.isCollapse = !this.isCollapse;
    },

    // 更新用户设置
    updateSettings(settings) {
      this.settings = { ...this.settings, ...settings };
    },

    // 重置状态
    resetState() {
      this.userInfo = null;
      this.roles = [];
      this.permissions = [];
      this.name = '';
      this.avatar = '';
      this.token = '';
      this.refreshToken = '';
      this.menuList = [];
      this.lastLoginTime = null;
      TokenManager.admin.clear();
    },

    // 初始化应用
    async initApp() {
      try {
        if (this.getAccessToken) {
          await this.getInfo();
          // 可以添加其他初始化操作
        }
      } catch (error) {
        this.resetState();
        throw error;
      }
    },

    // 登录
    async login(loginData) {
      try {
        const { username, password, roleId } = loginData;
        // 确保 roleId 是数字类型
        const parsedRoleId = parseInt(roleId, 10);

        if (isNaN(parsedRoleId)) {
          throw new Error('无效的角色ID');
        }

        const response = await adminLogin({
          username: username.trim(),
          password: password,
          roleId: parsedRoleId
        });
        
        if (response.code === 200 && response.data) {
          const { user, accessToken, refreshToken } = response.data;

          // 设置用户信息
          this.setUserInfo(user);

          // 设置token
          this.setToken(accessToken, refreshToken);

          // 存储用户角色
          const roleName = user.roleId === 4 ? 'admin' : 'staff';
          localStorage.setItem('userRole', roleName);

          return response;
        }
        throw new Error(response.message || '登录失败');
      } catch (error) {
        console.error('登录失败:', error);
        ElMessage.error(error.message || '登录失败，请稍后重试');
        throw error;
      }
    },

    // 获取用户信息
    async getInfo(userId) {
      try {
        // 检查 token 和 userId
        if (!this.getAccessToken) {
          throw new Error('未登录或登录已过期');
        }
        
        if (!userId) {
          throw new Error('用户ID不能为空');
        }

        // 尝试从缓存获取用户信息
        const cachedUserInfo = sessionStorage.getItem('userInfo');
        if (cachedUserInfo) {
          const parsedUserInfo = JSON.parse(cachedUserInfo);
          if (parsedUserInfo.userId === userId) {
            this.setUserInfo(parsedUserInfo);
            return { code: 200, data: { user: parsedUserInfo } };
          }
        }

        // 如果缓存中没有或 userId 不匹配，则从服务器获取
        const response = await getAdminInfo(userId);
        if (response.code === 200 && response.data) {
          const { user, roles } = response.data;

          // 验证响应数据的完整性
          if (!user || !user.userId) {
            throw new Error('获取的用户信息不完整');
          }

          // 设置用户信息
          this.setUserInfo(user);

          // 设置角色和权限
          if (roles) {
            this.setRoles(roles);
          }

          // 更新缓存
          sessionStorage.setItem('userInfo', JSON.stringify(user));

          return response;
        }

        throw new Error(response.message || '获取用户信息失败');
      } catch (error) {
        console.error('获取用户信息失败:', error);
        
        // 根据错误类型处理
        if (error.response?.status === 401) {
          ElMessage.error('登录已过期，请重新登录');
          this.resetState();
          return { code: 401, message: '登录已过期' };
        } else if (error.message.includes('网络错误')) {
          ElMessage.error('网络连接失败，请检查网络设置');
          return { code: -1, message: '网络连接失败' };
        } else {
          ElMessage.error(error.message || '获取用户信息失败');
          
          // 如果是 token 相关错误，清除状态
          if (error.message.includes('未登录') || error.message.includes('过期')) {
            this.resetState();
          }
          
          throw error;
        }
      }
    },

    // 更新用户信息
    async updateUserInfo(userInfo) {
      try {
        // 这里可以添加更新用户信息的API调用
        this.userInfo = { ...this.userInfo, ...userInfo };
        return true;
      } catch (error) {
        console.error('更新用户信息失败:', error);
        ElMessage.error(error.message || '更新用户信息失败');
        return false;
      }
    },

    // 刷新token
    async refreshAccessToken() {
      try {
        const response = await refreshAdminToken();
        if (response.code === 200 && response.data) {
          const { accessToken, refreshToken } = response.data;
          this.setToken(accessToken, refreshToken);
          return true;
        }
        return false;
      } catch (error) {
        console.error('刷新token失败:', error);
        return false;
      }
    },

    // 退出登录
    async logout() {
      try {
        await adminLogout();
        // 清除后台相关的存储
        TokenManager.admin.clear();
        // 清除 sessionStorage 中的后台相关数据
        sessionStorage.removeItem('userRole');
        sessionStorage.removeItem('rememberedUsername');
        sessionStorage.removeItem('rememberedRole');
        sessionStorage.removeItem('rememberRoleType');
        sessionStorage.removeItem('rememberedRoleId');
        
        // 重置状态
        this.resetState();
        
        // 跳转到后台登录页面
        window.location.href = '/admin/login';
        return true;
      } catch (error) {
        console.error('退出登录失败:', error);
        // 即使退出失败也重置状态
        this.resetState();
        throw error;
      }
    },

    // 检查权限
    hasPermission(permission) {
      return this.permissions.includes(permission);
    },

    // 检查角色
    hasRole(role) {
      return this.roles.includes(role);
    }
  }
});