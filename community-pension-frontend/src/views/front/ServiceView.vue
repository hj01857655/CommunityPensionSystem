<template>
    <div class="service-view">
      <el-card class="content-card" shadow="hover">
        <h3>服务预约</h3>
        <el-tabs v-model="activeTab">
          <el-tab-pane label="服务列表" name="list">
            <el-table :data="services" style="width: 100%">
              <el-table-column prop="name" label="服务名称" />
              <el-table-column prop="date" label="日期" />
              <el-table-column prop="status" label="状态">
                <template #default="{ row }">
                  <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作">
                <template #default="{ row }">
                  <el-button 
                    type="primary" 
                    size="small" 
                    :disabled="row.status === '已预约'"
                    @click="handleBook(row)"
                  >
                    预约
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
          <el-tab-pane label="我的预约" name="my">
            <el-timeline>
              <el-timeline-item
                v-for="item in myBookings"
                :key="item.id"
                :timestamp="item.date"
                :type="getTimelineType(item.status)"
              >
                {{ item.name }} - {{ item.status }}
              </el-timeline-item>
            </el-timeline>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted, onBeforeUnmount } from 'vue'
  import { ElMessage } from 'element-plus'
  import { debounce } from '@/utils/util'
  
  const activeTab = ref('list')
  
  const services = ref([
    { id: 1, name: '上门送餐', date: '2025-02-25', status: '可预约' },
    { id: 2, name: '健康体检', date: '2025-02-26', status: '已预约' },
    { id: 3, name: '康复护理', date: '2025-02-27', status: '可预约' },
  ])
  
  const myBookings = ref([
    { id: 1, name: '健康体检', date: '2025-02-26', status: '已确认' },
    { id: 2, name: '康复护理', date: '2025-02-20', status: '已完成' },
  ])
  
  const getStatusType = (status) => {
    const types = {
      '可预约': 'primary',
      '已预约': 'success',
      '已完成': 'info',
    }
    return types[status] || 'default'
  }
  
  const getTimelineType = (status) => {
    const types = {
      '已确认': 'primary',
      '已完成': 'success',
      '已取消': 'danger',
    }
    return types[status] || 'default'
  }
  
  const handleBook = (service) => {
    ElMessage.success(`预约成功：${service.name}`)
    service.status = '已预约'
  }
  
  
  const handleLayout = debounce(() => {
    
  }, 100)
  
  
  
  
  </script>
  
  <style scoped>
  .service-view {
    width: 100%;
  }
  
  .content-card {
    margin-bottom: 20px;
  }
  
  h3 {
    margin-bottom: 20px;
    color: #2c3e50;
    font-weight: 600;
  }
  </style>