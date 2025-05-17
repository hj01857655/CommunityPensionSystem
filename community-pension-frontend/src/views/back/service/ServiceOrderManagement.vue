<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="订单号" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单号"
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
      <el-form-item label="服务名称" prop="serviceName">
        <el-input
          v-model="queryParams.serviceName"
          placeholder="请输入服务名称"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="订单状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="订单状态" clearable style="width: 240px">
          <el-option
            v-for="dict in statusOptions"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
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
      <el-table-column label="订单ID" align="center" prop="orderId" width="80" />
      <el-table-column label="订单号" align="center" prop="orderNo" width="180" />
      <el-table-column label="用户名" align="center" prop="userName" :show-overflow-tooltip="true" />
      <el-table-column label="服务名称" align="center" prop="serviceName" :show-overflow-tooltip="true" />
      <el-table-column label="订单金额" align="center" prop="amount">
        <template #default="scope">
          <span>{{ scope.row.amount }}元</span>
        </template>
      </el-table-column>
      <el-table-column label="服务时间" align="center" prop="serviceTime" width="180">
        <template #default="scope">
          <span>{{ formatDate(scope.row.serviceTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="订单状态" align="center" prop="status">
        <template #default="scope">
          <el-tag :type="statusTagType(scope.row.status)">
            {{ statusLabel(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
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
            icon="Edit"
            @click="handleUpdate(scope.row)"
            v-if="scope.row.status === '1'"
          >处理</el-button>
          <el-button
            link
            icon="Delete"
            @click="handleDelete(scope.row)"
            v-if="scope.row.status === '0'"
          >取消</el-button>
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
    <el-dialog title="订单详情" v-model="viewOpen" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单号">{{ form.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ form.userName }}</el-descriptions-item>
        <el-descriptions-item label="服务名称">{{ form.serviceName }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">{{ form.amount }}元</el-descriptions-item>
        <el-descriptions-item label="服务时间">{{ formatDate(form.serviceTime) }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">{{ statusLabel(form.status) }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ form.phone }}</el-descriptions-item>
        <el-descriptions-item label="服务地址">{{ form.address }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ form.remark }}</el-descriptions-item>
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
        <el-form-item label="订单状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择订单状态">
            <el-option
              v-for="dict in statusOptions"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
              :disabled="dict.value === '0'"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="处理备注" prop="handleRemark">
          <el-input v-model="form.handleRemark" type="textarea" placeholder="请输入处理备注" />
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
  orderId: undefined,
  orderNo: undefined,
  userId: undefined,
  userName: undefined,
  serviceId: undefined,
  serviceName: undefined,
  amount: undefined,
  serviceTime: undefined,
  status: undefined,
  phone: undefined,
  address: undefined,
  remark: undefined,
  handleRemark: undefined,
  createTime: undefined,
  updateTime: undefined
});

// 表单校验规则
const rules = {
  status: [
    { required: true, message: "订单状态不能为空", trigger: "change" }
  ],
  handleRemark: [
    { required: true, message: "处理备注不能为空", trigger: "blur" }
  ]
};

const orderForm = ref(null);

// 状态标签类型
const statusTagType = (status) => {
  const map = {
    "0": "info",
    "1": "primary",
    "2": "warning",
    "3": "success",
    "4": "danger",
    "5": "danger"
  };
  return map[status] || "info";
};

// 状态显示文本
const statusLabel = (status) => {
  const option = statusOptions.find(item => item.value === status);
  return option ? option.label : "";
};

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
    if (response && response.records) {
      orderList.value = response.records;
      total.value = response.total;
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
    orderId: undefined,
    orderNo: undefined,
    userId: undefined,
    userName: undefined,
    serviceId: undefined,
    serviceName: undefined,
    amount: undefined,
    serviceTime: undefined,
    status: undefined,
    phone: undefined,
    address: undefined,
    remark: undefined,
    handleRemark: undefined,
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
  ids.value = selection.map(item => item.orderId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

/** 查看按钮操作 */
async function handleView(row) {
  reset();
  const orderId = row.orderId || ids.value[0];
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
  const orderId = row.orderId || ids.value[0];
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
        await serviceOrderStore.reviewOrder(form.value.orderId, form.value);
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
  const orderIds = row.orderId || ids.value;
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

onMounted(() => {
  getList();
});
</script>

<style scoped>
.el-tag + .el-tag {
  margin-left: 10px;
}
</style>
