<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form v-show="showSearch" ref="queryForm" :inline="true" :model="queryParams" label-width="100px">
      <el-row :gutter="16">
        <el-col :span="8">
          <el-form-item label="用户姓名" prop="userName">
            <el-input v-model="queryParams.userName" clearable placeholder="请输入用户姓名" @input="handleQuery"/>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="服务名称" prop="serviceName">
            <el-input v-model="queryParams.serviceName" clearable placeholder="请输入服务名称" @input="handleQuery"/>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="评分" prop="score">
            <el-select v-model="queryParams.score" clearable placeholder="请选择评分" style="width: 180px" @change="handleQuery">
              <el-option label="1星" value="1"/>
              <el-option label="2星" value="2"/>
              <el-option label="3星" value="3"/>
              <el-option label="4星" value="4"/>
              <el-option label="5星" value="5"/>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16" style="margin-top: 16px;">
        <el-col :span="8">
          <el-form-item label="评价状态" prop="status">
            <el-select v-if="reviewStatusOptions && reviewStatusOptions.length" v-model="queryParams.status" clearable placeholder="请选择状态"
                       style="width: 180px" @change="handleQuery">
              <el-option v-for="dict in reviewStatusOptions" :key="dict.value" :label="dict.label" :value="dict.value"/>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="处理状态" prop="isProcessed">
            <el-select v-if="processStatusOptions && processStatusOptions.length" v-model="queryParams.isProcessed" clearable
                       placeholder="请选择处理状态" style="width: 180px" @change="handleQuery">
              <el-option v-for="dict in processStatusOptions" :key="dict.value" :label="dict.label"
                         :value="dict.value"/>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="评价时间">
            <el-date-picker v-model="queryParams.dateRange" end-placeholder="结束日期" range-separator="-"
                            start-placeholder="开始日期" style="width: 100%" type="daterange"
                            value-format="YYYY-MM-DD" @change="handleQuery"/>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16" style="margin-top: 16px;">
        <el-col :span="24" style="text-align: center;">
          <el-form-item>
            <el-button icon="Search" type="primary" @click="handleQuery">查询</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <!-- 工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            :disabled="multiple"
            icon="Delete"
            plain
            type="danger"
            @click="handleBatchDelete"
        >批量删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            icon="Download"
            plain
            type="warning"
            @click="handleExport"
        >导出
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="handleQuery"></right-toolbar>
    </el-row>

    <!-- 评价统计卡片 -->
    <el-row :gutter="20" class="mb8">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>总评价数</span>
            </div>
          </template>
          <div class="stat-value">{{ statistics.total }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>平均评分</span>
            </div>
          </template>
          <div class="stat-value">
            <el-rate v-model="statistics.avgRating" disabled show-score text-color="#ff9900"/>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>待审核评价</span>
            </div>
          </template>
          <div class="stat-value">{{ statistics.pendingReview }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>已回复评价</span>
            </div>
          </template>
          <div class="stat-value">{{ statistics.replied }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="reviewList" @selection-change="handleSelectionChange">
      <el-table-column align="center" type="selection" width="55"/>
      <el-table-column align="center" label="评价ID" prop="id" width="80"/>
      <el-table-column align="center" label="服务ID" prop="serviceId" width="80"/>
      <el-table-column align="center" label="老人姓名" min-width="100" prop="elderName"/>
      <el-table-column align="center" label="评价人" min-width="100" prop="reviewUserName"/>
      <el-table-column align="center" label="服务名称" min-width="120" prop="serviceName"/>
      <el-table-column align="center" label="评价类型" min-width="100" prop="reviewTypeName"/>
      <el-table-column align="center" label="评分" prop="rating" width="120">
        <template #default="scope">
          <el-rate v-model="scope.row.rating" disabled show-score text-color="#ff9900"/>
        </template>
      </el-table-column>
      <el-table-column :show-overflow-tooltip="true" align="center" label="评价内容" min-width="180" prop="content"/>
      <el-table-column align="center" label="评价时间" prop="reviewTime" width="160"/>
      <el-table-column align="center" label="状态" prop="status" width="100">
        <template #default="scope">
          <el-tag v-if="reviewStatusOptions && reviewStatusOptions.length" :type="getStatusTagType(scope.row.status)">
            {{ getDictLabel(reviewStatusOptions, scope.row.status) }}
          </el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="处理状态" prop="isProcessed" width="100">
        <template #default="scope">
          <el-tag v-if="processStatusOptions && processStatusOptions.length" :type="scope.row.isProcessed === '1' ? 'success' : 'info'">
            {{ getDictLabel(processStatusOptions, scope.row.isProcessed) }}
          </el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column :show-overflow-tooltip="true" align="center" label="回复内容" min-width="180" prop="adminReply"/>
      <el-table-column align="center" label="回复时间" prop="replyTime" width="160"/>
      <el-table-column align="center" label="操作" width="220">
        <template #default="scope">
          <el-button link type="primary" @click="handleDetail(scope.row)">详情</el-button>
          <el-button v-if="!scope.row.adminReply" link type="success" @click="handleReply(scope.row)">回复</el-button>
          <el-button v-if="scope.row.status === 0" link type="warning" @click="handleAudit(scope.row)">审核</el-button>
          <el-button link type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
        v-if="total > 0"
        v-model:current-page="queryParams.current"
        v-model:page-size="queryParams.size"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handleCurrentChange"
        @size-change="handleSizeChange"
    />

    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialogVisible" append-to-body title="评价详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="评价ID">{{ currentReview.id }}</el-descriptions-item>
        <el-descriptions-item label="服务ID">{{ currentReview.serviceId }}</el-descriptions-item>
        <el-descriptions-item label="老人姓名">{{ currentReview.elderName }}</el-descriptions-item>
        <el-descriptions-item label="评价人">{{ currentReview.reviewUserName }}</el-descriptions-item>
        <el-descriptions-item label="服务名称">{{ currentReview.serviceName }}</el-descriptions-item>
        <el-descriptions-item label="评价类型">{{ currentReview.reviewTypeName }}</el-descriptions-item>
        <el-descriptions-item :span="2" label="评分">
          <el-rate v-model="currentReview.rating" disabled show-score text-color="#ff9900"/>
        </el-descriptions-item>
        <el-descriptions-item :span="2" label="评价内容">{{ currentReview.content }}</el-descriptions-item>
        <el-descriptions-item label="评价时间">{{ currentReview.reviewTime }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="reviewStatusOptions && reviewStatusOptions.length" :type="getStatusTagType(currentReview.status)">
            {{ getDictLabel(reviewStatusOptions, currentReview.status) }}
          </el-tag>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="处理状态">
          <el-tag v-if="processStatusOptions && processStatusOptions.length" :type="currentReview.isProcessed === '1' ? 'success' : 'info'">
            {{ getDictLabel(processStatusOptions, currentReview.isProcessed) }}
          </el-tag>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item :span="2" label="回复内容">{{ currentReview.adminReply }}</el-descriptions-item>
        <el-descriptions-item label="回复时间">{{ currentReview.replyTime }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 回复对话框 -->
    <el-dialog v-model="replyDialogVisible" append-to-body title="回复评价" width="500px">
      <el-form ref="replyFormRef" :model="replyForm" :rules="replyRules" label-width="80px">
        <el-form-item label="回复内容" prop="adminReply">
          <el-input v-model="replyForm.adminReply" :rows="3" placeholder="请输入回复内容" type="textarea"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="replyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReply">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog v-model="auditDialogVisible" append-to-body title="审核评价" width="500px">
      <el-form ref="auditFormRef" :model="auditForm" :rules="auditRules" label-width="80px">
        <el-form-item label="审核结果" prop="status">
          <el-radio-group v-model="auditForm.status">
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="auditForm.status === 2" label="审核理由" prop="auditReason">
          <el-input v-model="auditForm.auditReason" :rows="3" placeholder="请输入审核理由" type="textarea"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="auditDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAudit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { useServiceReviewStore } from '@/stores/back/service/review';
import { formatDate } from '@/utils/date';
import { useDict } from '@/utils/dict';
import { ElMessage, ElMessageBox } from 'element-plus';
import { computed, onActivated, onMounted, reactive, ref } from 'vue';
import * as XLSX from 'xlsx';

const serviceReviewStore = useServiceReviewStore();
const reviewList = computed(() => serviceReviewStore.reviewList);
const total = computed(() => serviceReviewStore.total);
const loading = computed(() => serviceReviewStore.loading);
const showSearch = ref(true);

// 获取字典
const {service_review_status, service_process_status} = useDict('service_review_status', 'service_process_status');
const reviewStatusOptions = computed(() => Array.isArray(service_review_status.value) ? service_review_status.value : []);
const processStatusOptions = computed(() => Array.isArray(service_process_status.value) ? service_process_status.value : []);

// 标签文本工具
const getDictLabel = (dict, value) => {
  if (!Array.isArray(dict)) return '';
  const item = dict.find(i => i.value === value);
  return item ? item.label : '';
};
// 标签颜色
const getStatusTagType = (status) => {
  switch (Number(status)) {
    case 0:
      return 'warning';
    case 1:
      return 'success';
    case 2:
      return 'danger';
    default:
      return '';
  }
};

// 统计数据
const statistics = reactive({
  total: 0,
  avgRating: 0,
  pendingReview: 0,
  replied: 0
});

// 查询参数
const queryParams = reactive({
  current: 1,
  size: 10,
  userName: '',
  serviceName: '',
  score: '',
  status: '',
  isProcessed: '',
  dateRange: []
});

// 获取评价列表
const handleQuery = async () => {
  try {
    const params = {
      ...queryParams,
    };
    
    // 由于评价功能已删除，显示一个模拟数据，并避免API调用
    reviewList.value = [];
    total.value = 0;
    ElMessage.warning('评价功能已从系统中移除，此页面仅作为展示使用。');
    
    // 不再调用后端API
    // await serviceReviewStore.getReviewList(params);
    
    calculateStatistics();
  } catch (error) {
    console.error('获取评价列表失败:', error);
  }
};

// 重置查询
const resetQuery = () => {
  queryParams.userName = '';
  queryParams.serviceName = '';
  queryParams.score = '';
  queryParams.status = '';
  queryParams.isProcessed = '';
  queryParams.dateRange = [];
  handleQuery();
};

// 计算统计数据
const calculateStatistics = () => {
  const list = reviewList.value;
  statistics.total = total.value;

  // 计算平均评分
  if (list.length > 0) {
    const totalRating = list.reduce((sum, item) => sum + Number(item.rating || 0), 0);
    statistics.avgRating = parseFloat((totalRating / list.length).toFixed(1));
  } else {
    statistics.avgRating = 0;
  }

  // 计算待审核评价数
  statistics.pendingReview = list.filter(item => item.status === 0).length;

  // 计算已回复评价数
  statistics.replied = list.filter(item => item.adminReply).length;
};

const handleCurrentChange = (val) => {
  queryParams.current = val;
  handleQuery();
};

const handleSizeChange = (val) => {
  queryParams.size = val;
  handleQuery();
};

const multipleSelection = ref([]);
const multiple = ref(true);
const handleSelectionChange = (selection) => {
  multipleSelection.value = selection;
  multiple.value = selection.length === 0;
};

const detailDialogVisible = ref(false);
const currentReview = ref({});
const handleDetail = async (row) => {
  try {
    const data = await serviceReviewStore.getReviewDetail(row.id);
    currentReview.value = data;
    detailDialogVisible.value = true;
  } catch (error) {
    ElMessage.error(error.message || '获取评价详情失败');
  }
};

const replyDialogVisible = ref(false);
const replyForm = ref({
  reviewId: '',
  adminReply: ''
});
const replyRules = {
  adminReply: [
    {required: true, message: '请输入回复内容', trigger: 'blur'}
  ]
};
const replyFormRef = ref(null);
const handleReply = (row) => {
  replyForm.value = {
    reviewId: row.id,
    adminReply: ''
  };
  replyDialogVisible.value = true;
};
const submitReply = async () => {
  replyFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
        await serviceReviewStore.replyReview({
          reviewId: replyForm.value.reviewId,
          reply: replyForm.value.adminReply,
          adminId: userInfo.userId
        });
        ElMessage.success('回复成功');
        replyDialogVisible.value = false;
        handleQuery();
      } catch (error) {
        ElMessage.error(error.message || '回复失败');
      }
    }
  });
};

// 审核对话框
const auditDialogVisible = ref(false);
const auditForm = ref({
  ids: [],
  status: 1,
  auditReason: ''
});
const auditRules = {
  status: [
    {required: true, message: '请选择审核结果', trigger: 'change'}
  ],
  auditReason: [
    {required: true, message: '请输入审核理由', trigger: 'blur'}
  ]
};
const auditFormRef = ref(null);
const handleAudit = (row) => {
  auditForm.value = {
    ids: [row.id],
    status: 1,
    auditReason: ''
  };
  auditDialogVisible.value = true;
};
const submitAudit = async () => {
  auditFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await serviceReviewStore.auditReview({
          ids: auditForm.value.ids,
          status: auditForm.value.status,
          auditReason: auditForm.value.auditReason
        });
        ElMessage.success('审核成功');
        auditDialogVisible.value = false;
        handleQuery();
      } catch (error) {
        ElMessage.error(error.message || '审核失败');
      }
    }
  });
};

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该服务评价?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await serviceReviewStore.deleteReview(row.id);
      ElMessage.success('删除成功');
      handleQuery();
    } catch (error) {
      ElMessage.error(error.message || '删除失败');
    }
  }).catch(() => {
  });
};

// 批量删除
const handleBatchDelete = () => {
  if (multipleSelection.value.length === 0) {
    ElMessage.warning('请至少选择一条记录');
    return;
  }

  ElMessageBox.confirm(`确认删除选中的${multipleSelection.value.length}条评价记录?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const ids = multipleSelection.value.map(item => item.id);
      // 实际项目中应该有批量删除API，这里使用循环单个删除模拟
      for (const id of ids) {
        await serviceReviewStore.deleteReview(id);
      }
      ElMessage.success('批量删除成功');
      handleQuery();
    } catch (error) {
      ElMessage.error(error.message || '批量删除失败');
    }
  }).catch(() => {
  });
};

// 导出功能
const handleExport = () => {
  try {
    ElMessage.info('正在准备导出数据，请稍候...');

    // 准备导出数据
    const exportData = reviewList.value.map(review => ({
      '评价ID': review.id,
      '服务ID': review.serviceId,
      '老人姓名': review.elderName,
      '评价人': review.reviewUserName,
      '服务名称': review.serviceName,
      '评价类型': review.reviewTypeName,
      '评分': review.rating,
      '评价内容': review.content,
      '评价时间': review.reviewTime,
      '状态': getDictLabel(reviewStatusOptions.value, review.status),
      '处理状态': getDictLabel(processStatusOptions.value, review.isProcessed),
      '回复内容': review.adminReply || '',
      '回复时间': review.replyTime || ''
    }));

    // 创建工作簿
    const worksheet = XLSX.utils.json_to_sheet(exportData);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, '服务评价记录');

    // 设置列宽
    const columnWidths = [
      {wch: 8},   // 评价ID
      {wch: 8},   // 服务ID
      {wch: 10},  // 老人姓名
      {wch: 10},  // 评价人
      {wch: 15},  // 服务名称
      {wch: 10},  // 评价类型
      {wch: 6},   // 评分
      {wch: 30},  // 评价内容
      {wch: 18},  // 评价时间
      {wch: 8},   // 状态
      {wch: 10},  // 处理状态
      {wch: 30},  // 回复内容
      {wch: 18}   // 回复时间
    ];
    worksheet['!cols'] = columnWidths;

    // 导出Excel文件
    XLSX.writeFile(workbook, `服务评价记录_${formatDate(new Date())}.xlsx`);

    ElMessage.success('导出成功');
  } catch (error) {
    console.error('导出失败:', error);
    ElMessage.error('导出失败: ' + (error.message || '未知错误'));
  }
};

onMounted(() => {
  serviceReviewStore.resetState && serviceReviewStore.resetState();
  handleQuery();
});

onActivated && onActivated(() => {
  serviceReviewStore.resetState && serviceReviewStore.resetState();
  handleQuery();
});
</script>

<style scoped>
.el-form {
  margin-bottom: 20px;
}

.mb8 {
  margin-bottom: 16px;
}

.stat-card {
  text-align: center;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 60px;
}
</style>
