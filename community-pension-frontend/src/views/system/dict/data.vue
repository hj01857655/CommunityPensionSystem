const handleUpdate = async (row) => {
  try {
    const res = await getDictDataDetail(row.dictCode)
    if (res.code === 200) {
      formData.value = res.data
      dialogVisible.value = true
    } else {
      ElMessage.error(res.msg || '获取字典数据失败')
    }
  } catch (error) {
    console.error('获取字典数据失败:', error)
    ElMessage.error('获取字典数据失败')
  }
}

const handleSubmit = async () => {
  if (!formData.value.dictType) {
    ElMessage.warning('请选择字典类型')
    return
  }
  if (!formData.value.dictLabel) {
    ElMessage.warning('请输入字典标签')
    return
  }
  if (!formData.value.dictValue) {
    ElMessage.warning('请输入字典键值')
    return
  }
  
  try {
    const api = formData.value.dictCode ? updateDictData : createDictData
    const res = await api(formData.value)
    if (res.code === 200) {
      ElMessage.success('操作成功')
      dialogVisible.value = false
      getList()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

const handleStatusChange = async (row) => {
  try {
    const res = await updateDictData({
      dictCode: row.dictCode,
      status: row.status
    });
    if (res.code === 200) {
      ElMessage.success('状态更新成功');
      getList();
    } else {
      ElMessage.error(res.msg || '状态更新失败');
      // 恢复原状态
      row.status = row.status === '0' ? '1' : '0';
    }
  } catch (error) {
    console.error('状态更新失败:', error);
    ElMessage.error('状态更新失败');
    // 恢复原状态
    row.status = row.status === '0' ? '1' : '0';
  }
}; 