<template>
  <div class="activity-checkin">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>活动签到管理</span>
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
        <el-form-item label="签到日期">
          <el-date-picker
            v-model="queryParams.date"
            type="date"
            placeholder="选择日期"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 签到记录列表 -->
      <el-table :data="checkinList" v-loading="loading">
        <el-table-column prop="activityTitle" label="活动名称" />
        <el-table-column prop="elderName" label="参与人" />
        <el-table-column prop="signInTime" label="签到时间" />
        <el-table-column prop="signOutTime" label="签退时间" />
        <el-table-column prop="remarks" label="备注" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button 
              v-if="!row.signOutTime"
              type="primary" 
              link 
              @click="handleSignOut(row)"
            >
              签退
            </el-button>
            <el-button 
              type="warning" 
              link 
              @click="handleRemark(row)"
            >
              备注
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

    <!-- 备注对话框 -->
    <el-dialog
      v-model="remarkDialogVisible"
      title="添加备注"
      width="500px"
    >
      <el-form ref="remarkFormRef" :model="remarkForm" :rules="remarkRules" label-width="100px">
        <el-form-item label="备注内容" prop="remarks">
          <el-input type="textarea" v-model="remarkForm.remarks" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="remarkDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRemarkSubmit">确定</el-button>
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
  date: ''
})

// 活动选项
const activityOptions = ref([])

// 签到记录列表
const checkinList = ref([])
const loading = ref(false)
const total = ref(0)

// 备注相关
const remarkDialogVisible = ref(false)
const remarkFormRef = ref(null)
const remarkForm = reactive({
  id: '',
  remarks: ''
})

// 备注表单验证规则
const remarkRules = {
  remarks: [{ required: true, message: '请输入备注内容', trigger: 'blur' }]
}

// 查询签到记录
const handleQuery = async () => {
  loading.value = true
  try {
    // TODO: 调用API获取签到记录列表
    loading.value = false
  } catch (error) {
    loading.value = false
    ElMessage.error('获取签到记录失败')
  }
}

// 重置查询
const resetQuery = () => {
  queryParams.activityId = ''
  queryParams.date = ''
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

// 签退操作
const handleSignOut = async (row) => {
  try {
    // TODO: 调用签退API
    ElMessage.success('签退成功')
    handleQuery()
  } catch (error) {
    ElMessage.error('签退失败')
  }
}

// 添加备注
const handleRemark = (row) => {
  remarkForm.id = row.id
  remarkForm.remarks = row.remarks || ''
  remarkDialogVisible.value = true
}

// 提交备注
const handleRemarkSubmit = async () => {
  if (!remarkFormRef.value) return
  await remarkFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // TODO: 调用更新备注API
        ElMessage.success('更新备注成功')
        remarkDialogVisible.value = false
        handleQuery()
      } catch (error) {
        ElMessage.error('更新备注失败')
      }
    }
  })
}

// 初始化
handleQuery()
</script>

<style scoped>
.activity-checkin {
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