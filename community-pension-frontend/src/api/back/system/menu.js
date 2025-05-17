import axios from '@/utils/axios';

// 查询菜单列表
export const listMenu = (query) => {
    return axios.get('/api/system/menu/list', { params: query });
};

// 查询菜单详细
export const getMenu = (menuId) => {
    return axios.get('/api/system/menu/' + menuId);
};

// 新增菜单
export const addMenu = (data) => {
    return axios.post('/api/system/menu', data);
};

// 修改菜单
export const updateMenu = (data) => {
    return axios.put('/api/system/menu', data);
};

// 删除菜单
export const delMenu = (menuId) => {
    return axios.delete('/api/system/menu/' + menuId);
};

// 查询菜单树结构
export const treeselect = () => {
    return axios.get('/api/system/menu/treeselect');
};

// 根据角色ID查询菜单树结构
export const roleMenuTreeselect = (roleId) => {
    return axios.get('/api/system/menu/roleMenuTreeselect/' + roleId);
};