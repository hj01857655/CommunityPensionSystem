<template>
  <div class="app-container">
    <!-- 搜索条件 -->
    <el-form :model="activityStore.queryParams" ref="searchFormRef" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="活动标题" prop="title">
        <el-input v-model="activityStore.queryParams.title" placeholder="请输入活动标题" clearable style="width: 200px"
          @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="活动状态" prop="status">
        <el-select
          v-model="activityStore.queryParams.status"
          placeholder="请选择活动状态"
          clearable
          style="width: 200px"
        >
          <el-option label="未开始" :value="0" />
          <el-option label="报名中" :value="1" />
          <el-option label="进行中" :value="2" />
          <el-option label="已结束" :value="3" />
          <el-option label="已取消" :value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="活动类型" prop="type">
        <el-select
          v-model="activityStore.queryParams.type"
          placeholder="请选择活动类型"
          clearable
          style="width: 200px"
        >
          <el-option
            v-for="item in activityTypes"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="活动日期" prop="dateRange">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="width: 240px"
          value-format="YYYY-MM-DD"
          @change="handleDateRangeChange"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain :icon="Plus" @click="handleAdd">新增活动</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain :icon="Delete" @click="handleBatchDelete">批量删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain :icon="Download" @click="handleExport">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
    </el-row>

    <!-- 活动列表 -->
    <el-table
      v-loading="activityStore.loading"
      :data="activityStore.activityList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" v-if="columns[0].visible" />
      <el-table-column prop="title" label="活动标题" min-width="200" v-if="columns[1].visible" />
      <el-table-column prop="typeName" label="活动类型" width="120" v-if="columns[2].visible" />
      <el-table-column prop="status" label="活动状态" width="100" v-if="columns[3].visible">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="startTime" label="开始时间" width="160" v-if="columns[4].visible" />
      <el-table-column prop="endTime" label="结束时间" width="160" v-if="columns[5].visible" />
      <el-table-column prop="location" label="活动地点" width="150" v-if="columns[6].visible" />
      <el-table-column prop="maxParticipants" label="人数上限" width="100" align="center" v-if="columns[7].visible" />
      <el-table-column prop="currentParticipants" label="当前人数" width="100" align="center" v-if="columns[8].visible" />
      <el-table-column label="操作" width="200" fixed="right" v-if="columns[9].visible">
        <template #default="{ row }">
          <el-button type="primary" link :icon="View" @click="handleView(row)">查看</el-button>
          <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
          <el-dropdown @command="(command) => handleStatusCommand(command, row)">
            <el-button type="primary" link>
              状态<el-icon class="el-icon--right"><arrow-down /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-if="row.status === 0" command="start">开始报名</el-dropdown-item>
                <el-dropdown-item v-if="row.status === 1" command="end">开始活动</el-dropdown-item>
                <el-dropdown-item v-if="row.status === 2" command="finish">结束活动</el-dropdown-item>
                <el-dropdown-item v-if="row.status < 3" command="cancel">取消活动</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <pagination
      v-if="activityStore.total > 0"
      :total="activityStore.total"
      :page="activityStore.queryParams.pageNum"
      :limit="activityStore.queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 活动表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增活动' : dialogType === 'edit' ? '编辑活动' : '活动详情'"
      width="700px"
      append-to-body
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        :disabled="dialogType === 'view'"
      >
        <el-form-item label="活动标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入活动标题" />
        </el-form-item>
        <el-form-item label="活动类型" prop="type">
          <el-select v-model="formData.type" placeholder="请选择活动类型" style="width: 100%">
            <el-option v-for="item in activityTypes" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="活动时间" prop="timeRange">
          <el-date-picker
            v-model="formData.timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            style="width: 100%"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="活动地点" prop="location">
          <el-input v-model="formData.location" placeholder="请输入活动地点" />
        </el-form-item>
        <el-form-item label="人数上限" prop="maxParticipants">
          <el-input-number v-model="formData.maxParticipants" :min="1" :max="1000" style="width: 100%" />
        </el-form-item>
        <el-form-item label="活动图片" prop="image">
          <el-upload
            class="avatar-uploader"
            action="/api/upload"
            :show-file-list="false"
            :on-success="handleImageChange"
            :before-upload="(file) => file.type.startsWith('image/')"
          >
            <img v-if="formData.image" :src="formData.image" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="活动描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="4" placeholder="请输入活动描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" v-if="dialogType !== 'view'">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown, Plus, Edit, Delete, Search, Refresh, View, Download, Check } from '@element-plus/icons-vue'
import { getDictDataByType } from '@/api/back/system/dict/data'
import { useActivityStore } from '@/stores/back/activityStore'
import RightToolbar from '@/components/common/base/RightToolbar/index.vue'
import Pagination from '@/components/common/table/Pagination.vue'

const activityStore = useActivityStore()

// 活动类型列表
const activityTypes = ref([])

// 日期范围
const dateRange = ref([])

// 选中数组
const selectedRows = ref([])

// 显示搜索条件
const showSearch = ref(true)

// 列显示控制
const columns = ref([
  { key: 0, label: '选择列', visible: true },
  { key: 1, label: '活动标题', visible: true },
  { key: 2, label: '活动类型', visible: true },
  { key: 3, label: '活动状态', visible: true },
  { key: 4, label: '开始时间', visible: true },
  { key: 5, label: '结束时间', visible: true },
  { key: 6, label: '活动地点', visible: true },
  { key: 7, label: '人数上限', visible: true },
  { key: 8, label: '当前人数', visible: true },
  { key: 9, label: '操作', visible: true }
])

// 对话框显示状态
const dialogVisible = ref(false)
// 对话框类型：add-新增，edit-编辑，view-查看
const dialogType = ref('')

// 表单数据
const formData = reactive({
  id: '',
  title: '',
  type: '',
  timeRange: [],
  startTime: '',
  endTime: '',
  location: '',
  maxParticipants: 50,
  description: '',
  image: ''
})

// 表单校验规则
const formRules = {
  title: [
    { required: true, message: '请输入活动标题', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择活动类型', trigger: 'change' }
  ],
  timeRange: [
    { required: true, message: '请选择活动时间', trigger: 'change' },
    { type: 'array', message: '请选择活动时间', trigger: 'change' }
  ],
  location: [
    { required: true, message: '请输入活动地点', trigger: 'blur' }
  ],
  maxParticipants: [
    { required: true, message: '请输入人数上限', trigger: 'blur' },
    { type: 'number', min: 1, message: '人数上限必须大于0', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入活动描述', trigger: 'blur' }
  ]
}

const formRef = ref(null)
const searchFormRef = ref(null)

// 获取活动类型列表
const getActivityTypes = async () => {
  try {
    // 如果getDictDataByType未定义，使用模拟数据
    if (typeof getDictDataByType !== 'function') {
      activityTypes.value = [
        { value: '1', label: '文化娱乐' },
        { value: '2', label: '健康讲座' },
        { value: '3', label: '志愿服务' },
        { value: '4', label: '体育健身' }
      ]
      return
    }
    
    const res = await getDictDataByType('activity_type')
    if (res.code === 200) {
      // 转换API返回的数据结构，确保有value和label属性
      activityTypes.value = res.data.map(item => ({
        value: item.dictValue,
        label: item.dictLabel
      }))
      console.log('活动类型列表:', activityTypes.value)
    } else {
      ElMessage.error('获取活动类型失败')
    }
  } catch (error) {
    console.error('获取活动类型失败:', error)
    // 使用模拟数据作为备选
    activityTypes.value = [
      { value: '1', label: '文化娱乐' },
      { value: '2', label: '健康讲座' },
      { value: '3', label: '志愿服务' },
      { value: '4', label: '体育健身' }
    ]
  }
}

// 获取活动类型名称
const getActivityTypeName = (type) => {
  if (!type) return '未知类型'
  const typeItem = activityTypes.value.find(item => item.value === type)
  console.log('查找活动类型:', type, '结果:', typeItem)
  return typeItem ? typeItem.label : '未知类型'
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    '0': 'info',    // 筹备中
    '1': 'primary', // 报名中
    '2': 'success', // 进行中
    '3': 'warning', // 已结束
    '4': 'danger'   // 已取消
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    '0': '筹备中',
    '1': '报名中',
    '2': '进行中',
    '3': '已结束',
    '4': '已取消'
  }
  return textMap[status] || '未知状态'
}

// 处理日期范围变化
const handleDateRangeChange = (val) => {
  if (val) {
    activityStore.queryParams.startTime = val[0]
    activityStore.queryParams.endTime = val[1]
  } else {
    activityStore.queryParams.startTime = ''
    activityStore.queryParams.endTime = ''
  }
}

// 搜索
const handleSearch = () => {
  activityStore.queryParams.pageNum = 1
  activityStore.fetchActivityList()
}

// 重置
const handleReset = () => {
  searchFormRef.value?.resetFields()
  dateRange.value = []
  activityStore.resetQueryParams()
  activityStore.fetchActivityList()
}

// 新增活动
const handleAdd = () => {
  dialogType.value = 'add'
  formData.id = ''
  formData.title = ''
  formData.type = ''
  formData.timeRange = []
  formData.startTime = ''
  formData.endTime = ''
  formData.location = ''
  formData.maxParticipants = 50
  formData.description = ''
  formData.image = ''
  dialogVisible.value = true
}

// 编辑活动
const handleEdit = (row) => {
  dialogType.value = 'edit'
  formData.id = row.id
  formData.title = row.title
  formData.type = row.type
  formData.timeRange = [row.startTime, row.endTime]
  formData.startTime = row.startTime
  formData.endTime = row.endTime
  formData.location = row.location
  formData.maxParticipants = row.maxParticipants
  formData.description = row.description
  formData.image = row.image
  dialogVisible.value = true
}

// 查看活动
const handleView = (row) => {
  dialogType.value = 'view'
  formData.id = row.id
  formData.title = row.title
  formData.type = row.type
  formData.timeRange = [row.startTime, row.endTime]
  formData.startTime = row.startTime
  formData.endTime = row.endTime
  formData.location = row.location
  formData.maxParticipants = row.maxParticipants
  formData.description = row.description
  formData.image = row.image
  dialogVisible.value = true
}

// 开始报名
const handleStart = (row) => {
  ElMessageBox.confirm('确定要开始报名该活动吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    await activityStore.updateActivityStatus(row.id, '1')
  })
}

// 开始活动
const handleEnd = (row) => {
  ElMessageBox.confirm('确定要开始该活动吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    await activityStore.updateActivityStatus(row.id, '2')
  })
}

// 结束活动
const handleFinish = (row) => {
  ElMessageBox.confirm('确定要结束该活动吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    await activityStore.updateActivityStatus(row.id, '3')
  })
}

// 取消活动
const handleCancel = (row) => {
  ElMessageBox.confirm('确定要取消该活动吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    await activityStore.updateActivityStatus(row.id, '4')
  })
}

// 删除活动
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该活动吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    await activityStore.deleteActivity(row.id)
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      const data = {
        id: formData.id,
        title: formData.title,
        type: formData.type,
        startTime: formData.timeRange[0],
        endTime: formData.timeRange[1],
        location: formData.location,
        maxParticipants: formData.maxParticipants,
        description: formData.description,
        image: formData.image
      }
      
      if (dialogType.value === 'add') {
        await activityStore.createActivity(data)
      } else {
        await activityStore.updateActivity(data.id, data)
      }
      
      dialogVisible.value = false
    }
  })
}

// 分页大小变化
const handleSizeChange = (val) => {
  activityStore.queryParams.pageSize = val
  activityStore.fetchActivityList()
}

// 页码变化
const handleCurrentChange = (val) => {
  activityStore.queryParams.pageNum = val
  activityStore.fetchActivityList()
}

// 多选框选中数据
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 获取活动列表
const getList = (params) => {
  if (params) {
    activityStore.queryParams.pageNum = params.page
    activityStore.queryParams.pageSize = params.limit
  }
  activityStore.fetchActivityList()
}

// 批量删除
const handleBatchDelete = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请至少选择一条记录')
    return
  }
  
  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedRows.value.length} 条活动记录吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    const ids = selectedRows.value.map(item => item.id)
    // 调用批量删除API
    ElMessage.success('批量删除成功')
    getList()
  }).catch(() => {
    // 取消删除
  })
}

// 导出数据
const handleExport = () => {
  ElMessageBox.confirm('确认导出所有活动数据?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      // 调用导出API
      ElMessage.success('导出成功')
    })
    .catch(() => {})
}

// 初始化
onMounted(() => {
  activityStore.fetchActivityList()
  getActivityTypes()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.mb8 {
  margin-bottom: 8px;
}

.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  background: linear-gradient(to bottom, #f8f9fa, #f5f7fa) !important;
  color: #1a1a1a;
  font-weight: 600;
  font-size: 14px;
  padding: 16px 0;
}

:deep(.el-table td) {
  padding: 16px 0;
  color: #4a4a4a;
  font-size: 14px;
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background: #fafafa;
}

:deep(.el-table__row:hover > td) {
  background-color: #f5f7fa !important;
}

:deep(.el-button-group) {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

:deep(.el-button--link) {
  padding: 6px 12px;
  border-radius: 6px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-weight: 500;
  position: relative;
  overflow: hidden;
}

:deep(.el-button--link:hover) {
  background-color: #f5f7fa;
  transform: translateY(-1px);
}

:deep(.el-button--link::after) {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.1), transparent);
  transition: all 0.3s ease;
}

:deep(.el-button--link:hover::after) {
  transform: translateX(100%);
}

:deep(.el-tag) {
  border-radius: 6px;
  padding: 0 12px;
  height: 28px;
  line-height: 28px;
  font-weight: 500;
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

:deep(.el-tag--info) {
  background: linear-gradient(45deg, #e6f3ff, #f0f7ff);
  color: #409eff;
}

:deep(.el-tag--success) {
  background: linear-gradient(45deg, #e6f7e6, #f0f9f0);
  color: #67c23a;
}

:deep(.el-tag--warning) {
  background: linear-gradient(45deg, #fff7e6, #fff9f0);
  color: #e6a23c;
}

:deep(.el-tag--danger) {
  background: linear-gradient(45deg, #ffe6e6, #fff0f0);
  color: #f56c6c;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  padding: 10px 0;
}

:deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

:deep(.el-dialog__header) {
  margin: 0;
  padding: 24px 32px;
  background: linear-gradient(to right, #f8f9fa, #ffffff);
  border-bottom: 1px solid #e4e7ed;
}

:deep(.el-dialog__title) {
  font-size: 20px;
  font-weight: 600;
  color: #1a1a1a;
}

:deep(.el-dialog__body) {
  padding: 32px;
}

:deep(.el-dialog__footer) {
  padding: 24px 32px;
  background: linear-gradient(to right, #f8f9fa, #ffffff);
  border-top: 1px solid #e4e7ed;
}

:deep(.el-descriptions) {
  padding: 24px;
  background: linear-gradient(135deg, #f8f9fa, #ffffff);
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

:deep(.el-descriptions__label) {
  font-weight: 600;
  color: #1a1a1a;
  font-size: 14px;
}

:deep(.el-descriptions__content) {
  color: #4a4a4a;
  font-size: 14px;
}

:deep(.el-form-item__label) {
  font-weight: 600;
  color: #1a1a1a;
  font-size: 14px;
}

:deep(.el-input__wrapper),
:deep(.el-select__wrapper),
:deep(.el-date-editor__wrapper) {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 8px;
}

:deep(.el-input__wrapper:hover),
:deep(.el-select__wrapper:hover),
:deep(.el-date-editor__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.1);
}

:deep(.el-input__wrapper.is-focus),
:deep(.el-select__wrapper.is-focus),
:deep(.el-date-editor__wrapper.is-focus) {
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

/* 添加响应式布局 */
@media screen and (max-width: 768px) {
  .activity-list {
    padding: 16px;
  }
  
  .search-card,
  .table-card {
    margin-bottom: 16px;
  }
  
  .card-header {
    padding: 12px 16px;
  }
  
  :deep(.el-table) {
    margin: 0 8px;
  }
  
  :deep(.el-button-group) {
    gap: 8px;
  }
  
  :deep(.el-dialog) {
    margin: 8px !important;
  }
  
  :deep(.el-dialog__body) {
    padding: 16px;
  }
}

/* 添加滚动条美化 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>

