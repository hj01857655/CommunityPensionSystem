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
    <el-table v-loading="loading" :row-key="getRowKey" @row-click="clickRow" ref="roleRef" @selection-change="handleSelectionChange" :data="tableData">
      <el-table-column label="序号" width="55" type="index" align="center">
        <template #default="scope">
          <span>{{ (pageNum - 1) * pageSize + scope.$index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column type="selection" :reserve-selection="true" width="55"></el-table-column>
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
import { ref, onMounted, nextTick, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { formatDate } from '@/utils/date';
import { getAuthRole, updateAuthRole } from '@/api/back/system/user';
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
const roleIds = ref([]);
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
  if (roleRef.value) {
    roleRef.value.toggleRowSelection(row);
  }
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  roleIds.value = selection.map((item) => item.roleId);
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
  const rIds = roleIds.value.join(',');
  updateAuthRole({ userId, roleIds: rIds }).then((response) => {
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
      
      await nextTick();
      if (roleRef.value && Array.isArray(roles.value)) {
        roles.value.forEach((row) => {
          if (row.flag) {
            roleRef.value.toggleRowSelection(row);
          }
        });
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