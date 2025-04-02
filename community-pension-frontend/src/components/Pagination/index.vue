<template>
    <div :class="{ 'hidden': hidden }" class="pagination-container">
        <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :layout="layout"
            :page-sizes="pageSizes"
            :pager-count="pagerCount"
            :total="total"
        />
    </div>
</template>

<script setup>
import { computed } from 'vue';
import { scroll } from '@/utils/scroll';

const props = defineProps({
    total: {
        type: Number,
        required: true
    },
    page: {
        type: Number,
        default: 1
    },
    limit: {
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
const emit=defineEmits();
// 当前页
const currentPage=computed({
    get(){
        return props.limit;
    },
    set(val){
        emit('update:limit',val);
    }
});
const pageSize=computed({
    get(){
        return props.limit;
    },
    set(val){
        emit('update:limit',val);
    }
});
const handleSizeChange=(val)=>{
    if(currentPage.value*val>props.total){
        currentPage.value=1;
    }
    emit('pagination',{page:val,limit:pageSize.value});
    if(props.autoScroll){
        scroll(0,800);
    }
}
</script>

<style scoped>
.pagination-container{
    background: #fff;
}
.pagination-container.hidden{
    display: none;
}
</style>