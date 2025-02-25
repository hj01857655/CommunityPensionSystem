<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="title">社区养老系统登录</h2>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        @submit.prevent="submitForm"
        @keyup.enter="submitForm"
      >
        <el-form-item prop="role">
          <el-select v-model="loginForm.role" placeholder="请选择角色" class="full-width" @change="onRoleChange">
            <el-option label="老人" value="elder" />
            <el-option label="老人家属" value="family" />
            <el-option label="社区工作人员" value="staff" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            clearable
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>
        <el-form-item prop="captcha" v-if="loginForm.role !== 'elder'">
          <div class="captcha-container">
            <el-input
              v-model="loginForm.captcha"
              placeholder="请输入验证码"
              prefix-icon="Check"
              clearable
              class="captcha-input"
            />
            <div class="captcha-box" @click="refreshCaptcha">
              <span>{{ captchaText }}</span>
            </div>
          </div>
        </el-form-item>
        <el-form-item class="extra-options">
          <el-checkbox
            v-model="rememberMe"
            :disabled="loginForm.role === 'elder'"
          >
            记住密码
          </el-checkbox>
          <a href="#" class="forgot-password" @click.prevent="forgotPassword">忘记密码？</a>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            :disabled="loading"
            class="full-width"
            @click="submitForm"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';

// 表单数据
const loginForm = ref({
  role: '',
  username: '',
  password: '',
  captcha: '',
});

// 表单引用
const loginFormRef = ref();

// 验证码相关
const captchaText = ref(generateCaptcha());

// 记住密码状态
const rememberMe = ref(false);

// 登录加载状态
const loading = ref(false);

// 路由
const router = useRouter();

// 表单校验规则
const loginRules = ref({
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' },
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value.toUpperCase() !== captchaText.value) {
          callback(new Error('验证码错误'));
        } else {
          callback();
        }
      },
      trigger: 'blur',
    },
  ],
});

// 生成随机验证码
function generateCaptcha() {
  const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789'; // 去掉易混淆字符
  let result = '';
  for (let i = 0; i < 4; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return result;
}

// 刷新验证码
const refreshCaptcha = () => {
  captchaText.value = generateCaptcha();
  loginForm.value.captcha = '';
};

// 角色变化时调整逻辑
const onRoleChange = (role) => {
  if (role === 'elder') {
    rememberMe.value = true; // 老人默认记住密码
    loginForm.value.captcha = ''; // 清空验证码
    // 移除验证码校验规则
    loginRules.value.captcha = [];
  } else {
    rememberMe.value = false; // 其他角色默认不记住
    loginRules.value.captcha = [
      { required: true, message: '请输入验证码', trigger: 'blur' },
      {
        validator: (rule, value, callback) => {
          if (value.toUpperCase() !== captchaText.value) {
            callback(new Error('验证码错误'));
          } else {
            callback();
          }
        },
        trigger: 'blur',
      },
    ];
    refreshCaptcha(); // 刷新验证码
  }
};

// 提交表单
const submitForm = () => {
  if (loading.value) return;
  loginFormRef.value.validate((valid) => {
    if (valid) {
      loading.value = true;
      setTimeout(() => {
        loading.value = false;
        ElMessage.success('登录成功');
        // 存储认证信息
        localStorage.setItem('token', 'mock-token');
        localStorage.setItem('userRole', loginForm.value.role);
        localStorage.setItem('userName', loginForm.value.username);
        localStorage.setItem('userId', 'mock-user-id'); // 模拟 userId，实际应从后端获取
        if (rememberMe.value) {
          localStorage.setItem('rememberedPassword', loginForm.value.password);
        } else {
          localStorage.removeItem('rememberedPassword');
        }
        const redirect = router.currentRoute.value.query.redirect || '/';
        router.push(redirect);
      }, 1000);
    } else {
      ElMessage.error('请填写完整信息或检查验证码');
    }
  });
};

// 忘记密码
const forgotPassword = () => {
  ElMessage.info('请联系管理员重置密码');
};

// 监听角色变化并初始化记住密码
watch(() => loginForm.value.role, (newRole) => {
  onRoleChange(newRole);
});

// 初始化时检查是否有记住的密码
const initializeForm = () => {
  const rememberedPassword = localStorage.getItem('rememberedPassword');
  if (rememberedPassword) {
    loginForm.value.password = rememberedPassword;
    rememberMe.value = true;
  }
};
initializeForm();
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: url('@/assets/login.png') no-repeat center center fixed;
  background-size: cover;
}

.login-box {
  width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(8px);
}

.title {
  text-align: center;
  font-size: 28px;
  color: #2c3e50;
  margin-bottom: 35px;
  font-weight: 600;
}

.full-width {
  width: 100%;
}

.el-form-item {
  margin-bottom: 24px;
}

.captcha-container {
  display: flex;
  align-items: center;
  gap: 12px;
}

.captcha-input {
  flex: 1;
}

.captcha-box {
  width: 110px;
  height: 40px;
  background: #f5f7fa;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 18px;
  font-weight: bold;
  color: #606266;
  user-select: none;
  transition: background 0.3s;
}

.captcha-box:hover {
  background: #e8ecef;
}

.extra-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 0 10px;
}

.forgot-password {
  font-size: 14px;
  color: #409eff;
  text-decoration: none;
}

.forgot-password:hover {
  text-decoration: underline;
}

.el-button {
  height: 46px;
  font-size: 16px;
  border-radius: 8px;
  transition: all 0.3s;
}

.el-button:hover {
  opacity: 0.9;
}

.el-input :deep(.el-input__wrapper) {
  border-radius: 6px;
  transition: all 0.3s;
}

.el-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 5px rgba(64, 158, 255, 0.5);
}
</style>