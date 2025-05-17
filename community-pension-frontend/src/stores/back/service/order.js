import { defineStore } from 'pinia';
import { ref } from 'vue';
import axios from '@/utils/axios'; // 修正axios导入路径
import { 
  getServiceOrderList,
  getServiceOrderDetail,
  createServiceOrder,
  reviewServiceOrder,
  assignServiceOrder,
  startServiceOrder,
  completeServiceOrder
} from '@/api/back/service/order';

export const useServiceOrderStore = defineStore('serviceOrder', () => {
  // 状态
  const orderList = ref([]);
  const total = ref(0);
  const loading = ref(false);

  // 重置状态
  const resetState = () => {
    orderList.value = [];
    total.value = 0;
    loading.value = false;
  };

  // 获取服务订单列表
  const getOrderList = async (params) => {
    loading.value = true;
    try {
      console.log('获取工单列表参数:', params);
      const res = await getServiceOrderList({
        current: params.current,
        size: params.size,
        serviceName: params.serviceName,
        status: params.status,
        startTime: params.startTime,
        endTime: params.endTime
      });
      console.log('工单列表原始数据:', res);
      if (res.code === 200) {
        // 处理数据字段映射
        orderList.value = res.data.records.map(item => ({
          ...item,
          appointmentTime: item.scheduleTime, // 映射预约时间字段
          elderName: item.userName // 映射服务对象字段
        }));
        console.log('处理后的工单列表:', orderList.value);
        total.value = res.data.total;
        return res.data;
      } else {
        throw new Error(res.message || '获取服务订单列表失败');
      }
    } catch (error) {
      console.error('获取服务订单列表失败:', error);
      throw error;
    } finally {
      loading.value = false;
    }
  };

  // 获取服务订单详情
  const getOrderDetail = async (orderId) => {
    try {
      const res = await getServiceOrderDetail(orderId);
      if (res.code === 200) {
        return res.data;
      } else {
        throw new Error(res.message || '获取服务订单详情失败');
      }
    } catch (error) {
      console.error('获取服务订单详情失败:', error);
      throw error;
    }
  };

  // 创建服务订单
  const createOrder = async (data) => {
    try {
      const res = await createServiceOrder({
        userId: data.userId,
        serviceItemId: data.serviceId,
        applyReason: data.remark,
        scheduleTime: data.appointmentTime
      });
      if (res.code === 200) {
        return res.data;
      } else {
        throw new Error(res.message || '创建服务订单失败');
      }
    } catch (error) {
      console.error('创建服务订单失败:', error);
      throw error;
    }
  };

  // 审核服务订单
  const reviewOrder = async (orderId, data) => {
    try {
      console.log('审核订单参数:', { orderId, data });
      if (!orderId) {
        throw new Error('订单ID不能为空');
      }
      const res = await reviewServiceOrder(orderId, data);
      console.log('审核订单响应:', res);
      if (res.code === 200) {
        return res.data;
      } else {
        throw new Error(res.message || '审核服务订单失败');
      }
    } catch (error) {
      console.error('审核服务订单失败:', error);
      throw error;
    }
  };

  // 分配服务订单
  const assignOrder = async (orderId) => {
    try {
      const res = await assignServiceOrder(orderId);
      if (res.code === 200) {
        return res.data;
      } else {
        throw new Error(res.message || '分配服务订单失败');
      }
    } catch (error) {
      console.error('分配服务订单失败:', error);
      throw error;
    }
  };

  // 开始服务
  const startOrder = async (orderId) => {
    try {
      const res = await startServiceOrder(orderId);
      if (res.code === 200) {
        return res.data;
      } else {
        throw new Error(res.message || '开始服务失败');
      }
    } catch (error) {
      console.error('开始服务失败:', error);
      throw error;
    }
  };

  // 完成服务
  const completeOrder = async (orderId, actualDuration) => {
    try {
      const res = await completeServiceOrder({
        id: orderId,
        actualDuration
      });
      if (res.code === 200) {
        return res.data;
      } else {
        throw new Error(res.message || '完成服务失败');
      }
    } catch (error) {
      console.error('完成服务失败:', error);
      throw error;
    }
  };

  return {
    orderList,
    total,
    loading,
    getOrderList,
    getOrderDetail,
    createOrder,
    reviewOrder,
    assignOrder,
    startOrder,
    completeOrder,
    resetState
  };
}); 