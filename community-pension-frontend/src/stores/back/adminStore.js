import { defineStore } from 'pinia';
import { adminLogin, adminLogout } from '@/api/back/AdminLogin';
import { ElMessage } from 'element-plus';

export const useAdminStore = defineStore('admin', {
  state: () => ({
    adminInfo: null, // 管理员信息
    isLoggedIn: false, // 是否登录
    roleId: null, // 角色ID
  }),
  getters: {
    // 获取头像URL的计算属性
    avatarUrl: (state) => {
      const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';
      if (!state.adminInfo?.avatar) return defaultAvatar;
      
      const avatarPath = state.adminInfo.avatar;
      // 如果已经是完整的URL，直接返回
      if (avatarPath.startsWith('http://') || avatarPath.startsWith('https://')) {
        return avatarPath;
      }
      
      // 如果是本地assets目录下的文件
      if (avatarPath.includes('src/assets')) {
        try {
          // 将路径转换为相对于assets的路径
          const assetPath = avatarPath.replace('src/assets/', '');
          // 使用new URL()构造资源URL
          return new URL(`../../assets/${assetPath}`, import.meta.url).href;
        } catch (error) {
          console.error('Error loading avatar from assets:', error);
          return defaultAvatar;
        }
      }
      
      // 如果是后端API路径，拼接后端服务地址
      const baseUrl = import.meta.env.PROD ? 'http://127.0.0.1:9000' : '';
      // 确保路径以/开头
      const normalizedPath = avatarPath.startsWith('/') ? avatarPath : `/${avatarPath}`;
      return `${baseUrl}${normalizedPath}`;
    }
  },
  actions: {
    // 登录
    async login(data) {
      try {
        const response = await adminLogin(data);
        if (response.code === 200 && response.data) {
          console.log("管理员登录接口在store中返回数据", response);
          
          // 将数据存储到会话存储
          sessionStorage.setItem("adminInfo", JSON.stringify(response.data.user));
          sessionStorage.setItem("roleId", JSON.stringify(response.data.user.roleId));
          sessionStorage.setItem("isAdminLoggedIn", "true");
          
          // 将数据传递给store
          this.adminInfo = response.data.user;
          this.roleId = response.data.user.roleId;
          this.isLoggedIn = true;
          
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