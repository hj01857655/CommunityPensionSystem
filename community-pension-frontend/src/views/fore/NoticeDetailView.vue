<template v-if="activeIndex === 'noticeDetail'">
    <div class="NoticeDetailView">
        <el-card class="content-card" shadow="hover">
            <el-col :span="24">
                <el-card class="content-card" shadow="hover">
                    <h3>通知详情</h3>
                <p>{{ selectedNotice.title }}</p>
                <p>{{ selectedNotice.date }}</p>
                    <el-button type="link" @click="activeIndex = 'notice'">返回</el-button>
                </el-card>
            </el-col>
        </el-card>
    </div>
</template>
<script setup>
import { ref } from 'vue';

const activeIndex = ref('noticeDetail');
const selectedNotice = ref(null);
</script>

<style scoped>
.NoticeDetailView {
    width: 30%;
}
</style>
