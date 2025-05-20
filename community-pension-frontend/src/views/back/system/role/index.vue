<template>
  <div class="app-container">
    <el-form ref="queryFormRef" :inline="true" :model="queryParams" class="search-form" label-width="80px">
      <el-form-item label="角色名">
        <el-input
            v-model="queryParams.roleName"
            clearable
            placeholder="请输入角色名称"
            style="width: 200px"
            @keyup.enter="handleQuery"
        >
          <template #prefix>
            <el-icon>
              <User/>
            </el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="权限字符">
        <el-input
            v-model="queryParams.roleKey"
            clearable
            placeholder="请输入权限字符"
            style="width: 200px"
            @keyup.enter="handleQuery"
        >
          <template #prefix>
            <el-icon>
              <Key/>
            </el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="状态">
        <el-select
            v-model="queryParams.status"
            clearable
            placeholder="角色状态"
            style="width: 200px"
        >
          <el-option value="0" label="正常"/>
          <el-option value="1" label="停用"/>
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间">
        <el-date-picker
            v-model="dateRange"
            end-placeholder="结束日期"
            range-separator="-"
            start-placeholder="开始日期"
            style="width: 240px"
            type="daterange"
            value-format="YYYY-MM-DD"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button icon="Search" type="primary" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            icon="Plus"
            plain
            type="primary"
            @click="handleAdd"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            :disabled="single"
            icon="Edit"
            plain
            type="success"
            @click="handleUpdate"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            :disabled="multiple"
            icon="Delete"
            plain
            type="danger"
            @click="handleDelete"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            icon="Download"
            plain
            type="warning"
            @click="handleExport"
        >导出
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="roleList" @selection-change="handleSelectionChange">
      <el-table-column align="center" type="selection" width="55"/>
      <el-table-column label="角色编号" prop="roleId" width="120"/>
      <el-table-column :show-overflow-tooltip="true" label="角色名称" prop="roleName" width="150"/>
      <el-table-column :show-overflow-tooltip="true" label="权限字符" prop="roleKey" width="150"/>
      <el-table-column label="显示顺序" prop="roleSort" width="100"/>
      <el-table-column align="center" label="状态" width="100">
        <template #default="scope">
          <el-switch
              v-model="scope.row.isActive"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column align="center" label="创建时间" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ formatDate(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作">
        <template #default="scope">
          <el-tooltip content="修改" placement="top">
            <el-button
                icon="Edit"
                link
                @click="handleUpdate(scope.row)"
            ></el-button>
          </el-tooltip>
          <el-tooltip content="删除" placement="top">
            <el-button
                icon="Delete"
                link
                @click="handleDelete(scope.row)"
            ></el-button>
          </el-tooltip>
        </template>
      </el-table-column>
    </el-table>

    <pagination
        v-show="total > 0"
        v-model:limit="queryParams.pageSize"
        v-model:page="queryParams.pageNum"
        :total="total"
        @pagination="getList"
    />

    <!-- 添加或修改角色配置对话框 -->
    <el-dialog v-model="open" :title="title" append-to-body width="500px">
      <el-form ref="roleFormRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称"/>
        </el-form-item>
        <el-form-item label="权限字符" prop="roleKey">
          <el-input v-model="form.roleKey" placeholder="请输入权限字符"/>
        </el-form-item>
        <el-form-item label="角色顺序" prop="roleSort">
          <el-input-number v-model="form.roleSort" :min="0" controls-position="right"/>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="'0'" label="正常"/>
            <el-radio :value="'1'" label="停用"/>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单权限">
          <el-checkbox v-model="menuExpand" @change="handleCheckedTreeExpand($event, 'menu')">展开/折叠</el-checkbox>
          <el-checkbox v-model="menuNodeAll" @change="handleCheckedTreeNodeAll($event, 'menu')">全选/全不选
          </el-checkbox>
          <el-checkbox v-model="form.menuCheckStrictly" @change="handleCheckedTreeConnect($event, 'menu')">父子联动
          </el-checkbox>
          <el-tree
              ref="menuRef"
              :check-strictly="!form.menuCheckStrictly"
              :data="menuOptions"
              :props="{ label: 'menuName', children: 'children' }"
              class="tree-border"
              empty-text="加载中，请稍候"
              node-key="menuId"
              show-checkbox
          ></el-tree>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" placeholder="请输入内容" type="textarea"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { treeselect as menuTreeselect, roleMenuTreeselect } from "@/api/back/system/menu";
import { useRoleStore } from '@/stores/back/roleStore';
import { formatDate } from '@/utils/date';
import { handleTree } from '@/utils/tree';
import { Key, User } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { nextTick, onMounted, ref } from 'vue';

const roleStore = useRoleStore();

// 遮罩层
const loading = ref(false);
// 选中数组
const ids = ref([]);
// 非单个禁用
const single = ref(true);
// 非多个禁用
const multiple = ref(true);
// 显示搜索条件
const showSearch = ref(true);
// 总条数
const total = ref(0);
// 角色表格数据
const roleList = ref([]);
// 弹出层标题
const title = ref("");
// 是否显示弹出层
const open = ref(false);
// 日期范围
const dateRange = ref([]);
// 菜单树选项
const menuOptions = ref([]);
// 菜单树展开
const menuExpand = ref(false);
// 菜单树选中
const menuNodeAll = ref(false);

// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  roleName: undefined,
  roleKey: undefined,
  isActive: undefined
});

// 表单参数
const form = ref({
  roleId: undefined,
  roleName: undefined,
  roleKey: undefined,
  roleSort: 0,
  isActive: 1,
  menuCheckStrictly: true,
  menuIds: [],
  remark: undefined
});

// 表单校验
const rules = {
  roleName: [
    {required: true, message: "角色名称不能为空", trigger: "blur"}
  ],
  roleKey: [
    {required: true, message: "权限字符不能为空", trigger: "blur"}
  ],
  roleSort: [
    {required: true, message: "角色顺序不能为空", trigger: "blur"}
  ]
};

// 引用DOM元素
const roleFormRef = ref(null);
const menuRef = ref(null);

/** 查询角色列表 */
async function getList() {
  loading.value = true;
  try {
    const response = await roleStore.fetchRoleList(queryParams.value, dateRange.value);
    
    // 将后端返回的status字符串转换为前端需要的isActive数值
    roleList.value = response.data.records.map(role => {
      // 后端status: "0"表示正常(对应前端isActive=1), "1"表示停用(对应前端isActive=0)
      role.isActive = role.status === "0" ? 1 : 0;
      return role;
    });
    total.value = response.data.total;
  } catch (error) {
    console.error("获取角色列表失败:", error);
    ElMessage.error("获取角色列表失败");
  } finally {
    loading.value = false;
  }
}

/** 查询菜单树结构 */
async function getMenuTreeselect() {
  try {
    const response = await menuTreeselect();
    console.log('菜单树数据:', response.data);
    
    // 处理菜单数据
    let menus = Array.isArray(response.data) ? response.data : [];
    // 转换为树形结构
    if (menus.length > 0 && !menus.some(item => item.children && item.children.length > 0)) {
      console.log('处理前的菜单数据:', menus);
      menus = handleTree(menus, 'menuId', 'parentId', 'children');
      console.log('处理后的树形结构:', menus);
    }
    menuOptions.value = menus;
  } catch (error) {
    console.error("获取菜单树失败:", error);
    ElMessage.error("获取菜单树失败");
    menuOptions.value = []; // 确保有默认值
  }
}

/** 根据角色ID查询菜单树结构 */
async function getRoleMenuTreeselect(roleId) {
  try {
    // 尝试获取角色菜单树
    const response = await roleMenuTreeselect(roleId);
    console.log('角色菜单树数据:', response.data);
    
    // 处理菜单数据，使用handleTree转换为树形结构
    let menus = Array.isArray(response.data.menus) ? response.data.menus : [];
    // 如果menus已经是树形结构，则直接使用；否则使用handleTree转换
    if (menus.length > 0 && !menus.some(item => item.children && item.children.length > 0)) {
      console.log('转换前的菜单数据:', menus);
      menus = handleTree(menus, 'menuId', 'parentId', 'children');
      console.log('转换后的树形结构:', menus);
    }
    menuOptions.value = menus;
    
    // 确保checkedKeys存在且为数组
    const checkedKeys = Array.isArray(response.data.checkedKeys) ? response.data.checkedKeys : [];
    console.log('选中的菜单ID:', checkedKeys);
    
    // 只有在menuRef已初始化时才设置选中项
    if (menuRef.value) {
      // 使用nextTick确保DOM更新后再设置选中项
      nextTick(() => {
        try {
          menuRef.value.setCheckedKeys(checkedKeys);
          console.log('已设置选中节点');
        } catch (err) {
          console.error('设置选中节点失败:', err);
        }
      });
    } else {
      console.warn('菜单树组件未初始化');
    }
  } catch (error) {
    // 如果获取角色菜单树失败，则获取普通菜单树
    console.error("获取角色菜单树失败:", error);
    ElMessage.warning("获取角色菜单树失败，使用普通菜单树");
    
    try {
      // 获取普通菜单树作为备选
      const response = await menuTreeselect();
      let menus = Array.isArray(response.data) ? response.data : [];
      // 转换为树形结构
      if (menus.length > 0 && !menus.some(item => item.children && item.children.length > 0)) {
        menus = handleTree(menus, 'menuId', 'parentId', 'children');
      }
      menuOptions.value = menus;
      console.log('备选菜单树:', menuOptions.value);
      
      // 清空选中项
      if (menuRef.value) {
        nextTick(() => {
          try {
            menuRef.value.setCheckedKeys([]);
          } catch (err) {
            console.error('清空选中节点失败:', err);
          }
        });
      }
    } catch (innerError) {
      console.error("获取菜单树失败:", innerError);
      ElMessage.error("获取菜单树失败");
      menuOptions.value = []; // 确保menuOptions始终有值
    }
  }
}

// 所有菜单节点数据
const menuNodesAll = ref([]);
// 复选框选中的节点
const checkedMenuNodes = ref([]);
// 菜单节点数组
const defaultProps = {
  children: "children",
  label: "label"
};

/** 树权限（展开/折叠）*/
function handleCheckedTreeExpand(value, type) {
  if (type == "menu") {
    const treeList = menuOptions.value;
    for (let i = 0; i < treeList.length; i++) {
      try {
        // 使用menuId作为节点的key
        const nodeKey = treeList[i].menuId;
        if (menuRef.value.store.nodesMap[nodeKey]) {
          menuRef.value.store.nodesMap[nodeKey].expanded = value;
        }
      } catch (err) {
        console.error('展开/折叠节点失败:', err);
      }
    }
  }
}

/** 树权限（全选/全不选） */
function handleCheckedTreeNodeAll(value, type) {
  if (type == "menu") {
    try {
      if (menuRef.value) {
        menuRef.value.setCheckedNodes(value ? menuOptions.value : []);
      }
    } catch (err) {
      console.error('全选/全不选操作失败:', err);
    }
  }
}

/** 树权限（父子联动） */
function handleCheckedTreeConnect(value, type) {
  if (type == "menu") {
    form.value.menuCheckStrictly = value;
  }
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    roleName: undefined,
    roleKey: undefined,
    isActive: undefined
  };
  handleQuery();
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.roleId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 修改按钮操作 */
async function handleUpdate(row) {
  reset();
  const roleId = row.roleId || ids.value[0];
  try {
    // 先获取角色基本信息
    const response = await roleStore.fetchRoleInfo(roleId);
    form.value = response.data;
    
    // 将后端返回的status字符串转换为前端表单需要的isActive数值
    form.value.isActive = form.value.status === "0" ? 1 : 0;
    
    // 先打开对话框，确保DOM渲染完成
    open.value = true;
    title.value = "修改角色";
    
    // 使用nextTick确保对话框中的树组件已经渲染
    nextTick(async () => {
      try {
        // 对话框打开后再获取菜单树数据
        await getRoleMenuTreeselect(roleId);
      } catch (err) {
        console.error("加载菜单树失败:", err);
        ElMessage.warning("加载菜单权限失败，请关闭对话框重试");
      }
    });
  } catch (error) {
    console.error("获取角色信息失败:", error);
    ElMessage.error("获取角色信息失败");
  }
}

/** 新增按钮操作 */
async function handleAdd() {
  reset();
  // 先打开对话框
  open.value = true;
  title.value = "添加角色";
  
  // 使用nextTick确保对话框中的树组件已经渲染
  nextTick(async () => {
    try {
      await getMenuTreeselect();
    } catch (err) {
      console.error("加载菜单树失败:", err);
      ElMessage.warning("加载菜单权限失败，请关闭对话框重试");
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const roleIdList = row.roleId || ids.value;
  ElMessageBox.confirm('是否确认删除角色编号为"' + roleIdList + '"的数据项?', "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(async function () {
    try {
      await roleStore.removeRole(roleIdList);
      getList();
      ElMessage.success("删除成功");
    } catch (error) {
      console.error("删除角色失败:", error);
      ElMessage.error("删除失败");
    }
  });
}

/** 导出按钮操作 */
function handleExport() {
  // 导出功能实现
  ElMessage.success("导出功能待实现");
}

/** 状态修改 */
async function handleStatusChange(row) {
  let text = row.isActive === 1 ? "停用" : "启用";
  try {
    await roleStore.updateRoleStatus({
      roleId: row.roleId,
      status: row.isActive === 1 ? "1" : "0" // 将isActive的值转换为status期望的格式
    });
    ElMessage.success(text + "成功");
  } catch (error) {
    console.error(`${text}角色失败:`, error);
    row.isActive = row.isActive === 1 ? 0 : 1; // 恢复原始状态
    ElMessage.error(text + "失败");
  }
}

/** 提交按钮 */
async function submitForm() {
  if (!roleFormRef.value) return;

  await roleFormRef.value.validate(async (valid) => {
    if (valid) {
      // 将前端表单的isActive数值转换为后端需要的status字符串
      const roleData = { ...form.value };
      roleData.status = roleData.isActive === 1 ? "0" : "1";
      
      if (form.value.roleId != undefined) {
        roleData.menuIds = getMenuAllCheckedKeys();
        try {
          await roleStore.updateRoleInfo(roleData);
          ElMessage.success("修改成功");
          open.value = false;
          getList();
        } catch (error) {
          console.error("修改角色失败:", error);
          ElMessage.error("修改失败");
        }
      } else {
        roleData.menuIds = getMenuAllCheckedKeys();
        try {
          await roleStore.createRole(roleData);
          ElMessage.success("新增成功");
          open.value = false;
          getList();
        } catch (error) {
          console.error("新增角色失败:", error);
          ElMessage.error("新增失败");
        }
      }
    }
  });
}

/** 获取菜单树选中值 */
function getMenuAllCheckedKeys() {
  // 目前被选中的菜单节点
  let checkedKeys = menuRef.value.getCheckedKeys();
  // 半选中的菜单节点
  let halfCheckedKeys = menuRef.value.getHalfCheckedKeys();
  checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys);
  return checkedKeys;
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

/** 表单重置 */
function reset() {
  form.value = {
    roleId: undefined,
    roleName: undefined,
    roleKey: undefined,
    roleSort: 0,
    status: '0',
    dataScope: '1',
    menuCheckStrictly: true,
    menuIds: [],
    remark: undefined
  };
  if (menuRef.value) {
    menuRef.value.setCheckedKeys([]);
  }
}

onMounted(() => {
  getList();
});
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.tree-border {
  margin-top: 5px;
  border: 1px solid #e5e6e7;
  background: #FFFFFF none;
  border-radius: 4px;
  width: 100%;
}

.search-form {
  margin-bottom: 20px;
}

.mb8 {
  margin-bottom: 8px;
}
</style>
