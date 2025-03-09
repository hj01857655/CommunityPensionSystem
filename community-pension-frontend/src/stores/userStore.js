import { defineStore } from 'pinia';
import { ref,computed } from 'vue';
import { userLogin, getUserProfile } from '@/api/user';
// 用户信息
export const useUserStore = defineStore('user', () => {
    // 登录token
    const token = ref('');
    const rememberMe = ref(false);
    // 用户信息
    const userInfo = ref({});
    // 登录状态
    const isLoggedIn = ref(false);
    // 用户角色
    const userRole = ref(null);

    // 删除token
    const removeToken = () => {
        localStorage.clear();
        token.value = '';
        userInfo.value = {};
        userRole.value = null;
        isLoggedIn.value = false;
    };
    // 设置token
    const setToken = (token) => {
        if (token) {
            localStorage.setItem('user-token', token);
        } else {
        }
    };
    //获取token
    const getToken = () => {
        const token = localStorage.getItem('user-token');
        if (token) {
            return token;
        } else {
            return null;
        }

    };
    
    // 用户登录
    const userLogins = async (loginData) => {
        const response = await userLogin(loginData);
        /**
         * {
         *  code:200,
         *  data:{
         *      token:userToken,
         *      userInfo:userInfo
         *  },
         *  message:response.message
         * }
         */
    
        if(response.code==200&&response.data){
            const userToken=response.data.token;
            if(userToken){
                removeToken();
                setToken(userToken);
                isLoggedIn.value=true;
            }
            return response.data;
        }else{
            return response;
        }
    };


    // 获取用户信息
    const getUserInfos = async () => {
        try {
            const response = await getUserProfile();
            console.log("userStore:getUserInfos:response:",response)
            return response
        } catch (error) {
            return { success: false, message: error.message || '获取用户信息失败' };
        }
    };

    // 退出登录
    const logouts = () => {
        if (getToken()) {
            removeToken();
        } else {
            localStorage.clear();
        }
    };

    return {
        rememberMe,
        isLoggedIn,
        userInfo,
        userRole,
        userLogins,
        getUserInfos,
        logouts
    };
});