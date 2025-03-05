<template>
    <div class="staff-management">
        <el-card shadow="hover" class="main-card">
            <template #header>
                <div class="card-header">
                    <h3>社区工作人员管理</h3>
                    <div class="header-actions">
                        <el-input v-model="searchQuery" placeholder="搜索姓名/职位/联系方式" class="search-input" clearable
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
            <el-table :data="filteredStaff" style="width: 100%" v-loading="loading" border class="staff-table">
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="name" label="姓名" width="120" />
                <el-table-column prop="phone" label="电话" width="120" />
                <el-table-column prop="address" label="地址" min-width="180" />
                <el-table-column prop="position" label="职位" width="120" />
                <el-table-column prop="createdAt" label="创建时间" width="180" />
                <el-table-column prop="updatedAt" label="更新时间" width="180" />
                <el-table-column label="操作" width="280" fixed="right">
                    <template #default="scope">
                        <div style="display: flex; gap: 8px;">
                            <el-button type="primary" size="small" @click="handleView(scope.row)">查看</el-button>
                            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
                            <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
            <div class="pagination-container">
                <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
                    :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper" :total="totalStaff"
                    @size-change="handleSizeChange" @current-change="handleCurrentChange" />
            </div>
        </el-card>
        <!-- 查看对话框 -->
        <el-dialog v-model="viewDialogVisible" title="查看工作人员" width="500px">
            <el-form :model="currentStaff" label-width="80px">
                <el-form-item label="姓名">
                    <el-input v-model="currentStaff.name" disabled></el-input>
                </el-form-item>
                <el-form-item label="电话">
                    <el-input v-model="currentStaff.phone" disabled></el-input>
                </el-form-item>
                <el-form-item label="地址">
                    <el-input v-model="currentStaff.address" disabled></el-input>
                </el-form-item>
                <el-form-item label="职位">
                    <el-input v-model="currentStaff.position" disabled></el-input>
                </el-form-item>
                <el-form-item label="创建时间">
                    <el-input v-model="currentStaff.createdAt" disabled></el-input>
                </el-form-item>
                <el-form-item label="更新时间">
                    <el-input v-model="currentStaff.updatedAt" disabled></el-input>
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="viewDialogVisible = false">关闭</el-button>
                </div>
            </template>
        </el-dialog>
        <!-- 编辑对话框 -->
        <el-dialog v-model="editDialogVisible" title="编辑工作人员" width="500px">
            <el-form :model="currentStaff" :rules="rules" ref="staffForm" label-width="80px">
                <el-form-item label="姓名" prop="name">
                    <el-input v-model="currentStaff.name"></el-input>
                </el-form-item>
                <el-form-item label="电话" prop="phone">
                    <el-input v-model="currentStaff.phone"></el-input>
                </el-form-item>
                <el-form-item label="地址" prop="address">
                    <el-input v-model="currentStaff.address"></el-input>
                </el-form-item>
                <el-form-item label="职位" prop="position">
                    <el-input v-model="currentStaff.position"></el-input>
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="editDialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="saveStaff">保存</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete, View } from '@element-plus/icons-vue'
import { getStaffList, addStaff, updateStaff, deleteStaff } from '@/api/staff'

// 工作人员列表数据
const staffList = ref([]);

// 定义 loading 状态
const loading = ref(false);

// 搜索查询
const searchQuery = ref('');

// 分页相关
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 获取工作人员列表
const fetchStaffList = async () => {
    loading.value = true;
    try {
        const params = {
            page: currentPage.value,
            size: pageSize.value,
            name: searchQuery.value || null
        };
        const response = await getStaffList(params);
        if (response.code === 200) {
            staffList.value = response.data.records;
            total.value = response.data.total;
        } else {
            ElMessage.error(response.message || '获取工作人员列表失败');
        }
    } catch (error) {
        console.error('获取工作人员列表出错:', error);
        ElMessage.error('获取工作人员列表失败');
    } finally {
        loading.value = false;
    }
};

// 过滤后的工作人员列表
const filteredStaff = computed(() => staffList.value);

const totalStaff = computed(() => total.value);

// 对话框显示状态
const viewDialogVisible = ref(false);
const editDialogVisible = ref(false);
const currentStaff = ref({});

// 表单验证规则
const rules = {
    name: [
        { required: true, message: '请输入姓名', trigger: 'blur' },
        { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
    ],
    phone: [
        { required: true, message: '请输入电话', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
    ],
    address: [
        { required: true, message: '请输入地址', trigger: 'blur' },
        { min: 5, max: 100, message: '长度在 5 到 100 个字符', trigger: 'blur' }
    ],
    position: [{ required: true, message: '请输入职位', trigger: 'blur' }]
};

// 表单引用
const staffForm = ref(null);

// 处理搜索
const handleSearch = () => {
    currentPage.value = 1;
    fetchStaffList();
};

// 处理添加
const handleAdd = () => {
    currentStaff.value = {
        name: '',
        phone: '',
        address: '',
        position: ''
    };
    editDialogVisible.value = true;
};

// 处理查看
const handleView = (row) => {
    currentStaff.value = { ...row };
    viewDialogVisible.value = true;
};

// 处理编辑
const handleEdit = (row) => {
    currentStaff.value = { ...row };
    editDialogVisible.value = true;
};

// 处理删除
const handleDelete = (row) => {
    ElMessageBox.confirm('确定要删除该工作人员吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            const response = await deleteStaff(row.id);
            if (response.code === 200) {
                ElMessage.success('删除成功');
                fetchStaffList();
            } else {
                ElMessage.error(response.message || '删除失败');
            }
        } catch (error) {
            console.error('删除工作人员出错:', error);
            ElMessage.error('删除失败');
        }
    }).catch(() => {
        ElMessage.info('已取消删除');
    });
};

// 保存工作人员信息
const saveStaff = async () => {
    if (!staffForm.value) return;
    
    await staffForm.value.validate(async (valid) => {
        if (valid) {
            try {
                const staffData = { ...currentStaff.value };
                const response = staffData.id
                    ? await updateStaff(staffData)
                    : await addStaff(staffData);

                if (response.code === 200) {
                    ElMessage.success(staffData.id ? '更新成功' : '添加成功');
                    editDialogVisible.value = false;
                    fetchStaffList();
                } else {
                    ElMessage.error(response.message || (staffData.id ? '更新失败' : '添加失败'));
                }
            } catch (error) {
                console.error('保存工作人员信息出错:', error);
                ElMessage.error(currentStaff.value.id ? '更新失败' : '添加失败');
            }
        }
    });
};

// 处理分页大小变化
const handleSizeChange = (val) => {
    pageSize.value = val;
    currentPage.value = 1;
    fetchStaffList();
};

// 处理当前页变化
const handleCurrentChange = (val) => {
    currentPage.value = val;
    fetchStaffList();
};

// 初始化加载数据
onMounted(() => {
    fetchStaffList();
});
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
