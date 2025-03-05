<template>
  <div class="elder-management">
    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h3>老人管理</h3>
          <div class="header-actions">
            <el-input v-model="searchQuery" class="search-input" clearable placeholder="搜索姓名/身份证号/电话"
              @input="handleSearch">
              <template #prefix>
                <el-icon>
                  <Search />
                </el-icon>
              </template>
            </el-input>
            <!-- 搜索按钮 -->
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <!-- 添加老人信息按钮 -->
            <el-button type="success" @click="handleAdd">添加老人信息</el-button>

          </div>
        </div>
      </template>
      <!-- 老人信息表 -->
      <el-table v-loading="loading" :data="filteredElders" border style="width: 100%">
        <el-table-column label="ID" prop="id" width="80" />
        <el-table-column label="姓名" prop="name" width="100" />
        <el-table-column label="性别" prop="gender" width="80">
          <template #default="scope">
            {{ scope.row.gender === 'male' ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column label="出生日期" prop="birthday" width="100">
          <template #default="scope">
            {{ scope.row.birthday }}
          </template>
        </el-table-column>
        <el-table-column label="年龄" width="80">
          <template #default="scope">
            {{ scope.row.age }}
          </template>
        </el-table-column>
        <el-table-column label="身份证号" prop="idCard" width="180" />
        <el-table-column label="联系电话" prop="phone" width="120" />
        <el-table-column label="住址" prop="address" show-overflow-tooltip />
        <el-table-column label="健康状况" prop="healthCondition" width="100">
          <template #default="scope">
            <el-tag :type="getHealthStatusType(scope.row.healthCondition)">
              {{ scope.row.healthCondition }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="创建时间" prop="createdAt" width="180" />
        <el-table-column label="更新时间" prop="updatedAt" width="180" />
        <el-table-column fixed="right" label="操作" width="220">
          <template #default="scope">
            <el-button size="small" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="success" @click="handleViewHealth(scope.row)">健康档案</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]"
          :total="totalElders" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <!-- 老人信息表单查看、编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '添加老人信息' : '编辑老人信息'" width="600px">
      <el-form ref="elderFormRef" :model="elderForm" :rules="elderRules" label-width="100px">
        <div class="form-section">
          <h4 class="section-title">基本信息</h4>
          <el-divider />
          <el-form-item label="姓名" prop="name">
            <el-tooltip content="请输入老人的真实姓名" placement="top">
              <el-input v-model="elderForm.name" placeholder="请输入老人的真实姓名" />
            </el-tooltip>
          </el-form-item>
          <el-form-item label="性别" prop="gender">
            <el-radio-group v-model="elderForm.gender">
              <el-radio value="male">男</el-radio>
              <el-radio value="female">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="出生日期" prop="birthday">
            <el-tooltip content="出生日期将用于自动计算年龄" placement="top">
              <div>
                <el-date-picker v-model="elderForm.birthday" placeholder="选择日期" type="date" value-format="YYYY-MM-DD" />
              </div>
            </el-tooltip>
          </el-form-item>
          <el-form-item label="身份证号" prop="idCard">
            <el-tooltip content="请输入18位有效身份证号码" placement="top">
              <el-input v-model="elderForm.idCard" placeholder="请输入18位有效身份证号码" />
            </el-tooltip>
          </el-form-item>
        </div>

        <div class="form-section">
          <h4 class="section-title">联系信息</h4>
          <el-divider />
          <el-form-item label="联系电话" prop="phone">
            <el-tooltip content="请输入11位手机号码" placement="top">
              <el-input v-model="elderForm.phone" placeholder="请输入11位手机号码" />
            </el-tooltip>
          </el-form-item>
          <el-form-item label="住址" prop="address">
            <el-tooltip content="请输入详细的居住地址，包括省市区街道门牌号" placement="top">
              <el-input v-model="elderForm.address" placeholder="请输入详细的居住地址" />
            </el-tooltip>
          </el-form-item>
          <el-form-item label="紧急联系人" prop="emergencyContact">
            <el-tooltip content="请输入紧急情况下可联系的亲属或朋友姓名" placement="top">
              <el-input v-model="elderForm.emergencyContactName" placeholder="请输入紧急联系人姓名" />
            </el-tooltip>
          </el-form-item>
          <el-form-item label="紧急电话" prop="emergencyPhone">
            <el-tooltip content="请输入紧急联系人的电话号码" placement="top">
              <el-input v-model="elderForm.emergencyContactPhone" placeholder="请输入紧急联系人电话" />
            </el-tooltip>
          </el-form-item>
        </div>


      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 查看、编辑健康档案对话框 -->
    <el-dialog v-model="healthDialogVisible" :title="healthDialogType === 'view' ? '健康档案' : '编辑健康档案'" width="700px">
      <template v-if="healthDialogType === 'view'">
        <!-- 基本信息 -->
        <el-descriptions :column="2" :title-class-name="'health-title'" border title="基本信息">
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
        <!-- 健康指标 -->
        <el-descriptions :column="2" :title-class-name="'health-title'" border title="健康指标">
          <el-descriptions-item label="身高">{{ healthData.height }} cm</el-descriptions-item>
          <el-descriptions-item label="体重">{{ healthData.weight }} kg</el-descriptions-item>
          <el-descriptions-item label="血压">{{ healthData.bloodPressure }}</el-descriptions-item>
          <el-descriptions-item label="血糖">{{ healthData.bloodSugar }} mmol/L</el-descriptions-item>
          <el-descriptions-item label="心率">{{ healthData.heartRate }} 次/分</el-descriptions-item>
          <el-descriptions-item label="体温">{{ healthData.temperature }} °C</el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <el-descriptions :column="1" :title-class-name="'health-title'" border title="病史信息">
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
            <el-tooltip content="请输入老人的身高，单位为厘米(cm)" placement="top">
              <el-input v-model="healthData.height" placeholder="请输入身高(cm)" />
            </el-tooltip>
          </el-form-item>
          <el-form-item label="体重" prop="weight">
            <el-tooltip content="请输入老人的体重，单位为千克(kg)" placement="top">
              <el-input v-model="healthData.weight" placeholder="请输入体重(kg)" />
            </el-tooltip>
          </el-form-item>
          <el-form-item label="血压" prop="bloodPressure">
            <el-tooltip content="请按照收缩压/舒张压格式填写，如120/80" placement="top">
              <el-input v-model="healthData.bloodPressure" placeholder="如：120/80 mmHg" />
            </el-tooltip>
          </el-form-item>
          <el-form-item label="血糖" prop="bloodSugar">
            <el-tooltip content="请输入空腹血糖值，单位为mmol/L" placement="top">
              <el-input v-model="healthData.bloodSugar" placeholder="请输入血糖值(mmol/L)" />
            </el-tooltip>
          </el-form-item>
          <el-form-item label="心率" prop="heartRate">
            <el-tooltip content="请输入静息心率，单位为次/分钟" placement="top">
              <el-input v-model="healthData.heartRate" placeholder="请输入心率(次/分钟)" />
            </el-tooltip>
          </el-form-item>
          <el-form-item label="体温" prop="temperature">
            <el-tooltip content="请输入体温，单位为摄氏度(°C)" placement="top">
              <el-input v-model="healthData.temperature" placeholder="请输入体温(°C)" />
            </el-tooltip>
          </el-form-item>
          <el-form-item label="用药情况" prop="medication">
            <el-tooltip content="请输入用药情况" placement="top">
              <el-input v-model="healthData.medication" :rows="2" placeholder="请输入用药情况" type="textarea" />
            </el-tooltip>
          </el-form-item>
          <el-form-item label="病史" prop="medicalHistory">
            <el-input v-model="healthData.medicalHistory" :rows="3" placeholder="请输入病史信息" type="textarea" />
          </el-form-item>
          <el-form-item label="过敏史" prop="allergies">
            <el-input v-model="healthData.allergies" :rows="2" placeholder="请输入过敏史信息" type="textarea" />
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
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

import { getElders, addElder, deleteElder, updateElder } from '@/api/elder';
import { debounce } from '@/utils/util';
// 定义响应式变量
const searchQuery = ref('')
const elders = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const dialogVisible = ref(false)
const dialogType = ref('add')
//
const elderFormRef = ref(null)
// 老人信息表单
const elderForm = ref({
  id: '',//id
  name: '',//姓名
  gender: '',//性别
  birthday: '',//出生日期
  age: '',//年龄
  idCard: '',//身份证号
  phone: '',//联系电话
  address: '',//住址
  emergencyContactName: '',//紧急联系人
  emergencyContactPhone: '',//紧急联系电话
  healthCondition: '',//健康状况
  remarks: '',//备注
  createTime: '',//创建时间
  updateTime: '',//更新时间
})
const elderRules = ref({
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
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}(\d|X)$/, message: '身份证号格式不正确', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '联系电话格式不正确', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入住址', trigger: 'blur' }
  ],
  emergencyContactName: [
    { required: true, message: '请输入紧急联系人', trigger: 'blur' }
  ],
  emergencyContactPhone: [
    { required: true, message: '请输入紧急联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '紧急联系电话格式不正确', trigger: 'blur' }
  ]
})
const healthDialogVisible = ref(false)
const healthDialogType = ref('view')
const selectedElder = ref({
  id: '',
  name: '',
  gender: '',
  birthday: '',
  idCard: '',
  phone: '',
  address: '',
})
const healthData = ref({})


const fetchElders = async () => {
  loading.value = true
  try {
    const response = await getElders({
      currentPage: currentPage.value,
      pageSize: pageSize.value
    })
    if (response.success) {
      const recordsData = response.data.data
      elders.value = recordsData.records

    } else {
      ElMessage.error('获取老人列表失败')
    }
  } catch (error) {
    ElMessage.error('获取老人列表失败')
  } finally {
    loading.value = false
  }
}
//过滤老人信息
const filteredElders = computed(() => {
  if (!searchQuery.value) {
    return elders.value;
  }

  const query = searchQuery.value.toLowerCase();
  return elders.value.filter(elder =>
    elder.name?.toLowerCase().includes(query) ||
    elder.idCard?.includes(query) ||
    elder.phone?.includes(query)
  );
})
// 计算总条数
const totalElders = computed(() => {
  return elders.value ? elders.value.length : 0;
})




// 获取健康状况标签类型
const getHealthStatusType = (status) => {
  const typeMap = {
    '高血压': 'warning',
    '糖尿病': 'danger',
    '心脏病': 'danger',
    '癌症': 'danger',
    '其他': 'info'
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
  console.log(row)
  dialogType.value = 'edit'
  Object.keys(elderForm.value).forEach(key => {
    if (key === 'healthCondition') {
      elderForm.value[key] = row.healthCondition || ''
    } else if (key === 'allergy') {
      elderForm.value[key] = row.allergy || ''
    } else {
      elderForm.value[key] = row[key] || ''
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
  healthData.value = {
    height: row.height || '',
    weight: row.weight || '',
    bloodPressure: row.bloodPressure || '',
    bloodSugar: row.bloodSugar || '',
    heartRate: row.heartRate || '',
    temperature: row.temperature || '',
    medication: row.medication || '',
    medicalHistory: row.medicalHistory || '',
    allergies: row.allergies || ''
  }
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



// 提交表单
const submitForm = () => {
  elderFormRef.value.validate(async (valid) => {
    if (valid) {
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
const handleSearch = debounce(() => {
  currentPage.value = 1;
  fetchElders();
}, 300);

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

.form-section {
  margin-bottom: 20px;
}

.section-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #409EFF;
}
</style>