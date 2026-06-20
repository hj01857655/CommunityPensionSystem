import {
    deleteActivity as apiDeleteActivity,
    create,
    getDetail,
    getList,
    getStats,
    update,
    updateStatus,
    uploadImage
} from '@/api/back/activity'
import { registerActivity as apiRegisterActivity } from '@/api/fore/activity'
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

    /**
     * 更新活动
     * @param {number} id - 活动ID
     * @param {Object} data - 活动数据
     */
    async function updateActivity(id, data) {
        try {
            console.log('更新活动:', id, data)
            
            await update(id, data)
            ElMessage.success('更新活动成功')
            await fetchActivityList()
        } catch (error) {
            console.error('更新活动失败:', error)
            ElMessage.error('更新活动失败' + (error.response?.data?.message ? ': ' + error.response.data.message : ''))
            throw error
        }
    }

    async function deleteActivity(id) {
        try {
            await apiDeleteActivity(id)
            ElMessage.success('删除成功')
            await fetchActivityList()
            return Promise.resolve()
        } catch (error) {
            return Promise.reject(error)
        }
    }

    async function updateActivityStatus(id, status) {
        try {
            // 修复：直接传递status值，而不是包装成对象
            await updateStatus(id, status)
            ElMessage.success('更新活动状态成功')
            await fetchActivityList()
        } catch (error) {
            ElMessage.error('更新活动状态失败: ' + (error.message || '未知错误'))
            throw error
        }
    }

    /**
     * 上传活动图片
     * @param {File} file - 要上传的图片文件
     * @returns {Promise<string>} - 返回图片URL
     */
    const uploadActivityImage = async (file) => {
        try {
            // 创建FormData对象
            const formData = new FormData()
            formData.append('file', file)
            
            // 调用上传API
            const response = await uploadImage(formData)
            
            if (response.code === 200) {
                return Promise.resolve(response.data)
            } else {
                ElMessage.error(response.msg || '上传图片失败')
                return Promise.reject(new Error(response.msg || '上传图片失败'))
            }
        } catch (error) {
            ElMessage.error('上传图片失败: ' + (error.message || '未知错误'))
            return Promise.reject(error)
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

    async function registerActivity(activityId, elderData) {
        try {
            await apiRegisterActivity(activityId, elderData)
            ElMessage.success('活动报名成功')
            return Promise.resolve()
        } catch (error) {
            ElMessage.error('活动报名失败: ' + (error.message || '未知错误'))
            return Promise.reject(error)
        }
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
        resetQueryParams,
        registerActivity,
        uploadActivityImage
    }
})