<template>
  <el-dialog
    :title="row ? '修改合约参数' : '新增合约参数'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    @close = 'handClose'
    append-to-body
    width="800px"
    class="transport-dialog"
  >
    <el-form
      :model="dataForm"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="110px"
      :rules="dataRule"
    >
      <el-form-item label="">
        <div class="green">交割收益：交割收益计算方式（0~100百分制）</div>
      </el-form-item>
      <el-form-item label="交割合约代码">
        <el-select
          v-model="options.value"
          placeholder="请选择"
          :disabled = "row?true:false"
          @change="changeVal()"
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
      <el-form
        :inline="true"
        :model="dataForm"
        :rules="dataRule"
        ref="dataForm"
      >
        <el-form-item label="时间" prop="timenum" label-width="110px">
          <el-input
            class="spamm"
            v-model="dataForm.timenum"
            placeholder="时间"
          ></el-input>
        </el-form-item>
        <el-form-item label-width="110px">
          <el-select
            class="spann selfloat"
            v-model="optionsTwo.value"
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
      </el-form>
      <el-form
        :inline="true"
        :model="dataForm"
        :rules="dataRule"
        ref="dataForm"
      >
        <el-form-item label="交割收益" prop="profitRatio" label-width="110px">
          <el-input
            class="spamm"
            v-model="dataForm.profitRatio"
            placeholder=""
          ></el-input>
          <el-input class="spannn" placeholder="~" disabled></el-input>
        </el-form-item>
        <el-form-item label="" prop="profitRatioMax" label-width="110px">
          <el-input
            class="spamm"
            v-model="dataForm.profitRatioMax"
            placeholder=""
          ></el-input>
          <el-input class="spannn" placeholder="%" disabled></el-input>
        </el-form-item>
      </el-form>
      <el-form-item label="最低购买金额" prop="unitAmount" label-width="110px">
        <el-input
          class="spammm"
          v-model="dataForm.unitAmount"
          placeholder="最低购买金额"
        ></el-input>
      </el-form-item>
      <el-form-item label="收益率基数" prop="profitRatioCardinality" label-width="110px">
        <el-input
          class="spammm"
          type="number"
          v-model="dataForm.profitRatioCardinality"
          placeholder="收益率基数"
        ></el-input>
      </el-form-item>
      <el-form-item label="手续费" prop="unitFee" label-width="110px">
        <el-input
          class="spammm"
          v-model="dataForm.unitFee"
          placeholder="手续费"
        ></el-input>
        <el-input class="spannn" placeholder="%" disabled></el-input>
      </el-form-item>
      <el-form-item label="资金密码" prop="loginSafeword" label-width="110px">
        <el-input
          class="spammm"
          v-model="dataForm.loginSafeword"
          placeholder="请输入登录人资金密码"
          type="password"
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
// import AddOrUpdate from './transcity-add-or-update'
export default {
  data() {
    return {
      // hasFreeCondition: 0,
      visible: false,
      second: 0,
      mafterValue: 0,
      options: [],
      optionsTwo: [
        {
          label: "秒",
          value: "second",
        },
        {
          label: "分",
          value: "minute",
        },
        {
          label: "时",
          value: "hour",
        },
        {
          label: "天",
          value: "day",
        },
      ],
      row: "",
      addOrUpdateVisible: false,
      dataForm: {
        loginSafeword: "",
        profitRatio: "",
        profitRatioMax: "",
        timenum: "",
        timeunit: "", //时间单位
        uuid: "",
        unitFee: "",
        unitAmount: "",
        profitRatioCardinality:'',
      },
      dataRule: {
        loginSafeword: [
          { required: true, message: "资金密码不能为空", trigger: "blur" },
        ],
        profitRatio: [
          { required: true, message: "交割收益不能为空", trigger: "blur" },
        ],
        profitRatioMax: [
          { required: true, message: "交割收益不能为空", trigger: "blur" },
        ],
        timenum: [{ required: true, message: "时间不能为空", trigger: "blur" }],
        unitFee: [
          { required: true, message: "手续费不能为空", trigger: "blur" },
        ],
        profitRatioCardinality: [
          { required: true, message: "收益率基数不能为空", trigger: "blur" },
        ],
        unitAmount: [
          { required: true, message: "最低购买金额不能为空", trigger: "blur" },
        ],
        remark: [
          {
            required: false,
            pattern: /\s\S+|S+\s|\S/,
            message: "输入格式有误",
            trigger: "blur",
          },
        ],
      },
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
      editVisible: false,
    };
  },

  components: {
    // AddOrUpdate
  },
  computed: {},
  methods: {
    init(arr,row) {
      this.row = row || "";
      this.options = arr || []
      console.log(row)
      if (row) {
        this.dataForm.profitRatio = row.profitRatio;
        this.dataForm.profitRatioMax = row.profitRatioMax;
        this.options.value = row.symbol;
        this.dataForm.timenum = row.timenum;
        this.optionsTwo.value = row.timeunit;
        this.dataForm.unitAmount = row.unitAmount;
        this.dataForm.unitFee = row.unitFee;
        this.dataForm.unitMaxAmount = row.unitMaxAmount;
        this.dataForm.uuid = row.uuid;
        this.dataForm.profitRatioCardinality = row.profitRatioCardinality
        console.log(this.dataForm.unitAmount)
      } else {
        this.options.value = this.options[0].value;
         this.optionsTwo.value = this.optionsTwo[0].value;
      }
      // this.$nextTick(() => {
      //   this.$refs.dataForm.resetFields();
      // });
      this.visible = true;
    },
    changeVal(val) {
      this.$forceUpdate();
    },
    handClose(){
        this.$data.dataForm=JSON.parse(JSON.stringify(this.$options.data().dataForm))
     this.$nextTick(() => {
            this.$refs['dataForm'].clearValidate() // 清除表单验证
          })
      },
    // 表单提交
    dataFormSubmit: Debounce(function () {
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          let n = this.$http.adornData({
            //修改
            loginSafeword: encrypt(this.dataForm.loginSafeword),
            profitRatio: this.dataForm.profitRatio,
            profitRatioMax: this.dataForm.profitRatioMax,
            symbol: this.options.value,
            timenum: this.dataForm.timenum,
            timeunit: this.optionsTwo.value,
            unitAmount: this.dataForm.unitAmount,
            unitFee: this.dataForm.unitFee,
            unitMaxAmount: this.dataForm.unitMaxAmount,
            uuid: this.dataForm.uuid,
            profitRatioCardinality:this.dataForm.profitRatioCardinality
          });
          let m = this.$http.adornData({
            //新增
            loginSafeword: encrypt(this.dataForm.loginSafeword),
            profitRatio: this.dataForm.profitRatio,
            profitRatioMax: this.dataForm.profitRatioMax,
            symbol: this.options.value,
            timenum: this.dataForm.timenum,
            timeunit: this.optionsTwo.value,
            unitAmount: this.dataForm.unitAmount,
            unitFee: this.dataForm.unitFee,
            unitMaxAmount: this.dataForm.unitMaxAmount,
            profitRatioCardinality:this.dataForm.profitRatioCardinality
          });
          let data = this.row ? n : m;
          this.$http({
            url: this.$http.adornUrl(
              `/normal/adminContractManageAction!/addFutures.action`
            ),
            method: "post",
            data: data,
          }).then(({ data }) => {
            if (data.code == 0) {
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1000,
                onClose: () => {
                  this.visible = false;
                  this.$emit("refreshDataList", this.page);
                },
              });
            } else {
              this.$message({
                message: data.msg,
                type: "error",
                duration: 1000,
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

<style lang="scss" scoped>
.transport-dialog .table-con .el-form-item {
  margin-top: 16px;
}
.spamm {
  width: 200px;
}
.spammm {
  width: 500px;
}
.spann {
  width: 80px;
}
.spannn {
  width: 50px;
}
</style>
