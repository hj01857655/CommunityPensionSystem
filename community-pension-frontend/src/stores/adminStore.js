import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import { login, getAdminInfo } from '@/api/admin';
import { ElMessage } from 'element-plus';


// 管理员状态管理
export const useAdminStore = defineStore('admin', () => {
    // 管理员信息
    const adminInfo = ref({
        username: '',
        
    });
    // 管理员token
    const token = ref('');
    
    // 设置token
    const setTokens = (newToken) => {
        token.value = newToken;
        localStorage.setItem('admin-token', newToken);
    };
    
    // 获取token
    const getTokens = () => {
        if (!token.value) {
            token.value = localStorage.getItem('admin-token') || '';
        }
        return token.value;
    };
    const removeTokens = () => {
        localStorage.removeItem('admin-token');
        token.value = '';
    };
    
    // 管理员登录
    const adminLogin = async (loginData) => {
        const response = await login(loginData);
        //response是正常的响应
        if(response.code === 200){
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
        token,
        setTokens,
        getTokens,
        adminLogin,
        getAdminInfos,
        logout,
    };
});
