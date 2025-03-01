<template>
  <div class="user-management">
    <el-card>
      <div class="filter-container">
        <el-input
          v-model="listQuery.search"
          placeholder="搜索用户"
          style="width: 300px"
          @keyup.enter="getList"
        />
        <el-button type="primary" @click="getList">搜索</el-button>
        <el-button type="success" @click="handleCreate">新增用户</el-button>
      </div>

      <el-table
        :data="list"
        border
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="phone" label="电话" />
        <el-table-column prop="role" label="角色" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="mini" @click="handleEdit(row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-model:current-page="listQuery.page"
        v-model:page-size="listQuery.limit"
        :total="total"
        @pagination="getList"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Pagination from '@/components/admin/Pagination.vue'

const listQuery = ref({
  page: 1,
  limit: 20,
  search: ''
})

const list = ref([
  { id: 1, name: '张三', phone: '13800138000', role: '管理员' },
  { id: 2, name: '李四', phone: '13900139000', role: '普通用户' }
])

const total = ref(2)
const loading = ref(false)

const getList = () => {
  // 调用API获取数据
}

const handleCreate = () => {
  // 新增用户逻辑
}

const handleEdit = (row) => {
  // 编辑用户逻辑
}

const handleDelete = (row) => {
  // 删除用户逻辑
}
</script> 