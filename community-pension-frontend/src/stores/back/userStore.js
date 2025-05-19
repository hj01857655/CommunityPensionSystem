import {
    addUser,
    assignRole,
    bindElderKinRelation,
    deleteUser,
    getAuthRole,
    getEldersByKinId,
    getKinsByElderId,
    getUnboundElders,
    getUnboundKins,
    getUserInfo,
    getUserList,
    getUserRoles,
    resetUserPassword,
    unbindElderKinRelation,
    updateAuthRole,
    updateUser,
    updateUserPassword,
    updateUserStatus,
    uploadAvatar
} from '@/api/back/system/user';
import { TokenManager } from '@/utils/axios';
import { ElMessage } from 'element-plus';
import { defineStore } from 'pinia';
import { ref } from 'vue';

/**
 * 后台管理系统用户状态管理
 * 用于管理后台管理系统的用户状态和操作
 * 包括用户管理、角色分配、权限控制等管理员功能
 * @module stores/back/userStore
 */

/**
 * 后台用户 Store
 * @typedef {Object} UserStore
 * @property {Ref<Object|null>} userInfo - 用户信息
 * @property {Ref<Array>} roles - 角色列表
 * @property {Ref<Array>} permissions - 权限列表
 * @property {Ref<Array>} userList - 用户列表
 * @property {Ref<number>} total - 总数
 * @property {Ref<boolean>} loading - 加载状态
 * @property {Ref<boolean>} isLoggedIn - 登录状态
 */
export const useUserStore = defineStore('user', () => {
    // 状态定义
    const userInfo = ref(null);
    const roles = ref([]);
    const permissions = ref([]);
    const userList = ref([]);
    const total = ref(0);
    const loading = ref(false);
    const isLoggedIn = ref(false);

    // 设置用户信息
    const setUserInfo = (user) => {
        if (user) {
            userInfo.value = user;
            isLoggedIn.value = true;
            roles.value = user.roles || [];
            permissions.value = user.permissions || [];

            // 使用 sessionStorage 存储后台用户信息
            sessionStorage.setItem("userInfo", JSON.stringify({
                userId: user.userId,
                username: user.username,
                name: user.name,
                avatar: user.avatar,
                roleId: user.roleId,
                roles: user.roles || [],
                permissions: user.permissions || [],
                phone: user.phone,
                email: user.email,
                gender: user.gender,
                isActive: user.isActive
            }));
            sessionStorage.setItem("isLoggedIn", "true");
            sessionStorage.setItem("roleId", user.roleId);
            sessionStorage.setItem("roles", JSON.stringify(user.roles || []));
            sessionStorage.setItem("permissions", JSON.stringify(user.permissions || []));
        }
    };

    // 登录方法
    const login = async (loginData) => {
        try {
            loading.value = true;
            const response = await loginApi(loginData);

            if (response.status === 200 && response.data.code === 0) {
                const { token, refreshToken, userInfo } = response.data.data;
                saveToken(token, refreshToken);

                // 存储用户信息
                userInfo.value = userInfo;
                sessionStorage.setItem('userInfo', JSON.stringify(userInfo));
                sessionStorage.setItem('userId', userInfo.userId);
                sessionStorage.setItem('username', userInfo.username);
                sessionStorage.setItem('role', userInfo.roles[0]);
                sessionStorage.setItem('roleName', userInfo.roleName || '管理员');

                loading.value = false;
                return true;
            } else {
                loading.value = false;
                return false;
            }
        } catch (error) {
            loading.value = false;
            throw error;
        }
    };

    // 登出方法
    const logout = async () => {
        // 清除状态
        userInfo.value = null;
        roles.value = [];
        permissions.value = [];
        isLoggedIn.value = false;

        // 清除 sessionStorage 存储
        sessionStorage.removeItem('userInfo');
        sessionStorage.removeItem('isLoggedIn');
        sessionStorage.removeItem('roleId');
        sessionStorage.removeItem('roles');
        sessionStorage.removeItem('permissions');

        // 清除token
        TokenManager.admin.clear();

        return true;
    };

    // 获取用户列表
    const fetchUsers = async (queryParams) => {
        try {
            loading.value = true;

            const response = await getUserList(queryParams);
            // console.log('获取用户列表响应:', response);

            if (response?.code === 200) {
                // console.log('用户列表数据:', response.data);
                userList.value = response.data.records || [];
                total.value = response.data.total || 0;
                return true;
            }
            console.error('获取用户列表失败，响应码:', response?.code);
            ElMessage.error(response?.message || '获取用户列表失败');
            return false;
        } catch (error) {
            console.error('获取用户列表失败，错误详情:', error);
            ElMessage.error('获取用户列表失败');
            return false;
        } finally {
            loading.value = false;
        }
    };

    // 获取用户信息
    const handleGetUserInfo = async (userId) => {
        try {
            

            const res = await getUserInfo(userId);
            if (res.code === 200) {
                return res;
            }
            userInfo.value = res;

            return { code: 200, data: userInfo.value };

        } catch (error) {
            console.error('获取用户信息失败:', error);
            throw error;
        }
    };

    /**
     * 修改密码 
     * @param {*} userId
     * @param {*} data
     * @returns {boolean}
     */
    const handleUpdatePwd = async (userId, data) => {
        try {
            const res = await updateUserPassword(userId, data);
            if (res.code === 200) {
                ElMessage.success('密码修改成功');
                return res;
            }
            ElMessage.error(res.msg || '密码修改失败');
            return null;
        } catch (error) {
            console.error('修改密码失败:', error);
            throw error;
        }
    };

    /**
     * 上传头像
     * @param {*} file
     * @returns
     */
    const handleUploadAvatar = async (file) => {
        try {
            const res = await uploadAvatar(file);
            if (res.code === 200) {
                userInfo.value.avatar = res.data;
                ElMessage.success('头像上传成功');
                return res;
            }
            ElMessage.error(res.msg || '头像上传失败');
            return null;
        } catch (error) {
            console.error('上传头像失败:', error);
            throw error;
        }
    };

    // 重置状态
    const resetState = () => {
        userInfo.value = null;
        roles.value = [];
        permissions.value = [];
        userList.value = [];
        total.value = 0;
    };

    /**
     * 新增用户
     * @param {*} userData
     * @returns {Promise<{code: number, data: number, msg: string}>} 成功时返回的data包含新增用户的ID
     */
    const handleAddUser = async (userData) => {
        try {
            // 处理roleId，转换为后端期望的格式
            const dataToSubmit = { ...userData };
            
            // 如果存在roleId但不存在roleIds，创建roleIds数组
            if (dataToSubmit.roleId !== undefined && !dataToSubmit.roleIds) {
                dataToSubmit.roleIds = [dataToSubmit.roleId];
            }
            
            // 日志输出提交的数据，帮助调试
            console.log('提交到后端的用户数据:', JSON.stringify(dataToSubmit, null, 2));
            
            const res = await addUser(dataToSubmit);
            if (res.code === 200) {
                ElMessage.success('新增用户成功');
                return res; // 后端应返回新增用户的ID
            }
            ElMessage.error(res.msg || '新增用户失败');
            return null;
        } catch (error) {
            console.error('新增用户失败:', error);
            throw error;
        }
    };

    /**
     * 更新用户
     * @param {*} userData
     * @returns
     */
    const handleUpdateUser = async (userData) => {
        try {
            // 验证userId是否存在
            if (!userData.userId) {
                ElMessage.error('用户ID不能为空');
                return false;
            }

            // 创建一个新对象用于提交
            const dataToSubmit = { ...userData };
            
            // 处理roleId，转换为后端期望的格式
            // 如果存在roleId但不存在roleIds，创建roleIds数组
            if (dataToSubmit.roleId !== undefined && !dataToSubmit.roleIds) {
                dataToSubmit.roleIds = [dataToSubmit.roleId];
            }
            // 确保roleIds不为null
            else if (!dataToSubmit.roleIds) {
                dataToSubmit.roleIds = [];
            }

            // 确保isActive不为undefined
            if (dataToSubmit.isActive === undefined) {
                dataToSubmit.isActive = 1;
            }

            const response = await updateUser(dataToSubmit.userId, dataToSubmit);
            if (response.code === 200) {
                ElMessage.success('更新用户成功');
                return true;
            }
            ElMessage.error(response.msg || '更新用户失败');
            return false;
        } catch (error) {
            console.error('更新用户失败:', error);
            ElMessage.error('更新用户失败');
            return false;
        }
    };

    /**
     * 删除用户
     * @param {*} userId
     * @returns
     */
    const handleDeleteUser = async (userId) => {
        try {
            const res = await deleteUser(userId);
            if (res.code === 200) {
                ElMessage.success('删除用户成功');
                return res;
            }
            ElMessage.error(res.msg || '删除用户失败');
            return null;
        } catch (error) {
            console.error('删除用户失败:', error);
            throw error;
        }
    };

    /**
     * 重置密码
     * @param {*} userId
     * @returns
     */
    const handleResetPassword = async (userId) => {
        try {
            const res = await resetUserPassword(userId);
            if (res.code === 200) {
                ElMessage.success('密码重置成功');
                return res;
            }
            ElMessage.error(res.msg || '密码重置失败');
            return null;
        } catch (error) {
            console.error('密码重置失败:', error);
            throw error;
        }
    };

    /**
     * 获取用户角色
     * @param {number} userId - 用户ID
     * @returns {Promise<Array<number>>}
     */
    const handleGetUserRoles = async (userId) => {
        try {
            const res = await getUserRoles(userId);
            if (res.code === 200) {
                return res;
            }
            ElMessage.error(res.msg || '获取用户角色失败');
            return null;
        } catch (error) {
            console.error('获取用户角色失败:', error);
            throw error;
        }
    };

    /**
     * 分配角色
     * @param {number} userId - 用户ID
     * @param {number[]} roleIds - 角色ID数组
     * @returns {Promise<boolean>}
     */
    const handleAssignRole = async (userId, roleIds) => {
        try {
            const res = await assignRole(userId, roleIds);
            if (res.code === 200) {
                ElMessage.success('角色分配成功');
                return true;
            }
            ElMessage.error(res.msg || '角色分配失败');
            return false;
        } catch (error) {
            console.error('角色分配失败:', error);
            throw error;
        }
    };

    /**
     * 更新用户状态
     * @param {*} userId
     * @param {*} status
     * @returns
     */
    const handleUpdateStatus = async (userId, status) => {
        try {
            const res = await updateUserStatus(userId, status);
            if (res.code === 200) {
                ElMessage.success('状态更新成功');
                return res;
            }
            ElMessage.error(res.msg || '状态更新失败');
            return null;
        } catch (error) {
            console.error('状态更新失败:', error);
            throw error;
        }
    };

    /**
     * 获取未绑定家属的老人列表
     * @returns {Promise<{code: number, data: Array<{userId: number, name: string, idCard: string}>, msg: string}>}
     */
    const fetchElderList = async () => {
        try {
            const res = await getUnboundElders();
            if (res.code === 200) {
                return res;
            }
            ElMessage.error(res.msg || '获取未绑定家属的老人列表失败');
            return null;
        } catch (error) {
            console.error('获取未绑定家属的老人列表失败:', error);
            throw error;
        }
    };

    /**
     * 获取用户角色授权信息
     * @param {number} userId - 用户ID
     * @returns {Promise<{code: number, data: {user: Object, roles: Array}, msg: string}>}
     */
    const handleGetAuthRole = async (userId) => {
        try {
            const res = await getAuthRole(userId);
            if (res.code === 200) {
                return res;
            }
            ElMessage.error(res.msg || '获取用户角色授权信息失败');
            return null;
        } catch (error) {
            console.error('获取用户角色授权信息失败:', error);
            throw error;
        }
    };

    /**
     * 更新用户角色授权信息
     * @param {Object} data - 授权信息
     * @param {number} data.userId - 用户ID
     * @param {string} data.roleIds - 角色ID字符串，多个ID用逗号分隔
     * @returns {Promise<{code: number, msg: string}>}
     */
    const handleUpdateAuthRole = async (data) => {
        try {
            const res = await updateAuthRole(data);
            if (res.code === 200) {
                ElMessage.success('角色授权更新成功');
                return res;
            }
            ElMessage.error(res.msg || '角色授权更新失败');
            return null;
        } catch (error) {
            console.error('角色授权更新失败:', error);
            throw error;
        }
    };

    /**
     * 获取老人名称
     * @param {number} elderId - 老人ID
     * @returns {Promise<{code: number, data: string, message?: string}>}
     */
    const fetchElderName = async (elderId) => {
        try {
            const res = await getUserInfo(elderId);
            if (res.code === 200) {
                return { code: 200, data: res.data.name };
            }
            return { code: res.code, message: res.msg };
        } catch (error) {
            console.error('获取老人名称失败:', error);
            return { code: 500, message: '获取老人名称失败' };
        }
    };

    /**
     * 获取未绑定老人的家属列表
     * @returns {Promise<{code: number, data: Array<{userId: number, name: string}>, msg: string}>}
     */
    const fetchUnboundKins = async () => {
        try {
            const res = await getUnboundKins();
            if (res.code === 200) {
                return res;
            }
            ElMessage.error(res.msg || '获取未绑定老人的家属列表失败');
            return null;
        } catch (error) {
            console.error('获取未绑定老人的家属列表失败:', error);
            throw error;
        }
    };

    // 绑定老人和家属关系
    const handleBindElderKinRelation = async (elderId, kinId, relationType) => {
        try {
            const res = await bindElderKinRelation(elderId, kinId, relationType);
            if (res.code === 200) {
                ElMessage.success('绑定关系成功');
                return res;
            }
            ElMessage.error(res.msg || '绑定关系失败');
            return null;
        } catch (error) {
            console.error('绑定关系失败:', error);
            throw error;
        }
    };

    // 解绑老人和家属关系
    const handleUnbindElderKinRelation = async (elderId, kinId) => {
        try {
            const res = await unbindElderKinRelation(elderId, kinId);
            if (res.code === 200) {
                ElMessage.success('解绑关系成功');
                return res;
            }
            ElMessage.error(res.msg || '解绑关系失败');
            return null;
        } catch (error) {
            console.error('解绑关系失败:', error);
            throw error;
        }
    };

    /**
     * 获取家属绑定的所有老人及关系类型
     * @param {number} kinId 家属ID
     * @returns {Promise<Array>} 老人列表
     */
    const fetchEldersByKinId = async (kinId) => {
        try {
            const res = await getEldersByKinId(kinId);
            if (res.code === 200) {
                return res.data || [];
            }
            console.error('获取家属绑定的老人列表失败:', res.msg);
            return [];
        } catch (error) {
            console.error('获取家属绑定的老人列表失败:', error);
            return [];
        }
    };

    // 获取未绑定家属的老人列表
    const fetchUnboundElders = async () => {
        try {
            const res = await getUnboundElders();
            if (res.code === 200) {
                return res;
            }
            ElMessage.error(res.msg || '获取未绑定家属的老人列表失败');
            return null;
        } catch (error) {
            console.error('获取未绑定家属的老人列表失败:', error);
            throw error;
        }
    };

    /**
     * 获取老人绑定的所有家属及关系类型
     * @param {number} elderId 老人ID
     * @returns {Promise<Array>} 家属列表
     */
    const fetchKinsByElderId = async (elderId) => {
        try {
            const res = await getKinsByElderId(elderId);
            if (res.code === 200) {
                return res.data || [];
            }
            console.error('获取老人绑定的家属列表失败:', res.msg);
            return [];
        } catch (error) {
            console.error('获取老人绑定的家属列表失败:', error);
            return [];
        }
    };

    return {
        userInfo,
        roles,
        permissions,
        userList,
        total,
        loading,
        isLoggedIn,
        fetchUsers,
        handleGetUserInfo,
        handleUpdatePwd,
        handleUploadAvatar,
        resetState,
        handleAddUser,
        handleUpdateUser,
        handleDeleteUser,
        handleResetPassword,
        handleGetUserRoles,
        handleAssignRole,
        handleUpdateStatus,
        fetchElderList,
        handleGetAuthRole,
        handleUpdateAuthRole,
        fetchElderName,
        fetchUnboundElders,
        fetchUnboundKins,
        handleBindElderKinRelation,
        handleUnbindElderKinRelation,
        fetchEldersByKinId,
        fetchKinsByElderId,
        login,
    };
});


// 默认导出
export default useUserStore;
