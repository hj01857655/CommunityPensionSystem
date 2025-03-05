<template>
    <div class="system-settings">
      <el-row :gutter="20">
        <!-- 系统设置 -->
        <el-col :span="24" :lg="12">
          <el-card shadow="hover" class="settings-card">
            <template #header>
              <div class="card-header">
                <h3>系统设置</h3>
              </div>
            </template>
            
            <el-form 
              ref="systemFormRef"
              :model="systemForm"
              :rules="systemRules"
              label-width="120px"
              class="settings-form"
            >
              <el-form-item label="系统名称" prop="systemName">
                <el-input v-model="systemForm.systemName" />
              </el-form-item>
              
              <el-form-item label="系统Logo">
                <el-upload
                  class="avatar-uploader"
                  action="#"
                  :show-file-list="false"
                  :auto-upload="false"
                  :on-change="handleLogoChange"
                  :before-upload="beforeLogoUpload"
                >
                  <img v-if="systemForm.logo" :src="systemForm.logo" class="avatar" />
                  <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                </el-upload>
              </el-form-item>
              
              <el-form-item label="系统描述" prop="description">
                <el-input 
                  v-model="systemForm.description" 
                  type="textarea" 
                  :rows="3" 
                />
              </el-form-item>
              
              <el-form-item label="备案信息" prop="icp">
                <el-input v-model="systemForm.icp" />
              </el-form-item>
              
              <el-form-item label="联系电话" prop="contactPhone">
                <el-input v-model="systemForm.contactPhone" />
              </el-form-item>
              
              <el-form-item label="联系邮箱" prop="contactEmail">
                <el-input v-model="systemForm.contactEmail" />
              </el-form-item>
              
              <el-form-item label="系统版本">
                <el-input v-model="systemForm.version" disabled />
              </el-form-item>
              
              <el-form-item>
                <el-button type="primary" @click="saveSystemSettings">保存设置</el-button>
                <el-button @click="resetSystemForm">重置</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
        
        <!-- 安全设置 -->
        <el-col :span="24" :lg="12">
          <el-card shadow="hover" class="settings-card">
            <template #header>
              <div class="card-header">
                <h3>安全设置</h3>
              </div>
            </template>
            
            <el-form 
              ref="securityFormRef"
              :model="securityForm"
              :rules="securityRules"
              label-width="160px"
              class="settings-form"
            >
              <el-form-item label="密码最小长度" prop="passwordMinLength">
                <el-input-number v-model="securityForm.passwordMinLength" :min="6" :max="20" />
              </el-form-item>
              
              <el-form-item label="密码复杂度要求" prop="passwordComplexity">
                <el-select v-model="securityForm.passwordComplexity" placeholder="请选择密码复杂度">
                  <el-option label="低 (仅字母和数字)" value="low" />
                  <el-option label="中 (字母、数字和特殊字符)" value="medium" />
                  <el-option label="高 (大小写字母、数字和特殊字符)" value="high" />
                </el-select>
              </el-form-item>
              
              <el-form-item label="登录失败锁定次数" prop="loginFailLockCount">
                <el-input-number v-model="securityForm.loginFailLockCount" :min="3" :max="10" />
              </el-form-item>
              
              <el-form-item label="账户锁定时间(分钟)" prop="accountLockTime">
                <el-input-number v-model="securityForm.accountLockTime" :min="5" :max="60" />
              </el-form-item>
              
              <el-form-item label="会话超时时间(分钟)" prop="sessionTimeout">
                <el-input-number v-model="securityForm.sessionTimeout" :min="10" :max="120" />
              </el-form-item>
              
              <el-form-item label="启用登录验证码" prop="enableCaptcha">
                <el-switch v-model="securityForm.enableCaptcha" />
              </el-form-item>
              
              <el-form-item label="启用IP限制" prop="enableIpRestriction">
                <el-switch v-model="securityForm.enableIpRestriction" />
              </el-form-item>
              
              <el-form-item label="允许的IP地址" prop="allowedIps" v-if="securityForm.enableIpRestriction">
                <el-input 
                  v-model="securityForm.allowedIps" 
                  type="textarea" 
                  :rows="3" 
                  placeholder="每行一个IP地址或IP段，例如：192.168.1.1 或 192.168.1.0/24"
                />
              </el-form-item>
              
              <el-form-item>
                <el-button type="primary" @click="saveSecuritySettings">保存设置</el-button>
                <el-button @click="resetSecurityForm">重置</el-button>
              </el-form-item>
            </el-form>
          </el-card>
          
          <!-- 数据备份 -->
          <el-card shadow="hover" class="settings-card">
            <template #header>
              <div class="card-header">
                <h3>数据备份</h3>
              </div>
            </template>
            
            <div class="backup-actions">
              <el-button type="primary" @click="handleBackup">立即备份</el-button>
              <el-button type="warning" @click="handleRestore">恢复数据</el-button>
            </div>
            
            <el-divider content-position="center">备份记录</el-divider>
            
            <el-table :data="backupRecords" style="width: 100%" border>
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="fileName" label="文件名" min-width="180" />
              <el-table-column prop="size" label="大小" width="100" />
              <el-table-column prop="createTime" label="创建时间" width="180" />
              <el-table-column label="操作" width="150" fixed="right">
                <template #default="scope">
                  <el-button type="primary" size="small" @click="handleDownload(scope.row)">下载</el-button>
                  <el-button type="danger" size="small" @click="handleDeleteBackup(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 恢复数据对话框 -->
      <el-dialog
        v-model="restoreDialogVisible"
        title="恢复数据"
        width="500px"
      >
        <el-alert
          title="警告：恢复数据将覆盖当前系统数据，请谨慎操作！"
          type="warning"
          :closable="false"
          show-icon
          style="margin-bottom: 20px;"
        />
        
        <el-form label-width="100px">
          <el-form-item label="选择备份">
            <el-select v-model="selectedBackup" placeholder="请选择备份文件">
              <el-option
                v-for="item in backupRecords"
                :key="item.id"
                :label="item.fileName"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="确认密码">
            <el-input v-model="restorePassword" type="password" placeholder="请输入管理员密码" show-password />
          </el-form-item>
        </el-form>
        
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="restoreDialogVisible = false">取消</el-button>
            <el-button type="danger" @click="confirmRestore">确认恢复</el-button>
          </span>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive, onMounted } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { Plus } from '@element-plus/icons-vue'
  
  // 系统设置表单
  const systemFormRef = ref(null)
  const systemForm = reactive({
    systemName: '社区养老系统',
    logo: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
    description: '为社区老人提供全方位的养老服务和健康管理',
    icp: '京ICP备12345678号',
    contactPhone: '400-123-4567',
    contactEmail: 'support@example.com',
    version: 'v1.0.0'
  })
  
  // 安全设置表单
  const securityFormRef = ref(null)
  const securityForm = reactive({
    passwordMinLength: 8,
    passwordComplexity: 'medium',
    loginFailLockCount: 5,
    accountLockTime: 30,
    sessionTimeout: 30,
    enableCaptcha: true,
    enableIpRestriction: false,
    allowedIps: ''
  })
  
  // 备份记录
  const backupRecords = ref([
    {
      id: 1,
      fileName: 'backup_20250225_120000.sql',
      size: '2.5MB',
      createTime: '2025-02-25 12:00:00'
    },
    {
      id: 2,
      fileName: 'backup_20250224_120000.sql',
      size: '2.4MB',
      createTime: '2025-02-24 12:00:00'
    },
    {
      id: 3,
      fileName: 'backup_20250223_120000.sql',
      size: '2.3MB',
      createTime: '2025-02-23 12:00:00'
    }
  ])
  
  // 恢复数据对话框
  const restoreDialogVisible = ref(false)
  const selectedBackup = ref('')
  const restorePassword = ref('')
  
  // 表单验证规则
  const systemRules = {
    systemName: [
      { required: true, message: '请输入系统名称', trigger: 'blur' }
    ],
    contactPhone: [
      { pattern: /^1[3-9]\d{9}$|^0\d{2,3}-\d{7,8}$/, message: '请输入正确的联系电话', trigger: 'blur' }
    ],
    contactEmail: [
      { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
    ]
  }
  
  const securityRules = {
    passwordMinLength: [
      { required: true, message: '请设置密码最小长度', trigger: 'blur' }
    ],
    passwordComplexity: [
      { required: true, message: '请选择密码复杂度', trigger: 'change' }
    ],
    loginFailLockCount: [
      { required: true, message: '请设置登录失败锁定次数', trigger: 'blur' }
    ],
    accountLockTime: [
      { required: true, message: '请设置账户锁定时间', trigger: 'blur' }
    ],
    sessionTimeout: [
      { required: true, message: '请设置会话超时时间', trigger: 'blur' }
    ]
  }
  
  // 处理Logo上传前的验证
  const beforeLogoUpload = (file) => {
    const isImage = file.type.startsWith('image/')
    const isLt2M = file.size / 1024 / 1024 < 2
  
    if (!isImage) {
      ElMessage.error('Logo必须是图片格式!')
      return false
    }
    if (!isLt2M) {
      ElMessage.error('Logo图片大小不能超过2MB!')
      return false
    }
    return true
  }
  
  // 处理Logo上传
  const handleLogoChange = (file) => {
    if (beforeLogoUpload(file.raw)) {
      const reader = new FileReader()
      reader.readAsDataURL(file.raw)
      reader.onload = () => {
        systemForm.logo = reader.result
      }
    }
  }
  
  // 保存系统设置
  const saveSystemSettings = () => {
    systemFormRef.value.validate((valid) => {
      if (valid) {
        // 这里应该调用API保存系统设置
        ElMessage.success('系统设置保存成功')
      }
    })
  }
  
  // 重置系统设置表单
  const resetSystemForm = () => {
    systemFormRef.value.resetFields()
  }
  
  // 保存安全设置
  const saveSecuritySettings = () => {
    securityFormRef.value.validate((valid) => {
      if (valid) {
        // 这里应该调用API保存安全设置
        ElMessage.success('安全设置保存成功')
      }
    })
  }
  
  // 重置安全设置表单
  const resetSecurityForm = () => {
    securityFormRef.value.resetFields()
  }
  
  // 立即备份
  const handleBackup = () => {
    ElMessageBox.confirm(
      '确定要立即备份系统数据吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    ).then(() => {
      // 这里应该调用API进行数据备份
      ElMessage.success('数据备份成功')
      
      // 模拟添加新的备份记录
      const now = new Date()
      const fileName = `backup_${now.getFullYear()}${String(now.getMonth() + 1).padStart(2, '0')}${String(now.getDate()).padStart(2, '0')}_${String(now.getHours()).padStart(2, '0')}${String(now.getMinutes()).padStart(2, '0')}${String(now.getSeconds()).padStart(2, '0')}.sql`
      const newBackup = {
        id: backupRecords.value.length + 1,
        fileName,
        size: '2.6MB',
        createTime: now.toLocaleString()
      }
      backupRecords.value.unshift(newBackup)
    }).catch(() => {
      // 取消备份
    })
  }
  
  // 恢复数据
  const handleRestore = () => {
    selectedBackup.value = ''
    restorePassword.value = ''
    restoreDialogVisible.value = true
  }
  
  // 确认恢复数据
  const confirmRestore = () => {
    if (!selectedBackup.value) {
      ElMessage.warning('请选择备份文件')
      return
    }
    
    if (!restorePassword.value) {
      ElMessage.warning('请输入管理员密码')
      return
    }
    
    // 这里应该验证密码并调用API恢复数据
    ElMessageBox.confirm(
      '恢复数据将覆盖当前系统数据，确定要继续吗？',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(() => {
      // 模拟恢复过程
      ElMessage.success('数据恢复成功，系统将在3秒后自动刷新')
      setTimeout(() => {
        window.location.reload()
      }, 3000)
      restoreDialogVisible.value = false
    }).catch(() => {
      // 取消恢复
    })
  }
  
  // 下载备份
  const handleDownload = (row) => {
    // 这里应该调用API下载备份文件
    ElMessage.success(`正在下载备份文件：${row.fileName}`)
  }
  
  // 删除备份
  const handleDeleteBackup = (row) => {
    ElMessageBox.confirm(
      `确定要删除备份文件 "${row.fileName}" 吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(() => {
      // 这里应该调用API删除备份文件
      backupRecords.value = backupRecords.value.filter(record => record.id !== row.id)
      ElMessage.success('备份文件删除成功')
    }).catch(() => {
      // 取消删除
    })
  }
  
  onMounted(() => {
    // 这里可以调用API获取系统设置和安全设置
  })
  </script>
  
  <style scoped>
  .system-settings {
    padding: 10px;
  }
  
  .settings-card {
    margin-bottom: 20px;
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .card-header h3 {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
  }
  
  .settings-form {
    max-width: 100%;
  }
  
  .avatar-uploader {
    width: 178px;
    height: 178px;
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
  }
  
  .avatar-uploader:hover {
    border-color: var(--el-color-primary);
  }
  
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    text-align: center;
    line-height: 178px;
  }
  
  .avatar {
    width: 178px;
    height: 178px;
    display: block;
  }
  
  .backup-actions {
    display: flex;
    gap: 10px;
    margin-bottom: 20px;
  }
  </style>