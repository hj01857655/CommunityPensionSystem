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
                    <el-radio-button value="all">全部</el-radio-button>
                    <el-radio-button value="published">已发布</el-radio-button>
                    <el-radio-button value="draft">草稿</el-radio-button>
                    <el-radio-button value="expired">已过期</el-radio-button>
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
        await noticeStore.loadNoticeDetail(row.id);
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
    padding: 24px;
    background-color: #f0f2f5;
    min-height: calc(100vh - 84px);
    animation: fadeIn 0.5s ease-out;
}

.main-card {
    margin-bottom: 24px;
    border-radius: 16px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    background: linear-gradient(to right bottom, #ffffff, #fafafa);
    overflow: hidden;
}

.main-card:hover {
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
    transform: translateY(-2px);
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 32px;
    background: linear-gradient(to right, #f8f9fa, #ffffff);
    border-bottom: 1px solid #e4e7ed;
}

.card-header h3 {
    margin: 0;
    font-size: 22px;
    font-weight: 600;
    color: #1a1a1a;
    letter-spacing: 0.5px;
}

.header-actions {
    display: flex;
    align-items: center;
    gap: 16px;
}

.search-input {
    width: 280px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.search-input:focus-within {
    width: 320px;
}

:deep(.el-input__wrapper) {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    border-radius: 8px;
    transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.1);
}

:deep(.el-input__wrapper.is-focus) {
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.filter-tags {
    margin: 24px 32px;
    padding: 16px;
    background: linear-gradient(135deg, #f8f9fa, #ffffff);
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

:deep(.el-radio-button__inner) {
    border-radius: 6px;
    padding: 8px 20px;
    transition: all 0.3s ease;
}

:deep(.el-radio-button__inner:hover) {
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.notice-table {
    margin: 0 32px 24px;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.notice-table :deep(th) {
    background: linear-gradient(to bottom, #f8f9fa, #f5f7fa) !important;
    color: #1a1a1a;
    font-weight: 600;
    font-size: 14px;
    padding: 16px 0;
}

.notice-table :deep(td) {
    padding: 16px 0;
    color: #4a4a4a;
    font-size: 14px;
}

.notice-table :deep(.el-table__row) {
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.notice-table :deep(.el-table__row:hover) {
    background-color: #f5f7fa !important;
    transform: translateY(-1px);
}

:deep(.el-tag) {
    border-radius: 6px;
    padding: 0 12px;
    height: 28px;
    line-height: 28px;
    font-weight: 500;
    border: none;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

:deep(.el-tag--success) {
    background: linear-gradient(45deg, #e6f7e6, #f0f9f0);
    color: #67c23a;
}

:deep(.el-tag--primary) {
    background: linear-gradient(45deg, #e6f3ff, #f0f7ff);
    color: #409eff;
}

:deep(.el-tag--warning) {
    background: linear-gradient(45deg, #fff7e6, #fff9f0);
    color: #e6a23c;
}

:deep(.el-tag--info) {
    background: linear-gradient(45deg, #f4f4f5, #f9f9fa);
    color: #909399;
}

.pagination-container {
    margin: 24px 32px;
    padding: 16px;
    display: flex;
    justify-content: flex-end;
    background: linear-gradient(to right, #f8f9fa, #ffffff);
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

:deep(.el-drawer) {
    border-radius: 16px;
    overflow: hidden;
}

:deep(.el-drawer__header) {
    margin: 0;
    padding: 24px 32px;
    background: linear-gradient(to right, #f8f9fa, #ffffff);
    border-bottom: 1px solid #e4e7ed;
}

:deep(.el-drawer__title) {
    font-size: 20px;
    font-weight: 600;
    color: #1a1a1a;
}

.notice-preview {
    padding: 32px;
    background: linear-gradient(135deg, #f8f9fa, #ffffff);
    min-height: 100%;
    position: relative;
}

.notice-header {
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid #e4e7ed;
}

.notice-header h2 {
    margin: 0 0 16px 0;
    font-size: 24px;
    font-weight: 600;
    color: #1a1a1a;
    line-height: 1.4;
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
    color: #4a4a4a;
    font-size: 15px;
    margin-bottom: 80px;
    padding: 24px;
    background: #ffffff;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.drawer-footer {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    padding: 24px 32px;
    background: linear-gradient(to right, #f8f9fa, #ffffff);
    text-align: right;
    border-top: 1px solid #e4e7ed;
}

:deep(.el-button) {
    padding: 12px 24px;
    border-radius: 8px;
    font-weight: 500;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.el-button:hover) {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

:deep(.el-button--primary) {
    background: linear-gradient(45deg, #409EFF, #66b1ff);
    border: none;
}

:deep(.el-button--primary:hover) {
    background: linear-gradient(45deg, #66b1ff, #409EFF);
}

@keyframes fadeIn {
    from {
        opacity: 0;
        transform: translateY(10px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

/* 响应式布局 */
@media screen and (max-width: 768px) {
    .notice-management {
        padding: 16px;
    }
    
    .card-header {
        padding: 16px 20px;
    }
    
    .header-actions {
        flex-direction: column;
        align-items: stretch;
    }
    
    .search-input {
        width: 100%;
    }
    
    .search-input:focus-within {
        width: 100%;
    }
    
    .filter-tags {
        margin: 16px 20px;
        padding: 12px;
        overflow-x: auto;
        white-space: nowrap;
    }
    
    .notice-table {
        margin: 0 20px 16px;
    }
    
    .pagination-container {
        margin: 16px 20px;
        padding: 12px;
    }
    
    .notice-preview {
        padding: 20px;
    }
    
    .notice-header h2 {
        font-size: 20px;
    }
    
    .notice-content {
        padding: 16px;
        margin-bottom: 60px;
    }
    
    .drawer-footer {
        padding: 16px 20px;
    }
}

/* 滚动条美化 */
::-webkit-scrollbar {
    width: 8px;
    height: 8px;
}

::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 4px;
}

::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
    background: #a8a8a8;
}
</style>


