<template>
  <div class="app-container information-page">
    <!-- 添加信息按钮 -->
    <el-row :gutter="20" class="mb-20">
      <el-col>
        <el-button type="primary" @click="handleAddInfo" v-hasPermi="['university:information:add']">新增报名信息</el-button>
      </el-col>
    </el-row>

    <!-- 信息列表 -->
    <el-table :data="infoList" v-loading="loading" border style="width: 100%">
      <el-table-column label="信息ID" prop="infoId" align="center"></el-table-column>
      <el-table-column label="用户名" prop="userName" align="center"></el-table-column>
      <el-table-column label="学科类型" prop="subject" align="center">
        <template #default="scope">
          {{ scope.row.subject === 1 ? '历史类' : '物理类' }}
        </template>
      </el-table-column>
      <el-table-column label="目标高校水平" prop="universityLevel" align="center">
        <template #default="scope">
          {{ getLevelLabel(scope.row.universityLevel) }}
        </template>
      </el-table-column>
      <el-table-column label="高考成绩" prop="score" align="center"></el-table-column>
      <el-table-column label="操作" align="center" width="280px">
        <template #default="scope">
          <el-button type="info" size="mini" @click="handleView(scope.row)">查看</el-button>
          <el-button type="primary" size="mini" @click="handleEdit(scope.row)" v-hasPermi="['university:information:edit']">编辑</el-button>
          <el-button type="danger" size="mini" @click="handleDelete(scope.row)" v-hasPermi="['university:information:delete']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="fetchInfoList"
    />

    <!-- ========================== 三梯度推荐清单 ========================== -->
    <div v-if="hasScore" class="tiered-section">
      <div class="tiered-header">
        <div class="tiered-title">
          <i class="el-icon-s-opportunity"></i>
          <span>志愿推荐清单</span>
          <span class="tiered-score-tag" v-if="tieredData.userScore">我的成绩：{{ tieredData.userScore }} 分</span>
        </div>
        <el-button type="primary" size="small" :loading="tieredLoading" @click="loadTieredData">
          <i class="el-icon-refresh"></i> 刷新推荐
        </el-button>
      </div>

      <div v-loading="tieredLoading">
        <el-row :gutter="16">
          <!-- 冲刺 -->
          <el-col :span="8">
            <el-card class="tier-card tier-reach" shadow="never">
              <div slot="header" class="tier-card-header">
                <span class="tier-icon">🚀</span>
                <span class="tier-name">冲刺志愿</span>
                <el-tag size="mini" type="danger">录取概率较低</el-tag>
              </div>
              <div v-if="tieredData.reach && tieredData.reach.length > 0">
                <div v-for="(item, idx) in tieredData.reach" :key="'r-' + idx" class="tier-item">
                  <div class="tier-item-rank">{{ idx + 1 }}</div>
                  <div class="tier-item-body">
                    <div class="tier-item-uni">{{ item.universityName }}</div>
                    <div class="tier-item-major">{{ item.majorName }}</div>
                    <div class="tier-item-meta">
                      <span>2025投档线：<b>{{ item.minScore2025 }}</b>分</span>
                      <span class="diff-tag diff-negative">高{{ Math.abs(item.scoreDiff) }}分</span>
                    </div>
                  </div>
                  <div class="tier-item-prob">
                    <el-progress type="circle" :percentage="item.probability" :width="48" :stroke-width="4" color="#F56C6C"></el-progress>
                  </div>
                </div>
              </div>
              <el-empty v-else description="暂无冲刺志愿" :image-size="60"></el-empty>
            </el-card>
          </el-col>

          <!-- 稳妥 -->
          <el-col :span="8">
            <el-card class="tier-card tier-match" shadow="never">
              <div slot="header" class="tier-card-header">
                <span class="tier-icon">🎯</span>
                <span class="tier-name">稳妥志愿</span>
                <el-tag size="mini" type="warning">录取概率中等</el-tag>
              </div>
              <div v-if="tieredData.match && tieredData.match.length > 0">
                <div v-for="(item, idx) in tieredData.match" :key="'m-' + idx" class="tier-item">
                  <div class="tier-item-rank">{{ idx + 1 }}</div>
                  <div class="tier-item-body">
                    <div class="tier-item-uni">{{ item.universityName }}</div>
                    <div class="tier-item-major">{{ item.majorName }}</div>
                    <div class="tier-item-meta">
                      <span>2025投档线：<b>{{ item.minScore2025 }}</b>分</span>
                      <span class="diff-tag diff-positive">超{{ item.scoreDiff }}分</span>
                    </div>
                  </div>
                  <div class="tier-item-prob">
                    <el-progress type="circle" :percentage="item.probability" :width="48" :stroke-width="4" color="#E6A23C"></el-progress>
                  </div>
                </div>
              </div>
              <el-empty v-else description="暂无稳妥志愿" :image-size="60"></el-empty>
            </el-card>
          </el-col>

          <!-- 保底 -->
          <el-col :span="8">
            <el-card class="tier-card tier-safe" shadow="never">
              <div slot="header" class="tier-card-header">
                <span class="tier-icon">🛡️</span>
                <span class="tier-name">保底志愿</span>
                <el-tag size="mini" type="success">录取概率较高</el-tag>
              </div>
              <div v-if="tieredData.safe && tieredData.safe.length > 0">
                <div v-for="(item, idx) in tieredData.safe" :key="'s-' + idx" class="tier-item">
                  <div class="tier-item-rank">{{ idx + 1 }}</div>
                  <div class="tier-item-body">
                    <div class="tier-item-uni">{{ item.universityName }}</div>
                    <div class="tier-item-major">{{ item.majorName }}</div>
                    <div class="tier-item-meta">
                      <span>2025投档线：<b>{{ item.minScore2025 }}</b>分</span>
                      <span class="diff-tag diff-positive">超{{ item.scoreDiff }}分</span>
                    </div>
                  </div>
                  <div class="tier-item-prob">
                    <el-progress type="circle" :percentage="item.probability" :width="48" :stroke-width="4" color="#67C23A"></el-progress>
                  </div>
                </div>
              </div>
              <el-empty v-else description="暂无保底志愿" :image-size="60"></el-empty>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 添加/编辑信息对话框 -->
    <el-dialog :visible.sync="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="infoForm" label-width="140px">
        <el-form-item label="学科类型">
          <el-select v-model="infoForm.subject" placeholder="请选择学科类型">
            <el-option label="历史类" :value="1"></el-option>
            <el-option label="物理类" :value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="目标高校水平">
          <el-select v-model="infoForm.universityLevel" placeholder="请选择目标高校水平">
            <el-option label="985高校" :value="0"></el-option>
            <el-option label="211高校" :value="1"></el-option>
            <el-option label="普通高校" :value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="高考成绩">
          <el-input-number v-model="infoForm.score" :min="0" :max="750"></el-input-number>
        </el-form-item>
        <el-form-item label="高校类型">
          <el-select v-model="infoForm.universityType" placeholder="请选择高校类型">
            <el-option label="综合" :value="1"></el-option>
            <el-option label="工科" :value="2"></el-option>
            <el-option label="财经" :value="3"></el-option>
            <el-option label="农业" :value="4"></el-option>
            <el-option label="政法" :value="5"></el-option>
            <el-option label="医药" :value="6"></el-option>
            <el-option label="林业" :value="7"></el-option>
            <el-option label="师范" :value="8"></el-option>
            <el-option label="体育" :value="9"></el-option>
            <el-option label="语言" :value="10"></el-option>
            <el-option label="艺术" :value="11"></el-option>
            <el-option label="民族" :value="12"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="理想专业方向">
          <el-select v-model="infoForm.idealMajorDirection" placeholder="请选择理想专业方向">
            <el-option label="工学" :value="1"></el-option>
            <el-option label="管理学" :value="2"></el-option>
            <el-option label="经济学" :value="3"></el-option>
            <el-option label="文学" :value="4"></el-option>
            <el-option label="医学" :value="5"></el-option>
            <el-option label="法学" :value="6"></el-option>
            <el-option label="教育学" :value="7"></el-option>
            <el-option label="农学" :value="8"></el-option>
            <el-option label="艺术学" :value="9"></el-option>
            <el-option label="哲学" :value="10"></el-option>
            <el-option label="历史学" :value="11"></el-option>
            <el-option label="理学" :value="12"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="地域偏好">
          <el-select v-model="infoForm.regionPreference" placeholder="请选择地域偏好">
            <el-option label="本省" :value="1"></el-option>
            <el-option label="一线城市" :value="2"></el-option>
            <el-option label="南方城市" :value="3"></el-option>
            <el-option label="北方城市" :value="4"></el-option>
            <el-option label="无要求" :value="5"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="日常兴趣">
          <el-select v-model="infoForm.dailyInterest" placeholder="请选择日常兴趣">
            <el-option label="阅读写作" :value="1"></el-option>
            <el-option label="实验研究" :value="2"></el-option>
            <el-option label="编程技术" :value="3"></el-option>
            <el-option label="艺术创作" :value="4"></el-option>
            <el-option label="社交活动" :value="5"></el-option>
            <el-option label="户外活动" :value="6"></el-option>
            <el-option label="手工制作" :value="7"></el-option>
            <el-option label="商业实践" :value="8"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">{{ dialogButtonText }}</el-button>
      </div>
    </el-dialog>

    <!-- 查看信息详情对话框 -->
    <el-dialog :visible.sync="viewDialogVisible" title="报名信息详情" width="60%">
      <el-form :model="viewInfoForm" label-width="140px">
        <el-form-item label="用户名">
          <el-input v-model="viewInfoForm.userName" disabled></el-input>
        </el-form-item>
        <el-form-item label="学科类型">
          <el-input :value="viewInfoForm.subject === 1 ? '历史类' : '物理类'" disabled></el-input>
        </el-form-item>
        <el-form-item label="目标高校水平">
          <el-input :value="getLevelLabel(viewInfoForm.universityLevel)" disabled></el-input>
        </el-form-item>
        <el-form-item label="高考成绩">
          <el-input v-model="viewInfoForm.score" disabled></el-input>
        </el-form-item>
        <el-form-item label="高校类型">
          <el-input :value="getUniversityTypeLabel(viewInfoForm.universityType)" disabled></el-input>
        </el-form-item>
        <el-form-item label="理想专业方向">
          <el-input :value="getMajorDirectionLabel(viewInfoForm.idealMajorDirection)" disabled></el-input>
        </el-form-item>
        <el-form-item label="地域偏好">
          <el-input :value="getRegionPreferenceLabel(viewInfoForm.regionPreference)" disabled></el-input>
        </el-form-item>
        <el-form-item label="日常兴趣">
          <el-input :value="getDailyInterestLabel(viewInfoForm.dailyInterest)" disabled></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { listAllInformation, getInformation, addInformation, updateInformation, deleteInformation } from "@/api/university/information";
import { getTieredRecommendations } from "@/api/university/recommendation";

export default {
  data() {
    return {
      loading: false,
      infoList: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
      },
      dialogVisible: false,
      viewDialogVisible: false,
      dialogTitle: "",
      dialogButtonText: "添加",
      infoForm: {
        infoId: null,
        userId: null,
        subject: null,
        universityLevel: null,
        score: null,
        universityType: null,
        idealMajorDirection: null,
        regionPreference: null,
        dailyInterest: null
      },
      viewInfoForm: {},
      tieredLoading: false,
      tieredData: { reach: [], match: [], safe: [], userScore: null }
    };
  },
  computed: {
    hasScore() {
      return this.infoList && this.infoList.some(item => item && item.score != null && item.score > 0);
    }
  },
  created() {
    this.fetchInfoList();
  },
  methods: {
    getLevelLabel(level) {
      const levels = { 0: '985高校', 1: '211高校', 2: '普通高校' };
      return levels[level] || '未知';
    },
    getUniversityTypeLabel(type) {
      const types = { 1: '综合', 2: '工科', 3: '财经', 4: '农业', 5: '政法', 6: '医药', 7: '林业', 8: '师范', 9: '体育', 10: '语言', 11: '艺术', 12: '民族' };
      return types[type] || '未知';
    },
    getMajorDirectionLabel(direction) {
      const directions = { 1: '工学', 2: '管理学', 3: '经济学', 4: '文学', 5: '医学', 6: '法学', 7: '教育学', 8: '农学', 9: '艺术学', 10: '哲学', 11: '历史学', 12: '理学' };
      return directions[direction] || '未知';
    },
    getRegionPreferenceLabel(preference) {
      const preferences = { 1: '本省', 2: '一线城市', 3: '南方城市', 4: '北方城市', 5: '无要求' };
      return preferences[preference] || '未知';
    },
    getDailyInterestLabel(interest) {
      const interests = { 1: '阅读写作', 2: '实验研究', 3: '编程技术', 4: '艺术创作', 5: '社交活动', 6: '户外活动', 7: '手工制作', 8: '商业实践' };
      return interests[interest] || '未知';
    },
    fetchInfoList() {
      this.loading = true;
      listAllInformation(this.queryParams).then((response) => {
        this.infoList = response.rows;
        this.total = response.total;
        if (!this.infoList || this.infoList.length === 0 || (this.infoList.length === 1 && this.infoList[0] === null)) {
          this.handleAddInfo();
        }
        this.loading = false;
        this.$nextTick(() => {
          if (this.hasScore) {
            this.loadTieredData();
          }
        });
      });
    },
    loadTieredData() {
      this.tieredLoading = true;
      getTieredRecommendations(10).then(res => {
        const data = res.data || {};
        this.tieredData = {
          reach: data.reach || [],
          match: data.match || [],
          safe: data.safe || [],
          userScore: data.userScore || null
        };
        this.tieredLoading = false;
      }).catch(() => {
        this.tieredLoading = false;
      });
    },
    handleView(row) {
      this.viewInfoForm = { ...row };
      this.viewDialogVisible = true;
    },
    handleAddInfo() {
      this.dialogTitle = "新增报名信息";
      this.dialogButtonText = "添加";
      this.clearForm();
      this.dialogVisible = true;
    },
    handleEdit(info) {
      this.dialogTitle = "编辑报名信息";
      this.dialogButtonText = "保存";
      getInformation(info.infoId).then((response) => {
        this.infoForm = response.data;
        this.dialogVisible = true;
      });
    },
    handleSubmit() {
      const action = this.infoForm.infoId ? updateInformation : addInformation;
      action(this.infoForm).then(() => {
        this.$message.success(this.infoForm.infoId ? "修改成功" : "添加成功");
        this.fetchInfoList();
        this.dialogVisible = false;
      });
    },
    handleDelete(info) {
      this.$confirm("确认删除该报名信息吗？", "提示", {
        type: "warning",
      }).then(() => {
        deleteInformation(info.infoId).then(() => {
          this.$message.success("删除成功");
          this.fetchInfoList();
        });
      });
    },
    clearForm() {
      this.infoForm = {
        infoId: null,
        userId: null,
        subject: null,
        universityLevel: null,
        score: null,
        universityType: null,
        idealMajorDirection: null,
        regionPreference: null,
        dailyInterest: null
      };
    }
  }
};
</script>

<style scoped lang="scss">
.mb-20 {
  margin-bottom: 20px;
}

.tiered-section {
  margin-top: 30px;

  .tiered-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    padding: 12px 16px;
    background: linear-gradient(135deg, #f0f5ff, #fafafa);
    border-radius: 6px;
    border: 1px solid #e4e7ed;

    .tiered-title {
      display: flex;
      align-items: center;
      gap: 10px;
      font-size: 18px;
      font-weight: 700;
      color: #303133;

      i { font-size: 22px; color: #409EFF; }

      .tiered-score-tag {
        font-size: 13px;
        font-weight: 400;
        color: #409EFF;
        background: rgba(64, 158, 255, 0.08);
        padding: 2px 10px;
        border-radius: 12px;
      }
    }
  }

  .tier-card {
    height: 100%;
    border-top: 3px solid transparent;

    &.tier-reach { border-top-color: #F56C6C; }
    &.tier-match { border-top-color: #E6A23C; }
    &.tier-safe  { border-top-color: #67C23A; }

    .tier-card-header {
      display: flex;
      align-items: center;
      gap: 8px;

      .tier-icon { font-size: 18px; }
      .tier-name { font-weight: 700; font-size: 15px; }
    }
  }

  .tier-item {
    display: flex;
    align-items: center;
    padding: 10px 8px;
    border-bottom: 1px solid #f5f5f5;
    transition: background 0.2s;

    &:last-child { border-bottom: none; }
    &:hover { background: #fafbfc; }

    .tier-item-rank {
      width: 24px;
      height: 24px;
      border-radius: 50%;
      background: #e4e7ed;
      color: #606266;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 12px;
      font-weight: 700;
      flex-shrink: 0;
      margin-right: 10px;
    }

    .tier-item-body {
      flex: 1;
      min-width: 0;

      .tier-item-uni {
        font-weight: 600;
        font-size: 13px;
        color: #303133;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
      .tier-item-major {
        font-size: 12px;
        color: #606266;
        margin-top: 2px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
      .tier-item-meta {
        font-size: 11px;
        color: #909399;
        margin-top: 3px;
        display: flex;
        align-items: center;
        gap: 8px;

        .diff-tag {
          font-size: 11px;
          padding: 0 6px;
          border-radius: 8px;
          font-weight: 600;
        }
        .diff-negative {
          color: #F56C6C;
          background: rgba(245, 108, 108, 0.08);
        }
        .diff-positive {
          color: #67C23A;
          background: rgba(103, 194, 58, 0.08);
        }
      }
    }

    .tier-item-prob {
      flex-shrink: 0;
      margin-left: 8px;
    }
  }
}
</style>
