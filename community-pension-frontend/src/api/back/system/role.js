import axios from '@/utils/axios';

/**
 * 角色管理相关接口
 */

/**
 * 超级管理员角色ID常量
 */
export const ADMIN_ROLE_ID = 4;

/**
 * 获取角色列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {string} [params.roleName] - 角色名称
 * @param {string} [params.roleKey] - 权限字符
 * @param {number} [params.roleSort] - 显示顺序
 * @param {string} [params.status] - 状态（0正常 1停用）
 * @returns {Promise<{code: number, data: {records: Array, total: number}, msg: string}>}
 */
export const getRoleList = (params) => {
    return axios.get('/api/system/role/list', { params })
}

/**
 * 获取所有角色（不分页）
 * @returns {Promise<{code: number, data: Array, msg: string}>}
 */
export const getAllRoles = () => {
    return axios.get('/api/system/role/all')
}

/**
 * 获取角色详情
 * @param {number} roleId - 角色ID
 * @returns {Promise<{code: number, data: Object, msg: string}>}
 */
export const getRole = (roleId) => {
    return axios.get(`/api/system/role/${roleId}`)
}

/**
 * 新增角色
 * @param {Object} data - 角色信息
 * @param {string} data.roleName - 角色名称
 * @param {string} data.roleKey - 角色标识
 * @param {number} data.roleSort - 显示顺序
 * @param {string} data.status - 状态（0正常 1停用）
 * @param {string} data.dataScope - 数据范围
 * @param {Array<number>} data.menuIds - 菜单ID列表
 * @param {string} [data.remark] - 备注
 * @returns {Promise<{code: number, msg: string}>}
 */
export const addRole = (data) => {
    return axios.post('/api/system/role', data)
}

/**
 * 修改角色
 * @param {Object} data - 角色信息
 * @param {number} data.roleId - 角色ID
 * @param {string} data.roleName - 角色名称
 * @param {string} data.roleKey - 角色标识
 * @param {number} data.roleSort - 显示顺序
 * @param {string} data.status - 状态（0正常 1停用）
 * @param {string} data.dataScope - 数据范围
 * @param {Array<number>} data.menuIds - 菜单ID列表
 * @param {string} [data.remark] - 备注
 * @returns {Promise<{code: number, msg: string}>}
 */
export const updateRole = (data) => {
    return axios.put('/api/system/role', data)
}

/**
 * 删除角色
 * @param {number} roleId - 角色ID
 * @returns {Promise<{code: number, msg: string}>}
 */
export const deleteRole = (roleId) => {
    return axios.delete(`/api/system/role/${roleId}`)
}

/**
 * 获取用户的角色
 * @param {number} userId - 用户ID
 * @returns {Promise<{code: number, data: Object, msg: string}>}
 */
export const getUserRole = (userId) => {
    return axios.get(`/api/system/role/user/${userId}`)
}

/**
 * 获取角色的权限（待实现）
 * @param {number} roleId 角色ID
 */
export function getRolePermissions(roleId) {
    console.warn('接口未实现：获取角色权限');
    return Promise.reject('接口未实现');
}

/**
 * 分配角色权限（待实现）
 * @param {number} roleId 角色ID
 * @param {Array<string>} permissionIds 权限ID列表
 */
export function assignPermissions(roleId, permissionIds) {
    console.warn('接口未实现：分配角色权限');
    return Promise.reject('接口未实现');
}

/**
 * 获取角色树形结构（待实现）
 */
export function getRoleTree() {
    console.warn('接口未实现：获取角色树');
    return Promise.reject('接口未实现');
}

/**
 * 获取角色的菜单权限
 * @param {number} roleId - 角色ID
 * @returns {Promise<{code: number, data: Array<number>, msg: string}>}
 */
export const getRoleMenus = (roleId) => {
    return axios.get(`/api/system/role/roleMenu/${roleId}`);
};

/**
 * 分配角色菜单权限
 * @param {Object} data - 角色菜单数据
 * @param {number} data.roleId - 角色ID
 * @param {Array<number>} data.menuIds - 菜单ID列表
 * @returns {Promise<{code: number, msg: string}>}
 */
export const assignRoleMenu = (data) => {
    return axios.put('/api/system/role/roleMenu', data);
};

/**
 * 获取角色菜单树
 * @param {number} roleId - 角色ID
 * @returns {Promise<{code: number, data: {menus: Array, checkedKeys: Array}, msg: string}>}
 */
export const getRoleMenuTree = (roleId) => {
    return axios.get(`/api/system/role/roleMenuTree/${roleId}`);
};

/**
 * 修改角色状态
 * @param {Object} data - 角色状态数据
 * @param {number} data.roleId - 角色ID
 * @param {string} data.status - 状态（0正常 1停用）
 * @returns {Promise<{code: number, msg: string}>}
 */
export const changeRoleStatus = (data) => {
    return axios.put('/api/system/role/changeStatus', data);
};





