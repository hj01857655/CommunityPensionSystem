import { defineStore } from 'pinia';
import { userLogin, } from '@/api/fore/user';
import {TokenManager} from '@/utils/axios';
import { computed } from 'vue';

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null,//用户信息
    elderInfo: null,//老人信息
    kinInfo: null,//家属信息
    isLoggedIn: false, //是否登录
    roleId: null, //角色id
    roles: [] // 用户角色列表
  }),
  getters: {
    // 获取头像URL的计算属性
    avatarUrl: (state) => {
      const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';
      if (!state.userInfo?.avatar) return defaultAvatar;
      
      const avatarPath = state.userInfo.avatar;
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
        const response = await userLogin(data);
        if (response.code == 200 && response.data && response.message == "登录成功") {
          localStorage.setItem("isLoggedIn", "true");
          this.isLoggedIn = true;
          //将数据传递给store
          this.userInfo = response.data.user;
          this.elderInfo = response.data.user.elder;
          this.kinInfo = response.data.user.kin;
          this.roleId = response.data.user.roleId;
          this.roles = ["elder", "kin"]; // 根据roleId设置角色
          localStorage.setItem("kinInfo", JSON.stringify(response.data.user.kin));
          localStorage.setItem("roleId", response.data.user.roleId);
          //使用tokenManager存储token
          TokenManager.user.set(response.data.accessToken,response.data.refreshToken);
        }
        return response;
      } catch (error) {
        console.error("登录错误", error);
        throw new Error('登录过程中发生错误，请稍后再试');
      }
    },

    // 获取用户信息
    async getUserInfo() {
      try {
        // 如果已经有用户信息，直接返回
        if (this.userInfo) {
          return { code: 200, data: this.userInfo };
        }
        
        // 从本地存储获取用户信息
        const userInfoStr = localStorage.getItem('userInfo');
        if (!userInfoStr) {
          return { code: 401, message: '用户未登录' };
        }
        
        try {
          this.userInfo = JSON.parse(userInfoStr);
          
          // 如果是老人，获取老人信息
          if (this.roleId === 1) {
            const elderInfoStr = localStorage.getItem('elderInfo');
            if (elderInfoStr) {
              this.elderInfo = JSON.parse(elderInfoStr);
              Object.assign(this.userInfo, this.elderInfo);
            }
          }
          
          // 如果是家属，获取家属信息
          if (this.roleId === 2) {
            const kinInfoStr = localStorage.getItem('kinInfo');
            if (kinInfoStr) {
              this.kinInfo = JSON.parse(kinInfoStr);
              Object.assign(this.userInfo, this.kinInfo);
            }
          }
          
          return { code: 200, data: this.userInfo };
        } catch (error) {
          console.error('解析用户信息失败:', error);
          return { code: 500, message: '获取用户信息失败' };
        }
      } catch (error) {
        console.error('获取用户信息失败:', error);
        return { code: 500, message: '获取用户信息失败' };
      }
    },
    
    // 更新用户信息
    async handleUpdateUserInfo(userData) {
      try {
        // 此处可以添加API调用来更新后端数据
        const response = await updateUserInfo(userData);
        
        // 更新本地存储
        this.userInfo = { ...this.userInfo, ...userData };
        localStorage.setItem('userInfo', JSON.stringify(this.userInfo));
        
        // 根据角色更新特定信息
        if (this.roleId === 1) { // 老人
          this.elderInfo = { ...this.elderInfo, ...userData };
          localStorage.setItem('elderInfo', JSON.stringify(this.elderInfo));
        } else if (this.roleId === 2) { // 家属
          this.kinInfo = { ...this.kinInfo, ...userData };
          localStorage.setItem('kinInfo', JSON.stringify(this.kinInfo));
        }
        
        return true;
      } catch (error) {
        console.error('更新用户信息失败:', error);
        return false;
      }
    },
    
    // 获取老人信息
    async getElderInfo(elderId) {
      try {
        // 如果已经有老人信息，直接返回
        if (this.elderInfo && this.elderInfo.id === elderId) {
          return { code: 200, data: this.elderInfo };
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
    },
    
    // 获取未绑定家属的老人列表
    async fetchUnboundElders() {
      try {
        const response = await getUnboundElders();
        if (response.code === 200) {
          return response.data;
        }
        return [];
      } catch (error) {
        console.error('获取未绑定家属老人列表失败:', error);
        return [];
      }
    },

    // 绑定老人和家属关系
    async bindElderKin(elderId, kinId, relationType) {
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
    },

    // 解绑老人和家属关系
    async unbindElderKin(elderId, kinId) {
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
    },

    // 获取老人的家属列表
    async fetchKinListByElderId(elderId) {
      try {
        const response = await getKinIdsByElderId(elderId);
        if (response.code === 200) {
          return response.data;
        }
        return [];
      } catch (error) {
        console.error('获取老人家属列表失败:', error);
        return [];
      }
    },

    // 登出
    async logout() {
      // 清除状态
      this.userInfo = null;
      this.elderInfo = null;
      this.kinInfo = null;
      this.isLoggedIn = false;
      this.roleId = null;
      this.roles = [];
      
      // 清除本地存储
      localStorage.removeItem('userInfo');
      localStorage.removeItem('elderInfo');
      localStorage.removeItem('kinInfo');
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('roleId');
      
      // 清除token
      TokenManager.user.clear();
      
      return true;
    }
  }
});
