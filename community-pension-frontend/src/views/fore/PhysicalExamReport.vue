<template>
  <div class="physical-exam-report">
    <el-table :data="reportList" v-loading="loading" style="width: 100%">
      <el-table-column prop="date" label="体检日期" min-width="120" />
      <el-table-column prop="hospital" label="体检医院" min-width="160" />
      <el-table-column prop="mainResult" label="主要结论" min-width="200" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="viewDetail(row.id)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="体检报告详情"
      width="80%"
      top="5vh"
      fullscreen
      destroy-on-close
    >
      <div v-loading="detailLoading" class="report-detail">
        <template v-if="reportDetail.report">
          <el-descriptions :column="3" border>
            <el-descriptions-item label="体检日期">{{ reportDetail.report.date }}</el-descriptions-item>
            <el-descriptions-item label="体检医院">{{ reportDetail.report.hospital }}</el-descriptions-item>
            <el-descriptions-item label="体检编号">{{ reportDetail.report.reportNo || '-' }}</el-descriptions-item>
            <el-descriptions-item label="体检人员">{{ reportDetail.report.elderName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ reportDetail.report.gender || '-' }}</el-descriptions-item>
            <el-descriptions-item label="年龄">{{ reportDetail.report.age || '-' }}</el-descriptions-item>
          </el-descriptions>
          
          <div class="report-section">
            <h3>体检结论</h3>
            <div class="report-conclusion">
              {{ reportDetail.report.mainResult || '暂无体检结论' }}
            </div>
          </div>
          
          <div v-if="reportDetail.items && reportDetail.items.length > 0" class="report-section">
            <h3>检查项目列表</h3>
            
            <!-- 一般检查 -->
            <div class="category-section" v-if="getItemsByCategory('一般检查').length > 0">
              <h4>一、一般检查</h4>
              <el-table :data="getItemsByCategory('一般检查')" border stripe style="width: 100%">
                <el-table-column prop="itemName" label="检查项目" min-width="150" />
                <el-table-column prop="itemValue" label="检测值" min-width="100" />
                <el-table-column prop="itemUnit" label="单位" min-width="80" />
                <el-table-column prop="normalRange" label="参考范围" min-width="120" />
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="row.status === 'normal' ? 'success' : 'danger'">
                      {{ row.status === 'normal' ? '正常' : '异常' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="remark" label="备注" min-width="150" />
              </el-table>
            </div>
            
            <!-- 抽血检查 -->
            <div class="category-section" v-if="getItemsByCategory('抽血检查').length > 0">
              <h4>二、抽血检查</h4>
              <el-table :data="getItemsByCategory('抽血检查')" border stripe style="width: 100%">
                <el-table-column prop="itemName" label="检查项目" min-width="150" />
                <el-table-column prop="itemValue" label="检测值" min-width="100" />
                <el-table-column prop="itemUnit" label="单位" min-width="80" />
                <el-table-column prop="normalRange" label="参考范围" min-width="120" />
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="row.status === 'normal' ? 'success' : 'danger'">
                      {{ row.status === 'normal' ? '正常' : '异常' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="remark" label="备注" min-width="150" />
              </el-table>
            </div>
            
            <!-- X光检查 -->
            <div class="category-section" v-if="getItemsByCategory('X光检查').length > 0">
              <h4>三、X光检查</h4>
              <el-table :data="getItemsByCategory('X光检查')" border stripe style="width: 100%">
                <el-table-column prop="itemName" label="检查项目" min-width="150" />
                <el-table-column prop="itemValue" label="检测值" min-width="100" />
                <el-table-column prop="normalRange" label="参考范围" min-width="120" />
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="row.status === 'normal' ? 'success' : 'danger'">
                      {{ row.status === 'normal' ? '正常' : '异常' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="remark" label="备注" min-width="180" />
              </el-table>
            </div>
            
            <!-- B超检查 -->
            <div class="category-section" v-if="getItemsByCategory('B超检查').length > 0">
              <h4>四、B超检查</h4>
              <el-table :data="getItemsByCategory('B超检查')" border stripe style="width: 100%">
                <el-table-column prop="itemName" label="检查项目" min-width="150" />
                <el-table-column prop="itemValue" label="检测值" min-width="100" />
                <el-table-column prop="normalRange" label="参考范围" min-width="120" />
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="row.status === 'normal' ? 'success' : 'danger'">
                      {{ row.status === 'normal' ? '正常' : '异常' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="remark" label="备注" min-width="180" />
              </el-table>
            </div>
            
            <!-- 特殊检查 -->
            <div class="category-section" v-if="getItemsByCategory('特殊检查').length > 0">
              <h4>五、特殊检查</h4>
              <el-table :data="getItemsByCategory('特殊检查')" border stripe style="width: 100%">
                <el-table-column prop="itemName" label="检查项目" min-width="150" />
                <el-table-column prop="itemValue" label="检测值" min-width="100" />
                <el-table-column prop="normalRange" label="参考范围" min-width="120" />
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="row.status === 'normal' ? 'success' : 'danger'">
                      {{ row.status === 'normal' ? '正常' : '异常' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="remark" label="备注" min-width="180" />
              </el-table>
            </div>
            
            <!-- 妇科检查 -->
            <div class="category-section" v-if="getItemsByCategory('妇科检查').length > 0">
              <h4>六、妇科检查</h4>
              <el-table :data="getItemsByCategory('妇科检查')" border stripe style="width: 100%">
                <el-table-column prop="itemName" label="检查项目" min-width="150" />
                <el-table-column prop="itemValue" label="检测值" min-width="100" />
                <el-table-column prop="normalRange" label="参考范围" min-width="120" />
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="row.status === 'normal' ? 'success' : 'danger'">
                      {{ row.status === 'normal' ? '正常' : '异常' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="remark" label="备注" min-width="180" />
              </el-table>
            </div>
            
            <!-- 其他检查 -->
            <div class="category-section" v-if="getItemsByCategory('其他').length > 0">
              <h4>七、其他检查</h4>
              <el-table :data="getItemsByCategory('其他')" border stripe style="width: 100%">
                <el-table-column prop="itemName" label="检查项目" min-width="150" />
                <el-table-column prop="itemValue" label="检测值" min-width="100" />
                <el-table-column prop="itemUnit" label="单位" min-width="80" />
                <el-table-column prop="normalRange" label="参考范围" min-width="120" />
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="row.status === 'normal' ? 'success' : 'danger'">
                      {{ row.status === 'normal' ? '正常' : '异常' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="remark" label="备注" min-width="150" />
              </el-table>
            </div>
          </div>
          
          <div v-if="reportDetail.report.fileUrl" class="report-section">
            <h3>体检报告原件</h3>
            <div class="report-file">
              <el-button type="primary" @click="previewReport(reportDetail.report.fileUrl)">
                查看报告文件
              </el-button>
              <p class="file-tip">* 点击查看由医疗机构盖章的完整体检报告</p>
            </div>
          </div>
          
          <div class="report-section">
            <h3>注意事项</h3>
            <div class="report-notice">
              <p>1. 本报告仅对本次检查负责，不作为医疗诊断依据；</p>
              <p>2. 如有异常项目，建议及时就医进行进一步检查；</p>
              <p>3. 体检报告需加盖区级以上医疗机构的公章方为有效；</p>
              <p>4. 如有疑问，请咨询您的体检医院或社区健康管理中心。</p>
            </div>
          </div>
        </template>
        <template v-else>
          <el-empty description="暂无体检报告详情" :image-size="200">
            <template #description>
              <p>未找到该体检报告的详细信息，请确认报告ID是否正确</p>
            </template>
          </el-empty>
        </template>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="printReport">打印报告</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { usePhysicalExamReportStore } from '@/stores/fore/physicalExamReportStore'
import { ElMessage } from 'element-plus'
import { onMounted, ref } from 'vue'

const physicalExamReportStore = usePhysicalExamReportStore()
const reportList = ref([])
const loading = ref(false)
const detailDialogVisible = ref(false)
const detailLoading = ref(false)
const reportDetail = ref({})

const fetchReports = async () => {
  loading.value = true
  try {
    await physicalExamReportStore.fetchList()
    reportList.value = physicalExamReportStore.reportList
  } catch (error) {
    console.error('获取体检报告列表失败:', error)
    ElMessage.error('获取体检报告列表失败')
  } finally {
    loading.value = false
  }
}

const viewDetail = async (id) => {
  if (!id) {
    ElMessage.warning('报告ID不能为空')
    return
  }
  
  detailDialogVisible.value = true
  detailLoading.value = true
  
  try {
    await physicalExamReportStore.fetchDetail(id)
    reportDetail.value = physicalExamReportStore.detail
    
    if (!reportDetail.value || !reportDetail.value.report) {
      ElMessage.warning('未找到体检报告详情')
    }
  } catch (error) {
    console.error('获取体检报告详情失败:', error)
    ElMessage.error('获取体检报告详情失败')
  } finally {
    detailLoading.value = false
  }
}

const getItemsByCategory = (category) => {
  if (!reportDetail.value.items || !Array.isArray(reportDetail.value.items)) {
    return []
  }
  
  return reportDetail.value.items.filter(item => item.category === category) || []
}

const previewReport = (fileUrl) => {
  if (!fileUrl) {
    ElMessage.warning('报告文件不存在')
    return
  }
  
  // 使用浏览器打开文件链接
  window.open(fileUrl, '_blank')
}

const printReport = () => {
  window.print()
}

onMounted(() => {
  fetchReports()
})
</script>

<style scoped>
.physical-exam-report {
  padding: 20px;
}

.report-detail {
  padding: 20px;
}

.report-section {
  margin-top: 30px;
}

.report-section h3 {
  font-size: 18px;
  color: #303133;
  font-weight: bold;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #EBEEF5;
}

.category-section {
  margin-bottom: 25px;
}

.category-section h4 {
  font-size: 16px;
  color: #409EFF;
  margin: 15px 0 10px;
  font-weight: 600;
}

.report-conclusion {
  padding: 15px;
  background-color: #f8f8f8;
  border-radius: 4px;
  line-height: 1.6;
  color: #303133;
}

.report-file {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.file-tip {
  margin-top: 10px;
  color: #909399;
  font-size: 12px;
}

.report-notice {
  padding: 15px;
  background-color: #f8f8f8;
  border-radius: 4px;
}

.report-notice p {
  margin: 5px 0;
  color: #606266;
  line-height: 1.6;
}

.dialog-footer {
  padding: 10px 20px 0;
  text-align: right;
}

@media print {
  .el-button {
    display: none;
  }
  
  .el-dialog__header, 
  .el-dialog__footer {
    display: none;
  }
  
  .report-detail {
    padding: 0;
  }
}
</style> 