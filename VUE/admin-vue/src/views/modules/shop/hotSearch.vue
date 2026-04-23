<template>
  <div class="mod-hosearch">
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
          v-if="isAuth('shop-spots:operate')"
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
    };
  },
  components: {
    // AddOrUpdate,
    // AddOrGogle
  },
  created() {
    this.type = this.option.column[0];
  },
  methods: {
    getDataList(page, params, done) {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl(
          "/normal/adminContractApplyOrderAction!/list.action"
        ),
        method: "get",
        params: this.$http.adornParams(
          Object.assign(
            {
              type: "forex", //forex->外汇,commodities->大宗商品，指数/ETF->indices, A-stocks->A股, HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币
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
      this.getDataList(this.page, params, done);
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
