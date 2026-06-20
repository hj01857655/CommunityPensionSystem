import { useState, useEffect, useCallback } from 'react'
import { notification as antdNotification } from 'antd'
import { noticeApi } from '@/api'
import type { Notice } from '@/types/notice'

/**
 * Custom Hook for Notifications Management
 * 
 * Features:
 * - Auto-fetch notifications on mount
 * - Real-time notification popup
 * - Mark as read functionality
 * - Unread count tracking
 */
export const useNotifications = () => {
  const [notifications, setNotifications] = useState<Notice[]>([])
  const [loading, setLoading] = useState(false)
  const [unreadCount, setUnreadCount] = useState(0)

  // Fetch notifications from API
  const fetchNotifications = useCallback(async () => {
    setLoading(true)
    try {
      const response = await noticeApi.getNoticeList({ current: 1, size: 10 })
      if (response.code === 200) {
        const data = response.data.records || []
        setNotifications(data)
        setUnreadCount(data.filter((n) => !n.isRead).length)

        // Show popup for first unread notification
        const firstUnread = data.find((n) => !n.isRead)
        if (firstUnread) {
          antdNotification.info({
            message: '新通知',
            description: firstUnread.title,
            placement: 'bottomRight',
            onClick: () => {
              // Will be handled by parent component
            },
          })
        }
      }
    } catch (error) {
      console.error('Failed to fetch notifications:', error)
    } finally {
      setLoading(false)
    }
  }, [])

  // Mark notification as read
  const markAsRead = useCallback(async (noticeId: number) => {
    try {
      await noticeApi.markNoticeAsRead(noticeId)
      setNotifications((prev) =>
        prev.map((n) => (n.noticeId === noticeId ? { ...n, isRead: true } : n))
      )
      setUnreadCount((prev) => Math.max(0, prev - 1))
    } catch (error) {
      console.error('Failed to mark as read:', error)
    }
  }, [])

  // Mark all as read
  const markAllAsRead = useCallback(() => {
    setNotifications((prev) => prev.map((n) => ({ ...n, isRead: true })))
    setUnreadCount(0)
  }, [])

  // Fetch on mount
  useEffect(() => {
    fetchNotifications()
  }, [fetchNotifications])

  return {
    notifications,
    loading,
    unreadCount,
    fetchNotifications,
    markAsRead,
    markAllAsRead,
  }
}
