<template>
  <div class="app-container">
    <!-- 搜索条件 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="活动名称" prop="activityId">
        <el-select v-model="queryParams.activityId" placeholder="请选择活动" clearable style="width: 200px">
          <el-option
            v-for="item in activityOptions"
            :key="item.id"
            :label="item.title"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="签到日期" prop="date">
        <el-date-picker
          v-model="queryParams.date"
          type="date"
          placeholder="选择日期"
          style="width: 200px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
        <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain :icon="Plus" @click="handleAdd">新增签到</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain :icon="Download" @click="handleExport">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
    </el-row>

    <!-- 签到记录列表 -->
    <el-table v-loading="loading" :data="checkinList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" v-if="columns[0].visible" />
      <el-table-column prop="activityTitle" label="活动名称" v-if="columns[1].visible" />
      <el-table-column prop="elderName" label="参与人" v-if="columns[2].visible" />
      <el-table-column prop="signInTime" label="签到时间" v-if="columns[3].visible" />
      <el-table-column prop="signOutTime" label="签退时间" v-if="columns[4].visible" />
      <el-table-column prop="remarks" label="备注" v-if="columns[5].visible" />
      <el-table-column label="操作" width="150" v-if="columns[6].visible">
        <template #default="{ row }">
          <el-button 
            v-if="!row.signOutTime"
            type="primary" 
            link 
            :icon="Check"
            @click="handleSignOut(row)"
          >
            签退
          </el-button>
          <el-button 
            type="primary" 
            link 
            :icon="Edit"
            @click="handleRemark(row)"
          >
            备注
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <pagination
      v-if="total > 0"
      :total="total"
      :page="queryParams.pageNum"
      :limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 备注对话框 -->
    <el-dialog
      v-model="remarkDialogVisible"
      title="添加备注"
      width="500px"
      append-to-body
    >
      <el-form ref="remarkFormRef" :model="remarkForm" :rules="remarkRules" label-width="100px">
        <el-form-item label="备注内容" prop="remarks">
          <el-input type="textarea" v-model="remarkForm.remarks" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="remarkDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleRemarkSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 新增签到对话框 -->
    <el-dialog
      v-model="addDialogVisible"
      title="新增签到"
      width="500px"
      append-to-body
    >
      <el-form ref="addFormRef" :model="addForm" :rules="addRules" label-width="100px">
        <el-form-item label="活动" prop="activityId">
          <el-select v-model="addForm.activityId" placeholder="请选择活动" style="width: 100%">
            <el-option
              v-for="item in activityOptions"
              :key="item.id"
              :label="item.title"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="参与人" prop="elderId">
          <el-select v-model="addForm.elderId" placeholder="请选择参与人" style="width: 100%">
            <el-option
              v-for="item in elderOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="签到时间" prop="signInTime">
          <el-date-picker
            v-model="addForm.signInTime"
            type="datetime"
            placeholder="选择签到时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input type="textarea" v-model="addForm.remarks" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAddSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Check, Download } from '@element-plus/icons-vue'
import RightToolbar from '@/components/common/base/RightToolbar/index.vue'
import Pagination from '@/components/common/Pagination.vue'

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  activityId: '',
  date: ''
})

// 遮罩层
const loading = ref(false)
// 选中数组
const selectedRows = ref([])
// 显示搜索条件
const showSearch = ref(true)
// 总条数
const total = ref(0)
// 签到记录列表
const checkinList = ref([])
// 活动选项
const activityOptions = ref([])
// 老人选项
const elderOptions = ref([])

// 列显示控制
const columns = ref([
  { key: 0, label: '选择列', visible: true },
  { key: 1, label: '活动名称', visible: true },
  { key: 2, label: '参与人', visible: true },
  { key: 3, label: '签到时间', visible: true },
  { key: 4, label: '签退时间', visible: true },
  { key: 5, label: '备注', visible: true },
  { key: 6, label: '操作', visible: true }
])

// 备注对话框
const remarkDialogVisible = ref(false)
const remarkForm = reactive({
  id: '',
  remarks: ''
})
const remarkRules = {
  remarks: [
    { required: true, message: '请输入备注内容', trigger: 'blur' }
  ]
}
const remarkFormRef = ref(null)

// 新增签到对话框
const addDialogVisible = ref(false)
const addForm = reactive({
  activityId: '',
  elderId: '',
  signInTime: '',
  remarks: ''
})
const addRules = {
  activityId: [
    { required: true, message: '请选择活动', trigger: 'change' }
  ],
  elderId: [
    { required: true, message: '请选择参与人', trigger: 'change' }
  ],
  signInTime: [
    { required: true, message: '请选择签到时间', trigger: 'change' }
  ]
}
const addFormRef = ref(null)
const queryRef = ref(null)

// 多选框选中数据
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 查询签到记录
const getList = (params) => {
  loading.value = true
  
  // 处理分页参数
  if (params) {
    queryParams.pageNum = params.page
    queryParams.pageSize = params.limit
  }
  
  // 这里应该调用API获取签到记录列表
  // 模拟数据
  setTimeout(() => {
    // 模拟数据
    checkinList.value = [
      {
        id: '1',
        activityId: '1',
        activityTitle: '健康讲座',
        elderId: '1',
        elderName: '张三',
        signInTime: '2025-05-06 09:30:00',
        signOutTime: '2025-05-06 11:30:00',
        remarks: '按时参加'
      },
      {
        id: '2',
        activityId: '1',
        activityTitle: '健康讲座',
        elderId: '2',
        elderName: '李四',
        signInTime: '2025-05-06 09:35:00',
        signOutTime: null,
        remarks: ''
      }
    ]
    total.value = 2
    loading.value = false
  }, 300)
}

// 查询活动列表
const getActivityOptions = () => {
  // 这里应该调用API获取活动列表
  // 模拟数据
  activityOptions.value = [
    { id: '1', title: '健康讲座' },
    { id: '2', title: '太极拳教学' },
    { id: '3', title: '书法比赛' }
  ]
}

// 查询老人列表
const getElderOptions = () => {
  // 这里应该调用API获取老人列表
  // 模拟数据
  elderOptions.value = [
    { id: '1', name: '张三' },
    { id: '2', name: '李四' },
    { id: '3', name: '王五' }
  ]
}

// 搜索按钮操作
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置按钮操作
const resetQuery = () => {
  if (queryRef.value) {
    queryRef.value.resetFields()
  }
  queryParams.pageNum = 1
  queryParams.pageSize = 10
  queryParams.activityId = ''
  queryParams.date = ''
  handleQuery()
}

// 新增签到
const handleAdd = () => {
  addDialogVisible.value = true
  addForm.activityId = ''
  addForm.elderId = ''
  addForm.signInTime = ''
  addForm.remarks = ''
  
  if (addFormRef.value) {
    addFormRef.value.resetFields()
  }
}

// 提交新增签到
const handleAddSubmit = () => {
  if (!addFormRef.value) return
  
  addFormRef.value.validate((valid) => {
    if (valid) {
      // 这里应该调用API提交新增签到
      ElMessage.success('新增签到成功')
      addDialogVisible.value = false
      getList()
    }
  })
}

// 导出数据
const handleExport = () => {
  ElMessageBox.confirm('确认导出所有签到记录数据?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      // 这里应该调用导出API
      ElMessage.success('导出成功')
    })
    .catch(() => {})
}

// 签退操作
const handleSignOut = (row) => {
  ElMessageBox.confirm(`确认为 ${row.elderName} 进行签退操作吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => {
    // 这里应该调用API进行签退操作
    ElMessage.success('签退成功')
    getList()
  }).catch(() => {})
}

// 添加备注
const handleRemark = (row) => {
  remarkDialogVisible.value = true
  remarkForm.id = row.id
  remarkForm.remarks = row.remarks || ''
}

// 提交备注
const handleRemarkSubmit = () => {
  if (!remarkFormRef.value) return
  
  remarkFormRef.value.validate((valid) => {
    if (valid) {
      // 这里应该调用API提交备注
      ElMessage.success('备注添加成功')
      remarkDialogVisible.value = false
      getList()
    }
  })
}

// 初始化
onMounted(() => {
  getActivityOptions()
  getElderOptions()
  getList()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}
</style>