import axios from '@/utils/axios'
import { TokenManager } from '@/utils/axios'

// 获取工作人员列表
export const getStaffList = async (params) => {
  try {
    const token = TokenManager.admin.get();
    if (!token) {
      return { success: false, message: '未登录或登录已过期' };
    }
    const response = await axios.get('/api/staffs/list', {
      params: {
        page: params?.page ?? 1,
        size: params?.size ?? 10
        
      },
      headers: {
        Authorization: `${token}`
      }
    })
    if(response.code === 200){
      return response.data
    }else{
      return response.message
    }
  } catch (error) {
    console.error('获取工作人员列表失败:', error);
    return { success: false, message: '获取工作人员列表失败，请稍后重试' };
  }
}
// 获取工作人员详情
export const getStaffDetail = async (id) => {
  try {
    const token = TokenManager.admin.get();
    if (!token) {
      return { success: false, message: '未登录或登录已过期' };
    }
    const response = await axios.get(`/api/staffs/${id}`,{
      headers: {
        Authorization: `${token}`
      }
    })
    if(response.code === 200){
      return response.data
    }else{
      return response.message
    }
  } catch (error) {
    console.error('获取工作人员详情失败:', error);
    return { success: false, message: '获取工作人员详情失败，请稍后重试' };
  }
}
// 添加工作人员
export const addStaff = async (data) => {
  try {
    const token = TokenManager.admin.get();
    if (!token) {
      return { success: false, message: '未登录或登录已过期' };
    }
    const response = await axios.post('/api/staffs/', data,{
      headers: {
        Authorization: `${token}`
      }
    })
    if(response.code === 200){
      return response.data
    }else{
      return response.message
    }
  } catch (error) {
    console.error('添加工作人员失败:', error);
    return { success: false, message: '添加工作人员失败，请稍后重试' };
  }
}

// 更新工作人员信息
export const updateStaff = async (data) => {
  try {
    const token = TokenManager.admin.get();
    if (!token) {
      return { success: false, message: '未登录或登录已过期' };
    }
    const response = await axios.put('/api/staffs/', data,{
      headers: {
        Authorization: `${token}`
      }
    })
    if(response.code === 200){
      return response.data
    }else{
      return response.message
    }
  } catch (error) {
    console.error('更新工作人员失败:', error);
    return { success: false, message: '更新工作人员失败，请稍后重试' };
  }
}

// 删除工作人员
export const deleteStaff = async (id) => {
  try {
    const token = TokenManager.admin.get();
    if (!token) {
      return { success: false, message: '未登录或登录已过期' };
    }
    const response = await axios.delete(`/api/staffs/${id}`,{
      headers: {
        Authorization: `${token}`
      }
    })
    if(response.code === 200){
      return response.data
    }else{
      return response.message
    }
  } catch (error) {
    console.error('删除工作人员失败:', error);
    return { success: false, message: '删除工作人员失败，请稍后重试' };
  }
}

// // 获取工作人员服务记录
// export const getStaffServiceRecords = async (id) => {
//   const response = await axios.get(`/api/staffs/service-records/${id}`,{
//     headers: {
//       Authorization: `${token}`
//     }
//   })
//   return response.data
// }