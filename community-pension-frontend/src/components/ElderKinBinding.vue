<template>
  <div class="elder-kin-binding">
    <el-tabs v-model="activeTab">
      <!-- 老人绑定家属 -->
      <el-tab-pane label="老人绑定家属" name="elderBindKin">
        <el-form :model="elderBindForm" label-width="120px" :rules="rules" ref="elderBindFormRef">
          <el-form-item label="选择老人" prop="elderId">
            <el-select v-model="elderBindForm.elderId" placeholder="请选择老人" filterable>
              <el-option
                v-for="elder in unboundElders"
                :key="elder.userId"
                :label="elder.name || elder.username"
                :value="elder.userId"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="选择家属" prop="kinId">
            <el-select v-model="elderBindForm.kinId" placeholder="请选择家属" filterable>
              <el-option
                v-for="kin in unboundKins"
                :key="kin.userId"
                :label="kin.name || kin.username"
                :value="kin.userId"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="关系类型" prop="relationType">
            <el-select v-model="elderBindForm.relationType" placeholder="请选择关系类型">
              <el-option label="父子" value="父子" />
              <el-option label="父女" value="父女" />
              <el-option label="母子" value="母子" />
              <el-option label="母女" value="母女" />
              <el-option label="祖孙" value="祖孙" />
              <el-option label="兄弟姐妹" value="兄弟姐妹" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleElderBindKin">绑定关系</el-button>
            <el-button @click="resetForm('elderBindFormRef')">重置</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
      
      <!-- 家属绑定老人 -->
      <el-tab-pane label="家属绑定老人" name="kinBindElder">
        <el-form :model="kinBindForm" label-width="120px" :rules="rules" ref="kinBindFormRef">
          <el-form-item label="选择家属" prop="kinId">
            <el-select v-model="kinBindForm.kinId" placeholder="请选择家属" filterable>
              <el-option
                v-for="kin in unboundKins"
                :key="kin.userId"
                :label="kin.name || kin.username"
                :value="kin.userId"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="选择老人" prop="elderId">
            <el-select v-model="kinBindForm.elderId" placeholder="请选择老人" filterable>
              <el-option
                v-for="elder in unboundElders"
                :key="elder.userId"
                :label="elder.name || elder.username"
                :value="elder.userId"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="关系类型" prop="relationType">
            <el-select v-model="kinBindForm.relationType" placeholder="请选择关系类型">
              <el-option label="子父" value="子父" />
              <el-option label="女父" value="女父" />
              <el-option label="子母" value="子母" />
              <el-option label="女母" value="女母" />
              <el-option label="孙祖" value="孙祖" />
              <el-option label="兄弟姐妹" value="兄弟姐妹" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleKinBindElder">绑定关系</el-button>
            <el-button @click="resetForm('kinBindFormRef')">重置</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
      
      <!-- 查看绑定关系 -->
      <el-tab-pane label="查看绑定关系" name="viewRelations">
        <el-form :model="queryForm" label-width="120px">
          <el-form-item label="查询类型">
            <el-radio-group v-model="queryForm.type">
              <el-radio label="elder">查询老人的家属</el-radio>
              <el-radio label="kin">查询家属的老人</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="选择用户" prop="userId">
            <el-select 
              v-model="queryForm.userId" 
              placeholder="请选择用户" 
              filterable
              :loading="userLoading"
            >
              <el-option
                v-for="user in queryForm.type === 'elder' ? allElders : allKins"
                :key="user.userId"
                :label="user.name || user.username"
                :value="user.userId"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleQueryRelations">查询</el-button>
            <el-button @click="resetQueryForm">重置</el-button>
          </el-form-item>
        </el-form>
        
        <!-- 关系列表 -->
        <el-table v-if="relationList.length > 0" :data="relationList" border style="width: 100%">
          <el-table-column prop="userId" label="用户ID" width="80" />
          <el-table-column prop="username" label="用户名" width="120" />
          <el-table-column prop="name" label="姓名" width="120" />
          <el-table-column prop="phone" label="电话" width="120" />
          <el-table-column prop="relationType" label="关系类型" width="120" />
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button 
                type="danger" 
                size="small" 
                @click="handleUnbind(scope.row)"
              >
                解绑
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-else description="暂无绑定关系" />
      </el-tab-pane>
    </el-tabs>
    
    <!-- 解绑确认对话框 -->
    <el-dialog
      title="解绑确认"
      v-model="unbindDialogVisible"
      width="30%"
    >
      <span>确定要解除与该用户的绑定关系吗？</span>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="unbindDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmUnbind">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  bindRelation, 
  unbindRelation, 
  getKinsByElderId, 
  getEldersByKinId, 
  getUnboundElders, 
  getUnboundKins 
} from '@/api/elderKinRelation'

// 表单数据
const elderBindForm = reactive({
  elderId: '',
  kinId: '',
  relationType: ''
})

const kinBindForm = reactive({
  elderId: '',
  kinId: '',
  relationType: ''
})

const queryForm = reactive({
  type: 'elder',
  userId: ''
})

// 表单验证规则
const rules = {
  elderId: [{ required: true, message: '请选择老人', trigger: 'change' }],
  kinId: [{ required: true, message: '请选择家属', trigger: 'change' }],
  relationType: [{ required: true, message: '请选择关系类型', trigger: 'change' }]
}

// 表单引用
const elderBindFormRef = ref(null)
const kinBindFormRef = ref(null)

// 数据列表
const unboundElders = ref([])
const unboundKins = ref([])
const allElders = ref([])
const allKins = ref([])
const relationList = ref([])

// 加载状态
const userLoading = ref(false)

// 当前选中的标签页
const activeTab = ref('elderBindKin')

// 解绑对话框
const unbindDialogVisible = ref(false)
const currentUnbindUser = ref(null)

// 获取未绑定的老人和家属列表
const fetchUnboundUsers = async () => {
  try {
    const [eldersRes, kinsRes] = await Promise.all([
      getUnboundElders(),
      getUnboundKins()
    ])
    
    unboundElders.value = eldersRes.data || []
    unboundKins.value = kinsRes.data || []
    
    // 同时更新所有用户列表
    allElders.value = [...unboundElders.value]
    allKins.value = [...unboundKins.value]
  } catch (error) {
    console.error('获取未绑定用户列表失败:', error)
    ElMessage.error('获取未绑定用户列表失败')
  }
}

// 老人绑定家属
const handleElderBindKin = async () => {
  elderBindFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await bindRelation({
          elderId: elderBindForm.elderId,
          kinId: elderBindForm.kinId,
          relationType: elderBindForm.relationType
        })
        
        if (res.code === 200) {
          ElMessage.success('绑定成功')
          resetForm('elderBindFormRef')
          // 刷新未绑定用户列表
          fetchUnboundUsers()
        } else {
          ElMessage.error(res.msg || '绑定失败')
        }
      } catch (error) {
        console.error('绑定失败:', error)
        ElMessage.error('绑定失败')
      }
    }
  })
}

// 家属绑定老人
const handleKinBindElder = async () => {
  kinBindFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await bindRelation({
          elderId: kinBindForm.elderId,
          kinId: kinBindForm.kinId,
          relationType: kinBindForm.relationType
        })
        
        if (res.code === 200) {
          ElMessage.success('绑定成功')
          resetForm('kinBindFormRef')
          // 刷新未绑定用户列表
          fetchUnboundUsers()
        } else {
          ElMessage.error(res.msg || '绑定失败')
        }
      } catch (error) {
        console.error('绑定失败:', error)
        ElMessage.error('绑定失败')
      }
    }
  })
}

// 查询绑定关系
const handleQueryRelations = async () => {
  if (!queryForm.userId) {
    ElMessage.warning('请选择用户')
    return
  }
  
  try {
    userLoading.value = true
    let res
    
    if (queryForm.type === 'elder') {
      // 查询老人的家属
      res = await getKinsByElderId(queryForm.userId)
      if (res.code === 200 && res.data) {
        relationList.value = res.data.map(kin => ({
          ...kin,
          relationType: '家属' // 这里应该从后端获取具体关系类型
        }))
      }
    } else {
      // 查询家属的老人
      res = await getEldersByKinId(queryForm.userId)
      if (res.code === 200 && res.data) {
        relationList.value = res.data.map(elder => ({
          ...elder,
          relationType: '老人' // 这里应该从后端获取具体关系类型
        }))
      }
    }
    
    if (relationList.value.length === 0) {
      ElMessage.info('未查询到绑定关系')
    }
  } catch (error) {
    console.error('查询绑定关系失败:', error)
    ElMessage.error('查询绑定关系失败')
  } finally {
    userLoading.value = false
  }
}

// 解绑关系
const handleUnbind = (user) => {
  currentUnbindUser.value = user
  unbindDialogVisible.value = true
}

// 确认解绑
const confirmUnbind = async () => {
  try {
    let elderId, kinId
    
    if (queryForm.type === 'elder') {
      // 当前查询的是老人的家属，所以当前用户是家属
      elderId = queryForm.userId
      kinId = currentUnbindUser.value.userId
    } else {
      // 当前查询的是家属的老人，所以当前用户是老人
      elderId = currentUnbindUser.value.userId
      kinId = queryForm.userId
    }
    
    const res = await unbindRelation(elderId, kinId)
    
    if (res.code === 200) {
      ElMessage.success('解绑成功')
      unbindDialogVisible.value = false
      // 刷新关系列表
      handleQueryRelations()
      // 刷新未绑定用户列表
      fetchUnboundUsers()
    } else {
      ElMessage.error(res.msg || '解绑失败')
    }
  } catch (error) {
    console.error('解绑失败:', error)
    ElMessage.error('解绑失败')
  }
}

// 重置表单
const resetForm = (formRef) => {
  if (formRef === 'elderBindFormRef') {
    elderBindFormRef.value.resetFields()
  } else if (formRef === 'kinBindFormRef') {
    kinBindFormRef.value.resetFields()
  }
}

// 重置查询表单
const resetQueryForm = () => {
  queryForm.userId = ''
  relationList.value = []
}

// 组件挂载时获取数据
onMounted(() => {
  fetchUnboundUsers()
})
</script>

<style scoped>
.elder-kin-binding {
  padding: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
