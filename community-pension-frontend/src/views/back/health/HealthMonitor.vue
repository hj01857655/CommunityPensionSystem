<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" v-show="showSearch">
      <el-form-item label="老人姓名" prop="elderName">
        <el-input
          v-model="queryParams.elderName"
          placeholder="请输入老人姓名"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="监测类型" prop="monitoringType">
        <el-select v-model="queryParams.monitoringType" placeholder="请选择监测类型" clearable style="width: 200px">
          <el-option label="血压" value="1" />
          <el-option label="血糖" value="2" />
          <el-option label="心率" value="3" />
          <el-option label="体温" value="4" />
          <el-option label="体重" value="5" />
        </el-select>
      </el-form-item>
      <el-form-item label="监测状态" prop="monitoringStatus">
        <el-select v-model="queryParams.monitoringStatus" placeholder="请选择监测状态" clearable style="width: 200px">
          <el-option label="正常" value="normal" />
          <el-option label="异常" value="abnormal" />
        </el-select>
      </el-form-item>
      <el-form-item label="处理状态" prop="isProcessed">
        <el-select v-model="queryParams.isProcessed" placeholder="请选择处理状态" clearable style="width: 200px">
          <el-option label="已处理" :value="true" />
          <el-option label="未处理" :value="false" />
        </el-select>
      </el-form-item>
      <el-form-item label="监测时间" prop="dateRange">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          style="width: 240px"
        />
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

    <el-table v-loading="loading" :data="monitorList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="监测ID" align="center" prop="id" width="80" />
      <el-table-column label="老人姓名" align="center" prop="elderName" :show-overflow-tooltip="true" />
      <el-table-column label="监测类型" align="center" prop="monitoringType">
        <template #default="scope">
          <dict-tag :options="monitorTypeOptions" :value="scope.row.monitoringType" />
        </template>
      </el-table-column>
      <el-table-column label="监测值" align="center" prop="monitoringValue" :show-overflow-tooltip="true">
        <template #default="scope">
          <span>{{ scope.row.monitoringValue }} {{ scope.row.monitoringUnit }}</span>
        </template>
      </el-table-column>
      <el-table-column label="监测状态" align="center" prop="monitoringStatus">
        <template #default="scope">
          <el-tag :type="scope.row.monitoringStatus === 'abnormal' ? 'danger' : 'success'">
            {{ scope.row.monitoringStatus === 'abnormal' ? '异常' : '正常' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="异常级别" align="center" prop="abnormalLevel" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.abnormalLevel" :type="getAbnormalLevelType(scope.row.abnormalLevel)">
            {{ getAbnormalLevelText(scope.row.abnormalLevel) }}
          </el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="监测时间" align="center" prop="monitoringTime" width="160">
        <template #default="scope">
          <span>{{ formatDate(scope.row.monitoringTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="处理状态" align="center" prop="isProcessed" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.isProcessed ? 'success' : 'info'">
            {{ scope.row.isProcessed ? '已处理' : '未处理' }}
          </el-tag>
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
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改健康监测对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="monitorFormRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="老人" prop="elderId">
          <el-select v-model="form.elderId" placeholder="请选择老人" style="width: 100%">
            <el-option v-for="elder in elderOptions" :key="elder.value" :label="elder.label" :value="elder.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="监测类型" prop="monitoringType">
          <el-select v-model="form.monitoringType" placeholder="请选择监测类型" @change="handleMonitorTypeChange">
            <el-option label="血压" value="1" />
            <el-option label="血糖" value="2" />
            <el-option label="心率" value="3" />
            <el-option label="体温" value="4" />
            <el-option label="体重" value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="监测值" prop="monitoringValue">
          <el-input 
            v-model="form.monitoringValue" 
            :placeholder="getMonitorValuePlaceholder()" 
            :suffix="getMonitorValueSuffix()"
            @input="handleMonitorValueInput"
          />
          <div class="form-tip" v-if="form.monitoringType">{{ getMonitorValueTip() }}</div>
          <div class="status-indicator" v-if="form.monitoringValue && form.monitoringStatus">
            <el-tag :type="form.monitoringStatus === 'abnormal' ? 'danger' : 'success'">
              {{ form.monitoringStatus === 'abnormal' ? '异常' : '正常' }}
            </el-tag>
          </div>
        </el-form-item>
        <el-form-item label="监测时间" prop="monitoringTime">
          <el-date-picker
            v-model="form.monitoringTime"
            type="datetime"
            placeholder="请选择监测时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="设备ID" prop="deviceId">
          <el-input v-model="form.deviceId" placeholder="请输入设备ID" />
        </el-form-item>
        <el-form-item label="异常描述" prop="abnormalDescription" v-if="form.monitoringStatus === 'abnormal'">
          <el-input
            v-model="form.abnormalDescription"
            type="textarea"
            placeholder="请输入异常描述"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="异常级别" prop="abnormalLevel" v-if="form.monitoringStatus === 'abnormal'">
          <el-select v-model="form.abnormalLevel" placeholder="请选择异常级别">
            <el-option label="轻度" value="low" />
            <el-option label="中度" value="medium" />
            <el-option label="重度" value="high" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理状态" prop="isProcessed">
          <el-switch v-model="form.isProcessed" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            placeholder="请输入备注"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 查看健康监测详情对话框 -->
    <el-dialog title="健康监测详情" v-model="viewOpen" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="监测ID">{{ form.id }}</el-descriptions-item>
        <el-descriptions-item label="老人名称">{{ form.elderName }}</el-descriptions-item>
        <el-descriptions-item label="老人年龄">{{ form.elderAge }}</el-descriptions-item>
        <el-descriptions-item label="老人性别">{{ form.elderGender }}</el-descriptions-item>
        <el-descriptions-item label="监测类型">
          <dict-tag :options="monitorTypeOptions" :value="form.monitoringType" />
        </el-descriptions-item>
        <el-descriptions-item label="监测值">
          {{ form.monitoringValue }} {{ form.monitoringUnit }}
        </el-descriptions-item>
        <el-descriptions-item label="监测状态">
          <el-tag :type="form.monitoringStatus === 'abnormal' ? 'danger' : 'success'">
            {{ form.monitoringStatus === 'abnormal' ? '异常' : '正常' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="监测时间">{{ formatDate(form.monitoringTime) }}</el-descriptions-item>
        <el-descriptions-item label="设备ID">{{ form.deviceId }}</el-descriptions-item>
        <el-descriptions-item label="处理状态">
          <el-tag :type="form.isProcessed ? 'success' : 'info'">
            {{ form.isProcessed ? '已处理' : '未处理' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item v-if="form.monitoringStatus !== 'normal'" label="异常级别">
          <el-tag v-if="form.abnormalLevel" :type="getAbnormalLevelType(form.abnormalLevel)">
            {{ getAbnormalLevelText(form.abnormalLevel) }}
          </el-tag>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item v-if="form.monitoringStatus !== 'normal'" label="异常描述" :span="2">
          {{ form.abnormalDescription || '-' }}
        </el-descriptions-item>
        <el-descriptions-item v-if="form.isProcessed" label="处理时间">
          {{ formatDate(form.processedTime) }}
        </el-descriptions-item>
        <el-descriptions-item v-if="form.isProcessed" label="处理人">
          {{ form.processedByName }}
        </el-descriptions-item>
        <el-descriptions-item v-if="form.isProcessed" label="处理结果" :span="2">
          {{ form.processedResult || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDate(form.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ formatDate(form.updatedAt) }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="viewOpen = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { getUserList } from '@/api/back/system/user';
import { useHealthMonitorStore } from '@/stores/back/health/healthMonitorStore';
import { ElMessage, ElMessageBox } from 'element-plus';
import { onMounted, ref } from 'vue';

const healthMonitorStore = useHealthMonitorStore();

// 遮罩层
const loading = ref(false);
// 选中数组
const ids = ref([]);
// 非单个禁用
const single = ref(true);
// 非多个禁用
const multiple = ref(true);
// 显示搜索条件
const showSearch = ref(true);
// 总条数
const total = ref(0);
// 健康监测表格数据
const monitorList = ref([]);
// 弹出层标题
const title = ref("");
// 是否显示弹出层
const open = ref(false);
// 是否显示详情弹出层
const viewOpen = ref(false);
// 日期范围
const dateRange = ref([]);
// 老人选项
const elderOptions = ref([]);

// 监测类型数据字典
const monitorTypeOptions = [
  { value: "1", label: "血压" },
  { value: "2", label: "血糖" },
  { value: "3", label: "心率" },
  { value: "4", label: "体温" },
  { value: "5", label: "体重" }
];

// 状态数据字典
const statusOptions = [
  { value: "正常", label: "正常", type: "success" },
  { value: "异常", label: "异常", type: "danger" }
];

// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  elderName: undefined,
  monitoringType: undefined,
  monitoringStatus: undefined,
  isProcessed: undefined,
  beginTime: undefined,
  endTime: undefined
});

// 表单参数
const form = ref({
  id: undefined,
  elderId: undefined,
  elderName: undefined,
  elderAge: undefined,
  elderGender: undefined,
  monitoringType: undefined,
  monitoringValue: undefined,
  monitoringUnit: undefined,
  monitoringStatus: undefined,
  abnormalLevel: undefined,
  monitoringTime: undefined,
  isProcessed: undefined,
  remark: undefined,
  deviceId: undefined,
  abnormalDescription: undefined,
  processedTime: undefined,
  processedByName: undefined,
  processedResult: undefined,
  createdAt: undefined,
  updatedAt: undefined
});

// 表单校验
const rules = {
  elderId: [
    { required: true, message: "老人不能为空", trigger: "blur" }
  ],
  monitoringType: [
    { required: true, message: "监测类型不能为空", trigger: "change" }
  ],
  monitoringValue: [
    { required: true, message: "监测值不能为空", trigger: "blur" },
    { validator: validateMonitorValue, trigger: "blur" }
  ],
  monitoringTime: [
    { required: true, message: "监测时间不能为空", trigger: "change" }
  ]
};

// 表单引用
const monitorFormRef = ref(null);
// 查询表单引用
const queryFormRef = ref(null);

// 监测值验证
function validateMonitorValue(rule, value, callback) {
  if (!value) {
    callback(new Error('监测值不能为空'));
    return;
  }
  
  try {
    // 根据监测类型验证并设置状态
    const isAbnormal = checkAbnormalValue(form.value.monitoringType, value);
    form.value.monitoringStatus = isAbnormal ? "abnormal" : "normal"; // 异常或正常
    
    callback();
  } catch (error) {
    callback(new Error(error.message));
  }
}

// 检查监测值是否异常
function checkAbnormalValue(type, value) {
  if (!type || !value) return false;
  
  const numValue = parseFloat(value);
  if (isNaN(numValue) && type !== '1') {
    throw new Error('监测值必须是数字');
  }
  
  switch (type) {
    case '1': // 血压
      // 血压格式应为 "收缩压/舒张压"，例如 "120/80"
      if (!/^\d+\/\d+$/.test(value)) {
        throw new Error('血压格式应为"收缩压/舒张压"，例如"120/80"');
      }
      const [systolic, diastolic] = value.split('/').map(Number);
      if (systolic < 60 || systolic > 200) {
        throw new Error('收缩压应在60-200之间');
      }
      if (diastolic < 40 || diastolic > 120) {
        throw new Error('舒张压应在40-120之间');
      }
      // 判断血压是否异常
      return (systolic > 140 || systolic < 90 || diastolic > 90 || diastolic < 60);
      
    case '2': // 血糖
      if (numValue < 1 || numValue > 30) {
        throw new Error('血糖值应在1-30 mmol/L之间');
      }
      // 判断血糖是否异常
      return (numValue > 7.0 || numValue < 3.9);
      
    case '3': // 心率
      if (numValue < 40 || numValue > 200) {
        throw new Error('心率应在40-200次/分钟之间');
      }
      // 判断心率是否异常
      return (numValue > 100 || numValue < 60);
      
    case '4': // 体温
      if (numValue < 35 || numValue > 42) {
        throw new Error('体温应在35-42°C之间');
      }
      // 判断体温是否异常
      return (numValue > 37.3 || numValue < 36.0);
      
    case '5': // 体重
      if (numValue < 20 || numValue > 200) {
        throw new Error('体重应在20-200kg之间');
      }
      // 体重没有明确的异常标准，可以根据BMI计算
      // 这里简单处理，暂不判断异常
      return false;
      
    default:
      return false;
  }
}

// 获取异常级别类型
function getAbnormalLevelType(level) {
  switch (level) {
    case 'low': return 'info';
    case 'medium': return 'warning';
    case 'high': return 'danger';
    default: return 'info';
  }
}

// 获取异常级别文本
function getAbnormalLevelText(level) {
  switch (level) {
    case 'low': return '轻度';
    case 'medium': return '中度';
    case 'high': return '重度';
    default: return '未知';
  }
}

// 获取监测值提示
function getMonitorValueTip() {
  switch (form.value.monitoringType) {
    case '1': return '请输入收缩压/舒张压，例如 120/80';
    case '2': return '请输入血糖值，单位 mmol/L';
    case '3': return '请输入心率，单位 次/分钟';
    case '4': return '请输入体温，单位 °C';
    case '5': return '请输入体重，单位 kg';
    default: return '';
  }
}

// 获取监测值占位符
function getMonitorValuePlaceholder() {
  switch (form.value.monitoringType) {
    case '1': return '请输入收缩压/舒张压';
    case '2': return '请输入血糖值';
    case '3': return '请输入心率';
    case '4': return '请输入体温';
    case '5': return '请输入体重';
    default: return '请输入监测值';
  }
}

// 获取监测值后缀
function getMonitorValueSuffix() {
  switch (form.value.monitoringType) {
    case '1': return 'mmHg';
    case '2': return 'mmol/L';
    case '3': return '次/分钟';
    case '4': return '°C';
    case '5': return 'kg';
    default: return '';
  }
}

// 监测类型改变事件处理
function handleMonitorTypeChange(val) {
  // 清空监测值
  form.value.monitoringValue = '';
  // 如果表单已经创建，重置监测值字段的验证
  if (monitorFormRef.value) {
    monitorFormRef.value.clearValidate('monitoringValue');
  }
}

// 监测值输入事件处理
function handleMonitorValueInput() {
  // 如果表单已经创建，重置监测值字段的验证
  if (monitorFormRef.value) {
    monitorFormRef.value.clearValidate('monitoringValue');
  }
  
  // 如果没有选择监测类型或没有输入监测值，不进行验证
  if (!form.value.monitoringType || !form.value.monitoringValue) {
    return;
  }
  
  try {
    // 根据监测类型验证并设置状态
    const isAbnormal = checkAbnormalValue(form.value.monitoringType, form.value.monitoringValue);
    form.value.monitoringStatus = isAbnormal ? "abnormal" : "normal"; // 异常或正常
  } catch (error) {
    // 验证失败时不设置状态
    console.error('监测值验证失败:', error.message);
  }
}

// 格式化日期
function formatDate(dateStr) {
  if (!dateStr) return '';
  return dateStr.replace('T', ' ').substring(0, 19);
}

/** 查询健康监测列表 */
async function getList() {
  loading.value = true;
  try {
    // 处理日期范围
    if (dateRange.value && dateRange.value.length === 2) {
      queryParams.value.beginTime = dateRange.value[0];
      queryParams.value.endTime = dateRange.value[1];
    } else {
      queryParams.value.beginTime = undefined;
      queryParams.value.endTime = undefined;
    }
    
    const result = await healthMonitorStore.getMonitorList(queryParams.value);
    monitorList.value = result.records;
    total.value = result.total;
  } catch (error) {
    console.error("获取健康监测列表失败:", error);
    ElMessage.error("获取健康监测列表失败");
  } finally {
    loading.value = false;
  }
}

/** 查询老人列表 */
async function getElders() {
  try {
    const response = await getUserList({ pageSize: 100 });
    if (response.code === 200) {
      elderOptions.value = response.data.records.map(item => ({
        value: item.userId,
        label: item.userName
      }));
    }
  } catch (error) {
    console.error("获取老人列表失败:", error);
  }
}

/** 格式化监测值 */
function formatMonitorValue(row) {
  if (!row || !row.monitoringValue) return '';
  
  switch (row.monitoringType) {
    case '1': return row.monitoringValue + ' mmHg';
    case '2': return row.monitoringValue + ' mmol/L';
    case '3': return row.monitoringValue + ' 次/分';
    case '4': return row.monitoringValue + ' °C';
    case '5': return row.monitoringValue + ' kg';
    default: return row.monitoringValue;
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
    elderId: undefined,
    elderName: undefined,
    elderAge: undefined,
    elderGender: undefined,
    monitoringType: undefined,
    monitoringValue: undefined,
    monitoringUnit: undefined,
    monitoringStatus: undefined,
    abnormalLevel: undefined,
    monitoringTime: undefined,
    isProcessed: undefined,
    remark: undefined,
    deviceId: undefined,
    abnormalDescription: undefined,
    processedTime: undefined,
    processedByName: undefined,
    processedResult: undefined,
    createdAt: undefined,
    updatedAt: undefined
  };
  monitorFormRef.value?.resetFields();
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  queryFormRef.value?.resetFields();
  handleQuery();
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加健康监测";
}

/** 修改按钮操作 */
async function handleUpdate(row) {
  reset();
  const id = row.id || ids.value[0];
  try {
    const response = await healthMonitorStore.getMonitorDetail(id);
    if (response) {
      form.value = response;
      open.value = true;
      title.value = "修改健康监测";
    }
  } catch (error) {
    console.error("获取健康监测详情失败:", error);
    ElMessage.error("获取健康监测详情失败");
  }
}

/** 查看按钮操作 */
async function handleView(row) {
  reset();
  const id = row.id || ids.value[0];
  try {
    const response = await healthMonitorStore.getMonitorDetail(id);
    if (response) {
      form.value = response;
      viewOpen.value = true;
    }
  } catch (error) {
    console.error("获取健康监测详情失败:", error);
    ElMessage.error("获取健康监测详情失败");
  }
}

/** 提交按钮 */
function submitForm() {
  monitorFormRef.value?.validate(async valid => {
    if (valid) {
      try {
        if (form.value.id) {
          await healthMonitorStore.updateMonitor(form.value);
          ElMessage.success("修改成功");
        } else {
          await healthMonitorStore.addMonitor(form.value);
          ElMessage.success("新增成功");
        }
        open.value = false;
        getList();
      } catch (error) {
        console.error("保存健康监测失败:", error);
        ElMessage.error("保存健康监测失败");
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const ids = row.id || ids.value;
  ElMessageBox.confirm(`是否确认删除健康监测编号为"${ids}"的数据项?`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(async function() {
    try {
      await healthMonitorStore.deleteMonitor(ids);
      getList();
      ElMessage.success("删除成功");
    } catch (error) {
      console.error("删除健康监测失败:", error);
      ElMessage.error("删除失败");
    }
  });
}

/** 导出按钮操作 */
async function handleExport() {
  try {
    // 构建导出参数
    const exportParams = { ...queryParams.value };
    // 处理日期范围
    if (dateRange.value && dateRange.value.length === 2) {
      exportParams.beginTime = dateRange.value[0];
      exportParams.endTime = dateRange.value[1];
    }
    // 删除分页参数
    delete exportParams.pageNum;
    delete exportParams.pageSize;
    
    ElMessageBox.confirm('是否确认导出所有健康监测数据?', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      try {
        const response = await healthMonitorStore.exportMonitors(exportParams);
        
        // 创建下载链接
        const link = document.createElement('a');
        link.href = URL.createObjectURL(response);
        link.download = `健康监测数据_${new Date().getTime()}.xlsx`;
        link.click();
        URL.revokeObjectURL(link.href);
        
        ElMessage.success('导出成功');
      } catch (error) {
        console.error('导出健康监测数据失败:', error);
        ElMessage.error('导出健康监测数据失败');
      }
    });
  } catch (error) {
    console.error('导出健康监测数据失败:', error);
    ElMessage.error('导出健康监测数据失败');
  }
}

onMounted(() => {
  getList();
  getElders();
});
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.mb8 {
  margin-bottom: 8px;
}

.form-tip {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.status-indicator {
  font-size: 12px;
  margin-top: 5px;
}
</style>
