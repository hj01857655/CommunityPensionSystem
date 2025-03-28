import { defineStore } from 'pinia';
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import {
    getRoleList,
    getAllRoles,
    getRole,
    addRole,
    updateRole,
    deleteRole,
    getUserRole,
    changeRoleStatus,
    getRoleMenus,
    assignRoleMenu,
    getRoleMenuTree
} from '@/api/back/system/role';

export const useRoleStore = defineStore('role', () => {
    // 状态定义
    const roleList = ref([]);      // 角色列表
    const total = ref(0);          // 总数
    const loading = ref(false);    // 加载状态
    const currentRole = ref(null); // 当前选中的角色
    const roleMenus = ref([]);     // 角色菜单权限
    const menuTree = ref([]);      // 菜单树

    // 查询参数
    const queryParams = ref({
        pageNum: 1,
        pageSize: 10,
        roleName: '',
        roleKey: '',
        roleSort: undefined,
        status: ''
    });

    // 获取角色列表（分页）
    const fetchRoleList = async (params = {}) => {
        try {
            loading.value = true;
            const res = await getRoleList({ ...queryParams.value, ...params });
            if (res.code === 200) {
                roleList.value = res.data.records;
                total.value = res.data.total;
            } else {
                ElMessage.error(res.msg || '获取角色列表失败');
            }
            return res;
        } catch (error) {
            console.error('获取角色列表失败:', error);
            throw error;
        } finally {
            loading.value = false;
        }
    };

    // 获取所有角色
    const fetchAllRoles = async () => {
        try {
            const res = await getAllRoles();
            if (res.code === 200) {
                return res.data;
            }
            ElMessage.error(res.msg || '获取所有角色失败');
            return [];
        } catch (error) {
            console.error('获取所有角色失败:', error);
            ElMessage.error('获取所有角色失败');
            return [];
        }
    };

    // 获取角色详情
    const fetchRoleInfo = async (roleId) => {
        try {
            const res = await getRole(roleId);
            if (res.code === 200) {
                currentRole.value = res.data;
                return res;
            }
            ElMessage.error(res.msg || '获取角色详情失败');
            return null;
        } catch (error) {
            console.error('获取角色详情失败:', error);
            throw error;
        }
    };

    // 新增角色
    const createRole = async (role) => {
        try {
            const res = await addRole(role);
            if (res.code === 200) {
                ElMessage.success('新增角色成功');
                return res;
            }
            ElMessage.error(res.msg || '新增角色失败');
            return null;
        } catch (error) {
            console.error('新增角色失败:', error);
            throw error;
        }
    };

    // 修改角色
    const updateRoleInfo = async (role) => {
        try {
            const res = await updateRole(role);
            if (res.code === 200) {
                ElMessage.success('修改角色成功');
                return res;
            }
            ElMessage.error(res.msg || '修改角色失败');
            return null;
        } catch (error) {
            console.error('修改角色失败:', error);
            throw error;
        }
    };

    // 删除角色
    const removeRole = async (roleId) => {
        try {
            const res = await deleteRole(roleId);
            if (res.code === 200) {
                ElMessage.success('删除角色成功');
                return res;
            }
            ElMessage.error(res.msg || '删除角色失败');
            return null;
        } catch (error) {
            console.error('删除角色失败:', error);
            throw error;
        }
    };

    // 获取用户角色
    const fetchUserRole = async (userId) => {
        try {
            const res = await getUserRole(userId);
            if (res.code === 200) {
                return res.data;
            }
            ElMessage.error(res.msg || '获取用户角色失败');
            return null;
        } catch (error) {
            console.error('获取用户角色失败:', error);
            ElMessage.error('获取用户角色失败');
            return null;
        }
    };

    // 修改角色状态
    const updateRoleStatus = async (data) => {
        try {
            const res = await changeRoleStatus(data);
            if (res.code === 200) {
                ElMessage.success('修改角色状态成功');
                return res;
            }
            ElMessage.error(res.msg || '修改角色状态失败');
            return null;
        } catch (error) {
            console.error('修改角色状态失败:', error);
            throw error;
        }
    };

    // 获取角色菜单权限
    const fetchRoleMenus = async (roleId) => {
        try {
            const res = await getRoleMenus(roleId);
            if (res.code === 200) {
                roleMenus.value = res.data;
                return res;
            }
            ElMessage.error(res.msg || '获取角色菜单权限失败');
            return null;
        } catch (error) {
            console.error('获取角色菜单权限失败:', error);
            throw error;
        }
    };

    /**
     * 获取角色菜单树
     * @param {number} roleId - 角色ID
     */
    const fetchRoleMenuTree = async (roleId) => {
        try {
            const response = await getRoleMenuTree(roleId);
            if (response.code === 200) {
                menuTree.value = response.data;
                return response.data;
            }
            throw new Error(response.msg || '获取角色菜单树失败');
        } catch (error) {
            console.error('获取角色菜单树失败:', error);
            ElMessage.error('获取角色菜单树失败');
            throw error;
        }
    };

    // 分配角色菜单权限
    const assignMenus = async (data) => {
        try {
            const res = await assignRoleMenu(data);
            if (res.code === 200) {
                ElMessage.success('分配权限成功');
                return res;
            }
            ElMessage.error(res.msg || '分配权限失败');
            return null;
        } catch (error) {
            console.error('分配权限失败:', error);
            throw error;
        }
    };

    // 重置查询参数
    const resetQueryParams = () => {
        queryParams.value = {
            pageNum: 1,
            pageSize: 10,
            roleName: '',
            roleKey: '',
            roleSort: undefined,
            status: ''
        };
    };

    return {
        // 状态
        roleList,
        total,
        loading,
        currentRole,
        roleMenus,
        menuTree,
        queryParams,
        
        // 方法
        fetchRoleList,
        fetchAllRoles,
        fetchRoleInfo,
        createRole,
        updateRoleInfo,
        removeRole,
        fetchUserRole,
        updateRoleStatus,
        fetchRoleMenus,
        fetchRoleMenuTree,
        assignMenus,
        resetQueryParams
    };
}); 