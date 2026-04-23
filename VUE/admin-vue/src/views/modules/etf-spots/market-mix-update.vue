<template>
  <el-dialog
    title="币种状态设置"
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
        <el-input v-model="dataForm.symbol" disabled placeholder="交易对"></el-input>
      </el-form-item>
      <el-form-item label="状态" prop="enable">
        <el-radio-group v-model="dataForm.enable">
          <el-radio :label="1">启用(上架)</el-radio>
          <el-radio :label="0">禁止(下架)</el-radio>
        </el-radio-group>
      </el-form-item>  
        <el-form-item label="前端显示" prop="showStatus">
          <el-radio-group v-model="dataForm.showStatus">
          <el-radio :label="'1'">显示</el-radio>
          <el-radio :label="'0'">隐藏</el-radio>
        </el-radio-group>
          </el-form-item>
          <el-form-item label="是否可交易" prop="tradeStatus">
            <el-radio-group v-model="dataForm.tradeStatus">
          <el-radio :label="'1'">是</el-radio>
          <el-radio :label="'0'">否</el-radio>
        </el-radio-group>
        </el-form-item>
      <el-form-item label="市价买" prop="canBuyAtMarketPrice">
        <el-radio-group v-model="dataForm.canBuyAtMarketPrice">
          <el-radio :label="'1'">是</el-radio>
          <el-radio :label="'0'">否</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="市价卖" prop="canSellAtMarketPrice">
        <el-radio-group v-model="dataForm.canSellAtMarketPrice">
          <el-radio :label="'1'">是</el-radio>
          <el-radio :label="'0'">否</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="限价可买" prop="limitCanBuy">
        <el-radio-group v-model="dataForm.limitCanBuy">
          <el-radio :label="'1'">是</el-radio>
          <el-radio :label="'0'">否</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="限价可卖" prop="limitCanSell">
        <el-radio-group v-model="dataForm.limitCanSell">
          <el-radio :label="'1'">是</el-radio>
          <el-radio :label="'0'">否</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="排序" prop="sorted">
        <el-input
          v-model="dataForm.sorted"
          placeholder="排序"
        ></el-input> </el-form-item>
        <el-form-item label="最高买单价" prop="maxmumPrice">
        <el-input
          v-model="dataForm.maxmumPrice"
          placeholder="最高买单价"
        ></el-input> </el-form-item>
      <el-form-item label="最小下单价" prop="minimumPrice">
        <el-input
          v-model="dataForm.minimumPrice"
          placeholder="最小下单价"
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
       
      }],
      langug: [], // 语言
      dataForm: {
      },
      dataRule: {
        sorted: [{ required: true, message: "排序不能为空", trigger: "blur" }],
        decimals: [{ required: true, message: "精度不能为空", trigger: "blur" }],
        maxmumOrder: [{ required: true, message: "最大下单量不能为空", trigger: "blur" }],
        maxmumPrice: [{ required: true, message: "最高买单价不能为空", trigger: "blur" }],
        minimumOrder: [{ required: true, message: "最小下单量不能为空", trigger: "blur" }],
        minimumPrice: [{ required: true, message: "最低卖单价不能为空", trigger: "blur" }],
        symbol: [{ required: true, message: "交易对不能为空", trigger: "blur" }],
        pipsAmount: [{ required: true, message: "最小挂单金额不能为空", trigger: "blur" }],
        quoteCurrency: [{ required: true, message: "结算币种不能为空", trigger: "blur" }],
        unitFee: [{ required: true, message: "手续费不能为空", trigger: "blur" }],
        canBuyAtMarketPrice: [{ required: true, message: "市价买不能为空", trigger: "blur" }],
        canSellAtMarketPrice: [{ required: true, message: "市价卖不能为空", trigger: "blur" }],
        tradeStatus: [{ required: true, message: "交易状态不能为空", trigger: "blur" }],
        limitCanBuy: [{ required: true, message: "限价可买不能为空", trigger: "blur" }],
        limitCanSell: [{ required: true, message: "限价可卖不能为空", trigger: "blur" }],
        enable: [{ required: true, message: "状态不能为空", trigger: "blur" }],
        showStatus: [{ required: true, message: "前端显示不能为空", trigger: "blur" }],
      },
      tempKey: -666666, // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
    };
  },
  methods: {
    init(row) {
      this.row = row || "";
      if (row) {
        this.dataForm = {...row}
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
              url: this.$http.adornUrl(`/normal/adminItemAction!/update.action`),
              method: "post",
              data: this.$http.adornData({
                tradeStatus: this.dataForm.tradeStatus,
                symbol: this.dataForm.symbol,
                sorted: this.dataForm.sorted,
                showStatus: this.dataForm.showStatus,
                minimumPrice: this.dataForm.minimumPrice,
                maxmumPrice: this.dataForm.maxmumPrice,
                uuid: this.dataForm.uuid,
                limitCanSell: this.dataForm.limitCanSell,
                limitCanBuy: this.dataForm.limitCanBuy,
                enable: this.dataForm.enable,
                canSellAtMarketPrice: this.dataForm.canSellAtMarketPrice,
                canBuyAtMarketPrice: this.dataForm.canBuyAtMarketPrice,
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
              url: this.$http.adornUrl(`/cms/add`),
              method: "post",
              data: this.$http.adornData({
                content: this.dataForm.content,
                contentCode: this.dataForm.contentCode,
                language: this.langug.value,
                loginSafeword: encrypt(this.dataForm.loginSafeword),
                model: this.options.value,
                title: this.dataForm.title,
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
                    this.visible = false;
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
