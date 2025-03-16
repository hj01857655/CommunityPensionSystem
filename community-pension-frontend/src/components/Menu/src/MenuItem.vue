<!-- 菜单项组件 -->
<template>
  <template v-if="!menu.hidden">
    <!-- 有子菜单 -->
    <el-sub-menu
      v-if="menu.children && menu.children.length > 0"
      :index="resolvePath(menu.path)"
    >
      <template #title>
        <menu-icon v-if="menu.meta?.icon" :icon="menu.meta.icon" />
        <span>{{ menu.meta?.title }}</span>
      </template>
      <menu-item
        v-for="child in menu.children"
        :key="child.path"
        :menu="child"
        :base-path="resolvePath(menu.path)"
      />
    </el-sub-menu>
    <!-- 无子菜单 -->
    <el-menu-item v-else :index="resolvePath(menu.path)">
      <menu-icon v-if="menu.meta?.icon" :icon="menu.meta.icon" />
      <template #title>{{ menu.meta?.title }}</template>
    </el-menu-item>
  </template>
</template>

<script setup>
import { computed } from 'vue'
import path from 'path-browserify'
import MenuIcon from './MenuIcon.vue'

const props = defineProps({
  menu: {
    type: Object,
    required: true
  },
  basePath: {
    type: String,
    default: ''
  }
})

// 解析路径
const resolvePath = (routePath) => {
  if (routePath.startsWith('/')) {
    return routePath
  }
  return path.resolve(props.basePath, routePath)
}
</script> 