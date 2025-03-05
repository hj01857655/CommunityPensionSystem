<!--NoticeType.vue-->
<template>
    <div class="notice-type">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>通知类型管理</span>
                    <el-input v-model="searchQuery" placeholder="搜索类型名称" clearable @clear="handleSearch" />
                    <el-button type="primary" @click="handleAdd">新增通知类型</el-button>
                </div>
            </template>
            <el-table :data="tableData" style="width: 100%">
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="name" label="类型名称" />
                <el-table-column prop="description" label="描述" />
                <el-table-column prop="createTime" label="创建时间" />
            </el-table>
            </el-card> 
            <el-dialog v-model="dialogVisible" title="新增通知类型" width="30%">
                <el-form :model="form" label-width="120px">
                    <el-form-item label="类型名称">
                        <el-input v-model="form.name" placeholder="请输入类型名称" />
                    </el-form-item>
                    <el-form-item label="描述">
                        <el-input v-model="form.description" type="textarea" placeholder="请输入描述" />
                    </el-form-item>
                </el-form>
                <template #footer>
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="handleSave">保存</el-button>
                </template>
            </el-dialog>
        </div>
</template>
<script setup>    
import { ref, reactive } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const searchQuery = ref('');
const dialogVisible = ref(false);
const form = reactive({
    name: '',
    description: ''
}); 

const tableData = ref([]);

const handleSearch = () => {
    console.log('搜索', searchQuery.value);
};

const handleAdd = () => {
    dialogVisible.value = true;
};

const handleSave = () => {
    console.log('保存', form);
};  

const handleEdit = (row) => {
    console.log('编辑', row);
};

const handleDelete = (row) => {
    console.log('删除', row);
};

onMounted(() => {
    // 模拟数据
    tableData.value = [
        {   
            id: 1,
            name: '系统公告',
            description: '系统公告描述',
            createTime: '2024-01-01'
        },
        {
            id: 2,
            name: '社区活动',
            description: '社区活动描述',
            createTime: '2024-01-02'
        },
        {
            id: 3,
            name: '健康讲座',
            description: '健康讲座描述',
            createTime: '2024-01-03'
        }

    ]
    console.log('tableData', tableData.value);
})

</script>

<style scoped>
.notice-type {
    padding: 20px;
}
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.search-input {
    width: 200px;
    margin-right: 10px;
}
.el-table {
    margin-top: 20px;
}
.el-dialog__body {
    padding: 20px;
}
.el-dialog__footer {
    text-align: right;
}
.el-button {
    margin-left: 10px;
}






</style>


