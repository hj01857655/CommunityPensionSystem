import { createRouter, createWebHistory } from 'vue-router';
import AdminLogin from '@/views/admin/AdminLogin.vue';
import Home from '@/views/front/Home.vue';
import Dashboard from '@/views/admin/Dashboard.vue';
import axios from '@/utils/axios';

const routes = [
  {
    path:'/',
    redirect:'/home'
  },
  //用户登录
  {
    path: '/login',
    name: 'Login',
    component: ()=>import('@/views/front/Login.vue'),
    meta: { requiresAuth: false, roles: ['elder','kin','staff','admin','guest'] },
  },
  //前台首页
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/front/Home.vue'),
    meta: { requiresAuth: true, roles: ['elder'] },
    children: [
      {
        path:'health',
        name:'HealthView',
        component:()=>import('@/views/front/HealthView.vue'),
        meta: { requiresAuth: true, roles: ['elder'] }
      },
      {
        path:'service',
        name:'ServiceView',
        component:()=>import('@/views/front/ServiceView.vue'),
        meta: { requiresAuth: true, roles: ['elder'] }
      },
      {
        path:'notice',
        name:'NoticeView',
        component:()=>import('@/views/front/NoticeView.vue'),
        meta: { requiresAuth: true, roles: ['elder'] }
      },
      {
        path:'activity',
        name:'ActivityView',
        component:()=>import('@/views/front/ActivityView.vue'),
        meta: { requiresAuth: true, roles: ['elder'] }
      },
      {
        path:'profile',
        name:'ProfileView',
        component:()=>import('@/views/front/ProfileView.vue'),
        meta: { requiresAuth: true, roles: ['elder'] },
        beforeEnter: async (to, from, next) => {
          try {
            const user=JSON.parse(localStorage.getItem('userInfo'));
            const response= await axios.get('/api/user/getUserInfo',{
              params:{
                roleId:user.roleId
              }
            });
            console.log(response.data);
            next();
          } catch (error) {
            next({ name: 'Login' });
          }
        }
      }
    ]
  },

  //管理员登录
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/admin/AdminLogin.vue')
  },
  //管理员首页
  {
    path: '/admin',
    name:'AdminLayout',
    component: () => import('@/views/admin/AdminLayout.vue'),
    redirect:'/admin/dashboard',
    meta: { requiresAuth: true, roles: ['admin', 'kin', 'staff'] },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: { title: '仪表盘', icon: 'odometer', roles: ['admin'] }
      },
      {
        path: 'activity',
        name: 'ActivityManagement',
        component: () => import('@/views/admin/activity/ActivityLayout.vue'),
        meta: { title: '社区活动管理', icon: 'calendar', roles: ['admin'] },
        children:[
          {
            path:'type',
            name:'ActivityTypeList',
            component: () => import('@/views/admin/activity/type.vue'),
            meta: { title: '活动类型管理', icon: 'calendar', roles: ['admin'] }
          },
          {
            path:'registration', 
            name:'ActivityRegistrationList',
            component: () => import('@/views/admin/activity/registration.vue'),
            meta: { title: '活动报名管理', icon: 'calendar', roles: ['admin'] }
          },
          {
            path:'checkin',
            name:'ActivityCheckinList',
            component: () => import('@/views/admin/activity/checkin.vue'),
            meta: { title: '活动签到管理', icon: 'calendar', roles: ['admin'] }
          }
        ]
      },
      {
        path: 'users',
        name: 'UserManagement',
        component: () => import('@/views/admin/user/index.vue'),
        meta: { title: '用户管理', icon: 'user', roles: ['admin'] },
        children:[
          {
            path:'user',
            name:'UserList',
            component:()=>import('@/views/admin/user/UserManagement.vue'),
            meta: { title: '用户管理', icon: 'user', roles: ['admin'] }
          },
          {
            path:'elder',
            name:'ElderList',
            component:()=>import('@/views/admin/user/ElderManagement.vue'),
            meta: { title: '老人管理', icon: 'user', roles: ['admin'] }
          },
          {
            path:'kin',
            name:'KinList',
            component:()=>import('@/views/admin/user/KinManagement.vue'),
            meta: { title: '亲属管理', icon: 'user', roles: ['admin'] }
          },
          {
            path:'staff',
            name:'StaffList',
            component:()=>import('@/views/admin/user/StaffManagement.vue'),
            meta: { title: '社区工作人员管理', icon: 'user', roles: ['admin'] }
          }
        ]
      },
      {
        path: 'roles',
        name: 'RoleManagement',
        component: () => import('@/views/admin/role/index.vue'),
        meta: { title: '角色管理', icon: 'user', roles: ['admin'] },
        children:[
          {
            path:'role',
            name:'RoleList',
            component:()=>import('@/views/admin/role/RoleManagement.vue'),
            meta: { title: '角色管理', icon: 'user', roles: ['admin'] }
          }
        ]
      },
      {
        path: 'services',
        name: 'ServiceManagement',
        component: () => import('@/views/admin/service/index.vue'),
        meta: { title: '服务预约管理', icon: 'service', roles: ['admin'] },
        children:[
          {
            path:'serviceType',
            name:'ServiceTypeList',
            component:()=>import('@/views/admin/service/ServiceManagement.vue'),
            meta: { title: '服务类型管理', icon: 'service', roles: ['admin'] }
          },
          {
            path:'serviceAppointment',
            name:'ServiceAppointmentList',
            component:()=>import('@/views/admin/service/ServiceManagement.vue'),
            meta: { title: '服务预约管理', icon: 'service', roles: ['admin'] }
          },
          {
            path:'serviceEvaluation',
            name:'ServiceEvaluationList',
            component:()=>import('@/views/admin/service/ServiceManagement.vue'),
            meta: { title: '服务评价管理', icon: 'service', roles: ['admin'] }
          }
        ]
      },
      {
        path: 'health',
        name: 'HealthManagement',
        component: () => import('@/views/admin/health/index.vue'),
        meta: { title: '健康监测管理', icon: 'heart', roles: ['admin'] },
        children:[
          {
            path:'healthRecord',
            name:'HealthRecordList',
            component:()=>import('@/views/admin/health/HealthManagement.vue'),
            meta: { title: '健康档案管理', icon: 'heart', roles: ['admin'] }
          },
          {
            path:'healthAssessment',
            name:'HealthAssessmentList',
            component:()=>import('@/views/admin/health/HealthManagement.vue'),
            meta: { title: '健康评估管理', icon: 'heart', roles: ['admin'] }
          },
          {
            path:'healthIntervention',
            name:'HealthInterventionList',
            component:()=>import('@/views/admin/health/HealthManagement.vue'),
            meta: { title: '健康干预管理', icon: 'heart', roles: ['admin'] }
          }
        ]
      },
      {
        path: 'notice',
        name: 'NoticeManagement',
        component: () => import('@/views/admin/notice/index.vue'),
        meta: { title: '通知公告管理', icon: 'bell', roles: ['admin'] },
        children: [
          {
            path: 'list',
            name: 'NoticeList',
            component: () => import('@/views/admin/notice/NoticeManagement.vue'),
            meta: { title: '通知公告列表', icon: 'document', roles: ['admin'] }
          },
          {
            path: 'publish',
            name: 'NoticePublish',
            component: () => import('@/views/admin/notice/NoticePublish.vue'),
            meta: { title: '发布通知', icon: 'edit', roles: ['admin'] }
          }
        ]
      },
      {
        path: 'notice',
        name: 'NoticeManagement',
        component: () => import('@/views/admin/notice/index.vue'),
        meta: { title: '通知公告管理', icon: 'bell', roles: ['admin'] },
        children:[
          {
            path:'list',
            name:'NoticeList',
            component:()=>import('@/views/admin/notice/NoticeList.vue'),
            meta: { title: '通知公告列表', icon: 'bell', roles: ['admin'] }
          },
          {
            path:'publish',
            name:'NoticePublish',
            component:()=>import('@/views/admin/notice/NoticePublish.vue'),
            meta: { title: '通知公告发布', icon: 'bell', roles: ['admin'] }
          }
        ]
      },
      {
        path: 'statistics',
        name: 'StatisticsManagement',
        component: () => import('@/views/admin/statistics/index.vue'),
        meta: { title: '统计分析', icon: 'chart', roles: ['admin'] },
        children: [
          {
            path: 'statistics',
            name: 'Statistics',
            component: () => import('@/views/admin/statistics/Statistics.vue'),
            meta: { title: '数据统计', icon: 'chart', roles: ['admin'] }
          }
        ]
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('@/views/admin/SystemSettings.vue'),
        meta: { title: '系统设置', icon: 'Setting', roles: ['admin'] }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/admin/user/Profile.vue'),
        meta: { title: '个人信息', icon: 'user', roles: ['admin'] }
      }
    ]
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, from, next) => {
  // 暂时注释掉权限控制，允许直接访问后台
  /*
  const userInfo = localStorage.getItem('userInfo');
  const requiresAuth = to.meta.requiresAuth;
  const allowedRoles = to.meta.roles;

  if (requiresAuth && !userInfo) {
    next({ name: 'Login' });
  } else if (userInfo && allowedRoles) {
    const user = JSON.parse(userInfo);
    if (allowedRoles.includes(user.role)) {
      next();
    } else {
      next({ name: 'Login' });
    }
  } else {
    next();
  }
  */
  
  // 直接放行所有路由
  next();
});

export default router;