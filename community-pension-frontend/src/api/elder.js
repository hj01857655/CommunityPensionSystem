import axios from '@/utils/axios'
import { ElMessage } from 'element-plus';
// 获取老人列表
export const getElders = async (params) => {
  try {
    const token = localStorage.getItem('token');
    const response = await axios.get('/api/elders', {
      params: {
        page: params.currentPage,
        size: params.pageSize
      },
      headers: {
        Authorization: `${token}`,
      },
    });
    return { success: true, data: response.data };
  } catch (error) {
    console.error('Get all users error:', error);
    ElMessage.error('获取老人失败，请稍后重试');
    return { success: false, error: error.message };
  }
};

export const getElderById = async (elder) => {
  try {
    const token = localStorage.getItem('token');
    //@GetMapping("/{id}")
    const response = await axios.get(`/api/elders/${elder.id}`, {
      headers: {
        Authorization: `${token}`,
      },
    });
    return { success: true, data: response.data };
  } catch (error) {
    console.error('Get elder by id error:', error);
    ElMessage.error('获取老人失败，请稍后重试');
    return { success: false, error: error.message };
  }
};

// 添加老人
export const addElder = async (elder) => {
  try {
    const token = localStorage.getItem('token');
    //@PostMapping
    const response = await axios.post('/api/elders', elder, {
      headers: {
        Authorization: `${token}`,
      },
    });
    return { success: true, data: response.data };
  } catch (error) {
    console.error('Add elder error:', error);
    ElMessage.error('添加老人失败，请稍后重试');
    return { success: false, error: error.message };
  }
};

// 更新老人
export const updateElder = async (elder) => {
  try {
    const token = localStorage.getItem('token');
    //@PutMapping("/{id}")
    const response = await axios.put(`/api/elders/${elder.id}`, {
      headers: {
        Authorization: `${token}`,
      },
    });
    return { success: true, data: response.data };
  } catch (error) {
    console.error('Update elder error:', error);
    ElMessage.error('更新老人失败，请稍后重试');
    return { success: false, error: error.message };
  }
};

// 删除老人
export const deleteElder = async (id) => {
  try {
    const token = localStorage.getItem('token');
    //@DeleteMapping("/{id}")
    const response = await axios.delete(`/api/elders/${id}`, {
      headers: {
        Authorization: `${token}`,
      },
    });
    return { success: true, data: response.data };
  } catch (error) {
    console.error('Delete elder error:', error);
    ElMessage.error('删除老人失败，请稍后重试');
    return { success: false, error: error.message };
  }
};








