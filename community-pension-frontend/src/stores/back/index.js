import { createPinia } from 'pinia';

const store = createPinia();

export default store;

export { useAuthStore } from './authStore'
export { useUserStore } from './userStore'
export { useAdminStore } from './adminStore'
export { useRoleStore } from './roleStore'
export { useHealthStore } from './healthStore'
export { useNoticeStore } from './noticeStore' 