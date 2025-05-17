<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="服务名称" prop="serviceName">
        <el-input
          v-model="queryParams.serviceName"
          placeholder="请输入服务名称"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户名" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入用户名"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="评分" prop="score">
        <el-select v-model="queryParams.score" placeholder="评分" clearable style="width: 240px">
          <el-option label="1星" value="1" />
          <el-option label="2星" value="2" />
          <el-option label="3星" value="3" />
          <el-option label="4星" value="4" />
          <el-option label="5星" value="5" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间">
        <el-date-picker
          v-model="dateRange"
          style="width: 240px"
          value-format="YYYY-MM-DD"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="reviewList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="评价ID" align="center" prop="reviewId" width="80" />
      <el-table-column label="订单号" align="center" prop="orderNo" width="180" />
      <el-table-column label="用户名" align="center" prop="userName" :show-overflow-tooltip="true" />
      <el-table-column label="服务名称" align="center" prop="serviceName" :show-overflow-tooltip="true" />
      <el-table-column label="评分" align="center" prop="score">
        <template #default="scope">
          <el-rate
            v-model="scope.row.score"
            disabled
            show-score
            text-color="#ff9900"
          />
        </template>
      </el-table-column>
      <el-table-column label="评价内容" align="center" prop="content" :show-overflow-tooltip="true" />
      <el-table-column label="评价时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ formatDate(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button
            link
            icon="View"
            @click="handleView(scope.row)"
          >查看</el-button>
          <el-button
            link
            icon="Delete"
            @click="handleDelete(scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.current"
      v-model:limit="queryParams.size"
      @pagination="getList"
    />

    <!-- 查看评价详情对话框 -->
    <el-dialog title="评价详情" v-model="open" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="评价ID">{{ form.reviewId }}</el-descriptions-item>
        <el-descriptions-item label="订单号">{{ form.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ form.userName }}</el-descriptions-item>
        <el-descriptions-item label="服务名称">{{ form.serviceName }}</el-descriptions-item>
        <el-descriptions-item label="评分" :span="2">
          <el-rate
            v-model="form.score"
            disabled
            show-score
            text-color="#ff9900"
          />
        </el-descriptions-item>
        <el-descriptions-item label="评价内容" :span="2">{{ form.content }}</el-descriptions-item>
        <el-descriptions-item label="评价图片" :span="2" v-if="form.images && form.images.length > 0">
          <el-image 
            v-for="(url, index) in form.images" 
            :key="index"
            style="width: 100px; height: 100px; margin-right: 10px;"
            :src="url" 
            :preview-src-list="form.images"
          />
        </el-descriptions-item>
        <el-descriptions-item label="评价时间">{{ formatDate(form.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ formatDate(form.updateTime) }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="open = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { useServiceReviewStore } from '@/stores/back/service';
import { formatDate } from '@/utils/date';
import { ElMessage, ElMessageBox } from 'element-plus';
import { onMounted, ref } from 'vue';

const serviceReviewStore = useServiceReviewStore();

const reviewList = ref([]);
const open = ref(false);
const loading = ref(false);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const dateRange = ref([]);

// 查询参数
const queryParams = ref({
  current: 1,
  size: 10,
  serviceName: undefined,
  userName: undefined,
  score: undefined,
  beginTime: undefined,
  endTime: undefined
});

// 表单参数
const form = ref({
  reviewId: undefined,
  orderId: undefined,
  orderNo: undefined,
  userId: undefined,
  userName: undefined,
  serviceId: undefined,
  serviceName: undefined,
  score: 5,
  content: undefined,
  images: [],
  createTime: undefined,
  updateTime: undefined
});

/** 查询评价列表 */
async function getList() {
  loading.value = true;
  try {
    // 处理时间范围
    if (dateRange.value && dateRange.value.length > 0) {
      queryParams.value.beginTime = dateRange.value[0];
      queryParams.value.endTime = dateRange.value[1];
    } else {
      queryParams.value.beginTime = undefined;
      queryParams.value.endTime = undefined;
    }

    const response = await serviceReviewStore.getReviewList(queryParams.value);
    if (response && response.records) {
      reviewList.value = response.records;
      total.value = response.total;
    }
  } catch (error) {
    console.error("获取评价列表失败:", error);
  } finally {
    loading.value = false;
  }
}

/** 表单重置 */
function reset() {
  form.value = {
    reviewId: undefined,
    orderId: undefined,
    orderNo: undefined,
    userId: undefined,
    userName: undefined,
    serviceId: undefined,
    serviceName: undefined,
    score: 5,
    content: undefined,
    images: [],
    createTime: undefined,
    updateTime: undefined
  };
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.current = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  queryParams.value = {
    current: 1,
    size: 10,
    serviceName: undefined,
    userName: undefined,
    score: undefined,
    beginTime: undefined,
    endTime: undefined
  };
  handleQuery();
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.reviewId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

/** 查看按钮操作 */
async function handleView(row) {
  reset();
  const reviewId = row.reviewId || ids.value[0];
  try {
    const response = await serviceReviewStore.getReviewDetail(reviewId);
    if (response) {
      form.value = response;
      open.value = true;
    }
  } catch (error) {
    console.error("获取评价详情失败:", error);
    ElMessage.error("获取评价详情失败");
  }
}

/** 删除按钮操作 */
function handleDelete(row) {
  const reviewIds = row.reviewId || ids.value;
  ElMessageBox.confirm('是否确认删除评价编号为"' + reviewIds + '"的数据项?', "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(async function() {
    try {
      await serviceReviewStore.deleteReview(reviewIds);
      getList();
      ElMessage.success("删除成功");
    } catch (error) {
      console.error("删除评价失败:", error);
      ElMessage.error("删除失败");
    }
  });
}

/** 导出按钮操作 */
function handleExport() {
  // 处理时间范围
  if (dateRange.value && dateRange.value.length > 0) {
    queryParams.value.beginTime = dateRange.value[0];
    queryParams.value.endTime = dateRange.value[1];
  } else {
    queryParams.value.beginTime = undefined;
    queryParams.value.endTime = undefined;
  }
  
  serviceReviewStore.exportList(queryParams.value);
}

onMounted(() => {
  getList();
});
</script>

<style scoped>
.el-tag + .el-tag {
  margin-left: 10px;
}
</style>
