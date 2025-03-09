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
              aria-label="角色选择">
              <el-option label="社区工作人员" :value="3" />
              <el-option label="管理员" :value="4" />
            </el-select>
          </el-form-item>

          <div class="login-options">
            <el-checkbox v-model="loginForm.remember" label="记住我" tabindex="4" aria-label="记住我" />
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
        <p>© {{ currentYear }} 社区养老系统 - 版权所有</p>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElNotification } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '@/api/user' // Assuming you have a login function in your user API

const router = useRouter()
const loginFormRef = ref(null)
const forgotFormRef = ref(null)
const loading = ref(false)
const resetLoading = ref(false)
const passwordVisible = ref(false)
const forgotPasswordVisible = ref(false)
const currentYear = computed(() => new Date().getFullYear())

//默认选择管理员角色
const defaultRoleId = ref(4)

// 登录表单

const loginForm = reactive({
  username: '',
  password: '',
  roleId: defaultRoleId.value,
  remember: true
})

// 忘记密码表单
const forgotForm = reactive({
  username: '',
  email: ''
})

// 登录表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于 6 个字符', trigger: 'blur' }
  ],
  roleId: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

// 忘记密码表单验证规则
const forgotRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}
// 处理登录
const handleLogin = async () => {
  // 如果登录表单不存在，则返回
  if (!loginFormRef.value) return
  // 验证登录表单
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) {
      return false
    }
    // 如果验证通过，则进行登录
    try {
      loading.value = true
      //删除密码
      const { remember, ...loginFormData } = loginForm
      
      const response = await login(loginFormData)
      console.log(response)
      if (response.success) {
        // 保存登录状态
        if (remember) {
          localStorage.setItem('rememberedUsername', loginForm.username)
          localStorage.setItem('rememberedRole', loginForm.roleId)
        }
        loading.value = false
        // 跳转到管理后台
        router.push('/admin/analysis/dashboard')
      } else {
        ElMessage.error('用户名、密码或角色错误，请重试')
      }

    } catch (error) {
      console.error('登录失败:', error)
      ElMessage.error('登录失败，请稍后重试')
    } finally {
      loading.value = false
    }
  })
}

// 处理忘记密码
const handleForgotPassword = () => {
  forgotPasswordVisible.value = true
}

// 处理重置密码
const handleResetPassword = async () => {
  if (!forgotFormRef.value) return

  await forgotFormRef.value.validate(async (valid) => {
    if (!valid) {
      return false
    }

    try {
      resetLoading.value = true

      // 模拟API请求
      await new Promise(resolve => setTimeout(resolve, 1500))

      ElMessage.success('重置密码链接已发送到您的邮箱，请查收')
      forgotPasswordVisible.value = false
    } catch (error) {
      console.error('重置密码失败:', error)
      console.error('重置密码失败，请稍后重试')
    } finally {
      resetLoading.value = false
    }
  })
}

// 检查是否有记住的用户名和角色
onMounted(() => {
  // 初始化adminStore
  const adminStore = useAdminStore()
  adminStore.getAdminInfos()
  
  // 仍然从localStorage获取记住的用户名和角色，因为这是登录页面特有的功能
  const rememberedUsername = localStorage.getItem('rememberedUsername')
  const rememberedRole = localStorage.getItem('rememberedRole')

  if (rememberedUsername) {
    loginForm.username = rememberedUsername
    loginForm.remember = true
  }

  if (rememberedRole) {
    loginForm.roleId = rememberedRole
  }
})
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