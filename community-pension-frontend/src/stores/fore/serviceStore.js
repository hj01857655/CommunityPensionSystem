import {
  cancelAppointment,
  createAppointment,
  getMyAppointments,
  getServiceDetail,
  getServiceList
} from '@/api/fore/service';
import { ElMessage } from 'element-plus';
import { defineStore } from 'pinia';
import { ref } from 'vue';

const useServiceStore = defineStore('service', () => {
  // 状态定义
  const serviceList = ref([]);
  const serviceDetail = ref(null);
  const loading = ref(false);
  const total = ref(0);
  const myAppointments = ref([]);
  const appointmentTotal = ref(0);

  // 获取服务列表
  const fetchServiceList = async (params) => {
    try {
      loading.value = true;
      const res = await getServiceList(params);
      if (res.code === 200) {
        serviceList.value = res.data.records || [];
        total.value = res.data.total || 0;
        return res.data;
      } else {
        ElMessage.error(res.message || '获取服务列表失败');
        return null;
      }
    } catch (error) {
      console.error('获取服务列表失败:', error);
      ElMessage.error('获取服务列表失败');
      return null;
    } finally {
      loading.value = false;
    }
  };

  // 获取服务详情
  const fetchServiceDetail = async (serviceId) => {
    try {
      loading.value = true;
      const res = await getServiceDetail(serviceId);
      if (res.code === 200) {
        serviceDetail.value = res.data;
        return res;
      }
      ElMessage.error(res.msg || '获取服务详情失败');
      return null;
    } catch (error) {
      console.error('获取服务详情失败:', error);
      ElMessage.error('获取服务详情失败');
      throw error;
    } finally {
      loading.value = false;
    }
  };

  // 创建预约
  const createServiceAppointment = async (data) => {
    try {
      loading.value = true;
      const res = await createAppointment(data);
      if (res.code === 200) {
        ElMessage.success('预约成功');
        return res.data;
      } else {
        ElMessage.error(res.message || '预约失败');
        throw new Error(res.message || '预约失败');
      }
    } catch (error) {
      console.error('预约失败:', error);
      throw error;
    } finally {
      loading.value = false;
    }
  };

  // 获取我的预约列表
  const fetchMyAppointments = async (params) => {
    try {
      loading.value = true;
      const res = await getMyAppointments(params);
      if (res.code === 200) {
        // 清空现有数据
        myAppointments.value.length = 0;
        
        // API直接返回数组，不需要取records
        const appointments = Array.isArray(res.data) ? res.data : [];
        
        // 对数据进行预处理，确保必要的字段存在
        const processedAppointments = appointments.map(item => {
          // 确保服务信息字段存在
          return {
            ...item,
            // 尝试从各种可能的字段中获取价格和时长信息
            price: item.price || item.serviceFee || item.actualFee || 0,
            duration: item.duration || item.serviceDuration || item.actualDuration || 0
          };
        });
        
        // 确保数据被正确赋值 - 遍历添加
        processedAppointments.forEach(item => {
          myAppointments.value.push(item);
        });
        
        // 设置总数
        appointmentTotal.value = processedAppointments.length;
        
        return processedAppointments;
      } else {
        throw new Error(res.message || '获取预约列表失败');
      }
    } catch (error) {
      myAppointments.value = [];
      appointmentTotal.value = 0;
      throw error;
    } finally {
      loading.value = false;
    }
  };

  // 取消预约
  const cancelServiceAppointment = async (appointmentId) => {
    try {
      loading.value = true;
      const res = await cancelAppointment(appointmentId);
      if (res.code === 200) {
        ElMessage.success('取消预约成功');
        return res.data;
      } else {
        ElMessage.error(res.message || '取消预约失败');
        throw new Error(res.message || '取消预约失败');
      }
    } catch (error) {
      console.error('取消预约失败:', error);
      throw error;
    } finally {
      loading.value = false;
    }
  };

  // 重置状态
  const resetState = () => {
    serviceList.value = [];
    serviceDetail.value = null;
    loading.value = false;
    total.value = 0;
    myAppointments.value = [];
    appointmentTotal.value = 0;
  };

  return {
    serviceList,
    serviceDetail,
    loading,
    total,
    myAppointments,
    appointmentTotal,
    fetchServiceList,
    fetchServiceDetail,
    createServiceAppointment,
    fetchMyAppointments,
    cancelServiceAppointment,
    resetState
  };
});

export default useServiceStore; 