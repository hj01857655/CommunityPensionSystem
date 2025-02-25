import { createRouter, createWebHistory } from 'vue-router';
import Home from '../views/Home.vue';
import Login from '../views/Login.vue';
import ChangePassword from '../views/Auth/ChangePassword.vue';
import Dashboard from '../views/admin/Dashboard.vue';

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: false },
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
  },
  {
    path: '/changePassword',
    name: 'ChangePassword',
    component: ChangePassword,
    meta: { requiresAuth: true },
  },
  {
    path: '/admin',
    name: 'Dashboard',
    component: Dashboard,
    meta: { requiresAuth: true, role: ['family', 'staff', 'admin'] },
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

const getAuthState = () => {
  const token = localStorage.getItem('token');
  const role = localStorage.getItem('userRole') || 'elder';
  return {
    isAuthenticated: !!token,
    userRole: role,
  };
};

router.beforeEach((to, from, next) => {
  const { isAuthenticated, userRole } = getAuthState();

  if (to.meta.requiresAuth) {
    if (!isAuthenticated) {
      return next({ name: 'Login', query: { redirect: to.fullPath } });
    } else {
      const requiredRole = to.meta.role;
      if (requiredRole) {
        const hasPermission = Array.isArray(requiredRole)
          ? requiredRole.includes(userRole)
          : requiredRole === userRole;
        if (!hasPermission) {
          if (userRole === 'elder') {
            return next({ name: 'Home' });
          } else {
            return next({ name: 'Dashboard' });
          }
        }
      }
    }
  }
  next();
});

export default router; 