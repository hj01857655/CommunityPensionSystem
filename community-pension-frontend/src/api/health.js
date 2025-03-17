import axios from 'axios';

// 获取健康记录列表
export const getHealthRecords = (params) => {
    return axios.get('/api/health-records/getHealthRecords', { params });
};

// 添加健康记录
export const addHealthRecord = (data) => {
    return axios.post('/api/health-records/addHealthRecords', data);
};

// 更新健康记录
export const updateHealthRecord = (data) => {
    return axios.put('/api/health-records/updateHealthRecords', data);
};

// 删除健康记录
export const deleteHealthRecord = (id) => {
    return axios.delete(`/api/health-records/${id}`);
};
