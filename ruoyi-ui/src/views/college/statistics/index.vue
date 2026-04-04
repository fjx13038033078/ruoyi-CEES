<template>
  <div class="app-container statistics-page">
    <el-row :gutter="20">
      <!-- 图表1：院校专业投档线走势 -->
      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <div slot="header" class="card-header">
            <b>院校专业投档线走势（2023-2025）</b>
            <el-select
              v-model="selectedUniId"
              filterable
              remote
              reserve-keyword
              placeholder="搜索并选择院校"
              :remote-method="filterUniversities"
              :loading="uniLoading"
              @change="onUniversityChange"
              style="width: 240px"
              size="small"
            >
              <el-option
                v-for="u in filteredUniOptions"
                :key="u.universityId"
                :label="u.universityName"
                :value="u.universityId"
              ></el-option>
            </el-select>
          </div>
          <div ref="majorTrendChart" class="chart-area"></div>
        </el-card>
      </el-col>

      <!-- 图表2：高校层次与类型分布 -->
      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <div slot="header"><b>高校层次与类型分布</b></div>
          <div ref="uniDistChart" class="chart-area"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <!-- 图表3：各省份高校资源对比 -->
      <el-col :span="24">
        <el-card shadow="never" class="chart-card">
          <div slot="header"><b>各省份高校资源对比</b></div>
          <div ref="provinceResourceChart" class="chart-area-tall"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import { getUniversityOptions, getMajorTrend, getAllProvinces, getUniversityDist } from '@/api/college/statistics';

const COLORS = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#9B59B6', '#1ABC9C', '#3498DB', '#E74C3C', '#2ECC71', '#F39C12'];

export default {
  name: 'CollegeStatistics',
  data() {
    return {
      uniOptions: [],
      filteredUniOptions: [],
      uniLoading: false,
      selectedUniId: null,
      provinceList: [],
      charts: {}
    };
  },
  mounted() {
    this.init();
  },
  beforeDestroy() {
    Object.values(this.charts).forEach(c => { if (c) c.dispose(); });
    window.removeEventListener('resize', this.handleResize);
  },
  methods: {
    async init() {
      window.addEventListener('resize', this.handleResize);
      const [uniRes, provRes, distRes] = await Promise.all([
        getUniversityOptions(),
        getAllProvinces(),
        getUniversityDist()
      ]);
      this.uniOptions = uniRes.data || [];
      this.filteredUniOptions = this.uniOptions.slice(0, 50);
      this.provinceList = provRes.data || [];

      if (this.uniOptions.length > 0) {
        this.selectedUniId = this.uniOptions[0].universityId;
        this.onUniversityChange(this.selectedUniId);
      }

      this.renderUniDist(distRes.data || {});
      this.renderProvinceResource();
    },

    filterUniversities(query) {
      if (query) {
        this.filteredUniOptions = this.uniOptions.filter(u => u.universityName.indexOf(query) > -1);
      } else {
        this.filteredUniOptions = this.uniOptions.slice(0, 50);
      }
    },

    async onUniversityChange(universityId) {
      if (!universityId) return;
      const res = await getMajorTrend(universityId);
      this.renderMajorTrend(res.data || []);
    },

    handleResize() {
      Object.values(this.charts).forEach(c => { if (c) c.resize(); });
    },

    // ========== 图表1：院校专业投档线走势 ==========
    renderMajorTrend(majors) {
      if (!this.$refs.majorTrendChart) return;
      if (this.charts.majorTrend) this.charts.majorTrend.dispose();
      const chart = echarts.init(this.$refs.majorTrendChart);
      this.charts.majorTrend = chart;

      const validMajors = majors.filter(m => m.minScore2023 || m.minScore2024 || m.minScore2025);
      if (validMajors.length === 0) {
        chart.setOption({ title: { text: '该院校暂无投档线数据', left: 'center', top: 'middle', textStyle: { color: '#999', fontSize: 14 } } });
        return;
      }

      const series = validMajors.map((m, i) => ({
        name: m.majorName,
        type: 'line',
        data: [m.minScore2023, m.minScore2024, m.minScore2025],
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        lineStyle: { width: 2 },
        itemStyle: { color: COLORS[i % COLORS.length] },
        connectNulls: true
      }));

      chart.setOption({
        tooltip: {
          trigger: 'axis',
          formatter: params => {
            let html = `<b>${params[0].axisValue}</b><br/>`;
            params.forEach(p => {
              if (p.value != null) html += `${p.marker} ${p.seriesName}：<b>${p.value}</b>分<br/>`;
            });
            return html;
          }
        },
        legend: {
          type: 'scroll',
          bottom: 0,
          textStyle: { fontSize: 11 }
        },
        grid: { top: 30, right: 20, bottom: 50, left: 55 },
        xAxis: { type: 'category', data: ['2023年', '2024年', '2025年'], boundaryGap: false },
        yAxis: { type: 'value', name: '投档线（分）', min: function(v) { return Math.max(0, v.min - 20); } },
        series
      }, true);
    },

    // ========== 图表2：高校层次与类型分布 ==========
    renderUniDist(dist) {
      if (!this.$refs.uniDistChart) return;
      if (this.charts.uniDist) this.charts.uniDist.dispose();
      const chart = echarts.init(this.$refs.uniDistChart);
      this.charts.uniDist = chart;

      chart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}：{c} 所（{d}%）' },
        legend: { bottom: 0, textStyle: { fontSize: 11 } },
        series: [
          {
            name: '高校层次',
            type: 'pie',
            radius: ['0%', '40%'],
            center: ['50%', '42%'],
            label: { position: 'inner', fontSize: 11, color: '#fff', formatter: '{b}\n{c}所' },
            data: [
              { value: dist.count985, name: '985', itemStyle: { color: '#F56C6C' } },
              { value: dist.count211Only, name: '211(非985)', itemStyle: { color: '#E6A23C' } },
              { value: dist.countDoubleFirstOnly, name: '双一流(非211)', itemStyle: { color: '#409EFF' } },
              { value: dist.countNormal, name: '普通院校', itemStyle: { color: '#C0C4CC' } }
            ].filter(d => d.value > 0)
          },
          {
            name: '办学类型',
            type: 'pie',
            radius: ['52%', '68%'],
            center: ['50%', '42%'],
            label: { formatter: '{b}: {c}所', fontSize: 11 },
            labelLine: { length: 12, length2: 8 },
            data: [
              { value: dist.countPublic, name: '公办', itemStyle: { color: '#67C23A' } },
              { value: dist.countPrivate, name: '民办', itemStyle: { color: '#9B59B6' } },
              { value: dist.countBachelor, name: '本科', itemStyle: { color: '#3498DB' } },
              { value: dist.countCollege, name: '专科', itemStyle: { color: '#1ABC9C' } }
            ].filter(d => d.value > 0)
          }
        ]
      }, true);
    },

    // ========== 图表3：各省份高校资源对比 ==========
    renderProvinceResource() {
      if (!this.$refs.provinceResourceChart) return;
      if (this.charts.provinceResource) this.charts.provinceResource.dispose();
      const chart = echarts.init(this.$refs.provinceResourceChart);
      this.charts.provinceResource = chart;

      const sorted = [...this.provinceList]
        .filter(p => p.numTotal > 0)
        .sort((a, b) => a.numTotal - b.numTotal);

      const provinces = sorted.map(p => p.provinceName);
      const data985 = sorted.map(p => p.num985 || 0);
      const data211 = sorted.map(p => Math.max(0, (p.num211 || 0) - (p.num985 || 0)));
      const dataOther = sorted.map(p => Math.max(0, (p.numTotal || 0) - (p.num211 || 0)));

      chart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' },
          formatter: params => {
            let html = `<b>${params[0].axisValue}</b><br/>`;
            let total = 0;
            params.forEach(p => { html += `${p.marker} ${p.seriesName}：<b>${p.value}</b> 所<br/>`; total += p.value; });
            html += `合计：<b>${total}</b> 所`;
            return html;
          }
        },
        legend: { top: 0, textStyle: { fontSize: 11 } },
        grid: { top: 30, right: 30, bottom: 10, left: 80, containLabel: false },
        xAxis: { type: 'value', name: '数量（所）' },
        yAxis: { type: 'category', data: provinces, axisLabel: { fontSize: 11 } },
        series: [
          { name: '985', type: 'bar', stack: 'total', data: data985, itemStyle: { color: '#F56C6C' }, barMaxWidth: 18 },
          { name: '211(非985)', type: 'bar', stack: 'total', data: data211, itemStyle: { color: '#E6A23C' }, barMaxWidth: 18 },
          { name: '其他院校', type: 'bar', stack: 'total', data: dataOther, itemStyle: { color: '#409EFF' }, barMaxWidth: 18 }
        ]
      }, true);
    }
  }
};
</script>

<style scoped lang="scss">
.statistics-page {
  .chart-card {
    height: 100%;
  }
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 8px;
  }
  .chart-area {
    width: 100%;
    height: 380px;
  }
  .chart-area-tall {
    width: 100%;
    height: 380px;
  }
}
</style>
