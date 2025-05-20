<template>
  <div class="messages-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>消息中心</span>
          <el-button type="primary" @click="markAllAsRead">全部标为已读</el-button>
        </div>
      </template>
      
      <el-tabs v-model="activeTab">
        <el-tab-pane label="全部消息" name="all">
          <message-list :type="'all'" />
        </el-tab-pane>
        <el-tab-pane label="未读消息" name="unread">
          <message-list :type="'unread'" />
        </el-tab-pane>
        <el-tab-pane label="已读消息" name="read">
          <message-list :type="'read'" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import MessageList from './components/MessageList.vue'
import { useMessageStore } from '@/stores/back/messageStore'

const messageStore = useMessageStore()
const activeTab = ref('all')

// 标记所有消息为已读
const markAllAsRead = async () => {
  try {
    await messageStore.markAllAsRead()
    // 消息列表组件会自动监听Store中的数据变化
  } catch (error) {
    // 错误已在store中处理
  }
}

// 初始化WebSocket连接
onMounted(() => {
  // 确保WebSocket连接已建立
  messageStore.initWsConnection()
})
</script>

<style scoped>
.messages-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
