<template>
    <div :class="{ 'hidden': hidden }" class="pagination-container">
        <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :layout="layout"
            :page-sizes="pageSizes"
            :pager-count="pagerCount"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />
    </div>
</template>

<script setup>
import { scroll } from '@/utils/scroll';
import { computed } from 'vue';

const props = defineProps({
    total: {
        type: Number,
        required: true
    },
    current: {
        type: Number,
        default: 1
    },
    size: {
        type: Number,
        default: 20
    },
    pageSizes: {
        type: Array,
        default: () => [10, 20, 30, 50]
    },
    pagerCount: {
        type: Number,
        default: document.body.clientWidth < 992 ? 5 : 7
    },
    layout: {
        type: String,
        default: 'total, sizes, prev, pager, next, jumper'
    },
    background: {
        type: Boolean,
        default: true
    },
    autoScroll: {
        type: Boolean,
        default: true
    },
    hidden: {
        type: Boolean,
        default: false
    }
});
// 定义emit
const emit = defineEmits(['update:current', 'update:size', 'pagination']);

// 当前页
const currentPage = computed({
    get() {
        return props.current;
    },
    set(val) {
        emit('update:current', val);
    }
});

// 每页条数
const pageSize = computed({
    get() {
        return props.size;
    },
    set(val) {
        emit('update:size', val);
    }
});

// 页码变化
const handleSizeChange = (val) => {
    emit('update:size', val);
    if (currentPage.value * val > props.total) {
        currentPage.value = 1;
    }
    emit('pagination', { current: currentPage.value, size: val });
    if (props.autoScroll) {
        scroll(0, 800);
    }
};

// 页数变化
const handleCurrentChange = (val) => {
    emit('update:current', val);
    emit('pagination', { current: val, size: pageSize.value });
    if (props.autoScroll) {
        scroll(0, 800);
    }
};
</script>

<style scoped>
.pagination-container{
    background: #fff;
}
.pagination-container.hidden{
    display: none;
}
</style>