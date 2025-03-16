<template>
    <!-- 角色管理 -->
    <div class="role-management">
        <el-card class="table-card">
            <!-- 搜索和操作栏 -->
            <div class="operation-bar">
                <div class="search-bar">
                    <el-input
                        v-model="searchQuery"
                        placeholder="请输入角色名称"
                        class="search-input"
                        clearable
                        @clear="handleSearch"
                        @keyup.enter="handleSearch"
                    >
                        <template #prefix>
                            <el-icon><Search /></el-icon>
                        </template>
                    </el-input>
                    <el-button type="primary" @click="handleSearch">
                        <el-icon><Search /></el-icon>
                        查询
                    </el-button>
                    <el-button @click="resetSearch">
                        <el-icon><Refresh /></el-icon>
                        重置
                    </el-button>
                </div>
                <div class="action-bar">
                    <el-button type="primary" @click="handleAdd">
                        <el-icon><Plus /></el-icon>
                        新增
                    </el-button>
                </div>
            </div>

            <!-- 角色表格 -->
            <el-table
                :data="tableData"
                style="width: 100%"
                v-loading="loading"
                border
                stripe
            >
                <el-table-column type="index" label="序号" width="60" align="center" />
                <el-table-column prop="name" label="角色名称" min-width="120" />
                <el-table-column prop="status" label="状态" width="100" align="center">
                    <template #default="{ row }">
                        <el-tag :type="row.status ? 'success' : 'danger'">
                            {{ row.status ? '启用' : '禁用' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="180" />
                <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
                <el-table-column label="操作" width="280" fixed="right" align="center">
                    <template #default="{ row }">
                        <el-button type="primary" size="small" @click="handleEdit(row)">
                            <el-icon><Edit /></el-icon>
                            编辑
                        </el-button>
                        <el-button type="success" size="small" @click="handleAssignPermission(row)">
                            <el-icon><Setting /></el-icon>
                            分配权限
                        </el-button>
                        <el-button type="danger" size="small" @click="handleDelete(row)">
                            <el-icon><Delete /></el-icon>
                            删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 分页器 -->
            <div class="pagination-container">
                <el-pagination
                    v-model:current-page="currentPage"
                    v-model:page-size="pageSize"
                    :page-sizes="[10, 20, 50, 100]"
                    :total="total"
                    layout="total, sizes, prev, pager, next, jumper"
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                />
            </div>
        </el-card>

        <!-- 角色表单对话框 -->
        <el-dialog
            v-model="dialogVisible"
            :title="dialogType === 'add' ? '新增角色' : '编辑角色'"
            width="500px"
            destroy-on-close
        >
            <el-form
                ref="formRef"
                :model="formData"
                :rules="rules"
                label-width="100px"
            >
                <el-form-item label="角色名称" prop="name">
                    <el-input v-model="formData.name" placeholder="请输入角色名称" />
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-switch
                        v-model="formData.status"
                        :active-value="true"
                        :inactive-value="false"
                        inline-prompt
                        active-text="启用"
                        inactive-text="禁用"
                    />
                </el-form-item>
                <el-form-item label="备注" prop="remark">
                    <el-input
                        v-model="formData.remark"
                        type="textarea"
                        placeholder="请输入备注信息"
                        :rows="3"
                    />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="handleSubmit">确定</el-button>
                </span>
            </template>
        </el-dialog>

        <!-- 权限分配对话框 -->
        <el-dialog
            v-model="permissionDialogVisible"
            title="分配权限"
            width="600px"
            destroy-on-close
        >
            <div v-if="currentRole" class="permission-dialog-content">
                <div class="current-role-info">
                    <h4>当前角色：{{ currentRole.name }}</h4>
                    <p>{{ currentRole.remark }}</p>
                </div>
                <el-tree
                    ref="permissionTreeRef"
                    :data="permissionTree"
                    :props="{ label: 'name', children: 'children' }"
                    show-checkbox
                    node-key="id"
                    default-expand-all
                    :default-checked-keys="selectedPermissions"
                    check-strictly
                />
            </div>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="permissionDialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="handleSavePermissions">确定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Plus, Edit, Delete, Setting, Refresh } from '@element-plus/icons-vue';

// 表格数据
const loading = ref(false);
const tableData = ref([
    {
        id: 1,
        name: '超级管理员',
        status: false,
        createTime: '2020-10-22 22:16:29',
        remark: '路型数列对到这新题律办'
    },
    {
        id: 2,
        name: '管理员',
        status: true,
        createTime: '1988-10-19 06:09:48',
        remark: '总原他制消马已改龙始罗里送'
    },
    {
        id: 3,
        name: '普通用户',
        status: false,
        createTime: '2020-01-16 01:45:11',
        remark: '全划片出天别进型车大差认况里小'
    },
    {
        id: 4,
        name: '游客',
        status: false,
        createTime: '2005-05-19 21:40:22',
        remark: '节白经常使局习根战规自每得'
    }
]);

// 分页相关
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(4);

// 搜索相关
const searchQuery = ref('');

// 表单相关
const dialogVisible = ref(false);
const dialogType = ref('add');
const formRef = ref(null);
const formData = ref({
    name: '',
    status: true,
    remark: ''
});

// 表单校验规则
const rules = {
    name: [
        { required: true, message: '请输入角色名称', trigger: 'blur' },
        { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
    ],
    remark: [
        { max: 200, message: '长度不能超过 200 个字符', trigger: 'blur' }
    ]
};

// 权限分配相关
const permissionDialogVisible = ref(false);
const currentRole = ref(null);
const permissionTree = ref([
    {
        id: 1,
        name: '首页',
        children: []
    },
    {
        id: 2,
        name: '分析页',
        children: []
    },
    {
        id: 3,
        name: '工作台',
        children: []
    }
]);
const selectedPermissions = ref([]);
const permissionTreeRef = ref(null);

// 搜索
const handleSearch = () => {
    // TODO: 实现搜索逻辑
    console.log('搜索:', searchQuery.value);
};

// 重置搜索
const resetSearch = () => {
    searchQuery.value = '';
    handleSearch();
};

// 新增角色
const handleAdd = () => {
    dialogType.value = 'add';
    formData.value = {
        name: '',
        status: true,
        remark: ''
    };
    dialogVisible.value = true;
};

// 编辑角色
const handleEdit = (row) => {
    dialogType.value = 'edit';
    formData.value = { ...row };
    dialogVisible.value = true;
};

// 删除角色
const handleDelete = async (row) => {
    try {
        await ElMessageBox.confirm(
            `确定要删除角色"${row.name}"吗？`,
            '警告',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }
        );
        // TODO: 调用删除API
        ElMessage.success('删除成功');
    } catch (error) {
        if (error !== 'cancel') {
            console.error('删除失败:', error);
            ElMessage.error('删除失败');
        }
    }
};

// 分配权限
const handleAssignPermission = (row) => {
    currentRole.value = row;
    selectedPermissions.value = [1, 2]; // 模拟已有权限
    permissionDialogVisible.value = true;
};

// 保存权限分配
const handleSavePermissions = async () => {
    if (!permissionTreeRef.value || !currentRole.value) return;
    
    try {
        const checkedKeys = permissionTreeRef.value.getCheckedKeys();
        const halfCheckedKeys = permissionTreeRef.value.getHalfCheckedKeys();
        const allSelectedKeys = [...checkedKeys, ...halfCheckedKeys];
        
        // TODO: 调用保存权限API
        console.log('保存权限:', allSelectedKeys);
        
        ElMessage.success('权限分配成功');
        permissionDialogVisible.value = false;
    } catch (error) {
        console.error('权限分配失败:', error);
        ElMessage.error('权限分配失败');
    }
};

// 提交表单
const handleSubmit = async () => {
    if (!formRef.value) return;
    
    try {
        await formRef.value.validate();
        // TODO: 调用保存API
        ElMessage.success(dialogType.value === 'add' ? '新增成功' : '编辑成功');
        dialogVisible.value = false;
        // 重新加载数据
    } catch (error) {
        console.error('表单提交失败:', error);
        ElMessage.error('操作失败');
    }
};

// 分页相关
const handleSizeChange = (val) => {
    pageSize.value = val;
    // TODO: 重新加载数据
};

const handleCurrentChange = (val) => {
    currentPage.value = val;
    // TODO: 重新加载数据
};

// 初始化
onMounted(() => {
    // TODO: 加载初始数据
});
</script>

<style scoped>
.role-management {
    padding: 20px;
}

.operation-bar {
    display: flex;
    justify-content: space-between;
    margin-bottom: 20px;
}

.search-bar {
    display: flex;
    gap: 10px;
}

.search-input {
    width: 200px;
}

.action-bar {
    display: flex;
    gap: 10px;
}

.pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
}

.dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
}

.permission-dialog-content {
    max-height: 500px;
    overflow-y: auto;
}

.current-role-info {
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 1px solid var(--el-border-color-lighter);
}

.current-role-info h4 {
    margin: 0 0 10px 0;
    font-size: 16px;
    font-weight: bold;
}

.current-role-info p {
    margin: 0;
    color: var(--el-text-color-secondary);
}
</style>