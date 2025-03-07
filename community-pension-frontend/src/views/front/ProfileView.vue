<template>
  <div class="profile-view">
    <el-card class="content-card" shadow="hover">
      <h3>个人信息</h3>
      <el-form :model="profileForm" label-width="120px">
        <el-form-item label="ID" style="display: none;">
          <el-input v-model="profileForm.id" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="profileForm.name" :disabled="!isEditMode" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="profileForm.gender" :disabled="!isEditMode">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="出生日期">
          <el-date-picker v-model="profileForm.birthday" :disabled="!isEditMode" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="toggleEditMode" v-if="!isEditMode">编辑</el-button>
          <el-button type="success" @click="saveProfile" v-if="isEditMode">保存</el-button>
          <el-button @click="toggleEditMode" v-if="isEditMode">取消</el-button>
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="profileForm.idCard" :disabled="!isEditMode" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="profileForm.phone" :disabled="!isEditMode" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="profileForm.address" :disabled="!isEditMode" />
        </el-form-item>
        <el-form-item label="紧急联系人">
          <el-input v-model="profileForm.emergencyContact" :disabled="!isEditMode" />
        </el-form-item>
        <el-form-item label="紧急联系人电话">
          <el-input v-model="profileForm.emergencyPhone" :disabled="!isEditMode" />
        </el-form-item>
        <el-form-item label="健康状况">
          <el-input v-model="profileForm.healthCondition" :disabled="!isEditMode" />
        </el-form-item>
        <el-form-item label="既往病史">
          <el-input v-model="profileForm.medicalHistory" :disabled="!isEditMode" />
        </el-form-item>
        <el-form-item label="过敏史">
          <el-input v-model="profileForm.allergy" :disabled="!isEditMode" />
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
          <el-input v-model="profileForm.remark" type="textarea" :rows="4" :disabled="!isEditMode" />
        </el-form-item>
      </el-form>    
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { getUserInfo } from '@/api/user'
// 个人信息表单
const profileForm = ref({})  
const isEditMode = ref(false)
const originalData = ref({})

// 切换编辑模式
const toggleEditMode = () => {
  isEditMode.value = !isEditMode.value
  if (!isEditMode.value) {
    profileForm.value = {...originalData.value}
  }
}
onMounted(async () => {
  getUserProfile()
})
// 获取用户信息
const getUserProfile= async () => {
  const roleId = JSON.parse(localStorage.getItem('userInfo')).roleId
  const res = await getUserInfo(roleId)
  console.log(res.data.data.elder)
  const userInfoData = res.data.data.elder
  profileForm.value ={
    ...userInfoData,
    id: userInfoData.id,
    name: userInfoData.name,
    gender: userInfoData.gender,
    birthday: userInfoData.birthday,
    idCard: userInfoData.idCard,
    phone: userInfoData.phone,
    address: userInfoData.address,
    emergencyContact: userInfoData.emergencyContactName,
    emergencyPhone: userInfoData.emergencyContactPhone,
    healthCondition: userInfoData.healthCondition,
    medicalHistory: userInfoData.healthRecords.medicalHistory,
    allergy: userInfoData.healthRecords.allergy,
    avatar: computed(() => {
      if (userInfoData.avatar) {
        const avatarUrl=require('@/assets/'+userInfoData.avatar)
        return avatarUrl
      } else {
        return 'https://cube.elemecdn.com/0/88/03b0d39583f487bce08eb6b7e3e16045183.png'
      }
    }),
    remark: userInfoData.remark
  }
  originalData.value = {...userInfoData}
}

// 保存个人信息
const saveProfile = async () => {
  try {
    const res = await axios.post('/api/user/update', profileForm.value)
    if (res.data.code === 1) {
      ElMessage.success('个人信息更新成功')
      originalData.value = {...profileForm.value}
      isEditMode.value = false
    } else {
      ElMessage.error(res.data.msg || '更新失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '更新失败')
  }
}
</script>


