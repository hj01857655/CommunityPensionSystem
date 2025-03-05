<template>
  <div class="profile-view">
    <el-card class="content-card" shadow="hover">
      <h3>个人信息</h3>
      <el-form :model="profileForm" label-width="120px">
        <el-form-item label="姓名">
          <el-input v-model="profileForm.name" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="profileForm.gender" placeholder="请选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="出生日期">
          <el-date-picker v-model="profileForm.birthday" type="date" placeholder="请选择出生日期" />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="profileForm.idCard" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="profileForm.phone" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="profileForm.address" />
        </el-form-item>
        <el-form-item label="紧急联系人">
          <el-input v-model="profileForm.emergencyContact" />
        </el-form-item>
        <el-form-item label="紧急联系人电话">
          <el-input v-model="profileForm.emergencyPhone" />
        </el-form-item>
        <el-form-item label="健康状况">
          <el-input v-model="profileForm.healthCondition" />
        </el-form-item>
        <el-form-item label="既往病史">
          <el-input v-model="profileForm.medicalHistory" />
        </el-form-item>
        <el-form-item label="过敏史">
          <el-input v-model="profileForm.allergy" />
        </el-form-item>
        <el-form-item label="头像">
          <el-upload
            action="https://jsonplaceholder.typicode.com/posts/"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
          >
            <el-avatar :size="100" :src="profileForm.avatar" />
          </el-upload>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="profileForm.remark" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveProfile">保存</el-button>
        </el-form-item>
      </el-form>    
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const profileForm = ref({
  name: '',
  gender: '',   
  birthday: '',
  idCard: '',
  phone: '',
  address: '',
  emergencyContact: '',
  emergencyPhone: '',
  healthCondition: '',
  medicalHistory: '',
  allergy: '',
  avatar: '',
  remark: ''
})  
onMounted(async()=>{
  
    
})
const formatDate = (date) => {
  return dayjs(date).format('MM-DD');
};
const handleAvatarSuccess = (res, file) => {
  profileForm.value.avatar = URL.createObjectURL(file.raw)
}

const saveProfile = async () => {
  try {
    const res = await axios.post('/api/user/update', profileForm.value)
    if (res.data.code === 1) {
      ElMessage.success('个人信息更新成功')
    } else {
      ElMessage.error(res.data.msg || '更新失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '更新失败')
  }
}
</script>   


