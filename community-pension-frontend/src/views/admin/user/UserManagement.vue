<template>
    <div class="user-management">
      <el-card shadow="hover" class="table-card">
        <template #header>
          <div class="card-header">
            <h3>用户管理</h3>
            <div class="header-actions">
              <el-input
                v-model="searchQuery"
                placeholder="搜索用户名"
                class="search-input"
                clearable
                @clear="handleSearch"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button type="success" @click="handleAdd">添加用户</el-button>
            </div>
          </div>
        </template>
        
        <el-table
          :data="filteredUsers"
          style="width: 100%"
          v-loading="loading"
          border
        >
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="username" label="用户名" width="120" />
          <el-table-column prop="password" label="密码" width="120" />
          <el-table-column prop="role" label="角色" width="120">
            <template #default="scope">
              <el-tag :type="getRoleType(scope.row.role)">
                {{ getRoleText(scope.row.role) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-switch
                v-model="scope.row.status"
                :active-value="1"
                :inactive-value="0"
                @change="handleStatusChange(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column prop="updateTime" label="更新时间" width="180" />
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="scope">
              <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
              <el-button type="warning" size="small" @click="handleResetPassword(scope.row)">重置密码</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="totalUsers"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
      
      <!-- 用户表单对话框 -->
      <el-dialog
        v-model="dialogVisible"
        :title="dialogType === 'add' ? '添加用户' : '编辑用户'"
        width="500px"
      >
        <el-form
          ref="userFormRef"
          :model="userForm"
          :rules="userRules"
          label-width="100px"
        >
          <el-form-item label="用户名" prop="username">
            <el-input v-model="userForm.username" :disabled="dialogType === 'edit'" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="userForm.password" type="password" show-password />
          </el-form-item>
        
          <el-form-item label="角色" prop="role">
            <el-select v-model="userForm.role" placeholder="请选择角色">
              <el-option label="管理员" value="admin" />
              <el-option label="老人" value="elder" />
              <el-option label="老人家属" value="family" />
              <el-option label="社区工作人员" value="worker" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-switch
              v-model="userForm.status"
              :active-value="1"
              :inactive-value="0"
              active-text="启用"
              inactive-text="禁用"
            />
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
  
  // 用户列表数据
  const users = ref([
    {
      id: 1,
      username: 'admin',
      password: '123456',
      role: 'admin',
      status: 1,
      createTime: '2025-01-01 00:00:00',
      updateTime: '2025-01-01 00:00:00'
    },
    {
      id: 2,
      username: 'zhangsan',
      password: '123456',
      role: 'elder',
      status: 1,
      createTime: '2025-01-02 10:30:00',
      updateTime: '2025-01-02 10:30:00'
    },
    {
      id: 3,
      username: 'lisi',
      password: '123456',
      role: 'family',
      status: 1,
      createTime: '2025-01-03 14:20:00',
      updateTime: '2025-01-03 14:20:00'
    },
    {
      id: 4,
      username: 'wangwu',
      password: '123456',
      role: 'worker',
      status: 0,
      createTime: '2025-01-04 09:15:00',
      updateTime: '2025-01-04 09:15:00'
    },
    {
      id: 5,
      username: 'zhaoliu',
      password: '123456',
      role: 'worker',
      status: 0,
      createTime: '2025-01-05 11:20:00',
      updateTime: '2025-01-05 11:20:00'
    },
    {
      id: 6,
      username: 'qianqi',
      password: '123456',
      role: 'worker',
      status: 0,
      createTime: '2025-01-06 12:30:00',
      updateTime: '2025-01-06 12:30:00'
    },
    {
      id: 7,
      username: 'sunba',
      password: '123456',
      role: 'worker',
      status: 0,
      createTime: '2025-01-07 13:40:00',
      updateTime: '2025-01-07 13:40:00'
    },
    {
      id: 8,
      username: 'yinji',
      password: '123456',
      role: 'worker',
      status: 0,
      createTime: '2025-01-08 14:50:00',
      updateTime: '2025-01-08 14:50:00'
    },
    {
      id: 9,
      username: 'zhouba',
      password: '123456',
      role: 'worker',
      status: 0,
      createTime: '2025-01-09 15:00:00',
      updateTime: '2025-01-09 15:00:00'
    },
    {
      id: 10,
      username: 'wujia',
      password: '123456',
      role: 'worker',
      status: 0,
      createTime: '2025-01-10 16:10:00',
      updateTime: '2025-01-10 16:10:00'
    },
    {
      id: 11,
      username: 'liujia',
      password: '123456',
      role: 'worker',
      status: 0,
      createTime: '2025-01-11 17:20:00',
      updateTime: '2025-01-11 17:20:00'
    },
    {
      id: 12,
      username: 'sunjia', 
      password: '123456', 
      role: 'worker', 
      status: 0, 
      createTime: '2025-01-12 18:30:00', 
      updateTime: '2025-01-12 18:30:00'
    },
    
  ])
  
  // 分页和搜索
  const loading = ref(false)
  const currentPage = ref(1)
  const pageSize = ref(10)
  const searchQuery = ref('')
  const totalUsers = computed(() => filteredUsers.value.length)
  
  // 过滤后的用户列表
  const filteredUsers = computed(() => {
    if (!searchQuery.value) {
      return users.value
    }
    
    const query = searchQuery.value.toLowerCase()
    return users.value.filter(user => 
      (user.username && user.username.toLowerCase().includes(query)) ||
      (user.name && user.name.toLowerCase().includes(query)) ||
      (user.phone && user.phone.includes(query))
    )
  })
  
  // 对话框相关
  const dialogVisible = ref(false)
  const dialogType = ref('add') // 'add' 或 'edit'
  const userFormRef = ref(null)
  const userForm = reactive({
    id: '',
    username: '',
    name: '',
    phone: '',
    email: '',
    role: '',
    status: 1,
    password: '',
    confirmPassword: ''
  })
  
  // 表单验证规则
  const userRules = {
    username: [
      { required: true, message: '请输入用户名', trigger: 'blur' },
      { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
    ],
    role: [
      { required: true, message: '请选择角色', trigger: 'change' }
    ],
    
    
  }
  
  // 获取角色文本
  const getRoleText = (role) => {
    const roleMap = {
      'admin': '管理员',
      'elder': '老人',
      'family': '老人家属',
      'worker': '社区工作人员'
    }
    return roleMap[role] || '未知角色'
  }
  
  // 获取角色标签类型
  const getRoleType = (role) => {
    const typeMap = {
      'admin': 'danger',
      'elder': 'success',
      'family': 'warning',
      'worker': 'info'
    }
    return typeMap[role] || ''
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
      if (key !== 'password' && key !== 'confirmPassword') {
        userForm[key] = row[key]
      }
    })
    dialogVisible.value = true
  }
  
  // 删除用户
  const handleDelete = (row) => {
    ElMessageBox.confirm(
      `确定要删除用户 ${row.name} 吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(() => {
      // 这里应该调用删除用户的API
      users.value = users.value.filter(user => user.id !== row.id)
      ElMessage.success('删除成功')
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
      // 这里应该调用重置密码的API
      ElMessage.success(`已重置 ${row.username} 的密码为默认密码`)
    }).catch(() => {
      // 取消重置
      ElMessage.info('取消重置')
    })
  }
  
  // 修改用户状态
  const handleStatusChange = (row) => {
    const statusText = row.status === 1 ? '启用' : '禁用'
    ElMessage.success(`已${statusText}用户 ${row.username}`)
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
  
  // 提交表单
  const submitForm = () => {
    userFormRef.value.validate((valid) => {
      if (valid) {
        if (dialogType.value === 'add') {
          // 添加用户
          const newUser = {
            id: users.value.length + 1,
            username: userForm.username,
            name: userForm.name,
            phone: userForm.phone,
            email: userForm.email,
            role: userForm.role,
            status: userForm.status,
            createTime: new Date().toLocaleString()
          }
          users.value.push(newUser)
          ElMessage.success('添加用户成功')
        } else {
          // 编辑用户
          const index = users.value.findIndex(user => user.id === userForm.id)
          if (index !== -1) {
            users.value[index] = { ...users.value[index], ...userForm }
            ElMessage.success('更新用户成功')
          }
        }
        dialogVisible.value = false
      } else {
        return false
      }
    })
  }
  
  onMounted(() => {
    console.log(users.value)
  })
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