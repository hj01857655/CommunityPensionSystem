<template>
  <div class="top-right-btn">
    <el-row>
      <el-tooltip class="item" effect="dark" content="刷新" placement="top">
        <el-button circle icon="Refresh" @click="refresh"></el-button>
      </el-tooltip>
      <el-tooltip class="item" effect="dark" :content="showSearch ? '隐藏搜索' : '显示搜索'" placement="top">
        <el-button circle icon="Search" @click="showSearch = !showSearch"></el-button>
      </el-tooltip>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const props = defineProps({
  showSearch: {
    type: Boolean,
    default: true
  }
});

const emit = defineEmits(['update:showSearch', 'queryTable']);

const refresh = () => {
  emit('queryTable');
};
</script>

<style scoped>
.top-right-btn {
  display: inline-block;
  margin-left: 10px;
}

.top-right-btn .el-button {
  margin-left: 5px;
}
</style> 