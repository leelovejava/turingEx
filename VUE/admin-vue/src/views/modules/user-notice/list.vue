<template>
  <div class="mod-user-notice">
    <avue-crud
      ref="crud"
      :page.sync="page"
      :data="dataList"
      :option="tableOption"
      @search-change="searchChange"
      @on-load="getDataList"
    >
      <template slot="menuLeft">
        <el-button
          type="primary"
          icon="el-icon-plus"
          size="small"
          v-if="isAuth('user:notice:save')"
          @click="addOrUpdateHandle()"
        >新增通知</el-button>
      </template>
      <template slot-scope="scope" slot="menu">
        <el-button
          type="text"
          icon="el-icon-edit"
          size="small"
          @click.stop="addOrUpdateHandle(scope.row)"
        >编辑</el-button>
        <el-button
          type="danger"
          icon="el-icon-delete"
          size="small"
          v-if="isAuth('user:notice:deletem')"
          @click.stop="deleteHandle(scope.row.id)"
        >删除</el-button>
      </template>
    </avue-crud>

    <el-dialog :title="form.uuid ? '修改通知' : '新增通知'" :visible.sync="dialogVisible" width="600px">
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="用户UID" prop="userCode">
          <el-input v-model="form.userCode" placeholder="留空表示所有用户" />
        </el-form-item>
<el-form-item label="标题" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>

const tableOption = {
  border: true,
  selection: false,
  addBtn: false,
  editBtn: false,
  delBtn: false,
  searchShow: true,
  searchMenuSpan: 6,
  column: [
    { label: '用户UID', prop: 'userCode', search: true },
    { label: '用户名', prop: 'userName' },
{ label: '标题', prop: 'title', search: true },
    { label: '内容', prop: 'content', overHidden: true },
    { label: '状态', prop: 'status', formatter: (row) => row.status === 1 ? '未读' : '已读' },
    { label: '创建时间', prop: 'createTime' }
  ]
}

export default {
  data() {
    return {
      dataList: [],
      tableOption,
      searchParams: {},
      page: { total: 0, currentPage: 1, pageSize: 10 },
      dialogVisible: false,
      form: {},
      rules: {
        title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
        content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
      }
    }
  },
  methods: {
    getDataList(page, done) {
      const params = {
        current: page ? page.currentPage : this.page.currentPage,
        size: page ? page.pageSize : this.page.pageSize,
        ...this.searchParams
      }
      this.$http({
        url: this.$http.adornUrl('/userNotice/list'),
        method: 'post',
        data: this.$http.adornData(params)
      }).then(({ data }) => {
        this.dataList = data.data.records
        this.page.total = data.data.total
        if (done) done()
      })
    },
    searchChange(params, done) {
      this.page.currentPage = 1
      this.searchParams = params
      this.getDataList(this.page, done)
    },
    addOrUpdateHandle(row) {
      this.form = row ? { ...row, loginSafeword: '' } : {}
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const url = this.form.uuid ? '/userNotice/update' : '/userNotice/add'
        const data = { ...this.form }
        this.$http({
          url: this.$http.adornUrl(url),
          method: 'post',
          data: this.$http.adornData(data)
        }).then(({ data }) => {
          if (data.code === 0) {
            this.$message({ message: '操作成功', type: 'success', duration: 1000 })
            this.dialogVisible = false
            this.getDataList(this.page)
          } else {
            this.$notify({ title: '消息', message: data.msg, type: 'warning' })
          }
        })
      })
    },
    deleteHandle(id) {
      this.$confirm('确定删除该通知?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/userNotice/delete'),
          method: 'post',
          data: this.$http.adornData({ id, loginSafeword: '' })
        }).then(({ data }) => {
          if (data.code === 0) {
            this.$message({ message: '操作成功', type: 'success', duration: 1000 })
            this.getDataList(this.page)
          } else {
            this.$notify({ title: '消息', message: data.msg, type: 'warning' })
          }
        })
      }).catch(() => {})
    }
  }
}
</script>
