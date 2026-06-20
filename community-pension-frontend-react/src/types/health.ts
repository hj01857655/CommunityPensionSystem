/**
 * Health Types
 */

export interface HealthRecord {
  recordId: number
  elderId: number
  bloodPressure?: string // 血压 "120/80"
  heartRate?: number // 心率
  bloodSugar?: number // 血糖
  temperature?: number // 体温
  weight?: number // 体重 (kg)
  height?: number // 身高 (cm)
  bmi?: number // BMI指数
  medicalHistory?: string // 病史
  allergy?: string // 过敏史
  symptoms?: string // 症状描述
  medication?: string // 用药情况
  recordType?: string // 记录类型
  recordTime: string // 记录时间
  symptomsRecordTime?: string
  createTime?: string
  updateTime?: string
}

export interface AddHealthRecordRequest {
  elderId: number
  bloodPressure?: string
  heartRate?: number
  bloodSugar?: number
  temperature?: number
  weight?: number
  height?: number
  bmi?: number
  medicalHistory?: string
  allergy?: string
  symptoms?: string
  medication?: string
  recordType?: string
  recordTime?: string
  symptomsRecordTime?: string
}

export interface UpdateHealthRecordRequest extends Partial<AddHealthRecordRequest> {
  recordId: number
}
