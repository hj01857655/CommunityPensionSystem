import axios, { TokenManager } from '@/utils/axios';
import { ElMessage } from 'element-plus';
// 获取token
export const getTokens = () => {
    return TokenManager.admin.get();
}
// 设置token
export const setToken = (newToken) => {
    TokenManager.admin.set(newToken);
}


//刷新token
export const refreshToken = async () => {
    const response = await axios.get('/api/users/refreshToken');
    console.log(response)
    if(response.status === 200 && response.data){
        setToken(response.data.token);
    }
}

// 管理员登录
export const adminLogin = async (loginData) => {
    try {
        const response = await axios.post('/api/users/adminLogin', loginData);
        console.log(response)
        if(response.status === 200 && response.data){
            const result = response.data;
            localStorage.clear()
            if(result.token){
                console.log("admin.js setToken:",result.token)
                setToken(result.token)
            }
            if(result.user){
                console.log("admin.jssetAdminInfo:",result.user)
                localStorage.setItem('adminInfo', JSON.stringify(result.user));
            }
            return result;
        }else{
            return {status:500,data:{},message:response.data.msg};
        }
    } catch (error) {
        console.log(error)
        return {status:500,data:{},message:error.message};
    }
}

// 退出登录
export const logouts = () => {
    if(getTokens()){
        TokenManager.admin.remove();
        localStorage.removeItem('adminInfo')
    }else{
        localStorage.clear();
    }
}
//重置密码
export const resetPassword = async (userId) => {
    try {
        const token = TokenManager.admin.get();
        const response = await axios.put(`/api/users/resetPassword`, null, {
            headers: {
                Authorization: `${token}`
            },
            params: {
                userId: userId
            }
        });
        ElMessage.success('重置成功');
        return { success: true, data: response.data };
    } catch (error) {
        console.error('Reset password error:', error);
        ElMessage.error('重置失败，请稍后重试');
        return { success: false, error: error.message };
    }
};

//获取所有用户
export const getAllUsers = async () => {
    try {
        // 使用TokenManager获取token
        const token = TokenManager.admin.get();
        if (!token) {
            ElMessage.error('未登录或登录已过期，请重新登录');
            return { success: false, error: '未登录或登录已过期' };
        }

        const response = await axios.get('/api/users', {
            headers: {
                Authorization: `${token}`,
            },
        });
        console.log(response.data)
        return { success: true, data: response.data };
    } catch (error) {
        console.error('Get all users error:', error);
        ElMessage.error('获取用户失败，请稍后重试');
        return { success: false, error: error.message };
    }
};
//获取用户
export const getUser = async (userId) => {
    try {
        const token = TokenManager.admin.get();
        const response = await axios.get(`/api/users/${userId}`, {
            headers: {
                Authorization: `${token}`,
            },
        });
        return { success: true, data: response.data };
    } catch (error) {
        console.error('Get user error:', error);
        ElMessage.error('获取用户失败，请稍后重试');
        return { success: false, error: error.message };
    }
};

//addUser
export const addUser = async (user) => {
    try {
        const token = TokenManager.admin.get();
        const response = await axios.post('/api/users/add', user, {
            headers: {
                Authorization: `${token}`,
            },
        });
        if (response.data.code === 401) {
            ElMessage.error(response.data.msg);
            return { success: false, error: response.data.msg };
        }
        ElMessage.success('添加成功');
        return { success: true, data: response.data };
    } catch (error) {
        console.error('Add user error:', error);
        ElMessage.error('添加失败，请稍后重试');
        return { success: false, error: error.message };
    }
};

//deleteUser
export const deleteUser = async (id) => {
    try {
        const token = TokenManager.admin.get();
        const response = await axios.delete(`/api/users/delete/${id}`, {
            headers: {
                Authorization: `${token}`,
            },
        });
        ElMessage.success('删除成功');
        return { success: true, data: response.data };
    } catch (error) {
        console.error('Delete user error:', error);
        ElMessage.error('删除失败，请稍后重试');
        return { success: false, error: error.message };
    }
};

//updateUser
export const updateUser = async (user) => {
    try {
        const token = TokenManager.admin.get();
        const response = await axios.put(`/api/users/update`,
            user,
            {
                headers: {
                    Authorization: `${token}`,
                },
            }
        );
        ElMessage.success('更新成功');
        return { success: true, data: response.data };
    } catch (error) {
        console.error('Update user error:', error);
        ElMessage.error('更新失败，请稍后重试');
        return { success: false, error: error.message };
    }
};