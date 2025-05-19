<template>
  <div class="activity-detail-view">
    <!-- 加载状态骨架屏 -->
    <template v-if="loading">
      <el-skeleton :rows="10" animated class="loading-skeleton" />
    </template>

    <template v-else>
      <el-card class="content-card" shadow="hover">
        <!-- 活动详情 -->
        <div v-if="activityDetail" class="activity-detail">
          <!-- 活动标题和状态 -->
          <div class="detail-header">
            <h2 class="title">{{ activityDetail.title }}</h2>
            <el-tag :type="getStatusType(activityDetail.status)" effect="dark" size="large" class="status-tag">
              {{ getStatusText(activityDetail.status) }}
            </el-tag>
          </div>
          
          <!-- 活动基本信息 -->
          <div class="basic-info">
            <div class="info-item">
              <el-icon><Calendar /></el-icon>
              <span>活动时间：{{ formatDateTime(activityDetail.startTime) }} - {{ formatDateTime(activityDetail.endTime) }}</span>
            </div>
            <div class="info-item">
              <el-icon><Location /></el-icon>
              <span>活动地点：{{ activityDetail.location }}</span>
            </div>
            <div class="info-item">
              <el-icon><User /></el-icon>
              <span>参与人数：{{ activityDetail.currentRegistrations || activityDetail.currentParticipants || 0 }}/{{ activityDetail.maxRegistrations || activityDetail.maxParticipants }}</span>
            </div>
            <div class="info-item">
              <el-icon><Document /></el-icon>
              <span>活动类型：{{ activityDetail.typeName }}</span>
            </div>
            <div v-if="activityDetail.status === 1" class="info-item registration-status">
              <el-icon><InfoFilled /></el-icon>
              <span>报名状态：
                <template v-if="isRegistered">
                  <el-tag size="small" type="success" effect="dark">已报名</el-tag>
                </template>
                <template v-else>
                  <el-tag size="small" type="warning" effect="dark">未报名</el-tag>
                </template>
              </span>
            </div>
          </div>
          
          <!-- 活动图片 -->
          <div v-if="activityDetail.image" class="activity-image">
            <el-image :src="activityDetail.image" alt="活动图片" fit="cover" lazy>
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                  <span>图片加载失败</span>
                </div>
              </template>
            </el-image>
          </div>
          
          <!-- 活动描述 -->
          <div class="description">
            <h3>活动描述</h3>
            <div class="description-content">{{ activityDetail.description }}</div>
          </div>
          
          <!-- 操作按钮 -->
          <div class="actions">
            <el-button
              v-if="canJoin(activityDetail.status)"
              :disabled="isRegistered"
              type="primary"
              size="large"
              :loading="registerLoading"
              @click="handleRegister"
              class="action-button"
            >
              <el-icon><Plus /></el-icon>
              {{ isRegistered ? '已报名' : '立即报名' }}
            </el-button>
            
            <el-button
              v-if="activityDetail.status === 2 && isRegistered && !isCheckedIn"
              type="success"
              size="large"
              :loading="checkInLoading"
              @click="handleCheckIn"
              class="action-button"
            >
              <el-icon><Check /></el-icon>
              签到
            </el-button>
            
            <el-button
              v-if="activityDetail.status === 2 && isRegistered && isCheckedIn"
              type="success"
              size="large"
              disabled
              class="action-button"
            >
              <el-icon><Check /></el-icon>
              已签到
            </el-button>
            
            <el-button
              v-if="isRegistered && canCancel(activityDetail.status)"
              type="danger"
              size="large"
              :loading="cancelLoading"
              @click="handleCancel"
              class="action-button"
            >
              <el-icon><Close /></el-icon>
              取消报名
            </el-button>
            
            <el-button @click="goBack" size="large" class="back-button">
              <el-icon><Back /></el-icon>
              返回
            </el-button>
          </div>
        </div>
        
        <!-- 无数据状态 -->
        <el-empty v-else description="未找到活动详情">
          <template #description>
            <p>未能找到此活动的详细信息</p>
          </template>
          <el-button type="primary" @click="goBack">返回活动列表</el-button>
        </el-empty>
      </el-card>

      <!-- 活动通知提示 -->
      <el-card v-if="activityDetail && activityDetail.status === 1 && !isRegistered" class="notice-card" shadow="hover">
        <template #header>
          <div class="notice-header">
            <el-icon><Bell /></el-icon>
            <span>活动提醒</span>
          </div>
        </template>
        <div class="notice-content">
          <p>此活动正在报名中</p>
          
          <!-- 报名截止时间显示 -->
          <template v-if="activityDetail.registrationEndTime || 
                           activityDetail.registerEndTime || 
                           activityDetail.signUpEndTime || 
                           activityDetail.enrollEndTime">
            <p>
              报名截止时间：
              <span class="highlight-time">{{ 
                formatDateTime(
                  activityDetail.registrationEndTime || 
                  activityDetail.registerEndTime || 
                  activityDetail.signUpEndTime || 
                  activityDetail.enrollEndTime
                )
              }}</span>
            </p>
          </template>
          <template v-else>
            <p>
              <el-alert
                title="注意：系统未设置报名截止时间"
                type="warning"
                :closable="false"
                show-icon
                class="registration-alert"
              >
                <template #default>
                  建议您尽快报名，以免错过活动机会
                </template>
              </el-alert>
            </p>
          </template>
          
          <!-- 活动开始时间 -->
          <p>
            活动开始时间：
            <span class="highlight-time">{{ formatDateTime(activityDetail.startTime) }}</span>
          </p>
          
          <!-- 活动结束时间 -->
          <p v-if="activityDetail.endTime">
            活动结束时间：
            <span class="highlight-time">{{ formatDateTime(activityDetail.endTime) }}</span>
          </p>
          
          <p>报名后可获得活动提醒和最新通知。</p>
        </div>
      </el-card>
    </template>
  </div>
</template>

<script setup>
import { useActivityStore } from '@/stores/fore/activityStore';
import { formatDateTime } from '@/utils/date';

import { useUserStore } from '@/stores/fore/userStore';
import { Back, Bell, Calendar, Check, Close, Document, InfoFilled, Location, Picture, Plus, User } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();
const activityStore = useActivityStore();
const userStore = useUserStore();

// 状态
const loading = ref(false);
const activityDetail = ref(null);
const registerLoading = ref(false);
const cancelLoading = ref(false);
const checkInLoading = ref(false);
const isRegistered = ref(false);
const isCheckedIn = ref(false);
const registerId = ref(null);

// 获取活动详情
const fetchActivityDetail = async () => {
  const activityId = route.params.id;
  if (!activityId) {
    ElMessage.error('活动ID不能为空');
    return;
  }
  
  loading.value = true;
  try {
    const detail = await activityStore.getActivityDetailAction(activityId);
    if (!detail) {
      ElMessage.warning('未找到该活动详情');
      activityDetail.value = null;
      return;
    }
    
    activityDetail.value = detail;
    document.title = `${detail.title || '活动详情'} - 社区养老服务平台`;
    
    // 检查是否已报名
    await checkRegistrationStatus();
    
    // 检查是否已签到
    if (activityDetail.value && activityDetail.value.status === 2) {
      await checkCheckInStatus();
    }
  } catch (error) {
    console.error('获取活动详情失败:', error);
    ElMessage.error('获取活动详情失败，请稍后重试');
    activityDetail.value = null;
  } finally {
    loading.value = false;
  }
};

// 检查报名状态
const checkRegistrationStatus = async () => {
  try {
    const activityId = route.params.id;
    console.log('检查活动报名状态，活动ID:', activityId);
    
    // 获取报名状态
    const status = await activityStore.getActivityStatusAction(activityId);
    console.log('获取到的报名状态:', status);
    
    // 检查状态结果，只有当status存在且有registerId时才认为已报名
    if (status && status.registerId) {
      console.log('用户已报名此活动，报名ID:', status.registerId);
      isRegistered.value = true;
      registerId.value = status.registerId;
    } else {
      console.log('用户未报名此活动或获取状态失败');
      isRegistered.value = false;
      registerId.value = null;
    }
  } catch (error) {
    console.error('检查报名状态失败:', error);
    
    // 检查错误类型，如果是"未找到报名记录"类型的错误，不显示错误消息
    if (error.message && (
        error.message.includes('未找到报名记录') || 
        error.message.includes('不存在') ||
        error.message.includes('NOT_REGISTERED')
      )) {
      console.log('用户未报名此活动');
    } else {
      // 其他错误情况，不要直接显示给用户，避免过多的错误提示
      console.warn('报名状态检查发生其他错误:', error.message);
    }
    
    // 出错时默认设为未报名状态，不显示错误消息给用户
    isRegistered.value = false;
    registerId.value = null;
  }
};

// 检查签到状态
const checkCheckInStatus = async () => {
  try {
    const activityId = route.params.id;
    const status = await activityStore.getCheckInStatusAction(activityId);
    isCheckedIn.value = status ? status.isCheckedIn : false;
  } catch (error) {
    console.error('检查签到状态失败:', error);
    isCheckedIn.value = false;
  }
};

// 获取活动状态文本
const getStatusText = (status) => {
  return activityStore.getStatusText(status);
};

// 获取活动状态类型
const getStatusType = (status) => {
  return activityStore.getStatusType(status);
};

// 判断活动是否可以报名
const canJoin = (status) => {
  return activityStore.canJoin(status);
};

// 判断是否可以取消报名
const canCancel = (status) => {
  // 只有活动状态为"筹备中"或"报名中"时才能取消报名
  return status === 0 || status === 1;
};

// 处理报名
const handleRegister = async () => {
  if (!activityDetail.value) {
    console.error('活动详情不存在，无法报名');
    ElMessage.error('活动详情不存在，无法报名');
    return;
  }
  
  console.log('开始报名流程 - 活动信息:', {
    id: activityDetail.value.id,
    title: activityDetail.value.title,
    status: activityDetail.value.status,
    isRegistered: isRegistered.value
  });
  
  // 检查登录状态
  const userInfo = userStore.userInfo || JSON.parse(localStorage.getItem('userInfo') || '{}');
  if (!userInfo || !userInfo.userId) {
    console.warn('用户未登录，无法报名');
    ElMessage.warning('请先登录后再报名活动');
    return;
  }
  
  console.log('用户信息:', {
    userId: userInfo.userId,
    username: userInfo.username,
    roles: userInfo.roles,
    roleId: userInfo.roleId || localStorage.getItem('roleId')
  });
  
  // 如果已经报名，则不再继续
  if (isRegistered.value) {
    console.log('用户已报名，无需重复报名');
    ElMessage.info('您已经报名了此活动');
    return;
  }
  
  registerLoading.value = true;
  try {
    console.log('调用报名API前...');
    
    // 确保活动ID是数字类型
    const activityToRegister = {
      ...activityDetail.value,
      id: Number(activityDetail.value.id)
    };
    
    const success = await activityStore.registerActivityAction(activityToRegister);
    console.log('报名API调用结果:', success);
    
    if (success) {
      isRegistered.value = true;
      await checkRegistrationStatus();
      // 显示成功消息
      ElMessage({
        message: '恭喜您，报名成功！',
        type: 'success',
        duration: 3000
      });
    } else {
      console.warn('报名API返回失败');
      // 错误消息已在store中处理，这里不再重复显示
    }
  } catch (error) {
    console.error('报名失败:', error);
    
    // 提取更详细的错误信息
    let errorMessage = '报名失败，请稍后重试';
    
    if (typeof error === 'object') {
      if (error.message) {
        errorMessage = error.message;
      } else if (error.response && error.response.data) {
        const responseData = error.response.data;
        errorMessage = responseData.message || responseData.msg || errorMessage;
      }
    }
    
    ElMessage.error(errorMessage);
  } finally {
    registerLoading.value = false;
  }
};

// 处理取消报名
const handleCancel = async () => {
  if (!registerId.value) {
    ElMessage.warning('未找到报名记录');
    return;
  }
  
  ElMessageBox.confirm(`确定要取消报名"${activityDetail.value.title}"活动吗？`, '取消报名', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    cancelLoading.value = true;
    try {
      const success = await activityStore.cancelActivityRegistrationAction(registerId.value);
      if (success) {
        isRegistered.value = false;
        registerId.value = null;
        ElMessage.success('取消报名成功');
      }
    } catch (error) {
      console.error('取消报名失败:', error);
    } finally {
      cancelLoading.value = false;
    }
  }).catch(() => {});
};

// 处理签到
const handleCheckIn = async () => {
  if (!activityDetail.value) return;
  
  checkInLoading.value = true;
  try {
    const success = await activityStore.selfCheckInAction(activityDetail.value);
    if (success) {
      isCheckedIn.value = true;
      ElMessage({
        message: '签到成功，祝您活动愉快！',
        type: 'success',
        duration: 3000
      });
    }
  } catch (error) {
    console.error('签到失败:', error);
  } finally {
    checkInLoading.value = false;
  }
};

// 返回上一页
const goBack = () => {
  router.back();
};

// 组件挂载时获取活动详情
onMounted(() => {
  fetchActivityDetail();
});
</script>

<style scoped>
.activity-detail-view {
  width: 100%;
  animation: fadeIn 0.5s ease-in-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.loading-skeleton {
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  background-color: #fff;
}

.content-card {
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.content-card:hover {
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 15px;
}

.title {
  margin: 0;
  font-size: 24px;
  color: #303133;
  position: relative;
  font-weight: 600;
}

.status-tag {
  font-size: 14px;
  padding: 6px 12px;
  border-radius: 4px;
}

.basic-info {
  background-color: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  font-size: 14px;
  color: #606266;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-item .el-icon {
  margin-right: 8px;
  color: #409eff;
}

.activity-image {
  margin-bottom: 20px;
  text-align: center;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.activity-image .el-image {
  width: 100%;
  max-height: 400px;
  object-fit: cover;
  border-radius: 8px;
  transition: transform 0.3s ease;
}

.activity-image .el-image:hover {
  transform: scale(1.02);
}

.image-error {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 200px;
  background-color: #f5f7fa;
  color: #909399;
}

.image-error .el-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.description {
  margin-bottom: 30px;
}

.description h3 {
  font-size: 18px;
  margin-bottom: 12px;
  color: #303133;
  position: relative;
  padding-left: 12px;
  border-left: 4px solid #409eff;
}

.description-content {
  font-size: 14px;
  line-height: 1.8;
  color: #606266;
  white-space: pre-line;
  padding: 15px;
  background-color: #fafafa;
  border-radius: 6px;
}

.actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px dashed #ebeef5;
}

.action-button {
  transition: all 0.3s ease;
}

.action-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.back-button {
  margin-left: auto;
}

.notice-card {
  margin-top: 20px;
  border-radius: 8px;
  background-color: #fef8e7;
  border-color: #faecd8;
  transition: all 0.3s ease;
}

.notice-card:hover {
  box-shadow: 0 4px 16px rgba(230, 162, 60, 0.2);
  transform: translateY(-2px);
}

.notice-header {
  display: flex;
  align-items: center;
  color: #e6a23c;
  font-weight: bold;
}

.notice-header .el-icon {
  margin-right: 8px;
  font-size: 18px;
}

.notice-content {
  color: #997b3d;
  font-size: 14px;
  line-height: 1.6;
}

.notice-content p {
  margin: 8px 0;
  padding: 2px 0;
}

.highlight-time {
  font-weight: bold;
  color: #e6a23c;
  background-color: rgba(230, 162, 60, 0.1);
  padding: 2px 6px;
  border-radius: 4px;
  display: inline-block;
}

.registration-alert {
  margin: 10px 0;
  border-radius: 6px;
}

.registration-alert :deep(.el-alert__title) {
  font-size: 14px;
  font-weight: bold;
}

.registration-alert :deep(.el-alert__content) {
  padding: 8px 0;
}

.registration-alert :deep(.el-alert__icon) {
  font-size: 16px;
  margin-right: 10px;
}

.registration-status {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed #eee;
}

.registration-status .el-icon {
  color: #f56c6c;
}

@media (max-width: 768px) {
  .detail-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .status-tag {
    margin-top: 10px;
  }
  
  .actions {
    flex-direction: column;
  }
  
  .actions .el-button {
    width: 100%;
  }
  
  .back-button {
    margin-left: 0;
  }
}
</style> 