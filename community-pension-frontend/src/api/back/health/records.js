import axios from '@/utils/axios'; // 使用您封装的 Axios 实例

// 获取健康档案
export const getHealthRecords = (elderId) => {
    return axios.get('/api/health-records/getHealthRecords', {
        params: { elderId }
    }).catch(error => {
        console.error('Error fetching health records:', error);
        throw error;
    });
};

// 添加健康档案
export const addHealthRecord = (data) => {
    return axios.post('/api/health-records/addHealthRecords', data)
        .catch(error => {
            console.error('Error adding health record:', error);
            throw error;
        });
};

// 更新健康档案
export const updateHealthRecord = (data) => {
    return axios.put('/api/health-records/updateHealthRecords', data)
        .catch(error => {
            console.error('Error updating health record:', error);
            throw error;
        });
};

// 删除健康档案
export const deleteHealthRecord = (id) => {
    return axios.delete(`/api/health-records/${id}`)
        .catch(error => {
            console.error('Error deleting health record:', error);
            throw error;
        });
};

// 获取所有健康档案
export const listHealthRecords = (page, size) => {
    return axios.get('/api/health-records/list', {
        params: { page, size }
    }).catch(error => {
        console.error('Error fetching health records list:', error);
        throw error;
    });
};

// 获取所有老人列表(用于下拉选择)
export const getElders = () => {
    return axios.get('/api/elders/list');
};