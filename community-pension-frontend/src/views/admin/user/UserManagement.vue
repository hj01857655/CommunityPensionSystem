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
          </div>
        </div>
      </template>

      <el-table :data="filteredUsers" style="width: 100%" v-loading="loading" border>
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
import { Search } from '@element-plus/icons-vue';
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
</style>