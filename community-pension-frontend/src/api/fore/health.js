import axios from '@/utils/axios'

/**
 * 获取健康记录列表
 * @param {number} elderId - 老人ID
 * @returns {Promise<{code: number, data: Array, message: string}>}
 */
export const getHealthRecords = (elderId) => {
  return axios.get(`/api/health/records/${elderId}`)
}

/**
 * 获取健康档案
 * @param {number} elderId - 老人ID
 * @returns {Promise<{code: number, data: Object, message: string}>}
 */
export const getHealthData = (elderId) => {
  return axios.get(`/api/health/data/${elderId}`)
}

/**
 * 更新健康档案
 * @param {Object} data - 健康档案数据
 * @param {number} data.id - 记录ID
 * @param {number} data.elderId - 老人ID
 * @param {string} data.bloodPressure - 血压值
 * @param {number} data.heartRate - 心率
 * @param {number} data.bloodSugar - 血糖值
 * @param {number} data.temperature - 体温
 * @param {number} data.weight - 体重
 * @param {number} data.height - 身高
 * @param {number} data.bmi - BMI指数
 * @param {string} data.medicalHistory - 病史
 * @param {string} data.allergy - 过敏史
 * @param {string} data.symptoms - 症状描述
 * @param {string} data.medication - 用药情况
 * @returns {Promise<{code: number, data: Object, message: string}>}
 */
export const updateHealthData = (data) => {
  return axios.put('/api/health/data', data)
}

/**
 * 添加健康记录
 * @param {Object} data - 健康记录数据
 * @param {number} data.elderId - 老人ID
 * @param {string} data.bloodPressure - 血压值
 * @param {number} data.heartRate - 心率
 * @param {number} data.bloodSugar - 血糖值
 * @param {number} data.temperature - 体温
 * @param {number} data.weight - 体重
 * @param {number} data.height - 身高
 * @param {number} data.bmi - BMI指数
 * @param {string} data.medicalHistory - 病史
 * @param {string} data.allergy - 过敏史
 * @param {string} data.symptoms - 症状描述
 * @param {string} data.medication - 用药情况
 * @param {string} data.recordType - 记录类型
 * @param {string} data.recordTime - 记录时间
 * @returns {Promise<{code: number, data: Object, message: string}>}
 */
export const addHealthData = (data) => {
  return axios.post('/api/health/records', data)
}

// 统一的错误处理函数
function handleError(error) {
  console.error('API call failed:', error)
  // 可以在这里添加更多的错误处理逻辑，比如通知用户
  throw error
}