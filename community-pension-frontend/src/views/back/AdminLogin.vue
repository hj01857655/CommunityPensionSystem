<template>
  <div class="admin-login-container">
    <div class="login-content">
      <div class="login-header">
        <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/vuejs/vuejs-original.svg" class="logo" alt="Logo" />
        <h1 class="title">社区养老系统</h1>
        <p class="subtitle">管理后台</p>
      </div>

      <el-card class="login-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <h2 class="form-title">后台管理登录</h2>
          </div>
        </template>

        <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" label-position="top"
          @keyup.enter="handleLogin">
          <el-form-item prop="username" label="用户名">
            <el-input v-model="loginForm.username" placeholder="请输入用户名" :prefix-icon="User" clearable tabindex="1"
              aria-label="用户名" />
          </el-form-item>

          <el-form-item prop="password" label="密码">
            <el-input v-model="loginForm.password" :type="passwordVisible ? 'text' : 'password'" placeholder="请输入密码"
              :prefix-icon="Lock" tabindex="2" aria-label="密码" show-password />
          </el-form-item>

          <el-form-item prop="roleId" label="角色">
            <el-select v-model="loginForm.roleId" placeholder="请选择角色" class="role-select" tabindex="3" 
              aria-label="角色选择" @change="onRoleChange">
              <el-option :label="'社区工作人员'" :value="3" />
              <el-option :label="'管理员'" :value="4" />
            </el-select>
          </el-form-item>

          <div class="login-options">
            <el-checkbox v-model="remember" label="记住我" tabindex="4" aria-label="记住我" />
            <el-link type="primary" :underline="false" @click="handleForgotPassword" tabindex="5" aria-label="忘记密码">
              忘记密码?
            </el-link>
          </div>

          <el-form-item>
            <el-button type="primary" class="login-button" :loading="loading" @click="handleLogin" tabindex="6"
              aria-label="登录">
              {{ loading ? '登录中...' : '登录' }}
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <div class="login-footer">
        <p>© {{ currentYear }} 社区养老系统 - @copyright 版权所有</p>
      </div>
    </div>

    <!-- 忘记密码对话框 -->
    <el-dialog v-model="forgotPasswordVisible" title="忘记密码" width="400px" center append-to-body destroy-on-close>
      <el-form :model="forgotForm" :rules="forgotRules" ref="forgotFormRef" label-position="top">
        <el-form-item prop="username" label="用户名">
          <el-input v-model="forgotForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item prop="email" label="邮箱">
          <el-input v-model="forgotForm.email" placeholder="请输入注册邮箱" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="forgotPasswordVisible = false">取消</el-button>
          <el-button type="primary" :loading="resetLoading" @click="handleResetPassword">
            发送重置链接
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { User, Lock } from '@element-plus/icons-vue';
import { useAdminStore } from '@/stores/back/adminStore';
import { TokenManager, storageConfig } from '@/utils/axios';

const router = useRouter();
const adminStore = useAdminStore();
const loginFormRef = ref(null);
const forgotFormRef = ref(null);
const loading = ref(false);
const resetLoading = ref(false);
const passwordVisible = ref(false);
const forgotPasswordVisible = ref(false);
const currentYear = computed(() => new Date().getFullYear());

const loginForm = reactive({
  username: '',//用户名
  password: '',//密码
  roleId: 4//角色id
});
const remember = ref(true);

const forgotForm = reactive({
  username: '',
  email: ''
});

const onRoleChange = (value) => {
  console.log(value);
  loginForm.roleId = parseInt(value, 10);
};

const loginRules = {
  username: [{ required: true, trigger: 'blur', message: '请输入用户名' }],
  password: [{ required: true, trigger: 'blur', message: '请输入密码' }]
};

const forgotRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
};

// 获取角色名称
const getRoleName = (roleId) => {
  const roleMap = {
    1: '老人'|'elder',
    2: '家属'|'kin',
    3: '社区工作人员'|'staff',
    4: '管理员'|'admin',
    5: '访客'|'visitor'
  };
  return roleMap[roleId] || '未知角色';
};
//
const getRoleId = (roleName) => {
  const roleMap = {
    '老人': 1,
    '家属': 2,
    '社区工作人员': 3,
    '管理员': 4,
    '访客': 5
  };
  return roleMap[roleName] || 4;
};
// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return;
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) {
      return false;
    }
    try {
      loading.value = true;
      if(typeof loginForm.roleId === 'string'){
        loginForm.roleId = getRoleId(loginForm.roleId);
      }
      const res = await adminStore.login(loginForm);
      if (res.code === 200) {
        ElMessage.success('登录成功');
        
        // 使用 TokenManager 存储 token
        TokenManager.admin.set(res.data.accessToken, res.data.refreshToken);
        
        // 使用 storageConfig 存储用户角色
        const storage = storageConfig.getStorage(storageConfig.admin);
        storage.setItem("userRole", loginForm.roleId === 4 ? "admin" : "staff");
        
        if (remember.value) {
          storage.setItem('rememberedUsername', loginForm.username);
          storage.setItem('rememberRoleType', loginForm.roleId);
          storage.setItem('rememberedRoleId', loginForm.roleId);
        } else {
          storage.removeItem('rememberedUsername');
          storage.removeItem('rememberedRole');
        }
        
        // 直接跳转，不使用 setTimeout
        router.push('/admin/home');
      }
    } catch (error) {
      console.error('登录失败:', error);
      ElMessage.error(error.message || '登录失败，请稍后重试');
    } finally {
      loading.value = false;
    }
  });
};

// 处理忘记密码
const handleForgotPassword = () => {
  forgotPasswordVisible.value = true;
};

// 处理重置密码
const handleResetPassword = async () => {
  if (!forgotFormRef.value) return;

  await forgotFormRef.value.validate(async (valid) => {
    if (!valid) {
      return false;
    }

    try {
      resetLoading.value = true;
      await new Promise(resolve => setTimeout(resolve, 1500));
      ElMessage.success('重置密码链接已发送到您的邮箱，请查收');
      forgotPasswordVisible.value = false;
    } catch (error) {
      console.error('重置密码失败:', error);
      ElMessage.error('重置密码失败，请稍后重试');
    } finally {
      resetLoading.value = false;
    }
  });
};

onMounted(() => {
  const storage = storageConfig.getStorage(storageConfig.admin);
  const rememberedUsername = storage.getItem('rememberedUsername');
  const rememberedRole = storage.getItem('rememberedRole');

  if (rememberedUsername) {
    loginForm.username = rememberedUsername;
    remember.value = true;
  }

  if (rememberedRole) {
    loginForm.roleId = rememberedRole;
  }
});
</script>



<style scoped>
.admin-login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
}

.login-content {
  width: 100%;
  max-width: 450px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo {
  width: 80px;
  height: 80px;
  margin-bottom: 16px;
  filter: drop-shadow(0 4px 6px rgba(0, 0, 0, 0.1));
}

.title {
  font-size: 28px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 8px;
}

.subtitle {
  font-size: 16px;
  color: #606266;
  margin: 0;
}

.login-card {
  width: 100%;
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.login-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
}

.card-header {
  text-align: center;
  padding: 10px 0;
}

.form-title {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
  color: #2c3e50;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.role-select {
  width: 100%;
}

.login-button {
  width: 100%;
  padding: 12px 20px;
  font-size: 16px;
  transition: all 0.3s ease;
}

.login-footer {
  margin-top: 20px;
  text-align: center;
  color: #606266;
  font-size: 14px;
}

/* 响应式调整 */
@media (max-width: 576px) {
  .login-content {
    max-width: 100%;
  }

  .title {
    font-size: 24px;
  }

  .subtitle {
    font-size: 14px;
  }
}

/* 动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-content {
  animation: fadeIn 0.6s ease-out;
}
</style>