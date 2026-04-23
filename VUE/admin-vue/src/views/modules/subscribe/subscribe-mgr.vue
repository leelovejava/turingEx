<template>
  <div class="mod-role">
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
          v-if="isAuth('subscribe:mgr:edit')"
          @click.stop="addOrUpdateHandle(options)"
          >新增</el-button
        >
        <avue-tabs :option="option2" @change="handleChange"></avue-tabs>
        <span v-if="type.prop === 'tab1'"></span>
        <span v-else-if="type.prop === 'tab2'"></span>
        <span v-else-if="type.prop === 'tab3'"></span>
        <!-- <el-button type="danger"
                   @click="deleteHandle()"
                   v-if="isAuth('sys:user:delete')"
                   size="small"
                   :disabled="dataListSelections.length <= 0">批量删除</el-button> -->
      </template>
      <!-- <template slot="ndhSearch">
        <avue-select
          v-model="options.value"
          placeholder="请选择"
          :dic="options"
        ></avue-select>
      </template> -->
      <template slot-scope="scope" slot="amountAfter">
        <span
          class="seachButton"
          v-if="isAuth('subscribe:mgr:edit')"
          @click="
            seachDress(scope.row.projectTypeSymbol, scope.row.projectTypeName)
          "
          >查看信息</span
        >
      </template>
      <template slot-scope="scope" slot="menu">
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('subscribe:mgr:edit')"
          @click.stop="addOrUpdateHandle(options, scope.row)"
          >编辑</el-button
        >
      </template>
    </avue-crud>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdate"
      @refreshDataList="getDataList"
    ></add-or-update>
    <add-other-update
      v-if="addOtherUpdateVisible"
      ref="addOtherUpdate"
      @refreshDataList="getDataList"
    ></add-other-update>
  </div>
</template>

<script>
import { tableOption } from "@/crud/subscribe/subscribe-mgr";
import AddOrUpdate from "./subscribe-mgr-add-or-update";
import AddOtherUpdate from "./subscribe-mixdate-update";
export default {
  data() {
    return {
      dataForm: {},
      dataList: [],
      options: [],
      type: {},
      state: "",
      option2: {
        column: [
          {
            label: "全部",
            prop: 0,
          },
          {
            label: "申购中",
            prop: 1,
          },
          {
            label: "已结束",
            prop: 2,
          },
        ],
      },
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      addOtherUpdateVisible: false,
      tableOption: tableOption,
      searchParams: {}, // 搜索条件
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
    };
  },
  components: {
    AddOrUpdate,
    AddOtherUpdate,
  },
  created() {
    this.getSymbol();
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
      // let projectTypeSymbol = ''
      // if(this.options.value){
      //   projectTypeSymbol = this.options.value
      // }else{
      //   projectTypeSymbol = ''
      // }
      this.$http({
        url: this.$http.adornUrl("/purchasing/list"),
        method: "post",
        data: this.$http.adornData(
          Object.assign(
            {
              status: this.state,
              //projectTypeSymbol:projectTypeSymbol,
              current: page == null ? this.page.currentPage : page.currentPage,
              size: page == null ? this.page.pageSize : page.pageSize,
            },
            params
          )
        ),
      }).then(({ data }) => {
        if (data.code == 0) {
          this.dataList = data.data.records;
          this.page.total = data.data.total;
        } else {
          this.$message({
            message: data.msg,
            type: "error",
            duration: 1000,
            onClose: () => {},
          });
        }

        this.dataListLoading = false;
        if (done) {
          done();
        }
      });
    },
    seachDress(symbol, name) {
      this.addOtherUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOtherUpdate.init(symbol, name);
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
    addOrUpdateHandle(arr, row) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(arr, row);
      });
    },
    // tab切换
    handleChange(column, params, done) {
      this.type = column;
      this.state = column.prop;
      this.getDataList(this.page, params, done);
    },
    // 获取项目种类列表
    getSymbol(page, params, done) {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/api/item!list.action?type=indices"),
        method: "get",
        params: this.$http.adornParams(Object.assign({}, params), false),
      }).then(({ data }) => {
        if (data.code == 0) {
          let arr = data.data;
          this.options = arr.map((item, index) => {
            return Object.assign({
              label: item.name,
              value: item.symbol,
            });
          });
        }
        if (done) {
          done();
        }
      });
    },
    // 删除
    deleteHandle(id) {
      this.$confirm(`确定进行[${id ? "删除" : "批量删除"}]操作?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.$http({
            url: this.$http.adornUrl("/purchasing/delete"),
            method: "post",
            data: this.$http.adornData(
              Object.assign({
                id: id,
              })
            ),
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
