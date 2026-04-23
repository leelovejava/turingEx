<template>
  <el-dialog
    :title="!row ? '新增' : '审核'"
    :close-on-click-modal="false"
    width="1200px"
    @close="handClose"
    :visible.sync="visible"
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="150px"
    >
      <el-form-item class="titleDivTwo" label="基础信息" prop="">
      </el-form-item>
      <el-row>
        <el-col :span="8">
          <el-form-item label="订单号" prop="orderNo">
            <el-input
              v-model="dataForm.orderNo"
              placeholder="订单号"
              disabled
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="申购时间" prop="createTime">
            <el-date-picker
              v-model="dataForm.createTime"
              type="datetime"
              value-format="yyyy-MM-dd HH:mm:ss"
              placeholder="选择日期时间"
            >
            </el-date-picker>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="股票代码" prop="symbolCode">
            <el-input
              v-model="dataForm.symbolCode"
              placeholder="股票代码"
              disabled
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="8">
          <el-form-item label="股票名字" prop="symbolName">
            <el-input
              v-model="dataForm.symbolName"
              placeholder="股票名字"
              disabled
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="申购股数" prop="subNumber">
            <el-input
              v-model="dataForm.subNumber"
              placeholder="申购股数"
              type="number"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="申购股价" prop="subPrice">
            <el-input
              v-model="dataForm.subPrice"
              placeholder="申购股价"
              type="number"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="8">
          <!-- 已中签状态 -->
          <el-form-item
            v-if="dataForm.status == 2"
            label="中签股数(1)"
            prop="winningNumber1"
          >
            <el-input
              v-model="dataForm.winningNumber1"
              placeholder="中签股数"
              type="number"
            ></el-input>
          </el-form-item>
          <!-- 未中签状态 -->
          <el-form-item
            v-if="dataForm.status !== 2"
            label="中签股数"
            prop="winningNumber"
          >
            <el-input
              v-model="dataForm.winningNumber"
              placeholder="中签股数333"
              type="number"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <!-- 已中签状态 -->
          <el-form-item
            v-if="dataForm.status == 2"
            label="认缴次数(1)"
            prop="userPromiseCount1"
          >
            <el-input
              v-model="dataForm.userPromiseCount1"
              placeholder="认缴次数"
              type="number"
            ></el-input>
          </el-form-item>
          <!-- 未中签状态 -->
          <el-form-item
            label="认缴次数"
            v-if="dataForm.status !== 2"
            prop="userPromiseCount"
          >
            <el-input
              v-model="dataForm.userPromiseCount"
              placeholder="认缴次数"
              type="number"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="已认缴次数" prop="subscribedCount">
            <el-input
              disabled
              v-model="dataForm.subscribedCount"
              placeholder="已认缴次数"
              type="number"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="8">
          <el-form-item label="中签应认缴金额" prop="requiredNumber">
            <el-input
              v-model="dataForm.requiredNumber"
              disabled
              placeholder="中签应认缴金额"
              type="number"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="已认缴金额" prop="subscribedAmount">
            <el-input
              disabled
              v-model="dataForm.subscribedAmount"
              placeholder="已认缴金额"
              type="number"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="申购状态" prop="status">
            <el-select
              disabled
              class="speaInputTwo"
              v-model="dataForm.status"
              placeholder="请选择"
              @change="changeVal()"
            >
              <el-option
                v-for="item in state"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <!-- 填写日期 -->
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
      imageUrl: "",
      row: "",
      dataForm: {
        status: "",
        orderNo: "", //订单号
        createTime: "", //申购时间
        symbolCode: "", //	股票代码
        symbolName: "", //	股票名字
        subNumber: "", //	申购股数
        subPrice: "", //	申购股价
        winningNumber: "", //	中签股数
        requiredNumber: "", //	中签认缴
        subscribedCount: "", //	已认缴次数
        subscribedAmount: "", //	已认缴金额
        userPromiseCount: "", //认缴次数
        winningNumber1: "",
        userPromiseCount1: "",
      },
      state: [
        {
          label: "申购中",
          value: 1,
        },
        {
          label: "已中签",
          value: 2,
        },
        {
          label: "未中签",
          value: 3,
        },
      ],
      dataRule: {
        //{ validator: validateindex, trigger: "blur" },
        createTime: [
          { required: true, message: "申购时间不能为空", trigger: "blur" },
        ],
        subNumber: [
          { required: true, message: "申购股数不能为空", trigger: "blur" },
        ],
        subPrice: [
          { required: true, message: "申购股价不能为空", trigger: "blur" },
        ],
        // status: [
        //   { required: true, message: "状态不能为空", trigger: "blur" },
        // ],
        marketPrice: [
          { required: true, message: "市价不能为空", trigger: "blur" },
        ],
        underwritingPrice: [
          { required: true, message: "承销价不能为空", trigger: "blur" },
        ],
        subscribeTotalNumber: [
          { required: true, message: "总申购数不能为空", trigger: "blur" },
        ],
        appliedSubscribeNumber: [
          { required: true, message: "已申购数不能为空", trigger: "blur" },
        ],
        drawDate: [
          { required: true, message: "抽签日期不能为空", trigger: "blur" },
        ],
        startSubscribeDate: [
          { required: true, message: "开放申购日期不能为空", trigger: "blur" },
        ],
        endSubscribeDate: [
          { required: true, message: "截止申购日不能为空", trigger: "blur" },
        ],
        issuanceDate: [
          { required: true, message: "发劵日期不能为空", trigger: "blur" },
        ],
        weight: [{ required: true, message: "权重不能为空", trigger: "blur" }],
        defaultLimit: [
          { required: true, message: "默认额度不能为空", trigger: "blur" },
        ],
        userPromiseCount: [
          // { validator: this.customValidation, trigger: "blur" },
          { required: true, message: "认缴次数不能为空", trigger: "blur" },
        ],
        userPromiseCount1: [
          { validator: this.speanMns, trigger: "blur" },
          { required: true, message: "认缴次数不能为空", trigger: "blur" },
        ],
        winningNumber: [
          // { validator: this.customValidation2, trigger: "blur" },
          { required: true, message: "中签股数不能为空", trigger: "blur" },
        ],
        winningNumber1: [
          { validator: this.speanMns, trigger: "blur" },
          { required: true, message: "中签股数不能为空", trigger: "blur" },
        ],
        speacm: [
          { validator: this.speanMns, trigger: "blur" },
          { required: true, message: "不能小于0且不能为空", trigger: "blur" },
        ],
      },
      tempKey: -666666, // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
    };
  },
  methods: {
    init(row) {
      this.row = row || "";
      if (row) {
        //winningNumber1:'',userPromiseCount1:''
        (this.dataForm.orderNo = row.orderNo), //订单号
          (this.dataForm.createTime = row.createTime), //申购时间
          (this.dataForm.symbolCode = row.symbolCode), //	股票代码
          (this.dataForm.symbolName = row.symbolName), //	股票名字
          (this.dataForm.subNumber = row.subNumber), //	申购股数
          (this.dataForm.subPrice = row.subPrice), //	申购股价
          (this.dataForm.requiredNumber = row.requiredNumber), //	中签认缴
          (this.dataForm.subscribedCount = row.subscribedCount), //	已认缴次数
          (this.dataForm.subscribedAmount = row.subscribedAmount), //	已认缴金额
          (this.dataForm.status = row.status);
        if (row.status == 2) {
          this.dataForm.winningNumber1 = row.winningNumber;
          this.dataForm.userPromiseCount1 = row.userPromiseCount;
        } else {
          (this.dataForm.winningNumber = row.winningNumber), //	中签股数
            (this.dataForm.userPromiseCount = row.userPromiseCount); //认缴次数
        }
      }

      // this.dataForm = {...row};
      this.visible = true;
    },
    handClose() {
      this.$data.dataForm = JSON.parse(
        JSON.stringify(this.$options.data().dataForm)
      );
      this.row = "";
      this.$nextTick(() => {
        this.$refs["dataForm"].clearValidate(); // 清除表单验证
      });
    },
    changeVal(val) {
      this.$forceUpdate();
    },
    customValidation(rule, value, callback) {
      // 自定义验证逻辑
      if (this.dataForm.winningNumber * 1 !== 0 && value == 0) {
        callback(new Error("中签股数不为0时,认缴次数不能为0")); // 验证失败，返回错误信息
      } else {
        callback(); // 验证通过
      }
    },
    customValidation2(rule, value, callback) {
      // 自定义验证逻辑
      if (this.dataForm.userPromiseCount * 1 == 0 && value > 0) {
        callback(new Error("中签股数不为0时,认缴次数不能为0")); // 验证失败，返回错误信息
      } else {
        callback(); // 验证通过
      }
    },
    speanMns(rule, value, callback) {
      // 自定义验证逻辑
      if (value <= 0) {
        callback(new Error("不能小于0")); // 验证失败，返回错误信息
      } else {
        callback(); // 验证通过
      }
    },
    // 表单提交
    dataFormSubmit: Debounce(function () {
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          let a, b;
          if (this.dataForm.status == 2) {
            a = this.dataForm.winningNumber1;
            b = this.dataForm.userPromiseCount1;
          } else {
            a = this.dataForm.winningNumber;
            b = this.dataForm.userPromiseCount;
          }
          this.$http({
            url: this.$http.adornUrl("/applyNewSharesOrder/update"),
            method: "post",
            data: this.$http.adornData({
              orderNo: this.dataForm.orderNo, //订单号
              createTime: this.dataForm.createTime, //申购时间
              symbolCode: this.dataForm.symbolCode, //	股票代码
              symbolName: this.dataForm.symbolName, //	股票名字
              subNumber: this.dataForm.subNumber, //	申购股数
              subPrice: this.dataForm.subPrice, //	申购股价
              winningNumber: a, //	中签股数
              requiredNumber: this.dataForm.requiredNumber, //	中签认缴
              subscribedCount: this.dataForm.subscribedCount, //	已认缴次数
              subscribedAmount: this.dataForm.subscribedAmount, //	已认缴金额
              userPromiseCount: b,
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
                onClose: () => {},
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
.titleDivTwo {
  height: 40px;
  border-left: 3px solid #1c4efa;
  background: #f4f7ff;
}
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
.speaInputTwo {
  width: 240px;
}
</style>
