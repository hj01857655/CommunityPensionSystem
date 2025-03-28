<template>
    <div class="notice-management">
        <el-card shadow="hover" class="main-card">
            <template #header>
                <div class="card-header">
                    <h3>通知公告管理</h3>
                    <div class="header-actions">
                        <el-input
                            v-model="searchQuery"
                            placeholder="搜索通知标题/内容"
                            class="search-input"
                            clearable
                            @clear="handleSearch"
                        >
                            <template #prefix>
                                <el-icon><Search /></el-icon>
                            </template>
                        </el-input>
                        <el-button type="primary" @click="handleSearch">
                            <el-icon><Search /></el-icon>
                            搜索
                        </el-button>
                        <el-button type="success" @click="handleAdd">
                            <el-icon><Plus /></el-icon>
                            发布通知
                        </el-button>
                    </div>
                </div>
            </template>

            <!-- 状态筛选标签 -->
            <div class="filter-tags">
                <el-radio-group v-model="statusFilter" size="large" @change="handleFilterChange">
                    <el-radio-button value="all" label="全部">全部</el-radio-button>
                    <el-radio-button value="published" label="已发布">已发布</el-radio-button>
                    <el-radio-button value="draft" label="草稿">草稿</el-radio-button>
                    <el-radio-button value="expired" label="已过期">已过期</el-radio-button>
                </el-radio-group>
            </div>

            <!-- 通知数据表格 -->
            <el-table
                :data="filteredNotices"
                style="width: 100%"
                v-loading="loading"
                border
                stripe
                highlight-current-row
                @row-click="handleRowClick"
                class="notice-table">
                <el-table-column prop="id" label="ID" width="80" align="center" />
                <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
                <el-table-column prop="type" label="类型" width="120">
                    <template #default="scope">
                        <el-tag :type="getTypeTagType(scope.row.type)" effect="light">
                            {{ scope.row.type }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="publishTime" label="发布时间" width="180">
                    <template #default="scope">
                        <el-tooltip :content="formatDateDetail(scope.row.publishTime)" placement="top">
                            <span>{{ formatDate(scope.row.publishTime) }}</span>
                        </el-tooltip>
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="100">
                    <template #default="scope">
                        <el-tag :type="getStatusType(scope.row.status)" effect="light">
                            {{ getStatusText(scope.row.status) }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="220" fixed="right" align="center">
                    <template #default="scope">
                        <el-button
                            type="primary"
                            size="small"
                            @click.stop="handleEdit(scope.row)"
                            :icon="Edit">
                            编辑
                        </el-button>
                        <el-button
                            type="danger"
                            size="small"
                            @click.stop="handleDelete(scope.row)"
                            :icon="Delete">
                            删除
                        </el-button>
                        <el-button
                            type="info"
                            size="small"
                            @click.stop="handlePreview(scope.row)"
                            :icon="View">
                            预览
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 分页 -->
            <div class="pagination-container">
                <el-pagination
                    v-model:current-page="currentPage"
                    v-model:page-size="pageSize"
                    :page-sizes="[10, 20, 50, 100]"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="totalNotices"
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                    background />
            </div>
        </el-card>

        <!-- 预览抽屉 -->
        <el-drawer
            v-model="previewDrawerVisible"
            title="通知预览"
            direction="rtl"
            size="40%">
            <div v-if="currentNotice" class="notice-preview">
                <div class="notice-header">
                    <h2>{{ currentNotice.title }}</h2>
                    <div class="notice-meta">
                        <el-tag size="small" :type="getTypeTagType(currentNotice.type)">{{ currentNotice.type }}</el-tag>
                        <span class="publish-time">发布时间：{{ formatDateDetail(currentNotice.publishTime) }}</span>
                        <span class="publisher">发布人：{{ currentNotice.publisher }}</span>
                    </div>
                </div>
                <el-divider />
                <div class="notice-content" v-html="currentNotice.content"></div>
                
                <div class="drawer-footer">
                    <el-button type="primary" @click="handleEdit(currentNotice)">编辑通知</el-button>
                </div>
            </div>
        </el-drawer>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Edit, Delete, View, Plus } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import { useNoticeStore } from '@/stores/back/noticeStore';
import { 
    getTypeTagType, 
    getStatusTagType, 
    noticeStatusMap,
    transformNoticeForFrontend,
    transformNoticeForBackend
} from '@/utils/notice';
import { formatDate, formatDateDetail } from '@/utils/date';

const router = useRouter();
const noticeStore = useNoticeStore();

// 获取通知列表
const getNotices = async () => {
    try {
        loading.value = true;
        await noticeStore.loadNoticeList({
            page: currentPage.value,
            pageSize: pageSize.value,
            status: statusFilter.value === 'published' ? '1' : 
                   statusFilter.value === 'draft' ? '0' : 
                   statusFilter.value === 'expired' ? '2' : '',
            search: searchQuery.value
        });
    } catch (error) {
        ElMessage.error('获取通知列表失败');
    } finally {
        loading.value = false;
    }
};

// 状态和加载
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const searchQuery = ref('');
const statusFilter = ref('all');

// 抽屉和当前选中通知
const previewDrawerVisible = ref(false);
const currentNotice = ref(null);

// 过滤后的通知列表
const filteredNotices = computed(() => {
    let result = noticeStore.noticeList;
    
    // 状态筛选
    if (statusFilter.value !== 'all') {
        const expectedStatus = statusFilter.value === 'published' ? '1' : 
                              statusFilter.value === 'draft' ? '0' : 
                              statusFilter.value === 'expired' ? '2' : '';
        result = result.filter(item => item.status === expectedStatus);
    }
    
    // 搜索筛选
    if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase();
        result = result.filter(item => 
            (item.title && item.title.toLowerCase().includes(query)) || 
            (item.content && item.content.toLowerCase().includes(query))
        );
    }
    
    return result;
});

// 总通知数
const totalNotices = computed(() => noticeStore.total);

// 获取状态类型
const getStatusType = (status) => {
    const typeMap = {
        '1': 'success',  // 已发布
        '0': 'info',     // 草稿
        '2': 'warning'   // 已撤回
    };
    return typeMap[status] || 'info';
};

// 获取状态文本
const getStatusText = (status) => {

    const textMap = {
        '1': '已发布',
        '0': '草稿',
        '2': '已撤回'
    };
    return textMap[status] || '未知';
};

// 搜索
const handleSearch = () => {
    currentPage.value = 1;
};

// 状态筛选变化
const handleFilterChange = () => {
    currentPage.value = 1;
};

// 添加通知
const handleAdd = () => {
    router.push('/admin/notice/publish');
};

// 编辑通知
const handleEdit = (row) => {
    router.push(`/admin/notice/publish?id=${row.id}`);
};

// 预览通知
const handlePreview = async (row) => {
    try {
        await noticeStore.fetchNoticeInfo(row.id);
        currentNotice.value = transformNoticeForFrontend(noticeStore.currentNotice);
        previewDrawerVisible.value = true;
    } catch (error) {
        console.error(error);
    }
};

// 删除通知
const handleDelete = async (row) => {
    try {
        await ElMessageBox.confirm(`确认删除通知 "${row.title}" 吗？`, '警告', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        });
        
        loading.value = true;
        await noticeStore.deleteNotice(row.id);
        ElMessage.success('删除成功');
        loading.value = false;
        
        // 如果删除的是当前预览的通知，关闭抽屉
        if (currentNotice.value && currentNotice.value.id === row.id) {
            previewDrawerVisible.value = false;
        }
    } catch (error) {
        console.error(error);
    }
};

// 处理行点击
const handleRowClick = (row) => {
    handlePreview(row);
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



onMounted(() => {
    getNotices();
});
</script>

<style scoped>
.notice-management {
    padding: 16px;
    background-color: #f5f7fa;
    min-height: calc(100vh - 120px);
}

.main-card {
    margin-bottom: 20px;
    transition: all 0.3s;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.main-card:hover {
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

.filter-tags {
    margin: 16px 0;
}

.notice-table {
    margin: 16px 0;
    border-radius: 4px;
    overflow: hidden;
}

.notice-table :deep(th) {
    background-color: #f5f7fa !important;
    color: #606266;
    font-weight: 600;
}

.notice-table :deep(.el-table__row) {
    cursor: pointer;
    transition: all 0.2s;
}

.notice-table :deep(.el-table__row:hover) {
    background-color: #ecf5ff !important;
}

.pagination-container {
    margin-top: 24px;
    display: flex;
    justify-content: flex-end;
    padding: 8px 0;
}

.notice-preview {
    padding: 16px;
    position: relative;
    min-height: 100%;
}

.notice-header {
    margin-bottom: 16px;
}

.notice-header h2 {
    margin: 0 0 16px 0;
    font-size: 22px;
    font-weight: 600;
    color: #303133;
}

.notice-meta {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 16px;
    color: #909399;
    font-size: 14px;
}

.notice-content {
    line-height: 1.8;
    color: #606266;
    margin-bottom: 80px;
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
    
    .filter-tags {
        overflow-x: auto;
        white-space: nowrap;
        padding-bottom: 8px;
    }
}
</style>


