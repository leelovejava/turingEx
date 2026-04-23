<template>
  <el-dialog
    :title="!row ? '新增ETF总类管理' : '修改ETF总类管理'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    width="1200px"
    @close="handClose"
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="180px"
    >
      <el-row>
        <el-col :span="12">
          <el-form-item label="项目ID" prop="uuid">
            <el-input
              class="speaInputTwo"
              v-model="dataForm.uuid"
              placeholder="项目ID"
              disabled
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="项目名称" prop="projectName">
            <el-input
              class="speaInputTwo"
              v-model="dataForm.projectName"
              placeholder="项目名称"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="项目总类">
            <el-select
              class="speaInputTwo"
              v-model="dataForm.projectTypeSymbol"
              placeholder="请选择"
              @change="changeValOne"
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
        </el-col>
        <el-col :span="12">
          <el-form-item label="成交量" prop="turnover">
            <el-input
              class="speaInputTwo"
              v-model="dataForm.turnover"
              placeholder="成交量"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="相关股票品种">
            <el-select
              class="speaInputTwo"
              v-model="dataForm.relatedStockVarieties"
              placeholder="请选择"
              multiple
              @change="changeVal()"
            >
              <el-option
                v-for="item in stocks"
                :key="item.symbol"
                :label="item.symbol"
                :value="item.symbol"
              >
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="初始价格" prop="initPrice">
            <el-input
              class="speaInputTwo"
              v-model="dataForm.initPrice"
              placeholder="请输入初始价格"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="数据源类别">
            <el-select
              class="speaInputTwo"
              v-model="dataForm.dataType"
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
        </el-col>
        <el-col :span="12">
          <el-form-item label="当前价格" prop="idCode">
            <el-input
              class="speaInputTwo"
              v-model="dataForm.idCode"
              placeholder="当前价格"
              disabled
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="代码" prop="code">
            <el-input
              class="speaInputTwo"
              v-model="dataForm.code"
              disabled
              placeholder="代码"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-col :span="12">
            <el-form-item label="今开" prop="open" label-width="60px">
              <el-input
                class="speaInputThtree"
                v-model="dataForm.open"
                placeholder="今开"
                disabled
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="今收" prop="close" label-width="60px">
              <el-input
                class="speaInputThtree"
                v-model="dataForm.close"
                placeholder="今收"
                disabled
              ></el-input>
            </el-form-item>
          </el-col>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="交易对">
            <el-input
              class="speaInputTwo"
              v-model="dataForm.transactionPairsSymbol"
              disabled
              placeholder="交易对"
            ></el-input>
            <!-- <el-select
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
            </el-select> -->
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-col :span="12">
            <el-form-item label="最高" prop="max" label-width="60px">
              <el-input
                class="speaInputThtree"
                v-model="dataForm.max"
                placeholder="最高"
                disabled
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最低" prop="min" label-width="60px">
              <el-input
                class="speaInputThtree"
                v-model="dataForm.min"
                placeholder="最低"
                disabled
              ></el-input>
            </el-form-item>
          </el-col>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="每张手续费" prop="fee">
            <el-input
              class="speaInputTwo"
              v-model="dataForm.fee"
              placeholder="每张手续费"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-col :span="12">
            <el-form-item label="昨结" prop="idCode" label-width="60px">
              <el-input
                class="speaInputThtree"
                v-model="dataForm.idCode"
                placeholder="昨结"
                disabled
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="成交量" prop="idCode" label-width="60px">
              <el-input
                class="speaInputThtree"
                v-model="dataForm.idCode"
                placeholder="成交量"
                disabled
              ></el-input>
            </el-form-item>
          </el-col>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="每张金额" prop="amount">
            <el-input
              class="speaInputTwo"
              v-model="dataForm.amount"
              placeholder="每张金额"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-col :span="12">
            <el-form-item label="持仓量" prop="idCode" label-width="60px">
              <el-input
                class="speaInputThtree"
                v-model="dataForm.idCode"
                placeholder="持仓量"
                disabled
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="日增仓" prop="idCode" label-width="60px">
              <el-input
                class="speaInputThtree"
                v-model="dataForm.idCode"
                placeholder="日增量"
                disabled
              ></el-input>
            </el-form-item>
          </el-col>
        </el-col>
      </el-row>
      <el-row>
        <el-form-item label="最小变动单位" prop="minUnit">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.minUnit"
            placeholder="最小变动单位"
          ></el-input>
        </el-form-item>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item
            label="最小变动单位的盈亏金额"
            prop="minProfitLoss"
            label-width="180px"
          >
            <el-input
              class="speaInputTwo"
              v-model="dataForm.minProfitLoss"
              placeholder="最小变动单位的盈亏金额"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <template>
              <el-radio-group v-model="dataForm.status" @change="changeFreeFee">
                <el-radio :label="1">上架</el-radio>
                <el-radio :label="0">下架</el-radio>
              </el-radio-group>
            </template>
          </el-form-item>
        </el-col>
      </el-row>
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
      stock: "",
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
        status: 1,
        fee: "",
        amount: "",
        publishTime: "",
        minQuantity: "",
        maxQuantity: "",
        whitePagerAddress: "",
        relatedStockVarieties: [],
      },
      fixstork: [], //返回的相关股票品种
      stocks: [],
      dataRule: {
        projectName: [
          { required: true, message: "申购项目名称不能为空", trigger: "blur" },
        ],
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
  mounted() {
    this.options.forEach((option) => {
      if (option.label === this.name) {
        this.selectedValues.push(option.value);
      }
    });
  },
  methods: {
    init(arr, row, stocks) {
      this.options = arr || [];
      this.row = row || "";
      this.stocks = stocks || [];
      this.stock = this.stocks[0] || "";
      if (row != null) {
        this.dataForm = { ...row }; //深拷贝不影响表单赋值
        this.dataForm.relatedStockVarieties = row.relatedStockVarieties.split(",");
        this.dataForm.code = row.projectTypeSymbol
        this.dataForm.transactionPairsSymbol = row.projectTypeSymbol
        //console.log(this.fixstork)
        // //反选相关股票品种
        // let relatedStockVarieties = stocks.filter((val) => {
        //   return this.fixstork.includes(val.name);
        // });
        // let arr = [];
        // relatedStockVarieties.map((res) => {
        //   arr.push(res.symbol);
        // });
        // this.dataForm.relatedStockVarieties = arr;
        this.dataForm.add = false;
        //this.getDesc(this.dataForm.uuid);
      } else {
        this.dataForm = {};
        this.dataForm.add = true;
        this.dataForm.dataType = this.optionsTwo[0].value;
        this.dataForm.projectTypeSymbol = this.options[0].value;
        this.dataForm.code = this.options[0].value
        this.dataForm.transactionPairsSymbol = this.options[0].value
      }
      this.visible = true;
    },

    handClose() {
      this.$data.dataForm = JSON.parse(
        JSON.stringify(this.$options.data().dataForm)
      );
      this.$nextTick(() => {
        this.$refs["dataForm"].clearValidate(); // 清除表单验证
      });
      this.optionsTwo.value = "";
      this.options.value = "";
    },
    changeVal(val) {
      this.$forceUpdate();
    },
    changeValOne(e){
      this.$forceUpdate();
      this.dataForm.code = e
      this.dataForm.transactionPairsSymbol = e
      console.log(e)
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
      console.log(file);
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
    changeFreeFee(val) {},
    AddProjectBreed() {
      console.log(this.dataForm.transactionPairsSymbol + "111111111111");
      //start
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(`/projectBreed/add`),
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
              relatedStockVarieties: this.dataForm.relatedStockVarieties, //相关股票品种
              status: parseInt(this.dataForm.status), //状态-0下架-1上架 int
              transactionPairsSymbol: this.dataForm.transactionPairsSymbol, //交易对
              turnover: parseFloat(this.dataForm.turnover), //成交量 number
              projectTypeSymbol: this.dataForm.projectTypeSymbol, //项目总类 1 全球ETF 2 能源ETF 3 黄金ETF 4.人工智能ETF
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
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(`/projectBreed/update`),
            method: "post",
            data: this.$http.adornData({
              id: this.dataForm.uuid,
              amount: this.dataForm.amount, //每张金额
              code: this.dataForm.code, //代码
              dataType: this.dataForm.dataType, //数据源类别-1:机器人-2:第三方数据采集
              fee: this.dataForm.fee, //每张手续费
              initPrice: this.dataForm.initPrice, //初始化价格
              minProfitLoss: this.dataForm.minProfitLoss, //最小变动单位的盈亏金额
              minUnit: this.dataForm.minUnit, //最小变动单位
              projectName: this.dataForm.projectName, //项目名称
              relatedStockVarieties: this.dataForm.relatedStockVarieties, //相关股票品种
              status: this.dataForm.status, //状态-0下架-1上架
              transactionPairsSymbol: this.dataForm.transactionPairsSymbol, //交易对
              turnover: this.dataForm.turnover, //成交量
              projectTypeSymbol: this.dataForm.projectTypeSymbol, //项目总类 1 全球ETF 2 能源ETF 3 黄金ETF 4.人工智能ETF
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
      }).then(({ data }) => {
        //this.dataForm = data.data;
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

.customWidth {
  width: 70%;
}
</style>
