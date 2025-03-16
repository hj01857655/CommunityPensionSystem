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
          
          <!-- 健康信息标签页（仅老人可见） -->
          <el-tab-pane label="健康信息" name="health" v-if="isElder">
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
            <!-- 家属特有信息 -->
            <template v-if="!isElder">
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="关联老人">
                    <el-input v-model="userStore.elderInfo?.name" disabled />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="与老人关系">
                    <el-input v-model="userStore.kinInfo?.relationship" :disabled="!isEditMode" />
                  </el-form-item>
                </el-col>
              </el-row>
            </template>
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
import { onMounted, ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/fore/useUserStore';
const userStore = useUserStore();

// 判断是否为老人角色
const isElder = computed(() => userStore.roleId === 3);

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
// 初始化表单数据
const initFormData = () => {
  if (isElder.value) {
    // 老人角色，显示完整信息
    profileForm.value = {
      ...userStore.userInfo,
      ...userStore.elderInfo
    }
  } else {
    // 家属角色，显示基本信息
    profileForm.value = {
      ...userStore.userInfo,
      ...userStore.kinInfo
    }
  }
}

// 保存个人信息
const saveProfile = async () => {
  try {
    // TODO: 调用保存接口
    ElMessage.success('保存成功')
    isEditMode.value = false
    originalData.value = { ...profileForm.value }
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

onMounted(() => {
  initFormData()
  originalData.value = { ...profileForm.value }
})

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


