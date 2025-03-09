import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import { adminLogin, getAdminInfo } from '@/api/admin';

// 管理员状态管理
export const useAdminStore = defineStore('admin', () => {
    // 管理员信息
    const adminInfo = ref({
        
    });
    // 管理员是否登录
    const isLoginIn = ref(false);
    // 管理员token
    const adminToken = ref('');
    
    // 设置token
    const setTokens = (newToken) => {
        adminToken.value = newToken;
        localStorage.setItem('admin-token', newToken);
    };
    
    // 获取token
    const getTokens = () => {
        if (!adminToken.value) {
            adminToken.value = localStorage.getItem('admin-token') || '';
        }
        return adminToken.value;
    };
    const removeTokens = () => {
        // 如果token存在，则清除token
        if(adminToken.value){
            localStorage.removeItem('admin-token');
            adminToken.value = '';
        }else{
            // 如果token不存在，则清除所有localStorage
            localStorage.clear();
        }
    };
    
    // 管理员登录
    const adminLogins = async (loginData) => {
        const response = await adminLogin(loginData);
        /**
         * {
         *  code: 200,
         *  message: "登录成功",
         *  data: {
         *      token: "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGVJZCI6NCwiaWF0IjoxNzQxNTM4NzEyLCJleHAiOjE3NDE1NDIzMTJ9.Q0nJ9tYzqsnow9006yCtu3NneX8EHwkUXx1Exm7zxVI",
         *      user: {
         *          id: 4,
         *          username: "admin",
         *          password: "123456",
         *          roleId: 4,
         *          status: 1,
         *          createTime: "2025-03-09 10:00:00",
         *          updateTime: "2025-03-09 10:00:00",
         *      }
         *  }
         * }
         */
        //response是正常的响应
        if(response&&response.data){
            console.log("adminStore.js:response.data", response.data)
            // 设置token
            setTokens(response.data.token)
            // 设置管理员信息
            adminInfo.value = response.data.user;
            return response.data;
        }else{
            console.log(response)
            return response;
        }
    };
    
    // 获取管理员信息
    const getAdminInfos = async () => {
        const currentToken = getTokens();
        if (!currentToken) {
           return { success: false, error: '未登录' };
        }
        const response = await getAdminInfo(currentToken);
        if (response.code === 200||response.success) {
            adminInfo.value = response.data.data;
            console.log(adminInfo.value)
            adminInfo.value.avatarUrl = computed(() => {
                return adminInfo.value.avatarUrl || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
            })
        }
        return response.data;
    };
    
    // 退出登录
    const logout = () => {
        // 清除token
        removeTokens();
        // 清除管理员信息
        adminInfo.value = {};
    };
    
    return {
        adminInfo,
        adminToken,
        setTokens,
        getTokens,
        adminLogins,
        getAdminInfos,
        logout,
    };
});
