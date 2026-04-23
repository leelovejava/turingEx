<template>
  <div class="mod-markets-config">
    <!-- <div style="margin:20px 0;font-size:25px">行情管理</div> -->
    <el-tabs v-model="stockName" @tab-click="stockHandleClick">
      <el-tab-pane v-if="isAuth('union-stocks:us')" name="US-stocks">
        <span slot="label">
          美股
          <el-badge :value="countNum('美股_0')" v-if="countNum('美股_0') > 0"></el-badge>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="isAuth('union-stocks:hk')" name="HK-stocks">
        <span slot="label">
          港股
          <el-badge :value="countNum('港股_0')" v-if="countNum('港股_0') > 0"></el-badge>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="isAuth('union-stocks:tw')" name="TW-stocks">
        <span slot="label">
          台股
          <el-badge :value="countNum('台股_0')" v-if="countNum('台股_0') > 0"></el-badge>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="isAuth('union-stocks:a')" name="A-stocks">
        <span slot="label">
          A股
          <el-badge :value="countNum('A股_0')" v-if="countNum('A股_0') > 0"></el-badge>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="isAuth('union-stocks:jp')" name="JP-stocks">
        <span slot="label">
          日股
          <el-badge :value="countNum('日股_0')" v-if="countNum('日股_0') > 0"></el-badge>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="isAuth('union-stocks:india')" name="INDIA-stocks">
        <span slot="label">
          印度股
          <el-badge :value="countNum('印度股_0')" v-if="countNum('印度股_0') > 0"></el-badge>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="isAuth('union-stocks:uk')" name="UK-stocks">
        <span slot="label">
          英股
          <el-badge :value="countNum('英股_0')" v-if="countNum('英股_0') > 0"></el-badge>
        </span>
      </el-tab-pane>
    </el-tabs>
    <avue-crud
      ref="crud"
      :page.sync="page"
      :data="dataList"
      :option="tableOption"
      @search-change="searchChange"
      @selection-change="selectionChange"
      @on-load="getDataList"
    >

      <template slot-scope="scope" slot="symname">
        <span>{{ scope.row.symbol + "/USDT" }}</span>
      </template>
      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('union-spots:operate')"
          @click.stop="addOrUpdateHandle(scope.row.symbol)"
          >调整</el-button
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
import { tableOption } from "@/crud/config/markets-config";
import AddOrUpdate from "./markets-add-or-update";
import { getStockName } from "./config";
export default {
  data() {
    return {
      dataList: [],
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      tableOption: tableOption,
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
      searchParams: {}, // 搜索条件
      stocksMap:{}
    };
  },
  components: {
    AddOrUpdate,
  },
  created() {
    this.stockName = getStockName();
    if(this.$store.state.common.stocksValue && this.$store.state.common.stocksValue !=""){
      this.stockName = this.$store.state.common.stocksValue;
      this.$store.commit("common/updateStocksValue", "")
    }
    this.getDataList();
    this.stocksMap = {};
  },
  methods: {
    // 获取数据列表
    getDataList(page, done) {

      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl(
          "/normal/adminMarketQuotationsManageAction!/list.action"
        ),
        method: "get",
        params: this.$http.adornParams(
          Object.assign(
            {
              type: this.stockName, //forex->外汇,commodities->大宗商品，指数/ETF->indices, A-stocks->A股, HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币
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
    addOrUpdateHandle(id) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id);
      });
    },
    // 删除
    deleteHandle(id) {
      var ids = id
        ? [id]
        : this.dataListSelections.map((item) => {
            return item.roleId;
          });
      this.$confirm(`确定进行[${id ? "删除" : "批量删除"}]操作?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.$http({
            url: this.$http.adornUrl("/sys/role"),
            method: "delete",
            data: this.$http.adornData(ids, false),
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
