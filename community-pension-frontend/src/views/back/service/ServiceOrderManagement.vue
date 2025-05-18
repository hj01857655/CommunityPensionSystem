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
            <el-select v-model="queryParams.status" placeholder="订单状态" clearable style="width: 100%; min-width: 180px;">
              <el-option 
                v-for="dict in statusOptions" 
                :key="dict.value" 
                :label="dict.label" 
                :value="dict.value"
                class="status-option"
              />
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
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete()"
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
          <template v-if="statusOperations[String(scope.row.status)]">
            <el-button 
              v-for="op in statusOperations[String(scope.row.status)]" 
              :key="op.action"
              link 
              :type="op.type" 
              :icon="op.icon" 
              @click="handleStatusUpdate(scope.row, op.action)"
            >
              {{ op.label }}
            </el-button>
          </template>
          <el-button 
            link 
            type="danger" 
            icon="Delete" 
            @click="handleDelete(scope.row)" 
            v-if="deletableStatuses.includes(Number(scope.row.status))"
          >
            取消
          </el-button>
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

    <!-- 添加或修改工单对话框 -->
    <el-dialog :title="title" v-model="open" width="700px" append-to-body>
      <el-form ref="orderForm" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户" prop="userId">
              <el-select v-model="form.userId" placeholder="请选择用户" filterable clearable style="width: 100%">
                <el-option
                  v-for="item in userOptions"
                  :key="item.userId"
                  :label="item.name"
                  :value="item.userId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="服务项目" prop="serviceItemId">
              <el-select v-model="form.serviceItemId" placeholder="请选择服务项目" filterable clearable style="width: 100%">
                <el-option
                  v-for="item in serviceOptions"
                  :key="item.serviceId"
                  :label="item.serviceName"
                  :value="item.serviceId"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="预约时间" prop="scheduleTime">
              <el-date-picker
                v-model="form.scheduleTime"
                type="datetime"
                placeholder="选择预约时间"
                style="width: 100%"
                :disabled-date="disabledDate"
                value-format="YYYY-MM-DDTHH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
                <el-option
                  v-for="dict in statusOptions"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="申请原因" prop="applyReason">
          <el-input v-model="form.applyReason" type="textarea" placeholder="请输入申请原因" />
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
import { useServiceItemStore } from '@/stores/back/service/item';
import { useServiceOrderStore } from '@/stores/back/service/order';
import { useUserStore } from '@/stores/back/userStore';
import { formatDate } from '@/utils/date';
import { useDict } from '@/utils/dict';
import { ElMessage, ElMessageBox } from 'element-plus';
import { computed, onMounted, ref } from 'vue';

const serviceOrderStore = useServiceOrderStore();
const userStore = useUserStore();
const serviceItemStore = useServiceItemStore();

// 使用字典
const { order_status } = useDict('order_status');

const orderList = ref([]);
const open = ref(false);
const loading = ref(false);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const viewOpen = ref(false);
const dateRange = ref([]);

// 用户和服务项目选项
const userOptions = ref([]);
const serviceOptions = ref([]);

// 状态数据字典
const statusOptions = computed(() => order_status.value || []);

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
  serviceItemId: undefined,
  scheduleTime: undefined,
  status: "0",
  applyReason: undefined,
  reviewRemark: undefined
});

// 表单校验规则
const rules = ref({
  userId: [
    { required: true, message: "用户不能为空", trigger: "blur" }
  ],
  serviceItemId: [
    { required: true, message: "服务项目不能为空", trigger: "blur" }
  ],
  scheduleTime: [
    { required: true, message: "预约时间不能为空", trigger: "blur" },
    { 
      validator: (rule, value, callback) => {
        if (value && new Date(value) <= new Date()) {
          callback(new Error('预约时间必须大于当前时间'));
        } else {
          callback();
        }
      }, 
      trigger: 'change' 
    }
  ],
  status: [
    { required: true, message: "状态不能为空", trigger: "blur" }
  ],
  applyReason: [
    { required: true, message: "申请原因不能为空", trigger: "blur" },
    { min: 5, max: 500, message: "申请原因长度必须在5-500个字符之间", trigger: "blur" }
  ]
});

const orderForm = ref(null);

/** 查询工单列表 */
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
    
    const response = await serviceOrderStore.getList(queryParams.value);
    if (response) {
      orderList.value = response.records;
      total.value = response.total;
    }
  } catch (error) {
    console.error("获取工单列表失败:", error);
  } finally {
    loading.value = false;
  }
}

/** 获取用户列表 */
async function getUserList() {
  try {
    const response = await userStore.fetchUsers({
      current: 1,
      size: 100
    });
    if (response && userStore.userList) {
      userOptions.value = userStore.userList;
    }
  } catch (error) {
    console.error("获取用户列表失败:", error);
  }
}

/** 获取服务项目列表 */
async function getServiceList() {
  try {
    const response = await serviceItemStore.getServiceItemList({
      current: 1,
      size: 100
    });
    if (response && serviceItemStore.serviceItemList) {
      serviceOptions.value = serviceItemStore.serviceItemList;
    }
  } catch (error) {
    console.error("获取服务项目列表失败:", error);
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
    serviceItemId: undefined,
    scheduleTime: undefined,
    status: "0",
    applyReason: undefined,
    reviewRemark: undefined
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
  try {
    const response = await serviceOrderStore.getOrderDetail(row.id);
    if (response) {
      form.value = response;
      viewOpen.value = true;
    }
  } catch (error) {
    console.error("获取订单详情失败:", error);
    ElMessage.error("获取订单详情失败");
  }
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  getUserList();
  getServiceList();
  open.value = true;
  title.value = "新增工单";
}

/** 修改按钮操作 */
async function handleUpdate(row) {
  reset();
  getUserList();
  getServiceList();
  const orderId = row?.id || ids.value[0];
  try {
    const response = await serviceOrderStore.getOrderDetail(orderId);
    if (response) {
      // 确保状态值是字符串类型，与字典中的值类型一致
      if (response.status !== undefined && response.status !== null) {
        response.status = String(response.status);
      }
      form.value = response;
      open.value = true;
      title.value = "修改工单";
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
        // 创建一个新对象用于API请求
        const apiData = { ...form.value };
        
        // 额外验证申请原因长度
        if (apiData.applyReason && apiData.applyReason.length < 5) {
          ElMessage.error('申请原因长度必须在5-500个字符之间');
          return;
        }
        
        // 额外验证预约时间
        if (apiData.scheduleTime && new Date(apiData.scheduleTime) <= new Date()) {
          ElMessage.error('预约时间必须大于当前时间');
          return;
        }
        
        // 确保ID是数字类型
        if (apiData.id) {
          apiData.id = Number(apiData.id);
        }
        
        // 确保状态值是数字类型
        if (apiData.status !== undefined && apiData.status !== null) {
          apiData.status = Number(apiData.status);
        }
        
        if (apiData.id) {
          await serviceOrderStore.updateOrder(apiData);
          ElMessage.success("修改成功");
        } else {
          await serviceOrderStore.addOrder(apiData);
          ElMessage.success("新增成功");
        }
        
        open.value = false;
        getList();
      } catch (error) {
        console.error("操作失败:", error);
        ElMessage.error(error.message || "操作失败");
      }
    }
  });
}

/** 状态修改操作 */
async function handleStatusUpdate(row, status) {
  // 从字典中查找对应状态的标签
  const statusItem = statusOptions.value.find(item => item.value === status);
  const text = statusItem ? statusItem.label : '更新状态';
  
  try {
    await serviceOrderStore.updateOrderStatus(row.id, status);
    getList();
    ElMessage.success(`${text}成功`);
  } catch (error) {
    console.error(`${text}失败:`, error);
    ElMessage.error(`${text}失败`);
  }
}

/** 删除按钮操作 */
function handleDelete(row) {
  const orderIds = row?.id || ids.value;
  ElMessageBox.confirm('是否确认删除工单编号为"' + orderIds + '"的数据项?', "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(async function() {
    try {
      await serviceOrderStore.deleteOrder(orderIds);
      getList();
      ElMessage.success("删除成功");
    } catch (error) {
      console.error("删除工单失败:", error);
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
  
  serviceOrderStore.exportList(queryParams.value);
}

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now();
};

// 定义状态操作映射
const statusOperations = computed(() => {
  return {
    '0': [
      { type: 'success', icon: 'Check', action: '1', label: '派单' },
      { type: 'danger', icon: 'Close', action: '5', label: '拒绝' }
    ],
    '1': [
      { type: 'primary', icon: 'VideoPlay', action: '2', label: '开始服务' }
    ],
    '2': [
      { type: 'success', icon: 'CircleCheck', action: '3', label: '完成服务' }
    ]
  };
});

// 获取可删除的状态列表
const deletableStatuses = [0, 1, 2];

onMounted(() => {
  // 获取工单列表
  getList();
  // 获取用户和服务项目列表，以便在新增和修改时使用
  getUserList();
  getServiceList();
});
</script>

<style scoped>
.el-tag + .el-tag {
  margin-left: 10px;
}
</style>
