<template>
  <div class="mod-us-pickAddr">
    <el-tabs v-model="stockName" @tab-click="stockHandleClick">
      <el-tab-pane v-if="isAuth('union-stocks:us')" name="US-stocks">
        <span slot="label">
          美股
          <el-badge :value="countNum('.us-spots-us-pickAddr')" v-if="countNum('.us-spots-us-pickAddr') > 0"></el-badge>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="isAuth('union-stocks:hk')" name="HK-stocks">
        <span slot="label">
          港股
          <el-badge :value="countNum('.hk-stocks-pickAddr')" v-if="countNum('.hk-stocks-pickAddr') > 0"></el-badge>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="isAuth('union-stocks:tw')" name="TW-stocks">
        <span slot="label">
          台股
          <el-badge :value="countNum('.tw-stocks-pickAddr')" v-if="countNum('.tw-stocks-pickAddr') > 0"></el-badge>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="isAuth('union-stocks:a')" name="A-stocks">
        <span slot="label">
          A股
          <el-badge :value="countNum('.A-stocks-pickAddr')" v-if="countNum('.A-stocks-pickAddr') > 0"></el-badge>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="isAuth('union-stocks:jp')" name="JP-stocks">
        <span slot="label">
          日股
          <el-badge :value="countNum('.jp-stocks-pickAddr')" v-if="countNum('.jp-stocks-pickAddr') > 0"></el-badge>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="isAuth('union-stocks:india')" name="INDIA-stocks">
        <span slot="label">
          印度股
          <el-badge :value="countNum('.INDIA-stocks-pickAddr')" v-if="countNum('.INDIA-stocks-pickAddr') > 0"></el-badge>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="isAuth('union-stocks:uk')" name="UK-stocks">
        <span slot="label">
          英股
          <el-badge :value="countNum('.UK-stocks-pickAddr')" v-if="countNum('.UK-stocks-pickAddr') > 0"></el-badge>
        </span>
      </el-tab-pane>
    </el-tabs>
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
          :dic="options[this.stockName]"
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
                   v-if="scope.row.state !=='created'&&isAuth('union-spots:operate')"
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
import { getStockName } from "./config";
import { isAuth } from '@/utils'
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
      searchParams: {}, // 搜索条件
      type: {},
      options: {},
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
      symbol:"",
      stocksMap:{}
    };
  },
  components: {
    pickUpdate
    // AddOrUpdate,
    // AddOrGogle
  },
  created() {
    this.stockName = getStockName();
    if(this.$store.state.common.stocksValue && this.$store.state.common.stocksValue !=""){
      this.stockName = this.$store.state.common.stocksValue;
      this.$store.commit("common/updateStocksValue", "")
    }
    this.type = this.option.column[0];
    let arr = ["union-stocks:us","union-stocks:hk","union-stocks:tw","union-stocks:a","union-stocks:jp","union-stocks:india","union-stocks:uk"];
    let name = ["US-stocks","HK-stocks","TW-stocks","A-stocks","JP-stocks","INDIA-stocks","UK-stocks"];
    for(let i=0 ; i < arr.length ; i++){
      if(isAuth(arr[i])){
        this.getAction(name[i]);
      }
    }

    this.stocksMap = {};
  },
  mounted() {
    this.$bus.$on('update-pickAddr', (data)=>{
      this.stockName = data;
    })
  },
  // 在页面销毁前销毁定时器
  beforeDestroy() {
    this.$bus.$off('update-pickAddr')
  },
  methods: {
    getDataList(page, done) {
      let symbol = "";
      if (this.options && this.options[this.stockName] &&this.options[this.stockName].id) {
        symbol = this.options[this.stockName].id;
      } else {
        symbol = "";
      }
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl(
          "/normal/adminFuturesOrderAction!/list.action"
        ),
        method: "get",
        params: this.$http.adornParams(
          Object.assign(
            {
              type: this.stockName, //forex->外汇,commodities->大宗商品，指数/ETF->indices, A-stocks->A股, HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币
              symbol: this.symbol,
              state: this.state,
              current: page == null ? this.page.currentPage : page.currentPage,
              size: page == null ? this.page.pageSize : page.pageSize,
            },
            this.searchParams
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
    getAction(name) {
      this.$http({
        url: this.$http.adornUrl("/normal/adminItemAction!/list"),
        method: "get",
        params: this.$http.adornParams({
          type:name
        }),
      }).then(({ data }) => {
        if (data.data.records) {
          this.options[name] = data.data.records.map((item, index) => {
            return Object.assign({}, { value: item.symbol, label: item.symbol });
          });
        }
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
    stockHandleClick(tab, event){

      // if(this.stocksMap[tab.name]!=1){
      //   this.stocksMap[tab.name]=1
      //   this.getAction();
      // }

      this.page.currentPage = 1; // 重置当前页为第一页
      this.getDataList();

    },
    countNum(type){
      if(!this.menuMap){
        // console.log(name + "-->");
        return 0;
      }
      // let type = this.menuMap[name];
      let num = this.main.tips[type]
      if(isNaN(num)){
        num = 0
      }
      // console.log(name + "-->" + type + "-->" + num);
      return num;
    },
  },
};
</script>
