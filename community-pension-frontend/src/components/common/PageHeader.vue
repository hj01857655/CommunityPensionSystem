<!-- 页面头部组件 -->
<template>
  <div class="page-header">
    <div class="header-title">
      <h3>{{ title }}</h3>
    </div>
    <div class="header-actions" v-if="showSearch || showAdd">
      <el-input
        v-if="showSearch"
        v-model="searchQuery"
        placeholder="请输入搜索关键词"
        class="search-input"
        clearable
        @clear="handleSearch"
        @keyup.enter="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-button v-if="showSearch" type="primary" @click="handleSearch">
        <el-icon><Search /></el-icon>
        搜索
      </el-button>
      <el-button v-if="showAdd" type="success" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { Search, Plus } from '@element-plus/icons-vue';

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  showSearch: {
    type: Boolean,
    default: false
  },
  showAdd: {
    type: Boolean,
    default: true
  }
});

const emit = defineEmits(['search', 'add']);

const searchQuery = ref('');

const handleSearch = () => {
  emit('search', searchQuery.value);
};

const handleAdd = () => {
  emit('add');
};
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-title h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 500;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.search-input {
  width: 200px;
}
</style> 