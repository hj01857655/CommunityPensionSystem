# 字典数据页面
<template>
  <div class="dict-data-container">
    <el-card class="box-card">
      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryRef" :inline="true">
        <el-form-item label="字典类型" prop="dictType">
          <el-select 
            v-model="queryParams.dictType" 
            placeholder="请选择字典类型" 
            clearable 
            style="width:200px"
            @change="handleQuery"
          >
            <el-option
              v-for="dict in dictTypeOptions"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="字典标签" prop="dictLabel">
          <el-input
            v-model="queryParams.dictLabel"
            placeholder="请输入字典标签"
            clearable
            @keyup.enter="handleQuery"
            @input="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select 
            v-model="queryParams.status" 
            placeholder="字典状态" 
            clearable 
            style="width:200px"
            @change="handleQuery"
          >
            <el-option label="全部" :value="''" />
            <el-option
              v-for="dict in normal_disable"
              :key="dict.value"
              :label="dict.label"
              :value="String(dict.value)"
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
            type="info"
            plain
            icon="Close"
            @click="goBack"
          >关闭</el-button>
        </el-col>
      </el-row>

      <!-- 数据表格 -->
      <el-table v-loading="dictDataStore.loading" :data="dictDataStore.dictDataList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="字典编码" align="center" prop="dictCode" />
        <el-table-column label="字典标签" align="center" prop="dictLabel" />
        <el-table-column label="字典键值" align="center" prop="dictValue" />
        <el-table-column label="字典排序" align="center" prop="dictSort" />
        <el-table-column label="状态" align="center" prop="status">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="0"
              :inactive-value="1"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="{ effect: 'light' }" />
        <el-table-column label="创建时间" align="center" prop="createTime" width="180">
          <template #default="scope">
            {{ scope.row.createTime ? formatDateDetail(scope.row.createTime) : '' }}
          </template>
        </el-table-column>
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
      <el-pagination
        v-show="dictDataStore.dictDataTotal > 0"
        v-model:current-page="queryParams.current"
        v-model:page-size="queryParams.size"
        :page-sizes="[10, 20, 30, 40]"
        :total="dictDataStore.dictDataTotal"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 添加或修改字典数据对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="DataRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="字典类型" prop="dictType">
          <el-input v-model="form.dictType" :disabled="true" />
        </el-form-item>
        <el-form-item label="数据标签" prop="dictLabel">
          <el-input v-model="form.dictLabel" placeholder="请输入数据标签" />
        </el-form-item>
        <el-form-item label="数据键值" prop="dictValue">
          <el-input v-model="form.dictValue" placeholder="请输入数据键值" />
        </el-form-item>
        <el-form-item label="样式属性" prop="cssClass">
          <el-input v-model="form.cssClass" placeholder="请输入样式属性" />
        </el-form-item>
        <el-form-item label="显示排序" prop="dictSort">
          <el-input-number v-model="form.dictSort" :min="0" />
        </el-form-item>
        <el-form-item label="回显样式" prop="listClass">
          <el-select v-model="form.listClass" placeholder="请选择回显样式">
            <el-option label="主要(primary)" value="primary" />
            <el-option label="成功(success)" value="success" />
            <el-option label="信息(info)" value="info" />
            <el-option label="警告(warning)" value="warning" />
            <el-option label="危险(danger)" value="danger" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="0">正常</el-radio>
            <el-radio :value="1">停用</el-radio>
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
import { useDictDataStore } from '@/stores/back/dictDataStore'
import { useDictTypeStore } from '@/stores/back/dictTypeStore'
import { formatDateDetail } from '@/utils/date'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCurrentInstance, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
const { proxy } = getCurrentInstance()
const router = useRouter()
const route = useRoute()
const dictDataStore = useDictDataStore()
const dictTypeStore = useDictTypeStore()

// 表单引用
const DataRef = ref()

// 使用字典
const { normal_disable } = proxy.useDict('normal_disable')

// 选中数组
const ids = ref([])
// 非单个禁用
const single = ref(true)
// 非多个禁用
const multiple = ref(true)
// 弹出层标题
const title = ref("")
// 是否显示弹出层
const open = ref(false)
// 状态数据字典
const statusOptions = ref([
  { value: 0, label: "正常" },
  { value: 1, label: "停用" }
])
// 字典类型选项
const dictTypeOptions = ref([])
// 当前选中的字典类型
const currentDictType = ref('')

// 查询参数
const queryParams = reactive({
  current: 1,
  size: 10,
  dictType: route.params.dictType,
  dictLabel: undefined,
  status: '' // 默认查全部
})

// 表单参数
const form = reactive({
  dictCode: undefined,
  dictType: route.params.dictType,
  dictLabel: undefined,
  dictValue: undefined,
  cssClass: undefined,
  listClass: undefined,
  dictSort: 0,
  status: 0,
  remark: undefined
})

// 获取字典数据列表
const getList = async (val) => {
  // 如果有分页参数传递过来，更新分页信息
  if (val) {
    queryParams.current = val.current;
    queryParams.size = val.size;
  }
  await dictDataStore.fetchDictDataList(queryParams)
  // 添加调试日志
  console.log('字典数据列表:', dictDataStore.dictDataList)
}

// 获取字典类型选项
const getDictTypeOptions = async () => {
  try {
    const response = await dictTypeStore.fetchDictTypeList()
    if (response.code === 200) {
      dictTypeOptions.value = response.data.records.map(item => ({
        label: item.dictName,
        value: item.dictType
      }))
      // 如果有路由参数，设置选中值
      if (route.params.dictType) {
        queryParams.dictType = route.params.dictType
        form.dictType = route.params.dictType
      }
    }
  } catch (error) {
    console.error('获取字典类型选项失败:', error)
    ElMessage.error('获取字典类型选项失败')
  }
}

// 获取字典类型名称
const getDictTypeName = async () => {
  try {
    const response = await dictTypeStore.fetchDictTypeList({ pageSize: 100 })
    if (response.code === 200) {
      const dictType = response.data.records.find(item => item.dictType === route.params.dictType)
      if (dictType) {
        currentDictType.value = dictType.dictName
      }
    }
  } catch (error) {
    console.error('获取字典类型名称失败:', error)
  }
}

// 初始化
const init = async () => {
  await getDictTypeOptions()
  await getList()
}

onMounted(() => {
  if (!route.params.dictType) {
    // 如果没有 dictType 参数，返回字典类型列表页
    router.push('/admin/system/dict')
    return
  }
  init()
})

// 监听路由参数变化
watch(
  () => route.params.dictType,
  async (newDictType) => {
    if (newDictType) {
      queryParams.dictType = newDictType
      form.dictType = newDictType
      await getList()
    }
  }
)

// 表单校验
const rules = {
  dictLabel: [
    { required: true, message: "数据标签不能为空", trigger: "blur" }
  ],
  dictValue: [
    { required: true, message: "数据键值不能为空", trigger: "blur" }
  ],
  dictSort: [
    { required: true, message: "显示排序不能为空", trigger: "blur" }
  ]
}

// 取消按钮
const cancel = () => {
  open.value = false
  reset()
}

// 表单重置
const reset = () => {
  form.dictCode = undefined
  form.dictType = route.params.dictType  // 重置时保持当前字典类型
  form.dictLabel = undefined
  form.dictValue = undefined
  form.cssClass = undefined
  form.listClass = undefined
  form.dictSort = 0
  form.status = 0
  form.remark = undefined
  if (DataRef.value) {
    DataRef.value.resetFields()
  }
}

// 搜索按钮操作
const handleQuery = () => {
  queryParams.current = 1
  getList()
}

// 重置按钮操作
const resetQuery = () => {
  queryParams.dictType = route.params.dictType  // 重置时保持当前字典类型
  queryParams.dictLabel = undefined
  queryParams.status = ''
  handleQuery()
}

// 返回按钮操作
const goBack = () => {
  router.push('/admin/system/dict')
}

// 多选框选中数据
const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => item.dictCode)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

// 新增按钮操作
const handleAdd = () => {
  reset()
  open.value = true
  title.value = "添加字典数据"
}

// 修改按钮操作
const handleUpdate = (row) => {
  reset()
  const dictCode = row.dictCode || ids.value[0]
  dictDataStore.fetchDictDataDetail(dictCode).then(response => {
    Object.assign(form, response.data)
    open.value = true
    title.value = "修改字典数据"
  })
}

// 提交按钮
const submitForm = async () => {
  dictDataRef.value?.validate(async (valid) => {
    if (valid) {
      if (form.dictCode) {
        await dictDataStore.updateDictDataInfo(form)
        ElMessage.success("修改成功")
      } else {
        await dictDataStore.createNewDictData(form)
        ElMessage.success("新增成功")
      }
      open.value = false
      getList()
      ElMessage.success("操作成功")
    }
  })
}

// 删除按钮操作
const handleDelete = (row) => {
  const dictCodes = row.dictCode || ids.value
  ElMessageBox.confirm('是否确认删除字典编码为"' + dictCodes + '"的数据项?', "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(async () => {
    await dictDataStore.removeDictData(dictCodes)
    getList()
    ElMessage.success("删除成功")
  })
}

// 导出按钮操作
const handleExport = () => {
  dictDataStore.exportDictDataList(queryParams)
}

// 状态修改
const handleStatusChange = async (row) => {
  const text = row.status === 0 ? "启用" : "停用"
  try {
    await dictDataStore.updateDictDataStatus(row.dictCode, row.status)
    ElMessage.success(text + "成功")
  } catch (error) {
    // 发生错误时恢复原状态
    row.status = row.status === 0 ? 1 : 0
    ElMessage.error(error.message || text + "失败")
    console.error('更新状态失败:', error)
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
</script>

<style scoped>
.dict-data-container {
  padding: 10px;
}
.mb8 {
  margin-bottom: 8px;
}
</style>