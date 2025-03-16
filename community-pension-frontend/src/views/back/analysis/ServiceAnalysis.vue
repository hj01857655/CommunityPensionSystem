<!-- 服务分析 -->
<template>
  <div class="service-analysis">
    <el-card>
      <div slot="header" class="clearfix">
        <span>服务分析</span>
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
            name: '服务1',
            count: 100
        },
        {
            id: 2,
            name: '服务2',
            count: 200
        },
        {
            id: 3,
            name: '服务3',
            count: 300
        },
        {
            id: 4,
            name: '服务4',
            count: 400
        },
        {
            id: 5,
            name: '服务5',
            count: 500
        },
        {
            id: 6,  
            name: '服务6',
            count: 600
        },
        {
            id: 7,
            name: '服务7',
            count: 700
        },
        {
            id: 8,
            name: '服务8',
            count: 800  
        },
    ]
    console.log(tableData.value);

})

</script>

<style scoped>
.service-analysis {
    padding: 20px;
}
.clearfix {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

</style>
