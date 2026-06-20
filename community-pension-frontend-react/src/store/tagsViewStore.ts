import { create } from 'zustand'
import { persist } from 'zustand/middleware'

/**
 * Tag View Interface
 */
export interface TagView {
  path: string
  title: string
  name: string
  query?: Record<string, string>
  affix?: boolean // Fixed tag (cannot be closed)
}

/**
 * Tags View Store State Interface
 */
interface TagsViewState {
  visitedViews: TagView[]
  cachedViews: string[] // Component names for keep-alive
  
  // Actions
  addView: (view: TagView) => void
  addVisitedView: (view: TagView) => void
  addCachedView: (name: string) => void
  delView: (path: string) => Promise<TagView | undefined>
  delVisitedView: (path: string) => void
  delCachedView: (name: string) => void
  delOthersViews: (path: string) => void
  delAllViews: () => void
  delAllCachedViews: () => void
  updateVisitedView: (view: TagView) => void
}

/**
 * Tags View Store
 * 
 * Manages tab navigation history (similar to Vue Router tabs)
 */
export const useTagsViewStore = create<TagsViewState>()(
  persist(
    (set, get) => ({
      visitedViews: [],
      cachedViews: [],

      addView: (view) => {
        get().addVisitedView(view)
        if (view.name) {
          get().addCachedView(view.name)
        }
      },

      addVisitedView: (view) => {
        set((state) => {
          const exists = state.visitedViews.some((v) => v.path === view.path)
          if (exists) {
            // Update existing view
            return {
              visitedViews: state.visitedViews.map((v) =>
                v.path === view.path ? { ...v, ...view } : v
              ),
            }
          }
          // Add new view
          return {
            visitedViews: [...state.visitedViews, view],
          }
        })
      },

      addCachedView: (name) => {
        set((state) => {
          if (state.cachedViews.includes(name)) {
            return state
          }
          return {
            cachedViews: [...state.cachedViews, name],
          }
        })
      },

      delView: async (path) => {
        const view = get().visitedViews.find((v) => v.path === path)
        get().delVisitedView(path)
        if (view?.name) {
          get().delCachedView(view.name)
        }
        return view
      },

      delVisitedView: (path) => {
        set((state) => ({
          visitedViews: state.visitedViews.filter((v) => v.path !== path),
        }))
      },

      delCachedView: (name) => {
        set((state) => ({
          cachedViews: state.cachedViews.filter((n) => n !== name),
        }))
      },

      delOthersViews: (path) => {
        set((state) => ({
          visitedViews: state.visitedViews.filter(
            (v) => v.affix || v.path === path
          ),
          cachedViews: [],
        }))
      },

      delAllViews: () => {
        set((state) => ({
          visitedViews: state.visitedViews.filter((v) => v.affix),
          cachedViews: [],
        }))
      },

      delAllCachedViews: () => {
        set({ cachedViews: [] })
      },

      updateVisitedView: (view) => {
        set((state) => ({
          visitedViews: state.visitedViews.map((v) =>
            v.path === view.path ? { ...v, ...view } : v
          ),
        }))
      },
    }),
    {
      name: 'tags-view-storage',
    }
  )
)
