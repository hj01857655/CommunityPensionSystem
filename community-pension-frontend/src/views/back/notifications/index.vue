<template>
  <div class="notifications-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>通知中心</span>
          <el-button type="primary" @click="markAllAsRead">全部标为已读</el-button>
        </div>
      </template>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="全部通知" name="all">
          <notification-list :type="'all'" />
        </el-tab-pane>
        <el-tab-pane label="未读通知" name="unread">
          <notification-list :type="'unread'" />
        </el-tab-pane>
        <el-tab-pane label="已读通知" name="read">
          <notification-list :type="'read'" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import NotificationList from './components/NotificationList.vue'

const activeTab = ref('all')

// 标记所有通知为已读
const markAllAsRead = async () => {
  try {
    // 这里应该调用API来标记所有通知为已读
    // await api.markAllNotificationsAsRead()
    ElMessage.success('所有通知已标记为已读')
    // 刷新通知列表
  } catch (error) {
    ElMessage.error('操作失败：' + error.message)
  }
}
</script>

<style scoped>
.notifications-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
