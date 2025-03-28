<!-- 图标选择组件 -->
<template>
  <div class="icon-select">
    <el-popover
      v-model:visible="visible"
      placement="bottom-start"
      :width="540"
      trigger="click"
    >
      <template #reference>
        <el-input
          v-model="search"
          placeholder="点击选择图标"
          :prefix-icon="selectedIcon"
          readonly
          @click="visible = true"
        />
      </template>
      <div class="icon-container">
        <el-input
          v-model="search"
          placeholder="搜索图标"
          clearable
          @input="filterIcons"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <div class="icon-list">
          <div
            v-for="icon in filteredIcons"
            :key="icon"
            class="icon-item"
            :class="{ active: selectedIcon === icon }"
            @click="selectIcon(icon)"
          >
            <el-icon><component :is="icon" /></el-icon>
            <span class="icon-name">{{ icon }}</span>
          </div>
        </div>
      </div>
    </el-popover>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { Search } from '@element-plus/icons-vue';
import * as ElementPlusIcons from '@element-plus/icons-vue';

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  }
});

const emit = defineEmits(['update:modelValue']);

const visible = ref(false);
const search = ref('');
const selectedIcon = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
});

// 获取所有图标
const allIcons = Object.keys(ElementPlusIcons);

// 过滤后的图标列表
const filteredIcons = computed(() => {
  if (!search.value) return allIcons;
  return allIcons.filter(icon => 
    icon.toLowerCase().includes(search.value.toLowerCase())
  );
});

// 选择图标
const selectIcon = (icon) => {
  selectedIcon.value = icon;
  visible.value = false;
};

// 过滤图标
const filterIcons = () => {
  // 搜索逻辑已通过计算属性实现
};
</script>

<style scoped>
.icon-select {
  display: inline-block;
  width: 100%;
}

.icon-container {
  padding: 10px;
}

.icon-list {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 8px;
  margin-top: 10px;
  max-height: 300px;
  overflow-y: auto;
}

.icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s;
}

.icon-item:hover {
  background-color: var(--el-color-primary-light-9);
}

.icon-item.active {
  background-color: var(--el-color-primary-light-8);
}

.icon-name {
  font-size: 12px;
  margin-top: 4px;
  color: var(--el-text-color-secondary);
  word-break: break-all;
  text-align: center;
}
</style> 