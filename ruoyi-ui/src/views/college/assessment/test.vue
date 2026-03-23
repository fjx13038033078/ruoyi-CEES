<template>
  <div class="app-container assessment-test">
    <div class="test-header">
      <h2>霍兰德职业兴趣测评</h2>
      <el-progress :percentage="progressPercent" :stroke-width="10" :format="progressFormat" color="#409EFF"></el-progress>
    </div>

    <div class="question-area" v-if="currentQuestions.length > 0">
      <div v-for="(q, qi) in currentQuestions" :key="q.questionId" class="question-card">
        <div class="question-num">第 {{ (currentPage - 1) * pageSize + qi + 1 }} 题 / 共 {{ questions.length }} 题</div>
        <div class="question-text">{{ q.questionText }}</div>
        <div class="options">
          <div
            :class="['option-card', { selected: answers[q.questionId] === 'A' }]"
            @click="selectOption(q.questionId, 'A')"
          >
            <div class="option-label">A</div>
            <div class="option-text">{{ q.optionA }}</div>
          </div>
          <div
            :class="['option-card', { selected: answers[q.questionId] === 'B' }]"
            @click="selectOption(q.questionId, 'B')"
          >
            <div class="option-label">B</div>
            <div class="option-text">{{ q.optionB }}</div>
          </div>
        </div>
      </div>
    </div>

    <div class="nav-buttons">
      <el-button :disabled="currentPage <= 1" @click="prevPage" icon="el-icon-arrow-left">上一页</el-button>
      <span class="page-info">第 {{ currentPage }} / {{ totalPages }} 页</span>
      <el-button v-if="currentPage < totalPages" type="primary" @click="nextPage">
        下一页 <i class="el-icon-arrow-right"></i>
      </el-button>
      <el-button v-else type="success" @click="handleSubmit" :loading="submitting" icon="el-icon-check">
        提交答案
      </el-button>
    </div>
  </div>
</template>

<script>
import { getQuestions, submitAnswers } from "@/api/college/assessment";

export default {
  name: "AssessmentTest",
  data() {
    return {
      recordId: null,
      questions: [],
      answers: {},
      currentPage: 1,
      pageSize: 5,
      submitting: false
    };
  },
  computed: {
    totalPages() {
      return Math.ceil(this.questions.length / this.pageSize);
    },
    currentQuestions() {
      const start = (this.currentPage - 1) * this.pageSize;
      return this.questions.slice(start, start + this.pageSize);
    },
    answeredCount() {
      return Object.keys(this.answers).length;
    },
    progressPercent() {
      return this.questions.length > 0 ? Math.round((this.answeredCount / this.questions.length) * 100) : 0;
    }
  },
  created() {
    this.recordId = this.$route.query.recordId;
    this.loadQuestions();
  },
  methods: {
    loadQuestions() {
      getQuestions().then(res => {
        this.questions = res.data || [];
      });
    },
    selectOption(questionId, option) {
      this.$set(this.answers, questionId, option);
    },
    prevPage() {
      if (this.currentPage > 1) this.currentPage--;
    },
    nextPage() {
      const unanswered = this.currentQuestions.filter(q => !this.answers[q.questionId]);
      if (unanswered.length > 0) {
        this.$message.warning('请回答完本页所有题目后再翻页');
        return;
      }
      if (this.currentPage < this.totalPages) this.currentPage++;
    },
    progressFormat() {
      return `${this.answeredCount}/${this.questions.length}`;
    },
    handleSubmit() {
      if (this.answeredCount < this.questions.length) {
        this.$message.warning(`还有 ${this.questions.length - this.answeredCount} 题未作答`);
        return;
      }
      this.$confirm('确定提交答案？提交后不可修改。', '提示', { type: 'info' }).then(() => {
        this.submitting = true;
        const answerList = Object.keys(this.answers).map(qid => ({
          questionId: parseInt(qid),
          selectedOption: this.answers[qid]
        }));
        submitAnswers({ recordId: parseInt(this.recordId), answers: answerList }).then(res => {
          this.$message.success('提交成功！正在生成报告...');
          const result = res.data;
          this.$router.push({ path: '/volunteer/report', query: { recordId: result.recordId } });
        }).catch(() => {
          this.submitting = false;
        });
      }).catch(() => {});
    }
  }
};
</script>

<style scoped lang="scss">
.assessment-test {
  max-width: 800px;
  margin: 0 auto;

  .test-header {
    margin-bottom: 24px;
    h2 { margin: 0 0 16px 0; text-align: center; }
  }

  .question-card {
    background: #fff;
    border: 1px solid #EBEEF5;
    border-radius: 8px;
    padding: 20px 24px;
    margin-bottom: 16px;

    .question-num { color: #909399; font-size: 13px; margin-bottom: 8px; }
    .question-text { font-size: 16px; font-weight: 600; margin-bottom: 16px; line-height: 1.6; }

    .options {
      display: flex;
      gap: 12px;

      .option-card {
        flex: 1;
        border: 2px solid #DCDFE6;
        border-radius: 8px;
        padding: 16px;
        cursor: pointer;
        transition: all 0.3s;
        text-align: center;

        &:hover { border-color: #409EFF; background: #ecf5ff; }
        &.selected {
          border-color: #409EFF;
          background: #409EFF;
          color: #fff;
          .option-label { background: rgba(255,255,255,0.3); color: #fff; }
        }

        .option-label {
          width: 32px; height: 32px; border-radius: 50%; background: #f0f0f0;
          display: inline-flex; align-items: center; justify-content: center;
          font-weight: 700; font-size: 14px; margin-bottom: 8px;
        }
        .option-text { font-size: 14px; line-height: 1.5; }
      }
    }
  }

  .nav-buttons {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 16px;
    margin-top: 24px;
    padding: 16px 0;

    .page-info { color: #909399; font-size: 14px; }
  }
}
</style>
