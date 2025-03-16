import axios from '@/utils/axios';

// 获取角色列表
export const getRoleList = (params) => {
    return axios.get('/api/system/role/list', { 
        params: {
            page: params.page,
            size: params.size,
            query: params.query
        }
    });
};

// 添加角色
export const addRole = (data) => {
    return axios.post('/api/system/role/add', data);
};

// 更新角色
export const updateRole = (data) => {
    return axios.put(`/api/system/role/update/${data.id}`, data);
};

// 删除角色
export const deleteRole = (id) => {
    return axios.delete(`/api/system/role/delete/${id}`);
};

// 获取角色详情
export const getRoleDetail = (id) => {
    return axios.get(`/api/system/role/detail/${id}`);
};

// 获取角色的权限
export const getRolePermissions = (roleId) => {
    return axios.get(`/api/system/role/permissions/${roleId}`);
};

// 分配角色权限
export const assignRolePermissions = (roleId, permissionIds) => {
    return axios.post(`/api/system/role/assign-permissions/${roleId}`, {
        permissionIds
    });
};