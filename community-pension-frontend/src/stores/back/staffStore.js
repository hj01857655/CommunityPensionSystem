import { defineStore } from 'pinia';
import { getStaffList, addStaff, updateStaff, deleteStaff } from '@/api/back/UserManage/StaffManage';
import { ElMessage } from 'element-plus';

export const useStaffStore = defineStore('staff', {
  state: () => ({
    staffList: [], // 社区工作人员列表
    total: 0, // 总条数
    currentPage: 1, // 当前页
    pageSize: 10, // 每页条数
    loading: false, // 加载状态
    searchQuery: '', // 搜索条件
  }),

  actions: {
    // 获取社区工作人员列表
    async fetchStaffs(params) {
      this.loading = true;
      try {
        const response = await getStaffList(params);
        console.log('API Response:', response); // 调试用
        if (response.code === 200) {
          this.staffList = response.data.records;
          this.total = response.data.total;
        } else {
          ElMessage.error(response.message || '获取社区工作人员列表失败');
        }
      } catch (error) {
        console.error('获取社区工作人员列表出错:', error);
        ElMessage.error('获取社区工作人员列表失败');
      } finally {
        this.loading = false;
      }
    },

    async addStaff(staffData) {
      try {
        const response = await addStaff(staffData);
        if (response.code === 200) {
          ElMessage.success('添加社区工作人员成功');
          this.fetchStaffs({ current: this.currentPage, size: this.pageSize, name: this.searchQuery });
          return response;
        } else {
          ElMessage.error(response.message || '添加社区工作人员失败');
        }
      } catch (error) {
        console.error('添加社区工作人员出错:', error);
        ElMessage.error('添加社区工作人员失败');
      }
    },

    async updateStaff(staffData) {
      try {
        const response = await updateStaff(staffData);
        if (response.code === 200) {
          ElMessage.success('更新社区工作人员成功');
          this.fetchStaffs({ current: this.currentPage, size: this.pageSize, name: this.searchQuery });
          return response;
        } else {
          ElMessage.error(response.message || '更新社区工作人员失败');
        }
      } catch (error) {
        console.error('更新社区工作人员出错:', error);
        ElMessage.error('更新社区工作人员失败');
      }
    },

    async deleteStaff(staffId) {
      try {
        const response = await deleteStaff(staffId);
        if (response.code === 200) {
          ElMessage.success('删除社区工作人员成功');
          this.fetchStaffs({ current: this.currentPage, size: this.pageSize, name: this.searchQuery });
          return response;
        } else {
          ElMessage.error(response.message || '删除社区工作人员失败');
        }
      } catch (error) {
        console.error('删除社区工作人员出错:', error);
        ElMessage.error('删除社区工作人员失败');
      }
    },
  }
}); 