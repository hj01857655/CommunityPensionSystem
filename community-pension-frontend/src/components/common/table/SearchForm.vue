<template>
  <div class="search-form-container">
    <el-form :model="formData" :inline="true" class="search-form">
      <slot></slot>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
        <slot name="buttons"></slot>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { Search, Refresh } from '@element-plus/icons-vue';
import { reactive, watch } from 'vue';

const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({})
  },
  // 是否在表单值变化时自动搜索
  autoSearch: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['update:modelValue', 'search', 'reset']);

// 表单数据，使用响应式对象
const formData = reactive({
  ...props.modelValue
});

// 监听 modelValue 变化，更新表单数据
watch(() => props.modelValue, (newVal) => {
  Object.keys(formData).forEach(key => {
    delete formData[key];
  });
  Object.assign(formData, newVal);
}, { deep: true });

// 监听表单数据变化，更新 modelValue
watch(formData, (newVal) => {
  emit('update:modelValue', { ...newVal });
  if (props.autoSearch) {
    emit('search', { ...newVal });
  }
}, { deep: true });

// 处理搜索
const handleSearch = () => {
  emit('search', { ...formData });
};

// 处理重置
const handleReset = () => {
  Object.keys(formData).forEach(key => {
    formData[key] = undefined;
  });
  emit('reset');
  emit('search', { ...formData });
};
</script>

<style scoped>
.search-form-container {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}
</style>
