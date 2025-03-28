import { defineStore } from 'pinia'
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getNoticeList, getNoticeDetail } from '@/api/fore/notice'

export const useNoticeStore = defineStore('foreNotice', () => {
  // 状态
  const noticeList = ref([])
  const noticeDetail = ref(null)
  const total = ref(0)
  const loading = ref(false)

  /**
   * 获取通知公告列表
   * @param {Object} params - 查询参数
   * @returns {Promise}
   */
  const fetchNoticeList = async (params = { current: 1, size: 10 }) => {
    try {
      loading.value = true
      const res = await getNoticeList(params)
      
      if (res.code === 200) {
        noticeList.value = res.data.records || []
        total.value = res.data.total || 0
        
        // 处理状态显示名称
        noticeList.value.forEach(notice => {
          notice.statusName = getStatusName(notice.status)
        })
        
        return res.data
      }
      
      ElMessage.error(res.message || '获取通知公告列表失败')
      return null
    } catch (error) {
      console.error('获取通知公告列表失败:', error)
      ElMessage.error(error.message || '获取通知公告列表失败')
      return null
    } finally {
      loading.value = false
    }
  }

  /**
   * 获取通知公告详情
   * @param {number} id - 通知公告ID
   * @returns {Promise}
   */
  const fetchNoticeDetail = async (id) => {
    try {
      loading.value = true
      const res = await getNoticeDetail(id)
      
      if (res.code === 200) {
        noticeDetail.value = res.data
        
        // 处理状态显示名称
        if (noticeDetail.value) {
          noticeDetail.value.statusName = getStatusName(noticeDetail.value.status)
        }
        
        return res.data
      }
      
      ElMessage.error(res.message || '获取通知公告详情失败')
      return null
    } catch (error) {
      console.error('获取通知公告详情失败:', error)
      ElMessage.error(error.message || '获取通知公告详情失败')
      return null
    } finally {
      loading.value = false
    }
  }

  /**
   * 根据状态值获取状态名称
   * @param {number} status - 状态值 (0:草稿, 1:已发布, 2:已撤回)
   * @returns {string} - 状态名称
   */
  const getStatusName = (status) => {
    switch (status) {
      case 0: return '草稿'
      case 1: return '已发布'
      case 2: return '已撤回'
      default: return '未知'
    }
  }

  return {
    noticeList,
    noticeDetail,
    total,
    loading,
    fetchNoticeList,
    fetchNoticeDetail
  }
})