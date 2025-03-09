import { defineStore } from 'pinia';
import { ref } from 'vue';
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
