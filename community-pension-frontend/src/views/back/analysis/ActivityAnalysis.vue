<!-- 活动分析 -->
<template>
  <div class="activity-analysis">
    <el-card>
      <div slot="header" class="clearfix">
        <span>活动分析</span>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElCard, ElTable, ElTableColumn } from 'element-plus';

const tableData = ref([]);

onMounted(() => {
    // 模拟数据
    tableData.value = [
        {
            id: 1,
            name: '活动1',
            count: 100
        },
        {
            id: 2,
            name: '活动2',
            count: 200
        },
        {
            id: 3,
            name: '活动3',
            count: 300
        },
        {
            id: 4,
            name: '活动4',
            count: 400
        },
        {
            id: 5,
            name: '活动5',
            count: 500
        },
        {
            id: 6,
            name: '活动6',
            count: 600
        },
        {
            id: 7,
            name: '活动7',
            count: 700
        },
        {
            id: 8,
            name: '活动8',
            count: 800
        },
        
    ]
    console.log(tableData.value);

})

</script>

<style scoped>
.activity-analysis {
    padding: 20px;
}
.clearfix {
    display: flex;
    justify-content: space-between;
    align-items: center;
}


</style>

        
