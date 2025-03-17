<template>
  <el-dialog
    v-model="dialogVisible"
    :title="title"
    :width="width"
    :close-on-click-modal="false"
    :close-on-press-escape="true"
    :destroy-on-close="true"
    @closed="handleClosed"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      :disabled="formType === 'view'"
      :label-width="labelWidth"
      :label-position="labelPosition"
    >
      <slot :form="formData" :disabled="formType === 'view'"></slot>
    </el-form>
    
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleCancel">{{ cancelText }}</el-button>
        <el-button v-if="formType !== 'view'" type="primary" :loading="loading" @click="handleSubmit">
          {{ submitText }}
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch, nextTick } from 'vue';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: '表单'
  },
  formType: {
    type: String,
    default: 'add', // 'add', 'edit', 'view'
    validator: (val) => ['add', 'edit', 'view'].includes(val)
  },
  data: {
    type: Object,
    default: () => ({})
  },
  rules: {
    type: Object,
    default: () => ({})
  },
  width: {
    type: String,
    default: '500px'
  },
  labelWidth: {
    type: String,
    default: '100px'
  },
  labelPosition: {
    type: String,
    default: 'right'
  },
  loading: {
    type: Boolean,
    default: false
  },
  submitText: {
    type: String,
    default: '确定'
  },
  cancelText: {
    type: String,
    default: '取消'
  }
});

const emit = defineEmits(['update:visible', 'submit', 'cancel', 'closed']);

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
});

const formRef = ref(null);
const formData = reactive({ ...props.data });

watch(() => props.data, (newVal) => {
  Object.keys(formData).forEach(key => {
    formData[key] = undefined;
  });
  Object.assign(formData, newVal);
}, { deep: true });

const handleSubmit = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    emit('submit', { ...formData });
  } catch (error) {
    console.error('表单验证失败:', error);
  }
};

const handleCancel = () => {
  dialogVisible.value = false;
  emit('cancel');
};

const handleClosed = () => {
  nextTick(() => {
    formRef.value?.resetFields();
  });
  emit('closed');
};
</script>

<style scoped>
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 