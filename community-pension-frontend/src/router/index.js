import { createRouter, createWebHistory } from 'vue-router';
import HealthView from '@/views/fore/HealthView.vue';
import ServiceView from '@/views/fore/ServiceView.vue';
import ActivityView from '@/views/fore/ActivityView.vue';
import NoticeView from '@/views/fore/NoticeView.vue';

const routes = [
  {
    path: '/',
    redirect: '/home'
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
    name: 'Home',
    component: () => import('@/views/fore/Home.vue'),
    meta: { requiresAuth: true, roles: ['elder'] },
    children: [
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
        component: ActivityView
      },
      {
        path: 'notice',
        name: 'NoticeView',
        component: NoticeView
      },
      {
        path: 'profile',
        name: 'ProfileView',
        component: () => import('@/views/fore/ProfileView.vue'),
        meta: { requiresAuth: true, roles: ['elder'] }
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
    redirect: '/admin/analysis/dashboard',
    meta: { requiresAuth: true, roles: ['admin', 'kin', 'staff'] },
    children: [
      {
        path: 'analysis',
        name: 'DataAnalysis',
        meta: { title: '数据分析看板', icon: 'odometer', roles: ['admin'] },
        children: [
          {
            path: 'dashboard',
            name: 'Dashboard',
            component: () => import('@/views/back/analysis/Dashboard.vue'),
            meta: { title: '仪表盘', icon: 'odometer', roles: ['admin'] }
          },
          {
            path: 'activity',
            name: 'ActivityAnalysis',
            component: () => import('@/views/back/analysis/ActivityAnalysis.vue'),
            meta: { title: '活动分析', icon: 'odometer', roles: ['admin'] }
          },
          {
            path: 'service',
            name: 'ServiceAnalysis',
            component: () => import('@/views/back/analysis/ServiceAnalysis.vue'),
            meta: { title: '服务分析', icon: 'odometer', roles: ['admin'] }
          },
          {
            path: 'health',
            name: 'HealthAnalysis',
            component: () => import('@/views/back/analysis/HealthAnalysis.vue'),
            meta: { title: '健康分析', icon: 'odometer', roles: ['admin'] }
          }
        ]
      },
      {
        path: 'activity',
        name: 'ActivityManagement',
        component: () => import('@/views/back/activity/ActivityLayout.vue'),
        meta: { title: '社区活动管理', icon: 'calendar', roles: ['admin'] },
        children: [
          {
            path: 'type',
            name: 'ActivityTypeList',
            component: () => import('@/views/back/activity/type.vue'),
            meta: { title: '活动类型管理', icon: 'calendar', roles: ['admin'] }
          },
          {
            path: 'registration',
            name: 'ActivityRegistrationList',
            component: () => import('@/views/back/activity/ActivityRegistration.vue'),
            meta: { title: '社区活动管理', icon: 'calendar', roles: ['admin'] }
          },
          {
            path: 'checkin',
            name: 'ActivityCheckinList',
            component: () => import('@/views/back/activity/ActivityCheckin.vue'),
            meta: { title: '活动签到管理', icon: 'calendar', roles: ['admin'] }
          }
        ]
      },
      {
        path: 'users',
        name: 'UserManagement',
        component: () => import('@/views/back/user/index.vue'),
        meta: { title: '用户管理', icon: 'user', roles: ['admin'] },
        children:[
          {
            path:'user',
            name:'UserList',
            component:()=>import('@/views/back/user/UserManagement.vue'),
            meta: { title: '用户管理', icon: 'user', roles: ['admin'] }
          },
          {
            path:'elder',
            name:'ElderList',
            component:()=>import('@/views/back/user/ElderManagement.vue'),
            meta: { title: '老人管理', icon: 'user', roles: ['admin'] }
          },
          {
            path:'kin',
            name:'KinList',
            component:()=>import('@/views/back/user/KinManagement.vue'),
            meta: { title: '亲属管理', icon: 'user', roles: ['admin'] }
          },
          {
            path:'staff',
            name:'StaffList',
            component:()=>import('@/views/back/user/StaffManagement.vue'),
            meta: { title: '社区工作人员管理', icon: 'user', roles: ['admin'] }
          }
        ]
      },
      {
        path: 'services',
        name: 'ServiceManagement',
        component: () => import('@/views/back/service/index.vue'),
        meta: { title: '服务预约管理', icon: 'service', roles: ['admin'] },
        children:[
          {
            path:'serviceType',
            name:'ServiceTypeList',
            component:()=>import('@/views/back/service/ServiceManagement.vue'),
            meta: { title: '服务类型管理', icon: 'service', roles: ['admin'] }
          },
          {
            path:'serviceAppointment',
            name:'ServiceAppointmentList',
            component:()=>import('@/views/back/service/ServiceManagement.vue'),
            meta: { title: '服务预约管理', icon: 'service', roles: ['admin'] }
          },
          {
            path:'serviceEvaluation',
            name:'ServiceEvaluationList',
            component:()=>import('@/views/back/service/ServiceManagement.vue'),
            meta: { title: '服务评价管理', icon: 'service', roles: ['admin'] }
          }
        ]
      },
      {
        path: 'health',
        name: 'HealthManagement',
        component: () => import('@/views/back/health/index.vue'),
        meta: { title: '健康监测管理', icon: 'heart', roles: ['admin'] },
        children:[
          {
            path:'record',
            name:'HealthRecord',
            component:()=>import('@/views/back/health/HealthRecord.vue'),
            meta: { title: '健康档案管理', icon: 'heart', roles: ['admin'] }
          },
          {
            path:'assessment',
            name:'HealthAssessment',
            component:()=>import('@/views/back/health/HealthAssessment.vue'),
            meta: { title: '健康评估管理', icon: 'heart', roles: ['admin'] }
          },
          {
            path:'monitor',
            name:'HealthMonitor',
            component:()=>import('@/views/back/health/HealthMonitor.vue'),
            meta: { title: '健康监测管理', icon: 'heart', roles: ['admin'] }
          },
        ]
      },
      {
        path: 'notice',
        name: 'NoticeManagement',
        component: () => import('@/views/back/notice/index.vue'),
        meta: { title: '通知公告管理', icon: 'bell', roles: ['admin'] },
        children:[
          {
            path:'type',
            name:'NoticeType',
            component:()=>import('@/views/back/notice/NoticeType.vue'),
            meta: { title: '通知公告类型管理', icon: 'bell', roles: ['admin'] }
          },
          {
            path:'list',
            name:'NoticeList',
            component:()=>import('@/views/back/notice/NoticeManagement.vue'),
            meta: { title: '通知公告管理', icon: 'bell', roles: ['admin'] }
          },
          {
            path:'publish',
            name:'NoticePublish',
            component:()=>import('@/views/back/notice/NoticePublish.vue'),
            meta: { title: '通知公告发布', icon: 'bell', roles: ['admin'] }
          }
        ]
      },
      {
        path: 'system',
        name: 'SystemManagement',
        component: () => import('@/views/back/system/index.vue'),
        meta: { title: '系统管理', icon: 'Setting', roles: ['admin'] },
        children:[
          {
            path:'setting',
            name:'SystemSetting',
            component:()=>import('@/views/back/system/SystemSetting.vue'),
            meta: { title: '系统设置', icon: 'Setting', roles: ['admin'] }
          },
          {
            path:'menu',
            name:'MenuManagement',
            component:()=>import('@/views/back/system/MenuManagement.vue'),
            meta: { title: '菜单管理', icon: 'Menu', roles: ['admin'] }
          },
          {
            path:'permission',
            name:'PermissionManagement',
            component:()=>import('@/views/back/system/PermissionManagement.vue'),
            meta: { title: '权限管理', icon: 'Lock', roles: ['admin'] }
          },
          {
            path:'role',
            name:'RoleManagement',
            component:()=>import('@/views/back/system/RoleManagement.vue'),
            meta: { title: '角色管理', icon: 'User', roles: ['admin'] }
          },
          {
            path:'info',
            name:'UserInfo',
            component:()=>import('@/views/back/system/UserInfo.vue'),
            meta: { title: '个人信息', icon: 'User', roles: ['admin'] }
          }
        ]
      },
      {
        path:'error',
        name:'ErrorPage',
        component:()=>import('@/components/common/error/ErrorPage.vue'),
        meta: { title: '错误页面', icon: 'Error', roles: ['admin'] },
        children:[
          {
            path:'403',
            name:'403',
            component:()=>import('@/views/error/403.vue'),
            meta: { title: '403 - 无权限访问', icon: 'Error', roles: ['admin'] }
          },
          {
            path:'404',
            name:'404',
            component:()=>import('@/views/error/404.vue'),
            meta: { title: '404 - 页面不存在', icon: 'Error', roles: ['admin'] }
          },
          {
            path:'500',
            name:'500',
            component:()=>import('@/views/error/500.vue'),
            meta: { title: '500 - 服务器错误', icon: 'Error', roles: ['admin'] }
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
  
  
  // 直接放行所有路由
  next();
});


export default router;