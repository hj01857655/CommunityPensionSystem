<template>
  <div class="health-view">
    <el-card class="content-card" shadow="hover">
      <h3>健康档案</h3>
      <div v-if="isEditMode">
        <el-form :model="healthForm" :rules="healthRules" ref="healthFormRef" label-width="120px">
          <!-- 基础指标 -->
          <el-divider content-position="left">基础指标</el-divider>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="身高" prop="height">
                <el-input-number v-model="healthForm.height" :min="100" :max="250" />
                <span class="unit">cm</span>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="体重" prop="weight">
                <el-input-number v-model="healthForm.weight" :min="30" :max="200" />
                <span class="unit">kg</span>
              </el-form-item>
            </el-col>
          </el-row>

          <!-- 生命体征 -->
          <el-divider content-position="left">生命体征</el-divider>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="血压" prop="bloodPressure">
                <el-input v-model="healthForm.bloodPressure" placeholder="如：120/80" />
                <span class="unit">mmHg</span>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="心率" prop="heartRate">
                <el-input-number v-model="healthForm.heartRate" :min="40" :max="200" />
                <span class="unit">次/分</span>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="血糖" prop="bloodSugar">
                <el-input-number v-model="healthForm.bloodSugar" :precision="1" :step="0.1" :min="2" :max="30" />
                <span class="unit">mmol/L</span>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="体温" prop="temperature">
                <el-input-number v-model="healthForm.temperature" :precision="1" :step="0.1" :min="35" :max="42" />
                <span class="unit">℃</span>
              </el-form-item>
            </el-col>
          </el-row>

          <!-- 健康状况 -->
          <el-divider content-position="left">健康状况</el-divider>
          <el-form-item label="既往病史" prop="medicalHistory">
            <el-input type="textarea" v-model="healthForm.medicalHistory" :rows="2" placeholder="请输入既往病史" />
          </el-form-item>
          <el-form-item label="过敏史" prop="allergy">
            <el-input type="textarea" v-model="healthForm.allergy" :rows="2" placeholder="请输入过敏史" />
          </el-form-item>
          <el-form-item label="当前症状" prop="symptoms">
            <el-input type="textarea" v-model="healthForm.symptoms" :rows="2" placeholder="请输入当前症状" />
          </el-form-item>
          <el-form-item label="用药情况" prop="medication">
            <el-input type="textarea" v-model="healthForm.medication" :rows="2" placeholder="请输入用药情况" />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSave">保存</el-button>
            <el-button @click="handleReset">重置</el-button>
            <el-button @click="toggleEditMode">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div v-else>
        <!-- 查看模式 -->
        <div class="health-info">
          <el-row :gutter="20">
            <el-col :span="12">
              <p><strong>身高:</strong> {{ healthForm.height }} cm</p>
              <p><strong>体重:</strong> {{ healthForm.weight }} kg</p>
              <p><strong>血压:</strong> {{ healthForm.bloodPressure }} mmHg</p>
              <p><strong>心率:</strong> {{ healthForm.heartRate }} 次/分</p>
            </el-col>
            <el-col :span="12">
              <p><strong>血糖:</strong> {{ healthForm.bloodSugar }} mmol/L</p>
              <p><strong>体温:</strong> {{ healthForm.temperature }} ℃</p>
              <p><strong>既往病史:</strong> {{ healthForm.medicalHistory }}</p>
              <p><strong>过敏史:</strong> {{ healthForm.allergy }}</p>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24">
              <p><strong>当前症状:</strong> {{ healthForm.symptoms }}</p>
              <p><strong>用药情况:</strong> {{ healthForm.medication }}</p>
            </el-col>
          </el-row>
        </div>
        <el-button type="primary" @click="toggleEditMode">编辑</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getHealthData, updateHealthData, addHealthData } from '@/api/fore/health'
import { useUserStore } from '@/stores/fore/useUserStore'

const healthFormRef = ref(null)
const isEditMode = ref(false) // 添加编辑模式状态
const loading = ref(false) // 添加加载状态
const healthRecords = ref([]) // 添加健康记录列表
const totalRecords = ref(0) // 添加总记录数

const healthForm = ref({
  id: null,
  elderId: null,
  height: 170,
  weight: 65,
  bloodPressure: '120/80',
  heartRate: 75,
  bloodSugar: 5.6,
  temperature: 36.5,
  medicalHistory: '',
  allergy: '',
  symptoms: '',
  medication: '',
  recordType: '日常监测',
  recordTime: new Date().toISOString()
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
        if (parseInt(systolic) < 60 || parseInt(systolic) > 200 || 
            parseInt(diastolic) < 40 || parseInt(diastolic) > 120) {
          callback(new Error('血压数值超出正常范围'))
        } else {
          callback()
        }
      } else {
        callback()
      }
    }, trigger: 'blur' }
  ],
  heartRate: [
    { required: true, message: '请输入心率', trigger: 'blur' },
    { type: 'number', min: 40, max: 200, message: '心率应在40-200次/分之间', trigger: 'blur' }
  ],
  bloodSugar: [
    { required: true, message: '请输入血糖', trigger: 'blur' },
    { type: 'number', min: 2, max: 30, message: '血糖值应在2-30mmol/L之间', trigger: 'blur' }
  ],
  temperature: [
    { required: true, message: '请输入体温', trigger: 'blur' },
    { type: 'number', min: 35, max: 42, message: '体温应在35-42℃之间', trigger: 'blur' }
  ]
}

const userStore = useUserStore()

// 获取用户角色
const userRole = computed(() => {
  return userStore.roles?.[0];
});

// 判断是否为老人角色
const isElder = computed(() => userRole.value === 'elder');

// 获取用户信息
const userInfo = computed(() => {
  return userStore.userInfo;
});

// 获取老人ID
const getElderId = () => {
  return userInfo.value?.userId;
}

// 检查登录状态
const checkLoginStatus = () => {
  if (!userInfo.value || !userInfo.value.userId) {
    // 如果 store 中没有，检查本地存储
    const localUserInfo = JSON.parse(localStorage.getItem("userInfo") || "{}");
    if (!localUserInfo || !localUserInfo.userId) {
      ElMessage.warning('请先登录')
      return false
    }
    // 如果本地存储有，但 store 中没有，则重新获取用户信息
    userStore.getUserInfo();
  }
  return true
}

const handleSave = () => {
  healthFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 确保表单中有elderId
        const elderId = getElderId()
        if (!elderId) {
          ElMessage.error('无法获取老人ID，请重新登录')
          return
        }
        
        healthForm.value.elderId = elderId
        healthForm.value.recordTime = new Date().toISOString()
        healthForm.value.symptomsRecordTime = new Date().toISOString()
        
        // 根据是否有id决定是新增还是更新
        let response
        if (healthForm.value.id) {
          response = await updateHealthData(healthForm.value)
        } else {
          response = await addHealthData(healthForm.value)
        }
        
        if (response.code === 200) {
          ElMessage.success(response.message || '健康数据保存成功')
          if (response.data && response.data.id) {
            healthForm.value.id = response.data.id
          }
          toggleEditMode() // 保存成功后退出编辑模式
        } else {
          ElMessage.error(response.message || '保存失败')
        }
      } catch (error) {
        ElMessage.error('保存失败：' + (error.message || '未知错误'))
        console.error('保存失败：', error)
      }
    }
  })
}

const handleReset = () => {
  healthFormRef.value.resetFields()
}

// 切换编辑模式
const toggleEditMode = () => {
  isEditMode.value = !isEditMode.value
}

// 获取初始健康数据
const fetchHealthData = async () => {
  loading.value = true
  try {
    const elderId = getElderId()
    if (!elderId) {
      ElMessage.warning('请先登录')
      return
    }
    
    const response = await getHealthData(elderId)
    
    if (response.code === 200 && response.data) {
      // 更新表单数据
      healthForm.value = {
        ...healthForm.value,
        ...response.data,
        elderId: elderId
      }
    } else if (response.code === 404) {
      // 健康档案不存在，准备创建新档案
      ElMessage.info('您还没有健康档案，请填写并保存')
      healthForm.value.elderId = elderId
      healthForm.value.id = null
    }
  } catch (error) {
    ElMessage.error('获取健康数据失败：' + (error.message || '未知错误'))
    console.error('获取健康数据失败：', error)
  } finally {
    loading.value = false
  }
}

// 获取健康记录列表
const fetchHealthRecords = async () => {
  if (!checkLoginStatus()) return
  
  loading.value = true
  try {
    const elderId = getElderId()
    if (!elderId) {
      ElMessage.warning('无法获取老人信息')
      return
    }
    
    const response = await userStore.getHealthRecords(elderId)
    if (response && response.data) {
      healthRecords.value = response.data
      totalRecords.value = response.data.length
    }
  } catch (error) {
    console.error('获取健康记录失败:', error)
    ElMessage.error('获取健康记录失败')
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await fetchHealthData()
  await fetchHealthRecords()
})
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
  max-width: 800px;
  margin: 0 auto;
}

.el-divider {
  margin: 24px 0;
}

.el-divider__text {
  font-size: 16px;
  font-weight: 500;
  color: #409EFF;
}

.health-info {
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.health-info p {
  margin: 8px 0;
  font-size: 14px;
  color: #333;
  line-height: 1.6;
}

.health-info strong {
  color: #409EFF;
}
</style>