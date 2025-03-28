# 字典数据页面
<template>
  <div class="dict-data-container">
    <el-card class="box-card">
      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryRef" :inline="true">
        <el-form-item label="字典标签" prop="dictLabel">
          <el-input
            v-model="queryParams.dictLabel"
            placeholder="请输入字典标签"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="字典状态" clearable>
            <el-option
              v-for="dict in statusOptions"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
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
      </el-row>

      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="dictDataList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="字典编码" align="center" prop="dictCode" />
        <el-table-column label="字典标签" align="center" prop="dictLabel" />
        <el-table-column label="字典键值" align="center" prop="dictValue" />
        <el-table-column label="字典排序" align="center" prop="dictSort" />
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
        <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
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

    <!-- 添加或修改字典数据对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="dictDataRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="字典标签" prop="dictLabel">
          <el-input v-model="form.dictLabel" placeholder="请输入字典标签" />
        </el-form-item>
        <el-form-item label="字典键值" prop="dictValue">
          <el-input v-model="form.dictValue" placeholder="请输入字典键值" />
        </el-form-item>
        <el-form-item label="字典排序" prop="dictSort">
          <el-input-number v-model="form.dictSort" :min="0" />
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
import { useRouter, useRoute } from 'vue-router'
import { 
  getDictDataList, 
  getDictData, 
  addDictData, 
  updateDictData, 
  deleteDictData, 
  changeDictDataStatus,
  getDictType
} from '@/api/back/system/dict'

const router = useRouter()
const route = useRoute()

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
// 字典数据表格数据
const dictDataList = ref([])
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
  dictType: undefined,
  dictLabel: undefined,
  status: undefined
})

// 表单参数
const form = reactive({
  dictCode: undefined,
  dictType: undefined,
  dictLabel: undefined,
  dictValue: undefined,
  dictSort: 0,
  status: 1,
  remark: undefined
})

// 表单校验
const rules = {
  dictLabel: [
    { required: true, message: "字典标签不能为空", trigger: "blur" }
  ],
  dictValue: [
    { required: true, message: "字典键值不能为空", trigger: "blur" }
  ]
}

/** 查询字典数据列表 */
function getList() {
  loading.value = true
  getDictDataList(queryParams).then(res => {
    if (res.data) {
      dictDataList.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      dictDataList.value = []
      total.value = 0
    }
    loading.value = false
  }).catch(error => {
    console.error('获取字典数据列表失败:', error)
    dictDataList.value = []
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
  form.dictCode = undefined
  form.dictLabel = undefined
  form.dictValue = undefined
  form.dictSort = 0
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
  queryParams.dictLabel = undefined
  queryParams.status = undefined
  handleQuery()
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加字典数据"
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.dictCode)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const dictCode = row.dictCode || ids.value[0]
  getDictData(dictCode).then(res => {
    if (res.data) {
      Object.assign(form, res.data)
      open.value = true
      title.value = "修改字典数据"
    }
  })
}

/** 提交按钮 */
function submitForm() {
  const dictDataRef = ref()
  dictDataRef.value?.validate((valid) => {
    if (valid) {
      if (form.dictCode) {
        updateDictData(form).then(() => {
          ElMessage.success('修改成功')
          open.value = false
          getList()
        })
      } else {
        addDictData(form).then(() => {
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
  const dictCodes = row.dictCode || ids.value
  ElMessageBox.confirm('是否确认删除字典编码为"' + dictCodes + '"的数据项?', "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    deleteDictData(dictCodes).then(() => {
      ElMessage.success('删除成功')
      getList()
    })
  })
}

/** 导出按钮操作 */
function handleExport() {
  // 实现导出功能
}

/** 状态修改 */
function handleStatusChange(row) {
  const text = row.status === 1 ? '启用' : '停用'
  ElMessageBox.confirm('确认要"' + text + '""' + row.dictLabel + '"字典数据吗?', "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    changeDictDataStatus({
      dictCode: row.dictCode,
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

/** 获取字典类型 */
function getDictTypeInfo() {
  const dictId = route.params.dictId
  getDictType(dictId).then(res => {
    if (res.data) {
      queryParams.dictType = res.data.dictType
      getList()
    }
  })
}

onMounted(() => {
  getDictTypeInfo()
})
</script>

<style scoped>
.dict-data-container {
  padding: 10px;
}
.mb8 {
  margin-bottom: 8px;
}
</style>