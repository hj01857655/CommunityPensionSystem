<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="100px">
      <el-row :gutter="16">
        <el-col :span="8">
          <el-form-item label="订单号" prop="orderNo">
            <el-input v-model="queryParams.orderNo" placeholder="请输入订单号" clearable style="width: 100%" @keyup.enter="handleQuery" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="用户名" prop="userName">
            <el-input v-model="queryParams.userName" placeholder="请输入用户名" clearable style="width: 100%" @keyup.enter="handleQuery" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="服务名称" prop="serviceName">
            <el-input v-model="queryParams.serviceName" placeholder="请输入服务名称" clearable style="width: 100%" @keyup.enter="handleQuery" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16" style="margin-top: 8px;">
        <el-col :span="8">
          <el-form-item label="订单状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="订单状态" clearable style="width: 100%">
              <el-option v-for="dict in statusOptions" :key="dict.value" :label="dict.label" :value="dict.value" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="创建时间">
            <el-date-picker v-model="dateRange" style="width: 100%" value-format="YYYY-MM-DD" type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <el-row :gutter="10" class="mb8">
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

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="工单ID" align="center" prop="id" width="80" />
      <el-table-column label="用户名" align="center" prop="userName" :show-overflow-tooltip="true" min-width="100" />
      <el-table-column label="服务名称" align="center" prop="serviceName" :show-overflow-tooltip="true" min-width="160" />
      <el-table-column label="服务类型" align="center" prop="serviceTypeName" :show-overflow-tooltip="true" min-width="120" />
      <el-table-column label="预约时间" align="center" prop="scheduleTime" width="180">
        <template #default="scope">
          <span>{{ formatDate(scope.row.scheduleTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="statusName">
        <template #default="scope">
          <el-tag>{{ scope.row.statusName }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="申请原因" align="center" prop="applyReason" :show-overflow-tooltip="true" min-width="140" />
      <el-table-column label="审核备注" align="center" prop="reviewRemark" :show-overflow-tooltip="true" min-width="140" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ formatDate(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link icon="View" @click="handleView(scope.row)">查看</el-button>
          <!-- 根据当前状态显示对应的操作按钮 -->
          <template v-if="scope.row.status == 0">
            <el-button link type="success" icon="Check" @click="handleStatusUpdate(scope.row, '1')">接单</el-button>
            <el-button link type="danger" icon="Close" @click="handleStatusUpdate(scope.row, '5')">拒绝</el-button>
          </template>
          <template v-else-if="scope.row.status == 1">
            <el-button link type="primary" icon="VideoPlay" @click="handleStatusUpdate(scope.row, '2')">开始服务</el-button>
          </template>
          <template v-else-if="scope.row.status == 2">
            <el-button link type="success" icon="CircleCheck" @click="handleStatusUpdate(scope.row, '3')">完成服务</el-button>
          </template>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-if="[0, 1, 2].includes(Number(scope.row.status))">取消</el-button>
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

    <!-- 查看订单详情对话框 -->
    <el-dialog title="工单详情" v-model="viewOpen" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="工单ID">{{ form.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ form.userName }}</el-descriptions-item>
        <el-descriptions-item label="服务名称">{{ form.serviceName }}</el-descriptions-item>
        <el-descriptions-item label="服务类型">{{ form.serviceTypeName }}</el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ formatDate(form.scheduleTime) }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ form.statusName }}</el-descriptions-item>
        <el-descriptions-item label="申请原因">{{ form.applyReason }}</el-descriptions-item>
        <el-descriptions-item label="审核备注">{{ form.reviewRemark }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDate(form.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ formatDate(form.updateTime) }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="viewOpen = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 处理订单对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="orderForm" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option v-for="dict in statusOptions" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="审核备注" prop="reviewRemark">
          <el-input v-model="form.reviewRemark" type="textarea" placeholder="请输入审核备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { useServiceOrderStore } from '@/stores/back/service';
import { formatDate } from '@/utils/date';
import { ElMessage, ElMessageBox } from 'element-plus';
import { onMounted, ref } from 'vue';

const serviceOrderStore = useServiceOrderStore();

const orderList = ref([]);
const open = ref(false);
const viewOpen = ref(false);
const loading = ref(false);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const dateRange = ref([]);

// 状态数据字典
const statusOptions = [
  { value: "0", label: "待处理" },
  { value: "1", label: "已接单" },
  { value: "2", label: "服务中" },
  { value: "3", label: "已完成" },
  { value: "4", label: "已取消" },
  { value: "5", label: "已拒绝" }
];

// 查询参数
const queryParams = ref({
  current: 1,
  size: 10,
  orderNo: undefined,
  userName: undefined,
  serviceName: undefined,
  status: undefined,
  beginTime: undefined,
  endTime: undefined
});

// 表单参数
const form = ref({
  id: undefined,
  userId: undefined,
  userName: undefined,
  serviceId: undefined,
  serviceName: undefined,
  serviceTypeName: undefined,
  scheduleTime: undefined,
  status: undefined,
  statusName: undefined,
  applyReason: undefined,
  reviewRemark: undefined,
  createTime: undefined,
  updateTime: undefined
});

// 表单校验规则
const rules = {
  status: [
    { required: true, message: "订单状态不能为空", trigger: "change" }
  ],
  reviewRemark: [
    { required: true, message: "审核备注不能为空", trigger: "blur" }
  ]
};

const orderForm = ref(null);

const canHandleStatus = ['0', '1', '2'];

/** 查询订单列表 */
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

    const response = await serviceOrderStore.getOrderList(queryParams.value);
    console.log('orderList response:', response);
    if (response && response.records) {
      orderList.value = response.records;
      total.value = response.total;
      response.records.forEach(item => {
        if (!item.id) {
          console.error('警告：有工单数据缺少id字段', item);
        } else {
          console.log('工单ID:', item.id, '工单数据:', item);
        }
      });
    }
  } catch (error) {
    console.error("获取订单列表失败:", error);
  } finally {
    loading.value = false;
  }
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

/** 表单重置 */
function reset() {
  form.value = {
    id: undefined,
    userId: undefined,
    userName: undefined,
    serviceId: undefined,
    serviceName: undefined,
    serviceTypeName: undefined,
    scheduleTime: undefined,
    status: undefined,
    statusName: undefined,
    applyReason: undefined,
    reviewRemark: undefined,
    createTime: undefined,
    updateTime: undefined
  };
  if (orderForm.value) {
    orderForm.value.resetFields();
  }
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
    orderNo: undefined,
    userName: undefined,
    serviceName: undefined,
    status: undefined,
    beginTime: undefined,
    endTime: undefined
  };
  handleQuery();
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

/** 查看按钮操作 */
async function handleView(row) {
  reset();
  const orderId = row.id || ids.value[0];
  try {
    const response = await serviceOrderStore.getOrderDetail(orderId);
    if (response) {
      form.value = response;
      viewOpen.value = true;
    }
  } catch (error) {
    console.error("获取订单详情失败:", error);
    ElMessage.error("获取订单详情失败");
  }
}

/** 处理按钮操作 */
async function handleUpdate(row) {
  reset();
  const orderId = row.id || ids.value[0];
  try {
    const response = await serviceOrderStore.getOrderDetail(orderId);
    if (response) {
      form.value = response;
      open.value = true;
      title.value = "处理订单";
    }
  } catch (error) {
    console.error("获取订单详情失败:", error);
    ElMessage.error("获取订单详情失败");
  }
}

/** 提交按钮 */
async function submitForm() {
  if (!orderForm.value) return;
  
  await orderForm.value.validate(async (valid) => {
    if (valid) {
      try {
        await serviceOrderStore.reviewOrder(form.value.id, form.value);
        ElMessage.success("处理成功");
        open.value = false;
        getList();
      } catch (error) {
        console.error("处理订单失败:", error);
        ElMessage.error("处理失败");
      }
    }
  });
}

/** 取消订单按钮操作 */
function handleDelete(row) {
  const orderIds = row.id || ids.value;
  ElMessageBox.confirm('是否确认取消订单编号为"' + orderIds + '"的数据项?', "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(async function() {
    try {
      await serviceOrderStore.cancelOrder(orderIds);
      getList();
      ElMessage.success("取消成功");
    } catch (error) {
      console.error("取消订单失败:", error);
      ElMessage.error("取消失败");
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
  
  serviceOrderStore.exportList(queryParams.value);
}

/** 状态更新 */
async function handleStatusUpdate(row, status) {
  console.log('handleStatusUpdate row:', row);
  if (!row.id) {
    ElMessage.error('工单ID不存在，无法更新状态！');
    return;
  }
  try {
    await serviceOrderStore.updateOrderStatus(row.id, status);
    ElMessage.success("状态更新成功");
    getList();
  } catch (error) {
    console.error("状态更新失败:", error);
    ElMessage.error("状态更新失败");
  }
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
