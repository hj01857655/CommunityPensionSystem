import axios from 'axios'

const axiosInstance = axios.create({
    baseURL: 'http://127.0.0.1:8080',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json'
    }
});
// 管理token
const TokenManager = {
    user:{
        get:()=>{
            return localStorage.getItem('user');
        },
        set:(user)=>{
            localStorage.setItem('user', user);
        },
        remove:()=>{
            localStorage.removeItem('user');
        }
    },
    admin:{
        get:()=>{
            return localStorage.getItem('admin-token');
        },
        set:(token)=>{
            localStorage.setItem('admin-token', token);
        },
        remove:()=>{
            localStorage.removeItem('admin-token');
            localStorage.removeItem('adminInfo')
        }
    }
}
const refreshToken = async ()=>{
    const response = await axiosInstance.post('/refreshToken', {
        headers:{
            Authorization: TokenManager.user.get()
        }
    })

    return response.data
    
}
// 添加请求拦截器
axiosInstance.interceptors.request.use(config => {
    config.headers['Authorization'] = `${TokenManager.user.get()}`;
    return config;
}, error => {
    return Promise.reject(error);
});
// 添加响应拦截器
axiosInstance.interceptors.response.use(response => {
    return response;
}, error => {
    return Promise.reject(error);
});
export {TokenManager}
export default axiosInstance;
