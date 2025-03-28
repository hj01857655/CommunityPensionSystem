<template>
  <span>{{ displayValue }}</span>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';

const props = defineProps({
  startVal: {
    type: Number,
    default: 0
  },
  endVal: {
    type: Number,
    default: 0
  },
  duration: {
    type: Number,
    default: 2000
  },
  decimals: {
    type: Number,
    default: 0
  },
  autoplay: {
    type: Boolean,
    default: true
  }
});

const displayValue = ref(props.startVal);
let startTime = null;
let timer = null;

const formatNumber = (num) => {
  return Number(num).toFixed(props.decimals);
};

const counter = () => {
  const now = Date.now();
  const progress = Math.min(1, (now - startTime) / props.duration);
  const currentValue = props.startVal + (props.endVal - props.startVal) * progress;
  
  displayValue.value = formatNumber(currentValue);

  if (progress < 1) {
    timer = requestAnimationFrame(counter);
  } else {
    displayValue.value = formatNumber(props.endVal);
  }
};

const start = () => {
  if (timer) {
    cancelAnimationFrame(timer);
  }
  startTime = Date.now();
  counter();
};

watch(() => props.endVal, (newVal) => {
  if (props.autoplay) {
    start();
  }
});

onMounted(() => {
  if (props.autoplay) {
    start();
  }
});
</script> 