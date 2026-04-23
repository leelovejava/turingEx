<template>
  <div class="mod-c2c_pay_order">
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
        <el-button type="text" @click.stop="searPaymsg(scope.row.order_no)"
          >点击查看</el-button
        >
      </template>
      <template slot-scope="scope" slot="paramName3">
        <el-button type="text" @click.stop="searchOtherMsg(scope.row)"
          ><span class="clcleChear">查看</span>
          <span v-if="scope.row.unread_msg" class="cicleMsg">{{
            scope.row.unread_msg
          }}</span></el-button
        >
      </template>
      <template slot-scope="scope" slot="paramName4">
        <el-button type="text" @click.stop="lastSearch(scope.row)"
          >更多信息</el-button
        >
      </template>
      <!-- <template slot-scope="scope" slot="menu">
        <el-select
          v-model="scope.row.select"
          class="celectSpeac"
          clearable
          placeholder="操作"
          @change="
            changeSelet(
              scope.row.userId,
              scope.row.select,
              scope.row.userCode,
              scope.row
            )
          "
        >
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </template> -->
      <template slot-scope="scope" slot="menu">

        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="
           scope.row.direction == 'buy'&&(scope.row.state == '1' ||
            scope.row.state == '2' ||
            scope.row.state == '5')&&
            isAuth('c2c:c2c_pay_order:pass')
          "
          @click.stop="passtHandle(scope.row, 'n')"
          >手动放行</el-button
        >
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="
          scope.row.direction == 'sell'&&(scope.row.state == '1' ||
            scope.row.state == '2' ||
            scope.row.state == '5')&& isAuth('c2c:c2c_pay_order:adminpass')
          "
          @click.stop="passtHandle(scope.row, 'n')"
          >已付款管理员手动放行</el-button
        >
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="
          isAuth('c2c:c2c_pay_order:transfer') &&
          scope.row.direction == 'sell'&&(scope.row.state == '0' ||
            scope.row.state == '2') 
          "
          @click.stop="passtSellHandle(scope.row)"
          >手动转账</el-button
        >
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('c2c:c2c_pay_order:pass') && scope.row.state == '0' && scope.row.direction == 'buy'"
          @click.stop="passtHandle(scope.row)"
          >一键通过</el-button
        >
        <el-button
          type="danger"
          icon="el-icon-delete"
          size="small"
          v-if="
            isAuth('c2c:c2c_pay_order:cancel') && (
            scope.row.state == '0' ||
            scope.row.state == '1' ||
            scope.row.state == '2' ||
            //scope.row.state == '3' ||
            scope.row.state == '5')
          "
          @click.stop="deletOrdeHandle(scope.row)"
          >取消订单</el-button
        >
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
    <!-- 参数管理 -->
    <lastMsg v-if="lastVisible" @refreshDataList="getDataList" ref="lastUpdate">
    </lastMsg>
    <!-- 参数管理 -->
    <payMsg v-if="payMsgVisible" @refreshDataList="getDataList" ref="payUpdate">
    </payMsg>
    <!-- 查看 聊天 -->
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
    <!-- 取消订单 -->
    <passtpn
      v-if="passtpnVisible"
      @refreshDataList="getDataList"
      ref="passtpnUpdate"
    >
    </passtpn>
    <!-- 手动转账 -->
        <passSelltpn
      v-if="passSelltpnVisible"
      @refreshDataList="getDataList"
      ref="passSelltUpdate"
    >
    </passSelltpn>
  </div>
</template>
<script>
import { tableOption } from "@/crud/order/c2c_order";
import AddOrUpdate from "./c2c-pay-update";
import parameters from "./grid-parameters";
import lastMsg from "./c2c-order-more-last";
import payMsg from "./c2c-order-pay-msg";
import otherMsg from "./c2c-order-other-msg";
import deletMsg from "./c2c-ord-delet";
import passtpn from "./c2c-ord-passtpn";
import passSelltpn from "./c2c-ord-passtSellpn";
export default {
  data() {
    return {
      dataList: [],
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      passSelltpnVisible:false,
      passtpnVisible: false,
      deletVisible: false,
      otherVisible: false,
      payMsgVisible: false,
      parametersFlag: false,
      lastVisible: false,
      tableOption: tableOption,
      options: [
        {
          value: "1",
          label: "修改账户余额",
        },
        {
          value: "2",
          label: "转移账户锁定金额",
        },
        {
          value: "3",
          label: "增加账户锁定金额",
        },
        {
          value: "4",
          label: "减少账户锁定金额",
        },
        {
          value: "5",
          label: "转移账户冻结金额",
        }
      ],
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
    passSelltpn
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
    console.log('停止定时器')
    // 在路由离开之前停止定时器
    this.stopTimer();
    next();
  },
  beforeRouteEnter(to, from, next) {
    console.log('启动定时器')
    // 在路由进入之前执行，通过 next 回调传递实例给回调函数
    next(vm => {
      vm.startTimer(); // 通过回调函数重新启动定时器
    });
  },
  methods: {
    // 获取数据列表
    getDataList(page, done) {
      this.dataListLoading = true;
      const params = {
        current: page == null ? this.page.currentPage : page.currentPage,
        size: page == null ? this.page.pageSize : page.pageSize,
        ...this.searchParams,
      };
      this.$http({
        url: this.$http.adornUrl("/c2cOrder/list"),
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
    changeSelet(uid, val, userCode, row) {
      if (val) {
        let m = this.options[val - 1].label; //弹窗标题
        if (
          val == 1 ||
          val == 8 ||
          val == 7 ||
          val == 9 ||
          val == 6 ||
          val == 10 ||
          val == 12
        ) {
          // 1资金账户 2转移账户锁定金额 3增加账户锁定金额 4减少账户锁定金额 5转移账户冻结金额 8谷歌验证 7密码 9资金密码 6提现限制流水 10强制退出 12 赠送USDT
          this.addOrUpdateVisible = true;
          this.$nextTick(() => {
            this.$refs.addOrUpdate.init(uid, m, val,row);
          });
        } else if (val == 2 || val == 5 || val == 3 || val == 4) {
          this.otherdateHand(uid, m, val, userCode);
        } else if (val == 11) {
          this.$router.push({
            path: "/sys-config-account-change-record-sys-config",
            query: { uid: userCode },
          });
          // router.push({ name: 'account-change-record-sys-config' })
        }
        row.select = "";
      }
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
        (column.property === "direction" && row.direction == "buy")
      ) {
        return "green";
      } else if (
        (column.property === "rolename" && row.rolename == "GUEST") ||
        (column.property === "state" && row.state == "0")
      ) {
        return "yellow";
      } else if (
        (column.property === "state" && row.state == "5") ||
        (column.property === "direction" && row.direction == "sell")
      ) {
        return "red";
      }
    },

    // 条件查询
    searchChange(params, done) {
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
    searPaymsg(id) {
      this.payMsgVisible = true;
      this.$nextTick(() => {
        this.$refs.payUpdate.init(id);
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
    passtSellHandle(row) {
      this.passSelltpnVisible = true;
      this.$nextTick(() => {
        this.$refs.passSelltUpdate.init(row);
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
