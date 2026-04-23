<template>
  <el-dialog
    title="修改永续合约"
    :close-on-click-modal="false"
    :visible.sync="visible"
    @close="handClose"
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="140px"
    >
      <el-form-item label="名称" prop="name">
        <el-input v-model="dataForm.name" placeholder="名称"></el-input>
      </el-form-item>
      <el-form-item label="登录人资金密码" prop="loginSafeword">
        <el-input
          v-model="dataForm.loginSafeword"
          type="password"
          placeholder="登录人资金密码"
        ></el-input>
      </el-form-item>
      <el-form-item label="代码" prop="symbol">
        <el-input
          v-model="dataForm.symbol"
          disabled
          placeholder="代码"
        ></el-input>
      </el-form-item>
      <el-form-item label="保留精度" prop="decimals">
        <el-input
          class="spann"
          v-model="dataForm.decimals"
          placeholder="位"
        ></el-input>
        <el-input class="spamm" placeholder="位" disabled></el-input>
      </el-form-item>
      <el-form-item label="交易对" prop="symbol">
        <el-input
          v-model="dataForm.symbol"
          disabled
          placeholder="交易对"
        ></el-input>
      </el-form-item>
      <el-form-item label="交易信息">
        <div class="green">
          盈亏公式：(合约总金额
          /每张金额)*(涨跌点数/最小变动单位*最小变动单位的盈亏金额)。
        </div>
      </el-form-item>
      <el-form-item label="金额/张" prop="unitAmount">
        <el-input
          v-model="dataForm.unitAmount"
          placeholder="金额/张"
        ></el-input>
      </el-form-item>
      <el-form-item label="合约手续费(百分比)" prop="unitPercentage">
        <el-input
          v-model="dataForm.unitPercentage"
          placeholder="合约手续费(百分比)"
          type="number"
        ></el-input>
      </el-form-item>
      <el-form-item label="手续费/张" prop="unitFee">
        <el-input v-model="dataForm.unitFee" placeholder="手续费/张"></el-input>
      </el-form-item>
      <el-form-item label="最小变动单位" prop="pips">
        <el-input v-model="dataForm.pips" placeholder="最小变动单位"></el-input>
      </el-form-item>
      <el-form-item>
        <div class="green">
          报价变动的最小幅度，行情低于设置单位不会计较盈亏
        </div>
      </el-form-item>
      <el-form-item label="最小变动单位的单位盈亏" prop="pipsAmount">
        <el-input
          v-model="dataForm.pipsAmount"
          placeholder="最小变动单位的单位盈亏"
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
import { treeDataTranslate } from "@/utils";
import { Debounce } from "@/utils/debounce";
import { encrypt } from "@/utils/crypto";
export default {
  data() {
    return {
      visible: false,
      menuList: [],
      menuListTreeProps: {
        label: "name",
        children: "children",
      },
      row: "",
      options: [], // 模块
      langug: [], // 语言
      dataForm: {
        loginSafeword: "",
        symbol: "",
        uuid: "",
        name: "",
        decimals: "", //精度
        unitAmount: "", //金额
        unitFee: "", //手续费
        pips: "", //最小变动单位
        pipsAmount: "",
        unitPercentage:'',//合约手续费(百分比)
      },
      dataRule: {
        name: [{ required: true, message: "名称不能为空", trigger: "blur" }],
        loginSafeword: [
          {
            required: true,
            message: "登录人资金密码不能为空",
            trigger: "blur",
          },
        ],
        unitAmount: [
          { required: true, message: "金额不能为空", trigger: "blur" },
        ],
        unitFee: [
          { required: true, message: "手续费不能为空", trigger: "blur" },
        ],
        pips: [
          { required: true, message: "最小变动单位不能为空", trigger: "blur" },
        ],
        pipsAmount: [
          {
            required: true,
            message: "最小变动单位的单位盈亏不能为空",
            trigger: "blur",
          },
        ],
        decimals: [
          { required: true, message: "精度不能为空", trigger: "blur" },
        ],
      },
      tempKey: -666666, // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
    };
  },
  methods: {
    init(row) {
      if (row) {
        this.dataForm = { ...row };
      } else {
      }
      this.visible = true;
    },
    // Open(call) {
    //   this.$prompt("登录人资金密码", "提示", {
    //     confirmButtonText: "确定",
    //     cancelButtonText: "取消",
    //   })
    //     .then(({ value }) => {
    //       this.dataForm.loginSafeword = value;
    //       if (call) {
    //         call();
    //       }
    //     })
    //     .catch(() => {
    //       this.$message({
    //         type: "info",
    //         message: "取消输入",
    //       });
    //     });
    // },
    changeVal(val) {
      this.$forceUpdate();
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
            this.$http({
              // 修改
              url: this.$http.adornUrl(
                `/normal/adminItemAction!/update.action`
              ),
              method: "post",
              data: this.$http.adornData({
                loginSafeword:encrypt(this.dataForm.loginSafeword),
                symbol:this.dataForm.symbol,
                uuid: this.dataForm.uuid,
                name: this.dataForm.name,
                decimals: this.dataForm.decimals, //精度
                unitAmount: this.dataForm.unitAmount, //金额
                unitFee: this.dataForm.unitFee, //手续费
                pips: this.dataForm.pips, //最小变动单位
                pipsAmount: this.dataForm.pipsAmount,
                unitPercentage:this.dataForm.unitPercentage
              }),
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
                  onClose: () => {
                   
                  },
                });
              }
            });
          }
        });
    }),
  },
};
</script>
<style scoped>
.spann {
  width: 90%;
}
.spamm {
  width: 10%;
}
</style>
