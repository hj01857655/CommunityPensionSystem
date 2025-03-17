<template>
  <div class="search-bar">
    <el-form :inline="true" :model="searchForm" @submit.prevent="handleSearch">
      <slot :form="searchForm"></slot>
      
      <el-form-item>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          查询
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
        <slot name="extra-buttons"></slot>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { reactive } from 'vue';
import { Search, Refresh } from '@element-plus/icons-vue';

const props = defineProps({
  fields: {
    type: Object,
    default: () => ({})
  }
});

const emit = defineEmits(['search', 'reset']);

const searchForm = reactive({ ...props.fields });

const handleSearch = () => {
  emit('search', { ...searchForm });
};

const handleReset = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = props.fields[key] || '';
  });
  emit('reset');
};
</script>

<style scoped>
.search-bar {
  margin-bottom: 20px;
}
</style> 