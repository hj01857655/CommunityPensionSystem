import { defineStore } from 'pinia';
import { userLogin } from '@/api/fore/user';
import {TokenManager} from '@/utils/axios';
import { computed } from 'vue';

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null,//用户信息
    elderInfo: null,//老人信息
    kinInfo: null,//家属信息
    isLoggedIn: false, //是否登录
    roleId: null, //角色id
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
      try{
        const response = await userLogin(data);
        //将数据传递给store
        if(response.code==200&&response.data&&response.message=="登录成功"){
          console.log("登录接口在store中返回数据", response);
          //将数据存储到本地
          localStorage.setItem("userInfo",JSON.stringify(response.data.user));
          localStorage.setItem("elderInfo",JSON.stringify(response.data.user.elder));
          localStorage.setItem("kinInfo",JSON.stringify(response.data.user.kin));
          localStorage.setItem("roleId",JSON.stringify(response.data.user.roleId));
          localStorage.setItem("isLoggedIn",true);
          //将数据传递给store
          this.userInfo = response.data.user;
          this.elderInfo = response.data.user.elder;
          this.kinInfo = response.data.user.kin;
          this.roleId = response.data.user.roleId;
          this.isLoggedIn = true;
      
          //使用tokenManager存储token
          TokenManager.user.set(response.data.accessToken,response.data.refreshToken);
          //设置登录状态

        }
        return response;
      }catch(error){
        console.error("登录错误", error);
        throw new Error('登录过程中发生错误，请稍后再试');
      }
    },
    //处理头像

    // 更新用户信息
    async updateProfile(data) {
      try {
        const response = await updateUserInfo(data);
        if (response.code === 200) {
          this.userInfo = response.data;
          if (this.roleId === 1) { // 老人
            this.elderInfo = response.data;
          } else if (this.roleId === 2) { // 家属
            this.kinInfo = response.data;
          }
          return response;
        }
      } catch (error) {
        console.error('更新用户信息错误:', error);
        throw error;
      }
    }
  }
});
