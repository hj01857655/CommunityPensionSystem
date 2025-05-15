<template>
  <div class="physical-exam-report-backend">
    <!-- 查询与操作栏 -->
    <el-row :gutter="16" class="toolbar">
      <el-col :span="12" class="toolbar-right">
        <el-button type="primary" @click="openAddDialog">新增体检报告</el-button>
        <el-tooltip content="下载标准体检报告模板，填写后上传" placement="top">
          <el-button type="info" link @click="downloadTemplate">
            <el-icon><Download /></el-icon>
            体检报告模板
          </el-button>
        </el-tooltip>
      </el-col>
    </el-row>

    <!-- 体检报告表格 -->
    <el-table
      :data="reportList"
      style="width: 100%; margin-top: 16px"
      :row-class-name="rowClassName"
      border
      v-loading="loading"
    >
      <el-table-column prop="date" label="体检日期" min-width="100" />
      <el-table-column prop="hospital" label="体检医院" min-width="160" />
      <el-table-column prop="mainResult" label="主要结果" min-width="200" show-overflow-tooltip />
      <el-table-column prop="fileUrl" label="报告文件" min-width="100">
        <template #default="{ row }">
          <el-button v-if="hasValidFileUrl(row.fileUrl)" size="small" type="primary" @click="previewFile(row.fileUrl)">预览</el-button>
          <el-button v-else size="small" @click="handleUploadForExisting(row)" type="success">上传附件</el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="viewDetail(row.id)">详情</el-button>
          <el-button size="small" type="primary" @click="openEditDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <Pagination
      v-if="total > pageSize"
      :total="total"
      :page="pageNum"
      :limit="pageSize"
      @pagination="handlePagination"
      @update:limit="handlePageSizeChange"
      style="margin-top: 16px"
    />

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px">
      <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
        <el-form-item label="体检日期" prop="date">
          <el-date-picker v-model="form.date" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="体检医院" prop="hospital">
          <el-input v-model="form.hospital" placeholder="请输入体检医院" />
        </el-form-item>
        <el-form-item label="主要结果" prop="mainResult">
          <el-input type="textarea" v-model="form.mainResult" :rows="3" placeholder="请输入体检主要结果" />
        </el-form-item>
        <el-form-item label="报告文件" prop="fileUrl">
          <el-upload
            ref="uploadRef"
            :action="null"
            :auto-upload="false"
            :show-file-list="true"
            :limit="1"
            :on-change="onFileChange"
            :on-remove="onFileRemove"
            accept=".pdf,.jpg,.jpeg,.png"
          >
            <el-button type="primary">选择文件</el-button>
            <el-button v-if="uploadFileList.length > 0" type="success" @click="handleManualUpload" :loading="uploading">上传文件</el-button>
            <template #tip>
              <div class="el-upload__tip">
                推荐上传<b>PDF格式</b>的体检报告，支持PDF(首选)、JPG、PNG格式，文件大小不超过10MB
              </div>
            </template>
          </el-upload>
          <div v-if="form.fileUrl" style="margin-top: 8px;">
            <el-button v-if="hasValidFileUrl(form.fileUrl)" size="small" type="primary" @click="previewFile(form.fileUrl)">预览已上传文件</el-button>
            <span class="file-name">{{ getFileName(form.fileUrl) }}</span>
          </div>
        </el-form-item>
        
        <!-- 体检项目编辑 -->
        <el-divider content-position="left">体检项目</el-divider>
        <div class="exam-items-edit">
          <div class="items-toolbar">
            <el-button type="primary" size="small" @click="addExamItem">
              <el-icon><Plus /></el-icon> 添加项目
            </el-button>
            <el-dropdown @command="handleAddPreset" trigger="click">
              <el-button type="success" size="small">
                <el-icon><Plus /></el-icon> 添加常用项目
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="bloodPressure">血压</el-dropdown-item>
                  <el-dropdown-item command="bloodSugar">血糖</el-dropdown-item>
                  <el-dropdown-item command="bloodLipids">血脂四项</el-dropdown-item>
                  <el-dropdown-item command="liverFunction">肝功能</el-dropdown-item>
                  <el-dropdown-item command="kidneyFunction">肾功能</el-dropdown-item>
                  <el-dropdown-item command="bloodRoutine">血常规</el-dropdown-item>
                  <el-dropdown-item command="urineRoutine">尿常规</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-button v-if="form.examItems && form.examItems.length > 0" type="danger" size="small" @click="clearExamItems">
              <el-icon><Delete /></el-icon> 清空项目
            </el-button>
          </div>
          
          <el-empty v-if="!form.examItems || form.examItems.length === 0" description="暂无体检项目" :image-size="100">
            <el-button type="primary" @click="addExamItem">添加项目</el-button>
          </el-empty>
          
          <el-table v-else :data="form.examItems" border stripe style="width: 100%; margin-top: 10px;">
            <el-table-column label="项目名称" min-width="120">
              <template #default="{ row, $index }">
                <el-input v-model="row.itemName" placeholder="请输入项目名称" />
              </template>
            </el-table-column>
            <el-table-column label="检测值" min-width="120">
              <template #default="{ row, $index }">
                <div class="value-unit-input">
                  <el-input v-model="row.itemValue" placeholder="检测值" class="value-input" />
                  <el-input v-model="row.itemUnit" placeholder="单位" class="unit-input" />
                </div>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="120">
              <template #default="{ row, $index }">
                <el-select v-model="row.abnormalFlag" placeholder="请选择">
                  <el-option :value="0" label="正常" />
                  <el-option :value="1" label="异常" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80">
              <template #default="{ row, $index }">
                <el-button type="danger" size="small" circle @click="removeExamItem($index)">
                  <el-icon><Close /></el-icon>
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="体检报告详情" width="600px">
      <div v-if="detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="体检日期">{{ getDetailData('date') || '暂无数据' }}</el-descriptions-item>
          <el-descriptions-item label="体检医院">{{ getDetailData('hospital') || '暂无数据' }}</el-descriptions-item>
          <el-descriptions-item label="主要结果">{{ getDetailData('mainResult') || '暂无数据' }}</el-descriptions-item>
          <el-descriptions-item label="报告文件">
            <el-button v-if="hasValidFileUrl(getDetailData('fileUrl'))" size="small" type="primary" @click="previewFile(getDetailData('fileUrl'))">预览</el-button>
            <el-button v-else size="small" @click="handleUploadForDetail" type="success">上传附件</el-button>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDateTime(getDetailData('createdAt')) }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ formatDateTime(getDetailData('updatedAt')) }}</el-descriptions-item>
        </el-descriptions>
        
        <!-- 体检项目列表 -->
        <div class="exam-items-section">
          <h3 class="section-title">体检项目</h3>
          <div v-if="getExamItems().length === 0" class="no-items">
            暂无体检项目数据
          </div>
          <el-table v-else :data="getExamItems()" border stripe style="width: 100%; margin-top: 10px;">
            <el-table-column prop="itemName" label="项目名称" min-width="120" />
            <el-table-column label="检测值" min-width="120">
              <template #default="{ row }">
                {{ row.itemValue }}{{ row.itemUnit ? ' ' + row.itemUnit : '' }}
              </template>
            </el-table-column>
            <el-table-column label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.abnormalFlag === 1 ? 'danger' : 'success'">
                  {{ row.abnormalFlag === 1 ? '异常' : '正常' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      reportList: [],
      loading: false,
      dialogVisible: false,
      dialogTitle: '',
      form: {
        date: '',
        hospital: '',
        mainResult: '',
        fileUrl: '',
        examItems: [],
      },
      rules: {
        date: [{ required: true, message: '请选择体检日期', trigger: 'blur' }],
        hospital: [{ required: true, message: '请输入体检医院', trigger: 'blur' }],
        mainResult: [{ required: true, message: '请输入主要结果', trigger: 'blur' }],
      },
      detailVisible: false,
      detail: null,
      pageNum: 1,
      pageSize: 10,
      total: 0,
    };
  },
  methods: {
    openAddDialog() {
      this.dialogTitle = '新增体检报告';
      this.dialogVisible = true;
    },
    openEditDialog(row) {
      this.dialogTitle = '编辑体检报告';
      this.form = { ...row };
      this.dialogVisible = true;
    },
    handleDelete(id) {
      // 删除逻辑
    },
    handleSubmit() {
      // 提交逻辑
    },
    viewDetail(id) {
      // 查看详情逻辑
    },
    downloadTemplate() {
      // 下载模板逻辑
    },
    previewFile(url) {
      // 预览文件逻辑
    },
    handleUploadForExisting(row) {
      // 上传文件逻辑
    },
    handlePagination(page) {
      this.pageNum = page;
      this.fetchReports();
    },
    handlePageSizeChange(size) {
      this.pageSize = size;
      this.fetchReports();
    },
    fetchReports() {
      // 获取报告列表逻辑
    },
    addExamItem() {
      this.form.examItems.push({ itemName: '', itemValue: '', itemUnit: '', abnormalFlag: 0 });
    },
    removeExamItem(index) {
      this.form.examItems.splice(index, 1);
    },
    handleAddPreset(command) {
      // 添加常用项目逻辑
    },
    clearExamItems() {
      this.form.examItems = [];
    },
    onFileChange(file, fileList) {
      // 文件改变逻辑
    },
    onFileRemove(file, fileList) {
      // 文件移除逻辑
    },
    handleManualUpload() {
      // 手动上传逻辑
    },
    getDetailData(key) {
      return this.detail ? this.detail[key] : null;
    },
    getExamItems() {
      return this.detail ? this.detail.examItems : [];
    },
    hasValidFileUrl(url) {
      return url && url.length > 0;
    },
    getFileName(url) {
      return url.substring(url.lastIndexOf('/') + 1);
    },
    formatDateTime(dateTime) {
      // 格式化日期时间逻辑
    },
  },
};
</script>

<style scoped>
.physical-exam-report-backend {
  padding: 20px;
}
.toolbar {
  margin-bottom: 20px;
}
.items-toolbar {
  margin-bottom: 10px;
}
.value-unit-input {
  display: flex;
}
.value-input {
  flex: 1;
  margin-right: 5px;
}
.unit-input {
  width: 60px;
}
</style> 