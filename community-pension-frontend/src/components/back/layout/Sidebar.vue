<template>
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
      <!-- 首页 -->
      <el-menu-item index="/admin/home/" @click="handleMenuClick('/admin/home/')">
        <template #title>
          <el-icon>
            <Odometer />
          </el-icon>
          <span>首页</span>
        </template>
      </el-menu-item>
      <!-- 系统设置 -->
      <el-sub-menu index="/admin/system">
        <template #title>
          <el-icon>
            <Setting />
          </el-icon>
          <span>系统管理中心</span>
        </template>
        <el-menu-item index="/admin/system/user">
          <template #title>
            <el-icon>
              <User />
            </el-icon>
            <span>用户管理</span>
          </template>
        </el-menu-item>
        <el-menu-item index="/admin/system/role">
          <template #title>
            <el-icon>
              <User />
            </el-icon>
            <span>角色管理</span>
          </template>
        </el-menu-item>
        <el-menu-item index="/admin/system/menu">
          <template #title>
            <el-icon>
              <Menu />
            </el-icon>
            <span>菜单管理</span>
          </template>
        </el-menu-item>
        <el-menu-item index="/admin/system/dict">
          <template #title>
            <el-icon>
              <Collection />
            </el-icon>
            <span>字典管理</span>
          </template>
        </el-menu-item>
        <el-menu-item index="/admin/system/setting">
          <template #title>
            <el-icon>
              <Setting />
            </el-icon>
            <span>系统设置</span>
          </template>
        </el-menu-item>
      </el-sub-menu>
      <!-- 数据分析看板 -->

      
      <!-- 服务预约管理 -->
      <el-sub-menu index="/admin/services">
        <template #title>
          <el-icon>
            <Service />
          </el-icon>
          <span>服务管理中心</span>
        </template>
        <el-menu-item index="/admin/services/service">
          <template #title>
            <el-icon>
              <Service />
            </el-icon>
            <span>服务项目管理</span>
          </template>
        </el-menu-item>
        <el-menu-item index="/admin/services/order">
          <template #title>
            <el-icon>
              <Service />
            </el-icon>
            <span>服务工单管理</span>
          </template>
        </el-menu-item>
        <el-menu-item index="/admin/services/review">
          <template #title>
            <el-icon>
              <Service />
            </el-icon>
            <span>服务评价管理</span>
          </template>
        </el-menu-item>
      </el-sub-menu>

      <!-- 健康监测管理 -->
      <el-sub-menu index="/admin/health">
        <template #title>
          <el-icon>
            <FirstAidKit />
          </el-icon>
          <span>健康管理中心</span>
        </template>
        <el-menu-item index="/admin/health/record">
          <template #title>
            <el-icon>
              <FirstAidKit />
            </el-icon>
            <span>健康档案管理</span>
          </template>
        </el-menu-item>
        <el-menu-item index="/admin/health/monitor">
          <template #title>
            <el-icon>
              <FirstAidKit />
            </el-icon>
            <span>健康监测管理</span>
          </template>
        </el-menu-item>
        <el-menu-item index="/admin/health/report">
          <template #title>
            <el-icon>
              <FirstAidKit />
            </el-icon>
            <span>体检报告管理</span>
          </template>
        </el-menu-item>
      </el-sub-menu>

      <!-- 社区活动管理 -->
      <el-sub-menu index="/admin/activity">
        <template #title>
          <el-icon>
            <Calendar />
          </el-icon>
          <span>社区活动中心</span>
        </template>
        <el-menu-item index="/admin/activity/list">
          <template #title>
            <el-icon>
              <Calendar />
            </el-icon>
            <span>活动管理</span>
          </template>
        </el-menu-item>
        <el-menu-item index="/admin/activity/participate">
          <template #title>
            <el-icon>
              <Calendar />
            </el-icon>
            <span>活动报名管理</span>
          </template>
        </el-menu-item>
        <el-menu-item index="/admin/activity/checkin">
          <template #title>
            <el-icon>
              <Calendar />
            </el-icon>
            <span>活动签到管理</span>
          </template>
        </el-menu-item>
      </el-sub-menu>

      <!-- 通知公告管理 -->
      <el-sub-menu index="/admin/notices">
        <template #title>
          <el-icon>
            <Bell />
          </el-icon>
          <span>通知公告管理</span>
        </template>
        <el-menu-item index="/admin/notices/">
          <template #title>
            <el-icon>
              <Bell />
            </el-icon>
            <span>通知列表</span>
          </template>
        </el-menu-item>
      </el-sub-menu>
    </el-menu>
  </el-aside>
</template>

<script setup>
import { useTagsViewStore } from '@/stores/back/tagsViewStore';
import {
  Bell,
  Calendar,
  Collection,
  FirstAidKit,
  HomeFilled,
  Menu,
  Odometer,
  Service,
  Setting,
  User
} from '@element-plus/icons-vue';
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const props = defineProps({
  isCollapse: {
    type: Boolean,
    default: false
  }
});

const route = useRoute();
const router = useRouter();
const tagsViewStore = useTagsViewStore();
const activeMenu = computed(() => route.path);

// 处理菜单点击事件，确保标签被正确激活
const handleMenuClick = (path) => {
  // 规范化路径
  const normalizedPath = path.replace(/\/$/, '');
  
  // 查找对应路径的标签
  const targetTag = tagsViewStore.visitedViews.find(tag => {
    // 规范化路径进行比较
    const tagPath = tag.path.replace(/\/$/, '');
    return tagPath === normalizedPath;
  });
  
  // 如果找到匹配的标签
  if (targetTag) {
    // 更新标签状态
    tagsViewStore.updateVisitedView(targetTag);
    
    // 强制导航到该路径，确保标签被激活
    router.push(targetTag.fullPath || targetTag.path);
  } else {
    // 如果没有找到匹配的标签，直接导航到该路径
    router.push(path);
  }
};
</script>

<style scoped>
.aside {
  background-color: #304156;
  color: #bfcbd9;
  transition: width 0.3s ease;
  overflow-x: hidden;
  height: 100vh;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 1001;
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
</style>