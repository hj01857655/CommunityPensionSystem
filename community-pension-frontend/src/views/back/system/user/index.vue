<template>
    <div class="app-container">
      <!-- 搜索条件 -->
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
        <el-form-item label="用户名称" prop="username">
          <el-input v-model="queryParams.username" placeholder="请输入用户名称" clearable style="width: 200px"
            @keyup.enter="handleQuery" />
        </el-form-item>
  
        <el-form-item label="状态" prop="isActive">
          <el-select v-model="queryParams.isActive" placeholder="用户状态" clearable style="width: 200px">
            <el-option label="正常" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="创建时间">
          <el-date-picker v-model="dateRange" style="width: 240px" value-format="YYYY-MM-DD" type="daterange"
            range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      <!-- 操作按钮 -->
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" plain :icon="Plus" @click="handleAdd">新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="success" plain :icon="Edit" :disabled="single" @click="handleTopUpdate">修改</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="danger" plain :icon="Delete" :disabled="multiple" @click="handleTopDelete">删除</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>
      <!-- 表格 -->
      <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" v-if="columns[0].visible" />
        <el-table-column label="用户编号" align="center" prop="userId" v-if="columns[1].visible" />
        <el-table-column label="用户名称" align="center" prop="username" v-if="columns[2].visible" />
        <el-table-column label="姓名" align="center" prop="name" v-if="columns[3].visible" />
        <el-table-column label="状态" align="center" prop="isActive" v-if="columns[4].visible">
          <template #default="scope">
            <el-switch 
              v-model="scope.row.isActive" 
              :active-value="1" 
              :inactive-value="0"
              :disabled="hasRole(scope.row, 4)" 
              @change="handleStatusChange(scope.row)" 
            />
          </template>
        </el-table-column>
        <el-table-column label="手机号码" align="center" prop="phone" v-if="columns[5].visible" />
        <el-table-column label="邮箱" align="center" prop="email" v-if="columns[6].visible" />
        <el-table-column label="角色" align="center" v-if="columns[7].visible">
          <template #default="scope">
            <el-tag 
              v-for="(roleName, index) in scope.row.roleNames" 
              :key="index"
              style="margin-right: 4px" 
              :type="getRoleTagType(scope.row.roleIds[index])"
            >
              {{ roleName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="绑定关系" align="center" v-if="columns[8].visible">
          <template #default="scope">
            <div v-if="hasRole(scope.row, 2)">
              <template v-if="scope.row.elders && scope.row.elders.length > 0">
                <div v-for="(elder, index) in scope.row.elders" :key="index" style="margin-bottom: 5px;">
                  <el-tag type="warning" effect="plain" style="cursor: pointer;" @click="handleBindingClick(scope.row, elder)">
                    {{ elder.relationType || '无关系' }} - {{ elder.name || '老人' }}
                  </el-tag>
                </div>
              </template>
              <span v-else class="link-text" @click="handleBindingClick(scope.row)">绑定老人</span> 
            </div>
            <div v-else-if="hasRole(scope.row, 1)">
              <template v-if="scope.row.kins && scope.row.kins.length > 0">
                <div v-for="(kin, index) in scope.row.kins" :key="index" style="margin-bottom: 5px;">
                  <el-tag type="success" effect="plain" style="cursor: pointer;" @click="handleBindingClick(scope.row, kin)">
                    {{ kin.relationType || '无关系' }} - {{ kin.name || '家属' }}
                  </el-tag>
                </div>
              </template>
              <span v-else class="link-text" @click="handleBindingClick(scope.row)">绑定家属</span>
            </div>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="180" v-if="columns[9].visible">
          <template #default="scope">
            <span>{{ formatDate(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150"
          v-if="columns[10].visible">
          <template #default="scope">
            <el-button type="primary" link :icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
            <el-button type="primary" link :icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
            <el-dropdown trigger="click" @command="(command) => handleMoreActions(command, scope.row)">
              <el-button type="primary" link>
                更多<el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="resetPassword">重置密码</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
  
      <!-- 分页组件 -->
      <Pagination
        v-if="total > 0"
        :total="total"
        :page="queryParams.current"
        :limit="queryParams.size"
        @pagination="getList"
      />
  
      <!-- 添加/修改用户对话框 -->
      <el-dialog 
        :title="dialogType === 'add' ? '添加用户' : (dialogType === 'binding' ? '绑定关系管理' : '修改用户')" 
        v-model="dialogVisible" 
        width="500px" 
        append-to-body
      >
        <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
          <el-form-item label="用户账号" prop="username">
            <el-input v-model="form.username" placeholder="用户账号" disabled />
          </el-form-item>
          <el-form-item label="角色" prop="roleId">
            <el-select v-model="form.roleId" placeholder="请选择角色" @change="handleRoleChange">
              <el-option label="老人" :value="1" />
              <el-option label="家属" :value="2" />
              <el-option label="社区工作人员" :value="3" />
              <el-option label="管理员" :value="4" />
            </el-select>
          </el-form-item>
  
          <!-- 基础信息（所有角色都显示） -->
          <el-form-item label="姓名" prop="name">
            <el-input v-model="form.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="手机号码" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入手机号码" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="form.email" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item label="状态" prop="isActive">
            <el-radio-group v-model="form.isActive" :disabled="form.roleId === 4">
              <el-radio :value="1">正常</el-radio>
              <el-radio :value="0">停用</el-radio>
            </el-radio-group>
          </el-form-item>
  
          <!-- 老人角色特定的表单项 -->
          <template v-if="form.roleId === 1">
            <el-form-item label="身份证号码" prop="idCard">
              <el-input v-model="form.idCard" placeholder="请输入身份证号码" />
            </el-form-item>
            <el-form-item label="出生日期" prop="birthday">
              <el-date-picker v-model="form.birthday" type="date" placeholder="选择出生日期" disabled />
            </el-form-item>
            <el-form-item label="年龄" prop="age">
              <el-input v-model="form.age" placeholder="请输入年龄" disabled />
            </el-form-item>
            <el-form-item label="紧急联系人姓名" prop="emergencyContactName">
              <el-input v-model="form.emergencyContactName" placeholder="请输入紧急联系人姓名" />
            </el-form-item>
            <el-form-item label="紧急联系人电话" prop="emergencyContactPhone">
              <el-input v-model="form.emergencyContactPhone" placeholder="请输入紧急联系人电话" />
            </el-form-item>
            <el-form-item label="健康状况" prop="healthCondition">
              <el-select v-model="form.healthCondition" placeholder="请选择健康状况">
                <el-option 
                  v-for="dict in healthLevelOptions" 
                  :key="dict.dictValue" 
                  :label="dict.dictLabel" 
                  :value="dict.dictValue" 
                />
              </el-select>
            </el-form-item>
          </template>
  
          <!-- 家属角色特定的表单项 -->
          <template v-if="form.roleId === 2">
            <el-form-item label="绑定老人" prop="elderId">
              <el-select v-model="form.elderId" placeholder="请选择要绑定的老人" clearable>
                <el-option
                  v-for="elder in elderList"
                  :key="elder.value"
                  :label="elder.label"
                  :value="elder.value"
                />
              </el-select>
              <el-button 
                v-if="dialogType === 'edit' && form.elderId" 
                type="danger" 
                link 
                style="margin-left: 10px"
                @click="handleUnbindElder"
              >
                解除绑定
              </el-button>
            </el-form-item>
            <el-form-item label="关系" prop="relationship">
              <el-select v-model="form.relationship" placeholder="请选择与老人的关系">
                <el-option label="子女" value="子女" />
                <el-option label="配偶" value="配偶" />
                <el-option label="兄弟姐妹" value="兄弟姐妹" />
                <el-option label="其他亲属" value="其他亲属" />
              </el-select>
            </el-form-item>
          </template>
  
          <!-- 社区工作人员角色特定的表单项 -->
          <template v-if="form.roleId === 3">
            <el-form-item label="部门" prop="department">
              <el-input v-model="form.department" placeholder="请输入部门" />
            </el-form-item>
            <el-form-item label="岗位" prop="position">
              <el-input v-model="form.position" placeholder="请输入岗位" />
            </el-form-item>
          </template>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button type="primary" @click="submitForm">{{ dialogType === 'binding' ? '保存绑定' : '确 定' }}</el-button>
            <el-button @click="cancel">取 消</el-button>
          </div>
        </template>
      </el-dialog>
  
      <!-- 用户详情对话框 -->
      <el-dialog 
        :title="detailForm && detailForm.isRelatedUser ? `绑定${detailForm.roleId === 1 ? '老人' : '家属'}详情` : '用户详情'" 
        v-model="detailDialogVisible" 
        width="500px" 
        append-to-body
      >
        <div v-loading="detailLoading" class="user-detail">
          <el-descriptions v-if="detailForm" :column="1" border>
            <el-descriptions-item label="用户账号">{{ detailForm.username }}</el-descriptions-item>
            <el-descriptions-item label="姓名">{{ detailForm.name }}</el-descriptions-item>
            <el-descriptions-item label="手机号码">{{ detailForm.phone }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ detailForm.email }}</el-descriptions-item>
            <el-descriptions-item label="角色">
              <el-tag 
                v-if="detailForm.roleId"
                style="margin-right: 4px" 
                :type="getRoleTagType(detailForm.roleId)"
              >
                {{ roleMap[detailForm.roleId] }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              {{ detailForm.isActive === 1 ? '正常' : '停用' }}
            </el-descriptions-item>
            
            <!-- 老人特有信息 -->
            <template v-if="detailForm.roleId === 1">
              <el-descriptions-item label="身份证号码">{{ detailForm.idCard || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="出生日期">{{ detailForm.birthday || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="年龄">{{ detailForm.age || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="健康状况">{{ getHealthLevelLabel(detailForm.healthCondition) || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="紧急联系人">{{ detailForm.emergencyContactName || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="紧急联系电话">{{ detailForm.emergencyContactPhone || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="绑定家属">
                <div v-if="detailForm.kins && detailForm.kins.length > 0">
                  <div v-for="(kin, index) in detailForm.kins" :key="index" class="mb-1">
                    <el-tag type="success" effect="plain" style="cursor: pointer;" @click="viewUserDetail(kin.userId)">
                      {{ kin.relationType || '无关系' }} - {{ kin.name || '家属' }}
                    </el-tag>
                  </div>
                </div>
                <span v-else>无绑定家属</span>
              </el-descriptions-item>
            </template>
            
            <!-- 家属特有信息 -->
            <template v-if="detailForm.roleId === 2">
              <el-descriptions-item label="绑定老人">
                <div v-if="detailForm.elders && detailForm.elders.length > 0">
                  <div v-for="(elder, index) in detailForm.elders" :key="index" class="mb-1">
                    <el-tag type="warning" effect="plain" style="cursor: pointer;" @click="viewUserDetail(elder.userId)">
                      {{ elder.relationType || '无关系' }} - {{ elder.name || '老人' }}
                    </el-tag>
                  </div>
                </div>
                <span v-else>无绑定老人</span>
              </el-descriptions-item>
            </template>
            
            <!-- 社区工作人员特有信息 -->
            <template v-if="detailForm.roleId === 3">
              <el-descriptions-item label="部门">{{ detailForm.department || '未填写' }}</el-descriptions-item>
              <el-descriptions-item label="岗位">{{ detailForm.position || '未填写' }}</el-descriptions-item>
            </template>
            
            <el-descriptions-item label="创建时间">{{ formatDate(detailForm.createTime) }}</el-descriptions-item>
          </el-descriptions>
        </div>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="detailDialogVisible = false">关 闭</el-button>
            <el-button type="primary" @click="handleEditFromDetail">编 辑</el-button>
          </div>
        </template>
      </el-dialog>
      
      <!-- 添加新的绑定关系对话框 -->
      <el-dialog 
        :title="bindType === 'elder' ? '绑定老人' : '绑定家属'" 
        v-model="bindDialogVisible" 
        width="500px" 
        append-to-body
      >
        <div v-loading="bindLoading">
          <!-- 家属绑定老人 -->
          <template v-if="bindType === 'elder'">
            <el-form ref="bindFormRef" :model="bindForm" label-width="80px">
              <el-form-item label="家属信息">
                <div>{{ bindForm.name }} ({{ bindForm.phone || '未填写电话' }})</div>
              </el-form-item>
              <el-form-item label="选择老人" prop="targetId">
                <el-select v-model="bindForm.targetId" placeholder="请选择要绑定的老人" filterable style="width: 100%">
                  <el-option
                    v-for="elder in availableElderList"
                    :key="elder.value"
                    :label="elder.label"
                    :value="elder.value"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="关系类型" prop="relationType">
                <el-select v-model="bindForm.relationType" placeholder="请选择与老人的关系" style="width: 100%">
                  <el-option label="子女" value="子女" />
                  <el-option label="配偶" value="配偶" />
                  <el-option label="兄弟姐妹" value="兄弟姐妹" />
                  <el-option label="其他亲属" value="其他亲属" />
                </el-select>
              </el-form-item>
            </el-form>
          </template>
          
          <!-- 老人绑定家属 -->
          <template v-else-if="bindType === 'kin'">
            <el-form ref="bindFormRef" :model="bindForm" label-width="80px">
              <el-form-item label="老人信息">
                <div>{{ bindForm.name }} ({{ bindForm.idCard || '未填写身份证' }})</div>
              </el-form-item>
              <el-form-item label="选择家属" prop="targetId">
                <el-select v-model="bindForm.targetId" placeholder="请选择要绑定的家属" filterable style="width: 100%">
                  <el-option
                    v-for="kin in availableKinList"
                    :key="kin.value"
                    :label="kin.label"
                    :value="kin.value"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="关系类型" prop="relationType">
                <el-select v-model="bindForm.relationType" placeholder="请选择与家属的关系" style="width: 100%">
                  <el-option label="子女" value="子女" />
                  <el-option label="配偶" value="配偶" />
                  <el-option label="兄弟姐妹" value="兄弟姐妹" />
                  <el-option label="其他亲属" value="其他亲属" />
                </el-select>
              </el-form-item>
            </el-form>
          </template>
          
          <!-- 已绑定关系列表 -->
          <div v-if="currentBindings.length > 0" class="mt-3">
            <div class="bind-list-title">已绑定{{ bindType === 'elder' ? '老人' : '家属' }}:</div>
            <el-table :data="currentBindings" style="width: 100%" border stripe>
              <el-table-column prop="name" :label="bindType === 'elder' ? '老人姓名' : '家属姓名'" />
              <el-table-column prop="relationType" label="关系类型" />
              <el-table-column label="操作" width="100" align="center">
                <template #default="scope">
                  <el-button type="danger" link @click="handleUnbindRelation(scope.row.userId)">
                    解绑
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
        <template #footer>
          <div class="dialog-footer">
            <el-button type="primary" @click="submitBindForm">确定绑定</el-button>
            <el-button @click="bindDialogVisible = false">关 闭</el-button>
          </div>
        </template>
      </el-dialog>
      
    </div>
  </template>
  
  <script setup>
  import { getDictDataByType } from '@/api/back/system/dict/data';
import RightToolbar from '@/components/common/base/RightToolbar/index.vue';
import Pagination from '@/components/common/table/Pagination.vue';
import { useUserStore } from '@/stores/back/userStore';
import { formatDate } from '@/utils/date';
import { ArrowDown, Delete, Edit, Plus, Refresh, Search } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { storeToRefs } from 'pinia';
import { pinyin } from 'pinyin-pro';
import { onMounted, reactive, ref, watch } from 'vue';
  
  const userStore = useUserStore();
  const { userList, total, loading } = storeToRefs(userStore);
  
  // 添加老人列表
  const elderList = ref([]);
  
  // 查询参数
  const queryParams = reactive({
    current: 1,
    size: 10,
    username: '',
    isActive: undefined,
    startTime: '',
    endTime: ''
  });
  
  // 日期范围
  const dateRange = ref([]);
  
  // 显示搜索条件
  const showSearch = ref(true);
  
  // 非单个禁用
  const single = ref(true);
  // 非多个禁用
  const multiple = ref(true);
  // 存储选中的用户
  const selectedUsers = ref([]);
  
  // 列信息
  const columns = ref([
    { key: 0, label: '选择', visible: true },
    { key: 1, label: '用户编号', visible: true },
    { key: 2, label: '用户名称', visible: true },
    { key: 3, label: '姓名', visible: true },
    { key: 4, label: '状态', visible: true },
    { key: 5, label: '手机号码', visible: true },
    { key: 6, label: '邮箱', visible: true },
    { key: 7, label: '角色', visible: true },
    { key: 8, label: '绑定关系', visible: true },
    { key: 9, label: '创建时间', visible: true },
    { key: 10, label: '操作', visible: true }
  ]);
  
  // 添加角色映射
  const roleMap = {
    1: '老人',
    2: '家属',
    3: '社区工作人员',
    4: '管理员'
  };
  
  // 判断用户是否拥有某角色
  const hasRole = (user, roleId) => {
    return user.roleId === roleId || (user.roleIds && user.roleIds.includes(roleId));
  };
  
  // 获取列表数据
  const getList = async () => {
    await userStore.fetchUsers(queryParams);
    
    // 处理用户绑定关系信息
    const promises = userList.value.map(async (user) => {
      // 确保roleIds不为null
      if (!user.roleIds) {
        user.roleIds = [];
      }
  
      // 如果是家属角色
      if (hasRole(user, 2)) {
        try {
          // 获取家属绑定的所有老人
          const elders = await userStore.fetchEldersByKinId(user.userId);
          if (elders && elders.length > 0) {
            user.elders = elders;
          } else {
            user.elders = [];
          }
        } catch (error) {
          console.error(`获取家属${user.userId}绑定的老人信息失败:`, error);
          user.elders = [];
        }
      }
  
      // 如果是老人角色
      if (hasRole(user, 1)) {
        try {
          // 获取老人绑定的所有家属
          const kins = await userStore.fetchKinsByElderId(user.userId);
          if (kins && kins.length > 0) {
            user.kins = kins;
          } else {
            user.kins = [];
          }
        } catch (error) {
          console.error(`获取老人${user.userId}绑定的家属信息失败:`, error);
          user.kins = [];
        }
      }
    });
    
    // 等待所有异步操作完成
    await Promise.all(promises);
  };
  
  // 搜索按钮操作
  const handleQuery = () => {
    queryParams.current = 1;
    // 处理日期范围
    if (dateRange.value && dateRange.value.length === 2) {
      queryParams.startTime = dateRange.value[0];
      queryParams.endTime = dateRange.value[1];
    } else {
      queryParams.startTime = '';
      queryParams.endTime = '';
    }
    getList();
  };
  
  // 重置按钮操作
  const resetQuery = () => {
    dateRange.value = [];
    queryParams.username = '';
    queryParams.isActive = undefined;
    queryParams.startTime = '';
    queryParams.endTime = '';
    handleQuery();
  };
  
  // 多选框选中数据
  const handleSelectionChange = (selection) => {
    selectedUsers.value = selection;
    single.value = selection.length !== 1;
    multiple.value = !selection.length;
  };
  
  // 状态修改
  const handleStatusChange = async (row) => {
    // 如果是管理员角色，不允许修改状态
    if (hasRole(row, 4)) {
      ElMessage.warning('管理员用户状态不允许修改');
      // 恢复原来的状态值
      row.isActive = 1; // 管理员默认为正常状态
      return;
    }

    const text = row.isActive === 1 ? '启用' : '停用';
    try {
      // 使用handleUpdateStatus函数更新用户状态
      const res = await userStore.handleUpdateStatus(row.userId, row.isActive);
  
      if (!res) {
        // 如果更新失败，恢复原来的状态值
        row.isActive = row.isActive === 1 ? 0 : 1;
      }
    } catch (error) {
      // 如果用户取消操作，恢复原来的状态值
      if (error !== 'cancel') {
        console.error('更新用户状态失败:', error);
        row.isActive = row.isActive === 1 ? 0 : 1;
      }
    }
  };
  
  // 更多操作触发
  const handleMoreActions = (command, row) => {
    switch (command) {
      case 'resetPassword':
        handleResetPassword(row);
        break;
    }
  };
  
  // 重置密码
  const handleResetPassword = async (row) => {
    try {
      await ElMessageBox.confirm(`确认重置"${row.username}"的密码吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      });
      await userStore.handleResetPassword(row.userId);
    } catch (error) {
      if (error !== 'cancel') {
        console.error('重置密码操作失败:', error);
      }
    }
  };
  
 
  
  // 对话框相关
  const dialogType = ref('add');
  const dialogVisible = ref(false);
  const roleDialogVisible = ref(false);
  const detailDialogVisible = ref(false);
  const detailForm = ref(null);
  const detailLoading = ref(false);
  const formRef = ref(null);
  
  // 家属绑定相关
  const kinBindDialogVisible = ref(false);
  const kinBindForm = ref({
    elderId: null,
    kinId: null,
    relationType: ''
  });
  const kinList = ref([]);
  
  const form = ref({
    userId: undefined,
    username: '',
    name: '',
    password: '',
    phone: '',
    email: '',
    isActive: 1,
    roleId: null,
    elderId: null,
    relationship: '',
    department: '',
    position: '',
    idCard: '',
    birthday: '',
    age: '',
    emergencyContactName: '',
    emergencyContactPhone: '',
    healthCondition: ''
  });
  const roleForm = ref({
    userId: undefined,
    username: undefined,
    roleIds: []
  });
  const roles = [
    { value: 1, label: '老人' },
    { value: 2, label: '家属' },
    { value: 3, label: '社区工作人员' },
    { value: 4, label: '管理员' }
  ];
  
  const isKin = ref(false);
  const isStaff = ref(false);
  const isElder = ref(false);
  const handleRoleChange = (roleId) => {
    // 将选择的角色ID转换为数组格式
    form.value.roleId = roleId;
    
    // 更新角色状态
    isKin.value = roleId === 2;
    isStaff.value = roleId === 3;
    isElder.value = roleId === 1;
  
    // 如果是家属角色，获取可绑定的老人列表
    if (isKin.value) {  
      getElderList();
    } else {
      // 如果不是家属角色，清空老人相关字段
      form.value.elderId = null;
      form.value.relationship = '';
      elderList.value = [];
    }
  };
  
  // 监听身份证号码变化并计算出生日期和年龄
  watch(() => form.value.idCard, (newIdCard) => {
    if (newIdCard && newIdCard.length === 18) {
      // 从身份证提取年月日
      const year = newIdCard.substring(6, 10);
      const month = newIdCard.substring(10, 12);
      const day = newIdCard.substring(12, 14);
      
      // 构造符合后端LocalDate格式的日期字符串 YYYY-MM-DD
      form.value.birthday = `${year}-${month}-${day}`;
      
      // 计算年龄
      const currentYear = new Date().getFullYear();
      form.value.age = currentYear - parseInt(year);
    } else {
      form.value.birthday = '';
      form.value.age = '';
    }
  });
  //监听姓名变化生成用户账号(姓氏拼音首字母 + 名字拼音)
  watch(() => form.value.name, (newName) => {
    if (newName) {
      // 分割姓和名
      const firstName = newName.charAt(0); // 获取第一个字作为姓
      const lastName = newName.slice(1);   // 剩余部分作为名
      
      // 获取姓的拼音首字母（大写）
      const firstPinyin = pinyin(firstName, { pattern: 'first' }).toUpperCase();
      // 获取名字的完整拼音（不带声调）
      const lastNamePinyin = pinyin(lastName, { toneType: 'none' }).replace(/\s/g, '');
      
      // 组合：姓氏拼音首字母 + 名字拼音
      form.value.username = `${firstPinyin}${lastNamePinyin}`;
    } else {
      form.value.username = '';
    }
  });
  // 表单校验规则
  const rules = {
    username: [
      { required: true, message: '请输入用户名称', trigger: 'blur' },
      { min: 4, max: 50, message: '用户名长度必须在4-50个字符之间', trigger: 'blur' }
    ],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
    name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
    phone: [
      { required: true, message: '请输入手机号码', trigger: 'blur' },
      { pattern: /^1[3-9]\d{9}$/, message: '手机号码格式不正确', trigger: 'blur' }
    ],
    email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }],
    roleId: [{ required: true, message: '请选择角色', trigger: 'change' }]
  };
  
  // 取消按钮
  const cancel = () => {
    dialogVisible.value = false;
    reset();
  };
  
  // 表单重置
  const reset = () => {
    form.value = {
      userId: undefined,
      username: '',
      name: '',
      password: '123456',  // 设置默认密码
      phone: '',
      email: '',
      isActive: 1,
      roleId: null,
      elderId: null,
      relationship: '',
      department: '',
      position: '',
      idCard: '',
      birthday: '',
      age: '',
      emergencyContactName: '',
      emergencyContactPhone: '',
      healthCondition: ''
    };
    formRef.value?.resetFields();
    // 重置角色状态
    isKin.value = false;
    isStaff.value = false;
    isElder.value = false;
  };
  
  // 新增按钮操作
  const handleAdd = () => {
    dialogType.value = 'add';
    reset();
    dialogVisible.value = true;
  };
  
  // 顶部工具栏修改按钮操作
  const handleTopUpdate = () => {
    if (selectedUsers.value && selectedUsers.value.length === 1) {
      handleUpdate(selectedUsers.value[0]);
    } else {
      ElMessage.warning('请选择一个用户');
    }
  };

  // 顶部工具栏删除按钮操作
  const handleTopDelete = () => {
    if (selectedUsers.value && selectedUsers.value.length >= 1) {
      handleDelete(selectedUsers.value[0]);
    } else {
      ElMessage.warning('请选择要删除的用户');
    }
  };
  
  // 修改按钮操作
  const handleUpdate = async (row) => {
    try {
      dialogType.value = 'edit';
      reset();
  
      // 获取用户详细信息
      const userResponse = await userStore.handleGetUserInfo(row.userId);
      if (!userResponse?.data) {
        ElMessage.error('获取用户信息失败');
        return;
      }
  
      const userData = userResponse.data;
      // 获取用户角色（兼容后端可能返回的不同格式）
      let roleId = userData.roleId;
      if (!roleId && userData.roleIds && userData.roleIds.length > 0) {
        roleId = userData.roleIds[0];
      }
      
      // 如果是家属角色，则获取与老人的关系信息
      let relationshipInfo = '';
      if (roleId === 2 && row.elders && row.elders.length > 0) {
        relationshipInfo = row.elders[0].relationType || '';
      }
      
      // 填充表单数据
      form.value = {
        ...userData,
        userId: row.userId, // 确保设置userId
        roleId: roleId, // 设置单个角色ID
        elderId: userData.bindElderIds?.[0] || null, // 获取绑定的老人ID
        relationship: relationshipInfo || userData.relationType || '' // 获取关系类型
      };
  
      // 更新角色状态
      isKin.value = roleId === 2;
      isStaff.value = roleId === 3;
      isElder.value = roleId === 1;
  
      // 如果是家属角色，获取可绑定的老人列表
      if (isKin.value) {
        await getElderList();
      }
  
      dialogVisible.value = true;
    } catch (error) {
      if (error !== 'cancel') {
        console.error('获取用户信息失败:', error);
        ElMessage.error('获取用户信息失败');
      }
    }
  };
  
  // 删除按钮操作
  const handleDelete = async (row) => {
    try {
      await ElMessageBox.confirm(`是否确认删除用户"${row.username}"？`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      });
  
      const success = await userStore.handleDeleteUser(row.userId);
      if (success) {
        await getList();
      }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('删除操作失败:', error);
      }
    }
  };
  
  // 表单提交
  const submitForm = () => {
    formRef.value?.validate(async (valid) => {
      if (valid) {
        try {
          // 添加额外验证，确保username不为空
          if (!form.value.username || form.value.username.trim() === '') {
            ElMessage.error('用户名不能为空，请先输入姓名');
            return;
          }
          
          // 创建一个新的表单对象，避免修改原始表单
          let formToSubmit = {};
          
          // 如果是家属角色
          if (form.value.roleId === 2) {
            // 仅保留基本信息，不传递绑定相关信息
            const { 
              userId, username, name, password, phone, email, 
              isActive, roleId
            } = form.value;
            
            formToSubmit = { 
              userId, username, name, password, phone, email, 
              isActive, roleId
            };
            
            // 单独处理绑定关系，如果设置了新的elderId和relationship，并且之前没有绑定过
            if (form.value.elderId && form.value.relationship) {
              // 获取当前用户绑定的老人列表
              const existingElders = await userStore.fetchEldersByKinId(form.value.userId);
              
              // 检查是否已经绑定了相同的老人
              const alreadyBound = existingElders && existingElders.some(
                elder => elder.userId === form.value.elderId
              );
              
              // 如果没有绑定过，则在用户更新成功后进行绑定
              if (!alreadyBound && form.value.elderId) {
                // 在提交后会单独调用绑定接口
                try {
                  await userStore.handleBindElderKinRelation(
                    form.value.elderId,  // 老人ID
                    form.value.userId,   // 家属ID
                    form.value.relationship
                  );
                  ElMessage.success(dialogType.value === 'add' ? '新增用户并绑定老人成功' : '更新用户信息并绑定老人成功');
                } catch (bindError) {
                  console.error('绑定老人失败:', bindError);
                  ElMessage.warning(dialogType.value === 'add' ? '用户新增成功，但绑定老人失败' : '用户信息更新成功，但绑定老人失败');
                }
              }
            }
          }
          // 如果是老人角色
          else if (form.value.roleId === 1) {
            // 老人角色只保留老人相关信息
            const {
              userId, username, name, password, phone, email,
              isActive, roleId, idCard, birthday, age,
              emergencyContactName, emergencyContactPhone, healthCondition
            } = form.value;
            
            formToSubmit = {
              userId, username, name, password, phone, email,
              isActive, roleId, idCard, birthday, age,
              emergencyContactName, emergencyContactPhone, healthCondition
            };
          }
          // 如果是社区工作人员角色
          else if (form.value.roleId === 3) {
            // 社区工作人员角色包含department和position字段
            const {
              userId, username, name, password, phone, email,
              isActive, roleId, department, position
            } = form.value;
            
            formToSubmit = {
              userId, username, name, password, phone, email,
              isActive, roleId, department, position
            };
          }
          // 如果是管理员角色
          else if (form.value.roleId === 4) {
            // 管理员角色只保留基本信息，并且状态始终为启用
            const {
              userId, username, name, password, phone, email,
              roleId
            } = form.value;
            
            formToSubmit = {
              userId, username, name, password, phone, email,
              isActive: 1, // 管理员始终为启用状态
              roleId
            };
          }
          // 其他角色
          else {
            // 其他角色只保留基本信息
            const {
              userId, username, name, password, phone, email,
              isActive, roleId
            } = form.value;
            
            formToSubmit = {
              userId, username, name, password, phone, email,
              isActive, roleId
            };
          }

          let success = false;
          
          // 区分新增和更新操作
          if (dialogType.value === 'add') {
            // 新增用户不需要userId
            const { userId, ...addData } = formToSubmit;
            
            // 确保密码字段存在，如果不存在则使用默认密码
            if (!addData.password) {
              addData.password = '123456';
            }
            
            // 记录发送到后端的数据
            console.log('新增用户准备提交的数据:', JSON.stringify(addData, null, 2));
            
            const res = await userStore.handleAddUser(addData);
            success = !!res;
            
            // 如果新增成功，获取返回的userId
            if (success && res.data) {
              form.value.userId = res.data;
            }
          } else {
            // 更新用户需要userId
            console.log('更新用户准备提交的数据:', JSON.stringify(formToSubmit, null, 2));
            success = await userStore.handleUpdateUser(formToSubmit);
          }
  
          if (success) {
            ElMessage.success(dialogType.value === 'add' ? '新增用户成功' : '更新用户信息成功');
            
            dialogVisible.value = false;
            await getList();
          }
        } catch (error) {
          if (error !== 'cancel') {
            console.error('提交表单失败:', error);
            // 检查是否是主键冲突的错误
            if (error.message && error.message.includes('Duplicate entry') && error.message.includes('elder_kin_relation.PRIMARY')) {
              ElMessage.error('系统自动绑定关系失败：该绑定关系已存在');
            } else {
              ElMessage.error('提交表单失败');
            }
          }
        }
      }
    });
  };
  
  // 角色表单提交
  const submitRoleForm = async () => {
    const success = await userStore.assignUserRole(
      roleForm.value.userId,
      roleForm.value.roleIds
    );
  
    if (success) {
      roleDialogVisible.value = false;
      await getList();
    }
  };
  
  // 获取未绑定家属的老人列表
  const getElderList = async () => {
    try {
      const response = await userStore.fetchElderList();
      if (response?.data) {
        // 处理未绑定老人的列表
        elderList.value = response.data.map(elder => ({
          value: elder.userId,
          label: `${elder.name}（${elder.idCard ? elder.idCard : '未填写身份证'}）`
        }));
        
        // 如果是编辑模式，需要获取当前绑定的老人信息
        if (dialogType.value === 'edit' && form.value.elderId) {
          const elderResponse = await userStore.handleGetUserInfo(form.value.elderId);
          if (elderResponse?.data) {
            // 检查是否已经在列表中
            const existingElder = elderList.value.find(item => item.value === form.value.elderId);
            if (!existingElder) {
              // 如果当前绑定的老人不在未绑定列表中，添加到列表开头
              elderList.value.unshift({
                value: elderResponse.data.userId,
                label: `${elderResponse.data.name}（${elderResponse.data.idCard ? elderResponse.data.idCard : '未填写身份证'}）`
              });
            }
          }
        }
      } else {
        elderList.value = [];
        if (!form.value.elderId) {
          ElMessage.warning('暂无可绑定的老人');
        }
      }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('获取老人列表失败:', error);
        ElMessage.error('获取老人列表失败');
      }
      elderList.value = [];
    }
  };
  
  // 解除家属与老人的绑定
  const handleUnbindElder = async () => {
    try {
      await ElMessageBox.confirm('确认要解除与当前老人的绑定关系吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      });
      
      // 暂存当前家属ID和老人ID
      const kinId = form.value.userId;
      const elderId = form.value.elderId;
      
      // 调用解除绑定接口
      const success = await userStore.unbindElder(kinId);
      
      if (success) {
        ElMessage.success('解除绑定成功');
        form.value.elderId = null;
        form.value.relationship = '';
        
        // 重新获取老人列表
        await getElderList();
        
        // 刷新主列表
        await getList();
      } else {
        ElMessage.error('解除绑定失败');
      }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('解除绑定操作失败:', error);
      }
    }
  };
  
  // 在script setup部分添加getRoleTagType方法
  const getRoleTagType = (roleId) => {
    switch (roleId) {
      case 1: // 老人
        return 'success';
      case 2: // 家属
        return 'warning';
      case 3: // 社区工作人员
        return 'info';
      case 4: // 管理员
        return 'danger';
      default:
        return 'info';
    }
  };
  
  // 获取未绑定老人的家属列表
  const getUnboundKinsList = async () => {
    try {
      const response = await userStore.fetchUnboundKins();
      if (response?.data) {
        // 处理未绑定老人的家属列表
      }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('获取未绑定老人的家属列表失败:', error);
        ElMessage.error('获取未绑定老人的家属列表失败');
      }
    }
  };
  
  // 绑定老人和家属关系
  const handleBindElderKin = async (elderId, kinId, relationType) => {
    try {
      const success = await userStore.handleBindElderKinRelation(elderId, kinId, relationType);
      if (success) {
        ElMessage.success('绑定关系成功');
        // 刷新列表
        await getList();
      }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('绑定关系失败:', error);
        ElMessage.error('绑定关系失败');
      }
    }
  };
  
  // 解绑老人和家属关系
  const handleUnbindElderKin = async (elderId, kinId) => {
    try {
      const success = await userStore.handleUnbindElderKinRelation(elderId, kinId);
      if (success) {
        ElMessage.success('解绑关系成功');
        // 刷新列表
        await getList();
      }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('解绑关系失败:', error);
        ElMessage.error('解绑关系失败');
      }
    }
  };
  
  // 编辑用户详情
  const handleEditFromDetail = () => {
    if (!detailForm.value) return;
    
    // 关闭详情对话框
    detailDialogVisible.value = false;
    
    // 创建一个包含必要信息的行对象，以供handleUpdate使用
    const row = {
      userId: detailForm.value.userId,
      username: detailForm.value.username,
      roleId: detailForm.value.roleId || null,
      // 如果详情页中已加载了绑定关系，则传递给编辑页面
      kins: detailForm.value.kins || [],
      elders: detailForm.value.elders || []
    };
    
    // 延迟一下再打开编辑对话框，避免两个对话框同时操作的问题
    setTimeout(() => {
      handleUpdate(row);
    }, 300);
  };
  
  // 查看用户详情
  const viewUserDetail = async (userId, isRelatedUser = false) => {
    try {
      // 先关闭当前详情对话框，避免多层嵌套
      if (detailDialogVisible.value) {
        detailDialogVisible.value = false;
        // 延迟一下再打开新的详情对话框
        await new Promise(resolve => setTimeout(resolve, 300));
      }
      
      detailDialogVisible.value = true;
      detailLoading.value = true;
      
      // 获取用户详细信息
      const response = await userStore.handleGetUserInfo(userId);
      if (!response?.data) {
        ElMessage.error('获取用户详情失败');
        detailDialogVisible.value = false;
        return;
      }
      
      const userData = response.data;
      const roleIds = userData.roleIds || [];
      
      // 获取绑定关系信息
      let userWithRelations = { 
        ...userData,
        isRelatedUser   // 标记是否为关联用户（绑定关系中的另一方）
      };
      
      // 并行获取绑定关系
      const promises = [];
      
      // 如果是老人角色，获取其绑定的家属信息
      if (roleIds.includes(1)) {
        promises.push(
          userStore.fetchKinsByElderId(userId)
            .then(kins => {
              userWithRelations.kins = kins || [];
            })
            .catch(error => {
              if (error !== 'cancel') {
                console.error('获取绑定家属信息失败:', error);
              }
              userWithRelations.kins = [];
            })
        );
      }
      
      // 如果是家属角色，获取其绑定的老人信息
      if (roleIds.includes(2)) {
        promises.push(
          userStore.fetchEldersByKinId(userId)
            .then(elders => {
              userWithRelations.elders = elders || [];
            })
            .catch(error => {
              if (error !== 'cancel') {
                console.error('获取绑定老人信息失败:', error);
              }
              userWithRelations.elders = [];
            })
        );
      }
      
      // 等待所有绑定关系加载完成
      await Promise.all(promises);
      
      // 更新详情表单
      detailForm.value = userWithRelations;
      
    } catch (error) {
      if (error !== 'cancel') {
        console.error('获取用户详情失败:', error);
        ElMessage.error('获取用户详情失败');
      }
    } finally {
      detailLoading.value = false;
    }
  };
  
  // 绑定关系点击
  const handleBindingClick = async (user, relatedUser) => {
    try {
      // 如果是点击绑定关系标签，应该显示用户详情而不是编辑页面
      if (relatedUser) {
        // 如果点击的是关联用户标签，显示关联用户的详情
        await viewUserDetail(relatedUser.userId, true);  // 添加标记表示这是关联用户
      } else {
        // 如果点击的是"绑定老人"或"绑定家属"文本，打开绑定管理对话框
        // 获取用户详细信息
        const userResponse = await userStore.handleGetUserInfo(user.userId);
        if (!userResponse?.data) {
          ElMessage.error('获取用户信息失败');
          return;
        }
        
        const userData = userResponse.data;
        
        // 重置绑定表单
        bindForm.value = {
          userId: userData.userId,
          name: userData.name,
          phone: userData.phone,
          idCard: userData.idCard,
          targetId: null,
          relationType: ''
        };
        
        // 根据角色类型确定绑定类型
        if (hasRole(user, 2)) {
          // 家属绑定老人
          bindType.value = 'elder';
          bindLoading.value = true;
          
          // 获取家属已绑定的老人列表
          const elders = await userStore.fetchEldersByKinId(user.userId);
          currentBindings.value = elders || [];
          
          // 获取可绑定的老人列表
          await getAvailableElderList();
          
          bindLoading.value = false;
        } else if (hasRole(user, 1)) {
          // 老人绑定家属
          bindType.value = 'kin';
          bindLoading.value = true;
          
          // 获取老人已绑定的家属列表
          const kins = await userStore.fetchKinsByElderId(user.userId);
          currentBindings.value = kins || [];
          
          // 获取可绑定的家属列表
          await getAvailableKinList();
          
          bindLoading.value = false;
        }
        
        // 显示绑定关系对话框
        bindDialogVisible.value = true;
      }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('处理绑定关系失败:', error);
        ElMessage.error('处理绑定关系失败');
      }
      bindLoading.value = false;
    }
  };
  
  // 修复submitBindForm方法
  const submitBindForm = async () => {
    try {
      if (!bindForm.value.targetId) {
        ElMessage.warning(`请选择要绑定的${bindType.value === 'elder' ? '老人' : '家属'}`);
        return;
      }
      if (!bindForm.value.relationType) {
        ElMessage.warning('请选择关系类型');
        return;
      }
      
      // 在绑定前先检查是否已经存在相同的绑定关系
      let existingRelations = [];
      
      if (bindType.value === 'elder') {
        // 家属绑定老人 - 检查当前家属是否已经绑定了要选择的老人
        existingRelations = await userStore.fetchEldersByKinId(bindForm.value.userId);
        const alreadyBound = existingRelations && existingRelations.some(
          elder => elder.userId === bindForm.value.targetId
        );
        
        if (alreadyBound) {
          ElMessage.warning('已经绑定了该老人，无需重复绑定');
          return;
        }
      } else {
        // 老人绑定家属 - 检查当前老人是否已经绑定了要选择的家属
        existingRelations = await userStore.fetchKinsByElderId(bindForm.value.userId);
        const alreadyBound = existingRelations && existingRelations.some(
          kin => kin.userId === bindForm.value.targetId
        );
        
        if (alreadyBound) {
          ElMessage.warning('已经绑定了该家属，无需重复绑定');
          return;
        }
      }
      
      // 没有重复绑定，执行绑定操作
      let success;
      if (bindType.value === 'elder') {
        // 家属绑定老人
        success = await userStore.handleBindElderKinRelation(
          bindForm.value.targetId,  // 老人ID
          bindForm.value.userId,    // 家属ID
          bindForm.value.relationType
        );
      } else {
        // 老人绑定家属
        success = await userStore.handleBindElderKinRelation(
          bindForm.value.userId,    // 老人ID
          bindForm.value.targetId,  // 家属ID
          bindForm.value.relationType
        );
      }
      
      if (success) {
        ElMessage.success('绑定关系成功');
        
        // 刷新绑定列表
        if (bindType.value === 'elder') {
          const elders = await userStore.fetchEldersByKinId(bindForm.value.userId);
          currentBindings.value = elders || [];
        } else {
          const kins = await userStore.fetchKinsByElderId(bindForm.value.userId);
          currentBindings.value = kins || [];
        }
        
        // 重置表单
        bindForm.value.targetId = null;
        bindForm.value.relationType = '';
        
        // 刷新主列表
        await getList();
      } else {
        ElMessage.error('绑定关系失败');
      }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('提交绑定关系失败:', error);
        // 检查是否是主键冲突的错误
        if (error.message && error.message.includes('Duplicate entry') && error.message.includes('elder_kin_relation.PRIMARY')) {
          ElMessage.error('已存在相同的绑定关系，无需重复绑定');
        } else {
          ElMessage.error('提交绑定关系失败');
        }
      }
    }
  };
  
  // 修复handleUnbindRelation方法
  const handleUnbindRelation = async (targetUserId) => {
    try {
      await ElMessageBox.confirm('确认要解除此绑定关系吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      });
      
      let success;
      if (bindType.value === 'elder') {
        // 解绑家属和老人的关系
        success = await userStore.handleUnbindElderKinRelation(
          targetUserId,  // 老人ID
          bindForm.value.userId  // 家属ID
        );
      } else {
        // 解绑老人和家属的关系
        success = await userStore.handleUnbindElderKinRelation(
          bindForm.value.userId,  // 老人ID
          targetUserId  // 家属ID
        );
      }
      
      if (success) {
        ElMessage.success('解除绑定成功');
        
        // 刷新绑定列表
        if (bindType.value === 'elder') {
          const elders = await userStore.fetchEldersByKinId(bindForm.value.userId);
          currentBindings.value = elders || [];
        } else {
          const kins = await userStore.fetchKinsByElderId(bindForm.value.userId);
          currentBindings.value = kins || [];
        }
        
        // 刷新主列表
        await getList();
      } else {
        ElMessage.error('解除绑定失败');
      }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('解除绑定操作失败:', error);
      }
    }
  };
  
  // 显示家属绑定对话框
  const showKinBindDialog = async () => {
    try {
      // 获取未绑定老人的家属列表
      const response = await userStore.fetchUnboundKins();
      if (response?.data) {
        kinList.value = response.data.map(kin => ({
          value: kin.userId,
          label: `${kin.name}（${kin.phone || '未填写电话'}）`
        }));
        
        // 重置绑定表单
        kinBindForm.value = {
          elderId: form.value.userId,
          kinId: null,
          relationType: ''
        };
        
        // 显示绑定对话框
        kinBindDialogVisible.value = true;
      } else {
        ElMessage.warning('暂无可绑定的家属');
      }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('获取家属列表失败:', error);
        ElMessage.error('获取家属列表失败');
      }
    }
  };
  
  // 提交家属绑定
  const submitKinBind = async () => {
    // 基本验证
    if (!kinBindForm.value.kinId) {
      ElMessage.warning('请选择要绑定的家属');
      return;
    }
    if (!kinBindForm.value.relationType) {
      ElMessage.warning('请选择关系类型');
      return;
    }
    
    try {
      // 检查是否已存在相同的绑定关系
      const existingKins = await userStore.fetchKinsByElderId(kinBindForm.value.elderId);
      const alreadyBound = existingKins && existingKins.some(
        kin => kin.userId === kinBindForm.value.kinId
      );
      
      if (alreadyBound) {
        ElMessage.warning('已经绑定了该家属，无需重复绑定');
        return;
      }
      
      // 调用绑定接口
      const success = await userStore.handleBindElderKinRelation(
        kinBindForm.value.elderId,
        kinBindForm.value.kinId,
        kinBindForm.value.relationType
      );
      
      if (success) {
        ElMessage.success('绑定家属成功');
        
        // 关闭绑定对话框
        kinBindDialogVisible.value = false;
        
        // 重新获取家属列表
        const kins = await userStore.fetchKinsByElderId(kinBindForm.value.elderId);
        form.value.kins = kins || [];
      } else {
        ElMessage.error('绑定家属失败');
      }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('绑定家属失败:', error);
        // 检查是否是主键冲突的错误
        if (error.message && error.message.includes('Duplicate entry') && error.message.includes('elder_kin_relation.PRIMARY')) {
          ElMessage.error('已存在相同的绑定关系，无需重复绑定');
        } else {
          ElMessage.error('绑定家属失败');
        }
      }
    }
  };
  
  // 添加新的绑定关系对话框
  const bindType = ref('elder');
  const bindDialogVisible = ref(false);
  const bindLoading = ref(false);
  const bindFormRef = ref(null);
  const bindForm = ref({
    targetId: null,
    relationType: ''
  });
  const availableElderList = ref([]);
  const availableKinList = ref([]);
  const currentBindings = ref([]);
  
  const getAvailableElderList = async () => {
    try {
      const response = await userStore.fetchElderList();
      if (response?.data) {
        availableElderList.value = response.data.map(elder => ({
          value: elder.userId,
          label: `${elder.name}（${elder.idCard ? elder.idCard : '未填写身份证'}）`
        }));
      } else {
        availableElderList.value = [];
      }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('获取可用老人列表失败:', error);
        ElMessage.error('获取可用老人列表失败');
      }
    }
  };
  
  const getAvailableKinList = async () => {
    try {
      const response = await userStore.fetchUnboundKins();
      if (response?.data) {
        availableKinList.value = response.data.map(kin => ({
          value: kin.userId,
          label: `${kin.name}（${kin.phone || '未填写电话'}）`
        }));
      } else {
        availableKinList.value = [];
      }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('获取可用家属列表失败:', error);
        ElMessage.error('获取可用家属列表失败');
      }
    }
  };
  
  // 在表单数据下方添加字典选项
  const healthLevelOptions = ref([]);
  
  // 获取健康状况字典数据
  const getHealthLevelDict = async () => {
    try {
      const response = await getDictDataByType('health_level');
      if (response.code === 200) {
        healthLevelOptions.value = response.data;
      }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('获取健康状况字典数据失败:', error);
      }
    }
  };
  
  // 根据健康状况值获取标签
  const getHealthLevelLabel = (value) => {
    if (!value) return '';
    const option = healthLevelOptions.value.find(item => item.dictValue === value);
    return option ? option.dictLabel : value;
  };
  
  onMounted(() => {
    getList();
    getHealthLevelDict(); // 获取健康状况字典数据
  });
  
  </script>
  <style scoped>
  .app-container {
    padding: 20px;
  }
  
  .mb8 {
    margin-bottom: 8px;
  }
  
  .small-padding {
    padding-left: 5px;
    padding-right: 5px;
  }
  
  .fixed-width .el-button--small {
    padding: 7px 10px;
    min-width: 60px;
  }
  
  .mb-1 {
    margin-bottom: 8px;
  }
  
  .user-detail .el-descriptions__label {
    width: 120px;
  }
  
  /* 添加新的样式 */
  .link-text {
    color: #409eff;
    cursor: pointer;
  }
  
  .link-text:hover {
    color: #66b1ff;
    text-decoration: underline;
  }
  
  .mt-3 {
    margin-top: 16px;
  }
  
  .bind-list-title {
    font-weight: bold;
    margin-bottom: 8px;
    font-size: 14px;
    color: #606266;
    border-bottom: 1px solid #ebeef5;
    padding-bottom: 8px;
  }
  </style>