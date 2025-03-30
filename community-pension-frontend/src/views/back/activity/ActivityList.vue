<template>
  <div class="activity-list">
    <!-- 搜索表单 -->
    <el-card class="search-card">
      <el-form :model="activityStore.queryParams" ref="searchFormRef" :inline="true">
        <el-form-item label="活动标题" prop="title">
          <el-input v-model="activityStore.queryParams.title" placeholder="请输入活动标题" clearable />
        </el-form-item>
        <el-form-item label="活动状态" prop="status">
          <el-select
            v-model="activityStore.queryParams.status"
            placeholder="活动状态"
            clearable
            style="width: 200px"
          >
            <el-option
              v-for="dict in statusOptions"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="活动时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateRangeChange"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>活动列表</span>
          <el-button type="primary" @click="handleAdd">发布活动</el-button>
        </div>
      </template>

      <!-- 活动列表 -->
      <el-table
        v-loading="activityStore.loading"
        :data="activityStore.activityList"
        border
        style="width: 100%"
      >
        <el-table-column prop="title" label="活动标题" min-width="200" />
        <el-table-column prop="type" label="活动类型" width="120">
          <template #default="{ row }">
            {{ getActivityTypeName(row.type) }}
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column prop="location" label="活动地点" width="150" />
        <el-table-column prop="maxParticipants" label="人数上限" width="100" />
        <el-table-column prop="currentParticipants" label="当前人数" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button-group>
              <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
              <el-button type="primary" link @click="handleView(row)">查看</el-button>
              <el-button
                v-if="row.status === '0'"
                type="success"
                link
                @click="handleStart(row)"
              >开始报名</el-button>
              <el-button
                v-if="row.status === '1'"
                type="warning"
                link
                @click="handleEnd(row)"
              >开始活动</el-button>
              <el-button
                v-if="row.status === '2'"
                type="info"
                link
                @click="handleFinish(row)"
              >结束活动</el-button>
              <el-button
                v-if="['0', '1', '2'].includes(row.status)"
                type="danger"
                link
                @click="handleCancel(row)"
              >取消</el-button>
              <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="activityStore.queryParams.pageNum"
          v-model:page-size="activityStore.queryParams.pageSize"
          :total="activityStore.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 活动表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '发布活动' : '编辑活动'"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="活动标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入活动标题" />
        </el-form-item>
        <el-form-item label="活动类型" prop="type">
          <el-select v-model="formData.type" placeholder="请选择活动类型">
            <el-option
              v-for="item in activityTypes"
              :key="item.dictValue"
              :label="item.dictLabel"
              :value="item.dictValue"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="活动时间" prop="time">
          <el-date-picker
            v-model="formData.time"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="活动地点" prop="location">
          <el-input v-model="formData.location" placeholder="请输入活动地点" />
        </el-form-item>
        <el-form-item label="人数上限" prop="maxParticipants">
          <el-input-number v-model="formData.maxParticipants" :min="1" />
        </el-form-item>
        <el-form-item label="活动描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入活动描述"
          />
        </el-form-item>
        <el-form-item v-if="dialogType === 'edit'" label="活动状态" prop="status">
          <el-select v-model="formData.status" placeholder="请选择活动状态">
            <el-option
              v-for="dict in statusOptions"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 活动详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="活动详情"
      width="700px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="活动标题">{{ detailData.title }}</el-descriptions-item>
        <el-descriptions-item label="活动类型">{{ getActivityTypeName(detailData.type) }}</el-descriptions-item>
        <el-descriptions-item label="活动状态">
          <el-tag :type="getStatusType(detailData.status)">
            {{ getStatusText(detailData.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="活动地点">{{ detailData.location }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ detailData.startTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ detailData.endTime }}</el-descriptions-item>
        <el-descriptions-item label="人数上限">{{ detailData.maxParticipants }}</el-descriptions-item>
        <el-descriptions-item label="当前人数">{{ detailData.currentParticipants }}</el-descriptions-item>
        <el-descriptions-item label="剩余名额">{{ detailData.maxParticipants - detailData.currentParticipants }}</el-descriptions-item>
        <el-descriptions-item label="活动描述" :span="2">{{ detailData.description }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useActivityStore } from '@/stores/back/activityStore'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDictDataByType } from '@/api/back/system/dict/data'

const activityStore = useActivityStore()
const searchFormRef = ref(null)
const formRef = ref(null)
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const dialogType = ref('add')
const dateRange = ref([])
const activityTypes = ref([]) // 活动类型列表
const detailData = ref({}) // 活动详情数据

// 表单数据
const formData = ref({
  title: '',
  type: '',
  time: [],
  location: '',
  maxParticipants: 50,
  description: '',
  status: 0
})

// 表单验证规则
const formRules = {
  title: [{ required: true, message: '请输入活动标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择活动类型', trigger: 'change' }],
  time: [{ required: true, message: '请选择活动时间', trigger: 'change' }],
  location: [{ required: true, message: '请输入活动地点', trigger: 'blur' }],
  maxParticipants: [{ required: true, message: '请输入人数上限', trigger: 'blur' }],
  description: [{ required: true, message: '请输入活动描述', trigger: 'blur' }],
  status: [{ required: true, message: '请选择活动状态', trigger: 'change' }]
}

// 获取活动类型列表
const getActivityTypes = async () => {
  try {
    const res = await getDictDataByType('activity_type')
    if (res.code === 200) {
      activityTypes.value = res.data
      console.log('活动类型列表:', activityTypes.value)
    } else {
      ElMessage.error('获取活动类型失败')
    }
  } catch (error) {
    console.error('获取活动类型失败:', error)
    ElMessage.error('获取活动类型失败')
  }
}

// 获取活动类型名称
const getActivityTypeName = (type) => {
  if (!type) return '未知类型'
  const typeItem = activityTypes.value.find(item => item.dictValue === type)
  console.log('查找活动类型:', type, '结果:', typeItem)
  return typeItem ? typeItem.dictLabel : '未知类型'
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
  formData.value = {
    title: '',
    type: '',
    time: [],
    location: '',
    maxParticipants: 50,
    description: '',
    status: 0
  }
  dialogVisible.value = true
}

// 编辑活动
const handleEdit = (row) => {
  dialogType.value = 'edit'
  formData.value = {
    ...row,
    time: [row.startTime, row.endTime],
    status: row.status
  }
  dialogVisible.value = true
}

// 查看活动
const handleView = async (row) => {
  // 确保活动类型列表已加载
  if (activityTypes.value.length === 0) {
    await getActivityTypes()
  }
  detailData.value = { ...row }
  detailDialogVisible.value = true
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
        ...formData.value,
        startTime: formData.value.time[0],
        endTime: formData.value.time[1]
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

// 在 script setup 部分添加
const statusOptions = [
  { value: 0, label: '筹备中' },
  { value: 1, label: '报名中' },
  { value: 2, label: '进行中' },
  { value: 3, label: '已结束' },
  { value: 4, label: '已取消' }
]

// 初始化
onMounted(() => {
  activityStore.fetchActivityList()
  getActivityTypes()
})
</script>

<style scoped>
.activity-list {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>

