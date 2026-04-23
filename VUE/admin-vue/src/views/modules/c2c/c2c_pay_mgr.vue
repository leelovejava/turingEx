<template>
  <div class="mod-c2c_pay_mgr">
    <avue-crud
      ref="crud"
      :page.sync="page"
      :data="dataList"
      :option="tableOption"
      @search-change="searchChange"
      @selection-change="selectionChange"
      @on-load="getDataList"
    >
      <template slot="menuLeft">
        <el-button
          type="primary"
          icon="el-icon-plus"
          size="small"
          v-if="isAuth('c2c:c2c_pay_mgr:add')"
          @click.stop="addOrUpdateHandle()"
          >新增支付方式</el-button
        >
      </template>
      <template slot="ndhSearch">
        <avue-select
          v-model="options.id"
          placeholder="请选择支付方式"
          :dic="options"
        ></avue-select>
      </template>
      <template slot-scope="scope" slot="methodImg">
        <img :src="scope.row.methodImgUrl" alt="" width="100"  />
      </template>
      <template slot-scope="scope" slot="qrcode">
        <img :src="scope.row.qrcodeImg" alt="" />
      </template>
      <template slot-scope="scope" slot="npOne">
        <span>{{ scope.row.paramName1 + ":" + scope.row.paramValue1 }}</span>
      </template>
      <template slot-scope="scope" slot="npTwo">
        <span v-if="scope.row.paramName2">{{
          scope.row.paramName2 +
          ":" +
          (scope.row.paramValue2 ? scope.row.paramValue2 : "")
        }}</span>
      </template>
      <template slot-scope="scope" slot="npThree">
        <span v-if="scope.row.paramName3">{{
          scope.row.paramName3 +
          ":" +
          (scope.row.paramValue3 ? scope.row.paramValue3 : "")
        }}</span>
      </template>
      <template slot-scope="scope" slot="moreNp">
        <span
          v-if="isAuth('c2c:c2c_pay_mgr:search')" 
          class="speacButton" 
          @click="moreNp(scope.row)">点击查看</span>
      </template>
      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('c2c:c2c_pay_mgr:edit')"
          @click.stop="addOrUpdateHandle(scope.row.uuid)"
          >编辑</el-button
        >
        <el-button
          type="danger"
          icon="el-icon-delete"
          size="small"
          v-if="isAuth('c2c:c2c_pay_mgr:delete')"
          @click.stop="deleteHandle(scope.row.uuid)"
          >删除</el-button
        >
      </template>
    </avue-crud>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdate"
      @refreshDataList="getDataList"
    ></add-or-update>
    <!-- 查看更多 -->
    <ctcmgr-reach-date
      v-if="ctcmgrReachDate"
      ref="addOrUpdateTwo"
      @refreshDataListTwo="getDataList"
    ></ctcmgr-reach-date>
    <!-- 删除 -->
    <ctc-delet-update
      v-if="addOrUpdateVisibleThree"
      ref="addOrUpdateThree"
      @refreshDataListThree="getDataList"
    ></ctc-delet-update>
  </div>
</template>
<script>
import { tableOption } from "@/crud/order/c2c_pay_mgr";
import AddOrUpdate from "./ctcmgr-add-or-update";
import CtcmgrReachDate from "./ctcmgr-reach-date";
import CtcDeletUpdate from "./ctcmgr-delet-update";
export default {
  data() {
    return {
      options: [],
      dataList: [],
      dataListLoading: false,
      ctcmgrReachDate: false,
      addOrUpdateVisibleThree: false,
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
    CtcmgrReachDate,
    CtcDeletUpdate,
  },
  created() {
    this.getC2cPaymentMethodType();
  },
  methods: {
    // 获取数据列表
    getDataList(page, params, done) {
      this.dataListLoading = true;
      let methodType = "";
      if (this.options.id) {
        methodType = this.options.id;
      } else {
        methodType = "";
      }
      this.$http({
        url: this.$http.adornUrl("/paymentMethod/list"),
        method: "post",
        data: this.$http.adornData(
          Object.assign(
            {
              methodType: methodType,
              type:2,// 1.银行卡  2.c2c
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
        if (data.code == 0) {
          data.data.forEach((item, index) => {
            data.data[index].label = item.name; //把后端请求过来的名字份放在label里
            data.data[index].value = item.id; //把后端请求过来的值放在value里
          });
          this.options = data.data;
        }
        if (done) {
          done();
        }
      });
    },
    //查看更多参数
    moreNp(row) {
      this.ctcmgrReachDate = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdateTwo.init(row);
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
    deleteHandle(id) {
      this.addOrUpdateVisibleThree = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdateThree.init(id);
      });
    },
  },
};
</script>
<style lang="scss" scoped>
.mod-mange {
}
.speacButton {
  color: rgb(69, 147, 235);
  cursor: pointer;
}
.speacButton:hover {
  color: rgb(8, 63, 134);
}
</style>
