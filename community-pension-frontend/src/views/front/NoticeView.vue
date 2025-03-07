<template>
  <div class="notice-list">
      <el-card shadow="hover" class="main-card">
          <template #header>
              <div class="card-header">
                  <h3>通知公告</h3>
                  <div class="header-actions">
                      <el-input
                          v-model="searchQuery"
                          placeholder="搜索通知标题"
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
                  </div>
              </div>
          </template>

          <!-- 通知类型筛选 -->
          <div class="filter-tags">
              <el-radio-group v-model="typeFilter" size="large" @change="handleFilterChange">
                  <el-radio-button label="all">全部</el-radio-button>
                  <el-radio-button label="健康通知">健康通知</el-radio-button>
                  <el-radio-button label="活动通知">活动通知</el-radio-button>
                  <el-radio-button label="工作通知">工作通知</el-radio-button>
                  <el-radio-button label="紧急通知">紧急通知</el-radio-button>
              </el-radio-group>
          </div>

          <!-- 置顶通知 -->
          <div v-if="topNotices.length > 0" class="top-notices">
              <div class="section-title">
                  <el-icon><Top /></el-icon>
                  <span>置顶通知</span>
              </div>
              <el-row :gutter="20">
                  <el-col v-for="notice in topNotices" :key="notice.id" :xs="24" :sm="12" :md="8" :lg="8" :xl="6">
                      <el-card
                          shadow="hover"
                          class="notice-card top-notice-card"
                          @click="handleViewNotice(notice)">
                          <div class="notice-card-header">
                              <el-tag size="small" type="danger" effect="dark">置顶</el-tag>
                              <el-tag size="small" :type="getTypeTagType(notice.type)">{{ notice.type }}</el-tag>
                          </div>
                          <h4 class="notice-card-title">{{ notice.title }}</h4>
                          <div class="notice-card-meta">
                              <span class="publish-time">{{ formatDate(notice.publishTime) }}</span>
                          </div>
                      </el-card>
                  </el-col>
              </el-row>
          </div>

          <!-- 通知列表 -->
          <div class="notice-grid">
              <div class="section-title">
                  <el-icon><List /></el-icon>
                  <span>全部通知</span>
              </div>
              <el-empty v-if="filteredNotices.length === 0" description="暂无通知" />
              <el-row :gutter="20">
                  <el-col v-for="notice in paginatedNotices" :key="notice.id" :xs="24" :sm="12" :md="8" :lg="8" :xl="6">
                      <el-card
                          shadow="hover"
                          class="notice-card"
                          @click="handleViewNotice(notice)">
                          <div class="notice-card-header">
                              <div class="tag-group">
                                  <el-tag v-if="!notice.isRead" size="small" type="info" effect="plain" class="unread-tag">未读</el-tag>
                                  <el-tag v-if="notice.isTop" size="small" type="danger" effect="dark">置顶</el-tag>
                                  <el-tag size="small" :type="getTypeTagType(notice.type)">{{ notice.type }}</el-tag>
                              </div>
                          </div>
                          <h4 class="notice-card-title">{{ notice.title }}</h4>
                          <div class="notice-card-meta">
                              <span class="publish-time">{{ formatDate(notice.publishTime) }}</span>
                          </div>
                      </el-card>
                  </el-col>
              </el-row>
          </div>

          <!-- 分页 -->
          <div class="pagination-container">
              <el-pagination
                  v-model:current-page="currentPage"
                  v-model:page-size="pageSize"
                  :page-sizes="[8, 16, 24, 32]"
                  layout="total, sizes, prev, pager, next, jumper"
                  :total="filteredNotices.length"
                  @size-change="handleSizeChange"
                  @current-change="handleCurrentChange"
                  background />
          </div>
      </el-card>

      <!-- 通知详情抽屉 -->
      <el-drawer
          v-model="detailDrawerVisible"
          title="通知详情"
          direction="rtl"
          size="50%">
          <div v-if="currentNotice" class="notice-detail">
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
              
              <div v-if="currentNotice.attachments && currentNotice.attachments.length > 0" class="notice-attachments">
                  <h4>附件列表</h4>
                  <ul>
                      <li v-for="(file, index) in currentNotice.attachments" :key="index">
                          <el-icon><Document /></el-icon>
                          <a :href="file.url" target="_blank">{{ file.name }}</a>
                      </li>
                  </ul>
              </div>
          </div>
      </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { Search, Top, List, Document } from '@element-plus/icons-vue';

// 通知列表数据
const notices = ref([
    {
        id: 1,
        title: '关于社区老年人健康体检的通知',
        type: '健康通知',
        content: '<p>各位社区老年人：</p><p>为了关心老年人健康，社区将于2024年6月15日至20日开展免费健康体检活动。请各位老年人携带身份证和社区卡到社区服务中心参加体检。</p><p>体检项目包括：血压、血糖、心电图、B超等基础检查项目。</p><p>请各位老年人合理安排时间参加。</p>',
        publishTime: '2024-06-01 09:00:00',
        publisher: '社区管理员',
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
        isTop: true,
        attachments: []
    },
    {
        id: 3,
        title: '防暑降温温馨提示',
        type: '健康通知',
        content: '<p>近期天气炎热，请各位老年人注意防暑降温，多喝水，少外出，保持室内通风。如有不适，请及时就医。</p>',
        publishTime: '2024-05-20 14:00:00',
        publisher: '社区卫生服务站',
        isTop: false,
        attachments: []
    },
    {
        id: 4,
        title: '社区环境整治通知',
        type: '工作通知',
        content: '<p>为创建美丽社区环境，定于本周六上午8:00-11:00进行社区环境整治，请各位居民积极配合。</p>',
        publishTime: '2024-05-18 16:30:00',
        publisher: '社区管理员',
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
  return notices.value.filter(notice => notice.isTop);
});

// 过滤后的通知列表（不包括置顶通知）
const filteredNotices = computed(() => {
  let result = notices.value.filter(notice => !notice.isTop);
  
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

// 类型筛选变化
const handleFilterChange = () => {
  currentPage.value = 1;
};

// 查看通知详情
const handleViewNotice = (notice) => {
    currentNotice.value = notice;
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
.notice-list {
  padding: 16px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 120px);
  max-width: 100%;
  margin: 0 auto;
}

.main-card {
  margin: 0 auto 20px;
  transition: all 0.3s;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  max-width: 100%;
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
  width: 100%;
  overflow-x: auto;
  white-space: nowrap;
}

.section-title {
  display: flex;
  align-items: center;
  margin: 24px 0 16px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.section-title .el-icon {
  margin-right: 8px;
  color: #409EFF;
}

.top-notices {
  margin-bottom: 24px;
}

.notice-card {
  height: 150px;
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  cursor: pointer;
  transition: all 0.3s;
  overflow: hidden;
}

.notice-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.top-notice-card {
  border-left: 3px solid #F56C6C;
}

.notice-card-header {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.notice-card-title {
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  flex: 1;
}

.notice-card-meta {
  margin-top: auto;
  color: #909399;
  font-size: 13px;
}

.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  padding: 8px 0;
}

.notice-detail {
  padding: 16px;
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
  margin-bottom: 24px;
}

.notice-attachments {
  margin-top: 24px;
  border-top: 1px dashed #e4e7ed;
  padding-top: 16px;
}

.notice-attachments h4 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #303133;
}

.notice-attachments ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.notice-attachments li {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.notice-attachments li .el-icon {
  margin-right: 8px;
  color: #409EFF;
}

.notice-attachments li a {
  color: #409EFF;
  text-decoration: none;
}

.notice-attachments li a:hover {
  text-decoration: underline;
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
      padding-bottom: 8px;
  }
  
  .notice-card {
      height: auto;
      min-height: 130px;
  }
}
.tag-group {
    display: flex;
    gap: 8px;
    align-items: center;
}

.unread-tag {
    background-color: #f56c6c;
    color: #fff;
    border: none;
}
</style>


