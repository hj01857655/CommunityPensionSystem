import { defineStore } from 'pinia';
import { 
  userLogin, 
  getUnboundElders, 
  getKinListByElderId,
  bindElderKinRelation,
  unbindElderKinRelation
} from '@/api/fore/user';
import { TokenManager } from '@/utils/axios';
import { computed } from 'vue';
import { getAvatarUrl } from '@/utils/avatarUtils';  // 导入头像工具函数
import axios from '@/utils/axios';
import { ElMessage } from 'element-plus';
import { getHealthRecords } from '@/api/fore/health';

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
      return getAvatarUrl(state.userInfo?.avatar);
    }
  },
  actions: {
    // 登录
    async login(loginForm) {
      try {
        const response = await userLogin(loginForm);
        
        if (response.code === 200) {
          console.log("useUserStore登录成功响应",response);
          // 保存用户信息到本地存储
          const userInfo = response.data.user;
          localStorage.setItem("userInfo", JSON.stringify(userInfo));
          localStorage.setItem("userId", userInfo.id);
          localStorage.setItem("username", userInfo.username);
          localStorage.setItem("name", userInfo.name);
          localStorage.setItem("roleId", userInfo.roleIds[0]);
          localStorage.setItem("role", userInfo.roleIds[0]);
          localStorage.setItem("roleName", userInfo.roleNames[0]);
          localStorage.setItem("isLoggedIn", "true");

          // 如果是家属，保存关联的老人信息
          if (userInfo.role === 'kin' && userInfo.bindElder) {
            localStorage.setItem("bindElder", JSON.stringify(userInfo.bindElder));
          }

          // 更新状态
          this.userInfo = userInfo;
          this.isLoggedIn = true;
          this.roleId = userInfo.roleId;
          this.roles = [userInfo.role];

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
    
    // 获取未绑定的老人列表
    async fetchUnboundElders() {
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
    },

    // 获取老人的家属列表
    async fetchKinListByElderId(elderId) {
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
    },

    // 获取健康记录
    async getHealthRecords(elderId) {
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
    }
  }
});
