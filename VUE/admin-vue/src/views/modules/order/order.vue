<template>
  <!-- 币币交易单 -->
  <div class="mod-order-order">
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
      <!-- <template slot="ndhSearch">
        <avue-select
          v-model="options.id"
          placeholder="请选择币种"
          :dic="options"
        ></avue-select>
      </template>
      <template slot-scope="scope" slot="timenum">
        <span>{{scope.row.timenum + '秒'}}</span
        >
      </template> -->
      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('order:order:make') && scope.row.state == 'submitted'&& scope.row.orderPriceType == 'limit'"
          @click.stop="addOrUpdateHandle(scope.row.orderNo,scope.row.symbolValue)"
          >限价成交</el-button
        >
        <el-button
          type="danger"
          icon="el-icon-delete"
          size="small"
          v-if="isAuth('order:order:make')&&scope.row.state=='submitted'"
          @click.stop="deleteHandle(scope.row.orderNo)"
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
    <!-- 谷歌验证 -->
    <!-- <add-or-gogle v-if="UpdateGogle"
                   ref="UpdateGogle"
                   @refreshDataList="getDataList"></add-or-gogle> -->
  </div>
</template>
<script>
import { tableOption } from "@/crud/order/order";
import AddOrUpdate from "./order-mix-data";
export default {
  data() {
    return {
      dataList: [],
      dataListLoading: false,
      dataListSelections: [],
      tableOption: tableOption,
      addOrUpdateVisible: false,
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
      searchParams: {}, // 搜索条件
      type: {},
      options: [],
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
    };
  },
  components: {
    AddOrUpdate,
    // AddOrGogle
  },
  created() {
    this.type = this.option.column[0];
    this.getAction();
  },
  methods: {
    getDataList(page, done) {
      let symbol = "";
      if (this.options.id) {
        symbol = this.options.id;
      } else {
        symbol = "";
      }
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/exchangeApplyOrder/list"),
        method: "post",
        data: this.$http.adornData(
          Object.assign(
            {
              //type:'forex', //forex->外汇,commodities->大宗商品，指数/ETF->indices, A-stocks->A股, HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币
              symbol: symbol,
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
      if (
        (column.property === "offset" && row.offset == "open") ||
        (column.property === "profit" && row.profit * 1 > 0)
      ) {
        return "green";
      } else if (
        (column.property === "offset" && row.offset == "close") ||
        (column.property === "profit" && row.profit * 1 < 0)
      ) {
        return "red";
      } else {
        return "";
      }
    },
    //币种列表
    getAction() {
      this.$http({
        url: this.$http.adornUrl("/normal/adminItemAction!/list"),
        method: "get",
        params: this.$http.adornParams({}),
      }).then(({ data }) => {
        if (data.data.records) {
          this.options = data.data.records.map((item, index) => {
            return Object.assign({}, { value: item.symbol, label: item.name });
          });
        }
        console.log(this.options);
      });
    },
    // 限价成交
    addOrUpdateHandle(id,symbolValue) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id,symbolValue);
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
          console.log(11111);
        })
        .catch((action) => {
          if (action === "cancel") {
            this.$http({
              url: this.$http.adornUrl("/exchangeApplyOrder/close"),
              method: "post",
              data: this.$http.adornData({
                orderNo: orderNo,
              }),
            }).then(({ data }) => {
              if (data.code == 0) {
                this.$message({
                  message: "撤销成功",
                  type: "success",
                  duration: 1500,
                  onClose: () => {
                    this.visible = false;
                    this.getDataList();
                  },
                });
              } else {
                this.$message({
                  message: data.msg,
                  type: "error",
                  duration: 1500,
                  onClose: () => {
                    this.visible = false;
                  },
                });
              }
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
    refreshChange(done) {
      this.getDataList(this.page, done);
    },
    // 多选变化
    selectionChange(val) {
      this.dataListSelections = val;
    },
  },
};
</script>
