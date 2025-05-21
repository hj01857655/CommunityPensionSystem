<template>
  <div class="app-container">
    <el-form ref="queryFormRef" :inline="true" :model="queryParams" class="search-form" label-width="80px">
      <el-form-item label="老人姓名">
        <el-input
            v-model="queryParams.elderName"
            clearable
            placeholder="请输入老人姓名"
            style="width: 200px"
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="记录类型">
        <el-select v-model="queryParams.recordType" clearable placeholder="请选择记录类型" style="width: 200px">
          <el-option 
            v-for="dict in health_record_type" 
            :key="dict.value" 
            :label="dict.label" 
            :value="dict.value"
          />
          <!-- 如果字典为空，显示默认选项 -->
          <el-option v-if="!health_record_type || health_record_type.length === 0" label="初始记录" value="初始记录"/>
          <el-option v-if="!health_record_type || health_record_type.length === 0" label="定期检查" value="定期检查"/>
          <el-option v-if="!health_record_type || health_record_type.length === 0" label="随访记录" value="随访记录"/>
          <el-option v-if="!health_record_type || health_record_type.length === 0" label="紧急记录" value="紧急记录"/>
        </el-select>
      </el-form-item>
      <el-form-item label="记录时间">
        <el-date-picker
            v-model="dateRange"
            end-placeholder="结束日期"
            range-separator="-"
            start-placeholder="开始日期"
            style="width: 240px"
            type="daterange"
            value-format="YYYY-MM-DD"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button icon="Search" type="primary" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            icon="Plus"
            plain
            type="primary"
            @click="handleAdd"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            :disabled="single"
            icon="Edit"
            plain
            type="success"
            @click="handleUpdate"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            :disabled="multiple"
            icon="Delete"
            plain
            type="danger"
            @click="handleDelete"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            icon="Download"
            plain
            type="warning"
            @click="handleExport"
        >导出
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="recordList" @selection-change="handleSelectionChange">
      <el-table-column align="center" type="selection" width="55"/>
      <el-table-column align="center" label="记录ID" prop="id" width="80"/>
      <el-table-column :show-overflow-tooltip="true" align="center" label="老人姓名" prop="elderName" width="100"/>
      <el-table-column align="center" label="年龄" prop="elderAge" width="60"/>
      <el-table-column align="center" label="性别" prop="elderGender" width="60"/>
      <el-table-column align="center" label="血压" prop="bloodPressure" width="80"/>
      <el-table-column align="center" label="心率" prop="heartRate" width="60"/>
      <el-table-column align="center" label="血糖" prop="bloodSugar" width="60"/>
      <el-table-column align="center" label="体温" prop="temperature" width="60"/>
      <el-table-column align="center" label="记录类型" prop="recordType" width="100"/>
      <el-table-column :show-overflow-tooltip="true" align="center" label="症状" prop="symptoms"/>
      <el-table-column align="center" label="记录时间" prop="recordTime" width="160">
        <template #default="scope">
          <span>{{ formatDate(scope.row.recordTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作" width="150">
        <template #default="scope">
          <el-button
              icon="View"
              link
              @click="handleView(scope.row)"
          >查看
          </el-button>
          <el-button
              icon="Edit"
              link
              @click="handleUpdate(scope.row)"
          >修改
          </el-button>
          <el-button
              icon="Delete"
              link
              @click="handleDelete(scope.row)"
          >删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
        v-show="total > 0"
        v-model:limit="queryParams.pageSize"
        v-model:page="queryParams.pageNum"
        :total="total"
        @pagination="getList"
    />

    <!-- 添加或修改健康记录对话框 -->
    <el-dialog v-model="open" :title="title" append-to-body width="800px">
      <el-form ref="recordFormRef" :model="form" :rules="rules" label-width="100px">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="基本信息" name="basic">
            <el-row>
              <el-col :span="12">
                <el-form-item label="老人" prop="elderId">
                  <el-select v-model="form.elderId" filterable placeholder="请选择老人" style="width: 100%"
                             @change="handleElderChange">
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
                    <el-option 
                      v-for="dict in health_record_type" 
                      :key="dict.value" 
                      :label="dict.label" 
                      :value="dict.value"
                    />
                    <!-- 如果字典为空，显示默认选项 -->
                    <el-option v-if="!health_record_type || health_record_type.length === 0" label="初始记录" value="初始记录"/>
                    <el-option v-if="!health_record_type || health_record_type.length === 0" label="定期检查" value="定期检查"/>
                    <el-option v-if="!health_record_type || health_record_type.length === 0" label="随访记录" value="随访记录"/>
                    <el-option v-if="!health_record_type || health_record_type.length === 0" label="紧急记录" value="紧急记录"/>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="8">
                <el-form-item label="老人姓名" prop="elderName">
                  <el-input v-model="form.elderName" disabled placeholder="老人姓名"/>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="年龄" prop="elderAge">
                  <el-input v-model="form.elderAge" disabled placeholder="年龄"/>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="性别" prop="elderGender">
                  <el-input v-model="form.elderGender" disabled placeholder="性别"/>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item label="记录时间" prop="recordTime">
                  <el-date-picker
                      v-model="form.recordTime"
                      placeholder="系统自动获取当前时间"
                      style="width: 100%"
                      type="datetime"
                      value-format="YYYY-MM-DD HH:mm:ss"
                      disabled
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="记录人" prop="recorderName">
                  <el-input v-model="form.recorderName" disabled placeholder="系统自动获取当前登录用户"/>
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>
          <el-tab-pane label="健康指标" name="indicators">
            <!-- 快速填充工具栏 -->
            <div class="quick-tools" style="margin-bottom: 15px; padding: 10px; background-color: #f8f9fa; border-radius: 4px;">
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-button type="primary" plain size="small" @click="fillNormalValues">填充正常值</el-button>
                  <el-button type="warning" plain size="small" @click="clearAllValues">清空所有值</el-button>
                </el-col>
                <el-col :span="12" style="text-align: right;">
                  <el-tag size="small" type="info">提示: 点击按钮快速填充或清空健康指标值</el-tag>
                </el-col>
              </el-row>
            </div>
            
            <el-row>
              <el-col :span="12">
                <el-form-item label="血压" prop="bloodPressure">
                  <el-input v-model="form.bloodPressure" placeholder="请输入血压(如120/80)">
                    <template #append>mmHg</template>
                    <template #prepend>
                      <el-dropdown trigger="click" @command="handleBloodPressurePreset">
                        <span style="cursor: pointer;">预设<el-icon class="el-icon--right"><arrow-down /></el-icon></span>
                        <template #dropdown>
                          <el-dropdown-menu>
                            <el-dropdown-item command="120/80">正常 (120/80)</el-dropdown-item>
                            <el-dropdown-item command="135/85">偏高 (135/85)</el-dropdown-item>
                            <el-dropdown-item command="110/70">偏低 (110/70)</el-dropdown-item>
                          </el-dropdown-menu>
                        </template>
                      </el-dropdown>
                    </template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="心率" prop="heartRate">
                  <el-input-number v-model="form.heartRate" :max="200" :min="0" placeholder="请输入心率"
                                   style="width: 100%">
                    <template #append>次/分</template>
                  </el-input-number>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item label="血糖" prop="bloodSugar">
                  <el-input-number v-model="form.bloodSugar" :max="30" :min="0" :precision="1" :step="0.1"
                                   placeholder="请输入血糖" style="width: 100%">
                    <template #append>mmol/L</template>
                    <template #prepend>
                      <el-dropdown trigger="click" @command="handleBloodSugarPreset">
                        <span style="cursor: pointer;">预设</span>
                        <template #dropdown>
                          <el-dropdown-menu>
                            <el-dropdown-item command="5.5">正常 (5.5)</el-dropdown-item>
                            <el-dropdown-item command="7.0">偏高 (7.0)</el-dropdown-item>
                            <el-dropdown-item command="4.0">偏低 (4.0)</el-dropdown-item>
                          </el-dropdown-menu>
                        </template>
                      </el-dropdown>
                    </template>
                  </el-input-number>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="体温" prop="temperature">
                  <el-input-number v-model="form.temperature" :max="45" :min="30" :precision="1" :step="0.1"
                                   placeholder="请输入体温" style="width: 100%">
                    <template #append>℃</template>
                    <template #prepend>
                      <el-dropdown trigger="click" @command="handleTemperaturePreset">
                        <span style="cursor: pointer;">预设</span>
                        <template #dropdown>
                          <el-dropdown-menu>
                            <el-dropdown-item command="36.5">正常 (36.5)</el-dropdown-item>
                            <el-dropdown-item command="37.5">低热 (37.5)</el-dropdown-item>
                            <el-dropdown-item command="38.5">发热 (38.5)</el-dropdown-item>
                          </el-dropdown-menu>
                        </template>
                      </el-dropdown>
                    </template>
                  </el-input-number>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item label="体重" prop="weight">
                  <el-input-number v-model="form.weight" :max="200" :min="0" :precision="1" :step="0.1"
                                   placeholder="请输入体重" style="width: 100%" @change="calculateBMI">
                    <template #append>kg</template>
                  </el-input-number>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="身高" prop="height">
                  <el-input-number v-model="form.height" :max="250" :min="0" :precision="1" :step="0.1"
                                   placeholder="请输入身高" style="width: 100%" @change="calculateBMI">
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
              <el-input v-model="form.medicalHistory" :rows="2" placeholder="请输入既往病史" type="textarea"/>
            </el-form-item>
            <el-form-item label="过敏史" prop="allergy">
              <el-input v-model="form.allergy" :rows="2" placeholder="请输入过敏史" type="textarea"/>
            </el-form-item>
            <el-form-item label="当前症状" prop="symptoms">
              <el-input v-model="form.symptoms" :rows="3" placeholder="请输入当前症状" type="textarea"/>
            </el-form-item>
            <el-form-item label="用药情况" prop="medication">
              <el-input v-model="form.medication" :rows="3" placeholder="请输入用药情况" type="textarea"/>
            </el-form-item>
            <el-form-item label="备注" prop="remarks">
              <el-input v-model="form.remarks" :rows="2" placeholder="请输入备注" type="textarea"/>
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
    <el-dialog v-model="viewOpen" append-to-body title="健康记录详情" width="800px">
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
            <el-descriptions-item v-if="form.bmi" label="BMI">
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
import { useHealthRecordStore } from '@/stores/back/health/healthRecordStore';
import { formatDate, formatDateTime } from '@/utils/date';
import { useDict } from '@/utils/dict';
import { ElMessage, ElMessageBox } from 'element-plus';
import { computed, onMounted, ref } from 'vue';
import * as XLSX from 'xlsx';

// 从字典中获取记录类型和BMI状态
const { health_record_type, bmi_status } = useDict('health_record_type', 'bmi_status');

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
  heartRate: 0,
  bloodSugar: 0,
  temperature: 36.5,
  weight: 0,
  height: 0,
  bmi: 0,
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
  elderId: [{required: true, message: "老人不能为空", trigger: "change"}],
  recordType: [{required: true, message: "记录类型不能为空", trigger: "change"}],
  bloodPressure: [
    {required: true, message: "血压不能为空", trigger: "blur"},
    {pattern: /^\d{2,3}\/\d{2,3}$/, message: "血压格式应为收缩压/舒张压，如120/80", trigger: "blur"}
  ],
  heartRate: [
    {required: true, message: "心率不能为空", trigger: "blur"},
    {type: 'number', min: 40, max: 180, message: "心率应在40-180次/分之间", trigger: "blur"}
  ],
  bloodSugar: [
    {required: true, message: "血糖不能为空", trigger: "blur"},
    {type: 'number', min: 2, max: 20, message: "血糖应在2-20mmol/L之间", trigger: "blur"}
  ],
  temperature: [
    {required: true, message: "体温不能为空", trigger: "blur"},
    {type: 'number', min: 35, max: 42, message: "体温应在35-42°C之间", trigger: "blur"}
  ],
  weight: [
    {type: 'number', min: 30, max: 150, message: "体重应在30-150kg之间", trigger: "blur"}
  ],
  height: [
    {type: 'number', min: 100, max: 220, message: "身高应在100-220cm之间", trigger: "blur"}
  ],
  recordTime: [{required: true, message: "记录时间不能为空", trigger: "blur"}],
  recorderName: [{required: true, message: "记录人不能为空", trigger: "blur"}]
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
  
  // 使用字典中的数据
  if (bmi_status.value && bmi_status.value.length > 0) {
    // 按照阈值从小到大排序
    const sortedStatus = [...bmi_status.value].sort((a, b) => parseFloat(a.value) - parseFloat(b.value));
    
    // 找到第一个阈值大于当前BMI的状态
    for (const status of sortedStatus) {
      if (bmi < parseFloat(status.value)) {
        return status.label;
      }
    }
    
    // 如果所有阈值都小于当前BMI，返回最后一个状态
    return sortedStatus[sortedStatus.length - 1].label;
  }
  
  // 如果字典为空，使用默认值
  if (bmi < 18.5) return '偏瘦';
  if (bmi < 24) return '正常';
  if (bmi < 28) return '超重';
  return '肥胖';
});

// 获取BMI状态对应的CSS类
const getBMIClass = computed(() => {
  const bmi = parseFloat(form.value.bmi);
  if (!bmi) return '';
  
  // 使用字典中的数据
  if (bmi_status.value && bmi_status.value.length > 0) {
    // 按照阈值从小到大排序
    const sortedStatus = [...bmi_status.value].sort((a, b) => parseFloat(a.value) - parseFloat(b.value));
    
    // 找到第一个阈值大于当前BMI的状态
    for (const status of sortedStatus) {
      if (bmi < parseFloat(status.value)) {
        return status.elTagClass || ''; // 使用字典中的CSS类
      }
    }
    
    // 如果所有阈值都小于当前BMI，返回最后一个状态的CSS类
    return sortedStatus[sortedStatus.length - 1].elTagClass || '';
  }
  
  // 如果字典为空，使用默认值
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
    heartRate: 0,
    bloodSugar: 0,
    temperature: 36.5,
    weight: 0,
    height: 0,
    bmi: 0,
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
  form.value.recordTime = formatDateTime(new Date());
  
  // 从sessionStorage获取当前登录用户信息
  const userInfoStr = sessionStorage.getItem('userInfo');
  if (userInfoStr) {
    try {
      const userInfo = JSON.parse(userInfoStr);
      form.value.recorderName = userInfo.name || userInfo.username || '';
      form.value.recorderId = userInfo.userId || '';
    } catch (e) {
      console.error('解析用户信息失败', e);
    }
  }
  
  // 提示用户选择老人
  ElMessage.info('请先选择一位老人，再填写健康记录');
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
      
      // 创建一个新对象，严格按照后端DTO的字段结构
      // 注意: 不包含 elderName, elderAge, elderGender 等前端显示字段
      const submitData = {
        id: form.value.id,
        elderId: form.value.elderId,
        bloodPressure: form.value.bloodPressure,
        heartRate: form.value.heartRate,
        bloodSugar: form.value.bloodSugar,
        temperature: form.value.temperature,
        weight: form.value.weight,
        height: form.value.height,
        bmi: form.value.bmi,
        medicalHistory: form.value.medicalHistory,
        allergy: form.value.allergy,
        symptoms: form.value.symptoms,
        symptomsRecordTime: form.value.symptomsRecordTime,
        medication: form.value.medication,
        recordTime: form.value.recordTime,
        recorderId: form.value.recorderId,
        recordType: form.value.recordType,
        remarks: form.value.remarks
      };

      // 移除值为undefined或null的字段
      Object.keys(submitData).forEach(key => {
        if (submitData[key] === undefined || submitData[key] === null) {
          delete submitData[key];
        }
      });
      
      // 确保 elderId 存在且非空
      if (!submitData.elderId) {
        ElMessage.warning("请选择老人");
        return;
      }

      console.log('提交的健康记录数据:', submitData);

      if (form.value.id) {
        healthRecordStore.updateRecord(submitData).then(res => {
          ElMessage.success("修改成功");
          open.value = false;
          getList();
        }).catch(err => {
          const errorMsg = err.response?.data?.message || err.message || '未知错误';
          ElMessage.error("修改失败：" + errorMsg);
          console.error("修改健康记录失败:", err);
        });
      } else {
        healthRecordStore.addRecord(submitData).then(res => {
          ElMessage.success("新增成功");
          open.value = false;
          getList();
        }).catch(err => {
          const errorMsg = err.response?.data?.message || err.message || '未知错误';
          ElMessage.error("新增失败：" + errorMsg);
          console.error("新增健康记录失败:", err);
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
  }).catch(() => {
  });
};

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    ElMessage.info('正在准备导出数据，请稍候...');
    
    // 准备导出数据
    const exportData = recordList.value.map(record => ({
      '记录ID': record.id,
      '老人姓名': record.elderName,
      '年龄': record.elderAge,
      '性别': record.elderGender,
      '血压': record.bloodPressure,
      '心率': record.heartRate,
      '血糖': record.bloodSugar,
      '体温': record.temperature,
      '体重(kg)': record.weight,
      '身高(cm)': record.height,
      'BMI': record.bmi,
      '记录类型': record.recordType,
      '症状': record.symptoms,
      '诊断结果': record.diagnosis,
      '处方': record.prescription,
      '建议': record.advice,
      '记录时间': formatDate(record.recordTime),
      '记录人员': record.recorder,
      '备注': record.remark
    }));
    
    // 创建工作簿
    const worksheet = XLSX.utils.json_to_sheet(exportData);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, '健康档案记录');
    
    // 设置列宽
    const columnWidths = [
      { wch: 8 },  // 记录ID
      { wch: 10 }, // 老人姓名
      { wch: 6 },  // 年龄
      { wch: 6 },  // 性别
      { wch: 10 }, // 血压
      { wch: 8 },  // 心率
      { wch: 8 },  // 血糖
      { wch: 8 },  // 体温
      { wch: 10 }, // 体重
      { wch: 10 }, // 身高
      { wch: 8 },  // BMI
      { wch: 12 }, // 记录类型
      { wch: 20 }, // 症状
      { wch: 20 }, // 诊断结果
      { wch: 20 }, // 处方
      { wch: 20 }, // 建议
      { wch: 18 }, // 记录时间
      { wch: 12 }, // 记录人员
      { wch: 20 }  // 备注
    ];
    worksheet['!cols'] = columnWidths;
    
    // 导出Excel文件
    XLSX.writeFile(workbook, `健康档案记录_${formatDate(new Date())}.xlsx`);
    
    ElMessage.success('导出成功');
  } catch (error) {
    console.error('导出失败:', error);
    ElMessage.error('导出失败: ' + (error.message || '未知错误'));
  }
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
    // 直接使用导入的API
    const { getUserList } = await import('@/api/back/system/user');
    
    // 获取所有用户
    const res = await getUserList({
      current: 1,
      size: 100 // 获取足够多的记录以确保所有老人都被加载
    });
    
    console.log('获取到的用户列表数据:', res);

    // 确保返回的数据格式正确
    if (res.code === 200 && res.data && res.data.records) {
      // 手动筛选出角色为老人的用户
      const elders = res.data.records.filter(user => 
        user.roleId === 1 || user.role === 'elder'
      );
      
      elderOptions.value = elders.map(user => ({
        userId: user.userId,
        name: user.name || user.username, // 优先使用name字段
        age: user.age || '',
        gender: user.gender || ''
      }));
      
      console.log('筛选后的老人列表:', elderOptions.value);
      
      if (elderOptions.value.length === 0) {
        ElMessage.warning('未找到老人用户，请先在用户管理中添加老人角色的用户');
      }
    } else {
      elderOptions.value = [];
      console.warn('获取老人列表返回的数据格式不正确或为空');
      ElMessage.warning('获取老人列表失败，请联系管理员');
    }
  } catch (error) {
    console.error("获取老人列表失败:", error);
    ElMessage.error("获取老人列表失败，请刷新页面重试");
    elderOptions.value = [];
  }
};

// 处理血压预设值选择
const handleBloodPressurePreset = (command) => {
  form.value.bloodPressure = command;
};

// 处理血糖预设值选择
const handleBloodSugarPreset = (command) => {
  form.value.bloodSugar = parseFloat(command);
};

// 处理体温预设值选择
const handleTemperaturePreset = (command) => {
  form.value.temperature = parseFloat(command);
};

// 填充正常值函数
const fillNormalValues = () => {
  form.value.bloodPressure = '120/80';
  form.value.heartRate = 75;
  form.value.bloodSugar = 5.5;
  form.value.temperature = 36.5;
  form.value.weight = form.value.weight || 65;
  form.value.height = form.value.height || 170;
  calculateBMI(); // 重新计算BMI
  ElMessage.success('已填充正常值');
};

// 清空所有健康指标值
const clearAllValues = () => {
  form.value.bloodPressure = '';
  form.value.heartRate = 0;
  form.value.bloodSugar = 0;
  form.value.temperature = 36.5;
  form.value.weight = 0;
  form.value.height = 0;
  form.value.bmi = 0;
  ElMessage.info('已清空所有健康指标值');
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
