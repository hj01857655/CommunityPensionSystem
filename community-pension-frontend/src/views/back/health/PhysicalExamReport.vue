<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="80px" class="search-form">
      <el-form-item label="用户名称">
        <el-input
          v-model="queryParams.elderName"
          placeholder="请输入用户名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="体检医院">
        <el-input
          v-model="queryParams.hospital"
          placeholder="请输入体检医院"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="体检时间">
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
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="reportList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="报告编号" align="center" prop="reportNo" />
      <el-table-column label="用户姓名" align="center" prop="elderName" :show-overflow-tooltip="true" />
      <el-table-column label="性别" align="center" prop="gender" width="60" />
      <el-table-column label="年龄" align="center" prop="age" width="60" />
      <el-table-column label="体检医院" align="center" prop="hospital" :show-overflow-tooltip="true" />
      <el-table-column label="体检日期" align="center" prop="date" width="120">
        <template #default="scope">
          <span>{{ formatDate(scope.row.date) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="主要结论" align="center" prop="mainResult" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link icon="View" @click="handleView(scope.row)">查看</el-button>
          <el-button link icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button link icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
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

    <!-- 添加或修改体检报告对话框 -->
    <el-dialog :title="title" v-model="open" width="80%" append-to-body>
      <el-form ref="reportFormRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="报告编号">
          <el-input v-model="form.reportNo" placeholder="自动生成" disabled />
        </el-form-item>
        <el-form-item label="用户" prop="elderId">
          <el-select v-model="form.elderId" placeholder="请选择用户" filterable @change="onUserChange">
            <el-option
              v-for="item in userOptions"
              :key="item.userId"
              :label="item.name"
              :value="item.userId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="性别">
          <el-input v-model="form.gender" placeholder="自动带出" disabled />
        </el-form-item>
        <el-form-item label="年龄">
          <el-input v-model="form.age" placeholder="自动带出" disabled />
        </el-form-item>
        <el-form-item label="体检日期" prop="date">
          <el-date-picker
            v-model="form.date"
            type="date"
            placeholder="选择体检日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="体检医院" prop="hospital">
          <el-input v-model="form.hospital" placeholder="请输入体检医院" />
        </el-form-item>
        <el-form-item label="主要结论" prop="mainResult">
          <el-input v-model="form.mainResult" type="textarea" placeholder="请输入主要结论" />
        </el-form-item>
        <el-form-item label="报告文件">
          <el-input v-model="form.fileUrl" placeholder="上传后自动填充" disabled />
        </el-form-item>
      </el-form>
      <!-- 体检项目编辑表格 -->
      <el-table :data="form.items" style="width:100%;margin-top:16px;">
        <el-table-column prop="itemName" label="项目名称" width="150">
          <template #default="scope">
            <el-select v-model="scope.row.itemName" placeholder="选择项目" filterable @change="(val) => handleItemSelect(val, scope.$index)">
              <el-option-group v-for="(items, category) in examItemsByCategory" :key="category" :label="category">
                <el-option v-for="item in items" :key="item.itemName" :label="item.itemName" :value="item.itemName" />
              </el-option-group>
            </el-select>
          </template>
        </el-table-column>
        <el-table-column prop="itemValue" label="检查结果" width="100">
          <template #default="scope">
            <el-input v-model="scope.row.itemValue" placeholder="检查结果" @input="(val) => checkAbnormalValue(val, scope.$index)" />
          </template>
        </el-table-column>
        <el-table-column prop="itemUnit" label="单位" width="80">
          <template #default="scope">
            <el-input v-model="scope.row.itemUnit" placeholder="单位" />
          </template>
        </el-table-column>
        <el-table-column prop="normalRange" label="参考范围" width="120">
          <template #default="scope">
            <el-input v-model="scope.row.normalRange" placeholder="参考范围" />
          </template>
        </el-table-column>
        <el-table-column prop="abnormalFlag" label="是否异常">
          <template #default="scope">
            <el-select v-model="scope.row.abnormalFlag" placeholder="是否异常">
              <el-option :value="0" label="正常" />
              <el-option :value="1" label="异常" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="100">
          <template #default="scope">
            <el-input v-model="scope.row.category" placeholder="分类" />
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" width="120">
          <template #default="scope">
            <el-input v-model="scope.row.remark" placeholder="备注" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="60">
          <template #default="scope">
            <el-button @click="removeItem(scope.$index)" type="danger" size="small">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-button type="primary" size="small" style="margin-top:8px;" @click="addItem">添加项目</el-button>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 查看体检报告详情对话框 -->
    <el-dialog title="体检报告详情" v-model="viewOpen" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="报告编号">{{ form.reportNo }}</el-descriptions-item>
        <el-descriptions-item label="用户姓名">{{ form.elderName }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ form.gender }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ form.age }}</el-descriptions-item>
        <el-descriptions-item label="体检医院">{{ form.hospital }}</el-descriptions-item>
        <el-descriptions-item label="体检日期">{{ formatDate(form.date) }}</el-descriptions-item>
        <el-descriptions-item label="主要结论" :span="2">{{ form.mainResult }}</el-descriptions-item>
        <el-descriptions-item label="报告文件" :span="2">
          <a v-if="form.fileUrl" :href="form.fileUrl" target="_blank">下载报告</a>
          <span v-else>无</span>
        </el-descriptions-item>
      </el-descriptions>
      <!-- 体检项目明细展示表格 -->
      <el-table :data="form.items" style="width:100%;margin-top:16px;">
        <el-table-column prop="itemName" label="项目名称" />
        <el-table-column prop="itemValue" label="检查结果" />
        <el-table-column prop="itemUnit" label="单位" />
        <el-table-column prop="normalRange" label="参考范围" />
        <el-table-column prop="abnormalFlag" label="是否异常">
          <template #default="scope">
            <span v-if="scope.row.abnormalFlag==1" style="color:red">异常</span>
            <span v-else>正常</span>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" />
        <el-table-column prop="remark" label="备注" />
      </el-table>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="viewOpen = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getUserList } from '@/api/back/system/user';
import { usePhysicalExamReportStore } from '@/stores/back/health/physicalExamReportStore';
import { formatDate } from '@/utils/date';

// 初始化store
const physicalExamReportStore = usePhysicalExamReportStore();

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
// 体检报告表格数据
const reportList = ref([]);
// 弹出层标题
const title = ref("");
// 是否显示弹出层
const open = ref(false);
// 是否显示详情弹出层
const viewOpen = ref(false);
// 日期范围
const dateRange = ref([]);
// 用户选项
const userOptions = ref([]);

// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  elderName: undefined,
  hospital: undefined,
  beginTime: undefined,
  endTime: undefined
});

// 表单参数
const form = ref({
  id: undefined,
  elderId: undefined,
  elderName: undefined,
  gender: undefined,
  age: undefined,
  date: undefined,
  hospital: undefined,
  mainResult: undefined,
  fileUrl: undefined,
  reportNo: undefined,
  items: [] // 体检项目明细
});

// 表单校验
const rules = {
  elderId: [
    { required: true, message: "用户不能为空", trigger: "blur" }
  ],
  hospital: [
    { required: true, message: "体检医院不能为空", trigger: "blur" }
  ],
  date: [
    { required: true, message: "体检日期不能为空", trigger: "change" }
  ],
  mainResult: [
    { required: true, message: "主要结论不能为空", trigger: "blur" }
  ]
};

// 表单引用
const reportFormRef = ref(null);
// 查询表单引用
const queryFormRef = ref(null);

// 按分类整理项目
const examItemsByCategory = computed(() => {
  const result = {};
  const items = physicalExamReportStore.examItems;
  
  items.forEach(item => {
    if (!result[item.category]) {
      result[item.category] = [];
    }
    result[item.category].push(item);
  });
  return result;
});

/** 查询体检报告列表 */
async function getList() {
  loading.value = true;
  try {
    const params = {
      ...queryParams.value,
      beginTime: dateRange.value ? dateRange.value[0] : undefined,
      endTime: dateRange.value ? dateRange.value[1] : undefined
    };
    const data = await physicalExamReportStore.getReportList(params);
    reportList.value = data.records || [];
    total.value = data.total || 0;
  } catch (error) {
    console.error('获取体检报告列表失败:', error);
    ElMessage.error('获取体检报告列表失败');
  } finally {
    loading.value = false;
  }
}

// 查询用户列表
async function getUsers() {
  try {
    const response = await getUserList({ pageSize: 100 });
    if (response && response.data && response.data.records) {
      // 只筛选角色为"老人"的用户
      const elderUsers = response.data.records.filter(user => 
        user.roles && user.roles.includes('elder')
      );
      
      userOptions.value = elderUsers.map(user => ({
        value: user.userId,
        label: user.name,
        ...user
      }));
    } else {
      console.warn('获取用户列表返回数据格式不正确');
    }
  } catch (error) {
    console.error('获取用户列表失败:', error);
    ElMessage.error('获取用户列表失败');
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
    reportNo: '',
    elderId: undefined,
    elderName: '',
    gender: '',
    age: '',
    hospital: '',
    date: new Date().toISOString().substr(0, 10),
    mainResult: '',
    items: []
  };
  reportFormRef.value?.resetFields();
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  queryFormRef.value?.resetFields();
  dateRange.value = [];
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
  title.value = "添加体检报告";
  // 使用从store获取的体检项目
  const items = physicalExamReportStore.getAvailableExamItems();
  form.value.items = items.map(item => ({ 
    ...item, 
    itemValue: '', 
    abnormalFlag: 0, 
    remark: '' 
  }));
}

/** 修改按钮操作 */
async function handleUpdate(row) {
  reset();
  const reportId = row.id || ids.value[0];
  try {
    const response = await physicalExamReportStore.getReportDetail(reportId);
    if (response && response.report) {
      form.value = { ...form.value, ...response.report, items: [] };
      if (response.itemsByCategory) {
        form.value.items = Object.values(response.itemsByCategory).flat();
      }
      open.value = true;
      title.value = "修改体检报告";
    }
  } catch (error) {
    console.error("获取体检报告详情失败:", error);
    ElMessage.error("获取体检报告详情失败");
  }
}

/** 查看按钮操作 */
async function handleView(row) {
  reset();
  const reportId = row.id || ids.value[0];
  try {
    const response = await physicalExamReportStore.getReportDetail(reportId);
    if (response && response.report) {
      form.value = { ...form.value, ...response.report, items: [] };
      if (response.itemsByCategory) {
        form.value.items = Object.values(response.itemsByCategory).flat();
      }
      viewOpen.value = true;
    }
  } catch (error) {
    console.error("获取体检报告详情失败:", error);
    ElMessage.error("获取体检报告详情失败");
  }
}

/** 提交按钮 */
function submitForm() {
  reportFormRef.value?.validate(async valid => {
    if (valid) {
      try {
        if (form.value.id) {
          await physicalExamReportStore.updateReport(form.value);
          ElMessage.success("修改成功");
        } else {
          await physicalExamReportStore.addReport(form.value);
          ElMessage.success("新增成功");
        }
        open.value = false;
        getList();
      } catch (error) {
        console.error("保存体检报告失败:", error);
        ElMessage.error("保存体检报告失败");
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const reportIds = row.id || ids.value;
  ElMessageBox.confirm(`是否确认删除体检报告编号为"${reportIds}"的数据项?`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(async function() {
    try {
      await physicalExamReportStore.deleteReport(reportIds);
      getList();
      ElMessage.success("删除成功");
    } catch (error) {
      console.error("删除体检报告失败:", error);
      ElMessage.error("删除失败");
    }
  });
}

function onUserChange(userId) {
  const user = userOptions.value.find(u => u.userId === userId);
  if (user) {
    form.value.elderName = user.name;
    form.value.gender = user.gender;
    form.value.age = user.age;
  }
}

function addItem() {
  form.value.items.push({
    itemName: '',
    itemValue: '',
    itemUnit: '',
    abnormalFlag: 0,
    category: '',
    remark: '',
    normalRange: ''
  });
}

function removeItem(index) {
  form.value.items.splice(index, 1);
}

function handleItemSelect(itemName, index) {
  // 检查是否已经存在相同的项目
  const isDuplicate = form.value.items.some((item, idx) => 
    item.itemName === itemName && idx !== index
  );
  
  if (isDuplicate) {
    ElMessage.warning(`项目"${itemName}"已存在，请勿重复添加`);
    form.value.items[index].itemName = ''; // 清空当前选择
    return;
  }
  
  const items = physicalExamReportStore.getAvailableExamItems();
  const selectedItem = items.find(item => item.itemName === itemName);
  if (selectedItem) {
    form.value.items[index] = {
      ...form.value.items[index],
      itemUnit: selectedItem.itemUnit,
      normalRange: selectedItem.normalRange,
      category: selectedItem.category
    };
  }
}

function checkAbnormalValue(value, index) {
  const item = form.value.items[index];
  
  // 如果没有参考范围或者值为空，不做处理
  if (!item.normalRange || !value) {
    return;
  }
  
  // 尝试将值转换为数字
  const numValue = parseFloat(value);
  if (isNaN(numValue)) {
    return; // 如果不是数字，不做处理
  }
  
  // 解析参考范围
  // 支持几种常见的参考范围格式：
  // 1. "3.9-6.1" 形式
  // 2. "<6.1" 或 ">3.9" 形式
  // 3. "≤6.1" 或 "≥3.9" 形式
  
  let isAbnormal = false;
  
  if (item.normalRange.includes('-')) {
    // 处理范围格式 "3.9-6.1"
    const [min, max] = item.normalRange.split('-').map(v => parseFloat(v));
    if (!isNaN(min) && !isNaN(max)) {
      isAbnormal = numValue < min || numValue > max;
    }
  } else if (item.normalRange.startsWith('<')) {
    // 处理小于格式 "<6.1"
    const max = parseFloat(item.normalRange.substring(1));
    if (!isNaN(max)) {
      isAbnormal = numValue >= max;
    }
  } else if (item.normalRange.startsWith('≤')) {
    // 处理小于等于格式 "≤6.1"
    const max = parseFloat(item.normalRange.substring(1));
    if (!isNaN(max)) {
      isAbnormal = numValue > max;
    }
  } else if (item.normalRange.startsWith('>')) {
    // 处理大于格式 ">3.9"
    const min = parseFloat(item.normalRange.substring(1));
    if (!isNaN(min)) {
      isAbnormal = numValue <= min;
    }
  } else if (item.normalRange.startsWith('≥')) {
    // 处理大于等于格式 "≥3.9"
    const min = parseFloat(item.normalRange.substring(1));
    if (!isNaN(min)) {
      isAbnormal = numValue < min;
    }
  }
  
  // 如果值异常，设置异常标志
  if (isAbnormal) {
    item.abnormalFlag = 1; // 异常
  } else {
    item.abnormalFlag = 0; // 正常
  }
}

onMounted(() => {
  getList();
  getUsers();
  physicalExamReportStore.getExamItems(); // 通过store获取体检项目列表
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
</style>
