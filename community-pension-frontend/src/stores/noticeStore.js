import { defineStore } from 'pinia';
import { ref } from 'vue';
import { getNoticeList as getNotices, getNoticeDetail as readNotice } from '@/api/notice';

export const useNoticeStore = defineStore('notice', () => {
    // 通知列表
    const notices = ref([]);
    // 未读通知数量
    const unreadCount = ref(0);
    // 加载状态
    const loading = ref(false);
    
    // 获取通知列表
    const fetchNotices = async () => {
        loading.value = true;
        try {
            const response = await getNotices();
            if (response.success) {
                notices.value = response.data.data.notices || [];
                updateUnreadCount();
                return { success: true };
            }
            return { success: false, message: response.error };
        } catch (error) {
            return { success: false, message: error.message || '获取通知失败' };
        } finally {
            loading.value = false;
        }
    };
    
    // 更新未读通知数量
    const updateUnreadCount = () => {
        unreadCount.value = notices.value.filter(notice => !notice.isRead).length;
    };
    
    // 标记通知为已读
    const markAsRead = async (noticeId) => {
        try {
            const response = await readNotice(noticeId);
            if (response.success) {
                const index = notices.value.findIndex(notice => notice.id === noticeId);
                if (index !== -1) {
                    notices.value[index].isRead = true;
                    updateUnreadCount();
                }
                return { success: true };
            }
            return { success: false, message: response.error };
        } catch (error) {
            return { success: false, message: error.message || '标记通知已读失败' };
        }
    };
    
    // 标记所有通知为已读
    const markAllAsRead = async () => {
        try {
            // 假设后端有批量标记已读的接口
            const noticeIds = notices.value.filter(notice => !notice.isRead).map(notice => notice.id);
            if (noticeIds.length === 0) return { success: true };
            
            // 这里应该调用批量标记已读的API
            // const response = await readAllNotices(noticeIds);
            
            // 前端先更新状态
            notices.value.forEach(notice => {
                notice.isRead = true;
            });
            updateUnreadCount();
            return { success: true };
        } catch (error) {
            return { success: false, message: error.message || '标记所有通知已读失败' };
        }
    };
    
    // 清空通知
    const clearNotices = () => {
        notices.value = [];
        unreadCount.value = 0;
    };
    
    return {
        notices,
        unreadCount,
        loading,
        fetchNotices,
        markAsRead,
        markAllAsRead,
        clearNotices
    };
});