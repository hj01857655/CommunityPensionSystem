<template>
  <div class="profile-view">
    <el-card class="content-card" shadow="hover">
      <h3>个人信息</h3>
      <el-form :model="profileForm" label-width="120px">
        <el-form-item label="ID" style="display: none;">
          <el-input v-model="profileForm.id"/>
        </el-form-item>

        <!-- 使用标签页分类显示信息 -->
        <el-tabs v-model="activeTab" type="card">
          <!-- 基本信息标签页 -->
          <el-tab-pane label="基本信息" name="basic">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="姓名">
                  <el-input v-model="profileForm.name" :disabled="!isEditMode"/>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="性别">
                  <el-select v-model="profileForm.gender" :disabled="!isEditMode" style="width: 100%">
                    <el-option label="男" value="男"/>
                    <el-option label="女" value="女"/>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="出生日期">
                  <el-date-picker v-model="profileForm.birthday" :disabled="!isEditMode" style="width: 100%"
                                  type="date" value-format="YYYY-MM-DD"/>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="身份证号">
                  <el-input v-model="profileForm.idCard" :disabled="!isEditMode"/>
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>

          <!-- 联系信息标签页 -->
          <el-tab-pane label="联系信息" name="contact">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="联系电话">
                  <el-input v-model="profileForm.phone" :disabled="!isEditMode"/>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="地址">
                  <el-input v-model="profileForm.address" :disabled="!isEditMode"/>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="紧急联系人">
                  <el-input v-model="profileForm.emergencyContactName" :disabled="!isEditMode"/>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="紧急联系人电话">
                  <el-input v-model="profileForm.emergencyContactPhone" :disabled="!isEditMode"/>
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>

          <!-- 其他信息标签页 -->
          <el-tab-pane label="其他信息" name="other">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="头像">
                  <el-upload
                      :before-upload="beforeAvatarUpload"
                      :disabled="!isEditMode"
                      :headers="uploadHeaders"
                      :on-success="handleAvatarSuccess"
                      :show-file-list="false"
                      action="/api/user/avatar"
                      class="avatar-uploader"
                  >
                    <img v-if="profileForm.avatar" :src="processedAvatarUrl" class="avatar"/>
                    <el-icon v-else class="avatar-uploader-icon">
                      <Plus/>
                    </el-icon>
                  </el-upload>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="个人简介">
                  <el-input
                      v-model="profileForm.remark"
                      :disabled="!isEditMode"
                      :rows="3"
                      placeholder="请输入个人简介"
                      type="textarea"
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
          <el-button v-if="!isEditMode" type="primary" @click="toggleEditMode">编辑</el-button>
          <el-button v-if="isEditMode" type="success" @click="saveProfile">保存</el-button>
          <el-button v-if="isEditMode" @click="toggleEditMode">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import {useUserStore} from '@/stores/fore/userStore';
import {getAvatarUrl} from '@/utils/avatarUtils';
import {TokenManager} from '@/utils/axios';
import {Plus} from '@element-plus/icons-vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import {computed, onMounted, ref} from 'vue';
import {getDictDataByType} from '@/api/back/system/dict/data';

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
  // 直接使用userStore中定义的userRole计算属性
  return userStore.userRole;
});

// 判断是否为老人角色
const isElder = computed(() => {
  const role = userRole.value;
  // 修复角色判断逻辑，接受数字1或字符串'elder'作为老人角色
  const isElderRole = role === 'elder' || role === 1;
  console.log('判断是否为老人角色:', isElderRole, '角色值:', role, '角色类型:', typeof role);
  return isElderRole;
});

// 判断是否为家属角色
const isKin = computed(() => {
  const role = userRole.value;
  // 修复角色判断逻辑，接受数字2或字符串'kin'作为家属角色
  const isKinRole = role === 'kin' || role === 2;
  console.log('判断是否为家属角色:', isKinRole, '角色值:', role, '角色类型:', typeof role);
  return isKinRole;
});

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
  allergy: '',//过敏史
  medicalHistory: '',//既往病史
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
    // 更新用户信息中的头像
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
    userInfo.avatar = response.data;
    localStorage.setItem('userInfo', JSON.stringify(userInfo));
    ElMessage.success('头像上传成功');
    console.log('头像上传成功，新的头像地址:', response.data);
  } else {
    ElMessage.error('头像上传失败');
  }
}

// 切换编辑模式
const toggleEditMode = () => {
  if (isEditMode.value) {
    // 取消编辑，恢复原始数据
    profileForm.value = { ...originalData.value };
  }
  isEditMode.value = !isEditMode.value;
};

// 初始化表单数据
const initFormData = async () => {
  try {
    // 从localStorage获取用户信息（前台系统使用localStorage存储数据）
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
    console.log('从localStorage获取的用户信息:', userInfo);

    if (!userInfo || !userInfo.userId) {
      ElMessage.error('获取用户信息失败，请重新登录');
      router.push('/login');
      return;
    }

    // 填充表单数据
    profileForm.value = {
      id: userInfo.userId,
      userId: userInfo.userId,
      name: userInfo.name || '',
      gender: userInfo.gender || '',
      birthday: userInfo.birthday || '',
      idCard: userInfo.idCard || '',
      phone: userInfo.phone || '',
      address: userInfo.address || '',
      email: userInfo.email || '',
      avatar: userInfo.avatar || '',
      emergencyContactName: userInfo.emergencyContactName || '',
      emergencyContactPhone: userInfo.emergencyContactPhone || '',
      username: userInfo.username || '',
      roleIds: userInfo.roleIds || [],
      bindElderIds: userInfo.bindElderIds || [],
      bindKinIds: userInfo.bindKinIds || [],
      relationType: userInfo.relationType || '',
      remark: userInfo.remark || ''
    };

    // 保存原始数据，用于取消编辑时恢复
    originalData.value = { ...profileForm.value };
    console.log('初始化的表单数据:', profileForm.value);

    // 获取角色相关信息
    if (isKin.value) {
      await getElderList();
    } else if (isElder.value) {
      await getKinList();
    }
  } catch (error) {
    console.error('初始化表单数据失败:', error);
    ElMessage.error('获取用户信息失败');
  }
};

// 保存个人信息
const saveProfile = async () => {
  try {
    await ElMessageBox.confirm('确认保存修改的信息吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 创建一个只包含必要字段的数据对象
    const formData = {
      userId: profileForm.value.id || profileForm.value.userId,
      name: profileForm.value.name,
      gender: profileForm.value.gender,
      birthday: profileForm.value.birthday,
      idCard: profileForm.value.idCard,
      phone: profileForm.value.phone,
      address: profileForm.value.address,
      email: profileForm.value.email,
      emergencyContactName: profileForm.value.emergencyContactName,
      emergencyContactPhone: profileForm.value.emergencyContactPhone,
      avatar: profileForm.value.avatar
    };

    // 确保userId存在
    if (!formData.userId) {
      ElMessage.error('用户ID不存在，无法保存信息');
      return;
    }

    console.log('准备提交的表单数据:', formData);

    const success = await userStore.handleUpdateUserInfo(formData);
    console.log(success);
    if (success) {
      ElMessage.success('保存成功');
      isEditMode.value = false;
      // 更新原始数据，以便下次取消编辑时能恢复到最新保存的状态
      originalData.value = {...profileForm.value};

      // 重新获取用户信息，确保数据同步
      await initFormData();
    } else {
      ElMessage.error('保存失败');
    }
  } catch (error) {
    if (error === 'cancel') {
      return
    }
    console.error('保存失败:', error);
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

// 计算属性：处理头像URL
const processedAvatarUrl = computed(() => {
  return getAvatarUrl(profileForm.value.avatar);
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
