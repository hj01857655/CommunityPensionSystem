<template>
  <div class="app-container">
    <!-- 搜索条件 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="老人姓名" prop="elderName">
        <el-input v-model="queryParams.elderName" placeholder="请输入老人姓名" clearable style="width: 200px"
          @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="健康状态" clearable style="width: 200px">
          <el-option label="正常" value="正常" />
          <el-option label="异常" value="异常" />
          <el-option label="需关注" value="需关注" />
        </el-select>
      </el-form-item>
      <el-form-item label="记录时间">
        <el-date-picker v-model="dateRange" style="width: 240px" value-format="YYYY-MM-DD" type="daterange"
          range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
        <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain :icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain :icon="Edit" :disabled="single" @click="handleEdit(selectedRow)">编辑</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain :icon="Delete" :disabled="multiple" @click="handleBatchDelete">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain :icon="View" @click="handleView(selectedRow)" :disabled="single">查看</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain :icon="Download" @click="handleExport">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
    </el-row>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="healthRecords" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" v-if="columns[0].visible" />
      <el-table-column prop="id" label="ID" width="80" align="center" v-if="columns[1].visible" />
      <el-table-column prop="elderName" label="老人姓名" width="120" align="center" v-if="columns[2].visible" />
      <el-table-column prop="recordTime" label="记录时间" width="180" align="center" v-if="columns[3].visible" />
      <el-table-column prop="bloodPressure" label="血压" width="120" align="center" v-if="columns[4].visible" />
      <el-table-column prop="heartRate" label="心率" width="100" align="center" v-if="columns[5].visible">
        <template #default="scope">
          {{ scope.row.heartRate }} 次/分
        </template>
      </el-table-column>
      <el-table-column prop="bloodSugar" label="血糖" width="100" align="center" v-if="columns[6].visible">
        <template #default="scope">
          {{ scope.row.bloodSugar }} mmol/L
        </template>
      </el-table-column>
      <el-table-column prop="temperature" label="体温" width="100" align="center" v-if="columns[7].visible">
        <template #default="scope">
          {{ scope.row.temperature }} °C
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100" align="center" v-if="columns[8].visible">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="220" v-if="columns[9].visible">
        <template #default="scope">
          <el-button type="primary" link :icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="primary" link :icon="View" @click="handleView(scope.row)">查看</el-button>
          <el-button type="primary" link :icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <pagination
      v-if="totalRecords > 0"
      :total="totalRecords"
      :page="queryParams.current"
      :limit="queryParams.size"
      @pagination="getList"
    />
      
    <!-- 健康记录表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加健康记录' : '编辑健康记录'"
      width="600px"
    >
      <el-form
        ref="healthFormRef"
        :model="healthForm"
        :rules="healthRules"
        label-width="100px"
      >
        <el-form-item label="老人" prop="elderId">
          <el-select v-model="healthForm.elderId" placeholder="请选择老人">
            <el-option
              v-for="elder in elderOptions"
              :key="elder.id"
              :label="elder.name"
              :value="elder.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="记录时间" prop="recordTime">
          <el-date-picker 
            v-model="healthForm.recordTime" 
            type="datetime" 
            placeholder="选择日期和时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm"
          />
        </el-form-item>
        <el-form-item label="血压" prop="bloodPressure">
          <el-input v-model="healthForm.bloodPressure" placeholder="例如：120/80" />
        </el-form-item>
        <el-form-item label="心率" prop="heartRate">
          <el-input-number v-model="healthForm.heartRate" :min="40" :max="200" />
          <span class="unit">次/分</span>
        </el-form-item>
        <el-form-item label="血糖" prop="bloodSugar">
          <el-input-number v-model="healthForm.bloodSugar" :min="1" :max="30" :precision="1" :step="0.1" />
          <span class="unit">mmol/L</span>
        </el-form-item>
        <el-form-item label="体温" prop="temperature">
          <el-input-number v-model="healthForm.temperature" :min="35" :max="42" :precision="1" :step="0.1" />
          <span class="unit">°C</span>
        </el-form-item>
        <el-form-item label="体重" prop="weight">
          <el-input-number v-model="healthForm.weight" :min="30" :max="150" :precision="1" :step="0.1" />
          <span class="unit">kg</span>
        </el-form-item>
        <el-form-item label="身高" prop="height">
          <el-input-number v-model="healthForm.height" :min="100" :max="250" :precision="1" :step="0.1" />
          <span class="unit">cm</span>
        </el-form-item>
        <el-form-item label="BMI" prop="bmi">
          <el-input-number v-model="healthForm.bmi" :min="10" :max="50" :precision="1" :step="0.1" disabled />
        </el-form-item>
        <el-form-item label="病史" prop="medicalHistory">
          <el-input 
            v-model="healthForm.medicalHistory" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入病史"
          />
        </el-form-item>
        <el-form-item label="过敏史" prop="allergy">
          <el-input 
            v-model="healthForm.allergy" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入过敏史"
          />
        </el-form-item>
        <el-form-item label="症状描述" prop="symptoms">
          <el-input 
            v-model="healthForm.symptoms" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入症状描述"
          />
        </el-form-item>
        <el-form-item label="用药情况" prop="medication">
          <el-input 
            v-model="healthForm.medication" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入用药情况"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="healthForm.status">
            <el-option label="正常" value="正常" />
            <el-option label="异常" value="异常" />
            <el-option label="需关注" value="需关注" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input 
            v-model="healthForm.remark" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 健康记录详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="健康记录详情"
      width="700px"
    >
      <el-descriptions 
        :column="2" 
        border 
        title="基本信息"
        :title-class-name="'health-title'"
      >
        <el-descriptions-item label="老人姓名">{{ selectedRecord.elderName }}</el-descriptions-item>
        <el-descriptions-item label="记录时间">{{ selectedRecord.recordTime }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(selectedRecord.status)">
            {{ selectedRecord.status }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="记录人员">{{ selectedRecord.recorder || '系统记录' }}</el-descriptions-item>
      </el-descriptions>
      
      <el-divider />
      
      <el-descriptions 
        :column="2" 
        border 
        title="健康指标"
        :title-class-name="'health-title'"
      >
        <el-descriptions-item label="血压">{{ selectedRecord.bloodPressure }}</el-descriptions-item>
        <el-descriptions-item label="心率">{{ selectedRecord.heartRate }} 次/分</el-descriptions-item>
        <el-descriptions-item label="血糖">{{ selectedRecord.bloodSugar }} mmol/L</el-descriptions-item>
        <el-descriptions-item label="体温">{{ selectedRecord.temperature }} °C</el-descriptions-item>
        <el-descriptions-item label="体重">{{ selectedRecord.weight }} kg</el-descriptions-item>
        <el-descriptions-item label="身高">{{ selectedRecord.height || '--' }} cm</el-descriptions-item>
      </el-descriptions>
      
      <el-divider />
      
      <el-descriptions 
        :column="1" 
        border 
        title="备注信息"
        :title-class-name="'health-title'"
      >
        <el-descriptions-item label="备注">{{ selectedRecord.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="handleEdit(selectedRecord)">编辑</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, View, Download } from '@element-plus/icons-vue'
import { useRoute } from 'vue-router'
import { useHealthStore } from '@/stores/back/healthStore'
import RightToolbar from '@/components/RightToolbar/index.vue'
import Pagination from '@/components/common/Pagination.vue'

const healthStore = useHealthStore()

// 遮罩层
const loading = ref(false)
// 选中数组
const selectedRows = ref([])
// 非单个禁用
const single = computed(() => selectedRows.value.length !== 1)
// 非多个禁用
const multiple = computed(() => selectedRows.value.length === 0)
// 显示搜索条件
const showSearch = ref(true)
// 选中行数据
const selectedRow = computed(() => selectedRows.value.length === 1 ? selectedRows.value[0] : null)
// 日期范围
const dateRange = ref([])

// 列显示控制
const columns = ref([
  { key: 0, label: '选择列', visible: true },
  { key: 1, label: 'ID', visible: true },
  { key: 2, label: '老人姓名', visible: true },
  { key: 3, label: '记录时间', visible: true },
  { key: 4, label: '血压', visible: true },
  { key: 5, label: '心率', visible: true },
  { key: 6, label: '血糖', visible: true },
  { key: 7, label: '体温', visible: true },
  { key: 8, label: '状态', visible: true },
  { key: 9, label: '操作', visible: true }
])

// 查询参数
const queryParams = ref({
  elderName: '',
  status: '',
  startDate: '',
  endDate: '',
  current: 1,
  size: 10
})

// 健康记录列表数据
const healthRecords = computed(() => healthStore.healthRecords)
const elderOptions = ref([])
const totalRecords = computed(() => healthStore.totalRecords)

// 对话框相关
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const dialogType = ref('add') // 'add' 或 'edit'
const healthFormRef = ref(null)
const healthForm = ref({
  elderId: '',
  recordTime: '',
  bloodPressure: '',
  heartRate: 75,
  bloodSugar: 5.6,
  temperature: 36.5,
  weight: 65,
  height: 170,
  bmi: 0,
  medicalHistory: '',
  allergy: '',
  symptoms: '',
  medication: '',
  status: '正常',
  remark: ''
})
const selectedRecord = ref({})

// 表单验证规则
const healthRules = {
  elderId: [
    { required: true, message: '请选择老人', trigger: 'change' }
  ],
  recordTime: [
    { required: true, message: '请选择记录时间', trigger: 'change' }
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
  heartRate: [
    { required: true, message: '请输入心率', trigger: 'blur' },
    { type: 'number', min: 40, max: 200, message: '心率应在40-200次/分钟之间', trigger: 'blur' }
  ],
  bloodSugar: [
    { required: true, message: '请输入血糖', trigger: 'blur' },
    { type: 'number', min: 2, max: 30, message: '血糖值应在2-30mmol/L之间', trigger: 'blur' }
  ],
  temperature: [
    { required: true, message: '请输入体温', trigger: 'blur' },
    { type: 'number', min: 35, max: 42, message: '体温应在35-42°C之间', trigger: 'blur' }
  ],
  weight: [
    { required: true, message: '请输入体重', trigger: 'blur' },
    { type: 'number', min: 30, max: 150, message: '体重应在30-150kg之间', trigger: 'blur' }
  ],
  height: [
    { required: true, message: '请输入身高', trigger: 'blur' },
    { type: 'number', min: 100, max: 250, message: '身高应在100-250cm之间', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ],
  remark: [
    { max: 500, message: '备注信息不能超过500个字符', trigger: 'blur' }
  ]
}

// 监听日期范围变化
watch(dateRange, (val) => {
  queryParams.value.startDate = val && val.length > 0 ? val[0] : ''
  queryParams.value.endDate = val && val.length > 0 ? val[1] : ''
})

// 多选框选中数据
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 获取状态标签类型
const getStatusType = (status) => {
  const typeMap = {
    '正常': 'success',
    '需关注': 'warning',
    '异常': 'danger'
  }
  return typeMap[status] || 'info'
}

// 查询列表
const getList = () => {
  loading.value = true
  // 处理查询参数
  const params = {
    elderName: queryParams.value.elderName,
    status: queryParams.value.status,
    startDate: queryParams.value.startDate,
    endDate: queryParams.value.endDate,
    current: queryParams.value.current,
    size: queryParams.value.size
  }
  
  healthStore.fetchAllHealthRecords(params.current, params.size, params)
    .then(() => {
      loading.value = false
    })
    .catch(() => {
      loading.value = false
    })
}

// 搜索按钮操作
const handleQuery = () => {
  queryParams.value.current = 1
  getList()
}

// 重置按钮操作
const resetQuery = () => {
  dateRange.value = []
  queryParams.value = {
    elderName: '',
    status: '',
    startDate: '',
    endDate: '',
    current: 1,
    size: 10
  }
  handleQuery()
}

// 添加健康记录
const handleAdd = () => {
  dialogType.value = 'add'
  resetForm()
  dialogVisible.value = true
}

// 编辑健康记录
const handleEdit = (row) => {
  if (!row && selectedRows.value.length !== 1) {
    return
  }
  const rowData = row || selectedRows.value[0]
  dialogType.value = 'edit'
  Object.keys(healthForm.value).forEach(key => {
    if (key in rowData) {
      healthForm.value[key] = rowData[key]
    }
  })
  dialogVisible.value = true
  if (detailDialogVisible.value) {
    detailDialogVisible.value = false
  }
}

// 查看健康记录详情
const handleView = (row) => {
  if (!row && selectedRows.value.length !== 1) {
    return
  }
  const rowData = row || selectedRows.value[0]
  selectedRecord.value = { ...rowData }
  detailDialogVisible.value = true
}

// 删除健康记录
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除 ${row.elderName} 的健康记录吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    healthStore.deleteHealthRecord(row.id)
      .then(() => {
        ElMessage.success('删除成功')
        getList()
      })
      .catch(error => {
        ElMessage.error(`删除失败: ${error.message}`)
      })
  }).catch(() => {
    // 取消删除
  })
}

// 批量删除健康记录
const handleBatchDelete = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请至少选择一条记录')
    return
  }
  
  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedRows.value.length} 条健康记录吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    const ids = selectedRows.value.map(item => item.id)
    // 这里可以调用批量删除API
    ElMessage.success('批量删除成功')
    getList()
  }).catch(() => {
    // 取消删除
  })
}

// 导出数据
const handleExport = () => {
  ElMessageBox.confirm('确认导出所有健康记录数据?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      ElMessage({
        type: 'success',
        message: '导出成功',
      })
      // 这里应该调用导出API
    })
    .catch(() => {})
}

// 分页大小变化
const handleSizeChange = (val) => {
  queryParams.value.size = val
  getList()
}

// 当前页变化
const handleCurrentChange = (val) => {
  queryParams.value.current = val
  getList()
}

// 重置表单
const resetForm = () => {
  if (healthFormRef.value) {
    healthFormRef.value.resetFields()
  }
  Object.keys(healthForm.value).forEach(key => {
    if (key === 'heartRate') {
      healthForm.value[key] = 75
    } else if (key === 'bloodSugar') {
      healthForm.value[key] = 5.6
    } else if (key === 'temperature') {
      healthForm.value[key] = 36.5
    } else if (key === 'weight') {
      healthForm.value[key] = 65
    } else if (key === 'height') {
      healthForm.value[key] = 170
    } else if (key === 'bmi') {
      healthForm.value[key] = 0
    } else if (key === 'status') {
      healthForm.value[key] = '正常'
    } else {
      healthForm.value[key] = ''
    }
  })
}

// 提交表单
const submitForm = () => {
  healthFormRef.value.validate((valid) => {
    if (valid) {
      const apiCall = dialogType.value === 'add'
        ? healthStore.addHealthRecord(healthForm.value)
        : healthStore.updateHealthRecord(healthForm.value)

      apiCall.then(() => {
        ElMessage.success(dialogType.value === 'add' ? '添加健康记录成功' : '更新健康记录成功')
        dialogVisible.value = false
        getList()
      }).catch(error => {
        ElMessage.error(`操作失败: ${error.message}`)
      })
    }
  })
}

onMounted(() => {
  getList()
  healthStore.fetchElderOptions()
})

function getElderIdFromSource() {
  const route = useRoute();
  return route.params.elderId;
}
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.mb8 {
  margin-bottom: 8px;
}

.unit {
  margin-left: 8px;
  color: #606266;
}

.health-title {
  font-weight: 600;
}

:deep(.el-tag) {
  border-radius: 4px;
}
</style>
