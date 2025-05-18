import axios from '@/utils/axios'; // 使用您封装的 Axios 实例

// 获取健康档案详情
export const getHealthRecords = (id) => {
    return axios.get(`/api/health/record/${id}`)
    .catch(error => {
        console.error('Error fetching health record details:', error);
        throw error;
    });
};

// 添加健康档案
export const addHealthRecord = (data) => {
    return axios.post('/api/health/record/addHealthRecords', data)
        .catch(error => {
            console.error('Error adding health record:', error);
            throw error;
        });
};

// 更新健康档案
export const updateHealthRecord = (data) => {
    return axios.put('/api/health/record/updateHealthRecords', data)
        .catch(error => {
            console.error('Error updating health record:', error);
            throw error;
        });
};

// 删除健康档案
export const deleteHealthRecord = (id) => {
    return axios.delete(`/api/health/record/${id}`)
        .catch(error => {
            console.error('Error deleting health record:', error);
            throw error;
        });
};

// 获取所有健康档案
export const listHealthRecords = (page, size, params = {}) => {
    return axios.get('/api/health/record/list', {
        params: { 
            page, 
            size,
            ...params
        }
    }).catch(error => {
        console.error('Error fetching health records list:', error);
        throw error;
    });
};

// 获取所有老人列表(用于下拉选择)
export const getElders = () => {
    return axios.get('/api/system/user/elders');
};

// 导出健康记录
export const exportHealthRecords = (params = {}) => {
    return axios.get('/api/health/record/export', {
        params,
        responseType: 'blob'  // 指定响应类型为blob
    }).catch(error => {
        console.error('Error exporting health records:', error);
        throw error;
    });
};