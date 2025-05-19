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
    // 获取标签概览
    tagsOverview() {
      return {
        total: this.visitedViews.length,
        cached: this.cachedViews.length,
        fixed: this.visitedViews.filter(tag => tag.meta && tag.meta.affix).length,
        active: this.visitedViews.filter(tag => tag.meta && tag.meta.active).length
      };
    }
  },

  actions: {
    // 优化版标题获取函数，限制查找深度以提高性能
    getViewTitle(view) {
      // 设置最大查找深度，避免无限递归
      const MAX_DEPTH = 5;
      let depth = 0;
      
      // 递归函数，用于查找标题
      const findTitle = (route, currentDepth) => {
        if (currentDepth > MAX_DEPTH) return '';
        
        // 检查当前路由是否有标题
        if (route.meta && route.meta.title) {
          return route.meta.title;
        }
        
        // 如果有子路由，递归查找
        if (route.children && route.children.length > 0) {
          for (const child of route.children) {
            const title = findTitle(child, currentDepth + 1);
            if (title) return title;
          }
        }
        
        return '';
      };
      
      // 首先检查视图本身是否有标题
      if (view.meta && view.meta.title) {
        return view.meta.title;
      }
      
      // 否则尝试从路由配置中查找
      return findTitle(view, depth);
    },

    // 添加视图
    addView(view) {
      // 如果视图无效，直接返回
      if (!view || !view.path) {
        console.warn('尝试添加无效视图:', view);
        return;
      }

      // 检查是否已经存在相同路径的视图
      const existingViewIndex = this.visitedViews.findIndex(v => v.path === view.path);

      if (existingViewIndex !== -1) {
        // 如果已存在，更新视图
        const existingView = this.visitedViews[existingViewIndex];
        
        // 合并元数据
        const updatedMeta = { ...existingView.meta, ...view.meta };
        
        // 创建更新后的视图对象
        const updatedView = {
          ...existingView,
          title: view.title || existingView.title || this.getViewTitle(view),
          meta: updatedMeta
        };
        
        // 更新视图
        this.visitedViews.splice(existingViewIndex, 1, updatedView);
      } else {
        // 如果不存在，添加新视图
        this.addVisitedView(view);
      }

      // 添加到缓存视图
      this.addCachedView(view);
      
      // 持久化标签
      persistTags(this.visitedViews);
      
      return {
        visitedViews: [...this.visitedViews],
        cachedViews: [...this.cachedViews]
      };
    },

    // 添加已访问视图
    addVisitedView(view) {
      // 如果视图无效，直接返回
      if (!view || !view.path) {
        console.warn('尝试添加无效的已访问视图:', view);
        return;
      }
      
      // 检查是否已存在相同路径的视图
      const existingViewIndex = this.visitedViews.findIndex(v => v.path === view.path);
      
      if (existingViewIndex !== -1) {
        // 如果已存在，更新视图
        const existingView = this.visitedViews[existingViewIndex];
        
        // 确保保留固定标签属性
        const isAffix = existingView.meta && existingView.meta.affix;
        
        // 合并元数据，确保保留固定标签属性
        const updatedMeta = {
          ...existingView.meta,
          ...view.meta,
          affix: isAffix || (view.meta && view.meta.affix)
        };
        
        // 创建更新后的视图对象
        const updatedView = {
          ...existingView,
          title: view.title || existingView.title || this.getViewTitle(view),
          meta: updatedMeta
        };
        
        // 更新视图
        this.visitedViews.splice(existingViewIndex, 1, updatedView);
      } else {
        // 如果不存在，添加新视图
        const newView = {
          ...view,
          title: view.title || this.getViewTitle(view),
          // 确保视图有元数据
          meta: view.meta || {}
        };
        
        this.visitedViews.push(newView);
      }
      
      // 持久化标签
      persistTags(this.visitedViews);
      
      return [...this.visitedViews];
    },

    // 添加缓存视图
    addCachedView(view) {
      if (!view || !view.name || this.cachedViews.includes(view.name)) return;
      
      // 如果视图需要缓存
      if (view.meta && view.meta.keepAlive) {
        this.cachedViews.push(view.name);
      }
      
      return [...this.cachedViews];
    },

    // 删除视图
    delView(view) {
      return new Promise(resolve => {
        this.delVisitedView(view);
        this.delCachedView(view);
        resolve({
          visitedViews: [...this.visitedViews],
          cachedViews: [...this.cachedViews]
        });
      });
    },

    // 删除已访问视图
    delVisitedView(view) {
      return new Promise(resolve => {
        // 过滤掉要删除的视图
        this.visitedViews = this.visitedViews.filter(v => {
          return v.path !== view.path || (v.meta && v.meta.affix);
        });
        
        // 持久化标签
        persistTags(this.visitedViews);
        
        resolve([...this.visitedViews]);
      });
    },

    // 删除缓存视图
    delCachedView(view) {
      return new Promise(resolve => {
        if (!view || !view.name) {
          resolve([...this.cachedViews]);
          return;
        }
        
        // 过滤掉要删除的缓存视图
        const index = this.cachedViews.indexOf(view.name);
        if (index > -1) {
          this.cachedViews.splice(index, 1);
        }
        
        resolve([...this.cachedViews]);
      });
    },

    // 删除其他视图
    delOthersViews(view) {
      return new Promise(resolve => {
        this.delOthersVisitedViews(view);
        this.delOthersCachedViews(view);
        resolve({
          visitedViews: [...this.visitedViews],
          cachedViews: [...this.cachedViews]
        });
      });
    },

    // 删除其他已访问视图，直接修改 state
    delOthersVisitedViews(view) {
      return new Promise(resolve => {
        // 保留当前视图和固定标签
        this.visitedViews = this.visitedViews.filter(v => {
          return v.path === view.path || (v.meta && v.meta.affix);
        });
        
        // 持久化标签
        persistTags(this.visitedViews);
        
        resolve([...this.visitedViews]);
      });
    },

    // 删除其他缓存视图，直接修改 state
    delOthersCachedViews(view) {
      return new Promise(resolve => {
        if (!view || !view.name) {
          // 如果没有指定视图名称，保留所有缓存
          resolve([...this.cachedViews]);
          return;
        }
        
        // 只保留当前视图的缓存
        const index = this.cachedViews.indexOf(view.name);
        if (index > -1) {
          // 只保留当前视图的缓存
          this.cachedViews = this.cachedViews.slice(index, index + 1);
        } else {
          // 如果当前视图不在缓存中，清空所有缓存
          this.cachedViews = [];
        }
        
        resolve([...this.cachedViews]);
      });
    },

    // 删除左侧视图，使用 Set 优化性能
    delLeftViews(view) {
      return new Promise(resolve => {
        this.delLeftVisitedViews(view);
        this.delLeftCachedViews(view);
        resolve({
          visitedViews: [...this.visitedViews],
          cachedViews: [...this.cachedViews]
        });
      });
    },

    // 删除左侧已访问视图
    delLeftVisitedViews(view) {
      return new Promise(resolve => {
        // 找到当前视图的索引
        const index = this.visitedViews.findIndex(v => v.path === view.path);
        
        if (index === -1) {
          resolve([...this.visitedViews]);
          return;
        }
        
        // 过滤出要保留的视图：当前视图右侧的视图和固定标签
        this.visitedViews = this.visitedViews.filter((v, i) => {
          return i >= index || (v.meta && v.meta.affix);
        });
        
        // 持久化标签
        persistTags(this.visitedViews);
        
        resolve([...this.visitedViews]);
      });
    },

    // 删除左侧缓存视图
    delLeftCachedViews(view) {
      return new Promise(resolve => {
        if (!view || !view.name) {
          resolve([...this.cachedViews]);
          return;
        }
        
        // 找到当前视图的索引
        const index = this.cachedViews.indexOf(view.name);
        
        if (index === -1) {
          resolve([...this.cachedViews]);
          return;
        }
        
        // 只保留当前视图及其右侧的缓存
        this.cachedViews = this.cachedViews.slice(index);
        
        resolve([...this.cachedViews]);
      });
    },

    // 删除右侧视图，使用 Set 优化性能
    delRightViews(view) {
      return new Promise(resolve => {
        this.delRightVisitedViews(view);
        this.delRightCachedViews(view);
        resolve({
          visitedViews: [...this.visitedViews],
          cachedViews: [...this.cachedViews]
        });
      });
    },

    // 删除右侧已访问视图
    delRightVisitedViews(view) {
      return new Promise(resolve => {
        // 找到当前视图的索引
        const index = this.visitedViews.findIndex(v => v.path === view.path);
        
        if (index === -1) {
          resolve([...this.visitedViews]);
          return;
        }
        
        // 过滤出要保留的视图：当前视图左侧的视图和固定标签
        this.visitedViews = this.visitedViews.filter((v, i) => {
          return i <= index || (v.meta && v.meta.affix);
        });
        
        // 持久化标签
        persistTags(this.visitedViews);
        
        resolve([...this.visitedViews]);
      });
    },

    // 删除右侧缓存视图
    delRightCachedViews(view) {
      return new Promise(resolve => {
        if (!view || !view.name) {
          resolve([...this.cachedViews]);
          return;
        }
        
        // 找到当前视图的索引
        const index = this.cachedViews.indexOf(view.name);
        
        if (index === -1) {
          resolve([...this.cachedViews]);
          return;
        }
        
        // 只保留当前视图及其左侧的缓存
        this.cachedViews = this.cachedViews.slice(0, index + 1);
        
        resolve([...this.cachedViews]);
      });
    },

    // 删除所有视图，保留首页标签
    delAllViews() {
      return new Promise(resolve => {
        this.delAllVisitedViews();
        this.delAllCachedViews();
        resolve({
          visitedViews: [...this.visitedViews],
          cachedViews: [...this.cachedViews]
        });
      });
    },

    // 删除所有已访问视图，保留首页和固定标签
    delAllVisitedViews() {
      return new Promise(resolve => {
        // 只保留固定标签
        const affixTags = this.visitedViews.filter(tag => tag.meta && tag.meta.affix);
        
        // 更新视图列表，只保留固定标签
        this.visitedViews = affixTags;
        
        // 持久化标签
        persistTags(this.visitedViews);
        
        resolve([...this.visitedViews]);
      });
    },

    // 删除所有缓存视图
    delAllCachedViews() {
      return new Promise(resolve => {
        // 清空缓存视图
        this.cachedViews = [];
        resolve([]);
      });
    },

    // 更新已访问视图 - 增强版本，确保标签激活
    updateVisitedView(view) {
      // 如果视图无效，直接返回
      if (!view || !view.path) {
        console.warn('尝试更新无效视图:', view);
        return;
      }
      
      // 查找视图索引
      const index = this.visitedViews.findIndex(v => v.path === view.path);
      
      if (index === -1) {
        // 如果视图不存在，添加它
        this.addVisitedView(view);
        return;
      }
      
      // 获取当前视图
      const currentView = this.visitedViews[index];
      
      // 确保保留固定标签属性
      const isAffix = currentView.meta && currentView.meta.affix;
      
      // 合并元数据，确保保留固定标签属性
      const updatedMeta = {
        ...currentView.meta,
        ...view.meta,
        affix: isAffix || (view.meta && view.meta.affix),
        // 标记为活动标签
        active: true
      };
      
      // 创建更新后的视图
      const updatedView = {
        ...currentView,
        title: view.title || currentView.title || this.getViewTitle(view),
        meta: updatedMeta
      };
      
      // 更新视图
      this.visitedViews.splice(index, 1, updatedView);
      
      // 持久化标签
      persistTags(this.visitedViews);
      
      return [...this.visitedViews];
    },

    // 检查视图是否激活（可根据实际需求自定义）
    isActiveView(view) {
      if (!view || !view.path) return false;
      return this.visitedViews.some(v => v.path === view.path && v.meta && v.meta.active);
    },

    // 移除已访问的标签页
    removeVisitedView(view) {
      for (const [i, v] of this.visitedViews.entries()) {
        if (v.path === view.path) {
          this.visitedViews.splice(i, 1);
          break;
        }
      }
      persistTags(this.visitedViews);
    },

    // 移除缓存的视图
    removeCachedView(view) {
      const index = this.cachedViews.indexOf(view.name);
      if (index > -1) {
        this.cachedViews.splice(index, 1);
      }
    },

    // 关闭其他标签
    closeOthersTags(view) {
      return new Promise(resolve => {
        // 保留当前视图和固定标签
        const affixTags = this.visitedViews.filter(tag => tag.meta && tag.meta.affix);
        const currentViewExists = this.visitedViews.some(tag => tag.path === view.path);
        
        // 如果当前视图不存在于标签列表中，先添加它
        if (!currentViewExists) {
          this.addVisitedView(view);
        }
        
        // 更新视图列表，只保留当前视图和固定标签
        this.visitedViews = [
          ...affixTags.filter(tag => tag.path !== view.path),
          ...this.visitedViews.filter(tag => tag.path === view.path)
        ];
        
        // 持久化标签
        persistTags(this.visitedViews);
        
        resolve();
      });
    },

    // 关闭左侧标签
    closeLeftTags(view) {
      return new Promise(resolve => {
        const viewIndex = this.visitedViews.findIndex(v => v.path === view.path);
        if (viewIndex === -1) {
          resolve();
          return;
        }
        
        // 保留当前视图右侧的标签和固定标签
        this.visitedViews = this.visitedViews.filter((tag, index) => {
          return index >= viewIndex || (tag.meta && tag.meta.affix);
        });
        
        persistTags(this.visitedViews);
        resolve();
      });
    },

    // 关闭右侧标签
    closeRightTags(view) {
      return new Promise(resolve => {
        const viewIndex = this.visitedViews.findIndex(v => v.path === view.path);
        if (viewIndex === -1) {
          resolve();
          return;
        }
        
        // 保留当前视图左侧的标签和固定标签
        this.visitedViews = this.visitedViews.filter((tag, index) => {
          return index <= viewIndex || (tag.meta && tag.meta.affix);
        });
        
        persistTags(this.visitedViews);
        resolve();
      });
    },

    // 关闭所有标签
    closeAllTags() {
      return new Promise(resolve => {
        // 只保留固定标签
        const affixTags = this.visitedViews.filter(tag => tag.meta && tag.meta.affix);
        this.visitedViews = affixTags;
        
        // 清空缓存视图
        this.cachedViews = [];
        
        // 持久化标签
        persistTags(this.visitedViews);
        
        resolve();
      });
    },

    // 刷新标签
    refreshTag(view) {
      return new Promise(resolve => {
        // 移除缓存视图，强制组件重新渲染
        this.delCachedView(view).then(() => {
          // 添加回缓存视图，确保下次访问时能够正常缓存
          this.addCachedView(view);
          resolve();
        });
      });
    },

    // 清理所有重复标签 - 增强版
    cleanupDuplicateTags() {
      return new Promise(resolve => {
        // 使用 Map 来存储唯一路径的标签
        const uniqueTags = new Map();
        
        // 遍历所有标签，只保留每个路径的最新标签
        for (const tag of this.visitedViews) {
          // 如果是固定标签，始终保留
          if (tag.meta && tag.meta.affix) {
            uniqueTags.set(`affix-${tag.path}`, tag);
            continue;
          }
          
          // 对于非固定标签，只保留每个路径的最新标签
          if (!uniqueTags.has(tag.path)) {
            uniqueTags.set(tag.path, tag);
          } else {
            // 如果已存在，检查是否需要更新（例如，如果新标签是活动的）
            const existingTag = uniqueTags.get(tag.path);
            
            // 如果新标签是活动的，或者有更新的元数据，则更新
            if ((tag.meta && tag.meta.active) || 
                (tag.meta && existingTag.meta && 
                 Object.keys(tag.meta).length > Object.keys(existingTag.meta).length)) {
              uniqueTags.set(tag.path, tag);
            }
          }
        }
        
        // 将 Map 转换回数组
        this.visitedViews = Array.from(uniqueTags.values());
        
        // 清理缓存视图中的重复项
        const uniqueCachedViews = new Set(this.cachedViews);
        this.cachedViews = Array.from(uniqueCachedViews);
        
        // 持久化标签
        persistTags(this.visitedViews);
        
        resolve({
          visitedViews: [...this.visitedViews],
          cachedViews: [...this.cachedViews]
        });
      });
    }
  }
});
