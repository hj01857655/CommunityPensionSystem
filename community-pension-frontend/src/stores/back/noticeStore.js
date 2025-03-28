import { defineStore } from 'pinia'
import { 
  getNoticeList, 
  getNoticeDetail, 
  addNotice, 
  updateNotice, 
  deleteNotice, 
  publishNotice, 
  withdrawNotice,
  toggleNoticeTop
} from '@/api/back/notice/notice'

export const useNoticeStore = defineStore('notice', {
  state: () => ({
    noticeList: [],
    total: 0,
    currentNotice: null
  }),

  actions: {
    /**
     * 获取通知公告列表
     * @param {Object} params - 查询参数
     * @returns {Promise<Object>}
     */
    async loadNoticeList(params) {
      try {
        // 确保status参数是数字类型
        if (params.status && params.status === 'all') {
          delete params.status
        }
        const res = await getNoticeList(params)
        this.noticeList = res.data.records.map(notice => ({
          ...notice,
          statusName: this.getStatusName(notice.status)
        }))
        this.total = res.data.total
        return res
      } catch (error) {
        console.error('获取通知公告列表失败:', error)
        throw error
      }
    },

    /**
     * 获取通知公告详情
     * @param {number} id - 通知公告ID
     * @returns {Promise<Object>}
     */
    async loadNoticeDetail(id) {
      try {
        const res = await getNoticeDetail(id)
        this.currentNotice = res.data
        return res
      } catch (error) {
        console.error('获取通知公告详情失败:', error)
        throw error
      }
    },

    /**
     * 新增通知公告
     * @param {Object} data - 通知公告数据
     * @returns {Promise<Object>}
     */
    async createNotice(data) {
      try {
        const res = await addNotice(data)
        return res
      } catch (error) {
        console.error('新增通知公告失败:', error)
        throw error
      }
    },

    /**
     * 修改通知公告
     * @param {Object} data - 通知公告数据
     * @returns {Promise<Object>}
     */
    async modifyNotice(data) {
      try {
        const res = await updateNotice(data)
        return res
      } catch (error) {
        console.error('修改通知公告失败:', error)
        throw error
      }
    },

    /**
     * 删除通知公告
     * @param {number} id - 通知公告ID
     * @returns {Promise<Object>}
     */
    async removeNotice(id) {
      try {
        const res = await deleteNotice(id)
        return res
      } catch (error) {
        console.error('删除通知公告失败:', error)
        throw error
      }
    },

    /**
     * 发布通知公告
     * @param {number} id - 通知公告ID
     * @returns {Promise<Object>}
     */
    async releaseNotice(id) {
      try {
        const res = await publishNotice(id)
        return res
      } catch (error) {
        console.error('发布通知公告失败:', error)
        throw error
      }
    },

    /**
     * 撤回通知公告
     * @param {number} id - 通知公告ID
     * @returns {Promise<Object>}
     */
    async recallNotice(id) {
      try {
        const res = await withdrawNotice(id)
        return res
      } catch (error) {
        console.error('撤回通知公告失败:', error)
        throw error
      }
    },

    /**
     * 置顶/取消置顶通知公告
     * @param {number} id - 通知公告ID
     * @param {boolean} isTop - 是否置顶
     * @returns {Promise<Object>}
     */
    async setNoticeTop(id, isTop) {
      try {
        const res = await toggleNoticeTop(id, isTop)
        return res
      } catch (error) {
        console.error('置顶/取消置顶通知公告失败:', error)
        throw error
      }
    },
    
    /**
     * 获取通知公告详情(兼容旧代码)
     * @param {number} id - 通知公告ID
     * @returns {Promise<Object>}
     */
    async fetchNoticeInfo(id) {
      return this.loadNoticeDetail(id)
    },
    
    /**
     * 根据状态值获取状态名称
     * @param {number} status - 状态值 (0:草稿, 1:已发布, 2:已撤回)
     * @returns {string} - 状态名称
     */
    getStatusName(status) {
      switch (status) {
        case 0: return '草稿'
        case 1: return '已发布'
        case 2: return '已撤回'
        default: return '未知'
      }
    }
  }
})