import { defineStore } from 'pinia';
import { ref } from 'vue';
import {
  listHealthMonitors,
  getHealthMonitor,
  addHealthMonitor,
  updateHealthMonitor,
  deleteHealthMonitor,
  exportHealthMonitors
} from '@/api/back/health/monitor';

export const useHealthMonitorStore = defineStore('healthMonitor', () => {
  // 状态
  const monitorList = ref([]);
  const currentMonitor = ref(null);
  const loading = ref(false);
  const total = ref(0);
  
  // 获取健康监测列表
  const getMonitorList = async (params) => {
    loading.value = true;
    try {
      const response = await listHealthMonitors(params);
      monitorList.value = response.data.records || [];
      total.value = response.data.total || 0;
      return response.data;
    } catch (error) {
      console.error('获取健康监测列表失败:', error);
      throw error;
    } finally {
      loading.value = false;
    }
  };
  
  // 获取健康监测详情
  const getMonitorDetail = async (id) => {
    try {
      const response = await getHealthMonitor(id);
      currentMonitor.value = response.data;
      return response.data;
    } catch (error) {
      console.error('获取健康监测详情失败:', error);
      throw error;
    }
  };
  
  // 添加健康监测
  const addMonitor = async (data) => {
    try {
      const response = await addHealthMonitor(data);
      return response.data;
    } catch (error) {
      console.error('添加健康监测失败:', error);
      throw error;
    }
  };
  
  // 更新健康监测
  const updateMonitor = async (data) => {
    try {
      const response = await updateHealthMonitor(data);
      return response.data;
    } catch (error) {
      console.error('更新健康监测失败:', error);
      throw error;
    }
  };
  
  // 删除健康监测
  const deleteMonitor = async (id) => {
    try {
      const response = await deleteHealthMonitor(id);
      return response.data;
    } catch (error) {
      console.error('删除健康监测失败:', error);
      throw error;
    }
  };

  // 导出健康监测数据
  const exportMonitors = async (params) => {
    try {
      const response = await exportHealthMonitors(params);
      return response;
    } catch (error) {
      console.error('导出健康监测数据失败:', error);
      throw error;
    }
  };

  return {
    monitorList,
    currentMonitor,
    loading,
    total,
    getMonitorList,
    getMonitorDetail,
    addMonitor,
    updateMonitor,
    deleteMonitor,
    exportMonitors
  };
});
