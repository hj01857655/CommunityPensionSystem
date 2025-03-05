import axiosInstance from '@/utils/axios';

// 获取通知列表
export const getNoticeList = (params) => {
  return axiosInstance.get('/api/notices', { params });
};

// 获取通知详情
export const getNoticeDetail = (id) => {
  return axiosInstance.get(`/api/notices/${id}`);
};

// 创建通知
export const createNotice = (data) => {
  return axiosInstance.post('/api/notices', data);
};

// 更新通知
export const updateNotice = (id, data) => {
  return axiosInstance.put(`/api/notices/${id}`, data);
};

// 删除通知
export const deleteNotice = (id) => {
  return axiosInstance.delete(`/api/notices/${id}`);
};

// 批量删除通知
export const batchDeleteNotices = (ids) => {
  return axiosInstance.delete('/api/notices/batch', { data: { ids } });
};

// 更新通知状态（发布、过期等）
export const updateNoticeStatus = (id, status) => {
  return axiosInstance.patch(`/api/notices/${id}/status`, { status });
};

// 更新置顶状态
export const updateNoticeTopStatus = (id, isTop) => {
  return axiosInstance.patch(`/api/notices/${id}/top`, { isTop });
};

// 批量更新通知状态
export const batchUpdateNoticeStatus = (ids, status) => {
  return axiosInstance.patch('/api/notices/batch/status', { ids, status });
};