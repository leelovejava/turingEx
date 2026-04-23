<template>
  <div class="mod-transport">
    <avue-crud
      ref="crud"
      :page.sync="page"
      :data="dataList2"
      :option="tableOption"
      @search-change="searchChange"
      @selection-change="selectionChange"
      :cell-class-name="addClasscolor"
      @on-load="getDataList"
    >
      <template slot="menuLeft">
        <el-tabs v-model="activeName" @tab-click="handleClick">
          <el-tab-pane label="当前委托" name="1"></el-tab-pane>
          <el-tab-pane label="历史委托" name="2"></el-tab-pane>
        </el-tabs>

        <el-form
          :inline="true"
          :model="dataForm"
          @keyup.enter.native="getDataList(this.page)"
        >
          <el-form-item label="项目名称:" label-width="80px">
            <el-input
              v-model="dataForm.name"
              placeholder="数字货币/股票/基金"
              clearable
            ></el-input>
          </el-form-item>

          <el-form-item label="订单状态:" label-width="80px">
            <el-select v-model="dataForm.OrderStatus" placeholder="全部">
              <el-option label="全部" value="0"></el-option>
              <el-option label="交易中" value="1"></el-option>
              <el-option label="已完成" value="2"></el-option>
              <el-option label="已撤单" value="3"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="订单方向:" label-width="80px">
            <el-select v-model="dataForm.OrderDirection" placeholder="全部">
              <el-option label="全部" value="0"></el-option>
              <el-option label="买入" value="1"></el-option>
              <el-option label="卖出" value="2"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="挂单类型:" label-width="80px">
            <el-select v-model="dataForm.OrderType" placeholder="全部">
              <el-option label="全部" value="0"></el-option>
              <el-option label="市价" value="1"></el-option>
              <el-option label="限价" value="2"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="机器人:" label-width="80px">
            <el-select v-model="dataForm.robot" placeholder="全部">
              <el-option label="全部" value="0"></el-option>
              <el-option label="查看机器人" value="1"></el-option>
              <el-option label="不看机器人" value="2"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="订单号:" label-width="80px">
            <el-input
              v-model="dataForm.OrderNo"
              placeholder="请输入订单号查询"
              clearable
            ></el-input>
          </el-form-item>

          <el-form-item label="用户ID:" label-width="80px">
            <el-input
              v-model="dataForm.userId"
              placeholder="请输入用户ID查询"
              clearable
            ></el-input>
          </el-form-item>

          <el-form-item label="结算单位:" label-width="80px">
            <el-input
              v-model="dataForm.PriceUnit"
              placeholder="请输入结算单位查询"
              clearable
            ></el-input>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              icon="el-icon-search"
              size="small"
              @click="getList()"
              v-if="isAuth('order:robot:details')"
              >查询</el-button
            >
          </el-form-item>
        </el-form>

        <el-tabs v-model="activeName2" @tab-click="handleClick2">
          <el-tab-pane label="今日" name="1"></el-tab-pane>
          <el-tab-pane label="昨日" name="2"></el-tab-pane>
          <el-tab-pane label="7日" name="3"></el-tab-pane>
          <el-tab-pane label="15日" name="4"></el-tab-pane>
          <el-tab-pane label="30日" name="5"></el-tab-pane>
        </el-tabs>
      </template>

      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('order:robot:details')"
          @click.stop="addOrUpdateHandle(scope.row.roleId)"
          >明细</el-button
        >

        <el-button
          type="danger"
          icon="el-icon-delete"
          size="small"
          v-if="isAuth('order:robot:details')"
          @click.stop="deleteHandle(scope.row)"
          >撤销</el-button
        >
      </template>
    </avue-crud>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdate"
      @refreshDataList="getDataList"
    ></add-or-update>
  </div>
</template>

<script>
import { tableOption } from "@/crud/robot/robot-order";
import AddOrUpdate from "./robot-order-add-or-update";
export default {
  data() {
    return {
      dataForm: {
        OrderStatus: "0",
        OrderDirection: "0",
        OrderType: "0",
        robot: "0",
      },
      activeName: "1",
      activeName2: "1",
      dataList2: [],
      dataList: [
        // {
        //   "OrderNo":"订单号",
        //   "userId":"用户ID",
        //   "symbol":"交易对",
        //   "name":"项目名称",
        //   "EntrustedQuantity":"委托量(USDT)",
        //   "turnover":"成交量(USDT)",
        //   "OrderType":"挂单类型",
        //   "OrderDirection":"挂单方向",
        //   "ListingPrice":"挂单价格",
        //   "RegistrationTime":"挂单时间",
        //   "OrderStatus":"挂单状态",
        // },
        {
          OrderNo: "1",
          userId: "1",
          symbol: "NEEQ/USD",
          name: "NEEQ/USD",
          EntrustedQuantity: 100,
          turnover: 200,
          OrderType: "1",
          OrderDirection: "1",
          ListingPrice: 300,
          RegistrationTime: "2023-05-30",
          OrderStatus: "1",
        },
        {
          OrderNo: "2",
          userId: "2",
          symbol: "BTC/USD",
          name: "BTC/USD",
          EntrustedQuantity: 100,
          turnover: 200,
          OrderType: "2",
          OrderDirection: "2",
          ListingPrice: 300,
          RegistrationTime: "2023-05-29",
          OrderStatus: "2",
        },
      ],
      dataListHistory: [
        // {
        //   "OrderNo":"订单号",
        //   "userId":"用户ID",
        //   "symbol":"交易对",
        //   "name":"项目名称",
        //   "EntrustedQuantity":"委托量(USDT)",
        //   "turnover":"成交量(USDT)",
        //   "OrderType":"挂单类型",
        //   "OrderDirection":"挂单方向",
        //   "ListingPrice":"挂单价格",
        //   "RegistrationTime":"挂单时间",
        //   "OrderStatus":"挂单状态",
        // },
        {
          OrderNo: "1",
          userId: "1",
          symbol: "NEEQ/USD",
          name: "NEEQ/USD",
          EntrustedQuantity: 100,
          turnover: 200,
          OrderType: "1",
          OrderDirection: "1",
          ListingPrice: 300,
          RegistrationTime: "2022-02-11",
          OrderStatus: "2",
        },
        {
          OrderNo: "2",
          userId: "2",
          symbol: "BTC/USD",
          name: "BTC/USD",
          EntrustedQuantity: 100,
          turnover: 200,
          OrderType: "2",
          OrderDirection: "2",
          ListingPrice: 300,
          RegistrationTime: "2022-02-11",
          OrderStatus: "2",
        },
      ],
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      tableOption: tableOption,
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
    };
  },
  components: {
    AddOrUpdate,
  },
  methods: {
    // 获取数据列表
    getDataList(page, params, done) {
      this.getList();

      return;
      let obj = {
        current: this.pageIndex,
        size: this.pageSize,
      };

      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/banner/list"),
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
        this.dataList = data.records;
        this.page.total = data.total;
        this.dataListLoading = false;
        if (done) {
          done();
        }
      });
    },
    // 条件查询
    searchChange(params, done) {
      this.getDataList(this.page, params, done);
    },
    // 多选变化
    selectionChange(val) {
      this.dataListSelections = val;
    },
    // 新增 / 修改
    addOrUpdateHandle(id) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id);
      });
    },
    // 删除
    deleteHandle(row) {
      //
      this.$confirm(`是否撤销?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          row.OrderStatus = "3";
          //
          this.$message({
            message: "操作成功",
            type: "success",
            duration: 1500,
            onClose: () => {},
          });
          //
        })
        .catch(() => {});
      //
    },
    handleClick(tab, event) {
      this.getList();
    },
    handleClick2(tab, event) {
      this.getList();
    },
    getList() {
      let call = (item) => {
        let flag0 =
          !this.dataForm.name || item.name.indexOf(this.dataForm.name) >= 0;

        let flag1 =
          this.dataForm.OrderStatus == "0" ||
          item.OrderStatus == this.dataForm.OrderStatus;
        let flag2 =
          this.dataForm.OrderDirection == "0" ||
          item.OrderDirection == this.dataForm.OrderDirection;
        let flag3 =
          this.dataForm.OrderType == "0" ||
          item.OrderType == this.dataForm.OrderType;

        let flag4 =
          !this.dataForm.OrderNo || item.OrderNo == this.dataForm.OrderNo;

        let flag5 =
          !this.dataForm.userId ||
          item.userId.indexOf(this.dataForm.userId) >= 0;

        let flag6 = false;

        var date = new Date(item.RegistrationTime);

        // console.log(new Date().getTime()); //Jul 31, 2021 00:00:00.000 (UTC+08:00) 或 Sunday, January

        // console.log(date.getTime());

        // console.log(((new Date().getTime()) - date.getTime()) / 1000);

        let tt =
          ((new Date().getTime() - date.getTime()) / 1000 / 3600 + 8) / 24;

        console.log("tt = " + tt);

        switch (this.activeName2) {
          case "1":
            {
              flag6 = tt < 1;
            }
            break;
          case "2":
            {
              flag6 = tt < 2 && tt >= 1;
            }
            break;
          case "3": //7
            {
              flag6 = tt < 7 && tt >= 2;
            }
            break;
          case "4": //15
            {
              flag6 = tt < 15 && tt >= 7;
            }
            break;
          case "5": //30
            {
              flag6 = tt >= 15;
            }
            break;
        }

        console.log("flag0 = " + flag0);
        console.log("flag1 = " + flag1);
        console.log("flag2 = " + flag2);
        console.log("flag3 = " + flag3);
        console.log("flag4 = " + flag4);
        console.log("flag5 = " + flag5);
        console.log("flag6 = " + flag6);

        return flag0 && flag1 && flag2 && flag3 && flag4 && flag5 && flag6;
      };

      if (this.activeName == "1") {
        this.dataList2 = this.dataList.filter((item) => {
          return call(item);
        });
      } else if (this.activeName == "2") {
        this.dataList2 = this.dataListHistory.filter((item) => {
          return call(item);
        });
      }
    },
    addClasscolor({ column, row }) {
      //表单样式
      if (column.property === "OrderDirection") {
        if (row.OrderDirection == "1") {
          return "green";
        } else {
          return "red";
        }
      } else if (column.property == "turnover") {
        return "blue";
      }
      return "";
    },
  },
};
</script>
