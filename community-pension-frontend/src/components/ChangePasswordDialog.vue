<template>
  <el-dialog v-model:visible="visible" title="修改密码">
    <el-form>
      <el-form-item label="旧密码">
        <el-input v-model="oldPassword" type="password"></el-input>
      </el-form-item>
      <el-form-item label="新密码">
        <el-input v-model="newPassword" type="password"></el-input>
      </el-form-item>
      <el-form-item label="确认新密码">
        <el-input v-model="confirmPassword" type="password"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="changePassword">确认</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'

const visible = ref(false)
const oldPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')

const changePassword = () => {
  // 处理修改密码逻辑
  if (newPassword.value !== confirmPassword.value) {
    alert('新密码和确认密码不一致')
    return
  }
  // 调用API进行密码修改
  visible.value = false
}

window.addEventListener('show-change-password', () => {
  visible.value = true
})
</script>

<style scoped>
/* 样式 */
</style> 