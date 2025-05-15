# 字典管理页面
<template>
  <div class="app-container">
      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
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
          <el-select v-model="queryParams.status" placeholder="字典状态" clearable style="width: 200px">
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
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="RefreshRight"
            @click="handleRefreshCache"
          >刷新缓存</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <!-- 数据表格 -->
      <el-table v-loading="dictTypeStore.loading" :data="dictTypeStore.dictTypeList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="字典编号" align="center" prop="dictId" />
        <el-table-column label="字典名称" align="center" prop="dictName" />
        <el-table-column 
          label="字典类型" 
          align="center" 
          prop="dictType" 
          :show-overflow-tooltip="{ effect: 'light' }"
        >
          <template #default="scope">
            <router-link
              :to="`/admin/system/dict/data/type/${scope.row.dictType}`"
              class="link-type"
            >
              {{ scope.row.dictType }}
            </router-link>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="status">
          <template #default="scope">
            <dict-tag
              :options="statusOptions"
              :value="scope.row.status"
            />
          </template>
        </el-table-column>
        <el-table-column 
          label="备注" 
          align="center" 
          prop="remark" 
          :show-overflow-tooltip="{ effect: 'light' }"
        />
        <el-table-column 
          label="创建时间" 
          align="center" 
          prop="createTime" 
          width="180"
          :formatter="(row, column, cellValue) => formatDateTime(cellValue)"
        />
        <el-table-column label="操作" align="center" width="180">
          <template #default="scope">
            <el-button
              link
              icon="Edit"
              @click="handleUpdate(scope.row)"
            >修改</el-button>
            <el-button
              link
              icon="Delete"
              @click="handleDelete(scope.row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination
        v-show="dictTypeStore.dictTypeTotal > 0"
        :total="dictTypeStore.dictTypeTotal"
        :current="queryParams.current"
        :size="queryParams.size"
        @pagination="getList"
      />

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

<script setup name="Dict">
import { useDictTypeStore } from '@/stores/back/dictTypeStore'
import { formatDateTime } from '@/utils/date'
import { useDict } from '@/utils/dict'
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, getCurrentInstance, onMounted, reactive, ref, toRefs } from 'vue'
import { useRouter } from 'vue-router'

const dictTypeStore = useDictTypeStore()
const router = useRouter()

// 获取当前实例
const { proxy } = getCurrentInstance()

// 使用字典
const { normal_disable } = useDict('normal_disable')

// 状态选项
const statusOptions = computed(() => normal_disable.value || [])

const typeList = ref([])
// 是否显示弹出层
const open = ref(false)
// 是否显示loading
const loading = ref(true)
// 是否显示搜索区域
const showSearch = ref(true)
// 选中数组
const ids = ref([])
// 非单个禁用
const single = ref(true)
// 非多个禁用
const multiple = ref(true)
// 总条数
const total = ref(0)
// 弹出层标题
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    current: 1, //当前页
    size: 10, //每页条数
    dictName: undefined, //字典名称
    dictType: undefined, //字典类型
    status: undefined //状态
  },
  rules: {
    dictName: [{ required: true, message: "字典名称不能为空", trigger: "blur" }],
    dictType: [{ required: true, message: "字典类型不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

// 获取字典类型列表
const getList = async (val) => {
  loading.value = true;
  // 如果有分页参数传递过来，更新分页信息
  if (val) {
    queryParams.value.current = val.current;
    queryParams.value.size = val.size;
  }
  await dictTypeStore.fetchDictTypeList(queryParams.value).then(response => {
    typeList.value = response.records;
    total.value = response.total;
    loading.value = false;
  })
}

// 取消按钮
const cancel = () => {
  open.value = false
  reset()
}

// 表单重置
const reset = () => {
  form.value = {
    dictId: undefined,
    dictName: undefined,
    dictType: undefined,
    status: 1,
    remark: undefined
  }
  proxy.$refs['dictRef']?.resetFields()
}

// 搜索按钮操作
const handleQuery = () => {
  queryParams.current = 1
  getList();
}

// 重置按钮操作
const resetQuery = () => {
  queryParams.dictName = undefined
  queryParams.dictType = undefined
  queryParams.status = undefined
  handleQuery()
}

// 多选框选中数据
const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => item.dictId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

// 新增按钮操作
const handleAdd = () => {
  reset()
  open.value = true
  title.value = "添加字典类型"
}

// 修改按钮操作
const handleUpdate = (row) => {
  reset()
  const dictId = row.dictId || ids.value[0]
  dictTypeStore.fetchDictTypeDetail(dictId).then(response => {
    if (response.code === 200 && response.data) {
      // 使用 Object.assign 确保所有字段都被正确赋值
      Object.assign(form.value, response.data)
      open.value = true
      title.value = "修改字典类型"
    } else {
      ElMessage.error(response.message || "获取字典类型详情失败")
    }
  }).catch(error => {
    console.error('获取字典类型详情失败:', error)
    ElMessage.error("获取字典类型详情失败")
  })
}

// 提交按钮
const submitForm = async () => {
  proxy.$refs['dictRef'].validate(async (valid) => {
    if (valid) {
      try {
        if (form.value.dictId != undefined) {
          await dictTypeStore.updateDictTypeInfo(form.value)
          ElMessage.success("修改成功")
        } else {
          await dictTypeStore.createNewDictType(form.value)
          ElMessage.success("新增成功")
        }
        open.value = false
        getList()
      } catch (error) {
        console.error('提交表单失败:', error)
        ElMessage.error(error.message || "操作失败")
      }
    }
  })
}

// 删除按钮操作
const handleDelete = (row) => {
  const dictIds = row.dictId || ids.value
  ElMessageBox.confirm('是否确认删除字典编号为"' + dictIds + '"的数据项?', "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(async () => {
    await dictTypeStore.removeDictType(dictIds)
    getList()
    ElMessage.success("删除成功")
  })
}

// 导出按钮操作
const handleExport = () => {
  dictTypeStore.exportDictType(queryParams)
}

// 刷新缓存按钮操作
const handleRefreshCache = async () => {
  await dictTypeStore.refreshDictTypeCache()
  ElMessage.success("刷新成功")
}

// 字典数据按钮操作
const handleData = (row) => {
  router.push(`/system/dict/data/type/${row.dictType}`)
}

// 状态修改
const handleStatusChange = async (row) => {
  const text = row.status === 1 ? "启用" : "停用"
  try {
    await dictTypeStore.updateDictTypeStatus(row.dictId, row.status)
    ElMessage.success(text + "成功")
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1
  }
}

// 分页大小改变
const handleSizeChange = (val) => {
  queryParams.size = val
  getList()
}

// 页码改变
const handleCurrentChange = (val) => {
  queryParams.current = val
  getList()
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