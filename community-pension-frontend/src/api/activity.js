import axiosInstance from '@/utils/axios';

// 获取活动列表
export const getActivityList = (params) => {
  return axiosInstance.get('/api/activities', { params });
};

// 获取活动详情
export const getActivityDetail = (id) => {
  return axiosInstance.get(`/api/activities/${id}`);
};

// 创建活动
export const createActivity = (data) => {
  return axiosInstance.post('/api/activities', data);
};

// 更新活动
export const updateActivity = (id, data) => {
  return axiosInstance.put(`/api/activities/${id}`, data);
};

// 删除活动
export const deleteActivity = (id) => {
  return axiosInstance.delete(`/api/activities/${id}`);
};

// 批量删除活动
export const batchDeleteActivities = (ids) => {
  return axiosInstance.delete('/api/activities/batch', { data: { ids } });
};

// 活动报名
export const registerActivity = (activityId, data) => {
  return axiosInstance.post(`/api/activities/${activityId}/register`, data);
};

// 取消报名
export const cancelRegistration = (activityId) => {
  return axiosInstance.delete(`/api/activities/${activityId}/register`);
};

// 获取活动报名列表
export const getActivityRegistrations = (activityId, params) => {
  return axiosInstance.get(`/api/activities/${activityId}/registrations`, { params });
};

// 审核报名
export const reviewRegistration = (activityId, registrationId, data) => {
  return axiosInstance.patch(`/api/activities/${activityId}/registrations/${registrationId}/review`, data);
};

// 批量审核报名
export const batchReviewRegistrations = (activityId, data) => {
  return axiosInstance.patch(`/api/activities/${activityId}/registrations/batch/review`, data);
};

// 导出报名数据
export const exportRegistrations = (activityId) => {
  return axiosInstance.get(`/api/activities/${activityId}/registrations/export`, {
    responseType: 'blob'
  });
};