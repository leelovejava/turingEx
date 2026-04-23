<template>
  <el-dialog
    :title="title"
    :visible.sync="visible"
    @close="handClose"
    width="800px"
    :append-to-body="true"
  >
    <avue-crud
      ref="crud"
      :page.sync="page"
      :data="dataList"
      :option="tableOption"
      @search-change="searchChange"
      @selection-change="selectionChange"
    >
      <template slot="menuLeft"> </template>
    </avue-crud>
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="80px"
      v-if="this.isShow"
    >
      <div class="spranDiv">交易</div>
      <!-- 锁定金额 -->
      <el-form-item label="转移方向" v-if="isShow == 2" label-width="100px">
        <el-select
          v-model="resetLock.value"
          placeholder="转移方向"
          @change="changeVal()"
          class="spanselect"
        >
          <el-option
            v-for="item in resetLock"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <!-- 冻结金额 -->
      <el-form-item label="转移方向" v-if="isShow == 5" label-width="100px">
        <el-select
          v-model="resetFreeze.value"
          placeholder="转移方向"
          @change="changeVal()"
          class="spanselect"
        >
          <el-option
            v-for="item in resetFreeze"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="账变币种" label-width="100px">
        <el-select
          v-model="usdtArr.value"
          placeholder="账变币种"
          width="200px"
          class="spanselect"
          @change="changeVal()"
        >
          <el-option
            v-for="item in usdtArr"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item
        :label="isShow == 3 || isShow == 4 ? '锁定金额' : '转移金额'"
        prop="moneyRevise"
        label-width="100px"
      >
        <el-input type="number" v-model="dataForm.moneyRevise"></el-input>
      </el-form-item>
      <div class="spranDiv">登录人资金密码</div>
      <el-form-item label="资金密码" prop="loginSafeword" label-width="100px">
        <el-input
          type="password"
          v-model="dataForm.loginSafeword"
          placeholder="请输入登录人资金密码"
        ></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button v-if="isShow == 6" type="primary" @click="cleMath()"
        >一键清0</el-button
      >
      <el-button @click="visible = false">取消</el-button>
      <el-button v-if="title !== '钱包'" type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>
<script>
import { tableOption } from "@/crud/sys/othermanger";
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
        callback(new Error("金额不能小于0"));
      } else {
        callback();
      }
    };
    return {
      dataList: [],
      dataListLoading: false,
      tableOption: tableOption,
      visible: false,
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
      title: "",
      isShow: "", //标题显示内同
      userId: "",
      userCode:'',
      dataForm: {
        moneyRevise: "", //账变金额
        loginSafeword: "", //登录人资金密码
      },

      usdtArr: [],
      resetLock: [
        {
          //锁定金额
          label: "可用金额->锁定金额",
          value: "moneryToLock",
        },
        {
          label: " 锁定金额->可用金额",
          value: "lockToMoney",
        },
      ],
      resetFreeze: [
        {
          //冻结金额
          label: "可用金额->冻结金额",
          value: "moneryToFreeze",
        },
        {
          label: " 冻结金额->可用金额",
          value: "freezeToMoney",
        },
      ],
      dataRule: {
        password: [
          { required: true, message: "密码不能为空", trigger: "blur" },
        ],
        loginSafeword: [
          { required: true, message: "资金密码不能为空", trigger: "blur" },
        ],
        moneyRevise: [
          { required: true, message: "金额不能为空", trigger: "blur" },
          { validator: vaBignumber, trigger: "blur" },
        ],
        // safePassword: [
        //   {required: true,message: "登录人资金密码不能为空",trigger: "blur",},
        // ],
        // loginSafeword: [
        //   { required: true, message: "登录人资金密码不能为空", trigger: "blur" },
        // ]
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
    init(uid, m, val,userCode) {
      this.userId = uid;
      this.userCode = userCode;
      this.title = m;
      this.isShow = val;
      this.resetLock.value = this.resetLock[0].value;
      this.resetFreeze.value = this.resetFreeze[0].value;
      this.getDataList()
      this.visible = true;
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
            this.dataForm.moneyWithdraw = 0;
            this.dataFormSubmit();
          }
        });
    },
    handClose() {
      if (this.isShow) {
        this.$data.dataForm = JSON.parse(
          JSON.stringify(this.$options.data().dataForm)
        );
        this.$nextTick(() => {
          this.$refs["dataForm"].clearValidate(); // 清除表单验证
        });
      }
    },
    changeVal(val) {
      this.$forceUpdate();
    },
    // 条件查询
    searchChange(params, done) {
      this.getDataList(this.page, params, done);
    },
    // 多选变化
    selectionChange(val) {
      this.dataListSelections = val;
    },
    // 获取数据列表
    getDataList(page, params, done) {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/statistics/walletExtendsAll"),
        method: "post",
        data: this.$http.adornData(
          Object.assign(
            {
              userId: this.userCode,
            },
            params
          )
        ),
      }).then(({ data }) => {
        if (data.code == 0) {
          //usdtArr
          this.dataList = data.data.wallet_data;
          this.page.total = data.data.total;
          let a = data.data.wallet_type_arr.split(",");
          this.usdtArr = a.map((item) => {
            return {
              value: item,
              label: item,
            };
          });
          this.usdtArr.value = this.usdtArr[0].value;
          this.dataListLoading = false;
        }

        if (done) {
          done();
        }
      });
    },
    // 表单提交
    //resetType=moneryToFreeze可用->冻结金 resetType = freezeToMoney 冻结->可用金额
    // 增加账户锁定金额resetType =addLock  减少账户锁定金额resetType=subLock
    //resetType =moneryToLock可用金额->锁定金额 resetType=lockToMoney  锁定金额->可用金额
    dataFormSubmit: Debounce(function () {
      console.log(this.dataForm.loginSafeword);
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          let data = {};
          if(this.isShow == 5){
            data = {
              moneyRevise: this.dataForm.moneyRevise,
              coinType: this.usdtArr.value, // 币种
              resetType: this.resetFreeze.value,
              loginSafeword: encrypt(this.dataForm.loginSafeword),
              id: this.userId,
            };
            this.$http({
            url: this.$http.adornUrl("/exchangeApplyOrder/resetFreeze"),
            method: "get",
            params: this.$http.adornParams(data),
          }).then(({ data }) => {
            console.log(data);
            if (data.code == 1) {
              this.$message({
                message: data.msg,
                type: "error",
              });
            } else {
              this.$message({
                message: this.title + "修改成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.visible = false;
                  this.$nextTick(() => {
                    this.$emit("refreshDataList");
                  });
                },
              });
            }
          });
          }else if(this.isShow == 2 || 3 || 4){
            if (this.isShow == 2) {
            //转移锁定金额
            data = {
              moneyRevise: this.dataForm.moneyRevise,
              coinType: this.usdtArr.value, // 币种
              resetType: this.resetLock.value,
              loginSafeword: encrypt(this.dataForm.loginSafeword),
              id: this.userId,
            };
          }else if (this.isShow == 3) {
            //增加账户锁定金额
            data = {
              moneyRevise: this.dataForm.moneyRevise,
              coinType: this.usdtArr.value, // 币种
              resetType: "addLock",
              loginSafeword: encrypt(this.dataForm.loginSafeword),
              id: this.userId,
            };
          } else if (this.isShow == 4) {
            //减少账户锁定金额
            data = {
              moneyRevise: this.dataForm.moneyRevise,
              coinType: this.usdtArr.value, // 币种
              resetType: "subLock",
              loginSafeword: encrypt(this.dataForm.loginSafeword),
              id: this.userId,
            };
          }
          this.$http({
            url: this.$http.adornUrl("/exchangeApplyOrder/resetLock"),
            method: "get",
            params: this.$http.adornParams(data),
          }).then(({ data }) => {
            console.log(data);
            if (data.code == 1) {
              this.$message({
                message: data.msg,
                type: "error",
              });
            } else {
              this.$message({
                message: this.title + "修改成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.visible = false;
                  this.$nextTick(() => {
                    this.$emit("refreshDataList");
                  });
                },
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
  width: 660px;
}
.spranDiv {
  width: 100%;
  border-bottom: 1px solid rgb(240, 233, 233);
  height: 50px;
  line-height: 50px;
  font-size: 18px;
  margin-bottom: 40px;
}
</style>
