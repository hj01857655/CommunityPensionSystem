import {defineStore} from 'pinia';
import {ref} from 'vue';
import {ElMessage} from 'element-plus';
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
    uploadAvatar,
} from '@/api/back/system/user';
import axios from 'axios';
import {TokenManager} from '@/utils/axios';

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
    const login = async (loginForm) => {
        try {
            const response = await userLogin(loginForm);

            if (response.code === 200) {
                console.log("后台登录成功响应", response);
                setUserInfo(response.data.user);

                // 保存token
                if (response.data.accessToken) {
                    TokenManager.admin.set(response.data.accessToken, response.data.refreshToken);
                } else {
                    console.error('登录响应中没有token');
                    ElMessage.error('登录失败：未获取到token');
                    return false;
                }

                return true;
            } else {
                console.log("后台登录失败响应", response);
                ElMessage.error('登录失败');
                return false;
            }
        } catch (error) {
            console.error('登录错误:', error.message);
            ElMessage.error(error.message || '登录失败');
            return false;
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
            console.log('开始获取用户列表，参数:', queryParams);
            console.log('当前路径:', window.location.pathname);
            console.log('管理员token:', TokenManager.admin.getAccessToken());

            const response = await getUserList(queryParams);
            console.log('获取用户列表响应:', response);

            if (response?.code === 200) {
                console.log('用户列表数据:', response.data);
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
            // 如果已经有用户信息，直接返回
            if (userInfo.value) {
                return {code: 200, data: userInfo.value};
            }

            // 从 sessionStorage 获取用户信息
            const userInfoStr = sessionStorage.getItem('userInfo');
            if (!userInfoStr) {
                const res = await getUserInfo(userId);
                if (res.code === 200) {
                    setUserInfo(res.data);
                    return res;
                }
                return {code: 401, message: '用户未登录'};
            }

            try {
                const parsedUserInfo = JSON.parse(userInfoStr);
                userInfo.value = parsedUserInfo;
                isLoggedIn.value = sessionStorage.getItem('isLoggedIn') === 'true';
                roles.value = JSON.parse(sessionStorage.getItem('roles') || '[]');
                permissions.value = JSON.parse(sessionStorage.getItem('permissions') || '[]');

                return {code: 200, data: userInfo.value};
            } catch (error) {
                console.error('解析用户信息失败:', error);
                return {code: 500, message: '获取用户信息失败'};
            }
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

    /**
     * 获取老人的家属列表
     * @param {number} elderId
     * @returns {Promise}
     */
    const handleGetKinsByElderId = async (elderId) => {
        try {
            const res = await getKinsByElderId(elderId);
            if (res.code === 200) {
                return res;
            }
            ElMessage.error(res.msg || '获取老人的家属列表失败');
            return null;
        } catch (error) {
            console.error('获取老人的家属列表失败:', error);
            throw error;
        }
    };

    /**
     * 获取家属的老人列表
     * @param {number} kinId
     * @returns {Promise}
     */
    const handleGetEldersByKinId = async (kinId) => {
        try {
            const res = await getEldersByKinId(kinId);
            if (res.code === 200) {
                return res;
            }
            ElMessage.error(res.msg || '获取家属的老人列表失败');
            return null;
        } catch (error) {
            console.error('获取家属的老人列表失败:', error);
            throw error;
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

    return {
        userInfo,
        roles,
        permissions,
        userList,
        total,
        loading,
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
        handleGetKinsByElderId,
        handleGetEldersByKinId,
    };
});


// 默认导出
export default useUserStore;
