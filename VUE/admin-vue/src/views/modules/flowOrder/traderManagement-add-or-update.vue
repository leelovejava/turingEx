<template>
  <el-dialog
    :title="!dataForm.uuid ? '新增' : '修改'"
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
          label-width="185px"
        >
          <el-form-item class="titleDivTwo" label="基础信息" prop="">
          </el-form-item>
          <el-row>
            <el-col :span="8">
              <el-form-item label="用户UID" prop="userCode">
                <el-input
                  :disabled="dataForm.uuid ? true : false"
                  v-model="dataForm.userCode"
                  placeholder="用户UID"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="交易员名称" prop="name">
                <el-input
                  v-model="dataForm.name"
                  placeholder="交易员名称"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="注册时间" prop="createTime">
                <el-date-picker
                  v-model="dataForm.createTime"
                  type="datetime"
                  placeholder="选择日期"
                  value-format="yyyy-MM-dd HH:mm:ss"
                ></el-date-picker>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="16">
              <el-form-item label="交易员简介" prop="remarks">
                <el-input
                  type="textarea"
                  v-model="dataForm.remarks"
                  :autosize="{ minRows: 4, maxRows: 8 }"
                  show-word-limit
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8"> </el-col>
          </el-row>
          <el-form-item label="头像图片" prop="img">
            <el-upload
              class="avatar-uploader"
              :action="$http.adornUrl('/api/uploadFile')"
              :headers="{ Authorization: $cookie.get('Authorization') }"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
            >
              <img v-if="imageUrl" :src="imageUrl" class="avatar" />
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
          </el-form-item>
          <el-form-item class="titleDivTwo" label="近3周交易信息" prop="">
          </el-form-item>
          <!-- 第1行 -->
          <el-row>
            <el-col :span="12">
              <el-col :span="14">
                <el-form-item label="近3周收益+偏差值" prop="week3Profit">
                  <el-input
                    placeholder="0.00"
                    disabled
                    v-model="dataForm.week3Profit"
                    class="input-with-select"
                  >
                    <el-button slot="append">+</el-button>
                  </el-input>
                </el-form-item></el-col
              >
              <el-col :span="6">
                <el-form-item
                  label=""
                  style="margin-left: -185px"
                  prop="deviationWeek3Profit"
                >
                  <el-input
                    type="number"
                    placeholder="0"
                    v-model="dataForm.deviationWeek3Profit"
                    class="input-with-select"
                  >
                  </el-input>
                </el-form-item>
              </el-col>
            </el-col>
            <el-col :span="12">
              <el-col :span="14">
                <el-form-item
                  label="近3周累计金额+偏差值"
                  prop="week3OrderAmount"
                >
                  <el-input
                    placeholder="0.00"
                    disabled
                    v-model="dataForm.week3OrderAmount"
                    class="input-with-select"
                  >
                    <el-button slot="append">+</el-button>
                  </el-input>
                </el-form-item></el-col
              >
              <el-col :span="6">
                <el-form-item
                  label=""
                  style="margin-left: -185px"
                  prop="deviationWeek3OrderAmount"
                >
                  <el-input
                    placeholder="0"
                    type="number"
                    v-model="dataForm.deviationWeek3OrderAmount"
                    class="input-with-select"
                  >
                  </el-input>
                </el-form-item>
              </el-col>
            </el-col>
          </el-row>

          <!-- 第2行  缺-->
          <el-row>
            <el-col :span="12">
              <el-col :span="14">
                <el-form-item
                  label="近3周收益率偏差值+偏差值"
                  prop="week3ProfitRatio"
                >
                  <el-input
                    placeholder="0.00"
                    disabled
                    v-model="dataForm.week3ProfitRatio"
                    class="input-with-select"
                  >
                    <el-button slot="append">+</el-button>
                  </el-input>
                </el-form-item></el-col
              >
              <el-col :span="6">
                <el-form-item
                  label=""
                  style="margin-left: -185px"
                  prop="deviationWeek3ProfitRatio"
                >
                  <el-input
                    placeholder="0"
                    type="number"
                    v-model="dataForm.deviationWeek3ProfitRatio"
                    class="input-with-select"
                  >
                    <el-button slot="append">%</el-button>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-col>
            <el-col :span="12">
              <el-col :span="14">
                <el-form-item
                  label="近3周盈利笔数+偏差值"
                  prop="week3OrderProfit"
                >
                  <el-input
                    placeholder="0"
                    disabled
                    v-model="dataForm.week3OrderProfit"
                    class="input-with-select"
                  >
                    <el-button slot="append">+</el-button>
                  </el-input>
                </el-form-item></el-col
              >
              <el-col :span="6">
                <el-form-item
                  label=""
                  style="margin-left: -185px"
                  prop="deviationWeek3OrderProfit"
                >
                  <el-input
                    type="number"
                    placeholder="请输入内容"
                    v-model="dataForm.deviationWeek3OrderProfit"
                    class="input-with-select"
                  >
                  </el-input>
                </el-form-item>
              </el-col>
            </el-col>
          </el-row>
          <!-- 第3行 -->
          <el-row>
            <el-col :span="12">
              <el-col :span="14">
                <el-form-item label="近3周交易笔数+偏差值" prop="week3OrderSum">
                  <el-input
                    placeholder="0"
                    disabled
                    v-model="dataForm.week3OrderSum"
                    class="input-with-select"
                  >
                    <el-button slot="append">+</el-button>
                  </el-input>
                </el-form-item></el-col
              >
              <el-col :span="6">
                <el-form-item
                  label=""
                  style="margin-left: -185px"
                  prop="deviationWeek3OrderSum"
                >
                  <el-input
                    placeholder="请输入内容"
                    type="number"
                    v-model="dataForm.deviationWeek3OrderSum"
                    class="input-with-select"
                  >
                  </el-input>
                </el-form-item>
              </el-col>
            </el-col>
          </el-row>
          <el-form-item class="titleDivTwo" label="累计交易信息" prop="">
          </el-form-item>
          <!-- 第4行 -->
          <el-row>
            <el-col :span="12">
              <el-col :span="14">
                <el-form-item label="累计收益+偏差值" prop="profit">
                  <el-input
                    placeholder="0.00"
                    disabled
                    v-model="dataForm.profit"
                    class="input-with-select"
                  >
                    <el-button slot="append">+</el-button>
                  </el-input>
                </el-form-item></el-col
              >
              <el-col :span="6">
                <el-form-item
                  label=""
                  style="margin-left: -185px"
                  prop="deviationProfit"
                >
                  <el-input
                    placeholder="0"
                    type="number"
                    v-model="dataForm.deviationProfit"
                    class="input-with-select"
                  >
                  </el-input>
                </el-form-item>
              </el-col>
            </el-col>
            <el-col :span="12">
              <el-col :span="14">
                <el-form-item label="累计金额+偏差值" prop="orderAmount">
                  <el-input
                    placeholder="0.00"
                    disabled
                    v-model="dataForm.orderAmount"
                    class="input-with-select"
                  >
                    <el-button slot="append">+</el-button>
                  </el-input>
                </el-form-item></el-col
              >
              <el-col :span="6">
                <el-form-item
                  label=""
                  style="margin-left: -185px"
                  prop="deviationOrderAmount"
                >
                  <el-input
                    placeholder="0"
                    v-model="dataForm.deviationOrderAmount"
                    class="input-with-select"
                  >
                  </el-input>
                </el-form-item>
              </el-col>
            </el-col>
          </el-row>

          <!-- 第5行 -->
          <el-row>
            <el-col :span="12">
              <el-col :span="14">
                <el-form-item label="累计收益率+偏差值" prop="profitRatio">
                  <el-input
                    placeholder="0.00"
                    disabled
                    v-model="dataForm.profitRatio"
                    class="input-with-select"
                  >
                    <el-button slot="append">+</el-button>
                  </el-input>
                </el-form-item></el-col
              >
              <el-col :span="6">
                <el-form-item
                  label=""
                  style="margin-left: -185px"
                  prop="deviationProfitRatio"
                >
                  <el-input
                    placeholder="0"
                    type="number"
                    v-model="dataForm.deviationProfitRatio"
                    class="input-with-select"
                  >
                    <el-button slot="append">%</el-button>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-col>
            <el-col :span="12">
              <el-col :span="14">
                <el-form-item label="累计盈利笔数+偏差值" prop="orderProfit">
                  <el-input
                    placeholder="0"
                    disabled
                    v-model="dataForm.orderProfit"
                    class="input-with-select"
                  >
                    <el-button slot="append">+</el-button>
                  </el-input>
                </el-form-item></el-col
              >
              <el-col :span="6">
                <el-form-item
                  label=""
                  style="margin-left: -185px"
                  prop="deviationOrderProfit"
                >
                  <el-input
                    placeholder="0"
                    type="number"
                    v-model="dataForm.deviationOrderProfit"
                    class="input-with-select"
                  >
                  </el-input>
                </el-form-item>
              </el-col>
            </el-col>
          </el-row>
          <!-- 第6行 -->
          <el-row>
            <el-col :span="12">
              <el-col :span="14">
                <el-form-item label="累计亏损笔数+偏差值" prop="orderLoss">
                  <el-input
                    placeholder="0"
                    disabled
                    v-model="dataForm.orderLoss"
                    class="input-with-select"
                  >
                    <el-button slot="append">+</el-button>
                  </el-input>
                </el-form-item></el-col
              >
              <el-col :span="6">
                <el-form-item
                  label=""
                  style="margin-left: -185px"
                  prop="deviationOrderLoss"
                >
                  <el-input
                    placeholder="请输入内容"
                    v-model="dataForm.deviationOrderLoss"
                    class="input-with-select"
                  >
                  </el-input>
                </el-form-item>
              </el-col>
            </el-col>
            <el-col :span="12">
              <el-col :span="14">
                <el-form-item label="累计跟随人数+偏差值" prop="followerSum">
                  <el-input
                    placeholder="0"
                    disabled
                    v-model="dataForm.followerSum"
                    class="input-with-select"
                  >
                    <el-button slot="append">+</el-button>
                  </el-input>
                </el-form-item></el-col
              >
              <el-col :span="6">
                <el-form-item
                  label=""
                  style="margin-left: -185px"
                  prop="deviationFollowerSum"
                >
                  <el-input
                    placeholder="0"
                    type="number"
                    v-model="dataForm.deviationFollowerSum"
                    class="input-with-select"
                  >
                  </el-input>
                </el-form-item>
              </el-col>
            </el-col>
          </el-row>
          <el-form-item class="titleDivTwo" label="带单信息" prop="">
          </el-form-item>
          <el-form-item class="" label="提示:" prop="">
            <div>
              <div class="green">
                带单币种（多品种的话用 ; 隔开,例如：BTC/USDT;ETH/USDT）
              </div>
            </div>
          </el-form-item>
          <el-row>
            <el-col :span="10">
              <el-form-item label="状态" prop="state">
                <el-select
                  v-model="dataForm.state"
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
            <el-col :span="10">
              <el-form-item label="带单币种" prop="symbols">
                <el-input
                  v-model="dataForm.symbols"
                  placeholder="用;隔开,例如：BTC/USDT;ETH/USDT"
                >
                </el-input>
              </el-form-item>
            </el-col>
            <!-- <el-col :span="8">
              <el-form-item label="周期" prop="cycle">
                <el-input
                  class="speaInputTwo"
                  v-model="dataForm.cycle"
                  placeholder="周期"
                >
                  <template v-slot:append>
                    <span>天</span>
                  </template>
                </el-input>
              </el-form-item>
            </el-col> -->
          </el-row>
          <el-row>
            <el-col :span="8">
              <el-form-item label="利润分成比例" prop="profitShareRatio">
                <el-input
                  class=""
                  v-model="dataForm.profitShareRatio"
                  type="number"
                  placeholder="利润分成比例"
                >
                  <template v-slot:append>
                    <span>%</span>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item label="" prop="cycle"> </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="此次跟单最多跟随人数" prop="followerMax">
                <el-input
                  class=""
                  v-model="dataForm.followerMax"
                  type="number"
                  placeholder="此次跟单最多跟随人数"
                >
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-col :span="14">
                <el-form-item label="当前跟随人数+偏差值" prop="followerNow">
                  <el-input
                    placeholder="0"
                    disabled
                    v-model="dataForm.followerNow"
                    class="input-with-select"
                  >
                    <el-button slot="append">+</el-button>
                  </el-input>
                </el-form-item></el-col
              >
              <el-col :span="6">
                <el-form-item
                  label=""
                  style="margin-left: -185px"
                  prop="deviationFollowerNow"
                >
                  <el-input
                    placeholder="请输入内容"
                    type="number"
                    v-model="dataForm.deviationFollowerNow"
                    class="input-with-select"
                  >
                  </el-input>
                </el-form-item>
              </el-col>
            </el-col>
            <el-col :span="8">
              <el-form-item label="此次跟单最小下单数" prop="followVolumnMin">
                <el-input
                  class=""
                  type="number"
                  v-model="dataForm.followVolumnMin"
                  placeholder="此次跟单最小下单数"
                >
                </el-input>
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
    var validateindex = (rule, value, callback) => {
      if (this.dataForm.cycle < 0 || this.dataForm.cycle == 0) {
        callback(new Error("周期不能小于等于0"));
      } else {
        callback();
      }
    };
    var validatedaily_rate = (rule, value, callback) => {
      if (this.dataForm.daily_rate < 0) {
        callback(new Error("日利率不能小于0"));
      } else if (this.dataForm.daily_rate > this.dataForm.daily_rate_max) {
        callback(new Error("日利率初始值不能大于结算值"));
      } else {
        callback();
      }
    };
    var validatedaily_rate_max = (rule, value, callback) => {
      if (this.dataForm.daily_rate_max < this.dataForm.daily_rate) {
        callback(new Error("日利率结算值不能小于初始值"));
      } else if (this.dataForm.daily_rate_max <= 0) {
        callback(new Error("日利率结算值不能为0"));
      } else {
        callback();
      }
    };
    var validatetoday_rate = (rule, value, callback) => {
      if (this.dataForm.today_rate < 0) {
        callback(new Error("今日利率不能小于0"));
      } else {
        callback();
      }
    };
    var validatetodefault_ratio = (rule, value, callback) => {
      if (this.dataForm.default_ratio < 0) {
        callback(new Error("违约结算比例不能小于0"));
      } else {
        callback();
      }
    };
    var validatetodeinvestment_min = (rule, value, callback) => {
      if (this.dataForm.investment_min <= 0) {
        callback(new Error("投资金额区间不能小于0"));
      } else {
        callback();
      }
    };
    var validatetodeinvestment_max = (rule, value, callback) => {
      if (this.dataForm.investment_max <= this.dataForm.investment_min) {
        callback(new Error("区间结算值不能小于等于初始值"));
      } else if (this.dataForm.investment_max <= 0) {
        callback(new Error("区间结算值不能小于等于初始值"));
      } else {
        callback();
      }
    };
    return {
      visible: false,
      imageUrl: "",
      dataForm: {
        id: "",
        userCode: "",
        name: "",
        createTime: "",
        remarks: "",
        week3Profit: "",
        deviationWeek3Profit: "",
        week3OrderAmount: "",
        deviationWeek3OrderAmount: "",
        week3OrderProfit: "",
        deviationWeek3OrderProfit: "",
        week3OrderSum: "",
        deviationWeek3OrderSum: "",
        profit: "",
        deviationProfit: "",
        orderAmount: "",
        deviationOrderAmount: "",
        profitRatio: "",
        deviationProfitRatio: "",
        orderProfit: "",
        deviationOrderProfit: "",
        orderLoss: "",
        deviationOrderLoss: "",
        followerSum: "",
        deviationFollowerSum: "",
        state: "",
        symbols: "",
        profitShareRatio: "",
        followerMax: "",
        followerNow: "",
        deviationFollowerNow: "",
        followVolumnMin: "",
        week3ProfitRatio: "",
        deviationWeek3ProfitRatio: "",
        img: "",
      },
      id: "",
      direction: [
        {
          label: "开启带单",
          value: "1",
        },
        {
          label: "停止带单",
          value: "0",
        }, {
          label: "禁止带单",
          value: "2",
        },
      ],
      dataRule: {
        //{ validator: validateindex, trigger: "blur" },
        userCode: [{ required: true, message: "UID不能为空", trigger: "blur" }],
        name: [{ required: true, message: "名称不能为空", trigger: "blur" }],
        createTime: [
          { required: true, message: "注册时间不能为空", trigger: "blur" },
        ],
        remarks: [{ required: true, message: "简介不能为空", trigger: "blur" }],
        symbols: [{ required: true, message: "币种不能为空", trigger: "blur" }],
        daily_rate: [
          { validator: validatedaily_rate, trigger: "blur" },
          { required: true, message: "日利率不能为空", trigger: "blur" },
        ],
        daily_rate_max: [
          { validator: validatedaily_rate_max, trigger: "blur" },
          { required: true, message: "日利率不能为空", trigger: "blur" },
        ],
        today_rate: [
          { validator: validatetoday_rate, trigger: "blur" },
          { required: true, message: "今日利率不能为空", trigger: "blur" },
        ],
        default_ratio: [
          { validator: validatetodefault_ratio, trigger: "blur" },
          { required: true, message: "违约结算比例不能为空", trigger: "blur" },
        ],
        investment_min: [
          { validator: validatetodeinvestment_min, trigger: "blur" },
          { required: true, message: "投资金额区间不能为空", trigger: "blur" },
        ],
        investment_max: [
          { validator: validatetodeinvestment_max, trigger: "blur" },
          { required: true, message: "投资金额区间不能为空", trigger: "blur" },
        ],
        login_safeword: [
          {
            required: true,
            message: "登录人资金密码不能为空",
            trigger: "blur",
          },
        ],
        input3: [
          { required: true, message: "投资金额区间不能为空", trigger: "blur" },
        ],
        // img: [{ required: true, message: "上传图片不能为空", trigger: "blur" }],
      },
      tempKey: -666666, // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
    };
  },
  methods: {
    init(id) {
      if (id) {
        this.id = id;
        this.getMassage();
      }else {
        this.dataForm.state = this.direction[0].value
      }
      if (this.dataForm.img) {
        // this.imageUrl =  this.dataForm.path
      }
      this.visible = true;
    },
    handleAvatarSuccess(res, file) {
      this.dataForm.img = res.data.path;
      console.log(file);
      this.imageUrl = URL.createObjectURL(file.raw);
      console.log(this.imageUrl);
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
      this.id = ''
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
          let url = ''
          if(this.dataForm.uuid){//更新
            url = `/normal/adminTrader!update.action`
          }else{//新增
            url = `/normal/adminTrader!add.action`
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
                  this.$emit("refreshDataList");
                  this.visible = false;
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
    getMassage() {
      this.$http({
        url: this.$http.adornUrl(`/normal/adminTrader!toUpdate.action`),
        method: "get",
        params: this.$http.adornParams({
          uuid: this.id,
        }),
      }).then(({ data }) => {
        console.log("data => " + JSON.stringify(data));
        if (data.code == 0) {
          this.dataForm = { ...data.data };
          this.imageUrl =  this.dataForm.path
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
</style>
