<template>
  <div class="notification-detail">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>通知详情</span>
          <div>
            <el-button type="primary" @click="goBack">返回列表</el-button>
            <el-button v-if="!notification.read" type="success" @click="markAsRead">标为已读</el-button>
          </div>
        </div>
      </template>
      
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="10" animated />
      </div>
      
      <div v-else-if="!notification.id" class="empty-container">
        <el-empty description="未找到通知详情" />
      </div>
      
      <div v-else class="notification-content">
        <h2 class="notification-title">{{ notification.title }}</h2>
        <div class="notification-meta">
          <span>发布时间: {{ notification.createTime }}</span>
          <el-tag v-if="notification.read" type="success">已读</el-tag>
          <el-tag v-else type="danger">未读</el-tag>
        </div>
        <div class="notification-body" v-html="notification.content"></div>
        
        <div v-if="notification.attachments && notification.attachments.length > 0" class="attachments">
          <h3>附件列表</h3>
          <ul>
            <li v-for="(attachment, index) in notification.attachments" :key="index">
              <el-link type="primary" :href="attachment.url" target="_blank">
                {{ attachment.name }}
              </el-link>
            </li>
          </ul>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const notification = ref({})
const loading = ref(true)

// 获取通知详情
const fetchNotificationDetail = async () => {
  const id = route.params.id
  if (!id) {
    ElMessage.error('通知ID不能为空')
    return
  }
  
  loading.value = true
  try {
    // 这里应该调用API获取通知详情
    // const response = await api.getNotificationDetail(id)
    // notification.value = response.data
    
    // 模拟数据
    setTimeout(() => {
      notification.value = {
        id: id,
        title: `通知标题 ${id}`,
        content: `<p>这是通知详情内容，ID: ${id}</p><p>这是一个示例通知详情，包含了通知的完整内容。</p><p>通知内容可以包含HTML格式的富文本。</p>`,
        createTime: new Date().toLocaleString(),
        read: false,
        attachments: [
          { name: '附件1.pdf', url: '#' },
          { name: '附件2.docx', url: '#' }
        ]
      }
      loading.value = false
    }, 1000)
  } catch (error) {
    ElMessage.error('获取通知详情失败：' + error.message)
    loading.value = false
  }
}

// 标记为已读
const markAsRead = async () => {
  try {
    // 这里应该调用API标记通知为已读
    // await api.markNotificationAsRead(notification.value.id)
    notification.value.read = true
    ElMessage.success('已标记为已读')
  } catch (error) {
    ElMessage.error('操作失败：' + error.message)
  }
}

// 返回列表
const goBack = () => {
  router.push('/admin/notifications')
}

onMounted(() => {
  fetchNotificationDetail()
})
</script>

<style scoped>
.notification-detail {
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

.notification-title {
  margin-top: 0;
  margin-bottom: 16px;
  font-size: 22px;
  font-weight: bold;
}

.notification-meta {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  color: #909399;
}

.notification-meta .el-tag {
  margin-left: 10px;
}

.notification-body {
  line-height: 1.6;
  color: #303133;
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
</style>
