import axios from '@/utils/axios';

// 获取服务列表
export const getServiceList = async () => {
  try {
    const response = await axios.get('/api/services');
    return response;
  } catch (error) {
    console.error('获取服务列表失败:', error);
    throw error;
  }
};

// 获取服务详情
export function getServiceDetail(id) {
  return axios.get(`/api/services/${id}`);
}

// 创建服务预约
export function createServiceAppointment(data) {
  return axios.post('/api/service-appointments', data);
}

// 获取我的服务预约列表
export function getMyServiceAppointments(params) {
  return axios.get('/api/service-appointments/my', { params });
}

// 取消服务预约
export function cancelServiceAppointment(id) {
  return axios.put(`/api/service-appointments/${id}/cancel`);
}

// 评价服务
export function evaluateService(id, data) {
  return axios.post(`/api/service-appointments/${id}/evaluate`, data);
}