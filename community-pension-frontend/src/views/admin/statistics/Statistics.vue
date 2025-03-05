<template>
    <div>
        <h1>数据分析看板管理</h1>
        <div ref="chartRef" style="width: 100%; height: 400px;"></div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';

const chartRef = ref(null);

onMounted(() => {
    if (chartRef.value) {
        const chartInstance = echarts.init(chartRef.value);
        const option = {
            title: {
                text: '服务预约量'
            },
            tooltip: {},
            xAxis: {
                type: 'category',
                data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
            },
            yAxis: {
                type: 'value'
            },
            series: [{
                name: '服务预约量',
                type: 'line',
                data: [100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1100, 1200],
                itemStyle: {
                    color: 'rgba(75, 192, 192, 1)'
                },
                lineStyle: {
                    width: 2
                }
            }]
        };
        chartInstance.setOption(option);
    }
});

</script>

<style scoped>
/* 样式 */
</style>
