<template>
  <div class="elder-management">
    <el-card shadow="hover" class="table-card">
      <template #header>
        <div class="card-header">
          <h3>老人管理</h3>
          <div class="header-actions">
            <el-input v-model="searchQuery" placeholder="搜索姓名/身份证号/电话" class="search-input" clearable
              @clear="handleSearch">
              <template #prefix>
                <el-icon>
                  <Search />
                </el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button type="success" @click="handleAdd">添加老人信息</el-button>
          </div>
        </div>
      </template>

      <el-table :data="filteredElders" style="width: 100%" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="scope">
            {{ scope.row.gender === 'male' ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column prop="birthday" label="出生日期" width="100">
          <template #default="scope">
            {{ scope.row.birthday }}
          </template>
        </el-table-column>
        <el-table-column label="年龄" width="80">
          <template #default="scope">
            {{ calculateAge(scope.row.birthday) }}
          </template>
        </el-table-column>
        <el-table-column prop="idCard" label="身份证号" width="180" />
        <el-table-column prop="phone" label="联系电话" width="120" />
        <el-table-column prop="address" label="住址" show-overflow-tooltip />
        <el-table-column prop="healthCondition" label="健康状况" width="100">
          <template #default="scope">
            <el-tag :type="getHealthStatusType(scope.row.healthCondition)">
              {{ scope.row.healthCondition }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column prop="updatedAt" label="更新时间" width="180" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="success" size="small" @click="handleViewHealth(scope.row)">健康档案</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper" :total="totalElders" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <!-- 老人信息表单对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '添加老人信息' : '编辑老人信息'" width="600px">
      <el-form ref="elderFormRef" :model="elderForm" :rules="elderRules" label-width="100px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="elderForm.name" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="elderForm.gender">
            <el-radio value="male">男</el-radio>
            <el-radio value="female">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="出生日期" prop="birthday">
          <el-date-picker v-model="elderForm.birthday" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="elderForm.idCard" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="elderForm.phone" />
        </el-form-item>
        <el-form-item label="住址" prop="address">
          <el-input v-model="elderForm.address" />
        </el-form-item>
        <el-form-item label="紧急联系人" prop="emergencyContact">
          <el-input v-model="elderForm.emergencyContact" />
        </el-form-item>
        <el-form-item label="紧急电话" prop="emergencyPhone">
          <el-input v-model="elderForm.emergencyPhone" />
        </el-form-item>
        <el-form-item label="健康状况" prop="healthCondition">
          <el-select v-model="elderForm.healthCondition" placeholder="请选择健康状况">
            <el-option label="良好" value="良好" />
            <el-option label="一般" value="一般" />
            <el-option label="较差" value="较差" />
            <el-option label="需要特别关注" value="需要特别关注" />
          </el-select>
        </el-form-item>
        <el-form-item label="病史" prop="medicalHistory">
          <el-input v-model="elderForm.medicalHistory" type="textarea" :rows="3" placeholder="请输入病史信息" />
        </el-form-item>
        <el-form-item label="过敏史" prop="allergies">
          <el-input v-model="elderForm.allergies" type="textarea" :rows="2" placeholder="请输入过敏史信息" />
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input v-model="elderForm.remarks" type="textarea" :rows="2" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 查看健康档案对话框 -->
    <el-dialog v-model="healthDialogVisible" :title="healthDialogType === 'view' ? '健康档案' : '编辑健康档案'" width="700px">
      <template v-if="healthDialogType === 'view'">
        <el-descriptions :column="2" border title="基本信息" :title-class-name="'health-title'">
          <el-descriptions-item label="姓名">{{ selectedElder.name }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ selectedElder.gender === 'male' ? '男' : '女' }}</el-descriptions-item>
          <el-descriptions-item label="年龄">{{ selectedElder.age }}</el-descriptions-item>
          <el-descriptions-item label="健康状况">
            <el-tag :type="getHealthStatusType(selectedElder.healthCondition)">
              {{ selectedElder.healthCondition }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <el-descriptions :column="2" border title="健康指标" :title-class-name="'health-title'">
          <el-descriptions-item label="身高">{{ healthData.height }} cm</el-descriptions-item>
          <el-descriptions-item label="体重">{{ healthData.weight }} kg</el-descriptions-item>
          <el-descriptions-item label="血压">{{ healthData.bloodPressure }}</el-descriptions-item>
          <el-descriptions-item label="血糖">{{ healthData.bloodSugar }} mmol/L</el-descriptions-item>
          <el-descriptions-item label="心率">{{ healthData.heartRate }} 次/分</el-descriptions-item>
          <el-descriptions-item label="体温">{{ healthData.temperature }} °C</el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <el-descriptions :column="1" border title="病史信息" :title-class-name="'health-title'">
          <el-descriptions-item label="病史">{{ selectedElder.medicalHistory || '无' }}</el-descriptions-item>
          <el-descriptions-item label="过敏史">{{ selectedElder.allergies || '无' }}</el-descriptions-item>
          <el-descriptions-item label="用药情况">{{ healthData.medication || '无' }}</el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <h4 class="health-title">健康记录</h4>
        <el-timeline>
          <el-timeline-item v-for="(record, index) in healthRecords" :key="index" :timestamp="record.date"
            :type="record.type">
            {{ record.content }}
          </el-timeline-item>
        </el-timeline>
      </template>

      <template v-else>
        <el-form ref="healthFormRef" :model="healthData" label-width="100px">
          <el-form-item label="身高" prop="height">
            <el-input v-model="healthData.height" />
          </el-form-item>
          <el-form-item label="体重" prop="weight">
            <el-input v-model="healthData.weight" />
          </el-form-item>
          <el-form-item label="血压" prop="bloodPressure">
            <el-input v-model="healthData.bloodPressure" />
          </el-form-item>
          <el-form-item label="血糖" prop="bloodSugar">
            <el-input v-model="healthData.bloodSugar" />
          </el-form-item>
          <el-form-item label="心率" prop="heartRate">
            <el-input v-model="healthData.heartRate" />
          </el-form-item>
          <el-form-item label="体温" prop="temperature">
            <el-input v-model="healthData.temperature" />
          </el-form-item>
          <el-form-item label="用药情况" prop="medication">
            <el-input v-model="healthData.medication" type="textarea" :rows="2" placeholder="请输入用药情况" />
          </el-form-item>
          <el-form-item label="病史" prop="medicalHistory">
            <el-input v-model="healthData.medicalHistory" type="textarea" :rows="3" placeholder="请输入病史信息" />
          </el-form-item>
          <el-form-item label="过敏史" prop="allergies">
            <el-input v-model="healthData.allergies" type="textarea" :rows="2" placeholder="请输入过敏史信息" />
          </el-form-item>
        </el-form>
      </template>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="healthDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="healthDialogType === 'view' ? handleEditHealth() : submitHealthForm">
            {{ healthDialogType === 'view' ? '编辑' : '保存' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

import { getElders, addElder, updateElder, deleteElder } from '@/api/elder'
const filteredElders = computed(() => {
  return elders.value
})
const totalElders = computed(() => {
  console.log(elders.value)
  return elders.value ? elders.value.length : 0;
})
// 分页和搜索
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const searchQuery = ref('')

// 老人列表数据
const elders = ref([])

// 获取老人列表
const fetchElders = async () => {
  loading.value = true
  try {
    getElders({
      currentPage,
      pageSize
    }).then(result => {
      console.log(result)
      if (result.success && result.data && result.data.data.records) {
        console.log(result.data.data.records)
        const RecordsData = result.data.data.records
        // 处理日期格式
        elders.value = RecordsData.map(elder => ({
          ...elder,
          birthday: elder.birthday ? elder.birthday.split('T')[0] : '',
          createTime: elder.createTime ? elder.createTime.split('T')[0] : '',
          updateTime: elder.updateTime ? elder.updateTime.split('T')[0] : ''
        }))
      } else {
        elders.value = []
      }
    })
} catch (error) {
  ElMessage.error('获取老人列表失败')
} finally {
  loading.value = false
}
}

// 对话框相关
const dialogVisible = ref(false)
// 对话框类型
const dialogType = ref('add') // 'add' 或 'edit'
// 表单引用 
const elderFormRef = ref(null)
// 表单数据
const elderForm = reactive({
  id: '',
  name: '',
  gender: 'male',
  birthday: '',
  age: '',
  idCard: '',
  phone: '',
  address: '',
  emergencyContactName: '',
  emergencyContactPhone: '',
  healthCondition: '',
  medicalHistory: '',
  allergy: '',
  avatar: '',
  remark: ''
})

// 表单验证规则
const elderRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  birthday: [
    { required: true, message: '请选择出生日期', trigger: 'change' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入住址', trigger: 'blur' }
  ],
  emergencyContactName: [
    { required: true, message: '请输入紧急联系人', trigger: 'blur' }
  ],
  emergencyContactPhone: [
    { required: true, message: '请输入紧急联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  healthCondition: [
    { required: true, message: '请选择健康状况', trigger: 'change' }
  ],
  medicalHistory: [
    { required: true, message: '请输入病史信息', trigger: 'blur' }
  ],
  allergy: [
    { required: true, message: '请输入过敏史信息', trigger: 'blur' }
  ],
  remark: [
    { required: true, message: '请输入备注信息', trigger: 'blur' }
  ]
}

// 健康档案相关
const healthDialogVisible = ref(false)
const healthDialogType = ref('view') // 'view' 或 'edit'
const selectedElder = ref({})
const healthData = reactive({
  height: '',
  weight: '',
  bloodPressure: '',
  bloodSugar: '',
  heartRate: '',
  temperature: '',
  medication: '',
  medicalHistory: '',
  allergies: ''
})
const healthRecords = ref([
  { date: '2025-02-20', type: 'primary', content: '进行了每月健康检查，血压、血糖正常' },
  { date: '2025-01-15', type: 'success', content: '流感疫苗接种' },
  { date: '2024-12-10', type: 'warning', content: '出现轻微感冒症状，已开药治疗' },
  { date: '2024-11-05', type: 'info', content: '进行了每月健康检查，血压偏高，建议调整用药' }
])

// 获取健康状况标签类型
const getHealthStatusType = (status) => {
  const typeMap = {
    '良好': 'success',
    '一般': 'warning',
    '较差': 'danger',
    '需要特别关注': 'danger'
  }
  return typeMap[status] || 'info'
}



// 老人
const handleAdd = () => {
  dialogType.value = 'add'
  resetForm()
  dialogVisible.value = true
}

// 编辑老人
const handleEdit = (row) => {
  dialogType.value = 'edit'
  Object.keys(elderForm).forEach(key => {
    if (key === 'healthCondition') {
      elderForm[key] = row.healthCondition || ''
    } else if (key === 'allergy') {
      elderForm[key] = row.allergy || ''
    } else {
      elderForm[key] = row[key] || ''
    }
  })
  dialogVisible.value = true
}
// 提交表单
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  fetchElders()
}
// 当前页变化
const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchElders()
}
// 查看健康档案
const handleViewHealth = (row) => {
  selectedElder.value = { ...row }
  Object.keys(healthData).forEach(key => {
    healthData[key] = row[key]
  })
  healthDialogVisible.value = true
  healthDialogType.value = 'view'
}

// 编辑健康档案
const handleEditHealth = () => {
  healthDialogType.value = 'edit'
  // Ensure health data is correctly loaded from the selected elder
  healthData.height = selectedElder.value.height || ''
  healthData.weight = selectedElder.value.weight || ''
  healthData.bloodPressure = selectedElder.value.bloodPressure || ''
  healthData.bloodSugar = selectedElder.value.bloodSugar || ''
  healthData.heartRate = selectedElder.value.heartRate || ''
  healthData.temperature = selectedElder.value.temperature || ''
  healthData.medication = selectedElder.value.medication || ''
  healthData.medicalHistory = selectedElder.value.medicalHistory || ''
  healthData.allergies = selectedElder.value.allergies || ''
}

// 提交健康档案表单
const submitHealthForm = () => {
  // 这里应该调用更新健康档案的API
  ElMessage.success('健康档案更新成功')
  healthDialogVisible.value = false
}

// 删除老人
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除 ${row.name} 的信息吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteElder(row.id)
      ElMessage.success('删除成功')
      fetchElders() // 刷新列表
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 取消删除
  })
}



// 重置表单
const resetForm = () => {
  if (elderFormRef.value) {
    elderFormRef.value.resetFields()
  }
  Object.keys(elderForm).forEach(key => {
    if (key === 'gender') {
      elderForm[key] = 'male'
    } else if (key === 'healthCondition') {
      elderForm[key] = '良好'
    } else {
      elderForm[key] = ''
    }
  })
}

// 计算年龄
const calculateAge = (birthday) => {
  if (!birthday) return ''
  const today = new Date()
  const birthDate = new Date(birthday)
  let age = today.getFullYear() - birthDate.getFullYear()
  const monthDiff = today.getMonth() - birthDate.getMonth()

  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
    age--
  }

  return age
}

// 提交表单
const submitForm = () => {
  elderFormRef.value.validate(async (valid) => {
    if (valid) {
      // 计算年龄
      elderForm.age = calculateAge(elderForm.birthday)

      try {
        const formData = {
          ...elderForm,
          healthCondition: elderForm.healthCondition,
          allergy: elderForm.allergy
        }

        if (dialogType.value === 'add') {
          await addElder(formData)
          ElMessage.success('添加老人信息成功')
        } else {
          await updateElder(formData)
          ElMessage.success('更新老人信息成功')
        }
        dialogVisible.value = false
        fetchElders() // 刷新列表
      } catch (error) {
        ElMessage.error(dialogType.value === 'add' ? '添加老人信息失败' : '更新老人信息失败')
      }
    } else {
      return false
    }
  })
}

// 搜索老人
const handleSearch = () => {
  currentPage.value = 1
  fetchElders()
}

onMounted(() => {
  fetchElders()
})
</script>

<style scoped>
.elder-management {
  padding: 10px;
}

.table-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-input {
  width: 250px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.health-title {
  font-weight: bold;
  color: #409EFF;
  margin: 16px 0;
}

:deep(.health-title) {
  background-color: #f0f9ff;
  padding: 8px;
  font-weight: bold;
}
</style>