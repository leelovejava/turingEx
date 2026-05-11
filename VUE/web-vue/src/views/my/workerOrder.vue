<template>
  <div class="right-padding">
    <div class="right-title">工单中心</div>
    <div class="toolbar">
      <el-button type="primary" @click="openCreate">发起工单</el-button>
    </div>

    <el-table :data="list" border v-loading="loading" style="width: 100%">
      <el-table-column prop="workOrderSn" label="工单号" min-width="190" />
      <el-table-column prop="title" label="标题" min-width="220" />
      <el-table-column prop="createTime" label="创建时间" min-width="170" />
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="row.workOrderStatus === '2' ? 'success' : 'warning'">
            {{ row.workOrderStatus === "2" ? "已完成" : "处理中" }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <span class="detail-link" @click="openDetail(row)">详情</span>
        </template>
      </el-table-column>
    </el-table>

    <div class="pager">
      <el-pagination
        background
        layout="total, prev, pager, next"
        :total="total"
        :current-page="pageNo"
        :page-size="pageSize"
        @current-change="onPageChange"
      />
    </div>

    <el-dialog v-model="createVisible" title="发起工单" width="560px">
      <el-form label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="createForm.title" maxlength="100" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="createForm.content" type="textarea" :rows="5" maxlength="1000" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreate">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="工单详情" width="720px">
      <div v-if="detail.order" class="detail-meta">
        <div>工单号: {{ detail.order.workOrderSn }}</div>
        <div>标题: {{ detail.order.title }}</div>
        <div>状态: {{ detail.order.workOrderStatus === "2" ? "已完成" : "处理中" }}</div>
      </div>
      <div class="replies">
        <div class="reply-item" v-for="item in detail.contents" :key="item.id">
          <div class="reply-head">
            <span>{{ item.replyRoleType === 2 ? "客服" : "我" }}</span>
            <span>{{ item.createTime }}</span>
          </div>
          <div class="reply-content">{{ item.content }}</div>
        </div>
      </div>
      <div v-if="detail.order && detail.order.workOrderStatus !== '2'" class="reply-form">
        <el-input v-model="replyContent" type="textarea" :rows="3" placeholder="输入回复内容" />
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button v-if="detail.order && detail.order.workOrderStatus !== '2'" type="primary" @click="submitReply">发送回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ElMessage } from "element-plus";
import workerOrderApi from "@/api/workerOrder";

export default {
  name: "WorkerOrder",
  data() {
    return {
      loading: false,
      list: [],
      pageNo: 1,
      pageSize: 10,
      total: 0,
      createVisible: false,
      createForm: {
        title: "",
        content: "",
      },
      detailVisible: false,
      detail: {
        order: null,
        contents: [],
      },
      replyContent: "",
    };
  },
  mounted() {
    this.loadList();
  },
  methods: {
    loadList() {
      this.loading = true;
      workerOrderApi
        .workerOrderList({ page_no: this.pageNo, page_size: this.pageSize })
        .then((res) => {
          const page = res.data || {};
          this.list = page.records || [];
          this.total = page.total || 0;
        })
        .finally(() => {
          this.loading = false;
        });
    },
    onPageChange(page) {
      this.pageNo = page;
      this.loadList();
    },
    openCreate() {
      this.createForm = { title: "", content: "" };
      this.createVisible = true;
    },
    submitCreate() {
      if (!this.createForm.title.trim()) {
        ElMessage.error("请输入标题");
        return;
      }
      workerOrderApi
        .workerOrderCreate({
          title: this.createForm.title.trim(),
          content: (this.createForm.content || "").trim(),
        })
        .then(() => {
          ElMessage.success("提交成功");
          this.createVisible = false;
          this.pageNo = 1;
          this.loadList();
        });
    },
    openDetail(row) {
      workerOrderApi.workerOrderDetail({ order_id: row.id }).then((res) => {
        const data = res.data || res || {};
        this.detail = {
          order: data.order || row,
          contents: data.contents || data.records || [],
        };
        this.replyContent = "";
        this.detailVisible = true;
      });
    },
    submitReply() {
      if (!this.replyContent.trim() || !this.detail.order) {
        ElMessage.error("请输入回复内容");
        return;
      }
      workerOrderApi
        .workerOrderReply({
          order_id: this.detail.order.id,
          content: this.replyContent.trim(),
        })
        .then(() => {
          ElMessage.success("发送成功");
          this.replyContent = "";
          this.openDetail(this.detail.order);
          this.loadList();
        });
    },
  },
};
</script>

<style scoped>
.right-padding {
  padding: 32px 40px;
}
.toolbar {
  margin-top: 16px;
  margin-bottom: 24px;
}
.pager {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
.detail-meta {
  line-height: 30px;
  margin-bottom: 12px;
}
.replies {
  max-height: 360px;
  overflow-y: auto;
}
.reply-item {
  border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 10px;
  margin-bottom: 10px;
}
.reply-head {
  display: flex;
  justify-content: space-between;
  color: #909399;
  margin-bottom: 8px;
}
.reply-content {
  white-space: pre-wrap;
  word-break: break-word;
}
.reply-form {
  margin-top: 8px;
}
.detail-link {
  color: #2465f1;
  cursor: pointer;
}
.detail-link:hover {
  text-decoration: underline;
}
</style>

