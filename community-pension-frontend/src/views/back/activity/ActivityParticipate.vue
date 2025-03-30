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
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="2">拒绝</el-radio>
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
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}
</style>
