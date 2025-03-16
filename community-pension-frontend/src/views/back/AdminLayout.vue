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
            <el-menu-item index="/admin/health/record">健康档案管理</el-menu-item>
            <el-menu-item index="/admin/health/assessment">健康评估管理</el-menu-item>
            <el-menu-item index="/admin/health/monitor">健康监测管理</el-menu-item>
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
            <el-menu-item index="/admin/activity/registration">社区活动管理</el-menu-item>
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
          <el-sub-menu index="/admin/system">
            <template #title>
              <el-icon>
                <Setting />
              </el-icon>
              <span>系统设置</span>
            </template>
            <el-menu-item index="/admin/system/setting">系统设置</el-menu-item>
            <el-menu-item index="/admin/system/menu">菜单管理</el-menu-item>
            <el-menu-item index="/admin/system/permission">权限管理</el-menu-item>
            <el-menu-item index="/admin/system/role">角色管理</el-menu-item>
            <el-menu-item index="/admin/system/info">个人信息</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>

      <!-- 主体内容区 -->
      <el-container class="main-container">
        <!-- 顶部导航栏 -->
        <el-header class="header">
          <div class="header-left">
            <!-- 折叠按钮 -->
            <el-icon class="toggle-icon" @click="toggleSidebar">
              <component :is="isCollapse ? Expand : Fold" />
            </el-icon>
            <!-- 面包屑导航 -->
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/admin/analysis/dashboard' }">首页</el-breadcrumb-item>
              <!-- 当前路由名称 -->
              <el-breadcrumb-item>{{ currentRoute }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <!-- 右上角用户信息 -->
          <div class="header-right">
            <!-- 下拉菜单 -->
            <el-dropdown trigger="click" @command="handleCommand">
              <span class="user-info">
                <!-- 用户头像 -->
                <el-avatar :size="32" :src="userInfo.avatarUrl" />
                <!-- 用户名 -->
                <span class="username">{{ userInfo.username }}</span>
                <!-- 下拉箭头 -->
                <el-icon>
                  <ArrowDown />
                </el-icon>
              </span>
              <!-- 下拉菜单 -->
              <template #dropdown>
                <el-dropdown-menu>
                 <el-dropdown-item command="profile" @click="navigateTo('/admin/system/info')">个人信息</el-dropdown-item>
                  <el-dropdown-item command="password" @click="navigateTo('/admin/system/password')">修改密码</el-dropdown-item>
                  <el-dropdown-item divided command="logout" @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 主体内容区 -->
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
import { Odometer, User, Service, FirstAidKit, Calendar, Bell, Setting, ArrowDown, HomeFilled, Expand, Fold } from '@element-plus/icons-vue';

// 路由
const router = useRouter()
// 计算当前路由名称
const route = useRoute();
// 侧边栏折叠状态
const isCollapse = ref(false)
const roleMap = ref({
  '1': '老人',
  '2': '亲属',
  '3': '社区工作人员',
  '4': '管理员',
  '5': '访客'
})
const userInfo = ref({})

const navigateTo = (path) => {
  router.push(path)
}
const handleLogout = () => {
  router.push('/admin/login')
  ElMessage.success('退出登录成功')
}
onMounted(()=>{
})

const currentRoute = computed(() => {
  const matched = route.matched;
  if (matched && matched.length > 1) {
    return matched[matched.length - 1].meta.title || '未知页面';
  }
  return '仪表盘';
});

// 计算当前激活的菜单项
const activeMenu = computed(() => {
  return router.path
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
      router.push('/admin/login')
      ElMessage.success('退出登录成功')
    }).catch(() => { })
  } else if (command === 'info') {
    // 跳转到个人信息页面
    router.push('/admin/system/info')
  } else if (command === 'password') {
    // 跳转到修改密码页面
    router.push('/admin/system/password')
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