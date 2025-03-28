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
                  <el-date-picker v-model="profileForm.birthday" :disabled="!isEditMode" type="date"
                    value-format="YYYY-MM-DD" style="width: 100%" />
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
                  <el-input v-model="profileForm.emergencyContactName" :disabled="!isEditMode" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="紧急联系人电话">
                  <el-input v-model="profileForm.emergencyContactPhone" :disabled="!isEditMode" />
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
            <!-- 老人角色信息 -->
            <template v-if="isElder">
              <el-divider content-position="left">已绑定家属</el-divider>
              <el-row style="margin-bottom: 20px">
                <el-col :span="24">
                  <el-button type="primary" @click="showBindDialog = true">绑定新家属</el-button>
                </el-col>
              </el-row>
              <el-table :data="familyMembers" border stripe>
                <el-table-column prop="name" label="姓名" />
                <el-table-column prop="relationship" label="关系" />
                <el-table-column prop="phone" label="联系电话" />
                <el-table-column label="操作" width="120">
                  <template #default="scope">
                    <el-button type="danger" link @click="handleUnbindFamily(scope.row)">
                      解除绑定
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </template>

            <template v-else-if="isKin">
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="关联老人">
                    <el-input v-model="elderName" disabled />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="与老人关系">
                    <el-input v-model="profileForm.relationship" :disabled="!isEditMode" />
                  </el-form-item>
                </el-col>
              </el-row>
            </template>

            <!-- 头像和备注部分 -->
            <el-divider />
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="头像">
                  <el-upload class="avatar-uploader" action="/api/user/avatar" :show-file-list="false"
                    :on-success="handleAvatarSuccess" :disabled="!isEditMode">
                    <el-avatar :size="100" :src="profileForm.avatar">
                      <el-icon>
                        <UserFilled />
                      </el-icon>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/fore/useUserStore';

const userStore = useUserStore();

// 绑定家属对话框相关状态
const showBindDialog = ref(false);
const bindForm = ref({
  elderId: '',
  relationType: ''
});

// 获取未绑定家属的老人列表
const unboundElders = ref([]);

// 打开绑定对话框
const openBindDialog = async () => {
  try {
    unboundElders.value = await userStore.fetchUnboundElders();
    showBindDialog.value = true;
  } catch (error) {
    ElMessage.error('获取老人列表失败');
  }
};

// 处理绑定操作
const handleBindFamily = async () => {
  try {
    const success = await userStore.bindElderKin(
      bindForm.value.elderId, 
      userStore.userInfo.id, 
      bindForm.value.relationType
    );
    if (success) {
      ElMessage.success('绑定成功');
      showBindDialog.value = false;
      // 刷新家属列表
      familyMembers.value = await userStore.fetchKinListByElderId(userStore.userInfo.id);
    }
  } catch (error) {
    ElMessage.error('绑定失败');
  }
};

// 处理解绑操作
const handleUnbindFamily = async (kin) => {
  try {
    const success = await userStore.unbindElderKin(userStore.userInfo.id, kin.id);
    if (success) {
      ElMessage.success('解绑成功');
      // 刷新家属列表
      familyMembers.value = await userStore.fetchKinListByElderId(userStore.userInfo.id);
    }
  } catch (error) {
    ElMessage.error('解绑失败');
  }
};

// 判断是否为老人角色
const isElder = computed(() => userStore.roles && userStore.roles.includes('elder'));
const isKin = computed(() => userStore.roles && userStore.roles.includes('kin'));

// 初始化老人姓名
const elderName = ref('未绑定老人');

// 个人信息表单
const profileForm = ref({
  id: '',//id
  name: '',//姓名
  gender: '',//性别
  birthday: '',//出生日期
  idCard: '',//身份证号
  phone: '',//联系电话
  address: '',//地址
  emergencyContactName: '',//紧急联系人
  emergencyContactPhone: '',//紧急联系人电话
  healthCondition: '',//健康状况
  medicalHistory: '',//既往病史
  allergy: '',//过敏史
  avatar: '',//头像
  remark: '',//备注
  relationship: '',//与老人关系
  elderId: null //绑定的老人ID
})

// 编辑模式
const isEditMode = ref(false)
const originalData = ref({})
// 当前激活的标签页
const activeTab = ref('basic')

const handleAvatarSuccess = (response) => {
  if (response && response.code === 200) {
    // 更新头像URL
    profileForm.value.avatar = response.data;
    ElMessage.success('头像上传成功');
  } else {
    ElMessage.error('头像上传失败');
  }
}

// 切换编辑模式
const toggleEditMode = () => {
  isEditMode.value = !isEditMode.value
  if (!isEditMode.value) {
    profileForm.value = { ...originalData.value }
  }
}

// 初始化表单数据
// 添加家属列表数据
const familyMembers = ref([])

// 修改 initFormData 方法
const initFormData = async () => {
  try {
    const response = await userStore.getUserInfo();
    if (!response || !response.data) {
      ElMessage.error('获取个人信息失败');
      return;
    }

    const userData = response.data;

    // 填充基本表单数据
    profileForm.value = {
      id: userData.userId || '',
      name: userData.name || '',
      gender: userData.gender || '',
      birthday: userData.birthday || '',
      idCard: userData.idCard || '',
      phone: userData.phone || '',
      address: userData.address || '',
      emergencyContactName: userData.emergencyContactName || '',
      emergencyContactPhone: userData.emergencyContactPhone || '',
      healthCondition: userData.healthCondition || '',
      medicalHistory: userData.medicalHistory || '',
      allergy: userData.allergy || '',
      avatar: userData.avatar || '',
      remark: userData.remark || '',
      relationship: userData.relationship || '',
      elderId: userData.elderId || null
    };

    // 如果是家属并且有绑定老人，获取老人信息
    if (isKin.value && userData.elderId) {
      try {
        const elderResponse = await userStore.getElderInfo(userData.elderId);
        if (elderResponse && elderResponse.data) {
          elderName.value = elderResponse.data.name || '未知老人';
        }
      } catch (error) {
        console.error('获取老人信息失败:', error);
      }
    }

    // 保存原始数据，用于取消编辑
    originalData.value = { ...profileForm.value };

  } catch (error) {
    console.error('初始化表单数据失败:', error);
    ElMessage.error('获取个人信息失败');
  }
}

// 保存个人信息
const saveProfile = async () => {
  try {
    await ElMessageBox.confirm('确认保存修改的信息吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const success = await userStore.handleUpdateUserInfo(profileForm.value);
    console.log(success);
    if (success) {
      ElMessage.success('保存成功');
      isEditMode.value = false;
      originalData.value = { ...profileForm.value };

      // 重新获取用户信息，确保数据同步
      await initFormData();
    } else {
      ElMessage.error('保存失败');
    }
  } catch (error) {
    if (error === 'cancel') {
      return
    }
    ElMessage.error('保存失败')
  }
}

onMounted(() => {
  initFormData();
})



// 添加绑定家属方法
const isDialogVisible = ref(false);
const familyForm = ref({
  name: '',
  relationship: '',
  phone: ''
});



const confirmBindFamily = async () => {
  try {
    const response = await userStore.bindFamily(familyForm.value);
    if (response && response.code === 200) {
      ElMessage.success('绑定家属成功');
      isDialogVisible.value = false;
      await initFormData();
    } else {
      ElMessage.error('绑定家属失败');
    }
  } catch (error) {
    ElMessage.error('绑定家属失败');
  }
};

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
