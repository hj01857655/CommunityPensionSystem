<template>
  <div class="notification-list">
    <el-empty v-if="notifications.length === 0" description="暂无通知" />
    <el-timeline v-else>
      <el-timeline-item
        v-for="notification in notifications"
        :key="notification.id"
        :timestamp="notification.createTime"
        :type="notification.read ? 'primary' : 'warning'"
      >
        <el-card class="notification-card" :class="{ 'unread': !notification.read }">
          <div class="notification-header">
            <span class="notification-title">{{ notification.title }}</span>
            <el-tag v-if="!notification.read" type="danger" size="small">未读</el-tag>
          </div>
          <div class="notification-content">{{ notification.content }}</div>
          <div class="notification-footer">
            <el-button link @click="viewDetail(notification.id)">查看详情</el-button>
            <el-button v-if="!notification.read" link @click="markAsRead(notification.id)">标为已读</el-button>
          </div>
        </el-card>
      </el-timeline-item>
    </el-timeline>
    
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 30, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useNotificationStore } from '@/stores/back/notificationStore'
import { storeToRefs } from 'pinia'

const props = defineProps({
  type: {
    type: String,
    default: 'all', // 'all', 'read', 'unread'
    required: true
  }
})

const router = useRouter()
const notificationStore = useNotificationStore()
const { notifications, loading, pagination } = storeToRefs(notificationStore)

const currentPage = computed({
  get: () => pagination.value.current,
  set: (val) => pagination.value.current = val
})

const pageSize = computed({
  get: () => pagination.value.size,
  set: (val) => pagination.value.size = val
})

const total = computed(() => pagination.value.total)

// 从后端获取通知列表
const fetchNotifications = async () => {
  try {
    await notificationStore.fetchNotifications({
      current: currentPage.value,
      size: pageSize.value,
      type: props.type
    })
  } catch (error) {
    ElMessage.error('获取通知列表失败：' + error.message)
  }
}

// 查看通知详情
const viewDetail = (id) => {
  router.push(`/admin/notifications/${id}`)
}

// 标记通知为已读
const markAsRead = async (id) => {
  try {
    await notificationStore.markAsRead(id)
    
    // 如果当前是未读列表，则需要重新加载
    if (props.type === 'unread') {
      fetchNotifications()
    }
  } catch (error) {
    // 错误已在store中处理
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchNotifications()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchNotifications()
}

// 监听类型变化
watch(() => props.type, () => {
  currentPage.value = 1
  fetchNotifications()
})

onMounted(() => {
  fetchNotifications()
})
</script>

<style scoped>
.notification-list {
  margin-top: 20px;
}

.notification-card {
  margin-bottom: 10px;
}

.notification-card.unread {
  border-left: 3px solid #f56c6c;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.notification-title {
  font-weight: bold;
  font-size: 16px;
}

.notification-content {
  color: #606266;
  margin-bottom: 10px;
}

.notification-footer {
  display: flex;
  justify-content: flex-end;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
