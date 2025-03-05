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
            <el-date-picker
              v-model="advancedSearchForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
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
              <el-icon><Delete /></el-icon> 批量删除
            </el-button>
            <el-button type="warning" :disabled="!selectedUsers.length" @click="handleBatchEnable">
              <el-icon><Check /></el-icon> 批量启用
            </el-button>
            <el-button type="info" :disabled="!selectedUsers.length" @click="handleBatchDisable">
              <el-icon><Close /></el-icon> 批量禁用
            </el-button>
          </el-button-group>
        </div>
        <div class="export-actions">
          <el-button type="success" @click="handleExportExcel">
            <el-icon><Document /></el-icon> 导出Excel
          </el-button>
          <el-button type="primary" @click="handlePrint">
            <el-icon><Printer /></el-icon> 打印
          </el-button>
          <el-button @click="refreshUserList">
            <el-icon><Refresh /></el-icon> 刷新
          </el-button>
        </div>
      </div>

      <el-table 
        :data="filteredUsers" 
        style="width: 100%" 
        v-loading="loading" 
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="password" label="密码" width="120" />
        <el-table-column prop="roleId" label="角色" width="120">
          <template #default="scope">
            <el-tag :type="getRoleType(scope.row.roleId)">
              {{ getRoleText(scope.row.roleId) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0"
              @change="handleStatusChange(scope.row)" />
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
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
            <el-button type="warning" size="small" @click="handleResetPassword(scope.row)">重置密码</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper" :total="totalUsers" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <!-- 用户表单对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '添加用户' : '编辑用户'" width="500px">
      <el-form ref="userFormRef" :model="userForm" :rules="userRules" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" :disabled="dialogType === 'edit'" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="userForm.password" type="password" show-password />
        </el-form-item>

        <el-form-item label="角色" prop="roleId">
          <el-select v-model="userForm.roleId" placeholder="请选择角色">
            <el-option label="老人" :value="1" />
            <el-option label="老人家属" :value="2" />
            <el-option label="社区工作人员" :value="3" />
            <el-option label="管理员" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="userForm.status" :active-value="1" :inactive-value="0" active-text="启用"
            inactive-text="禁用" />
        </el-form-item>

      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Delete, Check, Close, Document, Printer, Refresh, Plus } from '@element-plus/icons-vue';
import { ElTableColumn } from 'element-plus'
import { getAllUsers, addUser, updateUser, deleteUser, resetPassword } from '@/api/user';
import { formatDate } from '@/utils/date';
const users = ref([])
// 分页和搜索
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const searchQuery = ref('')
const totalUsers = computed(() => {
  return users.value ? users.value.length : 0
})
onMounted(() => {
  loading.value = true
  // 获取用户列表
  getAllUsers().then(response => {
    if (response.success) {
      console.log("获取到用户列表：", response.data.data)
      // 修正数据获取路径，确保获取到数组
      const userData = response.data.data;
      // 修正数据结构，保证 id 作为 key
      users.value = userData.map(item => ({ ...item, id: item.id }))
    } else {
      ElMessage.error(response.message || '获取用户列表失败')
    }
  }).catch(error => {
    ElMessage.error(error.message || '获取用户列表失败')
  }).finally(() => {
    loading.value = false
  })
})
console.log()


// 过滤后的用户列表
const filteredUsers = computed(() => {
  if (!users.value) return []

  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value

  let result = users.value
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(user =>
      (user.username && user.username.toLowerCase().includes(query))
    )
  }
  
  // 应用高级搜索过滤
  if (advancedSearchForm.roleId > 0) {
    result = result.filter(user => user.roleId === advancedSearchForm.roleId)
  }
  
  if (advancedSearchForm.status !== -1) {
    result = result.filter(user => user.status === advancedSearchForm.status)
  }
  
  if (advancedSearchForm.dateRange && advancedSearchForm.dateRange.length === 2) {
    const startDate = new Date(advancedSearchForm.dateRange[0]).getTime()
    const endDate = new Date(advancedSearchForm.dateRange[1]).getTime()
    result = result.filter(user => {
      const createTime = new Date(user.createTime).getTime()
      return createTime >= startDate && createTime <= endDate
    })
  }

  return result.slice(start, end)
})

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add') // 'add' 或 'edit'
const userFormRef = ref(null)
const userForm = reactive({
  id: '',
  username: '',
  roleId: '',
  status: 1,
  password: '',
})

// 表单验证规则
const userRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  password: [
    { required: dialogType.value === 'add', message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' },
    { pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{6,}$/, message: '密码必须包含大小写字母和数字', trigger: 'blur' }
  ],
  roleId: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

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
  console.log(searchQuery.value)
  currentPage.value = 1
}

// 添加用户
const handleAdd = () => {
  dialogType.value = 'add'
  resetForm()
  dialogVisible.value = true
}
// 编辑用户
const handleEdit = (row) => {
  dialogType.value = 'edit'
  Object.keys(userForm).forEach(key => {
    userForm[key] = row[key]
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
    deleteUser(row.id)
      .then(res => {
        if (res.success) {
          refreshUserList()
          ElMessage.success('删除成功')
        } else {
          ElMessage.error('删除失败')
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
    resetPassword(row.id)
      .then(res => {
        if (res.success) {
          ElMessage.success('密码重置成功')
        } else {
          ElMessage.error('密码重置失败')
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
  const updateData = {
    id: row.id,
    status: row.status
  }

  updateUser(updateData)
    .then(res => {
      if (res.success) {
        refreshUserList()
        ElMessage.success('状态更新成功')
      } else {
        row.status = row.status === 1 ? 0 : 1 // 恢复原状态
        ElMessage.error(res.message || '状态更新失败')
      }
    })
    .catch(error => {
      row.status = row.status === 1 ? 0 : 1 // 恢复原状态
      ElMessage.error('状态更新失败：' + error.message)
    })
}


// 分页大小变化
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
}

// 当前页变化
const handleCurrentChange = (val) => {
  currentPage.value = val
}

// 重置表单
const resetForm = () => {
  if (userFormRef.value) {
    userFormRef.value.resetFields()
  }
  Object.keys(userForm).forEach(key => {
    if (key === 'status') {
      userForm[key] = 1
    } else {
      userForm[key] = ''
    }
  })
}

const refreshUserList = () => {
  loading.value = true
  getAllUsers().then(response => {
    if (response.success) {
      const userData = response.data.data
      users.value = userData.map(item => ({ ...item, id: item.id }))
      ElMessage.success('数据刷新成功')
    } else {
      ElMessage.error(response.message || '获取用户列表失败')
    }
  }).catch(error => {
    ElMessage.error(error.message || '获取用户列表失败')
  }).finally(() => {
    loading.value = false
  })
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
  currentPage.value = 1
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
    const deletePromises = selectedUsers.value.map(user => deleteUser(user.id))
    
    Promise.all(deletePromises)
      .then(() => {
        refreshUserList()
        ElMessage.success('批量删除成功')
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

  const updatePromises = selectedUsers.value.map(user => {
    return updateUser({ id: user.id, status: 1 })
  })
  
  Promise.all(updatePromises)
    .then(() => {
      refreshUserList()
      ElMessage.success('批量启用成功')
    })
    .catch(error => {
      ElMessage.error('批量启用失败：' + error.message)
    })
}

// 批量禁用
const handleBatchDisable = () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请至少选择一个用户')
    return
  }

  const updatePromises = selectedUsers.value.map(user => {
    return updateUser({ id: user.id, status: 0 })
  })
  
  Promise.all(updatePromises)
    .then(() => {
      refreshUserList()
      ElMessage.success('批量禁用成功')
    })
    .catch(error => {
      ElMessage.error('批量禁用失败：' + error.message)
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
      loading.value = true
      if (dialogType.value === 'add') {
        const newUser = {
          ...userForm,
          roleId: parseInt(userForm.roleId)
        }

        addUser(newUser)
          .then(res => {
            if (res.success) {
              refreshUserList()
              ElMessage.success('添加用户成功')
              dialogVisible.value = false
            }
          })
          .catch(error => {
            ElMessage.error('添加用户失败：' + error.message)
          })
          .finally(() => {
            loading.value = false
          })
      } else {
        const updateData = {
          ...userForm
        }

        if (userForm.password) {
          updateData.password = userForm.password
        }

        updateUser(updateData)
          .then(res => {
            if (res.success) {
              refreshUserList()
              ElMessage.success('编辑用户成功')
              dialogVisible.value = false
            }
          })
          .catch(error => {
            ElMessage.error('编辑用户失败：' + error.message)
          })
          .finally(() => {
            loading.value = false
          })
      }
    } else {
      return false
    }
  })
}
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
</style>