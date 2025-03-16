<!-- 菜单组件 -->
<template>
  <el-menu
    :default-active="activeMenu"
    :collapse="isCollapse"
    :unique-opened="true"
    :collapse-transition="false"
    :router="true"
    class="el-menu-vertical"
  >
    <menu-item
      v-for="menu in menuList"
      :key="menu.path"
      :menu="menu"
      :base-path="menu.path"
    />
  </el-menu>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import MenuItem from './MenuItem.vue'

const props = defineProps({
  isCollapse: {
    type: Boolean,
    default: false
  },
  menuList: {
    type: Array,
    required: true
  }
})

const route = useRoute()

// 当前激活的菜单
const activeMenu = computed(() => {
  const { path, meta } = route
  if (meta.activeMenu) {
    return meta.activeMenu
  }
  return path
})
</script>

<style scoped>
.el-menu-vertical {
  height: 100%;
  border-right: none;
}

.el-menu-vertical:not(.el-menu--collapse) {
  width: 200px;
}
</style> 