<template>
  <div class="mod-ai-quant-questionnaire">
    <avue-crud
      ref="crud"
      :page.sync="page"
      :data="dataList"
      :option="tableOption"
      :table-loading="dataListLoading"
      @on-load="getDataList"
      @search-change="searchChange"
    >
      <template slot-scope="scope" slot="status">
        <el-tag v-if="scope.row.status === 'PASS'" type="success">通过</el-tag>
        <el-tag v-else-if="scope.row.status === 'NOPASS'" type="danger">拒绝</el-tag>
        <el-tag v-else type="warning">待审核</el-tag>
      </template>
      <template slot-scope="scope" slot="menu">
        <el-button type="primary" size="small" @click="viewDetail(scope.row)">详情</el-button>
        <el-button type="success" size="small" @click="openAudit(scope.row, 'PASS')">通过</el-button>
        <el-button type="danger" size="small" @click="openAudit(scope.row, 'NOPASS')">拒绝</el-button>
      </template>
    </avue-crud>

    <el-dialog title="问卷详情" :visible.sync="detailVisible" width="700px">
      <div v-if="detail">
        <p><b>用户ID：</b>{{ detail.userId || '-' }}</p>
        <p><b>用户UID：</b>{{ detail.userCode || '-' }}</p>
        <p><b>邮箱：</b>{{ detail.userMail || '-' }}</p>
        <p><b>{{ detail.question1Question }}：</b>{{ detail.question1Answer }}</p>
        <p><b>{{ detail.question2Question }}：</b>{{ detail.question2Answer }}</p>
        <p><b>{{ detail.question3Question }}：</b>{{ detail.question3Answer }}</p>
        <p><b>{{ detail.question4Question }}：</b>{{ detail.question4Answer }}</p>
        <p><b>{{ detail.question5Question }}：</b>{{ detail.question5Answer }}</p>
        <p><b>审核备注：</b>{{ detail.auditRemark || '-' }}</p>
      </div>
    </el-dialog>

    <el-dialog :title="auditForm.status === 'PASS' ? '审核通过' : '审核拒绝'" :visible.sync="auditVisible" width="500px">
      <el-input type="textarea" :rows="4" v-model="auditForm.auditRemark" placeholder="请输入审核备注"></el-input>
      <span slot="footer" class="dialog-footer">
        <el-button @click="auditVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAudit">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      dataList: [],
      dataListLoading: false,
      detailVisible: false,
      auditVisible: false,
      detail: null,
      searchParams: {},
      auditForm: {
        uuid: "",
        status: "PASS",
        auditRemark: "",
      },
      page: {
        total: 0,
        currentPage: 1,
        pageSize: 10,
      },
      tableOption: {
        border: true,
        index: true,
        stripe: true,
        menuAlign: "center",
        align: "center",
        searchMenuSpan: 6,
        addBtn: false,
        editBtn: false,
        delBtn: false,
        viewBtn: false,
        columnBtn: false,
        refreshBtn: true,
        column: [
          { label: "用户ID", prop: "userId", search: true },
          { label: "用户UID", prop: "userCode", search: true },
          { label: "邮箱", prop: "userMail" },
          {
            label: "状态",
            prop: "status",
            slot: true,
            type: "select",
            search: true,
            dicData: [
              { label: "待审核", value: "N" },
              { label: "通过", value: "PASS" },
              { label: "拒绝", value: "NOPASS" },
            ],
          },
          { label: "提交时间", prop: "createTime" },
        ],
      },
    };
  },
  methods: {
    getDataList(page, params, done) {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/normal/adminQuantQuestionnaireAction!/list.action"),
        method: "get",
        params: this.$http.adornParams({
          current: this.page.currentPage,
          size: this.page.pageSize,
          userCode: this.searchParams.userCode || this.searchParams.userId,
          status: this.searchParams.status,
        }),
      }).then(({ data }) => {
        if (data.code === 0 && data.data) {
          this.dataList = data.data.records || [];
          this.page.total = data.data.total || 0;
        }
        this.dataListLoading = false;
        done && done();
      }).catch(() => {
        this.dataListLoading = false;
        done && done();
      });
    },
    searchChange(params, done) {
      this.searchParams = params || {};
      this.page.currentPage = 1;
      this.getDataList(this.page, params, done);
    },
    viewDetail(row) {
      this.$http({
        url: this.$http.adornUrl("/normal/adminQuantQuestionnaireAction!/detail.action"),
        method: "get",
        params: this.$http.adornParams({ uuid: row.uuid }),
      }).then(({ data }) => {
        if (data.code === 0) {
          this.detail = data.data;
          this.detailVisible = true;
        }
      });
    },
    openAudit(row, status) {
      this.auditForm.uuid = row.uuid;
      this.auditForm.status = status;
      this.auditForm.auditRemark = row.auditRemark || "";
      this.auditVisible = true;
    },
    submitAudit() {
      this.$http({
        url: this.$http.adornUrl("/normal/adminQuantQuestionnaireAction!/updateStatus.action"),
        method: "post",
        data: this.$http.adornData(this.auditForm),
      }).then(({ data }) => {
        if (data.code === 0) {
          this.$message.success("操作成功");
          this.auditVisible = false;
          this.getDataList();
        } else {
          this.$message.error(data.msg || "操作失败");
        }
      });
    },
  },
};
</script>
