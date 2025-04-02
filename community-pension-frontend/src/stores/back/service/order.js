import { defineStore } from 'pinia';
import { ref } from 'vue';
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
      const res = await getServiceOrderList(params);
      if (res.code === 200) {
        orderList.value = res.data.data.records;
        total.value = res.data.data.total;
        return res.data.data;
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
      const res = await createServiceOrder(data);
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
      const res = await reviewServiceOrder(orderId, data);
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
  const assignOrder = async (orderId, data) => {
    try {
      const res = await assignServiceOrder(orderId, data);
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
  const completeOrder = async (orderId) => {
    try {
      const res = await completeServiceOrder(orderId);
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