<template>
  <el-dialog
    :title="'交易杠杆'"
    :close-on-click-modal="false"
    :visible.sync="visible"
  >
    <div class="mod-subscribe-general">
      <avue-crud
        ref="crud"
        :page.sync="page"
      :data="dataList"
      :option="tableOption"
      @search-change="searchChange"
      @on-load="getDataList"
      >
        <template slot="menuLeft">
          <el-button
            type="primary"
            icon="el-icon-plus"
            size="small"
            v-if="isAuth('sys:user:save')"
            @click.stop="addOrUpdateHandle(options)"
            >新增</el-button
          >
        </template>
        <template slot-scope="scope" slot="menu">
          <el-button
            type="danger"
            icon="el-icon-delete"
            size="small"
            v-if="isAuth('sys:user:delete')"
            @click.stop="deleteHandle(scope.row)"
            >删除</el-button
          >
        </template>
      </avue-crud>
      <!-- 弹窗, 新增 / 修改 -->
    </div>

    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
    <!-- 确认弹窗-end -->
  </el-dialog>
</template>

<script>
import { tableOption } from "@/crud/subscribe/lever";
import { treeDataTranslate } from "@/utils";
import { Debounce } from "@/utils/debounce";
import { encrypt } from "@/utils/crypto";
export default {
  data() {
    return {
      stock: "",
      visible: false,
      dialogFormVisible: true,
      formLabelWidth: "120px",
      type: "", //type:'forex', //forex->外汇,commodities->大宗商品，指数/ETF->indices, A-stocks->A股, HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币
      menuList: [],
      menuListTreeProps: {
        label: "name",
        children: "children",
      },
      options: [],
      optionsTwo: [
        {
          label: "机器人刷单",
          value: 1,
        },
        {
          label: "第三方数据采集",
          value: 2,
        },
      ],
      row: "",
      dataForm: {
        id: "",
      },
      dataRule: {},
      searchParams: {}, // 搜索条件
      tableOption: tableOption,
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
      dataList: [],
    };
  },
  methods: {
    init(row) {
      this.row = row || "";
      // console.log("stocks => " + stocks)
      if (row) {
        this.dataForm.id = row.uuid;
      } else {
      }
      this.getDataList(this.page)
      this.visible = true;
      // this.dialogFormVisible = false
    },
    // 获取数据列表
    getDataList(page, done) {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/normal/adminItemLeverageAction!/list"),
        method: "get",
        params: this.$http.adornParams(
          Object.assign(
            {
              type: "commodities", //type:'forex', //forex->外汇,commodities->大宗商品，指数/ETF->indices, A-stocks->A股, HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币
              itemId: this.dataForm.id,
              current: page == null ? this.page.currentPage : page.currentPage,
              size: page == null ? this.page.pageSize : page.pageSize,
            },
            this.searchParams
          )
        ),
      }).then(({ data }) => {
        if (data.code == 0) {
          console.log("data => " + data);
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
        // 条件查询
        searchChange(params, done) {
      this.page.currentPage = 1; // 重置当前页为第一页
      this.searchParams = params;
      this.getDataList(this.page, done);
    },
    // 新增 / 修改
    addOrUpdateHandle() {
      this.Add();
    },
    // 删除
    deleteHandle(row) {
      this.$confirm(`确定进行删除操作?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.$http({
            url: this.$http.adornUrl("/normal/adminItemLeverageAction!/delete"),
            method: "get",
            params: this.$http.adornParams(
              Object.assign({
                ids: row.uuid,
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
    Add(call) {
      this.$prompt("输入杠杆倍数", "新增杠杆", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
      })
        .then(({ value }) => {
          //
          this.$http({
            url: this.$http.adornUrl(
              "/normal/adminItemLeverageAction!/add.action"
            ),
            method: "post",
            data: this.$http.adornData(
              Object.assign({
                item_id: this.dataForm.id,
                lever_rate: value,
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
          //
          if (call) {
            call();
          }
        })
        .catch((e) => {
          console.log("error = " + e);
          this.$message({
            type: "info",
            message: "取消输入",
          });
        });
    },
  },
};
</script>

<style scoped>
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
.speaInputTwo {
  width: 250px;
}
.speaInputThtree {
  width: 120px;
}
.speaInputFive {
  width: 87px;
}
</style>
