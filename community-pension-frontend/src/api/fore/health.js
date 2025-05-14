import axios from '@/utils/axios'

/**
 * 获取健康记录列表
 * @param {number} elderId - 老人ID
 * @returns {Promise<{code: number, data: Array, message: string}>}
 */
export const getHealthRecords = (elderId) => {
    return axios.get(`/api/health/record/getHealthRecords`, {
        params: {elderId}
    })
}

/**
 * 获取用户健康数据
 * @param {string|number} userId - 用户ID
 * @returns {Promise} 返回健康数据
 */
export const getHealthData = (elderId) => {
    return axios.get(`/api/health/record/getHealthRecords`, {
        params: {elderId}
    })
}

/**
 * 更新用户健康数据
 * @param {Object} data - 健康数据
 * @returns {Promise} 更新结果
 */
export function updateHealthData(data) {
    return axios.put(`/api/health/record/updateHealthRecords`, data)
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
    // 计算BMI
    if (data.height && data.weight) {
        const heightInMeters = data.height / 100
        data.bmi = +(data.weight / (heightInMeters * heightInMeters)).toFixed(1)
    }

    // 如果没有提供日期时间，则生成当前时间并格式化为后端期望的格式
    if (!data.recordTime || !data.symptomsRecordTime) {
        const now = new Date();
        const formattedDateTime = now.getFullYear() + '-' + 
            String(now.getMonth() + 1).padStart(2, '0') + '-' + 
            String(now.getDate()).padStart(2, '0') + ' ' + 
            String(now.getHours()).padStart(2, '0') + ':' + 
            String(now.getMinutes()).padStart(2, '0') + ':' + 
            String(now.getSeconds()).padStart(2, '0');
        
        return axios.post('/api/health/record/addHealthRecords', {
            ...data,
            recordTime: data.recordTime || formattedDateTime,
            symptomsRecordTime: data.symptomsRecordTime || formattedDateTime
        })
    }

    return axios.post('/api/health/record/addHealthRecords', data)
}

/**
 * 检查用户是否有健康档案
 * @param {string|number} elderId - 老人ID
 * @returns {Promise<boolean>} 是否存在健康档案
 */
export const checkHealthRecordExists = async (elderId) => {
    try {
        const response = await getHealthRecords(elderId)
        return response.code === 200 && response.data != null
    } catch (error) {
        if (error.response && error.response.status === 404) {
            return false
        }
        throw error
    }
}

// 统一的错误处理函数
function handleError(error) {
  console.error('API call failed:', error)
  // 可以在这里添加更多的错误处理逻辑，比如通知用户
  throw error
}