<template>
  <div class="app-container">
    <h4 class="form-header h4">基本信息</h4>
    <el-form :model="form" label-width="80px">
      <el-row>
        <el-col :span="8" :offset="2">
          <el-form-item label="用户昵称" prop="nickName">
            <el-input v-model="form.nickName" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="8" :offset="2">
          <el-form-item label="登录账号" prop="userName">
            <el-input v-model="form.userName" disabled />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <h4 class="form-header h4">角色信息</h4>
    <el-table v-loading="loading" :row-key="getRowKey" @row-click="clickRow" ref="roleRef" :data="tableData">
      <el-table-column label="序号" width="55" type="index" align="center">
        <template #default="scope">
          <span>{{ (pageNum - 1) * pageSize + scope.$index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column type="radio" width="55">
        <template #default="scope">
          <el-radio v-model="selectedRoleId" :label="scope.row.roleId" @change="handleRadioChange(scope.row)">&nbsp;</el-radio>
        </template>
      </el-table-column>
      <el-table-column label="角色编号" align="center" prop="roleId" />
      <el-table-column label="角色名称" align="center" prop="roleName" />
      <el-table-column label="权限字符" align="center" prop="roleKey" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ formatDate(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <el-form label-width="100px">
      <div style="text-align: center; margin-left: -120px; margin-top: 30px">
        <el-button type="primary" @click="submitForm">提交</el-button>
        <el-button @click="handleClose">返回</el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { getAuthRole, updateAuthRole } from '@/api/back/system/user';
import { formatDate } from '@/utils/date';
import { ElMessage } from 'element-plus';
import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();
const userId = route.params.userId;
const username = route.query.username;

const roleRef = ref(null);
const loading = ref(true);
const total = ref(0);
const pageNum = ref(1);
const pageSize = ref(10);
const selectedRoleId = ref(null); // 选中的角色ID
const roles = ref([]);
const form = ref({
  nickName: '',
  userName: username || '',
  userId: userId || ''
});

// 计算表格数据
const tableData = computed(() => {
  if (!roles.value || !Array.isArray(roles.value)) return [];
  const start = (pageNum.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return roles.value.slice(start, end);
});

/** 单击选中行数据 */
function clickRow(row) {
  selectedRoleId.value = row.roleId;
}

/** 处理单选框变化 */
function handleRadioChange(row) {
  // 单选框已经处理了选中逻辑，不需要额外操作
  console.log('选中角色:', row.roleName);
}

/** 保存选中的数据编号 */
function getRowKey(row) {
  return row.roleId;
}

/** 关闭按钮 */
function handleClose() {
  router.go(-1);
}

/** 提交按钮 */
function submitForm() {
  if (!userId) {
    ElMessage.error('用户ID不能为空');
    return;
  }
  
  if (!selectedRoleId.value) {
    ElMessage.warning('请选择一个角色');
    return;
  }
  
  // 只提交选中的一个角色ID
  updateAuthRole({ userId, roleId: selectedRoleId.value.toString() }).then((response) => {
    if (response.code === 200) {
      ElMessage.success('授权成功');
      handleClose();
    } else {
      ElMessage.error(response.message || '授权失败');
    }
  });
}

/** 分页大小变化 */
function handleSizeChange(val) {
  pageSize.value = val;
  pageNum.value = 1;
}

/** 当前页变化 */
function handleCurrentChange(val) {
  pageNum.value = val;
}

// 初始化数据
async function initData() {
  if (!userId) {
    ElMessage.error('用户ID不能为空');
    return;
  }

  loading.value = true;
  try {
    const res = await getAuthRole(userId);
    if (res.code === 200) {
      const response = res.data;
      form.value = {
        ...form.value,
        ...response.user
      };
      roles.value = response.roles || [];
      total.value = roles.value.length;
      
      // 查找当前用户已分配的角色
      const userRole = roles.value.find(role => role.flag);
      if (userRole) {
        selectedRoleId.value = userRole.roleId;
      }
    } else {
      ElMessage.error(res.message || '获取角色信息失败');
    }
  } catch (error) {
    console.error('获取角色信息失败:', error);
    ElMessage.error('获取角色信息失败');
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  initData();
});
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.form-header {
  margin: 20px 0;
  font-weight: bold;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 