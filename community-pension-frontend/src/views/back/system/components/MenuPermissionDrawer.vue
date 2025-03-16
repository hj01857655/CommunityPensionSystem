<!-- 菜单权限添加抽屉 -->
<template>
  <el-drawer
    v-model="modelValue"
    title="新增按钮权限"
    :destroy-on-close="true"
  >
    <template #default>
      <Form 
        :rules="rules" 
        @register="formRegister" 
        :schema="formSchema" 
      />
    </template>
    <template #footer>
      <div class="drawer-footer">
        <el-button @click="() => (modelValue = false)">取消</el-button>
        <el-button type="primary" @click="confirm">确认</el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script setup>
import { reactive } from 'vue'
import { Form } from '@/components/Form'
import { useForm } from '@/hooks/web/useForm'
import { useValidator } from '@/hooks/web/useValidator'

// v-model绑定
const modelValue = defineModel()

// 表单验证
const { required } = useValidator()

// 表单配置
const formSchema = reactive([
  {
    field: 'label',
    label: '权限名称',
    component: 'Input',
    componentProps: {
      placeholder: '请输入权限名称'
    },
    colProps: {
      span: 24
    }
  },
  {
    field: 'value',
    label: '权限值',
    component: 'Input',
    componentProps: {
      placeholder: '请输入权限值'
    },
    colProps: {
      span: 24
    }
  }
])

// 表单方法
const { formRegister, formMethods } = useForm()
const { getFormData, getElFormExpose } = formMethods

// 事件
const emit = defineEmits(['confirm'])

// 表单验证规则
const rules = reactive({
  label: [required('请输入权限名称')],
  value: [required('请输入权限值')]
})

// 确认提交
const confirm = async () => {
  const elFormExpose = await getElFormExpose()
  if (!elFormExpose) return
  
  try {
    const valid = await elFormExpose.validate()
    if (valid) {
      const formData = await getFormData()
      formData.id = Date.now()
      emit('confirm', formData)
      modelValue.value = false
    }
  } catch (err) {
    console.error('表单验证失败:', err)
  }
}
</script>

<style scoped>
.drawer-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 0 20px 20px;
}
</style> 