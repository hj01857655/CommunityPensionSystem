<template>
  <div class="health-view">
    <el-card class="content-card" shadow="hover">
      <h3>健康中心</h3>
      <el-tabs v-model="activeTab" @tab-change="handleTabChange" lazy>
        <el-tab-pane label="健康监测记录" name="monitor">
          <!-- 趋势图区域 -->
          <HealthTrendChart ref="trendChartRef" :data="monitorList" :loading="monitorLoading" />
          <div style="margin-bottom: 16px; display: flex; justify-content: flex-end;">
            <el-button type="primary" @click="fetchHealthMonitorList" :loading="monitorLoading">
              <el-icon><RefreshRight /></el-icon> 刷新
            </el-button>
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
            <div v-loading="loading" class="health-info" ref="healthInfoRef">
              <template v-if="healthForm.id">
                <el-row :gutter="20">
                  <el-col :span="24">
                    <el-divider content-position="left">基础指标</el-divider>
                  </el-col>
                  <el-col :span="12">
                    <p><strong>身高:</strong> {{ healthForm.height || '未填写' }} cm</p>
                    <p><strong>体重:</strong> {{ healthForm.weight || '未填写' }} kg</p>
                  </el-col>
                  <el-col :span="12">
                    <p><strong>BMI:</strong> {{ healthForm.bmi || calculateBMI() || '未填写' }}</p>
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
                    <p><strong>既往病史:</strong> {{ healthForm.medicalHistory || '无' }}</p>
                    <p><strong>过敏史:</strong> {{ healthForm.allergy || '无' }}</p>
                  </el-col>
                  <el-col :span="12">
                    <p><strong>当前症状:</strong> {{ healthForm.symptoms || '无' }}</p>
                    <p><strong>用药情况:</strong> {{ healthForm.medication || '无' }}</p>
                  </el-col>
                </el-row>
                <div style="margin-top: 16px; display: flex; gap: 12px; align-items: center;">
                  <el-button size="small" :loading="copyLoading" @click="copyHealthInfo">
                    <el-icon><Document /></el-icon> 复制全部
                  </el-button>
                  <el-button size="small" :loading="exportLoading" @click="exportHealthInfoPDF">
                    <el-icon><Download /></el-icon> 导出PDF
                  </el-button>
                  <span style="color: #999; font-size: 13px; margin-left: 8px;">最后更新时间：{{ formatDateTime(healthForm.recordTime) }}</span>
                </div>

                <!-- 体检报告部分 -->
                <el-divider content-position="left">体检报告</el-divider>
                <PhysicalExamReport />

              </template>
              <template v-else>
                <div v-if="!loading" class="empty-data-placeholder">
                  <el-empty description="暂无健康档案数据" :image-size="120">
                    <template #description>
                      <p>您的健康档案尚未创建或加载失败</p>
                    </template>
                    <el-button type="primary" @click="fetchHealthData">创建健康档案</el-button>
                  </el-empty>
                </div>
              </template>
            </div>
            <div style="margin-top: 16px; display: flex; gap: 12px; align-items: center;">
              <el-button type="primary" :disabled="!healthForm.id" @click="toggleEditMode">
                <el-icon><Edit /></el-icon> 编辑
              </el-button>
              <el-button v-if="!loading" @click="fetchHealthData">
                <el-icon><RefreshRight /></el-icon> 刷新
              </el-button>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { addHealthData, getHealthData, updateHealthData } from '@/api/fore/health'
import { useHealthMonitorStore } from '@/stores/fore/healthMonitorStore'
import { usePhysicalExamReportStore } from '@/stores/fore/physicalExamReportStore'
import { useUserStore } from '@/stores/fore/userStore'
import { Document, Download, Edit, RefreshRight } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import html2canvas from 'html2canvas'
import jsPDF from 'jspdf'
import { computed, nextTick, onMounted, ref, watch } from 'vue'
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
const userStore = useUserStore()
const healthMonitorStore = useHealthMonitorStore()
const healthInfoRef = ref(null)

// 新增：体检报告组件ref
const physicalExamReportRef = ref(null)

// 新增：健康趋势图组件ref
const trendChartRef = ref(null)

// 健康监测store数据
const monitorList = computed(() => healthMonitorStore.monitorList)
const monitorTotal = computed(() => healthMonitorStore.total)
const monitorLoading = computed(() => healthMonitorStore.loading)

// 使用体检报告store
const physicalExamReportStore = usePhysicalExamReportStore()
const reportList = computed(() => physicalExamReportStore.reportList)
const fetchPhysicalExamReports = async () => {
  await physicalExamReportStore.fetchList({ pageNum: currentPage.value, pageSize: pageSize.value })
}

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
    
    // 获取当前时间并格式化为后端期望的格式 yyyy-MM-dd HH:mm:ss
    const now = new Date();
    const formattedDateTime = now.getFullYear() + '-' + 
      String(now.getMonth() + 1).padStart(2, '0') + '-' + 
      String(now.getDate()).padStart(2, '0') + ' ' + 
      String(now.getHours()).padStart(2, '0') + ':' + 
      String(now.getMinutes()).padStart(2, '0') + ':' + 
      String(now.getSeconds()).padStart(2, '0');
    
    // 确保健康状况字段有默认值
    const medicalHistory = healthForm.value.medicalHistory || '无';
    const allergy = healthForm.value.allergy || '无';
    const symptoms = healthForm.value.symptoms || '无';
    const medication = healthForm.value.medication || '无';
    
    // 确保ID是数值类型
    const formData = {
      ...healthForm.value,
      id: healthForm.value.id ? Number(healthForm.value.id) : null,
      recorderId: Number(localUserInfo.userId),
      elderId: Number(localUserInfo.userId), // 如果是老人，使用自己的ID
      // 确保数值类型字段是数值
      height: healthForm.value.height ? Number(healthForm.value.height) : null,
      weight: healthForm.value.weight ? Number(healthForm.value.weight) : null,
      heartRate: healthForm.value.heartRate ? Number(healthForm.value.heartRate) : null,
      bloodSugar: healthForm.value.bloodSugar ? Number(healthForm.value.bloodSugar) : null,
      temperature: healthForm.value.temperature ? Number(healthForm.value.temperature) : null,
      // 设置健康状况字段
      medicalHistory,
      allergy,
      symptoms,
      medication,
      // 使用正确格式的日期时间
      recordTime: formattedDateTime,
      symptomsRecordTime: formattedDateTime
    };

    // 计算 BMI
    if (formData.height && formData.weight) {
      const height = parseFloat(formData.height) / 100;
      const weight = parseFloat(formData.weight);
      formData.bmi = Number((weight / (height * height)).toFixed(2));
    }

    console.log('保存健康记录，参数：', JSON.stringify(formData, null, 2));

    const response = await updateHealthData(formData);
    console.log('保存响应：', response);

    if (response.code === 200) {
      ElMessage.success('保存成功');
      isEditMode.value = false;
      // 更新表单数据
      Object.assign(healthForm.value, response.data);
      
      // 打印接收到的记录时间，方便调试
      console.log('接收到的记录时间:', response.data.recordTime);
      
      // 确保健康状况字段有值
      if (!healthForm.value.medicalHistory) healthForm.value.medicalHistory = '无';
      if (!healthForm.value.allergy) healthForm.value.allergy = '无';
      if (!healthForm.value.symptoms) healthForm.value.symptoms = '无';
      if (!healthForm.value.medication) healthForm.value.medication = '无';
      
      // 如果BMI不存在，重新计算
      if (healthForm.value.height && healthForm.value.weight && !healthForm.value.bmi) {
        const height = parseFloat(healthForm.value.height) / 100;
        const weight = parseFloat(healthForm.value.weight);
        healthForm.value.bmi = Number((weight / (height * height)).toFixed(2));
        console.log('保存后计算BMI:', healthForm.value.bmi);
      }
      
      // 确保更新时间正确显示
      if (response.data.recordTime) {
        healthForm.value.recordTime = response.data.recordTime;
      } else {
        // 如果后端没有返回时间，使用当前格式化的时间
        healthForm.value.recordTime = formattedDateTime;
      }
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
    // tab切换后，等待DOM渲染再resize
    nextTick(() => {
      trendChartRef.value?.resizeChart?.()
    })
  } else if (tabName === 'profile') {
    fetchHealthData()
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

    console.log('正在获取健康数据，用户ID:', elderId);
    const response = await getHealthData(elderId);
    console.log('获取健康数据响应：', response);

    if (response.code === 200) {
      if (!response.data) {
        console.log('无健康记录，创建新记录');
        // 如果没有健康记录，创建新的
        await createNewHealthRecord();
      } else {
        // 打印接收到的健康数据，方便调试
        console.log('获取到的健康数据详情:', response.data);
        
        // 确保接收到的数据中的时间字段能正确显示
        Object.assign(healthForm.value, response.data);
        
        // 确保健康状况字段有默认值
        if (!healthForm.value.medicalHistory) healthForm.value.medicalHistory = '无';
        if (!healthForm.value.allergy) healthForm.value.allergy = '无';
        if (!healthForm.value.symptoms) healthForm.value.symptoms = '无';
        if (!healthForm.value.medication) healthForm.value.medication = '无';
        
        // 如果身高和体重存在但BMI不存在，计算BMI
        if (healthForm.value.height && healthForm.value.weight && !healthForm.value.bmi) {
          const height = parseFloat(healthForm.value.height) / 100;
          const weight = parseFloat(healthForm.value.weight);
          healthForm.value.bmi = Number((weight / (height * height)).toFixed(2));
          console.log('计算BMI:', healthForm.value.bmi);
        }
        
        // 如果recordTime存在但格式不正确，进行格式化
        if (healthForm.value.recordTime) {
          console.log('原始recordTime:', healthForm.value.recordTime);
          // 注意：不需要额外处理，因为我们已经有formatDateTime函数来处理显示逻辑
        }

        console.log('健康数据加载完成，当前数据:', healthForm.value);
      }
    } else {
      console.error('获取健康数据失败，错误码:', response.code, '错误信息:', response.message);
      throw new Error(response.message || '获取健康数据失败');
    }
  } catch (error) {
    console.error('获取健康数据失败:', error);
    // 尝试创建新记录作为后备方案
    if (!healthForm.value.id) {
      try {
        console.log('尝试作为后备方案创建新记录');
        await createNewHealthRecord();
      } catch (createError) {
        console.error('创建新记录失败:', createError);
        ElMessage.error('无法获取或创建健康记录，请刷新页面重试');
      }
    } else {
      ElMessage.error(error.response?.data?.message || error.message || '获取健康数据失败');
    }
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

    // 获取当前时间并格式化为后端期望的格式 yyyy-MM-dd HH:mm:ss
    const now = new Date();
    const formattedDateTime = now.getFullYear() + '-' + 
      String(now.getMonth() + 1).padStart(2, '0') + '-' + 
      String(now.getDate()).padStart(2, '0') + ' ' + 
      String(now.getHours()).padStart(2, '0') + ':' + 
      String(now.getMinutes()).padStart(2, '0') + ':' + 
      String(now.getSeconds()).padStart(2, '0');

    // 设置默认值
    const height = 170; // 默认身高170cm
    const weight = 65;  // 默认体重65kg

    // 计算BMI
    const heightInMeters = height / 100;
    const bmi = Number((weight / (heightInMeters * heightInMeters)).toFixed(2));

    const newHealthRecord = {
      elderId,
      recorderId,
      height: height,
      weight: weight,
      bloodPressure: '120/80',
      bloodSugar: 5.6,
      heartRate: 75,
      temperature: 36.5,
      bmi: bmi,
      medicalHistory: '无',
      allergy: '无',
      symptoms: '无',
      medication: '无',
      recordType: '日常监测',
      recordTime: formattedDateTime,
      symptomsRecordTime: formattedDateTime
    }

    console.log('创建新健康记录，参数：', JSON.stringify(newHealthRecord, null, 2));

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

const formatDateTime = (dateTime) => {
  if (!dateTime) return '无';
  
  // 处理不同格式的日期时间
  if (typeof dateTime === 'string') {
    // 如果是ISO格式（包含T）
    if (dateTime.includes('T')) {
      return dateTime.replace('T', ' ').slice(0, 19);
    }
    
    // 如果是标准格式（yyyy-MM-dd HH:mm:ss）
    if (/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/.test(dateTime)) {
      return dateTime;
    }
    
    // 如果只有日期没有时间（yyyy-MM-dd）
    if (/^\d{4}-\d{2}-\d{2}$/.test(dateTime)) {
      return dateTime + ' 00:00:00';
    }
    
    // 尝试使用Date对象解析
    try {
      const date = new Date(dateTime);
      if (!isNaN(date.getTime())) {
        return date.toISOString().replace('T', ' ').slice(0, 19);
      }
    } catch (e) {
      console.error('日期解析失败:', e, dateTime);
    }
    
    // 其他情况，直接返回
    return dateTime;
  }
  
  // 如果是Date对象
  if (dateTime instanceof Date) {
    return dateTime.toISOString().replace('T', ' ').slice(0, 19);
  }
  
  // 如果是时间戳
  if (typeof dateTime === 'number') {
    try {
      const date = new Date(dateTime);
      return date.toISOString().replace('T', ' ').slice(0, 19);
    } catch (e) {
      console.error('时间戳解析失败:', e, dateTime);
    }
  }
  
  // 如果都不是，返回原始值的字符串表示
  return String(dateTime);
}

const copyLoading = ref(false)
const exportLoading = ref(false)

function copyHealthInfo() {
  copyLoading.value = true
  const info = [
    '【基础指标】',
    `身高: ${healthForm.value.height || '未填写'} cm`,
    `体重: ${healthForm.value.weight || '未填写'} kg`,
    `BMI: ${healthForm.value.bmi || calculateBMI() || '未填写'}`,
    '【生命体征】',
    `血压: ${healthForm.value.bloodPressure || '未填写'} mmHg`,
    `心率: ${healthForm.value.heartRate || '未填写'} 次/分`,
    `血糖: ${healthForm.value.bloodSugar || '未填写'} mmol/L`,
    `体温: ${healthForm.value.temperature || '未填写'} ℃`,
    '【健康状况】',
    `既往病史: ${healthForm.value.medicalHistory || '无'}`,
    `过敏史: ${healthForm.value.allergy || '无'}`,
    `当前症状: ${healthForm.value.symptoms || '无'}`,
    `用药情况: ${healthForm.value.medication || '无'}`,
    `最后更新时间: ${formatDateTime(healthForm.value.recordTime)}`
  ].join('\n')
  
  navigator.clipboard.writeText(info).then(() => {
    ElMessage.success('健康档案已复制到剪贴板')
  }).catch((err) => {
    console.error('复制失败:', err);
    ElMessage.error('复制失败，请重试')
  }).finally(() => {
    copyLoading.value = false
  })
}

function exportHealthInfoPDF() {
  const el = healthInfoRef.value
  if (!el) return
  
  exportLoading.value = true
  ElMessage.info('正在生成PDF，请稍候...')
  
  html2canvas(el, { scale: 2 }).then(canvas => {
    try {
      const imgData = canvas.toDataURL('image/png')
      const pdf = new jsPDF('p', 'mm', 'a4')
      const pageWidth = pdf.internal.pageSize.getWidth()
      const pageHeight = pdf.internal.pageSize.getHeight()
      // 计算图片宽高，保持比例
      const imgWidth = pageWidth - 20
      const imgHeight = canvas.height * imgWidth / canvas.width
      pdf.addImage(imgData, 'PNG', 10, 10, imgWidth, imgHeight)
      pdf.save('健康档案.pdf')
      ElMessage.success('健康档案已导出为PDF')
    } catch (err) {
      console.error('导出PDF失败:', err)
      ElMessage.error('导出PDF失败，请重试')
    } finally {
      exportLoading.value = false
    }
  }).catch(err => {
    console.error('生成画布失败:', err)
    ElMessage.error('导出PDF失败，请重试')
    exportLoading.value = false
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

function calculateBMI() {
  if (healthForm.value.height && healthForm.value.weight) {
    const height = parseFloat(healthForm.value.height) / 100;
    const weight = parseFloat(healthForm.value.weight);
    const bmi = Number((weight / (height * height)).toFixed(2));
    return bmi;
  }
  return null;
}

// 检查健康档案是否完整
const isHealthDataComplete = computed(() => {
  return healthForm.value.id && 
         healthForm.value.height && 
         healthForm.value.weight && 
         healthForm.value.bloodPressure && 
         healthForm.value.heartRate && 
         healthForm.value.medicalHistory && 
         healthForm.value.allergy && 
         healthForm.value.symptoms && 
         healthForm.value.medication;
});

// 健康数据检查和自动重试
const checkAndRetryLoadHealthData = () => {
  // 如果当前是健康档案Tab但数据不完整
  if (activeTab.value === 'profile' && !isHealthDataComplete.value) {
    console.log('健康档案数据不完整，尝试重新获取...', healthForm.value);
    
    // 检查是否有ID但其他数据不完整，这表明数据可能有问题
    if (healthForm.value.id && (!healthForm.value.medicalHistory || !healthForm.value.medication)) {
      console.log('健康档案ID存在但数据不完整，重新获取');
      setTimeout(() => {
        fetchHealthData();
      }, 800);
    } 
    // 如果完全没有数据，可能是第一次加载
    else if (!healthForm.value.id) {
      console.log('健康档案ID不存在，尝试创建新记录');
      setTimeout(() => {
        // 如果loadAttempts小于最大尝试次数，继续尝试加载
        if (loadAttempts.value < maxLoadAttempts) {
          loadAttempts.value++;
          fetchHealthData();
        } else {
          console.log('达到最大尝试次数，停止尝试');
          ElMessage.warning('健康档案加载失败，请点击刷新按钮重试');
        }
      }, 1000);
    }
  }
};

const loadAttempts = ref(0);
const maxLoadAttempts = 3;

// 添加初始化函数，确保在挂载时加载数据
const initHealthData = async () => {
  const isLoggedIn = await checkLoginStatus();
  if (!isLoggedIn) return;
  
  loadAttempts.value = 0;
  console.log('初始化健康数据，当前活动Tab:', activeTab.value);
  
  try {
    // 如果当前是健康档案Tab，优先加载健康档案数据
    if (activeTab.value === 'profile') {
      await fetchHealthData();
      // 加载后检查数据是否完整
      setTimeout(() => {
        checkAndRetryLoadHealthData();
      }, 1000);
    } 
    // 如果当前是监测记录Tab，加载监测数据
    else if (activeTab.value === 'monitor') {
      await fetchHealthMonitorList();
    }
  } catch (error) {
    console.error('初始化健康数据失败:', error);
    ElMessage.error('初始化健康数据失败，请刷新页面重试');
  }
}

onMounted(async () => {
  // 初始化健康数据
  await initHealthData();
  
  // 检查是否有异常监测数据
  if (monitorList.value.some(item => item.monitoringStatus === 'abnormal')) {
    ElMessage.warning('检测到健康异常，请及时关注！');
  }

  // 在组件挂载时获取体检报告数据
  await fetchPhysicalExamReports();
})

// 监听Tab变化，确保数据加载完整
watch(activeTab, (newTab) => {
  if (newTab === 'profile') {
    // 如果切换到健康档案Tab，检查数据是否完整
    nextTick(() => {
      checkAndRetryLoadHealthData();
    });
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

.empty-data-placeholder {
  padding: 40px 20px;
  text-align: center;
}

.empty-data-placeholder p {
  margin: 12px 0;
  color: #909399;
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