import {
  addNotice,
  deleteNotice,
  getNoticeDetail,
  getNoticeList,
  publishNotice,
  toggleNoticeTop,
  updateNotice,
  withdrawNotice
} from '@/api/back/notice/notice'
import {defineStore} from 'pinia'
import {ref} from 'vue'

export const useNoticeStore = defineStore('notice', () => {
    // state
    const noticeList = ref([])
    const total = ref(0)
    const currentNotice = ref(null)

    // actions
    async function loadNoticeList(params) {
        try {
            if (params.status && params.status === 'all') {
                delete params.status
            }
            const res = await getNoticeList(params)
            noticeList.value = res.data.records.map(notice => ({
                ...notice,
                statusName: getStatusName(notice.status)
            }))
            total.value = res.data.total
            return res
        } catch (error) {
            console.error('获取通知公告列表失败:', error)
            throw error
        }
    }

    async function loadNoticeDetail(id) {
        try {
            const res = await getNoticeDetail(id)
            currentNotice.value = res.data
            return res
        } catch (error) {
            console.error('获取通知公告详情失败:', error)
            throw error
        }
    }

    async function createNotice(data) {
        try {
            return await addNotice(data)
        } catch (error) {
            console.error('新增通知公告失败:', error)
            throw error
        }
    }

    async function modifyNotice(data) {
        try {
            return await updateNotice(data)
        } catch (error) {
            console.error('修改通知公告失败:', error)
            throw error
        }
    }

    async function removeNotice(id) {
        try {
            return await deleteNotice(id)
        } catch (error) {
            console.error('删除通知公告失败:', error)
            throw error
        }
    }

    async function releaseNotice(id) {
        try {
            return await publishNotice(id)
        } catch (error) {
            console.error('发布通知公告失败:', error)
            throw error
        }
    }

    async function recallNotice(id) {
        try {
            return await withdrawNotice(id)
        } catch (error) {
            console.error('撤回通知公告失败:', error)
            throw error
        }
    }

    async function setNoticeTop(id, isTop) {
        try {
            return await toggleNoticeTop(id, isTop)
        } catch (error) {
            console.error('置顶/取消置顶通知公告失败:', error)
            throw error
        }
    }

    function getStatusName(status) {
        const statusStr = String(status);
        switch (statusStr) {
            case '0':
                return '草稿';
            case '1':
                return '已发布';
            case '2':
                return '已关闭';
            case '3':
                return '已过期';
            case '4':
                return '已归档';
            default:
                return '未知';
        }
    }

    return {
        noticeList,
        total,
        currentNotice,
        loadNoticeList,
        loadNoticeDetail,
        createNotice,
        modifyNotice,
        removeNotice,
        releaseNotice,
        recallNotice,
        setNoticeTop,
        getStatusName
    }
})