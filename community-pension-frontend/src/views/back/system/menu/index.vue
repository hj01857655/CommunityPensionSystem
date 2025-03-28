<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="菜单名称" prop="menuName">
        <el-input v-model="queryParams.menuName" placeholder="请输入菜单名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="菜单状态" clearable style="width: 200px">
          <el-option v-for="dict in dict.type.sys_normal_disable" :key="dict.value" :label="dict.label"
            :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
        <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain :icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain :icon="Sort" @click="toggleExpandAll">展开/折叠</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-if="refreshTable" v-loading="loading" :data="menuList" row-key="menuId"
      :default-expand-all="isExpandAll" :tree-props="{ children: 'children', hasChildren: 'hasChildren' }">
      <el-table-column prop="menuName" label="菜单名称" :show-overflow-tooltip="true" width="160">
        <template #default="scope">
          <span>{{ scope.row.menuName }}</span>
          <el-tag v-if="scope.row.menuType !== 'F'" type="info" size="small" class="ml-2">{{ menuTypeFormat(scope.row)
            }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="icon" label="图标" align="center" width="100">
        <template #default="scope">
          <svg-icon :icon-class="scope.row.icon" />
        </template>
      </el-table-column>
      <el-table-column prop="orderNum" label="排序" width="60"></el-table-column>
      <el-table-column prop="perms" label="权限标识" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column prop="component" label="组件路径" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'" size="small">
            {{ scope.row.status === '0' ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime">
        <template #default="scope">
          <span>{{ formatDate(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" size="small" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button link type="primary" icon="Plus" size="small" @click="handleAdd(scope.row)">新增</el-button>
          <el-button link type="primary" icon="Delete" size="small" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改菜单对话框 -->
    <el-dialog 
      v-model="open"
      :title="title" 
      width="580px" 
      append-to-body
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="上级菜单">
          <el-tree-select
            v-model="form.parentId"
            :data="menuOptions"
            :props="{
              value: 'menuId',
              label: 'menuName',
              children: 'children'
            }"
            placeholder="选择上级菜单"
            check-strictly
            :render-after-expand="false"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="form.menuType">
            <el-radio value="M">目录</el-radio>
            <el-radio value="C">菜单</el-radio>
            <el-radio value="F">按钮</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="form.menuType != 'F'" label="菜单图标">
          <IconSelect v-model="form.icon" placeholder="点击选择图标" />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="菜单名称" prop="menuName" required>
              <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示排序" prop="orderNum" required>
              <el-input-number v-model="form.orderNum" controls-position="right" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 目录和菜单共有的字段 -->
        <template v-if="form.menuType !== 'F'">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="是否外链">
                <el-radio-group v-model="form.isExternal">
                  <el-radio :value="true">是</el-radio>
                  <el-radio :value="false">否</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="路由地址" prop="path" required>
                <el-input v-model="form.path" placeholder="请输入路由地址" />
              </el-form-item>
            </el-col>
          </el-row>
        </template>

        <!-- 仅菜单类型为 C 时显示的字段 -->
        <template v-if="form.menuType === 'C'">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="组件路径" prop="component">
                <el-input v-model="form.component" placeholder="请输入组件路径" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="权限字符" prop="perms">
                <el-input v-model="form.perms" placeholder="请输入权限标识" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="路由参数">
                <el-input v-model="form.query" placeholder="请输入路由地址" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="是否缓存">
                <el-radio-group v-model="form.isCache">
                  <el-radio :value="true">缓存</el-radio>
                  <el-radio :value="false">不缓存</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
        </template>

        <!-- 按钮类型特有的字段 -->
        <template v-if="form.menuType === 'F'">
          <el-form-item label="权限字符" prop="perms" required>
            <el-input v-model="form.perms" placeholder="请输入权限标识" style="width: 50%" />
          </el-form-item>
        </template>

        <el-row :gutter="20">
          <template v-if="form.menuType === 'F'">
            <el-col :span="12">
              <el-form-item label="菜单状态">
                <el-radio-group v-model="form.status">
                  <el-radio :value="true">正常</el-radio>
                  <el-radio :value="false">停用</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </template>
          <template v-else>
            <el-col :span="12">
              <el-form-item label="显示状态">
                <el-radio-group v-model="form.visible">
                  <el-radio :value="true">显示</el-radio>
                  <el-radio :value="false">隐藏</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="菜单状态">
                <el-radio-group v-model="form.status">
                  <el-radio :value="true">正常</el-radio>
                  <el-radio :value="false">停用</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </template>
        </el-row>
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
import { ref, reactive, onMounted, nextTick } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus, Sort } from '@element-plus/icons-vue';
import { listMenu, getMenu, delMenu, addMenu, updateMenu } from '@/api/back/system/menu';
import IconSelect from '@/components/back/system/menu/IconSelect.vue';
import SvgIcon from '@/components/back/system/menu/SvgIcon.vue';
import { handleTree } from '@/utils/tree';
import RightToolbar from '@/components/back/system/menu/RightToolbar.vue';
import { formatDate } from '@/utils/date';

// 状态数据字典
const dict = reactive({
  type: {
    sys_normal_disable: [
      { value: "0", label: "正常" },
      { value: "1", label: "停用" }
    ],
    sys_show_hide: [
      { value: "0", label: "显示" },
      { value: "1", label: "隐藏" }
    ]
  }
});

const loading = ref(true);
const showSearch = ref(true);
const menuList = ref([]);
const menuOptions = ref([]);
const title = ref('');
const open = ref(false);
const isExpandAll = ref(false);
const refreshTable = ref(true);

// 查询参数
const queryParams = reactive({
  menuName: undefined,
  status: undefined,
});

const form = reactive({
  menuId: undefined,
  parentId: 0,
  menuName: '',
  icon: '',
  menuType: 'M',
  orderNum: 1,
  isExternal: false,
  path: '',
  component: '',
  perms: '',
  query: '',
  isCache: false,
  visible: true,
  status: true
});

const rules = {
  menuName: [
    { required: true, message: '菜单名称不能为空', trigger: 'blur' }
  ],
  orderNum: [
    { required: true, message: '显示排序不能为空', trigger: 'blur' }
  ],
  path: [
    { 
      validator: (rule, value, callback) => {
        if (form.menuType !== 'F' && !value) {
          callback(new Error('路由地址不能为空'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    },
    { 
      validator: (rule, value, callback) => {
        if (form.isExternal && value && !value.startsWith('http')) {
          callback(new Error('外链必须以http(s)://开头'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ],
  component: [
    { 
      validator: (rule, value, callback) => {
        if (form.menuType === 'C' && !value) {
          callback(new Error('组件路径不能为空'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ],
  perms: [
    {
      validator: (rule, value, callback) => {
        if (form.menuType === 'F' && !value) {
          callback(new Error('权限字符不能为空'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
};

const selected = (name) => {
  form.icon = name;
};

const getList = () => {
  loading.value = true;
  listMenu(queryParams).then((response) => {
    if (Array.isArray(response.data)) {
      menuList.value = handleTree(response.data, 'menuId');
    } else {
      menuList.value = [];
      console.warn('获取的菜单数据不是数组格式:', response.data);
    }
  }).catch(error => {
    console.error('获取菜单列表失败:', error);
    menuList.value = [];
    ElMessage.error('获取菜单列表失败');
  }).finally(() => {
    loading.value = false;
  });
};

const getTreeselect = async () => {
  try {
    const response = await listMenu();
    const rootMenu = { 
      menuId: 0, 
      menuName: '主目录',
      children: []
    };
    
    if (Array.isArray(response.data)) {
      rootMenu.children = handleTree(response.data, 'menuId', 'parentId', 'children');
    }
    
    menuOptions.value = [rootMenu];
  } catch (error) {
    console.error('获取菜单树失败:', error);
    menuOptions.value = [{
      menuId: 0,
      menuName: '主目录',
      children: []
    }];
    ElMessage.error('获取菜单树失败');
  }
};

const formRef = ref(null);
const queryRef = ref(null);

const resetFormData = (formName) => {
  if (formName === 'form') {
    formRef.value?.resetFields();
    // 重置表单数据
    Object.assign(form, {
      menuId: undefined,
      parentId: 0,
      menuName: '',
      icon: '',
      menuType: 'M',
      orderNum: 1,
      isExternal: false,
      path: '',
      component: '',
      perms: '',
      query: '',
      isCache: false,
      visible: true,
      status: true
    });
  } else if (formName === 'queryRef') {
    queryRef.value?.resetFields();
    // 重置查询参数
    Object.assign(queryParams, {
      menuName: undefined,
      status: undefined
    });
  }
};

const cancel = () => {
  open.value = false;
  resetFormData('form');
};

const handleQuery = () => {
  getList();
};

const resetQuery = () => {
  resetFormData('queryRef');
  handleQuery();
};

const handleAdd = (row) => {
  resetFormData('form');
  getTreeselect();
  if (row) {
    form.parentId = row.menuId;
  } else {
    form.parentId = 0;
  }
  title.value = '添加菜单';
  open.value = true;
  console.log('Dialog status:', open.value);
};

const toggleExpandAll = () => {
  refreshTable.value = false;
  isExpandAll.value = !isExpandAll.value;
  nextTick(() => {
    refreshTable.value = true;
  });
};

const handleUpdate = (row) => {
  resetFormData('form');
  getTreeselect();
  getMenu(row.menuId).then((response) => {
    Object.assign(form, response.data);
    open.value = true;
    title.value = '修改菜单';
  });
};

const submitForm = () => {
  formRef.value?.validate((valid) => {
    if (valid) {
      loading.value = true;
      if (form.menuId) {
        updateMenu(form).then(response => {
          if (response.code === 200) {
            ElMessage.success('修改成功');
            open.value = false;
            getList();
          } else {
            ElMessage.error(response.msg || '修改失败');
          }
        }).finally(() => {
          loading.value = false;
        });
      } else {
        addMenu(form).then(response => {
          if (response.code === 200) {
            ElMessage.success('新增成功');
            open.value = false;
            getList();
          } else {
            ElMessage.error(response.msg || '新增失败');
          }
        }).finally(() => {
          loading.value = false;
        });
      }
    }
  });
};

const handleDelete = (row) => {
  ElMessageBox.confirm('是否确认删除名称为"' + row.menuName + '"的数据项?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return delMenu(row.menuId);
  }).then(() => {
    getList();
    ElMessage.success('删除成功');
  }).catch(() => { });
};

const menuTypeFormat = (row) => {
  if (row.menuType === 'M') {
    return '目录';
  } else if (row.menuType === 'C') {
    return '菜单';
  } else if (row.menuType === 'F') {
    return '按钮';
  }
  return '';
};

onMounted(() => {
  getList();
});
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.mb8 {
  margin-bottom: 8px;
}

.small-padding {
  padding: 0 8px;
}

.fixed-width {
  width: 150px;
}
</style>
