<template>
  <div>
    <el-dialog
      :title="$t('message.user.shoujihaobangding')"
      v-model="visible"
      :close-on-click-modal="false"
      @close="handClose"
      width="40%"
      center
    >
      <div>
        <div class="css-13xytqh" style="margin-bottom: 15px">
          <el-input
            :placeholder="$t('message.user.qsr_shoujihaoliru')"
            clearable
            type="text"
            class="rest3"
            v-model="phone"
          >
          </el-input>
          <div class="bn-input-suffix css-vurnku">
            <div class="css-1gkkq18"></div>
          </div>
        </div>
        <div class="css-hiy16i">
          <div class="css-vpcnym">
            <div class="css-1fymml5">
              <div
                class="bn-tooltip-box css-1yof1af"
                data-popper-reference-hidden="false"
                data-popper-escaped="false"
                data-popper-placement="right-start"
                style="
                  position: absolute;
                  left: 392px;
                  top: -22px;
                  transition: opacity 120ms ease-in-out 0s,
                    transform 120ms ease-in-out 0s;
                  opacity: 0;
                  transform: translate3d(6px, 0px, 0px);
                  visibility: hidden;
                "
              >
                <i class="gap-fill"></i>
              </div>
              <div class="css-13xytqh">
                <el-input
                  :placeholder="$t('message.user.qsr_yanzhengma')"
                  clearable
                  type="text"
                  class="rest3"
                  value=""
                  v-model.trim="verifcodeNum"
                >
                  <template #append>
                    <el-button
                      :style="'color:#1D91FF'"
                      @click="getVerifcode"
                      :disabled="isDisabaled"
                      >{{ VeriCode }}</el-button
                    >
                  </template>
                </el-input>
                <div class="bn-input-suffix css-vurnku">
                  <div class="css-1gkkq18"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" @click="confirmBtn">{{
            $t("message.user.bangding")
          }}</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import Axios from "@/api/login.js";
import Axios2 from "@/api/my.js";
import { ElMessage } from "element-plus";
export default {
  props: {
    isShowPhone: {
      // 弹窗是否展示
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      timer: null,
      phone: "",
      verifcodeNum: "",
      VeriCode: this.$t("message.user.huoquyanzhengma"),
      isDisabaled: false,
    };
  },
  computed: {
    visible: {
      get() {
        return this.isShowPhone;
      },
      set(val) {
        this.$emit("update:isShowPhone", false);
      },
    },
  },
  methods: {
    //获取验证码
    getVerifcode() {
      if (this.phone != "") {
        this.isDisabaled = true;
        this.VeriCode = 60;
        clearInterval(this.timer);
        this.timer = setInterval(() => {
          this.VeriCode--;
          if (this.VeriCode == 0) {
            this.isDisabaled = false;
            clearInterval(this.timer);
            this.timer = null;
            this.VeriCode = this.$t("message.user.fasongyanzhengma");
          }
        }, 1000);

        Axios.getVeriCode({
          target: this.phone,
          areacode: "",
        }).then((res) => {
          if (res.code == "0") {
            ElMessage.success(
              this.$t("message.user.yanzhengmafasongchenggong")
            );
          }
        });
      } else {
        ElMessage.error(this.$t("message.home.shoujihaobunengweikong"));
      }
    },
    confirmBtn() {
      let numReg = /^([0-9]\d*)$/;
      if (!numReg.test(this.verifcodeNum)) {
        ElMessage.error(this.$t("message.user.yanzhengmazhinengshurushuzi"));
        return;
      }

      if (this.phone && this.verifcodeNum) {
        Axios2.savePhone({
          phone: this.phone,
          verifcode: this.verifcodeNum,
        }).then((res) => {
          if (res.code == "0") {
            ElMessage.success(this.$t("message.user.bangdingchenggong"));
            this.$parent.getUserInfo();
            this.phone = "";
            this.verifcodeNum = "";
          }
        });
      } else {
        ElMessage.warning(this.$t("qsr_wanzhengdexinxi"));
      }
    },
    handClose() {
      this.phone = "";
      this.verifcodeNum = "";
    },
  },
  unmounted() {
    if(this.timer){
      clearInterval(this.timer);
      this.timer = null
    }
  },
};
</script>

<style scoped>
@import url("@/assets/css/login/register.css");
@import url("@/assets/css/commonTrade/global.css");
.el-input--suffix >>> .el-input__inner {
  padding-right: 30px;
  outline: none;
  border: none;
}
</style>
