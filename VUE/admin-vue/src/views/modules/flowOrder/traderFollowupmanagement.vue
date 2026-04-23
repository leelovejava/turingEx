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
      </template>
      <template slot-scope="scope" slot="menu">

      </template>
    </avue-crud>
    <!-- 弹窗, 新增 / 修改 -->
    <!-- <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdate"
      @refreshDataList="getDataList"
    ></add-or-update> -->
  </div>
</template>

<script>
import { tableOption } from "@/crud/flowOrder/traderFollowupmanagement";
// import AddOrUpdate from "./traderManagement-add-or-update";
export default {
  data() {
    return {
      searchParams: {},
      dataForm: {},
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
    };
  },
  components: {
    //AddOrUpdate,
  },
  methods: {
    // 获取数据列表
    getDataList(page, done) {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/normal/adminTraderUser!list.action"),
        method: "get",
        params: this.$http.adornParams({
          current: this.page.currentPage,
          size: this.page.pageSize,
          ...this.searchParams,
        }),
      }).then(({ data }) => {
        console.log("data => " + JSON.stringify(data));
        this.dataList = data.data.page.records;
        this.page.total = data.data.pageSize;
        this.dataListLoading = false;
        if (done) {
          done();
        }
      });
    },
    // 条件查询
    searchChange(params, done) {
      this.page.currentPage = 1; // 重置当前页为第一页
      if (params["name"]) {
        params["name_para"] = params["name"];
      }
      this.searchParams = params;
      this.getDataList(this.page, done);
    },
    // 多选变化
    selectionChange(val) {
      this.dataListSelections = val;
    },

    addClasscolor({ column, row }) {
      //表单样式
      if (
        (column.property === "state" && row.state * 1 == 1) ||
        (column.property === "rolename" && row.rolename == "MEMBER")
      ) {
        return "green";
      } else if (column.property === "state" && row.state * 1 == 0) {
        return "red";
      } else if (column.property === "rolename" && row.rolename == "GUEST") {
        return "yellow";
      }
    },
  },
};
</script>
