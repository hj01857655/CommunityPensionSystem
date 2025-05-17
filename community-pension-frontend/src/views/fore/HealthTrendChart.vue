<template>
  <el-card class="trend-chart-card" shadow="never">
    <div class="trend-chart-header">
      <span>健康趋势图</span>
      <el-select v-model="selectedType" size="small" style="width: 120px; margin-left: 16px;">
        <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
    </div>
    <div v-loading="loading" style="height: 320px; position: relative;" ref="chartContainerRef">
      <template v-if="isChartVisible && filteredData.length > 0">
        <VueECharts
          ref="chartRef"
          :option="chartOption"
          autoresize
          @ready="onChartReady"
          style="height: 300px; width: 100%;"
        />
        <el-button
          size="small"
          style="position: absolute; right: 10px; top: 10px; z-index: 2;"
          @click="exportChart"
          :disabled="!chartReady"
        >
          导出图片
        </el-button>
      </template>
      <template v-else>
        <div style="height: 300px; display: flex; align-items: center; justify-content: center; color: #999; font-size: 16px;">
          暂无数据
        </div>
      </template>
    </div>
  </el-card>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { computed, defineExpose, nextTick, onMounted, ref, watch } from 'vue'
import VueECharts from 'vue-echarts'

const props = defineProps({
  data: {
    type: Array,
    default: () => []
  },
  loading: Boolean
})

const typeOptions = [
  { value: '1', label: '血压' },
  { value: '2', label: '血糖' },
  { value: '3', label: '体温' },
  { value: '4', label: '心率' }
]
const selectedType = ref('1')
const chartRef = ref(null)
const chartReady = ref(false)
const chartContainerRef = ref(null)
const isChartVisible = ref(false)

const filteredData = computed(() => {
  return props.data
    .filter(item => item.monitoringType === selectedType.value)
    .sort((a, b) => new Date(a.monitoringTime) - new Date(b.monitoringTime))
})

watch(selectedType, (val) => {
  if (filteredData.value.length === 0) {
    ElMessage.info('当前类型暂无数据')
  }
})

const chartOption = computed(() => {
  let yData = []
  let xData = []
  let series = []
  if (selectedType.value === '1') {
    // 血压，拆分收缩压/舒张压
    yData = filteredData.value.map(item => {
      if (item.monitoringValue && item.monitoringValue.includes('/')) {
        return item.monitoringValue.split('/').map(Number)
      }
      return [null, null]
    })
    xData = filteredData.value.map(item => item.monitoringTime?.slice(0, 16) || '')
    series = [
      {
        name: '收缩压',
        type: 'line',
        data: yData.map(arr => arr[0]),
        smooth: true
      },
      {
        name: '舒张压',
        type: 'line',
        data: yData.map(arr => arr[1]),
        smooth: true
      }
    ]
  } else {
    yData = filteredData.value.map(item => Number(item.monitoringValue))
    xData = filteredData.value.map(item => item.monitoringTime?.slice(0, 16) || '')
    series = [
      {
        name: typeOptions.find(t => t.value === selectedType.value)?.label,
        type: 'line',
        data: yData,
        smooth: true
      }
    ]
  }
  return {
    title: { text: typeOptions.find(t => t.value === selectedType.value)?.label + '趋势', left: 'center', top: 10, textStyle: { fontSize: 16 } },
    tooltip: { trigger: 'axis' },
    legend: { top: 40 },
    grid: { left: 40, right: 20, bottom: 40, top: 70 },
    xAxis: { type: 'category', data: xData, boundaryGap: false },
    yAxis: { type: 'value', min: 'dataMin', max: 'dataMax' },
    series
  }
})

function onChartReady() {
  chartReady.value = true
}

function checkChartVisible() {
  const el = chartContainerRef.value
  if (el && el.clientWidth > 0 && el.clientHeight > 0) {
    isChartVisible.value = true
  } else {
    isChartVisible.value = false
    requestAnimationFrame(checkChartVisible)
  }
}

onMounted(() => {
  requestAnimationFrame(checkChartVisible)
  if (filteredData.value.length === 0 && props.data.length > 0) {
    const firstTypeWithData = typeOptions.find(opt => props.data.some(item => item.monitoringType === opt.value))
    if (firstTypeWithData) {
      selectedType.value = firstTypeWithData.value
    }
  }
})

watch([filteredData, selectedType], async () => {
  chartReady.value = false
  await nextTick()
  setTimeout(() => {
    checkChartVisible()
    chartReady.value = true
    for (let i = 0; i < 3; i++) {
      setTimeout(() => resizeChart(), i * 100)
    }
  }, 50)
})

function exportChart() {
  const chart = chartRef.value?.getEchartsInstance?.()
  if (chart) {
    const url = chart.getDataURL({ type: 'png' })
    const a = document.createElement('a')
    a.href = url
    a.download = `${typeOptions.find(t => t.value === selectedType.value)?.label || '趋势图'}.png`
    a.click()
  } else {
    ElMessage.warning('图表尚未渲染，无法导出')
  }
}

function resizeChart(retry = 10) {
  // 兼容 vue-echarts 2.x/3.x/4.x
  const chartDom = chartRef.value?.$el || chartRef.value?.getDom?.() || chartRef.value?.$el?.parentNode
  if (!chartDom) return
  if (chartDom.clientWidth === 0 || chartDom.clientHeight === 0) {
    if (retry > 0) {
      setTimeout(() => resizeChart(retry - 1), 200)
    }
    return
  }
  const chart = chartRef.value?.getEchartsInstance?.()
  if (chart) {
    chart.resize()
  }
}

defineExpose({ resizeChart })
</script>

<style scoped>
.trend-chart-card {
  margin-bottom: 24px;
  border-radius: 8px;
}
.trend-chart-header {
  display: flex;
  align-items: center;
  font-weight: 500;
  font-size: 16px;
  margin-bottom: 8px;
}
</style> 