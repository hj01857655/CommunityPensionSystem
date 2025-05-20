import {
  auditServiceReview,
  deleteServiceReview,
  getServiceReviewDetail,
  getServiceReviewList,
  replyServiceReview
} from '@/api/back/service/review';
import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useServiceReviewStore = defineStore('serviceReview', () => {
  // 状态
  const reviewList = ref([]);
  const total = ref(0);
  const loading = ref(false);

  // 重置状态
  const resetState = () => {
    reviewList.value = [];
    total.value = 0;
    loading.value = false;
  };

  // 获取服务评价列表
  const getReviewList = async (params) => {
    loading.value = true;
    try {
      // 只传递后端支持的参数
      const apiParams = {
        current: params.current,
        size: params.size,
        serviceId: params.serviceId,
        status: params.status
      };
      
      const res = await getServiceReviewList(apiParams);
      if (res.code === 200 && res.data && res.data.records) {
        reviewList.value = res.data.records;
        total.value = Number(res.data.total) || 0;
        return res.data;
      } else {
        console.error('获取服务评价列表格式错误:', res);
        reviewList.value = [];
        total.value = 0;
        throw new Error(res.msg || '获取服务评价列表失败或数据格式错误');
      }
    } catch (error) {
      console.error('获取服务评价列表失败:', error);
      reviewList.value = [];
      total.value = 0;
      return Promise.reject(error);
    } finally {
      loading.value = false;
    }
  };

  // 获取服务评价详情
  const getReviewDetail = async (reviewId) => {
    try {
      const res = await getServiceReviewDetail(reviewId);
      if (res.code === 200) {
        return res.data;
      } else {
        throw new Error(res.message || '获取服务评价详情失败');
      }
    } catch (error) {
      console.error('获取服务评价详情失败:', error);
      throw error;
    }
  };

  // 回复服务评价
  const replyReview = async (replyData) => {
    try {
      const res = await replyServiceReview(replyData);
      if (res.code === 200) {
        return res;
      } else {
        throw new Error(res.msg || '回复服务评价失败');
      }
    } catch (error) {
      console.error('回复服务评价失败:', error);
      return Promise.reject(error);
    }
  };

  // 审核服务评价
  const auditReview = async (auditData) => {
    try {
      const res = await auditServiceReview(auditData);
      if (res.code === 200) {
        return res;
      } else {
        throw new Error(res.msg || '审核服务评价失败');
      }
    } catch (error) {
      console.error('审核服务评价失败:', error);
      return Promise.reject(error);
    }
  };

  // 删除服务评价
  const deleteReview = async (reviewId) => {
    try {
      const res = await deleteServiceReview(reviewId);
      if (res.code === 200) {
        return res;
      } else {
        throw new Error(res.msg || '删除服务评价失败');
      }
    } catch (error) {
      console.error('删除服务评价失败:', error);
      return Promise.reject(error);
    }
  };

  return {
    reviewList,
    total,
    loading,
    getReviewList,
    getReviewDetail,
    replyReview,
    auditReview,
    deleteReview,
    resetState
  };
});