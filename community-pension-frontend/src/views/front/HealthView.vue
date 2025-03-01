<template>
    <div class="health-view">
      <el-card class="content-card" shadow="hover">
        <h3>健康档案</h3>
        <el-form :model="healthForm" label-width="100px">
          <el-form-item label="身高">
            <el-input-number v-model="healthForm.height" :min="100" :max="250" />
            <span class="unit">cm</span>
          </el-form-item>
          <el-form-item label="体重">
            <el-input-number v-model="healthForm.weight" :min="30" :max="200" />
            <span class="unit">kg</span>
          </el-form-item>
          <el-form-item label="血压">
            <el-input v-model="healthForm.bloodPressure" placeholder="如：120/80" />
            <span class="unit">mmHg</span>
          </el-form-item>
          <el-form-item label="血糖">
            <el-input-number v-model="healthForm.bloodSugar" :precision="1" :step="0.1" />
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
  
  const healthForm = ref({
    height: 170,
    weight: 65,
    bloodPressure: '120/80',
    bloodSugar: 5.6,
  })
  
  const handleSave = () => {
    ElMessage.success('健康数据保存成功')
  }
  
  const handleReset = () => {
    healthForm.value = {
      height: 170,
      weight: 65,
      bloodPressure: '120/80',
      bloodSugar: 5.6,
    }
  }
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