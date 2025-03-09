<template>
  <div class="health-assessment">
    <el-card shadow="hover" class="table-card">
      <template #header>
        <div class="card-header">
          <h3>健康评估管理</h3>
          <div class="header-actions">
            <el-input
              v-model="searchQuery"
              placeholder="搜索老人姓名/评估类型"
              class="search-input"
              clearable
              @clear="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button type="success" @click="handleAdd">新增评估</el-button>
          </div>
        </div>
      </template>
      
      <el-table
        :data="filteredAssessments"
        style="width: 100%"
        v-loading="loading"
        border
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="elderName" label="老人姓名" width="120" />
        <el-table-column prop="assessmentType" label="评估类型" width="120">
          <template #default="scope">
            <el-tag :type="getTypeTag(scope.row.assessmentType)">
              {{ scope.row.assessmentType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assessmentTime" label="评估时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.assessmentTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="assessor" label="评估人" width="120" />
        <el-table-column prop="result" label="评估结果" width="120">
          <template #default="scope">
            <el-tag :type="getResultTag(scope.row.result)">
              {{ scope.row.result }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="suggestion" label="建议" min-width="200" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="success" size="small" @click="handleView(scope.row)">查看</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="totalAssessments"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 评估表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增评估' : '编辑评估'"
      width="600px"
    >
      <el-form
        ref="assessmentFormRef"
        :model="assessmentForm"
        :rules="assessmentRules"
        label-width="100px"
      >
        <el-form-item label="老人" prop="elderId">
          <el-select v-model="assessmentForm.elderId" placeholder="请选择老人">
            <el-option
              v-for="elder in elderOptions"
              :key="elder.id"
              :label="elder.name"
              :value="elder.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="评估类型" prop="assessmentType">
          <el-select v-model="assessmentForm.assessmentType" placeholder="请选择评估类型">
            <el-option label="日常生活能力评估" value="日常生活能力评估" />
            <el-option label="认知功能评估" value="认知功能评估" />
            <el-option label="心理健康评估" value="心理健康评估" />
            <el-option label="营养状况评估" value="营养状况评估" />
          </el-select>
        </el-form-item>
        <el-form-item label="评估时间" prop="assessmentTime">
          <el-date-picker
            v-model="assessmentForm.assessmentTime"
            type="datetime"
            placeholder="选择评估时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="评估人" prop="assessor">
          <el-select v-model="assessmentForm.assessor" placeholder="请选择评估人">
            <el-option
              v-for="staff in staffOptions"
              :key="staff.id"
              :label="staff.name"
              :value="staff.name"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="评估结果" prop="result">
          <el-select v-model="assessmentForm.result" placeholder="请选择评估结果">
            <el-option label="正常" value="正常" />
            <el-option label="轻度异常" value="轻度异常" />
            <el-option label="中度异常" value="中度异常" />
            <el-option label="重度异常" value="重度异常" />
          </el-select>
        </el-form-item>
        <el-form-item label="建议" prop="suggestion">
          <el-input
            v-model="assessmentForm.suggestion"
            type="textarea"
            :rows="3"
            placeholder="请输入评估建议"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search } from '@element-plus/icons-vue';
import { formatDate } from '@/utils/date';
import { getElders } from '@/api/elder';
import { getStaffList } from '@/api/staff';
import { createHealthAssessment, updateHealthAssessment, deleteHealthAssessment, getHealthAssessmentList } from '@/api/health';

// 评估列表数据
const assessments = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const searchQuery = ref('')
const dialogVisible = ref(false)
const dialogType = ref('add')

// 表单相关
const assessmentFormRef = ref(null)
const assessmentForm = ref({
  elderId: '',
  assessmentType: '',
  assessmentTime: '',
  assessor: '',
  result: '',
  suggestion: ''
})

// 选项数据
const elderOptions = ref([])
const staffOptions = ref([])

// 表单验证规则
const assessmentRules = {
  elderId: [{ required: true, message: '请选择老人', trigger: 'change' }],
  assessmentType: [{ required: true, message: '请选择评估类型', trigger: 'change' }],
  assessmentTime: [{ required: true, message: '请选择评估时间', trigger: 'change' }],
  assessor: [{ required: true, message: '请选择评估人', trigger: 'change' }],
  result: [{ required: true, message: '请选择评估结果', trigger: 'change' }],
  suggestion: [{ required: true, message: '请输入评估建议', trigger: 'blur' }]
}

// 过滤后的评估列表
const filteredAssessments = computed(() => {
  if (!assessments.value || !searchQuery.value) {
    return assessments.value || []
  }
  
  const query = searchQuery.value.toLowerCase()
  return assessments.value.filter(assessment => 
    (assessment.elderName && assessment.elderName.toLowerCase().includes(query)) ||
    (assessment.assessmentType && assessment.assessmentType.toLowerCase().includes(query))
  )
})

// 总评估数
const totalAssessments = computed(() => {
  return filteredAssessments.value ? filteredAssessments.value.length : 0
})

// 获取评估类型标签样式
const getTypeTag = (type) => {
  const typeMap = {
    '日常生活能力评估': 'primary',
    '认知功能评估': 'success',
    '心理健康评估': 'warning',
    '营养状况评估': 'info'
  }
  return typeMap[type] || 'default'
}

// 获取评估结果标签样式
const getResultTag = (result) => {
  const resultMap = {
    '正常': 'success',
    '轻度异常': 'warning',
    '中度异常': 'danger',
    '重度异常': 'danger'
  }
  return resultMap[result] || 'info'
}

// 初始化数据
const initData = async () => {
  try {
    loading.value = true;
    const [elderRes, staffRes, assessmentRes] = await Promise.all([
      getElders(),
      getStaffList(),
      getHealthAssessmentList()
    ]);
    elderOptions.value = elderRes.data;
    staffOptions.value = staffRes.data;
    assessments.value = assessmentRes.data;
  } catch (error) {
    console.error('数据加载失败：' + error.message);
  } finally {
    loading.value = false;
  }
};

// 提交表单
const submitForm = async () => {
  assessmentFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        const elderInfo = elderOptions.value.find(elder => elder.id === assessmentForm.value.elderId);
        const formData = {
          ...assessmentForm.value,
          elderName: elderInfo ? elderInfo.name : '未知'
        };

        if (dialogType.value === 'add') {
          await createHealthAssessment(formData);
          ElMessage.success('添加评估成功');
        } else {
          await updateHealthAssessment(formData);
          ElMessage.success('更新评估成功');
        }
        dialogVisible.value = false;
        initData(); // 刷新数据
      } catch (error) {
        console.error('操作失败：' + error.message);
      } finally {
        loading.value = false;
      }
    }
  });
};

// 删除评估
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除该评估记录吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteHealthAssessment(row.id);
      ElMessage.success('删除成功');
      initData(); // 刷新数据
    } catch (error) {
      console.error('删除失败：' + error.message);
    }
  }).catch(() => {
    // 取消删除
  });
};

// 组件挂载时初始化数据
onMounted(() => {
  initData();
});

// 分页大小变化
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

// 当前页变化
const handleCurrentChange = (val) => {
  currentPage.value = val
}
</script>

<style scoped>
.health-assessment {
  padding: 20px;
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
</style>