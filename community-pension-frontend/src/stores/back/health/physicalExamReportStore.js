import { defineStore } from 'pinia';
import { ref } from 'vue';
import { 
  listPhysicalExamReports, 
  getPhysicalExamReport, 
  addPhysicalExamReport, 
  updatePhysicalExamReport, 
  deletePhysicalExamReport 
} from '@/api/back/health/report';

export const usePhysicalExamReportStore = defineStore('physicalExamReport', () => {
  // 状态
  const reportList = ref([]);
  const currentReport = ref(null);
  const loading = ref(false);
  const total = ref(0);
  
  // 获取体检报告列表
  const getReportList = async (params) => {
    loading.value = true;
    try {
      const response = await listPhysicalExamReports(params);
      reportList.value = response.data.records || [];
      total.value = response.data.total || 0;
      return response.data;
    } catch (error) {
      console.error('获取体检报告列表失败:', error);
      throw error;
    } finally {
      loading.value = false;
    }
  };
  
  // 获取体检报告详情
  const getReportDetail = async (id) => {
    try {
      const response = await getPhysicalExamReport(id);
      currentReport.value = response.data;
      return response.data;
    } catch (error) {
      console.error('获取体检报告详情失败:', error);
      throw error;
    }
  };
  
  // 添加体检报告
  const addReport = async (data) => {
    try {
      const response = await addPhysicalExamReport(data);
      return response.data;
    } catch (error) {
      console.error('添加体检报告失败:', error);
      throw error;
    }
  };
  
  // 更新体检报告
  const updateReport = async (data) => {
    try {
      const response = await updatePhysicalExamReport(data);
      return response.data;
    } catch (error) {
      console.error('更新体检报告失败:', error);
      throw error;
    }
  };
  
  // 删除体检报告
  const deleteReport = async (id) => {
    try {
      const response = await deletePhysicalExamReport(id);
      return response.data;
    } catch (error) {
      console.error('删除体检报告失败:', error);
      throw error;
    }
  };

  return {
    reportList,
    currentReport,
    loading,
    total,
    getReportList,
    getReportDetail,
    addReport,
    updateReport,
    deleteReport
  };
});
