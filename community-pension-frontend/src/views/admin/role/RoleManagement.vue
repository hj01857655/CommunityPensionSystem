<template>
    <div class="role-management">
        <el-card shadow="hover" class="table-card">
            <template #header>
                <div class="card-header">
                    <h3>角色管理</h3>
                    <div class="header-actions">
                        <el-input v-model="searchQuery" placeholder="搜索角色名称" class="search-input" clearable
                            @clear="handleSearch">
                            <template #prefix>
                                <el-icon>
                                    <Search />
                                </el-icon>
                            </template>
                        </el-input>
                        <el-button type="primary" @click="handleSearch">
                            <el-icon>
                                <Search />
                            </el-icon>
                            搜索
                        </el-button>
                        <el-button type="success" @click="handleAdd">
                            <el-icon>
                                <Plus />
                            </el-icon>
                            添加角色
                        </el-button>
                    </div>
                </div>
            </template>

            <!-- 角色管理内容 -->
            <el-table :data="paginatedRoles" style="width: 100%" v-loading="loading" border stripe highlight-current-row
                @row-click="handleRowClick" class="role-table">
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
                <el-table-column label="状态" width="100" align="center">
                    <template #default="scope">
                        <el-tag :type="scope.row.status ? 'success' : 'danger'">
                            {{ scope.row.status ? '启用' : '禁用' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="200" fixed="right" align="center">
                    <template #default="scope">
                        <el-button type="primary" size="small" @click.stop="handleEdit(scope.row)">
                            <el-icon>
                                <Edit />
                            </el-icon>
                            编辑
                        </el-button>
                        <el-button type="danger" size="small" @click.stop="handleDelete(scope.row)">
                            <el-icon>
                                <Delete />
                            </el-icon>
                            删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <div class="pagination-container">
                <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
                    :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper" :total="totalRoles"
                    @size-change="handleSizeChange" @current-change="handleCurrentChange" background />
            </div>
        </el-card>

        <!-- 角色表单对话框 -->
        <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '添加角色' : '编辑角色'" width="500px"
            destroy-on-close :close-on-click-modal="false">
            <el-form ref="roleFormRef" :model="roleForm" :rules="roleRules" label-width="100px" status-icon>
                <el-form-item label="角色名称" prop="role_name">
                    <el-input v-model="roleForm.role_name" placeholder="请输入角色名称" maxlength="50" show-word-limit />
                </el-form-item>
                <el-form-item label="角色描述" prop="role_description">
                    <el-input v-model="roleForm.role_description" type="textarea" :rows="3" placeholder="请输入角色描述"
                        maxlength="255" show-word-limit />
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
        <el-drawer v-model="drawerVisible" title="角色详情" direction="rtl" size="30%">
            <div v-if="selectedRole" class="role-detail">
                <el-descriptions :column="1" border>
                    <el-descriptions-item label="角色ID">{{ selectedRole.id }}</el-descriptions-item>
                    <el-descriptions-item label="角色名称">{{ selectedRole.role_name }}</el-descriptions-item>
                    <el-descriptions-item label="角色描述">{{ selectedRole.role_description }}</el-descriptions-item>
                    <el-descriptions-item label="状态">
                        <el-tag :type="selectedRole.status ? 'success' : 'danger'">
                            {{ selectedRole.status ? '启用' : '禁用' }}
                        </el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="权限">
                        <div class="permission-tags">
                            <el-tag v-for="permId in selectedRole.permissions" :key="permId" type="info"
                                class="permission-tag">
                                {{ getPermissionName(permId) }}
                            </el-tag>
                            <span v-if="!selectedRole.permissions || selectedRole.permissions.length === 0">
                                暂无权限
                            </span>
                        </div>
                    </el-descriptions-item>
                    <el-descriptions-item label="创建时间">{{ formatDateDetail(selectedRole.create_time)
                    }}</el-descriptions-item>
                    <el-descriptions-item label="更新时间">{{ formatDateDetail(selectedRole.update_time)
                    }}</el-descriptions-item>
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
import { getRoleList, createRole, updateRole, deleteRole, getRoleDetail } from '@/api/role';

// 角色列表数据
const roles = ref([]);
// 加载状态
const loading = ref(false);
// 当前页
const currentPage = ref(1);
// 每页条数
const pageSize = ref(10);
// 搜索框输入内容
const searchQuery = ref('');
// 提交按钮loading状态
const submitLoading = ref(false);

// 是否显示高级选项
const showPermissions = ref(true); // 默认显示权限选择
const showStatus = ref(true); // 默认显示状态开关

// 抽屉相关
const drawerVisible = ref(false);
const selectedRole = ref(null);

// 权限列表数据
const permissions = ref([
    { id: 1, name: '查看权限' },
    { id: 2, name: '编辑权限' },
    { id: 3, name: '删除权限' },
    { id: 4, name: '管理用户' },
    { id: 5, name: '系统设置' }
]);

// 过滤后的角色列表
const filteredRoles = computed(() => {
    return roles.value;
});

// 分页后的角色列表
const paginatedRoles = computed(() => {
    return filteredRoles.value;
});

// 总角色数
const totalRoles = computed(() => {
    return roles.value ? roles.value.length : 0;
});

// 对话框相关
const dialogVisible = ref(false);
const dialogType = ref('add'); // 'add' 或 'edit'
const roleFormRef = ref(null);
// 角色表单
const roleForm = reactive({
    id: '',
    role_name: '',
    role_description: '',
    create_time: '',
    update_time: ''
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
    fetchRoles();
};

// 添加角色
const handleAdd = () => {
    dialogType.value = 'add';
    resetForm();
    dialogVisible.value = true;
};

// 编辑角色
const handleEdit = async (row) => {
    dialogType.value = 'edit';
    resetForm();

    try {
        loading.value = true;
        // 获取角色详情
        const response = await getRoleDetail(row.id);
        if (response.status && response.status !== 200) {
            ElMessage.error(response.message || '获取角色详情失败');
            return;
        }
        const roleDetail = response.data;

        // 使用延时确保表单已重置
        setTimeout(() => {
            // 复制角色数据到表单
            roleForm.id = roleDetail.id;
            roleForm.role_name = roleDetail.role_name;
            roleForm.role_description = roleDetail.role_description;
            dialogVisible.value = true;

            // 如果抽屉是打开的，则关闭它
            if (drawerVisible.value) {
                drawerVisible.value = false;
            }
        }, 0);
    } catch (error) {
        console.error('获取角色详情失败:', error);
        ElMessage.error('获取角色详情失败，请稍后重试');
    } finally {
        loading.value = false;
    }
};

// 删除角色
const handleDelete = async (row) => {
    try {
        await ElMessageBox.confirm(
            `确定要删除角色 "${row.role_name}" 吗？`,
            '警告',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }
        );

        loading.value = true;
        const deleteResponse = await deleteRole(row.id);
        if (deleteResponse.status && deleteResponse.status !== 200) {
            ElMessage.error(deleteResponse.message || '删除角色失败');
            return;
        }
        ElMessage.success('删除成功');

        // 如果删除的是当前选中的角色，关闭抽屉
        if (selectedRole.value && selectedRole.value.id === row.id) {
            drawerVisible.value = false;
            selectedRole.value = null;
        }

        // 重新获取角色列表
        await fetchRoles();
    } catch (error) {
        if (error !== 'cancel') {
            console.error('删除角色失败:', error);
            ElMessage.error('删除角色失败，请稍后重试');
        }
    } finally {
        loading.value = false;
    }
};

// 处理行点击
const handleRowClick = async (row) => {
    try {
        loading.value = true;
        // 获取角色详情
        const response = await getRoleDetail(row.id);
        if (response.status && response.status !== 200) {
            ElMessage.error(response.message || '获取角色详情失败');
            return;
        }
        selectedRole.value = response.data;
        drawerVisible.value = true;
    } catch (error) {
        console.error('获取角色详情失败:', error);
        ElMessage.error('获取角色详情失败，请稍后重试');
    } finally {
        loading.value = false;
    }
};

// 分页大小变化
const handleSizeChange = (val) => {
    pageSize.value = val;
    currentPage.value = 1;
    fetchRoles();
};

// 当前页变化
const handleCurrentChange = (val) => {
    currentPage.value = val;
    fetchRoles();
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
const submitForm = async () => {
    if (!roleFormRef.value) return;

    try {
        await roleFormRef.value.validate();
        submitLoading.value = true;

        const roleData = {
            role_name: roleForm.role_name,
            role_description: roleForm.role_description,
            status: roleForm.status,
            permissions: roleForm.permissions
        };

        if (dialogType.value === 'add') {
            // 添加角色
            const createResponse = await createRole(roleData);
            if (createResponse.status && createResponse.status !== 200) {
                ElMessage.error(createResponse.message || '添加角色失败');
                return;
            }
            ElMessage.success('添加角色成功');
        } else {
            // 编辑角色
            const updateResponse = await updateRole(roleForm.id, roleData);
            if (updateResponse.status && updateResponse.status !== 200) {
                ElMessage.error(updateResponse.message || '更新角色失败');
                return;
            }
            ElMessage.success('更新角色成功');
        }

        dialogVisible.value = false;
        // 重新获取角色列表
        await fetchRoles();
    } catch (error) {
        console.error(dialogType.value === 'add' ? '添加角色失败:' : '更新角色失败:', error);
        ElMessage.error(dialogType.value === 'add' ? '添加角色失败，请稍后重试' : '更新角色失败，请稍后重试');
    } finally {
        submitLoading.value = false;
    }
};

// 获取角色列表
const fetchRoles = async () => {
    loading.value = true;
    try {
        const params = {
            page: currentPage.value,
            size: pageSize.value,
            roleName: searchQuery.value || null
        };

        const response = await getRoleList(params);
        console.log('获取角色列表响应:', response.data);
        if (!response || !response.data) {
            ElMessage.error('获取角色列表失败1');
            roles.value = [];
            return;
        }

        // 处理返回的数据
        if (Array.isArray(response.data)) {
            roles.value = response.data;
        } else if (response.data && Array.isArray(response.data.records)) {
            const RoleData = response.data.records
            roles.value = RoleData.map(item => ({ ...item, id: item.id }));
        } else {
            roles.value = [];
            totalCount.value = 0;
            console.warn('返回的角色数据格式不符合预期', response.data);
        }
    } catch (error) {
        console.error('获取角色列表失败:', error);
        ElMessage.error('获取角色列表失败，请稍后重试');
        roles.value = [];
        totalCount.value = 0;
    } finally {
        loading.value = false;
    }
};

// 获取权限名称
const getPermissionName = (permissionId) => {
    const permission = permissions.value.find(p => p.id === permissionId);
    return permission ? permission.name : '未知权限';
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

.permission-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
}

.permission-tag {
    margin-right: 5px;
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