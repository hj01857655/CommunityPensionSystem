<template>
  <div class="admin-page">
    <!-- 页面标题和操作区域 -->
    <el-card class="header-card" shadow="hover">
      <div class="page-header">
        <div class="title-section">
          <h2 class="page-title">{{ title }}</h2>
          <span v-if="subtitle" class="page-subtitle">{{ subtitle }}</span>
        </div>
        
        <div class="action-section">
          <slot name="actions"></slot>
        </div>
      </div>
      
      <!-- 搜索区域 -->
      <div v-if="$slots.search" class="search-section">
        <slot name="search"></slot>
      </div>
    </el-card>
    
    <!-- 主内容区域 -->
    <el-card class="content-card" shadow="hover" :body-style="{ padding: '0px' }">
      <div class="table-toolbar" v-if="$slots.toolbar">
        <slot name="toolbar"></slot>
      </div>
      
      <div class="table-container">
        <slot></slot>
      </div>
      
      <div class="pagination-container" v-if="$slots.pagination">
        <slot name="pagination"></slot>
      </div>
    </el-card>
    
    <!-- 辅助内容、弹窗等 -->
    <slot name="extra"></slot>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true
  },
  subtitle: {
    type: String,
    default: ''
  }
});
</script>

<style scoped>
.admin-page {
  padding: 20px;
}

.header-card {
  margin-bottom: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.title-section {
  display: flex;
  flex-direction: column;
}

.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.page-subtitle {
  margin-top: 8px;
  font-size: 14px;
  color: #909399;
}

.action-section {
  display: flex;
  gap: 10px;
}

.search-section {
  margin-top: 10px;
}

.content-card {
  margin-bottom: 20px;
}

.table-toolbar {
  padding: 15px;
  border-bottom: 1px solid #ebeef5;
  background-color: #f9fafc;
}

.table-container {
  position: relative;
  min-height: 200px;
}

.pagination-container {
  padding: 15px;
  display: flex;
  justify-content: flex-end;
  background-color: #fff;
  border-top: 1px solid #ebeef5;
}
</style> 