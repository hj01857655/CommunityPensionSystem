<template>
  <div class="service-appointment">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <el-form :model="queryParams" ref="queryForm" :inline="true">
            <el-form-item label="用户ID" prop="userId">
              <el-input
                v-model="queryParams.userId"
                placeholder="请输入用户ID"
                clearable
                @keyup.enter="handleQuery"
              />
            </el-form-item>
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
            <el-form-item label="状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
                <el-option label="待审核" value="0" />
                <el-option label="已通过" value="1" />
                <el-option label="已拒绝" value="2" />
                <el-option label="待派单" value="3" />
                <el-option label="已派单" value="4" />
                <el-option label="服务中" value="5" />
                <el-option label="已完成" value="6" />
                <el-option label="已取消" value="7" />
              </el-select>
            </el-form-item>
            <el-form-item label="预约时间" prop="appointmentTime">
              <el-date-picker
                v-model="queryParams.appointmentTime"
                type="datetime"
                placeholder="请选择预约时间"
                value-format="YYYY-MM-DD HH:mm:ss"
                clearable
              />
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
        :data="serviceStore.serviceOrders"
      >
        <el-table-column label="工单编号" prop="orderId" width="100" />
        <el-table-column label="用户ID" prop="userId" width="100" />
        <el-table-column label="服务项目" prop="serviceName" />
        <el-table-column label="申请原因" prop="reason" show-overflow-tooltip />
        <el-table-column label="预约时间" prop="appointmentTime" width="180" />
        <el-table-column label="状态" prop="status" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === '0'"
              link
              type="primary"
              icon="Check"
              @click="handleReview(row, true)"
            >通过</el-button>
            <el-button
              v-if="row.status === '0'"
              link
              type="danger"
              icon="Close"
              @click="handleReview(row, false)"
            >拒绝</el-button>
            <el-button
              v-if="row.status === '3'"
              link
              type="primary"
              icon="User"
              @click="handleAssign(row)"
            >派单</el-button>
            <el-button
              v-if="row.status === '4'"
              link
              type="success"
              icon="VideoPlay"
              @click="handleStart(row)"
            >开始</el-button>
            <el-button
              v-if="row.status === '5'"
              link
              type="warning"
              icon="Check"
              @click="handleComplete(row)"
            >完成</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-if="serviceStore.serviceOrderTotal > 0"
        :total="serviceStore.serviceOrderTotal"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- 审核对话框 -->
    <el-dialog
      title="审核服务申请"
      v-model="reviewDialog.visible"
      width="500px"
      append-to-body
    >
      <el-form ref="reviewFormRef" :model="reviewForm" :rules="reviewRules" label-width="100px">
        <el-form-item label="申请原因">
          <div class="review-content">{{ currentOrder?.reason }}</div>
        </el-form-item>
        <el-form-item label="审核结果" prop="approved">
          <el-radio-group v-model="reviewForm.approved">
            <el-radio :label="true">通过</el-radio>
            <el-radio :label="false">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见" prop="reviewComment">
          <el-input
            v-model="reviewForm.reviewComment"
            type="textarea"
            :rows="4"
            placeholder="请输入审核意见"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitReview">确 定</el-button>
          <el-button @click="reviewDialog.visible = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 派单对话框 -->
    <el-dialog
      title="派单"
      v-model="assignDialog.visible"
      width="500px"
      append-to-body
    >
      <el-form ref="assignFormRef" :model="assignForm" :rules="assignRules" label-width="100px">
        <el-form-item label="服务人员" prop="staffId">
          <el-select v-model="assignForm.staffId" placeholder="请选择服务人员" style="width: 100%">
            <el-option
              v-for="staff in staffList"
              :key="staff.id"
              :label="staff.name"
              :value="staff.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitAssign">确 定</el-button>
          <el-button @click="assignDialog.visible = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 完成服务对话框 -->
    <el-dialog
      title="完成服务"
      v-model="completeDialog.visible"
      width="500px"
      append-to-body
    >
      <el-form ref="completeFormRef" :model="completeForm" :rules="completeRules" label-width="100px">
        <el-form-item label="实际时长" prop="actualDuration">
          <el-input-number
            v-model="completeForm.actualDuration"
            :min="1"
            :max="24"
            placeholder="请输入实际服务时长（小时）"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="服务评价" prop="rating">
          <el-rate
            v-model="completeForm.rating"
            :max="5"
            :texts="['很差', '较差', '一般', '较好', '很好']"
            show-text
          />
        </el-form-item>
        <el-form-item label="评价内容" prop="content">
          <el-input
            v-model="completeForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入服务评价内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitComplete">确 定</el-button>
          <el-button @click="completeDialog.visible = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { useServiceStore } from '@/stores/back/service';
import Pagination from '@/components/common/Pagination.vue';

const serviceStore = useServiceStore();

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  userId: '',
  serviceItemId: '',
  status: '',
  appointmentTime: ''
});

// 审核表单参数
const reviewFormRef = ref();
const reviewForm = reactive({
  orderId: undefined,
  approved: true,
  reviewComment: ''
});

// 审核表单校验规则
const reviewRules = {
  approved: [
    { required: true, message: '请选择审核结果', trigger: 'change' }
  ],
  reviewComment: [
    { required: true, message: '请输入审核意见', trigger: 'blur' },
    { min: 2, max: 200, message: '长度在 2 到 200 个字符', trigger: 'blur' }
  ]
};

// 派单表单参数
const assignFormRef = ref();
const assignForm = reactive({
  orderId: undefined,
  staffId: undefined
});

// 派单表单校验规则
const assignRules = {
  staffId: [
    { required: true, message: '请选择服务人员', trigger: 'change' }
  ]
};

// 完成服务表单参数
const completeFormRef = ref();
const completeForm = reactive({
  orderId: undefined,
  actualDuration: 1,
  rating: 5,
  content: ''
});

// 完成服务表单校验规则
const completeRules = {
  actualDuration: [
    { required: true, message: '请输入实际服务时长', trigger: 'blur' }
  ],
  rating: [
    { required: true, message: '请选择服务评分', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入评价内容', trigger: 'blur' },
    { min: 2, max: 500, message: '长度在 2 到 500 个字符', trigger: 'blur' }
  ]
};

// 弹窗参数
const reviewDialog = reactive({
  visible: false
});

const assignDialog = reactive({
  visible: false
});

const completeDialog = reactive({
  visible: false
});

// 当前工单
const currentOrder = ref(null);

// 服务人员列表
const staffList = ref([
  { id: 1, name: '张三' },
  { id: 2, name: '李四' },
  { id: 3, name: '王五' }
]);

// 获取工单列表
const getList = async () => {
  await serviceStore.getServiceOrders(queryParams);
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
  queryParams.userId = '';
  queryParams.serviceItemId = '';
  queryParams.status = '';
  queryParams.appointmentTime = '';
  handleQuery();
};

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    '0': 'info',
    '1': 'success',
    '2': 'danger',
    '3': 'warning',
    '4': 'primary',
    '5': 'success',
    '6': 'success',
    '7': 'info'
  };
  return typeMap[status] || '';
};

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    '0': '待审核',
    '1': '已通过',
    '2': '已拒绝',
    '3': '待派单',
    '4': '已派单',
    '5': '服务中',
    '6': '已完成',
    '7': '已取消'
  };
  return textMap[status] || '';
};

// 审核操作
const handleReview = (row, approved) => {
  currentOrder.value = row;
  reviewDialog.visible = true;
  Object.assign(reviewForm, {
    orderId: row.orderId,
    approved,
    reviewComment: ''
  });
};

// 提交审核
const submitReview = async () => {
  await reviewFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await serviceStore.reviewServiceOrder(reviewForm);
        ElMessage.success('审核成功');
        reviewDialog.visible = false;
        getList();
      } catch (error) {
        ElMessage.error(error.message || '审核失败');
      }
    }
  });
};

// 派单操作
const handleAssign = (row) => {
  currentOrder.value = row;
  assignDialog.visible = true;
  Object.assign(assignForm, {
    orderId: row.orderId,
    staffId: undefined
  });
};

// 提交派单
const submitAssign = async () => {
  await assignFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await serviceStore.assignServiceOrder(assignForm);
        ElMessage.success('派单成功');
        assignDialog.visible = false;
        getList();
      } catch (error) {
        ElMessage.error(error.message || '派单失败');
      }
    }
  });
};

// 开始服务操作
const handleStart = async (row) => {
  try {
    await serviceStore.startServiceOrder(row.orderId);
    ElMessage.success('开始服务成功');
    getList();
  } catch (error) {
    ElMessage.error(error.message || '开始服务失败');
  }
};

// 完成服务操作
const handleComplete = (row) => {
  currentOrder.value = row;
  completeDialog.visible = true;
  Object.assign(completeForm, {
    orderId: row.orderId,
    actualDuration: 1,
    rating: 5,
    content: ''
  });
};

// 提交完成服务
const submitComplete = async () => {
  await completeFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await serviceStore.completeServiceOrder(completeForm);
        ElMessage.success('完成服务成功');
        completeDialog.visible = false;
        getList();
      } catch (error) {
        ElMessage.error(error.message || '完成服务失败');
      }
    }
  });
};

// 导出操作
const handleExport = async () => {
  try {
    await serviceStore.exportServiceOrder(queryParams);
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
.service-appointment {
  padding: 20px;
}

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

.dialog-footer {
  text-align: right;
  padding-top: 20px;
}
</style>