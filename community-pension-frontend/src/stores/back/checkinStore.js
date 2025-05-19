import { getList as getActivityList } from '@/api/back/activity'
import * as checkinApi from '@/api/back/activity/checkin'
import { getElders } from '@/api/back/health/records'
import { ElMessage } from 'element-plus'
import { defineStore } from 'pinia'
import { computed, ref } from 'vue'

// 活动签到管理的Store - 使用组合式API风格
export const useCheckinStore = defineStore('back-checkin', () => {
  // state
  const checkinList = ref([])
  const total = ref(0)
  const loading = ref(false)
  const activityOptions = ref([])
  const elderOptions = ref([])
  const queryParams = ref({
    pageNum: 1,
    pageSize: 10,
    activityId: '',
    startTime: '',
    endTime: '',
    signoutStatus: '',
    elderName: ''
  })

  // getters
  const activeCheckins = computed(() =>
    checkinList.value.filter(item => item.signInTime && !item.signOutTime)
  )

  const completedCheckins = computed(() =>
    checkinList.value.filter(item => item.signInTime && item.signOutTime)
  )

  const hasQueryFilters = computed(() =>
    !!(queryParams.value.activityId || queryParams.value.startTime || queryParams.value.endTime)
  )

  // actions
  /**
   * 重置列表数据
   */
  function resetListData() {
    checkinList.value = []
    total.value = 0
  }

  /**
   * 处理API错误
   * @param {string} message - 错误信息
   */
  function handleApiError(message) {
    ElMessage.error(message)
  }

  /**
   * 获取签到记录列表
   * @param {Object} params - 分页参数
   */
  async function getList(params) {
    loading.value = true

    // 处理分页参数
    if (params) {
      queryParams.value.pageNum = params.page || params.pageNum
      queryParams.value.pageSize = params.limit || params.pageSize
    }

    try {
      // 创建一个新的参数对象，只包含有效的参数
      const requestParams = {
        pageNum: queryParams.value.pageNum,
        pageSize: queryParams.value.pageSize
      }

      // 只有当activityId有值时才添加到请求参数中
      if (queryParams.value.activityId) {
        requestParams.activityId = queryParams.value.activityId
      }

      // 添加其他可能的参数
      if (queryParams.value.startTime) {
        requestParams.startTime = queryParams.value.startTime
      }

      if (queryParams.value.endTime) {
        requestParams.endTime = queryParams.value.endTime
      }

      // 添加签退状态参数
      if (queryParams.value.signoutStatus !== '') {
        requestParams.signoutStatus = queryParams.value.signoutStatus
      }

      // 添加参与人姓名参数
      if (queryParams.value.elderName) {
        requestParams.elderName = queryParams.value.elderName
      }

      const response = await checkinApi.getList(requestParams)
      if (response.code === 200) {
        checkinList.value = response.data.records
        total.value = response.data.total
      } else {
        handleApiError(response.msg || '获取签到记录失败')
        resetListData()
      }
    } catch (error) {
      handleApiError('获取签到记录失败：' + error.message)
      resetListData()
    } finally {
      loading.value = false
    }
  }

  /**
   * 获取活动选项
   */
  async function getActivityOptions() {
    try {
      // 修改为不传递数组参数，而是使用多个请求合并结果
      // 获取报名中的活动
      const response1 = await getActivityList({
        pageNum: 1,
        pageSize: 100,
        status: 1 // 报名中
      })

      // 获取进行中的活动
      const response2 = await getActivityList({
        pageNum: 1,
        pageSize: 100,
        status: 2 // 进行中
      })

      // 获取已结束的活动
      const response3 = await getActivityList({
        pageNum: 1,
        pageSize: 100,
        status: 3 // 已结束
      })

      // 合并结果
      const allActivities = []

      if (response1.code === 200 && response1.data && response1.data.records) {
        allActivities.push(...response1.data.records)
      }

      if (response2.code === 200 && response2.data && response2.data.records) {
        allActivities.push(...response2.data.records)
      }

      if (response3.code === 200 && response3.data && response3.data.records) {
        allActivities.push(...response3.data.records)
      }

      // 转换为选项格式
      activityOptions.value = allActivities.map(item => ({
        id: item.id,
        title: item.title
      }))
    } catch (error) {
      handleApiError('获取活动列表失败：' + error.message)
    }
  }

  /**
   * 获取老人选项
   */
  async function getElderOptions() {
    try {
      const response = await getElders()

      if (response.code === 200) {
        elderOptions.value = response.data.map(item => ({
          id: item.userId,
          name: item.username
        }))
      } else {
        handleApiError(response.msg || '获取老人列表失败')
      }
    } catch (error) {
      handleApiError('获取老人列表失败：' + error.message)
    }
  }

  /**
   * 获取用户信息
   * @returns {Object|null} 用户信息
   */
  function getUserInfo() {
    const userInfoStr = sessionStorage.getItem('userInfo')
    if (!userInfoStr) {
      handleApiError('未获取到用户信息，请重新登录')
      return null
    }

    try {
      const userInfo = JSON.parse(userInfoStr)
      if (!userInfo || !userInfo.userId) {
        handleApiError('用户信息不完整，请重新登录')
        return null
      }
      return userInfo
    } catch (error) {
      handleApiError('解析用户信息失败，请重新登录')
      return null
    }
  }

  /**
   * 签到
   * @param {Object} data - 签到数据
   * @returns {Promise} 签到结果
   */
  async function checkin(data) {
    try {
      // 获取当前登录用户ID
      const userInfo = getUserInfo()
      if (!userInfo) {
        return Promise.reject(new Error('获取用户信息失败'))
      }

      // 先获取报名ID
      const registerResponse = await checkinApi.getRegisterIdByActivityAndElder(
        data.activityId,
        data.elderId
      )

      if (registerResponse.code !== 200 || !registerResponse.data) {
        return Promise.reject(new Error('未找到该老人的报名记录，请先为老人报名活动'))
      }

      // 创建签到数据
      const checkInData = {
        registerId: registerResponse.data,
        checkInUserId: userInfo.userId,
        isProxyCheckIn: 1, // 管理员代签
        remarks: data.remarks
      }

      // 调用签到API
      const response = await checkinApi.create(checkInData)

      if (response.code === 200) {
        ElMessage.success('签到成功')
        await getList() // 刷新列表
        return Promise.resolve()
      } else {
        return Promise.reject(new Error(response.msg || '签到失败'))
      }
    } catch (error) {
      return Promise.reject(error)
    }
  }

  /**
   * 签退
   * @param {Object} row - 签到记录
   * @returns {Promise} 签退结果
   */
  async function signOut(row) {
    try {
      const response = await checkinApi.signOut(row.registerId)

      if (response.code === 200) {
        ElMessage.success('签退成功')
        await getList() // 刷新列表
        return Promise.resolve()
      } else {
        return Promise.reject(new Error(response.msg || '签退失败'))
      }
    } catch (error) {
      return Promise.reject(error)
    }
  }

  /**
   * 处理文件下载
   * @param {Blob} data - 文件数据
   * @param {string} filename - 文件名
   */
  function handleFileDownload(data, filename) {
    const blob = new Blob([data], { type: 'application/vnd.ms-excel' })
    const link = document.createElement('a')

    link.href = URL.createObjectURL(blob)
    link.download = filename
    link.click()
    URL.revokeObjectURL(link.href)
  }

  /**
   * 导出签到记录
   * @returns {Promise} 导出结果
   */
  async function exportList() {
    try {
      // 创建一个新的参数对象
      const params = {}

      // 只有当activityId有值时才添加到请求参数中
      if (queryParams.value.activityId) {
        params.activityId = queryParams.value.activityId
      }

      // 添加其他可能的参数
      if (queryParams.value.startTime) {
        params.startTime = queryParams.value.startTime
      }

      if (queryParams.value.endTime) {
        params.endTime = queryParams.value.endTime
      }

      // 添加签退状态参数
      if (queryParams.value.signoutStatus !== '') {
        params.signoutStatus = queryParams.value.signoutStatus
      }

      // 添加参与人姓名参数
      if (queryParams.value.elderName) {
        params.elderName = queryParams.value.elderName
      }

      const response = await checkinApi.exportList(params)

      handleFileDownload(response, `签到记录_${new Date().getTime()}.xlsx`)

      ElMessage.success('导出成功')
      return Promise.resolve()
    } catch (error) {
      handleApiError('导出失败：' + error.message)
      return Promise.reject(error)
    }
  }

  /**
   * 重置查询参数
   */
  function resetQueryParams() {
    queryParams.value = {
      pageNum: 1,
      pageSize: 10,
      activityId: '',
      startTime: '',
      endTime: '',
      signoutStatus: '',
      elderName: ''
    }
  }

  /**
   * 重置列表数据
   */
  function resetList() {
    checkinList.value = []
    total.value = 0
  }

  /**
   * 设置日期范围
   * @param {Date} date - 日期对象
   */
  function setDateRange(date) {
    if (date) {
      const dateStr = date.toISOString().split('T')[0]
      queryParams.value.startTime = `${dateStr} 00:00:00`
      queryParams.value.endTime = `${dateStr} 23:59:59`
    } else {
      queryParams.value.startTime = ''
      queryParams.value.endTime = ''
    }
  }

  return {
    // state
    checkinList,
    total,
    loading,
    activityOptions,
    elderOptions,
    queryParams,

    // getters
    activeCheckins,
    completedCheckins,
    hasQueryFilters,

    // actions
    getList,
    getActivityOptions,
    getElderOptions,
    checkin,
    signOut,
    exportList,
    resetQueryParams,
    resetList,
    setDateRange
  }
})