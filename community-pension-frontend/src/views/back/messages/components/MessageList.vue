<template>
  <div class="message-list">
    <el-empty v-if="messages.length === 0" description="暂无消息" />
    <div v-else class="message-items">
      <el-card 
        v-for="message in messages" 
        :key="message.id" 
        class="message-card"
        :class="{ 'unread': !message.read }"
        shadow="hover"
      >
        <div class="message-header">
          <div class="message-info">
            <span class="message-sender">{{ message.sender }}</span>
            <span class="message-time">{{ message.createTime }}</span>
          </div>
          <div class="message-status">
            <el-tag v-if="!message.read" type="danger" size="small">未读</el-tag>
          </div>
        </div>
        <div class="message-title">{{ message.title }}</div>
        <div class="message-preview">{{ message.content }}</div>
        <div class="message-actions">
          <el-button link @click="viewDetail(message.id)">查看详情</el-button>
          <el-divider direction="vertical" />
          <el-button v-if="!message.read" link @click="markAsRead(message.id)">标为已读</el-button>
          <el-divider direction="vertical" v-if="!message.read" />
          <el-button link @click="deleteMessage(message.id)">删除</el-button>
        </div>
      </el-card>
    </div>
    
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { useMessageStore } from '@/stores/back/messageStore'
import { storeToRefs } from 'pinia'

const props = defineProps({
  type: {
    type: String,
    default: 'all', // 'all', 'read', 'unread'
    required: true
  }
})

const router = useRouter()
const messageStore = useMessageStore()
const { messages, loading, pagination } = storeToRefs(messageStore)

const currentPage = computed({
  get: () => pagination.value.current,
  set: (val) => pagination.value.current = val
})

const pageSize = computed({
  get: () => pagination.value.size,
  set: (val) => pagination.value.size = val
})

const total = computed(() => pagination.value.total)

// 从后端获取消息列表
const fetchMessages = async () => {
  try {
    await messageStore.fetchMessages({
      current: currentPage.value,
      size: pageSize.value,
      type: props.type
    })
  } catch (error) {
    ElMessage.error('获取消息列表失败：' + error.message)
  }
}

// 查看消息详情
const viewDetail = (id) => {
  router.push(`/admin/messages/${id}`)
}

// 标记消息为已读
const markAsRead = async (id) => {
  try {
    await messageStore.markAsRead(id)
    
    // 如果当前是未读列表，则需要重新加载
    if (props.type === 'unread') {
      fetchMessages()
    }
  } catch (error) {
    // 错误已在store中处理
  }
}

// 删除消息
const deleteMessage = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条消息吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 发送WebSocket消息删除消息
    messageStore.sendUserMessage({
      type: 'deleteMessage',
      data: { id }
    })
    
    // 从列表中移除
    fetchMessages() // 重新加载消息列表
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + error.message)
    }
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchMessages()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchMessages()
}

// 监听类型变化
watch(() => props.type, () => {
  currentPage.value = 1
  fetchMessages()
})

onMounted(() => {
  fetchMessages()
})
</script>

<style scoped>
.message-list {
  margin-top: 20px;
}

.message-items {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.message-card {
  transition: all 0.3s;
}

.message-card.unread {
  border-left: 3px solid #f56c6c;
  background-color: #fef6f6;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.message-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.message-sender {
  font-weight: bold;
  color: #409EFF;
}

.message-time {
  color: #909399;
  font-size: 12px;
}

.message-title {
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 8px;
}

.message-preview {
  color: #606266;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.message-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
