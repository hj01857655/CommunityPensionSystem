// 通知类型映射
export const noticeTypeMap = {
  '健康通知': 'success',
  '活动通知': 'primary',
  '工作通知': 'warning',
  '紧急通知': 'danger'
};

// 通知状态映射
export const noticeStatusMap = {
  'published': '已发布',
  'draft': '草稿',
  'expired': '已过期'
};

// 获取通知类型标签样式
export const getTypeTagType = (type) => {
  return noticeTypeMap[type] || 'info';
};

// 获取通知状态标签样式
export const getStatusTagType = (status) => {
  const typeMap = {
    'published': 'success',
    'draft': 'info',
    'expired': 'warning'
  };
  return typeMap[status] || 'info';
};

// 格式化日期 - 简短版本
export const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
};

// 格式化日期 - 详细版本
export const formatDateDetail = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`;
};

// 通知数据转换（后台到前台）
export const transformNoticeForFrontend = (notice) => {
  return {
    id: notice.id,
    title: notice.title,
    type: notice.type,
    content: notice.content,
    publishTime: notice.publishTime,
    publisher: notice.publisher,
    isTop: notice.isTop || false,
    isRead: notice.isRead || false,
    status: notice.status,
    attachments: notice.attachments || []
  };
};

// 通知数据转换（前台到后台）
export const transformNoticeForBackend = (notice) => {
  return {
    id: notice.id,
    title: notice.title,
    type: notice.type,
    content: notice.content,
    publishTime: notice.publishTime,
    publisher: notice.publisher,
    isTop: notice.isTop,
    status: notice.status,
    attachments: notice.attachments
  };
}; 