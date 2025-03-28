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
import { ref, watch, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/fore/useUserStore';
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
const loading = ref(false);
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
      loading.value = true;
      try {
        const response = await userStore.login(loginForm.value);
        console.log("登录接口在view中返回", response);
        //如果接口返回数据成功，view中是code
        if (response.code === 200&&response.data&&response.message) {
          console.log("前台登录页面登录成功返回", response.data);
          // 存储用户角色
          localStorage.setItem("userRole", loginForm.value.roleId === 1 ? "elder" : "kin");
          console.log("跳转到首页");
          router.push("/home");
        }else if(response.code==401){
          ElMessage.error(response.message);
          console.log("登录失败，跳转到登录页面");
          router.push("/login");
        }else{
          ElMessage.error(response.message);
        }
      } catch (err) {
        console.error("登录错误", err);
      } finally {
        loading.value = false;
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