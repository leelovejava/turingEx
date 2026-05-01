<template>
  <div class="mod-worker-order">
    <el-card>
      <div class="toolbar">
        <el-input v-model="query.workOrderSn" placeholder="工单号" clearable class="w180" />
        <el-select v-model="query.status" clearable placeholder="状态" class="w140">
          <el-option label="处理中" :value="1" />
          <el-option label="已完成" :value="2" />
        </el-select>
        <el-input v-model.number="query.memberId" placeholder="用户ID" clearable class="w140" />
        <el-button type="primary" @click="loadData(1)">查询</el-button>
      </div>

      <el-table :data="list" border v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="workOrderSn" label="工单号" min-width="180" />
        <el-table-column prop="title" label="标题" min-width="180" />
        <el-table-column prop="account" label="账号" min-width="140" />
        <el-table-column prop="memberId" label="用户ID" width="110" />
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.workOrderStatus === '2' ? 'success' : 'warning'">
              {{ scope.row.workOrderStatus === '2' ? '已完成' : '处理中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="260" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" @click="openDetail(scope.row)">详情</el-button>
            <el-button size="mini" type="primary" :disabled="scope.row.workOrderStatus === '2'" @click="openReply(scope.row)">回复</el-button>
            <el-button size="mini" type="danger" :disabled="scope.row.workOrderStatus === '2'" @click="finishOrder(scope.row)">结束</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pager">
        <el-pagination
          background
          layout="total, prev, pager, next, sizes"
          :total="page.total"
          :page-size="page.size"
          :current-page="page.current"
          :page-sizes="[10,20,50]"
          @size-change="onSizeChange"
          @current-change="onCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog title="工单详情" :visible.sync="detailVisible" width="760px">
      <div v-if="detail.order" class="detail-head">
        <div><b>工单号：</b>{{ detail.order.workOrderSn }}</div>
        <div><b>标题：</b>{{ detail.order.title }}</div>
        <div><b>状态：</b>{{ detail.order.workOrderStatus === '2' ? '已完成' : '处理中' }}</div>
      </div>
      <el-timeline>
        <el-timeline-item v-for="item in detail.contents" :key="item.id" :timestamp="item.createTime" placement="top">
          <el-card>
            <div class="reply-meta">{{ item.replyRoleType === 2 ? '系统/客服' : '用户' }} {{ item.account || '' }}</div>
            <div>{{ item.content }}</div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-dialog>

    <el-dialog title="回复工单" :visible.sync="replyVisible" width="520px">
      <el-input type="textarea" :rows="5" v-model="replyForm.content" placeholder="请输入回复内容" />
      <span slot="footer" class="dialog-footer">
        <el-button @click="replyVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReply">提交</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      loading: false,
      list: [],
      query: {
        workOrderSn: '',
        status: null,
        memberId: null,
      },
      page: {
        current: 1,
        size: 10,
        total: 0,
      },
      detailVisible: false,
      detail: {
        order: null,
        contents: [],
      },
      replyVisible: false,
      replyForm: {
        orderId: null,
        content: '',
      },
    }
  },
  activated() {
    this.loadData(1)
  },
  methods: {
    loadData(pageNo) {
      if (pageNo) {
        this.page.current = pageNo
      }
      this.loading = true
      this.$http({
        url: this.$http.adornUrl('/workerOrder/list'),
        method: 'post',
        data: this.$http.adornData({
          current: this.page.current,
          size: this.page.size,
          workOrderSn: this.query.workOrderSn || null,
          status: this.query.status,
          memberId: this.query.memberId,
        }),
      }).then(({ data }) => {
        const pageData = data.data || {}
        this.list = pageData.records || []
        this.page.total = pageData.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    onSizeChange(size) {
      this.page.size = size
      this.loadData(1)
    },
    onCurrentChange(current) {
      this.page.current = current
      this.loadData()
    },
    openDetail(row) {
      this.$http({
        url: this.$http.adornUrl('/workerOrder/detail'),
        method: 'get',
        params: { orderId: row.id },
      }).then(({ data }) => {
        this.detail = data.data || { order: null, contents: [] }
        this.detailVisible = true
      })
    },
    openReply(row) {
      this.replyForm.orderId = row.id
      this.replyForm.content = ''
      this.replyVisible = true
    },
    submitReply() {
      if (!this.replyForm.content) {
        this.$message.error('请输入回复内容')
        return
      }
      this.$http({
        url: this.$http.adornUrl('/workerOrder/reply'),
        method: 'post',
        data: this.$http.adornData(this.replyForm),
      }).then(() => {
        this.$message.success('操作成功')
        this.replyVisible = false
        this.loadData()
      })
    },
    finishOrder(row) {
      this.$confirm('确认结束该工单？', '提示', { type: 'warning' }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/workerOrder/finish'),
          method: 'post',
          data: this.$http.adornData({ orderId: row.id }),
        }).then(() => {
          this.$message.success('操作成功')
          this.loadData()
        })
      }).catch(() => {})
    },
  },
}
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
}
.w180 { width: 180px; }
.w140 { width: 140px; }
.pager {
  margin-top: 12px;
  text-align: right;
}
.reply-meta {
  color: #909399;
  margin-bottom: 6px;
}
.detail-head {
  margin-bottom: 12px;
  line-height: 1.8;
}
</style>
