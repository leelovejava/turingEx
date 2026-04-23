<template>
  <div class="mod-orde-rechange">
    <avue-crud
      ref="crud"
      :page.sync="page"
      :data="sortedDataList"
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
      <template slot-scope="scope" slot="img">
        <span  v-if="isAuth('order:rechange:mesage')" class="seachButton" @click="seachDress(scope.row)">查看信息</span>
        <!-- <img :src="scope.row.img" alt="" /> -->
      </template>
      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('order:rechange:manual') && scope.row.status == 0"
          @click.stop="
            manualRecharg(
              'n',
              scope.row.uuid,
              scope.row.coin,
              scope.row.channelAmount
            )
          "
          >手动到账</el-button
        >
        <el-button
          type="danger"
          icon="el-icon-delete"
          size="small"
          v-if="isAuth('order:rechange:turnDown') && scope.row.status == 0"
          @click.stop="manualRecharg('m', scope.row.uuid)"
          >驳回申请</el-button
        >
      </template>
    </avue-crud>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdate"
      @refreshDataList="getDataList"
    ></add-or-update>
    <consignment-info
      v-if="consignmentInfoVisible"
      ref="consignmentInfo"
    ></consignment-info>
  </div>
</template>

<script>
import AddOrUpdate from "./rechangeInfo";
import { tableOption } from "@/crud/shop/rechange";
import ConsignmentInfo from "./consignment-info-rechange";
export default {
  data() {
    return {
      addOrUpdateVisible: false,
      consignmentInfoVisible: false,
      dataList: [],
      dataListLoading: false,
      dataListSelections: [],
      tableOption: tableOption,
      searchParams: {}, // 搜索条件
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
      type: {},
      status: "",
      option: {
        column: [
          {
            label: "全部",
            prop: "",
          },
          {
            label: "未支付",
            prop: "0",
          },
          {
            label: "支付成功",
            prop: "1",
          },
          {
            label: "失败",
            prop: "2",
          },
        ],
      },
    };
  },
  components: {
    AddOrUpdate,
    ConsignmentInfo,
  },
  computed: {
    sortedDataList() {
      const dataListCopy = [...this.dataList];
      const statusZeroData = dataListCopy.filter((item) => item.status === 0);
      const otherData = dataListCopy.filter((item) => item.status !== 0);
      return [...statusZeroData, ...otherData];
    },
  },
  created() {
    this.type = this.option.column[0];
  },
  mounted() {
    this.$bus.$on("updateOfRechange", (data) => {
      this.getDataList();
    });
  },
  beforeDestroy() {
    // 组件被销毁了，不能进行数据传输
    // 解绑事件
    this.$bus.$off("updateOfRechange");
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
        url: this.$http.adornUrl("/rechargeOrder/list"),
        method: "post",
        data: this.$http.adornData(
          Object.assign(
            {
              status: this.status,
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
      if ((column.property === "direction" && row.direction == "buy")|| (column.property === "roleName" && row.roleName == 'MEMBER')) {
        return "green";
      } else if (column.property === "direction" && row.direction == "sell") {
        return "red";
      } else if((column.property === "roleName" && row.roleName == 'GUEST')){
        return "yellow";
      }
    },
    manualRecharg(type, id, coin, channelAmount) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(type, id, coin, channelAmount);
      });
    },
    seachDress(row) {
      this.consignmentInfoVisible = true;
      this.$nextTick(() => {
        this.$refs.consignmentInfo.init(row);
      });
    },
    // tab切换
    handleChange(column, params, done) {
      this.type = column;
      this.status = column.prop;
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
<style scoped>
.seachButton {
  cursor: pointer;
  color: rgb(69, 147, 235);
}
.seachButton:hover {
  color: rgb(8, 63, 134);
}
</style>
