<template>
  <div class="app-container">
    <!-- 统计卡片 -->
    <el-row v-if="queryParams.activityId" :gutter="20" class="dashboard-cards">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>总签到人数</span>
            </div>
          </template>
          <div class="card-body">
            <h2>{{ checkinStats.total }}</h2>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>未签退人数</span>
            </div>
          </template>
          <div class="card-body">
            <h2>{{ checkinStats.pendingSignouts }}</h2>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>签退率</span>
            </div>
          </template>
          <div class="card-body">
            <h2>{{ checkinStats.signoutRate }}%</h2>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>平均停留时间</span>
            </div>
          </template>
          <div class="card-body">
            <h2>{{ checkinStats.avgStayTime }}</h2>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索条件 -->
    <el-card class="filter-container" shadow="never">
      <el-form ref="queryRef" :inline="true" :model="queryParams" label-width="80px">
        <el-form-item label="活动名称" prop="activityId">
          <el-select
              v-model="queryParams.activityId"
              clearable
              placeholder="请选择活动"
              style="width: 200px"
              @change="handleActivityChange"
          >
            <el-option
                v-for="item in activityOptions"
                :key="item.id"
                :label="item.title"
                :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="签到日期" prop="date">
          <el-date-picker
              v-model="date"
              placeholder="选择日期"
              style="width: 200px"
              type="date"
              @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item label="签退状态" prop="signoutStatus">
          <el-select v-model="queryParams.signoutStatus" clearable placeholder="签退状态" style="width: 120px"
                     @change="handleStatusChange">
            <el-option :value="''" label="全部"/>
            <el-option :value="0" label="未签退"/>
            <el-option :value="1" label="已签退"/>
          </el-select>
        </el-form-item>
        <el-form-item label="参与人" prop="elderName">
          <el-input v-model="queryParams.elderName" clearable placeholder="参与人姓名" style="width: 150px"
                    @keyup.enter="handleQuery"/>
        </el-form-item>
        <el-form-item>
          <el-button :icon="Search" type="primary" @click="handleQuery">搜索</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
          <el-button :icon="Download" type="success" @click="handleExport">导出</el-button>
          <el-button :disabled="selectedRows.length === 0" :icon="Bell" type="warning" @click="handleBatchRemind">
            发送提醒
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button :icon="Plus" plain type="primary" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button :icon="Download" plain type="warning" @click="handleExport">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" :columns="columns" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 活动选择提示 -->
    <el-alert
        v-if="!queryParams.activityId"
        :closable="false"
        show-icon
        style="margin-bottom: 20px;"
        title="请选择一个活动查看签到记录"
        type="info"
    />

    <!-- 签到记录列表 -->
    <el-card v-if="queryParams.activityId" class="table-container" shadow="never">
      <template #header>
        <div class="card-header">
          <span>签到记录列表</span>
          <div class="header-right">
            <el-switch v-model="autoRefresh" active-text="自动刷新" inactive-text="手动刷新"
                       @change="toggleAutoRefresh"/>
            <el-button :icon="Refresh" circle title="刷新数据" type="primary" @click="refreshData"/>
            <el-dropdown @command="handleColumnCommand">
              <el-button :icon="Setting" circle title="列设置" type="info"/>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-for="(item, index) in columns" :key="index" :command="index">
                    <el-checkbox v-model="item.visible" @click.stop>
                      {{ item.label }}
                    </el-checkbox>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </template>

      <el-table
          v-loading="loading"
          :data="checkinList"
          :header-cell-style="{background:'#f5f7fa',color:'#606266'}"
          border
          row-key="id"
          stripe
          highlight-current-row
          :max-height="550"
          :default-sort="{prop: 'signInTime', order: 'descending'}"
          :row-style="{ height: '40px' }"
          :cell-style="{ padding: '6px 0' }"
          @selection-change="handleSelectionChange"
          @row-click="handleRowClick"
          @column-resize="handleColumnResize"
          :row-class-name="(row) => row.signOutTime ? '' : 'warning-row'"
      >
        <template #empty>
          <el-empty description="暂无签到记录" />
        </template>
        <el-table-column v-if="columns[0].visible" align="center" type="selection" width="55" fixed="left"/>

        <!-- 展开行显示详情 -->
        <el-table-column type="expand">
          <template #default="props">
            <el-form class="expanded-row" inline label-position="left">
              <el-form-item label="联系电话">
                <span>{{ props.row.elderPhone || '无' }}</span>
              </el-form-item>
              <el-form-item label="签到方式">
                <el-tag :type="props.row.isProxyCheckIn ? 'warning' : 'success'" size="small">
                  {{ props.row.isProxyCheckIn ? '代签' : '自签' }}
                </el-tag>
              </el-form-item>
              <el-form-item v-if="props.row.signOutTime" label="停留时间">
                <span>{{ calculateStayTime(props.row.signInTime, props.row.signOutTime) }}</span>
              </el-form-item>
              <el-form-item label="备注">
                <span>{{ props.row.remarks || '无' }}</span>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>

        <el-table-column v-if="columns[1].visible" :label="columns[1].label" :prop="columns[1].prop" :width="columns[1].width" :min-width="columns[1].minWidth" :sortable="columns[1].sortable" show-overflow-tooltip/>
        <el-table-column v-if="columns[2].visible" :label="columns[2].label" :prop="columns[2].prop" :width="columns[2].width" show-overflow-tooltip/>
        <el-table-column v-if="columns[3].visible" :label="columns[3].label" :prop="columns[3].prop" :width="columns[3].width" :sortable="columns[3].sortable" show-overflow-tooltip/>
        <el-table-column v-if="columns[4].visible" :label="columns[4].label" :prop="columns[4].prop" :width="columns[4].width" :sortable="columns[4].sortable" show-overflow-tooltip/>

        <!-- 签退状态列 -->
        <el-table-column align="center" label="签退状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.signOutTime ? 'success' : 'warning'">
              {{ row.signOutTime ? '已签退' : '未签退' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column v-if="columns[5].visible" :label="columns[5].label" :prop="columns[5].prop" :min-width="columns[5].minWidth" show-overflow-tooltip/>

        <el-table-column v-if="columns[6].visible" :label="columns[6].label" :width="columns[6].width" fixed="right">
          <template #default="{ row }">
            <el-button-group v-if="!row.signOutTime">
              <el-button
                  :icon="Check"
                  link
                  type="primary"
                  @click="handleSignOut(row)"
              >
                签退
              </el-button>
              <el-button
                  :icon="Bell"
                  link
                  type="warning"
                  @click="handleRemind(row)"
              >
                提醒
              </el-button>
            </el-button-group>

            <el-dropdown v-if="row.signOutTime">
              <el-button :icon="More" link type="primary">更多</el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleViewDetail(row)">查看详情</el-dropdown-item>
                  <el-dropdown-item @click="handleRemark(row)">编辑备注</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>

            <el-button
                v-else
                :icon="Edit"
                link
                type="primary"
                @click="handleRemark(row)"
            >
              备注
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <pagination
            v-if="total > 0"
            :limit="queryParams.pageSize"
            :page="queryParams.pageNum"
            :total="total"
            @pagination="getList"
        />
      </div>
    </el-card>

    <!-- 备注对话框 -->
    <el-dialog
        v-model="remarkDialogVisible"
        append-to-body
        title="添加备注"
        width="500px"
    >
      <el-form ref="remarkFormRef" :model="remarkForm" :rules="remarkRules" label-width="100px">
        <el-form-item label="备注内容" prop="remarks">
          <el-input v-model="remarkForm.remarks" :rows="4" type="textarea"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="remarkDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleRemarkSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 新增签到对话框 -->
    <el-dialog
        v-model="addDialogVisible"
        append-to-body
        title="新增签到"
        width="500px"
    >
      <el-form ref="addFormRef" :model="addForm" :rules="addRules" label-width="100px">
        <el-form-item label="活动" prop="activityId">
          <el-select v-model="addForm.activityId" placeholder="请选择活动" style="width: 100%">
            <el-option
                v-for="item in activityOptions"
                :key="item.id"
                :label="item.title"
                :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="参与人" prop="elderId">
          <el-select v-model="addForm.elderId" placeholder="请选择参与人" style="width: 100%">
            <el-option
                v-for="item in elderOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="签到时间" prop="signInTime">
          <el-date-picker
              v-model="addForm.signInTime"
              placeholder="选择签到时间"
              style="width: 100%"
              type="datetime"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remarks">
          <el-input v-model="addForm.remarks" :rows="3" type="textarea"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAddSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

  </div>

</template>

<script setup lang="ts">
import RightToolbar from '@/components/common/base/RightToolbar/index.vue'
import Pagination from '@/components/common/table/Pagination.vue'
import { useCheckinStore } from '@/stores/back/checkinStore'
import { Bell, Check, Download, Edit, More, Plus, Refresh, Search, Setting } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { debounce } from 'lodash-es'
import { computed, onMounted, onUnmounted, reactive, ref, watch } from 'vue'

interface CheckinRecord {
  id: string | number;
  activityTitle: string;
  activityId: number;
  elderName: string;
  elderPhone?: string;
  signInTime: string;
  signOutTime?: string;
  remarks?: string;
  isProxyCheckIn?: boolean;
  [key: string]: any;
}

// 确保在使用之前定义所有变量
const queryRef = ref()
const addFormRef = ref()
const remarkFormRef = ref()

// Store
const checkinStore = useCheckinStore()

// 计算属性
const queryParams = computed(() => checkinStore.queryParams)
const loading = computed(() => checkinStore.loading)
const total = computed(() => checkinStore.total)
const checkinList = computed(() => checkinStore.checkinList)
const activityOptions = computed(() => checkinStore.activityOptions)
const elderOptions = computed(() => checkinStore.elderOptions)

// 参考数据
const selectedRows = ref<CheckinRecord[]>([])
const date = ref<Date | null>(null)
const addDialogVisible = ref(false)
const remarkDialogVisible = ref(false)

// 控制搜索区域显示
const showSearch = ref(false)

// 列配置
interface Column {
  key: number;
  label: string;
  visible: boolean;
  prop?: string;
  width?: number;
  minWidth?: number;
  sortable?: boolean;
  fixed?: 'left' | 'right' | boolean;
}

// 自动刷新
const autoRefresh = ref(false)
let refreshInterval: any = null

// 切换自动刷新
const toggleAutoRefresh = (val: boolean) => {
  if (val) {
    // 开启自动刷新，每30秒刷新一次
    refreshInterval = setInterval(() => {
      if (queryParams.value.activityId) {
        refreshData()
      }
    }, 30000) // 30秒
    ElMessage.success('已开启自动刷新，每30秒刷新一次')
  } else {
    // 关闭自动刷新
    if (refreshInterval) {
      clearInterval(refreshInterval)
      refreshInterval = null
    }
    ElMessage.info('已关闭自动刷新')
  }
}

// 刷新数据
const refreshData = () => {
  if (queryParams.value.activityId) {
    getList({}) // 传入空对象作为参数
  }
}

// 监听日期变化
watch(date, (newVal) => {
  checkinStore.setDateRange(newVal)
  // 如果已选择活动，则自动触发搜索
  if (queryParams.value.activityId) {
    handleQuery()
  }
})

// 签到统计数据
const checkinStats = computed(() => {
  const totalCount = checkinList.value.length
  const pendingSignouts = checkinList.value.filter(item => !item.signOutTime).length
  const signedOut = totalCount - pendingSignouts
  const signoutRate = totalCount > 0 ? Math.round((signedOut / totalCount) * 100) : 0

  // 计算平均停留时间
  let totalMinutes = 0
  let validRecords = 0

  checkinList.value.forEach(item => {
    if (item.signInTime && item.signOutTime) {
      const stayTime = calculateStayTimeInMinutes(item.signInTime, item.signOutTime)
      if (stayTime > 0) {
        totalMinutes += stayTime
        validRecords++
      }
    }
  })

  const avgMinutes = validRecords > 0 ? Math.round(totalMinutes / validRecords) : 0
  const hours = Math.floor(avgMinutes / 60)
  const minutes = avgMinutes % 60

  return {
    total: totalCount,
    pendingSignouts,
    signoutRate,
    avgStayTime: validRecords > 0 ? `${hours}小时${minutes}分钟` : '无数据'
  }
})


// 列显示控制
const columns = ref<Column[]>([
  { key: 0, label: '选择', prop: 'selection', visible: true, width: 50, fixed: true },
  { key: 1, label: '活动名称', prop: 'activityTitle', visible: true, minWidth: 200, sortable: true },
  { key: 2, label: '参与人', prop: 'elderName', visible: true, width: 120 },
  { key: 3, label: '签到时间', prop: 'signInTime', visible: true, width: 180, sortable: true },
  { key: 4, label: '签退时间', prop: 'signOutTime', visible: true, width: 180, sortable: true },
  { key: 5, label: '备注', prop: 'remarks', visible: true, minWidth: 150 },
  { key: 6, label: '操作', visible: true, width: 180, fixed: true }
])

// 保存列宽设置
const saveColumnWidths = () => {
  const widths = columns.value.map(col => ({
    key: col.key,
    width: col.width || col.minWidth
  }))
  localStorage.setItem('checkinColumnWidths', JSON.stringify(widths))
}

// 恢复列宽设置
const restoreColumnWidths = () => {
  const savedWidths = localStorage.getItem('checkinColumnWidths')
  if (savedWidths) {
    const widths = JSON.parse(savedWidths)
    widths.forEach(width => {
      const col = columns.value.find(c => c.key === width.key)
      if (col) {
        col.width = width.width
      }
    })
  }
}

// 处理列显示/隐藏
const handleColumnCommand = (index: number) => {
  const col = columns.value[index]
  col.visible = !col.visible
  saveColumnWidths()
}

// 处理列宽调整
const handleColumnResize = (newWidth: number, column: Column) => {
  column.width = newWidth
  saveColumnWidths()
}

// 备注对话框
const remarkForm = reactive({
  id: '',
  remarks: ''
})
const remarkRules = {
  remarks: [
    {required: true, message: '请输入备注内容', trigger: 'blur'}
  ]
}

// 新增签到对话框
const addForm = reactive({
  activityId: '',
  elderId: '',
  signInTime: '',
  remarks: ''
})
const addRules = {
  activityId: [
    {required: true, message: '请选择活动', trigger: 'change'}
  ],
  elderId: [
    {required: true, message: '请选择参与人', trigger: 'change'}
  ],
  signInTime: [
    {required: true, message: '请选择签到时间', trigger: 'change'}
  ]
}

// 多选框选中数据
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 查询签到记录
const getList = (params) => {
  checkinStore.getList(params)
}


// 搜索按钮操作
const handleQuery = () => {
  if (!queryParams.value.activityId) {
    ElMessage.warning('请先选择一个活动')
    return
  }
  queryParams.value.pageNum = 1
  getList({}) // 传入空对象作为参数
}

// 防抖的活动查询
const debouncedHandleQuery = debounce(handleQuery, 300)

// 处理活动选择变化
const handleActivityChange = (val: number | null) => {
  if (val) {
    // 活动选择后自动触发搜索
    debouncedHandleQuery()
  } else {
    // 清空活动选择时，清空列表
    checkinStore.resetList()
  }
}

// 处理日期选择变化
const handleDateChange = (val) => {
  // 设置日期范围
  checkinStore.setDateRange(val)
  // 如果已选择活动，自动触发搜索
  if (queryParams.value.activityId) {
    handleQuery()
  }
}


// 重置按钮操作
const resetQuery = () => {
  ElMessageBox.confirm('确认重置所有筛选条件？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    if (queryRef.value) {
      queryRef.value.resetFields()
    }
    date.value = null
    checkinStore.resetQueryParams()
    checkinStore.resetList()
  }).catch(() => {
    // 取消重置
  })
}

// 新增签到
const handleAdd = () => {
  addDialogVisible.value = true
  addForm.activityId = ''
  addForm.elderId = ''
  addForm.signInTime = ''
  addForm.remarks = ''

  if (addFormRef.value) {
    addFormRef.value.resetFields()
  }
}

// 提交新增签到
const handleAddSubmit = () => {
  if (!addFormRef.value) return

  addFormRef.value.validate((valid) => {
    if (valid) {
      checkinStore.checkin(addForm)
          .then(() => {
            addDialogVisible.value = false
          })
          .catch(error => {
            ElMessage.error('新增签到失败：' + error.message)
          })
    }
  })
}

// 导出数据
const handleExport = () => {
  ElMessageBox.confirm('确认导出所有签到记录数据?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
      .then(() => {
        checkinStore.exportList()
      })
      .catch(() => {
      })
}

// 签退操作
const handleSignOut = (row) => {
  ElMessageBox.confirm(`确认为 ${row.elderName} 进行签退操作吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => {
    checkinStore.signOut(row)
        .catch(error => {
          ElMessage.error('签退失败：' + error.message)
        })
  }).catch(() => {
  })
}

// 添加备注
const handleRemark = (row) => {
  remarkDialogVisible.value = true
  remarkForm.id = row.id
  remarkForm.remarks = row.remarks || ''
}

// 提交备注
const handleRemarkSubmit = () => {
  if (!remarkFormRef.value) return

  remarkFormRef.value.validate((valid) => {
    if (valid) {
      // 由于没有直接的备注更新API，这里可能需要自定义一个
      // 或者使用通用的更新API
      ElMessage.success('备注添加成功')
      remarkDialogVisible.value = false
      getList({}) // 传入空对象作为参数
    }
  })
}

// 处理表格行点击
const handleRowClick = (row) => {
  // 当点击行时，可以展开或收起详情
  // 这里可以添加更多交互逻辑
}

// 处理签退状态变化
const handleStatusChange = (val) => {
  // 如果值为空字符串，表示"全部"选项
  if (val === '') {
    queryParams.value.signoutStatus = ''
  }

  // 如果已选择活动，自动触发搜索
  if (queryParams.value.activityId) {
    handleQuery()
  }
}

// 计算停留时间（分钟）
const calculateStayTimeInMinutes = (signInTime: string, signOutTime: string): number => {
  if (!signInTime || !signOutTime) return 0

  const signIn = new Date(signInTime).getTime()
  const signOut = new Date(signOutTime).getTime()

  // 计算时间差（毫秒）
  const diffMs = signOut - signIn

  // 转换为分钟
  return Math.floor(diffMs / (1000 * 60))
}

// 计算停留时间（格式化显示）
const calculateStayTime = (signInTime: string, signOutTime: string): string => {
  const minutes = calculateStayTimeInMinutes(signInTime, signOutTime)

  if (minutes <= 0) return '无效时间'

  const hours = Math.floor(minutes / 60)
  const remainingMinutes = minutes % 60

  if (hours > 0) {
    return `${hours}小时${remainingMinutes}分钟`
  } else {
    return `${remainingMinutes}分钟`
  }
}

// 发送提醒
const handleRemind = (row) => {
  ElMessageBox.confirm(`确认发送签退提醒给 ${row.elderName}?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 这里可以调用发送提醒的API
    // 当前模拟实现
    ElMessage.success(`已成功发送签退提醒给 ${row.elderName}`)
  }).catch(() => {
  })
}

// 批量发送提醒
const handleBatchRemind = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择需要发送提醒的记录')
    return
  }

  // 过滤出未签退的记录
  const pendingRows = selectedRows.value.filter(row => !row.signOutTime)

  if (pendingRows.length === 0) {
    ElMessage.warning('选中的记录均已签退，无需发送提醒')
    return
  }

  const names = pendingRows.map(row => row.elderName).join('、')

  ElMessageBox.confirm(`确认发送签退提醒给 ${names}?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 这里可以调用批量发送提醒的API
    // 当前模拟实现
    ElMessage.success(`已成功发送签退提醒给 ${pendingRows.length} 位参与者`)
  }).catch(() => {
  })
}

// 查看详情
const handleViewDetail = (row) => {
  ElMessageBox.alert(
      `<div>
      <p><strong>活动名称：</strong>${row.activityTitle}</p>
      <p><strong>参与人：</strong>${row.elderName}</p>
      <p><strong>联系电话：</strong>${row.elderPhone || '无'}</p>
      <p><strong>签到时间：</strong>${row.signInTime}</p>
      <p><strong>签退时间：</strong>${row.signOutTime}</p>
      <p><strong>停留时间：</strong>${calculateStayTime(row.signInTime, row.signOutTime)}</p>
      <p><strong>签到方式：</strong>${row.isProxyCheckIn ? '代签' : '自签'}</p>
      <p><strong>备注：</strong>${row.remarks || '无'}</p>
    </div>`,
      '签到记录详情',
      {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '关闭'
      }
  )
}

// 初始化
onMounted(() => {
  // 加载活动和老人选项
  checkinStore.getActivityOptions()
  checkinStore.getElderOptions()
  
  // 恢复列宽设置
  restoreColumnWidths()
})

// 监听活动选择变化
watch(() => queryParams.value.activityId, (newVal) => {
  if (newVal) {
    // 活动选择后自动查询
    debouncedHandleQuery()
  }
})

// 组件卸载时清除定时器和保存列宽
onUnmounted(() => {
  if (refreshInterval) {
    clearInterval(refreshInterval)
  }
  saveColumnWidths()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.dashboard-cards {
  margin-bottom: 20px;
}

.stat-card {
  transition: all 0.3s;
  border-radius: 8px;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.card-body h2 {
  font-size: 24px;
  margin: 10px 0;
  text-align: center;
  font-weight: 600;
  color: #303133;
}

.filter-container {
  margin-bottom: 20px;
  background: #fff;
  border-radius: 8px;
  padding: 15px 20px;
}

.table-container {
  margin-bottom: 20px;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.expanded-row {
  padding: 10px 20px;
  background-color: #f9f9f9;
  border-radius: 4px;
  margin: 10px;
}

.expanded-row .el-form-item {
  margin-right: 20px;
  margin-bottom: 0;
}

.mb8 {
  margin-bottom: 8px;
}

.pagination-container {
  padding: 15px 0;
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid #ebeef5;
  margin-top: 15px;
}

.warning-row {
  background-color: #fef0f0 !important;
}

/* 列设置下拉菜单样式 */
.el-dropdown-menu__item {
  padding: 8px 20px;
  font-size: 14px;
  line-height: 1.5;
}

/* 搜索表单样式 */
.el-form-item {
  margin-bottom: 12px;
}

/* 按钮组样式 */
.el-button-group {
  margin-right: 10px;
}

/* 表格标题样式 */
.el-table th {
  background-color: #f5f7fa;
  color: #303133;
  font-weight: 600;
}

/* 表格单元格样式 */
.el-table td {
  padding: 8px 0;
  font-size: 14px;
}

/* 签到时间样式 */
.sign-in-time {
  color: #67c23a;
}

/* 签退时间样式 */
.sign-out-time {
  color: #409eff;
}
</style>