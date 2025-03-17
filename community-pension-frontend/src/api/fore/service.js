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
  return request({
    url: `/api/services/${id}`,
    method: 'get'
  });
}

// 创建服务预约
export function createServiceAppointment(data) {
  return request({
    url: '/api/service-appointments',
    method: 'post',
    data
  });
}

// 获取我的服务预约列表
export function getMyServiceAppointments(params) {
  return request({
    url: '/api/service-appointments/my',
    method: 'get',
    params
  });
}

// 取消服务预约
export function cancelServiceAppointment(id) {
  return request({
    url: `/api/service-appointments/${id}/cancel`,
    method: 'put'
  });
}

// 评价服务
export function evaluateService(id, data) {
  return request({
    url: `/api/service-appointments/${id}/evaluate`,
    method: 'post',
    data
  });
}