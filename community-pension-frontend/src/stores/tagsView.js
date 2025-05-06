import { defineStore } from 'pinia';

// 从 localStorage 或 sessionStorage 中获取持久化的标签
const getPersistedTags = () => {
  try {
    const persistedTags = sessionStorage.getItem('tagsView');
    if (persistedTags) {
      return JSON.parse(persistedTags);
    }
  } catch (error) {
    console.error('从存储中恢复标签失败:', error);
  }
  return [];
};

// 将标签持久化到 localStorage 或 sessionStorage
const persistTags = (tags) => {
  try {
    sessionStorage.setItem('tagsView', JSON.stringify(tags));
  } catch (error) {
    console.error('持久化标签失败:', error);
  }
};

export const useTagsViewStore = defineStore('tagsView', {
  state: () => ({
    visitedViews: getPersistedTags(),
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
      
      // 使用更严格的检查标准来确定标签是否已存在
      const existingView = this.visitedViews.find(v => {
        // 规范化路径进行比较
        const vNormalizedPath = v.path.replace(/\/$/, '');
        return vNormalizedPath === normalizedPath;
      });
      
      if (existingView) {
        // 如果标签已存在，更新标签信息
        if (view.meta && view.meta.title && (!existingView.meta || !existingView.meta.title)) {
          existingView.meta = existingView.meta || {};
          existingView.meta.title = view.meta.title;
        }
        
        // 更新fullPath，以确保查询参数的变化能被捕获
        if (view.fullPath && view.fullPath !== existingView.fullPath) {
          existingView.fullPath = view.fullPath;
        }
        
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
      
      // 判断是否是首页标签
      const isHomeTag = normalizedPath === '/admin/home';
      
      if (isHomeTag) {
        // 如果是首页标签，放在数组的最前面
        this.visitedViews.unshift(viewToAdd);
      } else {
        // 其他标签放在数组最后
        this.visitedViews.push(viewToAdd);
      }
      
      // 持久化标签状态
      persistTags(this.visitedViews);
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
        return Promise.resolve();
      }
      return Promise.all([
        this.delVisitedView(view),
        this.delCachedView(view)
      ]);
    },

    // 删除已访问视图
    delVisitedView(view) {
      return new Promise(resolve => {
        const index = this.visitedViews.findIndex(v => v.path === view.path);
        if (index !== -1) {
          this.visitedViews.splice(index, 1);
          // 持久化标签状态
          persistTags(this.visitedViews);
        }
        resolve(this.visitedViews);
      });
    },

    // 删除缓存视图
    delCachedView(view) {
      if (!view || typeof view !== 'object' || !view.name) {
        return Promise.resolve();
      }
      return new Promise(resolve => {
        const viewName = String(view.name);
        const index = this.cachedViews.indexOf(viewName);
        if (index !== -1) {
          this.cachedViews.splice(index, 1);
        }
        resolve(this.cachedViews);
      });
    },

    // 删除其他视图
    delOthersViews(view) {
      if (!view || typeof view !== 'object' || !view.path) {
        return Promise.resolve();
      }
      return Promise.all([
        this.delOthersVisitedViews(view),
        this.delOthersCachedViews(view)
      ]);
    },

    // 删除其他已访问视图，直接修改 state
    delOthersVisitedViews(view) {
      return new Promise(resolve => {
        const keepViews = new Set();
        keepViews.add(view.path);
        this.visitedViews.forEach(v => {
          if (v.meta?.affix) {
            keepViews.add(v.path);
          }
        });
        this.visitedViews = this.visitedViews.filter(v => keepViews.has(v.path));
        resolve(this.visitedViews);
      });
    },

    // 删除其他缓存视图，直接修改 state
    delOthersCachedViews(view) {
      if (!view || typeof view !== 'object' || !view.name) {
        return Promise.resolve();
      }
      return new Promise(resolve => {
        const viewName = String(view.name);
        const keepViews = new Set();
        keepViews.add(viewName);
        this.visitedViews.forEach(v => {
          if (v.meta?.affix && v.name) {
            keepViews.add(String(v.name));
          }
        });
        this.cachedViews = this.cachedViews.filter(v => keepViews.has(v));
        resolve(this.cachedViews);
      });
    },

    // 删除左侧视图，使用 Set 优化性能
    delLeftViews(view) {
      if (!view || typeof view !== 'object' || !view.path) {
        return Promise.resolve();
      }
      return Promise.all([
        this.delLeftVisitedViews(view),
        this.delLeftCachedViews(view)
      ]);
    },

    // 删除左侧已访问视图
    delLeftVisitedViews(view) {
      return new Promise(resolve => {
        const index = this.visitedViews.findIndex(v => v.path === view.path);
        if (index === -1) {
          return resolve(this.visitedViews);
        }
        const keepPaths = new Set();
        this.visitedViews.forEach((v, i) => {
          if (i >= index || v.meta?.affix) {
            keepPaths.add(v.path);
          }
        });
        this.visitedViews = this.visitedViews.filter(v => keepPaths.has(v.path));
        resolve(this.visitedViews);
      });
    },

    // 删除左侧缓存视图
    delLeftCachedViews(view) {
      if (!view || typeof view !== 'object' || !view.path) {
        return Promise.resolve();
      }
      return new Promise(resolve => {
        const index = this.visitedViews.findIndex(v => v.path === view.path);
        if (index === -1) {
          return resolve(this.cachedViews);
        }
        const keepNames = new Set();
        this.visitedViews.forEach((v, i) => {
          if ((i >= index || v.meta?.affix) && v.name) {
            keepNames.add(String(v.name));
          }
        });
        this.cachedViews = this.cachedViews.filter(name => keepNames.has(name));
        resolve(this.cachedViews);
      });
    },

    // 删除右侧视图，使用 Set 优化性能
    delRightViews(view) {
      if (!view || typeof view !== 'object' || !view.path) {
        return Promise.resolve();
      }
      return Promise.all([
        this.delRightVisitedViews(view),
        this.delRightCachedViews(view)
      ]);
    },

    // 删除右侧已访问视图
    delRightVisitedViews(view) {
      return new Promise(resolve => {
        const index = this.visitedViews.findIndex(v => v.path === view.path);
        if (index === -1) {
          return resolve(this.visitedViews);
        }
        const keepPaths = new Set();
        this.visitedViews.forEach((v, i) => {
          if (i <= index || v.meta?.affix) {
            keepPaths.add(v.path);
          }
        });
        this.visitedViews = this.visitedViews.filter(v => keepPaths.has(v.path));
        resolve(this.visitedViews);
      });
    },

    // 删除右侧缓存视图
    delRightCachedViews(view) {
      if (!view || typeof view !== 'object' || !view.path) {
        return Promise.resolve();
      }
      return new Promise(resolve => {
        const index = this.visitedViews.findIndex(v => v.path === view.path);
        if (index === -1) {
          return resolve(this.cachedViews);
        }
        const keepNames = new Set();
        this.visitedViews.forEach((v, i) => {
          if ((i <= index || v.meta?.affix) && v.name) {
            keepNames.add(String(v.name));
          }
        });
        this.cachedViews = this.cachedViews.filter(v => keepNames.has(v));
        resolve(this.cachedViews);
      });
    },

    // 删除所有视图，保留首页标签
    delAllViews() {
      return new Promise(resolve => {
        // 先保存首页标签
        const homeTag = this.visitedViews.find(tag => {
          const normalizedPath = tag.path.replace(/\/$/, '');
          return normalizedPath === '/admin/home' || normalizedPath === '/dashboard';
        });
        
        Promise.all([
          this.delAllVisitedViews(),
          this.delAllCachedViews()
        ]).then(([visitedViews, cachedViews]) => {
          // 如果 visitedViews 中已经有首页标签，则不需要再添加
          const hasHomeTag = this.visitedViews.some(tag => {
            const normalizedPath = tag.path.replace(/\/$/, '');
            return normalizedPath === '/admin/home' || normalizedPath === '/dashboard';
          });
          
          // 只有当没有首页标签且 homeTag 存在时，才添加首页标签
          if (!hasHomeTag && homeTag) {
            this.visitedViews.unshift(homeTag);
          }
          
          // 清理可能存在的重复标签
          this.cleanupDuplicateTags();
          
          // 持久化标签状态
          persistTags(this.visitedViews);
          
          resolve({ visitedViews: this.visitedViews, cachedViews });
        });
      });
    },

    // 删除所有已访问视图，保留首页和固定标签
    delAllVisitedViews() {
      return new Promise(resolve => {
        // 保留固定标签和首页标签
        const tagsToKeep = this.visitedViews.filter(tag => {
          // 检查是否是固定标签
          if (tag.meta?.affix) return true;
          
          // 检查是否是首页标签
          const normalizedPath = tag.path.replace(/\/$/, '');
          return normalizedPath === '/admin/home' || normalizedPath === '/dashboard';
        });
        
        this.visitedViews = tagsToKeep;
        // 持久化标签状态
        persistTags(this.visitedViews);
        resolve([...this.visitedViews]);
      });
    },

    // 删除所有缓存视图
    delAllCachedViews() {
      return new Promise(resolve => {
        const keepNames = new Set();
        const affixTags = this.visitedViews.filter(tag => tag.meta?.affix);
        affixTags.forEach(v => {
          if (v.name) {
            keepNames.add(String(v.name));
          }
        });
        this.cachedViews = this.cachedViews.filter(v => keepNames.has(v));
        resolve(this.cachedViews);
      });
    },

    // 更新已访问视图 - 增强版本，确保标签激活
    updateVisitedView(view) {
      if (!view || typeof view !== 'object') {
        return;
      }
      
      // 规范化路径进行比较
      const viewPath = view.path?.replace(/\/$/, '') || '';
      if (!viewPath) return;
      
      // 先检查是否存在匹配的标签
      let found = false;
      
      for (let i = 0; i < this.visitedViews.length; i++) {
        const currentTag = this.visitedViews[i];
        const currentPath = currentTag.path.replace(/\/$/, '');
        
        // 使用规范化路径进行比较
        if (currentPath === viewPath) {
          // 找到匹配的标签，更新它
          const newView = {
            ...currentTag,
            ...view,
            title: view.meta?.title || currentTag.meta?.title || this.getViewTitle(view)
          };
          
          // 如果不是首页标签，则移动到数组最后，表示最近访问
          // 直接替换原位置的标签，不改变标签顺序
          this.visitedViews[i] = newView;
          
          // 不过首页标签还是需要特殊处理，确保它始终在第一位
          const isHomeTag = currentPath === '/admin/home';
          
          if (isHomeTag && i !== 0) {
            // 如果是首页标签但不在第一位，移动到第一位
            this.visitedViews.splice(i, 1);
            this.visitedViews.unshift(newView);
          }
          
          // 持久化标签状态
          persistTags(this.visitedViews);
          found = true;
          break;
        }
      }
      
      // 如果没有找到匹配的标签，则添加一个新标签
      if (!found) {
        this.addView(view);
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
      if (view.name) {
        this.delCachedView(view);
      }
    },

    // 清理所有重复标签 - 增强版
    cleanupDuplicateTags() {
      // 使用Map查找重复标签，以规范化路径为键
      const normalizedPathMap = new Map();
      const duplicateTags = [];
      const keepTags = [];
      
      // 第一步：分类所有标签，找出重复项和要保留的项
      this.visitedViews.forEach(tag => {
        // 规范化路径，去除末尾斜杠和重复斜杠
        const normalizedPath = tag.path.replace(/\/+$/, '').replace(/\/\/+/g, '/');
        
        // 如果这个路径已经存在
        if (normalizedPathMap.has(normalizedPath)) {
          // 获取已存在的标签
          const existingTag = normalizedPathMap.get(normalizedPath);
          
          // 判断哪个标签更好：优先保留有标题的标签
          if (tag.meta?.title && !existingTag.meta?.title) {
            // 如果新标签有标题而旧标签没有，替换旧标签
            duplicateTags.push(existingTag);
            normalizedPathMap.set(normalizedPath, tag);
            keepTags.push(tag);
          } else if (tag.meta?.affix && !existingTag.meta?.affix) {
            // 如果新标签是固定标签而旧标签不是，替换旧标签
            duplicateTags.push(existingTag);
            normalizedPathMap.set(normalizedPath, tag);
            keepTags.push(tag);
          } else {
            // 否则保留旧标签，将新标签标记为重复
            duplicateTags.push(tag);
          }
        } else {
          // 这是第一次出现的路径，添加到Map中
          normalizedPathMap.set(normalizedPath, tag);
          keepTags.push(tag);
        }
      });
      
      // 如果有重复标签，重新构建 visitedViews 数组
      if (duplicateTags.length > 0) {
        // 先过滤出要保留的标签
        const filteredTags = this.visitedViews.filter(tag => 
          keepTags.includes(tag) && !duplicateTags.includes(tag)
        );
        
        // 分离首页标签和其他标签
        const homeTag = filteredTags.find(tag => tag.path.replace(/\/$/, '') === '/admin/home');
        const otherTags = filteredTags.filter(tag => tag.path.replace(/\/$/, '') !== '/admin/home');
        
        // 重新构建数组，首页标签始终在第一位
        this.visitedViews = [];
        
        // 如果有首页标签，先添加它
        if (homeTag) {
          this.visitedViews.push(homeTag);
        }
        
        // 然后添加其他标签
        this.visitedViews = [...this.visitedViews, ...otherTags];
        
        // 持久化标签状态
        persistTags(this.visitedViews);
        
        // 同步清理缓存视图
        if (duplicateTags.some(tag => tag.name)) {
          // 如果有重复标签带有name属性，需要清理缓存
          const cachedSet = new Set(this.cachedViews);
          this.cachedViews = Array.from(cachedSet);
        }
      }
      
      return duplicateTags.length;
    }
  }
});