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
          <el-tab-pane v-if="isElder" label="健康信息" name="health">
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
              <el-col :span="12">
                <el-form-item label="头像">
                  <el-upload
                    class="avatar-uploader"
                    action="/api/user/avatar"
                    :show-file-list="false"
                    :on-success="handleAvatarSuccess"
                    :before-upload="beforeAvatarUpload"
                    :headers="uploadHeaders"
                  >
                    <img v-if="profileForm.avatar" :src="profileForm.avatar" class="avatar" />
                    <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                  </el-upload>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="个人简介">
                  <el-input
                    v-model="profileForm.remark"
                    type="textarea"
                    :rows="3"
                    placeholder="请输入个人简介"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 根据角色显示不同的表单项 -->
            <template v-if="isElder">
              <el-divider content-position="left">绑定家属信息</el-divider>
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="绑定家属">
                    <el-select v-model="profileForm.bindKinIds" placeholder="请选择绑定家属" class="full-width" multiple>
                      <el-option
                        v-for="kin in kinList"
                        :key="kin.id"
                        :label="kin.name"
                        :value="kin.id"
                      />
                    </el-select>
                    <div v-if="!kinList || kinList.length === 0" class="no-data">
                      暂无可绑定的家属
                    </div>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="与家属关系">
                    <el-select v-model="profileForm.relationType" placeholder="请选择与家属关系" class="full-width">
                      <el-option label="子女" value="子女" />
                      <el-option label="配偶" value="配偶" />
                      <el-option label="兄弟姐妹" value="兄弟姐妹" />
                      <el-option label="其他亲属" value="其他亲属" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
            </template>

            <template v-if="isKin">
              <el-divider content-position="left">绑定老人信息</el-divider>
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="绑定老人">
                    <el-select v-model="profileForm.bindElderIds" placeholder="请选择绑定老人" class="full-width" multiple>
                      <el-option
                        v-for="elder in elderList"
                        :key="elder.id"
                        :label="elder.name"
                        :value="elder.id"
                      />
                    </el-select>
                    <div v-if="!elderList || elderList.length === 0" class="no-data">
                      暂无可绑定的老人
                    </div>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="与老人关系">
                    <el-select v-model="profileForm.relationType" placeholder="请选择与老人关系" class="full-width">
                      <el-option label="子女" value="子女" />
                      <el-option label="配偶" value="配偶" />
                      <el-option label="兄弟姐妹" value="兄弟姐妹" />
                      <el-option label="其他亲属" value="其他亲属" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
            </template>
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
import { UserFilled, Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/fore/useUserStore';
import { TokenManager } from '@/utils/axios';

const userStore = useUserStore();

// 当前激活的标签页
const activeTab = ref('basic');

// 头像上传配置
const uploadHeaders = computed(() => ({
  'Authorization': `Bearer ${TokenManager.user.getAccessToken()}`
}));

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

// 获取用户角色
const userRole = computed(() => {
  return userStore.roles?.[0];
});

// 判断是否为老人角色
const isElder = computed(() => userRole.value === 'elder');
const isKin = computed(() => userRole.value === 'kin');

// 初始化老人姓名
const elderName = ref('未绑定老人');

// 个人信息表单
const profileForm = ref({
  id: '',//userId
  name: '',//姓名
  gender: '',//性别
  birthday: '',//出生日期
  idCard: '',//身份证号
  phone: '',//联系电话
  address: '',//地址
  email: '',//邮箱
  emergencyContactName: '',//紧急联系人
  emergencyContactPhone: '',//紧急联系人电话
  healthCondition: '',//健康状况
  avatar: '',//头像
  remark: '',//备注
  age: 0,//年龄
  department: '',//部门
  position: '',//职位
  isActive: 1,//是否激活
  roleIds: [],//角色ID列表
  roles: [],//角色列表
  roleNames: [],//角色名称列表
  bindElderIds: null,//绑定的老人ID列表
  bindKinIds: null,//绑定的家属ID列表
  relationType: null//关系类型
})

// 编辑模式
const isEditMode = ref(false)
const originalData = ref({})

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

    // 填充表单数据
    profileForm.value = {
      id: userData.userId || '',
      name: userData.name || '',
      gender: userData.gender || '',
      birthday: userData.birthday || '',
      idCard: userData.idCard || '',
      phone: userData.phone || '',
      address: userData.address || '',
      email: userData.email || '',
      emergencyContactName: userData.emergencyContactName || '',
      emergencyContactPhone: userData.emergencyContactPhone || '',
      healthCondition: userData.healthCondition || '',
      avatar: userData.avatar || '',
      remark: userData.remark || '',
      age: userData.age || 0,
      department: userData.department || '',
      position: userData.position || '',
      isActive: userData.isActive || 1,
      roleIds: userData.roleIds || [],
      roles: userData.roles || [],
      roleNames: userData.roleNames || [],
      bindElderIds: userData.bindElderIds || null,
      bindKinIds: userData.bindKinIds || null,
      relationType: userData.relationType || null
    };

    // 如果是家属并且有绑定老人，获取老人信息
    if (isKin.value && userData.bindElderIds && userData.bindElderIds.length > 0) {
      try {
        const elderResponse = await userStore.getElderInfo(userData.bindElderIds[0]);
        if (elderResponse && elderResponse.data) {
          elderName.value = elderResponse.data.name || '未知老人';
        }
      } catch (error) {
        console.error('获取老人信息失败:', error);
      }
    }

    // 保存原始数据，用于取消编辑
    originalData.value = { ...profileForm.value };

    // 根据角色获取对应的绑定列表
    if (isElder.value) {
      await getKinList();
    } else if (isKin.value) {
      await getElderList();
    }

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

// 获取未绑定的家属列表
const kinList = ref([]);
const getKinList = async () => {
  try {
    const response = await userStore.fetchKinListByElderId(profileForm.value.id);
    kinList.value = response || [];
    console.log('获取到的家属列表:', kinList.value);
  } catch (error) {
    console.error('获取家属列表失败:', error);
    ElMessage.error('获取家属列表失败');
  }
};

// 获取未绑定的老人列表
const elderList = ref([]);
const getElderList = async () => {
  try {
    const response = await userStore.fetchUnboundElders();
    elderList.value = response || [];
    console.log('获取到的老人列表:', elderList.value);
  } catch (error) {
    console.error('获取老人列表失败:', error);
    ElMessage.error('获取老人列表失败');
  }
};

// 添加头像上传前的验证方法
const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/gif';
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isJPG) {
    ElMessage.error('上传头像图片只能是 JPG/PNG/GIF 格式!');
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!');
  }
  return isJPG && isLt2M;
};

// 添加样式
const avatarStyle = {
  width: '100px',
  height: '100px',
  display: 'block'
};

// 组件挂载时初始化数据
onMounted(async () => {
  await initFormData();
});

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
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 178px;
  height: 178px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar-uploader:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: cover;
}

.upload-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

.no-data {
  margin-top: 8px;
  color: #909399;
  font-size: 14px;
  text-align: center;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}
</style>
