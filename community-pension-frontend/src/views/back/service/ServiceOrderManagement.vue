<template>
  <div class="app-container">
    <!-- 搜索条件 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="服务名称" prop="serviceName">
        <el-input
          v-model="queryParams.serviceName"
          placeholder="请输入服务名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleSearch"
        />
      </el-form-item>
      <el-form-item label="工单状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择工单状态"
          clearable
          style="width: 200px"
        >
          <el-option label="待审核" value="0" />
          <el-option label="已派单" value="1" />
          <el-option label="服务中" value="2" />
          <el-option label="已完成" value="3" />
          <el-option label="已取消" value="4" />
          <el-option label="已拒绝" value="5" />
        </el-select>
      </el-form-item>
      <el-form-item label="预约时间">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          style="width: 240px"
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
        <el-button type="primary" plain :icon="Plus" @click="handleAdd">新增预约</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain :icon="Edit" :disabled="single" @click="handleEdit">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain :icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 表格 -->
    <el-table
      v-loading="loading"
      :data="appointmentList"
      @selection-change="handleSelectionChange"
      border
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column prop="id" label="工单编号" width="80" align="center" />
      <el-table-column prop="serviceName" label="服务名称" min-width="150" align="center" />
      <el-table-column prop="elderName" label="服务对象" width="120" align="center">
        <template #default="scope">
          {{ scope.row.elderName || scope.row.userName || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="appointmentTime" label="预约时间" width="180" align="center">
        <template #default="scope">
          {{ scope.row.scheduleTime ? formatDateTime(scope.row.scheduleTime) : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100" align="center">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" align="center">
        <template #default="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="300" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button type="primary" link :icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="success" link :icon="Check" @click="handleReview(scope.row)" v-if="scope.row.status === 0">审核</el-button>
          <el-button type="success" link :icon="Check" @click="handleAssign(scope.row)" v-if="scope.row.status === 0">派单</el-button>
          <el-button type="warning" link :icon="Close" @click="handleCancel(scope.row)" v-if="[0, 1].includes(scope.row.status)">取消</el-button>
          <el-button type="danger" link :icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <pagination
      v-if="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 审核对话框 -->
    <el-dialog
      v-model="reviewDialogVisible"
      title="审核预约"
      width="500px"
      append-to-body
    >
      <el-form
        ref="reviewFormRef"
        :model="reviewForm"
        :rules="reviewRules"
        label-width="100px"
      >
        <el-form-item label="审核结果" prop="status">
          <el-radio-group v-model="reviewForm.status">
            <el-radio :value="1">通过</el-radio>
            <el-radio :value="5">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核备注" prop="reviewRemark">
          <el-input
            v-model="reviewForm.reviewRemark"
            type="textarea"
            :rows="3"
            placeholder="请输入审核备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitReview">确 定</el-button>
          <el-button @click="reviewDialogVisible = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 预约表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增预约' : '编辑预约'"
      width="600px"
      append-to-body
    >
      <el-form
        ref="appointmentFormRef"
        :model="appointmentForm"
        :rules="appointmentRules"
        label-width="100px"
      >
        <el-form-item label="服务名称" prop="serviceId">
          <el-select v-model="appointmentForm.serviceId" placeholder="请选择服务">
            <el-option
              v-for="service in serviceOptions"
              :key="service.id"
              :label="service.name"
              :value="service.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="预约老人" prop="elderId">
          <el-select v-model="appointmentForm.elderId" placeholder="请选择老人">
            <el-option
              v-for="elder in elderOptions"
              :key="elder.id"
              :label="elder.name"
              :value="elder.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="预约时间" prop="appointmentTime">
          <el-date-picker
            v-model="appointmentForm.appointmentTime"
            type="datetime"
            placeholder="选择预约时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="appointmentForm.remark" type="textarea" :rows="3" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="dialogVisible = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Delete, Edit, Plus, Refresh, Check, Close } from '@element-plus/icons-vue'
import { formatDate, formatDateTime } from '@/utils/date'
import Pagination from '@/components/common/Pagination.vue'
import RightToolbar from '@/components/common/base/RightToolbar/index.vue'
import { useServiceOrderStore } from '@/stores/back/service'
import { useServiceItemStore } from '@/stores/back/service'

// 初始化 store
const serviceOrderStore = useServiceOrderStore()
const serviceItemStore = useServiceItemStore()

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  serviceName: '',
  status: undefined,
  startTime: '',
  endTime: ''
})

// 日期范围
const dateRange = ref([])

// 显示搜索条件
const showSearch = ref(true)

// 非单个禁用
const single = ref(true)
// 非多个禁用
const multiple = ref(true)

// 加载状态
const loading = ref(false)

// 预约列表数据
const appointmentList = ref([])
const total = ref(0)

// 服务选项
const serviceOptions = ref([])

// 老人选项
const elderOptions = ref([])

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const appointmentFormRef = ref(null)

// 预约表单
const appointmentForm = ref({
  serviceId: '',
  elderId: '',
  appointmentTime: '',
  remark: ''
})

// 表单验证规则
const appointmentRules = {
  serviceId: [{ required: true, message: '请选择服务', trigger: 'change' }],
  elderId: [{ required: true, message: '请选择老人', trigger: 'change' }],
  appointmentTime: [{ required: true, message: '请选择预约时间', trigger: 'change' }]
}

// 审核对话框相关
const reviewDialogVisible = ref(false)
const reviewFormRef = ref(null)
const reviewForm = ref({
  id: '',
  status: 1,
  reviewRemark: ''
})

// 审核表单验证规则
const reviewRules = {
  status: [{ required: true, message: '请选择审核结果', trigger: 'change' }],
  reviewRemark: [{ required: true, message: '请输入审核备注', trigger: 'blur' }]
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    0: 'info',
    1: 'warning',
    2: 'primary',
    3: 'success',
    4: 'danger',
    5: 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    0: '待审核',
    1: '已派单',
    2: '服务中',
    3: '已完成',
    4: '已取消',
    5: '已拒绝'
  }
  return textMap[status] || '未知'
}

// 获取列表数据
const getList = async () => {
  try {
    console.log('开始获取工单列表，查询参数:', queryParams);
    await serviceOrderStore.getOrderList({
      current: queryParams.pageNum,
      size: queryParams.pageSize,
      serviceName: queryParams.serviceName,
      status: queryParams.status,
      startTime: queryParams.startTime,
      endTime: queryParams.endTime
    });
    console.log('从store获取到的工单列表:', serviceOrderStore.orderList);
    // 直接使用后端返回的数据，因为状态值已经是数字类型
    appointmentList.value = serviceOrderStore.orderList;
    total.value = serviceOrderStore.total;
    console.log('处理后的预约列表:', appointmentList.value);
    // 打印每条记录的状态值
    appointmentList.value.forEach(item => {
      console.log(`工单 ${item.id} 的状态值:`, item.status, typeof item.status);
    });
  } catch (error) {
    console.error('获取工单列表失败:', error);
    ElMessage.error('获取工单列表失败');
  }
}

// 搜索按钮操作
const handleSearch = () => {
  queryParams.pageNum = 1
  if (dateRange.value && dateRange.value.length === 2) {
    queryParams.startTime = dateRange.value[0]
    queryParams.endTime = dateRange.value[1]
  } else {
    queryParams.startTime = ''
    queryParams.endTime = ''
  }
  getList()
}

// 重置按钮操作
const handleReset = () => {
  dateRange.value = []
  queryParams.serviceName = ''
  queryParams.status = undefined
  queryParams.startTime = ''
  queryParams.endTime = ''
  handleSearch()
}

// 多选框选中数据
const handleSelectionChange = (selection) => {
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

// 新增按钮操作
const handleAdd = () => {
  dialogType.value = 'add'
  appointmentForm.value = {
    serviceId: '',
    elderId: '',
    appointmentTime: '',
    remark: ''
  }
  dialogVisible.value = true
}

// 修改按钮操作
const handleEdit = (row) => {
  dialogType.value = 'edit'
  appointmentForm.value = {
    id: row.id,
    serviceId: row.serviceItemId,
    serviceName: row.serviceName,
    elderId: row.userId,
    appointmentTime: row.scheduleTime,
    remark: row.applyReason,
    status: row.status
  }
  // 确保老人选项包含当前选中的老人
  const currentElder = {
    id: row.userId,
    name: row.userName
  }
  if (!elderOptions.value.some(elder => elder.id === currentElder.id)) {
    elderOptions.value.push(currentElder)
  }
  dialogVisible.value = true
}

// 删除按钮操作
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`是否确认删除工单编号为"${row.id}"的工单记录？`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await serviceOrderStore.reviewOrder(row.id, {
      status: 4,
      reviewRemark: '删除工单'
    })
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    console.error('删除操作取消或失败:', error)
  }
}

// 表单提交
const submitForm = () => {
  appointmentFormRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        if (dialogType.value === 'add') {
          await serviceOrderStore.createOrder({
            userId: appointmentForm.value.elderId,
            serviceId: appointmentForm.value.serviceId,
            remark: appointmentForm.value.remark,
            appointmentTime: appointmentForm.value.appointmentTime
          })
        } else {
          await serviceOrderStore.reviewOrder(appointmentForm.value.id, {
            status: appointmentForm.value.status,
            reviewRemark: appointmentForm.value.remark
          })
        }
        ElMessage.success(dialogType.value === 'add' ? '新增成功' : '修改成功')
        dialogVisible.value = false
        getList()
      } catch (error) {
        console.error('提交表单失败:', error)
        ElMessage.error('提交表单失败')
      }
    }
  })
}

// 派单操作
const handleAssign = async (row) => {
  try {
    await ElMessageBox.confirm(`是否确认派单？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await serviceOrderStore.assignOrder(row.id)
    ElMessage.success('派单成功')
    getList()
  } catch (error) {
    console.error('派单失败:', error)
    ElMessage.error('派单失败')
  }
}

// 取消操作
const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm(`是否确认取消工单？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await serviceOrderStore.reviewOrder(row.id, {
      status: 4,
      reviewRemark: '用户取消'
    })
    ElMessage.success('取消成功')
    getList()
  } catch (error) {
    console.error('取消失败:', error)
    ElMessage.error('取消失败')
  }
}

// 获取服务选项
const getServiceOptions = async () => {
  try {
    await serviceItemStore.getServiceItemList({
      current: 1,
      size: 100,
      status: 0
    })
    // 确保服务选项包含当前工单中的服务
    const currentServices = new Map()
    appointmentList.value.forEach(item => {
      if (item.serviceItemId && item.serviceName) {
        currentServices.set(item.serviceItemId, {
          id: item.serviceItemId,
          name: item.serviceName
        })
      }
    })
    // 合并当前工单中的服务和所有可用服务
    serviceItemStore.serviceItemList.forEach(service => {
      currentServices.set(service.id, {
        id: service.id,
        name: service.name
      })
    })
    serviceOptions.value = Array.from(currentServices.values())
  } catch (error) {
    console.error('获取服务列表失败:', error)
    ElMessage.error('获取服务列表失败')
  }
}

// 获取老人选项
const getElderOptions = async () => {
  try {
    // 从工单列表中提取老人信息
    const elders = new Map()
    appointmentList.value.forEach(item => {
      if (item.userId && item.userName) {
        elders.set(item.userId, {
          id: item.userId,
          name: item.userName
        })
      }
    })
    elderOptions.value = Array.from(elders.values())
  } catch (error) {
    console.error('获取老人列表失败:', error)
    ElMessage.error('获取老人列表失败')
  }
}

// 审核操作
const handleReview = (row) => {
  console.log('审核操作，当前行数据:', row);
  console.log('当前行状态值:', row.status, typeof row.status);
  reviewForm.value = {
    id: row.id,
    status: 1,
    reviewRemark: ''
  }
  reviewDialogVisible.value = true
}

// 提交审核
const submitReview = () => {
  reviewFormRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        console.log('提交审核，表单数据:', reviewForm.value);
        await serviceOrderStore.reviewOrder(reviewForm.value.id, {
          status: reviewForm.value.status,
          reviewRemark: reviewForm.value.reviewRemark
        })
        ElMessage.success('审核成功')
        reviewDialogVisible.value = false
        getList()
      } catch (error) {
        console.error('审核失败:', error)
        ElMessage.error('审核失败')
      }
    }
  })
}

onMounted(() => {
  getList()
  getServiceOptions()
  getElderOptions()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.mb8 {
  margin-bottom: 8px;
}

.small-padding {
  padding-left: 5px;
  padding-right: 5px;
}

.fixed-width .el-button--small {
  padding: 7px 10px;
  min-width: 60px;
}
</style>