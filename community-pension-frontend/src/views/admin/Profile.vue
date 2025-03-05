
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
                <el-form :model="form" label-width="120px">
                    <el-form-item label="管理员ID">
                        <span>{{ form.id }}</span>
                    </el-form-item>
                    <el-form-item label="管理员名称">
                        <el-input v-model="form.name" />
                    </el-form-item>
                    <el-form-item label="管理员角色">
                        <el-select v-model="form.role" placeholder="请选择管理员角色">
                            <el-option label="管理员" value="admin" />
                        </el-select>    
                    </el-form-item>
                    <el-form-item label="管理员状态">
                        <el-switch v-model="form.status" />
                    </el-form-item>
                </el-form>
            </div>  
        </el-card>
    </div>
</template>

<script setup>
import { ref } from 'vue';

// 管理员信息
const form = ref({
    id: '',
    name: '',
    email: '',
    role: '',
    status: false
}); 

const handleSave = () => {
    console.log('保存管理员信息');
};
</script>


<style scoped>
.admin-profile {
    padding: 20px;
}   
</style>


