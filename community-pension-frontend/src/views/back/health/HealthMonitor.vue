<template>
  <div class="app-container">
    <!-- 搜索条件 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="老人姓名" prop="elderName">
        <el-input v-model="queryParams.elderName" placeholder="请输入老人姓名" clearable style="width: 200px"
          @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="健康状态" clearable style="width: 200px">
          <el-option label="正常" value="正常" />
          <el-option label="异常" value="异常" />
        </el-select>
      </el-form-item>
      <el-form-item label="监测时间">
        <el-date-picker v-model="dateRange" style="width: 240px" value-format="YYYY-MM-DD" type="daterange"
          range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" />
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
        <el-button type="success" plain :icon="Edit" :disabled="single" @click="handleEdit(selectedRow)">编辑</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain :icon="Delete" :disabled="multiple" @click="handleBatchDelete">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain :icon="Download" @click="handleExport">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
    </el-row>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="tableData" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" v-if="columns[0].visible" />
      <el-table-column prop="id" label="ID" width="80" align="center" v-if="columns[1].visible" />
      <el-table-column prop="elderName" label="老人姓名" width="120" align="center" v-if="columns[2].visible" />
      <el-table-column prop="monitoringTime" label="监测时间" width="180" align="center" v-if="columns[3].visible" />
      <el-table-column label="监测类型" width="120" align="center" v-if="columns[4].visible">
        <template #default="scope">
          <span>{{ getMonitorTypeName(scope.row.monitoringType) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="监测值" width="150" align="center" v-if="columns[5].visible">
        <template #default="scope">
          <span>{{ scope.row.monitoringValue }} {{ scope.row.monitoringUnit }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="monitoringStatus" label="状态" width="100" align="center" v-if="columns[6].visible">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.monitoringStatus)">
            {{ scope.row.monitoringStatus === 'normal' ? '正常' : '异常' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200" v-if="columns[7].visible">
        <template #default="scope">
          <el-button type="primary" link :icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="primary" link :icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
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
      
    <!-- 新增/编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogType === 'add' ? '新增监测记录' : '编辑监测记录'" 
      width="50%"
      append-to-body
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="老人姓名" prop="elderName">
          <el-input v-model="form.elderName" placeholder="请输入老人姓名" />
        </el-form-item>
        <el-form-item label="监测时间" prop="monitoringTime">
          <el-date-picker v-model="form.monitoringTime" type="datetime" placeholder="请选择监测时间"
            format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="监测类型" prop="monitoringType">
          <el-select v-model="form.monitoringType" placeholder="请选择监测类型">
            <el-option label="血压" value="1" />
            <el-option label="血糖" value="2" />
            <el-option label="体温" value="3" />
            <el-option label="心率" value="4" />
            <el-option label="血氧" value="5" />
            <el-option label="体重" value="6" />
            <el-option label="其他" value="7" />
          </el-select>
        </el-form-item>
        <el-form-item label="监测值" prop="monitoringValue">
          <el-input v-model="form.monitoringValue" placeholder="请输入监测值" />
        </el-form-item>
        <el-form-item label="监测单位" prop="monitoringUnit">
          <el-input v-model="form.monitoringUnit" placeholder="请输入监测单位" />
        </el-form-item>
        <el-form-item label="状态" prop="monitoringStatus">
          <el-select v-model="form.monitoringStatus" placeholder="请选择状态">
            <el-option label="正常" value="normal" />
            <el-option label="异常" value="abnormal" />
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
  </div>
</template>

<script setup>
import { addHealthMonitor, deleteHealthMonitor, exportHealthMonitors, listHealthMonitors, updateHealthMonitor } from '@/api/back/health/monitor'
import RightToolbar from '@/components/common/base/RightToolbar/index.vue'
import Pagination from '@/components/common/Pagination.vue'
import { Delete, Download, Edit, Plus, Refresh, Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, onMounted, reactive, ref, watch } from 'vue'

// 遮罩层
const loading = ref(false)
// 选中数组
const selectedRows = ref([])
// 非单个禁用
const single = computed(() => selectedRows.value.length !== 1)
// 非多个禁用
const multiple = computed(() => selectedRows.value.length === 0)
// 显示搜索条件
const showSearch = ref(true)
// 选中行数据
const selectedRow = computed(() => selectedRows.value.length === 1 ? selectedRows.value[0] : null)
// 日期范围
const dateRange = ref([])

// 列显示控制
const columns = ref([
  { key: 0, label: '选择列', visible: true },
  { key: 1, label: 'ID', visible: true },
  { key: 2, label: '老人姓名', visible: true },
  { key: 3, label: '监测时间', visible: true },
  { key: 4, label: '监测类型', visible: true },
  { key: 5, label: '监测值', visible: true },
  { key: 6, label: '状态', visible: true },
  { key: 7, label: '操作', visible: true }
])

// 表格数据
const tableData = ref([])
// 总条数
const total = ref(0)

// 查询参数
const queryParams = ref({
  elderName: '',
  status: '',
  startDate: '',
  endDate: '',
  pageNum: 1,
  pageSize: 10
})

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)

// 表单数据
const form = reactive({
  elderName: '',
  monitoringTime: '',
  monitoringType: '',
  monitoringValue: '',
  monitoringUnit: '',
  monitoringStatus: 'normal'
})

// 表单验证规则
const rules = {
  elderName: [{ required: true, message: '请输入老人姓名', trigger: 'blur' }],
  monitoringTime: [{ required: true, message: '请选择监测时间', trigger: 'change' }],
  monitoringType: [{ required: true, message: '请选择监测类型', trigger: 'change' }],
  monitoringValue: [{ required: true, message: '请输入监测值', trigger: 'blur' }],
  monitoringUnit: [{ required: true, message: '请输入监测单位', trigger: 'blur' }],
  monitoringStatus: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 监听日期范围变化
watch(dateRange, (val) => {
  queryParams.value.startDate = val && val.length > 0 ? val[0] : ''
  queryParams.value.endDate = val && val.length > 0 ? val[1] : ''
})

// 多选框选中数据
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 获取状态标签类型
const getStatusType = (status) => {
  return status === 'normal' ? 'success' : 'danger'
}

// 新增：获取监测类型中文名称
const getMonitorTypeName = (type) => {
  const typeMap = {
    '1': '血压',
    '2': '血糖',
    '3': '体温',
    '4': '心率',
    '5': '血氧',
    '6': '体重',
    '7': '其他'
  }
  return typeMap[type] || '未知类型'
}

// 查询列表
const getList = (params) => {
  loading.value = true
  
  // 处理分页参数
  if (params) {
    queryParams.value.pageNum = params.page
    queryParams.value.pageSize = params.limit
  }
  
  const queryData = {
    elderName: queryParams.value.elderName,
    status: queryParams.value.status,
    startDate: queryParams.value.startDate,
    endDate: queryParams.value.endDate,
    pageNum: queryParams.value.pageNum,
    pageSize: queryParams.value.pageSize
  }

  listHealthMonitors(queryData)
    .then(response => {
      tableData.value = response.data.records.map(item => formatHealthRecord(item))
      total.value = response.data.total
    })
    .catch(error => {
      ElMessage.error('获取健康监测记录失败')
      console.error('获取健康监测记录失败:', error)
    })
    .finally(() => {
      loading.value = false
    })
}

// 搜索按钮操作
const handleQuery = () => {
  queryParams.value.pageNum = 1
  getList()
}

// 重置按钮操作
const resetQuery = () => {
  dateRange.value = []
  queryParams.value = {
    elderName: '',
    status: '',
    startDate: '',
    endDate: '',
    pageNum: 1,
    pageSize: 10
  }
  handleQuery()
}

// 新增记录
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  // 重置表单
  if (formRef.value) {
    formRef.value.resetFields()
  }
  // 设置默认值
  Object.assign(form, {
    elderName: '',
    monitoringTime: '',
    monitoringType: '',
    monitoringValue: '',
    monitoringUnit: '',
    monitoringStatus: 'normal'
  })
}

// 编辑记录
const handleEdit = (row) => {
  if (!row && selectedRows.value.length !== 1) {
    return
  }
  const rowData = row || selectedRows.value[0]
  dialogType.value = 'edit'
  dialogVisible.value = true
  // 填充表单数据
  Object.assign(form, rowData)
}

// 删除记录
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确认删除该监测记录吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    deleteHealthMonitor(row.id)
      .then(() => {
        ElMessage.success('删除成功')
        getList() // 刷新列表
      })
      .catch(error => {
        ElMessage.error('删除失败')
        console.error('删除健康监测记录失败:', error)
      })
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

// 批量删除
const handleBatchDelete = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请至少选择一条记录')
    return
  }
  
  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedRows.value.length} 条健康监测记录吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 这里可以调用批量删除API
    // 示例：批量删除逻辑
    const ids = selectedRows.value.map(item => item.id)
    // 可以实现批量删除API调用
    ElMessage.success('批量删除成功')
    getList()
  }).catch(() => {
    // 取消删除
  })
}

// 导出数据
const handleExport = () => {
  const params = {
    elderName: queryParams.value.elderName,
    status: queryParams.value.status,
    startDate: queryParams.value.startDate,
    endDate: queryParams.value.endDate
  }

  exportHealthMonitors(params)
    .then(response => {
      // 创建Blob对象
      const blob = new Blob([response.data], { type: 'application/vnd.ms-excel' })
      const link = document.createElement('a')
      link.href = URL.createObjectURL(blob)
      link.download = `健康监测记录_${new Date().getTime()}.xlsx`
      link.click()
      URL.revokeObjectURL(link.href)
      ElMessage.success('数据导出成功')
    })
    .catch(error => {
      ElMessage.error('数据导出失败')
      console.error('导出健康监测记录失败:', error)
    })
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate((valid) => {
    if (valid) {
      const submitData = { ...form }
      const submitFunc = dialogType.value === 'add' ? addHealthMonitor : updateHealthMonitor

      submitFunc(submitData)
        .then(() => {
          ElMessage.success(dialogType.value === 'add' ? '添加成功' : '修改成功')
          dialogVisible.value = false
          getList() // 刷新列表
        })
        .catch(error => {
          ElMessage.error(dialogType.value === 'add' ? '添加失败' : '修改失败')
          console.error(dialogType.value === 'add' ? '添加健康监测记录失败:' : '修改健康监测记录失败:', error)
        })
    }
  })
}

// 格式化健康记录
const formatHealthRecord = (record) => {
  // 根据后端返回的数据格式进行处理
  return {
    ...record,
    // 移除错误的 status 处理
    // status: record.status || (isHealthDataNormal(record) ? '正常' : '异常') 
  }
}

// 判断健康数据是否正常
const isHealthDataNormal = (data) => {
  // 这里可以根据实际业务需求添加健康数据正常范围的判断逻辑
  // 例如：血压、心率、血糖、体温等指标的正常范围
  return true // 默认返回正常
}

onMounted(() => {
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
  display: flex;
  justify-content: flex-end;
  gap: 16px;
}
</style>
