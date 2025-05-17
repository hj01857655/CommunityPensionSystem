<template>
  <div class="app-container">
    <!-- 搜索条件 -->
    <el-form :model="activityStore.queryParams" ref="searchFormRef" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="活动标题" prop="title">
        <el-input v-model="activityStore.queryParams.title" placeholder="请输入活动标题" clearable style="width: 200px" @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="活动状态" prop="status">
        <el-select v-model="activityStore.queryParams.status" placeholder="请选择活动状态" clearable style="width: 200px">
          <el-option label="未开始" :value="0" />
          <el-option label="报名中" :value="1" />
          <el-option label="进行中" :value="2" />
          <el-option label="已结束" :value="3" />
          <el-option label="已取消" :value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="活动类型" prop="type">
        <el-select v-model="activityStore.queryParams.type" placeholder="请选择活动类型" clearable style="width: 200px">
          <el-option v-for="item in activityTypes" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="活动日期" prop="dateRange">
        <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" style="width: 240px" value-format="YYYY-MM-DD" @change="handleDateRangeChange" />
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
    <el-table v-loading="activityStore.loading" :data="activityStore.activityList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" v-if="columns[0].visible" />
      <el-table-column prop="title" label="活动标题" min-width="200" v-if="columns[1].visible" />
      <el-table-column prop="typeName" label="活动类型" width="120" v-if="columns[2].visible" />
      <el-table-column prop="status" label="活动状态" width="100" v-if="columns[3].visible">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
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
              状态<el-icon class="el-icon--right"><ArrowDown /></el-icon>
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
    <pagination v-if="activityStore.total > 0" :total="activityStore.total" :page="activityStore.queryParams.pageNum" :limit="activityStore.queryParams.pageSize" @pagination="getList" />

    <!-- 活动表单对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '新增活动' : dialogType === 'edit' ? '编辑活动' : '活动详情'" width="700px" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px" :disabled="dialogType === 'view'">
        <el-form-item label="活动标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入活动标题" />
        </el-form-item>
        <el-form-item label="活动类型" prop="type">
          <el-select v-model="formData.type" placeholder="请选择活动类型" style="width: 100%">
            <el-option v-for="item in activityTypes" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="活动时间" prop="timeRange">
          <el-date-picker v-model="formData.timeRange" type="datetimerange" range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间" style="width: 100%" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="活动地点" prop="location">
          <el-input v-model="formData.location" placeholder="请输入活动地点" />
        </el-form-item>
        <el-form-item label="人数上限" prop="maxParticipants">
          <el-input-number v-model="formData.maxParticipants" :min="1" :max="1000" style="width: 100%" />
        </el-form-item>
        <el-form-item label="活动图片" prop="image">
          <el-upload class="avatar-uploader" action="/api/upload" :show-file-list="false" :on-success="handleImageChange" :before-upload="(file) => file.type.startsWith('image/')">
            <img v-if="formData.image" :src="formData.image" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><component :is="Plus" /></el-icon>
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
import { getDictDataByType } from '@/api/back/system/dict/data'
import Pagination from '@/components/common/table/Pagination.vue'
import RightToolbar from '@/components/common/table/TableToolbar.vue'
import { useActivityStore } from '@/stores/back/activityStore'
import { ArrowDown, Delete, Download, Edit, Plus, Refresh, Search, View } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const activityStore = useActivityStore()
const searchFormRef = ref()
const formRef = ref()
const showSearch = ref(true)
const dialogVisible = ref(false)
const dialogType = ref('add') // add, edit, view
const dateRange = ref([])
const selectedRows = ref([])
const loading = ref(false)

// 表格列控制
const columns = ref([
  { key: 'selection', label: '选择列', visible: true },
  { key: 'title', label: '活动标题', visible: true },
  { key: 'type', label: '活动类型', visible: true },
  { key: 'status', label: '活动状态', visible: true },
  { key: 'startTime', label: '开始时间', visible: true },
  { key: 'endTime', label: '结束时间', visible: true },
  { key: 'location', label: '活动地点', visible: true },
  { key: 'maxParticipants', label: '人数上限', visible: true },
  { key: 'currentParticipants', label: '当前人数', visible: true },
  { key: 'operation', label: '操作', visible: true }
])

// 活动类型选项
const activityTypes = ref([
  { value: '1', label: '文化娱乐' },
  { value: '2', label: '健康讲座' },
  { value: '3', label: '志愿服务' },
  { value: '4', label: '户外活动' },
  { value: '5', label: '节日庆祝' },
  { value: '6', label: '其他活动' }
])

// 表单数据
const formData = reactive({
  id: undefined,
  title: '',
  type: '',
  timeRange: [],
  startTime: '',
  endTime: '',
  location: '',
  maxParticipants: 50,
  currentParticipants: 0,
  image: '',
  description: '',
  status: 0
})

// 表单验证规则
const formRules = {
  title: [
    { required: true, message: '请输入活动标题', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择活动类型', trigger: 'change' }
  ],
  timeRange: [
    { required: true, message: '请选择活动时间', trigger: 'change' }
  ],
  location: [
    { required: true, message: '请输入活动地点', trigger: 'blur' }
  ],
  maxParticipants: [
    { required: true, message: '请输入人数上限', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入活动描述', trigger: 'blur' }
  ]
}

// 获取活动列表
const getList = () => {
  activityStore.fetchActivityList()
}

// 处理日期范围变化
const handleDateRangeChange = (val) => {
  if (val) {
    activityStore.queryParams.startDate = val[0]
    activityStore.queryParams.endDate = val[1]
  } else {
    activityStore.queryParams.startDate = undefined
    activityStore.queryParams.endDate = undefined
  }
}

// 搜索
const handleSearch = () => {
  activityStore.queryParams.pageNum = 1
  getList()
}

// 重置
const handleReset = () => {
  searchFormRef.value?.resetFields()
  dateRange.value = []
  activityStore.resetQueryParams()
  handleSearch()
}

// 多选框选中数据
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 新增活动
const handleAdd = () => {
  resetForm()
  dialogType.value = 'add'
  dialogVisible.value = true
}

// 查看活动
const handleView = (row) => {
  resetForm()
  dialogType.value = 'view'
  Object.assign(formData, row)
  if (row.startTime && row.endTime) {
    formData.timeRange = [row.startTime, row.endTime]
  }
  dialogVisible.value = true
}

// 编辑活动
const handleEdit = (row) => {
  resetForm()
  dialogType.value = 'edit'
  Object.assign(formData, row)
  if (row.startTime && row.endTime) {
    formData.timeRange = [row.startTime, row.endTime]
  }
  dialogVisible.value = true
}

// 删除活动
const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除活动"${row.title}"吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    loading.value = true
    activityStore.deleteActivity(row.id).then(() => {
      ElMessage.success('删除成功')
      getList()
    }).finally(() => {
      loading.value = false
    })
  }).catch(() => {})
}

// 批量删除
const handleBatchDelete = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请至少选择一条记录')
    return
  }
  const ids = selectedRows.value.map(item => item.id)
  ElMessageBox.confirm(`确认删除选中的${selectedRows.value.length}条记录吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    loading.value = true
    // 暂时注释掉，因为store中可能没有实现此方法
    // activityStore.batchDeleteActivity(ids).then(() => {
    Promise.all(ids.map(id => activityStore.deleteActivity(id))).then(() => {
      ElMessage.success('批量删除成功')
      getList()
    }).finally(() => {
      loading.value = false
    })
  }).catch(() => {})
}

// 导出活动
const handleExport = () => {
  ElMessage.info('导出功能开发中')
  // 暂时注释掉，因为store中可能没有实现此方法
  // loading.value = true
  // activityStore.exportActivity().then(response => {
  //   ElMessage.success('导出成功')
  // }).finally(() => {
  //   loading.value = false
  // })
}

// 处理状态变更命令
const handleStatusCommand = (command, row) => {
  const statusMap = {
    'start': { value: 1, text: '开始报名' },
    'end': { value: 2, text: '开始活动' },
    'finish': { value: 3, text: '结束活动' },
    'cancel': { value: 4, text: '取消活动' }
  }
  
  const { value, text } = statusMap[command]
  
  // 检查状态流转是否合法
  if (command === 'end' && row.status !== 1) {
    ElMessage.warning('只有在报名中的活动才能开始活动')
    return
  }
  
  if (command === 'finish' && row.status !== 2) {
    ElMessage.warning('只有进行中的活动才能结束活动')
    return
  }
  
  ElMessageBox.confirm(`确认将活动"${row.title}"状态更改为"${text}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    loading.value = true
    activityStore.updateActivityStatus(row.id, value).then(() => {
      ElMessage.success(`${text}成功`)
      getList()
    }).catch(error => {
      console.error('更新状态失败:', error)
    }).finally(() => {
      loading.value = false
    })
  }).catch(() => {})
}

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    0: 'info',    // 未开始
    1: 'success', // 报名中
    2: 'primary', // 进行中
    3: 'warning', // 已结束
    4: 'danger'   // 已取消
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '未开始',
    1: '报名中',
    2: '进行中',
    3: '已结束',
    4: '已取消'
  }
  return statusMap[status] || '未知'
}

// 处理图片上传成功
const handleImageChange = (res) => {
  if (res.code === 200) {
    formData.image = res.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error('上传失败')
  }
}

// 提交表单
const handleSubmit = () => {
  formRef.value?.validate((valid) => {
    if (valid) {
      if (formData.timeRange && formData.timeRange.length === 2) {
        formData.startTime = formData.timeRange[0]
        formData.endTime = formData.timeRange[1]
      }
      
      loading.value = true
      if (dialogType.value === 'add') {
        activityStore.createActivity(formData).then(() => {
          ElMessage.success('添加成功')
          dialogVisible.value = false
          getList()
        }).finally(() => {
          loading.value = false
        })
      } else if (dialogType.value === 'edit') {
        activityStore.updateActivity(formData.id, formData).then(() => {
          ElMessage.success('更新成功')
          dialogVisible.value = false
          getList()
        }).finally(() => {
          loading.value = false
        })
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(formData, {
    id: undefined,
    title: '',
    type: '',
    timeRange: [],
    startTime: '',
    endTime: '',
    location: '',
    maxParticipants: 50,
    currentParticipants: 0,
    image: '',
    description: '',
    status: 0
  })
}

// 初始化
onMounted(() => {
  try {
    getList()
    // 获取字典数据
    getDictDataByType('activity_type').then(res => {
      if (res.code === 200 && res.data) {
        activityTypes.value = res.data.map(item => ({
          value: item.dictValue,
          label: item.dictLabel
        }))
      }
    }).catch(() => {
      // 如果API调用失败，保留默认选项
    })
  } catch (error) {
    console.error('活动列表初始化失败:', error)
  }
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.mb8 {
  margin-bottom: 8px;
}

.avatar-uploader {
  width: 178px;
  height: 178px;
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: cover;
}

.dialog-footer {
  padding-top: 20px;
  text-align: right;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-table {
  margin-top: 15px;
  margin-bottom: 15px;
}

.el-date-picker {
  width: 100%;
}

.el-dropdown {
  margin-left: 10px;
  margin-right: 10px;
}
</style>
