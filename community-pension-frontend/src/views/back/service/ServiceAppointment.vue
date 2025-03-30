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
      <el-form-item label="预约状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择预约状态"
          clearable
          style="width: 200px"
        >
          <el-option label="待确认" value="0" />
          <el-option label="已确认" value="1" />
          <el-option label="已完成" value="2" />
          <el-option label="已取消" value="3" />
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
      <el-table-column prop="id" label="预约编号" width="80" align="center" />
      <el-table-column prop="serviceName" label="服务名称" min-width="150" align="center" />
      <el-table-column prop="elderName" label="预约老人" width="120" align="center" />
      <el-table-column prop="appointmentTime" label="预约时间" width="180" align="center">
        <template #default="scope">
          {{ formatDate(scope.row.appointmentTime) }}
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
      <el-table-column label="操作" width="150" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button type="primary" link :icon="Edit" @click="handleEdit(scope.row)">修改</el-button>
          <el-button type="primary" link :icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
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
import { Search, Delete, Edit, Plus, Refresh } from '@element-plus/icons-vue'
import { formatDate } from '@/utils/date'
import Pagination from '@/components/common/Pagination.vue'
import RightToolbar from '@/components/RightToolbar/index.vue'

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

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    0: 'info',
    1: 'success',
    2: 'warning',
    3: 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    0: '待确认',
    1: '已确认',
    2: '已完成',
    3: '已取消'
  }
  return textMap[status] || '未知'
}

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    // 这里应该调用获取预约列表的API
    // const res = await getAppointmentList(queryParams)
    // appointmentList.value = res.data.records
    // total.value = res.data.total
  } finally {
    loading.value = false
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
  appointmentForm.value = { ...row }
  dialogVisible.value = true
}

// 删除按钮操作
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`是否确认删除预约编号为"${row.id}"的预约记录？`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    // 这里应该调用删除预约的API
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
        // 这里应该调用新增或修改预约的API
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

// 获取服务选项
const getServiceOptions = async () => {
  try {
    // 这里应该调用获取服务列表的API
    // const res = await getServiceList()
    // serviceOptions.value = res.data
  } catch (error) {
    console.error('获取服务列表失败:', error)
    ElMessage.error('获取服务列表失败')
  }
}

// 获取老人选项
const getElderOptions = async () => {
  try {
    // 这里应该调用获取老人列表的API
    // const res = await getElderList()
    // elderOptions.value = res.data
  } catch (error) {
    console.error('获取老人列表失败:', error)
    ElMessage.error('获取老人列表失败')
  }
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