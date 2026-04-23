<template>
  <el-dialog
    :title="row ? '修改交易对' : '新增交易对'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    @close="handClose"
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="120px"
    >
    <el-form-item label="交易对" prop="symbol">
        <el-select
        disabled
          v-model="langug.symbol"
          placeholder="请选择"
          @change="changeVal()"
        >
          <el-option
            v-for="item in langug"
            :key="item.symbol"
            :label="item.name"
            :value="item.symbol"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="交易币种" prop="symbolData">
        <el-input
        disabled
          v-model="dataForm.symbolData"
          placeholder="交易币种"
        ></el-input> </el-form-item
      ><el-form-item label="结算币种" prop="quoteCurrency">
        <el-input
        disabled
          v-model="dataForm.quoteCurrency"
          placeholder="结算币种"
        ></el-input> </el-form-item
      ><el-form-item label="手续费" prop="unitFee">
        <el-input
          v-model="dataForm.unitFee"
          placeholder="手续费"
        ></el-input> </el-form-item
      ><el-form-item label="币种价格精度" prop="decimals">
        <el-input
          v-model="dataForm.decimals"
          placeholder="币种价格精度"
        ></el-input> </el-form-item
      ><el-form-item label="最低卖单价" prop="minimumPrice">
        <el-input
          v-model="dataForm.minimumPrice"
          placeholder="最低卖单价"
        ></el-input> </el-form-item
      ><el-form-item label="最高买单价" prop="maxmumPrice">
        <el-input
          v-model="dataForm.maxmumPrice"
          placeholder="最高买单价"
        ></el-input> </el-form-item
      ><el-form-item label="最小下单量" prop="minimumOrder">
        <el-input
          v-model="dataForm.minimumOrder"
          placeholder="最小下单量"
        ></el-input> </el-form-item
      ><el-form-item label="最大下单量" prop="maxmumOrder">
        <el-input
          v-model="dataForm.maxmumOrder"
          placeholder="最大下单量"
        ></el-input> </el-form-item
      >      
      <el-form-item label="交易模块">
        <el-select
          v-model="options.value"
          placeholder="请选择"
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
        <el-form-item label="排序" prop="sorted">
        <el-input
          v-model="dataForm.sorted"
          placeholder="排序"
        ></el-input> </el-form-item>
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
      row: "",  //forex->外汇,commodities->大宗商品，指数/ETF->indices, A-stocks->A股, HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币
      options: [{
        value:'forex',
        label:'外汇',
      },{
        value:'commodities',
        label:'大宗商品',
      },{
        value:'indices',
        label:'ETF',
      },{
        value:'A-stocks',
        label:'A股',
      },{
        value:'HK-stocks',
        label:'港股',
      },{
        value:'US-stocks',
        label:'美股',
      },{
        value:'cryptos',
        label:'虚拟货币',
      }],
      langug: [], // 币对
      dataForm: {
      },
      dataRule: {
        sorted: [{ required: true, message: "排序不能为空", trigger: "blur" }],
        decimals: [{ required: true, message: "精度不能为空", trigger: "blur" }],
        maxmumOrder: [{ required: true, message: "最大下单量不能为空", trigger: "blur" }],
        maxmumPrice: [{ required: true, message: "最高买单价不能为空", trigger: "blur" }],
        minimumOrder: [{ required: true, message: "最小下单量不能为空", trigger: "blur" }],
        minimumPrice: [{ required: true, message: "最低卖单价不能为空", trigger: "blur" }],
        //symbol: [{ required: true, message: "交易对不能为空", trigger: "blur" }],
        pipsAmount: [{ required: true, message: "最小挂单金额不能为空", trigger: "blur" }],
        quoteCurrency: [{ required: true, message: "结算币种不能为空", trigger: "blur" }],
        unitFee: [{ required: true, message: "手续费不能为空", trigger: "blur" }],
        // sorted: [{ required: true, message: "排序不能为空", trigger: "blur" }],
        // sorted: [{ required: true, message: "排序不能为空", trigger: "blur" }],
        // sorted: [{ required: true, message: "排序不能为空", trigger: "blur" }],

      },
      tempKey: -666666, // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
    };
  },
  methods: {
    init(arr,row) {
      this.langug = arr;
      this.row = row || "";
      if (row) {
        if(row.type){
          this.options.value = row.type
        }else{
          this.options.value = this.options[0].value;
        }
        this.dataForm = row
        this.langug.symbol = row.symbol;
        this.dataForm.id = row.uuid;
      } else {
        this.options.value = this.options[0].value;
        this.langug.symbol = this.langug[0].symbol;
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
      this.langug.value = "";
      this.options.value = "";
    },
    // 表单提交
    dataFormSubmit: Debounce(function () {
      if (this.row) {
        this.$refs["dataForm"].validate((valid) => {
          if (valid) {
            this.$http({
              // 修改
              url: this.$http.adornUrl(`/etf/klineConfig/addItem`),
              method: "post",
              data: this.$http.adornData({
                symbol: this.langug.symbol,
                symbolData: this.dataForm.symbolData,
                quoteCurrency: this.dataForm.quoteCurrency,
                unitFee: this.dataForm.unitFee,
                decimals: this.dataForm.decimals,
                minimumPrice: this.dataForm.minimumPrice,
                maxmumPrice: this.dataForm.maxmumPrice,
                minimumOrder: this.dataForm.minimumOrder,
                maxmumOrder: this.dataForm.maxmumOrder,
                type: this.options.value,
                uuid: this.dataForm.id,
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
      } else {
        this.$refs["dataForm"].validate((valid) => {
          if (valid) {
            this.$http({
              // 新增
              url: this.$http.adornUrl(`/etf/klineConfig/addItem`),
              method: "post",
              data: this.$http.adornData({
                symbol: this.langug.symbol,
                symbolData: this.dataForm.symbolData,
                quoteCurrency: this.dataForm.quoteCurrency,
                unitFee: this.dataForm.unitFee,
                decimals: this.dataForm.decimals,
                minimumPrice: this.dataForm.minimumPrice,
                maxmumPrice: this.dataForm.maxmumPrice,
                minimumOrder: this.dataForm.minimumOrder,
                maxmumOrder: this.dataForm.maxmumOrder,
                type: this.options.value,
                //uuid: this.dataForm.id,
                //uuid: this.dataForm.id,
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
      }
    }),
  },
};
</script>
