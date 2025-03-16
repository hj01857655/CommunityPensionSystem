import axios from '@/utils/axios';

// 获取权限列表
export const getPermissionList = (params) => {
    return axios.get('/api/system/permission/list', { 
        params: {
            page: params.page,
            size: params.size,
            query: params.query
        }
    });
};

// 添加权限
export const addPermission = (data) => {
    return axios.post('/api/system/permission/add', data);
};

// 更新权限
export const updatePermission = (data) => {
    return axios.put(`/api/system/permission/update/${data.id}`, data);
};

// 删除权限
export const deletePermission = (id) => {
    return axios.delete(`/api/system/permission/delete/${id}`);
};

// 获取权限树
export const getPermissionTree = () => {
    return axios.get('/api/system/permission/tree');
};

// 获取权限分类列表
export const getPermissionCategories = () => {
    return axios.get('/api/system/permission/categories');
};