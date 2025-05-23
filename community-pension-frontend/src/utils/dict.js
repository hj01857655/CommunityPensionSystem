import { getDictDataByType } from '@/api/back/system/dict/data'
import { toRefs, ref, onMounted } from 'vue'
import { useDictTypeStore } from '@/stores/back/dictTypeStore'

let dictTypeStore = null
const initDictTypeStore = () => {
  if (!dictTypeStore) {
    dictTypeStore = useDictTypeStore()
  }
  return dictTypeStore
}
export function useDict(...args) {
    // 创建一个响应式对象，为每个字典类型创建一个响应式数组
    const dictRef = {};
    
    // 初始化所有字典类型为空数组
    args.forEach(dictType => {
      dictRef[dictType] = ref([]);
    });

    // 异步获取字典数据
    const fetchDictData = async () => {
      try {
        const promises = args.map(dictType => 
          getDictDataByType(dictType).then(resp => ({
            type: dictType,
            data: resp.data.map(p => ({ 
              label: p.dictLabel, 
              value: p.dictValue, 
              elTagType: p.listClass, 
              elTagClass: p.cssClass 
            }))
          }))
        );

        const results = await Promise.all(promises);
        results.forEach(({ type, data }) => {
          // 更新响应式对象中的数据
          dictRef[type].value = data;
          console.log(`字典数据加载成功: ${type}`, data);
        });
      } catch (error) {
        console.error('获取字典数据失败:', error);
      }
    };

    // 立即执行一次获取数据，确保数据可用
    fetchDictData();

    // 返回字典对象
    return dictRef;
}

