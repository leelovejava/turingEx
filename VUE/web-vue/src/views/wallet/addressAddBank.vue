<template>
  <div id="__APP">
    <div style="position: relative"></div>
    <div class="css-tq0shg">
      <div class="css-14rvj6w">
        <div class="css-jrzkh7">
          <div id="header_global_js_wxgy34nj" class="css-1aac2e"></div>
        </div>
      </div>

      <div class="css-k2y2sp">
        <div data-bn-type="text" class="css-1104sf1">
          {{ $t("message.user.bianjishoukuanfangshi") }}
        </div>
      </div>

      <div class="main">
        <div class="tip-box">
          <i class="el-icon-warning icon"></i>
          {{ $t("message.user.tip7") }}
        </div>
        <div class="main-title">
          <div class="main-title-line"></div>
          {{ payItemInfo.methodName }}
        </div>
        <div>
          <div class="css-vurnku" v-if="payItemInfo.methodType == 2">
            <div>
              <div data-bn-type="text" class="css-kiaw5d">
                {{ $t("message.c2c.xuanzebizhong") }}
              </div>
              <el-select
                style="width: 500px; height: 50px"
                @change="selectVal"
                v-model="optionsValue"
              >
                <el-option
                  v-for="item in options"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </el-select>
            </div>
          </div>
          <div class="css-vurnku" v-if="payItemInfo.methodType == 2">
            <div>
              <div data-bn-type="text" class="css-kiaw5d">
                {{ $t("message.c2c.xuanzewangluo") }}
              </div>
              <div class="network-list">
                <div
                  class="network-item"
                  :class="[tabValue == item.name ? 'network-item-active' : '']"
                  @click="selectTab(item)"
                  v-for="(item, index) in listOpData1"
                  :key="index"
                >
                  {{ item.name }}
                </div>
              </div>
            </div>
          </div>
          <div class="css-vurnku" v-if="payItemInfo.methodType == 2">
            <div>
              <div data-bn-type="text" class="css-kiaw5d">
                {{ $t("message.user.dizhi") }}
              </div>
              <input
                data-bn-type="input"
                :placeholder="$t('message.c2c.tip155')"
                class="css-16fg16t input"
                v-model="payInfo.paramValue2"
              />
            </div>
          </div>
          <div class="css-vurnku">
            <div>
              <div data-bn-type="text" class="css-kiaw5d">
                {{ $t("message.user.xingming") }}
              </div>
              <input
                data-bn-type="input"
                :placeholder="$t('message.user.tip4')"
                class="css-16fg16t input"
                v-model="payInfo.realName"
              />
            </div>
          </div>
          <div class="css-vurnku" v-if="payItemInfo.methodType == 1">
            <div>
              <div data-bn-type="text" class="css-kiaw5d">
                {{ $t("message.user.tip8") }}
              </div>
              <input
                data-bn-type="input"
                :placeholder="$t('message.user.tip18')"
                class="css-16fg16t input"
                v-model="payInfo.paramValue3"
              />
            </div>
          </div>
          <div
            class="css-vurnku"
            v-if="payItemInfo.methodType != 1 && payItemInfo.methodType != 2"
          >
            <div>
              <div data-bn-type="text" class="css-kiaw5d">
                {{ payItemInfo.paramName1 }}
              </div>
              <input
                data-bn-type="input"
                :placeholder="payItemInfo.paramName1"
                class="css-16fg16t input"
                v-model="payInfo.paramValue1"
              />
            </div>
          </div>
          <div class="css-vurnku" v-if="payItemInfo.methodType == 1">
            <div>
              <div data-bn-type="text" class="css-kiaw5d">
                {{ $t("message.user.yinhangmingcheng") }}
              </div>
              <input
                data-bn-type="input"
                :placeholder="$t('message.user.tip9')"
                class="css-16fg16t input"
                v-model="payInfo.paramValue1"
              />
            </div>
          </div>

          <div class="css-vurnku" v-if="payItemInfo.methodType == 1">
            <div>
              <div data-bn-type="text" class="css-kiaw5d">
                {{ $t("message.user.tip10") }}
              </div>
              <input
                data-bn-type="input"
                :placeholder="$t('message.user.tip11')"
                class="css-16fg16t input"
                v-model="payInfo.paramValue2"
              />
            </div>
          </div>

          <div class="css-vurnku">
            <div>
              <div data-bn-type="text" class="css-kiaw5d">
                {{ $t("message.c2c.tip150") }}
              </div>
              <input
                data-bn-type="input"
                :placeholder="$t('message.c2c.tip150')"
                class="css-16fg16t input"
                v-model="payInfo.remark"
              />
            </div>
          </div>
          <div class="css-vurnku" v-if="payItemInfo.methodType != 1">
            <div>
              <div data-bn-type="text" class="css-kiaw5d">
                {{
                  payItemInfo.methodType == 5
                    ? $t("message.c2c.tip151")
                    : $t("message.c2c.erweima")
                }}
              </div>
              <el-upload
                class="avatar-uploader"
                :action="`${nowUrl}/api/api/uploadFile`"
                :headers="uploadHeader"
                accept=".jpg,.jpeg,.png,.gif.JPG,.JPEG,.PNG,.GIF"
                :show-file-list="false"
                :on-success="handelDoucumentsFront"
                :on-error="onErrorUpload"
                :before-upload="beforeAvatarUpload"
              >
                <img v-if="oneImg" :src="oneImg" class="avatar" />
                <i v-else class="el-icon-plus avatar-uploader-icon"></i>
              </el-upload>
            </div>
          </div>
        </div>
        <div class="tips">
          {{ $t("message.user.tip12") }}
        </div>
        <div class="but-box">
          <el-button class="but-item" @click="dialogVisible = true">{{
            $t("message.user.quxiao")
          }}</el-button>
          <el-button
            class="but-item"
            type="primary"
            :disabled="isDisabled"
            @click="saveData"
            >{{ $t("message.user.baocun") }}</el-button
          >
        </div>
      </div>
    </div>
    <div class="footer-box"></div>

    <el-dialog v-model="dialogVisible" width="470px">
      <img class="tip-img" src="@/assets/images/wallet/tip.png" />
      <div class="dig-title">{{ $t("message.user.tip13") }}</div>
      <div class="dig-text">{{ $t("message.user.tip14") }}</div>
      <template #footer>
        <span class="dialog-footer">
          <el-button class="but" @click="dialogVisible = false">{{
            $t("message.user.quxiao")
          }}</el-button>
          <el-button class="but" type="primary" @click="deleteIdFun">{{
            $t("message.user.queding")
          }}</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>
<script>
import { signatureGenerate } from "@/utils/signatureUtil";

import c2cAxios from "@/api/c2c.js";
import walletAxios from "@/api/wallet.js";
import { nowUrl } from "@/utils";
export default {
  data() {
    return {
      nowUrl,
      dialogVisible: false,
      methodId: "",
      payId: "",
      real_name: "", //姓名
      remark: "", //备注,
      lstData: [],
      payInfo: {
        paramValue1: "",
        paramValue2: "",
        paramValue3: "",
        paramValue4: "",
        paramName1: "",
        remark: "",
        real_name: "",
        qrcode: "",
        method_config_id: "",
      },
      payItemInfo: {},
      oneImg: "",
      qrcodeImg: "",
      options: [],
      optionsValue: "",
      listOpData1: [],
      tabValue: "",
      isDisabled: false,
      uploadHeader: {
        tissuePaper: "",
        sign: "",
        systemRandom: "",
      },
      type: "", //新增接口用到，1表示银行卡2表示c2c
      timeOutTimer1:null,
      timeOutTimer2:null,
      timeOutTimer3:null
    };
  },
  mounted() {
    if (this.$route.query.methodId) {
      this.methodId = this.$route.query.methodId;
      this.getAddressConfig();
    }
    if (this.$route.query.payId) {
      this.payId = this.$route.query.payId;
      this.getDetails();
    }
    this.type = this.$route.query.type;
    this.getAddress();
  },
  methods: {
    MenuChoose(id) {
      this.menuchoose = id;
    },
    selectVal() {
      this.getList();
    },
    selectTab(item) {
      this.tabValue = item.name;
    },
    onErrorUpload() {
      this.$message.warning(this.$t("message.user.shangchuan3"));
    },

    //上传付款凭证
    handelDoucumentsFront(res, file) {
      const { path, httpUrl } = res.data;
      this.oneImg = httpUrl;
      this.payInfo.qrcode = path;
    },
    beforeAvatarUpload(file) {
      //请求头加参数
      const { timestamp, signature, systemRandom } = signatureGenerate();
      this.uploadHeader.tissuePaper = timestamp;
      this.uploadHeader.sign = signature;
      this.uploadHeader.systemRandom = systemRandom;
      let types = [
        "image/jpeg",
        "image/jpg",
        "image/gif",
        "image/bmp",
        "image/png",
      ];
      const isImage = types.includes(file.type);
      const isJPG = file.type === "image/jpeg";
      const isLt5M = file.size / 1024 / 1024 < 5;

      if (!isImage) {
        this.$message.error(this.$t("message.user.shangchuan1"));
      }
      if (!isLt5M) {
        this.$message.error(this.$t("message.user.shangchuan2"));
      }
      return isImage && isLt5M;
    },
    //虚拟币获取币种
    getSymbolList() {
      this.options = [];
      c2cAxios.c2cSymbolList().then((res) => {
        if (res.code == "0") {
          for (const key in res.data) {
            let obj = {
              label: res.data[key],
              value: key,
            };
            this.options.push(obj);
          }
          if (this.payInfo.paramValue1) {
            this.optionsValue = this.payInfo.paramValue1;
          } else {
            this.optionsValue = this.options[0].value;
          }
          this.getList();
        }
      });
    },
    OnClickShowAddre() {
      this.payTypeShow = !this.payTypeShow;
    },
    AddressChangeShow(state) {
      this.showAddreChoose = state;
    },
    // 获取支付方式的详情
    getDetails() {
      let obj = {
        id: this.payId,
      };
      c2cAxios.getC2cPaymentMethod(obj).then((res) => {
        if (res.code == "0") {
          this.payInfo = res.data;
          this.oneImg = this.payInfo.qrcodePath;

          this.methodId = this.payInfo.methodConfigId;
          this.getAddressConfig();
        }
      });
    },
    // 获取支付方式的配置信息
    getAddressConfig() {
      var addParams = {
        id: this.methodId,
      };
      c2cAxios
        .getPayConfig(addParams)
        .then((res) => {
          if (res.code == "0") {
            this.payItemInfo = res.data;
            if (this.payItemInfo.methodType == 2) {
              this.getSymbolList();
            }
          }
        })
        .catch((err) => {
          console.error(err);
        });
    },
    getList() {
      this.listOpData1 = [];
      walletAxios
        .getChainName({
          coin: this.optionsValue,
        })
        .then((res) => {
          for (var i in res.data) {
            const item = res.data[i];
            this.listOpData1.push({
              icon: item.coin,
              name: item.blockchain_name,
              code: item.coin,
            });
          }
          if (this.payInfo.paramValue3) {
            this.listOpData1.map((item, index) => {
              if (item.blockchain_name == this.payInfo.paramValue3) {
                this.tabValue = this.listOpData1[index].name;
              }
            });
          } else {
            this.tabValue = this.listOpData1[0].name;
          }
        });
    },
    saveData() {
      if (!this.payInfo.realName) {
        this.$message.error(this.$t("message.user.tip4"));
        return;
      }
      if (this.payItemInfo.methodType == 1) {
        if (!this.payInfo.paramValue3) {
          this.$message.error(this.$t("message.user.tip15"));
          return;
        }
        if (!this.payInfo.paramValue1) {
          this.$message.error(this.$t("message.user.tip9"));
          return;
        }
      }
      if (
        this.payItemInfo.methodType != 1 &&
        this.payItemInfo.methodType != 2
      ) {
        if (!this.payInfo.paramValue1) {
          this.$message.error(
            this.$t("message.c2c.qingshuru") + this.payItemInfo.paramName1
          );
          return;
        }
      }
      //虚拟币判断
      if (this.payItemInfo.methodType == 2) {
        if (!this.payInfo.paramValue2) {
          this.$message.error(this.$t("message.user.xian7"));
          return;
        }
      }
      var obj = {
        method_config_id: this.methodId,
        real_name: this.payInfo.realName, //姓名
        param_value1: this.payInfo.paramValue1, //银行名称
        remark: this.payInfo.remark,
        qrcode: this.payInfo.qrcode,
      };
      if (this.payItemInfo.methodType == 1) {
        //银行卡
        obj.param_value2 = this.payInfo.paramValue2;
        obj.param_value3 = this.payInfo.paramValue3;
      }
      if (this.payItemInfo.methodType == 2) {
        //虚拟币
        obj.param_value3 = this.tabValue; //转账网络
        obj.param_value2 = this.payInfo.paramValue2; //提币地址
        obj.param_value1 = this.optionsValue; //币种
      }
      this.isDisabled = true;
      if (this.payId) {
        //修改
        obj.id = this.payId;
        c2cAxios.updatePayConfig(obj).then((res) => {
          if (res.code == "0") {
            this.$message({
              message: this.$t("message.user.xiugaichenggong"),
              type: "success",
            });
            this.timeOutTimer1 = setTimeout(() => {
              this.$router.go(-1);
            }, 1000);
          }
        });
      } else {
        obj.type = this.type;
        c2cAxios
          .addPayConfig(obj)
          .then((res) => {
            if (res.code == "0") {
              this.$message({
                message: this.$t("message.user.tianjiachenggong"),
                type: "success",
              });
              this.timeOutTimer2 = setTimeout(() => {
                this.$router.go(-1);
              }, 1000);
            }
          })
          .catch((err) => {
            console.error(err);
          });
      }

      this.timeOutTimer3 = setTimeout(() => {
        this.isDisabled = false;
      }, 2000);
    },
    getAddress() {
      var addParams = {
        methodId: this.methodId,
        real_name: this.payInfo.realName, //姓名
        param_value1: this.payInfo.paramValue1, //银行名称
        param_value2: this.payInfo.paramValue2, //开户行
        param_value3: this.payInfo.paramValue3, //银行卡号
        type: 1,
      };

      c2cAxios
        .userC2cPaymentMethodList(addParams)
        .then((res) => {
          this.listTableList = res.data;
        })
        .catch((err) => {
          console.error(err);
        });
    },
    OnClickPre() {
      this.$router.go(-1);
    },
    deleteIdFun() {
      this.$router.go(-1);
    },
    // 清除定时器
    unbindTimerHandle(){
      if(this.timeOutTimer1){
        clearTimeout(this.timeOutTimer1)
        this.timeOutTimer1 = null
      }
      if(this.timeOutTimer2){
        clearTimeout(this.timeOutTimer2)
        this.timeOutTimer2 = null
      }
      if(this.timeOutTimer3){
        clearTimeout(this.timeOutTimer3)
        this.timeOutTimer3 = null
      }
    }
  },
  unmounted() {
    this.unbindTimerHandle()
  },
};
</script>
<style scoped lang="css">
.css-k2y2sp {
  width: 100%;
  background: #ffffff;
  box-shadow: 0px 0px 6px rgba(0, 0, 0, 0.2);
}

.css-1104sf1 {
  height: 80px;
  display: flex;
  align-items: center;
  width: 1200px;
  margin: 0 auto;
  font-size: 24px;
  font-weight: bold;
  color: #000;
}

.main {
  width: 510px;
  margin: 0 auto;
  padding-bottom: 60px;
}

.tip-box {
  background: #eff7ff;
  min-height: 56px;
  border-radius: 5px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px 20px;
  font-size: 13px;
  margin-top: 60px;
}

.tip-box .icon {
  color: #1d91ff;
  margin-right: 10px;
  font-size: 20px;
}

.main-title {
  font-size: 20px;
  font-weight: bold;
  color: #000;
  display: flex;
  align-items: center;
  margin-top: 60px;
}

.main-title-line {
  background: #e6ba41;
  border-radius: 10px;
  width: 4px;
  height: 16px;
  margin-right: 8px;
}

.input {
  width: 480px;
  height: 50px;
  border: 1px solid #eaecef;
  border-radius: 5px;
  outline: none;
  padding-left: 20px;
  font-size: 14px;
}

.css-vurnku {
  margin-top: 20px;
}

.css-kiaw5d {
  padding: 15px 0;
}

.tips {
  font-size: 14px;
  padding: 30px 0;
}

.but-box {
}

.but-item {
  width: 248px;
}

.el-button {
  border: 1px solid #dcdfe6;
}

.footer-box {
  height: 100px;
  background: #ffffff;
  box-shadow: 0px 0px 6px rgba(0, 0, 0, 0.2);
}

.dig-title {
  font-size: 24px;
  font-weight: bold;
  text-align: center;
  margin-top: 30px;
}

.dig-text {
  font-size: 14px;
  margin-top: 20px;
  text-align: center;
  margin-bottom: 30px;
}

.tip-img {
  width: 60px;
  height: 60px;
  margin: 0 auto;
  display: block;
}

.dialog-footer {
  display: flex;
}

.dialog-footer .but {
  width: 210px;
}

:deep(.el-dialog__header) {
  border-bottom: none !important;
}

:deep(.el-select) .el-input__inner {
  height: 50px;
}

.network-list {
  display: flex;
}

.network-item {
  width: 90px;
  height: 50px;
  border: 1px solid #eaecef;
  border-radius: 5px;
  color: #727a89;
  margin-right: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.network-item-active {
  border: 1px solid #1d91ff !important;
  color: #000000;
}
</style>
