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
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const props = defineProps({
  type: {
    type: String,
    default: 'all', // 'all', 'read', 'unread'
    required: true
  }
})

const router = useRouter()
const messages = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 模拟数据，实际项目中应该从API获取
const fetchMessages = async () => {
  try {
    // 这里应该调用API获取消息列表
    // const response = await api.getMessages({
    //   page: currentPage.value,
    //   size: pageSize.value,
    //   type: props.type
    // })
    
    // 模拟数据
    const mockData = Array.from({ length: 15 }, (_, index) => ({
      id: index + 1,
      sender: `用户${index + 1}`,
      title: `消息标题 ${index + 1}`,
      content: `这是消息内容，这是一个示例消息，ID: ${index + 1}`,
      createTime: new Date(Date.now() - index * 86400000).toLocaleString(),
      read: props.type === 'read' ? true : (props.type === 'unread' ? false : (index % 2 === 0))
    }))
    
    messages.value = mockData
    total.value = 50 // 模拟总数
  } catch (error) {
    ElMessage.error('获取消息列表失败：' + error.message)
  }
}

// 查看消息详情
const viewDetail = (id) => {
  router.push(`/back/messages/${id}`)
}

// 标记消息为已读
const markAsRead = async (id) => {
  try {
    // 这里应该调用API标记消息为已读
    // await api.markMessageAsRead(id)
    ElMessage.success('已标记为已读')
    
    // 更新本地数据
    const message = messages.value.find(item => item.id === id)
    if (message) {
      message.read = true
    }
    
    // 如果当前是未读列表，则需要重新加载
    if (props.type === 'unread') {
      fetchMessages()
    }
  } catch (error) {
    ElMessage.error('操作失败：' + error.message)
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
    
    // 这里应该调用API删除消息
    // await api.deleteMessage(id)
    ElMessage.success('删除成功')
    
    // 从列表中移除
    messages.value = messages.value.filter(item => item.id !== id)
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
