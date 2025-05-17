<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="80px" class="search-form">
      <el-form-item label="老人姓名">
        <el-input
          v-model="queryParams.elderName"
          placeholder="请输入老人姓名"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="记录类型">
        <el-select v-model="queryParams.recordType" placeholder="请选择记录类型" clearable style="width: 200px">
          <el-option label="初始记录" value="初始记录" />
          <el-option label="定期检查" value="定期检查" />
          <el-option label="随访记录" value="随访记录" />
          <el-option label="紧急记录" value="紧急记录" />
        </el-select>
      </el-form-item>
      <el-form-item label="记录时间">
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

    <el-table v-loading="loading" :data="recordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="记录ID" align="center" prop="id" width="80" />
      <el-table-column label="老人姓名" align="center" prop="elderName" width="100" :show-overflow-tooltip="true" />
      <el-table-column label="年龄" align="center" prop="elderAge" width="60" />
      <el-table-column label="性别" align="center" prop="elderGender" width="60" />
      <el-table-column label="血压" align="center" prop="bloodPressure" width="80" />
      <el-table-column label="心率" align="center" prop="heartRate" width="60" />
      <el-table-column label="血糖" align="center" prop="bloodSugar" width="60" />
      <el-table-column label="体温" align="center" prop="temperature" width="60" />
      <el-table-column label="记录类型" align="center" prop="recordType" width="100" />
      <el-table-column label="症状" align="center" prop="symptoms" :show-overflow-tooltip="true" />
      <el-table-column label="记录时间" align="center" prop="recordTime" width="160">
        <template #default="scope">
          <span>{{ formatDate(scope.row.recordTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
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

    <!-- 添加或修改健康记录对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="recordFormRef" :model="form" :rules="rules" label-width="100px">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="基本信息" name="basic">
            <el-row>
              <el-col :span="12">
                <el-form-item label="老人" prop="elderId">
                  <el-select v-model="form.elderId" placeholder="请选择老人" filterable @change="handleElderChange" style="width: 100%">
                    <el-option
                      v-for="item in elderOptions"
                      :key="item.userId || ''"
                      :label="item.name || ''"
                      :value="item.userId || ''"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="记录类型" prop="recordType">
                  <el-select v-model="form.recordType" placeholder="请选择记录类型" style="width: 100%">
                    <el-option label="初始记录" value="初始记录" />
                    <el-option label="定期检查" value="定期检查" />
                    <el-option label="随访记录" value="随访记录" />
                    <el-option label="紧急记录" value="紧急记录" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="8">
                <el-form-item label="老人姓名" prop="elderName">
                  <el-input v-model="form.elderName" placeholder="老人姓名" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="年龄" prop="elderAge">
                  <el-input v-model="form.elderAge" placeholder="年龄" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="性别" prop="elderGender">
                  <el-input v-model="form.elderGender" placeholder="性别" disabled />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item label="记录时间" prop="recordTime">
                  <el-date-picker
                    v-model="form.recordTime"
                    type="datetime"
                    placeholder="选择记录时间"
                    value-format="YYYY-MM-DD HH:mm:ss"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="记录人" prop="recorderName">
                  <el-input v-model="form.recorderName" placeholder="记录人姓名" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>
          <el-tab-pane label="健康指标" name="indicators">
            <el-row>
              <el-col :span="12">
                <el-form-item label="血压" prop="bloodPressure">
                  <el-input v-model="form.bloodPressure" placeholder="请输入血压(如120/80)">
                    <template #append>mmHg</template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="心率" prop="heartRate">
                  <el-input-number v-model="form.heartRate" :min="0" :max="200" placeholder="请输入心率" style="width: 100%">
                    <template #append>次/分</template>
                  </el-input-number>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item label="血糖" prop="bloodSugar">
                  <el-input-number v-model="form.bloodSugar" :min="0" :max="30" :precision="1" :step="0.1" placeholder="请输入血糖" style="width: 100%">
                    <template #append>mmol/L</template>
                  </el-input-number>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="体温" prop="temperature">
                  <el-input-number v-model="form.temperature" :min="30" :max="45" :precision="1" :step="0.1" placeholder="请输入体温" style="width: 100%">
                    <template #append>°C</template>
                  </el-input-number>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item label="体重" prop="weight">
                  <el-input-number v-model="form.weight" :min="0" :max="200" :precision="1" :step="0.1" placeholder="请输入体重" @change="calculateBMI" style="width: 100%">
                    <template #append>kg</template>
                  </el-input-number>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="身高" prop="height">
                  <el-input-number v-model="form.height" :min="0" :max="250" :precision="1" :step="0.1" placeholder="请输入身高" @change="calculateBMI" style="width: 100%">
                    <template #append>cm</template>
                  </el-input-number>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row v-if="form.bmi">
              <el-col :span="24">
                <el-form-item label="BMI" prop="bmi">
                  <el-input v-model="form.bmi" disabled>
                    <template #append>
                      <span :class="getBMIClass">{{ getBMIStatus }}</span>
                    </template>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>
          <el-tab-pane label="病史与症状" name="symptoms">
            <el-form-item label="既往病史" prop="medicalHistory">
              <el-input v-model="form.medicalHistory" type="textarea" :rows="2" placeholder="请输入既往病史" />
            </el-form-item>
            <el-form-item label="过敏史" prop="allergy">
              <el-input v-model="form.allergy" type="textarea" :rows="2" placeholder="请输入过敏史" />
            </el-form-item>
            <el-form-item label="当前症状" prop="symptoms">
              <el-input v-model="form.symptoms" type="textarea" :rows="3" placeholder="请输入当前症状" />
            </el-form-item>
            <el-form-item label="用药情况" prop="medication">
              <el-input v-model="form.medication" type="textarea" :rows="3" placeholder="请输入用药情况" />
            </el-form-item>
            <el-form-item label="备注" prop="remarks">
              <el-input v-model="form.remarks" type="textarea" :rows="2" placeholder="请输入备注" />
            </el-form-item>
          </el-tab-pane>
        </el-tabs>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 查看健康记录详情对话框 -->
    <el-dialog title="健康记录详情" v-model="viewOpen" width="800px" append-to-body>
      <el-tabs v-model="viewActiveTab">
        <el-tab-pane label="基本信息" name="viewBasic">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="记录ID">{{ form.id }}</el-descriptions-item>
            <el-descriptions-item label="老人姓名">{{ form.elderName }}</el-descriptions-item>
            <el-descriptions-item label="年龄">{{ form.elderAge }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ form.elderGender }}</el-descriptions-item>
            <el-descriptions-item label="记录类型">{{ form.recordType }}</el-descriptions-item>
            <el-descriptions-item label="记录时间">{{ formatDate(form.recordTime) }}</el-descriptions-item>
            <el-descriptions-item label="记录人">{{ form.recorderName }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        <el-tab-pane label="健康指标" name="viewIndicators">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="血压">{{ form.bloodPressure }} mmHg</el-descriptions-item>
            <el-descriptions-item label="心率">{{ form.heartRate }} 次/分</el-descriptions-item>
            <el-descriptions-item label="血糖">{{ form.bloodSugar }} mmol/L</el-descriptions-item>
            <el-descriptions-item label="体温">{{ form.temperature }} °C</el-descriptions-item>
            <el-descriptions-item label="体重">{{ form.weight }} kg</el-descriptions-item>
            <el-descriptions-item label="身高">{{ form.height }} cm</el-descriptions-item>
            <el-descriptions-item label="BMI" v-if="form.bmi">
              {{ form.bmi }} <span :class="getBMIClass">{{ getBMIStatus }}</span>
            </el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        <el-tab-pane label="病史与症状" name="viewSymptoms">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="既往病史">{{ form.medicalHistory || '无' }}</el-descriptions-item>
            <el-descriptions-item label="过敏史">{{ form.allergy || '无' }}</el-descriptions-item>
            <el-descriptions-item label="当前症状">{{ form.symptoms || '无' }}</el-descriptions-item>
            <el-descriptions-item label="用药情况">{{ form.medication || '无' }}</el-descriptions-item>
            <el-descriptions-item label="备注">{{ form.remarks || '无' }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
      </el-tabs>
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
import { useHealthRecordStore } from '@/stores/back/health/healthRecordStore';
import { formatDate } from '@/utils/date';
import { ElMessage, ElMessageBox } from 'element-plus';
import { computed, onMounted, ref } from 'vue';

const healthRecordStore = useHealthRecordStore();

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
// 健康记录表格数据
const recordList = ref([]);
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
// 当前激活的表单标签页
const activeTab = ref('basic');
// 当前激活的查看标签页
const viewActiveTab = ref('viewBasic');

// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  elderName: undefined,
  recordType: undefined,
  beginTime: undefined,
  endTime: undefined
});

// 表单参数
const form = ref({
  id: undefined,
  elderId: '',
  elderName: '',
  elderAge: '',
  elderGender: '',
  bloodPressure: '',
  heartRate: '',
  bloodSugar: '',
  temperature: '',
  weight: '',
  height: '',
  bmi: '',
  medicalHistory: '',
  allergy: '',
  symptoms: '',
  medication: '',
  recordTime: '',
  recorderId: '',
  recorderName: '',
  recordType: '',
  remarks: ''
});

// 表单校验规则
const rules = ref({
  elderId: [{ required: true, message: "老人不能为空", trigger: "change" }],
  recordType: [{ required: true, message: "记录类型不能为空", trigger: "change" }],
  bloodPressure: [
    { required: true, message: "血压不能为空", trigger: "blur" },
    { pattern: /^\d{2,3}\/\d{2,3}$/, message: "血压格式应为收缩压/舒张压，如120/80", trigger: "blur" }
  ],
  heartRate: [
    { required: true, message: "心率不能为空", trigger: "blur" },
    { type: 'number', min: 40, max: 180, message: "心率应在40-180次/分之间", trigger: "blur" }
  ],
  bloodSugar: [
    { required: true, message: "血糖不能为空", trigger: "blur" },
    { type: 'number', min: 2, max: 20, message: "血糖应在2-20mmol/L之间", trigger: "blur" }
  ],
  temperature: [
    { required: true, message: "体温不能为空", trigger: "blur" },
    { type: 'number', min: 35, max: 42, message: "体温应在35-42°C之间", trigger: "blur" }
  ],
  weight: [
    { type: 'number', min: 30, max: 150, message: "体重应在30-150kg之间", trigger: "blur" }
  ],
  height: [
    { type: 'number', min: 100, max: 220, message: "身高应在100-220cm之间", trigger: "blur" }
  ],
  recordTime: [{ required: true, message: "记录时间不能为空", trigger: "blur" }],
  recorderName: [{ required: true, message: "记录人不能为空", trigger: "blur" }]
});

// 表单引用
const recordFormRef = ref(null);

// 计算BMI值
const calculateBMI = () => {
  if (form.value.weight && form.value.height) {
    form.value.bmi = (form.value.weight / Math.pow(form.value.height / 100, 2)).toFixed(1);
  } else {
    form.value.bmi = undefined;
  }
};

// 获取BMI状态
const getBMIStatus = computed(() => {
  const bmi = parseFloat(form.value.bmi);
  if (!bmi) return '';
  if (bmi < 18.5) return '偏瘦';
  if (bmi < 24) return '正常';
  if (bmi < 28) return '超重';
  return '肥胖';
});

// 获取BMI状态对应的CSS类
const getBMIClass = computed(() => {
  const bmi = parseFloat(form.value.bmi);
  if (!bmi) return '';
  if (bmi < 18.5) return 'text-warning';
  if (bmi < 24) return 'text-success';
  if (bmi < 28) return 'text-warning';
  return 'text-danger';
});

/** 查询健康记录列表 */
const getList = async () => {
  loading.value = true;
  try {
    // 处理日期范围
    if (dateRange.value && dateRange.value.length > 0) {
      queryParams.value.beginTime = dateRange.value[0];
      queryParams.value.endTime = dateRange.value[1];
    } else {
      queryParams.value.beginTime = undefined;
      queryParams.value.endTime = undefined;
    }
    
    // 使用store获取数据
    const res = await healthRecordStore.getRecordList({
      page: queryParams.value.pageNum,
      size: queryParams.value.pageSize,
      elderName: queryParams.value.elderName,
      recordType: queryParams.value.recordType,
      beginTime: queryParams.value.beginTime,
      endTime: queryParams.value.endTime
    });
    
    recordList.value = res.records || [];
    total.value = res.total || 0;
  } catch (error) {
    console.error("获取健康记录列表失败:", error);
    ElMessage.error("获取健康记录列表失败");
  } finally {
    loading.value = false;
  }
};

/** 取消按钮 */
const cancel = () => {
  open.value = false;
  resetForm();
};

/** 表单重置 */
const resetForm = () => {
  form.value = {
    id: undefined,
    elderId: '',
    elderName: '',
    elderAge: '',
    elderGender: '',
    bloodPressure: '',
    heartRate: '',
    bloodSugar: '',
    temperature: '',
    weight: '',
    height: '',
    bmi: '',
    medicalHistory: '',
    allergy: '',
    symptoms: '',
    medication: '',
    recordTime: '',
    recorderId: '',
    recorderName: '',
    recordType: '',
    remarks: ''
  };
  
  // 如果表单引用存在，重置验证状态
  if (recordFormRef.value) {
    recordFormRef.value.resetFields();
  }
  
  // 重置表单标签页
  activeTab.value = 'basic';
  viewActiveTab.value = 'viewBasic';
};

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
};

/** 重置按钮操作 */
const resetQuery = () => {
  dateRange.value = [];
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    elderName: undefined,
    recordType: undefined,
    beginTime: undefined,
    endTime: undefined
  };
  handleQuery();
};

// 多选框选中数据
const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
};

/** 新增按钮操作 */
const handleAdd = () => {
  resetForm();
  open.value = true;
  title.value = "添加健康记录";
  // 获取当前时间
  form.value.recordTime = formatDate(new Date(), 'YYYY-MM-DD HH:mm:ss');
  // 设置默认记录人（这里可以从用户登录信息中获取）
  form.value.recorderName = localStorage.getItem('userName') || '';
};

/** 修改按钮操作 */
const handleUpdate = (row) => {
  resetForm();
  const id = row?.id || ids.value[0];
  healthRecordStore.getRecordDetail(id).then(res => {
    Object.assign(form.value, res);
    open.value = true;
    title.value = "修改健康记录";
  });
};

/** 查看详情按钮操作 */
const handleView = (row) => {
  resetForm();
  const id = row.id;
  healthRecordStore.getRecordDetail(id).then(res => {
    Object.assign(form.value, res);
    viewOpen.value = true;
  });
};

/** 提交按钮 */
const submitForm = () => {
  if (!recordFormRef.value) {
    ElMessage.warning("表单引用不存在，请刷新页面重试");
    return;
  }
  
  recordFormRef.value.validate(valid => {
    if (valid) {
      // 计算BMI
      calculateBMI();
      
      if (form.value.id) {
        healthRecordStore.updateRecord(form.value).then(res => {
          ElMessage.success("修改成功");
          open.value = false;
          getList();
        }).catch(err => {
          ElMessage.error("修改失败：" + (err.message || '未知错误'));
        });
      } else {
        healthRecordStore.addRecord(form.value).then(res => {
          ElMessage.success("新增成功");
          open.value = false;
          getList();
        }).catch(err => {
          ElMessage.error("新增失败：" + (err.message || '未知错误'));
        });
      }
    }
  });
};

/** 删除按钮操作 */
const handleDelete = (row) => {
  const recordIds = row?.id ? [row.id] : ids.value;
  ElMessageBox.confirm('是否确认删除选中的健康记录?', "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    return healthRecordStore.deleteRecord(recordIds);
  }).then(() => {
    getList();
    ElMessage.success("删除成功");
  }).catch(() => {});
};

/** 导出按钮操作 */
const handleExport = () => {
  // 实现导出功能
  ElMessage.success("导出功能待实现");
};

/** 老人选择变更事件 */
const handleElderChange = (elderId) => {
  const elder = elderOptions.value.find(item => item.userId === elderId);
  if (elder) {
    form.value.elderName = elder.name;
    form.value.elderAge = elder.age;
    form.value.elderGender = elder.gender;
  }
};

/** 获取老人列表 */
const getElderList = async () => {
  try {
    // 使用正确的参数调用getUserList
    const res = await getUserList({
      current: 1,
      size: 100, // 获取足够多的记录以确保所有老人都被加载
      // 可以添加其他过滤条件，如果有特定字段标识老人角色
    });
    
    // 确保返回的数据格式正确
    if (res.data && res.data.records) {
      elderOptions.value = res.data.records.map(user => ({
        userId: user.userId || '',
        name: user.nickName || user.username || '',
        age: user.age || '',
        gender: user.gender || ''
      }));
    } else {
      elderOptions.value = [];
      console.warn('获取老人列表返回的数据格式不正确');
    }
  } catch (error) {
    console.error("获取老人列表失败:", error);
    ElMessage.error("获取老人列表失败，请刷新页面重试");
    elderOptions.value = [];
  }
};

onMounted(() => {
  getList();
  getElderList();
});
</script>

<style scoped>
.search-form {
  margin-bottom: 20px;
}
.mb8 {
  margin-bottom: 8px;
}
.text-success {
  color: #67c23a;
}
.text-warning {
  color: #e6a23c;
}
.text-danger {
  color: #f56c6c;
}
</style>
