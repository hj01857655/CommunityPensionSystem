<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="title">社区养老系统登录</h2>
      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" @submit.prevent="submitForm"
        @keyup.enter="submitForm">
        <el-form-item prop="roleId">
          <el-select v-model="loginForm.roleId" placeholder="请选择角色" class="full-width" @change="onRoleChange">
            <el-option label="老人" :value="1" />
            <el-option label="老人家属" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名/手机号/身份证号" clearable />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password
            clearable />
        </el-form-item>
        <el-form-item class="extra-options">
          <el-checkbox v-model="rememberMe" :disabled="loginForm.roleId === 1">
            记住密码
          </el-checkbox>
          <a href="#" class="forgot-password" @click.prevent="forgotPassword">忘记密码？</a>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" :disabled="loading" class="full-width" @click="submitForm">
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { useUserStore } from '@/stores/fore/userStore';
import { ElMessage } from 'element-plus';
import { computed, onMounted, ref, watch } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const userStore = useUserStore();

// 登录表单
const loginForm = ref({
  username: '',
  password: '',
  roleId: 1, // 设置默认选择角色为老人
});
// 表单引用
const loginFormRef = ref();
// 记住密码状态
const rememberMe = ref(false);
// 登录加载状态
const loading = computed(() => userStore.loading);
// 表单校验规则
const loginRules = ref({
  roleId: [{ required: true, message: '请选择角色', trigger: 'change' }],
  username: [
    { required: true, message: '请输入用户名/手机号/身份证号', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' },
  ],
});

// 角色变化时调整逻辑
const onRoleChange = () => {
  rememberMe.value = true; // 默认记住密码
};

// 登录
const submitForm = async () => {
  if (loading.value) return;
  loginFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        console.log("开始登录，表单数据：", loginForm.value);
        
        // 确保roleId是数字类型
        const loginData = {
          username: loginForm.value.username,
          password: loginForm.value.password,
          roleId: Number(loginForm.value.roleId)
        };
        console.log("处理后的登录数据：", loginData);
        
        // 使用用户存储的登录方法
        const success = await userStore.login(loginData);

        if (success) {
          // 确认登录状态已设置
          localStorage.setItem('isLoggedIn', 'true');
          
          // 如果选择记住密码，可以在这里处理本地存储
          if (rememberMe.value) {
            localStorage.setItem('rememberedUsername', loginForm.value.username);
            localStorage.setItem('rememberedRoleId', loginForm.value.roleId.toString());
          }
          
          ElMessage.success('登录成功');
          
          console.log("登录成功，用户角色:", userStore.userRole, "登录状态:", userStore.isLoggedIn);
          
          // 强制延迟跳转以确保状态更新
          setTimeout(() => {
            // 再次检查登录状态
            console.log("准备跳转前状态检查 - 登录状态:", userStore.isLoggedIn, "用户角色:", userStore.userRole);
            if (userStore.isLoggedIn) {
              console.log("登录状态有效，准备跳转到首页");
              router.push('/home');
            } else {
              console.warn("登录状态无效，重试...");
              // 强制更新状态并再次尝试
              userStore.setUserInfo(userStore.userInfo);
              setTimeout(() => router.push('/home'), 200);
            }
          }, 500);
        } else {
          ElMessage.error('登录失败，用户名或密码错误');
        }
      } catch (err) {
        console.error("登录过程发生错误：", err);
        ElMessage.error(err.message || "登录失败，请稍后重试");
      }
    }
  });
};

// 忘记密码
const forgotPassword = () => {
  ElMessage.warning('请联系管理员重置密码');
};

// 监听角色变化并初始化记住密码
watch(() => loginForm.value.roleId, onRoleChange);


onMounted(() => {
});
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: url('@/assets/login/login.png') no-repeat center center fixed;
  background-size: cover;
}

.login-box {
  width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
}

.title {
  text-align: center;
  font-size: 32px;
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

.el-form-item label {
  font-size: 18px;
}

.el-input {
  font-size: 18px;
}

.extra-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 0 10px;
  gap: 20px;
  font-size: 16px;
}

.forgot-password {
  font-size: 16px;
  color: #409eff;
  text-decoration: none;
  margin-left: 10px;
}

.forgot-password:hover {
  text-decoration: underline;
}

.el-button {
  height: 46px;
  font-size: 18px;
  border-radius: 8px;
  transition: opacity 0.3s;
}

.el-button:hover {
  opacity: 0.9;
}

.el-input :deep(.el-input__wrapper) {
  border-radius: 6px;
  transition: box-shadow 0.3s;
}

.el-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 5px rgba(64, 158, 255, 0.5);
}
</style>