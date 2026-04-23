<template>
  <el-dialog
    :title="'交易参数管理'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    width="1200px"
    @close="handClose"
  >
    <div class="mod-subscribe-general">
      <avue-crud
        ref="crud"
        :page.sync="page"
        :data="dataList"
        :option="tableOption"
        @search-change="searchChange"
        @selection-change="selectionChange"
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
        <template slot-scope="scope" slot="ndh">
          <span>{{
            scope.row.profitRatio + "~" + scope.row.profitRatioMax
          }}</span>
        </template>
        <template slot-scope="scope" slot="menu">
          <el-button
            type="primary"
            icon="el-icon-edit"
            size="small"
            v-if="isAuth('sys:user:update')"
            @click.stop="addOrUpdateHandle(options, scope.row)"
            >编辑</el-button
          >
          <el-button
            type="danger"
            icon="el-icon-delete"
            size="small"
            v-if="isAuth('sys:user:delete')"
            @click.stop="deleteHandle(scope.row.uuid)"
            >删除</el-button
          >
        </template>
      </avue-crud>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
    <!-- 弹窗, 新增 / 修改 -->
    <parameters
      v-if="parameters"
      ref="parameters"
      @refreshDataList="getDataList"
    ></parameters>
    <paramsDelet
      v-if="paramsDelet"
      ref="paramsDelet"
      @refreshDataList="getDataList"
    ></paramsDelet>
    <!-- 确认弹窗-end -->
  </el-dialog>
</template>

<script>
import parameters from "./parameters-add-or-update";
import paramsDelet from "./parameters-delet-update";
import { tableOptionData } from "@/crud/etf-spots/parameters";
import { treeDataTranslate } from "@/utils";
import { Debounce } from "@/utils/debounce";
import { encrypt } from "@/utils/crypto";
export default {
  data() {
    return {
      stock: "",
      visible: false,
      dialogFormVisible: true,
      paramsDelet: false,
      parameters: false,
      formLabelWidth: "120px",
      type: "", //type:'forex', //forex->外汇,commodities->大宗商品，指数/ETF->indices, A-stocks->A股, HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币
      menuList: [],
      options: [],
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
        symbol: "",
      },
      dataRule: {},
      tableOption: tableOptionData,
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
      dataList: [],
    };
  },
  created() {
    this.getSymbol();
  },
  components: {
    parameters,
    paramsDelet,
  },
  methods: {
    init(row) {
      this.row = row || "";
      this.tableOption = tableOptionData;
      // console.log("stocks => " + stocks)
      if (row) {
        this.dataForm.symbol = row.symbol;
        this.getDataList(this.page);
      } else {
      }
      this.visible = true;
      // this.dialogFormVisible = false
    },
    handClose() {
      this.tableOption = {};
      // this.$data.dataForm=JSON.parse(JSON.stringify(this.$options.data().dataForm))
      // this.$nextTick(() => {
      //     this.$refs['dataForm'].clearValidate() // 清除表单验证
      //   })
      // this.optionsTwo.value = ''
      // this.options.value = ''
    },
    changeVal(val) {
      this.$forceUpdate();
    },
    dataFormSubmit: Debounce(function () {}),
    // 条件查询
    searchChange(params, done) {
      this.getDataList(this.page, params, done);
    },
    // 获取项目种类列表
    getSymbol(page, params, done) {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/api/item!list.action"),
        method: "get",
        params: this.$http.adornParams(
          Object.assign(
            {
              type: "cryptos", //forex->外汇,commodities->大宗商品，指数/ETF->indices, A-stocks->A股, HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币
            },
            params
          ),
          false
        ),
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
    // 多选变化
    selectionChange(val) {
      this.dataListSelections = val;
    },
    handleAvatarSuccess(res, file) {
      this.dataForm.imageUrl = URL.createObjectURL(file.raw); //显示地址
      this.dataForm.imgUrl = res.data.path; //接口传递
      console.log(this.dataForm.imageUrl);
    },
    beforeAvatarUpload(file) {
      // const isJPG = file.type === 'image/jpeg';
      const isLt2M = file.size / 1024 / 1024 < 10;
      if (!isLt2M) {
        this.$message.error("上传图片大小不能超过 10MB!");
      }
      return isLt2M;
    },
    // 获取数据列表
    getDataList(page, params, done) {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl(
          "/normal/adminContractManageAction!/listPara.action"
        ),
        method: "get",
        params: this.$http.adornParams(
          Object.assign(
            {
              //type:this.type,//type:'forex', //forex->外汇,commodities->大宗商品，指数/ETF->indices, A-stocks->A股, HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币
              symbol: this.dataForm.symbol,
              current: page == null ? this.page.currentPage : page.currentPage,
              size: page == null ? this.page.pageSize : page.pageSize,
            },
            params
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
    // 新增 / 修改
    addOrUpdateHandle(arr, row) {
      this.parameters = true;
      this.$nextTick(() => {
        this.$refs.parameters.init(arr, row);
      });
    },
    // 删除
    deleteHandle(id) {
      this.paramsDelet = true;
      this.$nextTick(() => {
        this.$refs.paramsDelet.init(id);
      });
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
