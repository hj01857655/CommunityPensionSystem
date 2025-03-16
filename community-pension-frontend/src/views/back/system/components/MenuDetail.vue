<!-- 菜单详情组件 -->
<template>
  <div class="menu-detail">
    <el-descriptions :column="1" border>
      <el-descriptions-item label="菜单类型">
        {{ formData?.type === 1 ? '菜单' : '目录' }}
      </el-descriptions-item>

      <el-descriptions-item label="父级菜单">
        {{ formData?.parentName }}
      </el-descriptions-item>

      <el-descriptions-item label="菜单名称">
        {{ formData?.meta?.title }}
      </el-descriptions-item>

      <el-descriptions-item label="组件">
        {{ formData?.component === '#' ? '顶级目录' : formData?.component === '##' ? '子目录' : formData?.component }}
      </el-descriptions-item>

      <el-descriptions-item label="组件名称">
        {{ formData?.name }}
      </el-descriptions-item>

      <el-descriptions-item label="图标" v-if="formData?.meta?.icon">
        <el-icon>
          <component :is="formData.meta.icon" />
        </el-icon>
        <span class="ml-2">{{ formData?.meta?.icon }}</span>
      </el-descriptions-item>

      <el-descriptions-item label="路径">
        {{ formData?.path }}
      </el-descriptions-item>

      <el-descriptions-item label="高亮菜单">
        {{ formData?.meta?.activeMenu }}
      </el-descriptions-item>

      <el-descriptions-item label="按钮权限" v-if="formData?.permissionList?.length">
        <el-space wrap>
          <el-tag 
            v-for="permission in formData.permissionList" 
            :key="permission.value"
            class="mr-1"
          >
            {{ permission.label }}
          </el-tag>
        </el-space>
      </el-descriptions-item>

      <el-descriptions-item label="菜单状态">
        <el-tag :type="formData?.menuState ? 'success' : 'danger'">
          {{ formData?.menuState ? '启用' : '禁用' }}
        </el-tag>
      </el-descriptions-item>

      <el-descriptions-item label="是否隐藏">
        <el-tag :type="!formData?.meta?.hidden ? 'success' : 'danger'">
          {{ !formData?.meta?.hidden ? '显示' : '隐藏' }}
        </el-tag>
      </el-descriptions-item>

      <el-descriptions-item label="是否一直显示">
        <el-tag :type="formData?.meta?.alwaysShow ? 'success' : 'danger'">
          {{ formData?.meta?.alwaysShow ? '是' : '否' }}
        </el-tag>
      </el-descriptions-item>

      <el-descriptions-item label="是否清除缓存">
        <el-tag :type="formData?.meta?.noCache ? 'success' : 'danger'">
          {{ formData?.meta?.noCache ? '是' : '否' }}
        </el-tag>
      </el-descriptions-item>

      <el-descriptions-item label="是否显示面包屑">
        <el-tag :type="formData?.meta?.breadcrumb ? 'success' : 'danger'">
          {{ formData?.meta?.breadcrumb ? '是' : '否' }}
        </el-tag>
      </el-descriptions-item>

      <el-descriptions-item label="是否固定标签页">
        <el-tag :type="formData?.meta?.affix ? 'success' : 'danger'">
          {{ formData?.meta?.affix ? '是' : '否' }}
        </el-tag>
      </el-descriptions-item>

      <el-descriptions-item label="是否隐藏标签页">
        <el-tag :type="formData?.meta?.noTagsView ? 'success' : 'danger'">
          {{ formData?.meta?.noTagsView ? '是' : '否' }}
        </el-tag>
      </el-descriptions-item>

      <el-descriptions-item label="是否可跳转">
        <el-tag :type="formData?.meta?.canTo ? 'success' : 'danger'">
          {{ formData?.meta?.canTo ? '是' : '否' }}
        </el-tag>
      </el-descriptions-item>
    </el-descriptions>
  </div>
</template>

<script setup>
defineProps({
  formData: {
    type: Object,
    default: () => null
  }
});
</script>

<style scoped>
.menu-detail {
  padding: 20px;
}

.ml-2 {
  margin-left: 8px;
}

.mr-1 {
  margin-right: 4px;
}
</style> 