
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
                    <el-form-item label="管理员名称">
                        <el-input v-model="form.username" disabled />
                    </el-form-item>
                    <el-form-item label="管理员角色">
                        <el-select v-model="form.role" placeholder="请选择管理员角色" disabled>
                            <el-option label="管理员" value="admin" />
                        </el-select>    
                    </el-form-item>
                    <el-form-item label="管理员状态">
                        <el-switch v-model="form.status" disabled />
                    </el-form-item>
                </el-form>
            </div>  
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

// 管理员信息
const form = ref({
    id: '',
    username: '',
    role: ref('管理员'),
    status: false
}); 

//映射字典
const roleMap = ref({
    '1': '老人',
    '2': '亲属',
    '3': '社区工作人员',
    '4': '管理员',
    '5': '访客'
})

onMounted(() => {
    const userInfo = localStorage.getItem('userInfo');
    form.value = JSON.parse(userInfo);
    form.value.role = roleMap.value[form.value.roleId];
    //数据库中没有status字段，根据其他数据判断，如果存在roleId，则status为true，否则为false
    form.value.status = form.value.roleId ? true : false;
})
</script>

<style scoped>
.admin-profile {
    padding: 20px;
}   
</style>
