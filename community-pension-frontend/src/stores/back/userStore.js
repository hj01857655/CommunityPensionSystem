import { defineStore } from 'pinia';
import { getUserList, addUser, updateUser, deleteUser } from '@/api/back/UserManage/UserManage';
import { ElMessage } from 'element-plus';

export const useUserStore = defineStore('user', {
  state: () => ({
    userList: [], // 用户列表
    total: 0, // 总条数
    currentPage: 1, // 当前页
    pageSize: 10, // 每页条数
    loading: false, // 加载状态
    searchQuery: '', // 搜索条件
  }),

  actions: {
    // 获取用户列表
    async fetchUsers(params) {
      this.loading = true;
      try {
        const response = await getUserList(params);
        console.log('API Response:', response); // 调试用
        if (response.code === 200) {
          this.userList = response.data.records;
          this.total = response.data.total;
        } else {
          ElMessage.error(response.message || '获取用户列表失败');
        }
      } catch (error) {
        console.error('获取用户列表出错:', error);
        ElMessage.error('获取用户列表失败');
      } finally {
        this.loading = false;
      }
    },

    async addUser(userData) {
      try {
        const response = await addUser(userData);
        if (response.code === 200) {
          ElMessage.success('添加用户成功');
          this.fetchUsers({ current: this.currentPage, size: this.pageSize, username: this.searchQuery });
        } else {
          ElMessage.error(response.message || '添加用户失败');
        }
      } catch (error) {
        console.error('添加用户出错:', error);
        ElMessage.error('添加用户失败');
      }
    },

    async updateUser(userData) {
      try {
        const response = await updateUser(userData);
        if (response.code === 200) {
          ElMessage.success('更新用户成功');
          this.fetchUsers({ current: this.currentPage, size: this.pageSize, username: this.searchQuery });
        } else {
          ElMessage.error(response.message || '更新用户失败');
        }
      } catch (error) {
        console.error('更新用户出错:', error);
        ElMessage.error('更新用户失败');
      }
    },

    async deleteUser(userId) {
      try {
        const response = await deleteUser(userId);
        if (response.code === 200) {
          ElMessage.success('删除用户成功');
          this.fetchUsers({ current: this.currentPage, size: this.pageSize, username: this.searchQuery });
        } else {
          ElMessage.error(response.message || '删除用户失败');
        }
      } catch (error) {
        console.error('删除用户出错:', error);
        ElMessage.error('删除用户失败');
      }
    },
  }
}); 