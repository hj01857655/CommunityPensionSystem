import {
  deleteActivity as apiDeleteActivity,
  create,
  getDetail,
  getList,
  getStats,
  update,
  updateStatus
} from '@/api/back/activity'
import { ElMessage } from 'element-plus'
import { defineStore } from 'pinia'
import { reactive, ref } from 'vue'

export const useActivityStore = defineStore('activity', () => {
  // state
  const activityList = ref([])
  const currentActivity = ref(null)
  const total = ref(0)
  const loading = ref(false)
  const queryParams = reactive({
    pageNum: 1,
    pageSize: 10,
    title: '',
    type: '',
    status: '',
    startTime: '',
    endTime: ''
  })

  // actions
  async function fetchActivityList() {
    loading.value = true
    try {
      const res = await getList(queryParams)
      activityList.value = res.data.records
      total.value = res.data.total
    } catch (error) {
      ElMessage.error('获取活动列表失败')
      throw error
    } finally {
      loading.value = false
    }
  }

  async function fetchActivityDetail(id) {
    try {
      const res = await getDetail(id)
      currentActivity.value = res.data
      return res.data
    } catch (error) {
      ElMessage.error('获取活动详情失败')
      throw error
    }
  }

  async function createActivity(data) {
    try {
      await create(data)
      ElMessage.success('创建活动成功')
      await fetchActivityList()
    } catch (error) {
      ElMessage.error('创建活动失败')
      throw error
    }
  }

  async function updateActivity(id, data) {
    try {
      await update(id, data)
      ElMessage.success('更新活动成功')
      await fetchActivityList()
    } catch (error) {
      ElMessage.error('更新活动失败')
      throw error
    }
  }

  async function deleteActivity(id) {
    try {
      await apiDeleteActivity(id)
      ElMessage.success('删除活动成功')
      await fetchActivityList()
    } catch (error) {
      ElMessage.error('删除活动失败')
      throw error
    }
  }

  async function updateActivityStatus(id, status) {
    try {
      await updateStatus(id, { status })
      ElMessage.success('更新活动状态成功')
      await fetchActivityList()
    } catch (error) {
      ElMessage.error('更新活动状态失败')
      throw error
    }
  }

  async function fetchActivityStats() {
    try {
      const res = await getStats()
      return res.data
    } catch (error) {
      ElMessage.error('获取活动统计失败')
      throw error
    }
  }

  function resetQueryParams() {
    Object.assign(queryParams, {
      pageNum: 1,
      pageSize: 10,
      title: '',
      type: '',
      status: '',
      startTime: '',
      endTime: ''
    })
  }

  return {
    activityList,
    currentActivity,
    total,
    loading,
    queryParams,
    fetchActivityList,
    fetchActivityDetail,
    createActivity,
    updateActivity,
    deleteActivity,
    updateActivityStatus,
    fetchActivityStats,
    resetQueryParams
  }
}) 