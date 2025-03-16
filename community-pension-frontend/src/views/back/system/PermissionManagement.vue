<!-- 权限管理 -->
<template>
  <div class="permission-management">
    <el-card class="table-card">
      <page-header 
        title="权限管理"
        :show-search="true"
        :show-add="true"
        @search="handleSearch"
        @add="handleAdd"
      />

      <!-- 权限分类筛选 -->
      <div class="category-filter">
        <span class="filter-label">权限分类：</span>
        <el-radio-group v-model="selectedCategory" @change="handleCategoryChange">
          <el-radio-button label="all">全部</el-radio-button>
          <el-radio-button 
            v-for="category in categories" 
            :key="category.value" 
            :label="category.value"
          >
            {{ category.label }}
          </el-radio-button>
        </el-radio-group>
      </div>

      <crud-table
        :data="filteredTableData"
        :columns="columns"
        :loading="loading"
        @edit="handleEdit"
        @delete="handleDelete"
      />

      <pagination-footer
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 新增/编辑权限对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增权限' : '编辑权限'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="权限名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限代码" prop="code">
          <el-input v-model="formData.code" placeholder="请输入权限代码" />
        </el-form-item>
        <el-form-item label="权限分类" prop="category">
          <el-select v-model="formData.category" placeholder="请选择权限分类">
            <el-option 
              v-for="item in categories" 
              :key="item.value" 
              :label="item.label" 
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="父级权限" prop="parentId">
          <el-cascader
            v-model="formData.parentId"
            :options="permissionOptions"
            :props="{ checkStrictly: true, label: 'name', value: 'id', emitPath: false }"
            clearable
            placeholder="请选择父级权限"
          />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="formData.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="权限描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            placeholder="请输入权限描述"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="formData.status"
            :active-value="true"
            :inactive-value="false"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import PageHeader from '@/components/common/PageHeader.vue';
import CrudTable from '@/components/common/CrudTable.vue';
import PaginationFooter from '@/components/common/PaginationFooter.vue';
import { getPermissionList, addPermission, updatePermission, deletePermission, getPermissionTree, getPermissionCategories } from '@/api/back/SystemManage/PermissionManage';

// 表格列配置
const columns = [
  { prop: 'id', label: '权限ID', width: '80' },
  { prop: 'name', label: '权限名称', width: '150' },
  { prop: 'code', label: '权限代码', width: '150' },
  { prop: 'category', label: '权限分类', width: '120' },
  { prop: 'sort', label: '排序', width: '80' },
  { prop: 'description', label: '权限描述' },
  { prop: 'status', label: '状态', width: '80', type: 'status' },
  { 
    prop: 'operation', 
    label: '操作', 
    width: '150', 
    fixed: 'right', 
    type: 'custom',
    render: (row) => {
      return h('div', { class: 'operation-buttons' }, [
        h('el-button', { type: 'primary', size: 'small', onClick: () => handleEdit(row) }, [
          h('el-icon', null, [h(Edit)]),
          '编辑'
        ]),
        h('el-button', { type: 'danger', size: 'small', onClick: () => handleDelete(row) }, [
          h('el-icon', null, [h(Delete)]),
          '删除'
        ])
      ]);
    }
  }
];

// 数据相关
const loading = ref(false);
const searchQuery = ref('');
const tableData = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 权限分类
const categories = ref([]);
const selectedCategory = ref('all');

// 权限树
const permissionOptions = ref([]);

// 表单相关
const dialogVisible = ref(false);
const dialogType = ref('add');
const formRef = ref(null);
const formData = ref({
  name: '',
  code: '',
  category: '',
  parentId: null,
  sort: 0,
  description: '',
  status: true
});

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '请输入权限名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入权限代码', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择权限分类', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入权限描述', trigger: 'blur' },
    { max: 200, message: '长度不能超过 200 个字符', trigger: 'blur' }
  ],
  sort: [
    { required: true, message: '请输入排序值', trigger: 'blur' }
  ]
};

// 过滤后的表格数据
const filteredTableData = computed(() => {
  let result = tableData.value;
  
  // 按分类筛选
  if (selectedCategory.value !== 'all') {
    result = result.filter(item => item.category === selectedCategory.value);
  }
  
  // 按关键词搜索
  if (searchQuery.value) {
    result = result.filter(item =>
      item.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      item.code.toLowerCase().includes(searchQuery.value.toLowerCase())
    );
  }
  
  return result;
});

// 初始化数据
onMounted(async () => {
  await Promise.all([
    fetchPermissionList(),
    fetchCategories(),
    fetchPermissionTree()
  ]);
});

// 获取权限列表
const fetchPermissionList = async () => {
  loading.value = true;
  try {
    const response = await getPermissionList({
      page: currentPage.value,
      size: pageSize.value,
      query: searchQuery.value
    });
    tableData.value = response.data.content || [];
    total.value = response.data.totalElements || 0;
  } catch (error) {
    console.error('获取权限列表失败:', error);
    ElMessage.error('获取权限列表失败');
  } finally {
    loading.value = false;
  }
};

// 获取权限分类
const fetchCategories = async () => {
  try {
    const response = await getPermissionCategories();
    categories.value = response.data || [];
  } catch (error) {
    console.error('获取权限分类失败:', error);
    ElMessage.error('获取权限分类失败');
  }
};

// 获取权限树
const fetchPermissionTree = async () => {
  try {
    const response = await getPermissionTree();
    permissionOptions.value = response.data || [];
  } catch (error) {
    console.error('获取权限树失败:', error);
    ElMessage.error('获取权限树失败');
  }
};

// 分类变更
const handleCategoryChange = () => {
  currentPage.value = 1;
};

// 搜索
const handleSearch = (query) => {
  searchQuery.value = query;
  currentPage.value = 1;
  fetchPermissionList();
};

// 新增权限
const handleAdd = () => {
  dialogType.value = 'add';
  formData.value = {
    name: '',
    code: '',
    category: '',
    parentId: null,
    sort: 0,
    description: '',
    status: true
  };
  dialogVisible.value = true;
};

// 编辑权限
const handleEdit = (row) => {
  dialogType.value = 'edit';
  formData.value = { ...row };
  dialogVisible.value = true;
};

// 删除权限
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除权限"${row.name}"吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    
    await deletePermission(row.id);
    ElMessage.success('删除成功');
    await fetchPermissionList();
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除权限失败:', error);
      ElMessage.error('删除失败');
    }
  }
};

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    
    if (dialogType.value === 'add') {
      await addPermission(formData.value);
      ElMessage.success('新增成功');
    } else {
      await updatePermission(formData.value);
      ElMessage.success('编辑成功');
    }
    
    dialogVisible.value = false;
    await fetchPermissionList();
  } catch (error) {
    console.error('表单提交失败:', error);
    ElMessage.error('操作失败');
  }
};

// 分页相关
const handleSizeChange = (val) => {
  pageSize.value = val;
  fetchPermissionList();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  fetchPermissionList();
};
</script>

<style scoped>
.permission-management {
  padding: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.category-filter {
  margin: 15px 0;
  display: flex;
  align-items: center;
}

.filter-label {
  margin-right: 10px;
  font-weight: 500;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.operation-buttons {
  display: flex;
  gap: 5px;
}
</style>