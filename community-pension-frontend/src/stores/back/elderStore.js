import { defineStore } from 'pinia'
import { getElderList, addElder, updateElder, deleteElder, getUnboundElders } from '@/api/back/UserManage/ElderManage'
import { ElMessage } from 'element-plus'

export const useElderStore = defineStore('elder', {
  state: () => ({
    elderList: [],//老人列表
    total: 0,//总条数
    currentPage: 1,//当前页
    pageSize: 10,//每页条数
    loading: false,//加载状态
    searchQuery: '',//搜索条件
    unboundElderList: [] // 未绑定家属的老人列表
  }),

  actions: {
    // 获取老人列表
    async fetchElders(params) {
      this.loading = true;
      try {
        const response = await getElderList(params);
        if (response.code === 200) {
          this.elderList = response.data.records;
        } else {
          ElMessage.error(response.message || '获取老人列表失败');
        }
      } catch (error) {
        console.error('获取老人列表出错:', error);
        ElMessage.error('获取老人列表失败');
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

    // 获取未绑定家属的老人列表
    async fetchUnboundElders() {
      try {
        const response = await getUnboundElders();
        if (response.code === 200) {
          this.unboundElderList = response.data;
        } else {
          console.error('获取未绑定家属的老人列表失败:', response.message);
        }
      } catch (error) {
        console.error('获取未绑定家属的老人列表出错:', error);
      }
    },
  }
})