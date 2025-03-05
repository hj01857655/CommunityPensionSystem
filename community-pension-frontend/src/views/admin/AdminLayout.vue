<template>
  <div class="admin-layout">
    <el-container class="container">
      <!-- 侧边栏 -->
      <el-aside :width="isCollapse ? '64px' : '220px'" class="aside">
        <div class="logo">
          <template v-if="!isCollapse">
            <h2>社区养老系统</h2>
            <p>管理后台</p>
          </template>
          <template v-else>
            <el-icon class="logo-icon">
              <HomeFilled />
            </el-icon>
          </template>
        </div>
        <el-menu :default-active="activeMenu" class="el-menu-vertical" :collapse="isCollapse" background-color="#304156"
          text-color="#bfcbd9" active-text-color="#409EFF" router>
          <el-sub-menu index="dashboard">
            <template #title>
              <el-icon>
                <Odometer />
              </el-icon>
              <span>数据分析看板</span>
            </template>
            <el-menu-item index="/admin/analysis/dashboard">
              <template #title>
                <el-icon>
                  <Odometer />
                </el-icon>
                <span>仪表盘</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/admin/analysis/activity">
              <template #title>
                <el-icon>
                  <User />
                </el-icon>
                <span>用户活跃度分析</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/admin/analysis/service">
              <template #title>
                <el-icon>
                  <Service />
                </el-icon>
                <span>服务预约统计</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/admin/analysis/health">
              <template #title>
                <el-icon>
                  <FirstAidKit />
                </el-icon>
                <span>健康数据趋势分析</span>
              </template>
            </el-menu-item>
          </el-sub-menu>


          <el-sub-menu index="user">
            <template #title>
              <el-icon>
                <User />
              </el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/admin/users/user">用户列表</el-menu-item>
            <el-menu-item index="/admin/users/elder">老人管理</el-menu-item>
            <el-menu-item index="/admin/users/kin">亲属管理</el-menu-item>
            <el-menu-item index="/admin/users/staff">社区工作人员管理</el-menu-item>
          </el-sub-menu>
          <!-- 角色管理 -->
          <el-sub-menu index="roles">
            <template #title>
              <el-icon>
                <User />
              </el-icon>
              <span>角色管理</span>
            </template>
            <el-menu-item index="/admin/roles/role">角色列表</el-menu-item>
          </el-sub-menu>
          <!-- 服务预约管理 -->
          <el-sub-menu index="services">
            <template #title>
              <el-icon>
                <Service />
              </el-icon>
              <span>服务预约管理</span>
            </template>
            <el-menu-item index="/admin/services/serviceType">服务类型管理</el-menu-item>
            <el-menu-item index="/admin/services/serviceAppointment">服务预约管理</el-menu-item>
            <el-menu-item index="/admin/services/serviceEvaluation">服务评价管理</el-menu-item>
          </el-sub-menu>
          <!-- 健康监测管理 -->
          <el-sub-menu index="/admin/health">
            <template #title>
              <el-icon>
                <FirstAidKit />
              </el-icon>
              <span>健康监测管理</span>
            </template>
            <el-menu-item index="/admin/health/heal">健康监测管理</el-menu-item>
            <el-menu-item index="/admin/health/healthAssessment">健康评估管理</el-menu-item>
            <el-menu-item index="/admin/health/healthIntervention">健康干预管理</el-menu-item>
          </el-sub-menu>
          <!-- 社区活动管理 -->
          <el-sub-menu index="/admin/activity">
            <template #title>
              <el-icon>
                <Calendar />
              </el-icon>
              <span>社区活动管理</span>
            </template>
            <el-menu-item index="/admin/activity/type">活动类型管理</el-menu-item>
            <el-menu-item index="/admin/activity/registration">活动报名管理</el-menu-item>
            <el-menu-item index="/admin/activity/checkin">活动签到管理</el-menu-item>
          </el-sub-menu>
          <!-- 通知公告管理 -->
          <el-sub-menu index="/admin/notice">
            <template #title>
              <el-icon>
                <Bell />
              </el-icon>
              <span>通知公告管理</span>
            </template>
            <el-menu-item index="/admin/notice/list">通知公告列表</el-menu-item>
            <el-menu-item index="/admin/notice/publish">通知公告发布</el-menu-item>
          </el-sub-menu>
          
          <!-- 系统设置 -->
          <el-sub-menu index="/admin/settings">
            <template #title>
              <el-icon>
                <Setting />
              </el-icon>
              <span>系统设置</span>
            </template>
            <el-menu-item index="/admin/settings/systemSetting">系统设置管理</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>

      <!-- 主要内容区 -->
      <el-container class="main-container">
        <!-- 顶部导航栏 -->
        <el-header class="header">
          <div class="header-left">
            <el-icon class="toggle-icon" @click="toggleSidebar">
              <component :is="isCollapse ? 'Expand' : 'Fold'" />
            </el-icon>
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/admin/analysis/dashboard' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item>{{ currentRoute }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-dropdown trigger="click" @command="handleCommand">
              <span class="user-info">
                <el-avatar :size="32" :src="avatarUrl" />
                <span class="username">{{ username }}</span>
                <el-icon>
                  <ArrowDown />
                </el-icon>
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
        </el-header>

        <!-- 内容区域 -->
        <el-main class="main">
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus';
import {
  Odometer, User, Service, FirstAidKit, Calendar, Bell, DataAnalysis, Setting,
  Fold, Expand, ArrowDown, HomeFilled
} from '@element-plus/icons-vue';

const router = useRouter()
const route = useRoute()
const isCollapse = ref(false)
const username = ref('管理员')
const avatarUrl = ref('https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png')

onMounted(() => {
  // 从本地存储获取用户信息
  try {
    const userInfo = localStorage.getItem('userInfo');
    const token = localStorage.getItem('token');

    if (!userInfo || !token) {
      ElMessage.error('请先登录');
      return;
    }

    try {
      const parsedUserInfo = JSON.parse(userInfo);
      if (!parsedUserInfo || typeof parsedUserInfo !== 'object') {
        throw new Error('无效的用户信息格式');
      }
      username.value = parsedUserInfo.username || '管理员';

      ElNotification({
        title: '登录成功',
        message: '欢迎回来，' + username.value,
        type: 'success',
        duration: 2000
      });
    } catch (error) {
      console.error('获取用户信息失败:', error);
      ElMessage.error('获取用户信息失败，请重新登录');
    }
  }catch (error) {
    console.error('获取用户信息失败:', error);
    ElMessage.error('获取用户信息失败，请重新登录');
  }
})
// 计算当前路由名称
const currentRoute = computed(() => {
  const matched = route.matched
  if (matched.length > 1) {
    return matched[matched.length - 1].meta.title || '未知页面'
  }
  return '仪表盘'
})

// 计算当前激活的菜单项
const activeMenu = computed(() => {
  return route.path
})

// 切换侧边栏
const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

// 处理下拉菜单命令
const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      // 清除登录信息
      localStorage.removeItem('userInfo')
      localStorage.removeItem('roleId')
      ElMessage.success('退出登录成功')
      router.push('/login')
    }).catch(() => { })
  } else if (command === 'profile') {
    // 跳转到个人信息页面
    router.push('/admin/profile')
  } else if (command === 'password') {
    // 跳转到修改密码页面
    ElMessage.info('功能开发中')
  }
}


</script>

<style scoped>
.admin-layout {
  height: 100vh;
  width: 100%;
}

.container {
  height: 100%;
}

.aside {
  background-color: #304156;
  color: #bfcbd9;
  transition: width 0.3s ease;
  overflow-x: hidden;
}

.logo {
  height: 60px;
  padding: 10px 0;
  text-align: center;
  color: #fff;
  background-color: #263445;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  transition: all 0.3s ease;
}

.logo h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  white-space: nowrap;
  transition: all 0.3s ease;
}

.logo p {
  margin: 5px 0 0;
  font-size: 12px;
  opacity: 0.8;
  transition: all 0.3s ease;
}

.logo-icon {
  font-size: 24px;
  color: #409EFF;
  animation: logo-spin 0.5s ease-in-out;
}

@keyframes logo-spin {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}

.el-menu-vertical {
  border-right: none;
  min-height: calc(100vh - 60px);
  transition: width 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.el-menu-vertical:not(.el-menu--collapse) {
  width: 220px;
}

.el-menu--collapse {
  width: 64px;
}

.el-menu-vertical :deep(.el-sub-menu__title),
.el-menu-vertical :deep(.el-menu-item) {
  transition: background-color 0.3s, color 0.3s, padding 0.3s;
  overflow: hidden;
}

.el-menu-vertical :deep(.el-sub-menu__title:hover),
.el-menu-vertical :deep(.el-menu-item:hover) {
  background-color: #263445 !important;
}

.el-menu-vertical :deep(.el-menu-item.is-active) {
  background-color: #1f2d3d !important;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
}

.header {
  background-color: #fff;
  color: #333;
  line-height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  padding: 0 20px;
  transition: all 0.3s ease;
}

.header-left {
  display: flex;
  align-items: center;
}

.toggle-icon {
  font-size: 20px;
  cursor: pointer;
  margin-right: 20px;
}

.header-right {
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
  color: #333;
}

.main {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>