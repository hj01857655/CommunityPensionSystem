import { defineStore } from 'pinia';
<<<<<<< HEAD
import { ref } from 'vue';
=======
import { computed, ref } from 'vue';
>>>>>>> 2d66c49379de0f514f5b448f414d6cf67e21e6e6
import { adminLogin, setToken, clearToken, refreshToken } from '@/api/admin';

// 管理员状态管理
export const useAdminStore = defineStore('admin', () => {
    // 管理员信息
    const adminInfo = ref({

    });
    // 管理员是否登录
    const isLoginIn = ref(false);
    const remember = ref(false);
    // 管理员token
    const adminToken = ref('');

    // 管理员登录
    const adminLogins = async (loginData) => {
<<<<<<< HEAD
        const result = await adminLogin(loginData);
        //response是正常的响应
        if (result && result.data) {
            if (result && result.data) {
                console.log(result.data.token)
                console.log(result.data.user)
                // 设置token
                setToken(result.data.token)
                // 设置管理员信息
                adminInfo.value = result.data.user;
                isLoginIn.value = true;

                return result.data;
            } else {
                console.log(result)
                return result;
            }
        };
=======
        const response = await adminLogin(loginData);
        /**
         * response:
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
        if(response&&response.data){
            console.log("adminStore.js:response.data", response.data)
            // 设置token
            setToken(response.data.token)
            // 设置管理员信息
            adminInfo.value = response.data.user;
            isLoginIn.value = true;
            
            return response.data;
        }else{
            console.log(response)
            return response;
        }
>>>>>>> 2d66c49379de0f514f5b448f414d6cf67e21e6e6
    };
    // 退出登录
    const logout = () => {
        // 清除token
        clearToken();
        // 清除管理员信息
        adminInfo.value = {};
    };
    return {
        adminInfo,
        adminToken,
        isLoginIn,
        remember,
        adminLogins,
        logout,
        refreshToken,
    };
});
