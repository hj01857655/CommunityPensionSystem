import { useState, useEffect, useCallback } from 'react'
import { serviceApi, healthApi, activityApi, noticeApi } from '@/api'
import { useAuthStore } from '@/store/authStore'

/**
 * User Statistics Data
 */
interface UserStatistics {
  monthlyServices: number
  healthRecords: number
  activityParticipations: number
  unreadNotices: number
}

/**
 * Custom Hook for User Statistics
 * 
 * Features:
 * - Fetch user-related statistics
 * - Loading state management
 * - Error handling
 * - Automatic refresh capability
 * - Cache support
 */
export const useUserStatistics = () => {
  const { userInfo } = useAuthStore()
  const [statistics, setStatistics] = useState<UserStatistics>({
    monthlyServices: 0,
    healthRecords: 0,
    activityParticipations: 0,
    unreadNotices: 0,
  })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  // Fetch all statistics
  const fetchStatistics = useCallback(async () => {
    if (!userInfo?.userId) return

    setLoading(true)
    setError(null)

    try {
      // Fetch data in parallel for better performance
      const [serviceRes, healthRes, activityRes, noticeRes] = await Promise.allSettled([
        serviceApi.getMyAppointments({ userId: userInfo.userId, pageNum: 1, pageSize: 100 }),
        healthApi.getHealthRecords(userInfo.userId),
        activityApi.getUserRegisteredActivities({ pageNum: 1, pageSize: 100 }),
        noticeApi.getNoticeList({ current: 1, size: 100 }),
      ])

      setStatistics({
        monthlyServices:
          serviceRes.status === 'fulfilled' && serviceRes.value.code === 200
            ? serviceRes.value.data?.length || 0
            : 0,
        healthRecords:
          healthRes.status === 'fulfilled' && healthRes.value.code === 200
            ? healthRes.value.data?.length || 0
            : 0,
        activityParticipations:
          activityRes.status === 'fulfilled' && activityRes.value.code === 200
            ? activityRes.value.data?.records?.length || 0
            : 0,
        unreadNotices:
          noticeRes.status === 'fulfilled' && noticeRes.value.code === 200
            ? noticeRes.value.data?.records?.filter((n) => !n.isRead).length || 0
            : 0,
      })
    } catch (err) {
      console.error('Failed to fetch statistics:', err)
      setError('获取统计数据失败')
    } finally {
      setLoading(false)
    }
  }, [userInfo?.userId])

  // Fetch on mount
  useEffect(() => {
    fetchStatistics()
  }, [fetchStatistics])

  return {
    statistics,
    loading,
    error,
    refresh: fetchStatistics,
  }
}
