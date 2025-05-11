<template>
  <div class="app-container">
    <!-- 搜索条件 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="活动名称" prop="activityId">
        <el-select v-model="queryParams.activityId" placeholder="请选择活动" clearable style="width: 200px">
          <el-option
            v-for="item in activityOptions"
            :key="item.id"
            :label="item.title"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="报名状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 200px">
          <el-option label="已报名" :value="0" />
          <el-option label="已签到" :value="1" />
          <el-option label="已取消" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
        <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain :icon="Plus" @click="handleAdd">新增报名</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain :icon="Download" @click="handleExport">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
    </el-row>

    <!-- 报名记录列表 -->
    <el-table v-loading="loading" :data="participateList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" v-if="columns[0].visible" />
      <el-table-column prop="activityTitle" label="活动名称" v-if="columns[1].visible" />
      <el-table-column prop="elderName" label="报名人" v-if="columns[2].visible" />
      <el-table-column prop="status" label="状态" v-if="columns[3].visible">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="signTime" label="签到时间" v-if="columns[4].visible" />
      <el-table-column prop="applyTime" label="报名时间" v-if="columns[5].visible" />
      <el-table-column prop="remark" label="备注" v-if="columns[6].visible" />
      <el-table-column label="操作" width="180" v-if="columns[7].visible">
        <template #default="{ row }">
          <el-button 
            v-if="row.status === 0"
            type="primary" 
            link 
            :icon="Check"
            @click="handleAudit(row)"
          >
            审核
          </el-button>
          <el-button 
            v-if="row.status === 1"
            type="primary" 
            link 
            :icon="Check"
            @click="handleCheckin(row)"
          >
            签到
          </el-button>
          <el-button 
            type="primary" 
            link 
            :icon="Delete"
            @click="handleCancel(row)"
          >
            取消
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <pagination
      v-if="total > 0"
      :total="total"
      :page="queryParams.pageNum"
      :limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 审核对话框 -->
    <el-dialog
      v-model="auditDialogVisible"
      title="报名审核"
      width="500px"
      append-to-body
    >
      <el-form ref="auditFormRef" :model="auditForm" :rules="auditRules" label-width="100px">
        <el-form-item label="审核结果" prop="result">
          <el-radio-group v-model="auditForm.result">
            <el-radio :value="1">通过</el-radio>
            <el-radio :value="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见" prop="remark">
          <el-input type="textarea" v-model="auditForm.remark" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="auditDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAuditSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 新增报名对话框 -->
    <el-dialog
      v-model="addDialogVisible"
      title="新增报名"
      width="500px"
      append-to-body
    >
      <el-form ref="addFormRef" :model="addForm" :rules="addRules" label-width="100px">
        <el-form-item label="活动" prop="activityId">
          <el-select v-model="addForm.activityId" placeholder="请选择活动" style="width: 100%">
            <el-option
              v-for="item in activityOptions"
              :key="item.id"
              :label="item.title"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="报名人" prop="elderId">
          <el-select v-model="addForm.elderId" placeholder="请选择报名人" style="width: 100%">
            <el-option
              v-for="item in elderOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input type="textarea" v-model="addForm.remark" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAddSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Check, Delete, Download } from '@element-plus/icons-vue'
import RightToolbar from '@/components/common/base/RightToolbar/index.vue'
import Pagination from '@/components/common/Pagination.vue'

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  activityId: '',
  status: ''
})

// 遮罩层
const loading = ref(false)
// 选中数组
const selectedRows = ref([])
// 显示搜索条件
const showSearch = ref(true)
// 总条数
const total = ref(0)
// 报名记录列表
const participateList = ref([])
// 活动选项
const activityOptions = ref([])
// 老人选项
const elderOptions = ref([])

// 列显示控制
const columns = ref([
  { key: 0, label: '选择列', visible: true },
  { key: 1, label: '活动名称', visible: true },
  { key: 2, label: '报名人', visible: true },
  { key: 3, label: '状态', visible: true },
  { key: 4, label: '签到时间', visible: true },
  { key: 5, label: '报名时间', visible: true },
  { key: 6, label: '备注', visible: true },
  { key: 7, label: '操作', visible: true }
])

// 审核对话框
const auditDialogVisible = ref(false)
const auditForm = reactive({
  id: '',
  result: 1,
  remark: ''
})
const auditRules = {
  result: [
    { required: true, message: '请选择审核结果', trigger: 'change' }
  ]
}
const auditFormRef = ref(null)

// 新增报名对话框
const addDialogVisible = ref(false)
const addForm = reactive({
  activityId: '',
  elderId: '',
  remark: ''
})
const addRules = {
  activityId: [
    { required: true, message: '请选择活动', trigger: 'change' }
  ],
  elderId: [
    { required: true, message: '请选择报名人', trigger: 'change' }
  ]
}
const addFormRef = ref(null)
const queryRef = ref(null)

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    0: 'info',
    1: 'success',
    2: 'danger'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    0: '已报名',
    1: '已签到',
    2: '已取消'
  }
  return texts[status] || '未知'
}

// 多选框选中数据
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 查询参与记录
const getList = (params) => {
  loading.value = true
  
  // 处理分页参数
  if (params) {
    queryParams.pageNum = params.page
    queryParams.pageSize = params.limit
  }
  
  // 这里应该调用API获取参与记录列表
  // 模拟数据
  setTimeout(() => {
    // 模拟数据
    participateList.value = [
      {
        id: '1',
        activityId: '1',
        activityTitle: '健康讲座',
        elderId: '1',
        elderName: '张三',
        status: 1,
        signTime: '2025-05-06 09:30:00',
        applyTime: '2025-05-01 15:20:00',
        remark: '对健康知识很感兴趣'
      },
      {
        id: '2',
        activityId: '1',
        activityTitle: '健康讲座',
        elderId: '2',
        elderName: '李四',
        status: 0,
        signTime: null,
        applyTime: '2025-05-02 10:15:00',
        remark: ''
      }
    ]
    total.value = 2
    loading.value = false
  }, 300)
}

// 查询活动列表
const getActivityOptions = () => {
  // 这里应该调用API获取活动列表
  // 模拟数据
  activityOptions.value = [
    { id: '1', title: '健康讲座' },
    { id: '2', title: '太极拳教学' },
    { id: '3', title: '书法比赛' }
  ]
}

// 查询老人列表
const getElderOptions = () => {
  // 这里应该调用API获取老人列表
  // 模拟数据
  elderOptions.value = [
    { id: '1', name: '张三' },
    { id: '2', name: '李四' },
    { id: '3', name: '王五' }
  ]
}

// 搜索按钮操作
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置按钮操作
const resetQuery = () => {
  if (queryRef.value) {
    queryRef.value.resetFields()
  }
  queryParams.pageNum = 1
  queryParams.pageSize = 10
  queryParams.activityId = ''
  queryParams.status = ''
  handleQuery()
}

// 新增报名
const handleAdd = () => {
  addDialogVisible.value = true
  addForm.activityId = ''
  addForm.elderId = ''
  addForm.remark = ''
  
  if (addFormRef.value) {
    addFormRef.value.resetFields()
  }
}

// 提交新增报名
const handleAddSubmit = () => {
  if (!addFormRef.value) return
  
  addFormRef.value.validate((valid) => {
    if (valid) {
      // 这里应该调用API提交新增报名
      ElMessage.success('新增报名成功')
      addDialogVisible.value = false
      getList()
    }
  })
}

// 导出数据
const handleExport = () => {
  ElMessageBox.confirm('确认导出所有报名记录数据?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      // 这里应该调用导出API
      ElMessage.success('导出成功')
    })
    .catch(() => {})
}

// 审核操作
const handleAudit = (row) => {
  auditDialogVisible.value = true
  auditForm.id = row.id
  auditForm.result = 1
  auditForm.remark = ''
  
  if (auditFormRef.value) {
    auditFormRef.value.resetFields()
  }
}

// 提交审核
const handleAuditSubmit = () => {
  if (!auditFormRef.value) return
  
  auditFormRef.value.validate((valid) => {
    if (valid) {
      // 这里应该调用API提交审核
      ElMessage.success('审核提交成功')
      auditDialogVisible.value = false
      getList()
    }
  })
}

// 签到操作
const handleCheckin = (row) => {
  ElMessageBox.confirm(`确认为 ${row.elderName} 进行签到操作吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => {
    // 这里应该调用API进行签到操作
    ElMessage.success('签到成功')
    getList()
  }).catch(() => {})
}

// 取消报名
const handleCancel = (row) => {
  ElMessageBox.confirm(`确认取消 ${row.elderName} 的报名记录吗?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 这里应该调用API取消报名
    ElMessage.success('取消报名成功')
    getList()
  }).catch(() => {})
}

// 初始化
onMounted(() => {
  getActivityOptions()
  getElderOptions()
  getList()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.mb8 {
  margin-bottom: 8px;
}

.dialog-footer {
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

:deep(.el-tag--danger) {
  background: linear-gradient(45deg, #ffe6e6, #fff0f0);
  color: #f56c6c;
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

:deep(.el-pagination) {
  margin-top: 24px;
  padding: 16px 24px;
  display: flex;
  justify-content: flex-end;
  background: linear-gradient(to right, #f8f9fa, #ffffff);
  border-radius: 0 0 12px 12px;
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

:deep(.el-form-item__label) {
  font-weight: 600;
  color: #1a1a1a;
  font-size: 14px;
}

:deep(.el-input__wrapper),
:deep(.el-select__wrapper),
:deep(.el-radio-group) {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 8px;
}

:deep(.el-input__wrapper:hover),
:deep(.el-select__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.1);
}

:deep(.el-input__wrapper.is-focus),
:deep(.el-select__wrapper.is-focus) {
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 添加响应式布局 */
@media screen and (max-width: 768px) {
  .activity-participate {
    padding: 16px;
  }
  
  .search-form {
    padding: 16px;
    margin: 0 8px 16px;
  }
  
  :deep(.el-table) {
    margin: 0 8px;
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
