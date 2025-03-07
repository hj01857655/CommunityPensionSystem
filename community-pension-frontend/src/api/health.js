import axios from '@/utils/axios'

// 获取健康数据
export const getHealthData = async () => {
  const token = localStorage.getItem('token');
  const response = await axios.get('/api/health-records/getHealthRecords',
     { 
      headers: {
        Authorization: `${token}`,
      },
      params: {
        elderId: elderId
      }
     })
     return { success: true, data: response.data };
}

// 更新健康数据
export const updateHealthData = async (data) => {
  const token = localStorage.getItem('token');
  const response = await axios.post('/api/health-records/addHealthRecords', data,
     { 
      headers: {
        Authorization: `${token}`,
      },
     })
     return { success: true, data: response.data };
}