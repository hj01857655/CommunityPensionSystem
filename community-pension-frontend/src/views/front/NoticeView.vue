<template>
    <div class="notice-view">
      <el-card class="content-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <h3>通知公告</h3>
            <el-radio-group v-model="filter" size="small">
              <el-radio-button label="all">全部</el-radio-button>
              <el-radio-button label="unread">未读</el-radio-button>
            </el-radio-group>
          </div>
        </template>
        
        <div class="notice-list">
          <div 
            v-for="notice in filteredNotices" 
            :key="notice.id"
            class="notice-item"
            :class="{ unread: !notice.read }"
            @click="handleNoticeClick(notice)"
          >
            <div class="notice-content">
              <h4>{{ notice.title }}</h4>
              <p class="notice-preview">{{ notice.content }}</p>
            </div>
            <div class="notice-meta">
              <span class="notice-time">{{ formatDate(notice.date) }}</span>
              <el-tag 
                size="small"
                :type="notice.type === 'important' ? 'danger' : 'info'"
              >
                {{ notice.type === 'important' ? '重要' : '普通' }}
              </el-tag>
            </div>
          </div>
        </div>
      </el-card>
  
      <el-dialog
        v-model="dialogVisible"
        :title="selectedNotice.title"
        width="50%"
      >
        <div class="notice-detail">
          <p class="notice-time">发布时间：{{ formatDate(selectedNotice.date) }}</p>
          <div class="notice-content" v-html="selectedNotice.content"></div>
        </div>
      </el-dialog>
    </div>
  </template>
  
  <script setup>
  import { ref, computed } from 'vue'
  import dayjs from 'dayjs'
  
  const filter = ref('all')
  const dialogVisible = ref(false)
  const selectedNotice = ref({})
  
  const notices = ref([
    {
      id: 1,
      title: '关于开展全民健康体检的通知',
      content: '为了促进社区居民身体健康，我们将于本月底开展全民健康体检活动...',
      date: '2025-02-23',
      type: 'important',
      read: false
    },
    {
      id: 2,
      title: '春节期间社区服务安排',
      content: '春节期间（2025年2月9日至2月15日）社区服务调整安排如下...',
      date: '2025-02-20',
      type: 'normal',
      read: true
    },
    {
      id: 3,
      title: '社区文化活动中心装修通知',
      content: '社区文化活动中心将于3月1日起进行为期两周的装修维护...',
      date: '2025-02-19',
      type: 'normal',
      read: false
    }
  ])
  
  const filteredNotices = computed(() => {
    if (filter.value === 'unread') {
      return notices.value.filter(notice => !notice.read)
    }
    return notices.value
  })
  
  const formatDate = (date) => {
    return dayjs(date).format('YYYY-MM-DD HH:mm')
  }
  
  const handleNoticeClick = (notice) => {
    selectedNotice.value = notice
    dialogVisible.value = true
    if (!notice.read) {
      notice.read = true
    }
  }
  </script>
  
  <style scoped>
  .notice-view {
    width: 100%;
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  h3 {
    margin: 0;
    color: #2c3e50;
    font-weight: 600;
  }
  
  .notice-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
  
  .notice-item {
    padding: 16px;
    border-radius: 8px;
    background: #f8f9fa;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
  }
  
  .notice-item:hover {
    background: #f0f2f5;
    transform: translateX(4px);
  }
  
  .notice-item.unread {
    background: #ecf5ff;
  }
  
  .notice-content {
    flex: 1;
  }
  
  .notice-content h4 {
    margin: 0 0 8px 0;
    color: #2c3e50;
  }
  
  .notice-preview {
    color: #666;
    margin: 0;
    font-size: 14px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
  
  .notice-meta {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    gap: 8px;
    min-width: 100px;
  }
  
  .notice-time {
    color: #999;
    font-size: 12px;
  }
  
  .notice-detail {
    padding: 20px;
  }
  
  .notice-detail .notice-time {
    color: #666;
    margin-bottom: 16px;
  }
  
  .notice-detail .notice-content {
    line-height: 1.6;
  }
  </style>