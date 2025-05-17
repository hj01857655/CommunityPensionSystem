import axios from '@/utils/axios'; // 使用封装的 Axios 实例

// 获取体检报告列表（支持条件搜索）
export const listPhysicalExamReports = (params) => {
  // 将前端查询参数转换为后端所需的字段
  const query = { ...params };
  if (query.userName !== undefined) {
    query.elderName = query.userName;
    delete query.userName;
  }
  if (query.beginTime !== undefined) {
    query.dateStart = query.beginTime;
    delete query.beginTime;
  }
  if (query.endTime !== undefined) {
    query.dateEnd = query.endTime;
    delete query.endTime;
  }
  // examType 后端暂未支持，删除
  delete query.examType;

  return axios.get('/api/physicalExamReport/search', { params: query })
    .catch(error => {
      console.error('Error fetching physical exam reports list:', error);
      return Promise.reject(error);
    });
};

// 获取体检报告详情
export const getPhysicalExamReport = (id) => {
  return axios.get(`/api/physicalExamReport/detail/${id}`)
    .catch(error => {
      console.error('Error fetching physical exam report details:', error);
      return Promise.reject(error);
    });
};

// 添加体检报告
export const addPhysicalExamReport = (data) => {
  return axios.post('/api/physicalExamReport/add', data)
    .catch(error => {
      console.error('Error adding physical exam report:', error);
      return Promise.reject(error);
    });
};

// 更新体检报告
export const updatePhysicalExamReport = (data) => {
  return axios.put('/api/physicalExamReport/update', data)
    .catch(error => {
      console.error('Error updating physical exam report:', error);
      return Promise.reject(error);
    });
};

// 删除体检报告
export const deletePhysicalExamReport = (id) => {
  return axios.delete(`/api/physicalExamReport/${id}`)
    .catch(error => {
      console.error('Error deleting physical exam report:', error);
      return Promise.reject(error);
    });
};

// 导出体检报告数据
export const exportPhysicalExamReports = (params) => {
  return axios.get('/api/physicalExamReport/export', { 
    params,
    responseType: 'blob'
  })
    .catch(error => {
      console.error('Error exporting physical exam reports:', error);
      return Promise.reject(error);
    });
};
