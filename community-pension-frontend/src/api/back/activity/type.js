import axios from '@/utils/axios';

/**
 * 获取活动类型列表
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页条数
 * @returns {Promise<{
 *   code: number,
 *   data: {
 *     records: Array<{
 *       id: number,
 *       name: string,
 *       code: string,
 *       sort: number,
 *       status: number,
 *       createTime: string
 *     }>,
 *     total: number
 *   },
 *   msg: string
 * }>}
 */
export const getList = (params) => {
    return axios.get('/api/activity/type/list', { params });
};

/**
 * 获取所有活动类型(不分页)
 * @returns {Promise<{
 *   code: number,
 *   data: Array<{
 *     id: number,
 *     name: string,
 *     code: string
 *   }>,
 *   msg: string
 * }>}
 */
export const getAllTypes = () => {
    return axios.get('/api/activity/type/all');
};

/**
 * 创建活动类型
 * @param {Object} data - 活动类型数据
 * @param {string} data.name - 类型名称
 * @param {string} data.code - 类型编码
 * @param {number} data.sort - 排序号
 * @returns {Promise<{
 *   code: number,
 *   msg: string
 * }>}
 */
export const create = (data) => {
    return axios.post('/api/activity/type', data);
};

/**
 * 更新活动类型
 * @param {string|number} id - 类型ID
 * @param {Object} data - 活动类型数据
 * @param {string} data.name - 类型名称
 * @param {string} data.code - 类型编码
 * @param {number} data.sort - 排序号
 * @returns {Promise<{
 *   code: number,
 *   msg: string
 * }>}
 */
export const update = (id, data) => {
    return axios.put(`/api/activity/type/${id}`, data);
};

/**
 * 删除活动类型
 * @param {string|number} id - 类型ID
 * @returns {Promise<{
 *   code: number,
 *   msg: string
 * }>}
 */
export const remove = (id) => {
    return axios.delete(`/api/activity/type/${id}`);
};

/**
 * 更新活动类型状态
 * @param {string|number} id - 类型ID
 * @param {number} status - 状态值
 * @returns {Promise<{
 *   code: number,
 *   msg: string
 * }>}
 */
export const updateStatus = (id, status) => {
    return axios.put(`/api/activity/type/${id}/status`, { status });
};

// 获取活动类型详情
export const getActivityTypeDetail = (id) => {
    return axios.get(`/api/activity/type/detail/${id}`);
}; 