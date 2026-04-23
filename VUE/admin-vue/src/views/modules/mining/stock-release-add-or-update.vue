<template>
  <el-dialog
    :title="!row ? '新增' : '修改'"
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
          <!-- <el-form-item label="申购代码(字母)" prop="productName">
            <el-input
              v-model="dataForm.productName"
              placeholder="申购代码(字母)"
              :disabled="row ? true : false"
              @change="openMsg()"
            ></el-input>
          </el-form-item> -->
          <el-form-item label="申购代码(字母)" prop="productName">
            <el-select
            class="searchInput"
              v-model="dataForm.productName"
              filterable
              allow-create
              remote
              clearable
              reserve-keyword
              placeholder="请输入关键词"
              :remote-method="loadOptions"
              @change="openMsg()"
              :loading="loading"
            >
              <el-option
                v-for="item in symbolDate"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8" v-if="!dataForm.id">
          <el-form-item label="股票类型" prop="">
            <el-select class="searchInput" v-model="options.value" @change="changeVal"
                     placeholder="请选择">
              <el-option v-for="item in options"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="股票简称" prop="">
            <el-input
              v-model="dataForm.name"
              placeholder="股票简称"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8" v-if="dataForm.id">
          <el-form-item label="提示" prop="">
            <div class="green">输入申购代码获取股票简称</div>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="8">
          <el-form-item label="发行价" prop="marketPrice">
            <el-input
              v-model="dataForm.marketPrice"
              type="number"
              maxlength="11"
              placeholder="发行价"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="承销价" prop="underwritingPrice">
            <el-input
              type="number"
              maxlength="11"
              v-model="dataForm.underwritingPrice"
              placeholder="承销价"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="发行总数" prop="subscribeTotalNumber">
            <el-input
              type="number"
              maxlength="11"
              v-model="dataForm.subscribeTotalNumber"
              placeholder="发行总数"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="8">
          <el-form-item label="已申购数" prop="">
            <el-input
              type="number"
              maxlength="11"
              disabled
              v-model="dataForm.appliedSubscribeNumber"
              placeholder="已申购数"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="排序" prop="weight">
            <el-input
              v-model="dataForm.weight"
              maxlength="11"
              type="number"
              placeholder="排序"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="用户申购额度" prop="defaultLimit">
            <el-input
              type="number"
              maxlength="11"
              v-model="dataForm.defaultLimit"
              placeholder="用户申购额度"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="8">
          <el-form-item label="发行市场" prop="ipoStatus">
            <el-select
              class="speaInputTwo"
              v-model="dataForm.ipoStatus"
              placeholder="请选择"
              @change="changeVal()"
            >
              <el-option
                v-for="item in direction"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="锁定时间(天)" prop="lockDay">
            <el-input
              maxlength="11"
              type="number"
              v-model="dataForm.lockDay"
              placeholder="锁定时间(天)"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8" v-if="!dataForm.id">
          <el-form-item label="提示" prop="">
            <div class="green">输入申购代码获取股票简称</div>
          </el-form-item>
        </el-col>
      </el-row>
      <!-- 填写日期 -->
      <el-form-item class="titleDivTwo" label="日期填写" prop="">
      </el-form-item>
      <el-row>
        <el-col :span="8">
          <el-form-item label="发券日期" prop="issuanceDate">
            <el-date-picker
              v-model="dataForm.issuanceDate"
              type="datetime"
              value-format="yyyy-MM-dd HH:mm:ss"
              placeholder="选择日期时间"
            >
            </el-date-picker>
          </el-form-item>
        </el-col>
        <!-- <el-col :span="8">
          <el-form-item label="抽签日期" prop="drawDate">
            <el-date-picker
            v-model="dataForm.drawDate"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="选择日期时间"
        >
        </el-date-picker>
          </el-form-item>
        </el-col> -->
        <el-col :span="8">
          <el-form-item label="开放申购日" prop="startSubscribeDate">
            <el-date-picker
              v-model="dataForm.startSubscribeDate"
              type="datetime"
              value-format="yyyy-MM-dd HH:mm:ss"
              placeholder="选择日期时间"
            >
            </el-date-picker>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="截止申购日" prop="endSubscribeDate">
            <el-date-picker
              v-model="dataForm.endSubscribeDate"
              type="datetime"
              value-format="yyyy-MM-dd HH:mm:ss"
              placeholder="选择日期时间"
            >
            </el-date-picker>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="dataForm.status">
          <el-radio :label="1">未开始</el-radio>
          <el-radio :label="2">开放中</el-radio>
          <el-radio :label="3">已结束</el-radio>
        </el-radio-group>
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
      imageUrl: "",
      row: "",
      loading: false,
      dataForm: {
        appliedSubscribeNumber: "", //已申购数		false
        defaultLimit: "", //用户申购额度		false
        //drawDate:'',	//	抽签日期 2023-03-22 00:00:00		false
        endSubscribeDate: "", //	截止申购日期 2023-03-22 00:00:00		false
        marketPrice: "", //	市场价		false
        issuanceDate: "", //	发劵日期 2023-03-22 00:00:00		false
        name: "", //	股票简称		false
        productCode: "", //	申购代码（数字）		false
        productName: "", //	申购代码（字母）		false
        startSubscribeDate: "", //	开放申购日期 2023-03-22 00:00:00		false
        status: "", //	状态 1 未开始 2 开放中 3 已结束		false
        subscribeTotalNumber: "", //	发行总数		false
        underwritingPrice: "", //	承销价		false
        weight: "", //	权重		false
        ipoStatus: "",
        id: "",
        lockDay: "", //锁定时间
      },
      symbolDate: [],
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 30, // 每页显示多少条
      },
      direction: [
        {
          label: "待上市",
          value: 1,
        },
        {
          label: "已上市",
          value: 2,
        },
      ],
      dataRule: {
        //{ validator: validateindex, trigger: "blur" },
        name: [
          { required: true, message: "股票简称不能为空", trigger: "blur" },
        ],
        productName: [
          { required: true, message: "申购代码不能为空", trigger: "blur" },
        ],
        productCode: [
          { required: true, message: "申购代码不能为空", trigger: "blur" },
        ],
        status: [{ required: true, message: "状态不能为空", trigger: "blur" }],
        marketPrice: [
          { validator: this.validateWeightLength, trigger: "blur" },
          { required: true, message: "发行价不能为空", trigger: "blur" },
        ],
        underwritingPrice: [
          { validator: this.validateWeightLength, trigger: "blur" },
          { required: true, message: "承销价不能为空", trigger: "blur" },
        ],
        subscribeTotalNumber: [
          { validator: this.validateInteger, trigger: "blur" },
          { required: true, message: "发行总数不能为空", trigger: "blur" },
        ],
        appliedSubscribeNumber: [
          { validator: this.validateInteger, trigger: "blur" },
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
        weight: [
          { validator: this.validateWeightLength, trigger: "blur" },
          { required: true, message: "排序不能为空", trigger: "blur" },
        ],
        defaultLimit: [
          { required: true, message: "用户申购额度不能为空", trigger: "blur" },
          { validator: this.validateWeightLength, trigger: "blur" },
        ],
        lockDay: [
          { required: true, message: "锁定时间不能为空", trigger: "blur" },
          { validator: this.validateWeightLength, trigger: "blur" },
        ],
      },
      tempKey: -666666, // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
      options:[],
    };
  },
  methods: {
    init(row,arr) {
      this.options = arr || []
      this.row = row || "";
      console.log(row);
      if (!row) {
        this.dataForm.status = 1;
        this.dataForm.ipoStatus = 1;
        this.dataForm.id = "";
      } else {
        this.dataForm = { ...row };
        this.dataForm.id = row.uuid;
      }
      this.visible = true;
    },
    validateInteger(rule, value, callback) {
      const maxLength = 11; // 设置最大输入长度
      if (!/^\d+$/.test(value)) {
        callback(new Error("只能输入整数且不能为空"));
      } else if (value && value.toString().length > maxLength) {
        callback(new Error(`最大长度为 ${maxLength} 位字符`));
      } else {
        callback();
      }
    },
    loadOptions(query) {
      if (query !== "") {
        this.loading = true;
        this.$http({
          url: this.$http.adornUrl("/newSharesConfig/searchNewShare"),
          method: "post",
          data: this.$http.adornData(
            Object.assign({
              symbol: query,
              current: 1,
              size: 50,
            })
          ),
        }).then(({ data }) => {
          if(data.code == 0){
            this.symbolDate = data.data.map((item) => ({
            value: item.symbol,
            label: item.symbol+'('+item.name+')',
          }));
          }else{
            this.$message({
            message: data.msg,
            type: "error",
            duration: 1500,
            onClose: () => {},
          });
          }
          this.loading = false;
        });
      } else {
        this.symbolDate = [];
      }
    },
    validateWeightLength(rule, value, callback) {
      const maxLength = 11; // 设置最大输入长度

      if (value && value.toString().length > maxLength) {
        callback(new Error(`最大长度为 ${maxLength} 位字符`));
      } else {
        callback();
      }
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
    openMsg() {
      this.symbolDate = [];
      //获取 股票简称
      this.$http({
        url: this.$http.adornUrl("/newSharesConfig/getNewShare"),
        method: "post",
        data: this.$http.adornData({
          productCode: this.dataForm.productName,
        }),
      }).then(({ data }) => {
        if (data.code == 0) {
          this.dataForm.name = data.data.name;
        } else {
          // this.$message({
          //   message: data.msg,
          //   type: "error",
          //   duration: 1500,
          //   onClose: () => {},
          // });
        }
      });
    },
    // 表单提交
    dataFormSubmit: Debounce(function () {
      console.log("新增股票=>"+this.options.value);
      let url = `/newSharesConfig/update`;
      let dataType = {
        appliedSubscribeNumber: this.dataForm.appliedSubscribeNumber, //已申购数		false
        defaultLimit: this.dataForm.defaultLimit, //用户申购额度		false
        //drawDate:this.dataForm.drawDate,	//	抽签日期 2023-03-22 00:00:00		false
        endSubscribeDate: this.dataForm.endSubscribeDate, //	截止申购日期 2023-03-22 00:00:00		false
        marketPrice: this.dataForm.marketPrice, //	市场价		false
        issuanceDate: this.dataForm.issuanceDate, //	发劵日期 2023-03-22 00:00:00		false
        name: this.dataForm.name, //	股票简称		false
        productCode: this.dataForm.productCode, //	申购代码（数字）		false
        productName: this.dataForm.productName, //	申购代码（字母）		false
        startSubscribeDate: this.dataForm.startSubscribeDate, //	开放申购日期 2023-03-22 00:00:00		false
        status: this.dataForm.status, //	状态 1 未开始 2 开放中 3 已结束		false
        subscribeTotalNumber: this.dataForm.subscribeTotalNumber, //	发行总数		false
        underwritingPrice: this.dataForm.underwritingPrice, //	承销价		false
        weight: this.dataForm.weight, //	权重		false
        ipoStatus: this.dataForm.ipoStatus,
        id: this.dataForm.id,
        lockDay: this.dataForm.lockDay,
        type:this.options.value||"",
      };
      if (!this.dataForm.id) {
        console.log(this.row);
        url = `/newSharesConfig/add`;
        dataType = {
          appliedSubscribeNumber: this.dataForm.appliedSubscribeNumber, //已申购数		false
          defaultLimit: this.dataForm.defaultLimit, //用户申购额度		false
          //drawDate:this.dataForm.drawDate,	//	抽签日期 2023-03-22 00:00:00		false
          endSubscribeDate: this.dataForm.endSubscribeDate, //	截止申购日期 2023-03-22 00:00:00		false
          marketPrice: this.dataForm.marketPrice, //	市场价		false
          issuanceDate: this.dataForm.issuanceDate, //	发劵日期 2023-03-22 00:00:00		false
          name: this.dataForm.name, //	股票简称		false
          productCode: this.dataForm.productCode, //	申购代码（数字）		false
          productName: this.dataForm.productName, //	申购代码（字母）		false
          startSubscribeDate: this.dataForm.startSubscribeDate, //	开放申购日期 2023-03-22 00:00:00		false
          status: this.dataForm.status, //	状态 1 未开始 2 开放中 3 已结束		false
          subscribeTotalNumber: this.dataForm.subscribeTotalNumber, //	发行总数		false
          underwritingPrice: this.dataForm.underwritingPrice, //	承销价		false
          weight: this.dataForm.weight, //	权重		false
          ipoStatus: this.dataForm.ipoStatus,
          lockDay: this.dataForm.lockDay,
          type:this.options.value||"",
        };
      }
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(url),
            method: "post",
            data: this.$http.adornData(dataType),
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
.searchInput{
  width: 235px !important;
}
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
