<template>
  <div class="activity-participate">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>活动参与管理</span>
          <div class="header-right">
            <el-button type="primary" @click="handleExport">导出</el-button>
          </div>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="活动名称">
          <el-select v-model="queryParams.activityId" placeholder="请选择活动">
            <el-option
              v-for="item in activityOptions"
              :key="item.id"
              :label="item.title"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="参与状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态">
            <el-option label="已报名" :value="0" />
            <el-option label="已签到" :value="1" />
            <el-option label="已取消" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 参与记录列表 -->
      <el-table :data="participateList" v-loading="loading">
        <el-table-column prop="activityTitle" label="活动名称" />
        <el-table-column prop="elderName" label="参与人" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="signTime" label="签到时间" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 0"
              type="primary" 
              link 
              @click="handleAudit(row)"
            >
              审核
            </el-button>
            <el-button 
              v-if="row.status === 1"
              type="success" 
              link 
              @click="handleCheckin(row)"
            >
              签到
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        @current-change="handleQuery"
      />
    </el-card>

    <!-- 审核对话框 -->
    <el-dialog
      v-model="auditDialogVisible"
      title="参与审核"
      width="500px"
    >
      <el-form ref="auditFormRef" :model="auditForm" :rules="auditRules" label-width="100px">
        <el-form-item label="审核结果" prop="result">
          <el-radio-group v-model="auditForm.result">
            <el-radio :value="1">通过</el-radio>
            <el-radio :value="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见" prop="remark">
          <el-input type="textarea" v-model="auditForm.remark" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAuditSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  activityId: '',
  status: ''
})

// 活动选项
const activityOptions = ref([])

// 参与记录列表
const participateList = ref([])
const loading = ref(false)
const total = ref(0)

// 审核相关
const auditDialogVisible = ref(false)
const auditFormRef = ref(null)
const auditForm = reactive({
  id: '',
  result: 1,
  remark: ''
})

// 审核表单验证规则
const auditRules = {
  result: [{ required: true, message: '请选择审核结果', trigger: 'change' }],
  remark: [{ required: true, message: '请输入审核意见', trigger: 'blur' }]
}

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

// 查询参与记录
const handleQuery = async () => {
  loading.value = true
  try {
    // TODO: 调用API获取参与记录列表
    loading.value = false
  } catch (error) {
    loading.value = false
    ElMessage.error('获取参与记录失败')
  }
}

// 重置查询
const resetQuery = () => {
  queryParams.activityId = ''
  queryParams.status = ''
  handleQuery()
}

// 导出数据
const handleExport = async () => {
  try {
    // TODO: 调用导出API
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

// 审核操作
const handleAudit = (row) => {
  auditForm.id = row.id
  auditForm.result = 1
  auditForm.remark = ''
  auditDialogVisible.value = true
}

// 提交审核
const handleAuditSubmit = async () => {
  if (!auditFormRef.value) return
  await auditFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // TODO: 调用审核API
        ElMessage.success('审核成功')
        auditDialogVisible.value = false
        handleQuery()
      } catch (error) {
        ElMessage.error('审核失败')
      }
    }
  })
}

// 签到操作
const handleCheckin = async (row) => {
  try {
    // TODO: 调用签到API
    ElMessage.success('签到成功')
    handleQuery()
  } catch (error) {
    ElMessage.error('签到失败')
  }
}

// 初始化
handleQuery()
</script>

<style scoped>
.activity-participate {
  padding: 24px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 84px);
  animation: fadeIn 0.5s ease-out;
}

:deep(.el-card) {
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: linear-gradient(to right bottom, #ffffff, #fafafa);
}

:deep(.el-card:hover) {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: linear-gradient(to right, #f8f9fa, #ffffff);
  border-radius: 12px 12px 0 0;
}

.card-header span {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
  letter-spacing: 0.5px;
}

.header-right {
  display: flex;
  gap: 12px;
}

.search-form {
  padding: 24px;
  background: linear-gradient(135deg, #f8f9fa, #ffffff);
  border-radius: 12px;
  margin: 0 16px 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

:deep(.el-table) {
  margin: 0 16px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

:deep(.el-table th) {
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
