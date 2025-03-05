<template>
  <div class="service-appointment">
    <el-card shadow="hover" class="table-card">
      <template #header>
        <div class="card-header">
          <h3>服务预约管理</h3>
          <div class="header-actions">
            <el-input v-model="searchQuery" placeholder="搜索老人姓名/服务名称" class="search-input" clearable
              @clear="handleSearch">
              <template #prefix>
                <el-icon>
                  <Search />
                </el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button type="success" @click="handleAdd">新增预约</el-button>
          </div>
        </div>
      </template>

      <el-table :data="filteredAppointments" style="width: 100%" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="elderName" label="老人姓名" width="120" />
        <el-table-column prop="serviceName" label="服务项目" width="150" />
        <el-table-column prop="appointmentTime" label="预约时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.appointmentTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="staffName" label="服务人员" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="200" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="success" size="small" @click="handleComplete(scope.row)"
              :disabled="scope.row.status === '已完成'">完成</el-button>
            <el-button type="danger" size="small" @click="handleCancel(scope.row)"
              :disabled="scope.row.status === '已取消' || scope.row.status === '已完成'">取消</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper" :total="totalAppointments" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <!-- 预约表单对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '新增预约' : '编辑预约'" width="600px">
      <el-form ref="appointmentFormRef" :model="appointmentForm" :rules="appointmentRules" label-width="100px">
        <el-form-item label="老人" prop="elderId">
          <el-select v-model="appointmentForm.elderId" placeholder="请选择老人">
            <el-option v-for="elder in elderOptions" :key="elder.id" :label="elder.name" :value="elder.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="服务项目" prop="serviceId">
          <el-select v-model="appointmentForm.serviceId" placeholder="请选择服务项目">
            <el-option v-for="service in serviceOptions" :key="service.id" :label="service.name" :value="service.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="预约时间" prop="appointmentTime">
          <el-date-picker v-model="appointmentForm.appointmentTime" type="datetime" placeholder="选择预约时间"
            format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="服务人员" prop="staffId">
          <el-select v-model="appointmentForm.staffId" placeholder="请选择服务人员">
            <el-option v-for="staff in staffOptions" :key="staff.id" :label="staff.name" :value="staff.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="appointmentForm.remark" type="textarea" :rows="3" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search } from '@element-plus/icons-vue';
import { formatDate } from '@/utils/date';

// 预约列表数据
const appointments = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const searchQuery = ref('')
const dialogVisible = ref(false)
const dialogType = ref('add')

// 表单相关
const appointmentFormRef = ref(null)
const appointmentForm = ref({
  elderId: '',
  serviceId: '',
  appointmentTime: '',
  staffId: '',
  remark: ''
})

// 选项数据
const elderOptions = ref([])
const serviceOptions = ref([])
const staffOptions = ref([])

// 表单验证规则
const appointmentRules = {
  elderId: [{ required: true, message: '请选择老人', trigger: 'change' }],
  serviceId: [{ required: true, message: '请选择服务项目', trigger: 'change' }],
  appointmentTime: [{ required: true, message: '请选择预约时间', trigger: 'change' }],
  staffId: [{ required: true, message: '请选择服务人员', trigger: 'change' }]
}

// 过滤后的预约列表
const filteredAppointments = computed(() => {
  if (!appointments.value || !searchQuery.value) {
    return appointments.value || []
  }

  const query = searchQuery.value.toLowerCase()
  return appointments.value.filter(appointment =>
    (appointment.elderName && appointment.elderName.toLowerCase().includes(query)) ||
    (appointment.serviceName && appointment.serviceName.toLowerCase().includes(query))
  )
})

// 总预约数
const totalAppointments = computed(() => {
  return filteredAppointments.value ? filteredAppointments.value.length : 0
})

// 获取状态标签类型
const getStatusType = (status) => {
  const typeMap = {
    '待服务': 'warning',
    '服务中': 'primary',
    '已完成': 'success',
    '已取消': 'info'
  }
  return typeMap[status] || ''
}

// 搜索预约
const handleSearch = () => {
  currentPage.value = 1
}

// 新增预约
const handleAdd = () => {
  dialogType.value = 'add'
  appointmentForm.value = {
    elderId: '',
    serviceId: '',
    appointmentTime: '',
    staffId: '',
    remark: ''
  }
  dialogVisible.value = true
}

// 编辑预约
const handleEdit = (row) => {
  dialogType.value = 'edit'
  appointmentForm.value = { ...row }
  dialogVisible.value = true
}

// 完成预约
const handleComplete = (row) => {
  ElMessageBox.confirm(
    `确定要将该预约标记为已完成吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(() => {
    // 这里应该调用更新预约状态的API
    ElMessage.success('操作成功')
    row.status = '已完成'
  })
}

// 取消预约
const handleCancel = (row) => {
  ElMessageBox.confirm(
    `确定要取消该预约吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 这里应该调用取消预约的API
    ElMessage.success('预约已取消')
    row.status = '已取消'
  })
}

// 提交表单
const submitForm = () => {
  appointmentFormRef.value.validate((valid) => {
    if (valid) {
      // 这里应该调用添加/更新预约的API
      ElMessage.success(dialogType.value === 'add' ? '预约添加成功' : '预约更新成功')
      dialogVisible.value = false
    }
  })
}

// 分页大小变化
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

// 当前页变化
const handleCurrentChange = (val) => {
  currentPage.value = val
}
</script>

<style scoped>
.service-appointment {
  padding: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-input {
  width: 250px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>