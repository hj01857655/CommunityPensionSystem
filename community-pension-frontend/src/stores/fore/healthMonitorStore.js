import {
    addHealthMonitorRecord,
    deleteHealthMonitor,
    getAbnormalHealthMonitors,
    getHealthMonitorDetail,
    getHealthMonitors,
    getHealthMonitorStats,
    updateHealthMonitorRecord
} from '@/api/fore/monitor'
import { ElMessage } from 'element-plus'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useHealthMonitorStore = defineStore('healthMonitor', () => {
  // 健康监测历史列表
  const monitorList = ref([])
  const total = ref(0)
  const loading = ref(false)
  const monitorDetail = ref(null)
  const stats = ref(null)

  // 获取健康监测记录列表（分页）
  const fetchMonitorList = async (params) => {
    loading.value = true
    try {
      const res = await getHealthMonitors(params)
      if (res && res.data) {
        monitorList.value = res.data.records || []
        total.value = res.data.total || 0
      }
    } catch (e) {
      ElMessage.error('获取健康监测记录失败')
      monitorList.value = []
      total.value = 0
    } finally {
      loading.value = false
    }
  }

  // 获取单条健康监测详情
  const fetchMonitorDetail = async (id) => {
    loading.value = true
    try {
      const res = await getHealthMonitorDetail(id)
      if (res && res.data) {
        monitorDetail.value = res.data
      }
    } catch (e) {
      ElMessage.error('获取健康监测详情失败')
      monitorDetail.value = null
    } finally {
      loading.value = false
    }
  }

  // 新增健康监测记录
  const addMonitor = async (data) => {
    loading.value = true
    try {
      const res = await addHealthMonitorRecord(data)
      if (res && res.code === 200) {
        ElMessage.success('添加成功')
        return true
      }
      ElMessage.error(res?.message || '添加失败')
      return false
    } catch (e) {
      ElMessage.error('添加健康监测记录失败')
      return false
    } finally {
      loading.value = false
    }
  }

  // 更新健康监测记录
  const updateMonitor = async (data) => {
    loading.value = true
    try {
      const res = await updateHealthMonitorRecord(data)
      if (res && res.code === 200) {
        ElMessage.success('更新成功')
        return true
      }
      ElMessage.error(res?.message || '更新失败')
      return false
    } catch (e) {
      ElMessage.error('更新健康监测记录失败')
      return false
    } finally {
      loading.value = false
    }
  }

  // 删除健康监测记录
  const removeMonitor = async (id) => {
    loading.value = true
    try {
      const res = await deleteHealthMonitor(id)
      if (res && res.code === 200) {
        ElMessage.success('删除成功')
        return true
      }
      ElMessage.error(res?.message || '删除失败')
      return false
    } catch (e) {
      ElMessage.error('删除健康监测记录失败')
      return false
    } finally {
      loading.value = false
    }
  }

  // 获取异常健康监测记录
  const fetchAbnormalMonitors = async () => {
    loading.value = true
    try {
      const res = await getAbnormalHealthMonitors()
      if (res && res.data) {
        monitorList.value = res.data || []
      }
    } catch (e) {
      ElMessage.error('获取异常健康监测记录失败')
      monitorList.value = []
    } finally {
      loading.value = false
    }
  }

  // 获取健康监测统计
  const fetchMonitorStats = async () => {
    loading.value = true
    try {
      const res = await getHealthMonitorStats()
      if (res && res.data) {
        stats.value = res.data
      }
    } catch (e) {
      ElMessage.error('获取健康监测统计失败')
      stats.value = null
    } finally {
      loading.value = false
    }
  }

  return {
    monitorList,
    total,
    loading,
    monitorDetail,
    stats,
    fetchMonitorList,
    fetchMonitorDetail,
    addMonitor,
    updateMonitor,
    removeMonitor,
    fetchAbnormalMonitors,
    fetchMonitorStats
  }
}) 