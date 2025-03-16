<template>
  <div class="flex justify-center">
    <div class="text-center">
      <img width="350" :src="errorMap[type].url" alt="" />
      <div class="text-14px text-[var(--el-color-info)]">{{ errorMap[type].message }}</div>
      <div class="mt-20px">
        <el-button type="primary" @click="btnClick">{{ errorMap[type].buttonText }}</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import pageError from '@/assets/svgs/404.svg';
import networkError from '@/assets/svgs/500.svg';
import noPermission from '@/assets/svgs/403.svg';

const props = defineProps({
  type: {
    type: String,
    default: '404',
    validator: (value) => ['404', '500', '403'].includes(value)
  }
});

const emit = defineEmits(['errorClick']);

// 错误类型映射
const errorMap = {
  '404': {
    url: pageError,
    message: '页面不存在',
    buttonText: '返回首页'
  },
  '500': {
    url: networkError,
    message: '网络错误',
    buttonText: '返回首页'
  },
  '403': {
    url: noPermission,
    message: '没有权限',
    buttonText: '返回首页'
  }
};

// 按钮点击事件
const btnClick = () => {
  emit('errorClick', props.type);
};
</script>

<style scoped>
.flex {
  display: flex;
}

.justify-center {
  justify-content: center;
}

.text-center {
  text-align: center;
}

.text-14px {
  font-size: 14px;
}

.mt-20px {
  margin-top: 20px;
}
</style> 