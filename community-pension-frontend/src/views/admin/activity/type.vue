<template>
  <div class="activity-type">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>活动类型管理</span>
          <el-button type="primary" @click="handleAdd">新增活动类型</el-button>
        </div>
      </template>
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="类型名称" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '新增活动类型' : '编辑活动类型'">
      <el-form :model="form" label-width="120px">
        <el-form-item label="类型名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const tableData = ref([])
const dialogVisible = ref(false)
const dialogType = ref('add')
const form = ref({
  name: '',
  description: ''
})

// 获取活动类型列表
const getActivityTypes = async () => {
  // TODO: 调用后端API获取数据
  tableData.value = [
    {
      id: 1,
      name: '文化活动',
      description: '包括书法、绘画等文化类活动',
      createTime: '2024-01-20'
    }
  ]
}

// 新增活动类型
const handleAdd = () => {
  dialogType.value = 'add'
  form.value = {
    name: '',
    description: ''
  }
  dialogVisible.value = true
}

// 编辑活动类型
const handleEdit = (row) => {
  dialogType.value = 'edit'
  form.value = { ...row }
  dialogVisible.value = true
}

// 删除活动类型
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该活动类型吗？', '提示', {
      type: 'warning'
    })
    // TODO: 调用后端API删除数据
    ElMessage.success('删除成功')
    getActivityTypes()
  } catch (error) {
    console.error(error)
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    // TODO: 调用后端API保存数据
    ElMessage.success(dialogType.value === 'add' ? '新增成功' : '编辑成功')
    dialogVisible.value = false
    getActivityTypes()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  getActivityTypes()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>