import { defineStore } from 'pinia';

export const useTagsViewStore = defineStore('tagsView', {
  state: () => ({
    visitedViews: [],
    cachedViews: []
  }),

  getters: {
    // 获取标签统计信息
    tagsStats() {
      return {
        total: this.visitedViews.length,
        cached: this.cachedViews.length,
        fixed: this.visitedViews.filter(v => v.meta?.affix).length,
        withTitle: this.visitedViews.filter(v => v.meta?.title).length,
        withName: this.visitedViews.filter(v => v.name).length
      };
    },
    
    // 获取标签概览
    tagsOverview() {
      return this.visitedViews.map(tag => ({
        path: tag.path,
        title: tag.meta?.title || '未设置标题',
        name: tag.name || '未设置',
        isFixed: tag.meta?.affix || false,
        fullPath: tag.fullPath || tag.path
      }));
    }
  },

  actions: {
    // 优化版标题获取函数，限制查找深度以提高性能
    getViewTitle(view) {
      // 首先尝试从view.meta.title获取
      if (view.meta?.title) {
        return view.meta.title;
      }
      
      // 接着尝试从匹配的路由中获取，限制查找最后两个匹配项
      if (view.matched && view.matched.length > 0) {
        const maxCheck = Math.min(2, view.matched.length);
        for (let i = view.matched.length - 1; i >= view.matched.length - maxCheck; i--) {
          const matchedRoute = view.matched[i];
          if (matchedRoute.meta?.title) {
            return matchedRoute.meta.title;
          }
        }
      }
      
      // 如果没有找到标题，尝试从path获取
      const path = view.path || '';
      const parts = path.split('/');
      if (parts.length > 0) {
        const lastPart = parts[parts.length - 1];
        if (lastPart) {
          return lastPart.charAt(0).toUpperCase() + lastPart.slice(1);
        }
      }
      
      // 默认标题
      return '未命名标签';
    },

    // 添加视图
    addView(view) {
      if (!view || typeof view !== 'object') {
        return;
      }
      
      // 检查特殊路径，不添加登录页和错误页
      if (view.path === '/admin/login' || 
          view.path.includes('/error') || 
          view.meta?.showInTab === false) {
        return;
      }
      
      // 确保view有meta对象
      if (!view.meta) {
        view.meta = {};
      }
      
      // 确保有标题
      if (!view.meta.title) {
        view.meta.title = this.getViewTitle(view);
      }
      
      // 规范化路径，去除末尾斜杠
      const normalizedPath = view.path.replace(/\/$/, '');
      view.path = normalizedPath;
      
      // 使用规范化路径检查标签是否已存在
      const existingView = this.visitedViews.find(v => 
        v.path === normalizedPath || v.path === normalizedPath + '/'
      );
      
      if (existingView) {
        return;
      }
      
      this.addVisitedView(view);
      this.addCachedView(view);
    },

    // 添加已访问视图
    addVisitedView(view) {
      if (!view || typeof view !== 'object' || !view.path) {
        return;
      }

      // 检查特殊路径，不添加登录页和错误页
      if (view.path === '/admin/login' || 
          view.path.includes('/error') || 
          view.meta?.showInTab === false) {
        return;
      }

      // 规范化路径，确保不会因为末尾斜杠导致重复
      const normalizedPath = view.path.replace(/\/$/, '');
      
      // 使用规范化路径检查标签是否已存在
      const existingTag = this.visitedViews.find(v => 
        v.path === normalizedPath || v.path === normalizedPath + '/'
      );
      
      if (existingTag) {
        return;
      }
      
      // 确保有meta对象
      if (!view.meta) {
        view.meta = {};
      }
      
      // 确保有标题
      if (!view.meta.title) {
        view.meta.title = this.getViewTitle(view);
      }
      
      // 使用规范化的路径
      const viewToAdd = {
        ...view,
        path: normalizedPath,
        fullPath: view.fullPath || normalizedPath
      };
      
      this.visitedViews.push(viewToAdd);
    },

    // 添加缓存视图
    addCachedView(view) {
      if (!view || typeof view !== 'object' || !view.name) {
        return;
      }

      const viewName = String(view.name);
      if (view.meta?.noCache === true) return;
      if (this.cachedViews.includes(viewName)) return;
      this.cachedViews.push(viewName);
    },

    // 删除视图
    delView(view) {
      if (!view || typeof view !== 'object' || !view.path) {
        return Promise.resolve({ visitedViews: [...this.visitedViews], cachedViews: [...this.cachedViews] });
      }

      // 检查是否正在删除当前激活的视图
      const isActiveView = this.visitedViews.some(v => v.path === view.path && this.isActiveView(v));
      if (isActiveView && this.visitedViews.length > 1) {
        // 找到当前视图的索引
        const index = this.visitedViews.findIndex(v => v.path === view.path);
        // 选择上一个或下一个视图作为新的激活视图
        const newActiveIndex = index > 0 ? index - 1 : index + 1;
        const newActiveView = this.visitedViews[newActiveIndex];
        // 可以在这里触发路由跳转或其他逻辑
      }

      return Promise.all([
        this.delVisitedView(view),
        this.delCachedView(view)
      ]).then(([visitedViews, cachedViews]) => {
        return {
          visitedViews,
          cachedViews
        };
      });
    },

    // 删除已访问视图
    delVisitedView(view) {
      return new Promise(resolve => {
        const index = this.visitedViews.findIndex(v => v.path === view.path);
        if (index !== -1) {
          this.visitedViews.splice(index, 1);
        }
        resolve([...this.visitedViews]);
      });
    },

    // 删除缓存视图
    delCachedView(view) {
      if (!view || typeof view !== 'object' || !view.name) {
        return Promise.resolve([...this.cachedViews]);
      }

      const viewName = String(view.name);
      return new Promise(resolve => {
        const index = this.cachedViews.indexOf(viewName);
        if (index !== -1) {
          this.cachedViews.splice(index, 1);
        }
        resolve([...this.cachedViews]);
      });
    },

    // 删除其他视图
    delOthersViews(view) {
      if (!view || typeof view !== 'object' || !view.path) {
        return Promise.resolve({ visitedViews: [...this.visitedViews], cachedViews: [...this.cachedViews] });
      }

      return Promise.all([
        this.delOthersVisitedViews(view),
        this.delOthersCachedViews(view)
      ]).then(([visitedViews, cachedViews]) => {
        return {
          visitedViews,
          cachedViews
        };
      });
    },

    // 删除其他已访问视图，使用 Set 优化性能
    delOthersVisitedViews(view) {
      return new Promise(resolve => {
        const keepViews = new Set();
        keepViews.add(view.path);
        // 保留固定标签
        this.visitedViews.forEach(v => {
          if (v.meta?.affix) {
            keepViews.add(v.path);
          }
        });
        this.visitedViews = this.visitedViews.filter(v => keepViews.has(v.path));
        resolve([...this.visitedViews]);
      });
    },

    // 删除其他缓存视图，使用 Set 优化性能
    delOthersCachedViews(view) {
      if (!view || typeof view !== 'object' || !view.name) {
        return Promise.resolve([...this.cachedViews]);
      }

      const viewName = String(view.name);
      return new Promise(resolve => {
        const keepViews = new Set();
        keepViews.add(viewName);
        // 保留对应的已访问视图的缓存
        this.visitedViews.forEach(v => {
          if (v.meta?.affix && v.name) {
            keepViews.add(String(v.name));
          }
        });
        this.cachedViews = this.cachedViews.filter(v => keepViews.has(v));
        resolve([...this.cachedViews]);
      });
    },

    // 删除左侧视图，使用 Set 优化性能
    delLeftViews(view) {
      if (!view || typeof view !== 'object' || !view.path) {
        return Promise.resolve({ visitedViews: [...this.visitedViews], cachedViews: [...this.cachedViews] });
      }

      return Promise.all([
        this.delLeftVisitedViews(view),
        this.delLeftCachedViews(view)
      ]).then(([visitedViews, cachedViews]) => {
        return {
          visitedViews,
          cachedViews
        };
      });
    },

    // 删除左侧已访问视图
    delLeftVisitedViews(view) {
      return new Promise(resolve => {
        const index = this.visitedViews.findIndex(v => v.path === view.path);
        if (index === -1) {
          resolve([...this.visitedViews]);
          return;
        }

        const keepPaths = new Set();
        // 保留固定标签的路径
        this.visitedViews.forEach((v, i) => {
          if (i >= index || v.meta?.affix) {
            keepPaths.add(v.path);
          }
        });
        this.visitedViews = this.visitedViews.filter(v => keepPaths.has(v.path));
        resolve([...this.visitedViews]);
      });
    },

    // 删除左侧缓存视图
    delLeftCachedViews(view) {
      if (!view || typeof view !== 'object' || !view.path) {
        return Promise.resolve([...this.cachedViews]);
      }

      return new Promise(resolve => {
        const index = this.visitedViews.findIndex(v => v.path === view.path);
        if (index === -1) {
          resolve([...this.cachedViews]);
          return;
        }

        const keepNames = new Set();
        // 保留右侧视图和固定视图的名称
        this.visitedViews.forEach((v, i) => {
          if ((i >= index || v.meta?.affix) && v.name) {
            keepNames.add(String(v.name));
          }
        });
        this.cachedViews = this.cachedViews.filter(v => keepNames.has(v));
        resolve([...this.cachedViews]);
      });
    },

    // 删除右侧视图，使用 Set 优化性能
    delRightViews(view) {
      if (!view || typeof view !== 'object' || !view.path) {
        return Promise.resolve({ visitedViews: [...this.visitedViews], cachedViews: [...this.cachedViews] });
      }

      return Promise.all([
        this.delRightVisitedViews(view),
        this.delRightCachedViews(view)
      ]).then(([visitedViews, cachedViews]) => {
        return {
          visitedViews,
          cachedViews
        };
      });
    },

    // 删除右侧已访问视图
    delRightVisitedViews(view) {
      return new Promise(resolve => {
        const index = this.visitedViews.findIndex(v => v.path === view.path);
        if (index === -1) {
          resolve([...this.visitedViews]);
          return;
        }

        const keepPaths = new Set();
        // 保留左侧视图和固定视图的路径
        this.visitedViews.forEach((v, i) => {
          if (i <= index || v.meta?.affix) {
            keepPaths.add(v.path);
          }
        });
        this.visitedViews = this.visitedViews.filter(v => keepPaths.has(v.path));
        resolve([...this.visitedViews]);
      });
    },

    // 删除右侧缓存视图
    delRightCachedViews(view) {
      if (!view || typeof view !== 'object' || !view.path) {
        return Promise.resolve([...this.cachedViews]);
      }

      return new Promise(resolve => {
        const index = this.visitedViews.findIndex(v => v.path === view.path);
        if (index === -1) {
          resolve([...this.cachedViews]);
          return;
        }

        const keepNames = new Set();
        // 保留左侧视图和固定视图的名称
        this.visitedViews.forEach((v, i) => {
          if ((i <= index || v.meta?.affix) && v.name) {
            keepNames.add(String(v.name));
          }
        });
        this.cachedViews = this.cachedViews.filter(v => keepNames.has(v));
        resolve([...this.cachedViews]);
      });
    },

    // 删除所有视图
    delAllViews() {
      return Promise.all([
        this.delAllVisitedViews(),
        this.delAllCachedViews()
      ]).then(([visitedViews, cachedViews]) => {
        return {
          visitedViews,
          cachedViews
        };
      });
    },

    // 删除所有已访问视图
    delAllVisitedViews() {
      return new Promise(resolve => {
        const affixTags = this.visitedViews.filter(tag => tag.meta?.affix);
        this.visitedViews = affixTags;
        resolve([...this.visitedViews]);
      });
    },

    // 删除所有缓存视图
    delAllCachedViews() {
      return new Promise(resolve => {
        const keepNames = new Set();
        // 保留固定视图的缓存名称
        this.visitedViews.forEach(v => {
          if (v.meta?.affix && v.name) {
            keepNames.add(String(v.name));
          }
        });
        this.cachedViews = this.cachedViews.filter(v => keepNames.has(v));
        resolve([...this.cachedViews]);
      });
    },

    // 更新已访问视图
    updateVisitedView(view) {
      if (!view || typeof view !== 'object' || !view.path) {
        return;
      }

      for (let i = 0; i < this.visitedViews.length; i++) {
        if (this.visitedViews[i].path === view.path) {
          const newView = {
            ...this.visitedViews[i],
            ...view,
            title: this.getViewTitle(view)
          };
          this.visitedViews.splice(i, 1, newView);
          break;
        }
      }
    },

    // 检查视图是否激活（可根据实际需求自定义）
    isActiveView(view) {
      // 这里可以添加逻辑来判断视图是否为当前激活视图
      // 例如：比较路径是否与当前路由路径一致
      return view.path === window.location.pathname;
    },

    // 移除已访问的标签页
    removeVisitedView(view) {
      if (!view || !view.path) {
        return;
      }
      
      const index = this.visitedViews.findIndex(v => v.path === view.path);
      if (index !== -1) {
        this.visitedViews.splice(index, 1);
      }
    },

    // 移除缓存的视图
    removeCachedView(view) {
      if (!view || !view.name) return;
      
      const index = this.cachedViews.indexOf(view.name);
      if (index !== -1) {
        this.cachedViews.splice(index, 1);
      }
    },

    // 关闭其他标签
    closeOthersTags(view) {
      if (!view || !view.path) {
        return;
      }
      
      const currentPath = view.path;
      const homePath = '/admin/home';
      
      // 保留当前标签、首页和固定标签
      this.visitedViews = this.visitedViews.filter(v => 
        v.path === currentPath || v.path === homePath || (v.meta && v.meta.affix)
      );
      
      // 清理缓存视图
      const cacheNames = this.visitedViews
        .filter(v => v.name)
        .map(v => v.name);
      
      this.cachedViews = this.cachedViews.filter(name => 
        cacheNames.includes(name)
      );
    },

    // 关闭左侧标签
    closeLeftTags(view) {
      if (!view || !view.path) {
        return;
      }
      
      const index = this.visitedViews.findIndex(v => v.path === view.path);
      if (index === -1) return;
      
      // 保留index及之后的标签，以及首页和固定标签
      this.visitedViews = this.visitedViews.filter((v, i) => 
        i >= index || v.path === '/admin/home' || (v.meta && v.meta.affix)
      );
    },

    // 关闭右侧标签
    closeRightTags(view) {
      if (!view || !view.path) {
        return;
      }
      
      const index = this.visitedViews.findIndex(v => v.path === view.path);
      if (index === -1) return;
      
      // 保留index及之前的标签，以及首页和固定标签
      this.visitedViews = this.visitedViews.filter((v, i) => 
        i <= index || v.path === '/admin/home' || (v.meta && v.meta.affix)
      );
    },

    // 关闭所有标签
    closeAllTags() {
      // 只保留固定标签和首页
      this.visitedViews = this.visitedViews.filter(v => 
        (v.meta && v.meta.affix) || v.path === '/admin/home'
      );
      
      // 清理缓存视图
      const cacheNames = this.visitedViews
        .filter(v => v.name)
        .map(v => v.name);
      
      this.cachedViews = this.cachedViews.filter(name => 
        cacheNames.includes(name)
      );
    },

    // 刷新标签
    refreshTag(view) {
      if (!view || !view.path) {
        return;
      }
      
      // 从缓存列表中移除，强制重新创建
      if (view.name) {
        const index = this.cachedViews.indexOf(view.name);
        if (index !== -1) {
          this.cachedViews.splice(index, 1);
        }
      }
      
      // 更新标签信息
      const tagIndex = this.visitedViews.findIndex(v => v.path === view.path);
      if (tagIndex !== -1) {
        // 确保有meta对象和标题
        if (!view.meta) {
          view.meta = {};
        }
        if (!view.meta.title) {
          view.meta.title = this.getViewTitle(view);
        }
        
        this.visitedViews[tagIndex] = {
          ...this.visitedViews[tagIndex],
          ...view
        };
      }
    },

    // 调试方法：打印所有标签
    printAllTags() {
      return {
        stats: this.tagsStats,
        tags: this.tagsOverview
      };
    },
    
    // 调试方法：检查标签完整性
    checkTagsIntegrity() {
      const issues = [];
      
      // 检查每个标签
      this.visitedViews.forEach((tag, index) => {
        if (!tag.path) {
          issues.push(`标签 #${index} 缺少路径`);
        }
        
        if (!tag.meta) {
          issues.push(`标签 ${tag.path || `#${index}`} 缺少meta对象`);
        } else if (!tag.meta.title) {
          issues.push(`标签 ${tag.path} 缺少标题`);
        }
        
        if (tag.name && !this.cachedViews.includes(tag.name)) {
          issues.push(`标签 ${tag.path} 有name但未被缓存`);
        }
      });
      
      // 检查缓存视图
      this.cachedViews.forEach(name => {
        const found = this.visitedViews.some(v => v.name === name);
        if (!found) {
          issues.push(`缓存视图 ${name} 没有对应的访问视图`);
        }
      });
      
      return {
        hasIssues: issues.length > 0,
        issues
      };
    },
    
    // 查找并返回重复的标签
    findDuplicateTags() {
      const normalizedPaths = new Map();
      const duplicates = [];
      
      this.visitedViews.forEach(tag => {
        const normalizedPath = tag.path.replace(/\/$/, '');
        
        if (normalizedPaths.has(normalizedPath)) {
          duplicates.push({
            original: normalizedPaths.get(normalizedPath),
            duplicate: tag
          });
        } else {
          normalizedPaths.set(normalizedPath, tag);
        }
      });
      
      return duplicates;
    },
    
    // 清理所有重复标签
    cleanupDuplicateTags() {
      const duplicates = this.findDuplicateTags();
      
      if (duplicates.length > 0) {
        // 收集要删除的标签索引
        const indexesToRemove = [];
        
        duplicates.forEach(pair => {
          const index = this.visitedViews.findIndex(tag => tag === pair.duplicate);
          if (index !== -1) {
            indexesToRemove.push(index);
          }
        });
        
        // 从后向前删除，避免索引变化问题
        indexesToRemove.sort((a, b) => b - a).forEach(index => {
          this.visitedViews.splice(index, 1);
        });
      }
      
      return duplicates.length;
    }
  }
});