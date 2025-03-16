<template>
    <div class="health-view">
      <el-card class="content-card" shadow="hover">
        <h3>健康档案</h3>
        <el-form :model="healthForm" :rules="healthRules" ref="healthFormRef" label-width="100px">
          <el-form-item label="身高" prop="height">
            <el-input-number v-model="healthForm.height" :min="100" :max="250" />
            <span class="unit">cm</span>
          </el-form-item>
          <el-form-item label="体重" prop="weight">
            <el-input-number v-model="healthForm.weight" :min="30" :max="200" />
            <span class="unit">kg</span>
          </el-form-item>
          <el-form-item label="血压" prop="bloodPressure">
            <el-input v-model="healthForm.bloodPressure" placeholder="如：120/80" />
            <span class="unit">mmHg</span>
          </el-form-item>
          <el-form-item label="血糖" prop="bloodSugar">
            <el-input-number v-model="healthForm.bloodSugar" :precision="1" :step="0.1" :min="2" :max="30" />
            <span class="unit">mmol/L</span>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSave">保存</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue'
  import { ElMessage } from 'element-plus'
  import { getHealthData, updateHealthData } from '@/api/fore/health'
  
  const healthFormRef = ref(null)
  const healthForm = ref({
    height: 170,
    weight: 65,
    bloodPressure: '120/80',
    bloodSugar: 5.6,
  })
  
  const healthRules = {
    height: [
      { required: true, message: '请输入身高', trigger: 'blur' },
      { type: 'number', min: 100, max: 250, message: '身高应在100-250cm之间', trigger: 'blur' }
    ],
    weight: [
      { required: true, message: '请输入体重', trigger: 'blur' },
      { type: 'number', min: 30, max: 200, message: '体重应在30-200kg之间', trigger: 'blur' }
    ],
    bloodPressure: [
      { required: true, message: '请输入血压', trigger: 'blur' },
      { pattern: /^\d{2,3}\/\d{2,3}$/, message: '请输入正确的血压格式，如120/80', trigger: 'blur' },
      { validator: (rule, value, callback) => {
        if (value) {
          const [systolic, diastolic] = value.split('/')
          if (parseInt(systolic) < 60 || parseInt(systolic) > 200 || parseInt(diastolic) < 40 || parseInt(diastolic) > 120) {
            callback(new Error('血压数值超出正常范围'))
          } else {
            callback()
          }
        } else {
          callback()
        }
      }, trigger: 'blur' }
    ],
    bloodSugar: [
      { required: true, message: '请输入血糖', trigger: 'blur' },
      { type: 'number', min: 2, max: 30, message: '血糖值应在2-30mmol/L之间', trigger: 'blur' }
    ]
  }
  
  const handleSave = () => {
    healthFormRef.value.validate(async (valid) => {
      if (valid) {
        try {
          await updateHealthData(healthForm.value)
          ElMessage.success('健康数据保存成功')
        } catch (error) {
          console.error('保存失败：' + error.message)
        }
      }
    })
  }
  
  const handleReset = () => {
    healthFormRef.value.resetFields()
  }
  
  // 获取初始健康数据
  const fetchHealthData = async () => {
    try {
      const { data } = await getHealthData()
      if (data) {
        healthForm.value = data
      }
    } catch (error) {
      console.error('获取健康数据失败：' + error.message)
    }
  }
  
  // 页面加载时获取数据
  fetchHealthData()
  </script>
  
  <style scoped>
  .health-view {
    width: 100%;
  }
  
  .content-card {
    margin-bottom: 20px;
  }
  
  h3 {
    margin-bottom: 20px;
    color: #2c3e50;
    font-weight: 600;
  }
  
  .unit {
    margin-left: 8px;
    color: #666;
  }
  
  .el-form {
    max-width: 500px;
  }
  </style>