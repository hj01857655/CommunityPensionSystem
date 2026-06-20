import dayjs from 'dayjs'

/**
 * Format Utilities
 */

/**
 * Format date to YYYY-MM-DD HH:mm:ss
 */
export const formatDateTime = (date?: Date | string | number): string => {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

/**
 * Format date to YYYY-MM-DD
 */
export const formatDate = (date?: Date | string | number): string => {
  return dayjs(date).format('YYYY-MM-DD')
}

/**
 * Format date to HH:mm:ss
 */
export const formatTime = (date?: Date | string | number): string => {
  return dayjs(date).format('HH:mm:ss')
}

/**
 * Calculate BMI (Body Mass Index)
 * @param weight - Weight in kg
 * @param height - Height in cm
 * @returns BMI value rounded to 1 decimal place
 */
export const calculateBMI = (weight: number, height: number): number => {
  if (!weight || !height || height <= 0) return 0
  const heightInMeters = height / 100
  return +(weight / (heightInMeters * heightInMeters)).toFixed(1)
}

/**
 * Format phone number (e.g., 13812345678 -> 138****5678)
 */
export const maskPhone = (phone: string): string => {
  if (!phone || phone.length !== 11) return phone
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

/**
 * Format ID card (e.g., 110101199001011234 -> 110101********1234)
 */
export const maskIdCard = (idCard: string): string => {
  if (!idCard || idCard.length < 10) return idCard
  return idCard.replace(/^(.{6})(?:\d+)(.{4})$/, '$1********$2')
}
