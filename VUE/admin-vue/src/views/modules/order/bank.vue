<template>
  <div class="mod-bank">
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
          v-if="isAuth('order:bank:add')"
          @click.stop="addOrUpdateHandle('', '', optionsTwo)"
          >新增支付方式模板</el-button
        >
        <el-button
          type="primary"
          icon="el-icon-plus"
          size="small"
          v-if="isAuth('order:bank:config')"
          @click.stop="addLangugUpdate()"
          >支付方式类型 多语言配置</el-button
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
        <img :src="scope.row.methodImgUrl" alt="" />
      </template>
      <template slot-scope="scope" slot="menu">
        <el-select
          v-model="scope.row.select"
          class="celectSpeac"
          clearable
          v-if="isAuth('order:bank:operate')"
          placeholder="操作"
          @change="
            changeSelet(
              scope.row,
              scope.row.uuid,
              optionsTwo,
              scope.row.select,
              scope.row.methodTypeName
            )
          "
        >
          <el-option
            v-for="item in retnOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </template>
    </avue-crud>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdate"
      @refreshDataList="getDataList"
    ></add-or-update>
    <!-- 多语言配置 -->
    <cons-lang-update
      v-if="addOrUpdateVisibleTwo"
      ref="addOrUpdateTwo"
      @refreshDataListTwo="getDataList"
    ></cons-lang-update>
    <!-- 删除 -->
    <ctc-delet-update
      v-if="addOrUpdateVisibleThree"
      ref="addOrUpdateThree"
      @refreshDataListThree="getDataList"
    ></ctc-delet-update>
  </div>
</template>
<script>
import { tableOption } from "@/crud/order/cTwoc";
import AddOrUpdate from "./bankMore-add-or-update";
import ConsLangUpdate from "./cons-lang-update";
import CtcDeletUpdate from "./ctc-delet-update";
export default {
  data() {
    return {
      options: [],
      optionsTwo: [],
      dataList: [],
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      addOrUpdateVisibleTwo: false,
      addOrUpdateVisibleThree: false,
      tableOption: tableOption,
      searchParams: {}, // 搜索条件
      retnOptions: [
        {
          value: "1",
          label: "修改",
        },
        {
          value: "2",
          label: "删除",
        },
        {
          value: "3",
          label: "多语言设置",
        },
      ],
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
    };
  },
  components: {
    AddOrUpdate,
    ConsLangUpdate,
    CtcDeletUpdate,
  },
  created() {
    this.getC2cPaymentMethodType();
  },
  methods: {
    // 获取数据列表
    getDataList(page, done) {
      const params = {
        current: page == null ? this.page.currentPage : page.currentPage,
        size: page == null ? this.page.pageSize : page.pageSize,
        ...this.searchParams,
      };
      this.dataListLoading = true;
      let methodType = "";
      if (this.options.id) {
        console.log("ssss" + this.options.id);
        methodType = this.options.id;
      } else {
        methodType = "";
      }
      this.$http({
        url: this.$http.adornUrl("/paymentMethodConfig/list"),
        method: "post",
        data: this.$http.adornData(
          Object.assign(
            {
              type:1,// 1.银行卡  2.c2c
              methodType: methodType,
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
    // 获取支付方式数据列表
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
          //this.options.id = this.options[0].id
          this.optionsTwo = data.data;
        }
        if (done) {
          done();
        }
      });
    },
    changehot() {
      this.options.id = "";
    },
    // 条件查询
    searchChange(params, done) {
      this.page.currentPage = 1; // 重置当前页为第一页
      this.searchParams = params;
      this.getDataList(this.page, done);
    },
    // 多选变化
    selectionChange(val) {
      this.$forceUpdate();
      this.dataListSelections = val;
    },
    // 新增 / 修改支付方式模板
    addOrUpdateHandle(row, id, arr) {
      this.addOrUpdateVisible = true;
      this.addOrUpdateVisibleTwo = false;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(row, id, arr);
      });
    },
    // 新增 /修改多语言
    addLangugUpdate(id, methodTypeName) {
      this.addOrUpdateVisibleTwo = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdateTwo.initTwo(id, methodTypeName);
      });
    },
    deletmees(id) {
      this.addOrUpdateVisibleThree = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdateThree.init(id);
      });
    },
    changeSelet(row, id, arr, val, methodTypeName) {
      this.$forceUpdate();
      if (val == 1) {
        // 修改
        console.log(id);
        this.addOrUpdateHandle(row, id, arr);
      } else if (val == 3) {
        this.addLangugUpdate(id, methodTypeName);
      } else if (val == 2) {
        this.deletmees(id);
      }
      row.select = "";
    },
    // 删除
    deleteHandle(id) {
      var userIds = id
        ? [id]
        : this.dataListSelections.map((item) => {
            return item.userId;
          });
      this.$confirm(
        `确定对[id=${userIds.join(",")}]进行[${id ? "删除" : "批量删除"}]操作?`,
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      )
        .then(() => {
          this.$http({
            url: this.$http.adornUrl("/paymentMethodConfig/delete"),
            method: "delete",
            data: this.$http.adornData(userIds, false),
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
<style lang="scss" scoped>
/* 以下是下拉框操作按钮样式 */
::v-deep .celectSpeac .el-input__inner {
  background: #1c4efa !important;
}
::v-deep .celectSpeac .el-input__inner::placeholder {
  color: #fff;
}
.mod-mange {
}
</style>
