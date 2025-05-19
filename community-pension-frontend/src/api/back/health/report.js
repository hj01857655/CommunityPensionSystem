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
  return axios.delete(`/api/physicalExamReport/delete/${id}`)
    .catch(error => {
      console.error('Error deleting physical exam report:', error);
      return Promise.reject(error);
    });
};

// 获取体检项目列表
export const listExamItems = () => {
  return new Promise((resolve) => {
    // 由于后端API尚未实现，返回模拟数据
    const mockData = {
      code: 200,
      msg: "操作成功",
      data: [
        { 
          itemName: '血红蛋白', 
          itemUnit: 'g/L', 
          normalRange: '120-160',
          category: '血常规'
        },
        { 
          itemName: '白细胞计数', 
          itemUnit: '10^9/L', 
          normalRange: '4.0-10.0',
          category: '血常规'
        },
        { 
          itemName: '血小板计数', 
          itemUnit: '10^9/L', 
          normalRange: '100-300',
          category: '血常规'
        },
        { 
          itemName: '血糖', 
          itemUnit: 'mmol/L', 
          normalRange: '3.9-6.1',
          category: '生化'
        },
        { 
          itemName: '总胆固醇', 
          itemUnit: 'mmol/L', 
          normalRange: '3.1-5.7',
          category: '生化'
        },
        { 
          itemName: '甘油三酯', 
          itemUnit: 'mmol/L', 
          normalRange: '0.56-1.7',
          category: '生化'
        },
        { 
          itemName: '高密度脂蛋白', 
          itemUnit: 'mmol/L', 
          normalRange: '0.91-1.92',
          category: '生化'
        },
        { 
          itemName: '低密度脂蛋白', 
          itemUnit: 'mmol/L', 
          normalRange: '2.07-3.1',
          category: '生化'
        },
        { 
          itemName: '尿素氮', 
          itemUnit: 'mmol/L', 
          normalRange: '2.9-8.2',
          category: '生化'
        },
        { 
          itemName: '肌酐', 
          itemUnit: 'μmol/L', 
          normalRange: '53-97',
          category: '生化'
        },
        { 
          itemName: '尿酸', 
          itemUnit: 'μmol/L', 
          normalRange: '208-428',
          category: '生化'
        },
        { 
          itemName: '谷丙转氨酶', 
          itemUnit: 'U/L', 
          normalRange: '7-40',
          category: '肝功能'
        },
        { 
          itemName: '谷草转氨酶', 
          itemUnit: 'U/L', 
          normalRange: '13-35',
          category: '肝功能'
        },
        { 
          itemName: '收缩压', 
          itemUnit: 'mmHg', 
          normalRange: '90-140',
          category: '心血管'
        },
        { 
          itemName: '舒张压', 
          itemUnit: 'mmHg', 
          normalRange: '60-90',
          category: '心血管'
        },
        { 
          itemName: '心率', 
          itemUnit: '次/分', 
          normalRange: '60-100',
          category: '心血管'
        }
      ]
    };
    resolve(mockData);
    
    // 当后端API实现后，可以取消注释下面的代码
    // return axios.get('/api/physicalExamItem/list');
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
