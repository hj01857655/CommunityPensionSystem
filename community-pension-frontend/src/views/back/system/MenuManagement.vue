<!-- 菜单管理 -->
<template>
  <admin-page-template title="菜单管理" subtitle="管理系统的菜单结构和配置">
    <!-- 搜索栏 -->
    <template #search>
      <search-bar :fields="{ keyword: '' }" @search="handleSearch" @reset="resetSearch">
        <template #default="{ form }">
          <el-form-item label="菜单名称">
            <el-input v-model="form.keyword" placeholder="请输入菜单名称" clearable />
          </el-form-item>
        </template>
        
        <template #extra-buttons>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增菜单
          </el-button>
        </template>
      </search-bar>
    </template>
    
    <!-- 表格内容 -->
    <data-table
      :data="menuData"
      :loading="loading"
      row-key="id"
      default-expand-all
      :show-selection="false"
      :show-pagination="false"
      @edit="handleEdit"
      @delete="handleDelete"
      @view="handleView"
    >
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
    </data-table>
    
    <!-- 表单对话框 -->
    <template #extra>
      <form-dialog
        v-model:visible="dialogVisible"
        :title="dialogTitles[dialogType]"
        :form-type="dialogType"
        :data="currentRecord"
        :rules="formRules"
        width="600px"
        :loading="submitLoading"
        @submit="submitForm"
      >
        <template #default="{ form, disabled }">
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="上级菜单" prop="parentId">
                <el-tree-select
                  v-model="form.parentId"
                  :data="menuTreeData"
                  :props="{ label: 'title', value: 'id', children: 'children' }"
                  :disabled="disabled"
                  placeholder="请选择上级菜单"
                  check-strictly
                  clearable
                />
              </el-form-item>
            </el-col>
            
            <el-col :span="12">
              <el-form-item label="菜单名称" prop="title">
                <el-input v-model="form.title" :disabled="disabled" placeholder="请输入菜单名称" />
              </el-form-item>
            </el-col>
            
            <el-col :span="12">
              <el-form-item label="菜单图标" prop="icon">
                <el-input v-model="form.icon" :disabled="disabled" placeholder="请选择图标">
                  <template #prefix>
                    <el-icon v-if="form.icon">
                      <component :is="form.icon" />
                    </el-icon>
                  </template>
                  <template #append>
                    <el-button :disabled="disabled" @click="openIconSelector">
                      <el-icon><Select /></el-icon>
                    </el-button>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
            
            <el-col :span="12">
              <el-form-item label="路由地址" prop="path">
                <el-input v-model="form.path" :disabled="disabled" placeholder="请输入路由地址" />
              </el-form-item>
            </el-col>
            
            <el-col :span="12">
              <el-form-item label="组件路径" prop="component">
                <el-input v-model="form.component" :disabled="disabled" placeholder="请输入组件路径" />
              </el-form-item>
            </el-col>
            
            <el-col :span="12">
              <el-form-item label="权限标识" prop="permission">
                <el-input v-model="form.permission" :disabled="disabled" placeholder="请输入权限标识" />
              </el-form-item>
            </el-col>
            
            <el-col :span="12">
              <el-form-item label="排序" prop="sort">
                <el-input-number v-model="form.sort" :disabled="disabled" :min="0" :max="999" />
              </el-form-item>
            </el-col>
            
            <el-col :span="12">
              <el-form-item label="状态" prop="status">
                <el-switch
                  v-model="form.status"
                  :disabled="disabled"
                  :active-value="true"
                  :inactive-value="false"
                  inline-prompt
                  active-text="启用"
                  inactive-text="禁用"
                />
              </el-form-item>
            </el-col>
            
            <el-col :span="24">
              <el-form-item label="备注" prop="remark">
                <el-input
                  v-model="form.remark"
                  type="textarea"
                  :rows="3"
                  :disabled="disabled"
                  placeholder="请输入备注信息"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </template>
      </form-dialog>
      
      <!-- 图标选择器 (可以根据需要实现) -->
    </template>
  </admin-page-template>
</template>

<script setup>
import { ref, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Plus, Edit, Delete, View, Select } from '@element-plus/icons-vue';
import AdminPageTemplate from '@/components/admin/AdminPageTemplate.vue';
import SearchBar from '@/components/admin/SearchBar.vue';
import DataTable from '@/components/admin/DataTable.vue';
import FormDialog from '@/components/admin/FormDialog.vue';

// 数据及状态
const loading = ref(false);
const submitLoading = ref(false);
const dialogVisible = ref(false);
const dialogType = ref('add'); // 'add', 'edit', 'view'
const currentRecord = ref({});

// 菜单树形数据
const menuData = ref([
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

// 用于菜单选择器的树形数据
const menuTreeData = computed(() => {
  return [{ id: 0, title: '根菜单', children: JSON.parse(JSON.stringify(menuData.value)) }];
});

// 对话框标题
const dialogTitles = {
  add: '新增菜单',
  edit: '编辑菜单',
  view: '查看菜单详情'
};

// 表单验证规则
const formRules = {
  title: [
    { required: true, message: '请输入菜单名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  path: [
    { required: true, message: '请输入路由地址', trigger: 'blur' }
  ],
  component: [
    { required: true, message: '请输入组件路径', trigger: 'blur' }
  ],
  sort: [
    { required: true, message: '请输入排序值', trigger: 'blur' }
  ]
};

// 搜索
const handleSearch = (params) => {
  console.log('搜索菜单:', params);
  // 实现搜索逻辑
};

// 重置搜索
const resetSearch = () => {
  // 重置搜索条件并刷新数据
  fetchMenuList();
};

// 获取菜单列表
const fetchMenuList = async () => {
  loading.value = true;
  try {
    // 实际应用中应该从API获取数据
    // const response = await getMenuList();
    // menuData.value = response.data || [];
    // 模拟数据已经预设在menuData中
    await new Promise(resolve => setTimeout(resolve, 500));
  } catch (error) {
    console.error('获取菜单列表失败:', error);
    ElMessage.error('获取菜单列表失败');
  } finally {
    loading.value = false;
  }
};

// 新增菜单
const handleAdd = () => {
  dialogType.value = 'add';
  currentRecord.value = {
    parentId: null,
    title: '',
    icon: '',
    path: '',
    component: '',
    permission: '',
    sort: 0,
    status: true,
    remark: ''
  };
  dialogVisible.value = true;
};

// 编辑菜单
const handleEdit = (row) => {
  dialogType.value = 'edit';
  currentRecord.value = { ...row };
  dialogVisible.value = true;
};

// 查看菜单
const handleView = (row) => {
  dialogType.value = 'view';
  currentRecord.value = { ...row };
  dialogVisible.value = true;
};

// 删除菜单
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除菜单"${row.title}"吗？删除后不可恢复！`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    
    // 实际应用中应该调用API删除数据
    // await deleteMenu(row.id);
    ElMessage.success('删除成功');
    await fetchMenuList();
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除菜单失败:', error);
      ElMessage.error('删除失败');
    }
  }
};

// 提交表单
const submitForm = async (formData) => {
  submitLoading.value = true;
  try {
    if (dialogType.value === 'add') {
      // 实际应用中应该调用API创建数据
      // await createMenu(formData);
      ElMessage.success('新增成功');
    } else if (dialogType.value === 'edit') {
      // 实际应用中应该调用API更新数据
      // await updateMenu(formData);
      ElMessage.success('更新成功');
    }
    dialogVisible.value = false;
    await fetchMenuList();
  } catch (error) {
    console.error('提交表单失败:', error);
    ElMessage.error('操作失败');
  } finally {
    submitLoading.value = false;
  }
};

// 打开图标选择器
const openIconSelector = () => {
  // 实现图标选择器逻辑
  console.log('打开图标选择器');
};

// 页面初始化
onMounted(() => {
  fetchMenuList();
});
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