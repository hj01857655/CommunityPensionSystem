import router from '@/router'
import { useUserStore } from '@/stores/back/userStore'
// 检查权限
export const checkPermission = (requiredRoles) => {
  const userStore = useUserStore()
  return requiredRoles.some(role => userStore.roles.includes(role))
}
// 设置路由守卫
export const setupRouterGuard = () => {
  router.beforeEach((to, from, next) => {
    const userStore = useUserStore()
    
    if (to.meta.requiresAuth && !userStore.token) {
      next('/admin/login')
    } else if (to.meta.roles && !checkPermission(to.meta.roles)) {
      next('/403')
    } else {
      next()
    }
  })
}

export const formRules = {
  name: [
    { required: true, message: '请输入名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在2-50个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入代码', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_:]+$/, message: '只允许字母、数字和下划线', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入描述', trigger: 'blur' },
    { max: 200, message: '长度不能超过200个字符', trigger: 'blur' }
  ]
};