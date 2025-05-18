import { useServiceItemStore } from './item'
import { useServiceOrderStore } from './order'
import { useServiceReviewStore } from './review'

export {
  useServiceItemStore,
  useServiceOrderStore,
  useServiceReviewStore
}

// 为了兼容直接导入的情况，默认导出服务项目store
export { useServiceItemStore as useServiceStore } from './item'