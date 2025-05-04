import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getHealthData } from '@/api/fore/health'
import { useUserStore } from '@/stores/fore/userStore'
import { ElMessage } from 'element-plus'

export const useHealthStore = defineStore('health', () => {
  // 状态
  const healthData = ref(null)
  const loading = ref(false)
  const lastFetchTime = ref(0)
  const CACHE_TIME = 5 * 60 * 1000 // 缓存5分钟

  // 获取用户store
  const userStore = useUserStore()

  /**
   * 获取健康数据
   * @param {boolean} forceRefresh 是否强制刷新
   * @returns {Promise<Object>} 健康数据
   */
  const fetchHealthData = async (forceRefresh = false) => {
    // 如果有缓存且未过期，直接返回
    if (!forceRefresh && healthData.value && Date.now() - lastFetchTime.value < CACHE_TIME) {
      console.log('[健康Store] 使用缓存的健康数据');
      return healthData.value;
    }

    // 如果正在加载，返回当前数据
    if (loading.value) {
      console.log('[健康Store] 正在加载中，返回当前数据');
      return healthData.value;
    }

    try {
      loading.value = true;
      console.log('[健康Store] 开始获取健康数据');

      // 获取用户ID
      const userId = userStore.userInfo?.userId || userStore.userInfo?.id;
      if (!userId) {
        throw new Error('未获取到用户ID');
      }

      const response = await getHealthData(userId);
      if (response?.code === 200 && response.data) {
        healthData.value = response.data;
        lastFetchTime.value = Date.now();
        console.log('[健康Store] 健康数据获取成功');
        return healthData.value;
      } else {
        console.warn('[健康Store] 获取健康数据失败:', response);
        return null;
      }
    } catch (error) {
      console.error('[健康Store] 获取健康数据出错:', error);
      ElMessage.error('获取健康数据失败，请稍后重试');
      return null;
    } finally {
      loading.value = false;
    }
  }

  return {
    healthData,
    loading,
    fetchHealthData
  }
}) 