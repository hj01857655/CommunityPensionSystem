<template>
  <div class="profile-page">
    <el-row :gutter="20">
      <!-- 左侧个人信息卡片 -->
      <el-col :xs="24" :sm="24" :md="8" :lg="8" :xl="6">
        <el-card class="profile-card" shadow="hover">
          <div class="profile-header">
            <el-avatar :size="100" :src="avatarUrl" class="profile-avatar" />
            <h2 class="profile-name">{{ adminInfo.username }}</h2>
            <div class="profile-role">
              <el-tag type="success">{{ roleDisplay }}</el-tag>
            </div>
          </div>
          
          <el-divider />
          
          <div class="profile-info">
            <div class="info-item">
              <span class="info-label">账号状态:</span>
              <el-tag :type="statusValue ? 'success' : 'danger'" size="small">
                {{ statusValue ? '正常' : '禁用' }}
              </el-tag>
            </div>
            
            <div class="info-item">
              <span class="info-label">账号ID:</span>
              <span class="info-value">{{ adminInfo.id }}</span>
            </div>
            
            <div v-if="adminInfo.createTime" class="info-item">
              <span class="info-label">创建时间:</span>
              <span class="info-value">{{ formatDate(adminInfo.createTime) }}</span>
            </div>
            
            <div v-if="adminInfo.updateTime" class="info-item">
              <span class="info-label">更新时间:</span>
              <span class="info-value">{{ formatDate(adminInfo.updateTime) }}</span>
            </div>
            
            <div class="info-item">
              <span class="info-label">性别:</span>
              <span class="info-value">{{ adminInfo.gender || '未知' }}</span>
            </div>
          </div>
          
          <el-divider />
          
          <div class="profile-actions">
            <el-button type="primary" size="large" icon="Edit" @click="activeTab = 'password'">
              修改密码
            </el-button>
          </div>
        </el-card>
      </el-col>
      
      <!-- 右侧功能区域 -->
      <el-col :xs="24" :sm="24" :md="16" :lg="16" :xl="18">
        <el-tabs v-model="activeTab" class="profile-tabs">
          <!-- 用户信息标签页 -->
          <el-tab-pane label="个人资料" name="info">
            <el-card shadow="hover">
              <template #header>
                <div class="tab-header">
                  <span>个人详细信息</span>
                  <el-button type="primary" size="small" @click="isEditing = !isEditing">
                    {{ isEditing ? '取消' : '编辑' }}
                  </el-button>
                </div>
              </template>
              
              <el-form :model="adminInfo" label-width="120px" :disabled="!isEditing">
                <el-form-item label="用户名">
                  <el-input v-model="adminInfo.username" />
                </el-form-item>
                
                <el-form-item label="性别">
                  <el-select v-model="adminInfo.gender" placeholder="请选择性别">
                    <el-option label="男" value="男" />
                    <el-option label="女" value="女" />
                    <el-option label="其他" value="其他" />
                    <el-option label="未知" value="未知" />
                  </el-select>
                </el-form-item>
                
                <el-form-item label="联系方式" v-if="adminInfo.phone">
                  <el-input v-model="adminInfo.phone" />
                </el-form-item>
                
                <el-form-item label="电子邮箱" v-if="adminInfo.email">
                  <el-input v-model="adminInfo.email" />
                </el-form-item>
                
                <el-form-item label="地址" v-if="adminInfo.address">
                  <el-input v-model="adminInfo.address" type="textarea" :rows="2" />
                </el-form-item>
                
                <el-form-item label="备注" v-if="adminInfo.remark">
                  <el-input v-model="adminInfo.remark" type="textarea" :rows="2" />
                </el-form-item>
                
                <el-form-item v-if="isEditing">
                  <el-button type="primary" @click="saveUserInfo">保存</el-button>
                  <el-button @click="isEditing = false">取消</el-button>
                </el-form-item>
              </el-form>
            </el-card>
          </el-tab-pane>
          
          <!-- 修改密码标签页 -->
          <el-tab-pane label="修改密码" name="password">
            <el-card shadow="hover">
              <template #header>
                <div class="tab-header">
                  <span>密码设置</span>
                </div>
              </template>
              
              <el-form
                ref="passwordFormRef"
                :model="passwordForm"
                :rules="passwordRules"
                label-width="120px"
                class="password-form"
              >
                <el-form-item label="原密码" prop="oldPassword">
                  <el-input
                    v-model="passwordForm.oldPassword"
                    type="password"
                    placeholder="请输入原密码"
                    show-password
                  />
                </el-form-item>
                
                <el-form-item label="新密码" prop="newPassword">
                  <el-input
                    v-model="passwordForm.newPassword"
                    type="password"
                    placeholder="请输入新密码"
                    show-password
                  />
                </el-form-item>
                
                <el-form-item label="确认新密码" prop="confirmPassword">
                  <el-input
                    v-model="passwordForm.confirmPassword"
                    type="password"
                    placeholder="请再次输入新密码"
                    show-password
                  />
                </el-form-item>
                
                <el-form-item>
                  <el-button type="primary" :loading="loading" @click="submitForm">
                    修改密码
                  </el-button>
                  <el-button @click="resetForm">重置</el-button>
                </el-form-item>
              </el-form>
              
              <div class="password-tips">
                <h4>密码规则</h4>
                <ul>
                  <li>长度在6-20个字符</li>
                  <li>必须包含大写字母、小写字母和数字</li>
                  <li>不能与原密码相同</li>
                </ul>
              </div>
            </el-card>
          </el-tab-pane>
          
          <!-- 可以添加更多标签页，如安全设置、通知设置等 -->
        </el-tabs>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import axios from '@/utils/axios';
import { useAdminStore } from '@/stores/back/adminStore';
import { getAvatarUrl } from '@/utils/avatarUtils';
import { useRoute } from 'vue-router';

// 初始化adminStore
const adminStore = useAdminStore();

const route = useRoute();

// Tab页管理
const activeTab = ref(route.query.tab || 'info');
const isEditing = ref(false);

// 角色映射
const roleMap = {
  '1': '老人',
  '2': '亲属',
  '3': '社区工作人员',
  '4': '管理员',
  '5': '访客'
};

// 管理员信息
const adminInfo = ref(adminStore.adminInfo || {});

// 计算角色显示文本
const roleDisplay = computed(() => {
  return roleMap[adminInfo.value.roleId] ;
});

// 计算状态值，将isActive转换为布尔值
const statusValue = computed(() => {
  return adminInfo.value.isActive === 1 || adminInfo.value.isActive === '1';
});

// 计算头像URL
const avatarUrl = computed(() => getAvatarUrl(adminInfo.value.avatar));

// 添加日期格式化函数
const formatDate = (dateStr) => {
  if (!dateStr) return '';
  
  try {
    const date = new Date(dateStr);
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  } catch (e) {
    return dateStr;
  }
};

// 获取管理员信息
const getAdminInfo = async () => {
  try {
    // 直接从 localStorage 获取用户信息
    const userInfoStr = localStorage.getItem('userInfo');
    
    if (userInfoStr) {
      try {
        // 解析并使用 localStorage 中的用户信息
        const userInfo = JSON.parse(userInfoStr);
        adminInfo.value = userInfo;
        console.log('从 localStorage 加载的用户信息:', adminInfo.value);
        return; // 成功加载数据，直接返回
      } catch (e) {
        console.error('解析 localStorage 中的用户信息失败:', e);
      }
    }
    
    // 如果没有从 localStorage 获取到数据，使用默认数据
    console.log('使用默认用户数据');
    adminInfo.value = {
      id: 2,
      userId: 2,
      username: '管理员',
      roleId: 4,
      isActive: 1,
      gender: '男',
      email: 'admin@example.com',
      phone: '13800138000',
      createTime: new Date().toISOString(),
      updateTime: new Date().toISOString()
    };
    
    console.log('使用默认数据:', adminInfo.value);
  } catch (error) {
    console.error('获取管理员信息出错:', error);
    adminInfo.value = { username: '管理员', roleId: 4 }; // 设置默认值
  }
};

// 保存用户信息
const saveUserInfo = async () => {
  try {
    // 确保包含用户ID
    const userData = {
      id: adminInfo.value.id,  // 添加用户ID
      username: adminInfo.value.username,
      gender: adminInfo.value.gender || '未知',
      phone: adminInfo.value.phone || '',
      email: adminInfo.value.email || '',
      address: adminInfo.value.address || '',
      remark: adminInfo.value.remark || ''
    };
    
    console.log('发送的用户数据:', userData);
    
    const response = await axios.put('/api/user/profile', userData);
    
    if (response.code === 200) {
      ElMessage.success('个人信息保存成功');
      isEditing.value = false;
      
      // 更新存储的管理员信息
      if (adminStore) {
        adminStore.setAdminInfo(response.data);
      }
    } else {
      ElMessage.error(response.message || '保存失败');
    }
  } catch (error) {
    console.error('保存个人信息错误:', error);
    
    // 错误信息显示
    if (error.response) {
      ElMessage.error(`更新失败: ${error.response.data?.message || '未知错误'}`);
    } else {
      ElMessage.error('网络错误，请稍后再试');
    }
  }
};

// 密码修改部分
const passwordFormRef = ref(null);
const loading = ref(false);

// 表单数据
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 自定义验证规则 - 确认新密码
const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'));
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'));
  } else {
    callback();
  }
};

// 自定义验证规则 - 新密码不能与原密码相同
const validateNewPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入新密码'));
  } else if (value === passwordForm.oldPassword) {
    callback(new Error('新密码不能与原密码相同'));
  } else if (!/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{6,20}$/.test(value)) {
    callback(new Error('密码必须包含大小写字母和数字，长度在6-20之间'));
  } else {
    // 如果确认密码已经填写，则验证两次输入是否一致
    if (passwordForm.confirmPassword !== '') {
      passwordFormRef.value.validateField('confirmPassword');
    }
    callback();
  }
};

// 表单验证规则
const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6到20个字符', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, validator: validateNewPassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
};

// 提交表单
const submitForm = () => {
  passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        const response = await axios.post('/api/user/change-password', {
          userId: adminInfo.value.id,  // 添加用户ID
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        });
        
        if (response.code === 200) {
          ElMessage.success('密码修改成功');
          resetForm();
        } else {
          ElMessage.error(response.message || '密码修改失败');
        }
      } catch (error) {
        console.error('密码修改错误:', error);
        ElMessage.error('服务器错误，请稍后再试');
      } finally {
        loading.value = false;
      }
    } else {
      return false;
    }
  });
};

// 重置表单
const resetForm = () => {
  passwordFormRef.value.resetFields();
};

onMounted(() => {
  if (adminStore && typeof adminStore.initAdminInfo === 'function') {
    adminStore.initAdminInfo();
  }
  getAdminInfo();
  
  // 监听URL参数变化
  if (route.query.tab) {
    activeTab.value = route.query.tab;
  }
});
</script>

<style scoped>
.profile-page {
  padding: 20px;
}

.profile-card {
  margin-bottom: 20px;
}

.profile-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-bottom: 20px;
}

.profile-avatar {
  margin-bottom: 16px;
  border: 4px solid #f0f2f5;
}

.profile-name {
  margin: 8px 0;
  font-size: 20px;
  font-weight: 500;
}

.profile-role {
  margin-bottom: 10px;
}

.profile-info {
  padding: 10px 0;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  font-size: 14px;
}

.info-label {
  color: #606266;
}

.info-value {
  color: #303133;
  font-weight: 500;
}

.profile-actions {
  display: flex;
  justify-content: center;
  padding: 10px 0;
}

.profile-tabs {
  min-height: 500px;
}

.tab-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.password-form {
  max-width: 500px;
}

.password-tips {
  margin-top: 30px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
  color: #606266;
}

.password-tips h4 {
  margin-top: 0;
  margin-bottom: 10px;
  font-size: 14px;
  color: #303133;
}

.password-tips ul {
  margin: 0;
  padding-left: 20px;
}

.password-tips li {
  font-size: 13px;
  line-height: 1.8;
}
</style> 