<template>
  <div class="mod-variety">
    <avue-crud
      ref="crud"
      :page.sync="page"
      :data="dataList"
      :option="tableOption"
      :cell-class-name="addCellClass"
      @search-change="searchChange"
      @selection-change="selectionChange"
      @on-load="getDataList"
    >
      <template slot="menuLeft">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          :loading="dataListLoading"
          v-if="isAuth('sys:user:root')"
          @click.stop="addkinHandle('all',1)"
          >初始化K线图</el-button
        >
        <!-- <el-button
          type="primary"
          icon="el-icon-plus"
          size="small"
          @click.stop="addOrUpdateHandle(arr)"
          >添加交易对</el-button
        > -->
        <!-- <el-button
          type="primary"
          
          size="small"
          @click.stop="linKinHand(arr)"
          >行情K线设置</el-button
        > -->
      </template>
      <template slot="ndhSearch">
        <avue-select
          v-model="options.id"
          placeholder="请选择语言"
          :dic="options"
        ></avue-select>
      </template>
      <template slot-scope="scope" slot="shezhi">
        <span
          class="seachButton"
          v-if="isAuth('hk-spots:operate')"
          @click.stop="mixmarkHand(scope.row)"
          >设置</span
        >
      </template>
      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('hk-spots:operate')"
          @click.stop="addOrUpdateHandle(arr, scope.row)"
          >编辑</el-button
        >
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          :loading="dataListLoading"
          v-if="isAuth('sys:user:root')"
          @click.stop="addkinHandle(scope.row.symbol)"
          >初始化K线图</el-button
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
import { tableOption } from "@/crud/cryptos-spots/cryptos-variety";
import AddOrUpdate from "./market-add-or-update";
export default {
  data() {
    return {
      dataForm: {},
      dataList: [],
      options: [],
      optionsTwo: [],
      arr: [],
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      mixmarketVisible: false,
      linKinVisible: false,
      tableOption: tableOption,
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
      searchParams: {}, // 搜索条件
    };
  },
  components: {
    AddOrUpdate,
  },
  created() {
    this.getLanguage();
    this.getCmsModel();
    this.getHkStocks();
  },
  methods: {
    // 获取数据列表
    getDataList(page, done) {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/normal/adminItemAction!/list"),
        method: "get",
        params: this.$http.adornParams(
          Object.assign(
            {
              type: "HK-stocks", //forex->外汇,commodities->大宗商品，指数/ETF->indices, A-stocks->A股, HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币
              current: page == null ? this.page.currentPage : page.currentPage,
              size: page == null ? this.page.pageSize : page.pageSize,
            },
            this.searchParams
          )
        ),
      }).then(({ data }) => {
        if (data.code == 0) {
          this.dataList = data.data.records;
          this.page.total = data.data.total;
          this.dataListLoading = false;
        } else {
          this.$message({
            message: data.msg,
            type: "error",
          });
        }
        if (done) {
          done();
        }
      });
    },
    getLanguage(params, done) {
      // 获取语言
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/news/getLanguage"),
        method: "get",
        params: this.$http.adornParams(Object.assign({}, params), false),
      }).then(({ data }) => {
        if (data.code == 0) {
          let keys = Object.keys(data.data);
          let value = Object.values(data.data);
          for (let i = 0; i < keys.length; i++) {
            let obj = {};
            obj.label = value[i];
            obj.value = keys[i];
            this.options.push(obj);
          }
          this.dataListLoading = false;
        }
        if (done) {
          done();
        }
      });
    },
    //获取交易对
    getHkStocks() {
      this.$http({
        url: this.$http.adornUrl("/api/item!list.action?type=HK-stocks"),
        method: "get",
        params: this.$http.adornParams({}),
      }).then(({ data }) => {
        this.arr = data.data.map((item, index) => {
          return Object.assign({}, { symbol: item.symbol, name: item.name });
        });
      });
    },
    getCmsModel(params, done) {
      // 获取模块
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/cms/getCmsModel"),
        method: "get",
        params: this.$http.adornParams(Object.assign({}, params), false),
      }).then(({ data }) => {
        if (data.code == 0) {
          let keys = Object.keys(data.data);
          let value = Object.values(data.data);
          for (let i = 0; i < keys.length; i++) {
            let obj = {};
            obj.label = value[i];
            obj.value = keys[i];
            this.optionsTwo.push(obj);
          }
          this.dataListLoading = false;
        }
        if (done) {
          done();
        }
      });
    },
    addkinHandle(symbol,n) {
      let daType = {};
      if (n == 1) {
        daType = {
          paraInitSymbol: symbol,
          symbolType: "HK-stocks", //forex->外汇,commodities->大宗商品，指数/ETF->indices, A-stocks->A股, HK-stocks->港股.US-stocks->美股，cryptos->数字货币
        };
      } else {
        daType = {
          paraInitSymbol: symbol,
        };
      }
      //初始化k线
      this.dataListLoading = true;
      this.$confirm(`确定初始化K线?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          //
          this.$http({
            url: this.$http.adornUrl(
              "/normal/adminItemAction!/klineInit.action"
            ),
            method: "get",
            params: this.$http.adornParams(Object.assign(daType), false),
          }).then(({ data }) => {
            if (data.code == 0) {
              this.dataListLoading = false;
              this.$message({
                message: data.data,
                type: "success",
                duration: 1500,
                onClose: () => {
                  // this.getDataList(this.page);
                },
              });
            }else{
              this.dataListLoading = false;
              this.$message({
                message: data.msg,
                type: "error",
                duration: 1500,
                onClose: () => {
                  // this.getDataList(this.page);
                },
              });
            }
            //

            //
            if (done) {
              done();
            }
          });
        })
        .catch(() => {this.dataListLoading = false;});
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
    addOrUpdateHandle(arr, row) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(arr, row);
      });
    },
    //设置  linKin
    mixmarkHand(row) {
      this.mixmarketVisible = true;
      this.$nextTick(() => {
        this.$refs.mixmarket.init(row);
      });
    },
    //linKin
    linKinHand(arr) {
      this.linKinVisible = true;
      this.$nextTick(() => {
        this.$refs.linKin.init(arr);
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
            if (data.code == 0) {
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.getDataList();
                },
              });
            } else {
              this.$message({
                message: data.msg,
                type: "error",
                duration: 1500,
                onClose: () => {},
              });
            }
          });
        })
        .catch(() => {});
    },
    addCellClass({ row, column }) {
      // console.log("row = " + JSON.stringify(row))
      // console.log("column = " + JSON.stringify(column))
      if (row.outOrIn == "out") {
        row.outOrIn = "汇出";
      } else if (row.outOrIn == "in") {
        row.outOrIn = "汇入";
      }
    },
  },
};
</script>
<style scoped>
.seachButton {
  cursor: pointer;
  color: rgb(69, 147, 235);
}
.seachButton:hover {
  color: rgb(8, 63, 134);
}
</style>
