import axiosInstance from '@/utils/axios';

// 获取服务类型列表
export const getServiceTypeList = (params) => {
  return axiosInstance.get('/api/service-types', { params });
};

// 获取服务类型详情
export const getServiceTypeDetail = (id) => {
  return axiosInstance.get(`/api/service-types/${id}`);
};

// 创建服务类型
export const createServiceType = (data) => {
  return axiosInstance.post('/api/service-types', data);
};

// 更新服务类型
export const updateServiceType = (id, data) => {
  return axiosInstance.put(`/api/service-types/${id}`, data);
};

// 删除服务类型
export const deleteServiceType = (id) => {
  return axiosInstance.delete(`/api/service-types/${id}`);
};

// 获取预约记录列表
export const getAppointmentList = (params) => {
  return axiosInstance.get('/api/appointments', { params });
};

// 获取预约记录详情
export const getAppointmentDetail = (id) => {
  return axiosInstance.get(`/api/appointments/${id}`);
};

// 创建预约记录
export const createAppointment = (data) => {
  return axiosInstance.post('/api/appointments', data);
};

// 更新预约记录
export const updateAppointment = (id, data) => {
  return axiosInstance.put(`/api/appointments/${id}`, data);
};

// 取消预约
export const cancelAppointment = (id) => {
  return axiosInstance.patch(`/api/appointments/${id}/cancel`);
};

// 完成预约
export const completeAppointment = (id) => {
  return axiosInstance.patch(`/api/appointments/${id}/complete`);
};

// 获取服务评价列表
export const getEvaluationList = (params) => {
  return axiosInstance.get('/api/evaluations', { params });
};

// 获取服务评价详情
export const getEvaluationDetail = (id) => {
  return axiosInstance.get(`/api/evaluations/${id}`);
};

// 创建服务评价
export const createEvaluation = (data) => {
  return axiosInstance.post('/api/evaluations', data);
};

// 删除服务评价
export const deleteEvaluation = (id) => {
  return axiosInstance.delete(`/api/evaluations/${id}`);
};

// 回复服务评价
export const replyEvaluation = (id, data) => {
  return axiosInstance.post(`/api/evaluations/${id}/reply`, data);
};

// 获取服务统计数据
export const getServiceStatistics = () => {
  return axiosInstance.get('/api/service-statistics');
};