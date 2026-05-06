<template>
  <div class="login">
    <div class="login-box">
      <!-- <div class="top">
        <div class="logo"><img src="~@/assets/img/login-logo.png"
               alt=""></div>
      </div> -->
      <div class="mid">
        <div class="midBg">
          <el-form
            :model="dataForm"
            :rules="dataRule"
            ref="dataForm"
            @keyup.enter="dataFormSubmit()"
            status-icon
          >
            <el-form-item prop="userName">
              <el-input
                class="info"
                v-model="dataForm.userName"
                clearable
                placeholder="帐号"
              ></el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                class="info"
                v-model="dataForm.password"
                clearable
                show-password
                type="password"
                placeholder="密码"
              ></el-input>
            </el-form-item>
            <el-form-item prop="googleAuthCode">
              <el-input
                class="info"
                type="number"
                clearable
                v-model="dataForm.googleAuthCode"
                placeholder="谷歌验证码"
              ></el-input>
            </el-form-item>
            <!-- <el-form-item prop="captcha">
            <el-row :gutter="20">
              <el-col :span="14">
                <el-input v-model="dataForm.captcha"
                          placeholder="验证码">
                </el-input>
              </el-col>
              <el-col :span="10"
                      class="login-captcha">
                <img :src="captchaPath"
                     @click="getCaptcha()"
                     alt="">
              </el-col>
            </el-row>
          </el-form-item> -->
            <el-form-item>
              <!-- <div class="item-btn"><input type="primary"
                      :loading="loading"
                     value="登录"
                     @click="dataFormSubmit()"></div> -->
              <el-button
                type="primary"
                class="infoButton"
                :loading="loading"
                @click="dataFormSubmit()"
              >
                登录
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>

      <div class="bottom"></div>
    </div>
  </div>
</template>

<script>
import { getUUID } from "@/utils";
import { encrypt } from "@/utils/crypto";
export default {
  components: {},
  data() {
    return {
      dataForm: {
        userName: "",
        password: "",
        uuid: "",
        captcha: "",
        googleAuthCode: "",
      },
      loading: false,
      isSubmit: false,
      dataRule: {
        userName: [
          { required: true, message: "账号不能为空", trigger: "blur" },
        ],
        password: [
          { required: true, message: "密码不能为空", trigger: "blur" },
        ],
        googleAuthCode: [
          { required: true, message: "谷歌验证码不能为空", trigger: "blur" },
        ],
        captcha: [
          { required: true, message: "验证码不能为空", trigger: "blur" },
        ],
      },
      captchaPath: "",
    };
  },
  beforeDestroy() {
    document.removeEventListener("keyup", this.handerKeyup);
  },
  created() {
    //this.getCaptcha() //图片验证
    document.addEventListener("keyup", this.handerKeyup);
  },
  methods: {
    handerKeyup(e) {
      var keycode = document.all ? event.keyCode : e.which;
      if (keycode === 13) {
        this.dataFormSubmit();
      }
    },
    // 提交表单
    dataFormSubmit() {
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          this.loading = true;
          this.login();
          //this.$refs.verify.show() //图片验证
        }
      });
    },
    login(verifyResult) {
      if (this.isSubmit) {
        return
      }
      this.loading = true;
      this.isSubmit = true;
      //console.log(this.$http.adornUrl("/adminLogin"));
      this.$http({
        url: this.$http.adornUrl("/adminLogin"),
        method: "post",
        data: this.$http.adornData({
          userName: this.dataForm.userName,
          passWord: encrypt(this.dataForm.password),
          googleAuthCode: this.dataForm.googleAuthCode,
          // 'captchaVerification': verifyResult.captchaVerification
        }),
      })
        .then(({ data }) => {
          if (data.code == 0) {
            console.log("version = 1.0.0");
            this.$cookie.set("Authorization", data.data.accessToken);
            this.$router.push({ path: "/home" });
           setTimeout(()=>{ // 防止重复提交
              this.$login = true;
              this.loading = false;
              this.isSubmit = false
            },5000)
          } else {
            this.$message({
              message: data.msg,
              type: "error",
            });
            setTimeout(()=>{ // 防止重复提交
              this.$login = true;
              this.loading = false;
              this.isSubmit = false
            },2000)
          }
          // this.$router.replace({ name: 'home' })
        })
        .catch(() => {
          this.loading = false;
          this.isSubmit = true;
          this.$login = false;
        });
    },
    // dataFormSubmit () {
    //   this.$refs['dataForm'].validate((valid) => {
    //     if (valid) {
    //       this.$http({
    //         url: this.$http.adornUrl('/login?grant_type=admin'),
    //         method: 'post',
    //         data: this.$http.adornData({
    //           'principal': this.dataForm.userName,
    //           'credentials': this.dataForm.password,
    //           'sessionUUID': this.dataForm.uuid,
    //           'imageCode': this.dataForm.captcha
    //         })
    //       }).then(({ data }) => {
    //         this.$cookie.set('Authorization', 'bearer' + data.access_token)
    //         this.$router.replace({ name: 'home' })
    //       }).catch(() => {
    //         this.getCaptcha()
    //       })
    //     }
    //   })
    // },
    // 获取验证码
    getCaptcha() {
      this.dataForm.uuid = getUUID();
      this.captchaPath = this.$http.adornUrl(
        `/captcha.jpg?uuid=${this.dataForm.uuid}`
      );
    },
  },
};
</script>

<style lang="scss" scoped>
*,
*:before,
*:after {
  box-sizing: border-box;
}
.login {
  width: 100%;
  height: 100%;
  background: url(~@/assets/img/Maskgroup.png) no-repeat top;
  background-size: 100%;
  position: fixed;
}
.mid {
  background: url(~@/assets/img/longinBg.png) no-repeat center;
  background-size: 100%;
  width: 655px;
  height: 536px;
  padding-top: 101px;
}
.midBg {
  width: 76%;
  height: 320px;
  background: #fff;
  margin: 0 auto;
  padding: 44px 0 0 56px;
}
.login .login-box {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  height: 100%;
  padding-top: 10%;
}
.login .login-box .top {
  margin-bottom: 30px;
  text-align: center;
}
.login .login-box .top .logo {
  font-size: 0;
  max-width: 50%;
  margin: 0 auto;
}
.login .login-box .top .company {
  font-size: 16px;
  margin-top: 10px;
}
.login .login-box .mid {
  font-size: 14px;
}
.login .login-box .mid .item-btn {
  margin-top: 10px;
}
.login .login-box .mid .item-btn input {
  border: 0;
  width: 74%;
  height: 40px;
  box-shadow: 0;
  background: #1f87e8;
  color: #fff;
}
.info {
  width: 400px;
  margin: 0 auto;
  border-radius: 30px;
}
.infoButton {
  width: 400px;
  margin: 0 auto;
  margin-top: 10px;
  border-radius: 30px;
}
.login-captcha {
  height: 40px;
}
.login .login-box .bottom {
  position: absolute;
  bottom: 10%;
  width: 100%;
  color: #999;
  font-size: 12px;
  text-align: center;
}
.el-form-item {
  margin-bottom: 15px !important;
}
.info input[type="number"]::-webkit-outer-spin-button,
.info input[type="number"]::-webkit-inner-spin-button {
  -webkit-appearance: none;
}
::v-deep .el-input__inner {
  background: #f4f6fa !important;
  margin-bottom: 10px;
  border: none;
  border-radius: 30px;
}
.info::-webkit-outer-spin-button,
.info::-webkit-inner-spin-button {
  -webkit-appearance: none;
}
</style>
