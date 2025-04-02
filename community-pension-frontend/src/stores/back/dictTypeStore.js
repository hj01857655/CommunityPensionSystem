import { defineStore } from 'pinia'
import { ref } from 'vue'
import { 
  getDictTypeList, // 获取字典类型列表
  getDictTypeDetail, // 获取字典类型详情
  createDictType, // 创建字典类型
  updateDictType, // 更新字典类型
  deleteDictType, // 删除字典类型
  refreshDictCache // 刷新字典缓存
} from '@/api/back/system/dict/type'

export const useDictTypeStore = defineStore('dictType', () => {
  // 状态
  const dict=ref(new Array())
  const dictTypeList = ref([])
  const dictTypeTotal = ref(0)
  const loading = ref(false)
  // 获取字典
  async function getDict (_key) {
    if(_key==null&&_key==""){
      return null;
    }
    try{
      for(let i=0;i<this.dict.length;i++){
        if(this.dict[i].key==_key){
          return this.dict[i].value;
        }
      }
    }catch(e){
      return null;
    }
  }
  // 设置字典
  async function setDict (_key,_value) {
    if(_key!==null&&_key!=""){
      this.dict.push({key:_key,value:_value});
    }
  }
  // 删除字典
  async function removeDict (_key) {
    let bln = false;
    try {
      for(let i = 0; i < this.dict.length; i++) {
        if(this.dict[i].key == _key) {
          this.dict.splice(i, 1);
          bln = true;
        }
      }
    } catch(e) {
      bln = false;
    }
    return bln;
  }

  // 获取字典类型列表
  async function fetchDictTypeList(params) {
    loading.value = true
    try {
      const res = await getDictTypeList(params)
      if (res.data) {
        dictTypeList.value = res.data.records || []
        dictTypeTotal.value = res.data.total || 0
      }
      return res
    } finally {
      loading.value = false
    }
  }

  // 获取字典类型详情
  async function fetchDictTypeDetail(dictId) {
    return await getDictTypeDetail(dictId)
  }

  // 创建字典类型
  async function createNewDictType(data) {
    return await createDictType(data)
  }

  // 更新字典类型
  async function updateDictTypeInfo(data) {
    return await updateDictType(data)
  }

  // 删除字典类型
  async function removeDictType(dictIds) {
    return await deleteDictType(dictIds)
  }

  // 刷新字典缓存
  async function refreshDictTypeCache() {
    return await refreshDictCache()
  }

  // 更新字典类型状态
  async function updateDictTypeStatus(dictId, status) {
    return await updateDictType({ dictId, status })
  }

  return {
    // 状态
    dict,
    dictTypeList,
    dictTypeTotal,
    loading,
    // 方法
    getDict,
    setDict,
    removeDict,
    fetchDictTypeList,
    fetchDictTypeDetail,
    createNewDictType,
    updateDictTypeInfo,
    removeDictType,
    refreshDictTypeCache,
    updateDictTypeStatus
  }
}) 