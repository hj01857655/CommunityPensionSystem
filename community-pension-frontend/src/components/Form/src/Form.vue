<!-- 动态表单组件 -->
<template>
  <el-form
    ref="formRef"
    :model="formModel"
    :rules="rules"
    label-width="100px"
    :inline="inline"
    :label-position="labelPosition"
  >
    <el-row :gutter="gutter">
      <el-col
        v-for="schema in schema"
        :key="schema.field"
        v-bind="schema.colProps || { span: 24 }"
      >
        <el-form-item
          :label="schema.label"
          :prop="schema.field"
          :rules="schema.rules"
        >
          <component
            :is="getComponent(schema.component)"
            v-model="formModel[schema.field]"
            v-bind="schema.componentProps || {}"
          />
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>

<script setup>
import { ref, reactive, onMounted, resolveComponent, h } from 'vue'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const props = defineProps({
  schema: {
    type: Array,
    required: true
  },
  rules: {
    type: Object,
    default: () => ({})
  },
  inline: {
    type: Boolean,
    default: false
  },
  labelPosition: {
    type: String,
    default: 'right',
    validator: (value) => ['left', 'right', 'top'].includes(value)
  },
  gutter: {
    type: Number,
    default: 0
  }
})

const emit = defineEmits(['register'])

// 表单ref
const formRef = ref()

// 表单数据模型
const formModel = reactive({})

// 初始化表单数据
const initForm = () => {
  props.schema.forEach((item) => {
    formModel[item.field] = item.value !== undefined ? item.value : ''
  })
}

// 获取组件
const getComponent = (component) => {
  if (typeof component === 'string') {
    // 处理 Element Plus 组件
    const elComponent = resolveComponent(`el-${component.toLowerCase()}`)
    if (elComponent) {
      return elComponent
    }
    // 处理 Element Plus 图标
    const iconComponent = ElementPlusIconsVue[component]
    if (iconComponent) {
      return iconComponent
    }
    console.warn(`未找到组件: ${component}`)
    return 'div'
  }
  return component
}

// 表单方法
const formMethods = {
  // 获取表单数据
  getFormData: () => {
    return { ...formModel }
  },
  // 设置表单数据
  setFormData: (data) => {
    Object.assign(formModel, data)
  },
  // 重置表单
  resetFields: () => {
    formRef.value?.resetFields()
  },
  // 验证表单
  validate: () => {
    return formRef.value?.validate()
  },
  // 获取表单实例
  getElFormExpose: () => {
    return formRef.value
  }
}

// 向父组件暴露表单方法
onMounted(() => {
  initForm()
  emit('register', formMethods)
})
</script>

<style scoped>
.el-form {
  width: 100%;
}
</style> 