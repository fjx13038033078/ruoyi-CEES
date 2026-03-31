<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :inline="true" :model="searchParams" class="demo-form-inline">
      <el-form-item label="高校关键字">
        <el-input v-model="searchParams.keyword" placeholder="请输入高校名称关键字"></el-input>
      </el-form-item>
      <el-form-item label="高校类型">
        <el-select v-model="searchParams.type" placeholder="请选择" clearable>
          <el-option v-for="item in schoolTypeOptions" :key="item.value" :label="item.label"
                     :value="item.value"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="985">
        <el-select v-model="searchParams.is985" placeholder="是否985" clearable>
          <el-option label="是" :value="1"></el-option>
          <el-option label="否" :value="0"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="211">
        <el-select v-model="searchParams.is211" placeholder="是否211" clearable>
          <el-option label="是" :value="1"></el-option>
          <el-option label="否" :value="0"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="双一流">
        <el-select v-model="searchParams.isDoubleFirst" placeholder="是否双一流" clearable>
          <el-option label="是" :value="1"></el-option>
          <el-option label="否" :value="0"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="层次">
        <el-select v-model="searchParams.level" placeholder="请选择" clearable>
          <el-option label="本科" :value="0"></el-option>
          <el-option label="专科" :value="1"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 添加高校按钮 -->
    <el-row :gutter="20" class="mb-20">
      <el-col>
        <el-button type="primary" @click="handleAddUniversity" style="margin-bottom: 20px;"
                   v-hasPermi="['university:university:add']">新增高校
        </el-button>
      </el-col>
    </el-row>

    <!-- 高校列表 -->
    <el-table :data="universityList" v-loading="loading" border style="width: 100%">
      <el-table-column label="高校ID" prop="universityId" align="center" width="80"></el-table-column>
      <el-table-column label="高校名称" prop="universityName" align="center" min-width="140"></el-table-column>
      <el-table-column label="所在省份" prop="location" align="center" width="100"></el-table-column>
      <el-table-column label="所在城市" prop="city" align="center" width="100"></el-table-column>
      <el-table-column label="高校类型" align="center" width="80">
        <template #default="scope">
          <span>{{ scope.row.type === 0 ? '公办' : scope.row.type === 1 ? '民办' : '--' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="985" align="center" width="60">
        <template #default="scope">
          <span>{{ scope.row.is985 === 1 ? '是' : '否' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="211" align="center" width="60">
        <template #default="scope">
          <span>{{ scope.row.is211 === 1 ? '是' : '否' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="双一流" align="center" width="70">
        <template #default="scope">
          <span>{{ scope.row.isDoubleFirst === 1 ? '是' : '否' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="层次" align="center" width="70">
        <template #default="scope">
          <span>{{ scope.row.level === 0 ? '本科' : scope.row.level === 1 ? '专科' : '--' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="官网" align="center" min-width="120">
        <template #default="scope">
          <a v-if="scope.row.webUrl" :href="scope.row.webUrl" target="_blank" style="color:#409EFF">{{ scope.row.webUrl }}</a>
          <span v-else>--</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="280px">
        <template #default="scope">
          <el-button type="info" size="mini" @click="handleView(scope.row)">查看</el-button>
          <el-button type="primary" size="mini" @click="handleEdit(scope.row)"
                     v-hasPermi="['university:university:edit']">编辑
          </el-button>
          <el-button type="danger" size="mini" @click="handleDelete(scope.row)"
                     v-hasPermi="['university:university:delete']">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="fetchUniversities"
    />

    <!-- 添加/编辑高校对话框 -->
    <el-dialog :visible.sync="dialogVisible" :title="dialogTitle" width="550px">
      <el-form :model="universityForm" label-width="100px">
        <el-form-item label="高校名称">
          <el-input v-model="universityForm.universityName" placeholder="请输入高校名称"
                    style="width: 260px"></el-input>
        </el-form-item>
        <el-form-item label="所在省份">
          <el-select v-model="universityForm.location" placeholder="请选择省份" style="width:260px">
            <el-option v-for="item in provinceOptions" :key="item.value" :label="item.label"
                       :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="所在城市">
          <el-input v-model="universityForm.city" placeholder="请输入城市" style="width: 260px"></el-input>
        </el-form-item>
        <el-form-item label="高校类型">
          <el-select v-model="universityForm.type" placeholder="请选择类型" style="width:260px">
            <el-option v-for="item in schoolTypeOptions" :key="item.value" :label="item.label"
                       :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否985">
          <el-switch v-model="universityForm.is985" :active-value="1" :inactive-value="0" active-text="是"
                     inactive-text="否"></el-switch>
        </el-form-item>
        <el-form-item label="是否211">
          <el-switch v-model="universityForm.is211" :active-value="1" :inactive-value="0" active-text="是"
                     inactive-text="否"></el-switch>
        </el-form-item>
        <el-form-item label="是否双一流">
          <el-switch v-model="universityForm.isDoubleFirst" :active-value="1" :inactive-value="0" active-text="是"
                     inactive-text="否"></el-switch>
        </el-form-item>
        <el-form-item label="层次">
          <el-select v-model="universityForm.level" placeholder="请选择层次" style="width:260px">
            <el-option label="本科" :value="0"></el-option>
            <el-option label="专科" :value="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="院校官网">
          <el-input v-model="universityForm.webUrl" placeholder="请输入官网地址" style="width: 260px"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">{{ dialogButtonText }}</el-button>
      </div>
    </el-dialog>

    <!-- 查看高校详情对话框 -->
    <el-dialog :visible.sync="viewDialogVisible" title="高校详情" width="70%">
      <!-- 新增专业按钮 -->
      <el-button type="primary" @click="handleAddMajor(currentUniversityId)" style="margin-bottom: 20px;"
                 v-hasPermi="['university:major:add']">新增专业
      </el-button>
      <!-- 专业列表 -->
      <el-table :data="majorList" v-loading="loadingMajors" border style="width: 100%">
        <el-table-column label="专业ID" prop="majorId" align="center" width="80"></el-table-column>
        <el-table-column label="专业编号" prop="majorNum" align="center" width="90"></el-table-column>
        <el-table-column label="专业名称" prop="majorName" align="center" min-width="140"></el-table-column>
        <el-table-column label="报名科目" align="center" width="90">
          <template #default="scope">
            <span>{{ scope.row.subject === 1 ? '历史类' : '物理类' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="2023投档线" prop="minScore2023" align="center" width="100"></el-table-column>
        <el-table-column label="2024投档线" prop="minScore2024" align="center" width="100"></el-table-column>
        <el-table-column label="2025投档线" prop="minScore2025" align="center" width="100"></el-table-column>
        <el-table-column label="操作" align="center" width="280px">
          <template #default="scope">
            <el-button type="warning" size="mini" @click="handleCollectMajor(scope.row.majorId)" v-hasPermi="['university:major:like']">收藏</el-button>
            <el-button type="primary" size="mini" @click="handleViewMajor(scope.row)">查看</el-button>
            <el-button type="info" size="mini" @click="handleEditMajor(scope.row)" v-hasPermi="['university:major:edit']">修改</el-button>
            <el-button type="danger" size="mini" @click="handleDeleteMajor(scope.row)" v-hasPermi="['university:major:delete']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <span slot="footer" class="dialog-footer">
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>
    <!-- 新增/编辑专业对话框 -->
    <el-dialog :visible.sync="majorDialogVisible" :title="dialogTitle" width="450px">
      <el-form :model="majorForm" label-width="110px">
        <el-form-item label="专业编号">
          <el-input-number v-model="majorForm.majorNum" :min="0" style="width: 200px;"></el-input-number>
        </el-form-item>
        <el-form-item label="专业名称">
          <el-input v-model="majorForm.majorName" placeholder="请输入专业名称" style="width: 200px"></el-input>
        </el-form-item>
        <el-form-item label="报名科目">
          <el-select v-model="majorForm.subject" placeholder="请选择报名科目" style="width:200px">
            <el-option label="历史类" :value="1"></el-option>
            <el-option label="物理类" :value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="2023年投档线">
          <el-input-number v-model="majorForm.minScore2023" :min="0" style="width: 200px;"></el-input-number>
        </el-form-item>
        <el-form-item label="2024年投档线">
          <el-input-number v-model="majorForm.minScore2024" :min="0" style="width: 200px;"></el-input-number>
        </el-form-item>
        <el-form-item label="2025年投档线">
          <el-input-number v-model="majorForm.minScore2025" :min="0" style="width: 200px;"></el-input-number>
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="majorForm.description" placeholder="请输入备注"
                    style="width: 200px"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="majorDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitMajor">{{ dialogButtonText }}</el-button>
      </div>
    </el-dialog>

    <!-- 查看专业详情对话框 -->
    <el-dialog :visible.sync="majorDialogViewVisible" title="专业详情" width="450px">
      <el-form :model="majorForm" label-width="110px">
        <el-form-item label="专业编号">
          <el-input v-model="majorForm.majorNum" readonly style="width: 200px;"></el-input>
        </el-form-item>
        <el-form-item label="专业名称">
          <el-input v-model="majorForm.majorName" readonly style="width: 200px"></el-input>
        </el-form-item>
        <el-form-item label="报名科目">
          <el-select v-model="majorForm.subject" readonly style="width:200px">
            <el-option label="历史类" :value="1"></el-option>
            <el-option label="物理类" :value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="2023年投档线">
          <el-input v-model="majorForm.minScore2023" readonly style="width: 200px;"></el-input>
        </el-form-item>
        <el-form-item label="2024年投档线">
          <el-input v-model="majorForm.minScore2024" readonly style="width: 200px;"></el-input>
        </el-form-item>
        <el-form-item label="2025年投档线">
          <el-input v-model="majorForm.minScore2025" readonly style="width: 200px;"></el-input>
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="majorForm.description" readonly style="width: 200px"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="majorDialogViewVisible = false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  addUniversity,
  deleteUniversity,
  getUniversity,
  listUniversities,
  updateUniversity
} from "@/api/university/university";
import schoolTypeOptions from "@/api/university/schoolTypeOptions";
import provinceOptions from "@/api/university/provinceOptions";
import {addMajor, deleteMajor, listMajorsByUniversityId, updateMajor} from "@/api/university/major";
import {addStoreup} from "@/api/university/storeup";

export default {
  data() {
    return {
      schoolTypeOptions,
      provinceOptions,
      loading: false,
      universityList: [],
      majorList: [],
      loadingMajors: false,
      majorDialogVisible: false,
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
      },
      searchParams: {
        keyword: '',
        is985: null,
        is211: null,
        isDoubleFirst: null,
        type: null,
        level: null
      },
      dialogVisible: false,
      viewDialogVisible: false,
      majorDialogViewVisible: false,
      dialogTitle: "",
      dialogButtonText: "",
      universityForm: {
        universityId: null,
        universityName: "",
        location: "",
        city: "",
        type: null,
        is985: 0,
        is211: 0,
        isDoubleFirst: 0,
        level: null,
        webUrl: "",
      },
      majorForm: {
        universityId: null,
        majorNum: null,
        majorName: "",
        subject: 1,
        description: "",
        minScore2023: null,
        minScore2024: null,
        minScore2025: null,
      },
      viewUniversityForm: {},
      currentUniversityId: null,
      storeupForm: {
        majorId: null,
        actionType: 2,
      }
    };
  },
  created() {
    this.fetchUniversities();
  },
  methods: {
    provinceOptions() {
      return provinceOptions
    },
    schoolTypeOptions() {
      return schoolTypeOptions
    },
    handleSearch() {
      this.queryParams.pageNum = 1;
      this.fetchUniversities();
    },
    handleReset() {
      this.searchParams = {
        keyword: '',
        is985: null,
        is211: null,
        isDoubleFirst: null,
        type: null,
        level: null
      };
      this.handleSearch();
    },
    fetchUniversities() {
      this.loading = true;
      const params = {
        ...this.queryParams,
        ...this.searchParams
      };
      listUniversities(params).then((response) => {
        this.universityList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    handleView(row) {
      this.viewUniversityForm = {...row};
      this.fetchMajors(row.universityId);
      this.viewDialogVisible = true;
      this.currentUniversityId = row.universityId;
    },
    handleAddUniversity() {
      this.dialogTitle = "新增高校";
      this.dialogButtonText = "添加";
      this.clearForm();
      this.dialogVisible = true;
    },
    handleEdit(university) {
      this.dialogTitle = "编辑高校";
      this.dialogButtonText = "保存";
      getUniversity(university.universityId).then((response) => {
        this.universityForm = response.data;
        this.dialogVisible = true;
      });
    },
    handleSubmit() {
      const action = this.universityForm.universityId ? updateUniversity : addUniversity;
      action(this.universityForm).then(() => {
        this.$message.success(this.universityForm.universityId ? "修改成功" : "添加成功");
        this.fetchUniversities();
        this.dialogVisible = false;
      });
    },
    handleDelete(university) {
      this.$confirm("确认删除该高校信息吗？", "提示", {
        type: "warning",
      }).then(() => {
        deleteUniversity(university.universityId).then(() => {
          this.$message.success("删除成功");
          this.fetchUniversities();
        });
      });
    },
    clearForm() {
      this.universityForm = {
        universityId: null,
        universityName: "",
        location: "",
        city: "",
        type: null,
        is985: 0,
        is211: 0,
        isDoubleFirst: 0,
        level: null,
        webUrl: "",
      };
    },
    fetchMajors(universityId) {
      this.loadingMajors = true;
      listMajorsByUniversityId(universityId).then((response) => {
        this.majorList = response.data || [];
        this.loadingMajors = false;
      });
    },
    handleAddMajor(universityId) {
      this.dialogTitle = "新增专业";
      this.dialogButtonText = "添加";
      this.majorForm = {
        universityId: universityId,
        majorNum: null,
        majorName: "",
        subject: 1,
        description: "",
        minScore2023: null,
        minScore2024: null,
        minScore2025: null,
      };
      this.majorDialogVisible = true;
    },
    handleViewMajor(major) {
      addStoreup({
        majorId: major.majorId,
        actionType: 2,
      })
      this.majorForm = {...major};
      this.majorDialogViewVisible = true;
    },
    handleEditMajor(major) {
      this.dialogTitle = "编辑专业";
      this.dialogButtonText = "保存";
      this.majorForm = {...major};
      this.majorDialogVisible = true;
    },
    handleSubmitMajor() {
      const action = this.majorForm.majorId ? updateMajor : addMajor;
      action(this.majorForm).then(() => {
        this.$message.success(this.majorForm.majorId ? "修改成功" : "添加成功");
        this.fetchMajors(this.majorForm.universityId);
        this.majorDialogVisible = false;
      });
    },
    handleDeleteMajor(major) {
      this.$confirm("确认删除该专业信息吗？", "提示", {
        type: "warning",
      }).then(() => {
        deleteMajor(major.majorId).then(() => {
          this.$message.success("删除成功");
          this.fetchMajors(this.viewUniversityForm.universityId);
        });
      });
    },
    handleCollectMajor(majorId) {
      addStoreup({
        majorId: majorId,
        actionType: 1,
      }).then(() => {
        this.$message.success("收藏成功");
      });
    }
  },
};
</script>

<style scoped>
.demo-form-inline {
  margin-bottom: 20px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}
</style>
