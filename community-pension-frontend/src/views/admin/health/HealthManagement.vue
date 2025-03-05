<template>
    <div class="health-management">
      <el-card shadow="hover" class="table-card">
        <template #header>
          <div class="card-header">
            <h3>健康监测管理</h3>
            <div class="header-actions">
              <el-input
                v-model="searchQuery"
                placeholder="搜索姓名/身份证号"
                class="search-input"
                clearable
                @clear="handleSearch"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button type="success" @click="handleAdd">添加健康记录</el-button>
            </div>
          </div>
        </template>
        
        <el-table
          :data="filteredHealthRecords"
          style="width: 100%"
          v-loading="loading"
          border
        >
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="elderName" label="老人姓名" width="120" />
          <el-table-column prop="recordTime" label="记录时间" width="180" />
          <el-table-column prop="bloodPressure" label="血压" width="120" />
          <el-table-column prop="heartRate" label="心率" width="100">
            <template #default="scope">
              {{ scope.row.heartRate }} 次/分
            </template>
          </el-table-column>
          <el-table-column prop="bloodSugar" label="血糖" width="100">
            <template #default="scope">
              {{ scope.row.bloodSugar }} mmol/L
            </template>
          </el-table-column>
          <el-table-column prop="temperature" label="体温" width="100">
            <template #default="scope">
              {{ scope.row.temperature }} °C
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="scope">
              <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button type="success" size="small" @click="handleView(scope.row)">查看</el-button>
              <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="totalRecords"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
      
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
  import { ref, computed, reactive, onMounted } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { Search } from '@element-plus/icons-vue'
  
  // 健康记录列表数据
  const healthRecords = ref([
    {
      id: 1,
      elderId: 1,
      elderName: '张大爷',
      recordTime: '2025-02-25 09:30',
      bloodPressure: '120/80',
      heartRate: 75,
      bloodSugar: 5.6,
      temperature: 36.5,
      weight: 65,
      height: 170,
      status: '正常',
      recorder: '李护士',
      remark: '一切正常，继续保持良好的生活习惯'
    },
    {
      id: 2,
      elderId: 2,
      elderName: '李奶奶',
      recordTime: '2025-02-24 14:15',
      bloodPressure: '135/85',
      heartRate: 82,
      bloodSugar: 6.8,
      temperature: 36.7,
      weight: 58,
      height: 160,
      status: '需关注',
      recorder: '王医生',
      remark: '血糖偏高，建议减少糖分摄入，一周后复查'
    },
    {
      id: 3,
      elderId: 3,
      elderName: '王大爷',
      recordTime: '2025-02-23 10:45',
      bloodPressure: '125/75',
      heartRate: 68,
      bloodSugar: 5.2,
      temperature: 36.4,
      weight: 70,
      height: 175,
      status: '正常',
      recorder: '李护士',
      remark: '各项指标正常'
    },
    {
      id: 4,
      elderId: 4,
      elderName: '赵奶奶',
      recordTime: '2025-02-22 16:30',
      bloodPressure: '150/95',
      heartRate: 88,
      bloodSugar: 7.2,
      temperature: 37.1,
      weight: 62,
      height: 158,
      status: '异常',
      recorder: '张医生',
      remark: '血压偏高，已调整用药，需密切观察'
    }
  ])
  
  // 老人选项
  const elderOptions = ref([
    { id: 1, name: '张大爷' },
    { id: 2, name: '李奶奶' },
    { id: 3, name: '王大爷' },
    { id: 4, name: '赵奶奶' }
  ])
  
  // 分页和搜索
  const loading = ref(false)
  const currentPage = ref(1)
  const pageSize = ref(10)
  const searchQuery = ref('')
  const totalRecords = computed(() => filteredHealthRecords.value.length)
  
  // 过滤后的健康记录列表
  const filteredHealthRecords = computed(() => {
    if (!searchQuery.value) {
      return healthRecords.value
    }
    
    const query = searchQuery.value.toLowerCase()
    return healthRecords.value.filter(record => 
      record.elderName.toLowerCase().includes(query)
    )
  })
  
  // 对话框相关
  const dialogVisible = ref(false)
  const detailDialogVisible = ref(false)
  const dialogType = ref('add') // 'add' 或 'edit'
  const healthFormRef = ref(null)
  const healthForm = reactive({
    id: '',
    elderId: '',
    recordTime: '',
    bloodPressure: '',
    heartRate: 75,
    bloodSugar: 5.6,
    temperature: 36.5,
    weight: 65,
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
      { pattern: /^\d{2,3}\/\d{2,3}$/, message: '请输入正确的血压格式，如120/80', trigger: 'blur' }
    ],
    heartRate: [
      { required: true, message: '请输入心率', trigger: 'blur' }
    ],
    bloodSugar: [
      { required: true, message: '请输入血糖', trigger: 'blur' }
    ],
    temperature: [
      { required: true, message: '请输入体温', trigger: 'blur' }
    ],
    status: [
      { required: true, message: '请选择状态', trigger: 'change' }
    ]
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
  
  // 搜索健康记录
  const handleSearch = () => {
    currentPage.value = 1
  }
  
  // 添加健康记录
  const handleAdd = () => {
    dialogType.value = 'add'
    resetForm()
    dialogVisible.value = true
  }
  
  // 编辑健康记录
  const handleEdit = (row) => {
    dialogType.value = 'edit'
    Object.keys(healthForm).forEach(key => {
      if (key in row) {
        healthForm[key] = row[key]
      }
    })
    dialogVisible.value = true
    if (detailDialogVisible.value) {
      detailDialogVisible.value = false
    }
  }
  
  // 查看健康记录详情
  const handleView = (row) => {
    selectedRecord.value = { ...row }
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
      // 这里应该调用删除API
      healthRecords.value = healthRecords.value.filter(record => record.id !== row.id)
      ElMessage.success('删除成功')
    }).catch(() => {
      // 取消删除
    })
  }
  
  // 分页大小变化
  const handleSizeChange = (val) => {
    pageSize.value = val
    currentPage.value = 1
  }
  
  // 当前页变化
  const handleCurrentChange = (val) => {
    currentPage.value = val
  }
  
  // 重置表单
  const resetForm = () => {
    if (healthFormRef.value) {
      healthFormRef.value.resetFields()
    }
    Object.keys(healthForm).forEach(key => {
      if (key === 'heartRate') {
        healthForm[key] = 75
      } else if (key === 'bloodSugar') {
        healthForm[key] = 5.6
      } else if (key === 'temperature') {
        healthForm[key] = 36.5
      } else if (key === 'weight') {
        healthForm[key] = 65
      } else if (key === 'status') {
        healthForm[key] = '正常'
      } else {
        healthForm[key] = ''
      }
    })
  }
  
  // 提交表单
  const submitForm = () => {
    healthFormRef.value.validate((valid) => {
      if (valid) {
        if (dialogType.value === 'add') {
          // 添加健康记录
          const elderInfo = elderOptions.value.find(elder => elder.id === healthForm.elderId)
          const newRecord = {
            id: healthRecords.value.length + 1,
            ...healthForm,
            elderName: elderInfo ? elderInfo.name : '未知'
          }
          healthRecords.value.push(newRecord)
          ElMessage.success('添加健康记录成功')
        } else {
          // 编辑健康记录
          const index = healthRecords.value.findIndex(record => record.id === healthForm.id)
          if (index !== -1) {
            const elderInfo = elderOptions.value.find(elder => elder.id === healthForm.elderId)
            healthRecords.value[index] = { 
              ...healthForm,
              elderName: elderInfo ? elderInfo.name : healthRecords.value[index].elderName
            }
            ElMessage.success('更新健康记录成功')
          }
        }
        dialogVisible.value = false
      } else {
        return false
      }
    })
  }
  
  onMounted(() => {
    // 这里可以调用API获取健康记录列表和老人列表
  })
  </script>
  
  <style scoped>
  .health-management {
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
  
  .unit {
    margin-left: 8px;
    color: #909399;
    font-size: 14px;
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