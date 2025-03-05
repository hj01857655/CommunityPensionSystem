<template>
    <el-card 
      :class="['preview-card', cardClass]" 
      shadow="hover"
    >
      <template #header>
        <div class="card-header">
          <span class="card-title">
            <el-icon><component :is="icon" /></el-icon>
            {{ title }}
          </span>
          <el-button 
            v-if="showMore"
            link 
            type="primary" 
            class="more-btn" 
            @click="emitMore"
          >
            更多 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </template>
      <slot></slot>
    </el-card>
  </template>
  
  <script setup>
  import { ArrowRight } from '@element-plus/icons-vue';
  
  const props = defineProps({
    title: {
      type: String,
      required: true
    },
    icon: {
      type: String,
      required: true
    },
    cardClass: {
      type: String,
      default: ''
    },
    showMore: {
      type: Boolean,
      default: true
    }
  })
  
  const emit = defineEmits(['more'])
  
  const emitMore = () => {
    emit('more')
  }
  </script>
  
  <style scoped>
  .preview-card {
    min-height: 220px;
    border-radius: 8px;
    transition: all 0.3s ease;
    background-color: #fff;
    overflow: hidden;
    border: 1px solid #e0e0e0;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
  
  .preview-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 16px;
    font-weight: bold;
    color: #2c3e50;
    padding: 16px;
    background-color: #f5f5f5;
  }
  
  .card-title {
    display: flex;
    align-items: center;
    gap: 8px;
  }
  
  .more-btn {
    padding: 0;
    font-size: 14px;
    color: var(--el-color-primary);
    display: flex;
    align-items: center;
    gap: 4px;
    transition: opacity 0.3s ease;
  }
  
  .more-btn:hover {
    opacity: 0.8;
  }
  </style>