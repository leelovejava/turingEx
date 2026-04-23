<template>
  <el-dialog
    :title="row ? '修改行情时间段设置' : '新增行情时间段设置'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    append-to-body
    @close="handClose"
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="120px"
    >
      <el-form-item label="开始时间" prop="openTimeTs">
        <el-date-picker
          v-model="dataForm.openTimeTs"
          type="datetime"
          value-format="yyyy-MM-dd HH:mm:ss"
          placeholder="选择日期时间"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item label="结束时间" prop="closeTimeTs">
        <el-date-picker
          v-model="dataForm.closeTimeTs"
          type="datetime"
          value-format="yyyy-MM-dd HH:mm:ss"
          placeholder="选择日期时间"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item label="交易对" prop="symbol">
        <el-select
          v-model="options.symbol"
          placeholder="请选择"
          @change="changeVal()"
        >
          <el-option
            v-for="item in options"
            :key="item.symbol"
            :label="item.name"
            :value="item.symbol"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="成交量" prop="turnover">
        <el-input v-model="dataForm.turnover" placeholder="成交量"></el-input>
      </el-form-item>
      <el-form-item label="当前价格" prop="close" v-if="row">
        <el-input
          disabled
          v-model="dataForm.close"
          placeholder="当前价格"
        ></el-input> </el-form-item>
      <el-form-item label="最高价格" prop="high">
        <el-input
          v-model="dataForm.high"
          placeholder="最高价格"
        ></el-input> </el-form-item
      ><el-form-item label="最低价格" prop="low">
        <el-input v-model="dataForm.low" placeholder="最低价格"></el-input>
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
      row: "", //forex->外汇,commodities->大宗商品，指数/ETF->indices, A-stocks->A股, HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币
      options: [
        {
          value: "forex",
          label: "外汇",
        },
        {
          value: "commodities",
          label: "大宗商品",
        },
        {
          value: "indices",
          label: "指数/ETF",
        },
        {
          value: "A-stocks",
          label: "A股",
        },
        {
          value: "HK-stocks",
          label: "港股",
        },
        {
          value: "US-stocks",
          label: "美股",
        },
        {
          value: "cryptos",
          label: "虚拟货币",
        },
      ],
      langug: [], // 语言
      dataForm: {
        closeTimeTs: "",
        openTimeTs: "",
        high: "",
        low: "",
        turnover: "",
        uuid: "",
      },

      dataRule: {
        sorted: [{ required: true, message: "排序不能为空", trigger: "blur" }],
        decimals: [
          { required: true, message: "精度不能为空", trigger: "blur" },
        ],
        maxmumOrder: [
          { required: true, message: "最大下单量不能为空", trigger: "blur" },
        ],
        maxmumPrice: [
          { required: true, message: "最高买单价不能为空", trigger: "blur" },
        ],
        minimumOrder: [
          { required: true, message: "最小下单量不能为空", trigger: "blur" },
        ],
        minimumPrice: [
          { required: true, message: "最低卖单价不能为空", trigger: "blur" },
        ],
        high: [
          { required: true, message: "最高价格不能为空", trigger: "blur" },
        ],
        low: [{ required: true, message: "最低价格不能为空", trigger: "blur" }],
        pipsAmount: [
          { required: true, message: "最小挂单金额不能为空", trigger: "blur" },
        ],
        quoteCurrency: [
          { required: true, message: "结算币种不能为空", trigger: "blur" },
        ],
        unitFee: [
          { required: true, message: "手续费不能为空", trigger: "blur" },
        ],
        openTimeTs: [
          { required: true, message: "开始时间不能为空", trigger: "blur" },
        ],
        closeTimeTs: [
          { required: true, message: "结束时间不能为空", trigger: "blur" },
        ],
        turnover: [
          { required: true, message: "成交量不能为空", trigger: "blur" },
        ],
        // sorted: [{ required: true, message: "排序不能为空", trigger: "blur" }],
      },
      tempKey: -666666, // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
    };
  },
  methods: {
    init(arr, row) {
      this.options = arr;
      this.row = {...row} || "";
      console.log(row)
      if (row) {
        this.options.symbol = row.symbol;
        this.dataForm = row
        console.log(row.openTimeTs)
        if (typeof row.openTimeTs !== 'number') {
          this.dataForm.openTimeTs =row.openTimeTs
          this.dataForm.closeTimeTs = row.closeTimeTs
          }else{
            this.dataForm.openTimeTs = this.formatTimestamp(row.openTimeTs)
            this.dataForm.closeTimeTs = this.formatTimestamp(row.closeTimeTs)
          }     
      } else {
        this.options.symbol = this.options[0].symbol;
        // this.langug.value = this.langug[0].value;
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
    formatTimestamp(timestamp) {
  const date = new Date(timestamp)
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');
  const milliseconds = String(date.getMilliseconds()).padStart(3, '0');
  
  const formattedDate = `${year}-${month}-${day} ${hours}:${minutes}:${seconds} ${milliseconds}`;
  return formattedDate;
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
      let a = new Date(this.dataForm.openTimeTs);
      let b = new Date(this.dataForm.closeTimeTs);
      let aa = a.getTime();
      let bb = b.getTime();
      if (this.row) {
        this.$refs["dataForm"].validate((valid) => {
          if (valid) {
            this.$http({
              // 修改
              url: this.$http.adornUrl(`/klineStageConfig/add`),
              method: "post",
              data: this.$http.adornData({
                closeTimeTs: bb,
                openTimeTs: aa,
                symbol: this.options.symbol,
                high: this.dataForm.high,
                low: this.dataForm.low,
                turnover: this.dataForm.turnover,
                uuid:this.dataForm.uuid,
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
      } else {
        this.$refs["dataForm"].validate((valid) => {
          if (valid) {
            this.$http({
              // 新增
              url: this.$http.adornUrl(`/klineStageConfig/add`),
              method: "post",
              data: this.$http.adornData({
                closeTimeTs: bb,
                openTimeTs: aa,
                symbol: this.options.symbol,
                name:this.options.symbol,
                high: this.dataForm.high,
                low: this.dataForm.low,
                turnover: this.dataForm.turnover,
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
