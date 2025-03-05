<template>
  <div class="service-evaluation">
    <el-card shadow="hover" class="table-card">
      <template #header>
        <div class="card-header">
          <h3>服务评价管理</h3>
          <div class="header-actions">
            <el-input
              v-model="searchQuery"
              placeholder="搜索服务名称/评价内容"
              class="search-input"
              clearable
              @clear="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </div>
        </div>
      </template>
      
      <el-table
        :data="filteredEvaluations"
        style="width: 100%"
        v-loading="loading"
        border
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="serviceName" label="服务名称" width="150" />
        <el-table-column prop="elderName" label="评价人" width="120" />
        <el-table-column prop="rating" label="评分" width="120">
          <template #default="scope">
            <el-rate
              v-model="scope.row.rating"
              disabled
              show-score
              text-color="#ff9900"
            />
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评价内容" min-width="200" />
        <el-table-column prop="createTime" label="评价时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleView(scope.row)">查看详情</el-button>
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
          :total="totalEvaluations"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 评价详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="评价详情"
      width="600px"
    >
      <div class="evaluation-detail" v-if="currentEvaluation">
        <div class="detail-item">
          <span class="label">服务名称：</span>
          <span>{{ currentEvaluation.serviceName }}</span>
        </div>
        <div class="detail-item">
          <span class="label">评价人：</span>
          <span>{{ currentEvaluation.elderName }}</span>
        </div>
        <div class="detail-item">
          <span class="label">评分：</span>
          <el-rate
            v-model="currentEvaluation.rating"
            disabled
            show-score
            text-color="#ff9900"
          />
        </div>
        <div class="detail-item">
          <span class="label">评价内容：</span>
          <p class="evaluation-content">{{ currentEvaluation.content }}</p>
        </div>
        <div class="detail-item">
          <span class="label">评价时间：</span>
          <span>{{ formatDate(currentEvaluation.createTime) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">服务人员：</span>
          <span>{{ currentEvaluation.staffName }}</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search } from '@element-plus/icons-vue';
import { formatDate } from '@/utils/date';

// 评价列表数据
const evaluations = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const searchQuery = ref('')
const dialogVisible = ref(false)
const currentEvaluation = ref(null)

// 过滤后的评价列表
const filteredEvaluations = computed(() => {
  if (!evaluations.value || !searchQuery.value) {
    return evaluations.value || []
  }
  
  const query = searchQuery.value.toLowerCase()
  return evaluations.value.filter(evaluation => 
    (evaluation.serviceName && evaluation.serviceName.toLowerCase().includes(query)) ||
    (evaluation.content && evaluation.content.toLowerCase().includes(query))
  )
})

// 总评价数
const totalEvaluations = computed(() => {
  return filteredEvaluations.value ? filteredEvaluations.value.length : 0
})

// 获取状态标签类型
const getStatusType = (status) => {
  const typeMap = {
    '已发布': 'success',
    '待审核': 'warning',
    '已隐藏': 'info'
  }
  return typeMap[status] || ''
}

// 搜索评价
const handleSearch = () => {
  currentPage.value = 1
}

// 查看评价详情
const handleView = (row) => {
  currentEvaluation.value = row
  dialogVisible.value = true
}

// 删除评价
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除该评价记录吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 这里应该调用删除评价的API
    ElMessage.success('删除成功')
    evaluations.value = evaluations.value.filter(item => item.id !== row.id)
  }).catch(() => {
    // 取消删除
  })
}

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
.service-evaluation {
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

.evaluation-detail {
  padding: 20px;
}

.detail-item {
  margin-bottom: 15px;
}

.detail-item .label {
  font-weight: bold;
  margin-right: 10px;
  color: #606266;
}

.evaluation-content {
  margin: 10px 0;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  line-height: 1.5;
}
</style>