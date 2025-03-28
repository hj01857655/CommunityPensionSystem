<template>
  <div class="notice-detail-view">
    <el-card class="content-card" shadow="hover" v-loading="loading">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon class="header-icon"><Bell /></el-icon>
            <h3 class="title">通知详情</h3>
          </div>
          <el-button type="primary" link @click="$router.back()">
            <el-icon><ArrowLeft /></el-icon>
            返回列表
          </el-button>
        </div>
      </template>
      
      <div v-if="noticeDetail" class="notice-detail-content">
        <div class="notice-header">
          <div class="title-section">
            <h2 class="notice-title">{{ noticeDetail.title }}</h2>
          </div>
          <div class="meta-section">
            <div class="meta-item">
              <el-icon><Calendar /></el-icon>
              <span>发布时间：{{ formatDateTime(noticeDetail.publishTime || noticeDetail.createTime) }}</span>
            </div>
          </div>
        </div>
        
        <el-divider class="custom-divider" />
        
        <div class="notice-body">
          <div class="content-wrapper">
            {{ noticeDetail.content }}
          </div>
        </div>
      </div>
      
      <div v-else class="empty-content">
        <el-empty description="暂无通知详情">
          <template #image>
            <el-icon class="empty-icon"><Bell /></el-icon>
          </template>
        </el-empty>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useNoticeStore } from '@/stores/fore/noticeStore'
import { storeToRefs } from 'pinia'
import { Bell, Calendar, ArrowLeft } from '@element-plus/icons-vue'

const route = useRoute()
const noticeStore = useNoticeStore()
const { noticeDetail, loading } = storeToRefs(noticeStore)

// 获取通知详情
const fetchNoticeDetail = async () => {
  const id = route.params.id
  if (id) {
    await noticeStore.fetchNoticeDetail(id)
  }
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  switch (status) {
    case 0: return 'info'
    case 1: return 'success'
    case 2: return 'danger'
    default: return 'info'
  }
}

// 格式化日期时间
const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  const date = new Date(dateTimeStr)
  return `${date.getFullYear()}-${padZero(date.getMonth() + 1)}-${padZero(date.getDate())} ${padZero(date.getHours())}:${padZero(date.getMinutes())}`
}

// 补零
const padZero = (num) => {
  return num < 10 ? `0${num}` : num
}

onMounted(() => {
  fetchNoticeDetail()
})
</script>

<style scoped>
.notice-detail-view {
  flex: 1;
  height: 100%;
  background-color: #f5f7fa;
  display: flex;
  flex-direction: column;
  padding: 20px;
}

.content-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  width: 100%;
  margin: 0;
  border-radius: 0;
  transition: all 0.3s ease;
  background-color: #fff;
}

.content-card:hover {
  box-shadow: none;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 10px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-icon {
  font-size: 20px;
  color: #409EFF;
}

.title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.notice-detail-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
  width: 100%;
}

.notice-header {
  margin-bottom: 20px;
  width: 100%;
  text-align: center;
}

.title-section {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 15px;
  width: 100%;
}

.notice-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  line-height: 1.4;
  text-align: center;
}

.meta-section {
  display: flex;
  justify-content: center;
  gap: 20px;
  color: #909399;
  font-size: 14px;
  width: 100%;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.custom-divider {
  margin: 20px 0;
  border-color: #e4e7ed;
}

.notice-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  width: 100%;
}

.content-wrapper {
  flex: 1;
  line-height: 1.8;
  color: #606266;
  font-size: 16px;
  white-space: pre-wrap;
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
  width: 100%;
  text-align: center;
}

.empty-content {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background-color: #fff;
  border-radius: 8px;
  width: 100%;
}

.empty-icon {
  font-size: 48px;
  color: #909399;
  margin-bottom: 20px;
}

:deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 0;
  width: 100%;
}

:deep(.el-card__header) {
  padding: 15px 20px;
  border-bottom: 1px solid #ebeef5;
  background-color: #fff;
  width: 100%;
}

:deep(.el-button--primary.is-link) {
  font-size: 14px;
  padding: 0;
  height: auto;
}

:deep(.el-button--primary.is-link:hover) {
  color: #66b1ff;
}
</style>

