<template>
  <div class="mod-customer-service-sys-config">
    <avue-crud ref="crud"
              :page.sync="page"
               :data="dataList"
               :option="tableOption"
               @search-change="searchChange"
               @selection-change="selectionChange"
               :cell-class-name="addClasscolor"
               @on-load="getDataList">
      <template slot="menuLeft">
        <el-button type="primary"
                   icon="el-icon-plus"
                   size="small"
                   v-if="isAuth('sys:config:customer-service-sys-config:operate')"
                   @click.stop="addOrUpdateHandle()">新增客服</el-button>
      </template>
      <template slot="userndhSearch">
        <el-input
          v-model="userName"
          placeholder="用户名"
          clearable
        ></el-input>
      </template>
      <template slot-scope="scope"
                slot="menu">
        <el-select 
          v-if="isAuth('sys:config:customer-service-sys-config:operate')"
          v-model="scope.row.select"  class="celectSpeac" clearable placeholder="操作" @change="changeSelet(scope.row.select,scope.row,scope.row.googleAuthBind,scope.row.userId)">
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </template>
    </avue-crud>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update v-if="addOrUpdateVisible"
                   ref="addOrUpdate"
                   @refreshDataList="getDataList"></add-or-update>
    <add-other-update v-if="addOtherUpdateVisible"
                   ref="addOtherUpdate"
                   @refreshDataList="getDataList"></add-other-update>
    <!-- 谷歌验证 -->
    <add-or-gogle v-if="UpdateGogle"
                   ref="UpdateGogle"
                   @refreshDataList="getDataList"></add-or-gogle>
  </div>
</template>

<script>
import { tableOption } from '@/crud/sys/customer-service'
import AddOrUpdate from './customer-service-add-or-update'
import AddOtherUpdate from './customer-service-mix-or-update'
import AddOrGogle from './customer-googleAuthCode'
export default {
  data () {
    return {
      dataList: [],
      dataListLoading: false,
      UpdateGogle:false,
      userName:'',
      dataListSelections: [],
      addOrUpdateVisible: false,
      addOtherUpdateVisible:false,
      tableOption: tableOption,
      options: [
        {
          value: "1",
          label: "修改",
        },
        {
          value: "2",
          label: "修改密码",
        },
        {
          value: "3",
          label: "修改资金密码",
        },
        {
          value: "4",
          label: "强制下线",
        },
        {
          value: "5",
          label: "谷歌验证器",
        },
      ],
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10 // 每页显示多少条
      },
      dataForm: {
        userName: ''
      },
    }
  },
  components: {
    AddOrUpdate,
    AddOtherUpdate,
    AddOrGogle
  },
  methods: {
    // 获取数据列表
    getDataList (page, params, done) {
      this.dataListLoading = true
      this.$http({
        url: this.$http.adornUrl('/customer/list'),
        method: 'post',
        data: this.$http.adornData(
          Object.assign(
            {
              userName:this.userName,
              current: page == null ? this.page.currentPage : page.currentPage,
              size: page == null ? this.page.pageSize : page.pageSize,
            },
            params
          )
        ),
      }).then(({ data }) => {
        this.dataList = data.data.records
        this.page.total = data.data.total
        this.dataListLoading = false
        if (done) {
          done()
        }
      })
    },
    addClasscolor({ column, row }) {//表单样式
      if (
        (column.property === "onlineState" && row.onlineState*1 == 1) ||
        (column.property === "googleAuthBind" && row.googleAuthBind*1 == 1)
      ) {
        return "green";
      } else {
        return "";
      }
    },
    // 条件查询
    searchChange (params, done) {
      this.page.currentPage = 1;
      this.getDataList(this.page, params, done)
    },
    // 多选变化
    selectionChange (val) {
      this.dataListSelections = val
    },
    // 新增 / 修改
    addOrUpdateHandle (row) {
      this.addOrUpdateVisible = true
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(row)
      })
    },
    changeSelet(val,row,googleAuthBind,id){
      if(val){
        let m = this.options[val-1].label //弹窗标题
        if(val==2 || val == 3 || val == 4){ // 1修改 2修改密码 3修改资金密码 4强制下线 5谷歌验证器
          this.addOtherUpdateVisible = true;
          this.$nextTick(() => {
            this.$refs.addOtherUpdate.init(val,row,m);
          });
        }else if(val==1){
          this.addOrUpdateVisible = true
          this.$nextTick(() => {
          this.$refs.addOrUpdate.init(row)
      })
        } else if( val == 5){
          this.updategoogleAuthCode(googleAuthBind,id,row)
        }
      }
      row.select = "";
    },
        // 谷歌验证
    addOrUpdateGogle(googleAuthBind,id,data){
      this.UpdateGogle = true
      this.$nextTick(() => {
        this.$refs.UpdateGogle.init(googleAuthBind,id,data)
      })
    },
              // 谷歌验证码绑定
  updategoogleAuthCode (googleAuthBind,id,data) {
      if(googleAuthBind){        
          this.$confirm('谷歌验证器已绑定', '谷歌验证器', { //系统管理用户是否已绑定
          distinguishCancelAndClose: true,
          confirmButtonText: '确定',
          cancelButtonText: '解绑',
          type: 'success'
          }).then(() => {
          }).catch(action  => {
            if(action === 'cancel'){
              console.log(id)
              this.addOrUpdateGogle(googleAuthBind,id,data)
            }
          });
        }else{
          this.addOrUpdateGogle(googleAuthBind,id,data)
        }
      },
    // 删除
    deleteHandle (id) {
      var ids = id ? [id] : this.dataListSelections.map(item => {
        return item.roleId
      })
      this.$confirm(`确定进行[${id ? '删除' : '批量删除'}]操作?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/sys/role'),
          method: 'delete',
          data: this.$http.adornData(ids, false)
        }).then(({ data }) => {
          this.$message({
            message: '操作成功',
            type: 'success',
            duration: 1500,
            onClose: () => {
              this.getDataList()
            }
          })
        })
      }).catch(() => { })
    }
  }
}
</script>
<style  lang="scss" scoped>
 /* 以下是下拉框操作按钮样式 */
 ::v-deep .celectSpeac .el-input__inner{
  background: #1C4EFA !important;
  }
  ::v-deep .celectSpeac .el-input__inner::placeholder{
  color: #fff;
  }
</style>