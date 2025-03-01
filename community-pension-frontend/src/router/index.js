import { createRouter, createWebHistory } from 'vue-router';
import Home from '@/views/front/Home.vue';
import Login from '@/views/auth/Login.vue';
import { useUserStore } from '@/stores/user';

const routes = [
  //前台首页
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: false, roles: ['elder','guest'] },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/front/DashBoard.vue'),
        meta: { title: '首页'},
      },
      {
        path: 'Service',
        name: 'Service',
        component: () => import('@/views/front/ServiceView.vue'),
        meta: { title: '服务预约' },
      },
      {
        path: 'Health',
        name: 'Health',
        component: () => import('@/views/front/HealthView.vue'),
        meta: { title: '健康档案' },
      },
      {
        path: 'Activity',
        name: 'Activity',
        component: () => import('@/views/front/ActivityView.vue'),
        meta: { title: '社区活动' },
      },
      {
        path: 'Notice',
        name: 'Notice',
        component: () => import('@/views/front/NoticeView.vue'),
        meta: { title: '通知公告' },
      },
      {
        path: 'Profile',
        name: 'Profile',
        component: () => import('@/views/front/ProfileView.vue'),
        meta: { title: '个人信息' },
      }
    ]
  },
  //登录
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false, roles: ['elder','kin','staff','admin','guest'] },
  },
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/error/403.vue')
  },
  //404
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { requiresAuth: false, roles: ['elder','kin','staff','admin','guest'] },
  },
  //后台管理
  {
    path: '/admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    redirect: '/admin/AdminDashboard',
    meta: { requiresAuth: true, roles: ['admin', 'kin', 'staff'] },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '仪表盘', icon: 'odometer' }
      },
      {
        path: 'user',
        name: 'UserManagement',
        component: () => import('@/views/admin/UserManagement.vue'),
        //只有管理员和工作人员可以访问
        meta: { title: '用户管理', icon: 'user', roles: ['admin', 'staff'] }
      },
      {
        path: 'service',
        name: 'ServiceManagement',
        component: () => import('@/views/admin/ServiceManagement.vue'),
        //只有管理员、家属、工作人员可以访问
        meta: { title: '服务管理', icon: 'service', roles: ['admin', 'kin', 'staff'] }
      },
      {
        path: 'activity',
        name: 'ActivityManagement',
        component: () => import('@/views/admin/ActivityManagement.vue'),
        //只有管理员和工作人员可以访问
        meta: { title: '活动管理', icon: 'calendar',roles: ['admin',  'staff'] }
      },
      {
        path: 'notice',
        name: 'NoticeManagement',
        component: () => import('@/views/admin/NoticeManagement.vue'),
        //只有管理员和工作人员可以访问
        meta: { title: '通知管理', icon: 'bell',roles: ['admin',  'staff'] }
      },
      {
        path: 'settings',
        name: 'SystemSettings',
        component: () => import('@/views/admin/SystemSettings.vue'),
        //只有管理员可以访问
        meta: { title: '系统设置', icon: 'Setting',roles: ['admin'] }
      }
    ]
  },
  
];


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
});
//获取授权状态，如果已登录，则返回授权状态，否则返回未授权状态
const getAuthState = () => {
  const userStore = useUserStore();
  let userInfo = null; // 初始化用户信息为null
  let roleId = 'guest'; // 初始化角色ID为'guest'

  // 如果Pinia中有用户信息
  if (userStore.getElderInfo()) {
    userInfo = userStore.getElderInfo(); // 从Pinia中获取用户信息
    roleId = userInfo.roleId; // 获取用户的角色ID
  } 
  // 如果localStorage中有用户信息
  else if (localStorage.getItem('userInfo')) {
    userInfo = JSON.parse(localStorage.getItem('userInfo')); // 从localStorage中解析用户信息
    roleId = localStorage.getItem('roleId') || 'guest'; // 获取localStorage中的角色ID，如果没有则默认为'guest'
  }

  // 返回包含isAuthenticated和roleId的对象
  return {
    isAuthenticated: !!userInfo, // 如果userInfo不为null，则表示已认证
    roleId: roleId, // 返回角色ID
  };
};

//角色映射
const roleMap = {
  '1':'elder',//老人
  '2':'kin',//家属
  '3':'staff',//工作人员
  '4':'admin',//管理员
  '5':'guest',//游客
};

//路由守卫，如果未授权，则跳转到登录页面，否则跳转到对应页面

// router.beforeEach((to, from, next) => {
//   //获取授权状态
//   const { isAuthenticated, roleId } = getAuthState();
//   //如果需要授权
//   if (to.meta.requiresAuth) {
//     //如果未授权
//     if (!isAuthenticated) {
//       return next({ name: 'Login', query: { redirect: to.fullPath } });
//     } else {
//       //如果需要角色
//       const requiredRole = to.meta.roles;
//       if (requiredRole) {
//         //如果需要角色是数组
//         const hasPermission = Array.isArray(requiredRole)
//           ? requiredRole.includes(roleMap[roleId])
//           : requiredRole === roleMap[roleId];
//         if (!hasPermission) {
//           //如果角色是老人
//           if (roleId === '1') {
//             //如果角色是老人，则跳转到老人首页
//             return next({ name: 'Home' });
//           } else {
//             //如果角色是家属、工作人员、管理员，则跳转到管理员首页
//             return next({ name: 'AdminDashboard' });
//           }
//         }
//       }
//     }
//   }else{
//     next();
//   }
// });

export default router;