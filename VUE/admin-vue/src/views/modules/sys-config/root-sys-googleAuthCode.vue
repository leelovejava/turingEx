<template>
  <el-dialog
    :title="
      googleAuthBind ? '解绑' + n + '谷歌验证器' : '绑定' + n + '谷歌验证器'
    "
    :visible.sync="visible"
    @close="handClose"
    :append-to-body="true"
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="120px"
    >
      <div class="gogTop">
        <div
          v-if="!getInput"
          class="gogRefresgh"
          @click="getGoogleAuthSecret()"
        >
          <i class="el-icon-refresh"></i>
        </div>
      </div>
      <el-form-item
        v-if="getInput && !googleAuthBind"
        label="密钥"
        style="position: relative"
      >
        <el-input
          type="number"
          :disabled="true"
          placeholder="请点击右侧 生成密钥和二维码"
        ></el-input>
        <span class="maTreButton" @click="getGoogleAuthSecret()"
          >生成密钥和二维码</span
        >
      </el-form-item>
      <el-form-item v-show="!getInput" label="密钥" style="position: relative">
        <el-input
          type="text"
          :disabled="true"
          v-model="dataForm.secret"
        ></el-input>
        <el-button
          class="spaButton"
          type="button"
          v-clipboard:copy="dataForm.secret"
          v-clipboard:success="onCopy"
          v-clipboard:error="onError"
        >
          复制
        </el-button>
      </el-form-item>
      <div v-show="!getInput" class="qrCode">
        <img :src="gleimg" alt="" />
      </div>
      <el-form-item
        v-if="!googleAuthBind && n !== '超级'"
        label="谷歌验证"
        prop="code"
      >
        <el-input
          type="number"
          placeholder="请输入谷歌验证码"
          v-model="dataForm.code"
        ></el-input>
      </el-form-item>
      <el-form-item label="超级谷歌验证码" prop="rootGoogleAuthCode">
        <el-input
          type="number"
          placeholder="请输入超级谷歌验证码"
          v-model="dataForm.rootGoogleAuthCode"
        ></el-input>
      </el-form-item>
    </el-form>
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
      dataForm: {
        password: "",
        newPassword: "",
        urlencoded: "",
        rootGoogleAuthCode: "",
        code: "",
        secret: "", //谷歌密钥
      },
      googleAuthBind: "", //系统用户是否绑定谷歌验证器
      n: "", //超级谷歌还是admin判断类型String
      m: "", //m true=解绑  false = 绑定
      getInput: true,
      gleimg: "",
      gogleUs: "", //用户列表进入
      dataRule: {
        code: [
          { required: true, message: "谷歌验证码不能为空", trigger: "blur" },
        ],
        rootGoogleAuthCode: [
          {
            required: true,
            message: "超级谷歌验证码不能为空",
            trigger: "blur",
          },
        ],
      },
      data: {},
    };
  },
  computed: {
    userName: {
      get() {
        return this.$store.state.user.name;
      },
    },
    mainTabs: {
      get() {
        return this.$store.state.common.mainTabs;
      },
      set(val) {
        this.$store.commit("common/updateMainTabs", val);
      },
    },
  },
  created() {
    console.log(this.googleAuthBind);
  },
  methods: {
    // 初始化
    init(googleAuthBind, n, m) {
      this.visible = true;
      this.googleAuthBind = googleAuthBind;
      this.n = n;
      this.m = m;
      console.log(m);
      this.$nextTick(() => {
        this.getInput = true;
        this.$refs["dataForm"].resetFields();
      });
    },
    handClose() {
      this.$data.dataForm = JSON.parse(
        JSON.stringify(this.$options.data().dataForm)
      );
      this.$nextTick(() => {
        this.$refs["dataForm"].clearValidate(); // 清除表单验证
      });
    },
    // 表单提交
    dataFormSubmit: Debounce(function () {
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          if (this.googleAuthBind) {
            if (this.n === "超级") {
              //超级谷歌解绑
              this.$http({
                url: this.$http.adornUrl(
                  "/adminGoogleAuthAction/superGoogleAuthUnBind"
                ),
                method: "post",
                data: this.$http.adornData({
                  superGoogleAuthCode: this.dataForm.rootGoogleAuthCode,
                }),
              }).then(({ data }) => {
                if (data.code == 0) {
                  this.$message({
                    message: "解绑成功",
                    type: "success",
                    duration: 1500,
                    onClose: () => {
                      this.visible = false;
                      this.$emit("refreshDataList");
                    },
                  });
                } else {
                  this.$message({
                    message: data.msg || "解绑失败",
                    type: "error",
                    duration: 1500,
                    onClose: () => {
                      // this.visible = false
                      // this.$emit('refreshDataList')
                    },
                  });
                }
              });
            } else {
              this.$http({
                //admin解绑
                url: this.$http.adornUrl(
                  "/adminGoogleAuthAction/adminGoogleAuthUnBind"
                ),
                method: "post",
                data: this.$http.adornData({
                  superGoogleAuthCode: this.dataForm.rootGoogleAuthCode,
                }),
              }).then(({ data }) => {
                if (data.code == 0) {
                  this.$message({
                    message: "解绑成功",
                    type: "success",
                    duration: 1500,
                    onClose: () => {
                      this.visible = false;
                      this.$emit("refreshDataList");
                    },
                  });
                } else {
                  this.$message({
                    message: data.msg || "解绑失败",
                    type: "error",
                    duration: 1500,
                    onClose: () => {
                      // this.visible = false
                      // this.$emit('refreshDataList')
                    },
                  });
                }
              });
            }
          } else {
            if (this.n === "超级") {
              //超级绑定
              this.$http({
                url: this.$http.adornUrl(
                  "/adminGoogleAuthAction/superGoogleAuthBind"
                ),
                method: "post",
                data: this.$http.adornData({
                  superGoogleAuthCode: this.dataForm.rootGoogleAuthCode,
                  superGoogleAuthSecret: this.dataForm.secret,
                }),
              }).then(({ data }) => {
                if (data.code == 0) {
                  this.$message({
                    message: "绑定成功",
                    type: "success",
                    duration: 1500,
                    onClose: () => {
                      this.visible = false;
                      this.$emit("refreshDataList");
                    },
                  });
                } else {
                  this.$message({
                    message: data.msg || "绑定失败",
                    type: "error",
                    duration: 1500,
                    onClose: () => {
                      // this.visible = false
                      this.$emit("refreshDataList");
                    },
                  });
                }
              });
            } else {
              //admin绑定
              this.$http({
                url: this.$http.adornUrl(
                  "/adminGoogleAuthAction/adminGoogleAuthBind"
                ),
                method: "post",
                data: this.$http.adornData({
                  googleAuthCode: this.dataForm.code,
                  superGoogleAuthCode: this.dataForm.rootGoogleAuthCode,
                  googleAuthSecret: this.dataForm.secret,
                }),
              }).then(({ data }) => {
                if (data.code == 0) {
                  this.$message({
                    message: "绑定成功",
                    type: "success",
                    duration: 1500,
                    onClose: () => {
                      this.visible = false;
                      this.$emit("refreshDataList");
                    },
                  });
                } else {
                  this.$message({
                    message: data.msg || "绑定失败",
                    type: "error",
                    duration: 1500,
                    onClose: () => {
                      // this.visible = false
                      this.$emit("refreshDataList");
                    },
                  });
                }
              });
            }
          }
        }
      });
    }),
    //获取密钥
    getGoogleAuthSecret() {
      let name = "";
      if (this.n === "超级") {
        name = "super"; // root超级管理验证
      } else {
        name = "admin";
      }
      this.$http({
        url: this.$http.adornUrl("/getUserNameGoogleAuthSecret"),
        method: "get",
        params: this.$http.adornParams(
          Object.assign({
            userName: name + "@" + window.location.hostname,
          })
        ),
      }).then(({ data }) => {
        if (data.code == 0) {
          this.getInput = false;
          this.dataForm.secret = data.data.googleAuthSecret;
          this.gleimg = data.data.googleAuthImg;
          this.$message({
            message: "操作成功",
            type: "success",
            duration: 1500,
          });
        } else {
          this.$message({
            message: "生成失败",
            type: "error",
            duration: 1500,
          });
        }
      });
    },
    onCopy: function (e) {
      this.$message({
        message: "复制成功",
        type: "success",
      });
    },
    onError: function (e) {
      this.$message({
        message: "复制失败",
        type: "error",
      });
    },
  },
};
</script>
<style scoped>
.gogText {
  float: left;
}
.gogRefresgh {
  float: right;
  cursor: pointer;
}
.el-icon-refresh:hover {
  color: #02a1e9;
}
.gogTop {
  overflow: hidden;
  padding: 0 15px;
  height: 43px;
}
.maTreButton {
  position: absolute;
  width: 123px;
  right: 10px;
  color: #02a1e9;
  cursor: pointer;
}
.maTreButton:hover {
  color: rgb(59, 226, 184);
}
.qrCode {
  width: 219px;
  height: 228px;
  border: 1px solid #e1e4eb;
  margin: 0 auto;
  margin-bottom: 20px;
}
.spaButton {
  position: absolute;
  right: 2px;
  border: none;
  background: #eff2f5;
  color: #02a1e9;
}
</style>
