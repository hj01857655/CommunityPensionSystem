/**
 * 日期格式化工具函数
 * @param {Date|string|number} date 要格式化的日期
 * @param {string} [format='YYYY-MM-DD HH:mm:ss'] 格式化模式
 * @returns {string} 格式化后的日期字符串
 */
export const formatDate = (date, format = 'YYYY-MM-DD HH:mm:ss') => {
  if (!date) return '';
  
  // 如果传入的是时间戳或日期字符串，转换为Date对象
  if (!(date instanceof Date)) {
    date = new Date(date);
  }

  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const hours = date.getHours();
  const minutes = date.getMinutes();
  const seconds = date.getSeconds();

  // 补零函数
  const padZero = (num) => num.toString().padStart(2, '0');

  // 替换格式化字符
  return format
    .replace('YYYY', year)
    .replace('MM', padZero(month))
    .replace('DD', padZero(day))
    .replace('HH', padZero(hours))
    .replace('mm', padZero(minutes))
    .replace('ss', padZero(seconds));
}