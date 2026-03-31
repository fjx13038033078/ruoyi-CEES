<template>
  <div class="app-container volunteer-form-page">
    <el-page-header @back="goBack" :content="formInfo.formName || '志愿填报'"></el-page-header>

    <el-row :gutter="20" style="margin-top:20px">
      <!-- 左侧：院校专业搜索 -->
      <el-col :span="10">
        <el-card shadow="never">
          <div slot="header"><b>院校 / 专业搜索</b></div>
          <el-input v-model="searchKeyword" placeholder="输入院校名称搜索" @input="handleSearch" clearable>
            <el-button slot="append" icon="el-icon-search" @click="handleSearch"></el-button>
          </el-input>
          <div class="search-results" v-loading="searchLoading">
            <div v-for="uni in universityList" :key="uni.universityId" class="uni-item">
              <div class="uni-header" @click="toggleUni(uni)">
                <span class="uni-name">{{ uni.universityName }}</span>
                <span class="uni-location">{{ uni.location }}</span>
                <el-tag v-if="uni.is985" size="mini" type="danger">985</el-tag>
                <el-tag v-if="uni.is211" size="mini">211</el-tag>
                <el-tag v-if="uni.isDoubleFirst" size="mini" type="warning">双一流</el-tag>
                <i :class="expandedUni === uni.universityId ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
              </div>
              <div v-if="expandedUni === uni.universityId" class="major-list">
                <div v-for="m in majorList" :key="m.majorId" class="major-item" @click="addToVolunteer(uni, m)">
                  <span>{{ m.majorName }}</span>
                  <span class="score-tag">{{ (m.minScore2025 || m.minScore2024) ? (m.minScore2025 || m.minScore2024) + '分' : '-' }}</span>
                  <el-button type="text" size="mini" icon="el-icon-plus">添加</el-button>
                </div>
              </div>
            </div>
            <el-empty v-if="universityList.length === 0 && !searchLoading" description="请搜索院校" :image-size="80"></el-empty>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：志愿列表 -->
      <el-col :span="14">
        <el-card shadow="never">
          <div slot="header" class="vol-header">
            <b>志愿列表（{{ items.length }}个）</b>
            <div>
              <el-button type="success" size="small" @click="handleCheckAll" :loading="checking">全面校验</el-button>
            </div>
          </div>

          <div class="volunteer-list">
            <draggable v-model="items" handle=".drag-handle" @end="onDragEnd">
              <transition-group>
                <div v-for="(item, index) in items" :key="item.itemId || ('new-' + index)" class="vol-item" :class="checkStatusClass(item.checkStatus)">
                  <div class="drag-handle"><i class="el-icon-rank"></i></div>
                  <div class="vol-index">{{ index + 1 }}</div>
                  <div class="vol-content">
                    <div class="vol-uni-name">{{ item.universityName || '未选择院校' }}</div>
                    <div class="vol-major-info">
                      <el-tag v-if="item.majorName1" size="small">{{ item.majorName1 }}</el-tag>
                    </div>
                    <div v-if="item.checkMessages" class="check-msg">
                      <div v-for="(msg, mi) in parseMessages(item.checkMessages)" :key="mi" class="msg-item">
                        <i class="el-icon-warning-outline"></i> {{ msg }}
                      </div>
                    </div>
                  </div>
                  <div class="vol-actions">
                    <el-switch v-model="item.isAdjustment" :active-value="1" :inactive-value="0" active-text="调剂" inactive-text="" size="small"></el-switch>
                    <el-button type="text" class="danger-btn" icon="el-icon-delete" @click="handleDeleteItem(item, index)"></el-button>
                  </div>
                </div>
              </transition-group>
            </draggable>
            <el-empty v-if="items.length === 0" description="从左侧搜索并添加志愿"></el-empty>
          </div>
        </el-card>

        <!-- 校验结果面板 -->
        <el-card v-if="checkResults.length > 0" shadow="never" style="margin-top:15px">
          <div slot="header"><b>校验结果</b></div>
          <div v-for="r in checkResults" :key="r.itemId" class="check-result-item" :class="checkStatusClass(r.checkStatus)">
            <div class="check-head">
              <span>第{{ r.sortOrder }}志愿 - {{ r.universityName }} {{ r.majorName }}</span>
              <el-tag :type="r.checkStatus === 1 ? 'success' : (r.checkStatus === 3 ? 'danger' : 'warning')" size="small">
                {{ r.checkStatus === 1 ? '通过' : (r.checkStatus === 3 ? '不可填报' : '有预警') }}
              </el-tag>
            </div>
            <div v-for="(msg, i) in r.messages" :key="i" class="check-msg-line">
              <i class="el-icon-warning-outline"></i> {{ msg }}
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 选择专业对话框 -->
    <el-dialog title="选择专业志愿" :visible.sync="majorDialogVisible" width="600px">
      <p>院校：<b>{{ selectedUni ? selectedUni.universityName : '' }}</b></p>
      <el-checkbox-group v-model="selectedMajorIds" :max="6">
        <div v-for="m in dialogMajorList" :key="m.majorId" class="major-check-item">
          <el-checkbox :label="m.majorId">{{ m.majorName }} ({{ m.minScore2025 || m.minScore2024 || '-' }}分)</el-checkbox>
        </div>
      </el-checkbox-group>
      <div style="margin-top:15px">
        <el-checkbox v-model="isAdjustment" :true-label="1" :false-label="0">服从专业调剂</el-checkbox>
      </div>
      <span slot="footer">
        <el-button @click="majorDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAddItem">确定添加</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getFormDetail, getItems, addItem, deleteItem, updateItemSort, checkForm } from "@/api/college/volunteer";
import draggable from 'vuedraggable';

export default {
  name: "VolunteerForm",
  components: { draggable },
  data() {
    return {
      formId: null,
      formInfo: {},
      items: [],
      searchKeyword: '',
      universityList: [],
      majorList: [],
      expandedUni: null,
      searchLoading: false,
      checking: false,
      checkResults: [],
      majorDialogVisible: false,
      selectedUni: null,
      dialogMajorList: [],
      selectedMajorIds: [],
      isAdjustment: 1
    };
  },
  created() {
    this.formId = this.$route.query.formId;
    if (this.formId) {
      this.loadForm();
    }
  },
  methods: {
    goBack() {
      this.$router.push('/volunteer/form');
    },
    loadForm() {
      getFormDetail(this.formId).then(res => {
        this.formInfo = res.data || {};
        this.items = this.formInfo.items || [];
      });
    },
    handleSearch() {
      if (!this.searchKeyword) {
        this.universityList = [];
        return;
      }
      this.searchLoading = true;
      this.$http = this.$http || this.$axios;
      const request = require('@/utils/request').default;
      request({
        url: '/university/university/listAll',
        method: 'get',
        params: { keyword: this.searchKeyword, pageNum: 1, pageSize: 20 }
      }).then(res => {
        this.universityList = res.rows || [];
        this.searchLoading = false;
      }).catch(() => { this.searchLoading = false; });
    },
    toggleUni(uni) {
      if (this.expandedUni === uni.universityId) {
        this.expandedUni = null;
        return;
      }
      this.expandedUni = uni.universityId;
      const request = require('@/utils/request').default;
      request({
        url: '/university/major/listByUniversityId',
        method: 'get',
        params: { universityId: uni.universityId }
      }).then(res => {
        this.majorList = res.rows || res.data || [];
      });
    },
    addToVolunteer(uni, major) {
      this.selectedUni = uni;
      this.selectedMajorIds = [major.majorId];
      this.isAdjustment = 1;
      const request = require('@/utils/request').default;
      request({
        url: '/university/major/listByUniversityId',
        method: 'get',
        params: { universityId: uni.universityId }
      }).then(res => {
        this.dialogMajorList = res.rows || res.data || [];
        this.majorDialogVisible = true;
      });
    },
    confirmAddItem() {
      const ids = this.selectedMajorIds;
      const itemData = {
        formId: parseInt(this.formId),
        sortOrder: this.items.length + 1,
        universityId: this.selectedUni.universityId,
        majorId1: ids[0] || null,
        majorId2: ids[1] || null,
        majorId3: ids[2] || null,
        majorId4: ids[3] || null,
        majorId5: ids[4] || null,
        majorId6: ids[5] || null,
        isAdjustment: this.isAdjustment
      };
      addItem(itemData).then(res => {
        this.$message.success('添加成功');
        this.majorDialogVisible = false;
        this.loadForm();
      });
    },
    handleDeleteItem(item, index) {
      if (item.itemId) {
        deleteItem(item.itemId).then(() => {
          this.$message.success('已删除');
          this.loadForm();
        });
      } else {
        this.items.splice(index, 1);
      }
    },
    onDragEnd() {
      const sortData = this.items.map((item, index) => ({
        itemId: item.itemId,
        sortOrder: index + 1
      }));
      updateItemSort(sortData);
    },
    handleCheckAll() {
      this.checking = true;
      checkForm(this.formId).then(res => {
        this.checkResults = res.data || [];
        this.checking = false;
        this.loadForm();
        const warnings = this.checkResults.filter(r => r.checkStatus !== 1);
        if (warnings.length === 0) {
          this.$message.success('全部校验通过');
        } else {
          this.$message.warning(`共 ${warnings.length} 项预警，请查看下方校验结果`);
        }
      }).catch(() => { this.checking = false; });
    },
    parseMessages(json) {
      try { return JSON.parse(json); } catch (e) { return []; }
    },
    checkStatusClass(status) {
      return { 1: 'status-pass', 2: 'status-warning', 3: 'status-error' }[status] || '';
    }
  }
};
</script>

<style scoped lang="scss">
.volunteer-form-page {
  .search-results {
    max-height: 600px;
    overflow-y: auto;
    margin-top: 12px;
  }
  .uni-item {
    border: 1px solid #EBEEF5;
    border-radius: 4px;
    margin-bottom: 8px;
    .uni-header {
      padding: 10px 12px;
      cursor: pointer;
      display: flex;
      align-items: center;
      gap: 8px;
      &:hover { background: #f5f7fa; }
      .uni-name { font-weight: 600; flex: 1; }
      .uni-location { color: #909399; font-size: 13px; }
    }
    .major-list {
      border-top: 1px solid #EBEEF5;
      .major-item {
        padding: 8px 12px 8px 24px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        cursor: pointer;
        &:hover { background: #ecf5ff; }
        .score-tag { color: #E6A23C; font-size: 13px; }
      }
    }
  }
  .vol-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .volunteer-list {
    min-height: 200px;
  }
  .vol-item {
    display: flex;
    align-items: flex-start;
    padding: 12px;
    border: 1px solid #EBEEF5;
    border-radius: 6px;
    margin-bottom: 8px;
    transition: all 0.3s;
    &:hover { box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
    &.status-pass { border-left: 3px solid #67C23A; }
    &.status-warning { border-left: 3px solid #E6A23C; }
    &.status-error { border-left: 3px solid #F56C6C; }
    .drag-handle { cursor: grab; padding: 4px 8px; color: #C0C4CC; font-size: 18px; }
    .vol-index {
      width: 28px; height: 28px; border-radius: 50%; background: #409EFF; color: #fff;
      display: flex; align-items: center; justify-content: center; font-weight: 600;
      margin-right: 12px; flex-shrink: 0;
    }
    .vol-content { flex: 1; }
    .vol-uni-name { font-weight: 600; font-size: 15px; margin-bottom: 4px; }
    .vol-major-info { margin-top: 4px; }
    .check-msg {
      margin-top: 6px;
      .msg-item { color: #E6A23C; font-size: 13px; i { margin-right: 4px; } }
    }
    .vol-actions {
      display: flex; flex-direction: column; align-items: flex-end; gap: 8px;
      .danger-btn { color: #F56C6C; }
    }
  }
  .check-result-item {
    padding: 10px 12px;
    border: 1px solid #EBEEF5;
    border-radius: 4px;
    margin-bottom: 8px;
    &.status-pass { border-left: 3px solid #67C23A; }
    &.status-warning { border-left: 3px solid #E6A23C; }
    &.status-error { border-left: 3px solid #F56C6C; }
    .check-head {
      display: flex; justify-content: space-between; align-items: center;
      font-weight: 600; margin-bottom: 6px;
    }
    .check-msg-line { color: #E6A23C; font-size: 13px; margin-top: 4px; }
  }
  .major-check-item { padding: 6px 0; }
}
</style>
