import axios from '@/utils/axios';
import { ElMessage } from 'element-plus';
//登录
export const login = async (loginData) => {
    try {
        const response = await axios.post('/api/user/login', loginData);
        console.log(response);
        const { code, message,data } = response.data;
        const { user,token } = data;
        //将token设置到请求头
        localStorage.setItem('token', token);
        if (code === 200) {
            ElMessage.success('登录成功');
            //删除password后存储到本地存储
            const { password, ...userInfo } = user;
            localStorage.setItem('userInfo', JSON.stringify(userInfo));
            return { success: true, roleId: userInfo.roleId };
        } else {
            ElMessage.error(message);
            return { success: false };
        }
    } catch (error) {
        console.error('Login error:', error);
        ElMessage.error('登录失败');
        return { success: false, error: error.message };
    }
};
//获取用户信息
export const getUserInfo=async(roleId)=>{
    const response=await axios.get('/api/user/elders');
    const { code,message,data }=response.data;
    console.log(code);
    if(code==200){
        return { success: true, data };
    }else{
        return { success: false, message };
    }
}

//更新用户信息
export const updateUserInfo=async(userInfo)=>{
    const response=await axios.post('/api/user/update',userInfo);
    return response.data;
}


