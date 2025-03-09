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
            <el-checkbox label="all">所有人</el-checkbox>
            <el-checkbox label="elders">老人</el-checkbox>
            <el-checkbox label="staff">工作人员</el-checkbox>
            <el-checkbox label="family">家属</el-checkbox>
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

const router = useRouter();

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
    router.push('/admin/notice');
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
        router.push('/admin/notice');
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
const getNoticeDetail = (id) => {
  // 模拟API调用
  setTimeout(() => {
    // 假设这是从API获取的数据
    const noticeDetail = {
      id: id,
      title: '关于社区老年人健康体检的通知',
      type: '健康通知',
      content: '<p>各位社区老年人：</p><p>为了关心老年人健康，社区将于2024年6月15日至20日开展免费健康体检活动。请各位老年人携带身份证和社区卡到社区服务中心参加体检。</p><p>体检项目包括：血压、血糖、心电图、B超等基础检查项目。</p><p>请各位老年人合理安排时间参加。</p>',
      publishTime: '2024-06-01 09:00:00',
      receivers: ['all', 'elders'],
      isTop: true
    };
    
    // 填充表单
    Object.keys(noticeDetail).forEach(key => {
      if (key in noticeForm) {
        noticeForm[key] = noticeDetail[key];
      }
    });
    
    // 模拟附件
    fileList.value = [
      { name: '体检项目清单.docx', url: '' },
      { name: '体检注意事项.pdf', url: '' }
    ];
  }, 500);
};

onMounted(() => {
  if (isEdit.value) {
    getNoticeDetail(route.query.id);
  }
});
</script>

<style scoped>
.notice-publish {
  padding: 16px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 120px);
}

.main-card {
  margin-bottom: 20px;
  transition: all 0.3s;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.main-card:hover {
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.card-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.notice-form {
  padding: 20px 0;
}

.editor-container {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
}

.notice-preview {
  padding: 20px;
}

.notice-header {
  margin-bottom: 16px;
}

.notice-header h2 {
  margin: 0 0 16px 0;
  font-size: 22px;
  font-weight: 600;
  color: #303133;
}

.notice-meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
  color: #909399;
  font-size: 14px;
}

.notice-content {
  line-height: 1.8;
  color: #606266;
  min-height: 200px;
  white-space: pre-wrap;
}

.notice-attachments {
  margin-top: 24px;
  border-top: 1px dashed #e4e7ed;
  padding-top: 16px;
}

.notice-attachments h4 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #303133;
}

.notice-attachments ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.notice-attachments li {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  color: #606266;
}

.notice-attachments li .el-icon {
  margin-right: 8px;
  color: #409EFF;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .notice-form {
    padding: 10px 0;
  }
  
  .form-actions {
    flex-direction: column;
    align-items: stretch;
  }
  
  .form-actions .el-button {
    margin-bottom: 10px;
  }
}
</style>