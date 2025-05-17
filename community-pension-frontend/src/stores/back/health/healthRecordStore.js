import { defineStore } from 'pinia';
import { ref } from 'vue';
import {
  getHealthRecords,
  addHealthRecord,
  updateHealthRecord,
  deleteHealthRecord,
  listHealthRecords
} from '@/api/back/health/records';

export const useHealthRecordStore = defineStore('healthRecord', () => {
  // 状态
  const recordList = ref([]);
  const currentRecord = ref(null);
  const loading = ref(false);
  const total = ref(0);
  
  // 获取健康记录列表
  const getRecordList = async (params) => {
    loading.value = true;
    try {
      const { page, size, ...queryParams } = params;
      const response = await listHealthRecords(page, size, queryParams);
      recordList.value = response.data.records || [];
      total.value = response.data.total || 0;
      return response.data;
    } catch (error) {
      console.error('获取健康记录列表失败:', error);
      throw error;
    } finally {
      loading.value = false;
    }
  };
  
  // 获取健康记录详情
  const getRecordDetail = async (id) => {
    try {
      const response = await getHealthRecords(id);
      currentRecord.value = response.data;
      return response.data;
    } catch (error) {
      console.error('获取健康记录详情失败:', error);
      throw error;
    }
  };
  
  // 添加健康记录
  const addRecord = async (data) => {
    try {
      const response = await addHealthRecord(data);
      return response.data;
    } catch (error) {
      console.error('添加健康记录失败:', error);
      throw error;
    }
  };
  
  // 更新健康记录
  const updateRecord = async (data) => {
    try {
      const response = await updateHealthRecord(data);
      return response.data;
    } catch (error) {
      console.error('更新健康记录失败:', error);
      throw error;
    }
  };
  
  // 删除健康记录
  const deleteRecord = async (id) => {
    try {
      const response = await deleteHealthRecord(id);
      return response.data;
    } catch (error) {
      console.error('删除健康记录失败:', error);
      throw error;
    }
  };

  return {
    recordList,
    currentRecord,
    loading,
    total,
    getRecordList,
    getRecordDetail,
    addRecord,
    updateRecord,
    deleteRecord
  };
});
