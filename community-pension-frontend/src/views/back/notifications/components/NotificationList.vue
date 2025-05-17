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
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const props = defineProps({
  type: {
    type: String,
    default: 'all', // 'all', 'read', 'unread'
    required: true
  }
})

const router = useRouter()
const notifications = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 模拟数据，实际项目中应该从API获取
const fetchNotifications = async () => {
  try {
    // 这里应该调用API获取通知列表
    // const response = await api.getNotifications({
    //   page: currentPage.value,
    //   size: pageSize.value,
    //   type: props.type
    // })
    
    // 模拟数据
    const mockData = Array.from({ length: 15 }, (_, index) => ({
      id: index + 1,
      title: `通知标题 ${index + 1}`,
      content: `这是通知内容，这是一个示例通知，ID: ${index + 1}`,
      createTime: new Date(Date.now() - index * 86400000).toLocaleString(),
      read: props.type === 'read' ? true : (props.type === 'unread' ? false : (index % 2 === 0))
    }))
    
    notifications.value = mockData
    total.value = 50 // 模拟总数
  } catch (error) {
    ElMessage.error('获取通知列表失败：' + error.message)
  }
}

// 查看通知详情
const viewDetail = (id) => {
  router.push(`/back/notifications/${id}`)
}

// 标记通知为已读
const markAsRead = async (id) => {
  try {
    // 这里应该调用API标记通知为已读
    // await api.markNotificationAsRead(id)
    ElMessage.success('已标记为已读')
    
    // 更新本地数据
    const notification = notifications.value.find(item => item.id === id)
    if (notification) {
      notification.read = true
    }
    
    // 如果当前是未读列表，则需要重新加载
    if (props.type === 'unread') {
      fetchNotifications()
    }
  } catch (error) {
    ElMessage.error('操作失败：' + error.message)
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
