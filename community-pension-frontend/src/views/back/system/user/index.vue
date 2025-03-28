<template>
  <div class="app-container">
    <!-- 搜索条件 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户名称" prop="username">
        <el-input v-model="queryParams.username" placeholder="请输入用户名称" clearable style="width: 200px"
          @keyup.enter="handleQuery" />
      </el-form-item>

      <el-form-item label="状态" prop="isActive">
        <el-select v-model="queryParams.isActive" placeholder="用户状态" clearable style="width: 200px">
          <el-option label="正常" :value="1" />
          <el-option label="停用" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间">
        <el-date-picker v-model="dateRange" style="width: 240px" value-format="YYYY-MM-DD" type="daterange"
          range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
        <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain :icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain :icon="Edit" :disabled="single" @click="handleUpdate">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain :icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain :icon="Upload" @click="handleImport">导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain :icon="Download" @click="handleExport">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>
    <!-- 表格 -->
    <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" v-if="columns[0].visible" />
      <el-table-column label="用户编号" align="center" prop="userId" v-if="columns[1].visible" />
      <el-table-column label="用户名称" align="center" prop="username" v-if="columns[2].visible" />
      <el-table-column label="姓名" align="center" prop="name" v-if="columns[3].visible" />
      <el-table-column label="状态" align="center" prop="isActive" v-if="columns[4].visible">
        <template #default="scope">
          <el-switch v-model="scope.row.isActive" :active-value="1" :inactive-value="0"
            @change="handleStatusChange(scope.row)" />
        </template>
      </el-table-column>
      <el-table-column label="手机号码" align="center" prop="phone" v-if="columns[5].visible" />
      <el-table-column label="邮箱" align="center" prop="email" v-if="columns[6].visible" />
      <el-table-column label="角色" align="center" v-if="columns[7].visible">
        <template #default="scope">
          <el-tag 
            v-for="(roleName, index) in scope.row.roleNames" 
            :key="index"
            style="margin-right: 4px" 
            :type="getRoleTagType(scope.row.roleIds[index])"
          >
            {{ roleName }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="绑定关系" align="center" v-if="columns[8].visible">
        <template #default="scope">
          <div v-if="hasRole(scope.row, 2)">
            <el-tag type="warning" v-if="scope.row.elderId">
              {{ scope.row.relationship || '无' }} - {{ scope.row.elderName ? scope.row.elderName : '已绑定老人' }}
            </el-tag>
            <span v-else>未绑定</span>
          </div>
          <div v-else-if="hasRole(scope.row, 1)">
            <span v-if="scope.row.roleIds && scope.row.roleIds.includes(1)">
              <el-tag type="info">老人</el-tag>
            </span>
            <span v-if="scope.row.roleIds && scope.row.roleIds.includes(2)">
              <el-tag type="success">家属</el-tag>
            </span>
          </div>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180" v-if="columns[9].visible">
        <template #default="scope">
          <span>{{ formatDate(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150"
        v-if="columns[10].visible">
        <template #default="scope">
          <el-button type="primary" link :icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button type="primary" link :icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
          <el-dropdown trigger="click" @command="(command) => handleMoreActions(command, scope.row)">
            <el-button type="primary" link>
              更多<el-icon class="el-icon--right"><arrow-down /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="resetPassword">重置密码</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <pagination
      v-if="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加/修改用户对话框 -->
    <el-dialog :title="dialogType === 'add' ? '添加用户' : '修改用户'" v-model="dialogVisible" width="500px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户账号" prop="username">
          <el-input v-model="form.username" placeholder="用户账号" disabled />
        </el-form-item>
        <el-form-item label="角色" prop="roleIds">
          <el-select v-model="form.roleIds" placeholder="请选择角色" multiple @change="handleRoleChange">
            <el-option label="老人" :value="1" />
            <el-option label="家属" :value="2" />
            <el-option label="社区工作人员" :value="3" />
            <el-option label="管理员" :value="4" />
          </el-select>
        </el-form-item>

        <!-- 基础信息（所有角色都显示） -->
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号码" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="状态" prop="isActive">
          <el-radio-group v-model="form.isActive">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 老人角色特定的表单项 -->
        <template v-if="form.roleIds.includes(1)">
          <el-form-item label="身份证号码" prop="idCard">
            <el-input v-model="form.idCard" placeholder="请输入身份证号码" />
          </el-form-item>
          <el-form-item label="出生日期" prop="birthday">
            <el-date-picker v-model="form.birthday" type="date" placeholder="选择出生日期" disabled />
          </el-form-item>
          <el-form-item label="年龄" prop="age">
            <el-input v-model="form.age" placeholder="请输入年龄" disabled />
          </el-form-item>
          <el-form-item label="紧急联系人姓名" prop="emergencyContactName">
            <el-input v-model="form.emergencyContactName" placeholder="请输入紧急联系人姓名" />
          </el-form-item>
          <el-form-item label="紧急联系人电话" prop="emergencyContactPhone">
            <el-input v-model="form.emergencyContactPhone" placeholder="请输入紧急联系人电话" />
          </el-form-item>
          <el-form-item label="健康状况" prop="healthCondition">
            <el-input v-model="form.healthCondition" placeholder="请输入健康状况" />
          </el-form-item>
        </template>

        <!-- 家属角色特定的表单项 -->
        <template v-if="form.roleIds.includes(2)">
          <el-form-item label="绑定老人" prop="elderId">
            <el-select v-model="form.elderId" placeholder="请选择要绑定的老人" clearable>
              <el-option
                v-for="elder in elderList"
                :key="elder.value"
                :label="elder.label"
                :value="elder.value"
              />
            </el-select>
            <el-button 
              v-if="dialogType === 'edit' && form.elderId" 
              type="danger" 
              link 
              style="margin-left: 10px"
              @click="handleUnbindElder"
            >
              解除绑定
            </el-button>
          </el-form-item>
          <el-form-item label="关系" prop="relationship">
            <el-select v-model="form.relationship" placeholder="请选择与老人的关系">
              <el-option label="子女" value="子女" />
              <el-option label="配偶" value="配偶" />
              <el-option label="兄弟姐妹" value="兄弟姐妹" />
              <el-option label="其他亲属" value="其他亲属" />
            </el-select>
          </el-form-item>
        </template>

        <!-- 社区工作人员角色特定的表单项 -->
        <template v-if="form.roleIds.includes(3)">
          <el-form-item label="部门" prop="department">
            <el-input v-model="form.department" placeholder="请输入部门" />
          </el-form-item>
          <el-form-item label="岗位" prop="position">
            <el-input v-model="form.position" placeholder="请输入岗位" />
          </el-form-item>
        </template>
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
import { ref, watch, onMounted, reactive } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Delete, Edit, Plus, Upload, Download, Refresh, ArrowDown } from '@element-plus/icons-vue';
import { formatDate } from '@/utils/date';
import { useUserStore } from '@/stores/back/userStore';
import { storeToRefs } from 'pinia';
import RightToolbar from '@/components/RightToolbar/index.vue';
import Pagination from '@/components/common/Pagination.vue';
import { pinyin } from 'pinyin-pro';

const userStore = useUserStore();
const { userList, total, loading } = storeToRefs(userStore);

// 添加老人列表
const elderList = ref([]);

// 查询参数
const queryParams = reactive({
  current: 1,
  size: 10,
  username: '',
  isActive: undefined,
  startTime: '',
  endTime: ''
});

// 日期范围
const dateRange = ref([]);

// 显示搜索条件
const showSearch = ref(true);

// 非单个禁用
const single = ref(true);
// 非多个禁用
const multiple = ref(true);

// 列信息
const columns = ref([
  { key: 0, label: '选择', visible: true },
  { key: 1, label: '用户编号', visible: true },
  { key: 2, label: '用户名称', visible: true },
  { key: 3, label: '姓名', visible: true },
  { key: 4, label: '状态', visible: true },
  { key: 5, label: '手机号码', visible: true },
  { key: 6, label: '邮箱', visible: true },
  { key: 7, label: '角色', visible: true },
  { key: 8, label: '绑定关系', visible: true },
  { key: 9, label: '创建时间', visible: true },
  { key: 10, label: '操作', visible: true }
]);

// 添加角色映射
const roleMap = {
  1: '老人',
  2: '家属',
  3: '社区工作人员',
  4: '管理员'
};

// 判断用户是否拥有某角色
const hasRole = (user, roleId) => {
  return user.roleIds && user.roleIds.includes(roleId);
};

// 获取列表数据
const getList = async () => {
  await userStore.fetchUsers(queryParams);
  
  // 处理用户绑定关系信息
  const promises = userList.value.map(async (user) => {
    // 确保roleIds不为null
    if (!user.roleIds) {
      user.roleIds = [];
    }

    // 如果是家属角色且有elderId
    if (hasRole(user, 2) && user.elderId) {
      try {
        // 获取家属绑定的老人名称
        const elderResponse = await userStore.fetchElderName(user.elderId);
        if (elderResponse?.data) {
          user.elderName = elderResponse.data;
        }
      } catch (error) {
        console.error(`获取用户${user.userId}绑定的老人信息失败:`, error);
      }
    }
  });
  
  // 等待所有异步操作完成
  await Promise.all(promises);
};

// 搜索按钮操作
const handleQuery = () => {
  queryParams.current = 1;
  // 处理日期范围
  if (dateRange.value && dateRange.value.length === 2) {
    queryParams.startTime = dateRange.value[0];
    queryParams.endTime = dateRange.value[1];
  } else {
    queryParams.startTime = '';
    queryParams.endTime = '';
  }
  getList();
};

// 重置按钮操作
const resetQuery = () => {
  dateRange.value = [];
  queryParams.username = '';
  queryParams.isActive = undefined;
  queryParams.startTime = '';
  queryParams.endTime = '';
  handleQuery();
};

// 多选框选中数据
const handleSelectionChange = (selection) => {
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
};

// 状态修改
const handleStatusChange = async (row) => {
  const text = row.isActive === 1 ? '启用' : '停用';
  try {
    await ElMessageBox.confirm(`确认要"${text}""${row.username}"用户吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });

    // 确保roleIds不为null
    if (!row.roleIds) {
      row.roleIds = [];
    }

    const success = await userStore.handleUpdateUser({
      userId: row.userId,
      isActive: row.isActive,
      roleIds: row.roleIds // 添加roleIds
    });

    if (!success) {
      row.isActive = row.isActive === 1 ? 0 : 1;
    }
  } catch {
    row.isActive = row.isActive === 1 ? 0 : 1;
  }
};

// 更多操作触发
const handleMoreActions = (command, row) => {
  switch (command) {
    case 'resetPassword':
      handleResetPassword(row);
      break;
  }
};

// 重置密码
const handleResetPassword = async (row) => {
  try {
    await ElMessageBox.confirm(`确认重置"${row.username}"的密码吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    await userStore.handleResetPassword(row.userId);
  } catch (error) {
    console.error('重置密码操作取消或失败:', error);
  }
};

// 导入
const handleImport = () => {

};

// 导出
const handleExport = () => {
};

// 对话框相关
const dialogType = ref('add');
const dialogVisible = ref(false);
const roleDialogVisible = ref(false);
const formRef = ref(null);

const form = ref({
  userId: undefined,
  username: '',
  name: '',
  password: '',
  phone: '',
  email: '',
  isActive: 1,
  roleIds: [],
  elderId: null,
  relationship: '',
  department: '',
  position: '',
  idCard: '',
  birthday: '',
  age: '',
  emergencyContactName: '',
  emergencyContactPhone: '',
  healthCondition: ''
});
const roleForm = ref({
  userId: undefined,
  username: undefined,
  roleIds: []
});
const roles = [
  { value: 1, label: '老人' },
  { value: 2, label: '家属' },
  { value: 3, label: '社区工作人员' },
  { value: 4, label: '管理员' }
];

const isKin = ref(false);
const isStaff = ref(false);
const isElder = ref(false);
const handleRoleChange = (selectedRoles) => {
  // 检查是否同时选择了老人和家属角色
  if (selectedRoles.includes(1) && selectedRoles.includes(2)) {
    ElMessage.warning('不能同时选择老人和家属角色');
    // 恢复之前的选择
    form.value.roleIds = form.value.roleIds.filter(roleId => !(roleId === 1 || roleId === 2));
    return;
  }

  // 更新角色状态
  isKin.value = selectedRoles.includes(2);
  isStaff.value = selectedRoles.includes(3);
  isElder.value = selectedRoles.includes(1);

  // 如果是家属角色，获取可绑定的老人列表
  if (isKin.value) {
    getElderList();
  } else {
    // 如果不是家属角色，清空老人相关字段
    form.value.elderId = null;
    form.value.relationship = '';
    elderList.value = [];
  }
};

// 监听身份证号码变化并计算出生日期和年龄
watch(() => form.value.idCard, (newIdCard) => {
  if (newIdCard && newIdCard.length === 18) {
    // 从身份证提取年月日
    const year = newIdCard.substring(6, 10);
    const month = newIdCard.substring(10, 12);
    const day = newIdCard.substring(12, 14);
    
    // 构造符合后端LocalDate格式的日期字符串 YYYY-MM-DD
    const birthDateStr = `${year}-${month}-${day}`;
    form.value.birthday = birthDateStr;
    
    // 计算年龄
    const currentYear = new Date().getFullYear();
    form.value.age = currentYear - parseInt(year);
  } else {
    form.value.birthday = '';
    form.value.age = '';
  }
});
//监听姓名变化生成用户账号(姓氏拼音首字母 + 名字拼音)
watch(() => form.value.name, (newName) => {
  if (newName) {
    // 分割姓和名
    const firstName = newName.charAt(0); // 获取第一个字作为姓
    const lastName = newName.slice(1);   // 剩余部分作为名
    
    // 获取姓的拼音首字母（大写）
    const firstPinyin = pinyin(firstName, { pattern: 'first' }).toUpperCase();
    // 获取名字的完整拼音（不带声调）
    const lastNamePinyin = pinyin(lastName, { toneType: 'none' }).replace(/\s/g, '');
    
    // 组合：姓氏拼音首字母 + 名字拼音
    form.value.username = `${firstPinyin}${lastNamePinyin}`;
  } else {
    form.value.username = '';
  }
});
// 表单校验规则
const rules = {
  username: [{ required: true, message: '请输入用户名称', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号码', trigger: 'blur' }],
  email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }],
  roleIds: [{ required: true, message: '请选择角色', trigger: 'change' }]
};

// 取消按钮
const cancel = () => {
  dialogVisible.value = false;
  reset();
};

// 表单重置
const reset = () => {
  form.value = {
    userId: undefined,
    username: '',
    name: '',
    password: '123456',  // 设置默认密码
    phone: '',
    email: '',
    isActive: 1,
    roleIds: [],
    elderId: null,
    relationship: '',
    department: '',
    position: '',
    idCard: '',
    birthday: '',
    age: '',
    emergencyContactName: '',
    emergencyContactPhone: '',
    healthCondition: ''
  };
  formRef.value?.resetFields();
  // 重置角色状态
  isKin.value = false;
  isStaff.value = false;
  isElder.value = false;
};

// 新增按钮操作
const handleAdd = () => {
  dialogType.value = 'add';
  reset();
  dialogVisible.value = true;
};

// 修改按钮操作
const handleUpdate = async (row) => {
  try {
    dialogType.value = 'edit';
    reset();

    // 获取用户详细信息
    const userResponse = await userStore.handleGetUserInfo(row.userId);
    if (!userResponse?.data) {
      ElMessage.error('获取用户信息失败');
      return;
    }

    const userData = userResponse.data;
    const roleIds = userData.roleIds || [];

    // 填充表单数据
    form.value = {
      ...userData,
      userId: row.userId, // 确保设置userId
      roleIds: roleIds,
      elderId: userData.bindElderIds?.[0] || null, // 获取绑定的老人ID
      relationship: userData.relationType || '' // 获取关系类型
    };

    // 更新角色状态
    isKin.value = roleIds.includes(2);
    isStaff.value = roleIds.includes(3);
    isElder.value = roleIds.includes(1);

    // 如果是家属角色，获取可绑定的老人列表
    if (isKin.value) {
      await getElderList();
    }

    dialogVisible.value = true;
  } catch (error) {
    console.error('获取用户信息失败:', error);
    ElMessage.error('获取用户信息失败');
  }
};

// 删除按钮操作
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`是否确认删除用户"${row.username}"？`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });

    const success = await userStore.deleteUser(row.userId);
    if (success) {
      await getList();
    }
  } catch (error) {
    console.error('删除操作取消或失败:', error);
  }
};

// 表单提交
const submitForm = () => {
  formRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        // 确保userId存在
        if (!form.value.userId) {
          ElMessage.error('用户ID不能为空');
          return;
        }

        const success = form.value.userId ?
          await userStore.handleUpdateUser(form.value) :
          await userStore.handleAddUser(form.value);

        if (success) {
          dialogVisible.value = false;
          await getList();
        }
      } catch (error) {
        console.error('提交表单失败:', error);
        ElMessage.error('提交表单失败');
      }
    }
  });
};

// 角色表单提交
const submitRoleForm = async () => {
  const success = await userStore.assignUserRole(
    roleForm.value.userId,
    roleForm.value.roleIds
  );

  if (success) {
    roleDialogVisible.value = false;
    await getList();
  }
};

// 获取未绑定家属的老人列表
const getElderList = async () => {
  try {
    const response = await userStore.fetchElderList();
    if (response?.data) {
      // 处理未绑定老人的列表
      elderList.value = response.data.map(elder => ({
        value: elder.userId,
        label: `${elder.name}（${elder.idCard ? elder.idCard : '未填写身份证'}）`
      }));
      
      // 如果是编辑模式，需要获取当前绑定的老人信息
      if (dialogType.value === 'edit' && form.value.elderId) {
        const elderResponse = await userStore.handleGetUserInfo(form.value.elderId);
        if (elderResponse?.data) {
          // 检查是否已经在列表中
          const existingElder = elderList.value.find(item => item.value === form.value.elderId);
          if (!existingElder) {
            // 如果当前绑定的老人不在未绑定列表中，添加到列表开头
            elderList.value.unshift({
              value: elderResponse.data.userId,
              label: `${elderResponse.data.name}（${elderResponse.data.idCard ? elderResponse.data.idCard : '未填写身份证'}）`
            });
          }
        }
      }
    } else {
      elderList.value = [];
      if (!form.value.elderId) {
        ElMessage.warning('暂无可绑定的老人');
      }
    }
  } catch (error) {
    console.error('获取老人列表失败:', error);
    ElMessage.error('获取老人列表失败');
    elderList.value = [];
  }
};

// 解除家属与老人的绑定
const handleUnbindElder = async () => {
  try {
    await ElMessageBox.confirm('确认要解除与当前老人的绑定关系吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    // 暂存当前家属ID和老人ID
    const kinId = form.value.userId;
    const elderId = form.value.elderId;
    
    // 调用解除绑定接口
    const success = await userStore.unbindElder(kinId);
    
    if (success) {
      ElMessage.success('解除绑定成功');
      form.value.elderId = null;
      form.value.relationship = '';
      
      // 重新获取老人列表
      await getElderList();
    } else {
      ElMessage.error('解除绑定失败');
    }
  } catch (error) {
    console.error('解除绑定操作取消或失败:', error);
  }
};

// 在script setup部分添加getRoleTagType方法
const getRoleTagType = (roleId) => {
  switch (roleId) {
    case 1: // 老人
      return 'success';
    case 2: // 家属
      return 'warning';
    case 3: // 社区工作人员
      return 'info';
    case 4: // 管理员
      return 'danger';
    default:
      return 'info';
  }
};

// 获取未绑定老人的家属列表
const getUnboundKinsList = async () => {
  try {
    const response = await userStore.fetchUnboundKins();
    if (response?.data) {
      // 处理未绑定老人的家属列表
      console.log('未绑定老人的家属列表:', response.data);
    }
  } catch (error) {
    console.error('获取未绑定老人的家属列表失败:', error);
    ElMessage.error('获取未绑定老人的家属列表失败');
  }
};

// 绑定老人和家属关系
const handleBindElderKin = async (elderId, kinId, relationType) => {
  try {
    const success = await userStore.handleBindElderKinRelation(elderId, kinId, relationType);
    if (success) {
      ElMessage.success('绑定关系成功');
      // 刷新列表
      await getList();
    }
  } catch (error) {
    console.error('绑定关系失败:', error);
    ElMessage.error('绑定关系失败');
  }
};

// 解绑老人和家属关系
const handleUnbindElderKin = async (elderId, kinId) => {
  try {
    const success = await userStore.handleUnbindElderKinRelation(elderId, kinId);
    if (success) {
      ElMessage.success('解绑关系成功');
      // 刷新列表
      await getList();
    }
  } catch (error) {
    console.error('解绑关系失败:', error);
    ElMessage.error('解绑关系失败');
  }
};

// 获取老人的所有家属ID
const getKinIdsByElder = async (elderId) => {
  try {
    const response = await userStore.handleGetKinIdsByElderId(elderId);
    if (response?.data) {
      console.log('老人的所有家属ID:', response.data);
    }
  } catch (error) {
    console.error('获取老人的所有家属ID失败:', error);
    ElMessage.error('获取老人的所有家属ID失败');
  }
};

// 获取家属的所有老人ID
const getElderIdsByKin = async (kinId) => {
  try {
    const response = await userStore.handleGetElderIdsByKinId(kinId);
    if (response?.data) {
      console.log('家属的所有老人ID:', response.data);
    }
  } catch (error) {
    console.error('获取家属的所有老人ID失败:', error);
    ElMessage.error('获取家属的所有老人ID失败');
  }
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
  padding-left: 5px;
  padding-right: 5px;
}

.fixed-width .el-button--small {
  padding: 7px 10px;
  min-width: 60px;
}
</style>
