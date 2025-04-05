import { defineStore } from 'pinia';
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import {
  getServiceList,
  getServiceDetail,
  createAppointment,
  getMyAppointments,
  cancelAppointment,
  evaluateService
} from '@/api/fore/service';

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
      console.log('开始获取预约列表，参数:', params);
      const res = await getMyAppointments(params);
      console.log('获取预约列表响应:', res);
      if (res.code === 200) {
        console.log('预约列表数据:', res.data);
        myAppointments.value = Array.isArray(res.data) ? res.data : [];
        appointmentTotal.value = myAppointments.value.length;
        console.log('更新后的预约列表:', myAppointments.value);
        console.log('更新后的总数:', appointmentTotal.value);
        return res.data;
      } else {
        throw new Error(res.message || '获取预约列表失败');
      }
    } catch (error) {
      console.error('获取预约列表失败:', error);
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

  // 评价服务
  const handleEvaluateService = async (appointmentId, evaluation) => {
    try {
      loading.value = true;
      const res = await evaluateService(appointmentId, evaluation);
      if (res.code === 200) {
        ElMessage.success('评价成功');
        return res.data;
      } else {
        ElMessage.error(res.message || '评价失败');
        throw new Error(res.message || '评价失败');
      }
    } catch (error) {
      console.error('评价失败:', error);
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
    handleEvaluateService,
    resetState
  };
});

export default useServiceStore; 