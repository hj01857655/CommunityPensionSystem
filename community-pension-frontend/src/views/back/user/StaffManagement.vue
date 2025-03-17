<template>
    <div class="staff-management">
        <el-card shadow="hover" class="main-card">
            <template #header>
                <div class="card-header">
                    <h3>社区工作人员管理</h3>
                    <div class="header-actions">
                        <el-input v-model="staffStore.searchQuery" placeholder="搜索姓名/职位/联系方式" class="search-input" clearable
                            @clear="handleSearch" @keyup.enter="handleSearch">
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
                            添加工作人员
                        </el-button>
                    </div>
                </div>
            </template>
            
            <!-- 工具栏 -->
            <div class="toolbar">
                <div class="batch-actions">
                    <el-button-group>
                        <el-button type="danger" :disabled="!selectedStaffs.length" @click="handleBatchDelete">
                            <el-icon>
                                <Delete />
                            </el-icon> 批量删除
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
                    <el-button @click="refreshStaffList">
                        <el-icon>
                            <Refresh />
                        </el-icon> 刷新
                    </el-button>
                </div>
            </div>
            
            <el-table 
                :data="staffList" 
                style="width: 100%" 
                v-loading="loading" 
                border 
                class="staff-table"
                @selection-change="handleSelectionChange"
            >
                <el-table-column type="selection" width="55" />
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="name" label="姓名" width="120" />
                <el-table-column prop="position" label="职位" width="120" />
                <el-table-column prop="department" label="部门" width="150" />
                <el-table-column prop="phone" label="手机号" width="120" />
                <el-table-column prop="email" label="邮箱" width="180" show-overflow-tooltip />
                <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
                <el-table-column prop="create_time" label="创建时间" width="180" />
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
        
        <!-- 工作人员表单对话框 -->
        <el-dialog
            v-model="dialogVisible"
            :title="dialogType === 'add' ? '新增工作人员' : dialogType === 'edit' ? '编辑工作人员' : '查看工作人员'"
            width="600px"
        >
            <el-form
                ref="staffFormRef"
                :model="staffForm"
                :rules="staffRules"
                label-width="100px"
                :disabled="dialogType === 'view'"
            >
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="姓名" prop="name">
                            <el-input v-model="staffForm.name" placeholder="请输入姓名" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="部门" prop="department">
                            <el-input v-model="staffForm.department" placeholder="请输入部门" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="职位" prop="position">
                            <el-input v-model="staffForm.position" placeholder="请输入职位" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="手机号" prop="phone">
                            <el-input v-model="staffForm.phone" placeholder="请输入手机号" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="邮箱" prop="email">
                            <el-input v-model="staffForm.email" placeholder="请输入邮箱" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="地址" prop="address">
                            <el-input v-model="staffForm.address" placeholder="请输入地址" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="备注" prop="remark">
                            <el-input v-model="staffForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
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
import { Search, Delete, Document, Printer, Refresh, Plus, Edit, View } from '@element-plus/icons-vue';
import { useStaffStore } from '@/stores/back/staffStore';
import { useRoute } from 'vue-router';

const staffStore = useStaffStore();
const route = useRoute();

// 使用计算属性确保响应式
const staffList = computed(() => staffStore.staffList);
const loading = computed(() => staffStore.loading);
const total = computed(() => staffStore.total);
const currentPage = computed(() => staffStore.currentPage);
const pageSize = computed(() => staffStore.pageSize);

// 选中的工作人员
const selectedStaffs = ref([]);

// 对话框状态
const dialogVisible = ref(false);
const dialogType = ref('add'); // 'add', 'edit', 'view'

// 工作人员表单
const staffFormRef = ref(null);
const staffForm = reactive({
    id: null,
    name: '',
    gender: '',
    age: 30,
    phone: '',
    email: '',
    position: '',
    department: '',
    hireDate: null,
    address: '',
    remark: ''
});

// 表单验证规则
const staffRules = {
    name: [
        { required: true, message: '请输入姓名', trigger: 'blur' },
        { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
    ],
    gender: [
        { required: true, message: '请选择性别', trigger: 'change' }
    ],
    age: [
        { required: true, message: '请输入年龄', trigger: 'blur' }
    ],
    phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
    ],
    email: [
        { required: false, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
    ],
    position: [
        { required: true, message: '请输入职位', trigger: 'blur' }
    ],
    department: [
        { required: true, message: '请输入部门', trigger: 'blur' }
    ]
};

onMounted(() => {
    staffStore.fetchStaffs({
        current: staffStore.currentPage,
        size: staffStore.pageSize,
        name: staffStore.searchQuery,
    });
});

// 添加路由监听，在路由切换时重新加载数据
watch(() => route.name, (newRouteName) => {
    if (newRouteName === 'StaffManagement') {
        staffStore.fetchStaffs({
            current: staffStore.currentPage,
            size: staffStore.pageSize,
            name: staffStore.searchQuery,
        });
    }
});

// 处理表格选择变化
const handleSelectionChange = (selection) => {
    selectedStaffs.value = selection;
};

// 搜索
const handleSearch = () => {
    staffStore.currentPage = 1;
    staffStore.fetchStaffs({
        current: staffStore.currentPage,
        size: staffStore.pageSize,
        name: staffStore.searchQuery,
    });
};

// 刷新列表
const refreshStaffList = () => {
    staffStore.fetchStaffs({
        current: staffStore.currentPage,
        size: staffStore.pageSize,
        name: staffStore.searchQuery,
    });
};

// 分页大小变化
const handleSizeChange = (val) => {
    staffStore.pageSize = val;
    staffStore.fetchStaffs({
        current: staffStore.currentPage,
        size: staffStore.pageSize,
        name: staffStore.searchQuery,
    });
};

// 当前页变化
const handleCurrentChange = (val) => {
    staffStore.currentPage = val;
    staffStore.fetchStaffs({
        current: staffStore.currentPage,
        size: staffStore.pageSize,
        name: staffStore.searchQuery,
    });
};

// 重置表单
const resetForm = () => {
    if (staffFormRef.value) {
        staffFormRef.value.resetFields();
    }
    Object.assign(staffForm, {
        id: null,
        name: '',
        gender: '',
        age: 30,
        phone: '',
        email: '',
        position: '',
        department: '',
        hireDate: null,
        address: '',
        remark: ''
    });
};

// 添加工作人员
const handleAdd = () => {
    dialogType.value = 'add';
    resetForm();
    dialogVisible.value = true;
};

// 编辑工作人员
const handleEdit = (row) => {
    dialogType.value = 'edit';
    resetForm();
    Object.assign(staffForm, row);
    dialogVisible.value = true;
};

// 查看工作人员
const handleView = (row) => {
    dialogType.value = 'view';
    resetForm();
    Object.assign(staffForm, row);
    dialogVisible.value = true;
};

// 删除工作人员
const handleDelete = (row) => {
    ElMessageBox.confirm(
        '确定要删除该工作人员吗？',
        '警告',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(() => {
        staffStore.deleteStaff(row.id);
    }).catch(() => {
        // 取消删除
    });
};

// 批量删除
const handleBatchDelete = () => {
    if (selectedStaffs.value.length === 0) {
        ElMessage.warning('请至少选择一个工作人员');
        return;
    }

    ElMessageBox.confirm(
        `确定要删除选中的 ${selectedStaffs.value.length} 个工作人员吗？`,
        '警告',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(() => {
        const deletePromises = selectedStaffs.value.map(staff => staffStore.deleteStaff(staff.id));

        Promise.all(deletePromises)
            .then(results => {
                const allSuccess = results.every(res => res && res.code === 200);
                if (allSuccess) {
                    ElMessage.success('批量删除成功');
                } else {
                    ElMessage.warning('部分工作人员删除失败');
                }
            })
            .catch(error => {
                ElMessage.error('批量删除失败：' + error.message);
            });
    }).catch(() => {
        // 取消删除
    });
};

// 导出Excel
const handleExportExcel = () => {
    ElMessage.success('导出Excel功能已触发，实际导出功能需要后端支持');
};

// 打印功能
const handlePrint = () => {
    ElMessage.success('正在准备打印...');
    // 保存原始标题
    const originalTitle = document.title;
    // 设置打印时的标题
    document.title = '社区工作人员管理列表';

    // 创建打印样式
    const style = document.createElement('style');
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
    `;
    document.head.appendChild(style);

    // 执行打印
    window.print();

    // 打印完成后清理
    setTimeout(() => {
        document.head.removeChild(style);
        document.title = originalTitle;
    }, 100);
};

// 提交表单
const submitForm = () => {
    if (!staffFormRef.value) return;
    
    staffFormRef.value.validate((valid) => {
        if (valid) {
            const staffData = { ...staffForm };
            
            if (dialogType.value === 'add') {
                staffStore.addStaff(staffData)
                    .then(() => {
                        dialogVisible.value = false;
                    });
            } else {
                staffStore.updateStaff(staffData)
                    .then(() => {
                        dialogVisible.value = false;
                    });
            }
        }
    });
};
</script>

<style scoped>
.staff-management {
    padding: 20px;
}

.main-card {
    margin-bottom: 20px;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.header-actions {
    display: flex;
    gap: 10px;
}

.search-input {
    width: 300px;
}

.toolbar {
    margin: 20px 0;
    display: flex;
    justify-content: space-between;
}

.batch-actions, .export-actions {
    display: flex;
    gap: 10px;
}

.pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
}

.dialog-footer {
    text-align: right;
    margin-top: 20px;
}

.staff-table {
    margin-top: 20px;
}
</style>
