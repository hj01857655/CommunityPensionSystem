import { ref } from 'vue'

/**
 * @typedef {Object} FormMethods
 * @property {() => Promise<Object>} getFormData - 获取表单数据
 * @property {(data: Object) => void} setFormData - 设置表单数据
 * @property {() => void} resetFields - 重置表单
 * @property {() => Promise<boolean>} validate - 验证表单
 * @property {() => Object} getElFormExpose - 获取表单实例
 */

/**
 * 表单操作 Hook
 * @returns {{
 *   formRegister: (methods: FormMethods) => void,
 *   formMethods: FormMethods
 * }}
 */
export const useForm = () => {
  const formMethods = ref()

  const register = (methods) => {
    formMethods.value = methods
  }

  return {
    formRegister: register,
    formMethods: {
      getFormData: async () => {
        return await formMethods.value?.getFormData()
      },
      setFormData: (data) => {
        formMethods.value?.setFormData(data)
      },
      resetFields: () => {
        formMethods.value?.resetFields()
      },
      validate: async () => {
        return await formMethods.value?.validate()
      },
      getElFormExpose: () => {
        return formMethods.value?.getElFormExpose()
      }
    }
  }
} 