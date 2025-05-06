<template>
    <div class="app-container">
        <!-- 搜索条件 -->
        <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px">
            <el-form-item label="通知标题" prop="title">
                <el-input v-model="searchQuery" placeholder="请输入通知标题/内容" clearable style="width: 200px"
                    @keyup.enter="handleSearch" @clear="handleSearch" />
            </el-form-item>
            <el-form-item label="状态" prop="status">
                <el-select v-model="statusFilter" placeholder="通知状态" clearable style="width: 200px" @change="handleFilterChange">
                    <el-option label="全部" value="all" />
                    <el-option label="已发布" value="published" />
                    <el-option label="草稿" value="draft" />
                    <el-option label="已过期" value="expired" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
                <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
            </el-form-item>
        </el-form>

        <!-- 操作按钮 -->
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button type="primary" plain :icon="Plus" @click="handleAdd">发布通知</el-button>
            </el-col>
            <right-toolbar v-model:showSearch="showSearch" @queryTable="handleQuery"></right-toolbar>
        </el-row>

        <!-- 通知数据表格 -->
        <el-table
            :data="filteredNotices"
            style="width: 100%"
            v-loading="loading"
            border
            stripe
            highlight-current-row
            @row-click="handleRowClick">
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

        <!-- 分页组件 -->
        <Pagination
            v-if="totalNotices > 0"
            :total="totalNotices"
            :page="currentPage"
            :limit="pageSize"
            @pagination="handlePagination"
        />

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
import RightToolbar from '@/components/back/system/menu/RightToolbar.vue';
import Pagination from '@/components/common/Pagination.vue';
import { useNoticeStore } from '@/stores/back/noticeStore';
import { formatDate, formatDateDetail } from '@/utils/date';
import {
    getTypeTagType,
    transformNoticeForFrontend
} from '@/utils/notice';
import { Delete, Edit, Plus, Refresh, Search, View } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

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
const showSearch = ref(true);

// 查询参数
const queryParams = ref({
    title: '',
    status: ''
});

// 抽屉和当前选中通知
const previewDrawerVisible = ref(false);
const currentNotice = ref(null);

// 通知列表（直接使用store中的数据，状态筛选已在API请求中处理）
const filteredNotices = computed(() => {
    // 直接使用store中的数据
    return noticeStore.noticeList;
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
    getNotices();
};

// 状态筛选变化
const handleFilterChange = () => {
    currentPage.value = 1;
    getNotices();
};

// 右侧工具栏刷新按钮
const handleQuery = () => {
    getNotices();
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
        await noticeStore.removeNotice(row.id);
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

// 分页处理
const handlePagination = (val) => {
    currentPage.value = val.page;
    pageSize.value = val.limit;
    getNotices();
};

// 重置查询
const resetQuery = () => {
    searchQuery.value = '';
    statusFilter.value = 'all';
    currentPage.value = 1;
    getNotices();
};



onMounted(() => {
    getNotices();
});
</script>

<style scoped>
.app-container {
    padding: 20px;
}

.mb8 {
    margin-bottom: 8px;
}

.small-padding {
    padding-left: 5px;
    padding-right: 5px;
}

.fixed-width .el-button--small {
    padding: 7px 10px;
    min-width: 60px;
}

.notice-table :deep(td) {
    padding: 16px 0;
    color: #4a4a4a;
}

.notice-table :deep(th) {
    background: linear-gradient(to bottom, #f8f9fa, #f5f7fa) !important;
    color: #1a1a1a;
    font-weight: 600;
    font-size: 14px;
    padding: 16px 0;
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

:deep(.el-tag--info) {
    background: linear-gradient(45deg, #e6f3ff, #f0f7ff);
    color: #409eff;
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

:deep(.el-tag--danger) {
    background: linear-gradient(45deg, #ffe6e6, #fff0f0);
    color: #f56c6c;
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

:deep(.el-button--link) {
    padding: 6px 12px;
    border-radius: 6px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    font-weight: 500;
    position: relative;
    overflow: hidden;
}

:deep(.el-button--link:hover) {
    background-color: #f5f7fa;
    transform: translateY(-1px);
}

:deep(.el-button--link::after) {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.1), transparent);
    transition: all 0.3s ease;
}

:deep(.el-button--link:hover::after) {
    transform: translateX(100%);
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
    .app-container {
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
    
    :deep(.el-dialog) {
        margin: 8px !important;
    }
    
    :deep(.el-dialog__body) {
        padding: 16px;
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


