<template>
  <div class="app-container">
    <!-- 搜索条件 -->
    <el-form :model="activityStore.queryParams" ref="searchFormRef" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="活动标题" prop="title">
        <el-input v-model="activityStore.queryParams.title" placeholder="请输入活动标题" clearable style="width: 200px" @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="活动状态" prop="status">
        <el-select v-model="activityStore.queryParams.status" placeholder="请选择活动状态" clearable style="width: 200px">
          <el-option label="未开始" :value="0" />
          <el-option label="报名中" :value="1" />
          <el-option label="进行中" :value="2" />
          <el-option label="已结束" :value="3" />
          <el-option label="已取消" :value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="活动类型" prop="type">
        <el-select v-model="activityStore.queryParams.type" placeholder="请选择活动类型" clearable style="width: 200px">
          <el-option v-for="item in activityTypes" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="活动日期" prop="dateRange">
        <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" style="width: 240px" value-format="YYYY-MM-DD" @change="handleDateRangeChange" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain :icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain :icon="Delete" @click="handleBatchDelete" :disabled="!selectedRows.length">
          批量删除
          <span v-if="selectedRows.length">({{ selectedRows.length }})</span>
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain :icon="Download" @click="handleExport">导出</el-button>
      </el-col>
      <el-col :span="12" class="text-right">
        <el-tooltip content="自动刷新" placement="top">
          <el-switch
            v-model="isAutoRefresh"
            inline-prompt
            :active-text="autoRefreshTime + 's'"
            inactive-text="关闭"
            @change="toggleAutoRefresh"
            style="margin-right: 10px;"
          />
        </el-tooltip>
        <el-dropdown @command="changeRefreshTime" trigger="click">
          <el-button link>
            <el-icon><ArrowDown /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item :command="10">10秒</el-dropdown-item>
              <el-dropdown-item :command="30">30秒</el-dropdown-item>
              <el-dropdown-item :command="60">1分钟</el-dropdown-item>
              <el-dropdown-item :command="300">5分钟</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns" :showAdd="false"></right-toolbar>
    </el-row>

    <!-- 列设置下拉菜单 -->
    <el-dropdown trigger="click" @command="handleColumnCommand">
      <el-button type="primary" class="column-settings-btn">
        列设置<el-icon class="el-icon--right"><ArrowDown /></el-icon>
      </el-button>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item v-for="column in columns" :key="column.key" :command="column.key">
            <el-checkbox v-model="column.visible" @click.stop>{{ column.label }}</el-checkbox>
          </el-dropdown-item>
          <el-dropdown-item divided>
            <el-button link @click="resetColumnSettings">重置</el-button>
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>

    <!-- 活动列表 -->
    <el-table
      v-loading="activityStore.loading"
      :data="activityStore.activityList"
      @selection-change="handleSelectionChange"
      @sort-change="handleSortChange"
      @filter-change="handleFilterChange"
      :row-key="row => row.id"
      :row-class-name="tableRowClassName"
      :header-cell-style="{background: '#f5f7fa', color: '#606266'}"
      border
      stripe
      highlight-current-row
      @row-contextmenu="handleContextMenu"
      style="width: 100%"
      v-el-table-infinite-scroll="loadMore"
    >
      <template v-for="column in columns" :key="column.key">
        <!-- 选择列 -->
        <el-table-column 
          v-if="column.key === 0 && column.visible"
          type="selection" 
          :width="column.width" 
          :align="column.align || 'center'"
          :fixed="column.fixed"
        />
        
        <!-- 状态列 -->
        <el-table-column 
          v-else-if="column.key === 3 && column.visible"
          :prop="column.prop" 
          :label="column.label" 
          :width="column.width" 
          :align="column.align || 'center'"
          :sortable="column.sortable"
          :filters="column.filterOptions"
          :column-key="column.prop"
          :filter-multiple="column.filterOptions?.length > 0"
          :fixed="column.fixed"
        >
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        
        <!-- 操作列 -->
        <el-table-column 
          v-else-if="column.key === 9 && column.visible"
          :label="column.label" 
          :width="column.width" 
          :fixed="column.fixed"
          class-name="small-padding fixed-width"
        >
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="handleView(row)">查看</el-button>
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-dropdown @command="(command) => handleStatusCommand(command, row)">
              <el-button type="primary" link>
                状态<el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-if="row.status === 0" command="start">开始报名</el-dropdown-item>
                  <el-dropdown-item v-if="row.status === 1" command="end">开始活动</el-dropdown-item>
                  <el-dropdown-item v-if="row.status === 2" command="finish">结束活动</el-dropdown-item>
                  <el-dropdown-item v-if="row.status < 3" command="cancel">取消活动</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <!-- 其它普通列 -->
        <el-table-column
          v-else-if="column.visible"
          :prop="column.prop"
          :label="column.label"
          :width="column.width"
          :align="column.align || 'center'"
          :sortable="column.sortable"
          :fixed="column.fixed"
        />
      </template>
    </el-table>

    <!-- 分页组件 -->
    <pagination v-if="activityStore.total > 0" :total="activityStore.total" :page="activityStore.queryParams.pageNum" :limit="activityStore.queryParams.pageSize" @pagination="handlePagination" />

    <!-- 活动表单对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '新增活动' : dialogType === 'edit' ? '编辑活动' : '活动详情'" width="700px" append-to-body destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px" :disabled="dialogType === 'view'">
        <el-form-item label="活动标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入活动标题" />
        </el-form-item>
        <el-form-item label="活动类型" prop="type">
          <el-select v-model="formData.type" placeholder="请选择活动类型" style="width: 100%">
            <el-option v-for="item in activityTypes" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="活动时间" prop="timeRange">
          <el-date-picker
            v-model="formData.timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            style="width: 100%"
            value-format="YYYY-MM-DDTHH:mm:ss"
            :disabled-date="disabledDate"
          />
        </el-form-item>
        <el-form-item label="活动地点" prop="location">
          <el-input v-model="formData.location" placeholder="请输入活动地点" />
        </el-form-item>
        <el-form-item label="人数上限" prop="maxParticipants">
          <el-input-number v-model="formData.maxParticipants" :min="1" :max="1000" style="width: 100%" />
        </el-form-item>
        <el-form-item label="活动图片" prop="image">
          <el-upload class="avatar-uploader" action="/api/upload" :show-file-list="false" :on-success="handleImageChange" :before-upload="(file) => file.type.startsWith('image/')">
            <img v-if="formData.image" :src="formData.image" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><component :is="Plus" /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="活动描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="4" placeholder="请输入活动描述" />
        </el-form-item>
        <el-form-item label="活动状态" prop="status" v-if="dialogType === 'edit' || dialogType === 'view'">
          <el-select v-model="formData.status" placeholder="请选择活动状态" style="width: 100%" v-if="dialogType === 'edit'">
            <el-option label="未开始" :value="0" />
            <el-option label="报名中" :value="1" />
            <el-option label="进行中" :value="2" />
            <el-option label="已结束" :value="3" />
            <el-option label="已取消" :value="4" />
          </el-select>
          <el-tag v-else :type="getStatusType(formData.status)">{{ getStatusText(formData.status) }}</el-tag>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" v-if="dialogType !== 'view'">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import RightToolbar from '@/components/common/base/RightToolbar/index.vue';
import Pagination from '@/components/common/table/Pagination.vue';
import { useActivityStore } from '@/stores/back/activityStore';
import { useDict } from '@/utils/dict';
import { ArrowDown, Delete, Download, Edit, Plus, Refresh, Search, View } from '@element-plus/icons-vue';
import { ElDropdown, ElDropdownItem, ElDropdownMenu, ElMessage, ElMessageBox, ElTooltip } from 'element-plus';
import { nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue';
const activityStore = useActivityStore();

// 注册表格无限滚动指令
const vElTableInfiniteScroll = {
  mounted(el, binding) {
    const scrollWrap = el.querySelector('.el-table__body-wrapper')
    const scrollLoad = () => {
      const scrollDistance = scrollWrap.scrollHeight - scrollWrap.scrollTop - scrollWrap.clientHeight
      if (scrollDistance <= 50) {
        binding.value()
      }
    }
    scrollWrap.addEventListener('scroll', scrollLoad)
    el._scrollLoad = scrollLoad
  },
  unmounted(el) {
    const scrollWrap = el.querySelector('.el-table__body-wrapper')
    if (scrollWrap && el._scrollLoad) {
      scrollWrap.removeEventListener('scroll', el._scrollLoad)
    }
  }
}


import { useRouter } from 'vue-router';

const router = useRouter()
const searchFormRef = ref()
const formRef = ref()
const showSearch = ref(true)
const dialogVisible = ref(false)
const dialogType = ref('add') // add, edit, view
const dateRange = ref([])
const selectedRows = ref([])
const loading = ref(false)

// 表格列控制
const columns = ref([
  { 
    key: 0, 
    label: '选择', 
    prop: 'selection',
    visible: true, 
    fixed: 'left',
    width: 50,
    sortable: false,
    filterable: false
  },
  { 
    key: 1, 
    label: '活动标题', 
    prop: 'title',
    visible: true, 
    minWidth: 200,
    sortable: 'custom',
    filterable: true,
    filterOptions: []
  },
  { 
    key: 2, 
    label: '活动类型', 
    prop: 'type',
    visible: true, 
    width: 120,
    sortable: 'custom',
    filterable: true,
    filterOptions: []
  },
  { 
    key: 3, 
    label: '活动状态', 
    prop: 'status',
    visible: true, 
    width: 100,
    sortable: 'custom',
    filterable: true,
    filterOptions: [
      { text: '未开始', value: 0 },
      { text: '报名中', value: 1 },
      { text: '进行中', value: 2 },
      { text: '已结束', value: 3 },
      { text: '已取消', value: 4 }
    ]
  },
  { 
    key: 4, 
    label: '开始时间', 
    prop: 'startTime',
    visible: true, 
    width: 160,
    sortable: 'custom',
    filterable: false
  },
  { 
    key: 5, 
    label: '结束时间', 
    prop: 'endTime',
    visible: true, 
    width: 160,
    sortable: 'custom',
    filterable: false
  },
  { 
    key: 6, 
    label: '活动地点', 
    prop: 'location',
    visible: true, 
    minWidth: 150,
    sortable: 'custom',
    filterable: true,
    filterOptions: []
  },
  { 
    key: 7, 
    label: '人数上限', 
    prop: 'maxParticipants',
    visible: true, 
    width: 100,
    sortable: 'custom',
    filterable: false,
    align: 'center'
  },
  { 
    key: 9, 
    label: '操作', 
    prop: 'actions',
    visible: true, 
    width: 200,
    fixed: 'right',
    sortable: false,
    filterable: false
  }
])

// 活动类型选项
const activityTypes = ref([
  { value: '1', label: '文化娱乐' },
  { value: '2', label: '健康讲座' },
  { value: '3', label: '志愿服务' },
  { value: '4', label: '户外活动' },
  { value: '5', label: '节日庆祝' },
  { value: '6', label: '其他活动' }
])

// 表单数据
const formData = reactive({
  id: undefined,
  title: '',
  type: '',
  timeRange: [],
  startTime: '',
  endTime: '',
  location: '',
  maxParticipants: 50,
  // 移除 currentParticipants 和 image 字段，因为后端不支持
  description: '',
  status: 0,
  organizerId: parseInt(sessionStorage.getItem('userId')) || 1 // 从会话存储中获取当前用户ID作为组织者ID
})

// 表单验证规则
const formRules = {
  title: [
    { required: true, message: '请输入活动标题', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择活动类型', trigger: 'change' }
  ],
  timeRange: [
    { required: true, message: '请选择活动时间', trigger: 'change' }
  ],
  location: [
    { required: true, message: '请输入活动地点', trigger: 'blur' }
  ],
  maxParticipants: [
    { required: true, message: '请输入人数上限', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入活动描述', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择活动状态', trigger: 'change' }
  ]
}

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now()
}

// 获取活动列表
const getList = () => {
  activityStore.fetchActivityList()
}

// 处理日期范围变化
const handleDateRangeChange = (val) => {
  if (val) {
    activityStore.queryParams.startDate = val[0]
    activityStore.queryParams.endDate = val[1]
  } else {
    activityStore.queryParams.startDate = undefined
    activityStore.queryParams.endDate = undefined
  }
}

// 搜索
const handleSearch = async () => {
  // 确保活动类型数据已加载
  if (!activityTypes.value || activityTypes.value.length === 0) {
    await loadActivityTypes()
  }
  
  activityStore.queryParams.pageNum = 1
  getList()
}

// 重置
const handleReset = () => {
  searchFormRef.value?.resetFields()
  dateRange.value = []
  activityStore.resetQueryParams()
  handleSearch()
}

// 多选框选中数据
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 加载活动类型数据
const loadActivityTypes = async () => {
  try {
    // 使用 useDict 函数获取字典数据
    const { activity_type } = useDict('activity_type');
    
    // 等待字典数据加载
    await new Promise(resolve => setTimeout(resolve, 100));
    
    if (activity_type.value && activity_type.value.length > 0) {
      activityTypes.value = activity_type.value.map(item => ({
        value: item.value,
        label: item.label
      }));
      console.log('活动类型数据加载成功:', activityTypes.value);
      return true;
    } else {
      console.warn('获取活动类型数据为空，使用默认数据');
      // 使用默认数据
      activityTypes.value = [
        { value: '1', label: '文化娱乐' },
        { value: '2', label: '健康讲座' },
        { value: '3', label: '体育健身' },
        { value: '4', label: '志愿服务' },
        { value: '5', label: '节日庆祝' },
        { value: '6', label: '技能培训' },
        { value: '7', label: '社交联谊' },
        { value: '8', label: '公益慈善' },
        { value: '9', label: '其他活动' }
      ];
      return false;
    }
  } catch (error) {
    console.error('获取活动类型数据失败:', error);
    // 出错时使用默认数据
    activityTypes.value = [
      { value: '1', label: '文化娱乐' },
      { value: '2', label: '健康讲座' },
      { value: '3', label: '体育健身' },
      { value: '4', label: '志愿服务' },
      { value: '5', label: '节日庆祝' },
      { value: '6', label: '技能培训' },
      { value: '7', label: '社交联谊' },
      { value: '8', label: '公益慈善' },
      { value: '9', label: '其他活动' }
    ];
    return false;
  }
}

// 新增活动
const handleAdd = async () => {
  resetForm()
  
  // 确保活动类型数据已加载
  if (!activityTypes.value || activityTypes.value.length === 0) {
    await loadActivityTypes()
  }
  
  dialogType.value = 'add'
  dialogVisible.value = true
  // 注意：对话框标题直接在模板中通过三元表达式设置，无需在这里设置
}

// 查看活动
const handleView = (row) => {
  resetForm()
  dialogType.value = 'view'
  Object.assign(formData, row)
  if (row.startTime && row.endTime) {
    formData.timeRange = [row.startTime, row.endTime]
  }
  dialogVisible.value = true
}

// 编辑活动
const handleEdit = (row) => {
  resetForm()
  dialogType.value = 'edit'
  Object.assign(formData, row)
  if (row.startTime && row.endTime) {
    formData.timeRange = [row.startTime, row.endTime]
  }
  dialogVisible.value = true
}

// 删除活动
const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除活动"${row.title}"吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    loading.value = true
    // 直接删除活动
    activityStore.deleteActivity(row.id).then(() => {
      ElMessage.success('删除成功');
      getList();
    }).catch(error => {
      console.error('删除活动失败:', error);
      if (error.response && error.response.data && error.response.data.message) {
        ElMessage.error(error.response.data.message);
      } else {
        ElMessage.error('删除活动失败');
      }
    }).finally(() => {
      loading.value = false;
    });
  }).catch(() => {});
};

// 批量删除
const handleBatchDelete = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请至少选择一条记录')
    return
  }
  const ids = selectedRows.value.map(item => item.id)
  ElMessageBox.confirm(`确认删除选中的${selectedRows.value.length}条记录吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    loading.value = true
    let hasError = false
    let errorMessage = ''

    const deletePromises = ids.map(id =>
      activityStore.deleteActivity(id).catch(error => {
        hasError = true
        if (error.response && error.response.data && error.response.data.message) {
          errorMessage = error.response.data.message
        }
        // 返回一个成功的Promise，避免Promise.all中断
        return Promise.resolve(false)
      })
    )

    Promise.all(deletePromises).then(results => {
      const successCount = results.filter(result => result === true).length

      if (successCount === ids.length) {
        ElMessage.success('批量删除成功')
      } else if (successCount > 0) {
        ElMessage.warning(`部分删除成功，${successCount}/${ids.length}条记录删除成功`)
        if (errorMessage) {
          ElMessage.error(errorMessage)
        }
      } else {
        ElMessage.error(errorMessage || '批量删除失败')
      }

      getList()
    }).finally(() => {
      loading.value = false
    })
  }).catch(() => {})
}

// 导出活动
const handleExport = () => {
  ElMessage.info('导出功能开发中')
  // 暂时注释掉，因为store中可能没有实现此方法
  // loading.value = true
  // activityStore.exportActivity().then(response => {
  //   ElMessage.success('导出成功')
  // }).finally(() => {
  //   loading.value = false
  // })
}

// 处理状态变更命令
const handleStatusCommand = (command, row) => {
  const statusMap = {
    'start': { value: 1, text: '开始报名' },
    'end': { value: 2, text: '开始活动' },
    'finish': { value: 3, text: '结束活动' },
    'cancel': { value: 4, text: '取消活动' }
  }

  const { value, text } = statusMap[command]

  // 检查状态流转是否合法
  if (command === 'end' && row.status !== 1) {
    ElMessage.warning('只有在报名中的活动才能开始活动')
    return
  }

  if (command === 'finish' && row.status !== 2) {
    ElMessage.warning('只有进行中的活动才能结束活动')
    return
  }

  ElMessageBox.confirm(`确认将活动"${row.title}"状态更改为"${text}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    loading.value = true
    activityStore.updateActivityStatus(row.id, value).then(() => {
      ElMessage.success(`${text}成功`)
      getList()
    }).catch(error => {
      console.error('更新状态失败:', error)
    }).finally(() => {
      loading.value = false
    })
  }).catch(() => {})
}

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    0: 'info',    // 未开始
    1: 'success', // 报名中
    2: 'primary', // 进行中
    3: 'warning', // 已结束
    4: 'danger'   // 已取消
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '未开始',
    1: '报名中',
    2: '进行中',
    3: '已结束',
    4: '已取消'
  }
  return statusMap[status] || '未知'
}

// 处理图片上传成功
const handleImageChange = (res) => {
  if (res.code === 200) {
    formData.image = res.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error('上传失败')
  }
}

// 提交表单
const handleSubmit = () => {
  formRef.value?.validate((valid) => {
    if (valid) {
      // 创建一个新对象来保存要提交的数据
      const submitData = { ...formData };
      
      // 处理时间范围
      if (submitData.timeRange && submitData.timeRange.length === 2) {
        submitData.startTime = submitData.timeRange[0]
        submitData.endTime = submitData.timeRange[1]
        // 删除timeRange字段，因为后端不需要
        delete submitData.timeRange
      }
      
      // 确保type字段是字符串类型，与后端保持一致
      if (submitData.type && typeof submitData.type === 'number') {
        submitData.type = submitData.type.toString()
      }

      loading.value = true
      if (dialogType.value === 'add') {
        activityStore.createActivity(submitData).then(() => {
          ElMessage.success('添加成功')
          dialogVisible.value = false
          getList()
        }).catch(error => {
          console.error('创建活动失败:', error)
          if (error.response && error.response.data && error.response.data.message) {
            ElMessage.error(error.response.data.message)
          } else {
            ElMessage.error('创建活动失败')
          }
        }).finally(() => {
          loading.value = false
        })
      } else if (dialogType.value === 'edit') {
        activityStore.updateActivity(submitData.id, submitData).then(() => {
          ElMessage.success('更新成功')
          dialogVisible.value = false
          getList()
        }).catch(error => {
          console.error('更新活动失败:', error)
          if (error.response && error.response.data && error.response.data.message) {
            ElMessage.error(error.response.data.message)
          } else {
            ElMessage.error('更新活动失败')
          }
        }).finally(() => {
          loading.value = false
        })
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(formData, {
    id: undefined,
    title: '',
    type: '',
    timeRange: [],
    startTime: '',
    endTime: '',
    location: '',
    maxParticipants: 50,
    // 移除 currentParticipants 和 image 字段，因为后端不支持
    description: '',
    status: 0,
    organizerId: parseInt(sessionStorage.getItem('userId')) || 1
  })
}

// 处理分页
const handlePagination = ({ page, limit }) => {
  activityStore.queryParams.pageNum = page
  activityStore.queryParams.pageSize = limit
  getList()
}

// 自动刷新相关
const autoRefreshInterval = ref(null)
const autoRefreshTime = ref(30) // 默认30秒自动刷新
const isAutoRefresh = ref(false)

// 开始自动刷新
const startAutoRefresh = () => {
  if (autoRefreshInterval.value) clearInterval(autoRefreshInterval.value)
  autoRefreshInterval.value = setInterval(() => {
    getList()
  }, autoRefreshTime.value * 1000)
}

// 停止自动刷新
const stopAutoRefresh = () => {
  if (autoRefreshInterval.value) {
    clearInterval(autoRefreshInterval.value)
    autoRefreshInterval.value = null
  }
}

// 切换自动刷新
const toggleAutoRefresh = (val) => {
  if (val) {
    startAutoRefresh()
  } else {
    stopAutoRefresh()
  }
}

// 修改自动刷新时间
const changeRefreshTime = (time) => {
  autoRefreshTime.value = time
  if (isAutoRefresh.value) {
    startAutoRefresh()
  }
}

// 处理表格排序
const handleSortChange = ({ column, prop, order }) => {
  if (!prop) {
    activityStore.queryParams.orderByColumn = ''
    activityStore.queryParams.isAsc = ''
  } else {
    activityStore.queryParams.orderByColumn = prop
    activityStore.queryParams.isAsc = order === 'ascending' ? 'asc' : 'desc'
  }
  getList()
}

// 处理列筛选
const handleFilterChange = (filters) => {
  Object.keys(filters).forEach(prop => {
    const column = columns.value.find(col => col.prop === prop)
    if (column && column.filterable) {
      activityStore.queryParams[`${prop}Filter`] = filters[prop]
    }
  })
  getList()
}

// 重置所有筛选
const resetAllFilters = () => {
  columns.value.forEach(column => {
    if (column.filterable) {
      activityStore.queryParams[`${column.prop}Filter`] = []
    }
  })
  getList()
}

// 表格行类名
const tableRowClassName = ({ row }) => {
  if (row.status === 3) return 'disabled-row' // 已结束的活动
  if (row.status === 4) return 'canceled-row' // 已取消的活动
  return ''
}

// 处理右键菜单
const contextMenu = ref(null)
const showContextMenu = ref(false)
const contextMenuPosition = ref({ x: 0, y: 0 })
const contextMenuRow = ref(null)

const handleContextMenu = (row, column, event) => {
  event.preventDefault()
  contextMenuRow.value = row
  contextMenuPosition.value = {
    x: event.clientX,
    y: event.clientY
  }
  showContextMenu.value = true
  
  // 点击其他地方关闭菜单
  const closeMenu = (e) => {
    if (!contextMenu.value?.contains(e.target)) {
      showContextMenu.value = false
      document.removeEventListener('click', closeMenu)
    }
  }
  nextTick(() => {
    document.addEventListener('click', closeMenu)
  })
}

// 处理列设置
const handleColumnCommand = (command) => {
  const column = columns.value.find(col => col.key === command)
  if (column) {
    column.visible = !column.visible
  }
}

// 重置列设置
const resetColumnSettings = () => {
  columns.value.forEach(col => {
    col.visible = true
  })
}

// 处理菜单项点击
const handleMenuCommand = (command) => {
  const row = contextMenuRow.value
  switch (command) {
    case 'view':
      handleView(row)
      break
    case 'edit':
      handleEdit(row)
      break
    case 'delete':
      handleDelete(row)
      break
    case 'export':
      handleExportRow(row)
      break
  }
  showContextMenu.value = false
}

// 导出单行数据
const handleExportRow = (row) => {
  // 这里实现导出单行数据的逻辑
  ElMessage.success(`导出活动: ${row.title}`)
}

// 滚动加载更多
const loadingMore = ref(false)
const noMoreData = ref(false)

const loadMore = async () => {
  if (loadingMore.value || noMoreData.value) return
  
  loadingMore.value = true
  try {
    const currentPage = activityStore.queryParams.pageNum
    const total = activityStore.total
    const pageSize = activityStore.queryParams.pageSize
    
    if (currentPage * pageSize >= total) {
      noMoreData.value = true
      return
    }
    
    activityStore.queryParams.pageNum++
    await activityStore.getActivityList()
  } catch (error) {
    console.error('加载更多失败:', error)
  } finally {
    loadingMore.value = false
  }
}

// 组件卸载时清除定时器
onBeforeUnmount(() => {
  stopAutoRefresh()
})

// 初始化
onMounted(async () => {
  try {
    // 首先加载活动类型数据
    await loadActivityTypes()
    
    // 然后加载活动列表
    getList()
  } catch (error) {
    console.error('活动列表初始化失败:', error)
    // 即使出错也尝试加载活动列表
    getList()
  }
})
</script>

<style scoped>
.column-settings-btn {
  margin-left: 10px;
}

.column-settings-btn .el-icon {
  margin-left: 5px;
}

.column-item {
  display: flex;
  align-items: center;
  padding: 8px 16px;
}

.column-item .el-checkbox {
  margin-right: 8px;
}

.column-label {
  margin-left: 5px;
}

/* 表格操作按钮 */
.table-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

/* 响应式调整 */
@media screen and (max-width: 1200px) {
  .el-table__body {
    font-size: 12px;
  }
  
  .el-button + .el-button {
    margin-left: 4px;
  }
  
  .el-button--small {
    padding: 5px 8px;
  }
}
.app-container {
  padding: 20px;
  position: relative;
}

/* 右键菜单样式 */
.context-menu {
  position: fixed;
  z-index: 3000;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 5px 0;
  min-width: 120px;
}

.context-menu-item {
  padding: 8px 16px;
  cursor: pointer;
  font-size: 14px;
  color: #606266;
  display: flex;
  align-items: center;
}

.context-menu-item i {
  margin-right: 8px;
}

.context-menu-item:hover {
  background: #f5f7fa;
  color: #409eff;
}

/* 禁用行样式 */
:deep(.disabled-row) {
  color: #909399;
  background-color: #f5f7fa !important;
}

/* 已取消行样式 */
:deep(.canceled-row) {
  color: #c0c4cc;
  text-decoration: line-through;
}

/* 标题单元格 */
.title-cell {
  display: inline-block;
  max-width: 100%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  vertical-align: middle;
}

/* 加载更多提示 */
.load-more {
  text-align: center;
  padding: 10px;
  color: #909399;
  font-size: 12px;
}

/* 操作列按钮间距 */
.el-button + .el-dropdown {
  margin-left: 8px;
}

.mb8 {
  margin-bottom: 8px;
}

.avatar-uploader {
  width: 178px;
  height: 178px;
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: cover;
}

.dialog-footer {
  padding-top: 20px;
  text-align: right;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-table {
  margin-top: 15px;
  margin-bottom: 15px;
}

.el-date-picker {
  width: 100%;
}

.el-dropdown {
  margin-left: 10px;
  margin-right: 10px;
}
</style>
