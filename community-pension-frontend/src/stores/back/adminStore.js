import { defineStore } from 'pinia';
import { adminLogin, adminLogout } from '@/api/back/AdminLogin';
import { getAvatarUrl } from '@/utils/avatarUtils';

export const useAdminStore = defineStore('admin', {
  state: () => ({
    adminInfo: {}, // 管理员信息
    isLoggedIn: false, // 是否登录
    roleId: null, // 角色ID
  }),
  getters: {
    avatarUrl: (state) => {
      return getAvatarUrl(state.adminInfo.avatar);
    },
  },
  actions: {
    // 登录
    async login(data) {
      try {
        const response = await adminLogin(data);
        if (response.code === 200 && response.data) {
          // 先更新 store 中的 adminInfo
          this.adminInfo = response.data.user;
          this.roleId = response.data.user.roleId;
          this.isLoggedIn = true;

          // 然后存储到 sessionStorage
          sessionStorage.setItem("adminInfo", JSON.stringify(response.data.user));
          sessionStorage.setItem("roleId", JSON.stringify(response.data.user.roleId));
          sessionStorage.setItem("isAdminLoggedIn", "true");

          console.log("管理员信息已更新:", this.adminInfo);

          return response;
        }
        return response;
      } catch (error) {
        console.error("管理员登录错误", error);
        throw new Error('登录过程中发生错误，请稍后再试');
      }
    },
    
    // 退出登录
    async logout() {
      try {
        const response = await adminLogout();
        if (response.code === 200) {
          // 清除会话存储
          sessionStorage.removeItem("adminInfo");
          sessionStorage.removeItem("roleId");
          sessionStorage.removeItem("isAdminLoggedIn");
          
          // 清除store
          this.adminInfo = null;
          this.roleId = null;
          this.isLoggedIn = false;
          
          return response;
        }
        return response;
      } catch (error) {
        console.error("管理员退出错误", error);
        throw new Error('退出过程中发生错误，请稍后再试');
      }
    },
    
    // 初始化管理员信息（从本地存储加载）
    initAdminInfo() {
      const adminInfoStr = sessionStorage.getItem("adminInfo");
      const isLoggedIn = sessionStorage.getItem("isAdminLoggedIn") === "true";
      const roleId = sessionStorage.getItem("roleId");
      
      if (adminInfoStr && isLoggedIn) {
        this.adminInfo = JSON.parse(adminInfoStr);
        this.isLoggedIn = true;
        this.roleId = roleId ? JSON.parse(roleId) : null;
      }
    }
  }
});