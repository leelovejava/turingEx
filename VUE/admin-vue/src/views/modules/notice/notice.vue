<template>
  <div class="mod-transport">
    <avue-crud
      ref="crud"
      :page.sync="page"
      :data="dataList"
      :option="tableOption"
      @search-change="searchChange"
      @selection-change="selectionChange"
      @on-load="getDataList"
    >
      <template slot="menuLeft">
        <el-button
          type="primary"
          icon="el-icon-plus"
          size="small"
          v-if="isAuth('notice:add')"
          @click.stop="addOrUpdateHandle()"
        >新增通知</el-button>
      </template>
      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('notice:update')"
          @click.stop="addOrUpdateHandle(scope.row)"
        >修改</el-button>
        <el-button
          type="danger"
          icon="el-icon-delete"
          size="small"
          v-if="isAuth('notice:delete')"
          @click.stop="deleteHandle(scope.row.id)"
        >删除</el-button>
      </template>
    </avue-crud>
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdate"
      @refreshDataList="getDataList"
    ></add-or-update>
  </div>
</template>

<script>
import { tableOption } from '@/crud/notice'
import AddOrUpdate from './notice-add-or-update'
import { encrypt } from '@/utils/crypto'

export default {
  data() {
    return {
      dataList: [],
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      tableOption: tableOption,
      searchParams: {},
      page: {
        total: 0,
        currentPage: 1,
        pageSize: 10
      }
    }
  },
  components: {
    AddOrUpdate
  },
  methods: {
    getDataList(page, done) {
      const params = {
        current: page == null ? this.page.currentPage : page.currentPage,
        size: page == null ? this.page.pageSize : page.pageSize,
        ...this.searchParams
      }
      this.dataListLoading = true
      this.$http({
        url: this.$http.adornUrl('/userNotice/list'),
        method: 'post',
        data: this.$http.adornData(params)
      }).then(({ data }) => {
        this.dataList = data.data.records
        this.page.total = data.data.total
        this.dataListLoading = false
        if (done) {
          done()
        }
      })
    },
    searchChange(params, done) {
      this.page.currentPage = 1
      this.searchParams = params
      this.getDataList(this.page, done)
    },
    selectionChange(val) {
      this.dataListSelections = val
    },
    addOrUpdateHandle(row) {
      this.addOrUpdateVisible = true
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(row)
      })
    },
    deleteHandle(id) {
      this.Open(() => {
        this.$http({
          url: this.$http.adornUrl('/userNotice/delete'),
          method: 'post',
          data: this.$http.adornData({
            id: id,
            loginSafeword: encrypt(this.loginSafeword)
          })
        }).then(({ data }) => {
          if (data.code == 0) {
            this.getDataList(this.page)
            this.$message({
              message: '操作成功',
              type: 'success',
              duration: 1000,
              onClose: () => {
                this.visible = false
              }
            })
          } else {
            this.$notify({
              title: '消息',
              message: data.msg,
              type: 'warning'
            })
          }
        })
      })
    },
    Open(call) {
      this.$prompt('登录人资金密码', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(({ value }) => {
        this.loginSafeword = value
        if (call) {
          call()
        }
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消输入'
        })
      })
    }
  }
}
</script>

<style scoped></style>
