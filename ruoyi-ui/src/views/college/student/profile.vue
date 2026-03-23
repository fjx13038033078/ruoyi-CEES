<template>
  <div class="app-container">
    <el-card shadow="never">
      <div slot="header">
        <b>我的考生档案</b>
        <el-tag v-if="profile.verifyStatus !== undefined" :type="verifyTagType(profile.verifyStatus)" size="small" style="margin-left:12px">
          {{ verifyText(profile.verifyStatus) }}
        </el-tag>
      </div>

      <el-form ref="profileForm" :model="profile" :rules="rules" label-width="110px" style="max-width:700px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="profile.realName" placeholder="真实姓名"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="sex">
              <el-radio-group v-model="profile.sex">
                <el-radio label="0">男</el-radio>
                <el-radio label="1">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="profile.idCard" placeholder="18位身份证号"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="考生号" prop="examNumber">
              <el-input v-model="profile.examNumber" placeholder="考生号"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所在省份" prop="province">
              <el-input v-model="profile.province" placeholder="如：湖北"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="高考年份" prop="examYear">
              <el-date-picker v-model="profile.examYear" type="year" value-format="yyyy" placeholder="选择年份" style="width:100%"></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="科类" prop="subjectType">
              <el-radio-group v-model="profile.subjectType">
                <el-radio :label="1">历史类</el-radio>
                <el-radio :label="2">物理类</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="选科组合">
              <el-input v-model="profile.selectedSubjects" placeholder="如：物理,化学,生物"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">成绩信息</el-divider>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="总分" prop="totalScore">
              <el-input-number v-model="profile.totalScore" :min="0" :max="750" style="width:100%"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="语文">
              <el-input-number v-model="profile.scoreChinese" :min="0" :max="150" style="width:100%"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="数学">
              <el-input-number v-model="profile.scoreMath" :min="0" :max="150" style="width:100%"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="英语">
              <el-input-number v-model="profile.scoreEnglish" :min="0" :max="150" style="width:100%"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="首选科目">
              <el-input-number v-model="profile.scorePrimary" :min="0" :max="100" style="width:100%"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="再选科目1">
              <el-input-number v-model="profile.scoreSecondary1" :min="0" :max="100" style="width:100%"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="再选科目2">
              <el-input-number v-model="profile.scoreSecondary2" :min="0" :max="100" style="width:100%"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">特殊情况</el-divider>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="是否色盲">
              <el-radio-group v-model="profile.colorBlind">
                <el-radio :label="0">否</el-radio>
                <el-radio :label="1">是</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否色弱">
              <el-radio-group v-model="profile.colorWeak">
                <el-radio :label="0">否</el-radio>
                <el-radio :label="1">是</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="saving">保存档案</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { getMyProfile, saveProfile } from "@/api/college/student";

export default {
  name: "StudentProfile",
  data() {
    return {
      profile: {
        realName: '', sex: '0', idCard: '', examNumber: '', province: '', examYear: null,
        subjectType: null, selectedSubjects: '', totalScore: null,
        scoreChinese: null, scoreMath: null, scoreEnglish: null,
        scorePrimary: null, scoreSecondary1: null, scoreSecondary2: null,
        colorBlind: 0, colorWeak: 0
      },
      rules: {
        realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
        subjectType: [{ required: true, message: '请选择科类', trigger: 'change' }]
      },
      saving: false
    };
  },
  created() {
    this.loadProfile();
  },
  methods: {
    loadProfile() {
      getMyProfile().then(res => {
        if (res.data) {
          Object.assign(this.profile, res.data);
          if (this.profile.examYear) {
            this.profile.examYear = String(this.profile.examYear);
          }
        }
      });
    },
    handleSave() {
      this.$refs.profileForm.validate(valid => {
        if (valid) {
          this.saving = true;
          const data = { ...this.profile };
          if (data.examYear) data.examYear = parseInt(data.examYear);
          saveProfile(data).then(() => {
            this.$message.success('保存成功');
            this.saving = false;
            this.loadProfile();
          }).catch(() => { this.saving = false; });
        }
      });
    },
    verifyTagType(status) {
      return { 0: 'warning', 1: 'success', 2: 'danger' }[status] || 'info';
    },
    verifyText(status) {
      return { 0: '待审核', 1: '已通过', 2: '已驳回' }[status] || '未知';
    }
  }
};
</script>
