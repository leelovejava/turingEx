<template>
  <div class="mod-subscribe-general">
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
          v-if="isAuth('subscribe:general:operate')"
          @click.stop="addOrUpdateHandle(options)"
          >新增</el-button
        >
      </template>
      <template slot-scope="scope" slot="close">
        <span>{{ scope.row.realtimeDto.close }}</span>
      </template>
      <template slot-scope="scope" slot="open">
        <span>{{ scope.row.realtimeDto.open }}</span>
      </template>
      <template slot="ndhSearch">
        <avue-select
          v-model="options.value"
          placeholder="请选择"
          :dic="options"
        ></avue-select>
      </template>
      <template slot-scope="scope" slot="amountAfter">
        <span
          class="seachButton"
          v-if="isAuth('subscribe:general:operate')"
          @click="
            seachDress(scope.row.projectTypeSymbol, scope.row.projectTypeName)
          "
          >查看信息</span
        >
      </template>

      <template slot-scope="scope" slot="menu">
        <el-select
          v-model="scope.row.select"
          v-if="isAuth('subscribe:general:operate')"
          class="celectSpeac"
          clearable
          placeholder="操作"
          @change="changeSelet(scope.row)"
        >
          <el-option
            v-for="item in options1"
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
    <add-other-update
      v-if="addOtherUpdateVisible"
      ref="addOtherUpdate"
      @refreshDataList="getDataList"
    ></add-other-update>

    <lever
      v-if="leverVisible"
      ref="lever"
      @refreshDataList="getDataList"
    ></lever>
  </div>
</template>

<script>
import { tableOption } from "@/crud/subscribe/subscribe-general";
import AddOrUpdate from "./subscribe-general-add-or-update";
import AddOtherUpdate from "./subscribe-mixdate-update";
import lever from "./lever";
export default {
  data() {
    return {
      dataForm: {},
      dataList: [],
      options: [],
      type: {},
      state: "",
      options1: [
        {
          value: "1",
          label: "修改",
        },
        {
          value: "2",
          label: "交易杠杆",
        },
        {
          value: "3",
          label: "初始化K线",
        },
      ],
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
      searchParams: {}, // 搜索条件
      addOrUpdateVisible: false,
      addOtherUpdateVisible: false,
      leverVisible: false,
      tableOption: tableOption,
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
      stocks: [],
    };
  },
  components: {
    AddOrUpdate,
    AddOtherUpdate,
    lever,
  },
  created() {
    this.getSymbol();
    this.getUsStocks();
    this.getHkStocks();
    this.getAStocks();
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
      this.$http({
        url: this.$http.adornUrl("/projectBreed/list"),
        method: "post",
        data: this.$http.adornData(
          Object.assign(
            {
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
        this.$refs.addOrUpdate.init(arr, row, this.stocks);
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
    //行情数据
    getUsStocks() {
      this.$http({
        url: this.$http.adornUrl("/api/item!list.action?type=us-stocks"),
        method: "get",
        params: this.$http.adornParams({}),
      }).then(({ data }) => {
        // console.log("us-stocks data => " + JSON.stringify(data));
        this.UsStocks = data.data;
        this.stocks = this.stocks.concat(data.data);
      });
    },
    getHkStocks() {
      this.$http({
        url: this.$http.adornUrl("/api/item!list.action?type=hk-stocks"),
        method: "get",
        params: this.$http.adornParams({}),
      }).then(({ data }) => {
        // console.log("hk-stocks data => " + JSON.stringify(data));
        this.UsStocks = data.data;
        this.stocks = this.stocks.concat(data.data);
      });
    },
    getAStocks() {
      this.$http({
        url: this.$http.adornUrl("/api/item!list.action?type=A-stocks"),
        method: "get",
        params: this.$http.adornParams({}),
      }).then(({ data }) => {
        // console.log("A-stocks data => " + JSON.stringify(data));
        this.AStocks = data.data;
        this.stocks = this.stocks.concat(data.data);
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
    changeSelet(row) {
      console.log("row => " + JSON.stringify(row));
      switch (row.select) {
        case "1": //修改
          {
            this.addOrUpdateVisible = true;
            this.$nextTick(() => {
              this.$refs.addOrUpdate.init(this.options, row, this.stocks);
            });
          }
          break;
        case "2": //交易杠杆
          {
            this.leverVisible = true;
            this.$nextTick(() => {
              this.$refs.lever.init(row);
            });
          }
          break;
        case "3": //初始化K线
          {
            //
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
                  params: this.$http.adornParams(
                    Object.assign({
                      paraInitSymbol: row.code || "",
                    }),
                    false
                  ),
                }).then(({ data }) => {
                  if (data.code == 0) {
                    console.log("k初始化 =>" + JSON.stringify(data));
                    this.$message({
                    message: "操作成功",
                    type: "success",
                    duration: 1500,
                  });
                  }else{
                    this.$message({
                    message: data.msg,
                    type: "error",
                    duration: 1500,
                    onClose: () => {},
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
            //
          }
          break;
      }

      row.select = "";
    },
  },
};
</script>
<style scoped>
/* 以下是下拉框操作按钮样式 */
::v-deep .celectSpeac .el-input__inner {
  background: #1c4efa !important;
}
::v-deep .celectSpeac .el-input__inner::placeholder {
  color: #fff;
}
.seachButton {
  cursor: pointer;
  color: rgb(69, 147, 235);
}
.seachButton:hover {
  color: rgb(8, 63, 134);
}
</style>
