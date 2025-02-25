<template>
  <div class="login-container">
    <el-form ref="loginFormRef" :model="loginForm" :rules="rules" label-width="100px" class="login-form">
      <h2>社区养老系统后台管理</h2>
      <el-form-item label="用户名" prop="username">
        <el-input v-model="loginForm.username" placeholder="请输入用户名"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="loginForm.password" type="password" placeholder="请输入密码"></el-input>
      </el-form-item>
      <el-form-item label="角色" prop="role">
        <el-select v-model="loginForm.role" placeholder="请选择角色">
          <el-option label="老人" value="elder"></el-option>
          <el-option label="老人家属" value="member"></el-option>
          <el-option label="社区工作人员" value="staff"></el-option>
          <el-option label="管理员" value="admin"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="login" class="login-button">登录</el-button>
      </el-form-item>
      <el-form-item class="link-item">
        <el-link type="primary" :underline="false" href="/register">注册</el-link>
        <el-divider direction="vertical" />
        <el-link type="primary" :underline="false" href="/forgetPassword">忘记密码</el-link>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import axios from 'axios';

const loginForm = reactive({
  username: '',
  password: '',
  role: ''
});

const rules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 10, message: '用户名长度在3-10个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码长度在6-16个字符之间', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
});

const loginFormRef = ref(null);
const router = useRouter();

const login = () => {
  loginFormRef.value.validate((valid) => {
    if (valid) {
      axios.post('http://localhost:3000/login', loginForm)
        .then(response => {
          if (response.data.code === 1) {
            ElMessage.success('登录成功!');
            localStorage.setItem('token', response.data.token);
            localStorage.setItem('userRole', loginForm.role);
            localStorage.setItem('userName', loginForm.username);
            router.push('/');
          } else {
            ElMessage.error(response.data.msg || '登录失败!');
          }
        });
    } else {
      console.log('error submit!');
      return false;
    }
  });
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
  background-image: url('https://i.pinimg.com/564x/fa/31/33/fa31338cb516ad9ee955eea01bc6b387.jpg');
  background-size: cover;
  background-position: center;
}

.login-form {
  width: 350px;
  padding: 30px;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 10px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  text-align: center;
}

h2 {
  margin-bottom: 20px;
  color: #333;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.login-button {
  width: 100%;
  background-color: #409eff;
  border-color: #409eff;
  color: #fff;
  font-weight: bold;
}

.login-button:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

.link-item {
  display: flex;
  justify-content: center;
  align-items: center;
}

.link-item .el-link {
  margin: 0 5px;
}
</style> 