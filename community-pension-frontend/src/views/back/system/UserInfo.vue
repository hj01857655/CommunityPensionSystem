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
import { useAdminStore } from '@/stores/back/adminStore';

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
    try {
        // 先尝试从store获取信息
        if (adminStore && adminStore.adminInfo && adminStore.adminInfo.username) {
            adminInfo.value = adminStore.adminInfo;
        } else {
            // 如果store中没有，则尝试从sessionStorage获取
            const adminInfoStr = sessionStorage.getItem('adminInfo');
            if (adminInfoStr) {
                adminInfo.value = JSON.parse(adminInfoStr);
                // 同时更新store中的信息
                if (adminStore) {
                    adminStore.adminInfo = adminInfo.value;
                }
            } else {
                // 如果sessionStorage也没有，则使用空对象
                adminInfo.value = {};
            }
        }
        console.log('当前管理员信息:', adminInfo.value);
    } catch (error) {
        console.error('获取管理员信息出错:', error);
        adminInfo.value = {}; // 出错时使用空对象
    }
};

onMounted(() => {
    // 确保先初始化adminStore，再获取管理员信息
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
