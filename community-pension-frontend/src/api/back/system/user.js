import axios from '@/utils/axios';

/**
 * 用户信息管理相关接口
 */

/**
 * 分页获取用户列表
 * @param {Object} query - 查询参数
 * @param {number} [query.current=1] - 当前页码
 * @param {number} [query.size=10] - 每页显示条数
 * @param {string} [query.username] - 用户名（可选）
 * @param {boolean} [query.isActive] - 是否激活（可选）
 * @param {string} [query.startTime] - 开始时间（可选）
 * @param {string} [query.endTime] - 结束时间（可选）
 * @returns {Promise<{code: number, data: {records: Array<{userId: number, username: string, nickName: string, email: string, phone: string, status: number, createTime: string}>, total: number}, msg: string}>}
 */
export const getUserList = async (query) => {
  return axios.get('/api/system/user/list', {
    params: {
      current: query.current || 1,
      size: query.size || 10,
      username: query.username || '',
      isActive: query.isActive,
      startTime: query.startTime || '',
      endTime: query.endTime || ''
    }
  });
};

/**
 * 获取用户详细信息
 * @param {number} userId - 用户ID
 */
export const getUserInfo = async (userId) => {
  return axios.get(`/api/system/user/${userId}`);
};

/**
 * 新增用户
 * @param {Object} data - 用户信息
 */
export const addUser = data => {
  console.log('API层发送的新增用户数据:', JSON.stringify(data, null, 2));
  
  // 创建一个新对象，确保一些必要字段存在
  const processedData = { ...data };
  
  // 确保roleIds字段存在且格式正确（数组格式）
  if (processedData.roleId !== undefined && !processedData.roleIds) {
    processedData.roleIds = [processedData.roleId];
    // 移除单独的roleId，防止与roleIds冲突
    delete processedData.roleId;
  }
  
  // 确保有默认密码
  if (!processedData.password) {
    processedData.password = '123456';
  }
  
  // 确保isActive格式正确（后端可能需要数值而非布尔值）
  if (processedData.isActive !== undefined) {
    processedData.isActive = Number(processedData.isActive);
  }
  
  // 去除undefined和null值，避免传递可能导致后端解析错误的值
  Object.keys(processedData).forEach(key => {
    if (processedData[key] === undefined || processedData[key] === null) {
      delete processedData[key];
    }
  });
  
  console.log('处理后发送的新增用户数据:', JSON.stringify(processedData, null, 2));
  return axios.post('/api/system/user/add', processedData);
};

/**
 * 更新用户
 * @param {number} userId - 用户ID
 * @param {Object} data - 用户信息
 * @returns {Promise<{code: number, msg: string}>}
 */
export const updateUser = (userId, data) => {
  return axios.put(`/api/system/user/${userId}`, data);
};

/**
 * 重置用户密码
 * @param {number} userId - 用户ID
 */
export const resetUserPassword = userId => {
  return axios.put(`/api/system/user/${userId}/reset-password`);
};

/**
 * 修改用户密码
 * @param {number} userId - 用户ID
 * @param {Object} data - 密码信息
 * @param {string} data.oldPassword - 旧密码
 * @param {string} data.newPassword - 新密码
 */
export function updateUserPassword(userId, data) {
  return axios.put(`/api/system/user/${userId}/password`, data);
}

/**
 * 上传头像
 * @param {[*]} file
 * @returns
 */
export function uploadAvatar(file) {
  const formData = new FormData()
  formData.append('file', file)
  return axios.post('/api/system/user/avatar', formData, {
      headers: {
          'Content-Type': 'multipart/form-data'
      }
  })
}

/**
 * 删除用户
 * @param {number} userId - 用户ID
 */
export const deleteUser = userId => {
  return axios.delete(`/api/system/user/${userId}`);
};

/**
 * 批量删除用户
 * @param {number[]} userIds - 用户ID数组
 */
export const deleteUsers = userIds => {
  return axios.delete('/api/system/user/batch', {
    data: userIds
  });
};

/**
 * 用户角色相关接口
 */

/**
 * 获取用户角色
 * @param {number} userId - 用户ID
 * @returns {Promise<{code: number, data: Array<number>, msg: string}>}
 */
export const getUserRoles = userId => {
  return axios.get(`/api/system/user/${userId}/roles`);
};

/**
 * 分配用户角色
 * @param {number} userId - 用户ID
 * @param {number[]} roleIds - 角色ID数组
 * @returns {Promise<{code: number, msg: string}>}
 */
export function assignRole(userId, roleIds) {
    return axios.post(`/api/system/user/assignRoles/${userId}/`, roleIds);
}

/**
 * 更新用户状态
 * @param {number} userId - 用户ID
 * @param {number} status - 状态值
 */
export const updateUserStatus = (userId, status) => {
  return axios.put(`/api/system/user/${userId}/status`, { isActive: status });
};

/**
 * 获取未绑定家属的老人列表
 * @returns {Promise<{code: number, data: Array<{userId: number, name: string}>, msg: string}>}
 */
export const getUnboundElders = () => {
  return axios.get('/api/system/user/unbound/elders');
};

/**
 * 获取用户角色授权信息
 * @param {number} userId - 用户ID
 * @returns {Promise<{code: number, data: {user: Object, roles: Array}, msg: string}>}
 */
export const getAuthRole = userId => {
  return axios.get(`/api/system/user/${userId}/auth-role`);
};

/**
 * 更新用户角色授权信息
 * @param {Object} data - 授权信息
 * @param {number} data.userId - 用户ID
 * @param {string} data.roleIds - 角色ID字符串，多个ID用逗号分隔
 * @returns {Promise<{code: number, msg: string}>}
 */
export const updateAuthRole = data => {
  return axios.put(`/api/system/user/${data.userId}/auth-role`, {
    roleIds: data.roleIds
  });
};

/**
 * 获取未绑定老人的家属列表
 * @returns {Promise<{code: number, data: Array<{userId: number, name: string}>, msg: string}>}
 */
export const getUnboundKins = () => {
  return axios.get('/api/system/user/unbound/kins');
};

/**
 * 绑定老人和家属关系
 * @param {number} elderId - 老人ID
 * @param {number} kinId - 家属ID
 * @param {string} relationType - 关系类型
 * @returns {Promise<{code: number, msg: string}>}
 */
export const bindElderKinRelation = (elderId, kinId, relationType) => {
    return axios.post('/api/system/user/bind-relation', null, {
        params: {
            elderId,
            kinId,
            relationType
        }
  });
};

/**
 * 解绑老人和家属关系
 * @param {number} elderId - 老人ID
 * @param {number} kinId - 家属ID
 * @returns {Promise<{code: number, msg: string}>}
 */
export const unbindElderKinRelation = (elderId, kinId) => {
  return axios.post('/api/system/user/unbind-relation', {
    elderId,
    kinId
  });
};

/**
 * 获取老人的家属列表
 * @param {number} elderId - 老人ID
 * @returns {Promise<{code: number, data: Array<{userId: number, username: string, name: string, relationType: string}>, msg: string}>}
 */
export const getKinsByElderId = (elderId) => {
    return axios.get(`/api/system/user/kins/${elderId}`);
};

/**
 * 获取家属的老人列表
 * @param {number} kinId - 家属ID
 * @returns {Promise<{code: number, data: Array<{userId: number, username: string, name: string, relationType: string}>, msg: string}>}
 */
export const getEldersByKinId = (kinId) => {
    return axios.get(`/api/system/user/elders/${kinId}`);
};




