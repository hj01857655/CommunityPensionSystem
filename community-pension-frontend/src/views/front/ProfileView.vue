<template>
  <div class="profile-view">
    <el-card class="content-card" shadow="hover">
      <h3>个人信息</h3>
      <el-form :model="profileForm" label-width="120px">
        <el-form-item label="ID" style="display: none;">
          <el-input v-model="profileForm.id" />
        </el-form-item>
        
        <!-- 使用标签页分类显示信息 -->
        <el-tabs v-model="activeTab" type="card">
          <!-- 基本信息标签页 -->
          <el-tab-pane label="基本信息" name="basic">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="姓名">
                  <el-input v-model="profileForm.name" :disabled="!isEditMode" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="性别">
                  <el-select v-model="profileForm.gender" :disabled="!isEditMode" style="width: 100%">
                    <el-option label="男" value="男" />
                    <el-option label="女" value="女" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="出生日期">
                  <el-date-picker v-model="profileForm.birthday" :disabled="!isEditMode" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="身份证号">
                  <el-input v-model="profileForm.idCard" :disabled="!isEditMode" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>
          
          <!-- 联系信息标签页 -->
          <el-tab-pane label="联系信息" name="contact">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="联系电话">
                  <el-input v-model="profileForm.phone" :disabled="!isEditMode" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="地址">
                  <el-input v-model="profileForm.address" :disabled="!isEditMode" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="紧急联系人">
                  <el-input v-model="profileForm.emergencyContact" :disabled="!isEditMode" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="紧急联系人电话">
                  <el-input v-model="profileForm.emergencyPhone" :disabled="!isEditMode" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>
          
          <!-- 健康信息标签页 -->
          <el-tab-pane label="健康信息" name="health">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="健康状况">
                  <el-input v-model="profileForm.healthCondition" :disabled="!isEditMode" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="过敏史">
                  <el-input v-model="profileForm.allergy" :disabled="!isEditMode" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="既往病史">
              <el-input v-model="profileForm.medicalHistory" :disabled="!isEditMode" type="textarea" :rows="2" />
            </el-form-item>
          </el-tab-pane>
          
          <!-- 其他信息标签页 -->
          <el-tab-pane label="其他信息" name="other">
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="头像">
                  <el-upload
                    class="avatar-uploader"
                    action="https://jsonplaceholder.typicode.com/posts/"
                    :show-file-list="false"
                    :on-success="handleAvatarSuccess"
                    :disabled="!isEditMode"
                  >
                    <el-avatar :size="100" :src="profileForm.avatar">
                      <el-icon><UserFilled /></el-icon>
                    </el-avatar>
                    <div class="upload-tip" v-if="isEditMode">点击上传头像</div>
                  </el-upload>
                </el-form-item>
              </el-col>
              <el-col :span="16">
                <el-form-item label="备注">
                  <el-input v-model="profileForm.remark" type="textarea" :rows="4" :disabled="!isEditMode" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>
        </el-tabs>
        
        <el-form-item>
          <el-button type="primary" @click="toggleEditMode" v-if="!isEditMode">编辑</el-button>
          <el-button type="success" @click="saveProfile" v-if="isEditMode">保存</el-button>
          <el-button @click="toggleEditMode" v-if="isEditMode">取消</el-button>
        </el-form-item>
      </el-form>    
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'
import axios from 'axios'
import { getUserInfo } from '@/api/user'
// 个人信息表单
const profileForm = ref({
  id: '',//id
  name: '',//姓名
  gender: '',//性别
  birthday: '',//出生日期
  idCard: '',//身份证号
  phone: '',//联系电话
  address: '',//地址
  emergencyContact: '',//紧急联系人
  emergencyPhone: '',//紧急联系人电话
  healthCondition: '',//健康状况
  medicalHistory: '',//既往病史
  allergy: '',//过敏史
  avatar: '',//头像
  remark: '',//备注
})  

// 编辑模式
const isEditMode = ref(false)
const originalData = ref({})
// 当前激活的标签页
const activeTab = ref('basic')
const handleAvatarSuccess = (response) => {
  if (response) {
    // 更新头像URL
    profileForm.value.avatar = response.url
    ElMessage.success('头像上传成功')
  }
}
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
  profileForm.value = {
    ...userInfoData,
    id: userInfoData.id,
    name: userInfoData.name,
    gender: userInfoData.gender === 'male' ? '男' : '女',
    birthday: userInfoData.birthday,
    idCard: userInfoData.idCard,
    phone: userInfoData.phone,
    address: userInfoData.address,
    emergencyContact: userInfoData.emergencyContactName,
    emergencyPhone: userInfoData.emergencyContactPhone,
    healthCondition: userInfoData.healthCondition,
    medicalHistory: userInfoData.medicalHistory || '',
    avatar: userInfoData.avatar ? `http://127.0.0.1:8081/src/assets/${userInfoData.avatar}` : '',
    remark: userInfoData.remark
  }
  originalData.value = {...profileForm.value}
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
      console.error(res.data.msg || '更新失败')
    }
  } catch (error) {
    console.error(error.message || '更新失败')
  }
}
</script>

<style scoped>
.profile-view {
  width: 100%;
}

.content-card {
  margin-bottom: 20px;
}

h3 {
  margin-bottom: 20px;
  color: #2c3e50;
  font-weight: 600;
}

.form-section {
  margin-bottom: 20px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #409EFF;
  margin: 15px 0 5px 0;
}

.avatar-uploader {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.upload-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}
</style>


