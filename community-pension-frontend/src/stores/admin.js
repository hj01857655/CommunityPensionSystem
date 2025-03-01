import { defineStore } from 'pinia';

export const useAdminStore = defineStore('admin', {
  state: () => ({
    adminInfo: {
      // 初始化后台管理相关信息
      dashboardData: null,
      settings: {},
    },
  }),
  actions: {
    setDashboardData(data) {
      this.adminInfo.dashboardData = data;
    },
    updateSettings(newSettings) {
      this.adminInfo.settings = { ...this.adminInfo.settings, ...newSettings };
    },
  },
}); 