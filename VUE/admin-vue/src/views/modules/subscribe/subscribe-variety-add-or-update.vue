<template>
  <el-dialog
    :title="row ? '修改ETF品种管理' : '新增ETF品种管理'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    @close="handClose"
    width="700px"
  >
    <el-form
      :model="dataForm"
      ref="dataForm"
      :rules="dataRule"
      @keyup.enter.native="dataFormSubmit()"
      label-width="120px"
      style="width: auto"
    >
      <el-form-item label="项目ID" prop="uuid" style="width: auto">
        <el-input
          v-model="dataForm.uuid"
          placeholder=""
          disabled
          style="width: 510px"
        ></el-input>
      </el-form-item>

      <el-form-item label="成分股名称" prop="relatedStockName">
        <el-select
          class="speaInputTwo"
          v-model="dataForm.relatedStockName"
          disabled
          placeholder="请选择"
          multiple
          @change="changeVal()"
        >
          <el-option
            v-for="item in stocks"
            :key="item.symbol"
            :label="item.name"
            :value="item.symbol"
          >
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="数据源类别">
        <el-select
          v-model="optionsTwo.value"
          placeholder="请选择"
          @change="changeVal()"
        >
          <el-option
            v-for="item in optionsTwo"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="代码" prop="symbol">
        <el-input
          v-model="dataForm.realtime.symbol"
          placeholder="代码"
          style="width: 510px"
        ></el-input>
      </el-form-item>

      <el-form-item label="项目名称" prop="transactionPairsSymbol">
        <el-input
          v-model="dataForm.transactionPairsSymbol"
          placeholder="项目名称"
          style="width: 510px"
        ></el-input>
      </el-form-item>

      <el-form-item label="交易对">
        <el-select
          class="speaInputTwo"
          v-model="dataForm.transactionPairsSymbol"
          placeholder="请选择"
          @change="changeVal()"
        >
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="初始价格">
        <el-input
          v-model="dataForm.realtime.open"
          disabled
          placeholder=""
          style="width: 510px"
        ></el-input>
      </el-form-item>

      <el-form-item label="当前价格">
        <el-input
          v-model="dataForm.realtime.close"
          disabled
          placeholder=""
          style="width: 510px"
        ></el-input>
      </el-form-item>

      <el-form :inline="true" label-width="120px">
        <el-form-item label="今开" prop="open" label-width="120px">
          <el-input
            class="speaInputThtree"
            v-model="dataForm.realtime.open"
            placeholder="今开"
            disabled
          ></el-input>
        </el-form-item>
        <el-form-item label="今收" prop="close" label-width="120px">
          <el-input
            class="speaInputThtree"
            v-model="dataForm.realtime.close"
            placeholder="今收"
            disabled
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="最高" prop="max" label-width="120px">
          <el-input
            class="speaInputThtree"
            v-model="dataForm.realtime.high"
            placeholder="最高"
            disabled
          ></el-input>
        </el-form-item>
        <el-form-item label="最低" prop="min" label-width="120px">
          <el-input
            class="speaInputThtree"
            v-model="dataForm.realtime.low"
            placeholder="最低"
            disabled
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="昨结" prop="amount" label-width="120px">
          <el-input
            class="speaInputThtree"
            v-model="dataForm.realtime.close"
            placeholder="昨结"
            disabled
          ></el-input>
        </el-form-item>
        <el-form-item label="成交量" prop="amount" label-width="120px">
          <el-input
            class="speaInputThtree"
            v-model="dataForm.realtime.amount"
            placeholder="成交量"
            disabled
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="持仓量" prop="amount" label-width="120px">
          <el-input
            class="speaInputThtree"
            v-model="dataForm.realtime.amount"
            placeholder="持仓量"
            disabled
          ></el-input>
        </el-form-item>
        <el-form-item label="日增仓" prop="amount" label-width="120px">
          <el-input
            class="speaInputThtree"
            v-model="dataForm.realtime.amount"
            placeholder="日增量"
            disabled
          ></el-input>
        </el-form-item>
      </el-form>

      <el-form-item label="状态" prop="status">
        <template>
          <el-radio-group v-model="dataForm.status" @change="changeFreeFee">
            <el-radio :label="0">上架</el-radio>
            <el-radio :label="1">下架</el-radio>
          </el-radio-group>
        </template>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
    <!-- 确认弹窗-end -->
  </el-dialog>
</template>

<script>
import { treeDataTranslate } from "@/utils";
import { Debounce } from "@/utils/debounce";
import { encrypt } from "@/utils/crypto";
export default {
  data() {
    return {
      visible: false,
      dialogFormVisible: true,
      formLabelWidth: "120px",
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
        idCode: "",
        projectName: "",
        issuePrice: "",
        currency: "usdt",
        expectedLaunchTime: "",
        subscriptionEndTime: "",
        subscriptionStartTime: "",
        publishTime: "",
        minQuantity: "",
        maxQuantity: "",
        whitePagerAddress: "",
        relatedStockName: [],
        realtime: {},
      },
      fixstork: [], //返回的相关股票品种
      stocks: [],
      dataRule: {
        relatedStockName: [
          { required: true, message: "成分股不能为空", trigger: "blur" },
        ],
        projectName: [{ required: true, message: "不能为空", trigger: "blur" }],
        turnover: [
          { required: true, message: "成交量不能为空", trigger: "blur" },
        ],
        initPrice: [
          { required: true, message: "初始化价格不能为空", trigger: "blur" },
        ],
        minProfitLoss: [
          {
            required: true,
            message: "最小变动单位的盈亏金额不能为空",
            trigger: "blur",
          },
        ],
        minUnit: [
          { required: true, message: "最小变动单位不能为空", trigger: "blur" },
        ],
        fee: [
          { required: true, message: "每张手续费不能为空", trigger: "blur" },
        ],
        amount: [
          { required: true, message: "每张金额不能为空", trigger: "blur" },
        ],
      },
    };
  },
  methods: {
    init(arr, row, stocks) {
      console.log("ro@w => " + JSON.stringify(row));
      this.options = arr || [];
      this.row = row || "";
      this.stocks = stocks || [];
      this.stock = this.stocks[0] || "";
      console.log(row);
      if (row) {
        this.dataForm.id = row.uuid;
        this.dataForm = { ...row } || {};
        if (!this.dataForm.realtime) {
          this.dataForm.realtime = {};
        }
        this.fixstork = row.relatedStockName.split(",");
        //反选相关股票品种
        let relatedStockName = stocks.filter((val) => {
          return this.fixstork.includes(val.name);
        });
        let arr = [];
        relatedStockName.map((res) => {
          arr.push(res.name);
        });
        this.dataForm.relatedStockName = arr;
        this.dataForm.add = false;

        this.optionsTwo.value = this.dataForm.dataType;

        this.getDesc(this.dataForm.uuid);
      } else {
        this.dataForm = {};
        this.dataForm.add = true;

        this.optionsTwo.value = this.optionsTwo[0].value;
        this.options.value = this.options[0].value;
      }

      this.visible = true;
    },
    handClose() {
      this.$data.dataForm = JSON.parse(
        JSON.stringify(this.$options.data().dataForm)
      );
      this.$nextTick(() => {
        // this.$refs['dataForm'].clearValidate() // 清除表单验证
        this.dataForm = {};
        this.dataForm.realtime = {};
      });
      this.optionsTwo.value = "";
      this.options.value = "";
    },
    changeVal(val) {
      this.$forceUpdate();
    },
    dataFormSubmit: Debounce(function () {
      if (this.row) {
        //更新修改
        this.UpdateProjectBreed(this.row);
      } else {
        //新增
        this.AddProjectBreed();
      }
    }),
    handleAvatarSuccess(res, file) {
      this.dataForm.imageUrl = URL.createObjectURL(file.raw); //显示地址
      this.dataForm.imgUrl = res.data.path; //接口传递
    },
    beforeAvatarUpload(file) {
      // const isJPG = file.type === 'image/jpeg';
      const isLt2M = file.size / 1024 / 1024 < 10;
      if (!isLt2M) {
        this.$message.error("上传图片大小不能超过 10MB!");
      }
      return isLt2M;
    },
    changeFreeFee(val) {},
    AddProjectBreed() {
      //start
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(`/projectVariety/add`),
            method: "post",
            data: this.$http.adornData({
              // 'id':this.dataForm.id,

              amount: parseFloat(this.dataForm.amount), //每张金额 number
              code: this.dataForm.code, //代码
              dataType: parseInt(this.dataForm.dataType), //数据源类别-1:机器人-2:第三方数据采集 int
              fee: parseFloat(this.dataForm.fee), //每张手续费 number
              initPrice: parseFloat(this.dataForm.initPrice), //初始化价格 number
              minProfitLoss: parseFloat(this.dataForm.minProfitLoss), //最小变动单位的盈亏金额 number
              minUnit: parseFloat(this.dataForm.minUnit), //最小变动单位
              projectName: this.dataForm.projectName, //项目名称
              relatedStockVarieties: [], //相关股票品种
              status: parseInt(this.dataForm.status), //状态-0下架-1上架 int
              transactionPairsSymbol: this.dataForm.transactionPairsSymbol, //交易对
              turnover: parseFloat(this.dataForm.turnover), //成交量 number
              projectTypeSymbol: this.options.value, //项目总类 1 全球ETF 2 能源ETF 3 黄金ETF 4.人工智能ETF
            }),
          }).then(({ data }) => {
            if (data.code == 0) {
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.dialogFormVisible = false;
                  this.visible = false;
                  this.$emit("refreshDataList");
                },
              });
            } else {
              this.$message({
                message: data.msg,
                type: "error",
                duration: 1500,
                onClose: () => {
                  this.dialogFormVisible = false;
                  this.visible = false;
                },
              });
            }
          });
        }
      });
      //end
    },
    UpdateProjectBreed(row) {
      //start
      this.$refs["dataForm"].validate((valid) => {
        //修改
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(`/projectVariety/update`),
            method: "post",
            data: this.$http.adornData({
              id: this.dataForm.uuid,
              relatedStockName: this.dataForm.relatedStockName, //成分股
              amount: this.dataForm.amount, //每张金额
              code: this.dataForm.code, //代码
              dataType: this.optionsTwo.value, //数据源类别-1:机器人-2:第三方数据采集
              fee: this.dataForm.fee, //每张手续费
              initPrice: this.dataForm.initPrice, //初始化价格
              minProfitLoss: this.dataForm.minProfitLoss, //最小变动单位的盈亏金额
              minUnit: this.dataForm.minUnit, //最小变动单位
              projectName: this.dataForm.projectName, //项目名称
              relatedStockVarieties: this.dataForm.relatedStockVarieties, //相关股票品种
              status: this.dataForm.status, //状态-0下架-1上架
              transactionPairsSymbol: this.dataForm.transactionPairsSymbol, //交易对
              turnover: this.dataForm.turnover, //成交量
              projectTypeSymbol: this.options.value, //项目总类 1 全球ETF 2 能源ETF 3 黄金ETF 4.人工智能ETF
            }),
          }).then(({ data }) => {
            if (data.code == 0) {
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.dialogFormVisible = false;
                  this.visible = false;
                  this.$emit("refreshDataList");
                },
              });
            } else {
              this.$message({
                message: data.msg,
                type: "error",
                duration: 1500,
                onClose: () => {
                  this.dialogFormVisible = false;
                  this.visible = false;
                },
              });
            }
          });
        }
      });
      //end
    },
    //获取详情
    getDesc(uuid) {
      this.$http({
        url: this.$http.adornUrl("/projectBreed/getDesc"),
        method: "post",
        data: this.$http.adornData({
          id: uuid,
        }),
      }).then(({ data }) => {});
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

.frame {
  width: 700px;
}

.customWidth1 {
  position: relative;
  margin: 0 auto 50px;
  border-radius: 2px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
  box-sizing: border-box;
  width: 60%;
}
/* 
  ::deep .customWidth {
     width: 45%;
  }  */
</style>
