import { defineStore } from 'pinia'
import {
  getList,
  getDetail,
  create,
  update,
  deleteActivity,
  updateStatus,
  getStats
} from '@/api/back/activity'
import { ElMessage } from 'element-plus'

export const useActivityStore = defineStore('activity', {
  state: () => ({
    activityList: [],
    currentActivity: null,
    total: 0,
    loading: false,
    queryParams: {
      pageNum: 1,
      pageSize: 10,
      title: '',
      type: '',
      status: '',
      startTime: '',
      endTime: ''
    }
  }),

  actions: {
    // 获取活动列表
    async fetchActivityList() {
      this.loading = true
      try {
        const res = await getList(this.queryParams)
        this.activityList = res.data.records
        this.total = res.data.total
      } catch (error) {
        ElMessage.error('获取活动列表失败')
        throw error
      } finally {
        this.loading = false
      }
    },

    // 获取活动详情
    async fetchActivityDetail(id) {
      try {
        const res = await getDetail(id)
        this.currentActivity = res.data
        return res.data
      } catch (error) {
        ElMessage.error('获取活动详情失败')
        throw error
      }
    },

    // 创建活动
    async createActivity(data) {
      try {
        await create(data)
        ElMessage.success('创建活动成功')
        this.fetchActivityList()
      } catch (error) {
        ElMessage.error('创建活动失败')
        throw error
      }
    },

    // 更新活动
    async updateActivity(id, data) {
      try {
        await update(id, data)
        ElMessage.success('更新活动成功')
        this.fetchActivityList()
      } catch (error) {
        ElMessage.error('更新活动失败')
        throw error
      }
    },

    // 删除活动
    async deleteActivity(id) {
      try {
        await deleteActivity(id)
        ElMessage.success('删除活动成功')
        this.fetchActivityList()
      } catch (error) {
        ElMessage.error('删除活动失败')
        throw error
      }
    },

    // 更新活动状态
    async updateActivityStatus(id, status) {
      try {
        await updateStatus(id, status)
        ElMessage.success('更新活动状态成功')
        this.fetchActivityList()
      } catch (error) {
        ElMessage.error('更新活动状态失败')
        throw error
      }
    },

    // 获取活动统计
    async fetchActivityStats() {
      try {
        const res = await getStats()
        return res.data
      } catch (error) {
        ElMessage.error('获取活动统计失败')
        throw error
      }
    },

    // 重置查询参数
    resetQueryParams() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        title: '',
        type: '',
        status: '',
        startTime: '',
        endTime: ''
      }
    }
  }
}) 