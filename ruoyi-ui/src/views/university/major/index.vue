<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :inline="true" :model="searchParams" class="demo-form-inline">
      <el-form-item label="专业关键字">
        <el-input v-model="searchParams.keyword" placeholder="请输入专业关键字" clearable style="width: 180px"></el-input>
      </el-form-item>
      <el-form-item label="所学科目">
        <el-select v-model="searchParams.subject" placeholder="请选择" clearable style="width: 120px">
          <el-option label="历史类" :value="1"></el-option>
          <el-option label="物理类" :value="2"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="投档线年份">
        <el-select v-model="searchParams.scoreYear" placeholder="筛选所用年份" style="width: 130px">
          <el-option label="2025年" :value="2025"></el-option>
          <el-option label="2024年" :value="2024"></el-option>
          <el-option label="2023年" :value="2023"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="分数线范围">
        <span class="score-range">
          <el-input-number v-model="searchParams.minScore" :min="0" :max="750" placeholder="最低" controls-position="right" style="width: 130px"></el-input-number>
          <span class="range-sep">~</span>
          <el-input-number v-model="searchParams.maxScore" :min="0" :max="750" placeholder="最高" controls-position="right" style="width: 130px"></el-input-number>
        </span>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 专业列表 -->
    <el-table :data="majorList" v-loading="loading" border style="width: 100%">
      <el-table-column label="院校名称" prop="universityName" align="center" min-width="160"></el-table-column>
      <el-table-column label="专业名称" prop="majorName" align="center" min-width="140"></el-table-column>
      <el-table-column label="所学科目" prop="subject" align="center" width="90">
        <template #default="scope">
          <span>{{ scope.row.subject === 1 ? '历史类' : '物理类' }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="说明"
        prop="description"
        align="left"
        min-width="220"
        show-overflow-tooltip
        :formatter="formatDescription"
      />
      <el-table-column label="2023投档线" prop="minScore2023" align="center" width="100">
        <template #default="scope">
          <span>{{ formatScore(scope.row.minScore2023) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="2024投档线" prop="minScore2024" align="center" width="100">
        <template #default="scope">
          <span>{{ formatScore(scope.row.minScore2024) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="2025投档线" prop="minScore2025" align="center" width="100">
        <template #default="scope">
          <span>{{ formatScore(scope.row.minScore2025) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200" fixed="right">
        <template #default="scope">
          <el-button
            type="primary"
            size="mini"
            @click="handleViewDetail(scope.row)"
          >查看专业</el-button>
          <el-button
            type="success"
            size="mini"
            @click="handleStoreup(scope.row)"
          >收藏</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="fetchMajors"
    />

    <!-- 专业组明细弹窗 -->
    <el-dialog
      :visible.sync="detailDialogVisible"
      :title="detailDialogTitle"
      width="750px"
      append-to-body
    >
      <el-table :data="majorDetailList" v-loading="detailLoading" border style="width: 100%">
        <el-table-column label="具体专业名称" prop="specificMajorName" align="center" min-width="150"></el-table-column>
        <el-table-column label="招生计划数" prop="planCount" align="center" width="100">
          <template #default="scope">{{ scope.row.planCount != null ? scope.row.planCount : '-' }}</template>
        </el-table-column>
        <el-table-column label="学费（元/年）" prop="tuitionFee" align="center" width="120">
          <template #default="scope">{{ scope.row.tuitionFee != null ? scope.row.tuitionFee : '-' }}</template>
        </el-table-column>
        <el-table-column label="学制" prop="studyYears" align="center" width="80">
          <template #default="scope">{{ scope.row.studyYears || '-' }}</template>
        </el-table-column>
        <el-table-column label="备注" prop="requirements" align="left" min-width="180" show-overflow-tooltip>
          <template #default="scope">{{ scope.row.requirements || '-' }}</template>
        </el-table-column>
      </el-table>
      <span slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { listMajors, getMajorDetails } from "@/api/university/major";
import { addStoreup } from "@/api/university/storeup";

export default {
  data() {
    return {
      loading: false,
      majorList: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
      },
      searchParams: {
        keyword: '',
        subject: null,
        scoreYear: 2025,
        minScore: null,
        maxScore: null,
      },
      detailDialogVisible: false,
      detailDialogTitle: '',
      detailLoading: false,
      majorDetailList: []
    };
  },
  created() {
    this.fetchMajors();
  },
  methods: {
    formatScore(val) {
      if (val === null || val === undefined || val === '') return '-';
      return val;
    },
    formatDescription(row, column, cellValue) {
      if (cellValue === null || cellValue === undefined || cellValue === '') {
        return '-';
      }
      return cellValue;
    },
    fetchMajors() {
      this.loading = true;
      const params = {
        ...this.queryParams,
        ...this.searchParams
      };
      listMajors(params).then((response) => {
        this.majorList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    handleSearch() {
      this.queryParams.pageNum = 1;
      this.fetchMajors();
    },
    handleReset() {
      this.searchParams = {
        keyword: '',
        subject: null,
        scoreYear: 2025,
        minScore: null,
        maxScore: null,
      };
      this.handleSearch();
    },
    handleViewDetail(row) {
      this.detailDialogTitle = (row.universityName || '') + ' - ' + (row.majorName || '') + ' 招收专业';
      this.detailDialogVisible = true;
      this.detailLoading = true;
      this.majorDetailList = [];
      getMajorDetails(row.majorId).then(res => {
        this.majorDetailList = res.data || [];
        this.detailLoading = false;
      }).catch(() => {
        this.detailLoading = false;
      });
    },
    handleStoreup(row) {
      const data = {
        majorId: row.majorId,
        actionType: 1,
      };
      addStoreup(data).then(() => {
        this.$message.success("收藏成功");
      });
    }
  }
};
</script>

<style scoped>
.demo-form-inline {
  margin-bottom: 20px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}
.score-range {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}
.range-sep {
  color: #909399;
}
</style>
