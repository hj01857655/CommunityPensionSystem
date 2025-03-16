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
                <el-form :model="adminStore.adminInfo" label-width="120px">
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
                </el-form>
            </div>  
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';


const roleMap = ref({
    '1': '老人',
    '2': '亲属',
    '3': '社区工作人员',
    '4': '管理员',
    '5': '访客'
});

// 管理员信息
const adminInfo = ref({});

// 计算角色显示文本
const roleDisplay = computed(() => {
    return roleMap.value[adminInfo.value.roleId] ;
});

// 计算状态值，将数字转换为布尔值
const statusValue = computed(() => {
    // 将状态值转换为布尔值，1表示true，其他值表示false
    return adminInfo.value.status === 1 || adminInfo.value.status === '1';
});

// 获取管理员信息
const getAdminInfo = async () => {
    if (adminStore.adminInfo.username) {
        adminInfo.value = adminStore.adminInfo;
    } else {
        adminInfo.value = JSON.parse(localStorage.getItem('adminInfo') || '{}');
    }
    console.log(adminInfo.value);
};

onMounted(() => {
    getAdminInfo();
});
</script>

<style scoped>
.admin-profile {
    padding: 20px;
}   
</style>
