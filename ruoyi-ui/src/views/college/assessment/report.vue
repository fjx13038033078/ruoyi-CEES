<template>
  <div class="app-container assessment-report" v-loading="loading">
    <el-page-header @back="goBack" content="测评报告"></el-page-header>

    <div v-if="result" class="report-content">
      <!-- 霍兰德代码 -->
      <div class="code-section">
        <h2>你的霍兰德代码</h2>
        <div class="holland-code">
          <span v-for="(c, i) in result.hollandCode.split('')" :key="i" class="code-char" :style="{ background: dimColor(c) }">
            {{ c }}
          </span>
        </div>
        <p class="code-label">
          {{ result.hollandCode.split('').map(c => dimName(c)).join(' + ') }}
        </p>
      </div>

      <el-row :gutter="24">
        <!-- 雷达图 -->
        <el-col :span="12">
          <el-card shadow="never">
            <div slot="header"><b>六维度得分分布</b></div>
            <div ref="radarChart" class="radar-chart"></div>
          </el-card>
        </el-col>

        <!-- 性格描述 -->
        <el-col :span="12">
          <el-card shadow="never">
            <div slot="header"><b>性格特征分析</b></div>
            <div class="personality-text" v-html="formatText(result.personalitySummary)"></div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 推荐专业 -->
      <el-card shadow="never" style="margin-top:20px">
        <div slot="header"><b>推荐专业方向</b></div>
        <div v-for="(dir, di) in recommendedMajors" :key="di" class="direction-section">
          <div class="direction-header">
            <el-tag :style="{ background: dimColor(dir.hollandCode), borderColor: dimColor(dir.hollandCode) }" size="small" effect="dark">
              {{ dir.hollandCode }}
            </el-tag>
            <span class="direction-name">{{ dir.direction }}</span>
            <span class="direction-desc">{{ dir.description }}</span>
          </div>
          <div class="major-chips" v-if="dir.majors && dir.majors.length > 0">
            <div v-for="m in dir.majors" :key="m.majorId" class="major-chip">
              <div class="chip-name">{{ m.majorName }}</div>
              <div class="chip-info">{{ m.universityName }} | {{ m.minScore2024 ? m.minScore2024 + '分' : '-' }}</div>
            </div>
          </div>
          <div v-else class="no-major">暂未匹配到符合条件的具体专业</div>
        </div>
      </el-card>
    </div>

    <el-empty v-if="!loading && !result" description="未找到测评结果"></el-empty>
  </div>
</template>

<script>
import { getResult } from "@/api/college/assessment";
import * as echarts from 'echarts';

export default {
  name: "AssessmentReport",
  data() {
    return {
      loading: true,
      result: null,
      recommendedMajors: [],
      colorMap: {
        R: '#F56C6C', I: '#E6A23C', A: '#67C23A', S: '#409EFF', E: '#9B59B6', C: '#606266'
      },
      nameMap: {
        R: '现实型', I: '研究型', A: '艺术型', S: '社会型', E: '企业型', C: '常规型'
      }
    };
  },
  created() {
    const recordId = this.$route.query.recordId;
    if (recordId) {
      this.loadResult(recordId);
    } else {
      this.loading = false;
    }
  },
  methods: {
    goBack() {
      this.$router.push('/volunteer/assessment');
    },
    loadResult(recordId) {
      getResult(recordId).then(res => {
        this.result = res.data;
        if (this.result && this.result.recommendedMajors) {
          try {
            this.recommendedMajors = JSON.parse(this.result.recommendedMajors);
          } catch (e) {
            this.recommendedMajors = [];
          }
        }
        this.loading = false;
        this.$nextTick(() => { this.renderRadar(); });
      }).catch(() => { this.loading = false; });
    },
    renderRadar() {
      if (!this.result || !this.$refs.radarChart) return;
      const chart = echarts.init(this.$refs.radarChart);
      const r = this.result;
      chart.setOption({
        radar: {
          indicator: [
            { name: '现实型(R)', max: 15 },
            { name: '研究型(I)', max: 15 },
            { name: '艺术型(A)', max: 15 },
            { name: '社会型(S)', max: 15 },
            { name: '企业型(E)', max: 15 },
            { name: '常规型(C)', max: 15 }
          ],
          shape: 'polygon',
          splitNumber: 5,
          name: { textStyle: { color: '#333', fontSize: 13 } }
        },
        series: [{
          type: 'radar',
          data: [{
            value: [r.rScore, r.iScore, r.aScore, r.sScore, r.eScore, r.cScore],
            name: '得分',
            areaStyle: { color: 'rgba(64,158,255,0.2)' },
            lineStyle: { color: '#409EFF', width: 2 },
            itemStyle: { color: '#409EFF' }
          }]
        }],
        tooltip: {}
      });
      window.addEventListener('resize', () => chart.resize());
    },
    dimColor(code) {
      if (!code) return '#ccc';
      return this.colorMap[code.charAt(0)] || '#ccc';
    },
    dimName(c) {
      return this.nameMap[c] || c;
    },
    formatText(text) {
      if (!text) return '';
      return text.replace(/\n/g, '<br>');
    }
  }
};
</script>

<style scoped lang="scss">
.assessment-report {
  .report-content { margin-top: 24px; }

  .code-section {
    text-align: center;
    margin-bottom: 24px;
    h2 { margin-bottom: 16px; }
    .holland-code {
      display: flex;
      justify-content: center;
      gap: 12px;
      .code-char {
        width: 56px; height: 56px; border-radius: 50%; color: #fff;
        display: flex; align-items: center; justify-content: center;
        font-size: 24px; font-weight: 700;
      }
    }
    .code-label { color: #606266; margin-top: 12px; font-size: 16px; }
  }

  .radar-chart { width: 100%; height: 350px; }

  .personality-text {
    color: #606266;
    line-height: 1.8;
    font-size: 14px;
    padding: 8px 0;
  }

  .direction-section {
    margin-bottom: 20px;
    padding-bottom: 16px;
    border-bottom: 1px solid #EBEEF5;
    &:last-child { border-bottom: none; }

    .direction-header {
      display: flex;
      align-items: center;
      gap: 10px;
      margin-bottom: 10px;
      .direction-name { font-weight: 600; font-size: 16px; }
      .direction-desc { color: #909399; font-size: 13px; }
    }

    .major-chips {
      display: flex;
      flex-wrap: wrap;
      gap: 10px;
      .major-chip {
        border: 1px solid #EBEEF5;
        border-radius: 6px;
        padding: 8px 14px;
        background: #fafafa;
        .chip-name { font-weight: 600; font-size: 14px; }
        .chip-info { color: #909399; font-size: 12px; margin-top: 2px; }
      }
    }

    .no-major { color: #C0C4CC; font-size: 13px; padding: 8px 0; }
  }
}
</style>
