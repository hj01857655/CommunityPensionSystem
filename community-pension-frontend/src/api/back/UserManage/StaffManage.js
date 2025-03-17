import axios from '@/utils/axios';

/**
 * 社区工作人员信息管理相关接口
 */

/**
 * 分页查询社区工作人员列表
 * @param {Object} params - 查询参数
 * @param {number} params.current - 页码
 * @param {number} params.size - 每页数量
 * @param {string} [params.name] - 姓名（可选）
 * @returns {Promise<{code: number, data: Object, message: string}>}
 */
export const getStaffList = async (params) => {
    try {
        const response = await axios.get('/api/staffs', { params });
        console.log('获取社区工作人员列表成功:', response.data);
        return response;
    } catch (error) {
        console.error('获取社区工作人员列表失败:', error);
        throw error;
    }
};

/**
 * 添加社区工作人员
 * @param {Object} staffData - 社区工作人员数据
 * @returns {Promise<{code: number, data: Object, message: string}>}
 */
export const addStaff = async (staffData) => {
    try {
        const response = await axios.post('/api/staffs', staffData);
        return response;
    } catch (error) {
        console.error('添加社区工作人员失败:', error);
        throw error;
    }
};

/**
 * 更新社区工作人员
 * @param {Object} staffData - 社区工作人员数据
 * @returns {Promise<{code: number, data: Object, message: string}>}
 */
export const updateStaff = async (staffData) => {
    try {
        const response = await axios.put(`/api/staffs/${staffData.id}`, staffData);
        return response;
    } catch (error) {
        console.error('更新社区工作人员失败:', error);
        throw error;
    }
};

/**
 * 删除社区工作人员
 * @param {number} staffId - 社区工作人员ID
 * @returns {Promise<{code: number, data: Object, message: string}>}
 */
export const deleteStaff = async (staffId) => {
    try {
        const response = await axios.delete(`/api/staffs/${staffId}`);
        return response;
    } catch (error) {
        console.error('删除社区工作人员失败:', error);
        throw error;
    }
};

/**
 * 获取所有社区工作人员
 * @returns {Promise<{code: number, data: Object[], message: string}>}
 */
export const getAllStaffs = async () => {
    try {
        const response = await axios.get('/api/staffs/all');
        return response;
    } catch (error) {
        console.error('获取所有社区工作人员失败:', error);
        throw error;
    }
};
