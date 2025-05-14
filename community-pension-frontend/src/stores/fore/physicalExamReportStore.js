import {
    createReport,
    deleteReport,
    getReportDetail,
    getReportList,
    updateReport,
    uploadFile
} from '@/api/fore/physicalExamReport'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const usePhysicalExamReportStore = defineStore('physical-exam-report', () => {
  // 状态
  const reportList = ref([])
  const total = ref(0)
  const detail = ref({})
  const loading = ref(false)
  const error = ref(null)

  /**
   * 分页获取体检报告列表
   * @param {Object} params - 额外查询参数（如 pageNum, pageSize, 其它筛选条件）
   */
  async function fetchList(params = {}) {
    let elderId = params.elderId
    if (!elderId) {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      elderId = userInfo.userId
    }
    if (!elderId) {
      error.value = '未获取到老人ID'
      return
    }
    loading.value = true
    error.value = null
    try {
      const res = await getReportList({ ...params, elderId })
      
      // 适配不同的响应格式
      if (res.code === 200) {
        // 直接的响应格式 { code: 200, data: { records: [], total: 0 } }
        const data = res.data
        reportList.value = data.records || []
        total.value = data.total || 0
        return res
      } else if (res.data && res.data.code === 200) {
        // 嵌套的响应格式 { data: { code: 200, data: { records: [], total: 0 } } }
        const data = res.data.data
        reportList.value = data.records || []
        total.value = data.total || 0
        return res.data
      } else {
        error.value = res.message || res.data?.message || '获取数据失败'
        return res
      }
    } catch (e) {
      error.value = e.message
      console.error('获取体检报告异常:', e)
      return { code: 500, message: e.message }
    } finally {
      loading.value = false
    }
  }

  /**
   * 获取体检报告详情
   * @param {number|string} id - 体检报告ID
   */
  async function fetchDetail(id) {
    if (!id) {
      error.value = '体检报告ID不能为空';
      return { code: 400, message: error.value };
    }
    
    loading.value = true;
    error.value = null;
    
    try {
      const res = await getReportDetail(id);
      
      // 适配不同的响应格式
      if (res.code === 200) {
        // 直接的响应格式 { code: 200, data: {...} }
        if (res.data) {
          // 检查数据是否已经包含report和items结构
          if (res.data.report) {
            // 数据已经是正确的结构，直接使用
            detail.value = { ...res.data };
          } else if (typeof res.data === 'object') {
            // 数据是普通对象，需要包装成正确的结构
            detail.value = {
              report: { ...res.data },
              items: []
            };
          } else {
            // 如果不是对象，尝试解析
            try {
              const parsedData = typeof res.data === 'string' ? JSON.parse(res.data) : res.data;
              detail.value = {
                report: parsedData,
                items: []
              };
            } catch (parseErr) {
              console.error('解析详情数据失败:', parseErr);
              detail.value = { 
                report: { raw: res.data },
                items: []
              };
            }
          }
        } else {
          detail.value = {};
          error.value = '未找到体检报告详情';
        }
        return res;
      } else if (res.data && res.data.code === 200) {
        // 嵌套的响应格式 { data: { code: 200, data: {...} } }
        if (res.data.data) {
          detail.value = res.data.data;
        } else {
          detail.value = {};
          error.value = '未找到体检报告详情';
        }
        return res.data;
      } else {
        error.value = res.message || res.data?.message || '获取详情失败';
        detail.value = {};
        return res;
      }
    } catch (e) {
      error.value = e.message || '获取体检报告详情时发生异常';
      detail.value = {};
      console.error('获取体检报告详情异常:', e);
      return { code: 500, message: error.value };
    } finally {
      loading.value = false;
    }
  }

  /**
   * 上传体检报告文件
   * @param {FormData} formData - 文件表单数据
   * @returns {Promise<string>} 文件URL
   */
  async function upload(formData) {
    loading.value = true
    error.value = null
    try {
      const res = await uploadFile(formData)
      
      // 适配不同的响应格式
      if (res.code === 200 && res.data) {
        // 直接的响应格式 { code: 200, data: "fileUrl" }
        if (typeof res.data === 'string') {
          return res.data;
        } else if (typeof res.data === 'object') {
          return res.data.url || res.data.fileUrl || res.data.path || '';
        }
        return '';
      } else if (res.data && res.data.code === 200) {
        // 嵌套的响应格式 { data: { code: 200, data: ... } }
        const data = res.data.data;
        if (!data) return '';
        if (typeof data === 'string') {
          return data;
        } else if (typeof data === 'object') {
          return data.url || data.fileUrl || data.path || '';
        }
        return '';
      } else if (res.url || res.fileUrl || res.path) {
        // 直接返回url对象格式 { url: "..." }
        return res.url || res.fileUrl || res.path;
      } else {
        error.value = res.message || res.data?.message || '上传失败: 未获取到文件URL';
        console.warn('上传响应格式不符合预期:', res);
        return '';
      }
    } catch (e) {
      error.value = e.message || '上传文件发生异常';
      console.error('上传文件异常:', e);
      return '';
    } finally {
      loading.value = false;
    }
  }

  /**
   * 新增体检报告
   * @param {Object} data - 体检报告数据
   * @returns {Promise<boolean>}
   */
  async function add(data) {
    loading.value = true
    error.value = null
    try {
      const res = await createReport(data)
      
      // 适配不同的响应格式
      if (res.code === 200 || res.code === 201) {
        return true
      } else if (res.data && (res.data.code === 200 || res.data.code === 201)) {
        return true
      } else {
        error.value = res.message || res.data?.message || '新增失败'
        return false
      }
    } catch (e) {
      error.value = e.message
      console.error('新增体检报告异常:', e)
      return false
    } finally {
      loading.value = false
    }
  }

  /**
   * 编辑体检报告
   * @param {Object} data - 体检报告数据
   * @returns {Promise<boolean>}
   */
  async function update(data) {
    loading.value = true
    error.value = null
    try {
      const res = await updateReport(data)
      
      // 适配不同的响应格式
      if (res.code === 200) {
        return true
      } else if (res.data && res.data.code === 200) {
        return true
      } else {
        error.value = res.message || res.data?.message || '更新失败'
        return false
      }
    } catch (e) {
      error.value = e.message
      console.error('更新体检报告异常:', e)
      return false
    } finally {
      loading.value = false
    }
  }

  /**
   * 删除体检报告
   * @param {number|string} id - 体检报告ID
   * @returns {Promise<boolean>}
   */
  async function remove(id) {
    loading.value = true
    error.value = null
    try {
      const res = await deleteReport(id)
      
      // 适配不同的响应格式
      if (res.code === 200 || res.code === 204) {
        return true
      } else if (res.data && (res.data.code === 200 || res.data.code === 204)) {
        return true
      } else {
        error.value = res.message || res.data?.message || '删除失败'
        return false
      }
    } catch (e) {
      error.value = e.message
      console.error('删除体检报告异常:', e)
      return false
    } finally {
      loading.value = false
    }
  }

  // 状态和方法暴露
  return {
    reportList,
    total,
    detail,
    loading,
    error,
    fetchList,
    fetchDetail,
    upload,
    add,
    update,
    remove
  }
}) 