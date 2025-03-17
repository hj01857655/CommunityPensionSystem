import { defineStore } from 'pinia'
import { getKinList, getKinById, addKin, updateKin, deleteKin, getKinsByElderId, getAllKins } from '@/api/back/UserManage/KinManage'
import { ElMessage } from 'element-plus'

export const useKinStore = defineStore('kin', {
  state: () => ({
    kinList: [],
    total: 0,
    currentPage: 1,
    pageSize: 10,
    loading: false,
    searchQuery: '',
    elderKins: [], // 特定老人的亲属列表
    currentElderId: null, // 当前选中的老人ID
  }),

  actions: {
    // 获取亲属列表
    async fetchKins() {
      this.loading = true;
      try {
        const params = {
          current: this.currentPage,
          size: this.pageSize,
          query: this.searchQuery,
        };
        const response = await getKinList(params);
        if (response.data) {
          this.kinList = response.data.records;
          this.total = response.data.total;
        }
      } catch (error) {
        console.error('获取亲属列表失败:', error);
      } finally {
        this.loading = false;
      }
    },

    // 添加亲属
    async addKin(data) {
      try {
        await addKin(data);
        this.fetchKins(); // 重新获取数据
        return true;
      } catch (error) {
        console.error('添加亲属失败:', error);
        return false;
      }
    },

    // 更新亲属信息
    async updateKin(data) {
      try {
        await updateKin(data);
        this.fetchKins(); // 重新获取数据
        return true;
      } catch (error) {
        console.error('更新亲属失败:', error);
        return false;
      }
    },

    // 删除亲属
    async deleteKin(id) {
      try {
        await deleteKin(id);
        this.fetchKins(); // 重新获取数据
        return true;
      } catch (error) {
        console.error('删除亲属失败:', error);
        return false;
      }
    },

    // 根据老人ID获取亲属列表
    async fetchKinsByElderId(elderId) {
      this.loading = true;
      this.currentElderId = elderId;
      try {
        const response = await getKinsByElderId(elderId);
        console.log('获取老人亲属列表', response);
        if (response.code === 200) {
          this.elderKins = response.data || [];
        } else {
          this.elderKins = [];
          ElMessage.error(response.message || '获取老人亲属列表失败');
        }
      } catch (error) {
        console.error('获取老人亲属列表出错:', error);
        ElMessage.error('获取老人亲属列表失败');
        this.elderKins = [];
      } finally {
        this.loading = false;
      }
    },

    // 获取所有亲属（不分页）
    async fetchAllKins() {
      try {
        const response = await getAllKins();
        return response.code === 200 ? response.data : [];
      } catch (error) {
        console.error('获取所有亲属出错:', error);
        ElMessage.error('获取所有亲属失败');
        return [];
      }
    }
  }
})