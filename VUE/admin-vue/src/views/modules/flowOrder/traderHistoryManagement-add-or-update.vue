<template>
  <el-dialog
    :title="!id ? '新增' : '修改'"
    :close-on-click-modal="false"
    width="1200px"
    @close="handClose"
    :visible.sync="visible"
  >
    <el-scrollbar class="vertical-scrollbar">
      <div class="scroll-content">
        <el-form
          :model="dataForm"
          :rules="dataRule"
          ref="dataForm"
          @keyup.enter.native="dataFormSubmit()"
          label-width="220px"
        >
          <el-form-item class="titleDivTwo" label="基础信息" prop="">
          </el-form-item>
          <el-row>
            <el-col :span="12">
              <el-form-item label="交易员UID" prop="usercode">
                <el-input
                  :disabled="dataForm.uuid ? true : false"
                  v-model="dataForm.usercode"
                  placeholder="交易员UID"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="购买品种" prop="symbol">
                <el-select
                  v-model="dataForm.symbol"
                  class="spainut"
                  placeholder="请选择"
                  @change="changeVal()"
                >
                  <el-option
                    v-for="item in option"
                    :key="item.symbol"
                    :label="item.name"
                    :value="item.symbol"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="操作类型" prop="direction">
                <el-select
                  v-model="dataForm.direction"
                  class="spainut"
                  placeholder="请选择"
                  @change="changeVal()"
                >
                  <el-option
                    v-for="item in direction"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="盈亏" prop="profit">
                <el-input
                  v-model="dataForm.profit"
                  placeholder="盈亏"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="涨跌幅" prop="changeRatio">
                <el-input
                  type="number"
                  v-model="dataForm.changeRatio"
                  placeholder="涨跌幅"
                >
                  <template v-slot:append>
                    <span>%</span>
                  </template></el-input
                >
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="委托数量(张)" prop="volumeOpen">
                <el-input
                  type="number"
                  v-model="dataForm.volumeOpen"
                  placeholder="委托数量(张)"
                >
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="杠杆倍数(整数)" prop="leverRate">
                <el-input
                  type="number"
                  v-model="dataForm.leverRate"
                  placeholder="杠杆倍数(整数)"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="状态" prop="state">
                <el-select
                  class="spainut"
                  v-model="dataForm.state"
                  placeholder="请选择"
                  @change="changeVal()"
                >
                  <el-option
                    v-for="item in state"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="开仓价格" prop="tradeAvgPrice">
                <el-input
                  type="number"
                  v-model="dataForm.tradeAvgPrice"
                  placeholder="开仓价格"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="平仓价格" prop="closeAvgPrice">
                <el-input
                  type="number"
                  v-model="dataForm.closeAvgPrice"
                  placeholder="平仓价格"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="下单时间" prop="createTime">
                <el-date-picker
                  v-model="dataForm.createTime"
                  type="datetime"
                  placeholder="选择日期"
                  value-format="yyyy-MM-dd HH:mm:ss"
                ></el-date-picker>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="平仓时间" prop="closeTime">
                <el-date-picker
                  v-model="dataForm.closeTime"
                  type="datetime"
                  placeholder="选择日期"
                  value-format="yyyy-MM-dd HH:mm:ss"
                ></el-date-picker>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
    </el-scrollbar>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>
<script>
import { Debounce } from "@/utils/debounce";
import { encrypt } from "@/utils/crypto";
export default {
  data() {
    return {
      visible: false,
      imageUrl: "",
      dataForm: {
        uuid: "",
        usercode: "",
        symbol: "",
        direction: "",
        profit: "",
        changeRatio: "",
        volumeOpen: "",
        leverRate: "",
        tradeAvgPrice: "",
        closeAvgPrice: "",
        createTime: "",
        closeTime: "",
      },
      id: "",
      option: [
        {
          symbol: 'btc',
          name: "DOGE/USTD",
        },
      ],
      state: [
        {
          label: "已提交(持仓)",
          value: "submitted ",
        },
        {
          label: "完成(持仓)",
          value: "created",
        },
      ],
      direction: [
        {
          label: "开多",
          value: "buy",
        },
        {
          label: "开空",
          value: "sell",
        },
      ],
      dataRule: {
        //{ validator: validateindex, trigger: "blur" },
        usercode: [{ required: true, message: "UID不能为空", trigger: "blur" }],
        username: [
          { required: true, message: "名称不能为空", trigger: "blur" },
        ],
        volume: [
          {
            required: true,
            message: "跟单张数或比例不能为空",
            trigger: "blur",
          },
        ],
        volumeMax: [
          { required: true, message: "最大持仓张数不能为空", trigger: "blur" },
        ],
        profit: [{ required: true, message: "盈亏不能为空", trigger: "blur" }],
        changeRatio: [
          { required: true, message: "涨跌幅不能为空", trigger: "blur" },
        ],
        volumeOpen: [
          { required: true, message: "委托数量不能为空", trigger: "blur" },
        ],
        leverRate: [
          { required: true, message: "杠杆倍数不能为空", trigger: "blur" },
        ],
        tradeAvgPrice: [
          { required: true, message: "开仓价格不能为空", trigger: "blur" },
        ],
        closeAvgPrice: [
          { required: true, message: "平仓价格不能为空", trigger: "blur" },
        ],
        createTime: [
          { required: true, message: "下单时间不能为空", trigger: "blur" },
        ],
        closeTime: [
          { required: true, message: "平仓时间不能为空", trigger: "blur" },
        ],
      },
      tempKey: -666666, // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
    };
  },
  methods: {
    init(id) {
      this.getAction()
      if (id) {
        this.id = id;
        this.getMassage();
      } else {
        this.dataForm.symbol = this.option[0].symbol;
        this.dataForm.state = this.state[0].value;
        this.dataForm.direction = this.direction[0].value;
      }
      if (this.dataForm.img) {
        // this.imageUrl =  this.dataForm.path
      }
      this.visible = true;
    },
    handleAvatarSuccess(res, file) {
      this.dataForm.img = res.data.path;
      this.imageUrl = URL.createObjectURL(file.raw);
    },
    beforeAvatarUpload(file) {
      // const isJPG = file.type === 'image/jpeg';
      const isLt2M = file.size / 1024 / 1024 < 10;

      // if (!isJPG) {
      //   this.$message.error('上传头像图片只能是 JPG 格式!');
      // }
      if (!isLt2M) {
        this.$message.error("上传图片大小不能超过 10MB!");
      }
      // return isJPG && isLt2M;
      return isLt2M;
    },
    handClose() {
      this.id = "";
      this.$data.dataForm = JSON.parse(
        JSON.stringify(this.$options.data().dataForm)
      );
      this.imageUrl = "";
      this.$nextTick(() => {
        this.$refs["dataForm"].clearValidate(); // 清除表单验证
      });
    },
    changeVal(val) {
      this.$forceUpdate();
    },

    dataFormSubmit: Debounce(function () {
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          let url = "";
          if (this.id) {
            //更新
            url = `/normal/adminTraderOrder!update.action`;
          } else {
            //新增
            url = `/normal/adminTraderOrder!add.action`;
          }
          // 添加需要提交的表单字段及其值到 FormData 对象
          this.$http({
            url: this.$http.adornUrl(url),
            method: "post",
            data: this.$http.adornData(this.dataForm),
          }).then(({ data }) => {
            if (data.code == 0) {
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.visible = false;
                  this.$emit("refreshDataList");
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
        }
      });
    }),
    //行情数据币种
    getAction() {
      this.$http({
        url: this.$http.adornUrl("/normal/adminItemAction!/list"),
        method: "get",
        params: this.$http.adornParams({
          type: "cryptos", //forex->外汇,commodities->大宗商品，指数/ETF->indices, A-stocks->A股, HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币
          current: 1,
          size: 1000000,
        }),
      }).then(({ data }) => {
        if (data.data.records) {
          this.option = data.data.records.map((item, index) => {
            return Object.assign({}, { symbol: item.symbol, name: item.name });
          });
        }
      });
    },
    getMassage() {
      this.$http({
        url: this.$http.adornUrl(`/normal/adminTraderOrder!toUpdate.action`),
        method: "get",
        params: this.$http.adornParams({
          uuid: this.id,
        }),
      }).then(({ data }) => {
        console.log("data => " + JSON.stringify(data));
        if (data.code == 0) {
          console.log("data => " + JSON.stringify(data));
          this.dataForm = { ...data.data };
          // this.dataForm.uuid = data.data.uuid;
          // this.dataForm.traderUsercode = data.data.traderUserCode;
          // // this.dataForm.userType=data.data.,
          // this.dataForm.usercode = data.data.userCode;
          // this.dataForm.followType = data.data.followType;
          // this.dataForm.username = data.data.username;
          // this.dataForm.volume = data.data.volume;
          // this.dataForm.volumeMax = data.data.volumeMax;
          // this.dataForm.profit = data.data.profit;
          // this.dataForm.amountSum = data.data.amountSum;
          // this.dataForm.stopProfit = data.data.stopProfit;
          // this.dataForm.stopLoss = data.data.stopLoss;
          //   this.imageUrl = this.dataForm.img;
          // console.log(this.imageUrl)
        } else {
          this.$message({
            message: data.msg,
            type: "error",
            duration: 1500,
            onClose: () => {},
          });
        }
      });
    },
    // 表单提交
    // dataFormSubmit: Debounce(function () {
    //   if (!this.dataForm.img) {
    //     this.$message.error("上传图片不能为空");
    //     return;
    //   }

    //   let url = `/normal/adminFinanceAction!update.action`;
    //   if (!this.dataForm.id) {
    //     url = `/normal/adminFinanceAction!add.action`;
    //   }
    //   this.$refs["dataForm"].validate((valid) => {
    //     if (valid) {
    //       this.$http({
    //         url: this.$http.adornUrl(url),
    //         method: "get",
    //         params: this.$http.adornParams({
    //           ...this.dataForm,
    //           login_safeword: encrypt(this.dataForm.login_safeword),
    //         }),
    //       }).then(({ data }) => {
    //         console.log("data => " + JSON.stringify(data));
    //         this.$message({
    //           message: "操作成功",
    //           type: "success",
    //           duration: 1500,
    //           onClose: () => {
    //             this.visible = false;
    //             this.$emit("refreshDataList");
    //           },
    //         });
    //       });
    //     }
    //   });
    // }),
  },
};
</script>
<style scoped>
.titleDivTwo {
  height: 40px;
  border-left: 3px solid #1c4efa;
  background: #f4f7ff;
}
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
.vertical-scrollbar .scroll-content {
  max-height: 600px;
  overflow-y: auto;
  overflow-x: hidden;
}
.vertical-scrollbar .scroll-content::-webkit-scrollbar {
  width: 4px;
}

.vertical-scrollbar .scroll-content::-webkit-scrollbar-track {
  background-color: #f1f1f1;
}

.vertical-scrollbar .scroll-content::-webkit-scrollbar-thumb {
  background-color: #888;
  border-radius: 4px;
}

.vertical-scrollbar .scroll-content::-webkit-scrollbar-thumb:hover {
  background-color: #555;
}
.spainut {
  width: 360px;
}
</style>
