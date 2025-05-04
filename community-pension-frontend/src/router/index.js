import {createRouter, createWebHistory} from 'vue-router';
import HealthView from '@/views/fore/HealthView.vue';
import ServiceView from '@/views/fore/ServiceView.vue';
import ActivityView from '@/views/fore/ActivityView.vue'; // 社区活动视图组件
import NoticeView from '@/views/fore/NoticeView.vue';
import ProfileView from '@/views/fore/ProfileView.vue';
import {storageConfig, TokenManager} from '@/utils/axios';
import {ElMessage} from 'element-plus';
import {useTagsViewStore} from '@/stores/tagsView';

const routes = [
  // 根路径重定向
  {
    path: '/',
    redirect: '/home'
  },
  // 错误页面路由
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/error/403.vue'),
    meta: { requiresAuth: false, roles: ['elder', 'kin', 'staff', 'admin', 'guest'] }
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { requiresAuth: false, roles: ['elder', 'kin', 'staff', 'admin', 'guest'] }
  },
  {
    path: '/500',
    name: 'ServerError',
    component: () => import('@/views/error/500.vue'),
    meta: { requiresAuth: false, roles: ['elder', 'kin', 'staff', 'admin', 'guest'] }
  },
  //用户登录
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/fore/Login.vue'),
    meta: { requiresAuth: false, roles: ['elder', 'kin', 'staff', 'admin', 'guest'] },
  },
  //前台首页
  {
    path: '/home',
    component: () => import('@/views/fore/Home.vue'),
    redirect: '/home/dashboard',
    meta: { requiresAuth: true, roles: ['elder', 'kin'] },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/fore/DashBoard.vue')
      },
      {
        path: 'health',
        name: 'HealthView',
        component: HealthView
      },
      {
        path: 'service',
        name: 'ServiceView',
        component: ServiceView
      },
      {
        path: 'activity',
        name: 'ActivityView',
        component: ActivityView,
        meta: { title: '社区活动', icon: 'calendar' } // 社区活动路由，整合了活动列表和我的报名
      },
      {
        path: 'notice',
        name: 'NoticeView',
        component: NoticeView
      },
      {
        path: 'notice/:id',
        name: 'NoticeDetailView',
        component: () => import('@/views/fore/NoticeDetailView.vue')
      },
      {
        path: 'profile',
        name: 'ProfileView',
        component: ProfileView
      }
    ]
  },

  //管理员登录
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/back/AdminLogin.vue')
  },
  //管理员首页
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('@/views/back/AdminLayout.vue'),
    redirect: '/admin/home',
    meta: { requiresAuth: true, roles: ['admin', 'staff'] },
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/back/home/Home.vue'),
        meta: { title: '首页', icon: 'odometer', roles: ['admin', 'staff'] }
      },
      {
        path: 'system',
        name: 'SystemManagement',
        component: () => import('@/views/back/system/index.vue'),
        meta: { title: '系统管理', icon: 'Setting', roles: ['admin', 'staff'] },
        children: [

          {
            path: 'user',
            name: 'UserManagement',
            component: () => import('@/views/back/system/user/index.vue'),
            meta: { title: '用户管理', icon: 'User', roles: ['admin', 'staff'] }
          },
          {
            path: 'role',
            name: 'RoleManagement',
            component: () => import('@/views/back/system/role/index.vue'),
            meta: { title: '角色管理', icon: 'User', activeMenu: '/admin/system/role' }
          },
          {
            path: 'menu',
            name: 'MenuManagement',
            component: () => import('@/views/back/system/menu/index.vue'),
            meta: { title: '菜单管理', icon: 'Menu', activeMenu: '/admin/system/menu' }
          },
          {
            path: 'dict',
            name: 'DictManagement',
            component: () => import('@/views/back/system/dict/index.vue'),
            meta: { title: '字典管理', icon: 'Document', activeMenu: '/admin/system/dict' }
          },
          {
            path: 'dict/data/type/:dictType',
            name: 'DictDataManagement',
            component: () => import('@/views/back/system/dict/data.vue'),
            meta: { title: '字典数据', icon: 'Document', activeMenu: '/admin/system/dict/data' }
          },
          {
            path: 'setting',
            name: 'SystemSetting',
            component: () => import('@/views/back/system/SystemSetting.vue'),
            meta: { title: '系统设置', icon: 'Setting', roles: ['admin', 'staff'] }
          },
          {
            path: 'user/profile',
            redirect: '/admin/system/user/profile?tab=password',
            meta: { roles: ['admin', 'staff'] }
          }
        ]
      },
      //社区活动管理
      {
        path: 'activity',
        name: 'ActivityManagement',
        component: () => import('@/views/back/activity/index.vue'),
        meta: { title: '社区活动管理', icon: 'calendar', roles: ['admin', 'staff'] }, // 管理端活动管理模块
        children: [
          {
            path: 'list',
            name: 'ActivityList',
            component: () => import('@/views/back/activity/ActivityList.vue'),
            meta: { title: '活动管理', icon: 'calendar', roles: ['admin', 'staff'] } // 管理员发布和管理活动的页面
          },
          {
            path: 'participate',
            name: 'ActivityParticipate',
            component: () => import('@/views/back/activity/ActivityParticipate.vue'),
            meta: { title: '活动报名管理', icon: 'calendar', roles: ['admin', 'staff'] } // 管理活动报名记录的页面
          },
          {
            path: 'checkin',
            name: 'ActivityCheckin',
            component: () => import('@/views/back/activity/ActivityCheckin.vue'),
            meta: { title: '活动签到管理', icon: 'calendar', roles: ['admin', 'staff'] } // 管理活动签到记录的页面
          }
        ]
      },
      {
        path: 'services',
        name: 'ServiceManagement',
        component: () => import('@/views/back/service/index.vue'),
        meta: { title: '服务预约管理', icon: 'service', roles: ['admin', 'staff'] },
        children: [
          {
            path: 'service',
            name: 'ServiceList',
            component: () => import('@/views/back/service/ServiceManagement.vue'),
            meta: { title: '服务项目管理', icon: 'service', roles: ['admin', 'staff'] }
          },
          {
            path: 'order',
            name: 'ServiceOrderManagement',
            component: () => import('@/views/back/service/ServiceOrderManagement.vue'),
            meta: { title: '服务工单管理', icon: 'service', roles: ['admin', 'staff'] }
          },
          {
            path: 'evaluation',
            name: 'ServiceEvaluationList',
            component: () => import('@/views/back/service/ServiceEvaluation.vue'),
            meta: { title: '服务评价管理', icon: 'service', roles: ['admin', 'staff'] }
          }
        ]
      },
      {
        path: 'health',
        name: 'HealthManagement',
        component: () => import('@/views/back/health/index.vue'),
        meta: { title: '健康监测管理', icon: 'heart', roles: ['admin', 'staff'] },
        children: [
          {
            path: 'record',
            name: 'HealthRecord',
            component: () => import('@/views/back/health/HealthRecord.vue'),
            meta: { title: '健康档案管理', icon: 'heart', roles: ['admin', 'staff'] }
          },
          {
            path: 'monitor',
            name: 'HealthMonitor',
            component: () => import('@/views/back/health/HealthMonitor.vue'),
            meta: { title: '健康监测管理', icon: 'heart', roles: ['admin', 'staff'] }
          },
        ]
      },
      {
        path: 'notice',
        name: 'NoticeManagement',
        component: () => import('@/views/back/notice/index.vue'),
        meta: { title: '通知公告', icon: 'bell', roles: ['admin', 'staff'] },
        children: [
          {
            path: '',
            name: 'NoticeList',
            component: () => import('@/views/back/notice/NoticeManagement.vue'),
            meta: { title: '通知列表', icon: 'bell', roles: ['admin', 'staff'] }
          },
          {
            path: 'publish',
            name: 'NoticePublish',
            component: () => import('@/views/back/notice/NoticePublish.vue'),
            meta: { title: '通知发布', icon: 'bell', roles: ['admin', 'staff'] }
          }
        ]
      },

      {
        path: 'error',
        name: 'ErrorPage',
        component: () => import('@/components/common/error/ErrorPage.vue'),
        meta: { title: '错误页面', icon: 'Error', roles: ['admin', 'staff', 'elder', 'kin', 'guest'] },
        children: [
          {
            path: '403',
            name: '403',
            component: () => import('@/views/error/403.vue'),
            meta: { title: '403 - 无权限访问', icon: 'Error', roles: ['admin', 'staff', 'elder', 'kin', 'guest'] }
          },
          {
            path: '404',
            name: '404',
            component: () => import('@/views/error/404.vue'),
            meta: { title: '404 - 页面不存在', icon: 'Error', roles: ['admin', 'staff', 'elder', 'kin', 'guest'] }
          },
          {
            path: '500',
            name: '500',
            component: () => import('@/views/error/500.vue'),
            meta: { title: '500 - 服务器错误', icon: 'Error', roles: ['admin', 'staff', 'elder', 'kin', 'guest'] }
          }
        ]
      }
    ]
  },

];

const router = createRouter({
  history: createWebHistory(),

  routes
});

router.beforeEach((to, from, next) => {
    console.log('路由守卫 - 当前路由:', to.path);

    // 获取 tagsView store
    const tagsViewStore = useTagsViewStore();

    // 如果是后台管理路由，添加到标签视图
    if (to.path.startsWith('/admin') && to.meta?.title) {
        tagsViewStore.visitedViews.push({
            path: to.path,
            name: to.name,
            meta: to.meta
        });
    }

  // 如果是错误页面或登录页面，直接放行
  if (to.path === '/403' || to.path === '/404' || to.path === '/500' ||
    to.path === '/login' || to.path === '/admin/login') {
    return next();
  }

  const isAdminRoute = to.path.startsWith('/admin');

    // 处理后台路由认证
  if (isAdminRoute) {
      const token = TokenManager.admin.getAccessToken();
      const isAdminLoggedIn = !!token;
    
    if (!isAdminLoggedIn) {
      return next('/admin/login');
    }

      try {
          const adminUserInfo = JSON.parse(storageConfig.getStorage(storageConfig.admin).getItem("userInfo") || "{}");
          const adminRole = adminUserInfo.roles?.[0];

          // 检查管理员角色
          if (!adminRole || (adminRole !== 'admin' && adminRole !== 'staff')) {
              console.log('用户没有管理员或工作人员角色');
              return next('/403');
          }
      } catch (error) {
          console.error('解析管理员用户信息失败:', error);
          return next('/admin/login');
      }

    return next();
  }

  // 处理前台路由认证
  if (to.meta.requiresAuth) {
      try {
          // 从 localStorage 获取前台用户登录状态
          const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
          const userInfoStr = localStorage.getItem('userInfo');

          if (!isLoggedIn || !userInfoStr) {
              console.log("路由守卫 - 未登录，重定向到登录页");
              ElMessage.warning('请先登录');
              return next('/login');
          }

          const userInfo = JSON.parse(userInfoStr);
          if (!userInfo || !userInfo.userId) {
              console.log("路由守卫 - 用户信息不完整，重定向到登录页");
              ElMessage.warning('登录信息已失效，请重新登录');
              localStorage.removeItem('isLoggedIn');
              localStorage.removeItem('userInfo');
              return next('/login');
          }

          // 检查用户角色
          const roles = userInfo.roles || [];
          if (!roles.includes('elder') && !roles.includes('kin')) {
              console.log("路由守卫 - 角色不匹配，重定向到403");
              ElMessage.error('您没有权限访问该页面');
              return next('/403');
          }

          // 如果需要特定角色且用户角色不符合要求
          if (to.meta.roles && !to.meta.roles.some(role => roles.includes(role))) {
              console.log("路由守卫 - 角色权限不足，重定向到403");
              ElMessage.error('您没有权限访问该页面');
              return next('/403');
          }
      } catch (error) {
          console.error('解析用户信息失败:', error);
          ElMessage.error('登录信息已失效，请重新登录');
          localStorage.removeItem('isLoggedIn');
          localStorage.removeItem('userInfo');
          return next('/login');
      }
  }

  next();
});

export default router;