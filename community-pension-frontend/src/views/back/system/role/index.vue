<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryFormRef" :inline="true" label-width="80px" class="search-form">
      <el-form-item label="角色名称">
        <el-input
          v-model="queryParams.roleName"
          placeholder="请输入角色名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        >
          <template #prefix>
            <el-icon><User /></el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="权限字符">
        <el-input
          v-model="queryParams.roleKey"
          placeholder="请输入权限字符"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        >
          <template #prefix>
            <el-icon><Key /></el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="显示顺序">
        <el-input-number
          v-model="queryParams.roleSort"
          placeholder="请输入显示顺序"
          :min="0"
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 200px">
          <el-option
            v-for="dict in statusOptions"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          >
            <template #default>
              <el-icon :class="dict.value === '0' ? 'text-success' : 'text-danger'">
                <component :is="dict.value === '0' ? 'CircleCheck' : 'CircleClose'" />
              </el-icon>
              <span class="ml-2">{{ dict.label }}</span>
            </template>
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>
  
    <el-table v-loading="loading" :data="roleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="角色ID" align="center" prop="roleId" width="120" />
      <el-table-column label="角色名称" align="center" prop="roleName" :show-overflow-tooltip="true" />
      <el-table-column label="角色权限字符串" align="center" prop="roleKey" :show-overflow-tooltip="true" />
      <el-table-column label="显示顺序" align="center" prop="roleSort" width="100" />
      <el-table-column label="状态" align="center" width="100">
        <template #default="scope">
          <el-switch
            v-model="scope.row.status"
            :active-value="'1'"
            :inactive-value="'0'"
            @change="handleStatusChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ formatDate(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="220">
        <template #default="scope">
          <el-button
            link
            icon="Edit"
            size="small"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            link
            icon="CircleCheck"
            size="small"
            @click="handlePermission(scope.row)"
          >权限</el-button>
          <el-button
            link
            icon="Delete"
            size="small"
            @click="handleDelete(scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-if="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改角色对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="roleFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="权限字符" prop="roleKey">
          <el-input v-model="form.roleKey" placeholder="请输入权限字符" />
        </el-form-item>
        <el-form-item label="角色顺序" prop="roleSort">
          <el-input-number v-model="form.roleSort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="0">正常</el-radio>
            <el-radio :value="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单权限" prop="menuIds">
          <el-tree
            ref="menuTreeRef"
            :data="menuOptions"
            :props="{ label: 'menuName', children: 'children' }"
            show-checkbox
            node-key="menuId"
            :default-checked-keys="form.menuIds"
            :default-expanded-keys="expandedKeys"
            @check="handleMenuCheck"
            @expand="handleMenuExpand"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 分配角色权限对话框 -->
    <el-dialog :title="title" v-model="permissionOpen" width="600px" append-to-body>
      <el-form :model="permissionForm" label-width="80px">
        <el-form-item label="角色名称">
          <el-input v-model="permissionForm.roleName" :disabled="true" />
        </el-form-item>
        <el-form-item label="权限字符">
          <el-input v-model="permissionForm.roleKey" :disabled="true" />
        </el-form-item>
        <el-form-item label="权限分配">
          <el-tree
            ref="menuTreeRef"
            :data="menuOptions"
            show-checkbox
            node-key="menuId"
            empty-text="加载中，请稍候"
            :props="{ label: 'menuName', children: 'children' }"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitPermission">确 定</el-button>
          <el-button @click="cancelPermission">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
  
<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { listMenu } from '@/api/back/system/menu';
import { handleTree } from '@/utils/tree';
import { formatDate } from '@/utils/date';
import RightToolbar from '@/components/common/base/RightToolbar/index.vue';
import Pagination from '@/components/common/Pagination.vue';
import { useRoleStore } from '@/stores/back/roleStore';
import { storeToRefs } from 'pinia';

const roleStore = useRoleStore();
const { roleList, total, loading, queryParams, menuTree } = storeToRefs(roleStore);

// 选中数组
const ids = ref([]);
// 非单个禁用
const single = ref(true);
// 非多个禁用
const multiple = ref(true);
// 显示搜索条件
const showSearch = ref(true);
// 弹出层标题
const title = ref("");
// 是否显示弹出层
const open = ref(false);
// 是否显示权限弹出层
const permissionOpen = ref(false);
// 表单引用
const roleFormRef = ref(null);
// 菜单树引用
const menuTreeRef = ref(null);
// 查询表单引用
const queryFormRef = ref(null);

// 状态数据字典
const statusOptions = [
  { value: "1", label: "停用" },
  { value: "0", label: "正常" }
];

// 菜单树选项
const menuOptions = ref([]);
const expandedKeys = ref([]);

// 表单参数
const form = reactive({
  roleId: undefined,
  roleName: undefined,
  roleKey: undefined,
  roleSort: 0,
  status: "0",
  menuIds: [],
  remark: undefined
});

// 权限表单
const permissionForm = reactive({
  roleId: undefined,
  roleName: undefined,
  roleKey: undefined,
  menuIds: []
});

// 表单校验
const rules = {
  roleName: [
    { required: true, message: "角色名称不能为空", trigger: "blur" },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  roleKey: [
    { required: true, message: "权限字符不能为空", trigger: "blur" },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  roleSort: [
    { required: true, message: "显示顺序不能为空", trigger: "blur" }
  ],
  status: [
    { required: true, message: '状态不能为空', trigger: 'change' }
  ]
};

/** 查询角色信息列表 */
const getList = async () => {
  try {
    await roleStore.fetchRoleList();
  } catch (error) {
    console.error('获取角色列表失败:', error);
    ElMessage.error('获取角色列表失败');
  }
};

/** 查询菜单树结构 */
const getMenuTreeselect = async () => {
  try {
    const response = await listMenu();
    if (response.code === 200 && Array.isArray(response.data)) {
      menuOptions.value = handleTree(response.data, 'menuId', 'parentId');
    }
  } catch (error) {
    console.error('获取菜单树失败:', error);
    ElMessage.error('获取菜单树失败');
  }
};

/** 根据角色ID查询菜单树结构 */
const getRoleMenuTreeselect = async (roleId) => {
  try {
    const res = await roleStore.fetchRoleMenuTree(roleId);
    if (res && res.code === 200) {
      return res.data;
    }
    return [];
  } catch (error) {
    console.error('获取角色菜单树失败:', error);
    ElMessage.error('获取角色菜单树失败');
    return [];
  }
};

// 取消按钮
const cancel = () => {
  open.value = false;
  resetForm();
};

// 取消权限按钮
const cancelPermission = () => {
  permissionOpen.value = false;
};

// 表单重置
const resetForm = () => {
  roleFormRef.value?.resetFields();
  form.roleId = undefined;
  form.roleName = undefined;
  form.roleKey = undefined;
  form.roleSort = 0;
  form.status = "0";
  form.menuIds = [];
  form.remark = undefined;
};

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
};

/** 重置按钮操作 */
const resetQuery = () => {
  roleStore.resetQueryParams();
  queryFormRef.value?.resetFields();
  handleQuery();
};

// 多选框选中数据
const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => item.roleId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
};

/** 角色状态修改 */
const handleStatusChange = async (row) => {
  const text = row.status === "0" ? "停用" : "启用";
  try {
    await ElMessageBox.confirm(`确认要${text}${row.roleName}角色吗？`);
    await roleStore.updateRoleStatus({
      roleId: row.roleId,
      status: row.status
    });
    ElMessage.success(`${text}成功`);
    getList();
  } catch (error) {
    if (error !== 'cancel') {
      console.error('更新角色状态失败:', error);
      row.status = row.status === "0" ? "1" : "0";
      ElMessage.error('更新角色状态失败');
    } else {
      row.status = row.status === "0" ? "1" : "0";
    }
  }
};

/** 新增按钮操作 */
const handleAdd = async () => {
  resetForm();
  open.value = true;
  title.value = "添加角色";
  await getMenuTreeselect();
};

/** 修改按钮操作 */
const handleUpdate = async (row) => {
  resetForm();
  const roleId = row?.roleId || ids.value[0];
  try {
    if (row) {
      Object.assign(form, {
        roleId: row.roleId,
        roleName: row.roleName,
        roleKey: row.roleKey,
        roleSort: row.roleSort,
        status: row.status,
        remark: row.remark
      });
      open.value = true;
      title.value = "修改角色";
      await getMenuTreeselect();
      const menuIds = await getRoleMenuTreeselect(roleId);
      form.menuIds = menuIds;
      nextTick(() => {
        menuTreeRef.value?.setCheckedKeys(menuIds);
      });
    }
  } catch (error) {
    console.error('获取角色详情失败:', error);
    ElMessage.error('获取角色详情失败');
  }
};

/** 分配权限操作 */
const handlePermission = async (row) => {
  permissionForm.roleId = row.roleId;
  permissionForm.roleName = row.roleName;
  permissionForm.roleKey = row.roleKey;
  permissionOpen.value = true;
  title.value = "分配权限";
  try {
    const menuTree = await getRoleMenuTreeselect(row.roleId);
    if (menuTree) {
      permissionForm.menuIds = menuTree;
      nextTick(() => {
        menuTreeRef.value?.setCheckedKeys(menuTree);
      });
    }
  } catch (error) {
    console.error('获取角色菜单树失败:', error);
    ElMessage.error('获取角色菜单树失败');
  }
};

/** 提交按钮 */
const submitForm = () => {
  roleFormRef.value?.validate(async valid => {
    if (valid) {
      try {
        if (form.roleId) {
          await roleStore.updateRoleInfo(form);
          ElMessage.success("修改成功");
        } else {
          await roleStore.createRole(form);
          ElMessage.success("新增成功");
        }
        open.value = false;
        getList();
      } catch (error) {
        console.error('保存角色失败:', error);
        ElMessage.error('保存角色失败');
      }
    }
  });
};

/** 提交权限按钮 */
const submitPermission = async () => {
  try {
    const checkedKeys = menuTreeRef.value?.getCheckedKeys() || [];
    const halfCheckedKeys = menuTreeRef.value?.getHalfCheckedKeys() || [];
    const menuIds = [...checkedKeys, ...halfCheckedKeys];
    await roleStore.assignMenus({
      roleId: permissionForm.roleId,
      menuIds
    });
    ElMessage.success("分配权限成功");
    permissionOpen.value = false;
  } catch (error) {
    console.error('分配权限失败:', error);
    ElMessage.error('分配权限失败');
  }
};

/** 删除按钮操作 */
const handleDelete = async (row) => {
  const roleIds = row?.roleId || ids.value;
  try {
    await ElMessageBox.confirm(`确认删除角色编号为"${roleIds}"的数据项?`);
    await roleStore.removeRole(roleIds);
    await getList();
    ElMessage.success("删除成功");
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除角色失败:', error);
      ElMessage.error('删除角色失败');
    }
  }
};

/** 导出按钮操作 */
const handleExport = () => {
  // 实现导出功能，可以根据实际需求调整
  ElMessage.info("导出功能待实现");
};

// 菜单树节点选中状态改变
const handleMenuCheck = (data, { checkedKeys }) => {
  form.menuIds = checkedKeys;
};

// 菜单树节点展开状态改变
const handleMenuExpand = (data) => {
  expandedKeys.value = data;
};

onMounted(() => {
  getList();
  getMenuTreeselect();
});
</script>

<style scoped>
.search-form {
  background-color: #fff;
  padding: 18px;
  margin-bottom: 10px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.search-form :deep(.el-form-item) {
  margin-bottom: 18px;
  margin-right: 18px;
}

.search-form :deep(.el-form-item__label) {
  font-weight: 500;
}

.text-success {
  color: #67C23A;
}

.text-danger {
  color: #F56C6C;
}

.ml-2 {
  margin-left: 8px;
}
</style>

<style scoped>
.app-container {
  background-color: #fff;
  padding: 18px;
  margin-bottom: 10px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.app-container :deep(.el-form-item) {
  margin-bottom: 18px;
  margin-right: 18px;
}

.app-container :deep(.el-form-item__label) {
  font-weight: 500;
}

.text-success {
  color: #67C23A;
}

.text-danger {
  color: #F56C6C;
}

.ml-2 {
  margin-left: 8px;
}
</style>

