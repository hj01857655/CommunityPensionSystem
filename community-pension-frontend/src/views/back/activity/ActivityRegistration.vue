<template>
  <div class="activity-registration">
    <el-card shadow="hover" class="main-card">
      <template #header>
        <div class="card-header">
          <h3>社区活动管理</h3>
          <div class="header-actions">
            <el-input 
              v-model="searchQuery" 
              placeholder="搜索活动名称/报名人" 
              class="search-input" 
              clearable 
              @clear="handleSearch">
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button type="success" @click="handleExport">
              <el-icon><Download /></el-icon>
              导出数据
            </el-button>
          </div>
        </div>
      </template>

      <!-- 状态筛选标签 -->
      <div class="filter-tags">
        <el-radio-group v-model="statusFilter" size="large" @change="handleFilterChange">
          <el-radio-button label="all">全部</el-radio-button>
          <el-radio-button label="pending">待审核</el-radio-button>
          <el-radio-button label="approved">已通过</el-radio-button>
          <el-radio-button label="rejected">已拒绝</el-radio-button>
          <el-radio-button label="canceled">已取消</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 报名数据表格 -->
      <el-table 
        :data="filteredTableData" 
        style="width: 100%" 
        v-loading="loading"
        border
        stripe
        highlight-current-row
        @row-click="handleRowClick"
        class="registration-table">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="activityName" label="活动名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="elderName" label="报名人" width="120" />
        <el-table-column prop="registrationTime" label="报名时间" width="180">
          <template #default="scope">
            <el-tooltip :content="formatDateDetail(scope.row.registrationTime)" placement="top">
              <span>{{ formatDate(scope.row.registrationTime) }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" effect="light">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="scope">
            <el-button 
              v-if="scope.row.status === 'pending'" 
              type="primary" 
              size="small" 
              @click.stop="handleApprove(scope.row)"
              :icon="Check">
              审核
            </el-button>
            <el-button 
              v-if="scope.row.status === 'pending' || scope.row.status === 'approved'" 
              type="danger" 
              size="small" 
              @click.stop="handleCancel(scope.row)"
              :icon="Close">
              取消报名
            </el-button>
            <el-button 
              type="info" 
              size="small" 
              @click.stop="handleViewDetail(scope.row)"
              :icon="InfoFilled">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 修改分页组件 -->
      <pagination
        v-if="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- 审核对话框 -->
    <el-dialog 
      v-model="approveDialogVisible" 
      title="报名审核" 
      width="500px"
      destroy-on-close
      :close-on-click-modal="false">
      <el-form :model="approveForm" label-width="120px" status-icon>
        <el-form-item label="活动名称">
          <el-input v-model="currentRegistration.activityName" disabled />
        </el-form-item>
        <el-form-item label="报名人">
          <el-input v-model="currentRegistration.elderName" disabled />
        </el-form-item>
        <el-form-item label="报名时间">
          <el-input v-model="currentRegistration.registrationTime" disabled />
        </el-form-item>
        <el-form-item label="审核结果" required>
          <el-radio-group v-model="approveForm.status">
            <el-radio value="approved">通过</el-radio>
            <el-radio value="rejected">不通过</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input 
            v-model="approveForm.comment" 
            type="textarea" 
            :rows="3"
            placeholder="请输入审核意见"
            maxlength="200"
            show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="approveDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitApprove" :loading="submitLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 详情抽屉 -->
    <el-drawer
      v-model="detailDrawerVisible"
      title="报名详情"
      direction="rtl"
      size="30%">
      <div v-if="currentRegistration" class="registration-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="报名ID">{{ currentRegistration.id }}</el-descriptions-item>
          <el-descriptions-item label="活动名称">{{ currentRegistration.activityName }}</el-descriptions-item>
          <el-descriptions-item label="报名人">{{ currentRegistration.elderName }}</el-descriptions-item>
          <el-descriptions-item label="报名时间">{{ formatDateDetail(currentRegistration.registrationTime) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentRegistration.status)">
              {{ getStatusText(currentRegistration.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item v-if="currentRegistration.approveTime" label="审核时间">
            {{ formatDateDetail(currentRegistration.approveTime) }}
          </el-descriptions-item>
          <el-descriptions-item v-if="currentRegistration.approveBy" label="审核人">
            {{ currentRegistration.approveBy }}
          </el-descriptions-item>
          <el-descriptions-item v-if="currentRegistration.comment" label="审核意见">
            {{ currentRegistration.comment }}
          </el-descriptions-item>
        </el-descriptions>
        
        <div class="activity-info">
          <h4>活动信息</h4>
          <el-card shadow="never" class="info-card">
            <template #header>
              <div class="activity-header">
                <span>{{ currentRegistration.activityName }}</span>
                <el-tag size="small" type="success">进行中</el-tag>
              </div>
            </template>
            <div class="activity-content">
              <p><el-icon><Calendar /></el-icon> 活动时间：2024-06-15 14:00 - 16:00</p>
              <p><el-icon><Location /></el-icon> 活动地点：社区活动中心</p>
              <p><el-icon><User /></el-icon> 负责人：王老师</p>
              <p><el-icon><Phone /></el-icon> 联系电话：13800138000</p>
            </div>
          </el-card>
        </div>
        
        <div class="drawer-footer">
          <el-button 
            v-if="currentRegistration.status === 'pending'" 
            type="primary" 
            @click="handleApprove(currentRegistration)">
            审核
          </el-button>
          <el-button 
            v-if="currentRegistration.status === 'pending' || currentRegistration.status === 'approved'" 
            type="danger" 
            @click="handleCancel(currentRegistration)">
            取消报名
          </el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Check, Close, Download, InfoFilled, Calendar, Location, User, Phone } from '@element-plus/icons-vue';
import Pagination from '@/components/common/Pagination.vue';

// 报名数据
const tableData = ref([
  {
    id: 1,
    activityName: '书法活动',
    elderName: '张三',
    registrationTime: '2024-01-20 10:30:00',
    status: 'pending',
    comment: '',
    approveTime: '',
    approveBy: ''
  },
  {
    id: 2,
    activityName: '太极拳教学',
    elderName: '李四',
    registrationTime: '2024-01-19 14:20:00',
    status: 'approved',
    comment: '符合条件，通过审核',
    approveTime: '2024-01-19 16:30:00',
    approveBy: '王管理员'
  },
  {
    id: 3,
    activityName: '健康讲座',
    elderName: '王五',
    registrationTime: '2024-01-18 09:15:00',
    status: 'rejected',
    comment: '活动人数已满',
    approveTime: '2024-01-18 11:20:00',
    approveBy: '李管理员'
  },
  {
    id: 4,
    activityName: '棋牌比赛',
    elderName: '赵六',
    registrationTime: '2024-01-17 13:40:00',
    status: 'canceled',
    comment: '老人身体不适，取消报名',
    approveTime: '',
    approveBy: ''
  },
  {
    id: 5,
    activityName: '合唱团排练',
    elderName: '钱七',
    registrationTime: '2024-01-16 15:10:00',
    status: 'approved',
    comment: '通过审核',
    approveTime: '2024-01-16 16:00:00',
    approveBy: '王管理员'
  }
]);

// 状态和加载
const loading = ref(false);
const submitLoading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const searchQuery = ref('');
const statusFilter = ref('all');

// 对话框和抽屉
const approveDialogVisible = ref(false);
const detailDrawerVisible = ref(false);
const currentRegistration = ref({});

// 审核表单
const approveForm = reactive({
  status: 'approved',
  comment: ''
});

// 过滤后的数据
const filteredTableData = computed(() => {
  let result = tableData.value;
  
  // 状态筛选
  if (statusFilter.value !== 'all') {
    result = result.filter(item => item.status === statusFilter.value);
  }
  
  // 搜索筛选
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(item => 
      item.activityName.toLowerCase().includes(query) || 
      item.elderName.toLowerCase().includes(query)
    );
  }
  
  return result;
});

// 总条目数
const totalItems = computed(() => filteredTableData.value.length);

// 格式化日期 - 简短版本
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
};

// 格式化日期 - 详细版本
const formatDateDetail = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`;
};

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    'pending': 'warning',
    'approved': 'success',
    'rejected': 'danger',
    'canceled': 'info'
  };
  return typeMap[status] || 'info';
};

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    'pending': '待审核',
    'approved': '已通过',
    'rejected': '已拒绝',
    'canceled': '已取消'
  };
  return textMap[status] || '未知状态';
};

// 搜索
const handleSearch = () => {
  currentPage.value = 1;
};

// 状态筛选变化
const handleFilterChange = () => {
  currentPage.value = 1;
};

// 导出数据
const handleExport = () => {
  ElMessage.success('数据导出成功');
};

// 审核报名
const handleApprove = (row) => {
  currentRegistration.value = { ...row };
  approveForm.status = 'approved';
  approveForm.comment = '';
  approveDialogVisible.value = true;
};

// 查看详情
const handleViewDetail = (row) => {
  currentRegistration.value = { ...row };
  detailDrawerVisible.value = true;
};

// 取消报名
const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm(`确认取消 ${row.elderName} 的 "${row.activityName}" 报名记录吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    loading.value = true;
    
    // 模拟API调用
    setTimeout(() => {
      // 更新状态
      const index = tableData.value.findIndex(item => item.id === row.id);
      if (index !== -1) {
        tableData.value[index].status = 'canceled';
        tableData.value[index].comment = '用户取消报名';
        
        // 如果是当前选中的报名，更新详情
        if (currentRegistration.value && currentRegistration.value.id === row.id) {
          currentRegistration.value = { ...tableData.value[index] };
        }
      }
      
      loading.value = false;
      ElMessage.success('取消报名成功');
    }, 500);
  } catch (error) {
    console.error(error);
  }
};

// 提交审核
const submitApprove = async () => {
  submitLoading.value = true;
  
  // 模拟API调用
  setTimeout(() => {
    // 更新状态
    const index = tableData.value.findIndex(item => item.id === currentRegistration.value.id);
    if (index !== -1) {
      const now = new Date().toISOString().replace('T', ' ').substring(0, 19);
      
      tableData.value[index].status = approveForm.status;
      tableData.value[index].comment = approveForm.comment;
      tableData.value[index].approveTime = now;
      tableData.value[index].approveBy = '当前管理员';
      
      // 更新详情
      currentRegistration.value = { ...tableData.value[index] };
    }
    
    submitLoading.value = false;
    approveDialogVisible.value = false;
    ElMessage.success('审核完成');
  }, 600);
};

// 处理行点击
const handleRowClick = (row) => {
  currentRegistration.value = { ...row };
  detailDrawerVisible.value = true;
};

// 分页大小变化
const handleSizeChange = (val) => {
  pageSize.value = val;
  currentPage.value = 1;
};

// 当前页变化
const handleCurrentChange = (val) => {
  currentPage.value = val;
};

// 获取报名列表
const getRegistrations = async () => {
  loading.value = true;
  
  // 模拟API调用
  setTimeout(() => {
    // 实际项目中，这里应该调用API获取数据
    loading.value = false;
  }, 500);
};

onMounted(() => {
  getRegistrations();
});
</script>

<style scoped>
.activity-registration {
  padding: 16px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 120px);
}

.main-card {
  margin-bottom: 20px;
  transition: all 0.3s;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.main-card:hover {
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.card-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-input {
  width: 280px;
  transition: all 0.3s;
}

.search-input:focus-within {
  width: 320px;
}

.filter-tags {
  margin: 16px 0;
}

.registration-table {
  margin: 16px 0;
  border-radius: 4px;
  overflow: hidden;
}

.registration-table :deep(th) {
  background-color: #f5f7fa !important;
  color: #606266;
  font-weight: 600;
}

.registration-table :deep(.el-table__row) {
  cursor: pointer;
  transition: all 0.2s;
}

.registration-table :deep(.el-table__row:hover) {
  background-color: #ecf5ff !important;
}

.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  padding: 8px 0;
}

.registration-detail {
  padding: 16px;
}

.activity-info {
  margin-top: 24px;
}

.activity-info h4 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #303133;
}

.info-card {
  border-radius: 8px;
  margin-bottom: 16px;
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.activity-content {
  color: #606266;
}

.activity-content p {
  margin: 8px 0;
  display: flex;
  align-items: center;
}

.activity-content .el-icon {
  margin-right: 8px;
  color: #409EFF;
}

.drawer-footer {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px;
  background-color: #fff;
  text-align: right;
  border-top: 1px solid #e4e7ed;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .header-actions {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .search-input {
    width: 100%;
  }
  
  .header-actions .el-button {
    margin-top: 8px;
  }
  
  .filter-tags {
    overflow-x: auto;
    white-space: nowrap;
    padding-bottom: 8px;
  }
}
</style>