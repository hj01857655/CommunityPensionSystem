# 字典管理页面
<template>
  <div class="dict-container">
    <el-card class="box-card">
      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryRef" :inline="true">
        <el-form-item label="字典名称" prop="dictName">
          <el-input
            v-model="queryParams.dictName"
            placeholder="请输入字典名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="字典类型" prop="dictType">
          <el-input
            v-model="queryParams.dictType"
            placeholder="请输入字典类型"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="字典状态" clearable>
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮区域 -->
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="Plus"
            @click="handleAdd"
          >新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="success"
            plain
            icon="Edit"
            :disabled="single"
            @click="handleUpdate"
          >修改</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="danger"
            plain
            icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
          >删除</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="warning"
            plain
            icon="Download"
            @click="handleExport"
          >导出</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="RefreshRight"
            @click="handleRefreshCache"
          >刷新缓存</el-button>
        </el-col>
      </el-row>

      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="dictList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="字典编号" align="center" prop="dictId" />
        <el-table-column label="字典名称" align="center" prop="dictName" />
        <el-table-column label="字典类型" align="center" prop="dictType" :show-overflow-tooltip="true">
          <template #default="scope">
            <router-link 
              :to="`/admin/system/dict/type/${scope.row.dictId}`"
              class="link-type"
            >
              {{ scope.row.dictType }}
            </router-link>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="status">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
        <el-table-column 
          label="创建时间" 
          align="center" 
          prop="createTime" 
          width="180"
          :formatter="(row, column, cellValue) => formatDate(cellValue)"
        />
        <el-table-column label="操作" align="center" width="180">
          <template #default="scope">
            <el-button
              type="link"
              icon="Edit"
              @click="handleUpdate(scope.row)"
            >修改</el-button>
            <el-button
              type="link"
              icon="Delete"
              @click="handleDelete(scope.row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-show="total > 0"
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 30, 40]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 添加或修改字典类型对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="dictRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="字典名称" prop="dictName">
          <el-input v-model="form.dictName" placeholder="请输入字典名称" />
        </el-form-item>
        <el-form-item label="字典类型" prop="dictType">
          <el-input v-model="form.dictType" placeholder="请输入字典类型" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { formatDate } from '@/utils/date'
import { 
  getDictTypeList, 
  getDictType, 
  addDictType, 
  updateDictType, 
  deleteDictType, 
  changeDictTypeStatus,
  refreshCache 
} from '@/api/back/system/dict'

const router = useRouter()

// 遮罩层
const loading = ref(false)
// 选中数组
const ids = ref([])
// 非单个禁用
const single = ref(true)
// 非多个禁用
const multiple = ref(true)
// 总条数
const total = ref(0)
// 字典表格数据
const dictList = ref([])
// 弹出层标题
const title = ref("")
// 是否显示弹出层
const open = ref(false)
// 状态数据字典
const statusOptions = [
  { label: '正常', value: 1 },
  { label: '停用', value: 0 }
]

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  dictName: undefined,
  dictType: undefined,
  status: undefined
})

// 表单参数
const form = reactive({
  dictId: undefined,
  dictName: undefined,
  dictType: undefined,
  status: 1,
  remark: undefined
})

// 表单校验
const rules = {
  dictName: [
    { required: true, message: "字典名称不能为空", trigger: "blur" }
  ],
  dictType: [
    { required: true, message: "字典类型不能为空", trigger: "blur" }
  ]
}

/** 查询字典类型列表 */
function getList() {
  loading.value = true
  console.log('开始获取字典类型列表，参数：', queryParams)
  getDictTypeList(queryParams).then(res => {
    console.log('获取字典类型列表响应：', res)
    if (res.data) {
      dictList.value = res.data.records || []
      total.value = res.data.total || 0
      console.log('设置字典类型列表数据：', dictList.value)
      console.log('设置总数：', total.value)
    } else {
      console.log('响应数据格式不正确：', res)
      dictList.value = []
      total.value = 0
    }
    loading.value = false
  }).catch(error => {
    console.error('获取字典类型列表失败:', error)
    dictList.value = []
    total.value = 0
    loading.value = false
  })
}

/** 取消按钮 */
function cancel() {
  open.value = false
  reset()
}

/** 表单重置 */
function reset() {
  form.dictId = undefined
  form.dictName = undefined
  form.dictType = undefined
  form.status = 1
  form.remark = undefined
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  queryParams.dictName = undefined
  queryParams.dictType = undefined
  queryParams.status = undefined
  handleQuery()
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加字典类型"
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.dictId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

/** 修改按钮操作 */
function handleUpdate(row) {
  loading.value = true
  reset()
  const dictId = row.dictId || ids.value[0]
  getDictType(dictId).then(res => {
    if (res.data) {
      Object.assign(form, res.data)
      open.value = true
      title.value = "修改字典类型"
    }
  }).finally(() => {
    loading.value = false
  })
}

/** 提交按钮 */
function submitForm() {
  const dictRef = ref()
  dictRef.value?.validate((valid) => {
    if (valid) {
      if (form.dictId) {
        updateDictType(form).then(() => {
          ElMessage.success('修改成功')
          open.value = false
          getList()
        })
      } else {
        addDictType(form).then(() => {
          ElMessage.success('新增成功')
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const dictIds = row.dictId || ids.value
  ElMessageBox.confirm('是否确认删除字典编号为"' + dictIds + '"的数据项?', "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    deleteDictType(dictIds).then(() => {
      ElMessage.success('删除成功')
      getList()
    })
  })
}

/** 导出按钮操作 */
function handleExport() {
  // 实现导出功能
}

/** 字典数据按钮操作 */
function handleData(row) {
  router.push(`/admin/system/dict/type/${row.dictId}`)
}

/** 状态修改 */
function handleStatusChange(row) {
  const text = row.status === 1 ? '启用' : '停用'
  ElMessageBox.confirm('确认要"' + text + '""' + row.dictName + '"字典类型吗?', "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    changeDictTypeStatus({
      dictId: row.dictId,
      status: row.status
    }).then(() => {
      ElMessage.success(text + '成功')
    })
  }).catch(() => {
    row.status = row.status === 1 ? 0 : 1
  })
}

/** 分页大小改变 */
function handleSizeChange(val) {
  queryParams.pageSize = val
  getList()
}

/** 分页页码改变 */
function handleCurrentChange(val) {
  queryParams.pageNum = val
  getList()
}

/** 刷新缓存操作 */
function handleRefreshCache() {
  ElMessageBox.confirm('是否确认刷新字典缓存?', "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    refreshCache().then(() => {
      ElMessage.success('刷新缓存成功')
    })
  })
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.dict-container {
  padding: 10px;
}
.mb8 {
  margin-bottom: 8px;
}
.link-type {
  color: var(--el-color-primary);
  text-decoration: none;
}
.link-type:hover {
  color: var(--el-color-primary-light-3);
}
</style>