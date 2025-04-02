<template>
  <div class="notice-publish">
    <el-card shadow="hover" class="main-card">
      <template #header>
        <div class="card-header">
          <h3>{{ isEdit ? '编辑通知' : '发布通知' }}</h3>
          <div class="header-actions">
            <el-button @click="handleBack">
              <el-icon><Back /></el-icon>
              返回列表
            </el-button>
          </div>
        </div>
      </template>

      <el-form 
        ref="noticeFormRef" 
        :model="noticeForm" 
        :rules="noticeRules" 
        label-width="100px"
        label-position="top"
        status-icon
        class="notice-form">
        <el-form-item label="通知标题" prop="title">
          <el-input 
            v-model="noticeForm.title" 
            placeholder="请输入通知标题"
            maxlength="100"
            show-word-limit />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="通知类型" prop="type">
              <el-select v-model="noticeForm.type" placeholder="请选择通知类型" style="width: 100%">
                <el-option label="健康通知" value="健康通知" />
                <el-option label="活动通知" value="活动通知" />
                <el-option label="工作通知" value="工作通知" />
                <el-option label="紧急通知" value="紧急通知" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发布时间" prop="publishTime">
              <el-date-picker
                v-model="noticeForm.publishTime"
                type="datetime"
                placeholder="选择发布时间"
                style="width: 100%"
                value-format="YYYY-MM-DD HH:mm:ss"
                :disabled-date="disabledDate" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="通知内容" prop="content">
          <div class="editor-container">
            <el-input
              v-model="noticeForm.content"
              type="textarea"
              :rows="10"
              placeholder="请输入通知内容"
              maxlength="5000"
              show-word-limit />
          </div>
        </el-form-item>
        
        <el-form-item label="附件">
          <el-upload
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            multiple
            :limit="5"
            :file-list="fileList">
            <el-button type="primary">
              <el-icon><Upload /></el-icon>
              选择文件
            </el-button>
            <template #tip>
              <div class="el-upload__tip">
                支持 jpg、png、pdf、doc、docx、xls、xlsx 格式文件，单个文件不超过10MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
        
        <el-form-item label="接收对象" prop="receivers">
          <el-checkbox-group v-model="noticeForm.receivers">
            <el-checkbox value="all">所有人</el-checkbox>
            <el-checkbox value="elders">老人</el-checkbox>
            <el-checkbox value="staff">工作人员</el-checkbox>
            <el-checkbox value="family">家属</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        
        <el-form-item label="是否置顶" prop="isTop">
          <el-switch v-model="noticeForm.isTop" />
        </el-form-item>
        
        <el-form-item>
          <div class="form-actions">
            <el-button @click="handleSaveDraft" :loading="savingDraft">保存草稿</el-button>
            <el-button type="primary" @click="handlePublish" :loading="publishing">立即发布</el-button>
            <el-button type="info" @click="handlePreview">预览</el-button>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 预览对话框 -->
    <el-dialog
      v-model="previewVisible"
      title="通知预览"
      width="60%"
      top="5vh"
      :close-on-click-modal="false">
      <div class="notice-preview">
        <div class="notice-header">
          <h2>{{ noticeForm.title || '通知标题' }}</h2>
          <div class="notice-meta">
            <el-tag size="small" :type="getTypeTagType(noticeForm.type)">{{ noticeForm.type || '通知类型' }}</el-tag>
            <span class="publish-time">发布时间：{{ noticeForm.publishTime || '未设置' }}</span>
            <span class="publisher">发布人：当前管理员</span>
          </div>
        </div>
        <el-divider />
        <div class="notice-content" v-html="noticeForm.content || '通知内容'"></div>
        
        <div v-if="fileList.length > 0" class="notice-attachments">
          <h4>附件列表</h4>
          <ul>
            <li v-for="(file, index) in fileList" :key="index">
              <el-icon><Document /></el-icon>
              <span>{{ file.name }}</span>
            </li>
          </ul>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="previewVisible = false">关闭</el-button>
          <el-button type="primary" @click="handlePublish" :loading="publishing">立即发布</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Back, Upload, Document } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import { useRoute } from 'vue-router';
import { useNoticeStore } from '@/stores/back/noticeStore';
const router = useRouter();
const route = useRoute();

// 判断是否为编辑模式
const isEdit = computed(() => {
  return route.query.id !== undefined;
});

// 表单引用
const noticeFormRef = ref(null);

// 表单数据
const noticeForm = reactive({
  id: '',
  title: '',
  type: '',
  content: '',
  publishTime: '',
  receivers: ['all'],
  isTop: false
});

// 表单验证规则
const noticeRules = {
  title: [
    { required: true, message: '请输入通知标题', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择通知类型', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入通知内容', trigger: 'blur' }
  ],
  receivers: [
    { required: true, message: '请选择接收对象', trigger: 'change' }
  ]
};

// 附件列表
const fileList = ref([]);

// 状态
const savingDraft = ref(false);
const publishing = ref(false);
const previewVisible = ref(false);

// 获取通知类型标签样式
const getTypeTagType = (type) => {
  const typeMap = {
    '健康通知': 'success',
    '活动通知': 'primary',
    '工作通知': 'warning',
    '紧急通知': 'danger'
  };
  return typeMap[type] || 'info';
};

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7; // 禁用昨天及之前的日期
};

// 处理文件变化
const handleFileChange = (file, fileList) => {
  // 这里可以添加文件类型和大小的验证
  console.log('文件变化:', file, fileList);
};

// 处理文件移除
const handleFileRemove = (file, fileList) => {
  console.log('文件移除:', file, fileList);
};

// 返回列表
const handleBack = () => {
  ElMessageBox.confirm('确定要离开当前页面吗？未保存的内容将会丢失', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    router.push('/admin/notice/');
  }).catch(() => {
    // 取消离开
  });
};

// 保存草稿
const handleSaveDraft = () => {
  noticeFormRef.value.validate((valid) => {
    if (valid) {
      savingDraft.value = true;
      
      // 模拟API调用
      setTimeout(() => {
        ElMessage.success('草稿保存成功');
        savingDraft.value = false;
      }, 600);
    } else {
      return false;
    }
  });
};

// 发布通知
const handlePublish = () => {
  noticeFormRef.value.validate((valid) => {
    if (valid) {
      publishing.value = true;
      
      // 模拟API调用
      setTimeout(() => {
        ElMessage.success(isEdit.value ? '通知更新成功' : '通知发布成功');
        publishing.value = false;
        previewVisible.value = false;
        router.push('/admin/notice/');
      }, 800);
    } else {
      return false;
    }
  });
};

// 预览通知
const handlePreview = () => {
  noticeFormRef.value.validate((valid) => {
    if (valid) {
      previewVisible.value = true;
    } else {
      ElMessage.warning('请先完善通知信息');
      return false;
    }
  });
};

// 获取通知详情
const getNoticeDetail = async (id) => {
  try {
    await noticeStore.loadNoticeDetail(id);
    const noticeDetail = noticeStore.currentNotice;
    
    // 填充表单
    Object.keys(noticeDetail).forEach(key => {
      if (key in noticeForm) {
        noticeForm[key] = noticeDetail[key];
      }
    });
    
    // 处理附件
    if (noticeDetail.attachments) {
      fileList.value = noticeDetail.attachments.map(att => ({
        name: att.name,
        url: att.url
      }));
    }
  } catch (error) {
    console.error('获取通知详情失败:', error);
    ElMessage.error('获取通知详情失败');
  }
};

onMounted(() => {
  if (isEdit.value) {
    getNoticeDetail(route.query.id);
  }
});
</script>

<style scoped>
.notice-publish {
  padding: 24px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 84px);
  animation: fadeIn 0.5s ease-out;
}

.main-card {
  margin-bottom: 24px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: linear-gradient(to right bottom, #ffffff, #fafafa);
  overflow: hidden;
}

.main-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 32px;
  background: linear-gradient(to right, #f8f9fa, #ffffff);
  border-bottom: 1px solid #e4e7ed;
}

.card-header h3 {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
  color: #1a1a1a;
  letter-spacing: 0.5px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.notice-form {
  padding: 32px;
}

:deep(.el-form-item__label) {
  font-weight: 600;
  color: #1a1a1a;
  font-size: 14px;
  margin-bottom: 8px;
}

:deep(.el-input__wrapper),
:deep(.el-select__wrapper),
:deep(.el-date-editor__wrapper),
:deep(.el-textarea__wrapper) {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 8px;
  padding: 8px 12px;
}

:deep(.el-input__wrapper:hover),
:deep(.el-select__wrapper:hover),
:deep(.el-date-editor__wrapper:hover),
:deep(.el-textarea__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.1);
}

:deep(.el-input__wrapper.is-focus),
:deep(.el-select__wrapper.is-focus),
:deep(.el-date-editor__wrapper.is-focus),
:deep(.el-textarea__wrapper.is-focus) {
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.editor-container {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.editor-container:hover {
  border-color: #409EFF;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.1);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px dashed #e4e7ed;
}

:deep(.el-button) {
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.el-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

:deep(.el-button--primary) {
  background: linear-gradient(45deg, #409EFF, #66b1ff);
  border: none;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(45deg, #66b1ff, #409EFF);
}

:deep(.el-upload) {
  width: 100%;
}

:deep(.el-upload-dragger) {
  width: 100%;
  border-radius: 8px;
  border: 2px dashed #e4e7ed;
  transition: all 0.3s ease;
}

:deep(.el-upload-dragger:hover) {
  border-color: #409EFF;
  background-color: #f5f7fa;
}

:deep(.el-upload__tip) {
  margin-top: 8px;
  color: #909399;
  font-size: 13px;
}

:deep(.el-checkbox-group) {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

:deep(.el-checkbox) {
  margin-right: 0;
}

:deep(.el-switch) {
  --el-switch-on-color: #67c23a;
  --el-switch-off-color: #909399;
}

.notice-preview {
  padding: 32px;
  background: linear-gradient(135deg, #f8f9fa, #ffffff);
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.notice-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.notice-header h2 {
  margin: 0 0 16px 0;
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
  line-height: 1.4;
}

.notice-meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
  color: #909399;
  font-size: 14px;
}

:deep(.el-tag) {
  border-radius: 6px;
  padding: 0 12px;
  height: 28px;
  line-height: 28px;
  font-weight: 500;
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

:deep(.el-tag--success) {
  background: linear-gradient(45deg, #e6f7e6, #f0f9f0);
  color: #67c23a;
}

:deep(.el-tag--primary) {
  background: linear-gradient(45deg, #e6f3ff, #f0f7ff);
  color: #409eff;
}

:deep(.el-tag--warning) {
  background: linear-gradient(45deg, #fff7e6, #fff9f0);
  color: #e6a23c;
}

:deep(.el-tag--danger) {
  background: linear-gradient(45deg, #ffe6e6, #fff0f0);
  color: #f56c6c;
}

.notice-content {
  line-height: 1.8;
  color: #4a4a4a;
  min-height: 200px;
  white-space: pre-wrap;
  font-size: 15px;
}

.notice-attachments {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px dashed #e4e7ed;
}

.notice-attachments h4 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #1a1a1a;
}

.notice-attachments ul {
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.notice-attachments li {
  display: flex;
  align-items: center;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.notice-attachments li:hover {
  background: #e6f3ff;
  transform: translateY(-2px);
}

.notice-attachments li .el-icon {
  margin-right: 8px;
  color: #409EFF;
  font-size: 18px;
}

.notice-attachments li span {
  color: #606266;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  padding-top: 24px;
  border-top: 1px dashed #e4e7ed;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式布局 */
@media screen and (max-width: 768px) {
  .notice-publish {
    padding: 16px;
  }
  
  .card-header {
    padding: 16px 20px;
  }
  
  .notice-form {
    padding: 20px;
  }
  
  .form-actions {
    flex-direction: column;
    gap: 12px;
  }
  
  .form-actions .el-button {
    width: 100%;
  }
  
  .notice-preview {
    padding: 20px;
  }
  
  .notice-header h2 {
    font-size: 20px;
  }
  
  .notice-attachments ul {
    grid-template-columns: 1fr;
  }
}

/* 滚动条美化 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>