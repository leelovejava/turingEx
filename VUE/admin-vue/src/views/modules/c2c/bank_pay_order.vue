<template>
  <div class="mod-bank_pay_order">
    <avue-crud
      ref="crud"
      :page.sync="page"
      :data="dataList"
      :option="tableOption"
      @search-change="searchChange"
      @selection-change="selectionChange"
      :cell-class-name="addClasscolor"
      @on-load="getDataList"
    >
      <!-- <template slot="menuLeft">
        <el-button
          type="primary"
          icon="el-icon-plus"
          size="small"
          v-if="isAuth('sys:user:save')"
          @click.stop="addOrUpdateHandle()"
          >新增演示账号</el-button
        >
      </template> -->
      <template slot-scope="scope" slot="methodImg">
        <img :src="scope.row.methodImg" alt="" />
      </template>
      <template slot-scope="scope" slot="uscodemn">
        <span>{{ scope.row.c2c_user_code }}</span>
      </template>
      <template slot-scope="scope" slot="c2cUscodemn">
        <span>{{ scope.row.c2c_user_party_code }}</span>
      </template>
      <template slot-scope="scope" slot="username">
        <el-button type="text" @click.stop="searchName(scope.row)">{{
          scope.row.username
        }}</el-button>
      </template>
      <template slot-scope="scope" slot="c2c_user_party_name">
        <el-button type="text" @click.stop="searchName(scope.row)">{{
          scope.row.c2c_user_party_name
        }}</el-button>
      </template>
      <template slot-scope="scope" slot="method_type_name">
        <el-button
          v-if="isAuth('bank:orderpay:butsearch')"
          type="text"
          @click.stop="searPaymsg(scope.row)"
          >点击查看</el-button
        >
      </template>
      <template slot-scope="scope" slot="paramName3">
        <el-button
          v-if="isAuth('bank:orderpay:kefusearch')"
          type="text"
          @click.stop="searchOtherMsg(scope.row)"
          ><span class="clcleChear">查看</span>
          <span v-if="scope.row.unread_msg" class="cicleMsg">{{
            scope.row.unread_msg
          }}</span></el-button
        >
      </template>
      <template slot-scope="scope" slot="paramName4">
        <el-button
          type="text"
          v-if="isAuth('bank:orderpay:moreMessage')"
          @click.stop="lastSearch(scope.row)"
          >更多信息</el-button
        >
      </template>
      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="
            scope.row.state == '0' &&
            scope.row.direction == 'recharge' &&
            isAuth('bank:orderpay:manualRelease')
          "
          @click.stop="passtHandle(scope.row)"
          >手动放行</el-button
        >
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="
            scope.row.state == '0' &&
            scope.row.direction == 'withdraw' &&
            isAuth('bank:orderpay:manualPayment')
          "
          @click.stop="passtHandle(scope.row, 'n')"
          >手动到账</el-button
        >
        <el-button
          type="danger"
          icon="el-icon-edit"
          size="small"
          v-if="scope.row.state == '0' && isAuth('bank:orderpay:cancel')"
          @click.stop="deletOrdeHandle(scope.row)"
          >取消订单</el-button
        >
        <!-- <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="scope.row.state == '0'"
          @click.stop="passtHandle(scope.row)"
          >一键通过</el-button
        > -->
      </template>
    </avue-crud>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdate"
      @refreshDataList="getDataList"
    ></add-or-update>
    <!-- 参数管理 -->
    <parameters
      v-if="parametersFlag"
      @refreshDataList="getDataList"
      ref="parameters"
    >
    </parameters>
    <!-- 更多信息 -->
    <lastMsg v-if="lastVisible" @refreshDataList="getDataList" ref="lastUpdate">
    </lastMsg>
    <!-- 支付方式 -->
    <payMsg v-if="payMsgVisible" @refreshDataList="getDataList" ref="payUpdate">
    </payMsg>
    <!-- 参数管理 -->
    <otherMsg
      v-if="otherVisible"
      @refreshDataList="getDataList"
      ref="otherUpdate"
    >
    </otherMsg>
    <!-- 取消订单 -->
    <deletMsg
      v-if="deletVisible"
      @refreshDataList="getDataList"
      ref="deletUpdate"
    >
    </deletMsg>
    <!-- 放行，到账 -->
    <passtpn
      v-if="passtpnVisible"
      @refreshDataList="getDataList"
      ref="passtpnUpdate"
    >
    </passtpn>
  </div>
</template>
<script>
import { tableOption } from "@/crud/order/bank_order";
import AddOrUpdate from "./c2c-pay-update";
import parameters from "./grid-parameters";
import lastMsg from "./bank-order-more-last";
import payMsg from "./bank-order-pay-msg";
import otherMsg from "./bank-order-other-msg";
import deletMsg from "./bank-ord-delet";
import passtpn from "./bank-ord-passtpn";
export default {
  data() {
    return {
      dataList: [],
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      passtpnVisible: false,
      deletVisible: false,
      otherVisible: false,
      payMsgVisible: false,
      parametersFlag: false,
      lastVisible: false,
      tableOption: tableOption,
      searchParams: {}, // 搜索条件
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
    };
  },
  components: {
    AddOrUpdate,
    parameters,
    lastMsg,
    payMsg,
    otherMsg,
    deletMsg,
    passtpn,
  },
  created() {
    this.getC2cPaymentMethodType();
  },
  // 在页面销毁前销毁定时器
  beforeDestroy() {
    this.stopTimer();
  },
  // mounted() {
  //   this.startTimer();
  // },
  beforeRouteLeave(to, from, next) {
    console.log("停止定时器");
    // 在路由离开之前停止定时器
    this.stopTimer();
    next();
  },
  beforeRouteEnter(to, from, next) {
    console.log("启动定时器");
    // 在路由进入之前执行，通过 next 回调传递实例给回调函数
    next((vm) => {
      vm.startTimer(); // 通过回调函数重新启动定时器
    });
  },
  methods: {
    // 获取数据列表
    getDataList(page, done) {
      const params = {
        current: page == null ? this.page.currentPage : page.currentPage,
        size: page == null ? this.page.pageSize : page.pageSize,
        ...this.searchParams,
      };
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/bankCardOrder/list"),
        method: "post",
        data: this.$http.adornData(
          Object.assign(
            {
              current: page == null ? this.page.currentPage : page.currentPage,
              size: page == null ? this.page.pageSize : page.pageSize,
            },
            params
          )
        ),
      }).then(({ data }) => {
        console.log(data.total);
        this.dataList = data.data.records;
        this.page.total = data.data.total;
        this.dataListLoading = false;
        if (done) {
          done();
        }
      });
    },
    // 启动定时器
    startTimer() {
      this.timer = setInterval(() => {
        this.getDataList(this.page);
      }, 5000); // 5秒钟执行一次
    },
    // 停止定时器
    stopTimer() {
      clearInterval(this.timer);
    },
    // 获取数据列表
    getC2cPaymentMethodType(page, params, done) {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl(
          "/paymentMethodConfig/getC2cPaymentMethodType"
        ),
        method: "get",
        params: this.$http.adornParams(Object.assign({}, params), false),
      }).then(({ data }) => {
        console.log(data);
        if (done) {
          done();
        }
      });
    },
    addClasscolor({ column, row }) {
      //表单样式
      if (
        (column.property === "rolename" && row.rolename == "MEMBER") ||
        (column.property === "state" && row.state == "3") ||
        (column.property === "direction" && row.direction == "recharge")
      ) {
        return "green";
      } else if (
        (column.property === "rolename" && row.rolename == "GUEST") ||
        (column.property === "state" && row.state == "0")
      ) {
        return "yellow";
      } else if (
        (column.property === "state" && row.state == "5") ||
        (column.property === "direction" && row.direction == "withdraw")
      ) {
        return "red";
      }
    },

    // 条件查询
    searchChange(params, done) {
      if (params["state"]) {
        params["status"] = params["state"];
      }
      this.page.currentPage = 1; // 重置当前页为第一页
      this.searchParams = params;
      this.getDataList(this.page, done);
    },
    // 多选变化
    selectionChange(val) {
      this.dataListSelections = val;
    },
    // 新增 / 修改
    addOrUpdateHandle(row, id) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(row, id);
      });
    },
    searchName(row) {
      //this.userNamevisible = true
      this.parametersFlag = true;
      this.$nextTick(() => {
        this.$refs.parameters.init(row);
      });
    },
    lastSearch(row) {
      //this.userNamevisible = true
      this.lastVisible = true;
      this.$nextTick(() => {
        this.$refs.lastUpdate.init(row);
      });
    },
    searPaymsg(row) {
      this.payMsgVisible = true;
      this.$nextTick(() => {
        this.$refs.payUpdate.init(row);
      });
    },
    searchOtherMsg(row) {
      this.otherVisible = true;
      this.$nextTick(() => {
        this.$refs.otherUpdate.init(row);
      });
    },
    deletOrdeHandle(row) {
      this.deletVisible = true;
      this.$nextTick(() => {
        this.$refs.deletUpdate.init(row);
      });
    },
    passtHandle(row, n) {
      this.passtpnVisible = true;
      this.$nextTick(() => {
        this.$refs.passtpnUpdate.init(row, n);
      });
    },
    // 删除
    deleteHandle(id) {
      var userIds = id
        ? [id]
        : this.dataListSelections.map((item) => {
            return item.userId;
          });
      this.$confirm(
        `确定对[id=${userIds.join(",")}]进行[${id ? "删除" : "批量删除"}]操作?`,
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      )
        .then(() => {
          this.$http({
            url: this.$http.adornUrl("/sys/user"),
            method: "delete",
            data: this.$http.adornData(userIds, false),
          }).then(({ data }) => {
            this.$message({
              message: "操作成功",
              type: "success",
              duration: 1500,
              onClose: () => {
                this.getDataList();
              },
            });
          });
        })
        .catch(() => {});
    },
  },
};
</script>
<style lang="scss" scoped>
.mod-mange {
}
.clcleChear {
  float: left;
}
.cicleMsg {
  color: #fff;
  background: red;
  border-radius: 50%;
  float: right;
  height: 15px;
  width: 15px;
  line-height: 15px;
}
</style>
