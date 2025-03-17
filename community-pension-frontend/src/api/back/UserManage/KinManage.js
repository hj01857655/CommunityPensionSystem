import axios from '@/utils/axios';

/**
 * 亲属信息管理相关接口
 */

/**
 * 获取亲属列表（带分页和查询条件）
 * @param {Object} params - 查询参数
 * @param {number} params.current - 页码
 * @param {number} params.size - 每页数量
 * @param {string} [params.query] - 搜索关键词
 * @param {string} [params.sortBy] - 排序字段
 * @param {string} [params.order] - 排序方式：asc/desc
 * @returns {Promise<{code: number, data: Object, message: string}>}
 */
export const getKinList = async (params) => {
    try {
        const response = await axios.get('/api/kins', { params });
        return response;
    } catch (error) {
        console.error('获取亲属列表失败:', error);
        throw error;
    }
};

/**
 * 根据ID获取亲属信息
 * @param {string|number} id - 亲属ID
 * @returns {Promise<{code: number, data: Object, message: string}>}
 */
export const getKinById = async (id) => {
    try {
        const response = await axios.get(`/api/kins/${id}`);
        return response;
    } catch (error) {
        console.error('获取亲属信息失败:', error);
        throw error;
    }
};

/**
 * 创建亲属信息
 * @param {Object} data - 亲属信息
 * @returns {Promise<{code: number, data: Object, message: string}>}
 */
export const addKin = async (data) => {
    try {
        const response = await axios.post('/api/kins', data);
        return response;
    } catch (error) {
        console.error('创建亲属信息失败:', error);
        throw error;
    }
};

/**
 * 更新亲属信息
 * @param {Object} data - 亲属信息
 * @param {string|number} data.id - 亲属ID
 * @returns {Promise<{code: number, data: Object, message: string}>}
 */
export const updateKin = async (data) => {
    try {
        const response = await axios.put(`/api/kins/${data.id}`, data);
        return response.data;
    } catch (error) {
        console.error('更新亲属信息失败:', error);
        throw error;
    }
};

/**
 * 删除亲属信息
 * @param {string|number} id - 亲属ID
 * @returns {Promise<{code: number, data: Object, message: string}>}
 */
export const deleteKin = async (id) => {
    try {
        const response = await axios.delete(`/api/kins/${id}`);
        return response;
    } catch (error) {
        console.error('删除亲属信息失败:', error);
        throw error;
    }
};

/**
 * 批量删除亲属信息
 * @param {Array<string|number>} ids - 亲属ID数组
 * @returns {Promise<{code: number, data: Object, message: string}>}
 */
export const batchDeleteKins = async (ids) => {
    try {
        const response = await axios.delete('/api/kins/batch', { data: { ids } });
        return response;
    } catch (error) {
        console.error('批量删除亲属信息失败:', error);
        throw error;
    }
};

/**
 * 根据老人ID获取亲属列表
 * @param {string|number} elderId - 老人ID
 * @returns {Promise<{code: number, data: Object, message: string}>}
 */
export const getElderByElderId = async (elderId) => {
    if (!elderId) {
        throw new Error('请求参数错误');
    }
    
    try {
        const response = await axios.get(`/api/elders/${elderId}`);
        console.log('获取亲属绑定的老人', response);
        return response;
    } catch (error) {
        console.error('获取亲属绑定的老人失败:', error);
        throw error;
    }
};

/**
 * 获取所有亲属（不分页）
 * @returns {Promise<{code: number, data: Array, message: string}>}
 */
export const getAllKins = async () => {
    try {
        const response = await axios.get('/api/kins/all');
        return response;
    } catch (error) {
        console.error('获取所有亲属失败:', error);
        throw error;
    }
};

// 绑定家属与老人
export const bindKinToElder = async (kinId, elderId) => {
  try {
    const response = await axios.post(`/api/kins/bind/${kinId}/${elderId}`);
    return response.data;
  } catch (error) {
    console.error('绑定家属与老人失败:', error);
    throw error;
  }
};

// 解绑家属与老人
export const unbindKinFromElder = async (kinId) => {
  try {
    const response = await axios.delete(`/api/kins/unbind/${kinId}`);
    return response.data;
  } catch (error) {
    console.error('解绑家属与老人失败:', error);
    throw error;
  }
};