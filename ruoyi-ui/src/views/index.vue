<template>
  <div>
    <div v-if="isAdmin" class="admin-dashboard">
      <!-- 管理员仪表盘 -->
      <div class="admin-header">
        <div class="admin-welcome">
          <div class="welcome-icon">
            <i class="el-icon-s-custom"></i>
          </div>
          <div class="welcome-info">
            <h2>欢迎回来，管理员</h2>
            <p>{{ currentDate }}</p>
          </div>
        </div>
      </div>

      <!-- 快速操作和通知区域 -->
      <el-row :gutter="20" class="quick-actions">
        <el-col :span="12">
          <el-card class="action-card">
            <div slot="header" class="card-header">
              <i class="el-icon-s-operation"></i>
              <span>快速操作</span>
            </div>
            <div class="action-buttons">
              <div class="action-btn btn-blue" @click="handleQuickAction('add')">
                <i class="el-icon-edit-outline"></i>
                <span>新增信息</span>
              </div>
              <div class="action-btn btn-green" @click="handleQuickAction('view')">
                <i class="el-icon-pie-chart"></i>
                <span>查看统计</span>
              </div>
              <div class="action-btn btn-orange" @click="handleQuickAction('settings')">
                <i class="el-icon-setting"></i>
                <span>系统设置</span>
              </div>
              <div class="action-btn btn-purple" @click="goToPage('/system/user')">
                <i class="el-icon-user"></i>
                <span>用户管理</span>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="notice-card">
            <div slot="header" class="card-header">
              <i class="el-icon-bell"></i>
              <span>最新通知</span>
            </div>
            <div class="notice-timeline">
              <el-timeline>
                <el-timeline-item
                  v-for="(notice, index) in recentNotices"
                  :key="index"
                  :timestamp="notice.createTime"
                  placement="top"
                >
                  <div class="timeline-content">{{ notice.noticeTitle || notice.title }}</div>
                </el-timeline-item>
              </el-timeline>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 普通用户视图 -->
    <div v-else class="user-dashboard">
      <!-- 新title样式 -->
      <div class="page-title-section">
        <div class="title-content">
          <div class="title-icon">
            <i class="el-icon-s-opportunity"></i>
          </div>
          <div class="title-text">
            <h1>高考志愿智能填报系统</h1>
            <p class="title-desc">智能分析 · 精准推荐 · 助力圆梦</p>
          </div>
        </div>
        <div class="title-decoration"></div>
      </div>

      <div class="dashboard-content">
        <!-- 普通用户：近期通知公告（只读，点击标题查看正文） -->
        <el-card class="user-notice-card" shadow="never">
          <div slot="header" class="user-notice-header">
            <span><i class="el-icon-bell"></i> 通知公告</span>
            <span class="user-notice-sub">近期更新 · 仅可查看</span>
          </div>
          <div v-if="userHomeNotices.length" class="user-notice-list">
            <div
              v-for="item in userHomeNotices"
              :key="item.noticeId"
              class="user-notice-row"
              @click="showNoticeContent(item)"
            >
              <span class="notice-title-text">{{ item.noticeTitle }}</span>
              <span class="notice-time">{{ parseTime(item.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
              <i class="el-icon-arrow-right notice-arrow"></i>
            </div>
          </div>
          <el-empty v-else description="暂无通知公告" :image-size="64"></el-empty>
        </el-card>

        <!-- 轮播图和MBTI测试区域 -->
        <el-row :gutter="20" class="top-section">
          <el-col :span="18">
            <el-card class="carousel-card" :body-style="{ padding: '0' }">
              <el-carousel :interval="4000" type="card" height="300px">
                <el-carousel-item>
                  <a href="https://www.hit.edu.cn/" target="_blank">
                    <img src="../assets/images/01.jpg" alt="Image 1" style="width: 100%;">
                  </a>
                </el-carousel-item>
                <el-carousel-item>
                  <a href="https://www.ustb.edu.cn/" target="_blank">
                    <img src="../assets/images/02.jpg" alt="Image 2" style="width: 100%;">
                  </a>
                </el-carousel-item>
                <el-carousel-item>
                  <a href="https://www.xjtu.edu.cn/" target="_blank">
                    <img src="../assets/images/03.jpg" alt="Image 3" style="width: 100%;">
                  </a>
                </el-carousel-item>
              </el-carousel>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="mbti-card" :body-style="{ padding: '0', height: '100%' }">
              <div class="mbti-content">
                <div class="mbti-image">
                  <img src="../assets/images/04.jpg" alt="MBTI测试" style="width: 100%; height: 100%; object-fit: cover;">
                </div>
                <div class="mbti-overlay">
                  <h3>MBTI 性格测试</h3>
                  <p>了解你的性格类型</p>
                  <el-button
                    type="primary"
                    class="mbti-button"
                    @click="openMbtiTest"
                  >
                    开始测试
                  </el-button>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 推荐内容展示区域 -->
        <div class="recommend-section" style="margin-top: 20px;">
          <div class="recommend-header-bar">
            <div class="header-left">
              <i class="el-icon-star-on"></i>
              <span class="header-title">为您推荐</span>
              <span class="header-subtitle">基于您的个人情况精选</span>
            </div>
          </div>
          <div class="recommend-body">
            <el-skeleton :loading="loadingRecommend" animated :rows="2">
              <template slot="default">
                <el-row :gutter="12" v-if="recommendedMajors.length > 0">
                  <el-col :span="6" v-for="(item, index) in recommendedMajors" :key="index">
                    <el-card class="recommend-item-card" shadow="hover">
                      <div class="card-rank">{{ index + 1 }}</div>
                      <div class="card-body">
                        <div class="card-university">{{ item.universityName }}</div>
                        <div class="card-major">{{ item.majorName }}</div>
                      </div>
                      <div class="card-footer">
                        <span class="score-label">分数线</span>
                        <span class="score-value">{{ item.minScore2024 }}分</span>
                      </div>
                    </el-card>
                  </el-col>
                </el-row>
                <div v-else class="no-recommend">
                  <i class="el-icon-warning-outline"></i>
                  <p>暂无推荐数据，请先完善个人信息</p>
                </div>
              </template>
            </el-skeleton>
          </div>
        </div>

        <!-- 系统功能介绍 -->
        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :span="24">
            <el-card class="feature-card">
              <div slot="header" class="section-header">
                <i class="el-icon-s-grid"></i>
                <span>系统功能</span>
              </div>
              <div class="feature-grid">
                <div class="feature-item" @click="goToPage('/university/information')">
                  <div class="feature-icon">
                    <i class="el-icon-school"></i>
                  </div>
                  <div class="feature-text">
                    <h4>院校查询</h4>
                    <p>全国高校信息一站式查询</p>
                  </div>
                </div>
                <div class="feature-item" @click="goToPage('/university/major')">
                  <div class="feature-icon">
                    <i class="el-icon-reading"></i>
                  </div>
                  <div class="feature-text">
                    <h4>专业探索</h4>
                    <p>深入了解各类专业详情</p>
                  </div>
                </div>
                <div class="feature-item" @click="goToPage('/university/chinaMap')">
                  <div class="feature-icon">
                    <i class="el-icon-map-location"></i>
                  </div>
                  <div class="feature-text">
                    <h4>地图分布</h4>
                    <p>可视化查看院校地理分布</p>
                  </div>
                </div>
                <div class="feature-item" @click="goToPage('/university/feedback')">
                  <div class="feature-icon">
                    <i class="el-icon-magic-stick"></i>
                  </div>
                  <div class="feature-text">
                    <h4>问题反馈</h4>
                    <p>系统问题及时反馈</p>
                  </div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>

      <!-- 弹出的公告内容卡片 -->
      <el-dialog :title="selectedNotice.title" :visible.sync="showNoticeDialog" width="780px" append-to-body>
        <div slot="title" style="text-align: center;">{{ selectedNotice.title }}</div>
        <div v-html="selectedNotice.content" class="notice-content"></div>
      </el-dialog>
  </div>
</template>

<script>
import {listNotice, getNotice, getPublicNotice} from "@/api/system/notice";
import {recommendMajors} from "@/api/university/recommendation";
import * as echarts from 'echarts'

export default {
  name: "Notice",
  dicts: ['sys_notice_status', 'sys_notice_type'],
  data() {
    return {
      isAdmin: false,
      currentDate: '',
      recentNotices: [],
      /** 普通用户首页：近期 5 条正常状态公告 */
      userHomeNotices: [],
      // 遮罩层
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      noticeList: [],
      title: "",
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        noticeTitle: undefined,
        createBy: undefined,
        status: undefined
      },
      selectedNotice: {
        title: '',
        content: ''
      },
      showNoticeDialog: false,
      form: {},
      // 推荐数据
      loadingRecommend: false,
      recommendedMajors: [],
      rules: {
        noticeTitle: [
          {required: true, message: "公告标题不能为空", trigger: "blur"}
        ],
        noticeType: [
          {required: true, message: "公告类型不能为空", trigger: "change"}
        ]
      }
    };
  },
  created() {
    this.getInfo();
    if (this.isAdmin) {
      this.getList();
    } else {
      this.getUserHomeNotices();
    }
    this.setCurrentDate();
    this.fetchRecommendedMajors();
  },
  mounted() {
    this.initEchartsText();
  },
  methods: {
    getInfo() {
      this.isAdmin = this.$store.getters.roles.includes('admin');
    },
    setCurrentDate() {
      const now = new Date();
      this.currentDate = now.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        weekday: 'long'
      });
    },
    handleQuickAction(action) {
      switch(action) {
        case 'add':
          this.$router.push('/university/information');
          break;
        case 'view':
          this.$router.push('/university/chinaMap');
          break;
        case 'settings':
          this.$router.push('/system/user');
          break;
      }
    },
    getList() {
      this.loading = true;
      listNotice(this.queryParams).then(response => {
        this.noticeList = response.rows;
        this.recentNotices = response.rows.slice(0, 5);
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 普通用户首页：仅拉取状态为「正常」的最近 5 条 */
    getUserHomeNotices() {
      listNotice({
        pageNum: 1,
        pageSize: 5,
        status: '0'
      }).then((response) => {
        this.userHomeNotices = response.rows || [];
      }).catch(() => {
        this.userHomeNotices = [];
      });
    },
    showNoticeContent(row) {
      const api = this.isAdmin ? getNotice : getPublicNotice;
      this.loading = true;
      api(row.noticeId).then((response) => {
        const data = response.data;
        this.selectedNotice.title = data.noticeTitle;
        this.selectedNotice.content = data.noticeContent;
        this.showNoticeDialog = true;
        this.loading = false;
      }).catch(() => {
        this.loading = false;
        this.$message.error('加载公告失败');
      });
    },
    initEchartsText() {
      const chartDom = this.$refs.echartsText;
      const myChart = echarts.init(chartDom);
      const option = {
        graphic: {
          elements: [
            {
              type: 'text',
              left: 'center',
              top: 'center',
              style: {
                text: '高考志愿智能填报系统',
                fontSize: 80,
                fontWeight: 'bold',
                lineDash: [0, 200],
                lineDashOffset: 0,
                fill: 'transparent',
                stroke: '#000',
                lineWidth: 1
              },
              keyframeAnimation: {
                duration: 3000,
                loop: true,
                keyframes: [
                  {
                    percent: 0.7,
                    style: {
                      fill: 'transparent',
                      lineDashOffset: 200,
                      lineDash: [200, 0]
                    }
                  },
                  {
                    percent: 0.8,
                    style: {
                      fill: 'transparent'
                    }
                  },
                  {
                    percent: 1,
                    style: {
                      fill: 'black'
                    }
                  }
                ]
              }
            }
          ]
        }
      };
      myChart.setOption(option);
    },
    openMbtiTest() {
      // 使用更安全的方式打开新窗口
      try {
        const url = 'https://www.16personalities.com/ch/人格测试';
        const newWindow = window.open(url, '_blank');

        // 检查是否被浏览器阻止弹窗
        if (!newWindow || newWindow.closed || typeof newWindow.closed === 'undefined') {
          // 如果弹窗被阻止，提示用户手动打开
          this.$message({
            message: '浏览器阻止了弹窗，请手动复制链接访问：' + url,
            type: 'warning',
            duration: 5000,
            showClose: true
          });

          // 同时尝试复制链接到剪贴板
          this.copyToClipboard(url);
        }
      } catch (error) {
        console.error('打开MBTI测试页面失败:', error);
        this.$message.error('打开测试页面失败，请稍后重试');
      }
    },
    copyToClipboard(text) {
      // 复制链接到剪贴板
      if (navigator.clipboard && window.isSecureContext) {
        navigator.clipboard.writeText(text).then(() => {
          this.$message.success('链接已复制到剪贴板');
        }).catch(() => {
          this.fallbackCopyToClipboard(text);
        });
      } else {
        this.fallbackCopyToClipboard(text);
      }
    },
    fallbackCopyToClipboard(text) {
      // 兼容性方案：创建临时textarea元素
      const textarea = document.createElement('textarea');
      textarea.value = text;
      document.body.appendChild(textarea);
      textarea.select();
      try {
        document.execCommand('copy');
        this.$message.success('链接已复制到剪贴板');
      } catch (error) {
        console.error('复制失败:', error);
      }
      document.body.removeChild(textarea);
    },
    goToPage(path) {
      this.$router.push(path);
    },
    // 获取推荐专业
    fetchRecommendedMajors() {
      if (this.isAdmin) return; // 管理员不需要获取推荐数据
      this.loadingRecommend = true;
      recommendMajors()
        .then((response) => {
          this.recommendedMajors = response.data || [];
        })
        .catch(() => {
          this.recommendedMajors = [];
        })
        .finally(() => {
          this.loadingRecommend = false;
        });
    }
  }
};
</script>

<style scoped lang="scss">
/* ========== 管理员首页样式 ========== */
.admin-dashboard {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 84px);

  .admin-header {
    margin-bottom: 24px;

    .admin-welcome {
      display: flex;
      align-items: center;
      padding: 24px;
      background: #fff;
      border: 1px solid #e4e7ed;

      .welcome-icon {
        width: 60px;
        height: 60px;
        background: #1a5fb4;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 20px;

        i {
          font-size: 28px;
          color: #fff;
        }
      }

      .welcome-info {
        h2 {
          margin: 0 0 8px 0;
          font-size: 22px;
          color: #303133;
          font-weight: 600;
        }

        p {
          margin: 0;
          color: #909399;
          font-size: 14px;
        }
      }
    }
  }

  .quick-actions {
    .action-card, .notice-card {
      height: 320px;
      border: 1px solid #e4e7ed;

      .card-header {
        display: flex;
        align-items: center;
        font-size: 16px;
        font-weight: 600;
        color: #303133;

        i {
          margin-right: 8px;
          color: #1a5fb4;
        }
      }
    }

    .action-card {
      .action-buttons {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 16px;
        padding: 10px;

        .action-btn {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          padding: 20px;
          cursor: pointer;
          transition: all 0.2s ease;

          i {
            font-size: 28px;
            color: #fff;
            margin-bottom: 10px;
          }

          span {
            font-size: 14px;
            color: #fff;
            font-weight: 500;
          }

          &:hover {
            opacity: 0.85;
            transform: translateY(-2px);
          }

          &.btn-blue { background: #1a5fb4; }
          &.btn-green { background: #26a269; }
          &.btn-orange { background: #e5a50a; }
          &.btn-purple { background: #613583; }
        }
      }
    }

    .notice-card {
      .notice-timeline {
        padding: 10px;
        height: calc(100% - 60px);
        overflow-y: auto;

        .timeline-content {
          font-size: 14px;
          color: #606266;
        }
      }
    }
  }
}

/* ========== 普通用户首页样式 ========== */
.user-dashboard {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 84px);
}

.page-title-section {
  margin-bottom: 24px;
  padding: 30px;
  background: #fff;
  border: 1px solid #e4e7ed;
  position: relative;

  .title-content {
    display: flex;
    align-items: center;

    .title-icon {
      width: 64px;
      height: 64px;
      background: #1a5fb4;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 24px;

      i {
        font-size: 32px;
        color: #fff;
      }
    }

    .title-text {
      h1 {
        margin: 0 0 8px 0;
        font-size: 28px;
        color: #303133;
        font-weight: 700;
        letter-spacing: 2px;
      }

      .title-desc {
        margin: 0;
        font-size: 14px;
        color: #909399;
        letter-spacing: 4px;
      }
    }
  }

  .title-decoration {
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 3px;
    background: #1a5fb4;
  }
}

.user-notice-card {
  margin-bottom: 20px;
  border: 1px solid #e4e7ed;

  .user-notice-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 16px;
    font-weight: 600;
    color: #303133;

    i {
      margin-right: 8px;
      color: #1a5fb4;
    }

    .user-notice-sub {
      font-size: 12px;
      font-weight: 400;
      color: #909399;
    }
  }

  .user-notice-list {
    .user-notice-row {
      display: flex;
      align-items: center;
      padding: 12px 14px;
      border-bottom: 1px solid #f0f0f0;
      cursor: pointer;
      transition: background 0.2s;

      &:last-child {
        border-bottom: none;
      }

      &:hover {
        background: #f5f9ff;
      }

      .notice-title-text {
        flex: 1;
        font-size: 14px;
        color: #303133;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        padding-right: 12px;
      }

      .notice-time {
        font-size: 12px;
        color: #909399;
        flex-shrink: 0;
      }

      .notice-arrow {
        margin-left: 8px;
        color: #c0c4cc;
        font-size: 12px;
      }
    }
  }
}

.top-section {
  .carousel-card {
    height: 340px;
    border: 1px solid #e4e7ed;
    overflow: hidden;

    ::v-deep .el-carousel__item {
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }
  }

  .mbti-card {
    height: 340px;
    border: 1px solid #e4e7ed;
    overflow: hidden;

    &:hover {
      .mbti-overlay {
        opacity: 1;
      }
    }

    .mbti-content {
      position: relative;
      height: 300px;
      width: 100%;
    }

    .mbti-image {
      height: 100%;
      width: 100%;
      overflow: hidden;

      img {
        transition: transform 0.3s ease;
      }

      &:hover img {
        transform: scale(1.05);
      }
    }

    .mbti-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.75);
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      opacity: 0;
      transition: all 0.3s ease;
      color: white;
      padding: 20px;

      h3 {
        font-size: 22px;
        margin-bottom: 10px;
        font-weight: 600;
      }

      p {
        font-size: 14px;
        margin-bottom: 20px;
        color: rgba(255, 255, 255, 0.8);
      }

      .mbti-button {
        background: #1a5fb4;
        border: none;
        padding: 10px 24px;
        font-size: 14px;

        &:hover {
          background: #1c71d8;
        }
      }
    }
  }
}

.recommend-section {
  .recommend-header-bar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px 16px;
    background: #fff;
    border: 1px solid #e4e7ed;
    border-bottom: none;

    .header-left {
      display: flex;
      align-items: center;

      i {
        color: #1a5fb4;
        font-size: 18px;
        margin-right: 8px;
      }

      .header-title {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }

      .header-subtitle {
        margin-left: 12px;
        font-size: 13px;
        color: #909399;
      }
    }
  }

  .recommend-body {
    padding: 16px;
    background: #fff;
    border: 1px solid #e4e7ed;
  }

  .recommend-item-card {
    position: relative;
    border: 1px solid #e4e7ed;
    margin-bottom: 12px;

    ::v-deep .el-card__body {
      padding: 12px;
    }

    .card-rank {
      position: absolute;
      top: 0;
      left: 0;
      width: 24px;
      height: 24px;
      background: #1a5fb4;
      color: #fff;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 12px;
      font-weight: 600;
    }

    .card-body {
      padding-left: 16px;

      .card-university {
        font-size: 12px;
        color: #606266;
        margin-bottom: 4px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }

      .card-major {
        font-size: 14px;
        color: #303133;
        font-weight: 600;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }

    .card-footer {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-top: 10px;
      padding-top: 8px;
      border-top: 1px solid #ebeef5;

      .score-label {
        font-size: 11px;
        color: #909399;
      }

      .score-value {
        font-size: 14px;
        font-weight: 700;
        color: #c01c28;
      }
    }
  }

  .no-recommend {
    text-align: center;
    padding: 30px 20px;
    color: #909399;

    i {
      font-size: 36px;
      margin-bottom: 12px;
      display: block;
    }

    p {
      margin: 0;
      font-size: 14px;
    }
  }
}

.feature-card {
  border: 1px solid #e4e7ed;

  .section-header {
    display: flex;
    align-items: center;
    font-size: 16px;
    font-weight: 600;
    color: #303133;

    i {
      margin-right: 8px;
      color: #1a5fb4;
      font-size: 18px;
    }
  }

  .feature-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    padding: 10px;

    .feature-item {
      display: flex;
      align-items: center;
      padding: 20px;
      background: #f5f7fa;
      border: 1px solid #e4e7ed;
      cursor: pointer;
      transition: all 0.2s ease;

      &:hover {
        background: #e8f4ff;
        border-color: #1a5fb4;

        .feature-icon {
          background: #1a5fb4;

          i {
            color: #fff;
          }
        }
      }

      .feature-icon {
        width: 48px;
        height: 48px;
        background: #fff;
        border: 1px solid #e4e7ed;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 16px;
        transition: all 0.2s ease;

        i {
          font-size: 24px;
          color: #1a5fb4;
          transition: all 0.2s ease;
        }
      }

      .feature-text {
        h4 {
          margin: 0 0 4px 0;
          font-size: 15px;
          color: #303133;
          font-weight: 600;
        }

        p {
          margin: 0;
          font-size: 12px;
          color: #909399;
        }
      }
    }
  }
}

/* ========== 公共样式 ========== */
::v-deep .el-card {
  border-radius: 0;

  .el-card__header {
    border-bottom: 1px solid #e4e7ed;
    padding: 16px 20px;
  }
}

::v-deep .el-button {
  border-radius: 0;
}

::v-deep .el-tag {
  border-radius: 0;
}

::v-deep .el-timeline-item__node {
  border-radius: 0;
}

.notice-content::v-deep img {
  max-width: 100%;
  height: auto;
  display: block;
  margin: 0 auto;
}
</style>
