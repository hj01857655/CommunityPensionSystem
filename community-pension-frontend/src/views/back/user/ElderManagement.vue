<template>
  <div class="elder-management">
    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h3>老人管理</h3>
          <div class="header-actions">
            <el-input v-model="elderStore.searchQuery" class="search-input" clearable placeholder="搜索姓名/身份证号/电话"
              @input="handleSearch">
              <template #prefix>
                <el-icon>
                  <Search />
                </el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button type="success" @click="handleAdd">添加老人信息</el-button>
          </div>
        </div>
      </template>
      <el-table v-loading="loading" :data="filteredElders" border style="width: 100%">
        <el-table-column label="ID" prop="id" width="80" />
        <el-table-column label="姓名" prop="name" width="100" />
        <el-table-column label="性别" prop="gender" width="80">
          <template #default="scope">
            {{ scope.row.gender === 'male' ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column label="出生日期" prop="birthday" width="100">
          <template #default="scope">
            {{ scope.row.birthday }}
          </template>
        </el-table-column>
        <el-table-column label="年龄" width="80">
          <template #default="scope">
            {{ scope.row.age }}
          </template>
        </el-table-column>
        <el-table-column label="身份证号" prop="idCard" width="180" />
        <el-table-column label="健康状况" prop="healthCondition" width="100">
          <template #default="scope">
            <el-tag :type="getHealthStatusType(scope.row.healthCondition)">
              {{ scope.row.healthCondition }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="220">
          <template #default="scope">
            <el-button size="small" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="success" @click="handleViewHealth(scope.row)">健康档案</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="elderStore.currentPage"
          v-model:page-size="elderStore.pageSize"
          :total="elderStore.total"
          :page-sizes="[10, 20, 30, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '添加老人信息' : '编辑老人信息'" width="600px">
      <el-form ref="elderFormRef" :model="elderForm" :rules="elderRules" label-width="100px">
        <div class="form-section">
          <h4 class="section-title">基本信息</h4>
          <el-divider />
          <el-form-item label="姓名" prop="name">
            <el-tooltip content="请输入老人的真实姓名" placement="top">
              <el-input v-model="elderForm.name" placeholder="请输入老人的真实姓名" />
            </el-tooltip>
          </el-form-item>
          <el-form-item label="性别" prop="gender">
            <el-radio-group v-model="elderForm.gender">
              <el-radio value="male" label="男">男</el-radio>
              <el-radio value="female" label="女">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="出生日期" prop="birthday">
            <el-tooltip content="出生日期将用于自动计算年龄" placement="top">
              <div>
                <el-date-picker v-model="elderForm.birthday" placeholder="选择日期" type="date" value-format="YYYY-MM-DD" />
              </div>
            </el-tooltip>
          </el-form-item>
          <el-form-item label="身份证号" prop="idCard">
            <el-tooltip content="请输入18位有效身份证号码" placement="top">
              <el-input v-model="elderForm.idCard" placeholder="请输入18位有效身份证号码" />
            </el-tooltip>
          </el-form-item>
        </div>

        <div class="form-section">
          <h4 class="section-title">联系信息</h4>
          <el-divider />
          <el-form-item label="紧急联系人" prop="emergencyContact">
            <el-tooltip content="请输入紧急情况下可联系的亲属或朋友姓名" placement="top">
              <el-input v-model="elderForm.emergencyContactName" placeholder="请输入紧急联系人姓名" />
            </el-tooltip>
          </el-form-item>
          <el-form-item label="紧急电话" prop="emergencyPhone">
            <el-tooltip content="请输入紧急联系人的电话号码" placement="top">
              <el-input v-model="elderForm.emergencyContactPhone" placeholder="请输入紧急联系人电话" />
            </el-tooltip>
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="healthDialogVisible" :title="healthDialogType === 'view' ? '健康档案' : '编辑健康档案'" width="700px">
      <template v-if="healthDialogType === 'view'">
        <el-descriptions :column="2" :title-class-name="'health-title'" border title="基本信息">
          <el-descriptions-item label="姓名">{{ selectedElder.name }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ selectedElder.gender === 'male' ? '男' : '女' }}</el-descriptions-item>
          <el-descriptions-item label="年龄">{{ selectedElder.age }}</el-descriptions-item>
          <el-descriptions-item label="健康状况">
            <el-tag :type="getHealthStatusType(selectedElder.healthCondition)">
              {{ selectedElder.healthCondition }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <el-divider />
        <el-descriptions :column="2" :title-class-name="'health-title'" border title="健康指标">
          <el-descriptions-item label="身高">{{ healthData.height }} cm</el-descriptions-item>
          <el-descriptions-item label="体重">{{ healthData.weight }} kg</el-descriptions-item>
          <el-descriptions-item label="血压">{{ healthData.bloodPressure }}</el-descriptions-item>
          <el-descriptions-item label="血糖">{{ healthData.bloodSugar }} mmol/L</el-descriptions-item>
          <el-descriptions-item label="心率">{{ healthData.heartRate }} 次/分</el-descriptions-item>
          <el-descriptions-item label="体温">{{ healthData.temperature }} °C</el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <el-descriptions :column="1" :title-class-name="'health-title'" border title="病史信息">
          <el-descriptions-item label="病史">{{ healthData.medicalHistory || '无' }}</el-descriptions-item>
          <el-descriptions-item label="过敏史">{{ healthData.allergies || '无' }}</el-descriptions-item>
          <el-descriptions-item label="用药情况">{{ healthData.medication || '无' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { debounce } from '@/utils/util';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search } from '@element-plus/icons-vue';
import { useElderStore } from '@/stores/back/elderStore';
import { getElderList, addElder, updateElder, deleteElder, getHealthRecords, updateHealthRecords } from '@/api/back/UserManage/ElderManage';

const elderStore = useElderStore();
const elders = ref([]);

const loading = ref(false);
const dialogVisible = ref(false);
const dialogType = ref('add');
const elderFormRef = ref(null);
const elderForm = ref({
  id: '',
  name: '',
  gender: '',
  birthday: '',
  age: '',
  idCard: '',
  emergencyContactName: '',
  emergencyContactPhone: '',
  healthCondition: '',
  remarks: '',
});
const elderRules = ref({
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  birthday: [
    { required: true, message: '请选择出生日期', trigger: 'change' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}(\d|X)$/, message: '身份证号格式不正确', trigger: 'blur' }
  ],
  emergencyContactName: [
    { required: true, message: '请输入紧急联系人', trigger: 'blur' }
  ],
  emergencyContactPhone: [
    { required: true, message: '请输入紧急联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '紧急联系电话格式不正确', trigger: 'blur' }
  ],
  healthCondition: [
    { required: true, message: '请选择健康状况', trigger: 'change' }
  ]
});
const healthDialogVisible = ref(false);
const healthDialogType = ref('view');
const selectedElder = ref({
  id: '',
  name: '',
  gender: '',
  birthday: '',
  idCard: '',
});
const healthData = ref({
  height: '',
  weight: '',
  bloodPressure: '',
  bloodSugar: '',
  heartRate: '',
  temperature: '',
  medication: '',
  medicalHistory: '',
  allergies: '',
});

const healthRecords = ref([]);

// 获取老人列表
const fetchElders = async () => {
  loading.value = true;
  try {
    await elderStore.fetchElders();
    elders.value = elderStore.elderList;
  } catch (error) {
    console.error('获取老人列表失败:', error);
    elders.value = [];
  } finally {
    loading.value = false;
  }
};

// 过滤老人信息
const filteredElders = computed(() => {
  if (!elderStore.searchQuery) {
    return elders.value;
  }
  const query = elderStore.searchQuery.toLowerCase();
  return elders.value.filter(elder =>
    elder.name?.toLowerCase().includes(query) ||
    elder.idCard?.includes(query)
  );
});


// 获取健康状况标签类型
const getHealthStatusType = (status) => {
  const typeMap = {
    '高血压': 'warning',
    '糖尿病': 'danger',
    '心脏病': 'danger',
    '癌症': 'danger',
    '其他': 'info'
  };
  return typeMap[status] || 'info';
};

const handleAdd = () => {
  dialogType.value = 'add';
  resetForm();
  dialogVisible.value = true;
};

const handleEdit = (row) => {
  dialogType.value = 'edit';
  Object.keys(elderForm.value).forEach(key => {
    elderForm.value[key] = row[key] || '';
  });
  dialogVisible.value = true;
};

const handleViewHealth = async (row) => {
  selectedElder.value = {
    ...row,
    name: row.name,
    gender: row.gender,
    age: row.age,
    healthCondition: row.healthCondition
  };
  try {
    const response = await getHealthRecords(row.id);
    if (response.code === 200 && response.data) {
      healthData.value = {
        height: response.data.height || '',
        weight: response.data.weight || '',
        bloodPressure: response.data.bloodPressure || '',
        bloodSugar: response.data.bloodSugar || '',
        heartRate: response.data.heartRate || '',
        temperature: response.data.temperature || '',
        medication: response.data.medication || '无',
        medicalHistory: response.data.medicalHistory || '无',
        allergies: response.data.allergies || '无'
      };
    }
  } catch (error) {
    console.error('获取健康记录失败');
  }
  healthRecords.value = [
    {
      date: row.lastCheckTime || '暂无检查记录',
      type: 'primary',
      content: `最近一次体检：${row.lastCheckResult || '暂无检查结果'}`
    }
  ];
  healthDialogVisible.value = true;
  healthDialogType.value = 'view';
};

const handleEditHealth = () => {
  healthDialogType.value = 'edit';
  healthData.value = { ...healthData.value };
};

const submitHealthForm = async () => {
  try {
    await updateHealthRecords(healthData.value);
    ElMessage.success('健康档案更新成功');
    healthDialogVisible.value = false;
  } catch (error) {
    console.error('健康档案更新失败');
  }
};

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该老人信息吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      await elderStore.deleteElder(row.id);
    })
    .catch(() => {
      ElMessage.info('已取消删除');
    });
};

const resetForm = () => {
  if (elderFormRef.value) {
    elderFormRef.value.resetFields();
  }
  Object.keys(elderForm).forEach(key => {
    if (key === 'gender') {
      elderForm[key] = 'male';
    } else if (key === 'healthCondition') {
      elderForm[key] = '良好';
    } else {
      elderForm[key] = '';
    }
  });
};

const handleSearch = debounce(() => {
  elderStore.currentPage = 1;
  fetchElders();
});

const handleSizeChange = (val) => {
  elderStore.pageSize = val;
  // elderStore.currentPage = 1;
  fetchElders();
};

const handleCurrentChange = (val) => {
  elderStore.currentPage = val;
  fetchElders();
};

const submitForm = () => {
  elderFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const formData = {
          id: elderForm.value.id,
          name: elderForm.value.name,
          gender: elderForm.value.gender,
          birthday: elderForm.value.birthday,
          age: elderForm.value.age,
          idCard: elderForm.value.idCard,
          emergencyContactName: elderForm.value.emergencyContactName,
          emergencyContactPhone: elderForm.value.emergencyContactPhone,
          healthCondition: elderForm.value.healthCondition,
          remarks: elderForm.value.remarks,
        };

        if (dialogType.value === 'add') {
          await addElder(formData);
          ElMessage.success('添加老人信息成功');
        } else {
          await updateElder(formData);
          ElMessage.success('更新老人信息成功');
        }
        dialogVisible.value = false;
        fetchElders();
      } catch (error) {
        console.error('提交表单错误:', error);
        console.error(dialogType.value === 'add' ? '添加老人信息失败' : '更新老人信息失败');
      }
    } else {
      return false;
    }
  });
};

onMounted(() => {
  fetchElders();
});
</script>

<style scoped>
.elder-management {
  padding: 10px;
}

.table-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-input {
  width: 250px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.health-title {
  font-weight: bold;
  color: #409EFF;
  margin: 16px 0;
}

:deep(.health-title) {
  background-color: #f0f9ff;
  padding: 8px;
  font-weight: bold;
}

.form-section {
  margin-bottom: 20px;
}

.section-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #409EFF;
}
</style>