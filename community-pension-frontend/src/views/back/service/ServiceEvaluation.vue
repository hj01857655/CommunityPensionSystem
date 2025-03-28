<template>
  <div class="service-evaluation">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <el-form :model="queryParams" ref="queryForm" :inline="true">
            <el-form-item label="服务项目" prop="serviceItemId">
              <el-select v-model="queryParams.serviceItemId" placeholder="请选择服务项目" clearable>
                <el-option
                  v-for="item in serviceStore.serviceItems"
                  :key="item.serviceId"
                  :label="item.serviceName"
                  :value="item.serviceId"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="评分" prop="rating">
              <el-select v-model="queryParams.rating" placeholder="请选择评分" clearable>
                <el-option label="1星" :value="1" />
                <el-option label="2星" :value="2" />
                <el-option label="3星" :value="3" />
                <el-option label="4星" :value="4" />
                <el-option label="5星" :value="5" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
              <el-button icon="Refresh" @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>
          <div class="right-btns">
            <el-button type="warning" icon="Download" @click="handleExport">导出</el-button>
          </div>
        </div>
      </template>

      <el-table
        v-loading="serviceStore.loading"
        :data="serviceStore.serviceReviews"
      >
        <el-table-column label="评价ID" prop="id" width="100" />
        <el-table-column label="工单编号" prop="orderId" width="100" />
        <el-table-column label="用户ID" prop="userId" width="100" />
        <el-table-column label="服务项目" prop="serviceName" />
        <el-table-column label="评分" prop="rating" width="100">
          <template #default="{ row }">
            <el-rate
              v-model="row.rating"
              disabled
              show-score
              text-color="#ff9900"
            />
          </template>
        </el-table-column>
        <el-table-column label="评价内容" prop="content" show-overflow-tooltip />
        <el-table-column label="回复内容" prop="replyContent" show-overflow-tooltip />
        <el-table-column label="评价时间" prop="createTime" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="!row.replyContent"
              link
              type="primary"
              icon="ChatLineRound"
              @click="handleReply(row)"
            >回复</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-if="serviceStore.serviceReviewTotal > 0"
        :total="serviceStore.serviceReviewTotal"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- 回复评价对话框 -->
    <el-dialog
      title="回复评价"
      v-model="replyDialog.visible"
      width="500px"
      append-to-body
    >
      <el-form ref="replyFormRef" :model="replyForm" :rules="replyRules" label-width="100px">
        <el-form-item label="评价内容">
          <div class="review-content">{{ currentReview?.content }}</div>
        </el-form-item>
        <el-form-item label="回复内容" prop="replyContent">
          <el-input
            v-model="replyForm.replyContent"
            type="textarea"
            :rows="4"
            placeholder="请输入回复内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitReply">确 定</el-button>
          <el-button @click="replyDialog.visible = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { useServiceStore } from '@/stores/back/service';
import Pagination from '@/components/common/Pagination.vue';

const serviceStore = useServiceStore();

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  serviceItemId: '',
  rating: undefined
});

// 回复表单参数
const replyFormRef = ref();
const replyForm = reactive({
  id: undefined,
  replyContent: ''
});

// 回复表单校验规则
const replyRules = {
  replyContent: [
    { required: true, message: '请输入回复内容', trigger: 'blur' },
    { min: 2, max: 200, message: '长度在 2 到 200 个字符', trigger: 'blur' }
  ]
};

// 弹窗参数
const replyDialog = reactive({
  visible: false
});

// 当前评价
const currentReview = ref(null);

// 获取评价列表
const getList = async () => {
  await serviceStore.getServiceReviews(queryParams);
};

// 获取服务项目列表
const getServiceItems = async () => {
  await serviceStore.getServiceItems({
    pageSize: 100,
    status: '0'
  });
};

// 查询操作
const handleQuery = () => {
  queryParams.pageNum = 1;
  getList();
};

// 重置操作
const resetQuery = () => {
  queryParams.serviceItemId = '';
  queryParams.rating = undefined;
  handleQuery();
};

// 回复操作
const handleReply = (row) => {
  currentReview.value = row;
  replyDialog.visible = true;
  Object.assign(replyForm, {
    id: row.id,
    replyContent: ''
  });
};

// 提交回复
const submitReply = async () => {
  await replyFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await serviceStore.replyServiceReview(replyForm);
        ElMessage.success('回复成功');
        replyDialog.visible = false;
        getList();
      } catch (error) {
        ElMessage.error(error.message || '回复失败');
      }
    }
  });
};

// 导出操作
const handleExport = async () => {
  try {
    await serviceStore.exportServiceReview(queryParams);
    ElMessage.success('导出成功');
  } catch (error) {
    ElMessage.error(error.message || '导出失败');
  }
};

onMounted(() => {
  getServiceItems();
  getList();
});
</script>

<style scoped>
.service-evaluation {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
  }

  .right-btns {
    margin-left: 20px;
    white-space: nowrap;
  }

  .el-card {
    margin-bottom: 20px;
  }

  :deep(.el-card__header) {
    padding: 10px 20px;
  }

  .review-content {
    padding: 10px;
    background-color: #f5f7fa;
    border-radius: 4px;
    margin-bottom: 10px;
    line-height: 1.5;
  }
}

.dialog-footer {
  text-align: right;
  padding-top: 20px;
}
</style>