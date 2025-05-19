import { defineStore } from 'pinia';
import { ref } from 'vue';
import { 
  listPhysicalExamReports, 
  getPhysicalExamReport, 
  addPhysicalExamReport, 
  updatePhysicalExamReport, 
  deletePhysicalExamReport,
  listExamItems
} from '@/api/back/health/report';

export const usePhysicalExamReportStore = defineStore('physicalExamReport', () => {
  // 状态
  const reportList = ref([]);
  const currentReport = ref(null);
  const loading = ref(false);
  const total = ref(0);
  const examItems = ref([]);
  
  // 预设的体检项目数据（作为备用）
  const fallbackExamItems = [
    { 
      itemName: '血红蛋白', 
      itemUnit: 'g/L', 
      normalRange: '120-160',
      category: '血常规'
    },
    { 
      itemName: '白细胞计数', 
      itemUnit: '10^9/L', 
      normalRange: '4.0-10.0',
      category: '血常规'
    },
    { 
      itemName: '血小板计数', 
      itemUnit: '10^9/L', 
      normalRange: '100-300',
      category: '血常规'
    },
    { 
      itemName: '血糖', 
      itemUnit: 'mmol/L', 
      normalRange: '3.9-6.1',
      category: '生化'
    },
    { 
      itemName: '总胆固醇', 
      itemUnit: 'mmol/L', 
      normalRange: '3.1-5.7',
      category: '生化'
    },
    { 
      itemName: '甘油三酯', 
      itemUnit: 'mmol/L', 
      normalRange: '0.56-1.7',
      category: '生化'
    },
    { 
      itemName: '高密度脂蛋白', 
      itemUnit: 'mmol/L', 
      normalRange: '0.91-1.92',
      category: '生化'
    },
    { 
      itemName: '低密度脂蛋白', 
      itemUnit: 'mmol/L', 
      normalRange: '2.07-3.1',
      category: '生化'
    },
    { 
      itemName: '尿素氮', 
      itemUnit: 'mmol/L', 
      normalRange: '2.9-8.2',
      category: '生化'
    },
    { 
      itemName: '肌酐', 
      itemUnit: 'μmol/L', 
      normalRange: '53-97',
      category: '生化'
    },
    { 
      itemName: '尿酸', 
      itemUnit: 'μmol/L', 
      normalRange: '208-428',
      category: '生化'
    },
    { 
      itemName: '谷丙转氨酶', 
      itemUnit: 'U/L', 
      normalRange: '7-40',
      category: '肝功能'
    },
    { 
      itemName: '谷草转氨酶', 
      itemUnit: 'U/L', 
      normalRange: '13-35',
      category: '肝功能'
    },
    { 
      itemName: '收缩压', 
      itemUnit: 'mmHg', 
      normalRange: '90-140',
      category: '心血管'
    },
    { 
      itemName: '舒张压', 
      itemUnit: 'mmHg', 
      normalRange: '60-90',
      category: '心血管'
    },
    { 
      itemName: '心率', 
      itemUnit: '次/分', 
      normalRange: '60-100',
      category: '心血管'
    }
  ];
  
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
  
  // 获取体检项目列表
  const getExamItems = async () => {
    try {
      const response = await listExamItems();
      if (response.data && Array.isArray(response.data)) {
        examItems.value = response.data;
      } else if (response.data && Array.isArray(response.data.records)) {
        examItems.value = response.data.records;
      } else {
        console.warn('获取体检项目列表返回格式不正确，使用预设项目');
        examItems.value = fallbackExamItems;
      }
      return examItems.value;
    } catch (error) {
      console.error('获取体检项目列表失败:', error);
      examItems.value = fallbackExamItems;
      return examItems.value;
    }
  };
  
  // 获取当前可用的体检项目（优先使用后端数据，如果没有则使用备用数据）
  const getAvailableExamItems = () => {
    return examItems.value.length > 0 ? examItems.value : fallbackExamItems;
  };

  return {
    reportList,
    currentReport,
    loading,
    total,
    examItems,
    getReportList,
    getReportDetail,
    addReport,
    updateReport,
    deleteReport,
    getExamItems,
    getAvailableExamItems
  };
});
