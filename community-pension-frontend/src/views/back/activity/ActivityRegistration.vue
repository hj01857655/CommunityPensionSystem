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
      <el-form-item label="报名人" prop="elderName">
        <el-select v-model="queryParams.elderName" placeholder="请选择报名人" clearable style="width: 200px">
          <el-option
            v-for="item in elderOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="报名状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 200px">
          <el-option label="待审核" :value="0" />
          <el-option label="已通过" :value="1" />
          <el-option label="已拒绝" :value="2" />
          <el-option label="已取消" :value="3" />
          <el-option label="已签到" :value="4" />
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
        <el-button type="primary" plain :icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain :icon="Download" @click="handleExport">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="(params) => getList(params)" :columns="columns"></right-toolbar>
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
            v-if="row.status === 1 && row.activityStatus === 2"
            type="primary"
            link
            :icon="Check"
            @click="handleCheckin(row)"
          >
            签到
          </el-button>
          <el-tooltip v-else-if="row.status === 1 && row.activityStatus !== 2" content="活动尚未开始或已结束，无法签到" placement="top">
            <el-button
              type="info"
              link
              :icon="Check"
              disabled
            >
              签到
            </el-button>
          </el-tooltip>
          <el-button
            v-if="row.status === 0 || row.status === 1"
            type="danger"
            link
            :icon="Delete"
            @click="handleCancel(row)"
          >
            取消报名
          </el-button>
          <el-button
            v-if="row.status === 0"
            type="warning"
            link
            :icon="Edit"
            @click="handleAudit(row)"
          >
            审核
          </el-button>
          <el-button
            v-if="row.status === 4"
            type="info"
            link
            :icon="View"
            @click="handleViewDetail(row)"
          >
            查看详情
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
      @pagination="(params) => getList(params)"
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

    <!-- 查看详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="参与详情"
      width="600px"
      append-to-body
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="活动名称" :span="2">{{ detailForm.activityTitle }}</el-descriptions-item>
        <el-descriptions-item label="参与者姓名">{{ detailForm.elderName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailForm.elderPhone || '无' }}</el-descriptions-item>
        <el-descriptions-item label="报名时间">{{ detailForm.applyTime }}</el-descriptions-item>
        <el-descriptions-item label="签到时间">{{ detailForm.signTime }}</el-descriptions-item>
        <el-descriptions-item label="参与状态" :span="2">
          <el-tag :type="getStatusType(detailForm.status)">
            {{ getStatusText(detailForm.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailForm.remark || '无' }}</el-descriptions-item>
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
import RightToolbar from '@/components/common/base/RightToolbar/index.vue'
import Pagination from '@/components/common/table/Pagination.vue'
import { useParticipateStore } from '@/stores/back/participateStore'
import { Check, Delete, Download, Edit, Plus, Refresh, Search, View } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'

// 使用Pinia状态管理
const participateStore = useParticipateStore()

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  activityId: '',
  elderName: '',
  status: ''
})

// 列表数据
const participateList = computed(() => participateStore.list)
const total = computed(() => participateStore.total)
const loading = computed(() => participateStore.loading)

// 选中数组
const selectedRows = ref([])
// 显示搜索条件
const showSearch = ref(true)

// 活动选项
const activityOptions = computed(() => participateStore.activityOptions)
// 老人选项
const elderOptions = computed(() => participateStore.elderOptions)

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

// 查看详情对话框
const detailDialogVisible = ref(false)
const detailForm = reactive({
  activityTitle: '',
  elderName: '',
  elderPhone: '',
  applyTime: '',
  signTime: '',
  status: '',
  remark: ''
})

// 获取状态类型
const getStatusType = (status) => {
  return participateStore.statusTypeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  return participateStore.statusTextMap[status] || '未知'
}

// 多选框选中数据
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 查询参与记录
const getList = (params) => {
  // 处理分页参数
  if (params) {
    queryParams.pageNum = params.page
    queryParams.pageSize = params.limit
  }

  // 调用store获取参与记录列表
  participateStore.getList(queryParams).catch(error => {
    ElMessage.error('获取参与记录列表失败：' + error.message)
  })
}

// 搜索按钮操作
const handleQuery = () => {
  queryParams.pageNum = 1
  getList(null)
}

// 重置按钮操作
const resetQuery = () => {
  if (queryRef.value) {
    queryRef.value.resetFields()
  }
  queryParams.pageNum = 1
  queryParams.pageSize = 10
  queryParams.activityId = ''
  queryParams.elderName = ''
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
      // 调用store新增报名
      participateStore.add(addForm).then(response => {
        ElMessage.success('新增报名成功')
        addDialogVisible.value = false
        getList(null)
      }).catch(error => {
        ElMessage.error('新增报名失败：' + error.message)
      })
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
      // 调用导出API
      participateStore.exportList(queryParams).then(() => {
        ElMessage.success('导出成功')
      }).catch(error => {
        ElMessage.error('导出失败：' + error.message)
      })
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
      // 调用store提交审核
      participateStore.audit({
        id: auditForm.id,
        status: auditForm.result,
        remark: auditForm.remark
      }).then(response => {
        ElMessage.success('审核提交成功')
        auditDialogVisible.value = false
        getList(null)
      }).catch(error => {
        ElMessage.error('审核提交失败：' + error.message)
      })
    }
  })
}

// 签到操作
const handleCheckin = (row) => {
  // 再次检查活动状态，确保只有"进行中"的活动才能签到
  if (row.activityStatus !== 2) {
    ElMessage.warning('只有进行中的活动才能签到');
    return;
  }

  ElMessageBox.confirm(`确认为 ${row.elderName} 进行签到操作吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => {
    // 从sessionStorage获取用户信息
    const userInfoStr = sessionStorage.getItem('userInfo');
    if (!userInfoStr) {
      ElMessage.error('未获取到用户信息，请重新登录');
      return;
    }

    // 解析用户信息
    try {
      const userInfo = JSON.parse(userInfoStr);
      if (!userInfo || !userInfo.userId) {
        ElMessage.error('用户信息不完整，请重新登录');
        return;
      }

      // 调用store进行签到操作，传入用户ID
      participateStore.checkin(row.id, userInfo.userId).then(response => {
        ElMessage.success('签到成功')
        getList(null)
      }).catch(error => {
        ElMessage.error('签到失败：' + error.message)
      })
    } catch (error) {
      ElMessage.error('解析用户信息失败，请重新登录');
    }
  }).catch(() => {})
}

// 取消报名
const handleCancel = (row) => {
  ElMessageBox.confirm(`确认取消 ${row.elderName} 的报名记录吗?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 调用store取消报名
    participateStore.cancel(row.id).then(response => {
      ElMessage.success('取消报名成功')
      getList(null)
    }).catch(error => {
      ElMessage.error('取消报名失败：' + error.message)
    })
  }).catch(() => {})
}

// 查看详情
const handleViewDetail = (row) => {
  detailDialogVisible.value = true
  detailForm.activityTitle = row.activityTitle
  detailForm.elderName = row.elderName
  detailForm.elderPhone = row.elderPhone
  detailForm.applyTime = row.applyTime
  detailForm.signTime = row.signTime
  detailForm.status = row.status
  detailForm.remark = row.remark
}

// 初始化
onMounted(() => {
  participateStore.getActivityOptions()
  participateStore.getElderOptions()
  getList(null)
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
