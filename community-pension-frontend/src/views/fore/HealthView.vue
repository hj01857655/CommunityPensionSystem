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
import {onMounted, ref} from 'vue'
import {ElMessage} from 'element-plus'
import {addHealthData, getHealthData, updateHealthData} from '@/api/fore/health'
import {useForegroundUserStore} from '@/stores/fore/userStore'
import {useRouter} from 'vue-router'

const router = useRouter()
const healthFormRef = ref(null)
const isEditMode = ref(false)
const loading = ref(false)
const healthRecords = ref([])
const totalRecords = ref(0)

const healthForm = ref({
  id: null,
  elderId: null,
  recorderId: null,
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
  recordTime: new Date().toISOString(),
  symptomsRecordTime: new Date().toISOString()
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

const userStore = useForegroundUserStore()

// 检查登录状态
const checkLoginStatus = () => {
  if (!userStore.isLoggedIn || !userStore.userInfo?.userId) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return false
  }
  return true
}

const handleSave = async () => {
  if (!checkLoginStatus()) return

  try {
    loading.value = true

    // 确保有必要的字段
    const userInfo = userStore.userInfo;
    const formData = {
      ...healthForm.value,
      recorderId: userInfo.userId,
      elderId: userInfo.userId, // 如果是老人，使用自己的ID
      recordTime: new Date().toISOString().split('.')[0],
      symptomsRecordTime: new Date().toISOString().split('.')[0]
    }

    // 计算 BMI
    if (formData.height && formData.weight) {
      const height = parseFloat(formData.height) / 100
      const weight = parseFloat(formData.weight)
      formData.bmi = (weight / (height * height)).toFixed(2)
    }

    console.log('保存健康记录，参数：', JSON.stringify(formData, null, 2))

    const response = await updateHealthData(formData)
    console.log('保存响应：', response)

    if (response.code === 200) {
      ElMessage.success('保存成功')
      isEditMode.value = false
      // 更新表单数据
      Object.assign(healthForm.value, response.data)
    } else {
      throw new Error(response.message || '保存失败')
    }
  } catch (error) {
    console.error('保存健康记录失败:', error)
    ElMessage.error(error.response?.data?.message || error.message || '保存失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  healthFormRef.value.resetFields()
}

// 切换编辑模式
const toggleEditMode = () => {
  isEditMode.value = !isEditMode.value
}

// 获取健康记录列表
const fetchHealthRecords = async () => {
  if (!checkLoginStatus()) {
    return;
  }

  loading.value = true;
  try {
    const elderId = userStore.getElderId();
    if (!elderId) {
      ElMessage.warning('请先登录后再访问健康记录');
      return;
    }

    const response = await userStore.getHealthRecords(elderId);
    if (response && response.data) {
      healthRecords.value = Array.isArray(response.data) ? response.data : [response.data];
      totalRecords.value = healthRecords.value.length;
    } else {
      healthRecords.value = [];
      totalRecords.value = 0;
    }
  } catch (error) {
    console.error('获取健康记录失败:', error);
    ElMessage.error(error.response?.data?.message || error.message || '获取健康记录失败');
    healthRecords.value = [];
    totalRecords.value = 0;
  } finally {
    loading.value = false;
  }
};

const fetchHealthData = async () => {
  if (!checkLoginStatus()) return

  loading.value = true
  try {
    const userInfo = userStore.userInfo;
    const elderId = userInfo.userId // 如果是老人，使用自己的ID

    if (!elderId) {
      throw new Error('无法获取用户ID')
    }

    const response = await getHealthData(elderId)
    console.log('获取健康数据响应：', response)

    if (response.code === 200) {
      if (!response.data) {
        // 如果没有健康记录，创建新的
        await createNewHealthRecord()
      } else {
        Object.assign(healthForm.value, response.data)
      }
    } else {
      throw new Error(response.message || '获取健康数据失败')
    }
  } catch (error) {
    console.error('获取健康数据失败:', error)
    ElMessage.error(error.response?.data?.message || error.message || '获取健康数据失败')
  } finally {
    loading.value = false
  }
}

const createNewHealthRecord = async () => {
  try {
    const userInfo = userStore.userInfo;
    if (!userInfo) {
      throw new Error('用户未登录')
    }

    const recorderId = userInfo.userId
    const elderId = userStore.getElderId()

    if (!elderId || !recorderId) {
      throw new Error('无法获取用户ID')
    }

    const now = new Date().toISOString()
    const newHealthRecord = {
      elderId,
      recorderId,
      height: '',
      weight: '',
      bloodPressure: '',
      bloodSugar: '',
      heartRate: '',
      temperature: '',
      symptoms: '',
      recordTime: now,
      symptomsRecordTime: now
    }

    const response = await addHealthData(newHealthRecord)
    if (response.data.code === 200) {
      ElMessage.success('健康记录创建成功')
      return response.data.data
    } else {
      throw new Error(response.data.message || '创建健康记录失败')
    }
  } catch (error) {
    console.error('创建健康记录失败:', error)
    ElMessage.error(error.message || '创建健康记录失败')
    throw error
  }
}

onMounted(async () => {
  if (!checkLoginStatus()) return
  await fetchHealthData()
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