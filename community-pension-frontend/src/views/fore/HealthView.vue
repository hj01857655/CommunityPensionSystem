<template>
  <div class="health-view">
    <el-card class="content-card" shadow="hover">
      <h3>健康档案</h3>
      <el-tabs v-model="activeTab" @tab-change="handleTabChange" lazy>
        <el-tab-pane label="健康监测记录" name="monitor">
          <!-- 趋势图区域 -->
          <HealthTrendChart :data="monitorList" :loading="monitorLoading" />
          <div style="margin-bottom: 16px; display: flex; justify-content: flex-end;">
            <el-button type="primary" icon="Refresh" @click="fetchHealthMonitorList" :loading="monitorLoading">刷新</el-button>
          </div>
          <el-table :data="monitorList" v-loading="monitorLoading" style="width: 100%;" :row-class-name="monitorRowClass">
            <template #empty>
              <div style="padding: 40px 0; color: #999; font-size: 16px; text-align: center;">
                暂无健康监测数据
              </div>
            </template>
            <el-table-column prop="monitoringTime" label="时间" min-width="140">
              <template #default="{ row }">
                <span>{{ row.monitoringTime }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="monitoringType" label="类型" min-width="80">
              <template #default="{ row }">
                {{ monitoringTypeText(row.monitoringType) }}
              </template>
            </el-table-column>
            <el-table-column prop="monitoringValue" label="数值" min-width="80" />
            <el-table-column prop="monitoringUnit" label="单位" min-width="60" />
            <el-table-column prop="monitoringStatus" label="状态" min-width="80">
              <template #default="{ row }">
                <el-tag :type="row.monitoringStatus === 'normal' ? 'success' : 'danger'">
                  {{ row.monitoringStatus === 'normal' ? '正常' : '异常' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="abnormalDescription" label="异常说明" min-width="120" />
          </el-table>
          <el-pagination
            v-if="monitorTotal > pageSize"
            style="margin-top: 16px; text-align: right;"
            :current-page="currentPage"
            :page-size="pageSize"
            :total="monitorTotal"
            layout="total, prev, pager, next"
            @current-change="handlePageChange"
          />
        </el-tab-pane>
        <el-tab-pane label="健康档案" name="profile">
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
            <div class="health-info" ref="healthInfoRef">
              <el-row :gutter="20">
                <el-col :span="24">
                  <el-divider content-position="left">基础指标</el-divider>
                </el-col>
                <el-col :span="12">
                  <p><strong>身高:</strong> {{ healthForm.height || '未填写' }} cm</p>
                  <p><strong>体重:</strong> {{ healthForm.weight || '未填写' }} kg</p>
                </el-col>
                <el-col :span="12">
                  <p><strong>BMI:</strong> {{ healthForm.bmi || '未填写' }}</p>
                </el-col>
                <el-col :span="24">
                  <el-divider content-position="left">生命体征</el-divider>
                </el-col>
                <el-col :span="12">
                  <p><strong>血压:</strong> <el-tag v-if="isAbnormal('bloodPressure')" type="danger">{{ healthForm.bloodPressure }}</el-tag><span v-else>{{ healthForm.bloodPressure || '未填写' }}</span> mmHg</p>
                  <p><strong>心率:</strong> <el-tag v-if="isAbnormal('heartRate')" type="danger">{{ healthForm.heartRate }}</el-tag><span v-else>{{ healthForm.heartRate || '未填写' }}</span> 次/分</p>
                </el-col>
                <el-col :span="12">
                  <p><strong>血糖:</strong> <el-tag v-if="isAbnormal('bloodSugar')" type="danger">{{ healthForm.bloodSugar }}</el-tag><span v-else>{{ healthForm.bloodSugar || '未填写' }}</span> mmol/L</p>
                  <p><strong>体温:</strong> <el-tag v-if="isAbnormal('temperature')" type="danger">{{ healthForm.temperature }}</el-tag><span v-else>{{ healthForm.temperature || '未填写' }}</span> ℃</p>
                </el-col>
                <el-col :span="24">
                  <el-divider content-position="left">健康状况</el-divider>
                </el-col>
                <el-col :span="12">
                  <p><strong>既往病史:</strong> {{ healthForm.medicalHistory || '未填写' }}</p>
                  <p><strong>过敏史:</strong> {{ healthForm.allergy || '未填写' }}</p>
                </el-col>
                <el-col :span="12">
                  <p><strong>当前症状:</strong> {{ healthForm.symptoms || '未填写' }}</p>
                  <p><strong>用药情况:</strong> {{ healthForm.medication || '未填写' }}</p>
                </el-col>
              </el-row>
              <div style="margin-top: 16px; display: flex; gap: 12px; align-items: center;">
                <el-button size="small" @click="copyHealthInfo">复制全部</el-button>
                <el-button size="small" @click="exportHealthInfoPDF">导出PDF</el-button>
                <span style="color: #999; font-size: 13px; margin-left: 8px;">最后更新时间：{{ healthForm.recordTime ? (healthForm.recordTime.replace('T', ' ').slice(0, 19)) : '无' }}</span>
              </div>
            </div>
            <el-button type="primary" @click="toggleEditMode">编辑</el-button>
          </div>
        </el-tab-pane>
        <el-tab-pane label="体检报告" name="exam">
          <PhysicalExamReport />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { addHealthData, getHealthData, updateHealthData } from '@/api/fore/health'
import { useHealthMonitorStore } from '@/stores/fore/healthMonitorStore'
import { useForegroundUserStore } from '@/stores/fore/userStore'
import { ElMessage } from 'element-plus'
import html2canvas from 'html2canvas'
import jsPDF from 'jspdf'
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import HealthTrendChart from './HealthTrendChart.vue'
import PhysicalExamReport from './PhysicalExamReport.vue'

const router = useRouter()
const healthFormRef = ref(null)
const isEditMode = ref(false)
const loading = ref(false)
const activeTab = ref(localStorage.getItem('health-active-tab') || 'monitor')
const currentPage = ref(1)
const pageSize = ref(10)
const userStore = useForegroundUserStore()
const healthMonitorStore = useHealthMonitorStore()
const healthInfoRef = ref(null)

// 健康监测store数据
const monitorList = computed(() => healthMonitorStore.monitorList)
const monitorTotal = computed(() => healthMonitorStore.total)
const monitorLoading = computed(() => healthMonitorStore.loading)

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

const checkLoginStatus = async () => {
  try {
    // 检查是否有token，使用正确的key
    const token = localStorage.getItem('user-access-token');
    if (!token) {
      ElMessage.warning('请先登录后再访问健康记录');
      router.push('/login');
      return false;
    }

    // 从本地存储获取用户信息
    const localUserInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
    if (!localUserInfo || !localUserInfo.userId) {
      ElMessage.warning('请先登录后再访问健康记录');
      router.push('/login');
      return false;
    }

    // 更新store中的用户信息
    if (!userStore.userInfo) {
      userStore.setUserInfo(localUserInfo);
    }

    return true;
  } catch (error) {
    console.error('检查登录状态失败:', error);
    ElMessage.error('登录状态验证失败，请重新登录');
    router.push('/login');
    return false;
  }
}

const handleSave = async () => {
  if (!checkLoginStatus()) return;

  try {
    loading.value = true;

    // 从本地存储获取用户信息
    const localUserInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
    const formData = {
      ...healthForm.value,
      recorderId: localUserInfo.userId,
      elderId: localUserInfo.userId, // 如果是老人，使用自己的ID
      recordTime: new Date().toISOString().split('.')[0],
      symptomsRecordTime: new Date().toISOString().split('.')[0]
    };

    // 计算 BMI
    if (formData.height && formData.weight) {
      const height = parseFloat(formData.height) / 100;
      const weight = parseFloat(formData.weight);
      formData.bmi = (weight / (height * height)).toFixed(2);
    }

    console.log('保存健康记录，参数：', JSON.stringify(formData, null, 2));

    const response = await updateHealthData(formData);
    console.log('保存响应：', response);

    if (response.code === 200) {
      ElMessage.success('保存成功');
      isEditMode.value = false;
      // 更新表单数据
      Object.assign(healthForm.value, response.data);
    } else {
      throw new Error(response.message || '保存失败');
    }
  } catch (error) {
    console.error('保存健康记录失败:', error);
    ElMessage.error(error.response?.data?.message || error.message || '保存失败');
  } finally {
    loading.value = false;
  }
}

const handleReset = () => {
  healthFormRef.value.resetFields()
}

// 切换编辑模式
const toggleEditMode = () => {
  isEditMode.value = !isEditMode.value
}

const handleTabChange = (tabName) => {
  activeTab.value = tabName
  localStorage.setItem('health-active-tab', tabName)
  if (tabName === 'monitor') {
    fetchHealthMonitorList()
  } else if (tabName === 'profile') {
    fetchHealthData()
  } else if (tabName === 'exam') {
    // 体检报告
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchHealthMonitorList()
}

// 通过store获取健康监测历史
const fetchHealthMonitorList = async () => {
  if (!checkLoginStatus()) return
  const localUserInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const userId = localUserInfo.userId
  if (!userId) {
    ElMessage.warning('请先登录后再访问健康监测')
    return
  }
  await healthMonitorStore.fetchMonitorList({
    userId,
    pageNum: currentPage.value,
    pageSize: pageSize.value
  })
}

const fetchHealthData = async () => {
  const isLoggedIn = await checkLoginStatus();
  if (!isLoggedIn) return;

  loading.value = true;
  try {
    // 从本地存储获取用户信息
    const localUserInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
    const elderId = localUserInfo.userId; // 如果是老人，使用自己的ID

    if (!elderId) {
      throw new Error('无法获取用户ID');
    }

    const response = await getHealthData(elderId);
    console.log('获取健康数据响应：', response);

    if (response.code === 200) {
      if (!response.data) {
        // 如果没有健康记录，创建新的
        await createNewHealthRecord();
      } else {
        Object.assign(healthForm.value, response.data);
      }
    } else {
      throw new Error(response.message || '获取健康数据失败');
    }
  } catch (error) {
    console.error('获取健康数据失败:', error);
    ElMessage.error(error.response?.data?.message || error.message || '获取健康数据失败');
  } finally {
    loading.value = false;
  }
}

const createNewHealthRecord = async () => {
  try {
    // 从本地存储获取用户信息
    const localUserInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
    if (!localUserInfo || !localUserInfo.userId) {
      throw new Error('用户未登录')
    }

    const recorderId = localUserInfo.userId;
    const elderId = localUserInfo.userId; // 如果是老人，使用自己的ID

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
    if (response.code === 200) {
      ElMessage.success('健康记录创建成功')
      Object.assign(healthForm.value, response.data);
      return response.data;
    } else {
      throw new Error(response.message || '创建健康记录失败')
    }
  } catch (error) {
    console.error('创建健康记录失败:', error)
    ElMessage.error(error.message || '创建健康记录失败')
    throw error
  }
}

const monitoringTypeText = (type) => {
  switch (type) {
    case '1': return '血压'
    case '2': return '血糖'
    case '3': return '体温'
    case '4': return '心率'
    case '5': return '血氧'
    case '6': return '体重'
    default: return '其他'
  }
}

function copyHealthInfo() {
  const info = [
    '【基础指标】',
    `身高: ${healthForm.value.height || '未填写'} cm`,
    `体重: ${healthForm.value.weight || '未填写'} kg`,
    `BMI: ${healthForm.value.bmi || '未填写'}`,
    '【生命体征】',
    `血压: ${healthForm.value.bloodPressure || '未填写'} mmHg`,
    `心率: ${healthForm.value.heartRate || '未填写'} 次/分`,
    `血糖: ${healthForm.value.bloodSugar || '未填写'} mmol/L`,
    `体温: ${healthForm.value.temperature || '未填写'} ℃`,
    '【健康状况】',
    `既往病史: ${healthForm.value.medicalHistory || '未填写'}`,
    `过敏史: ${healthForm.value.allergy || '未填写'}`,
    `当前症状: ${healthForm.value.symptoms || '未填写'}`,
    `用药情况: ${healthForm.value.medication || '未填写'}`,
    `最后更新时间: ${healthForm.value.recordTime ? (healthForm.value.recordTime.replace('T', ' ').slice(0, 19)) : '无'}`
  ].join('\n')
  navigator.clipboard.writeText(info).then(() => {
    ElMessage.success('健康档案已复制到剪贴板')
  })
}

function exportHealthInfoPDF() {
  const el = healthInfoRef.value
  if (!el) return
  html2canvas(el, { scale: 2 }).then(canvas => {
    const imgData = canvas.toDataURL('image/png')
    const pdf = new jsPDF('p', 'mm', 'a4')
    const pageWidth = pdf.internal.pageSize.getWidth()
    const pageHeight = pdf.internal.pageSize.getHeight()
    // 计算图片宽高，保持比例
    const imgWidth = pageWidth - 20
    const imgHeight = canvas.height * imgWidth / canvas.width
    pdf.addImage(imgData, 'PNG', 10, 10, imgWidth, imgHeight)
    pdf.save('健康档案.pdf')
  })
}

function monitorRowClass({ row }) {
  return row.monitoringStatus === 'abnormal' ? 'abnormal-row' : '';
}

function isAbnormal(field) {
  if (field === 'bloodPressure' && healthForm.value.bloodPressure) {
    const [sys, dia] = healthForm.value.bloodPressure.split('/').map(Number);
    return sys > 140 || dia > 90 || sys < 90 || dia < 60;
  }
  if (field === 'bloodSugar' && healthForm.value.bloodSugar) {
    return healthForm.value.bloodSugar < 3.9 || healthForm.value.bloodSugar > 7.8;
  }
  if (field === 'temperature' && healthForm.value.temperature) {
    return healthForm.value.temperature < 36.0 || healthForm.value.temperature > 37.2;
  }
  if (field === 'heartRate' && healthForm.value.heartRate) {
    return healthForm.value.heartRate < 60 || healthForm.value.heartRate > 100;
  }
  return false;
}

onMounted(async () => {
  const isLoggedIn = await checkLoginStatus();
  if (isLoggedIn) {
    fetchHealthMonitorList();
  }
  if (monitorList.value.some(item => item.monitoringStatus === 'abnormal')) {
    ElMessage.warning('检测到健康异常，请及时关注！')
  }
})
</script>

<style scoped>
.health-view {
  width: 100%;
  transition: all 0.3s ease-in-out;
}

.content-card {
  margin-bottom: 20px;
  transition: all 0.3s ease;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

h3 {
  margin-bottom: 20px;
  color: #2c3e50;
  font-weight: 600;
  transition: color 0.3s ease;
  position: relative;
  padding-bottom: 10px;
}

h3::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 60px;
  height: 3px;
  background-color: #409EFF;
  transition: all 0.3s ease;
}

.unit {
  margin-left: 8px;
  color: #666;
  transition: color 0.3s ease;
  font-size: 13px;
}

.el-form {
  max-width: 800px;
  margin: 0 auto;
}

.el-divider {
  margin: 24px 0;
  transition: all 0.3s ease;
}

.el-divider__text {
  font-size: 16px;
  font-weight: 500;
  color: #409EFF;
  transition: color 0.3s ease;
}

.health-info {
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.03);
}

.health-info p {
  margin: 8px 0;
  font-size: 14px;
  color: #333;
  line-height: 1.6;
  transition: color 0.3s ease;
  padding: 5px 0;
  border-bottom: 1px dashed rgba(0, 0, 0, 0.05);
}

.health-info p:last-child {
  border-bottom: none;
}

.health-info strong {
  color: #409EFF;
  transition: color 0.3s ease;
  font-weight: 600;
  margin-right: 8px;
  display: inline-block;
  min-width: 80px;
}

/* 表单控件在深色模式下的优化 - 仅限健康视图页面 */
:root.dark .health-view .el-input__wrapper,
:root.dark .health-view .el-textarea__inner {
  background-color: #282828;
  box-shadow: 0 0 0 1px #383838 inset;
  transition: all 0.3s ease;
}

:root.dark .health-view .el-input__wrapper.is-focus {
  box-shadow: 0 0 0 1px #5e9eff inset, 0 0 0 2px rgba(94, 158, 255, 0.1);
}

:root.dark .health-view .el-input__inner,
:root.dark .health-view .el-textarea__inner {
  color: #e6e6e6;
}

:root.dark .health-view .el-button {
  transition: all 0.3s ease;
}

:root.dark .health-view .el-button--primary {
  background-color: #5e9eff;
  border-color: #5e9eff;
}

:root.dark .health-view .el-button--primary:hover {
  background-color: #4a8eff;
  border-color: #4a8eff;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(94, 158, 255, 0.3);
}

:root.dark .health-view .el-button--default {
  background-color: #1f1f1f;
  border-color: #383838;
  color: #e6e6e6;
}

:root.dark .health-view .el-button--default:hover {
  background-color: #2d2d2d;
  border-color: #484848;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

/* 输入框数字增减按钮 - 仅限健康视图页面 */
:root.dark .health-view .el-input-number__decrease,
:root.dark .health-view .el-input-number__increase {
  background-color: #252525;
  border-color: #383838;
  color: #bfbfbf;
  transition: all 0.2s ease;
}

:root.dark .health-view .el-input-number__decrease:hover,
:root.dark .health-view .el-input-number__increase:hover {
  color: #5e9eff;
  background-color: #2d2d2d;
}

/* 健康信息卡片动画效果 */
:root.dark .health-view .content-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px 0 rgba(0, 0, 0, 0.6);
}

/* 表单项标签 */
:root.dark .health-view .el-form-item__label {
  color: #bfbfbf;
  font-weight: 500;
}

/* 卡片标题增强 */
:root.dark .health-view .content-card h3 {
  border-bottom: 1px solid #383838;
  padding-bottom: 12px;
  font-size: 20px;
  margin-bottom: 24px;
}

/* 表单项交互效果 */
:root.dark .health-view .el-form-item {
  transition: all 0.3s ease;
  padding: 4px 0;
}

:root.dark .health-view .el-form-item:hover {
  background-color: rgba(94, 158, 255, 0.03);
  transform: translateX(4px);
  border-radius: 4px;
}

/* 分割线文本强调 */
:root.dark .health-view .el-divider__text {
  padding: 0 15px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

/* 数据展示视觉效果强化 */
:root.dark .health-view .health-info p {
  transition: all 0.3s ease;
  padding: 8px 6px;
  border-radius: 4px;
}

:root.dark .health-view .health-info p:hover {
  background-color: rgba(94, 158, 255, 0.05);
  transform: translateX(4px);
}

/* 滚动条美化 - 仅限健康视图页面 */
:root.dark .health-view ::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

:root.dark .health-view ::-webkit-scrollbar-track {
  background-color: #1a1a1a;
  border-radius: 4px;
}

:root.dark .health-view ::-webkit-scrollbar-thumb {
  background-color: #3a3a3a;
  border-radius: 4px;
}

:root.dark .health-view ::-webkit-scrollbar-thumb:hover {
  background-color: #4a4a4a;
}

/* textarea 特别优化 - 仅限健康视图页面 */
:root.dark .health-view .el-textarea__inner:focus {
  border-color: #5e9eff;
  outline: none;
}

:root.dark .health-view .el-textarea__inner {
  transition: all 0.3s ease;
}

/* 输入框焦点状态增强 - 仅限健康视图页面 */
:root.dark .health-view .el-input.is-focus .el-input__wrapper {
  box-shadow: 0 0 0 1px #5e9eff inset, 0 0 0 2px rgba(94, 158, 255, 0.1);
}

:root.dark .health-view .el-input-number.is-controls-right .el-input-number__decrease,
:root.dark .health-view .el-input-number.is-controls-right .el-input-number__increase {
  background-color: #252525;
  border-color: #383838;
}

.abnormal-row {
  background: #fff0f0 !important;
  color: #d9001b !important;
}
</style>