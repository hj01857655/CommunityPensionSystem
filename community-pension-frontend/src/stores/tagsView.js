import { defineStore } from 'pinia';

export const useTagsViewStore = defineStore('tagsView', {
  state: () => ({
    visitedViews: [],
    cachedViews: []
  }),

  actions: {
    // 添加访问过的标签
    addVisitedView(view) {
      // 如果是首页相关路径，不添加标签
      if (['/admin', '/admin/home', '/admin/index'].includes(view.path)) {
        return;
      }
      // 如果标签已存在，不重复添加
      if (this.visitedViews.some(v => v.path === view.path)) {
        return;
      }
      this.visitedViews.push(
        Object.assign({}, view, {
          title: view.meta?.title || 'no-name',
          closable: true
        })
      );
    },

    // 移除标签
    removeVisitedView(view) {
      const index = this.visitedViews.indexOf(view);
      if (index > -1) {
        this.visitedViews.splice(index, 1);
      }
    },

    // 添加缓存视图
    addCachedView(view) {
      if (this.cachedViews.includes(view.name)) {
        return;
      }
      this.cachedViews.push(view.name);
    },

    // 移除缓存视图
    removeCachedView(view) {
      const index = this.cachedViews.indexOf(view.name);
      if (index > -1) {
        this.cachedViews.splice(index, 1);
      }
    },

    // 关闭其他标签
    closeOthersTags(view) {
      this.visitedViews = this.visitedViews.filter(v => v.path === view.path || v.meta?.affix);
      this.cachedViews = this.cachedViews.filter(name => name === view.name || name === 'Index');
    },

    // 关闭左侧标签
    closeLeftTags(view) {
      const index = this.visitedViews.indexOf(view);
      if (index > 0) {
        this.visitedViews = this.visitedViews.filter(v => v.meta?.affix || this.visitedViews.indexOf(v) >= index);
        this.cachedViews = this.cachedViews.filter(name => name === 'Index' || this.visitedViews.some(v => v.name === name));
      }
    },

    // 关闭右侧标签
    closeRightTags(view) {
      const index = this.visitedViews.indexOf(view);
      if (index < this.visitedViews.length - 1) {
        this.visitedViews = this.visitedViews.filter(v => v.meta?.affix || this.visitedViews.indexOf(v) <= index);
        this.cachedViews = this.cachedViews.filter(name => name === 'Index' || this.visitedViews.some(v => v.name === name));
      }
    },

    // 关闭所有标签
    closeAllTags() {
      this.visitedViews = this.visitedViews.filter(v => v.meta?.affix);
      this.cachedViews = this.cachedViews.filter(name => name === 'Index');
    },

    // 刷新标签
    refreshTag(view) {
      const index = this.visitedViews.indexOf(view);
      if (index > -1) {
        this.visitedViews.splice(index, 1, { ...view });
      }
    },

    // 初始化首页标签
    initDashboardTab() {
      const dashboardTab = {
        title: '首页',
        path: '/admin/home',
        name: 'Index',
        meta: { title: '首页', affix: true },
        closable: false
      };
      
      // 清空现有标签
      this.visitedViews = [];
      this.cachedViews = [];
      
      // 添加首页标签
      this.visitedViews.push(dashboardTab);
      this.cachedViews.push(dashboardTab.name);
    }
  },

  persist: {
    enabled: true,
    strategies: [
      {
        key: 'tagsView',
        storage: localStorage,
        paths: ['visitedViews', 'cachedViews']
      }
    ]
  }
}); 