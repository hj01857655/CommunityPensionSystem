<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="100px">
      <el-row :gutter="16">
        <el-col :span="8">
          <el-form-item label="用户姓名" prop="userName">
            <el-input v-model="queryParams.userName" placeholder="请输入用户姓名" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="服务名称" prop="serviceName">
            <el-input v-model="queryParams.serviceName" placeholder="请输入服务名称" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="评分" prop="score">
            <el-input v-model="queryParams.score" placeholder="请输入评分" clearable />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16" style="margin-top: 8px;">
        <el-col :span="8">
          <el-form-item label="评价时间">
            <el-date-picker v-model="queryParams.dateRange" type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item>
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="reviewList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="评价ID" prop="id" width="80" align="center" />
      <el-table-column label="服务ID" prop="serviceId" width="80" align="center" />
      <el-table-column label="老人姓名" prop="elderName" min-width="100" align="center" />
      <el-table-column label="评价人" prop="reviewUserName" min-width="100" align="center" />
      <el-table-column label="服务名称" prop="serviceName" min-width="120" align="center" />
      <el-table-column label="评价类型" prop="reviewTypeName" min-width="100" align="center" />
      <el-table-column label="评分" prop="rating" width="80" align="center" />
      <el-table-column label="评价内容" prop="content" min-width="180" align="center" show-overflow-tooltip />
      <el-table-column label="评价时间" prop="reviewTime" width="160" align="center" />
      <el-table-column label="回复内容" prop="adminReply" min-width="180" align="center" show-overflow-tooltip />
      <el-table-column label="回复时间" prop="replyTime" width="160" align="center" />
      <el-table-column label="操作" width="180" align="center">
        <template #default="scope">
          <el-button type="primary" link @click="handleDetail(scope.row)">详情</el-button>
          <el-button type="success" link v-if="!scope.row.adminReply" @click="handleReply(scope.row)">回复</el-button>
          <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-if="total > 0"
      :total="total"
      v-model:current-page="queryParams.current"
      v-model:page-size="queryParams.size"
      @current-change="handleCurrentChange"
      @size-change="handleSizeChange"
      layout="total, sizes, prev, pager, next, jumper"
    />

    <!-- 详情对话框 -->
    <el-dialog title="评价详情" v-model="detailDialogVisible" width="600px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="评价ID">{{ currentReview.id }}</el-descriptions-item>
        <el-descriptions-item label="服务ID">{{ currentReview.serviceId }}</el-descriptions-item>
        <el-descriptions-item label="老人姓名">{{ currentReview.elderName }}</el-descriptions-item>
        <el-descriptions-item label="评价人">{{ currentReview.reviewUserName }}</el-descriptions-item>
        <el-descriptions-item label="服务名称">{{ currentReview.serviceName }}</el-descriptions-item>
        <el-descriptions-item label="评价类型">{{ currentReview.reviewTypeName }}</el-descriptions-item>
        <el-descriptions-item label="评分">{{ currentReview.rating }}</el-descriptions-item>
        <el-descriptions-item label="评价内容" :span="2">{{ currentReview.content }}</el-descriptions-item>
        <el-descriptions-item label="评价时间">{{ currentReview.reviewTime }}</el-descriptions-item>
        <el-descriptions-item label="回复内容" :span="2">{{ currentReview.adminReply }}</el-descriptions-item>
        <el-descriptions-item label="回复时间">{{ currentReview.replyTime }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 回复对话框 -->
    <el-dialog title="回复评价" v-model="replyDialogVisible" width="500px" append-to-body>
      <el-form ref="replyFormRef" :model="replyForm" :rules="replyRules" label-width="80px">
        <el-form-item label="回复内容" prop="adminReply">
          <el-input v-model="replyForm.adminReply" type="textarea" :rows="3" placeholder="请输入回复内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="replyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReply">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { useServiceReviewStore } from '@/stores/back/service/review';
import { ElMessage, ElMessageBox } from 'element-plus';
import { onActivated, onMounted, reactive, ref } from 'vue';

const serviceReviewStore = useServiceReviewStore();
const reviewList = serviceReviewStore.reviewList;
const total = serviceReviewStore.total;
const loading = serviceReviewStore.loading;

const queryParams = reactive({
  current: 1,
  size: 10,
  userName: '',
  serviceName: '',
  score: '',
  dateRange: []
});

const handleQuery = async () => {
  const params = {
    ...queryParams,
    beginTime: queryParams.dateRange?.[0] || '',
    endTime: queryParams.dateRange?.[1] || ''
  };
  await serviceReviewStore.getReviewList(params);
};

const resetQuery = () => {
  queryParams.userName = '';
  queryParams.serviceName = '';
  queryParams.score = '';
  queryParams.dateRange = [];
  handleQuery();
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
const handleSelectionChange = (selection) => {
  multipleSelection.value = selection;
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
    { required: true, message: '请输入回复内容', trigger: 'blur' }
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
  }).catch(() => {});
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
</style>
