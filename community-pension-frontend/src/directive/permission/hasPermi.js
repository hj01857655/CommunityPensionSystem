import { useUserStore } from '@/stores/back/userStore';

export default {
  mounted(el, binding) {
    const { value } = binding;
    const userStore = useUserStore();
    const permissions = userStore.permissions;

    if (value && value.length > 0) {
      const hasPermission = permissions.some(permission => {
        return value.includes(permission);
      });

      if (!hasPermission) {
        el.parentNode?.removeChild(el);
      }
    } else {
      throw new Error('需要指定权限，例如 v-hasPermi="[\'system:user:add\',\'system:user:edit\']"');
    }
  }
}; 