import { defineStore } from 'pinia';
import { 
  getServiceItemList,
  getServiceItemDetail,
  createServiceItem,
  updateServiceItem,
  deleteServiceItem,
  updateServiceItemStatus
} from '@/api/back/service/item';
import { 
  getServiceOrderList,
  getServiceOrderDetail,
  createServiceOrder,
  reviewServiceOrder,
  assignServiceOrder,
  startServiceOrder,
  completeServiceOrder
} from '@/api/back/service/order';
import { 
  getServiceReviewList,
  getServiceReviewDetail,
  replyServiceReview,
  getServiceAverageRating
} from '@/api/back/service/review';

export const useServiceStore = defineStore('service', {
  state: () => ({
    // 服务项目相关
    serviceItems: [],
    serviceItemTotal: 0,
    currentServiceItem: null,
    
    // 服务工单相关
    serviceOrders: [],
    serviceOrderTotal: 0,
    currentServiceOrder: null,
    
    // 服务评价相关
    serviceReviews: [],
    serviceReviewTotal: 0,
    currentServiceReview: null,
    
    // 加载状态
    loading: false,
    
    // 查询参数
    queryParams: {
      pageNum: 1,
      pageSize: 10
    }
  }),
  
  actions: {
    // 服务项目相关操作
    async getServiceItems(params) {
      this.loading = true;
      try {
        const { data } = await getServiceItemList(params);
        this.serviceItems = data.records;
        this.serviceItemTotal = data.total;
      } finally {
        this.loading = false;
      }
    },
    
    async getServiceItemDetail(id) {
      this.loading = true;
      try {
        const { data } = await getServiceItemDetail(id);
        this.currentServiceItem = data;
        return data;
      } finally {
        this.loading = false;
      }
    },
    
    async createServiceItem(data) {
      return await createServiceItem(data);
    },
    
    async updateServiceItem(data) {
      return await updateServiceItem(data);
    },
    
    async deleteServiceItem(ids) {
      return await deleteServiceItem(ids);
    },
    
    async updateServiceItemStatus(data) {
      return await updateServiceItemStatus(data);
    },
    
    // 服务工单相关操作
    async getServiceOrders(params) {
      this.loading = true;
      try {
        const { data } = await getServiceOrderList(params);
        this.serviceOrders = data.records;
        this.serviceOrderTotal = data.total;
      } finally {
        this.loading = false;
      }
    },
    
    async getServiceOrderDetail(id) {
      this.loading = true;
      try {
        const { data } = await getServiceOrderDetail(id);
        this.currentServiceOrder = data;
        return data;
      } finally {
        this.loading = false;
      }
    },
    
    async createServiceOrder(data) {
      return await createServiceOrder(data);
    },
    
    async reviewServiceOrder(data) {
      return await reviewServiceOrder(data);
    },
    
    async assignServiceOrder(id) {
      return await assignServiceOrder(id);
    },
    
    async startServiceOrder(id) {
      return await startServiceOrder(id);
    },
    
    async completeServiceOrder(data) {
      return await completeServiceOrder(data);
    },
    
    // 服务评价相关操作
    async getServiceReviews(params) {
      this.loading = true;
      try {
        const { data } = await getServiceReviewList(params);
        this.serviceReviews = data.records;
        this.serviceReviewTotal = data.total;
      } finally {
        this.loading = false;
      }
    },
    
    async getServiceReviewDetail(id) {
      this.loading = true;
      try {
        const { data } = await getServiceReviewDetail(id);
        this.currentServiceReview = data;
        return data;
      } finally {
        this.loading = false;
      }
    },
    
    async replyServiceReview(data) {
      return await replyServiceReview(data);
    },
    
    async getServiceAverageRating(serviceId) {
      const { data } = await getServiceAverageRating(serviceId);
      return data;
    },
    
    // 重置状态
    resetState() {
      this.serviceItems = [];
      this.serviceItemTotal = 0;
      this.currentServiceItem = null;
      this.serviceOrders = [];
      this.serviceOrderTotal = 0;
      this.currentServiceOrder = null;
      this.serviceReviews = [];
      this.serviceReviewTotal = 0;
      this.currentServiceReview = null;
      this.loading = false;
      this.queryParams = {
        pageNum: 1,
        pageSize: 10
      };
    }
  }
}); 