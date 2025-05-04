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
              <el-divider content-position="left">已绑定家属信息</el-divider>
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="已绑定家属">
                    <template v-if="profileForm.bindKinIds && profileForm.bindKinIds.length > 0">
                      <el-tag
                          v-for="kinId in profileForm.bindKinIds"
                          :key="kinId"
                          :closable="false"
                          class="tag-item"
                      >
                        {{ getKinName(kinId) }}
                      </el-tag>
                    </template>
                    <div v-else class="no-data">
                      暂未绑定家属
                    </div>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="与家属关系">
                    <el-input v-model="profileForm.relationType" disabled/>
                  </el-form-item>
                </el-col>
              </el-row>
            </template>

            <template v-if="isKin">
              <el-divider content-position="left">已绑定老人信息</el-divider>
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="已绑定老人">
                    <template v-if="profileForm.bindElderIds && profileForm.bindElderIds.length > 0">
                      <el-tag
                          v-for="elderId in profileForm.bindElderIds"
                          :key="elderId"
                          class="tag-item"
                          closable
                          @close="handleUnbindElder(elderId)"
                      >
                        {{ getElderName(elderId) }}
                      </el-tag>
                    </template>
                    <div v-else class="no-data">
                      暂未绑定老人
                    </div>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="与老人关系">
                    <el-input v-model="profileForm.relationType" disabled/>
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
import {computed, onMounted, ref} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Plus} from '@element-plus/icons-vue'
import {useUserStore} from '@/stores/fore/userStore';
import {TokenManager} from '@/utils/axios';

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
  const role = userStore.userInfo?.roleId || userStore.userInfo?.roles?.[0]
  console.log('当前用户角色:', role)
  return role
})

// 判断是否为老人角色
const isElder = computed(() => {
  const role = userRole.value
  console.log('判断是否为老人角色:', role)
  return role === 'elder' || role === 1 || userStore.userInfo?.roleIds?.includes(1)
})

// 判断是否为家属角色
const isKin = computed(() => {
  const role = userRole.value
  console.log('判断是否为家属角色:', role)
  return role === 'kin' || role === 2 || userStore.userInfo?.roleIds?.includes(2)
})

// 加载状态
const isLoadingKinList = ref(false);
const isLoadingElderList = ref(false);

// 获取家属列表
const kinList = ref([]);
const getKinList = async () => {
  try {
    isLoadingKinList.value = true;
    if (!profileForm.value.id) {
      console.warn('用户ID为空，无法获取家属列表');
      kinList.value = [];
      return;
    }

    console.log('开始获取家属列表，用户ID:', profileForm.value.id);
    const response = await userStore.fetchKinListByElderId(profileForm.value.id);
    console.log('获取到的家属列表原始数据:', response);

    if (!response) {
      console.warn('获取家属列表响应为空');
      kinList.value = [];
      return;
    }

    let data = [];
    if (Array.isArray(response)) {
      data = response;
    } else if (response.data && Array.isArray(response.data)) {
      data = response.data;
    } else if (response.code === 200 && Array.isArray(response.data)) {
      data = response.data;
    }

    // 处理数据，确保每个家属对象都有必要的字段
    kinList.value = data.map(kin => {
      if (!kin) {
        console.warn('家属数据为空，跳过处理');
        return null;
      }

      const id = kin.id || kin.userId;
      if (!id) {
        console.warn('家属ID为空，跳过处理:', kin);
        return null;
      }

      return {
        id,
        name: kin.name || '未知家属',
        userId: kin.userId || id,
        relationType: kin.relationType || '未知关系'
      };
    }).filter(kin => kin !== null);

    // 更新绑定家属ID列表和关系类型
    if (kinList.value.length > 0) {
      profileForm.value.bindKinIds = kinList.value.map(kin => kin.id);
      profileForm.value.relationType = kinList.value[0].relationType;
      console.log('更新后的绑定家属ID列表:', profileForm.value.bindKinIds);
      console.log('更新后的关系类型:', profileForm.value.relationType);
    }
  } catch (error) {
    console.error('获取家属列表失败:', error);
    ElMessage.error('获取家属列表失败');
    kinList.value = [];
  } finally {
    isLoadingKinList.value = false;
  }
};

// 获取老人列表
const elderList = ref([]);
const getElderList = async () => {
  try {
    isLoadingElderList.value = true;
    if (!profileForm.value.id) {
      console.warn('用户ID为空，无法获取老人列表');
      elderList.value = [];
      return;
    }

    console.log('开始获取老人列表，用户ID:', profileForm.value.id);
    const response = await userStore.fetchElderListByKinId(profileForm.value.id);
    console.log('获取到的老人列表原始数据:', response);

    if (!response) {
      console.warn('获取老人列表响应为空');
      elderList.value = [];
      return;
    }

    let data = [];
    if (Array.isArray(response)) {
      data = response;
    } else if (response.data && Array.isArray(response.data)) {
      data = response.data;
    } else if (response.code === 200 && Array.isArray(response.data)) {
      data = response.data;
    }

    // 处理数据，确保每个老人对象都有必要的字段
    elderList.value = data.map(elder => {
      if (!elder) {
        console.warn('老人数据为空，跳过处理');
        return null;
      }

      const id = elder.id || elder.userId;
      if (!id) {
        console.warn('老人ID为空，跳过处理:', elder);
        return null;
      }

      return {
        id,
        name: elder.name || '未知老人',
        userId: elder.userId || id,
        relationType: elder.relationType || '未知关系'
      };
    }).filter(elder => elder !== null);

    // 更新绑定老人ID列表和关系类型
    if (elderList.value.length > 0) {
      profileForm.value.bindElderIds = elderList.value.map(elder => elder.id);
      profileForm.value.relationType = elderList.value[0].relationType;
      console.log('更新后的绑定老人ID列表:', profileForm.value.bindElderIds);
      console.log('更新后的关系类型:', profileForm.value.relationType);
    }
  } catch (error) {
    console.error('获取老人列表失败:', error);
    ElMessage.error('获取老人列表失败');
    elderList.value = [];
  } finally {
    isLoadingElderList.value = false;
  }
};

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

// 获取健康档案
const getHealthRecord = async () => {
  try {
    const response = await userStore.getHealthRecordByElderId(profileForm.value.id)
    console.log('获取到的健康档案:', response)

    if (response.code === 200 && response.data) {
      const healthRecord = response.data
      profileForm.value.allergy = healthRecord.allergy || ''
      profileForm.value.medicalHistory = healthRecord.medicalHistory || ''
    }
  } catch (error) {
    console.error('获取健康档案失败:', error)
  }
}

// 初始化表单数据
const initFormData = async () => {
  try {
    // 获取用户信息
    const userInfo = await userStore.getUserInfo()
    if (!userInfo || userInfo.code !== 200) {
      ElMessage.error('获取用户信息失败')
      return
    }

    // 保存原始表单数据
    const originalFormData = {...profileForm.value}

    // 更新表单数据，保留原有值作为后备
    profileForm.value = {
      id: userInfo.data.userId || originalFormData.id,
      name: userInfo.data.name || originalFormData.name,
      gender: userInfo.data.gender || originalFormData.gender,
      phone: userInfo.data.phone || originalFormData.phone,
      email: userInfo.data.email || originalFormData.email,
      avatar: userInfo.data.avatar || originalFormData.avatar,
      roles: userInfo.data.roles || originalFormData.roles,
      isActive: userInfo.data.isActive || originalFormData.isActive,
      // 老人特有字段
      idCard: userInfo.data.idCard || originalFormData.idCard,
      birthday: userInfo.data.birthday || originalFormData.birthday,
      age: userInfo.data.age || originalFormData.age,
      healthCondition: userInfo.data.healthCondition || originalFormData.healthCondition,
      emergencyContactName: userInfo.data.emergencyContactName || originalFormData.emergencyContactName,
      emergencyContactPhone: userInfo.data.emergencyContactPhone || originalFormData.emergencyContactPhone,
      // 家属特有字段
      elderId: userInfo.data.elderId || originalFormData.elderId,
      elderName: userInfo.data.elderName || originalFormData.elderName,
      relationType: userInfo.data.relationType || originalFormData.relationType,
      // 绑定关系
      bindKinIds: userInfo.data.bindKinIds || originalFormData.bindKinIds,
      bindElderIds: userInfo.data.bindElderIds || originalFormData.bindElderIds
    }

    // 根据角色加载相关列表
    if (isElder.value) {
      await getKinList()
    } else if (isKin.value) {
      await getElderList()
    }
  } catch (error) {
    console.error('初始化表单数据失败:', error)
    ElMessage.error('初始化表单数据失败')
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

// 获取家属名称
const getKinName = (kinId) => {
  console.log('获取家属名称，kinId:', kinId);
  console.log('当前家属列表:', kinList.value);

  if (!kinId) {
    console.warn('家属ID为空');
    return '未知家属';
  }

  const kin = kinList.value.find(k => k.id === kinId || k.userId === kinId);
  console.log('找到的家属信息:', kin);

  if (!kin) {
    console.warn('未找到对应的家属信息');
    return '未知家属';
  }

  return kin.name || '未知家属';
};

// 获取老人名称
const getElderName = (elderId) => {
  console.log('获取老人名称，elderId:', elderId);
  console.log('当前老人列表:', elderList.value);

  if (!elderId) {
    console.warn('老人ID为空');
    return '未知老人';
  }

  const elder = elderList.value.find(e => e.id === elderId || e.userId === elderId);
  console.log('找到的老人信息:', elder);

  if (!elder) {
    console.warn('未找到对应的老人信息');
    return '未知老人';
  }

  return elder.name || '未知老人';
};

// 解绑家属
const handleUnbindKin = async (kinId) => {
  try {
    await ElMessageBox.confirm('确定要解绑该家属吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });

    const success = await userStore.unbindElderKin(profileForm.value.id, kinId);
    if (success) {
      ElMessage.success('解绑成功');
      // 更新绑定列表
      profileForm.value.bindKinIds = profileForm.value.bindKinIds.filter(id => id !== kinId);
    } else {
      ElMessage.error('解绑失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('解绑失败');
    }
  }
};

// 解绑老人
const handleUnbindElder = async (elderId) => {
  try {
    await ElMessageBox.confirm('确定要解绑该老人吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });

    const success = await userStore.unbindElderKin(elderId, profileForm.value.id);
    if (success) {
      ElMessage.success('解绑成功');
      // 更新绑定列表
      profileForm.value.bindElderIds = profileForm.value.bindElderIds.filter(id => id !== elderId);
    } else {
      ElMessage.error('解绑失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('解绑失败');
    }
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

.loading-tip {
  margin-top: 8px;
  color: #909399;
  font-size: 14px;
  text-align: center;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.loading-tip .el-icon {
  font-size: 16px;
}

.tag-item {
  margin-right: 8px;
  margin-bottom: 8px;
}
</style>
