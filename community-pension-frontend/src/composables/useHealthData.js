import { ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

export function useHealthData() {
  const healthData = ref({
    bloodPressure: '',
    heartRate: '',
    bloodSugar: '',
  })

  const loading = ref(false)

  async function fetchHealthData(roleId) {
    if (!roleId) return
    
    loading.value = true
    try {
      const res = await axios.get('/api/getHealth', { params: { roleId } })
      healthData.value = res.data.data
    } catch (error) {
      ElMessage.error('获取健康数据失败')
      console.error('获取健康数据失败:', error)
    } finally {
      loading.value = false
    }
  }

  async function updateHealthData(data) {
    loading.value = true
    try {
      const res = await axios.post('/api/updateHealth', data)
      if (res.data.code === 1) {
        ElMessage.success('健康数据更新成功')
        await fetchHealthData(data.elderInfoId)
        return true
      }
      throw new Error(res.data.msg || '更新失败')
    } catch (error) {
      ElMessage.error(error.message || '更新健康数据失败')
      return false
    } finally {
      loading.value = false
    }
  }

  return {
    healthData,
    loading,
    fetchHealthData,
    updateHealthData
  }
}