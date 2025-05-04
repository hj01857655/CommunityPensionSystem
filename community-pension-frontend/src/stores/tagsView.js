import {defineStore} from 'pinia';

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

        // 如果标签已存在，更新它
        const index = this.visitedViews.findIndex(v => v.path === view.path);
        if (index > -1) {
            // 更新标签信息
            this.visitedViews[index] = {
                ...this.visitedViews[index],
                title: view.meta?.title || 'no-name'
            };
        return;
      }

        // 添加新标签
        this.visitedViews.push({
            ...view,
            title: view.meta?.title || 'no-name',
            closable: !view.meta?.affix
        });

        // 如果是需要缓存的组件，添加到缓存列表
        if (view.name && !this.cachedViews.includes(view.name)) {
            this.cachedViews.push(view.name);
        }
    },

    // 移除标签
    removeVisitedView(view) {
        const index = this.visitedViews.findIndex(v => v.path === view.path);
      if (index > -1) {
        this.visitedViews.splice(index, 1);
      }
        this.removeCachedView(view);
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
        this.visitedViews = this.visitedViews.filter(v =>
            v.path === view.path || v.meta?.affix || v.path === '/admin/home'
        );
        this.cachedViews = this.cachedViews.filter(name =>
            name === view.name || name === 'Home'
        );
    },

    // 关闭所有标签
    closeAllTags() {
        // 保留首页和固定标签
        this.visitedViews = this.visitedViews.filter(v =>
            v.meta?.affix || v.path === '/admin/home'
        );
        this.cachedViews = ['Home'];
    },

      // 更新标签
      updateVisitedView(view) {
          const index = this.visitedViews.findIndex(v => v.path === view.path);
      if (index > -1) {
          this.visitedViews[index] = {
              ...this.visitedViews[index],
              title: view.meta?.title || this.visitedViews[index].title
          };
      }
    },

    // 初始化首页标签
      initHomeTab() {
          const homeTab = {
        title: '首页',
        path: '/admin/home',
              name: 'Home',
        meta: { title: '首页', affix: true },
        closable: false
      };
      
      // 清空现有标签
          this.visitedViews = [homeTab];
          this.cachedViews = ['Home'];
      },

      // 关闭左侧标签
      closeLeftTags(view) {
          const index = this.visitedViews.findIndex(v => v.path === view.path);
          if (index > 0) {
              this.visitedViews = this.visitedViews.filter(v =>
                  v.meta?.affix || v.path === '/admin/home' || this.visitedViews.indexOf(v) >= index
              );
          }
      },

      // 关闭右侧标签
      closeRightTags(view) {
          const index = this.visitedViews.findIndex(v => v.path === view.path);
          if (index < this.visitedViews.length - 1) {
              this.visitedViews = this.visitedViews.filter(v =>
                  v.meta?.affix || v.path === '/admin/home' || this.visitedViews.indexOf(v) <= index
              );
          }
      },

      // 刷新标签
      refreshTag(view) {
          const index = this.visitedViews.findIndex(v => v.path === view.path);
          if (index > -1) {
              this.visitedViews[index] = {...view};
          }
      },

      // 添加缓存视图
      addCachedView(view) {
          if (view.name && !this.cachedViews.includes(view.name)) {
              this.cachedViews.push(view.name);
          }
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