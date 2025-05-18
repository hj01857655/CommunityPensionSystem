<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="服务名称" prop="serviceName">
        <el-input
          v-model="queryParams.serviceName"
          placeholder="请输入服务名称"
          clearable
          style="width: 240px; min-width: 240px;"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="服务状态" clearable style="width: 240px; min-width: 240px;">
          <el-option
            v-for="dict in statusOptions"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
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

    <el-table v-loading="loading" :data="serviceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="服务ID" align="center" prop="serviceId" />
      <el-table-column label="服务名称" align="center" prop="serviceName" :show-overflow-tooltip="true" />
      <el-table-column label="服务描述" align="center" prop="description" :show-overflow-tooltip="true" />
      <el-table-column label="服务价格" align="center" prop="price">
        <template #default="scope">
          <span>{{ scope.row.price }}元</span>
        </template>
      </el-table-column>
      <el-table-column label="服务时长" align="center" prop="duration">
        <template #default="scope">
          <span>{{ scope.row.duration }}分钟</span>
        </template>
      </el-table-column>
      <el-table-column label="服务类型" align="center" prop="serviceTypeName">
        <template #default="scope">
          <span>{{ scope.row.serviceTypeName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="1"
            inactive-value="0"
            @change="handleStatusChange(scope.row)"
          ></el-switch>
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
            icon="Edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
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

    <!-- 添加或修改服务对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="serviceForm" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="服务名称" prop="serviceName">
          <el-input v-model="form.serviceName" placeholder="请输入服务名称" />
        </el-form-item>
        <el-form-item label="服务描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入服务描述" />
        </el-form-item>
        <el-form-item label="服务价格" prop="price">
          <el-input-number v-model="form.price" :precision="2" :step="10" :min="0" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="服务时长" prop="duration">
          <el-input-number v-model="form.duration" :min="0" :step="10" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="服务类型" prop="serviceType">
          <el-select v-model="form.serviceType" placeholder="请选择服务类型" style="width: 100%">
            <el-option
              v-for="dict in serviceTypeOptions"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in statusOptions"
              :key="dict.value"
              :value="dict.value"
              :label="dict.label"
            >{{ dict.label }}</el-radio>
          </el-radio-group>
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
import { useServiceItemStore } from '@/stores/back/service';
import { formatDate } from '@/utils/date';
import { useDict } from '@/utils/dict';
import { ElMessage, ElMessageBox } from 'element-plus';
import { computed, onMounted, ref } from 'vue';

const serviceStore = useServiceItemStore();

// 使用字典
const { service_status, service_type } = useDict('service_status', 'service_type');

const serviceList = ref([]);
const open = ref(false);
const loading = ref(false);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");

// 状态数据字典
const statusOptions = computed(() => service_status.value || [
  { value: "1", label: "正常" },
  { value: "0", label: "停用" }
]);

// 服务类型选项
const serviceTypeOptions = computed(() => service_type.value || [
  { value: "medical", label: "医疗服务" },
  { value: "cleaning", label: "保洁服务" },
  { value: "repair", label: "维修服务" }
]);

// 查询参数
const queryParams = ref({
  current: 1,
  size: 10,
  serviceName: undefined,
  status: undefined
});

// 表单参数
const form = ref({
  serviceId: undefined,
  serviceName: undefined,
  description: undefined,
  price: 0,
  duration: 30,
  status: "1",
  serviceType: "medical"
});

// 表单校验规则
const rules = {
  serviceName: [
    { required: true, message: "服务名称不能为空", trigger: "blur" },
    { min: 2, max: 50, message: '服务名称长度必须在2到50个字符之间', trigger: 'blur' }
  ],
  description: [
    { required: true, message: "服务描述不能为空", trigger: "blur" }
  ],
  price: [
    { required: true, message: "服务价格不能为空", trigger: "blur" }
  ],
  duration: [
    { required: true, message: "服务时长不能为空", trigger: "blur" }
  ],
  serviceType: [
    { required: true, message: "服务类型不能为空", trigger: "blur" }
  ]
};

const serviceForm = ref(null);

/** 查询服务列表 */
async function getList() {
  loading.value = true;
  try {
    const response = await serviceStore.getServiceItemList(queryParams.value);
    if (response) {
      serviceList.value = response.records;
      total.value = response.total;
    }
  } catch (error) {
    console.error("获取服务列表失败:", error);
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
    serviceId: undefined,
    serviceName: undefined,
    description: undefined,
    price: 0,
    duration: 30,
    status: "1",
    serviceType: "medical"
  };
  if (serviceForm.value) {
    serviceForm.value.resetFields();
  }
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.current = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  queryParams.value = {
    current: 1,
    size: 10,
    serviceName: undefined,
    status: undefined
  };
  handleQuery();
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.serviceId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加服务项目";
}

/** 修改按钮操作 */
async function handleUpdate(row) {
  reset();
  // 确保ID是字符串类型，避免JavaScript大整数精度问题
  const serviceId = row.serviceId ? String(row.serviceId) : (ids.value[0] ? String(ids.value[0]) : '');
  console.log('准备修改服务项目，ID:', serviceId);
  
  if (!serviceId) {
    ElMessage.error("服务ID不能为空");
    return;
  }
  
  try {
    console.log('调用获取服务详情API');
    const response = await serviceStore.getServiceItemDetail(serviceId);
    console.log('获取到的服务详情:', response);
    
    if (response) {
      // 确保所有必要的字段都存在
      form.value = {
        serviceId: response.serviceId,
        serviceName: response.serviceName || '',
        description: response.description || '',
        price: response.price || 0,
        duration: response.duration || 30,
        status: response.status || '0',
        serviceType: response.serviceType || 'medical'
      };
      
      console.log('设置表单数据:', form.value);
      open.value = true;
      title.value = "修改服务项目";
    } else {
      ElMessage.error("获取服务详情失败");
    }
  } catch (error) {
    console.error("获取服务详情失败:", error);
    ElMessage.error("获取服务详情失败");
  }
}

/** 提交按钮 */
async function submitForm() {
  if (!serviceForm.value) return;
  
  await serviceForm.value.validate(async (valid) => {
    if (valid) {
      try {
        // 创建一个新对象用于API请求，避免修改原表单数据
        const apiData = { ...form.value };
        
        // 如果是编辑模式，确保serviceId是数字类型
        if (apiData.serviceId) {
          apiData.serviceId = Number(apiData.serviceId);
        }
        
        // 确保价格是字符串类型，以便后端正确解析为BigDecimal
        if (apiData.price !== undefined && apiData.price !== null) {
          apiData.price = String(apiData.price);
        }
        
        console.log('提交的表单数据:', apiData);
        
        if (apiData.serviceId) {
          await serviceStore.updateServiceItem(apiData);
          ElMessage.success("修改成功");
        } else {
          await serviceStore.addServiceItem(apiData);
          ElMessage.success("新增成功");
        }
        
        open.value = false;
        getList();
      } catch (error) {
        console.error("操作失败:", error);
        ElMessage.error("操作失败");
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  // 如果传入了行对象，使用该行的ID；否则使用选中的ID数组
  const serviceId = row?.serviceId;
  const serviceIds = serviceId ? Number(serviceId) : ids.value.map(id => Number(id));
  
  console.log('准备删除的服务ID:', serviceIds, typeof serviceIds, Array.isArray(serviceIds));
  
  // 构造确认消息
  const confirmMessage = serviceId 
    ? `是否确认删除服务项目编号为"${serviceId}"的数据项?`
    : `是否确认删除选中的${ids.value.length}个服务项目?`;
  
  ElMessageBox.confirm(confirmMessage, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(async function() {
    try {
      console.log('用户确认删除，发送删除请求...');
      
      let result = null;
      
      if (serviceId) {
        // 单个删除
        result = await serviceStore.deleteServiceItem(Number(serviceId));
      } else if (ids.value.length > 0) {
        // 批量删除
        result = await serviceStore.batchDeleteServiceItem(ids.value.map(id => Number(id)));
      } else {
        ElMessage.warning('请选择要删除的数据');
        return;
      }
      
      // 只有当返回结果为true时才刷新列表
      if (result === true) {
        getList();
        ElMessage.success("删除成功");
      } else {
        console.log('删除操作未返回成功结果:', result);
        // 错误消息已在store方法中处理
      }
    } catch (error) {
      console.error("删除服务失败:", error);
      ElMessage.error("删除失败");
    }
  });
}

/** 导出按钮操作 */
function handleExport() {
  serviceStore.exportServiceItemList(queryParams.value);
}

/** 状态修改 */
async function handleStatusChange(row) {
  let text = row.status === "0" ? "启用" : "停用";
  try {
    await serviceStore.updateServiceItemStatus(row.serviceId, row.status);
    ElMessage.success(text + "成功");
  } catch (error) {
    console.error(`${text}服务失败:`, error);
    row.status = row.status === "0" ? "1" : "0";
    ElMessage.error(text + "失败");
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
.button-new-tag {
  margin-left: 10px;
  height: 32px;
  line-height: 30px;
  padding-top: 0;
  padding-bottom: 0;
}
.input-new-tag {
  width: 90px;
  margin-left: 10px;
  vertical-align: bottom;
}
</style>
