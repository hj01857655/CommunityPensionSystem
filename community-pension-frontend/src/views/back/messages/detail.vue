<template>
  <div class="message-detail">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>消息详情</span>
          <div>
            <el-button type="primary" @click="goBack">返回列表</el-button>
            <el-button v-if="!message.read" type="success" @click="markAsRead">标为已读</el-button>
            <el-button type="danger" @click="deleteMessage">删除消息</el-button>
          </div>
        </div>
      </template>
      
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="10" animated />
      </div>
      
      <div v-else-if="!message.id" class="empty-container">
        <el-empty description="未找到消息详情" />
      </div>
      
      <div v-else class="message-content">
        <div class="message-header-info">
          <h2 class="message-title">{{ message.title }}</h2>
          <div class="message-meta">
            <span class="message-sender">发送人: {{ message.sender }}</span>
            <span class="message-time">时间: {{ message.createTime }}</span>
            <el-tag v-if="message.read" type="success">已读</el-tag>
            <el-tag v-else type="danger">未读</el-tag>
          </div>
        </div>
        
        <div class="message-body">
          <p v-html="message.content"></p>
        </div>
        
        <div v-if="message.attachments && message.attachments.length > 0" class="attachments">
          <h3>附件列表</h3>
          <ul>
            <li v-for="(attachment, index) in message.attachments" :key="index">
              <el-link type="primary" :href="attachment.url" target="_blank">
                {{ attachment.name }}
              </el-link>
            </li>
          </ul>
        </div>
        
        <div class="reply-section" v-if="message.canReply">
          <h3>回复消息</h3>
          <el-form>
            <el-form-item>
              <el-input
                v-model="replyContent"
                type="textarea"
                :rows="4"
                placeholder="请输入回复内容"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="sendReply">发送回复</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const message = ref({})
const loading = ref(true)
const replyContent = ref('')

// 获取消息详情
const fetchMessageDetail = async () => {
  const id = route.params.id
  if (!id) {
    ElMessage.error('消息ID不能为空')
    return
  }
  
  loading.value = true
  try {
    // 这里应该调用API获取消息详情
    // const response = await api.getMessageDetail(id)
    // message.value = response.data
    
    // 模拟数据
    setTimeout(() => {
      message.value = {
        id: id,
        sender: `用户${id}`,
        title: `消息标题 ${id}`,
        content: `<p>这是消息详情内容，ID: ${id}</p><p>这是一个示例消息详情，包含了消息的完整内容。</p><p>消息内容可以包含HTML格式的富文本。</p>`,
        createTime: new Date().toLocaleString(),
        read: false,
        canReply: true,
        attachments: [
          { name: '附件1.pdf', url: '#' },
          { name: '附件2.docx', url: '#' }
        ]
      }
      loading.value = false
    }, 1000)
  } catch (error) {
    ElMessage.error('获取消息详情失败：' + error.message)
    loading.value = false
  }
}

// 标记为已读
const markAsRead = async () => {
  try {
    // 这里应该调用API标记消息为已读
    // await api.markMessageAsRead(message.value.id)
    message.value.read = true
    ElMessage.success('已标记为已读')
  } catch (error) {
    ElMessage.error('操作失败：' + error.message)
  }
}

// 删除消息
const deleteMessage = async () => {
  try {
    await ElMessageBox.confirm('确定要删除这条消息吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 这里应该调用API删除消息
    // await api.deleteMessage(message.value.id)
    ElMessage.success('删除成功')
    goBack()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + error.message)
    }
  }
}

// 发送回复
const sendReply = async () => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('回复内容不能为空')
    return
  }
  
  try {
    // 这里应该调用API发送回复
    // await api.replyMessage({
    //   messageId: message.value.id,
    //   content: replyContent.value
    // })
    ElMessage.success('回复发送成功')
    replyContent.value = ''
  } catch (error) {
    ElMessage.error('发送回复失败：' + error.message)
  }
}

// 返回列表
const goBack = () => {
  router.push('/back/messages')
}

onMounted(() => {
  fetchMessageDetail()
})
</script>

<style scoped>
.message-detail {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.loading-container, .empty-container {
  padding: 40px 0;
  text-align: center;
}

.message-header-info {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #EBEEF5;
}

.message-title {
  margin-top: 0;
  margin-bottom: 16px;
  font-size: 22px;
  font-weight: bold;
}

.message-meta {
  display: flex;
  align-items: center;
  gap: 15px;
  color: #909399;
}

.message-sender {
  font-weight: bold;
}

.message-body {
  line-height: 1.6;
  color: #303133;
  padding: 10px 0;
  min-height: 200px;
}

.attachments {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #EBEEF5;
}

.attachments h3 {
  font-size: 16px;
  margin-bottom: 10px;
}

.attachments ul {
  list-style: none;
  padding-left: 0;
}

.attachments li {
  margin-bottom: 8px;
}

.reply-section {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #EBEEF5;
}

.reply-section h3 {
  font-size: 16px;
  margin-bottom: 15px;
}
</style>
