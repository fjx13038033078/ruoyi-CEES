<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="68px" class="query-form">
      <el-form-item label="姓名" prop="realName">
        <el-input v-model="queryParams.realName" placeholder="考生姓名" clearable size="small" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="考生号" prop="examNumber">
        <el-input v-model="queryParams.examNumber" placeholder="考生号" clearable size="small" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="verifyStatus">
        <el-select v-model="queryParams.verifyStatus" placeholder="认证状态" clearable size="small">
          <el-option label="待审核" :value="0" />
          <el-option label="已通过" :value="1" />
          <el-option label="已驳回" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="profileList" border>
      <el-table-column label="档案ID" prop="profileId" width="80" align="center" />
      <el-table-column label="用户名" prop="userName" width="120" />
      <el-table-column label="真实姓名" prop="realName" width="100" />
      <el-table-column label="身份证号" prop="idCard" width="180" />
      <el-table-column label="考生号" prop="examNumber" width="150" />
      <el-table-column label="省份" prop="province" width="80" />
      <el-table-column label="科类" width="80" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.subjectType === 1 ? '历史类' : (scope.row.subjectType === 2 ? '物理类' : '-') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="总分" prop="totalScore" width="70" align="center" />
      <el-table-column label="认证状态" width="100" align="center">
        <template slot-scope="scope">
          <el-tag :type="verifyTagType(scope.row.verifyStatus)" size="small">
            {{ verifyText(scope.row.verifyStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="申请时间" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" fixed="right" width="200">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleDetail(scope.row)">详情</el-button>
          <el-button v-if="scope.row.verifyStatus === 0" size="mini" type="text" icon="el-icon-check" style="color:#67C23A" @click="handleApprove(scope.row)">通过</el-button>
          <el-button v-if="scope.row.verifyStatus === 0" size="mini" type="text" icon="el-icon-close" style="color:#F56C6C" @click="handleReject(scope.row)">驳回</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 详情对话框 -->
    <el-dialog title="考生档案详情" :visible.sync="detailVisible" width="700px">
      <el-descriptions :column="2" border v-if="currentProfile">
        <el-descriptions-item label="真实姓名">{{ currentProfile.realName }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ currentProfile.sex === '0' ? '男' : '女' }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ currentProfile.idCard }}</el-descriptions-item>
        <el-descriptions-item label="考生号">{{ currentProfile.examNumber }}</el-descriptions-item>
        <el-descriptions-item label="省份">{{ currentProfile.province }}</el-descriptions-item>
        <el-descriptions-item label="高考年份">{{ currentProfile.examYear }}</el-descriptions-item>
        <el-descriptions-item label="科类">{{ currentProfile.subjectType === 1 ? '历史类' : '物理类' }}</el-descriptions-item>
        <el-descriptions-item label="选科组合">{{ currentProfile.selectedSubjects }}</el-descriptions-item>
        <el-descriptions-item label="高考总分">{{ currentProfile.totalScore }}</el-descriptions-item>
        <el-descriptions-item label="语文">{{ currentProfile.scoreChinese }}</el-descriptions-item>
        <el-descriptions-item label="数学">{{ currentProfile.scoreMath }}</el-descriptions-item>
        <el-descriptions-item label="英语">{{ currentProfile.scoreEnglish }}</el-descriptions-item>
        <el-descriptions-item label="首选科目">{{ currentProfile.scorePrimary }}</el-descriptions-item>
        <el-descriptions-item label="再选科目1">{{ currentProfile.scoreSecondary1 }}</el-descriptions-item>
        <el-descriptions-item label="再选科目2">{{ currentProfile.scoreSecondary2 }}</el-descriptions-item>
        <el-descriptions-item label="色盲">{{ currentProfile.colorBlind === 1 ? '是' : '否' }}</el-descriptions-item>
        <el-descriptions-item label="色弱">{{ currentProfile.colorWeak === 1 ? '是' : '否' }}</el-descriptions-item>
        <el-descriptions-item label="认证状态">
          <el-tag :type="verifyTagType(currentProfile.verifyStatus)">{{ verifyText(currentProfile.verifyStatus) }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { listProfiles, verifyProfile } from "@/api/college/student";

export default {
  name: "StudentVerify",
  data() {
    return {
      loading: false,
      profileList: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        realName: undefined,
        examNumber: undefined,
        verifyStatus: undefined
      },
      detailVisible: false,
      currentProfile: null
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.loading = true;
      listProfiles(this.queryParams).then(res => {
        this.profileList = res.rows || [];
        this.total = res.total || 0;
        this.loading = false;
      });
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.$refs.queryForm.resetFields();
      this.handleQuery();
    },
    handleDetail(row) {
      this.currentProfile = row;
      this.detailVisible = true;
    },
    handleApprove(row) {
      this.$confirm('确认通过该考生的认证？', '提示', { type: 'info' }).then(() => {
        verifyProfile(row.profileId, 1).then(() => {
          this.$message.success('已通过认证');
          this.getList();
        });
      }).catch(() => {});
    },
    handleReject(row) {
      this.$confirm('确认驳回该考生的认证？', '提示', { type: 'warning' }).then(() => {
        verifyProfile(row.profileId, 2).then(() => {
          this.$message.success('已驳回');
          this.getList();
        });
      }).catch(() => {});
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

<style scoped>
.query-form {
  margin-bottom: 16px;
}
</style>
