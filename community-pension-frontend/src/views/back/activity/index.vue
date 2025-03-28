<template>
    <div class="activity-management">
      <el-card shadow="hover" class="table-card">
        <template #header>
          <div class="card-header">
            <h3>社区活动管理</h3>
            <div class="header-actions">
              <el-input
                v-model="searchQuery"
                placeholder="搜索活动名称"
                class="search-input"
                clearable
                @clear="handleSearch"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button type="success" @click="handleAdd">添加活动</el-button>
            </div>
          </div>
        </template>
        
        <el-table
          :data="filteredActivities"
          style="width: 100%"
          v-loading="loading"
          border
        >
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="活动名称" min-width="150" />
          <el-table-column prop="time" label="活动时间" width="180" />
          <el-table-column prop="location" label="活动地点" min-width="150" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="participants" label="报名人数" width="100" />
          <el-table-column label="操作" width="250" fixed="right">
            <template #default="scope">
              <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button type="success" size="small" @click="handleViewParticipants(scope.row)">查看报名</el-button>
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
            :total="totalActivities"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
      
      <!-- 活动表单对话框 -->
      <el-dialog
        v-model="dialogVisible"
        :title="dialogType === 'add' ? '添加活动' : '编辑活动'"
        width="600px"
      >
        <el-form
          ref="activityFormRef"
          :model="activityForm"
          :rules="activityRules"
          label-width="100px"
        >
          <el-form-item label="活动名称" prop="title">
            <el-input v-model="activityForm.title" />
          </el-form-item>
          <el-form-item label="活动时间" prop="time">
            <el-date-picker 
              v-model="activityForm.time" 
              type="datetime" 
              placeholder="选择日期和时间"
              format="YYYY-MM-DD HH:mm"
              value-format="YYYY-MM-DD HH:mm"
            />
          </el-form-item>
          <el-form-item label="活动地点" prop="location">
            <el-input v-model="activityForm.location" />
          </el-form-item>
          <el-form-item label="活动状态" prop="status">
            <el-select v-model="activityForm.status">
              <el-option value="未开始" label="未开始" />
              <el-option value="报名中" label="报名中" />
              <el-option value="进行中" label="进行中" />
              <el-option value="已结束" label="已结束" />
            </el-select>
          </el-form-item>
          <el-form-item label="活动描述" prop="description">
            <el-input 
              v-model="activityForm.description" 
              type="textarea" 
              :rows="4" 
            />
          </el-form-item>
          <el-form-item label="人数限制" prop="maxParticipants">
            <el-input-number 
              v-model="activityForm.maxParticipants" 
              :min="0" 
              :max="1000"
              :step="1"
            />
            <span class="form-tip">0表示不限制人数</span>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitForm">确定</el-button>
          </span>
        </template>
      </el-dialog>
      
      <!-- 报名人员对话框 -->
      <el-dialog
        v-model="participantsDialogVisible"
        title="报名人员列表"
        width="700px"
      >
        <div v-if="selectedActivity" class="activity-info">
          <h4>{{ selectedActivity.title }}</h4>
          <p>时间：{{ selectedActivity.time }}</p>
          <p>地点：{{ selectedActivity.location }}</p>
        </div>
        
        <el-table :data="participants" style="width: 100%" border>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="姓名" width="120" />
          <el-table-column prop="phone" label="联系电话" width="150" />
          <el-table-column prop="signupTime" label="报名时间" width="180" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === '已签到' ? 'success' : 'info'">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150">
            <template #default="scope">
              <el-button 
                type="primary" 
                size="small" 
                :disabled="scope.row.status === '已签到'"
                @click="handleSignIn(scope.row)"
              >
                签到
              </el-button>
              <el-button 
                type="danger" 
                size="small" 
                @click="handleRemoveParticipant(scope.row)"
              >
                移除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-dialog>
    </div>
  </template>
  
  <script setup>
  import { ref, computed, reactive, onMounted } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { Search } from '@element-plus/icons-vue'
  
  // 活动列表数据
  const activities = ref([
    {
      id: 1,
      title: '健康讲座',
      time: '2025-02-28 14:00',
      location: '社区活动中心',
      status: '未开始',
      participants: 15,
      description: '关于老年人的健康生活的专业讲座',
      maxParticipants: 50
    },
    {
      id: 2,
      title: '太极班',
      time: '2025-03-01 09:00',
      location: '社区广场',
      status: '报名中',
      participants: 20,
      description: '由专业教练指导的太极拳课程',
      maxParticipants: 30
    },
    {
      id: 3,
      title: '棋牌比赛',
      time: '2025-03-05 14:00',
      location: '棋牌室',
      status: '报名中',
      participants: 12,
      description: '象棋、麻将等多种棋牌比赛',
      maxParticipants: 40
    },
    {
      id: 4,
      title: '春节联欢会',
      time: '2025-02-10 18:00',
      location: '社区大礼堂',
      status: '已结束',
      participants: 120,
      description: '社区春节联欢晚会',
      maxParticipants: 200
    }
  ])
  
  // 分页和搜索
  const loading = ref(false)
  const currentPage = ref(1)
  const pageSize = ref(10)
  const searchQuery = ref('')
  const totalActivities = computed(() => filteredActivities.value.length)
  
  // 过滤后的活动列表
  const filteredActivities = computed(() => {
    if (!searchQuery.value) {
      return activities.value
    }
    
    const query = searchQuery.value.toLowerCase()
    return activities.value.filter(activity => 
      activity.title.toLowerCase().includes(query) ||
      activity.location.toLowerCase().includes(query)
    )
  })
  
  // 对话框相关
  const dialogVisible = ref(false)
  const dialogType = ref('add') // 'add' 或 'edit'
  const activityFormRef = ref(null)
  const activityForm = reactive({
    id: '',
    title: '',
    time: '',
    location: '',
    status: '未开始',
    participants: 0,
    description: '',
    maxParticipants: 0
  })
  
  // 表单验证规则
  const activityRules = {
    title: [
      { required: true, message: '请输入活动名称', trigger: 'blur' }
    ],
    time: [
      { required: true, message: '请选择活动时间', trigger: 'change' }
    ],
    location: [
      { required: true, message: '请输入活动地点', trigger: 'blur' }
    ],
    status: [
      { required: true, message: '请选择活动状态', trigger: 'change' }
    ]
  }
  
  // 报名人员对话框
  const participantsDialogVisible = ref(false)
  const selectedActivity = ref(null)
  const participants = ref([
    { id: 1, name: '张三', phone: '13800138001', signupTime: '2025-02-20 10:30', status: '已报名' },
    { id: 2, name: '李四', phone: '13800138002', signupTime: '2025-02-20 11:15', status: '已签到' },
    { id: 3, name: '王五', phone: '13800138003', signupTime: '2025-02-21 09:45', status: '已报名' }
  ])
  
  // 获取状态标签类型
  const getStatusType = (status) => {
    const typeMap = {
      '未开始': 'info',
      '报名中': 'primary',
      '进行中': 'success',
      '已结束': 'danger'
    }
    return typeMap[status] || 'info'
  }
  
  // 搜索活动
  const handleSearch = () => {
    currentPage.value = 1
  }
  
  // 添加活动
  const handleAdd = () => {
    dialogType.value = 'add'
    resetForm()
    dialogVisible.value = true
  }
  
  // 编辑活动
  const handleEdit = (row) => {
    dialogType.value = 'edit'
    Object.keys(activityForm).forEach(key => {
      activityForm[key] = row[key]
    })
    dialogVisible.value = true
  }
  
  // 查看报名人员
  const handleViewParticipants = (row) => {
    selectedActivity.value = row
    participantsDialogVisible.value = true
  }
  
  // 删除活动
  const handleDelete = (row) => {
    ElMessageBox.confirm(
      `确定要删除活动 "${row.title}" 吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(() => {
      // 这里应该调用删除API
      activities.value = activities.value.filter(activity => activity.id !== row.id)
      ElMessage.success('删除成功')
    }).catch(() => {
      // 取消删除
    })
  }
  
  // 签到
  const handleSignIn = (participant) => {
    participant.status = '已签到'
    ElMessage.success(`${participant.name} 签到成功`)
  }
  
  // 移除报名人员
  const handleRemoveParticipant = (participant) => {
    ElMessageBox.confirm(
      `确定要移除 ${participant.name} 的报名记录吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(() => {
      // 这里应该调用API移除报名记录
      participants.value = participants.value.filter(p => p.id !== participant.id)
      if (selectedActivity.value) {
        selectedActivity.value.participants--
      }
      ElMessage.success('移除成功')
    }).catch(() => {
      // 取消移除
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
    if (activityFormRef.value) {
      activityFormRef.value.resetFields()
    }
    Object.keys(activityForm).forEach(key => {
      if (key === 'status') {
        activityForm[key] = '未开始'
      } else if (key === 'participants' || key === 'maxParticipants') {
        activityForm[key] = 0
      } else {
        activityForm[key] = ''
      }
    })
  }
  
  // 提交表单
  const submitForm = () => {
    activityFormRef.value.validate((valid) => {
      if (valid) {
        if (dialogType.value === 'add') {
          // 添加活动
          const newActivity = {
            id: activities.value.length + 1,
            ...activityForm,
            participants: 0
          }
          activities.value.push(newActivity)
          ElMessage.success('添加活动成功')
        } else {
          // 编辑活动
          const index = activities.value.findIndex(activity => activity.id === activityForm.id)
          if (index !== -1) {
            activities.value[index] = { ...activityForm }
            ElMessage.success('更新活动成功')
          }
        }
        dialogVisible.value = false
      } else {
        return false
      }
    })
  }
  
  onMounted(() => {
    // 这里可以调用API获取活动列表
  })
  </script>
  
  <style scoped>
  .activity-management {
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
  
  .activity-info {
    margin-bottom: 20px;
    padding-bottom: 15px;
    border-bottom: 1px solid #ebeef5;
  }
  
  .activity-info h4 {
    margin: 0 0 10px 0;
    font-size: 18px;
    color: #303133;
  }
  
  .activity-info p {
    margin: 5px 0;
    color: #606266;
  }
  
  .form-tip {
    margin-left: 10px;
    color: #909399;
    font-size: 12px;
  }
  </style>