/**
 * @typedef {Object} ValidatorRule
 * @property {boolean} [required] - 是否必填
 * @property {string} [message] - 错误提示信息
 * @property {'blur'|'change'|Array<'blur'|'change'>} [trigger] - 触发方式
 * @property {number} [min] - 最小长度
 * @property {number} [max] - 最大长度
 * @property {string} [type] - 类型
 * @property {RegExp} [pattern] - 正则表达式
 * @property {Function} [validator] - 自定义验证函数
 */

/**
 * 表单验证 Hook
 * @returns {{
 *   required: (message?: string, trigger?: ValidatorRule['trigger']) => ValidatorRule,
 *   length: (min: number, max: number, message?: string, trigger?: ValidatorRule['trigger']) => ValidatorRule,
 *   pattern: (pattern: RegExp, message: string, trigger?: ValidatorRule['trigger']) => ValidatorRule,
 *   validator: (validator: Function, trigger?: ValidatorRule['trigger']) => ValidatorRule
 * }}
 */
export const useValidator = () => {
  const required = (message = '该项为必填项', trigger = 'blur') => {
    return {
      required: true,
      message,
      trigger
    }
  }

  const length = (min, max, message, trigger = 'blur') => {
    return {
      min,
      max,
      message: message || `长度应在 ${min} 到 ${max} 个字符之间`,
      trigger
    }
  }

  const pattern = (pattern, message, trigger = 'blur') => {
    return {
      pattern,
      message,
      trigger
    }
  }

  const validator = (validator, trigger = 'blur') => {
    return {
      validator,
      trigger
    }
  }

  return {
    required,
    length,
    pattern,
    validator
  }
} 