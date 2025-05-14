<template>
  <div class="physical-exam-report">
    <!-- 查询与操作栏 -->
    <el-row :gutter="16" class="toolbar">
      <el-col :span="12" class="toolbar-right">
        <el-button type="primary" @click="openAddDialog">新增体检报告</el-button>
        <el-tooltip content="下载标准体检报告模板，填写后上传" placement="top">
          <el-button type="info" link @click="downloadTemplate">
            <el-icon><Download /></el-icon>
            体检报告模板
          </el-button>
        </el-tooltip>
      </el-col>
    </el-row>

    <!-- 体检报告表格 -->
    <el-table
      :data="reportList"
      style="width: 100%; margin-top: 16px"
      :row-class-name="rowClassName"
      border
      v-loading="loading"
    >
      <el-table-column prop="date" label="体检日期" min-width="100" />
      <el-table-column prop="hospital" label="体检医院" min-width="160" />
      <el-table-column prop="mainResult" label="主要结果" min-width="200" show-overflow-tooltip />
      <el-table-column prop="fileUrl" label="报告文件" min-width="100">
        <template #default="{ row }">
          <el-button v-if="hasValidFileUrl(row.fileUrl)" size="small" type="primary" @click="previewFile(row.fileUrl)">预览</el-button>
          <el-button v-else size="small" @click="handleUploadForExisting(row)" type="success">上传附件</el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="viewDetail(row.id)">详情</el-button>
          <el-button size="small" type="primary" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 空数据提示 -->
    <el-empty v-if="reportList.length === 0 && !loading" description="暂无体检报告"></el-empty>

    <!-- 分页 -->
    <Pagination
      v-if="total > pageSize"
      :total="total"
      :page="pageNum"
      :limit="pageSize"
      @pagination="handlePagination"
      @update:limit="handlePageSizeChange"
      style="margin-top: 16px"
    />

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px">
      <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
        <el-form-item label="体检日期" prop="date">
          <el-date-picker v-model="form.date" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="体检医院" prop="hospital">
          <el-input v-model="form.hospital" placeholder="请输入体检医院" />
        </el-form-item>
        <el-form-item label="主要结果" prop="mainResult">
          <el-input type="textarea" v-model="form.mainResult" :rows="3" placeholder="请输入体检主要结果" />
        </el-form-item>
        <el-form-item label="报告文件" prop="fileUrl">
          <el-upload
            ref="uploadRef"
            :action="null"
            :auto-upload="false"
            :show-file-list="true"
            :limit="1"
            :on-change="onFileChange"
            :on-remove="onFileRemove"
            accept=".pdf,.jpg,.jpeg,.png"
          >
            <el-button type="primary">选择文件</el-button>
            <el-button v-if="uploadFileList.length > 0" type="success" @click="handleManualUpload" :loading="uploading">上传文件</el-button>
            <template #tip>
              <div class="el-upload__tip">
                推荐上传<b>PDF格式</b>的体检报告，支持PDF(首选)、JPG、PNG格式，文件大小不超过10MB
              </div>
            </template>
          </el-upload>
          <div v-if="form.fileUrl" style="margin-top: 8px;">
            <el-button v-if="hasValidFileUrl(form.fileUrl)" size="small" type="primary" @click="previewFile(form.fileUrl)">预览已上传文件</el-button>
            <span class="file-name">{{ getFileName(form.fileUrl) }}</span>
          </div>
        </el-form-item>
        
        <!-- 体检项目编辑 -->
        <el-divider content-position="left">体检项目</el-divider>
        <div class="exam-items-edit">
          <div class="items-toolbar">
            <el-button type="primary" size="small" @click="addExamItem">
              <el-icon><Plus /></el-icon> 添加项目
            </el-button>
            <el-dropdown @command="handleAddPreset" trigger="click">
              <el-button type="success" size="small">
                <el-icon><Plus /></el-icon> 添加常用项目
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="bloodPressure">血压</el-dropdown-item>
                  <el-dropdown-item command="bloodSugar">血糖</el-dropdown-item>
                  <el-dropdown-item command="bloodLipids">血脂四项</el-dropdown-item>
                  <el-dropdown-item command="liverFunction">肝功能</el-dropdown-item>
                  <el-dropdown-item command="kidneyFunction">肾功能</el-dropdown-item>
                  <el-dropdown-item command="bloodRoutine">血常规</el-dropdown-item>
                  <el-dropdown-item command="urineRoutine">尿常规</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-button v-if="form.examItems && form.examItems.length > 0" type="danger" size="small" @click="clearExamItems">
              <el-icon><Delete /></el-icon> 清空项目
            </el-button>
          </div>
          
          <el-empty v-if="!form.examItems || form.examItems.length === 0" description="暂无体检项目" :image-size="100">
            <el-button type="primary" @click="addExamItem">添加项目</el-button>
          </el-empty>
          
          <el-table v-else :data="form.examItems" border stripe style="width: 100%; margin-top: 10px;">
            <el-table-column label="项目名称" min-width="120">
              <template #default="{ row, $index }">
                <el-input v-model="row.itemName" placeholder="请输入项目名称" />
              </template>
            </el-table-column>
            <el-table-column label="检测值" min-width="120">
              <template #default="{ row, $index }">
                <div class="value-unit-input">
                  <el-input v-model="row.itemValue" placeholder="检测值" class="value-input" />
                  <el-input v-model="row.itemUnit" placeholder="单位" class="unit-input" />
                </div>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="120">
              <template #default="{ row, $index }">
                <el-select v-model="row.abnormalFlag" placeholder="请选择">
                  <el-option :value="0" label="正常" />
                  <el-option :value="1" label="异常" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80">
              <template #default="{ row, $index }">
                <el-button type="danger" size="small" circle @click="removeExamItem($index)">
                  <el-icon><Close /></el-icon>
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="体检报告详情" width="600px">
      <div v-if="detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="体检日期">{{ getDetailData('date') || '暂无数据' }}</el-descriptions-item>
          <el-descriptions-item label="体检医院">{{ getDetailData('hospital') || '暂无数据' }}</el-descriptions-item>
          <el-descriptions-item label="主要结果">{{ getDetailData('mainResult') || '暂无数据' }}</el-descriptions-item>
          <el-descriptions-item label="报告文件">
            <el-button v-if="hasValidFileUrl(getDetailData('fileUrl'))" size="small" type="primary" @click="previewFile(getDetailData('fileUrl'))">预览</el-button>
            <el-button v-else size="small" @click="handleUploadForDetail" type="success">上传附件</el-button>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDateTime(getDetailData('createdAt')) }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ formatDateTime(getDetailData('updatedAt')) }}</el-descriptions-item>
        </el-descriptions>
        
        <!-- 体检项目列表 -->
        <div class="exam-items-section">
          <h3 class="section-title">体检项目</h3>
          <div v-if="getExamItems().length === 0" class="no-items">
            暂无体检项目数据
          </div>
          <el-table v-else :data="getExamItems()" border stripe style="width: 100%; margin-top: 10px;">
            <el-table-column prop="itemName" label="项目名称" min-width="120" />
            <el-table-column label="检测值" min-width="120">
              <template #default="{ row }">
                {{ row.itemValue }}{{ row.itemUnit ? ' ' + row.itemUnit : '' }}
              </template>
            </el-table-column>
            <el-table-column label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.abnormalFlag === 1 ? 'danger' : 'success'">
                  {{ row.abnormalFlag === 1 ? '异常' : '正常' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      <div v-else class="empty-detail">
        <el-empty description="暂无详情数据"></el-empty>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 文件预览弹窗 -->
    <el-dialog v-model="previewVisible" title="体检报告预览" fullscreen append-to-body :close-on-click-modal="false" :destroy-on-close="true">
      <div class="file-preview-container">
        <div v-if="loading" class="preview-loading">
          <el-skeleton animated :rows="10" />
        </div>
        <div v-else-if="previewError" class="preview-error">
          <el-empty :description="previewError">
            <template #description>
              <p>{{ previewError }}</p>
              <p class="error-tips">可能原因：文件格式不支持在线预览或文件不存在</p>
            </template>
            <el-button type="primary" @click="downloadCurrentFile">下载查看</el-button>
          </el-empty>
        </div>
        <div v-else-if="isPdfPreview" class="pdf-preview">
          <!-- 使用对象标签代替iframe预览PDF，这样不会被router拦截 -->
          <object
            :data="previewUrl"
            type="application/pdf"
            width="100%"
            height="100%"
            @load="handleIframeLoad"
            @error="handleIframeError"
          >
            <div class="pdf-fallback">
              <p>您的浏览器无法直接预览PDF，<a :href="previewUrl" target="_blank">点击此处在新标签页打开</a>，或下载后查看</p>
              <el-button type="primary" @click="downloadCurrentFile">下载文件</el-button>
            </div>
          </object>
        </div>
        <div v-else-if="isImagePreview" class="image-preview">
          <img :src="previewUrl" alt="体检报告图片预览" class="preview-image" />
        </div>
        <div v-else class="unsupported-preview">
          <el-empty description="无法预览该类型文件">
            <el-button type="primary" @click="downloadCurrentFile">下载查看</el-button>
          </el-empty>
        </div>
      </div>
      <template #footer>
        <div class="preview-footer">
          <el-button @click="previewVisible = false">关闭</el-button>
          <el-button type="primary" @click="downloadCurrentFile">下载</el-button>
          <span v-if="currentFileType" class="file-type-info">文件类型: {{ currentFileType }}</span>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { usePhysicalExamReportStore } from '@/stores/fore/physicalExamReportStore'
import { ArrowDown, Close, Delete, Download, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { storeToRefs } from 'pinia'
import { defineExpose, onMounted, reactive, ref } from 'vue'

defineOptions({ name: 'PhysicalExamReport' })

// Store
const store = usePhysicalExamReportStore()
const { reportList, total, detail, loading, error } = storeToRefs(store)

// API相关
const uploadUrl = ref('/api/physicalExamReport/upload')
const uploadHeaders = ref({
  // 不在这里设置Content-Type，由浏览器自动设置multipart/form-data和boundary
})
const uploadData = ref({
  // 可以添加额外的表单数据
  userId: ''
})

// 查询与分页
const pageNum = ref(1)
const pageSize = ref(10)

// 弹窗与表单
const dialogVisible = ref(false)
const dialogTitle = ref('新增体检报告')
const form = reactive({ 
  id: null, 
  elderId: '', 
  date: '', 
  hospital: '',
  mainResult: '',
  fileUrl: '',
  examItems: [] 
})
const rules = { 
  date: [{ required: true, message: '请选择体检日期', trigger: 'change' }],
  hospital: [{ required: true, message: '请输入体检医院', trigger: 'blur' }]
}
const formRef = ref(null)
const detailVisible = ref(false)

// 文件预览功能
const previewVisible = ref(false)
const previewUrl = ref('')
const currentFileUrl = ref('')
const previewError = ref('')
const isPdfPreview = ref(false)
const isImagePreview = ref(false)
const currentFileType = ref('')

// 文件上传相关
const uploadRef = ref(null)
const uploadFileList = ref([])
const uploading = ref(false)

// 更新文件上传方法
function onFileChange(file, fileList) {
  console.log('文件选择:', file)
  uploadFileList.value = fileList
}

function onFileRemove(file, fileList) {
  uploadFileList.value = fileList
}

// 手动处理文件上传
async function handleManualUpload() {
  if (uploadFileList.value.length === 0) {
    ElMessage.warning('请先选择要上传的文件')
    return
  }
  
  const file = uploadFileList.value[0].raw
  if (!file) {
    ElMessage.warning('无法获取文件数据')
    return
  }
  
  // 准备FormData
  const formData = new FormData()
  formData.append('file', file)
  
  // 添加用户ID等信息
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  formData.append('userId', userInfo.userId || '')
  
  uploading.value = true
  try {
    // 导入上传文件的API
    const { uploadFile } = await import('@/api/fore/physicalExamReport')
    
    console.log('开始上传文件:', file.name)
    const response = await uploadFile(formData)
    console.log('文件上传响应:', response)
    
    if (response.code === 200 && response.data) {
      form.fileUrl = response.data
      ElMessage.success('文件上传成功')
      // 清除文件列表
      uploadFileList.value = []
      // 重置上传组件
      uploadRef.value.clearFiles()
    } else {
      throw new Error(response.message || '上传失败')
    }
  } catch (error) {
    console.error('文件上传错误:', error)
    ElMessage.error('文件上传失败: ' + (error.message || '未知错误'))
  } finally {
    uploading.value = false
  }
}

// 检查文件URL是否有效
function hasValidFileUrl(url) {
  // 检查 url 是否为 null 或 undefined
  if (url === null || url === undefined) return false;
  // 检查是否为默认的"暂无数据"字符串
  if (url === '暂无数据') return false;
  // 检查是否为空字符串或只包含空白字符
  if (typeof url !== 'string' || url.trim().length === 0) return false;
  // 检查是否为字符串形式的"null"或"undefined"
  if (url === 'null' || url === 'undefined') return false;
  
  // 通过所有检查，URL 有效
  return true;
}

// 格式化文件URL
function getFileUrl(url) {
  // 如果URL无效，直接返回空字符串
  if (!hasValidFileUrl(url)) return '';
  
  // 如果是完整URL，直接返回
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url;
  }
  
  // 如果是相对路径，构建为绝对路径（使用完整的基础URL）
  // 这样可以避免被Vue Router拦截
  return `${window.location.origin}${url}`;
}

function handlePagination({ page, limit }) {
  pageNum.value = page
  pageSize.value = limit
  onSearch()
}
function handlePageSizeChange(val) {
  pageSize.value = val
  pageNum.value = 1
  onSearch()
}

// 格式化日期时间
function formatDateTime(dateTimeStr) {
  if (!dateTimeStr) return '暂无记录';
  return dateTimeStr.replace('T', ' ').slice(0, 19);
}

// 新增
function openAddDialog() {
  dialogTitle.value = '新增体检报告'
  // 从localStorage中获取当前用户ID
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
  Object.assign(form, { 
    id: null, 
    elderId: userInfo.userId || '', 
    date: '', 
    hospital: '',
    mainResult: '',
    fileUrl: '',
    examItems: [] 
  })
  dialogVisible.value = true
}

// 编辑
async function openEditDialog(row) {
  dialogTitle.value = '编辑体检报告'
  
  // 先清空表单
  Object.assign(form, { 
    id: null, 
    elderId: '', 
    date: '', 
    hospital: '',
    mainResult: '',
    fileUrl: '',
    examItems: [] 
  });
  
  // 如果有ID，需要先获取详情数据
  if (row.id) {
    try {
      // 显示加载中状态
      loading.value = true;
      
      // 获取详情数据
      await store.fetchDetail(row.id);
      
      if (detail.value && detail.value.report) {
        // 设置基本信息
        Object.assign(form, {
          id: detail.value.report.id,
          elderId: detail.value.report.elderId,
          date: detail.value.report.date,
          hospital: detail.value.report.hospital,
          mainResult: detail.value.report.mainResult,
          fileUrl: detail.value.report.fileUrl
        });
        
        // 设置体检项目
        form.examItems = detail.value.items ? [...detail.value.items] : [];
      } else {
        // 如果没有获取到详情，使用传入的行数据
        Object.assign(form, row);
        form.examItems = [];
      }
      
      loading.value = false;
    } catch (err) {
      loading.value = false;
      console.error('获取体检报告详情失败:', err);
      ElMessage.error('获取体检报告详情失败，请稍后再试');
      
      // 使用传入的行数据
      Object.assign(form, row);
      form.examItems = [];
    }
  } else {
    // 直接使用传入的行数据
    Object.assign(form, row);
    form.examItems = row.examItems || [];
  }
  
  dialogVisible.value = true
}

// 提交
async function handleSubmit() {
  await formRef.value.validate()
  
  if (!form.elderId) {
    // 确保设置了当前用户ID
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
    form.elderId = userInfo.userId;
    if (!form.elderId) {
      ElMessage.warning('无法获取用户ID，请先登录');
      return;
    }
  }
  
  // 验证体检项目数据
  if (form.examItems && form.examItems.length > 0) {
    // 过滤掉不完整的项目
    form.examItems = form.examItems.filter(item => 
      item.itemName && item.itemName.trim() !== ''
    );
    
    // 检查项目数据有效性
    for (const item of form.examItems) {
      // 设置默认值
      if (item.abnormalFlag === undefined || item.abnormalFlag === null) {
        item.abnormalFlag = 0;
      }
      // 转换为数字类型
      item.abnormalFlag = Number(item.abnormalFlag);
    }
  }
  
  // 构建提交数据
  const submitData = {
    ...form,
    items: form.examItems || []
  };
  
  let ok = false
  if (form.id) {
    ok = await store.update(submitData)
    if (ok) ElMessage.success('编辑成功')
  } else {
    ok = await store.add(submitData)
    if (ok) ElMessage.success('新增成功')
  }
  if (ok) {
    dialogVisible.value = false
    onSearch()
  } else {
    ElMessage.error(store.error.value)
  }
  uploadFileList.value = []
}

// 删除
function handleDelete(id) {
  ElMessageBox.confirm('确定删除该体检报告吗？', '提示', { type: 'warning' })
    .then(async () => {
      const ok = await store.remove(id)
      if (ok) {
        ElMessage.success('删除成功')
        onSearch()
      } else {
        ElMessage.error(store.error.value)
      }
    })
}

// 安全地获取详情数据的辅助函数
const getDetailData = (field, defaultValue = '暂无数据') => {
  if (!detail.value || !detail.value.report) return defaultValue;
  return detail.value.report[field] || defaultValue;
};

// 获取体检项目列表
const getExamItems = () => {
  if (!detail.value || !detail.value.items) return [];
  return detail.value.items || [];
};

// 详情
async function viewDetail(id) {
  if (!id) {
    ElMessage.warning('体检报告ID不能为空');
    return;
  }
  
  try {
    // 调用store的fetchDetail方法获取详情
    await store.fetchDetail(id);
    
    // 直接使用详情数据
    if (error.value) {
      ElMessage.error(error.value);
    } else {
      // 显示详情对话框
      detailVisible.value = true;
    }
  } catch (err) {
    console.error('获取详情失败:', err);
    ElMessage.error('获取详情失败: ' + (err?.message || '未知错误'));
  }
}

// 行高亮（可根据异常高亮）
function rowClassName({ row }) {
  // 可根据包含特定关键词的体检结果进行高亮
  if (row.mainResult && (
    row.mainResult.includes('异常') || 
    row.mainResult.includes('偏高') || 
    row.mainResult.includes('偏低') ||
    row.mainResult.includes('建议复查')
  )) {
    return 'warning-row';
  }
  return '';
}

onMounted(() => {
  // 组件加载时自动查询当前用户的体检报告
  onSearch();
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  uploadData.value.userId = userInfo.userId || ''
  
  // 检查环境是否支持PDF预览
  try {
    // 获取当前域名，检查是否在开发环境
    const isDev = window.location.hostname === 'localhost' || 
                 window.location.hostname === '127.0.0.1';
    
    if (isDev) {
      console.log('当前处于开发环境，请确保Vite配置了/upload的代理')
    }
  } catch (error) {
    console.error('环境检查失败:', error)
  }
})

// 添加 onSearch 方法用于获取当前用户的体检报告数据
async function onSearch() {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
  const userId = userInfo.userId;
  
  if (!userId) {
    ElMessage.warning('未找到用户信息，请先登录');
    return;
  }
  
  try {
    loading.value = true;
    
    // 准备查询参数
    const queryParams = {
      elderId: userId,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    };
    
    // 使用store的fetchList方法
    const result = await store.fetchList(queryParams);
    
    loading.value = false;
    
    if (result && result.code === 200 && result.data) {
      // 更新列表数据
      reportList.value = result.data.records || [];
      total.value = result.data.total || 0;
    } else {
      ElMessage.error((result && result.message) || '获取体检报告失败');
    }
  } catch (err) {
    loading.value = false;
    console.error('获取体检报告失败:', err);
    ElMessage.error('获取体检报告失败: ' + (err?.message || '未知错误'));
  }
}

// 为现有记录上传附件
function handleUploadForExisting(row) {
  // 打开编辑对话框，聚焦在文件上传区域
  openEditDialog(row)
  // 可以添加一个setTimeout让焦点移到上传按钮
  setTimeout(() => {
    // 如果需要，这里可以添加代码聚焦到文件上传区域
    ElMessage.info('请选择要上传的体检报告文件')
  }, 300)
}

// 为详情视图上传附件
function handleUploadForDetail() {
  // 获取完整的详情数据对象
  const reportData = detail.value?.report || {};
  
  // 创建一个包含必要字段的对象
  const editData = {
    id: reportData.id,
    elderId: reportData.elderId,
    date: reportData.date || '',
    hospital: reportData.hospital || '',
    mainResult: reportData.mainResult || '',
    fileUrl: reportData.fileUrl || '',
    examItems: reportData.items || []
  };
  
  // 使用当前查看的详情数据打开编辑对话框
  openEditDialog(editData);
  
  setTimeout(() => {
    ElMessage.info('请选择要上传的体检报告文件');
  }, 300);
}

// 下载体检报告模板
async function downloadTemplate() {
  try {
    // 先从接口获取模板路径
    const axiosInstance = await import('@/utils/axios').then(mod => mod.default);
    const response = await axiosInstance.get('/api/physicalExamReport/template');
    
    if (response.code === 200 && response.data) {
      // 获取模板路径
      const templatePath = response.data;
      
      // 创建一个临时链接元素并触发下载
      const link = document.createElement('a');
      
      // 因为这是静态资源路径，可以直接通过代理访问
      link.href = templatePath;
      
      link.target = '_blank';
      link.download = '体检报告模板.txt';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      
      ElMessage.success('模板下载已开始');
    } else {
      ElMessage.error('获取模板失败: ' + (response.message || '未知错误'));
    }
  } catch (error) {
    console.error('下载模板失败:', error);
    ElMessage.error('下载模板失败，请稍后再试: ' + error.message);
  }
}

// 添加体检项目
function addExamItem() {
  form.examItems.push({
    itemName: '',
    itemValue: '',
    itemUnit: '',
    abnormalFlag: 0
  });
}

// 清除所有体检项目
function clearExamItems() {
  form.examItems = [];
}

// 移除单个体检项目
function removeExamItem(index) {
  form.examItems.splice(index, 1);
}

// 添加预设体检项目
function handleAddPreset(command) {
  // 定义预设体检项目
  const presetItems = {
    bloodPressure: [
      { itemName: '收缩压', itemValue: '', itemUnit: 'mmHg', abnormalFlag: 0 },
      { itemName: '舒张压', itemValue: '', itemUnit: 'mmHg', abnormalFlag: 0 }
    ],
    bloodSugar: [
      { itemName: '空腹血糖', itemValue: '', itemUnit: 'mmol/L', abnormalFlag: 0 },
      { itemName: '餐后2小时血糖', itemValue: '', itemUnit: 'mmol/L', abnormalFlag: 0 }
    ],
    bloodLipids: [
      { itemName: '总胆固醇', itemValue: '', itemUnit: 'mmol/L', abnormalFlag: 0 },
      { itemName: '甘油三酯', itemValue: '', itemUnit: 'mmol/L', abnormalFlag: 0 },
      { itemName: '高密度脂蛋白', itemValue: '', itemUnit: 'mmol/L', abnormalFlag: 0 },
      { itemName: '低密度脂蛋白', itemValue: '', itemUnit: 'mmol/L', abnormalFlag: 0 }
    ],
    liverFunction: [
      { itemName: '谷丙转氨酶(ALT)', itemValue: '', itemUnit: 'U/L', abnormalFlag: 0 },
      { itemName: '谷草转氨酶(AST)', itemValue: '', itemUnit: 'U/L', abnormalFlag: 0 },
      { itemName: '总胆红素', itemValue: '', itemUnit: 'μmol/L', abnormalFlag: 0 }
    ],
    kidneyFunction: [
      { itemName: '肌酐', itemValue: '', itemUnit: 'μmol/L', abnormalFlag: 0 },
      { itemName: '尿素氮', itemValue: '', itemUnit: 'mmol/L', abnormalFlag: 0 }
    ],
    bloodRoutine: [
      { itemName: '白细胞计数', itemValue: '', itemUnit: '10^9/L', abnormalFlag: 0 },
      { itemName: '红细胞计数', itemValue: '', itemUnit: '10^12/L', abnormalFlag: 0 },
      { itemName: '血红蛋白', itemValue: '', itemUnit: 'g/L', abnormalFlag: 0 },
      { itemName: '血小板计数', itemValue: '', itemUnit: '10^9/L', abnormalFlag: 0 }
    ],
    urineRoutine: [
      { itemName: '尿蛋白', itemValue: '', itemUnit: '', abnormalFlag: 0 },
      { itemName: '尿糖', itemValue: '', itemUnit: '', abnormalFlag: 0 },
      { itemName: '尿潜血', itemValue: '', itemUnit: '', abnormalFlag: 0 }
    ]
  };
  
  // 获取选中的预设项目
  const selectedPreset = presetItems[command];
  
  if (selectedPreset && selectedPreset.length > 0) {
    // 确保examItems已初始化
    if (!form.examItems) {
      form.examItems = [];
    }
    
    // 添加预设项目到体检项目列表
    form.examItems.push(...selectedPreset);
    
    ElMessage.success(`已添加${selectedPreset.length}个${command === 'bloodPressure' ? '血压' : 
                                              command === 'bloodSugar' ? '血糖' : 
                                              command === 'bloodLipids' ? '血脂四项' : 
                                              command === 'liverFunction' ? '肝功能' : 
                                              command === 'kidneyFunction' ? '肾功能' : 
                                              command === 'bloodRoutine' ? '血常规' : 
                                              command === 'urineRoutine' ? '尿常规' : ''}项目`);
  }
}

/**
 * 预览文件
 * @param {string} fileUrl - 文件URL
 */
function previewFile(fileUrl) {
  if (!hasValidFileUrl(fileUrl)) {
    ElMessage.warning('文件不存在或文件路径无效')
    return
  }
  
  // 重置预览状态
  previewUrl.value = ''
  previewError.value = ''
  isPdfPreview.value = false
  isImagePreview.value = false
  currentFileType.value = ''
  currentFileUrl.value = ''
  
  // 保存当前文件URL
  currentFileUrl.value = getFileUrl(fileUrl)
  
  // 开始加载
  loading.value = true
  previewVisible.value = true
  
  // 获取文件类型
  const fileExtMatch = fileUrl.match(/\.([^.]+)$/)
  const fileExt = fileExtMatch ? fileExtMatch[1].toLowerCase() : ''
  
  // 判断文件类型
  if (['pdf'].includes(fileExt)) {
    // PDF预览
    isPdfPreview.value = true
    currentFileType.value = 'PDF文档'
    
    // 检查是否支持PDF嵌入预览
    try {
      // 对于PDF文件，获取完整URL并防止被Vue Router拦截
      const pdfUrl = getFileUrl(fileUrl)
      
      // 可以给PDF预览URL添加时间戳参数，避免缓存问题
      previewUrl.value = `${pdfUrl}?t=${new Date().getTime()}`
      
      console.log('PDF预览URL:', previewUrl.value)
    } catch (error) {
      console.error('设置PDF预览URL失败:', error)
      previewError.value = '无法预览PDF文件: ' + error.message
    }
  } else if (['jpg', 'jpeg', 'png'].includes(fileExt)) {
    // 图片预览
    isImagePreview.value = true
    currentFileType.value = fileExt.toUpperCase() + '图片'
    // 同样对图片URL添加时间戳
    previewUrl.value = `${getFileUrl(fileUrl)}?t=${new Date().getTime()}`
  } else {
    // 不支持的文件类型
    previewError.value = '不支持在线预览此类型的文件'
    currentFileType.value = fileExt ? fileExt.toUpperCase() : '未知类型'
  }
  
  // 模拟加载完成
  setTimeout(() => {
    loading.value = false
  }, 800)
}

/**
 * 处理iframe加载事件
 */
function handleIframeLoad(event) {
  console.log('iframe加载成功')
  loading.value = false
}

/**
 * 处理iframe加载错误事件
 */
function handleIframeError(error) {
  console.error('iframe加载失败:', error)
  previewError.value = 'PDF文件加载失败，请尝试下载后查看'
  loading.value = false
}

/**
 * 下载当前预览文件
 */
function downloadCurrentFile() {
  if (!currentFileUrl.value) {
    ElMessage.warning('文件URL为空，无法下载')
    return
  }
  
  try {
    // 创建一个隐藏的a标签
    const link = document.createElement('a')
    
    // 设置链接属性
    link.href = currentFileUrl.value
    link.target = '_blank'
    
    // 提取文件名，如果没有则设置默认名称
    let fileName = '体检报告'
    
    // 尝试从URL中提取文件名
    const urlObj = new URL(currentFileUrl.value, window.location.origin)
    const pathParts = urlObj.pathname.split('/')
    const lastPart = pathParts[pathParts.length - 1]
    
    if (lastPart && lastPart.includes('.')) {
      fileName = decodeURIComponent(lastPart)
    } else if (currentFileType.value) {
      // 根据文件类型添加扩展名
      if (currentFileType.value.includes('PDF')) {
        fileName += '.pdf'
      } else if (currentFileType.value.includes('JPG') || currentFileType.value.includes('JPEG')) {
        fileName += '.jpg'
      } else if (currentFileType.value.includes('PNG')) {
        fileName += '.png'
      }
    }
    
    // 设置下载文件名
    link.download = fileName
    
    // 模拟点击链接触发下载
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    ElMessage.success(`${fileName} 下载已开始`)
  } catch (error) {
    console.error('下载文件失败:', error)
    ElMessage.error('下载文件失败: ' + error.message)
  }
}

/**
 * 从URL中获取文件名
 * @param {string} url - 文件URL
 * @returns {string} 文件名
 */
function getFileName(url) {
  if (!hasValidFileUrl(url)) return '';
  
  try {
    const urlPath = new URL(url, window.location.origin).pathname;
    const fileName = urlPath.split('/').pop();
    return fileName || '未知文件';
  } catch (e) {
    // 如果URL解析失败，尝试直接从路径中获取
    const pathParts = url.split('/');
    return pathParts[pathParts.length - 1] || '未知文件';
  }
}

defineExpose({ onSearch })
</script>

<style lang="scss" scoped>
.physical-exam-report {
  .toolbar {
    margin-bottom: 16px;
    .toolbar-right {
      text-align: right;
    }
  }
}

.text-muted {
  color: #909399;
  font-size: 13px;
}

.empty-detail {
  padding: 40px 0;
  text-align: center;
}

.debug-info {
  display: none;
}

:deep(.warning-row) {
  background-color: #fdf6ec;
  
  td {
    color: #e6a23c;
  }
}

.exam-items-section {
  margin-top: 20px;
  
  .section-title {
    font-size: 16px;
    font-weight: bold;
    margin-bottom: 10px;
    padding-bottom: 8px;
    border-bottom: 1px solid #ebeef5;
  }
  
  .no-items {
    padding: 20px 0;
    text-align: center;
    color: #909399;
    background-color: #f8f8f9;
    border-radius: 4px;
  }
}

:deep(.el-tag--danger) {
  background-color: rgba(245, 108, 108, 0.1);
}

:deep(.el-tag--success) {
  background-color: rgba(103, 194, 58, 0.1);
}

.exam-items-edit {
  margin-top: 20px;
  
  .items-toolbar {
    margin-bottom: 10px;
  }
}

.value-unit-input {
  display: flex;
  align-items: center;
  
  .value-input {
    width: 100px;
    margin-right: 10px;
  }
  
  .unit-input {
    width: 60px;
  }
}

.file-preview-container {
  height: calc(100vh - 180px);
  overflow: hidden;
  position: relative;
}

.preview-loading {
  padding: 40px;
}

.preview-error {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.error-tips {
  font-size: 14px;
  color: #909399;
  margin-top: 10px;
}

.pdf-preview {
  height: 100%;
  overflow: hidden;
  border: 1px solid #ececec;
  border-radius: 4px;
  background-color: #f8f9fa;
}

.pdf-preview iframe {
  width: 100%;
  height: 100%;
  border: none;
  transition: opacity 0.3s;
}

.image-preview {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: auto;
  background-color: #f8f9fa;
  border: 1px solid #ececec;
  border-radius: 4px;
  padding: 10px;
}

.preview-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;
}

.preview-image:hover {
  transform: scale(1.02);
}

.unsupported-preview {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-footer {
  display: flex;
  align-items: center;
}

.file-type-info {
  margin-left: auto;
  font-size: 14px;
  color: #606266;
}

.file-name {
  font-size: 13px;
  color: #909399;
  margin-left: 10px;
}

/* 预览对话框的覆盖样式 */
:deep(.el-dialog__body) {
  padding: 10px;
  height: calc(100vh - 130px);
  overflow: hidden;
}

:deep(.el-dialog__header) {
  padding-bottom: 5px;
  margin-bottom: 0;
  border-bottom: 1px solid #eee;
}

:deep(.el-dialog__footer) {
  padding-top: 5px;
  border-top: 1px solid #eee;
}

.pdf-fallback {
  padding: 20px;
  text-align: center;
  background-color: #f8f9fa;
}

.pdf-fallback p {
  margin-bottom: 15px;
}

/* 在深色模式下优化预览 */
:root.dark .pdf-preview {
  background-color: #2c2c2c;
  border-color: #3a3a3a;
}

:root.dark .image-preview {
  background-color: #2c2c2c;
  border-color: #3a3a3a;
}

:root.dark .preview-image {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.3);
}
</style> 