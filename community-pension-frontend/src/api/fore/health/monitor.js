import axios from '@/utils/axios';

// 获取健康监测记录列表
export const getHealthMonitors = (params) => {
  return axios.get('/api/health-monitors/user', { params })
    .catch(error => {
      console.error('Error fetching health monitors:', error);
      return Promise.reject(error);
    });
};

// 获取健康监测记录详情
export const getHealthMonitorDetail = (id) => {
  return axios.get(`/api/health-monitors/user/${id}`)
    .catch(error => {
      console.error('Error fetching health monitor detail:', error);
      return Promise.reject(error);
    });
};

// 添加健康监测记录
export const addHealthMonitorRecord = (data) => {
  return axios.post('/api/health-monitors/user', data)
    .catch(error => {
      console.error('Error adding health monitor record:', error);
      return Promise.reject(error);
    });
};

// 更新健康监测记录
export const updateHealthMonitorRecord = (data) => {
  return axios.put(`/api/health-monitors/user/${data.id}`, data)
    .catch(error => {
      console.error('Error updating health monitor record:', error);
      return Promise.reject(error);
    });
};

// 获取异常健康监测记录
export const getAbnormalHealthMonitors = () => {
  return axios.get('/api/health-monitors/user/abnormal')
    .catch(error => {
      console.error('Error fetching abnormal health monitors:', error);
      return Promise.reject(error);
    });
};

// 获取健康监测统计数据
export const getHealthMonitorStats = () => {
  return axios.get('/api/health-monitors/user/stats')
    .catch(error => {
      console.error('Error fetching health monitor stats:', error);
      return Promise.reject(error);
    });
};