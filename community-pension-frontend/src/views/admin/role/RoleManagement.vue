<template>
    <div class="role-management">
        <el-card shadow="hover" class="table-card">
            <template #header>
                <div class="card-header">
                    <h3>角色管理</h3>
                    <div class="header-actions">
                        <el-input v-model="searchQuery" placeholder="搜索角色名称" class="search-input" clearable @clear="handleSearch">
                            <template #prefix>
                                <el-icon>
                                    <Search />
                                </el-icon>
                            </template>
                        </el-input>
                        <el-button type="primary" @click="handleSearch">
                            <el-icon><Search /></el-icon>
                            搜索
                        </el-button>
                        <el-button type="success" @click="handleAdd">
                            <el-icon><Plus /></el-icon>
                            添加角色
                        </el-button>
                    </div>
                </div>
            </template>

            <!-- 角色管理内容 -->
            <el-table 
                :data="paginatedRoles" 
                style="width: 100%" 
                v-loading="loading" 
                border 
                stripe
                highlight-current-row
                @row-click="handleRowClick"
                class="role-table">
                <el-table-column prop="id" label="ID" width="80" align="center" />
                <el-table-column prop="role_name" label="角色名称" width="150" />
                <el-table-column prop="role_description" label="角色描述" show-overflow-tooltip />
                <el-table-column prop="create_time" label="创建时间" width="180">
                    <template #default="scope">
                        <el-tooltip :content="formatDateDetail(scope.row.create_time)" placement="top">
                            <span>{{ formatDate(scope.row.create_time) }}</span>
                        </el-tooltip>
                    </template>
                </el-table-column>
                <el-table-column prop="update_time" label="更新时间" width="180">
                    <template #default="scope">
                        <el-tooltip :content="formatDateDetail(scope.row.update_time)" placement="top">
                            <span>{{ formatDate(scope.row.update_time) }}</span>
                        </el-tooltip>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="180" fixed="right" align="center">
                    <template #default="scope">
                        <el-button type="primary" size="small" @click.stop="handleEdit(scope.row)" :icon="Edit">编辑</el-button>
                        <el-button type="danger" size="small" @click.stop="handleDelete(scope.row)" :icon="Delete">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <div class="pagination-container">
                <el-pagination 
                    v-model:current-page="currentPage" 
                    v-model:page-size="pageSize" 
                    :page-sizes="[10, 20, 50, 100]"
                    layout="total, sizes, prev, pager, next, jumper" 
                    :total="totalRoles" 
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                    background />
            </div>
        </el-card>

        <!-- 角色表单对话框 -->
        <el-dialog 
            v-model="dialogVisible" 
            :title="dialogType === 'add' ? '添加角色' : '编辑角色'" 
            width="500px"
            destroy-on-close
            :close-on-click-modal="false">
            <el-form 
                ref="roleFormRef" 
                :model="roleForm" 
                :rules="roleRules" 
                label-width="100px"
                status-icon>
                <el-form-item label="角色名称" prop="role_name">
                    <el-input 
                        v-model="roleForm.role_name" 
                        placeholder="请输入角色名称"
                        maxlength="50"
                        show-word-limit />
                </el-form-item>
                <el-form-item label="角色描述" prop="role_description">
                    <el-input 
                        v-model="roleForm.role_description" 
                        type="textarea" 
                        :rows="3"
                        placeholder="请输入角色描述"
                        maxlength="255"
                        show-word-limit />
                </el-form-item>
                <el-form-item v-if="showPermissions" label="权限列表" prop="permissions">
                    <el-select v-model="roleForm.permissions" multiple placeholder="请选择权限">
                        <el-option v-for="item in permissions" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                </el-form-item>
                <el-form-item v-if="showStatus" label="状态" prop="status">
                    <el-switch v-model="roleForm.status" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="submitForm" :loading="submitLoading">确定</el-button>
                </span>
            </template>
        </el-dialog>

        <!-- 角色详情抽屉 -->
        <el-drawer
            v-model="drawerVisible"
            title="角色详情"
            direction="rtl"
            size="30%">
            <div v-if="selectedRole" class="role-detail">
                <el-descriptions :column="1" border>
                    <el-descriptions-item label="角色ID">{{ selectedRole.id }}</el-descriptions-item>
                    <el-descriptions-item label="角色名称">{{ selectedRole.role_name }}</el-descriptions-item>
                    <el-descriptions-item label="角色描述">{{ selectedRole.role_description }}</el-descriptions-item>
                    <el-descriptions-item label="创建时间">{{ formatDateDetail(selectedRole.create_time) }}</el-descriptions-item>
                    <el-descriptions-item label="更新时间">{{ formatDateDetail(selectedRole.update_time) }}</el-descriptions-item>
                </el-descriptions>
                
                <div class="drawer-footer">
                    <el-button type="primary" @click="handleEdit(selectedRole)">编辑角色</el-button>
                </div>
            </div>
        </el-drawer>
    </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Edit, Delete, Plus } from '@element-plus/icons-vue';
import { formatDate as formatDateUtil } from '@/utils/date';

// 角色列表数据
const roles = ref([
    { 
        id: 1, 
        role_name: '管理员', 
        role_description: '管理员拥有最高权限，可以管理所有用户信息、角色信息、服务预约信息、健康监测信息、社区活动信息、通知公告信息、数据分析看板信息、系统设置信息',
        create_time: '2024-01-01 08:00:00',
        update_time: '2024-01-01 08:00:00',
        status: true,
        permissions: [1, 2, 3, 4, 5]
    },
    { 
        id: 2, 
        role_name: '社区工作人员', 
        role_description: '社区工作人员拥有社区工作人员权限，包括老人管理、服务预约管理、健康监测管理、社区活动管理、通知公告管理、数据分析看板管理、系统设置管理',
        create_time: '2024-01-01 08:00:00',
        update_time: '2024-01-01 08:00:00',
        status: true,
        permissions: [1, 2, 3]
    },
    { 
        id: 3, 
        role_name: '老人家属', 
        role_description: '老人家属拥有老人家属权限，包括老人管理、服务预约管理、健康监测管理、社区活动管理、通知公告管理、数据分析看板管理、系统设置管理',
        create_time: '2024-01-01 08:00:00',
        update_time: '2024-01-01 08:00:00',
        status: true,
        permissions: [1, 2]
    }
]);

// 权限列表数据
const permissions = ref([
    { id: 1, name: '查看权限' },
    { id: 2, name: '编辑权限' },
    { id: 3, name: '删除权限' },
    { id: 4, name: '管理用户' },
    { id: 5, name: '系统设置' }
]);

// 分页和搜索
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const searchQuery = ref('');
const submitLoading = ref(false);

// 是否显示高级选项
const showPermissions = ref(false);
const showStatus = ref(false);

// 抽屉相关
const drawerVisible = ref(false);
const selectedRole = ref(null);

// 过滤后的角色列表
const filteredRoles = computed(() => {
    if (!searchQuery.value) {
        return roles.value;
    }

    const query = searchQuery.value.toLowerCase();
    return roles.value.filter(role =>
        role.role_name.toLowerCase().includes(query)
    );
});

// 分页后的角色列表
const paginatedRoles = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value;
    const end = start + pageSize.value;
    return filteredRoles.value.slice(start, end);
});

// 总角色数
const totalRoles = computed(() => filteredRoles.value.length);

// 对话框相关
const dialogVisible = ref(false);
const dialogType = ref('add'); // 'add' 或 'edit'
const roleFormRef = ref(null);
const roleForm = reactive({
    id: '',
    role_name: '',
    role_description: '',
    status: true,
    permissions: []
});

// 表单验证规则
const roleRules = {
    role_name: [
        { required: true, message: '请输入角色名称', trigger: 'blur' },
        { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
    ],
    role_description: [
        { required: true, message: '请输入角色描述', trigger: 'blur' },
        { max: 255, message: '长度不能超过 255 个字符', trigger: 'blur' }
    ]
};

// 格式化日期 - 简短版本
const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
};

// 格式化日期 - 详细版本
const formatDateDetail = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`;
};

// 搜索角色
const handleSearch = () => {
    currentPage.value = 1;
};

// 添加角色
const handleAdd = () => {
    dialogType.value = 'add';
    resetForm();
    dialogVisible.value = true;
};

// 编辑角色
const handleEdit = (row) => {
    dialogType.value = 'edit';
    resetForm();
    
    // 使用延时确保表单已重置
    setTimeout(() => {
        // 复制角色数据到表单
        roleForm.id = row.id;
        roleForm.role_name = row.role_name;
        roleForm.role_description = row.role_description;
        roleForm.status = row.status || true;
        roleForm.permissions = row.permissions || [];
        
        dialogVisible.value = true;
        
        // 如果抽屉是打开的，则关闭它
        if (drawerVisible.value) {
            drawerVisible.value = false;
        }
        
        console.log('编辑角色:', row);
    }, 0);
};

// 删除角色
const handleDelete = (row) => {
    ElMessageBox.confirm(
        `确定要删除角色 "${row.role_name}" 吗？`,
        '警告',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(() => {
        loading.value = true;
        
        // 模拟API调用
        setTimeout(() => {
            // 这里应该调用删除API
            roles.value = roles.value.filter(role => role.id !== row.id);
            ElMessage.success('删除成功');
            
            // 如果删除的是当前选中的角色，关闭抽屉
            if (selectedRole.value && selectedRole.value.id === row.id) {
                drawerVisible.value = false;
                selectedRole.value = null;
            }
            
            loading.value = false;
        }, 500);
    }).catch(() => {
        // 取消删除
    });
};

// 处理行点击
const handleRowClick = (row) => {
    selectedRole.value = row;
    drawerVisible.value = true;
};

// 分页大小变化
const handleSizeChange = (val) => {
    pageSize.value = val;
    currentPage.value = 1;
};

// 当前页变化
const handleCurrentChange = (val) => {
    currentPage.value = val;
};

// 重置表单
const resetForm = () => {
    if (roleFormRef.value) {
        roleFormRef.value.resetFields();
    }
    
    // 重置表单数据
    roleForm.id = '';
    roleForm.role_name = '';
    roleForm.role_description = '';
    roleForm.status = true;
    roleForm.permissions = [];
};

// 提交表单
const submitForm = () => {
    if (!roleFormRef.value) return;
    
    roleFormRef.value.validate((valid) => {
        if (valid) {
            submitLoading.value = true;
            
            // 模拟API调用
            setTimeout(() => {
                if (dialogType.value === 'add') {
                    // 添加角色
                    const now = new Date().toISOString().replace('T', ' ').substring(0, 19);
                    const newRole = {
                        id: Math.max(...roles.value.map(r => r.id), 0) + 1, // 确保ID唯一
                        role_name: roleForm.role_name,
                        role_description: roleForm.role_description,
                        status: roleForm.status,
                        permissions: [...roleForm.permissions],
                        create_time: now,
                        update_time: now
                    };
                    roles.value.push(newRole);
                    ElMessage({
                        message: '添加角色成功',
                        type: 'success',
                        duration: 2000
                    });
                    
                    console.log('添加角色成功:', newRole);
                } else {
                    // 编辑角色
                    const index = roles.value.findIndex(role => role.id === roleForm.id);
                    if (index !== -1) {
                        const now = new Date().toISOString().replace('T', ' ').substring(0, 19);
                        
                        // 创建更新后的角色对象
                        const updatedRole = { 
                            ...roles.value[index],
                            role_name: roleForm.role_name,
                            role_description: roleForm.role_description,
                            status: roleForm.status,
                            permissions: [...roleForm.permissions],
                            update_time: now
                        };
                        
                        // 更新角色列表中的数据
                        roles.value[index] = updatedRole;
                        
                        // 如果编辑的是当前选中的角色，更新选中角色
                        if (selectedRole.value && selectedRole.value.id === updatedRole.id) {
                            selectedRole.value = { ...updatedRole };
                        }
                        
                        ElMessage({
                            message: '更新角色成功',
                            type: 'success',
                            duration: 2000
                        });
                        
                        console.log('更新角色成功:', updatedRole);
                    }
                }
                submitLoading.value = false;
                dialogVisible.value = false;
            }, 600);
        } else {
            return false;
        }
    });
};

// 获取角色列表
const fetchRoles = () => {
    loading.value = true;
    
    // 模拟API调用
    setTimeout(() => {
        // 实际项目中，这里应该调用API获取角色列表
        loading.value = false;
    }, 500);
};

onMounted(() => {
    // 初始化数据
    fetchRoles();
});
</script>

<style scoped>
.role-management {
    padding: 16px;
    background-color: #f5f7fa;
    min-height: calc(100vh - 120px);
}

.table-card {
    margin-bottom: 20px;
    transition: all 0.3s;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.table-card:hover {
    box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 0;
}

.card-header h3 {
    margin: 0;
    font-size: 20px;
    font-weight: 600;
    color: #303133;
}

.header-actions {
    display: flex;
    align-items: center;
    gap: 12px;
}

.search-input {
    width: 280px;
    transition: all 0.3s;
}

.search-input:focus-within {
    width: 320px;
}

.pagination-container {
    margin-top: 24px;
    display: flex;
    justify-content: flex-end;
    padding: 8px 0;
}

.role-table {
    margin: 16px 0;
    border-radius: 4px;
    overflow: hidden;
}

.role-table :deep(th) {
    background-color: #f5f7fa !important;
    color: #606266;
    font-weight: 600;
}

.role-table :deep(.el-table__row) {
    cursor: pointer;
    transition: all 0.2s;
}

.role-table :deep(.el-table__row:hover) {
    background-color: #ecf5ff !important;
}

.role-detail {
    padding: 16px;
}

.drawer-footer {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    padding: 16px;
    background-color: #fff;
    text-align: right;
    border-top: 1px solid #e4e7ed;
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
    .header-actions {
        flex-direction: column;
        align-items: flex-start;
    }
    
    .search-input {
        width: 100%;
    }
    
    .header-actions .el-button {
        margin-top: 8px;
    }
}
</style> 