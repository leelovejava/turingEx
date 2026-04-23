<template>
  <div class="mod-etf-pickAddr">
    <avue-crud
      ref="crud"
      :page.sync="page"
      :data="dataList"
      :option="tableOption"
      :cell-class-name="addClasscolor"
      @search-change="searchChange"
      @selection-change="selectionChange"
      @refresh-change="refreshChange"
      @on-load="getDataList"
    >
      <template slot="menuLeft">
        <avue-tabs :option="option" @change="handleChange"></avue-tabs>
        <span v-if="type.prop === 'tab1'"></span>
        <span v-else-if="type.prop === 'tab2'"></span>
        <span v-else-if="type.prop === 'tab3'"></span>
        <span v-else-if="type.prop === 'tab4'"></span>
      </template>
      <template slot="ndhSearch">
        <avue-select
          v-model="symbol"
          placeholder="请选择币种"
          :dic="options"
        ></avue-select>
      </template>
      <template slot-scope="scope" slot="timenum">
        <span>{{ scope.row.timenum + getTimeUnitLabel(scope.row.timeunit) }}</span>
      </template>
      <template slot-scope="scope" slot="createTimeTss">
        <span>{{ formatTimestamp(scope.row.createTimeTs) }}</span>
      </template>
      <template slot-scope="scope" slot="settlementTimes">
        <span>{{ formatTimestamp(scope.row.settlementTime) }}</span>
      </template>
      <template slot-scope="scope"
                slot="menu">
        <el-button type="primary"
                   icon="el-icon-edit"
                   size="small"
                   v-if="scope.row.state !=='created' && isAuth('etf-spots:operate')"
                   @click.stop="pickHandle(scope.row.orderNo)">场控</el-button>
        
      </template>
    </avue-crud>
    <pickUpdate v-if="pickUpdateVisible"
                   ref="pickUpdate"
                   @refreshDataList="getDataList"></pickUpdate>
    <!-- 弹窗, 新增 / 修改 -->
    <!-- <add-or-update v-if="addOrUpdateVisible"
                   ref="addOrUpdate"
                   @refreshDataList="getDataList"></add-or-update> -->
    <!-- 谷歌验证 -->
    <!-- <add-or-gogle v-if="UpdateGogle"
                   ref="UpdateGogle"
                   @refreshDataList="getDataList"></add-or-gogle> -->
  </div>
</template>
<script>
import { tableOption } from "@/crud/shop/pickAddr";
import pickUpdate from "./pick-update";
export default {
  data() {
    return {
      dataList: [],
      time: [
        {
          label: "秒",
          value: "second",
        },
        {
          label: "分",
          value: "minute",
        },
        {
          label: "时",
          value: "hour",
        },
        {
          label: "天",
          value: "day",
        },
      ],
      dataListLoading: false,
      pickUpdateVisible:false,
      dataListSelections: [],
      tableOption: tableOption,
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
      type: {},
      options: [],
      searchParams: {}, // 搜索条件
      state: "",
      option: {
        column: [
          {
            label: "全部",
            prop: "",
          },
          {
            label: "已提交",
            prop: "submitted",
          },
          {
            label: "已撤销",
            prop: "canceled",
          },
          {
            label: "委托完成",
            prop: "created",
          },
        ],
      },
      symbol:""
    };
  },
  components: {
    pickUpdate
    // AddOrUpdate,
    // AddOrGogle
  },
  created() {
    this.type = this.option.column[0];
    this.getAction();
  },
  methods: {
    getDataList(page, done) {
      const params = {
        current: page == null ? this.page.currentPage : page.currentPage,
        size: page == null ? this.page.pageSize : page.pageSize,
        ...this.searchParams,
      };
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl(
          "/normal/adminFuturesOrderAction!/list.action"
        ),
        method: "get",
        params: this.$http.adornParams(
          Object.assign(
            {
              type: "indices", //forex->外汇,commodities->大宗商品，指数/ETF->indices, A-stocks->A股, HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币
              symbol: this.symbol,
              state: this.state,
              current: page == null ? this.page.currentPage : page.currentPage,
              size: page == null ? this.page.pageSize : page.pageSize,
            },
            params
          )
        ),
      }).then(({ data }) => {
        this.dataList = data.data.records;
        this.page.total = data.data.total;
        this.dataListLoading = false;
        if (done) {
          done();
        }
      });
    },
    getTimeUnitLabel(timeunit) {
      const foundTimeUnit = this.time.find(unit => unit.value === timeunit);
      return foundTimeUnit ? foundTimeUnit.label : '';
    },
    addClasscolor({ column, row }) {
      //表单样式
      if (
        (column.property === "direction" && row.direction == "buy") ||
        (column.property === "profit" && row.profit * 1 > 0) ||
        (column.property === "roleName" && row.roleName == "MEMBER") ||
        (column.property === "state" && row.state == "submitted")||
        (column.property === "profitLosssStr" && row.profitLosssStr == "盈利")
      ) {
        return "green";
      } else if (
        (column.property === "direction" && row.direction == "sell") ||
        (column.property === "profit" && row.profit * 1 < 0)||
        (column.property === "profitLosssStr" && row.profitLosssStr == "亏损")
      ) {
        return "red";
      } else if (column.property === "roleName" && row.roleName == "GUEST") {
        return "yellow";
      }
    },
    //币种列表
    getAction() {
      this.$http({
        url: this.$http.adornUrl("/normal/adminItemAction!/list"),
        method: "get",
        params: this.$http.adornParams({
          type:'indices'
        }),
      }).then(({ data }) => {
        if (data.data.records) {
          this.options = data.data.records.map((item, index) => {
            return Object.assign({}, { value: item.symbol, label: item.symbol });
          });
        }
        //console.log(this.options);
      });
    },
    // 撤销操
    deleteHandle(orderNo) {
      this.$confirm("您确认要撤销操作吗", "是否确认撤销", {
        //系统管理用户是否已绑定
        distinguishCancelAndClose: true,
        confirmButtonText: "取消",
        cancelButtonText: "撤销",
        type: "success",
      })
        .then(() => {
          //console.log(11111);
        })
        .catch((action) => {
          if (action === "cancel") {
            this.$http({
              url: this.$http.adornUrl(
                "/normal/adminContractApplyOrderAction!/close.action"
              ),
              method: "get",
              params: this.$http.adornParams({
                orderNo: orderNo,
              }),
            }).then(({ data }) => {
              this.$message({
                message: "撤销成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.visible = false;
                  this.getDataList();
                },
              });
            });
          } else {
          }
        });
    },
    formatTimestamp(timestamp) {
      const date = new Date(timestamp * 1000);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, "0");
      const day = String(date.getDate()).padStart(2, "0");
      const hours = String(date.getHours()).padStart(2, "0");
      const minutes = String(date.getMinutes()).padStart(2, "0");
      const seconds = String(date.getSeconds()).padStart(2, "0");
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    },
    // 场控
    pickHandle(id) {
      this.pickUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.pickUpdate.init(id);
      });
    },
    // tab切换
    handleChange(column, params, done) {
      this.type = column;
      this.state = column.prop;
      this.getDataList(this.page, params, done);
    },
    // 条件查询
    searchChange(params, done) {
      this.page.currentPage = 1; // 重置当前页为第一页
      this.searchParams = params;
      this.getDataList(this.page, done);
    },
    // 点击刷新按钮
    refreshChange(params, done) {
      this.getDataList(this.page, params, done);
    },
    // 多选变化
    selectionChange(val) {
      this.dataListSelections = val;
    },
  },
};
</script>
