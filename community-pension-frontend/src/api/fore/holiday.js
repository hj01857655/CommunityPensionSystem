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
    
    // 调用节假日API
    const response = await axios.get(`/holiday/v1/is_holiday`, {
      params: {
        date: dateStr
      },
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      }
    })
    
    // 缓存结果
    holidayCache.set(dateStr, response.data)
    return response.data
  } catch (error) {
    console.error('获取节假日信息失败:', error)
    return null
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