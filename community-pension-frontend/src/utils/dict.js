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
    const res = ref({});
    
    // 初始化所有字典类型为空数组
    args.forEach(dictType => {
      res.value[dictType] = [];
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
          res.value[type] = data;
        });
      } catch (error) {
        console.error('获取字典数据失败:', error);
      }
    };

    // 立即执行获取数据
    fetchDictData();

    return toRefs(res.value);
}

