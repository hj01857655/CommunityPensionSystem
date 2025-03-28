import axios from '@/utils/axios';

/**
 * 获取通知公告列表
 * @param {Object} params - 查询参数
 * @returns {Promise<Object>}
 */
export const getNoticeList = (params) => {
  return axios.get('/api/notice/list', { params })
    .catch(error => {
      console.error('获取通知公告列表失败:', error);
      return Promise.reject(error);
    });
};

/**
 * 获取通知公告详情
 * @param {number} id - 通知公告ID
 * @returns {Promise<Object>}
 */
export const getNoticeDetail = (id) => {
  return axios.get(`/api/notice/${id}`)
    .catch(error => {
      console.error('获取通知公告详情失败:', error);
      return Promise.reject(error);
    });
};

/**
 * 新增通知公告
 * @param {Object} data - 通知公告数据
 * @returns {Promise<Object>}
 */
export const addNotice = (data) => {
  return axios.post('/api/notices', data)
    .catch(error => {
      console.error('新增通知公告失败:', error);
      return Promise.reject(error);
    });
};

/**
 * 更新通知公告
 * @param {Object} data - 通知公告数据
 * @returns {Promise<Object>}
 */
export const updateNotice = (data) => {
  return axios.put(`/api/notice/${data.id}`, data)
    .catch(error => {
      console.error('更新通知公告失败:', error);
      return Promise.reject(error);
    });
};

/**
 * 删除通知公告
 * @param {number} id - 通知公告ID
 * @returns {Promise<Object>}
 */
export const deleteNotice = (id) => {
  return axios.delete(`/api/notice/${id}`)
    .catch(error => {
      console.error('删除通知公告失败:', error);
      return Promise.reject(error);
    });
};

/**
 * 发布通知公告
 * @param {number} id - 通知公告ID
 * @returns {Promise<Object>}
 */
export const publishNotice = (id) => {
  return axios.put(`/api/notice/publish/${id}`)
    .catch(error => {
      console.error('发布通知公告失败:', error);
      return Promise.reject(error);
    });
};

/**
 * 撤回通知公告
 * @param {number} id - 通知公告ID
 * @returns {Promise<Object>}
 */
export const withdrawNotice = (id) => {
  return axios.put(`/api/notice/withdraw/${id}`)
    .catch(error => {
      console.error('撤回通知公告失败:', error);
      return Promise.reject(error);
    });
};

/**
 * 置顶/取消置顶通知公告
 * @param {number} id - 通知公告ID
 * @param {boolean} isTop - 是否置顶
 * @returns {Promise<Object>}
 */
export const toggleNoticeTop = (id, isTop) => {
  return axios.put(`/api/notice/top/${id}`, { isTop })
    .catch(error => {
      console.error('更新通知公告置顶状态失败:', error);
      return Promise.reject(error);
    });
}; 