import { useState, useEffect, useMemo, useRef } from 'react'
import { Card, Select, Button, Empty, message } from 'antd'
import { DownloadOutlined } from '@ant-design/icons'
import ReactECharts from 'echarts-for-react'
import type { EChartsOption } from 'echarts'
import type { HealthRecord } from '@/types/health'

interface HealthTrendChartProps {
  data: HealthRecord[]
  loading?: boolean
}

/**
 * Health Trend Chart Component
 * 
 * Features:
 * - Multi-type health monitoring trend visualization
 * - Blood pressure dual-line chart (systolic/diastolic)
 * - Interactive type selection
 * - Export chart as image
 * - Responsive design
 */
const HealthTrendChart: React.FC<HealthTrendChartProps> = ({ data = [], loading = false }) => {
  const chartRef = useRef<ReactECharts>(null)
  const [selectedType, setSelectedType] = useState('1')

  const typeOptions = [
    { value: '1', label: '血压' },
    { value: '2', label: '血糖' },
    { value: '3', label: '体温' },
    { value: '4', label: '心率' },
  ]

  // Filter and transform data by selected type
  const chartData = useMemo(() => {
    return data
      .filter((item) => {
        // Filter by type: check if the field has a value
        switch (selectedType) {
          case '1':
            return item.bloodPressure
          case '2':
            return item.bloodSugar
          case '3':
            return item.temperature
          case '4':
            return item.heartRate
          default:
            return false
        }
      })
      .sort((a, b) => new Date(a.recordTime).getTime() - new Date(b.recordTime).getTime())
  }, [data, selectedType])

  // Generate chart option
  const chartOption = useMemo((): EChartsOption => {
    const selectedLabel = typeOptions.find((t) => t.value === selectedType)?.label || ''
    
    if (chartData.length === 0) {
      return {}
    }

    const xData = chartData.map((item) => {
      const time = item.recordTime || ''
      return time.slice(0, 16) // Format: YYYY-MM-DD HH:mm
    })

    let series: any[] = []

    if (selectedType === '1') {
      // Blood pressure - dual line (systolic/diastolic)
      const systolicData: number[] = []
      const diastolicData: number[] = []

      chartData.forEach((item) => {
        if (item.bloodPressure && item.bloodPressure.includes('/')) {
          const [sys, dia] = item.bloodPressure.split('/').map(Number)
          systolicData.push(sys)
          diastolicData.push(dia)
        } else {
          systolicData.push(0)
          diastolicData.push(0)
        }
      })

      series = [
        {
          name: '收缩压',
          type: 'line',
          data: systolicData,
          smooth: true,
          itemStyle: { color: '#f5222d' },
          lineStyle: { width: 2 },
        },
        {
          name: '舒张压',
          type: 'line',
          data: diastolicData,
          smooth: true,
          itemStyle: { color: '#1890ff' },
          lineStyle: { width: 2 },
        },
      ]
    } else {
      // Single line for other types
      const yData = chartData.map((item) => {
        switch (selectedType) {
          case '2':
            return Number(item.bloodSugar) || 0
          case '3':
            return Number(item.temperature) || 0
          case '4':
            return Number(item.heartRate) || 0
          default:
            return 0
        }
      })

      series = [
        {
          name: selectedLabel,
          type: 'line',
          data: yData,
          smooth: true,
          itemStyle: { color: '#52c41a' },
          lineStyle: { width: 2 },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                { offset: 0, color: 'rgba(82, 196, 26, 0.3)' },
                { offset: 1, color: 'rgba(82, 196, 26, 0.05)' },
              ],
            },
          },
        },
      ]
    }

    return {
      title: {
        text: `${selectedLabel}趋势`,
        left: 'center',
        top: 10,
        textStyle: {
          fontSize: 16,
          fontWeight: 500,
        },
      },
      tooltip: {
        trigger: 'axis',
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        borderColor: '#ddd',
        borderWidth: 1,
        textStyle: {
          color: '#333',
        },
      },
      legend: {
        top: 40,
        data: series.map((s) => s.name),
      },
      grid: {
        left: 50,
        right: 30,
        bottom: 50,
        top: 80,
        containLabel: true,
      },
      xAxis: {
        type: 'category',
        data: xData,
        boundaryGap: false,
        axisLine: {
          lineStyle: {
            color: '#d9d9d9',
          },
        },
        axisLabel: {
          color: '#666',
          fontSize: 12,
          rotate: 30,
        },
      },
      yAxis: {
        type: 'value',
        axisLine: {
          show: false,
        },
        axisTick: {
          show: false,
        },
        axisLabel: {
          color: '#666',
        },
        splitLine: {
          lineStyle: {
            color: '#f0f0f0',
            type: 'dashed',
          },
        },
      },
      series,
    }
  }, [chartData, selectedType])

  // Export chart as image
  const exportChart = () => {
    const chartInstance = chartRef.current?.getEchartsInstance()
    if (chartInstance) {
      const url = chartInstance.getDataURL({
        type: 'png',
        pixelRatio: 2,
        backgroundColor: '#fff',
      })
      const a = document.createElement('a')
      a.href = url
      a.download = `${typeOptions.find((t) => t.value === selectedType)?.label || '趋势图'}.png`
      a.click()
      message.success('图表已导出')
    } else {
      message.warning('图表尚未渲染，无法导出')
    }
  }

  // Handle type change
  const handleTypeChange = (value: string) => {
    setSelectedType(value)
    const hasData = data.some((item) => {
      switch (value) {
        case '1':
          return item.bloodPressure
        case '2':
          return item.bloodSugar
        case '3':
          return item.temperature
        case '4':
          return item.heartRate
        default:
          return false
      }
    })
    if (!hasData) {
      message.info('当前类型暂无数据')
    }
  }

  // Auto-select first available type on mount
  useEffect(() => {
    if (data.length > 0) {
      const firstAvailableType = typeOptions.find((opt) =>
        data.some((item) => {
          switch (opt.value) {
            case '1':
              return item.bloodPressure
            case '2':
              return item.bloodSugar
            case '3':
              return item.temperature
            case '4':
              return item.heartRate
            default:
              return false
          }
        })
      )
      if (firstAvailableType) {
        setSelectedType(firstAvailableType.value)
      }
    }
  }, [data])

  return (
    <Card
      className="mb-6 rounded-lg"
      bodyStyle={{ padding: '16px' }}
      loading={loading}
    >
      <div className="flex items-center justify-between mb-4">
        <span className="font-medium text-base">健康趋势图</span>
        <Select
          value={selectedType}
          onChange={handleTypeChange}
          style={{ width: 120 }}
          size="small"
        >
          {typeOptions.map((option) => (
            <Select.Option key={option.value} value={option.value}>
              {option.label}
            </Select.Option>
          ))}
        </Select>
      </div>

      <div style={{ height: 320, position: 'relative' }}>
        {chartData.length > 0 ? (
          <>
            <ReactECharts
              ref={chartRef}
              option={chartOption}
              style={{ height: '100%', width: '100%' }}
              opts={{ renderer: 'canvas' }}
            />
            <Button
              size="small"
              icon={<DownloadOutlined />}
              onClick={exportChart}
              style={{
                position: 'absolute',
                right: 10,
                top: 10,
                zIndex: 2,
              }}
            >
              导出图片
            </Button>
          </>
        ) : (
          <div className="h-full flex items-center justify-center">
            <Empty description="暂无数据" image={Empty.PRESENTED_IMAGE_SIMPLE} />
          </div>
        )}
      </div>
    </Card>
  )
}

export default HealthTrendChart
