<template>
  <div class="notice-view">
    <!-- 搜索和筛选区域 -->
    <div class="search-filter-container">
      <el-input
        v-model="searchQuery"
        placeholder="搜索通知标题"
        class="search-input"
        @input="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <!-- 通知列表 -->
    <el-row :gutter="20" class="notice-list">
      <el-col
        v-for="notice in paginatedNotices"
        :key="notice.id"
        :xs="24" :sm="12" :md="8"
      >
        <el-card
          class="notice-card"
          shadow="hover"
          @click="viewNoticeDetail(notice)"
        >
          <div class="notice-header">
            <span class="publish-time">{{ formatDate(notice.publishTime || notice.createTime) }}</span>
          </div>
          <h3 class="notice-title">{{ notice.title }}</h3>
          <div class="notice-content">{{ formatContent(notice.content) }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 分页器 -->
    <div class="pagination-container">
      <Pagination
        v-model:page="currentPage"
        v-model:limit="pageSize"
        :total="filteredNotices.length"
        @pagination="handlePagination"
      />
    </div>

    <!-- 通知详情对话框 -->
    <el-dialog v-model="detailVisible" :title="currentNotice.title" width="60%">
      <div class="notice-content">
        <div class="notice-meta">
          <span>发布时间: {{ formatDateTime(currentNotice.publishTime || currentNotice.createTime) }}</span>
          <span>状态: {{ getStatusName(currentNotice.status) }}</span>
        </div>
        <div class="notice-body">{{ currentNotice.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { useNoticeStore } from '@/stores/fore/noticeStore'
import { storeToRefs } from 'pinia'
import Pagination from '@/components/common/Pagination.vue'

const router = useRouter()
const noticeStore = useNoticeStore()
const { noticeList, loading } = storeToRefs(noticeStore)

// 数据
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const detailVisible = ref(false)
const currentNotice = ref({})

// 获取通知列表
const fetchData = async () => {
  await noticeStore.fetchNoticeList({
    current: currentPage.value,
    size: pageSize.value
  })
}

// 计算属性
const filteredNotices = computed(() => {
  let result = noticeList.value.filter(item => item.status === 1) // 只显示已发布的通知

  // 搜索筛选
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(item =>
      item.title.toLowerCase().includes(query)
    )
  }

  return result
})

const paginatedNotices = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredNotices.value.slice(start, end)
})

// 方法
const getStatusName = (status) => {
  switch (status) {
    case 0: return '草稿'
    case 1: return '已发布'
    case 2: return '已撤回'
    default: return '未知'
  }
}

const getStatusTagType = (status) => {
  switch (status) {
    case 0: return 'info'
    case 1: return 'success'
    case 2: return 'danger'
    default: return 'info'
  }
}

const formatContent = (content) => {
  if (!content) return ''
  return content.length > 100 ? content.substring(0, 100) + '...' : content
}

const formatDate = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  const date = new Date(dateTimeStr)
  return `${date.getFullYear()}-${padZero(date.getMonth() + 1)}-${padZero(date.getDate())}`
}

const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  const date = new Date(dateTimeStr)
  return `${date.getFullYear()}-${padZero(date.getMonth() + 1)}-${padZero(date.getDate())} ${padZero(date.getHours())}:${padZero(date.getMinutes())}`
}

const padZero = (num) => {
  return num < 10 ? `0${num}` : num
}

const viewNoticeDetail = (notice) => {
  router.push({
    name: 'NoticeDetailView',
    params: { id: notice.id }
  })
}

const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

const handlePagination = ({ page, limit }) => {
  currentPage.value = page
  pageSize.value = limit
  fetchData()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.notice-view {
  padding: 20px;
}

.search-filter-container {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}

.notice-list {
  margin-bottom: 20px;
}

.notice-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.notice-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.1);
}

.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.notice-title {
  margin: 10px 0;
  font-size: 16px;
  font-weight: bold;
}

.notice-content {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
}

.pagination-container {
  margin-top: 20px;
  text-align: center;
}

.notice-body {
  line-height: 1.8;
  white-space: pre-wrap;
}
</style>