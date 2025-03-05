<template>
    <div class="notice-list">
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
                    <el-radio-button label="all">全部</el-radio-button>
                    <el-radio-button label="published">已发布</el-radio-button>
                    <el-radio-button label="draft">草稿</el-radio-button>
                    <el-radio-button label="expired">已过期</el-radio-button>
                </el-radio-group>
            </div>

            <!-- 通知数据表格 -->
            <div class="table-toolbar">
                <el-button-group>
                    <el-button type="danger" :disabled="!selectedNotices.length" @click="handleBatchDelete">
                        <el-icon><Delete /></el-icon>
                        批量删除
                    </el-button>
                    <el-button type="warning" :disabled="!selectedNotices.length" @click="handleBatchExpire">
                        <el-icon><Timer /></el-icon>
                        设为过期
                    </el-button>
                </el-button-group>
            </div>

            <el-table
                :data="filteredNotices"
                style="width: 100%"
                v-loading="loading"
                border
                stripe
                highlight-current-row
                @row-click="handleRowClick"
                @selection-change="handleSelectionChange"
                class="notice-table">
                <el-table-column type="selection" width="55" />
                <el-table-column type="index" width="50" label="序号" />
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
                <el-table-column prop="isTop" label="置顶" width="80" align="center">
                    <template #default="scope">
                        <el-switch
                            v-model="scope.row.isTop"
                            @change="(val) => handleTopChange(scope.row, val)"
                            :loading="scope.row.topLoading"
                        />
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
import { Search, Edit, Delete, View, Plus, Timer } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// 通知列表数据
// 选中的通知列表
const selectedNotices = ref([]);

// 通知列表数据
const notices = ref([
    {
        id: 1,
        title: '关于社区老年人健康体检的通知',
        type: '健康通知',
        content: '<p>各位社区老年人：</p><p>为了关心老年人健康，社区将于2024年6月15日至20日开展免费健康体检活动。请各位老年人携带身份证和社区卡到社区服务中心参加体检。</p><p>体检项目包括：血压、血糖、心电图、B超等基础检查项目。</p><p>请各位老年人合理安排时间参加。</p>',
        publishTime: '2024-06-01 09:00:00',
        publisher: '社区管理员',
        status: 'published',
        isTop: true,
        topLoading: false
    },
    {
        id: 2,
        title: '端午节活动安排',
        type: '活动通知',
        content: '<p>各位社区居民：</p><p>端午节即将到来，社区将组织以下活动：</p><ol><li>包粽子比赛</li><li>诗词朗诵会</li><li>传统文化讲座</li></ol><p>欢迎大家踊跃参加！</p>',
        publishTime: '2024-06-05 10:30:00',
        publisher: '社区活动部',
        status: 'published'
    },
    {
        id: 3,
        title: '社区环境整治工作通知',
        type: '工作通知',
        content: '<p>为创建美丽社区环境，定于本周六上午8:00-11:00进行社区环境整治工作，请各位居民积极配合。</p>',
        publishTime: '',
        publisher: '社区管理员',
        status: 'draft'
    },
    {
        id: 4,
        title: '防暑降温温馨提示',
        type: '健康通知',
        content: '<p>近期天气炎热，请各位老年人注意防暑降温，多喝水，少外出，保持室内通风。如有不适，请及时就医。</p>',
        publishTime: '2024-05-20 14:00:00',
        publisher: '社区卫生服务站',
        status: 'expired'
    }
]);

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
    let result = notices.value;
    
    // 状态筛选
    if (statusFilter.value !== 'all') {
        result = result.filter(item => item.status === statusFilter.value);
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
const totalNotices = computed(() => filteredNotices.value.length);

// 格式化日期 - 简短版本
const formatDate = (dateString) => {
    if (!dateString) return '未发布';
    const date = new Date(dateString);
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
};

// 格式化日期 - 详细版本
const formatDateDetail = (dateString) => {
    if (!dateString) return '未发布';
    const date = new Date(dateString);
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`;
};

// 获取状态类型
const getStatusType = (status) => {
    const typeMap = {
        'published': 'success',
        'draft': 'info',
        'expired': 'danger'
    };
    return typeMap[status] || 'info';
};

// 获取状态文本
const getStatusText = (status) => {
    const textMap = {
        'published': '已发布',
        'draft': '草稿',
        'expired': '已过期'
    };
    return textMap[status] || '未知状态';
};

// 获取通知类型标签样式
const getTypeTagType = (type) => {
    const typeMap = {
        '健康通知': 'success',
        '活动通知': 'primary',
        '工作通知': 'warning',
        '紧急通知': 'danger'
    };
    return typeMap[type] || 'info';
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
const handlePreview = (row) => {
    currentNotice.value = { ...row };
    previewDrawerVisible.value = true;
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
        
        // 模拟API调用
        setTimeout(() => {
            notices.value = notices.value.filter(item => item.id !== row.id);
            ElMessage.success('删除成功');
            loading.value = false;
            
            // 如果删除的是当前预览的通知，关闭抽屉
            if (currentNotice.value && currentNotice.value.id === row.id) {
                previewDrawerVisible.value = false;
            }
        }, 500);
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

// 处理选择变化
const handleSelectionChange = (selection) => {
    selectedNotices.value = selection;
};

// 处理置顶状态变化
const handleTopChange = async (row, value) => {
    row.topLoading = true;
    try {
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 500));
        row.isTop = value;
        ElMessage.success(`${value ? '置顶' : '取消置顶'}成功`);
    } catch (error) {
        console.error(error);
        ElMessage.error(`${value ? '置顶' : '取消置顶'}失败`);
        row.isTop = !value;
    } finally {
        row.topLoading = false;
    }
};

// 批量删除
const handleBatchDelete = async () => {
    try {
        await ElMessageBox.confirm(`确认删除选中的 ${selectedNotices.value.length} 条通知吗？`, '警告', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        });
        
        loading.value = true;
        
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 500));
        
        const deleteIds = selectedNotices.value.map(item => item.id);
        notices.value = notices.value.filter(item => !deleteIds.includes(item.id));
        ElMessage.success('批量删除成功');
        
        // 清空选择
        selectedNotices.value = [];
    } catch (error) {
        console.error(error);
    } finally {
        loading.value = false;
    }
};

// 批量设置过期
const handleBatchExpire = async () => {
    try {
        await ElMessageBox.confirm(`确认将选中的 ${selectedNotices.value.length} 条通知设置为过期吗？`, '警告', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        });
        
        loading.value = true;
        
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 500));
        
        selectedNotices.value.forEach(notice => {
            const target = notices.value.find(item => item.id === notice.id);
            if (target) {
                target.status = 'expired';
            }
        });
        
        ElMessage.success('批量设置过期成功');
        
        // 清空选择
        selectedNotices.value = [];
    } catch (error) {
        console.error(error);
    } finally {
        loading.value = false;
    }
};

// 获取通知列表
const getNotices = () => {
    loading.value = true;
    
    // 模拟API调用
    setTimeout(() => {
        // 实际项目中，这里应该调用后端API
        loading.value = false;
    }, 500);
};

// 页面加载时获取通知列表
onMounted(() => {
    getNotices();
});

</script>

<style scoped>
.notice-list {
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
    align-items: center;
}

.search-input {
    width: 300px;
}

.filter-tags {
    margin: 20px 0;
}

.table-toolbar {
    margin-bottom: 20px;
}

.notice-table {
    margin-bottom: 20px;
}

.pagination-container {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
}

.notice-preview {
    padding: 20px;
}

.notice-header {
    margin-bottom: 20px;
}

.notice-meta {
    margin-top: 10px;
    display: flex;
    gap: 15px;
    color: #666;
    font-size: 14px;
}

.notice-content {
    line-height: 1.6;
    font-size: 16px;
}

.drawer-footer {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    padding: 20px;
    background-color: #fff;
    border-top: 1px solid #eee;
    text-align: right;
}
</style>


