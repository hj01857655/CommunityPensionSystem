/**
 * 日期时间工具函数
 */

/**
 * 格式化日期为YYYY-MM-DD
 * @param {string|Date} dateString - 日期字符串或Date对象
 * @returns {string} 格式化后的日期字符串
 */
export const formatDate = (dateString) => {
  if (!dateString) return ''
  try {
    const date = dateString instanceof Date ? dateString : new Date(dateString)
    if (isNaN(date.getTime())) return ''
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
  } catch (error) {
    console.error('日期格式化错误:', error)
    return ''
  }
}

/**
 * 格式化日期时间为YYYY-MM-DD HH:mm:ss
 * @param {string|Date} dateString - 日期字符串或Date对象
 * @returns {string} 格式化后的日期时间字符串
 */
export const formatDateTime = (dateString) => {
  if (!dateString) return ''
  try {
    const date = dateString instanceof Date ? dateString : new Date(dateString)
    if (isNaN(date.getTime())) return ''
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    const seconds = String(date.getSeconds()).padStart(2, '0')
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
  } catch (error) {
    console.error('日期时间格式化错误:', error)
    return ''
  }
}

/**
 * 格式化日期为YYYY-MM-DD（简短版）
 * @param {string|Date} dateString - 日期字符串或Date对象
 * @returns {string} 格式化后的日期字符串
 */
export const formatDateShort = (dateString) => {
  if (!dateString) return ''
  try {
    const date = dateString instanceof Date ? dateString : new Date(dateString)
    if (isNaN(date.getTime())) return ''
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
  } catch (error) {
    console.error('日期格式化错误:', error)
    return ''
  }
}

/**
 * 格式化日期时间为YYYY-MM-DD HH:mm:ss（详细版）
 * @param {string|Date} dateString - 日期字符串或Date对象
 * @returns {string} 格式化后的日期时间字符串
 */
export const formatDateDetail = (dateString) => {
  if (!dateString) return ''
  try {
    const date = dateString instanceof Date ? dateString : new Date(dateString)
    if (isNaN(date.getTime())) return ''
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    const seconds = String(date.getSeconds()).padStart(2, '0')
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
  } catch (error) {
    console.error('日期时间格式化错误:', error)
    return ''
  }
}

/**
 * 格式化为相对时间
 * @param {string|Date} dateString - 日期字符串或Date对象
 * @returns {string} 相对时间描述
 */
export const formatRelativeTime = (dateString) => {
  if (!dateString) return ''
  try {
    const date = dateString instanceof Date ? dateString : new Date(dateString)
    if (isNaN(date.getTime())) return ''
    
    const now = new Date()
    const diff = now.getTime() - date.getTime()
    const seconds = Math.floor(diff / 1000)
    const minutes = Math.floor(seconds / 60)
    const hours = Math.floor(minutes / 60)
    const days = Math.floor(hours / 24)

    if (days > 7) {
      return formatDateTime(date)
    } else if (days > 0) {
      return `${days}天前`
    } else if (hours > 0) {
      return `${hours}小时前`
    } else if (minutes > 0) {
      return `${minutes}分钟前`
    } else {
      return '刚刚'
    }
  } catch (error) {
    console.error('相对时间格式化错误:', error)
    return ''
  }
}

/**
 * 判断是否为有效日期
 * @param {string|Date} dateString - 日期字符串或Date对象
 * @returns {boolean} 是否为有效日期
 */
export const isValidDate = (dateString) => {
  if (!dateString) return false
  try {
    const date = dateString instanceof Date ? dateString : new Date(dateString)
    return !isNaN(date.getTime())
  } catch (error) {
    return false
  }
}