<!-- 菜单管理 -->
<template>
  <div class="menu-management">
    <el-card class="table-card">
      <!-- 搜索和操作栏 -->
      <div class="operation-bar">
        <div class="search-bar">
          <el-input
            v-model="searchQuery"
            placeholder="请输入菜单名称"
            class="search-input"
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="resetSearch">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </div>
        <div class="action-bar">
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增
          </el-button>
        </div>
      </div>

      <!-- 菜单表格 -->
      <el-table
        :data="tableData"
        style="width: 100%"
        v-loading="loading"
        row-key="id"
        border
        default-expand-all
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="title" label="菜单名称" min-width="180" />
        <el-table-column prop="icon" label="图标" width="80" align="center">
          <template #default="{ row }">
            <el-icon v-if="row.icon">
              <component :is="row.icon" />
            </el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="component" label="组件路径" min-width="180">
          <template #default="{ row }">
            {{ row.component === '#' ? '顶级目录' : row.component === '##' ? '子目录' : row.component }}
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由地址" min-width="180" />
        <el-table-column prop="permission" label="权限标识" min-width="150" />
        <el-table-column prop="sort" label="排序" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status ? 'success' : 'danger'">
              {{ row.status ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="success" size="small" @click="handleDetail(row)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增菜单' : dialogType === 'edit' ? '编辑菜单' : '查看菜单'"
      width="600px"
      destroy-on-close
    >
      <component
        :is="dialogComponent"
        ref="formRef"
        :form-data="currentRow"
        :disabled="dialogType === 'detail'"
      />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button v-if="dialogType !== 'detail'" type="primary" @click="handleSubmit">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Plus, Edit, Delete, View, Refresh } from '@element-plus/icons-vue';
import MenuForm from './components/MenuForm.vue';
import MenuDetail from './components/MenuDetail.vue';

// 表格数据
const loading = ref(false);
const tableData = ref([
  {
    id: 1,
    title: '系统管理',
    icon: 'Setting',
    component: '#',
    path: '/system',
    permission: 'system',
    sort: 1,
    status: true,
    children: [
      {
        id: 2,
        title: '用户管理',
        icon: 'User',
        component: 'system/UserManagement',
        path: '/system/user',
        permission: 'system:user',
        sort: 1,
        status: true
      },
      {
        id: 3,
        title: '角色管理',
        icon: 'UserFilled',
        component: 'system/RoleManagement',
        path: '/system/role',
        permission: 'system:role',
        sort: 2,
        status: true
      },
      {
        id: 4,
        title: '菜单管理',
        icon: 'Menu',
        component: 'system/MenuManagement',
        path: '/system/menu',
        permission: 'system:menu',
        sort: 3,
        status: true
      }
    ]
  }
]);

// 搜索相关
const searchQuery = ref('');

// 对话框相关
const dialogVisible = ref(false);
const dialogType = ref('add'); // add, edit, detail
const currentRow = ref(null);
const formRef = ref(null);

// 动态组件
const dialogComponent = computed(() => {
  return dialogType.value === 'detail' ? MenuDetail : MenuForm;
});

// 搜索
const handleSearch = () => {
  // TODO: 实现搜索逻辑
  console.log('搜索:', searchQuery.value);
};

// 重置搜索
const resetSearch = () => {
  searchQuery.value = '';
  handleSearch();
};

// 新增菜单
const handleAdd = () => {
  dialogType.value = 'add';
  currentRow.value = null;
  dialogVisible.value = true;
};

// 编辑菜单
const handleEdit = (row) => {
  dialogType.value = 'edit';
  currentRow.value = { ...row };
  dialogVisible.value = true;
};

// 查看菜单详情
const handleDetail = (row) => {
  dialogType.value = 'detail';
  currentRow.value = { ...row };
  dialogVisible.value = true;
};

// 删除菜单
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除菜单"${row.title}"吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    // TODO: 调用删除API
    ElMessage.success('删除成功');
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error);
      ElMessage.error('删除失败');
    }
  }
};

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return;
  
  try {
    const formData = await formRef.value.submit();
    if (formData) {
      // TODO: 调用保存API
      ElMessage.success(dialogType.value === 'add' ? '新增成功' : '编辑成功');
      dialogVisible.value = false;
      // 重新加载数据
    }
  } catch (error) {
    console.error('表单提交失败:', error);
    ElMessage.error('操作失败');
  }
};
</script>

<style scoped>
.menu-management {
  padding: 20px;
}

.operation-bar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.search-bar {
  display: flex;
  gap: 10px;
}

.search-input {
  width: 200px;
}

.action-bar {
  display: flex;
  gap: 10px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 