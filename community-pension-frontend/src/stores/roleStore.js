import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { getRoleList, createRole, updateRole, deleteRole, getRoleDetail } from '@/api/role';
import { ElMessage } from 'element-plus';

export const useRoleStore = defineStore('role', () => {
  // 状态
  const roles = ref([]);
  const loading = ref(false);
  const totalRoles = ref(0);
  const currentPage = ref(1);
  const pageSize = ref(10);
  const searchQuery = ref('');
  
  // 获取角色列表
  const fetchRoles = async () => {
    loading.value = true;
    try {
      const params = {
        page: currentPage.value,
        size: pageSize.value,
        roleName: searchQuery.value || null
      };

      const response = await getRoleList(params);
      if (!response || !response.data) {
        console.error('获取角色列表失败，请检查网络连接或稍后重试。');
        roles.value = [];
        return;
      }

      // 处理返回的数据
      if (response.data && Array.isArray(response.data.records)) {
        roles.value = response.data.records.map(item => ({
          id: item.id,
          role_name: item.roleName,
          role_description: item.roleDescription,
          status: item.status,
          create_time: item.createTime,
          update_time: item.updateTime,
          permissions: item.permissions || []
        }));
        totalRoles.value = response.data.total || 0;
      } else {
        roles.value = [];
        totalRoles.value = 0;
        console.warn('返回的角色数据格式不符合预期', response.data);
      }
    } catch (error) {
      console.error('获取角色列表失败:', error);
      console.error('获取角色列表失败，请稍后重试。');
      roles.value = [];
      totalRoles.value = 0;
    } finally {
      loading.value = false;
    }
  };

  // 获取角色详情
  const fetchRoleDetail = async (id) => {
    loading.value = true;
    try {
      const response = await getRoleDetail(id);
      if (response.status && response.status !== 200) {
        console.error(response.message || '获取角色详情失败');
        return null;
      }
      return response.data;
    } catch (error) {
      console.error('获取角色详情失败:', error);
      console.error('获取角色详情失败，请稍后重试');
      return null;
    } finally {
      loading.value = false;
    }
  };

  // 添加角色
  const addRole = async (roleData) => {
    try {
      const response = await createRole(roleData);
      if (response.status && response.status !== 200) {
        console.error(response.message || '添加角色失败');
        return false;
      }
      ElMessage.success('添加角色成功');
      await fetchRoles(); // 刷新列表
      return true;
    } catch (error) {
      console.error('添加角色失败:', error);
      console.error('添加角色失败，请稍后重试');
      return false;
    }
  };

  // 更新角色
  const editRole = async (id, roleData) => {
    try {
      const response = await updateRole(id, roleData);
      if (response.status && response.status !== 200) {
        console.error(response.message || '更新角色失败');
        return false;
      }
      ElMessage.success('更新角色成功');
      await fetchRoles(); // 刷新列表
      return true;
    } catch (error) {
      console.error('更新角色失败:', error);
      console.error('更新角色失败，请稍后重试');
      return false;
    }
  };

  // 删除角色
  const removeRole = async (id) => {
    try {
      const response = await deleteRole(id);
      if (response.status && response.status !== 200) {
        console.error(response.message || '删除角色失败');
        return false;
      }
      ElMessage.success('删除角色成功');
      await fetchRoles(); // 刷新列表
      return true;
    } catch (error) {
      console.error('删除角色失败:', error);
      console.error('删除角色失败，请稍后重试');
      return false;
    }
  };

  // 计算属性
  const paginatedRoles = computed(() => roles.value);

  // 重置分页和搜索条件
  const resetFilters = () => {
    currentPage.value = 1;
    searchQuery.value = '';
  };

  // 设置分页信息
  const setPagination = (page, size) => {
    currentPage.value = page;
    pageSize.value = size;
  };

  // 设置搜索条件
  const setSearchQuery = (query) => {
    searchQuery.value = query;
    currentPage.value = 1; // 重置到第一页
  };

  return {
    // 状态
    roles,
    loading,
    totalRoles,
    currentPage,
    pageSize,
    searchQuery,
    paginatedRoles,
    
    // 操作方法
    fetchRoles,
    fetchRoleDetail,
    addRole,
    editRole,
    removeRole,
    resetFilters,
    setPagination,
    setSearchQuery
  };
});