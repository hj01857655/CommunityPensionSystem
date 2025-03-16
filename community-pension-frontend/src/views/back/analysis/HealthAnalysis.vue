<!-- 健康分析 -->
<template>
  <div class="health-analysis">
    <el-card>
      <div slot="header" class="clearfix">
        <span>健康分析</span>
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
            name: '健康1',
            count: 100
        },
        {
            id: 2,
            name: '健康2',
            count: 200
        },
        {
            id: 3,
            name: '健康3',
            count: 300
        },
        {
            id: 4,
            name: '健康4',
            count: 400
        },
        {
            id: 5,
            name: '健康5',
            count: 500
        },
        {
            id: 6,
            name: '健康6',
            count: 600
        },
        {
            id: 7,
            name: '健康7',
            count: 700
        },
        {
            id: 8,
            name: '健康8',
            count: 800
        }, 
    ]
    console.log(tableData.value);

})

</script>

<style scoped>
.health-analysis {
    padding: 20px;
}
.clearfix {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

</style>








