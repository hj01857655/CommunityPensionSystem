import axios from '@/utils/axios';

/**
 * 系统配置管理相关接口
 */

/**
 * 获取系统配置列表
 * @returns {Promise<{code: number, data: Array<{configId: number, configName: string, configKey: string, configValue: string, configType: string}>, msg: string}>}
 */
export const getConfigList = async () => {
  return await axios.get('/api/system/config/list');
};

/**
 * 根据配置键名获取系统配置
 * @param {string} configKey - 配置键名
 * @returns {Promise<{code: number, data: string, msg: string}>}
 */
export const getConfigValueByKey = async (configKey) => {
  return await axios.get(`/api/system/config/configKey/${configKey}`);
};

/**
 * 获取系统设置（包含所有系统相关配置）
 * @returns {Promise<{code: number, data: {systemName: string, logo: string, description: string, icp: string, contactPhone: string, contactEmail: string, version: string}, msg: string}>}
 */
export const getSystemSettings = async () => {
  return await axios.get('/api/system/config/settings');
};

/**
 * 获取安全设置（包含所有安全相关配置）
 * @returns {Promise<{code: number, data: {passwordMinLength: number, passwordComplexity: string, loginFailLockCount: number, accountLockTime: number, sessionTimeout: number, enableCaptcha: boolean, enableIpRestriction: boolean, allowedIps: string}, msg: string}>}
 */
export const getSecuritySettings = async () => {
  return await axios.get('/api/system/config/security');
};

/**
 * 更新系统设置
 * @param {Object} data - 系统设置信息
 * @param {string} data.systemName - 系统名称
 * @param {string} data.logo - 系统Logo
 * @param {string} data.description - 系统描述
 * @param {string} data.icp - 备案信息
 * @param {string} data.contactPhone - 联系电话
 * @param {string} data.contactEmail - 联系邮箱
 * @returns {Promise<{code: number, msg: string}>}
 */
export const updateSystemSettings = (data) => {
  return axios.put('/api/system/config/updateSystemSettings', data);
};

/**
 * 更新安全设置
 * @param {Object} data - 安全设置信息
 * @param {number} data.passwordMinLength - 密码最小长度
 * @param {string} data.passwordComplexity - 密码复杂度要求
 * @param {number} data.loginFailLockCount - 登录失败锁定次数
 * @param {number} data.accountLockTime - 账户锁定时间(分钟)
 * @param {number} data.sessionTimeout - 会话超时时间(分钟)
 * @param {boolean} data.enableCaptcha - 是否启用登录验证码
 * @param {boolean} data.enableIpRestriction - 是否启用IP限制
 * @param {string} data.allowedIps - 允许的IP地址列表
 * @returns {Promise<{code: number, msg: string}>}
 */
export const updateSecuritySettings = (data) => {
  return axios.put('/api/system/config/updateSecuritySettings', data);
};

/**
 * 数据备份相关接口
 */

/**
 * 获取备份文件列表
 * @returns {Promise<{code: number, data: Array<{id: number, fileName: string, size: string, createTime: string}>, msg: string}>}
 */
export const getBackupList = async () => {
  return await axios.get('/api/system/backup/list');
};

/**
 * 创建新的数据备份
 * @returns {Promise<{code: number, data: {id: number, fileName: string, size: string, createTime: string}, msg: string}>}
 */
export const createBackup = () => {
  return axios.post('/api/system/backup/create');
};

/**
 * 下载备份文件
 * @param {string} fileName - 备份文件名
 * @returns {Promise<Blob>}
 */
export const downloadBackup = (fileName) => {
  return axios.get('/api/system/backup/download', {
    params: { fileName },
    responseType: 'blob'
  });
};

/**
 * 删除备份文件
 * @param {string} fileName - 备份文件名
 * @returns {Promise<{code: number, msg: string}>}
 */
export const deleteBackup = (fileName) => {
  return axios.delete('/api/system/backup/delete', {
    params: { fileName }
  });
};

/**
 * 恢复备份
 * @param {Object} data - 恢复信息
 * @param {string} data.backupId - 备份ID
 * @param {string} data.password - 管理员密码
 * @returns {Promise<{code: number, msg: string}>}
 */
export const restoreBackup = (data) => {
  return axios.post('/api/system/backup/restore', data);
}; 

/**
 * 获取参数设置（包含所有系统参数相关配置）
 * @returns {Promise<{code: number, data: {maxUploadSize: number, allowedFileTypes: Array<string>, defaultPageSize: number, dataCacheTime: number, enableOperationLog: boolean, enableLoginLog: boolean}, msg: string}>}
 */
export const getParamsSettings = async () => {
  // 直接返回模拟数据，不发起实际请求
  console.log('使用参数设置模拟数据');
  return Promise.resolve({
    code: 200,
    data: {
      maxUploadSize: 10,
      allowedFileTypes: ['image', 'document', 'spreadsheet', 'archive', 'audio', 'video'],
      defaultPageSize: 10,
      dataCacheTime: 30,
      enableOperationLog: true,
      enableLoginLog: true
    },
    msg: 'success'
  });
};

/**
 * 更新参数设置
 * @param {Object} data - 参数设置信息
 * @param {number} data.maxUploadSize - 文件上传大小限制(MB)
 * @param {Array<string>} data.allowedFileTypes - 允许上传的文件类型
 * @param {number} data.defaultPageSize - 默认分页大小
 * @param {number} data.dataCacheTime - 数据缓存时间(分钟)
 * @param {boolean} data.enableOperationLog - 是否开启操作日志
 * @param {boolean} data.enableLoginLog - 是否开启登录日志
 * @returns {Promise<{code: number, msg: string}>}
 */
export const updateParamsSettings = (data) => {
  // 直接返回模拟成功响应，不发起实际请求
  console.log('使用更新参数设置模拟数据', data);
  return Promise.resolve({
    code: 200,
    msg: '保存成功（模拟数据）'
  });
};