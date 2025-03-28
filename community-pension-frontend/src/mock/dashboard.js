// 生成过去n天的日期数组
const generateDates = (days) => {
  const dates = []
  for (let i = days - 1; i >= 0; i--) {
    const date = new Date()
    date.setDate(date.getDate() - i)
    dates.push(date.toLocaleDateString())
  }
  return dates
}

// 生成随机数据
const generateRandomData = (min, max, count) => {
  return Array.from({ length: count }, () => 
    Math.floor(Math.random() * (max - min + 1)) + min
  )
}

// 生成趋势数据
export const getTrendData = (type = 'week') => {
  const days = type === 'week' ? 7 : 30
  const dates = generateDates(days)
  return {
    dates,
    newUsers: generateRandomData(10, 100, days),
    activeUsers: generateRandomData(50, 200, days)
  }
}

// 生成活动类型分布数据
export const getActivityTypes = () => {
  return [
    { value: 335, name: '文化娱乐' },
    { value: 234, name: '健康讲座' },
    { value: 158, name: '体育运动' },
    { value: 135, name: '志愿服务' },
    { value: 120, name: '技能培训' }
  ]
}

// 生成统计数据
export const getStatistics = () => {
  return [
    { 
      title: '总用户数', 
      value: Math.floor(Math.random() * 2000) + 1000,
      trend: (Math.random() * 30 - 15).toFixed(1),
      icon: 'User'
    },
    { 
      title: '活跃用户', 
      value: Math.floor(Math.random() * 1000) + 500,
      trend: (Math.random() * 30 - 15).toFixed(1),
      icon: 'Timer'
    },
    { 
      title: '总活动数', 
      value: Math.floor(Math.random() * 200) + 100,
      trend: (Math.random() * 30 - 15).toFixed(1),
      icon: 'List'
    },
    { 
      title: '进行中活动', 
      value: Math.floor(Math.random() * 30) + 10,
      trend: (Math.random() * 30 - 15).toFixed(1),
      icon: 'Bell'
    }
  ]
}

// 生成活动列表数据
export const getActivities = (count = 3) => {
  const status = ['未开始', '进行中', '已结束']
  const activities = [
    '春季健康讲座', '老年人手机使用培训', '社区棋牌比赛',
    '广场舞比赛', '义工服务日', '手工艺制作班',
    '太极拳教学', '书法班', '合唱团排练'
  ]
  
  return Array.from({ length: count }, () => ({
    name: activities[Math.floor(Math.random() * activities.length)],
    date: new Date(Date.now() + (Math.random() * 10 - 5) * 86400000)
      .toLocaleString('zh-CN', { 
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      }),
    participants: Math.floor(Math.random() * 50) + 10,
    status: status[Math.floor(Math.random() * status.length)]
  }))
}

// 生成年龄分布数据
export const getAgeDistribution = () => {
  return [
    { range: '60-65岁', count: Math.floor(Math.random() * 200) + 300 },
    { range: '66-70岁', count: Math.floor(Math.random() * 200) + 350 },
    { range: '71-75岁', count: Math.floor(Math.random() * 150) + 200 },
    { range: '76-80岁', count: Math.floor(Math.random() * 100) + 100 },
    { range: '80岁以上', count: Math.floor(Math.random() * 50) + 20 }
  ]
}

// 生成服务满意度数据
export const getSatisfaction = () => {
  return {
    excellent: Math.floor(Math.random() * 20) + 40, // 40-60%
    good: Math.floor(Math.random() * 15) + 25, // 25-40%
    average: Math.floor(Math.random() * 10) + 10, // 10-20%
    poor: Math.floor(Math.random() * 5) + 1 // 1-6%
  }
} 