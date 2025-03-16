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
import { ArrowRight } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';

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
})

const emit = defineEmits(['more'])
const router = useRouter()

const emitMore = () => {
    if (!props.loading) {
        emit('more', props.moreRouteName);
    }
}
</script>

<style scoped>
.preview-card {
    min-height: 220px;
    border-radius: 8px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    background-color: #fff;
    overflow: hidden;
    border: 1px solid #e0e0e0;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.preview-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
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
    padding: 16px;
    border-bottom: 1px solid #ebeef5;
}

.card-title {
    display: flex;
    align-items: center;
    gap: 8px;
}

.card-badge {
    margin-left: 8px;
}

.more-btn {
    padding: 0;
    font-size: 14px;
    display: flex;
    align-items: center;
    gap: 4px;
    transition: all 0.3s ease;
}

.more-btn:hover {
    opacity: 0.8;
    transform: translateX(4px);
}

.more-btn:active {
    transform: translateX(2px);
}

.card-content {
    padding: 16px;
    overflow-y: auto;
    scrollbar-width: thin;
    scrollbar-color: #909399 #f4f4f5;
}

.card-content::-webkit-scrollbar {
    width: 6px;
}

.card-content::-webkit-scrollbar-track {
    background: #f4f4f5;
    border-radius: 3px;
}

.card-content::-webkit-scrollbar-thumb {
    background: #909399;
    border-radius: 3px;
}

.card-footer {
    padding: 12px 16px;
    border-top: 1px solid #ebeef5;
    background-color: #fafafa;
}

/* 添加响应式设计 */
@media screen and (max-width: 768px) {
    .preview-card {
        min-height: 180px;
    }

    .card-header {
        padding: 12px;
        font-size: 14px;
    }

    .card-content {
        padding: 12px;
    }
}
</style>