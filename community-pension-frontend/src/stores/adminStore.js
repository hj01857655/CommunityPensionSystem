import { defineStore } from 'pinia';
import { ref } from 'vue';
import { adminLogin, setToken, logouts, refreshToken } from '@/api/admin';

// 管理员状态管理
export const useAdminStore = defineStore('admin', () => {
    // 管理员信息
    const adminInfo = ref({

    });
    // 管理员是否登录
    const isLoginIn = ref(false);
    // 管理员token
    const adminToken = ref('');

    // 管理员登录
    const adminLogins = async (loginData) => {
        const result = await adminLogin(loginData);
        console.log(result)
        //response是正常的响应
        if (result && result.data && result.data.token && result.data.user) {
            // 设置token
            setToken(result.data.token)
            localStorage.setItem("adminInfo",JSON.stringify(result.data.user))
            // 设置管理员信息
            adminInfo.value = result.data.user;
            isLoginIn.value = true;
            localStorage.setItem('isLoginIn', true)
            return result.data;
        } else {
            console.log(result)
            return result;
        }
    };
    // 退出登录
    const logout = () => {
        // 清除token
        logouts();
    };
    return {
        adminInfo,
        isLoginIn,
        adminLogins,
        logout,
        refreshToken,
    };
});
