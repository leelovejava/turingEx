<template>
  <el-dialog
    :title="row ? '修改行情品种' : '新增行情品种'"
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
    <el-form-item label="名称" prop="name">
        <el-input
          v-model="dataForm.name"
          placeholder="交易币种"
        ></el-input> </el-form-item
      >
      <el-form-item label="代码" prop="symbol">
        <el-input 
          v-model="dataForm.symbol" disabled
          placeholder="代码"
        ></el-input> </el-form-item
      >
    <!-- <el-form-item label="代码" prop="symbol">
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
      </el-form-item> -->
<el-form-item label="保留精度(位)" prop="decimals">
        <el-input
          v-model="dataForm.decimals"
          placeholder="保留精度(位)"
        ></el-input> </el-form-item
      ><el-form-item label="交易量倍数(倍)" prop="multiple">
        <el-input
          v-model="dataForm.multiple"
          placeholder="交易量倍数(倍)"
        ></el-input> </el-form-item
      ><el-form-item label="借贷利率(%)" prop="borrowingRate">
        <el-input
          v-model="dataForm.borrowingRate"
          placeholder="借贷利率(%)"
        ></el-input> </el-form-item>
        <el-form-item label="交易对" prop="symbol">
        <el-input 
          v-model="dataForm.symbol" disabled
          placeholder="交易对"
        ></el-input> </el-form-item
      >
      <!-- <el-form-item label="交易对" prop="symbol">
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
      </el-form-item> -->
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
      row: "",  //forex->外汇,commodities->大宗商品，指数/ETF->indices, A-stocks->A股, TW-stocks->港股.US-stocks->美股，cryptos->虚拟货币
      options: [{
        value:'forex',
        label:'外汇',
      },{
        value:'commodities',
        label:'大宗商品',
      },{
        value:'indices',
        label:'指数/ETF',
      },{
        value:'A-stocks',
        label:'A股',
      },{
        value:'TW-stocks',
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
        name: [{ required: true, message: "名称不能为空", trigger: "blur" }],
        decimals: [{ required: true, message: "精度不能为空", trigger: "blur" }],
        multiple: [{ required: true, message: "交易量倍数不能为空", trigger: "blur" }],
        borrowingRate: [{ required: true, message: "借贷利率不能为空", trigger: "blur" }],
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
                symbol: this.dataForm.symbol,
                name:this.dataForm.name,
                decimals:this.dataForm.decimals,
                multiple:this.dataForm.multiple,
                borrowingRate:this.dataForm.borrowingRate,
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
                symbol: this.dataForm.symbol,
                name:this.dataForm.name,
                decimals:this.dataForm.decimals,
                multiple:this.dataForm.multiple,
                borrowingRate:this.dataForm.borrowingRate,
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
