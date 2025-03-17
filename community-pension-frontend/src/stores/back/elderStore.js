import { defineStore } from 'pinia'
import { getElderList, getElderById, addElder, updateElder, deleteElder, getElders } from '@/api/back/UserManage/ElderManage'
import { ElMessage } from 'element-plus'

export const useElderStore = defineStore('elder', {
  state: () => ({
    elderList: [],
    total: 0,
    currentPage: 1,
    pageSize: 10,
    loading: false,
    searchQuery: '',
  }),

  actions: {
    // 获取老人列表
    async fetchElders() {
      this.loading = true;
      try {
        const params = {
          current: this.currentPage,
          size: this.pageSize,
          query: this.searchQuery,
        };
        console.log(params);
        const response = await getElderList(params);
        console.log(response);

        if (response.data) {
          const { records, total } = response.data;
          this.elderList = records;
          this.total = total;
        }
      } catch (error) {
        console.error('获取老人列表出错:', error);
        ElMessage.error('获取老人列表失败');
        this.elderList = [];
        this.total = 0;
      } finally {
        this.loading = false;
      }
    },

    // 添加老人
    async addElder(elderData) {
      try {
        const response = await addElder(elderData)
        if (response.code === 200) {
          ElMessage.success('添加老人成功')
          await this.fetchElders()
          return true
        } else {
          ElMessage.error(response.message || '添加老人失败')
          return false
        }
      } catch (error) {
        console.error('添加老人出错:', error)
        ElMessage.error('添加老人失败')
        return false
      }
    },

    // 更新老人信息
    async updateElder(elderData) {
      try {
        const response = await updateElder(elderData)
        if (response.code === 200) {
          ElMessage.success('更新老人信息成功')
          await this.fetchElders()
          return true
        } else {
          ElMessage.error(response.message || '更新老人信息失败')
          return false
        }
      } catch (error) {
        console.error('更新老人信息出错:', error)
        ElMessage.error('更新老人信息失败')
        return false
      }
    },

    // 删除老人
    async deleteElder(id) {
      try {
        const response = await deleteElder(id)
        if (response.code === 200) {
          ElMessage.success('删除老人成功')
          await this.fetchElders()
          return true
        } else {
          ElMessage.error(response.message || '删除老人失败')
          return false
        }
      } catch (error) {
        console.error('删除老人出错:', error)
        ElMessage.error('删除老人失败')
        return false
      }
    },

    
  }
})