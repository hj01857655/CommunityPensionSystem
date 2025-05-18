import {
  assignServiceOrder,
  completeServiceOrder,
  createServiceOrder,
  exportServiceOrder,
  getServiceOrderDetail,
  getServiceOrderList,
  getServiceOrderStatusDict,
  reviewServiceOrder,
  startServiceOrder,
  updateServiceOrder
} from '@/api/back/service/order';
import axios from '@/utils/axios'; // 修正axios导入路径
import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useServiceOrderStore = defineStore('serviceOrder', () => {
  // 状态
  const orderList = ref([]);
  const total = ref(0);
  const loading = ref(false);
  const statusOptions = ref([]); // 状态字典选项

  // 重置状态
  const resetState = () => {
    orderList.value = [];
    total.value = 0;
    loading.value = false;
    // 不重置状态字典
  };

  // 获取状态字典
  const getStatusDict = async () => {
    try {
      const res = await getServiceOrderStatusDict();
      if (res.code === 200) {
        statusOptions.value = res.data;
        return res.data;
      } else {
        throw new Error(res.message || '获取状态字典失败');
      }
    } catch (error) {
      console.error('获取状态字典失败:', error);
      throw error;
    }
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
        userName: params.userName,
        serviceType: params.serviceType,
        status: params.status,
        beginTime: params.beginTime,
        endTime: params.endTime
      });
      console.log('工单列表原始数据:', res);
      if (res.code === 200) {
        orderList.value = res.data.records;
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
  const addOrder = async (data) => {
    try {
      const res = await createServiceOrder({
        userId: data.userId,
        serviceItemId: data.serviceItemId,
        applyReason: data.applyReason,
        scheduleTime: data.scheduleTime,
        status: data.status
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

  // 修改服务订单
  const updateOrder = async (data) => {
    try {
      if (!data.id) {
        throw new Error('订单ID不能为空');
      }
      const res = await updateServiceOrder({
        id: data.id,
        userId: data.userId,
        serviceItemId: data.serviceId,
        applyReason: data.applyReason,
        scheduleTime: data.scheduleTime,
        status: data.status,
        reviewRemark: data.reviewRemark
      });
      if (res.code === 200) {
        return res.data;
      } else {
        throw new Error(res.message || '修改服务订单失败');
      }
    } catch (error) {
      console.error('修改服务订单失败:', error);
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
  const completeOrder = async (orderId, duration = 0, fee = 0) => {
    try {
      const res = await completeServiceOrder({
        id: orderId,
        duration,
        fee
      });
      // 返回完整响应对象
      return res;
    } catch (error) {
      console.error('完成服务失败:', error);
      throw error;
    }
  };

  // 删除服务订单
  const deleteOrder = async (orderIds) => {
    try {
      // 如果是数组，转为逗号分隔的字符串
      const ids = Array.isArray(orderIds) ? orderIds.join(',') : orderIds;
      const res = await axios.delete(`/api/service/order/${ids}`);
      if (res.code === 200) {
        return res.data;
      } else {
        throw new Error(res.message || '删除服务订单失败');
      }
    } catch (error) {
      console.error('删除服务订单失败:', error);
      throw error;
    }
  };

  // 导出服务订单
  const exportList = async (params) => {
    try {
      return await exportServiceOrder(params);
    } catch (error) {
      console.error('导出服务订单失败:', error);
      throw error;
    }
  };

  // 更新订单状态（状态流转）
  const updateOrderStatus = async (orderId, status) => {
    try {
      if (!orderId) {
        throw new Error('订单ID不能为空');
      }
      
      let res;
      
      // 根据目标状态调用不同的API
      switch (status) {
        case '1': // 已派单
          res = await reviewServiceOrder(orderId, { status: 1, reviewRemark: '已派单' });
          break;
        case '2': // 服务中
          res = await startServiceOrder(orderId);
          break;
        case '3': // 已完成
          res = await completeOrder(orderId, 0, 0); // duration=0, fee=0
          break;
        case '4': // 已取消
          res = await reviewServiceOrder(orderId, { status: 4, reviewRemark: '已取消' });
          break;
        case '5': // 已拒绝
          res = await reviewServiceOrder(orderId, { status: 5, reviewRemark: '已拒绝' });
          break;
        default:
          throw new Error('不支持的状态值');
      }
      
      if (res.code === 200) {
        return res.data;
      } else {
        throw new Error(res.message || '更新订单状态失败');
      }
    } catch (error) {
      console.error('更新订单状态失败:', error);
      throw error;
    }
  };

  // 取消订单
  const cancelOrder = async (orderIds) => {
    try {
      // 如果是数组，转为逗号分隔的字符串
      const ids = Array.isArray(orderIds) ? orderIds.join(',') : orderIds;
      const res = await axios.delete(`/api/service/order/${ids}`);
      if (res.code === 200) {
        return res.data;
      } else {
        throw new Error(res.message || '取消服务订单失败');
      }
    } catch (error) {
      console.error('取消服务订单失败:', error);
      throw error;
    }
  };

  return {
    orderList,
    total,
    loading,
    statusOptions,
    getList: getOrderList,
    getOrderDetail,
    addOrder,
    updateOrder,
    reviewOrder,
    assignOrder,
    startOrder,
    completeOrder,
    deleteOrder,
    exportList,
    updateOrderStatus,
    cancelOrder,
    resetState,
    getStatusDict
  };
});