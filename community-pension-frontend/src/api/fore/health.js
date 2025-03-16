import axios from '@/utils/axios'

// 获取健康档案
export const getHealthData = async (elderId) => {
  try {
    if (!elderId) {
      throw new Error('获取健康档案需要提供老人ID');
    }
    const response = await axios.get('/api/health-records/getHealthRecords', {
      params: { elderId }
    });
    return response;
  } catch (error) {
    console.error('获取健康档案错误:', error);
    throw error; // 直接抛出错误，让上层处理
  }
}

// 添加健康档案
export const addHealthData = async (data) => {
  try {
    if (!data.elderId) {
      throw new Error('添加健康档案需要提供老人ID');
    }
    const response = await axios.post('/api/health-records/addHealthRecords', data);
    return response;
  } catch (error) {
    console.error('添加健康档案错误:', error);
    throw error;
  }
}

// 更新健康档案
export const updateHealthData = async (data) => {
  try {
    if (!data.id) {
      throw new Error('更新健康档案需要提供记录ID');
    }
    if (!data.elderId) {
      throw new Error('更新健康档案需要提供老人ID');
    }
    const response = await axios.put('/api/health-records/updateHealthRecords', data);
    return response;
  } catch (error) {
    console.error('更新健康档案错误:', error);
    throw error;
  }
}

// 统一的错误处理函数
function handleError(error) {
  console.error('API call failed:', error)
  // 可以在这里添加更多的错误处理逻辑，比如通知用户
  throw error
}