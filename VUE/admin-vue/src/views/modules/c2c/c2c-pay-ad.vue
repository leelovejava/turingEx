<template>
  <div class="mod-role">
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
      <template slot="menuLeft">
        <el-button
          type="primary"
          icon="el-icon-plus"
          size="small"
          v-if="isAuth('c2c:c2c-pay-ad:add')"
          @click.stop="addOrUpdateHandle(currencyMap, symbolMap, expireTimeMap)"
          >新增</el-button
        >
      </template>
      <template slot-scope="scope" slot="img">
        <img :src="scope.row.head_img" alt="" width="50" />
      </template>
      <template slot="nnhSearch">
        <avue-select
          v-model="currencyMap.id"
          placeholder="请选择支付币种"
          :dic="currencyMap"
        ></avue-select>
      </template>
      <template slot="nmhSearch">
        <avue-select
          v-model="symbolMap.id"
          placeholder="请选择上架币种"
          :dic="symbolMap"
        ></avue-select>
      </template>
      <template slot-scope="scope" slot="pay_typeM">
        <div v-html="scope.row.pay_type"></div>
      </template>
      <template slot-scope="scope" slot="nth">
        <span v-if="isAuth('c2c:c2c-pay-ad:search')" 
          class="seachButton" @click="searchMsg(scope.row)">点击查看</span>
      </template>
      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('c2c:c2c-pay-ad:edit')" 
          @click.stop="
            addOrUpdateHandle(currencyMap, symbolMap, expireTimeMap, scope.row)
          "
          >编辑</el-button
        >
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('c2c:c2c-pay-ad:recede')" 
          @click.stop="removeHandle(scope.row.id)"
          >退还所有保证金</el-button
        >

      </template>
    </avue-crud>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdate"
      @refreshDataList="getDataList"
    ></add-or-update>
    <removeDeposit
      v-if="removeDepositVisible"
      ref="removeDepositmn"
      @refreshDataList="getDataList"
    ></removeDeposit>
    <moreMsg
      v-if="moreMsgtVisible"
      ref="moreMsgmn"
      @refreshDataList="getDataList"
    ></moreMsg>
  </div>
</template>

<script>
import { tableOption } from "@/crud/c2c/c2c-pay-ad";
import AddOrUpdate from "./c2c-pay-ad-add-or-update";
import removeDeposit from "./c2c-ad-remove-despoti";
import moreMsg from "./c2c-ad-more-msg";
export default {
  data() {
    return {
      dataForm: {},
      dataList: [],
      dataListLoading: false,
      removeDepositVisible: false,
      moreMsgtVisible: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      currencyMap: [], //支付币种
      symbolMap: [], //上架币种
      expireTimeMap: [], //支付时效
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
    removeDeposit,
    moreMsg,
  },
  created() {
    this.getSymbolMap(); //上架币种
    this.getCurrencyMap(); //支付币种
    this.getExpireTimeMap(); //支付时效
  },
  methods: {
    // 获取数据列表
    getDataList(page, params, done) {
      this.dataListLoading = true;
      let symbol = "";
      let currency = "";
      if (this.symbolMap.id) {
        symbol = this.symbolMap.id;
      } else {
        symbol = "";
      }
      if (this.currencyMap.id) {
        currency = this.currencyMap.id;
      } else {
        currency = "";
      }
      this.$http({
        url: this.$http.adornUrl("/c2cAdvert/list"),
        method: "post",
        data: this.$http.adornData(
          Object.assign(
            {
              symbol: symbol,
              currency: currency,
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
      if (
        (column.property === "direction" && row.direction == "buy") ||
        (column.property === "c2c_user_type" && row.c2c_user_type * 1 == 2) ||
        (column.property === "on_sale" && row.on_sale * 1 == 1)
      ) {
        return "green";
      } else if (
        (column.property === "direction" && row.direction == "sell") ||
        (column.property === "c2c_user_type" && row.c2c_user_type * 1 == 1) ||
        (column.property === "on_sale" && row.on_sale * 1 == 0)
      ) {
        return "red";
      } else {
        return "";
      }
    },
    getSymbolMap(params, done) {
      //所有上架币种
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/c2cAdvert/getSymbolMap"),
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
            this.symbolMap.push(obj);
          }
        }
        if (done) {
          done();
        }
      });
    },
    getCurrencyMap(params, done) {
      //所有支付币种
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/c2cAdvert/getCurrencyMap"),
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
            this.currencyMap.push(obj);
          }
        }
        if (done) {
          done();
        }
      });
    },
    getExpireTimeMap(params, done) {
      //支付时效
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/c2cAdvert/getExpireTimeMap"),
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
            this.expireTimeMap.push(obj);
          }
        }
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
    removeHandle(id) {
      console.log(id);
      this.removeDepositVisible = true;
      this.$nextTick(() => {
        this.$refs.removeDepositmn.init(id);
      });
    },
    // 新增 / 修改
    addOrUpdateHandle(currencyMap, symbolMap, expireTimeMap, row) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(currencyMap, symbolMap, expireTimeMap, row);
      });
    },
    searchMsg(row) {
      this.moreMsgtVisible = true;
      this.$nextTick(() => {
        this.$refs.moreMsgmn.init(row);
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
  },
};
</script>
