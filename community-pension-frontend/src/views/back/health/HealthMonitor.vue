<template>
  <div class="health-monitor">
    <el-card class="box-card">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
          <el-form-item label="老人姓名">
            <el-input v-model="searchForm.elderName" placeholder="请输入老人姓名" clearable />
          </el-form-item>
          <el-form-item label="监测时间">
            <el-date-picker v-model="searchForm.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期"
              end-placeholder="结束日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 操作按钮 -->
      <div class="operation-bar">
        <el-button type="primary" @click="handleAdd">新增监测记录</el-button>
        <el-button type="success" @click="handleExport">导出数据</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="elderName" label="老人姓名" width="120" />
        <el-table-column prop="monitorTime" label="监测时间" width="180" />
        <el-table-column prop="bloodPressure" label="血压" width="120" />
        <el-table-column prop="heartRate" label="心率" width="100" />
        <el-table-column prop="bloodSugar" label="血糖" width="100" />
        <el-table-column prop="temperature" label="体温" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === '正常' ? 'success' : 'danger'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>

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
          <el-form-item label="监测时间" prop="monitorTime">
            <el-date-picker v-model="form.monitorTime" type="datetime" placeholder="请选择监测时间"
              format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" />
          </el-form-item>
          <el-form-item label="血压" prop="bloodPressure">
            <el-input v-model="form.bloodPressure" placeholder="请输入血压值" />
          </el-form-item>
          <el-form-item label="心率" prop="heartRate">
            <el-input v-model="form.heartRate" placeholder="请输入心率值" />
          </el-form-item>
          <el-form-item label="血糖" prop="bloodSugar">
            <el-input v-model="form.bloodSugar" placeholder="请输入血糖值" />
          </el-form-item>
          <el-form-item label="体温" prop="temperature">
            <el-input v-model="form.temperature" placeholder="请输入体温值" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="form.status" placeholder="请选择状态">
              <el-option label="正常" value="正常" />
              <el-option label="异常" value="异常" />
            </el-select>
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input v-model="form.remark" type="textarea" placeholder="请输入备注信息" />
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleSubmit">确定</el-button>
          </span>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listHealthMonitors, getHealthMonitor, addHealthMonitor, updateHealthMonitor, deleteHealthMonitor, exportHealthMonitors } from '@/api/back/health/monitor'

// 搜索表单数据
const searchForm = reactive({
  elderName: '',
  dateRange: []
})

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 分页数据
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 对话框数据
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)

// 表单数据
const form = reactive({
  elderName: '',
  monitorTime: '',
  bloodPressure: '',
  heartRate: '',
  bloodSugar: '',
  temperature: '',
  status: '正常',
  remark: ''
})

// 表单验证规则
const rules = {
  elderName: [{ required: true, message: '请输入老人姓名', trigger: 'blur' }],
  monitorTime: [{ required: true, message: '请选择监测时间', trigger: 'change' }],
  bloodPressure: [{ required: true, message: '请输入血压值', trigger: 'blur' }],
  heartRate: [{ required: true, message: '请输入心率值', trigger: 'blur' }],
  bloodSugar: [{ required: true, message: '请输入血糖值', trigger: 'blur' }],
  temperature: [{ required: true, message: '请输入体温值', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 搜索方法
const handleSearch = () => {
  loading.value = true;
  const params = {
    elderName: searchForm.elderName,
    startDate: searchForm.dateRange && searchForm.dateRange[0],
    endDate: searchForm.dateRange && searchForm.dateRange[1],
    pageNum: currentPage.value,
    pageSize: pageSize.value
  };

  listHealthMonitors(params)
    .then(response => {
      tableData.value = response.data.records.map(item => formatHealthRecord(item));
      total.value = response.data.total;
    })
    .catch(error => {
      ElMessage.error('获取健康监测记录失败');
      console.error('获取健康监测记录失败:', error);
    })
    .finally(() => {
      loading.value = false;
    });
}

// 重置搜索
const resetSearch = () => {
  searchForm.elderName = ''
  searchForm.dateRange = []
  handleSearch()
}

// 新增记录
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  // 重置表单
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 编辑记录
const handleEdit = (row) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  // 填充表单数据
  Object.assign(form, row)
}

// 删除记录
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该监测记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      deleteHealthMonitor(row.id)
        .then(() => {
          ElMessage.success('删除成功');
          handleSearch(); // 刷新列表
        })
        .catch(error => {
          ElMessage.error('删除失败');
          console.error('删除健康监测记录失败:', error);
        });
    })
    .catch(() => {
      ElMessage.info('已取消删除');
    });
}

// 导出数据
const handleExport = () => {
  const params = {
    elderName: searchForm.elderName,
    startDate: searchForm.dateRange && searchForm.dateRange[0],
    endDate: searchForm.dateRange && searchForm.dateRange[1]
  };

  exportHealthMonitors(params)
    .then(response => {
      // 创建Blob对象
      const blob = new Blob([response.data], { type: 'application/vnd.ms-excel' });
      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.download = `健康监测记录_${new Date().getTime()}.xlsx`;
      link.click();
      URL.revokeObjectURL(link.href);
      ElMessage.success('数据导出成功');
    })
    .catch(error => {
      ElMessage.error('数据导出失败');
      console.error('导出健康监测记录失败')
    });
  }
  // 提交表单
  const handleSubmit = async () => {
    if (!formRef.value) return

    await formRef.value.validate((valid) => {
      if (valid) {
        const submitData = { ...form };
        const submitFunc = dialogType.value === 'add' ? addHealthMonitor : updateHealthMonitor;

        submitFunc(submitData)
          .then(() => {
            ElMessage.success(dialogType.value === 'add' ? '添加成功' : '修改成功');
            dialogVisible.value = false;
            handleSearch(); // 刷新列表
          })
          .catch(error => {
            ElMessage.error(dialogType.value === 'add' ? '添加失败' : '修改失败');
            console.error(dialogType.value === 'add' ? '添加健康监测记录失败:' : '修改健康监测记录失败:', error);
          });
      }
    });
  }

  // 格式化健康记录
  const formatHealthRecord = (record) => {
    // 根据后端返回的数据格式进行处理
    return {
      ...record,
      // 可以在这里添加额外的格式化逻辑
      status: record.status || (isHealthDataNormal(record) ? '正常' : '异常')
    };
  };

  // 判断健康数据是否正常
  const isHealthDataNormal = (data) => {
    // 这里可以根据实际业务需求添加健康数据正常范围的判断逻辑
    // 例如：血压、心率、血糖、体温等指标的正常范围
    return true; // 默认返回正常
  };

  // 分页大小改变
  const handleSizeChange = (val) => {
    pageSize.value = val;
    handleSearch();
  };

  // 当前页改变
  const handleCurrentChange = (val) => {
    currentPage.value = val
    handleSearch()
  }

</script>

<style scoped>
.health-monitor {
  padding: 24px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 84px);
  animation: fadeIn 0.5s ease-out;
}

.box-card {
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: linear-gradient(to right bottom, #ffffff, #fafafa);
  overflow: hidden;
}

.box-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.search-bar {
  padding: 24px 32px;
  background: linear-gradient(135deg, #f8f9fa, #ffffff);
  border-radius: 12px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

:deep(.el-form-item__label) {
  font-weight: 600;
  color: #1a1a1a;
  font-size: 14px;
  margin-bottom: 8px;
}

:deep(.el-input__wrapper),
:deep(.el-date-editor__wrapper) {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover),
:deep(.el-date-editor__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.1);
}

:deep(.el-input__wrapper.is-focus),
:deep(.el-date-editor__wrapper.is-focus) {
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.operation-bar {
  margin: 24px 32px;
  display: flex;
  gap: 16px;
}

:deep(.el-button) {
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.el-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

:deep(.el-button--primary) {
  background: linear-gradient(45deg, #409EFF, #66b1ff);
  border: none;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(45deg, #66b1ff, #409EFF);
}

:deep(.el-button--success) {
  background: linear-gradient(45deg, #67c23a, #85ce61);
  border: none;
}

:deep(.el-button--success:hover) {
  background: linear-gradient(45deg, #85ce61, #67c23a);
}

:deep(.el-table) {
  margin: 0 32px 24px;
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

:deep(.el-table__row) {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.el-table__row:hover) {
  background-color: #f5f7fa !important;
  transform: translateY(-1px);
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

:deep(.el-tag--success) {
  background: linear-gradient(45deg, #e6f7e6, #f0f9f0);
  color: #67c23a;
}

:deep(.el-tag--danger) {
  background: linear-gradient(45deg, #ffe6e6, #fff0f0);
  color: #f56c6c;
}

.pagination-container {
  margin: 24px 32px;
  padding: 16px;
  display: flex;
  justify-content: flex-end;
  background: linear-gradient(to right, #f8f9fa, #ffffff);
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

:deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

:deep(.el-dialog__header) {
  margin: 0;
  padding: 24px 32px;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

:deep(.el-dialog__title) {
  font-size: 20px;
  font-weight: 600;
  color: #1a1a1a;
  background: linear-gradient(45deg, #1a1a1a, #4a4a4a);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

:deep(.el-dialog__body) {
  padding: 32px;
}

:deep(.el-dialog__footer) {
  padding: 24px 32px;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
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

/* 响应式布局 */
@media screen and (max-width: 768px) {
  .health-monitor {
    padding: 16px;
  }
  
  .search-bar {
    padding: 16px 20px;
  }
  
  .operation-bar {
    margin: 16px 20px;
    flex-direction: column;
  }
  
  .operation-bar .el-button {
    width: 100%;
  }
  
  :deep(.el-table) {
    margin: 0 20px 16px;
  }
  
  .pagination-container {
    margin: 16px 20px;
    padding: 12px;
  }
  
  :deep(.el-dialog__body) {
    padding: 20px;
  }
  
  :deep(.el-dialog__header),
  :deep(.el-dialog__footer) {
    padding: 16px 20px;
  }
}

/* 滚动条美化 */
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