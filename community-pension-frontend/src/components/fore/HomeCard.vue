<template>
    <el-card 
        :class="[
            'preview-card',
            cardClass,
            { 'is-loading': loading }
        ]" 
        :shadow="shadow"
    >
        <template #header>
            <div class="card-header" :style="{ backgroundColor: headerBgColor }">
                <span class="card-title" :style="{ color: titleColor }">
                    <el-icon v-if="icon" :size="iconSize">
                        <component :is="icon" />
                    </el-icon>
                    {{ title }}
                    <el-tag 
                        v-if="badge" 
                        :type="badgeType" 
                        size="small" 
                        class="card-badge"
                    >
                        {{ badge }}
                    </el-tag>
                </span>
                <el-button 
                    v-if="showMore"
                    :link="true"
                    :type="moreButtonType"
                    class="more-btn" 
                    @click="emitMore"
                    :disabled="loading"
                >
                    {{ moreText }} <el-icon><ArrowRight /></el-icon>
                </el-button>
            </div>
        </template>
        
        <!-- 加载状态 -->
        <el-skeleton v-if="loading" :rows="3" animated />
        
        <!-- 默认内容插槽 -->
        <div 
            v-else 
            class="card-content"
            :style="{ maxHeight: contentMaxHeight }"
        >
            <slot></slot>
        </div>

        <!-- 底部插槽 -->
        <template #footer v-if="$slots.footer">
            <div class="card-footer">
                <slot name="footer"></slot>
            </div>
        </template>
    </el-card>
</template>

<script setup>
import {ArrowRight} from '@element-plus/icons-vue';

const props = defineProps({
    title: {
        type: String,
        required: true
    },
    icon: {
        type: String,
        default: ''
    },
    cardClass: {
        type: String,
        default: ''
    },
    showMore: {
        type: Boolean,
        default: true
    },
    loading: {
        type: Boolean,
        default: false
    },
    shadow: {
        type: String,
        default: 'hover',
        validator: (value) => ['always', 'hover', 'never'].includes(value)
    },
    headerBgColor: {
        type: String,
        default: '#f5f5f5'
    },
    titleColor: {
        type: String,
        default: '#2c3e50'
    },
    iconSize: {
        type: Number,
        default: 18
    },
    badge: {
        type: [String, Number],
        default: ''
    },
    badgeType: {
        type: String,
        default: 'info',
        validator: (value) => ['success', 'warning', 'info', 'danger'].includes(value)
    },
    moreText: {
        type: String,
        default: '更多'
    },
    moreButtonType: {
        type: String,
        default: 'primary'
    },
    contentMaxHeight: {
        type: String,
        default: 'none'
    },
    moreRouteName: {
        type: String,
        default: ''
    }
});

const emit = defineEmits(['more']);

const emitMore = () => {
    if (!props.loading) {
        emit('more', props.moreRouteName);
    }
}
</script>

<style scoped>
.preview-card {
    min-height: 220px;
    height: 100%;
    width: 100%;
  min-width: 380px;
    border-radius: 8px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    background-color: #fff;
    overflow: hidden;
    border: 1px solid #e0e0e0;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    display: flex;
    flex-direction: column;
}

/* 添加暗黑主题支持 */
:root.dark .preview-card {
    background-color: #2a2a2a;
    border-color: #3a3a3a;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.preview-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
}

/* 添加暗黑主题支持 */
:root.dark .preview-card:hover {
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.3);
}

.preview-card.is-loading {
    pointer-events: none;
    opacity: 0.8;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 16px;
    font-weight: bold;
    padding: 16px 20px;
    border-bottom: 1px solid #ebeef5;
    flex-shrink: 0;
}

/* 添加暗黑主题支持 */
:root.dark .card-header {
    border-bottom-color: #3a3a3a;
    color: #eee;
    background-color: #252525 !important; /* 覆盖props中的headerBgColor */
}

.card-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px; /* 恢复标题字体大小 */
}

/* 添加图标颜色适配 */
:root.dark .card-title .el-icon {
    color: #66b1ff;
}

.card-badge {
    margin-left: 0px;
}

.more-btn {
    padding: 0;
    font-size: 14px;
    display: flex;
    align-items: center;
    gap: 4px;
    transition: all 0.3s ease;
}

/* 添加暗黑主题支持 */
:root.dark .more-btn {
    color: #66b1ff;
}

.more-btn:hover {
    opacity: 0.8;
    transform: translateX(4px);
}

.more-btn:active {
    transform: translateX(2px);
}

.card-content {
    padding: 16px 20px;
    overflow-y: auto;
    scrollbar-width: thin;
    scrollbar-color: #909399 #f4f4f5;
    min-height: 120px;
    flex: 1;
    display: flex;
    flex-direction: column;
}

/* 添加暗黑主题支持 */
:root.dark .card-content {
    color: #ddd;
    scrollbar-color: #666 #333;
}

.card-content::-webkit-scrollbar {
    width: 6px;
}

.card-content::-webkit-scrollbar-track {
    background: #f4f4f5;
    border-radius: 3px;
}

/* 添加暗黑主题支持 */
:root.dark .card-content::-webkit-scrollbar-track {
    background: #333;
}

.card-content::-webkit-scrollbar-thumb {
    background: #909399;
    border-radius: 3px;
}

/* 添加暗黑主题支持 */
:root.dark .card-content::-webkit-scrollbar-thumb {
    background: #666;
}

.card-footer {
    padding: 12px 20px;
    border-top: 1px solid #ebeef5;
    background-color: #fafafa;
    flex-shrink: 0;
}

/* 添加暗黑主题支持 */
:root.dark .card-footer {
    border-top-color: #3a3a3a;
    background-color: #252525;
    color: #aaa;
}

/* 添加响应式设计 */
@media screen and (min-width: 1600px) {
    .preview-card {
        min-height: 240px;
      min-width: 420px;
    }
    
    .card-header {
      padding: 18px 24px;
        font-size: 17px;
    }
    
    .card-content {
      padding: 18px 24px;
    }
    
    .card-footer {
      padding: 14px 24px;
    }
}

@media screen and (max-width: 1366px) {
    .preview-card {
        min-height: 200px;
      min-width: 360px;
    }
    
    .card-header {
      padding: 14px 20px;
        font-size: 15px;
    }
    
    .card-content {
      padding: 14px 20px;
    }
    
    .card-footer {
      padding: 12px 20px;
    }
}

@media screen and (max-width: 768px) {
    .preview-card {
        min-height: 180px;
      min-width: 320px;
    }

    .card-header {
      padding: 12px 18px;
        font-size: 14px;
    }

    .card-content {
      padding: 12px 18px;
        min-height: 100px;
    }
    
    .card-footer {
      padding: 10px 18px;
    }
}
</style>