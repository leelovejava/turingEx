<template>
  <el-dialog
    :title="title"
    :visible.sync="visible"
    width="800px"
    :append-to-body="true"
    @close="handClose"
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="80px"
    >
      <el-form-item
        :label="isShow == 1 ? '账变金额(不能小于0)' : '账变金额USDT(不能小于0)'"
        v-if="isShow == 1 || isShow == 12"
        prop="moneyRevise"
        :label-width="isShow == 1 ? '160px' : '200PX'"
      >
        <el-input type="number" v-model="dataForm.moneyRevise"></el-input>
      </el-form-item>
      <el-form-item label="账变类型" v-if="isShow == 1" label-width="160px">
        <el-select
          v-model="options.value"
          placeholder="账变类型"
          @change="changeVal()"
          class="spanselect"
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
      <el-form-item label="账变币种" v-if="isShow == 1" label-width="160px">
        <el-select
          v-model="optionsTwo.value"
          placeholder="账变币种"
          width="200px"
          class="spanselect"
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
      <el-form-item
        v-if="isShow == 7"
        label="用户重置密码"
        prop="password"
        label-width="160px"
      >
        <el-input
          type="password"
          v-model="dataForm.password"
          placeholder="请输入用户重置密码"
        ></el-input>
      </el-form-item>
      <el-form-item
        v-if="isShow == 9"
        label="用户重置资金密码"
        prop="newSafeword"
        label-width="160px"
      >
        <el-input
          type="password"
          v-model="dataForm.newSafeword"
          placeholder="请输入用户重置资金密码"
        ></el-input>
      </el-form-item>
      <el-form-item
        v-if="
          isShow == 1 ||
          isShow == 8 ||
          isShow == 7 ||
          isShow == 9 ||
          isShow == 10 ||
          isShow == 12
        "
        label="登录人资金密码"
        prop="safePassword"
        :label-width="isShow == 12 ? '200PX' : '160px'"
      >
        <el-input
          type="password"
          v-model="dataForm.safePassword"
          placeholder="请输入登录人资金密码"
        ></el-input>
      </el-form-item>
      <el-form-item
        v-if="isShow == 8 || isShow == 7 || isShow == 9 || isShow == 10"
        label="登录人谷歌验证器"
        prop="googleAuthCode"
        label-width="160px"
      >
        <el-input
          type="number"
          v-model="dataForm.googleAuthCode"
          placeholder="请输入登录人谷歌验证码"
        ></el-input>
      </el-form-item>
      <el-form-item
        v-if="isShow == 6"
        label="限制流水"
        prop="withdrawLimitAmount"
      >
        <el-input
          type="number"
          v-model="dataForm.withdrawLimitAmount"
          placeholder="0.0"
        ></el-input>
      </el-form-item>
      <div v-if="isShow == 6" style="margin: 10px; color: #278927">
        增加请输入正数，减少请输入负数
      </div>
      <!-- <el-form-item label="原密码" prop="password">
        <el-input type="password" v-model="dataForm.password"></el-input>
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input type="password" v-model="dataForm.newPassword"></el-input>
      </el-form-item>
     <!-- <el-form-item label="确认密码" prop="confirmPassword">
        <el-input type="password" v-model="dataForm.confirmPassword"></el-input>
      </el-form-item> -->
      <template v-if="isShow == 13">
        <el-form-item label="用户ID" prop="userId" label-width="160px">
          <el-input v-model="row.userId" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="用户名" prop="userName" label-width="160px">
          <el-input v-model="row.userName" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="借贷状态" prop="loanStatus" label-width="160px">
          <el-radio-group v-model="dataForm.loanStatus">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="2">禁止</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="可贷金额(借贷)" prop="loanCanAmount" label-width="160px">
          <el-input-number v-model="dataForm.loanCanAmount" :precision="2" :step="100" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="已贷金额(借贷)" prop="loanAlreadyAmount" label-width="160px">
          <el-input-number v-model="dataForm.loanAlreadyAmount" :precision="2" :step="100" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="是否老客户" prop="isOldUser" label-width="160px">
          <el-radio-group v-model="dataForm.isOldUser">
            <el-radio :label="1">老客户</el-radio>
            <el-radio :label="2">新客户</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="购买量化机器状态" prop="createRobotStatus" label-width="160px">
          <el-radio-group v-model="dataForm.createRobotStatus">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="2">禁止</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="提币状态" prop="txState" label-width="160px">
          <el-radio-group v-model="dataForm.txState">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="2">禁止</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="期权预设结果" prop="optionPreResult" label-width="160px">
          <el-radio-group v-model="dataForm.optionPreResult">
            <el-radio :label="-1">亏损</el-radio>
            <el-radio :label="0">未设置</el-radio>
            <el-radio :label="1">盈利</el-radio>
          </el-radio-group>
        </el-form-item>
      </template>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button v-if="isShow == 6" type="primary" @click="cleMath()"
        >一键清0</el-button
      >
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
    var validateConfirmPassword = (rule, value, callback) => {
      if (this.dataForm.newPassword !== value) {
        callback(new Error("确认密码与新密码不一致"));
      } else {
        callback();
      }
    };
    var vaBignumber = (rule, value, callback) => {
      if (this.dataForm.moneyRevise < 0) {
        callback(new Error("账变金额不能小于0"));
      } else {
        callback();
      }
    };
    return {
      visible: false,
      title: "",
      row: "",
      isShow: "", //标题显示内同
      userId: "",
      dataForm: {
        moneyRevise: "", //账变金额
        safePassword: "", //登录人资金密码
        googleAuthCode: "", //登录人谷歌验证器
        newSafeword: "", //新资金密码
        withdrawLimitAmount: "", //限制提现流水
        password: "",
        newPassword: "",
        confirmPassword: "",
        loanStatus: 1,
        loanCanAmount: 0,
        loanAlreadyAmount: 0,
        isOldUser: 2,
        createRobotStatus: 1,
        txState: 1,
        optionPreResult: 0,
      },
      options: [
        {
          value: 1,
          label: "平台充值金额(正式用户记录报表)",
        },
        {
          value: 2,
          label: "平台扣除金额(不记录报表)",
        },
      ],
      optionsTwo: [
        {
          value: "usdt",
          label: "usdt",
        },
        {
          value: "btc",
          label: "btc",
        },
        {
          value: "eth",
          label: "eth",
        },
        {
          value: "usdc",
          label: "usdc",
        },
      ],
      dataRule: {
        password: [
          { required: true, message: "密码不能为空", trigger: "blur" },
        ],
        newSafeword: [
          { required: true, message: "资金密码不能为空", trigger: "blur" },
        ],
        moneyRevise: [
          { required: true, message: "账变金额不能为空", trigger: "blur" },
          { validator: vaBignumber, trigger: "blur" },
        ],
        safePassword: [
          {
            required: true,
            message: "登录人资金密码不能为空",
            trigger: "blur",
          },
        ],
        googleAuthCode: [
          {
            required: true,
            message: "登录人谷歌验证码不能为空",
            trigger: "blur",
          },
        ],
        newPassword: [
          { required: true, message: "新密码不能为空", trigger: "blur" },
        ],
        confirmPassword: [
          { required: true, message: "确认密码不能为空", trigger: "blur" },
          { validator: validateConfirmPassword, trigger: "blur" },
        ],
      },
    };
  },
  computed: {
    mainTabs: {
      get() {
        return this.$store.state.common.mainTabs;
      },
      set(val) {
        this.$store.commit("common/updateMainTabs", val);
      },
    },
  },
  methods: {
    // 初始化
    init(uid, m, val, row) {
      this.clear();
      this.userId = uid;
      this.row = row;
      this.title = m;
      this.isShow = val;
      this.dataForm.withdrawLimitAmount = this.row.withdrawLimitAmount;
      this.visible = true;
      this.options.value = this.options[0].value;
      this.optionsTwo.value = this.optionsTwo[0].value;
      if (val == 13) {
        this.dataForm.loanStatus = row.loanStatus || 1;
        this.dataForm.loanCanAmount = row.loanCanAmount ? Number(row.loanCanAmount) : 0;
        this.dataForm.loanAlreadyAmount = row.loanAlreadyAmount ? Number(row.loanAlreadyAmount) : 0;
        this.dataForm.isOldUser = row.isOldUser || 2;
        this.dataForm.createRobotStatus = row.createRobotStatus || 1;
        this.dataForm.txState = row.txState || 1;
        this.dataForm.optionPreResult = row.optionPreResult || 0;
      }
      // this.$nextTick(() => {
      //   console.log(4);
      //   this.$refs["dataForm"].resetFields();
      // });
    },
    cleMath() {
      this.$confirm("一键清零提现流水限制", "是否清零流水限制", {
        //是否已绑定
        distinguishCancelAndClose: true,
        confirmButtonText: "取消",
        cancelButtonText: "确定",
        type: "warning",
      })
        .then(() => {})
        .catch((action) => {
          if (action === "cancel") {
            this.dataForm.withdrawLimitAmount = 0;
            this.dataFormSubmit();
          }
        });
    },
    clear() {
      // this.options.value = ''
      // this.optionsTwo.value = ''
      // this.row = "";
      this.dataForm = {
        moneyRevise: "", //账变金额
        safePassword: "", //登录人资金密码
        googleAuthCode: "", //登录人谷歌验证器
        newSafeword: "", //新资金密码
        withdrawLimitAmount: "", //限制提现流水
        password: "",
        newPassword: "",
        confirmPassword: "",
      };
      console.log(this.dataForm);
    },
    changeVal(val) {
      this.$forceUpdate();
    },
    handClose() {
      this.row = "";
      this.$data.dataForm = JSON.parse(
        JSON.stringify(this.$options.data().dataForm)
      );
      this.$nextTick(() => {
        this.$refs["dataForm"].clearValidate(); // 清除表单验证
      });
    },
    // 表单提交
    dataFormSubmit: Debounce(function () {
      console.log(this.dataForm.safePassword);
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          if (this.isShow == 1) {
            //修改账变金额
            this.$http({
              url: this.$http.adornUrl("/user/updateWallt"),
              method: "post",
              data: this.$http.adornData({
                moneyRevise: this.dataForm.moneyRevise,
                coinType: this.optionsTwo.value,
                accountType: this.options.value,
                safePassword: encrypt(this.dataForm.safePassword),
                userId: this.userId,
              }),
            }).then(({ data }) => {
              if (data.code == 0) {
                this.$message({
                  message: "已修改账户金额",
                  type: "success",
                  duration: 1000,
                  onClose: () => {
                    this.visible = false;
                    this.$nextTick(() => {
                      this.$emit("refreshDataList");
                    });
                  },
                });
              } else {
                this.$message({
                  message: data.msg,
                  type: "error",
                });
              }
            });
          } else if (this.isShow == 8) {
            //解绑谷歌验证器
            this.$http({
              url: this.$http.adornUrl("/user/deleteGooleAuthCode"),
              method: "post",
              data: this.$http.adornData({
                googleAuthCode: this.dataForm.googleAuthCode,
                loginSafeword: encrypt(this.dataForm.safePassword),
                userId: this.userId,
              }),
            }).then(({ data }) => {
              if (data.code == 0) {
                this.$message({
                  message: "已解绑谷歌验证器",
                  type: "success",
                  duration: 1000,
                  onClose: () => {
                    this.visible = false;
                    this.$nextTick(() => {
                      this.$emit("refreshDataList");
                    });
                  },
                });
              } else {
                this.$message({
                  message: data.msg,
                  type: "error",
                });
              }
            });
          } else if (this.isShow == 7) {
            //重置密码
            this.$http({
              url: this.$http.adornUrl("/user/restLoginPasswrod"),
              method: "post",
              data: this.$http.adornData({
                googleAuthCode: this.dataForm.googleAuthCode,
                loginSafeword: encrypt(this.dataForm.safePassword),
                password: encrypt(this.dataForm.password),
                userId: this.userId,
              }),
            }).then(({ data }) => {
              if (data.code == 0) {
                this.$message({
                  message: "密码修改成功",
                  type: "success",
                  duration: 1000,
                  onClose: () => {
                    this.visible = false;
                    this.$nextTick(() => {
                      this.$emit("refreshDataList");
                    });
                  },
                });
              } else {
                this.$message({
                  message: data.msg,
                  type: "error",
                });
              }
            });
          } else if (this.isShow == 9) {
            //重置资金密码
            this.$http({
              url: this.$http.adornUrl("/user/restSafePassword"),
              method: "post",
              data: this.$http.adornData({
                googleAuthCode: this.dataForm.googleAuthCode,
                loginSafeword: encrypt(this.dataForm.safePassword),
                newSafeword: encrypt(this.dataForm.newSafeword),
                userId: this.userId,
              }),
            }).then(({ data }) => {
              if (data.code == 0) {
                this.$message({
                  message: "密码修改成功",
                  type: "success",
                  duration: 1000,
                  onClose: () => {
                    this.visible = false;
                    this.$nextTick(() => {
                      this.$emit("refreshDataList");
                    });
                  },
                });
              } else {
                this.$message({
                  message: data.msg,
                  type: "error",
                });
              }
            });
          } else if (this.isShow == 6) {
            //限制提现流水
            this.$http({
              url: this.$http.adornUrl("/user/updateWithdrawalLimitFlow"),
              method: "post",
              data: this.$http.adornData({
                moneyWithdraw: this.dataForm.withdrawLimitAmount,
                userId: this.userId,
              }),
            }).then(({ data }) => {
              if (data.code == 0) {
                this.$message({
                  message: "修改成功",
                  type: "success",
                  duration: 1000,
                  onClose: () => {
                    this.visible = false;
                    this.$nextTick(() => {
                      this.$emit("refreshDataList");
                    });
                  },
                });
              } else {
                this.$message({
                  message: data.msg,
                  type: "error",
                });
              }
            });
          } else if (this.isShow == 10) {
            //限制登录
            this.$http({
              url: this.$http.adornUrl("/user/resetUserLoginState"),
              method: "post",
              data: this.$http.adornData({
                loginSafeword: encrypt(this.dataForm.safePassword),
                googleAuthCode: this.dataForm.googleAuthCode,
                userId: this.userId,
              }),
            }).then(({ data }) => {
              if (data.code == 0) {
                this.$message({
                  message: "修改成功",
                  type: "success",
                  duration: 1000,
                  onClose: () => {
                    this.visible = false;
                    this.$nextTick(() => {
                      this.$emit("refreshDataList");
                    });
                  },
                });
              } else {
                this.$message({
                  message: data.msg,
                  type: "error",
                });
              }
            });
          } else if (this.isShow == 12) {
            //赠送USDT
            this.$http({
              url: this.$http.adornUrl("/user/resetExchange"),
              method: "post",
              data: this.$http.adornData({
                loginSafeword: encrypt(this.dataForm.safePassword),
                id: this.userId,
                changeType: "change",
                moneyRevise: this.dataForm.moneyRevise,
              }),
            }).then(({ data }) => {
              if (data.code == 0) {
                this.$message({
                  message: "赠送成功",
                  type: "success",
                  duration: 1000,
                  onClose: () => {
                    this.visible = false;
                    this.$nextTick(() => {
                      this.$emit("refreshDataList");
                    });
                  },
                });
              } else {
                this.$message({
                  message: data.msg,
                  type: "error",
                });
              }
            });
          } else if (this.isShow == 13) {
            //用户状态管理
            this.$http({
              url: this.$http.adornUrl("/admin/user/updateUserStatus"),
              method: "post",
              data: this.$http.adornData({
                userId: this.userId,
                loanStatus: this.dataForm.loanStatus,
                loanCanAmount: this.dataForm.loanCanAmount,
                loanAlreadyAmount: this.dataForm.loanAlreadyAmount,
                isOldUser: this.dataForm.isOldUser,
                createRobotStatus: this.dataForm.createRobotStatus,
                txState: this.dataForm.txState,
                optionPreResult: this.dataForm.optionPreResult,
              }),
            }).then(({ data }) => {
              if (data.code == 0) {
                this.$message({
                  message: "操作成功",
                  type: "success",
                  duration: 1000,
                  onClose: () => {
                    this.visible = false;
                    this.$nextTick(() => {
                      this.$emit("refreshDataList");
                    });
                  },
                });
              } else {
                this.$message({
                  message: data.msg,
                  type: "error",
                });
              }
            });
          }
        }
      });

      // this.$refs['dataForm'].validate((valid) => {
      //   if (valid) {
      //     this.$http({
      //       url: this.$http.adornUrl('/changeSafeword'),
      //       method: 'post',
      //       data: this.$http.adornData({
      //         'oldSafeword': encrypt(this.dataForm.password),
      //         'newSafeword': encrypt(this.dataForm.newPassword)
      //       })
      //     }).then(({data}) => {
      //       this.$message({
      //         message: '操作成功',
      //         type: 'success',
      //         duration: 1500,
      //         onClose: () => {
      //           this.visible = false
      //           this.$nextTick(() => {
      //             // this.mainTabs = []
      //             // clearLoginInfo()
      //             // this.$router.replace({ name: 'login' })
      //           })
      //         }
      //       })
      //     })
      //   }
      // })
    }),
  },
};
</script>
<style scoped>
.spanselect {
  width: 600px;
}
</style>
