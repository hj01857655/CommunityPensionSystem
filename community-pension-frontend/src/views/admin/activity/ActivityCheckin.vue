<template>
  <div class="activity-checkin">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>活动签到管理</span>
        </div>
      </template>
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="activityName" label="活动名称" />
        <el-table-column prop="elderName" label="签到人" />
        <el-table-column prop="checkinTime" label="签到时间" />
        <el-table-column prop="status" label="状态" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="handleView(scope.row)">查看</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 查看签到详情对话框 -->
    <el-dialog v-model="dialogVisible" title="签到详情">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="活动名称">{{ currentCheckin.activityName }}</el-descriptions-item>
        <el-descriptions-item label="签到人">{{ currentCheckin.elderName }}</el-descriptions-item>
        <el-descriptions-item label="签到时间">{{ currentCheckin.checkinTime }}</el-descriptions-item>
        <el-descriptions-item label="签到状态">{{ currentCheckin.status }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ currentCheckin.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref([])
const dialogVisible = ref(false)
const currentCheckin = ref({})

// 获取签到列表
const getCheckins = async () => {
  // TODO: 调用后端API获取数据
  tableData.value = [
    {
      id: 1,
      activityName: '书法活动',
      elderName: '张三',
      checkinTime: '2024-01-20 14:30',
      status: '已签到',
      remark: '准时参加'
    }
  ]
}

// 查看签到详情
const handleView = (row) => {
  currentCheckin.value = { ...row }
  dialogVisible.value = true
}

// 删除签到记录
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该签到记录吗？', '提示', {
      type: 'warning'
    })
    // TODO: 调用后端API删除数据
    ElMessage.success('删除成功')
    getCheckins()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  getCheckins()
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