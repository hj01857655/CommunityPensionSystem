<template>
    <el-container style="height: 100vh;">
        <el-header>
            <el-dropdown>
                <span class="el-dropdown-link">
                    <img src="@/assets/avatar.png" alt="Avatar" class="avatar">
                </span>
                <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item @click="goToProfile">个人中心</el-dropdown-item>
                    <el-dropdown-item @click="goToSettings">设置</el-dropdown-item>
                    <el-dropdown-item @click="logout">退出</el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
        </el-header>
        <el-container>
            <el-aside width="200px">
                <el-menu default-active="1" class="el-menu-vertical-demo">
                    <el-menu-item index="1">用户注册与登录</el-menu-item>
                    <el-menu-item index="2">用户信息管理</el-menu-item>
                    <el-submenu index="3">
                        <template #title>权限管理</template>
                        <el-menu-item index="3-1">角色管理</el-menu-item>
                        <el-menu-item index="3-2">权限设置</el-menu-item>
                    </el-submenu>
                    <el-submenu index="4">
                        <template #title>服务管理</template>
                        <el-menu-item index="4-1">服务预约</el-menu-item>
                        <el-menu-item index="4-2">预约处理</el-menu-item>
                    </el-submenu>
                    <el-submenu index="5">
                        <template #title>健康监测管理</template>
                        <el-menu-item index="5-1">健康数据录入</el-menu-item>
                        <el-menu-item index="5-2">健康数据查看</el-menu-item>
                    </el-submenu>
                    <el-submenu index="6">
                        <template #title>社区活动管理</template>
                        <el-menu-item index="6-1">活动创建</el-menu-item>
                        <el-menu-item index="6-2">活动参与者管理</el-menu-item>
                    </el-submenu>
                    <el-submenu index="7">
                        <template #title>数据分析管理</template>
                        <el-menu-item index="7-1">用户活跃度分析</el-menu-item>
                        <el-menu-item index="7-2">服务使用情况分析</el-menu-item>
                        <el-menu-item index="7-3">活动参与度分析</el-menu-item>
                    </el-submenu>
                </el-menu>
            </el-aside>
            <el-main>
                <el-row :gutter="20">
                    <el-col :span="8">
                        <el-card>
                            <h3>快速访问</h3>
                            <p>这里可以放置一些快捷操作按钮。</p>
                        </el-card>
                    </el-col>
                    <el-col :span="8">
                        <el-card>
                            <h3>系统通知</h3>
                            <p>显示最新的系统通知和更新。</p>
                        </el-card>
                    </el-col>
                    <el-col :span="8">
                        <el-card>
                            <h3>统计概览</h3>
                            <p>展示一些关键的统计数据。</p>
                        </el-card>
                    </el-col>
                </el-row>
            </el-main>
        </el-container>
    </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

const router = useRouter();

const goToProfile = () => {
    ElMessage.info('前往个人中心');
    // router.push('/profile');
};

const goToSettings = () => {
    ElMessage.info('前往设置');
    // router.push('/settings');
};

const logout = () => {
    ElMessage.info('已退出');
    // 执行退出逻辑
    // router.push('/login');
};
</script>

<style scoped>
.el-header {
    background-color: #409eff;
    color: white;
    text-align: right;
    line-height: 60px;
    padding-right: 20px;
}

.avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    cursor: pointer;
}

.el-aside {
    background-color: #d3dce6;
}

.el-main {
    padding: 20px;
}

.el-card {
    text-align: center;
    padding: 20px;
}
</style> 