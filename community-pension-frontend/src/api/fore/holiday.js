import axios from '@/utils/axios'

// 缓存API调用结果
const holidayCache = new Map()

/**
 * 获取节假日信息
 * @param {Date} date - 要检查的日期
 * @returns {Promise<Object|null>} - 节假日信息，如果不是节假日则返回null
 */
const getHolidayInfo = async (date) => {
  try {
    // 格式化日期为YYYY-MM-DD
    const dateStr = date.toISOString().split('T')[0]
    
    // 检查缓存
    if (holidayCache.has(dateStr)) {
      return holidayCache.get(dateStr)
    }
    
    // 调用节假日API，增加超时设置和错误处理
    const response = await axios.get(`/holiday/v1/is_holiday`, {
      params: {
        date: dateStr
      },
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      timeout: 3000 // 减少超时时间，避免长时间等待
    })
    
    // 缓存结果
    holidayCache.set(dateStr, response.data)
    return response.data
  } catch (error) {
    console.error('获取节假日信息失败:', error)
    // 错误发生时，使用本地判断周末的逻辑作为备用方案
    const day = date.getDay()
    const isWeekend = day === 0 || day === 6 // 0是周日，6是周六
    
    // 创建一个简单的备用响应对象
    const fallbackResponse = {
      holiday: isWeekend,
      name: isWeekend ? (day === 0 ? '周日' : '周六') : null,
      type: isWeekend ? 1 : 0, // 1表示休息日，0表示工作日
      isOffDay: isWeekend
    }
    
    // 缓存备用结果
    holidayCache.set(dateStr, fallbackResponse)
    return fallbackResponse
  }
}

/**
 * 检查指定日期是否为节假日
 * @param {Date} date - 要检查的日期
 * @returns {Promise<boolean>} - 是否为节假日
 */
export const checkHoliday = async (date) => {
  const holidayInfo = await getHolidayInfo(date)
  if (!holidayInfo) {
    // 如果API调用失败，回退到周末检查
    const day = date.getDay()
    return day === 0 || day === 6 // 0是周日，6是周六
  }
  return holidayInfo.is_holiday
}

/**
 * 获取节假日名称
 * @param {Date} date - 要检查的日期
 * @returns {Promise<string|null>} - 节假日名称，如果不是节假日则返回null
 */
export const getHolidayName = async (date) => {
  const holidayInfo = await getHolidayInfo(date)
  return holidayInfo?.holiday?.name || null
}

/**
 * 获取节假日是否休息日
 * @param {Date} date - 要检查的日期
 * @returns {Promise<boolean|null>} - 是否休息日，如果不是节假日则返回null
 */
export const isHolidayOffDay = async (date) => {
  const holidayInfo = await getHolidayInfo(date)
  return holidayInfo?.holiday?.isOffDay || null
} 