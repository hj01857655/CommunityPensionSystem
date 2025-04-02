import { defineStore } from 'pinia'
import { ref } from 'vue'
import { 
  getDictDataList, 
  getDictDataDetail, 
  createDictData, 
  updateDictData, 
  deleteDictData,
  getDictDataByType,
  exportDictData
} from '@/api/back/system/dict/data'

export const useDictDataStore = defineStore('dictData', () => {
  // 状态
  const dictDataList = ref([])
  const dictDataTotal = ref(0)
  const dictDataCache = ref({})
  const loading = ref(false)

  // 获取字典数据列表
  async function fetchDictDataList(params) {
    loading.value = true
    try {
      const res = await getDictDataList(params)
      console.log('API返回数据:', res)  // 添加调试日志
      if (res.code === 200) {
        dictDataList.value = res.data.records || []
        dictDataTotal.value = res.data.total || 0
      }
    } catch (error) {
      console.error('获取字典数据列表失败:', error)
    } finally {
      loading.value = false
    }
  }

  // 获取字典数据详情
  async function fetchDictDataDetail(dictCode) {
    return await getDictDataDetail(dictCode)
  }

  // 创建字典数据
  async function createNewDictData(data) {
    return await createDictData(data)
  }

  // 更新字典数据
  async function updateDictDataInfo(data) {
    return await updateDictData(data)
  }

  // 删除字典数据
  async function removeDictData(dictCodes) {
    return await deleteDictData(dictCodes)
  }

  // 根据字典类型获取字典数据
  async function fetchDictDataByType(dictType) {
    if (dictDataCache.value[dictType]) {
      return dictDataCache.value[dictType]
    }
    const res = await getDictDataByType(dictType)
    if (res.data) {
      dictDataCache.value[dictType] = res.data
    }
    return res
  }

  // 更新字典数据状态
  async function updateDictDataStatus(dictCode, status) {
    // 从缓存中获取字典数据
    const dictData = dictDataList.value.find(item => item.dictCode === dictCode)
    if (!dictData) {
      throw new Error('字典数据不存在')
    }
    // 构建完整的更新数据
    const updateData = {
      dictCode: dictData.dictCode,
      dictType: dictData.dictType,
      dictLabel: dictData.dictLabel,
      dictValue: dictData.dictValue,
      dictSort: dictData.dictSort,
      status: status,
      remark: dictData.remark
    }
    return await updateDictData(updateData)
  }

  // 导出字典数据
  async function exportDictDataList(params) {
    return await exportDictData(params)
  }

  // 清除字典数据缓存
  function clearDictDataCache() {
    dictDataCache.value = {}
  }

  return {
    // 状态
    dictDataList,
    dictDataTotal,
    dictDataCache,
    loading,
    // 方法
    fetchDictDataList,
    fetchDictDataDetail,
    createNewDictData,
    updateDictDataInfo,
    removeDictData,
    fetchDictDataByType,
    updateDictDataStatus,
    exportDictDataList,
    clearDictDataCache
  }
}) 