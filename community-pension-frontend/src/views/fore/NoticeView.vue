<template>
  <div class="notice-view">
    <!-- 搜索和筛选区域 -->
    <div class="search-filter-container">
      <el-input
        v-model="searchQuery"
        placeholder="搜索通知标题"
        class="search-input"
        @input="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      
      <el-select
        v-model="typeFilter"
        placeholder="通知类型"
        class="type-filter"
        @change="handleFilterChange"
      >
        <el-option label="全部" value="all" />
        <el-option label="健康通知" value="健康通知" />
        <el-option label="活动通知" value="活动通知" />
        <el-option label="工作通知" value="工作通知" />
        <el-option label="紧急通知" value="紧急通知" />
      </el-select>
    </div>

    <!-- 置顶通知区域 -->
    <div v-if="topNotices.length" class="top-notices-section">
      <div class="section-title">
        <el-icon><Top /></el-icon>
        <span>置顶通知</span>
      </div>
      <el-row :gutter="20" class="notice-grid">
        <el-col v-for="notice in topNotices" :key="notice.id" :xs="24" :sm="12" :md="8" :lg="6">
          <el-card 
            class="notice-card top-notice" 
            shadow="hover"
            @click="handleViewNotice(notice)"
          >
            <div class="notice-card-header">
              <el-tag :type="getTypeTagType(notice.type)" size="small">{{ notice.type }}</el-tag>
              <span class="top-icon"><el-icon><Top /></el-icon></span>
            </div>
            <h3 class="notice-title" :class="{ 'unread': !notice.isRead }">{{ notice.title }}</h3>
            <div class="notice-meta">
              <span class="publish-time">{{ formatDate(notice.publishTime) }}</span>
              <span class="publisher">{{ notice.publisher }}</span>
            </div>
            <div v-if="notice.attachments?.length" class="notice-attachments">
              <el-icon><Document /></el-icon>
              <span>{{ notice.attachments.length }}个附件</span>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 普通通知列表 -->
    <div class="normal-notices-section">
      <div class="section-title">
        <el-icon><List /></el-icon>
        <span>通知列表</span>
      </div>
      <el-row :gutter="20" class="notice-grid">
        <el-col v-for="notice in paginatedNotices" :key="notice.id" :xs="24" :sm="12" :md="8" :lg="6">
          <el-card 
            class="notice-card" 
            shadow="hover"
            @click="handleViewNotice(notice)"
          >
            <div class="notice-card-header">
              <el-tag :type="getTypeTagType(notice.type)" size="small">{{ notice.type }}</el-tag>
            </div>
            <h3 class="notice-title" :class="{ 'unread': !notice.isRead }">{{ notice.title }}</h3>
            <div class="notice-meta">
              <span class="publish-time">{{ formatDate(notice.publishTime) }}</span>
              <span class="publisher">{{ notice.publisher }}</span>
            </div>
            <div v-if="notice.attachments?.length" class="notice-attachments">
              <el-icon><Document /></el-icon>
              <span>{{ notice.attachments.length }}个附件</span>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 分页器 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[8, 16, 24, 32]"
          :total="filteredNotices.length"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 通知详情抽屉 -->
    <el-drawer
      v-model="detailDrawerVisible"
      title="通知详情"
      size="50%"
      :destroy-on-close="true"
    >
      <template v-if="currentNotice">
        <div class="notice-detail">
          <div class="notice-detail-header">
            <h2>{{ currentNotice.title }}</h2>
            <div class="notice-detail-meta">
              <el-tag :type="getTypeTagType(currentNotice.type)" size="small">
                {{ currentNotice.type }}
              </el-tag>
              <span class="publish-info">
                发布时间：{{ formatDateDetail(currentNotice.publishTime) }}
              </span>
              <span class="publisher-info">
                发布人：{{ currentNotice.publisher }}
              </span>
            </div>
          </div>
          <div class="notice-detail-content" v-html="currentNotice.content"></div>
          <div v-if="currentNotice.attachments?.length" class="notice-detail-attachments">
            <h4>附件列表：</h4>
            <el-space direction="vertical" style="width: 100%">
              <el-link
                v-for="attachment in currentNotice.attachments"
                :key="attachment.name"
                type="primary"
                :href="attachment.url"
                target="_blank"
              >
                <el-icon><Document /></el-icon>
                {{ attachment.name }}
              </el-link>
            </el-space>
          </div>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { Search, Top, List, Document } from '@element-plus/icons-vue';
import { 
    getTypeTagType, 
    getStatusTagType, 
    formatDate, 
    formatDateDetail,
    noticeStatusMap,
    transformNoticeForFrontend,
    transformNoticeForBackend
} from '@/utils/notice';

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
        isRead: false,
        attachments: [
            { name: '体检项目清单.docx', url: '#' },
            { name: '体检注意事项.pdf', url: '#' }
        ]
    },
    {
        id: 2,
        title: '端午节活动安排',
        type: '活动通知',
        content: '<p>各位社区居民：</p><p>端午节即将到来，社区将组织以下活动：</p><ol><li>包粽子比赛</li><li>诗词朗诵会</li><li>传统文化讲座</li></ol><p>欢迎大家踊跃参加！</p>',
        publishTime: '2024-06-05 10:30:00',
        publisher: '社区活动部',
        status: 'published',
        isTop: true,
        isRead: true,
        attachments: []
    },
    {
        id: 3,
        title: '防暑降温温馨提示',
        type: '健康通知',
        content: '<p>近期天气炎热，请各位老年人注意防暑降温，多喝水，少外出，保持室内通风。如有不适，请及时就医。</p>',
        publishTime: '2024-05-20 14:00:00',
        publisher: '社区卫生服务站',
        status: 'published',
        isTop: false,
        isRead: false,
        attachments: []
    },
    {
        id: 4,
        title: '社区环境整治通知',
        type: '工作通知',
        content: '<p>为创建美丽社区环境，定于本周六上午8:00-11:00进行社区环境整治，请各位居民积极配合。</p>',
        publishTime: '2024-05-18 16:30:00',
        publisher: '社区管理员',
        status: 'published',
        isTop: false,
        attachments: []
    },
    {
        id: 5,
        title: '老年人心理健康讲座',
        type: '健康通知',
        content: '<p>为提高老年人心理健康水平，社区将于本周五下午2:00在社区活动中心举办心理健康讲座，欢迎参加。</p>',
        publishTime: '2024-05-15 11:20:00',
        publisher: '社区卫生服务站',
        status: 'published',
        isTop: false,
        attachments: []
    },
    {
        id: 6,
        title: '社区志愿者招募',
        type: '工作通知',
        content: '<p>社区现招募志愿者参与社区服务工作，有意者请到社区服务中心报名。</p>',
        publishTime: '2024-05-10 09:45:00',
        publisher: '社区管理员',
        status: 'published',
        isTop: false,
        attachments: []
    },
    {
        id: 7,
        title: '紧急通知：暴雨预警',
        type: '紧急通知',
        content: '<p>气象部门发布暴雨预警，请各位居民注意防范，减少外出，确保安全。</p>',
        publishTime: '2024-05-08 18:30:00',
        publisher: '社区管理员',
        status: 'published',
        isTop: false,
        attachments: []
    },
    {
        id: 8,
        title: '社区棋牌比赛',
        type: '活动通知',
        content: '<p>社区将举办棋牌比赛，欢迎各位居民报名参加。</p>',
        publishTime: '2024-05-05 10:00:00',
        publisher: '社区活动部',
        status: 'published',
        isTop: false,
        attachments: []
    }
]);

// 状态和筛选
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(8);
const searchQuery = ref('');
const typeFilter = ref('all');

// 抽屉和当前选中通知
const detailDrawerVisible = ref(false);
const currentNotice = ref(null);

// 置顶通知
const topNotices = computed(() => {
    return notices.value.filter(notice => notice.isTop && notice.status === 'published');
});

// 过滤后的通知列表（不包括置顶通知）
const filteredNotices = computed(() => {
    let result = notices.value.filter(notice => !notice.isTop && notice.status === 'published');
    
    // 类型筛选
    if (typeFilter.value !== 'all') {
        result = result.filter(item => item.type === typeFilter.value);
    }
    
    // 搜索筛选
    if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase();
        result = result.filter(item => 
            item.title.toLowerCase().includes(query)
        );
    }
    
    return result;
});

// 分页后的通知列表
const paginatedNotices = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value;
    const end = start + pageSize.value;
    return filteredNotices.value.slice(start, end);
});

// 搜索
const handleSearch = () => {
    currentPage.value = 1;
};

// 类型筛选变化
const handleFilterChange = () => {
    currentPage.value = 1;
};

// 查看通知详情
const handleViewNotice = (notice) => {
    currentNotice.value = transformNoticeForFrontend(notice);
    detailDrawerVisible.value = true;
    // 标记为已读
    if (!notice.isRead) {
        notice.isRead = true;
        // TODO: 调用后端API更新已读状态
        // updateNoticeReadStatus(notice.id);
    }
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

// 获取通知列表
const getNotices = () => {
    loading.value = true;
    
    // 模拟API调用
    setTimeout(() => {
        // 实际项目中，这里应该调用API获取数据
        loading.value = false;
    }, 500);
};

onMounted(() => {
    getNotices();
});
</script>

<style scoped>
.notice-view {
    padding: 20px;
}

.search-filter-container {
    display: flex;
    gap: 20px;
    margin-bottom: 20px;
}

.search-input {
    width: 300px;
}

.type-filter {
    width: 150px;
}

.section-title {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 16px;
    font-size: 18px;
    font-weight: bold;
}

.notice-grid {
    margin-bottom: 20px;
}

.notice-card {
    height: 100%;
    cursor: pointer;
    transition: transform 0.3s;
}

.notice-card:hover {
    transform: translateY(-5px);
}

.notice-card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
}

.top-icon {
    color: var(--el-color-danger);
}

.notice-title {
    margin: 0 0 12px 0;
    font-size: 16px;
    line-height: 1.4;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
}

.notice-title.unread::before {
    content: "●";
    color: var(--el-color-danger);
    margin-right: 5px;
    font-size: 12px;
}

.notice-meta {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: var(--el-text-color-secondary);
    margin-bottom: 8px;
}

.notice-attachments {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 12px;
    color: var(--el-text-color-secondary);
}

.pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 20px;
}

.notice-detail {
    padding: 20px;
}

.notice-detail-header {
    margin-bottom: 24px;
}

.notice-detail-header h2 {
    margin: 0 0 16px 0;
}

.notice-detail-meta {
    display: flex;
    gap: 16px;
    align-items: center;
    color: var(--el-text-color-secondary);
}

.notice-detail-content {
    margin-bottom: 24px;
    line-height: 1.6;
}

.notice-detail-attachments {
    padding-top: 16px;
    border-top: 1px solid var(--el-border-color-lighter);
}

.notice-detail-attachments h4 {
    margin: 0 0 12px 0;
}
</style>


