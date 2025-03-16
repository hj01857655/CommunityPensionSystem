import axios from '@/utils/axios';

/**
 * 老人信息管理相关接口
 */

/**
 * 获取老人列表（带分页和查询条件）
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页数量
 * @param {string} [params.query] - 搜索关键词
 * @param {string} [params.sortBy] - 排序字段
 * @param {string} [params.order] - 排序方式：asc/desc
 * @returns {Promise<{code: number, data: Object, message: string}>}
 */
export const getElderList = async (params) => {
    try {
        const response = await axios.get('/api/elders/list', { params });
        return response;
    } catch (error) {
        console.error('获取老人列表失败:', error);
        throw error;
    }
};

/**
 * 根据ID获取老人信息
 * @param {string|number} id - 老人ID
 * @returns {Promise<{code: number, data: Object, message: string}>}
 */
export const getElderById = async (id) => {
    try {
        const response = await axios.get(`/api/elders/${id}`);
        return response;
    } catch (error) {
        console.error('获取老人信息失败:', error);
        throw error;
    }
};

/**
 * 创建老人信息
 * @param {Object} data - 老人信息
 * @returns {Promise<{code: number, data: Object, message: string}>}
 */
export const addElder = async (data) => {
    try {
        const response = await axios.post('/api/elders', data);
        return response;
    } catch (error) {
        console.error('创建老人信息失败:', error);
        throw error;
    }
};

/**
 * 更新老人信息
 * @param {Object} data - 老人信息
 * @param {string|number} data.id - 老人ID
 * @returns {Promise<{code: number, data: Object, message: string}>}
 */
export const updateElder = async (data) => {
    try {
        const response = await axios.put(`/api/elders/${data.id}`, data);
        return response;
    } catch (error) {
        console.error('更新老人信息失败:', error);
        throw error;
    }
};

/**
 * 删除老人信息
 * @param {string|number} id - 老人ID
 * @returns {Promise<{code: number, data: Object, message: string}>}
 */
export const deleteElder = async (id) => {
    try {
        const response = await axios.delete(`/api/elders/${id}`);
        return response;
    } catch (error) {
        console.error('删除老人信息失败:', error);
        throw error;
    }
};

/**
 * 批量删除老人信息
 * @param {Array<string|number>} ids - 老人ID数组
 * @returns {Promise<{code: number, data: Object, message: string}>}
 */
export const batchDeleteElders = async (ids) => {
    try {
        const response = await axios.delete('/api/elders/batch', { data: { ids } });
        return response;
    } catch (error) {
        console.error('批量删除老人信息失败:', error);
        throw error;
    }
};

/**
 * 更新老人状态
 * @param {string|number} id - 老人ID
 * @param {number} status - 状态值
 * @returns {Promise<{code: number, data: Object, message: string}>}
 */
export const updateElderStatus = async (id, status) => {
    try {
        const response = await axios.put(`/api/elders/${id}/status`, { status });
        return response;
    } catch (error) {
        console.error('更新老人状态失败:', error);
        throw error;
    }
};

/**
 * 获取老人健康记录
 * @param {string|number} elderId - 老人ID
 * @returns {Promise<{code: number, data: Object, message: string}>}
 */
export const getHealthRecords = async (elderId) => {
    try {
        const response = await axios.get(`/api/elders/${elderId}/health-records`);
        return response;
    } catch (error) {
        console.error('获取健康记录失败:', error);
        throw error;
    }
};

/**
 * 更新老人健康记录
 * @param {Object} data - 健康记录信息
 * @param {string|number} data.elderId - 老人ID
 * @returns {Promise<{code: number, data: Object, message: string}>}
 */
export const updateHealthRecords = async (data) => {
    try {
        const response = await axios.put(`/api/elders/${data.elderId}/health-records`, data);
        return response;
    } catch (error) {
        console.error('更新健康记录失败:', error);
        throw error;
    }
};

/**
 * 导出老人信息
 * @param {Object} params - 导出参数
 * @returns {Promise<Blob>}
 */
export const exportElders = async (params) => {
    try {
        const response = await axios.get('/api/elders/export', {
            params,
            responseType: 'blob'
        });
        return response;
    } catch (error) {
        console.error('导出老人信息失败:', error);
        throw error;
    }
};

/**
 * 导入老人信息
 * @param {File} file - 要导入的文件
 * @returns {Promise<{code: number, data: Object, message: string}>}
 */
export const importElders = async (file) => {
    try {
        const formData = new FormData();
        formData.append('file', file);
        const response = await axios.post('/api/elders/import', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
        return response;
    } catch (error) {
        console.error('导入老人信息失败:', error);
        throw error;
    }
};
