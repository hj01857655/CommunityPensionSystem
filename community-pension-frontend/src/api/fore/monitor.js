import axios from '@/utils/axios';

// 获取健康监测记录列表（分页）
export const getHealthMonitors = (params) => {
  return axios.get('/api/health/monitor/list', { params })
    .catch(error => {
      console.error('Error fetching health monitors:', error);
      return Promise.reject(error);
    });
};

// 获取健康监测记录详情
export const getHealthMonitorDetail = (id) => {
  return axios.get(`/api/health/monitor/${id}`)
    .catch(error => {
      console.error('Error fetching health monitor detail:', error);
      return Promise.reject(error);
    });
};

// 添加健康监测记录
export const addHealthMonitorRecord = (data) => {
  return axios.post('/api/health/monitor', data)
    .catch(error => {
      console.error('Error adding health monitor record:', error);
      return Promise.reject(error);
    });
};

// 更新健康监测记录
export const updateHealthMonitorRecord = (data) => {
  return axios.put('/api/health/monitor', data)
    .catch(error => {
      console.error('Error updating health monitor record:', error);
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

// 获取异常健康监测记录（如后端有实现可补充路径）
export const getAbnormalHealthMonitors = () => {
  return axios.get('/api/health/monitor/abnormal')
    .catch(error => {
      console.error('Error fetching abnormal health monitors:', error);
      return Promise.reject(error);
    });
};

// 获取健康监测统计数据（如后端有实现可补充路径）
export const getHealthMonitorStats = (elderId, params = {}) => {
  return axios.get(`/api/health/monitor/stats/${elderId}`, { params })
    .catch(error => {
      console.error('Error fetching health monitor stats:', error);
      return Promise.reject(error);
    });
};