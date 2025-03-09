import { defineStore } from 'pinia';
import { ref } from 'vue';
import { getHealthRecords, addHealthRecord, updateHealthRecord, deleteHealthRecord } from '@/api/healthRecords';

export const useHealthStore = defineStore('health', () => {
    // 健康记录列表
    const healthRecords = ref([]);
    // 当前选中的健康记录
    const currentRecord = ref(null);
    // 加载状态
    const loading = ref(false);
    // 分页信息
    const pagination = ref({
        currentPage: 1,
        pageSize: 10,
        total: 0
    });
    
    // 获取健康记录列表
    const fetchHealthRecords = async (elderId, params = {}) => {
        loading.value = true;
        try {
            const queryParams = {
                elderId,
                page: params.page || pagination.value.currentPage,
                size: params.size || pagination.value.pageSize,
                ...params
            };
            
            const response = await getHealthRecords(queryParams);
            if (response.success) {
                healthRecords.value = response.data.data.records || [];
                pagination.value = {
                    currentPage: response.data.data.current || 1,
                    pageSize: response.data.data.size || 10,
                    total: response.data.data.total || 0
                };
                return { success: true, data: response.data.data };
            }
            return { success: false, message: response.error };
        } catch (error) {
            return { success: false, message: error.message || '获取健康记录失败' };
        } finally {
            loading.value = false;
        }
    };
    
    // 添加健康记录
    const addRecord = async (recordData) => {
        try {
            const response = await addHealthRecord(recordData);
            if (response.success) {
                // 刷新列表
                await fetchHealthRecords(recordData.elderId);
                return { success: true, data: response.data.data };
            }
            return { success: false, message: response.error };
        } catch (error) {
            return { success: false, message: error.message || '添加健康记录失败' };
        }
    };
    
    // 更新健康记录
    const updateRecord = async (recordData) => {
        try {
            const response = await updateHealthRecord(recordData);
            if (response.success) {
                // 更新本地记录
                const index = healthRecords.value.findIndex(record => record.id === recordData.id);
                if (index !== -1) {
                    healthRecords.value[index] = { ...healthRecords.value[index], ...recordData };
                }
                return { success: true };
            }
            return { success: false, message: response.error };
        } catch (error) {
            return { success: false, message: error.message || '更新健康记录失败' };
        }
    };
    
    // 删除健康记录
    const removeRecord = async (recordId, elderId) => {
        try {
            const response = await deleteHealthRecord(recordId);
            if (response.success) {
                // 从列表中移除
                healthRecords.value = healthRecords.value.filter(record => record.id !== recordId);
                // 更新分页信息
                pagination.value.total -= 1;
                // 如果当前页没有数据且不是第一页，则跳转到上一页
                if (healthRecords.value.length === 0 && pagination.value.currentPage > 1) {
                    pagination.value.currentPage -= 1;
                    await fetchHealthRecords(elderId);
                }
                return { success: true };
            }
            return { success: false, message: response.error };
        } catch (error) {
            return { success: false, message: error.message || '删除健康记录失败' };
        }
    };
    
    // 设置当前选中的记录
    const setCurrentRecord = (record) => {
        currentRecord.value = record;
    };
    
    // 清空健康记录
    const clearHealthRecords = () => {
        healthRecords.value = [];
        currentRecord.value = null;
        pagination.value = {
            currentPage: 1,
            pageSize: 10,
            total: 0
        };
    };
    
    return {
        healthRecords,
        currentRecord,
        loading,
        pagination,
        fetchHealthRecords,
        addRecord,
        updateRecord,
        removeRecord,
        setCurrentRecord,
        clearHealthRecords
    };
});