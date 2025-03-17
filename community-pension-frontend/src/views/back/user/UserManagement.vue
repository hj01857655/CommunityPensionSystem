<template>
  <div class="user-management">
    <el-card shadow="hover" class="table-card">
      <template #header>
        <div class="card-header">
          <h3>用户管理</h3>
          <div class="header-actions">
            <el-input v-model="searchQuery" placeholder="搜索用户名" class="search-input" clearable @clear="handleSearch">
              <template #prefix>
                <el-icon>
                  <Search />
                </el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button type="success" @click="handleAdd">添加用户</el-button>
            <el-dropdown @command="handleAdvancedSearch" split-button type="primary">
              高级搜索
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="byRole">按角色搜索</el-dropdown-item>
                  <el-dropdown-item command="byStatus">按状态搜索</el-dropdown-item>
                  <el-dropdown-item command="byDate">按日期搜索</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </template>

      <!-- 高级搜索表单 -->
      <div v-if="showAdvancedSearch" class="advanced-search-form">
        <el-form :inline="true" :model="advancedSearchForm">
          <el-form-item label="角色" v-if="advancedSearchType === 'byRole'">
            <el-select v-model="advancedSearchForm.roleId" placeholder="请选择角色">
              <el-option label="全部" :value="0" />
              <el-option label="老人" :value="1" />
              <el-option label="老人家属" :value="2" />
              <el-option label="社区工作人员" :value="3" />
              <el-option label="管理员" :value="4" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态" v-if="advancedSearchType === 'byStatus'">
            <el-select v-model="advancedSearchForm.status" placeholder="请选择状态">
              <el-option label="全部" :value="-1" />
              <el-option label="启用" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item label="创建日期" v-if="advancedSearchType === 'byDate'">
            <el-date-picker v-model="advancedSearchForm.dateRange" type="daterange" range-separator="至"
              start-placeholder="开始日期" end-placeholder="结束日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="applyAdvancedSearch">应用</el-button>
            <el-button @click="resetAdvancedSearch">重置</el-button>
            <el-button @click="showAdvancedSearch = false">关闭</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 工具栏 -->
      <div class="toolbar">
        <div class="batch-actions">
          <el-button-group>
            <el-button type="danger" :disabled="!selectedUsers.length" @click="handleBatchDelete">
              <el-icon>
                <Delete />
              </el-icon> 批量删除
            </el-button>
            <el-button type="warning" :disabled="!selectedUsers.length" @click="handleBatchEnable">
              <el-icon>
                <Check />
              </el-icon> 批量启用
            </el-button>
            <el-button type="info" :disabled="!selectedUsers.length" @click="handleBatchDisable">
              <el-icon>
                <Close />
              </el-icon> 批量禁用
            </el-button>
          </el-button-group>
        </div>
        <div class="export-actions">
          <el-button type="success" @click="handleExportExcel">
            <el-icon>
              <Document />
            </el-icon> 导出Excel
          </el-button>
          <el-button type="primary" @click="handlePrint">
            <el-icon>
              <Printer />
            </el-icon> 打印
          </el-button>
          <el-button @click="refreshUserList">
            <el-icon>
              <Refresh />
            </el-icon> 刷新
          </el-button>
        </div>
      </div>

      <el-table :data="userList" style="width: 100%" v-loading="loading" border
        @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="isAdmin" label="管理员" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isAdmin ? 'danger' : 'info'">
              {{ row.isAdmin ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isActive" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.isActive)">
              {{ getStatusText(row.isActive) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" show-overflow-tooltip />
        <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="roleId" label="角色" width="120">
          <template #default="scope">
            <el-tag :type="getRoleType(scope.row.roleId)">
              {{ getRoleText(scope.row.roleId) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.updateTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="scope">
            <el-button-group>
              <el-button type="primary" size="small" @click="handleEdit(scope.row)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button type="success" size="small" @click="handleView(scope.row)">
                <el-icon><View /></el-icon>
                查看
              </el-button>
              <el-button type="danger" size="small" @click="handleDelete(scope.row)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 用户表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增用户' : dialogType === 'edit' ? '编辑用户' : '查看用户'"
      width="600px"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userRules"
        label-width="100px"
        :disabled="dialogType === 'view'"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="userForm.username" placeholder="请输入用户名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
              <el-input v-model="userForm.password" type="password" placeholder="请输入密码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色" prop="roleId">
              <el-select v-model="userForm.roleId" placeholder="请选择角色">
                <el-option label="老人" :value="1" />
                <el-option label="老人家属" :value="2" />
                <el-option label="社区工作人员" :value="3" />
                <el-option label="管理员" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="userForm.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="userForm.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="isActive">
              <el-select v-model="userForm.isActive" placeholder="请选择状态">
                <el-option label="正常" :value="1" />
                <el-option label="禁用" :value="0" />
                <el-option label="锁定" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="地址" prop="address">
              <el-input v-model="userForm.address" placeholder="请输入地址" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="管理员" prop="isAdmin">
              <el-switch
                v-model="userForm.isAdmin"
                :active-value="1"
                :inactive-value="0"
                inline-prompt
                active-text="是"
                inactive-text="否"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="头像" prop="avatar">
              <el-upload
                class="avatar-uploader"
                action="/api/upload"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
                :disabled="dialogType === 'view'"
              >
                <img v-if="userForm.avatar" :src="getAvatarUrl(userForm.avatar)" class="avatar" />
                <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="userForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" v-if="dialogType !== 'view'">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Delete, Check, Close, Document, Printer, Refresh, Plus, Edit, View } from '@element-plus/icons-vue';
import { formatDate } from '@/utils/date';
import { useUserStore } from '@/stores/back/userStore';
import { useRoute } from 'vue-router';

const userStore = useUserStore();
const route = useRoute();

// 使用计算属性确保响应式
const userList = computed(() => userStore.userList);
const loading = computed(() => userStore.loading);
const total = computed(() => userStore.total);
const currentPage = computed(() => userStore.currentPage);
const pageSize = computed(() => userStore.pageSize);
const searchQuery = computed(() => userStore.searchQuery);

onMounted(() => {
  userStore.fetchUsers({
    current: userStore.currentPage,
    size: userStore.pageSize,
    username: userStore.searchQuery,
  });
});

// 添加路由监听，在路由切换时重新加载数据
watch(() => route.name, (newRouteName) => {
  if (newRouteName === 'UserManagement') {
    userStore.fetchUsers({
      current: userStore.currentPage,
      size: userStore.pageSize,
      username: userStore.searchQuery,
    });
  }
});

// 过滤后的用户列表
const filteredUsers = computed(() => {
  if (!userList.value) return [];

  let result = userList.value;

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(user =>
      (user.username && user.username.toLowerCase().includes(query))
    );
  }

  // 应用高级搜索过滤
  if (advancedSearchForm.roleId > 0) {
    result = result.filter(user => user.roleId === advancedSearchForm.roleId);
  }

  if (advancedSearchForm.status !== -1) {
    result = result.filter(user => user.status === advancedSearchForm.status);
  }

  if (advancedSearchForm.dateRange && advancedSearchForm.dateRange.length === 2) {
    const startDate = new Date(advancedSearchForm.dateRange[0]).getTime();
    const endDate = new Date(advancedSearchForm.dateRange[1]).getTime();
    result = result.filter(user => {
      const createTime = new Date(user.createTime).getTime();
      return createTime >= startDate && createTime <= endDate;
    });
  }

  // 确保 result 是一个数组
  if (!Array.isArray(result)) {
    result = [];
  }

  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;

  return result.slice(start, end);
})

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add') // 'add' 或 'edit'
const userFormRef = ref(null)
const userForm = reactive({
  id: '',
  username: '',
  password: '',
  roleId: '',
  phone: '',
  email: '',
  address: '',
  isActive: 1,
  isAdmin: 0,
  avatar: '',
  remark: ''
})

// 表单验证规则
const userRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '长度在 3 到 50 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  password: [
    { required: dialogType.value === 'add', message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' },
    { pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{6,}$/, message: '密码必须包含大小写字母和数字', trigger: 'blur' }
  ],
  roleId: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入地址', trigger: 'blur' }
  ]
};

// 获取角色文本
const getRoleText = (roleId) => {
  const roleMap = {
    1: '老人',
    2: '老人家属',
    3: '社区工作人员',
    4: '管理员'
  };
  return roleMap[roleId] || '未知角色';
}
// 获取角色标签类型
const getRoleType = (roleId) => {
  const typeMap = {
    1: 'info',
    2: 'success',
    3: 'warning',
    4: 'danger'
  };
  return typeMap[roleId] || 'info';
}

// 搜索用户
const handleSearch = () => {
  userStore.currentPage = 1;
  userStore.fetchUsers({
    current: userStore.currentPage,
    size: userStore.pageSize,
    username: userStore.searchQuery,
  });
};

// 添加用户
const handleAdd = () => {
  dialogType.value = 'add'
  resetForm()
  dialogVisible.value = true
}
// 编辑用户
const handleEdit = (row) => {
  dialogType.value = 'edit'
  Object.assign(userForm, {
    id: row.id,
    username: row.username,
    roleId: row.roleId,
    phone: row.phone,
    email: row.email,
    address: row.address,
    isActive: row.isActive,
    isAdmin: row.isAdmin,
    avatar: row.avatar,
    remark: row.remark
  })
  dialogVisible.value = true
}

// 删除用户
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除用户 ${row.username || row.name || ''} 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 调用删除用户的API
    userStore.deleteUser(row.id)
      .then(res => {
        if (res.code === 200) {
          userStore.fetchUsers({ current: userStore.currentPage, size: userStore.pageSize, username: userStore.searchQuery });
          ElMessage.success('删除成功')
        } else {
          ElMessage.error(res.message || '删除失败')
        }
      })
      .catch(error => {
        ElMessage.error('删除失败：' + error.message)
      })
  }).catch(() => {
    // 取消删除
  })
}

// 重置密码
const handleResetPassword = (row) => {
  ElMessageBox.confirm(
    `确定要重置 ${row.username} 的密码吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    userStore.resetPassword(row.id)
      .then(res => {
        if (res.code === 200) {
          ElMessage.success('密码重置成功')
        } else {
          ElMessage.error(res.message || '密码重置失败')
        }
      })
      .catch(error => {
        ElMessage.error('密码重置失败：' + error.message)
      })
  }).catch(() => {
    ElMessage.info('取消重置')
  })
}

// 修改用户状态
const handleStatusChange = (row) => {
  const originalStatus = row.isActive // 使用 isActive 替代 status
  const updateData = {
    id: row.id,
    isActive: row.isActive,
    ...row
  }
  
  userStore.updateUser(updateData)
    .then(res => {
      if (res.code === 200) {
        ElMessage.success('状态更新成功')
        userStore.fetchUsers({ current: userStore.currentPage, size: userStore.pageSize, username: userStore.searchQuery });
      } else {
        row.isActive = originalStatus
        ElMessage.error(res.message || '状态更新失败')
      }
    })
    .catch(error => {
      row.isActive = originalStatus
      ElMessage.error('状态更新失败：' + error.message)
    })
}

// 分页大小变化
const handleSizeChange = (val) => {
  userStore.pageSize = val;
  userStore.fetchUsers({
    current: userStore.currentPage,
    size: userStore.pageSize,
    username: userStore.searchQuery,
  });
}

// 当前页变化
const handleCurrentChange = (val) => {
  userStore.currentPage = val;
  userStore.fetchUsers({
    current: userStore.currentPage,
    size: userStore.pageSize,
    username: userStore.searchQuery,
  });
}

// 重置表单
const resetForm = () => {
  if (userFormRef.value) {
    userFormRef.value.resetFields()
  }
  Object.assign(userForm, {
    id: '',
    username: '',
    password: '',
    roleId: '',
    phone: '',
    email: '',
    address: '',
    isActive: 1,
    isAdmin: 0,
    avatar: '',
    remark: ''
  })
}

const refreshUserList = () => {
  userStore.fetchUsers({ current: userStore.currentPage, size: userStore.pageSize, username: userStore.searchQuery });
}

// 修改提交表单逻辑
// 高级搜索相关
const showAdvancedSearch = ref(false)
const advancedSearchType = ref('')
const advancedSearchDialogVisible = ref(false)
const advancedSearchForm = reactive({
  roleId: 0,
  status: -1,
  dateRange: []
})

// 批量操作相关
const selectedUsers = ref([])

// 处理表格选择变化
const handleSelectionChange = (selection) => {
  selectedUsers.value = selection
}

// 高级搜索
const handleAdvancedSearch = (command) => {
  advancedSearchType.value = command
  showAdvancedSearch.value = true
}

// 应用高级搜索
const applyAdvancedSearch = () => {
  userStore.currentPage = 1
  advancedSearchDialogVisible.value = false
  showAdvancedSearch.value = false
}

// 重置高级搜索
const resetAdvancedSearch = () => {
  advancedSearchForm.roleId = 0
  advancedSearchForm.status = -1
  advancedSearchForm.dateRange = []
}

// 批量删除
const handleBatchDelete = () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请至少选择一个用户')
    return
  }

  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedUsers.value.length} 个用户吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 这里应该调用批量删除API，但目前后端可能没有提供，所以使用循环单个删除
    const deletePromises = selectedUsers.value.map(user => userStore.deleteUser(user.id))

    Promise.all(deletePromises)
      .then(results => {
        const allSuccess = results.every(res => res.code === 200)
        if (allSuccess) {
          userStore.fetchUsers({ current: userStore.currentPage, size: userStore.pageSize, username: userStore.searchQuery });
          ElMessage.success('批量删除成功')
        } else {
          ElMessage.warning('部分用户删除失败')
          userStore.fetchUsers({ current: userStore.currentPage, size: userStore.pageSize, username: userStore.searchQuery });
        }
      })
      .catch(error => {
        ElMessage.error('批量删除失败：' + error.message)
      })
  }).catch(() => {
    // 取消删除
  })
}

// 批量启用
const handleBatchEnable = () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请至少选择一个用户')
    return
  }

  const updatePromises = selectedUsers.value.map(user => userStore.updateUser({ 
    id: user.id, 
    isActive: 1,
    ...user  // 保留其他字段
  }))

  Promise.all(updatePromises)
    .then(results => {
      const allSuccess = results.every(res => res.code === 200)
      if (allSuccess) {
        userStore.fetchUsers({ current: userStore.currentPage, size: userStore.pageSize, username: userStore.searchQuery });
        ElMessage.success('批量启用成功')
      } else {
        ElMessage.warning('部分用户启用失败')
        userStore.fetchUsers({ current: userStore.currentPage, size: userStore.pageSize, username: userStore.searchQuery });
      }
    })
    .catch(error => {
      ElMessage.error('批量启用失败：' + error.message)
      userStore.fetchUsers({ current: userStore.currentPage, size: userStore.pageSize, username: userStore.searchQuery });
    })
}

// 批量禁用
const handleBatchDisable = () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请至少选择一个用户')
    return
  }

  const updatePromises = selectedUsers.value.map(user => userStore.updateUser({ 
    id: user.id, 
    isActive: 0,
    ...user  // 保留其他字段
  }))

  Promise.all(updatePromises)
    .then(results => {
      const allSuccess = results.every(res => res.code === 200)
      if (allSuccess) {
        userStore.fetchUsers({ current: userStore.currentPage, size: userStore.pageSize, username: userStore.searchQuery });
        ElMessage.success('批量禁用成功')
      } else {
        ElMessage.warning('部分用户禁用失败')
        userStore.fetchUsers({ current: userStore.currentPage, size: userStore.pageSize, username: userStore.searchQuery });
      }
    })
    .catch(error => {
      ElMessage.error('批量禁用失败：' + error.message)
      userStore.fetchUsers({ current: userStore.currentPage, size: userStore.pageSize, username: userStore.searchQuery });
    })
}

// 导出Excel
const handleExportExcel = () => {
  ElMessage.success('导出Excel功能已触发，实际导出功能需要后端支持')
  // 实际导出功能需要后端支持，这里只是示例
  // 可以使用第三方库如xlsx.js实现前端导出
}

// 打印功能
const handlePrint = () => {
  ElMessage.success('正在准备打印...')
  // 保存原始标题
  const originalTitle = document.title
  // 设置打印时的标题
  document.title = '用户管理列表'

  // 创建打印样式
  const style = document.createElement('style')
  style.innerHTML = `
    @media print {
      /* 隐藏不需要打印的元素 */
      .header-actions, .toolbar, .pagination-container, 
      .el-table-column--selection, .el-table-column--fixed-right,
      .el-button, .el-switch__core {
        display: none !important;
      }
      /* 调整表格样式 */
      .el-table {
        width: 100% !important;
        border-collapse: collapse !important;
      }
      .el-table th, .el-table td {
        border: 1px solid #dcdfe6 !important;
      }
      /* 确保内容完整显示 */
      .el-table__body {
        width: 100% !important;
      }
      /* 分页打印设置 */
      @page {
        size: A4 landscape;
        margin: 2cm;
      }
    }
  `
  document.head.appendChild(style)

  // 执行打印
  window.print()

  // 打印完成后清理
  setTimeout(() => {
    document.head.removeChild(style)
    document.title = originalTitle
  }, 100)
}

const submitForm = () => {
  userFormRef.value.validate((valid) => {
    if (valid) {
      userStore.loading = true
      const submitData = {
        ...userForm,
        roleId: parseInt(userForm.roleId),
        updateTime: new Date().toISOString()
      }

      if (dialogType.value === 'add') {
        submitData.createTime = new Date().toISOString()
        
        userStore.addUser(submitData)
          .then(res => {
            if (res.code === 200) {
              userStore.fetchUsers({ current: userStore.currentPage, size: userStore.pageSize, username: userStore.searchQuery });
              ElMessage.success('添加用户成功')
              dialogVisible.value = false
            } else {
              ElMessage.error(res.message || '添加用户失败')
            }
          })
          .catch(error => {
            ElMessage.error('添加用户失败：' + error.message)
          })
          .finally(() => {
            userStore.loading = false
          })
      } else {
        // 编辑时不需要传递密码，除非有修改
        if (!submitData.password) {
          delete submitData.password
        }
        
        userStore.updateUser(submitData)
          .then(res => {
            if (res.code === 200) {
              userStore.fetchUsers({ current: userStore.currentPage, size: userStore.pageSize, username: userStore.searchQuery });
              ElMessage.success('编辑用户成功')
              dialogVisible.value = false
            } else {
              ElMessage.error(res.message || '编辑用户失败')
            }
          })
          .catch(error => {
            ElMessage.error('编辑用户失败：' + error.message)
          })
          .finally(() => {
            userStore.loading = false
          })
      }
    } else {
      return false
    }
  })
}

// 状态相关
const getStatusType = (status) => {
  const typeMap = {
    0: 'danger',
    1: 'success',
    2: 'warning'
  };
  return typeMap[status] || 'info';
};

const getStatusText = (status) => {
  const textMap = {
    0: '禁用',
    1: '正常',
    2: '锁定'
  };
  return textMap[status] || '未知';
};

// 查看用户
const handleView = (row) => {
  dialogType.value = 'view'
  Object.assign(userForm, {
    id: row.id,
    username: row.username,
    roleId: row.roleId,
    phone: row.phone,
    email: row.email,
    address: row.address,
    isActive: row.isActive,
    isAdmin: row.isAdmin,
    avatar: row.avatar,
    remark: row.remark
  })
  dialogVisible.value = true
}

// 头像上传相关
const handleAvatarSuccess = (response, file) => {
  if (response.code === 200) {
    userForm.avatar = response.data;
    ElMessage.success('头像上传成功');
  } else {
    ElMessage.error('头像上传失败');
  }
};

const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isJPG) {
    ElMessage.error('上传头像图片只能是 JPG/PNG 格式!');
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!');
  }
  return isJPG && isLt2M;
};

// 获取头像URL
const getAvatarUrl = (avatarPath) => {
  return `http://localhost:8000/src/assets${avatarPath}`;
};
</script>

<style scoped>
.user-management {
  padding: 10px;
}

.table-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-input {
  width: 250px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.advanced-search-form {
  margin: 15px 0;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.toolbar {
  margin: 15px 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.batch-actions,
.export-actions {
  display: flex;
  gap: 10px;
}

@media print {

  .header-actions,
  .toolbar,
  .pagination-container,
  .el-table-column--selection,
  .el-table-column--fixed-right {
    display: none !important;
  }
}

.avatar-uploader {
  text-align: center;
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
  line-height: 100px;
}

.avatar {
  width: 100px;
  height: 100px;
  display: block;
}
</style>