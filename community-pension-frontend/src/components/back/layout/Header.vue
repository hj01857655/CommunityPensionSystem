<template>
  <div class="header">
    <div class="left">
      <el-icon class="toggle-sidebar" @click="toggleSidebar">
        <component :is="isCollapse ? 'Expand' : 'Fold'" />
      </el-icon>
      <breadcrumb />
    </div>
    <div class="right">
      <el-dropdown trigger="click" @command="handleCommand">
        <span class="user-info">
          <el-avatar :size="32" :src="userInfo.avatar" />
          <span class="username">{{ userInfo.userName }}</span>
          <el-icon><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">个人信息</el-dropdown-item>
            <el-dropdown-item command="password">修改密码</el-dropdown-item>
            <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAdminStore } from '@/stores/back/adminStore';
import Breadcrumb from './Breadcrumb.vue';
import { ElMessageBox } from 'element-plus';

const props = defineProps({
  isCollapse: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['toggle-sidebar']);
const router = useRouter();
const adminStore = useAdminStore();

const userInfo = computed(() => adminStore.userInfo || {});

const toggleSidebar = () => {
  emit('toggle-sidebar');
};

const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/admin/profile');
      break;
    case 'password':
      router.push('/admin/password');
      break;
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });
        await adminStore.logout();
        router.push('/login');
      } catch (error) {
        // 用户取消操作
      }
      break;
  }
};
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  padding: 0 20px;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.left {
  display: flex;
  align-items: center;
}

.toggle-sidebar {
  font-size: 20px;
  cursor: pointer;
  margin-right: 20px;
}

.right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin: 0 8px;
  font-size: 14px;
}
</style>