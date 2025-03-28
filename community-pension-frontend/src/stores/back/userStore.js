import { defineStore } from 'pinia';
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import {
    getUserInfo,
    getUserList,
    addUser,
    updateUser,
    updateUserPassword,
    resetUserPassword,
    uploadAvatar,
    deleteUser,
    getUserRoles,
    assignRole,
    updateUserStatus,
    getUnboundElders,
    getAuthRole,
    updateAuthRole,
    getUnboundKins,
    bindElderKinRelation,
    unbindElderKinRelation,
    getKinIdsByElderId,
    getElderIdsByKinId,
} from '@/api/back/system/user';
import axios from 'axios';

export const useUserStore = defineStore('user', () => {
    // 状态定义
    const userInfo = ref(null);
    const roles = ref([]);
    const permissions = ref([]);
    const userList = ref([]);
    const total = ref(0);
    const loading = ref(false);

    // 获取用户信息
    const handleGetUserInfo = async (userId) => {
        try {
            const res = await getUserInfo(userId);
            if (res.code === 200) {
                userInfo.value = res.data;
                roles.value = res.data.roles || [];
                permissions.value = res.data.permissions || [];
                return res;
            }
            ElMessage.error(res.msg || '获取用户信息失败');
            return null;
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
     * 获取用户列表
     * @param {*} params
     * @returns
     */
    const fetchUsers = async (params) => {
        try {
            loading.value = true;
            const res = await getUserList({
                current: params.page,
                size: params.limit,
                ...params.query
            });
            if (res.code === 200) {
                userList.value = res.data.records;
                total.value = res.data.total;
                return res;
            }
            ElMessage.error(res.msg || '获取用户列表失败');
            return null;
        } catch (error) {
            console.error('获取用户列表失败:', error);
            throw error;
        } finally {
            loading.value = false;
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
     * @returns
     */
    const handleAddUser = async (userData) => {
        try {
            const res = await addUser(userData);
            if (res.code === 200) {
                ElMessage.success('新增用户成功');
                return res;
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

            // 确保roleIds不为null
            if (!userData.roleIds) {
                userData.roleIds = [];
            }

            // 确保isActive不为undefined
            if (userData.isActive === undefined) {
                userData.isActive = 1;
            }
            
            const response = await updateUser(userData.userId, userData);
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

    // 获取老人名称
    const fetchElderName = async (elderId) => {
        try {
            const response = await axios.get(`/api/system/user/${elderId}`);
            if (response.data.code === 200) {
                return {code: 200, data: response.data.data.name};
            }
            return {code: response.data.code, message: response.data.message};
        } catch (error) {
            console.error('获取老人名称失败:', error);
            return {code: 500, message: '获取老人名称失败'};
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

    // 获取老人的所有家属ID
    const handleGetKinIdsByElderId = async (elderId) => {
        try {
            const res = await getKinIdsByElderId(elderId);
            if (res.code === 200) {
                return res;
            }
            ElMessage.error(res.msg || '获取老人的所有家属ID失败');
            return null;
        } catch (error) {
            console.error('获取老人的所有家属ID失败:', error);
            throw error;
        }
    };

    // 获取家属的所有老人ID
    const handleGetElderIdsByKinId = async (kinId) => {
        try {
            const res = await getElderIdsByKinId(kinId);
            if (res.code === 200) {
                return res;
            }
            ElMessage.error(res.msg || '获取家属的所有老人ID失败');
            return null;
        } catch (error) {
            console.error('获取家属的所有老人ID失败:', error);
            throw error;
        }
    };

    return {
        userInfo,
        roles,
        permissions,
        userList,
        total,
        loading,
        handleGetUserInfo,
        handleUpdatePwd,
        fetchUsers,
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
        fetchUnboundKins,
        handleBindElderKinRelation,
        handleUnbindElderKinRelation,
        handleGetKinIdsByElderId,
        handleGetElderIdsByKinId,
    };
});