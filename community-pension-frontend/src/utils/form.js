export const resetForm = (formRef) => {
  if (!formRef) return
  formRef.resetFields()
  Object.keys(formRef.model).forEach(key => {
    formRef.model[key] = typeof formRef.model[key] === 'boolean' ? false : ''
  })
}