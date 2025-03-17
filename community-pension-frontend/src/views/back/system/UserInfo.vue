<!-- 管理员个人中心 -->
<template>
    <div class="admin-profile">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>个人信息</span>
                </div>
            </template>
            <div class="card-content">
                <el-form :model="adminInfo" label-width="120px">
                    <!-- 不显示管理员ID -->
                    <el-form-item label="管理员ID" prop="id" style="display: none;">
                        <el-input v-model="adminInfo.id" disabled />
                    </el-form-item>
                    <el-form-item label="管理员名称">
                        <el-input v-model="adminInfo.username" disabled />
                    </el-form-item>
                    <el-form-item label="管理员角色">
                        <el-input v-model="roleDisplay" disabled />
                    </el-form-item>
                    <el-form-item label="管理员状态">
                        <el-switch v-model="statusValue" disabled />
                    </el-form-item>
                    <el-form-item label="头像">
                        <el-avatar :src="avatarUrl" size="large" />
                    </el-form-item>
                </el-form>
            </div>  
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useAdminStore } from '@/stores/back/adminStore';
import { getAvatarUrl } from '@/utils/avatarUtils';

// 初始化adminStore
const adminStore = useAdminStore();

const roleMap = ref({
    '1': '老人',
    '2': '亲属',
    '3': '社区工作人员',
    '4': '管理员',
    '5': '访客'
});

// 管理员信息
const adminInfo = ref(adminStore.adminInfo);

// 计算角色显示文本
const roleDisplay = computed(() => {
    return roleMap.value[adminInfo.value.roleId];
});

// 计算状态值，将isActive转换为布尔值
const statusValue = computed(() => {
    // 确保将isActive值正确映射为布尔值
    return adminInfo.value.isActive === 1 || adminInfo.value.isActive === '1';
});

// 计算头像URL
const avatarUrl = computed(() => getAvatarUrl(adminInfo.value.avatar));

// 获取管理员信息
const getAdminInfo = async () => {
    try {
        if (adminStore && adminStore.adminInfo && adminStore.adminInfo.username) {
            adminInfo.value = adminStore.adminInfo;
        } else {
            const adminInfoStr = sessionStorage.getItem('adminInfo');
            if (adminInfoStr) {
                adminInfo.value = JSON.parse(adminInfoStr);
                if (adminStore) {
                    adminStore.adminInfo = adminInfo.value;
                }
            } else {
                adminInfo.value = {};
            }
        }
        console.log('当前管理员信息:', adminInfo.value);
    } catch (error) {
        console.error('获取管理员信息出错:', error);
        adminInfo.value = {};
    }
};

onMounted(() => {
    if (adminStore && typeof adminStore.initAdminInfo === 'function') {
        adminStore.initAdminInfo();
    }
    getAdminInfo();
});
</script>

<style scoped>
.admin-profile {
    padding: 20px;
}   
</style>
