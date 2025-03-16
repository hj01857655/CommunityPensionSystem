<!-- 菜单表单组件 -->
<template>
  <el-form
    ref="formRef"
    :model="form"
    :rules="rules"
    label-width="100px"
    :disabled="disabled"
  >
    <el-form-item label="菜单类型" prop="type">
      <el-radio-group v-model="form.type" @change="handleTypeChange">
        <el-radio-button :label="0">目录</el-radio-button>
        <el-radio-button :label="1">菜单</el-radio-button>
        <el-radio-button :label="2">按钮</el-radio-button>
      </el-radio-group>
    </el-form-item>

    <el-form-item label="上级菜单" prop="parentId">
      <el-tree-select
        v-model="form.parentId"
        :data="menuTree"
        :props="{ label: 'title', value: 'id', children: 'children' }"
        :default-expand-all="true"
        placeholder="请选择上级菜单"
        check-strictly
        clearable
        @change="handleParentChange"
      />
    </el-form-item>

    <el-form-item label="菜单名称" prop="title">
      <el-input v-model="form.meta.title" placeholder="请输入菜单名称" />
    </el-form-item>

    <el-form-item label="菜单图标" prop="icon" v-if="form.type !== 2">
      <el-input v-model="form.meta.icon" placeholder="请选择图标">
        <template #append>
          <el-button @click="openIconSelector">
            <el-icon><Select /></el-icon>
          </el-button>
        </template>
      </el-input>
    </el-form-item>

    <el-form-item label="组件路径" prop="component" v-if="form.type === 1">
      <el-input 
        v-model="form.component" 
        placeholder="请输入组件路径"
        :disabled="form.type === 0"
      />
    </el-form-item>

    <el-form-item label="路由名称" prop="name" v-if="form.type !== 2">
      <el-input v-model="form.name" placeholder="请输入路由名称" />
    </el-form-item>

    <el-form-item label="路由地址" prop="path" v-if="form.type !== 2">
      <el-input v-model="form.path" placeholder="请输入路由地址" />
    </el-form-item>

    <el-form-item label="权限标识" prop="permission" v-if="form.type !== 0">
      <el-table 
        :data="form.permissionList || []" 
        style="width: 100%"
        v-if="form.type === 1"
      >
        <el-table-column type="index" width="50" />
        <el-table-column prop="value" label="权限值">
          <template #default="{ row }">
            <el-input 
              v-if="row.editing" 
              v-model="row.value" 
              size="small"
              @blur="handlePermissionSave(row)"
            />
            <span v-else>{{ row.value }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="label" label="权限名称">
          <template #default="{ row }">
            <el-input 
              v-if="row.editing" 
              v-model="row.label" 
              size="small"
              @blur="handlePermissionSave(row)"
            />
            <el-tag v-else>{{ row.label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button 
              v-if="!row.editing" 
              type="primary" 
              link
              @click="handlePermissionEdit(row)"
            >
              编辑
            </el-button>
            <el-popconfirm
              title="确定删除该权限吗？"
              @confirm="handlePermissionDelete(row)"
            >
              <template #reference>
                <el-button type="danger" link>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-input 
        v-else 
        v-model="form.permission" 
        placeholder="请输入权限标识"
      />
      <div class="mt-2" v-if="form.type === 1">
        <el-button type="primary" @click="showPermissionDrawer = true">
          添加权限
        </el-button>
      </div>
    </el-form-item>

    <el-form-item label="排序" prop="sort">
      <el-input-number v-model="form.sort" :min="0" :max="999" />
    </el-form-item>

    <el-form-item label="状态" prop="status">
      <el-radio-group v-model="form.status">
        <el-radio :label="1">启用</el-radio>
        <el-radio :label="0">禁用</el-radio>
      </el-radio-group>
    </el-form-item>

    <el-divider>高级选项</el-divider>

    <el-form-item label="显示状态" prop="hidden">
      <el-switch
        v-model="form.meta.hidden"
        :active-value="false"
        :inactive-value="true"
      />
    </el-form-item>

    <el-form-item label="总是显示" prop="alwaysShow" v-if="form.type === 0">
      <el-switch v-model="form.meta.alwaysShow" />
    </el-form-item>

    <el-form-item label="缓存" prop="noCache" v-if="form.type === 1">
      <el-switch
        v-model="form.meta.noCache"
        :active-value="false"
        :inactive-value="true"
      />
    </el-form-item>

    <el-form-item label="面包屑" prop="breadcrumb" v-if="form.type !== 2">
      <el-switch v-model="form.meta.breadcrumb" />
    </el-form-item>

    <el-form-item label="固定标签" prop="affix" v-if="form.type === 1">
      <el-switch v-model="form.meta.affix" />
    </el-form-item>
  </el-form>

  <!-- 权限添加抽屉 -->
  <el-drawer
    v-model="showPermissionDrawer"
    title="添加权限"
    size="400px"
  >
    <el-form
      ref="permissionFormRef"
      :model="permissionForm"
      :rules="permissionRules"
      label-width="100px"
    >
      <el-form-item label="权限值" prop="value">
        <el-input v-model="permissionForm.value" placeholder="请输入权限值" />
      </el-form-item>
      <el-form-item label="权限名称" prop="label">
        <el-input v-model="permissionForm.label" placeholder="请输入权限名称" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div style="flex: auto">
        <el-button @click="showPermissionDrawer = false">取消</el-button>
        <el-button type="primary" @click="handlePermissionAdd">确定</el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { Select } from '@element-plus/icons-vue';

const props = defineProps({
  formData: {
    type: Object,
    default: () => null
  },
  disabled: {
    type: Boolean,
    default: false
  }
});

// 表单ref
const formRef = ref(null);
const permissionFormRef = ref(null);

// 缓存组件路径
const cacheComponent = ref('');

// 表单数据
const form = reactive({
  parentId: null,
  type: 0,
  name: '',
  title: '',
  component: '#',
  path: '',
  permission: '',
  permissionList: [],
  sort: 0,
  status: 1,
  meta: {
    title: '',
    icon: '',
    hidden: false,
    alwaysShow: false,
    noCache: false,
    breadcrumb: true,
    affix: false
  }
});

// 表单校验规则
const rules = {
  type: [{ required: true, message: '请选择菜单类型', trigger: 'change' }],
  'meta.title': [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  path: [{ required: true, message: '请输入路由地址', trigger: 'blur' }],
  name: [{ required: true, message: '请输入路由名称', trigger: 'blur' }],
  component: [{ required: true, message: '请输入组件路径', trigger: 'blur' }],
  sort: [{ required: true, message: '请输入排序', trigger: 'blur' }]
};

// 权限表单数据
const permissionForm = reactive({
  value: '',
  label: ''
});

// 权限表单校验规则
const permissionRules = {
  value: [{ required: true, message: '请输入权限值', trigger: 'blur' }],
  label: [{ required: true, message: '请输入权限名称', trigger: 'blur' }]
};

// 菜单树数据
const menuTree = ref([
  {
    id: 1,
    title: '系统管理',
    children: [
      {
        id: 2,
        title: '用户管理'
      },
      {
        id: 3,
        title: '角色管理'
      }
    ]
  }
]);

// 权限抽屉显示状态
const showPermissionDrawer = ref(false);

// 处理菜单类型变更
const handleTypeChange = (val) => {
  if (val === 0) {
    form.component = form.parentId ? '##' : '#';
  } else if (val === 1) {
    form.component = cacheComponent.value;
  }
};

// 处理上级菜单变更
const handleParentChange = (val) => {
  if (form.type === 0) {
    form.component = val ? '##' : '#';
  }
};

// 打开图标选择器
const openIconSelector = () => {
  // TODO: 实现图标选择器
  ElMessage.info('图标选择器开发中...');
};

// 处理权限编辑
const handlePermissionEdit = (row) => {
  row.editing = true;
};

// 处理权限保存
const handlePermissionSave = (row) => {
  row.editing = false;
};

// 处理权限删除
const handlePermissionDelete = (row) => {
  const index = form.permissionList.findIndex(item => item.value === row.value);
  if (index !== -1) {
    form.permissionList.splice(index, 1);
  }
};

// 处理权限添加
const handlePermissionAdd = async () => {
  if (!permissionFormRef.value) return;
  
  try {
    await permissionFormRef.value.validate();
    form.permissionList.push({
      ...permissionForm,
      id: Date.now()
    });
    permissionForm.value = '';
    permissionForm.label = '';
    showPermissionDrawer.value = false;
  } catch (error) {
    console.error('权限表单验证失败:', error);
  }
};

// 初始化表单数据
const initFormData = () => {
  if (props.formData) {
    const data = { ...props.formData };
    if (data.type === 1) {
      cacheComponent.value = data.component;
    }
    Object.assign(form, data);
  }
};

// 提交表单
const submit = async () => {
  if (!formRef.value) return null;
  
  try {
    await formRef.value.validate();
    return { ...form };
  } catch (error) {
    console.error('表单验证失败:', error);
    return null;
  }
};

// 暴露方法给父组件
defineExpose({
  submit
});

// 生命周期钩子
onMounted(() => {
  initFormData();
});
</script>

<style scoped>
.el-tree-select {
  width: 100%;
}

.mt-2 {
  margin-top: 8px;
}
</style> 