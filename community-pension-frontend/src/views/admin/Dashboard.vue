<template>
  <div class="dashboard-container">
    <el-row gutter="20">
      <el-col :span="8">
        <el-card shadow="hover">
          <h3>用户统计</h3>
          <el-statistic
            title="总用户数"
            :value="userCount"
            prefix-icon="el-icon-user"
          />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <h3>服务统计</h3>
          <el-statistic
            title="总服务数"
            :value="serviceCount"
            prefix-icon="el-icon-s-tools"
          />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <h3>活动统计</h3>
          <el-statistic
            title="总活动数"
            :value="activityCount"
            prefix-icon="el-icon-s-flag"
          />
        </el-card>
      </el-col>
    </el-row>
    <el-row gutter="20" class="mt-20">
      <el-col :span="24">
        <el-card shadow="hover">
          <h3>最近活动</h3>
          <el-table :data="recentActivities" style="width: 100%">
            <el-table-column prop="name" label="活动名称" />
            <el-table-column prop="date" label="日期" />
            <el-table-column prop="status" label="状态" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const userCount = ref(120);
const serviceCount = ref(45);
const activityCount = ref(30);

const recentActivities = ref([
  { name: '健康讲座', date: '2025-02-25', status: '已完成' },
  { name: '社区义诊', date: '2025-02-20', status: '进行中' },
  { name: '棋牌比赛', date: '2025-02-18', status: '已完成' },
]);
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.mt-20 {
  margin-top: 20px;
}
</style>