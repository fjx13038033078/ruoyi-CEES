<template>
  <div class="app-container">
    <div class="volunteer-header">
      <h2>我的志愿方案</h2>
      <el-button type="primary" icon="el-icon-plus" @click="handleAddForm">新建方案</el-button>
    </div>

    <el-row :gutter="20">
      <el-col :span="8" v-for="form in formList" :key="form.formId">
        <el-card class="form-card" shadow="hover">
          <div slot="header" class="card-header">
            <span class="form-name">{{ form.formName || '未命名方案' }}</span>
            <el-tag :type="statusTag(form.status)" size="small">{{ statusText(form.status) }}</el-tag>
          </div>
          <div class="card-body">
            <p><i class="el-icon-date"></i> 年份：{{ form.examYear || '-' }}</p>
            <p><i class="el-icon-tickets"></i> 批次：{{ batchText(form.batchType) }}</p>
            <p><i class="el-icon-s-data"></i> 志愿数：{{ form.totalItems || 0 }}</p>
            <p v-if="form.warningCount > 0" class="warning-text">
              <i class="el-icon-warning"></i> {{ form.warningCount }} 项预警
            </p>
          </div>
          <div class="card-footer">
            <el-button type="text" @click="handleEdit(form)">编辑填报</el-button>
            <el-button type="text" @click="handleCheck(form)">校验</el-button>
            <el-button type="text" class="danger-btn" @click="handleDelete(form)">删除</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="formList.length === 0" description="暂无志愿方案，请点击右上角新建"></el-empty>

    <!-- 新建方案对话框 -->
    <el-dialog title="新建志愿方案" :visible.sync="dialogVisible" width="500px">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
        <el-form-item label="方案名称" prop="formName">
          <el-input v-model="formData.formName" placeholder="如：冲刺方案、稳妥方案"></el-input>
        </el-form-item>
        <el-form-item label="高考年份" prop="examYear">
          <el-date-picker v-model="formData.examYear" type="year" value-format="yyyy" placeholder="选择年份" style="width:100%"></el-date-picker>
        </el-form-item>
        <el-form-item label="批次" prop="batchType">
          <el-select v-model="formData.batchType" placeholder="选择批次" style="width:100%">
            <el-option label="提前批" :value="1"></el-option>
            <el-option label="本科批" :value="2"></el-option>
            <el-option label="专科批" :value="3"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getFormList, addForm, deleteForm, checkForm } from "@/api/college/volunteer";

export default {
  name: "VolunteerIndex",
  data() {
    return {
      formList: [],
      dialogVisible: false,
      formData: { formName: '', examYear: '', batchType: null },
      formRules: {
        formName: [{ required: true, message: '请输入方案名称', trigger: 'blur' }],
        batchType: [{ required: true, message: '请选择批次', trigger: 'change' }]
      }
    };
  },
  created() {
    this.loadList();
  },
  methods: {
    loadList() {
      getFormList().then(res => {
        this.formList = res.data || [];
      });
    },
    handleAddForm() {
      this.formData = { formName: '', examYear: new Date().getFullYear().toString(), batchType: 2 };
      this.dialogVisible = true;
    },
    submitForm() {
      this.$refs.formRef.validate(valid => {
        if (valid) {
          const data = { ...this.formData, examYear: parseInt(this.formData.examYear) };
          addForm(data).then(res => {
            this.$message.success('创建成功');
            this.dialogVisible = false;
            this.loadList();
          });
        }
      });
    },
    handleEdit(form) {
      this.$router.push({ path: '/volunteer/formEdit', query: { formId: form.formId } });
    },
    handleCheck(form) {
      checkForm(form.formId).then(res => {
        const results = res.data || [];
        const warnings = results.filter(r => r.checkStatus !== 1);
        if (warnings.length === 0) {
          this.$message.success('校验通过，无预警信息');
        } else {
          this.$message.warning(`校验完成，共 ${warnings.length} 项预警`);
        }
        this.loadList();
      });
    },
    handleDelete(form) {
      this.$confirm('确定删除该志愿方案及其所有志愿？', '提示', { type: 'warning' }).then(() => {
        deleteForm(form.formId).then(() => {
          this.$message.success('删除成功');
          this.loadList();
        });
      }).catch(() => {});
    },
    statusTag(status) {
      return { 0: 'info', 1: 'success', 2: 'warning' }[status] || 'info';
    },
    statusText(status) {
      return { 0: '草稿', 1: '已校验', 2: '已锁定' }[status] || '草稿';
    },
    batchText(type) {
      return { 1: '提前批', 2: '本科批', 3: '专科批' }[type] || '-';
    }
  }
};
</script>

<style scoped lang="scss">
.volunteer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  h2 { margin: 0; font-size: 20px; }
}
.form-card {
  margin-bottom: 20px;
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    .form-name { font-weight: 600; font-size: 16px; }
  }
  .card-body {
    p { margin: 8px 0; color: #606266; font-size: 14px; i { margin-right: 6px; color: #909399; } }
    .warning-text { color: #E6A23C; font-weight: 600; }
  }
  .card-footer {
    border-top: 1px solid #EBEEF5;
    padding-top: 12px;
    margin-top: 12px;
    display: flex;
    gap: 12px;
    .danger-btn { color: #F56C6C; }
  }
}
</style>
