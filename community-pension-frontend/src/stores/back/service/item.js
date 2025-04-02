import { defineStore } from 'pinia';
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { 
  getList,
  getDetail,
  create,
  update,
  remove,
  updateStatus,
  exportList
} from '@/api/back/service/item';

export const useServiceItemStore = defineStore('serviceItem', () => {
  // 状态定义
  const serviceItemList = ref([]);
  const total = ref(0);
  const loading = ref(false);
  const currentItem = ref(null);

  // 查询参数
  const queryParams = ref({
    page: 1,
    limit: 10,
    serviceName: '',
    serviceType: ''
  });

  // 重置状态
  const resetState = () => {
    serviceItemList.value = [];
    total.value = 0;
    loading.value = false;
    currentItem.value = null;
    queryParams.value = {
      page: 1,
      limit: 10,
      serviceName: '',
      serviceType: ''
    };
  };

  // 获取服务项目列表
  const getServiceItemList = async (params = {}) => {
    loading.value = true;
    try {
      const res = await getList({
        ...queryParams.value,
        ...params
      });
      
      if (res.code === 200) {
        serviceItemList.value = res.data.records;
        total.value = res.data.total;
        return res.data;
      } else {
        ElMessage.error(res.message || '获取服务项目列表失败');
        return null;
      }
    } catch (error) {
      console.error('获取服务项目列表失败:', error);
      ElMessage.error('获取服务项目列表失败');
      return null;
    } finally {
      loading.value = false;
    }
  };

  // 获取服务项目详情
  const getServiceItemDetail = async (id) => {
    try {
      const res = await getDetail(id);
      if (res.code === 200) {
        currentItem.value = res.data;
        return res.data;
      } else {
        ElMessage.error(res.message || '获取服务项目详情失败');
        return null;
      }
    } catch (error) {
      console.error('获取服务项目详情失败:', error);
      ElMessage.error('获取服务项目详情失败');
      return null;
    }
  };

  // 添加服务项目
  const addServiceItem = async (data) => {
    try {
      const res = await create({
        serviceName: data.name,
        description: data.description,
        price: data.price,
        duration: data.duration,
        status: data.is_active
      });
      
      if (res.code === 200) {
        ElMessage.success('添加服务项目成功');
        return res.data;
      } else {
        ElMessage.error(res.message || '添加服务项目失败');
        return null;
      }
    } catch (error) {
      console.error('添加服务项目失败:', error);
      ElMessage.error('添加服务项目失败');
      return null;
    }
  };

  // 更新服务项目
  const updateServiceItem = async (data) => {
    try {
      const res = await update({
        serviceId: data.id,
        serviceName: data.name,
        description: data.description,
        price: data.price,
        duration: data.duration,
        status: data.is_active
      });
      
      if (res.code === 200) {
        ElMessage.success('更新服务项目成功');
        return res.data;
      } else {
        ElMessage.error(res.message || '更新服务项目失败');
        return null;
      }
    } catch (error) {
      console.error('更新服务项目失败:', error);
      ElMessage.error('更新服务项目失败');
      return null;
    }
  };

  // 删除服务项目
  const deleteServiceItem = async (id) => {
    try {
      const res = await remove(id);
      if (res.code === 200) {
        ElMessage.success('删除服务项目成功');
        return res.data;
      } else {
        ElMessage.error(res.message || '删除服务项目失败');
        return null;
      }
    } catch (error) {
      console.error('删除服务项目失败:', error);
      ElMessage.error('删除服务项目失败');
      return null;
    }
  };

  // 批量删除服务项目
  const batchDeleteServiceItem = async (ids) => {
    try {
      const res = await remove(ids.join(','));
      if (res.code === 200) {
        ElMessage.success('批量删除服务项目成功');
        return res.data;
      } else {
        ElMessage.error(res.message || '批量删除服务项目失败');
        return null;
      }
    } catch (error) {
      console.error('批量删除服务项目失败:', error);
      ElMessage.error('批量删除服务项目失败');
      return null;
    }
  };

  // 更新服务项目状态
  const updateServiceItemStatus = async (serviceId, status) => {
    try {
      const res = await updateStatus({ id: serviceId, status });
      if (res.code === 200) {
        ElMessage.success('更新服务项目状态成功');
        return res.data;
      } else {
        ElMessage.error(res.message || '更新服务项目状态失败');
        return null;
      }
    } catch (error) {
      console.error('更新服务项目状态失败:', error);
      ElMessage.error('更新服务项目状态失败');
      return null;
    }
  };

  // 导出服务项目列表
  const exportServiceItemList = async (params = {}) => {
    try {
      const res = await exportList({
        ...queryParams.value,
        ...params
      });
      if (res.code === 200) {
        return res.data;
      } else {
        ElMessage.error(res.message || '导出服务项目列表失败');
        return null;
      }
    } catch (error) {
      console.error('导出服务项目列表失败:', error);
      ElMessage.error('导出服务项目列表失败');
      return null;
    }
  };

  return {
    // 状态
    serviceItemList,
    total,
    loading,
    currentItem,
    queryParams,
    
    // 方法
    resetState,
    getServiceItemList,
    getServiceItemDetail,
    addServiceItem,
    updateServiceItem,
    deleteServiceItem,
    batchDeleteServiceItem,
    updateServiceItemStatus,
    exportServiceItemList
  };
}); 