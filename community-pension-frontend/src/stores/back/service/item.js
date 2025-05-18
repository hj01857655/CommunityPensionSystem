import {
  batchRemove,
  create,
  exportList,
  getDetail,
  getList,
  remove,
  update,
  updateStatus
} from '@/api/back/service/item';
import { ElMessage } from 'element-plus';
import { defineStore } from 'pinia';
import { ref } from 'vue';

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
      console.log('开始获取服务详情，ID:', id);
      const res = await getDetail(id);
      console.log('获取服务详情响应:', res);
      
      if (res.code === 200) {
        // 检查data是否为null
        if (res.data) {
          currentItem.value = res.data;
          return res.data;
        } else {
          console.error('服务详情数据为空');
          ElMessage.error('获取服务项目详情失败：数据不存在');
          return null;
        }
      } else {
        ElMessage.error(res.message || '获取服务项目详情失败');
        return null;
      }
    } catch (error) {
      console.error('获取服务项目详情失败:', error);
      if (error.response) {
        console.error('错误响应:', error.response.data);
      }
      ElMessage.error('获取服务项目详情失败');
      return null;
    }
  };

  // 添加服务项目
  const addServiceItem = async (data) => {
    try {
      console.log('Store 接收到的数据:', data);
      
      // 确保数据类型正确
      const requestData = {
        serviceName: String(data.serviceName || ''),
        description: String(data.description || ''),
        price: Number(data.price || 0),
        duration: Number(data.duration || 30),
        status: String(data.status || '0'),
        serviceType: String(data.serviceType || '')
      };
      
      console.log('处理后准备发送的数据:', requestData);

      // 使用JSON格式提交
      const res = await create(requestData);
      
      if (res.code === 200) {
        ElMessage.success('添加服务项目成功');
        return res.data;
      } else {
        ElMessage.error(res.message || '添加服务项目失败');
        return null;
      }
    } catch (error) {
      console.error('添加服务项目失败:', error);
      if (error.response && error.response.data) {
        console.error('错误详情:', error.response.data);
        ElMessage.error(error.response.data.message || '添加服务项目失败');
      } else {
        ElMessage.error('添加服务项目失败');
      }
      return null;
    }
  };

  // 更新服务项目
  const updateServiceItem = async (data) => {
    try {
      console.log('更新服务项目接收到的数据:', data);
      
      // 确保数据类型正确
      const requestData = {
        serviceId: data.serviceId,
        serviceName: String(data.serviceName || ''),
        description: String(data.description || ''),
        price: Number(data.price || 0),
        duration: Number(data.duration || 30),
        status: String(data.status || '0'),
        serviceType: String(data.serviceType || '')
      };
      
      console.log('处理后准备发送的更新数据:', requestData);
      
      const res = await update(requestData);
      console.log('更新API返回结果:', res);
      
      if (res.code === 200) {
        ElMessage.success('更新服务项目成功');
        return res.data;
      } else {
        ElMessage.error(res.message || '更新服务项目失败');
        return null;
      }
    } catch (error) {
      console.error('更新服务项目失败:', error);
      if (error.response) {
        console.error('错误状态码:', error.response.status);
        console.error('错误数据:', error.response.data);
      }
      ElMessage.error('更新服务项目失败');
      return null;
    }
  };

  // 删除服务项目
  const deleteServiceItem = async (id) => {
    try {
      console.log('Store中接收到的删除ID:', id, typeof id);
      
      if (!id) {
        console.error('删除服务项目失败: ID不能为空');
        ElMessage.error('删除失败：ID不能为空');
        return null;
      }
      
      // 确保ID是字符串或数字
      const serviceId = String(id);
      console.log('处理后的删除ID:', serviceId);
      
      const res = await remove(serviceId);
      console.log('删除API返回结果:', res);
      
      if (res.code === 200) {
        if (res.data === true) {
          ElMessage.success('删除服务项目成功');
          return res.data;
        } else {
          console.error('删除服务项目失败: 后端返回false');
          ElMessage.error('删除失败：后端操作未成功');
          return null;
        }
      } else {
        ElMessage.error(res.message || '删除服务项目失败');
        return null;
      }
    } catch (error) {
      console.error('删除服务项目失败:', error);
      if (error.response) {
        console.error('错误状态码:', error.response.status);
        console.error('错误数据:', error.response.data);
      }
      ElMessage.error('删除服务项目失败');
      return null;
    }
  };

  // 批量删除服务项目
  const batchDeleteServiceItem = async (ids) => {
    try {
      console.log('批量删除接收到的IDs:', ids);
      
      if (!ids || !ids.length) {
        console.error('批量删除服务项目失败: IDs不能为空');
        ElMessage.error('批量删除失败：请选择要删除的数据');
        return null;
      }
      
      // 将ID数组转换为逗号分隔的字符串
      const serviceIds = Array.isArray(ids) ? ids.join(',') : String(ids);
      console.log('处理后的批量删除IDs:', serviceIds);
      
      const res = await batchRemove(serviceIds);
      console.log('批量删除API返回结果:', res);
      
      if (res.code === 200) {
        if (res.data === true) {
          ElMessage.success('批量删除服务项目成功');
          return res.data;
        } else {
          console.error('批量删除服务项目失败: 后端返回false');
          ElMessage.error('批量删除失败：后端操作未成功');
          return null;
        }
      } else {
        ElMessage.error(res.message || '批量删除服务项目失败');
        return null;
      }
    } catch (error) {
      console.error('批量删除服务项目失败:', error);
      if (error.response) {
        console.error('错误状态码:', error.response.status);
        console.error('错误数据:', error.response.data);
      }
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