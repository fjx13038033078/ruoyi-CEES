<template>
  <div class="app-container assessment-index">
    <div class="page-header">
      <h2>霍兰德职业兴趣测评</h2>
      <p class="desc">通过50道二选一测试题，分析你的职业兴趣类型，为你推荐匹配的专业方向。</p>
    </div>

    <el-row :gutter="24">
      <el-col :span="16">
        <el-card shadow="never">
          <div class="intro-section">
            <h3>关于霍兰德测评</h3>
            <p>霍兰德职业兴趣理论将人的职业兴趣分为六种类型：</p>
            <el-row :gutter="12" class="type-grid">
              <el-col :span="8" v-for="t in types" :key="t.code">
                <div class="type-card" :style="{ borderColor: t.color }">
                  <div class="type-code" :style="{ background: t.color }">{{ t.code }}</div>
                  <div class="type-name">{{ t.name }}</div>
                  <div class="type-desc">{{ t.desc }}</div>
                </div>
              </el-col>
            </el-row>
            <div class="test-info">
              <p><i class="el-icon-time"></i> 预计用时：10-15分钟</p>
              <p><i class="el-icon-document"></i> 共50道题，每题二选一</p>
              <p><i class="el-icon-info"></i> 无对错之分，请根据直觉作答</p>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card shadow="never" class="action-card">
          <div class="action-content">
            <el-button type="primary" size="large" icon="el-icon-s-promotion" @click="handleStart">
              {{ latestRecord && latestRecord.status === 0 ? '继续测评' : '开始测评' }}
            </el-button>
            <el-button v-if="latestResult" type="success" size="large" icon="el-icon-data-analysis" @click="viewReport">
              查看最新报告
            </el-button>
          </div>
          <div v-if="latestResult" class="latest-result">
            <h4>最近测评结果</h4>
            <p>霍兰德代码：<b>{{ latestResult.hollandCode }}</b></p>
            <p>测评时间：{{ latestResult.createTime }}</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { startTest, getLatestRecord, getLatestResult } from "@/api/college/assessment";

export default {
  name: "AssessmentIndex",
  data() {
    return {
      latestRecord: null,
      latestResult: null,
      types: [
        { code: 'R', name: '现实型', desc: '动手操作、工具机械', color: '#F56C6C' },
        { code: 'I', name: '研究型', desc: '思考分析、科学探索', color: '#E6A23C' },
        { code: 'A', name: '艺术型', desc: '创意表达、审美设计', color: '#67C23A' },
        { code: 'S', name: '社会型', desc: '助人服务、教育沟通', color: '#409EFF' },
        { code: 'E', name: '企业型', desc: '领导决策、组织管理', color: '#9B59B6' },
        { code: 'C', name: '常规型', desc: '规范有序、数据处理', color: '#606266' }
      ]
    };
  },
  created() {
    this.loadStatus();
  },
  methods: {
    loadStatus() {
      getLatestRecord().then(res => { this.latestRecord = res.data; });
      getLatestResult().then(res => { this.latestResult = res.data; });
    },
    handleStart() {
      if (this.latestRecord && this.latestRecord.status === 0) {
        this.$router.push({ path: '/volunteer/assessment', query: { recordId: this.latestRecord.recordId } });
      } else {
        startTest().then(res => {
          const record = res.data;
          this.$router.push({ path: '/volunteer/assessment', query: { recordId: record.recordId } });
        });
      }
    },
    viewReport() {
      if (this.latestResult) {
        this.$router.push({ path: '/volunteer/report', query: { recordId: this.latestResult.recordId } });
      }
    }
  }
};
</script>

<style scoped lang="scss">
.assessment-index {
  .page-header {
    margin-bottom: 24px;
    h2 { margin: 0 0 8px 0; font-size: 22px; }
    .desc { color: #909399; margin: 0; }
  }
  .intro-section {
    h3 { margin-top: 0; }
    p { color: #606266; line-height: 1.8; }
  }
  .type-grid { margin: 16px 0; }
  .type-card {
    border: 2px solid #ddd;
    border-radius: 8px;
    padding: 12px;
    text-align: center;
    margin-bottom: 12px;
    .type-code {
      width: 36px; height: 36px; border-radius: 50%; color: #fff;
      display: inline-flex; align-items: center; justify-content: center;
      font-weight: 700; font-size: 16px; margin-bottom: 6px;
    }
    .type-name { font-weight: 600; font-size: 15px; margin-bottom: 4px; }
    .type-desc { color: #909399; font-size: 12px; }
  }
  .test-info {
    margin-top: 16px;
    p { margin: 6px 0; color: #606266; i { margin-right: 6px; color: #409EFF; } }
  }
  .action-card {
    .action-content {
      display: flex; flex-direction: column; gap: 12px;
      .el-button { width: 100%; }
    }
    .latest-result {
      margin-top: 20px; padding-top: 16px; border-top: 1px solid #EBEEF5;
      h4 { margin-top: 0; }
      p { color: #606266; margin: 6px 0; b { color: #409EFF; font-size: 18px; } }
    }
  }
}
</style>
