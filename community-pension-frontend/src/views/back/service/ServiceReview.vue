<template>
  <div class="app-container">
    <!-- 搜索条件 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="服务 ID" prop="serviceId">
        <el-input
          v-model="queryParams.serviceId"
          placeholder="请输入服务 ID"
          clearable
          style="width: 200px"
          @keyup.enter="handleSearch"
        />
      </el-form-item>
      <el-form-item label="评价状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择评价状态"
          clearable
          style="width: 200px"
        >
          <el-option label="全部" value="" />
          <el-option label="待审核" :value="0" />
          <el-option label="显示" :value="1" />
          <el-option label="隐藏" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="danger" plain :icon="Delete" :disabled="multiple" @click="handleBulkDelete">批量删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 表格 -->
    <el-table
      v-loading="loading"
      :data="reviewList"
      @selection-change="handleSelectionChange"
      border
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column prop="id" label="评价编号" width="80" align="center" />
      <el-table-column prop="serviceName" label="服务名称" min-width="150" align="center" />
      <el-table-column prop="elderName" label="评价老人" width="120" align="center" />
      <el-table-column prop="rating" label="评分" width="100" align="center">
        <template #default="scope">
          <el-rate
            v-model="scope.row.rating"
            disabled
            show-score
            text-color="#ff9900"
          />
        </template>
      </el-table-column>
      <el-table-column prop="content" label="评价内容" min-width="200" align="center" show-overflow-tooltip />
      <el-table-column prop="replyContent" label="回复内容" min-width="200" align="center" show-overflow-tooltip />
      <el-table-column prop="createTime" label="评价时间" width="180" align="center">
        <template #default="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button type="primary" link :icon="View" @click="handleView(scope.row)">查看</el-button>
          <el-button 
            v-if="!scope.row.replyContent"
            type="success" 
            link 
            :icon="ChatLineRound" 
            @click="handleReply(scope.row)"
          >回复</el-button>
          <el-button type="primary" link :icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <pagination
      v-if="total > 0"
      :total="total"
      v-model:page="queryParams.current"
      v-model:limit="queryParams.size"
      @pagination="getList"
    />

    <!-- 评价详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'view' ? '评价详情' : '回复评价'"
      width="600px"
      append-to-body
    >
      <el-descriptions v-if="dialogType === 'view'" :column="1" border>
        <el-descriptions-item label="服务名称">{{ currentEvaluation?.serviceName }}</el-descriptions-item>
        <el-descriptions-item label="评价老人">{{ currentEvaluation?.elderName }}</el-descriptions-item>
        <el-descriptions-item label="评分">
          <el-rate
            v-model="currentRating"
            disabled
            show-score
            text-color="#ff9900"
          />
        </el-descriptions-item>
        <el-descriptions-item label="评价内容">{{ currentEvaluation?.content }}</el-descriptions-item>
        <el-descriptions-item label="回复内容">{{ currentEvaluation?.replyContent || '暂无回复' }}</el-descriptions-item>
        <el-descriptions-item label="评价时间">{{ formatDate(currentEvaluation?.createTime) }}</el-descriptions-item>
      </el-descriptions>
      <el-form v-else ref="replyFormRef" :model="replyForm" :rules="replyRules" label-width="100px">
        <el-form-item label="评价内容">
          <div class="review-content">{{ currentEvaluation?.content }}</div>
        </el-form-item>
        <el-form-item label="回复内容" prop="replyContent">
          <el-input
            v-model="replyForm.replyContent"
            type="textarea"
            :rows="4"
            placeholder="请输入回复内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button v-if="dialogType === 'reply'" type="primary" @click="submitReply">确 定</el-button>
          <el-button @click="dialogVisible = false">{{ dialogType === 'reply' ? '取 消' : '关 闭' }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import Pagination from '@/components/common/Pagination.vue'
import RightToolbar from '@/components/RightToolbar/index.vue'
import { useServiceReviewStore } from '@/stores/back/service/review'
import { formatDate } from '@/utils/date'
import { ChatLineRound, Delete, Refresh, Search, View } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'

// 实例化 Store
const serviceReviewStore = useServiceReviewStore()

// 查询参数
const queryParams = reactive({
  current: 1,
  size: 10,
  serviceId: null,
  status: ''
})

// 显示搜索条件
const showSearch = ref(true)

// 非多个禁用
const multiple = ref(true)
const selectedIds = ref([])

// 加载状态
const loading = computed(() => serviceReviewStore.loading)

// 评价列表数据
const reviewList = computed(() => serviceReviewStore.reviewList)
const total = computed(() => serviceReviewStore.total)

// 对话框相关
const dialogVisible = ref(false)
const currentEvaluation = ref(null)

// 对话框类型
const dialogType = ref('view')

// 回复表单
const replyFormRef = ref(null)
const replyForm = reactive({
  id: undefined,
  replyContent: ''
})

// 回复表单校验规则
const replyRules = {
  replyContent: [
    { required: true, message: '请输入回复内容', trigger: 'blur' },
    { min: 2, max: 200, message: '长度在 2 到 200 个字符', trigger: 'blur' }
  ]
}

// 在 script setup 部分添加
const currentRating = ref(0)

// 获取列表数据
const getList = async () => {
  try {
    await serviceReviewStore.getReviewList(queryParams)
  } catch (error) {
    ElMessage.error(error.message || '获取评价列表失败')
  }
}

// 搜索按钮操作
const handleSearch = () => {
  queryParams.current = 1
  getList()
}

// 重置按钮操作
const handleReset = () => {
  queryParams.serviceId = null
  queryParams.status = ''
  handleSearch()
}

// 多选框选中数据
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
  multiple.value = !selection.length
}

// 查看按钮操作
const handleView = (row) => {
  dialogType.value = 'view'
  currentEvaluation.value = row
  currentRating.value = row.rating || 0
  dialogVisible.value = true
}

// 回复按钮操作
const handleReply = (row) => {
  dialogType.value = 'reply'
  currentEvaluation.value = row
  replyForm.id = row.id
  replyForm.replyContent = ''
  dialogVisible.value = true
}

// 提交回复
const submitReply = async () => {
  if (!replyFormRef.value) return
  
  await replyFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await serviceReviewStore.replyReview(replyForm)
        ElMessage.success('回复成功')
        dialogVisible.value = false
        getList()
      } catch (error) {
        ElMessage.error(error.message || '回复失败')
      }
    }
  })
}

// 删除按钮操作
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`是否确认删除评价编号为"${row.id}"的评价记录？`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await serviceReviewStore.deleteReview(row.id)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除操作失败')
    }
  }
}

// 新增：批量删除操作
const handleBulkDelete = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请至少选择一条数据进行删除')
    return
  }
  try {
    await ElMessageBox.confirm(`是否确认删除选中的 ${selectedIds.value.length} 条评价记录？`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    for (const id of selectedIds.value) {
      await serviceReviewStore.deleteReview(id)
    }
    ElMessage.success('批量删除成功')
    getList()
    selectedIds.value = []
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '批量删除失败')
    }
  }
}

// 页面加载时获取数据
onMounted(() => {
  getList()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.mb8 {
  margin-bottom: 8px;
}

.small-padding {
  padding-left: 5px;
  padding-right: 5px;
}

.fixed-width .el-button--small {
  padding: 7px 10px;
  min-width: 60px;
}

.review-content {
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 10px;
  line-height: 1.5;
}
</style>