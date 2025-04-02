import { defineStore } from 'pinia';
import { ref } from 'vue';
import { 
  getServiceReviewList,
  getServiceReviewDetail,
  replyServiceReview,
  getServiceAverageRating
} from '@/api/back/service/review';

export const useServiceReviewStore = defineStore('serviceReview', () => {
  // 状态
  const reviewList = ref([]);
  const total = ref(0);
  const loading = ref(false);
  const averageRating = ref(0);

  // 重置状态
  const resetState = () => {
    reviewList.value = [];
    total.value = 0;
    loading.value = false;
    averageRating.value = 0;
  };

  // 获取服务评价列表
  const getReviewList = async (params) => {
    loading.value = true;
    try {
      const res = await getServiceReviewList(params);
      if (res.code === 200) {
        reviewList.value = res.data.data.records;
        total.value = res.data.data.total;
        return res.data.data;
      } else {
        throw new Error(res.message || '获取服务评价列表失败');
      }
    } catch (error) {
      console.error('获取服务评价列表失败:', error);
      throw error;
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
  const replyReview = async (reviewId, data) => {
    try {
      const res = await replyServiceReview(reviewId, data);
      if (res.code === 200) {
        return res.data;
      } else {
        throw new Error(res.message || '回复服务评价失败');
      }
    } catch (error) {
      console.error('回复服务评价失败:', error);
      throw error;
    }
  };

  // 获取服务平均评分
  const getAverageRating = async (serviceId) => {
    try {
      const res = await getServiceAverageRating(serviceId);
      if (res.code === 200) {
        averageRating.value = res.data;
        return res.data;
      } else {
        throw new Error(res.message || '获取服务平均评分失败');
      }
    } catch (error) {
      console.error('获取服务平均评分失败:', error);
      throw error;
    }
  };

  return {
    reviewList,
    total,
    loading,
    averageRating,
    getReviewList,
    getReviewDetail,
    replyReview,
    getAverageRating,
    resetState
  };
}); 