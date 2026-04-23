<template>
  <div class="mod-us-hosearch">
    <el-tabs v-model="stockName" @tab-click="stockHandleClick">
      <el-tab-pane v-if="isAuth('union-stocks:us')" name="US-stocks">
        <span slot="label">
          美股
          <el-badge :value="countNum('.us-spots-us-hotSearch')" v-if="countNum('.us-spots-us-hotSearch') > 0"></el-badge>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="isAuth('union-stocks:hk')" name="HK-stocks">
        <span slot="label">
          港股
          <el-badge :value="countNum('.hk-stocks-hotSearch')" v-if="countNum('.hk-stocks-hotSearch') > 0"></el-badge>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="isAuth('union-stocks:tw')" name="TW-stocks">
        <span slot="label">
          台股
          <el-badge :value="countNum('.tw-stocks-hotSearch')" v-if="countNum('.tw-stocks-hotSearch') > 0"></el-badge>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="isAuth('union-stocks:a')" name="A-stocks">
        <span slot="label">
          A股
          <el-badge :value="countNum('.A-stocks-hotSearch')" v-if="countNum('.A-stocks-hotSearch') > 0"></el-badge>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="isAuth('union-stocks:jp')" name="JP-stocks">
        <span slot="label">
          日股
          <el-badge :value="countNum('.jp-stocks-hotSearch')" v-if="countNum('.jp-stocks-hotSearch') > 0"></el-badge>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="isAuth('union-stocks:india')" name="INDIA-stocks">
        <span slot="label">
          印度股
          <el-badge :value="countNum('.INDIA-stocks-hotSearch')" v-if="countNum('.INDIA-stocks-hotSearch') > 0"></el-badge>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="isAuth('union-stocks:uk')" name="UK-stocks">
        <span slot="label">
          英股
          <el-badge :value="countNum('.UK-stocks-hotSearch')" v-if="countNum('.UK-stocks-hotSearch') > 0"></el-badge>
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
      <template slot-scope="scope" slot="menu">
        <el-button
          type="danger"
          icon="el-icon-delete"
          size="small"
          v-if="isAuth('union-spots:operate')"
          @click.stop="deleteHandle(scope.row.orderNo)"
          >撤销</el-button
        >
      </template>
    </avue-crud>
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
import { tableOption } from "@/crud/shop/hotSearch";
import { getStockName } from "./config";
export default {
  data() {
    return {
      dataList: [],
      dataListLoading: false,
      dataListSelections: [],
      tableOption: tableOption,
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
      searchParams: {}, // 搜索条件
      type: {},
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
      stocksMap:{}
    };
  },
  components: {
    // AddOrUpdate,
    // AddOrGogle
  },
  created() {
    this.type = this.option.column[0];
    this.stockName = getStockName();
    if(this.$store.state.common.stocksValue && this.$store.state.common.stocksValue !=""){
      this.stockName = this.$store.state.common.stocksValue;
      this.$store.commit("common/updateStocksValue", "")
    }
    this.stocksMap = {};
  },
  mounted() {
    this.$bus.$on('update-hotSearch', (data)=>{
      this.stockName = data;
    })
  },
  // 在页面销毁前销毁定时器
  beforeDestroy() {
    this.$bus.$off('update-hotSearch')
  },
  methods: {
    getDataList(page, done) {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl(
          "/normal/adminContractApplyOrderAction!/list.action"
        ),
        method: "get",
        params: this.$http.adornParams(
          Object.assign(
            {
              type: this.stockName, //forex->外汇,commodities->大宗商品，指数/ETF->indices, A-stocks->A股, HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币
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
    addClasscolor({ column, row }) {
      //表单样式
      if (column.property === "direction" && row.direction == "buy") {
        return "green";
      } else if (column.property === "direction" && row.direction == "sell") {
        return "red";
      } else {
        return "";
      }
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
          console.log(11111);
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
