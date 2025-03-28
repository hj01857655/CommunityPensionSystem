import { defineStore } from 'pinia';
import { getHealthRecords, addHealthRecord, updateHealthRecord, deleteHealthRecord, getElders, listHealthRecords } from '@/api/back/health/records';

export const useHealthStore = defineStore('health', {
    state: () => ({
        healthRecords: [],
        elderOptions: [],
        loading: false,
        totalRecords: 0,
        currentPage: 1,
        pageSize: 10,
    }),
    actions: {
        async fetchHealthRecords(elderId) {
            this.loading = true;
            try {
                if (!elderId) {
                    throw new Error('elderId is required');
                }
                const response = await getHealthRecords(elderId);
                console.log(response.data.data);
                console.log(response.data.data.records);
                this.healthRecords = response.data.data.records;
                this.totalRecords = response.data.data.records.length;
            } catch (error) {
                console.error('Failed to fetch health records:', error);
            } finally {
                this.loading = false;
            }
        },
        async addHealthRecord(data) {
            try {
                await addHealthRecord(data);
                this.fetchHealthRecords(); // 重新获取数据
            } catch (error) {
                console.error('添加健康记录失败:', error);
            }
        },
        async updateHealthRecord(data) {
            try {
                await updateHealthRecord(data);
                this.fetchHealthRecords(); // 重新获取数据
            } catch (error) {
                console.error('更新健康记录失败:', error);
            }
        },
        async deleteHealthRecord(id) {
            try {
                await deleteHealthRecord(id);
                this.fetchHealthRecords(); // 重新获取数据
            } catch (error) {
                console.error('删除健康记录失败:', error);
            }
        },
        async fetchElderOptions() {
            try {
                const response = await getElders();
                this.elderOptions = response.data;
            } catch (error) {
                console.error('获取老人列表失败:', error);
            }
        },
        async fetchAllHealthRecords(page = 1, size = 10) {
            this.loading = true;
            try {
                const response = await listHealthRecords(page, size);
                this.healthRecords = response.data.records;
                this.totalRecords = response.data.total;
            } catch (error) {
                console.error('Failed to fetch all health records:', error);
            } finally {
                this.loading = false;
            }
        },
        handleCurrentChange(page) {
            this.currentPage = page;
            this.fetchHealthRecords();
        },
        setPage(page) {
            this.currentPage = page;
            this.fetchElderOptions();
        }
    }
}); 