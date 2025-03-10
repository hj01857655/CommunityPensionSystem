
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
                        <el-input v-model="adminStore.adminInfo.id" disabled />
                    </el-form-item>
                    <el-form-item label="管理员名称">
                        <el-input v-model="adminStore.adminInfo.username" disabled />
                    </el-form-item>
                    <el-form-item label="管理员角色">
                        <el-select v-model="adminStore.adminInfo.role" placeholder="请选择管理员角色" disabled>

                            
                        </el-select>    
                    </el-form-item>
                    <el-form-item label="管理员状态">
                        <el-switch v-model="adminStore.adminInfo.status" disabled />
                    </el-form-item>
                </el-form>
            </div>  
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useAdminStore } from '@/stores/adminStore';

const adminStore = useAdminStore();

const roleMap = ref({
    '1': '老人',
    '2': '亲属',
    '3': '社区工作人员',
    '4': '管理员',
    '5': '访客'
})

onMounted(() => {
    console.log(adminStore.adminInfo.status)
    if(adminStore.adminInfo){
        adminStore.adminInfo.role = roleMap.value[adminStore.adminInfo.roleId];
    }

    
})
</script>

<style scoped>
.admin-profile {
    padding: 20px;
}   
</style>
