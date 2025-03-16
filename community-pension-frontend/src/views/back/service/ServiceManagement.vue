<template>
    <div class="service-management">
      <el-card shadow="hover" class="table-card">
        <template #header>
          <div class="card-header">
            <h3>服务预约管理</h3>
            <div class="header-actions">
              <el-input
                v-model="searchQuery"
                placeholder="搜索服务名称"
                class="search-input"
                clearable
                @clear="handleSearch"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button type="success" @click="handleAdd">添加服务</el-button>
            </div>
          </div>
        </template>
        
        <el-tabs v-model="activeTab" @tab-click="handleTabClick">
          <el-tab-pane label="服务项目类型" name="services">
            <el-table
              :data="filteredServices"
              style="width: 100%"
              v-loading="loading"
              border
            >
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="name" label="服务名称" min-width="150" />
              <el-table-column prop="category" label="类别" width="120">
                <template #default="scope">
                  <el-tag :type="getCategoryType(scope.row.category)">
                    {{ scope.row.category }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="price" label="价格" width="120">
                <template #default="scope">
                  {{ scope.row.price }} 元/次
                </template>
              </el-table-column>
              <el-table-column prop="duration" label="时长" width="120">
                <template #default="scope">
                  {{ scope.row.duration }} 分钟
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="100">
                <template #default="scope">
                  <el-switch
                    v-model="scope.row.status"
                    :active-value="'可用'"
                    :inactive-value="'停用'"
                    @change="handleStatusChange(scope.row)"
                  />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="200" fixed="right">
                <template #default="scope">
                  <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
                  <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
          
          <el-tab-pane label="服务预约" name="appointments">
            <el-table
              :data="filteredAppointments"
              style="width: 100%"
              v-loading="loading"
              border
            >
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="elderName" label="老人姓名" width="120" />
              <el-table-column prop="serviceName" label="服务项目" min-width="150" />
              <el-table-column prop="appointmentTime" label="预约时间" width="180" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="getAppointmentStatusType(scope.row.status)">
                    {{ scope.row.status }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="staffName" label="服务人员" width="120" />
              <el-table-column label="操作" width="250" fixed="right">
                <template #default="scope">
                  <el-button 
                    type="primary" 
                    size="small" 
                    @click="handleViewAppointment(scope.row)"
                  >
                    查看
                  </el-button>
                  <el-button 
                    type="success" 
                    size="small" 
                    :disabled="scope.row.status !== '待确认'"
                    @click="handleConfirmAppointment(scope.row)"
                  >
                    确认
                  </el-button>
                  <el-button 
                    type="danger" 
                    size="small" 
                    :disabled="scope.row.status === '已完成' || scope.row.status === '已取消'"
                    @click="handleCancelAppointment(scope.row)"
                  >
                    取消
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
          <el-tab-pane label="服务评价" name="serviceEvaluation">
            <el-table
              :data="filteredServiceEvaluations"
              style="width: 100%"
              v-loading="loading"
              border
            >
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="elderName" label="老人姓名" width="120" />
              <el-table-column prop="serviceName" label="服务项目" min-width="150" />
              <el-table-column prop="rating" label="评分" width="100" />
              <el-table-column prop="comment" label="评价内容" min-width="200" />
            </el-table>
          </el-tab-pane>
        </el-tabs>
        
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="totalItems"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
      
      <!-- 服务表单对话框 -->
      <el-dialog
        v-model="serviceDialogVisible"
        :title="dialogType === 'add' ? '添加服务' : '编辑服务'"
        width="600px"
      >
        <el-form
          ref="serviceFormRef"
          :model="serviceForm"
          :rules="serviceRules"
          label-width="100px"
        >
          <el-form-item label="服务名称" prop="name">
            <el-input v-model="serviceForm.name" />
          </el-form-item>
          <el-form-item label="服务类别" prop="category">
            <el-select v-model="serviceForm.category" placeholder="请选择服务类别">
              <el-option label="生活照料" value="生活照料" />
              <el-option label="医疗护理" value="医疗护理" />
              <el-option label="康复训练" value="康复训练" />
              <el-option label="心理慰藉" value="心理慰藉" />
              <el-option label="文化娱乐" value="文化娱乐" />
            </el-select>
          </el-form-item>
          <el-form-item label="服务价格" prop="price">
            <el-input-number v-model="serviceForm.price" :min="0" :precision="2" :step="10" />
            <span class="unit">元/次</span>
          </el-form-item>
          <el-form-item label="服务时长" prop="duration">
            <el-input-number v-model="serviceForm.duration" :min="0" :step="15" />
            <span class="unit">分钟</span>
          </el-form-item>
          <el-form-item label="服务描述" prop="description">
            <el-input 
              v-model="serviceForm.description" 
              type="textarea" 
              :rows="4" 
            />
          </el-form-item>
          <el-form-item label="服务状态" prop="status">
            <el-radio-group v-model="serviceForm.status">
              <el-radio value="可用" label="可用">可用</el-radio>
              <el-radio value="停用" label="停用">停用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="serviceDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitServiceForm">确定</el-button>
          </span>
        </template>
      </el-dialog>
      
      <!-- 预约详情对话框 -->
      <el-dialog
        v-model="appointmentDialogVisible"
        title="预约详情"
        width="700px"
      >
        <el-descriptions :column="2" border>
          <el-descriptions-item label="预约编号">{{ selectedAppointment.id }}</el-descriptions-item>
          <el-descriptions-item label="老人姓名">{{ selectedAppointment.elderName }}</el-descriptions-item>
          <el-descriptions-item label="服务项目">{{ selectedAppointment.serviceName }}</el-descriptions-item>
          <el-descriptions-item label="预约时间">{{ selectedAppointment.appointmentTime }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ selectedAppointment.phone }}</el-descriptions-item>
          <el-descriptions-item label="服务地址">{{ selectedAppointment.address }}</el-descriptions-item>
          <el-descriptions-item label="服务人员">{{ selectedAppointment.staffName || '未分配' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getAppointmentStatusType(selectedAppointment.status)">
              {{ selectedAppointment.status }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ selectedAppointment.remark || '无' }}</el-descriptions-item>
        </el-descriptions>
        
        <template v-if="selectedAppointment.status === '待确认'">
          <el-divider content-position="center">分配服务人员</el-divider>
          <el-form :model="assignForm" label-width="100px">
            <el-form-item label="服务人员">
              <el-select v-model="assignForm.staffId" placeholder="请选择服务人员">
                <el-option
                  v-for="staff in staffOptions"
                  :key="staff.id"
                  :label="staff.name"
                  :value="staff.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="assignForm.remark" type="textarea" :rows="2" />
            </el-form-item>
          </el-form>
        </template>
        
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="appointmentDialogVisible = false">关闭</el-button>
            <template v-if="selectedAppointment.status === '待确认'">
              <el-button type="success" @click="handleAssignStaff">确认并分配</el-button>
              <el-button type="danger" @click="handleCancelAppointment(selectedAppointment)">取消预约</el-button>
            </template>
          </span>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <script setup>
  import { ref, computed, reactive, onMounted } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { Search } from '@element-plus/icons-vue'
  
  // 标签页
  const activeTab = ref('services')
  const filteredServiceEvaluations = ref([
    {
      id:1,
      elderName:'张大爷',
      serviceName:'上门送餐',
      rating:4.5,
      comment:'服务非常周到，饭菜可口，老人非常满意'
    },
    {
      id:2,
      elderName:'李奶奶',
      serviceName:'健康体检',
      rating:4.8,
      comment:'医生非常专业，检查细致，服务态度好'
    },
    {
      id:3,
      elderName:'王大爷',
      serviceName:'康复按摩',
      rating:4.2,
      comment:'按摩师手法专业，缓解了老人的疼痛'
    },
    {
      id:4,
      elderName:'赵奶奶',
      serviceName:'上门送餐',
      rating:4.0,
      comment:'服务一般，饭菜口味一般，老人有些不满意'
    },
    {
      id:5,
      elderName:'孙大爷',
      serviceName:'健康体检',
      rating:3.5,
      comment:'医生服务态度一般，检查结果反馈不及时'
    }
  ])
  // 服务列表数据
  const services = ref([
    {
      id: 1,
      name: '上门送餐',
      category: '生活照料',
      price: 30,
      duration: 30,
      description: '为行动不便的老人提供上门送餐服务，确保营养均衡',
      status: '可用'
    },
    {
      id: 2,
      name: '健康体检',
      category: '医疗护理',
      price: 150,
      duration: 60,
      description: '提供基础健康检查，包括血压、血糖、心率等指标监测',
      status: '可用'
    },
    {
      id: 3,
      name: '康复按摩',
      category: '康复训练',
      price: 120,
      duration: 45,
      description: '专业康复师提供按摩服务，缓解肌肉疼痛，促进血液循环',
      status: '可用'
    },
    {
      id: 4,
      name: '心理咨询',
      category: '心理慰藉',
      price: 200,
      duration: 60,
      description: '专业心理咨询师提供一对一心理疏导和情感支持',
      status: '停用'
    }
  ])
  
  // 预约列表数据
  const appointments = ref([
    {
      id: 1,
      elderName: '张大爷',
      elderInfoId: 1,
      serviceId: 1,
      serviceName: '上门送餐',
      appointmentTime: '2025-02-25 12:00',
      phone: '13800138001',
      address: '北京市朝阳区健康路1号',
      staffId: 1,
      staffName: '李护工',
      status: '已确认',
      remark: '老人行动不便，需要送到家门口'
    },
    {
      id: 2,
      elderName: '李奶奶',
      elderInfoId: 2,
      serviceId: 2,
      serviceName: '健康体检',
      appointmentTime: '2025-02-26 09:30',
      phone: '13800138002',
      address: '北京市海淀区长寿路2号',
      staffId: null,
      staffName: '',
      status: '待确认',
      remark: '老人有高血压病史，请注意血压测量'
    },
    {
      id: 3,
      elderName: '王大爷',
      elderInfoId: 3,
      serviceId: 3,
      serviceName: '康复按摩',
      appointmentTime: '2025-02-27 15:00',
      phone: '13800138003',
      address: '北京市西城区福寿街3号',
      staffId: 2,
      staffName: '张护工',
      status: '已完成',
      remark: '老人右腿有旧伤，按摩时请轻柔'
    },
    {
      id: 4,
      elderName: '赵奶奶',
      elderInfoId: 4,
      serviceId: 1,
      serviceName: '上门送餐',
      appointmentTime: '2025-02-25 18:00',
      phone: '13800138004',
      address: '北京市东城区安康路4号',
      staffId: null,
      staffName: '',
      status: '已取消',
      remark: '老人临时有事外出，取消本次服务'
    }
  ])
  
  // 服务人员选项
  const staffOptions = ref([
    { id: 1, name: '李护工' },
    { id: 2, name: '张护工' },
    { id: 3, name: '王护工' },
    { id: 4, name: '赵护工' }
  ])
  
  // 分页和搜索
  const loading = ref(false)
  const currentPage = ref(1)
  const pageSize = ref(10)
  const searchQuery = ref('')
  // 计算总数
  const totalItems = computed(() => {
    if (activeTab.value === 'services') {
      return filteredServices.value.length
    } else {
      return filteredAppointments.value.length
    }
  })
  
  // 过滤后的服务列表
  const filteredServices = computed(() => {
    if (!searchQuery.value) {
      return services.value
    }
    
    const query = searchQuery.value.toLowerCase()
    return services.value.filter(service => 
      service.name.toLowerCase().includes(query) ||
      service.category.toLowerCase().includes(query)
    )
  })
  
  // 过滤后的预约列表
  const filteredAppointments = computed(() => {
    if (!searchQuery.value) {
      return appointments.value
    }
    
    const query = searchQuery.value.toLowerCase()
    return appointments.value.filter(appointment => 
      appointment.elderName.toLowerCase().includes(query) ||
      appointment.serviceName.toLowerCase().includes(query)
    )
  })
  
  // 对话框相关
  const serviceDialogVisible = ref(false)
  const appointmentDialogVisible = ref(false)
  const dialogType = ref('add') // 'add' 或 'edit'
  const serviceFormRef = ref(null)
  const serviceForm = reactive({
    id: '',
    name: '',
    category: '',
    price: 0,
    duration: 0,
    description: '',
    status: '可用'
  })
  const selectedAppointment = ref({})
  const assignForm = reactive({
    staffId: '',
    remark: ''
  })
  
  // 表单验证规则
  const serviceRules = {
    name: [
      { required: true, message: '请输入服务名称', trigger: 'blur' }
    ],
    category: [
      { required: true, message: '请选择服务类别', trigger: 'change' }
    ],
    price: [
      { required: true, message: '请输入服务价格', trigger: 'blur' }
    ],
    duration: [
      { required: true, message: '请输入服务时长', trigger: 'blur' }
    ],
    status: [
      { required: true, message: '请选择服务状态', trigger: 'change' }
    ]
  }
  
  // 获取服务类别标签类型
  const getCategoryType = (category) => {
    const typeMap = {
      '生活照料': 'success',
      '医疗护理': 'danger',
      '康复训练': 'warning',
      '心理慰藉': 'info',
      '文化娱乐': 'primary'
    }
    return typeMap[category] || 'info'
  }
  
  // 获取预约状态标签类型
  const getAppointmentStatusType = (status) => {
    const typeMap = {
      '待确认': 'warning',
      '已确认': 'primary',
      '进行中': 'success',
      '已完成': 'info',
      '已取消': 'danger'
    }
    return typeMap[status] || 'info'
  }
  
  // 标签页切换
  const handleTabClick = () => {
    searchQuery.value = ''
    currentPage.value = 1
  }
  
  // 搜索
  const handleSearch = () => {
    currentPage.value = 1
  }
  
  // 添加服务
  const handleAdd = () => {
    dialogType.value = 'add'
    resetServiceForm()
    serviceDialogVisible.value = true
  }
  
  // 编辑服务
  const handleEdit = (row) => {
    dialogType.value = 'edit'
    Object.keys(serviceForm).forEach(key => {
      serviceForm[key] = row[key]
    })
    serviceDialogVisible.value = true
  }
  
  // 删除服务
  const handleDelete = (row) => {
    ElMessageBox.confirm(
      `确定要删除服务 "${row.name}" 吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(() => {
      // 这里应该调用删除API
      services.value = services.value.filter(service => service.id !== row.id)
      ElMessage.success('删除成功')
    }).catch(() => {
      // 取消删除
    })
  }
  
  // 修改服务状态
  const handleStatusChange = (row) => {
    ElMessage.success(`服务 "${row.name}" 已${row.status === '可用' ? '启用' : '停用'}`)
  }
  
  // 查看预约详情
  const handleViewAppointment = (row) => {
    selectedAppointment.value = { ...row }
    assignForm.staffId = row.staffId || ''
    assignForm.remark = row.remark || ''
    appointmentDialogVisible.value = true
  }
  
  // 确认预约
  const handleConfirmAppointment = (row) => {
    handleViewAppointment(row)
  }
  
  // 取消预约
  const handleCancelAppointment = (row) => {
    ElMessageBox.confirm(
      `确定要取消 ${row.elderName} 的 "${row.serviceName}" 预约吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(() => {
      // 这里应该调用API取消预约
      const index = appointments.value.findIndex(appointment => appointment.id === row.id)
      if (index !== -1) {
        appointments.value[index].status = '已取消'
        ElMessage.success('预约已取消')
        appointmentDialogVisible.value = false
      }
    }).catch(() => {
      // 取消操作
    })
  }
  
  // 分配服务人员
  const handleAssignStaff = () => {
    if (!assignForm.staffId) {
      ElMessage.warning('请选择服务人员')
      return
    }
    
    // 这里应该调用API分配服务人员
    const index = appointments.value.findIndex(appointment => appointment.id === selectedAppointment.value.id)
    if (index !== -1) {
      const staff = staffOptions.value.find(staff => staff.id === assignForm.staffId)
      appointments.value[index].staffId = assignForm.staffId
      appointments.value[index].staffName = staff ? staff.name : ''
      appointments.value[index].status = '已确认'
      appointments.value[index].remark = assignForm.remark || appointments.value[index].remark
      
      ElMessage.success('预约已确认并分配服务人员')
      appointmentDialogVisible.value = false
    }
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
  
  // 重置服务表单
  const resetServiceForm = () => {
    if (serviceFormRef.value) {
      serviceFormRef.value.resetFields()
    }
    Object.keys(serviceForm).forEach(key => {
      if (key === 'price' || key === 'duration') {
        serviceForm[key] = 0
      } else if (key === 'status') {
        serviceForm[key] = '可用'
      } else {
        serviceForm[key] = ''
      }
    })
  }
  
  // 提交服务表单
  const submitServiceForm = () => {
    serviceFormRef.value.validate((valid) => {
      if (valid) {
        if (dialogType.value === 'add') {
          // 添加服务
          const newService = {
            id: services.value.length + 1,
            ...serviceForm
          }
          services.value.push(newService)
          ElMessage.success('添加服务成功')
        } else {
          // 编辑服务
          const index = services.value.findIndex(service => service.id === serviceForm.id)
          if (index !== -1) {
            services.value[index] = { ...serviceForm }
            ElMessage.success('更新服务成功')
          }
        }
        serviceDialogVisible.value = false
      } else {
        return false
      }
    })
  }
  
  onMounted(() => {
    // 这里可以调用API获取服务列表和预约列表
  })
  </script>
  
  <style scoped>
  .service-management {
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
  </style>