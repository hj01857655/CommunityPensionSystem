<template>
  <div class="notification-center">
    <el-popover
        :width="320"
        placement="bottom-end"
        popper-class="notification-popover"
        trigger="click"
    >
      <template #reference>
        <el-badge :hidden="unreadCount === 0" :max="99" :value="unreadCount">
          <el-button :icon="Bell" circle class="notification-btn"/>
        </el-badge>
      </template>

      <div class="notification-header">
        <h3>通知中心</h3>
        <div class="notification-actions">
          <el-button :disabled="notifications.length === 0" link type="primary" @click="markAllAsRead">
            全部已读
          </el-button>
          <el-button :disabled="notifications.length === 0" link type="danger" @click="clearAll">
            清空
          </el-button>
        </div>
      </div>

      <el-divider/>

      <div v-if="notifications.length > 0" class="notification-list">
        <div
            v-for="notification in notifications"
            :key="notification.id"
            :class="{ 'notification-unread': notification.status === 0 }"
            class="notification-item"
            @click="handleNotificationClick(notification)"
        >
          <div class="notification-icon">
            <el-icon :color="getIconColor(notification.type)" :size="20">
              <component :is="getIconComponent(notification.type)"/>
            </el-icon>
          </div>
          <div class="notification-content">
            <div class="notification-title">{{ notification.title }}</div>
            <div class="notification-message">{{ notification.content }}</div>
            <div class="notification-time">{{ formatTime(notification.createTime) }}</div>
          </div>
          <div class="notification-actions">
            <el-button
                v-if="notification.status === 0"
                circle
                size="small"
                type="primary"
                @click.stop="markAsRead(notification)"
            >
              <el-icon>
                <Check/>
              </el-icon>
            </el-button>
            <el-button
                circle
                size="small"
                type="danger"
                @click.stop="removeNotification(notification)"
            >
              <el-icon>
                <Delete/>
              </el-icon>
            </el-button>
          </div>
        </div>
      </div>

      <div v-else class="notification-empty">
        <el-empty description="暂无通知"/>
      </div>

      <div v-if="notifications.length > 0" class="notification-footer">
        <el-pagination
            :current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            layout="prev, pager, next"
            small
            @current-change="handlePageChange"
        />
      </div>
    </el-popover>
  </div>
</template>

<script setup>
import { Bell, Check, CircleCheckFilled, Delete, InfoFilled, SuccessFilled, Warning } from '@element-plus/icons-vue';
import { formatDistanceToNow } from 'date-fns';
import { zhCN } from 'date-fns/locale';
import { ElMessage, ElMessageBox } from 'element-plus';
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

// 通知列表
const notifications = ref([]);
// 总通知数
const total = ref(0);
// 当前页
const currentPage = ref(1);
// 每页显示数量
const pageSize = ref(5);
// 路由
const router = useRouter();

// 未读通知数量
const unreadCount = computed(() => {
  return notifications.value.filter(item => item.status === 0).length;
});

// 获取通知列表
const fetchNotifications = async () => {
  try {
    // 这里应该调用API获取通知列表
    // 示例代码，实际应该替换为真实API调用
    const response = {
      code: 200,
      data: {
        records: [
          {
            id: 1,
            title: '系统通知',
            content: '欢迎使用社区养老系统',
            status: 0,
            type: 'SYSTEM',
            createTime: new Date().toISOString()
          },
          {
            id: 2,
            title: '健康提醒',
            content: '您今天还没有记录血压数据',
            status: 0,
            type: 'HEALTH',
            createTime: new Date(Date.now() - 3600000).toISOString()
          }
        ],
        total: 2
      }
    };

    if (response.code === 200) {
      notifications.value = response.data.records;
      total.value = response.data.total;
    }
  } catch (error) {
    console.error('获取通知列表失败:', error);
    ElMessage.error('获取通知列表失败');
  }
};

// 标记通知为已读
const markAsRead = async (notification) => {
  try {
    // 这里应该调用API标记通知为已读
    // 示例代码，实际应该替换为真实API调用
    const response = {
      code: 200
    };

    if (response.code === 200) {
      notification.status = 1;
      ElMessage.success('标记已读成功');
    }
  } catch (error) {
    console.error('标记已读失败:', error);
    ElMessage.error('标记已读失败');
  }
};

// 标记所有通知为已读
const markAllAsRead = async () => {
  try {
    // 这里应该调用API标记所有通知为已读
    // 示例代码，实际应该替换为真实API调用
    const response = {
      code: 200
    };

    if (response.code === 200) {
      notifications.value.forEach(item => {
        item.status = 1;
      });
      ElMessage.success('全部标记已读成功');
    }
  } catch (error) {
    console.error('全部标记已读失败:', error);
    ElMessage.error('全部标记已读失败');
  }
};

// 删除通知
const removeNotification = async (notification) => {
  try {
    // 这里应该调用API删除通知
    // 示例代码，实际应该替换为真实API调用
    const response = {
      code: 200
    };

    if (response.code === 200) {
      const index = notifications.value.findIndex(item => item.id === notification.id);
      if (index !== -1) {
        notifications.value.splice(index, 1);
      }
      ElMessage.success('删除通知成功');
    }
  } catch (error) {
    console.error('删除通知失败:', error);
    ElMessage.error('删除通知失败');
  }
};

// 清空所有通知
const clearAll = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有通知吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });

    // 这里应该调用API清空所有通知
    // 示例代码，实际应该替换为真实API调用
    const response = {
      code: 200
    };

    if (response.code === 200) {
      notifications.value = [];
      total.value = 0;
      ElMessage.success('清空通知成功');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清空通知失败:', error);
      ElMessage.error('清空通知失败');
    }
  }
};

// 处理通知点击
const handleNotificationClick = (notification) => {
  // 标记为已读
  if (notification.status === 0) {
    markAsRead(notification);
  }

  // 根据通知类型跳转到不同页面
  switch (notification.type) {
    case 'HEALTH':
      router.push('/health');
      break;
    case 'SERVICE':
      router.push('/service');
      break;
    case 'ACTIVITY':
      router.push('/activity');
      break;
    default:
      // 默认不跳转
      break;
  }
};

// 处理分页
const handlePageChange = (page) => {
  currentPage.value = page;
  fetchNotifications();
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return '';
  try {
    return formatDistanceToNow(new Date(time), {
      addSuffix: true,
      locale: zhCN
    });
  } catch (error) {
    console.error('时间格式化失败:', error);
    return time;
  }
};

// 获取图标组件
const getIconComponent = (type) => {
  switch (type) {
    case 'SYSTEM':
      return InfoFilled;
    case 'HEALTH':
      return Warning;
    case 'SERVICE':
      return SuccessFilled;
    case 'ACTIVITY':
      return SuccessFilled;
    case 'EMERGENCY':
      return CircleCheckFilled;
    default:
      return InfoFilled;
  }
};

// 获取图标颜色
const getIconColor = (type) => {
  switch (type) {
    case 'SYSTEM':
      return '#409EFF';
    case 'HEALTH':
      return '#E6A23C';
    case 'SERVICE':
      return '#67C23A';
    case 'ACTIVITY':
      return '#67C23A';
    case 'EMERGENCY':
      return '#F56C6C';
    default:
      return '#909399';
  }
};

// 组件挂载时获取通知列表
onMounted(() => {
  fetchNotifications();
});
</script>

<style scoped>
.notification-center {
  display: inline-block;
}

.notification-btn {
  font-size: 18px;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 10px;
}

.notification-header h3 {
  margin: 0;
  font-size: 16px;
}

.notification-actions {
  display: flex;
  gap: 10px;
}

.notification-list {
  max-height: 400px;
  overflow-y: auto;
}

.notification-item {
  display: flex;
  padding: 10px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background-color 0.3s;
}

.notification-item:hover {
  background-color: #f5f7fa;
}

.notification-unread {
  background-color: #ecf5ff;
}

.notification-icon {
  margin-right: 10px;
  display: flex;
  align-items: center;
}

.notification-content {
  flex: 1;
  overflow: hidden;
}

.notification-title {
  font-weight: bold;
  margin-bottom: 5px;
}

.notification-message {
  color: #606266;
  font-size: 14px;
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.notification-time {
  color: #909399;
  font-size: 12px;
}

.notification-empty {
  padding: 20px 0;
}

.notification-footer {
  padding: 10px 0;
  display: flex;
  justify-content: center;
}
</style>
