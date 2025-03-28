import axios from '@/utils/axios';

// 获取活动类型列表
export const getActivityTypeList = (params) => {
    return axios.get('/api/activity/type/list', { params });
};

// 添加活动类型
export const addActivityType = (data) => {
    return axios.post('/api/activity/type/add', data);
};

// 更新活动类型
export const updateActivityType = (data) => {
    return axios.put(`/api/activity/type/update/${data.id}`, data);
};

// 删除活动类型
export const deleteActivityType = (id) => {
    return axios.delete(`/api/activity/type/delete/${id}`);
};

// 获取活动类型详情
export const getActivityTypeDetail = (id) => {
    return axios.get(`/api/activity/type/detail/${id}`);
}; 