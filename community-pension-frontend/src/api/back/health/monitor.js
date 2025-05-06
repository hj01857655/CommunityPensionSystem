import axios from '@/utils/axios';

// 获取健康监测记录列表
export const listHealthMonitors = (params) => {
  return axios.get('/api/health/monitor/list', { params })
    .catch(error => {
      console.error('Error fetching health monitors list:', error);
      return Promise.reject(error);
    });
};

// 获取健康监测记录详情
export const getHealthMonitor = (id) => {
  return axios.get(`/api/health/monitor/${id}`)
    .catch(error => {
      console.error('Error fetching health monitor details:', error);
      return Promise.reject(error);
    });
};

// 添加健康监测记录
export const addHealthMonitor = (data) => {
  return axios.post('/api/health/monitor', data)
    .catch(error => {
      console.error('Error adding health monitor:', error);
      return Promise.reject(error);
    });
};

// 更新健康监测记录
export const updateHealthMonitor = (data) => {
  return axios.put(`/api/health/monitor`, data)
    .catch(error => {
      console.error('Error updating health monitor:', error);
      return Promise.reject(error);
    });
};

// 删除健康监测记录
export const deleteHealthMonitor = (id) => {
  return axios.delete(`/api/health/monitor/${id}`)
    .catch(error => {
      console.error('Error deleting health monitor:', error);
      return Promise.reject(error);
    });
};

// 导出健康监测数据
export const exportHealthMonitors = (params) => {
  return axios.get('/api/health/monitor/export', { 
    params,
    responseType: 'blob'
  })
    .catch(error => {
      console.error('Error exporting health monitors:', error);
      return Promise.reject(error);
    });
};

// 获取异常健康监测记录
export const getAbnormalHealthMonitors = (params) => {
  return axios.get('/api/health/monitor/abnormal', { params })
    .catch(error => {
      console.error('Error fetching abnormal health monitors:', error);
      return Promise.reject(error);
    });
};