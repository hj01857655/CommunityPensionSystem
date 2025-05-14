import axios from '@/utils/axios'

/**
 * 分页获取体检报告列表
 * @param {Object} params - 查询参数 { elderId, pageNum, pageSize }
 * @param {Object} headers - 请求头 { 'user-id': string, 'role': string }
 * @returns {Promise}
 */
export const getReportList = (params, headers) => {
  return axios.get('/api/physicalExamReport/page', { params, headers })
    .catch(error => {
      console.error('Error fetching physical exam report list:', error)
      return Promise.reject(error)
    })
}

/**
 * 获取体检报告详情
 * @param {number|string} id - 体检报告ID
 * @param {Object} headers - 请求头 { 'user-id': string, 'role': string }
 * @returns {Promise}
 */
export const getReportDetail = (id, headers) => {
  return axios.get(`/api/physicalExamReport/${id}`, { headers })
    .catch(error => {
      console.error('Error fetching physical exam report detail:', error)
      return Promise.reject(error)
    })
}

/**
 * 新增体检报告
 * @param {Object} data - 体检报告数据
 * @param {Object} headers - 请求头 { 'user-id': string, 'role': string }
 * @returns {Promise}
 */
export const createReport = (data, headers) => {
  return axios.post('/api/physicalExamReport/add', data, { headers })
    .catch(error => {
      console.error('Error creating physical exam report:', error)
      return Promise.reject(error)
    })
}

/**
 * 更新体检报告
 * @param {Object} data - 体检报告数据
 * @param {Object} headers - 请求头 { 'user-id': string, 'role': string }
 * @returns {Promise}
 */
export const updateReport = (data, headers) => {
  return axios.put('/api/physicalExamReport/update', data, { headers })
    .catch(error => {
      console.error('Error updating physical exam report:', error)
      return Promise.reject(error)
    })
}

/**
 * 删除体检报告
 * @param {number|string} id - 体检报告ID
 * @param {Object} headers - 请求头 { 'user-id': string, 'role': string }
 * @returns {Promise}
 */
export const deleteReport = (id, headers) => {
  return axios.delete(`/api/physicalExamReport/${id}`, { headers })
    .catch(error => {
      console.error('Error deleting physical exam report:', error)
      return Promise.reject(error)
    })
}

/**
 * 上传体检报告文件
 * @param {FormData} formData - 文件表单数据
 * @returns {Promise}
 */
export const uploadFile = (formData) => {
  return axios.post('/api/physicalExamReport/upload', formData, {
    headers: {
      // 不设置Content-Type，让浏览器自动设置带boundary的multipart/form-data
    }
  })
    .catch(error => {
      console.error('体检报告文件上传失败:', error)
      return Promise.reject(error)
    })
} 