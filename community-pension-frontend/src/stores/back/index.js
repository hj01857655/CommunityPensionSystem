import {createPinia} from 'pinia';

const store = createPinia();

export default store;

export {useAuthStore} from '@/stores/back/authStore'
export {useUserStore} from '@/stores/back/userStore'
export {useAdminStore} from '@/stores/back/adminStore'
export {useRoleStore} from '@/stores/back/roleStore'
export {useHealthStore} from '@/stores/back/healthStore'
export {useNoticeStore} from '@/stores/back/noticeStore'