<template>
  <el-dialog
    :title="!dataForm.id ? '新增申购管理' : '修改申购管理'"
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
      <el-form-item label="ID" prop="idCode">
        <el-input v-model="dataForm.idCode" placeholder="请输入ID"></el-input>
      </el-form-item>
      <el-form-item label="项目总类">
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
      <el-form-item label="申购项目名称" prop="projectName">
        <el-input
          v-model="dataForm.projectName"
          placeholder="申购项目名称"
        ></el-input>
      </el-form-item>
      <el-form-item label="数据源类别">
        <el-select
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
      <el-form-item label="发行价" prop="issuePrice">
        <el-input v-model="dataForm.issuePrice" placeholder="发行价"></el-input>
      </el-form-item>
      <el-form-item label="接受申购的币种">
        <el-input
          v-model="dataForm.currency"
          disabled
          placeholder="发行价"
        ></el-input>
      </el-form-item>
      <el-form-item label="预计上线时间">
        <el-date-picker
          v-model="dataForm.expectedLaunchTime"
          type="datetime"
          value-format="yyyy-MM-dd HH:mm:ss"
          placeholder="选择日期时间"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item label="开始申购时间">
        <el-date-picker
          v-model="dataForm.subscriptionStartTime"
          type="datetime"
          value-format="yyyy-MM-dd HH:mm:ss"
          placeholder="选择日期时间"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item label="结束申购时间">
        <el-date-picker
          v-model="dataForm.subscriptionEndTime"
          type="datetime"
          value-format="yyyy-MM-dd HH:mm:ss"
          placeholder="选择日期时间"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item label="公布结果时间">
        <el-date-picker
          v-model="dataForm.publishTime"
          type="datetime"
          value-format="yyyy-MM-dd HH:mm:ss"
          placeholder="选择日期时间"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item label="白皮书地址" prop="whitePagerAddress">
        <el-input
          v-model="dataForm.whitePagerAddress"
          placeholder="白皮书地址"
        ></el-input>
      </el-form-item>
      <el-form-item label="最小申购数量" prop="minQuantity">
        <el-input
          v-model="dataForm.minQuantity"
          placeholder="最小申购数量"
        ></el-input>
      </el-form-item>
      <el-form-item label="最大申购数量" prop="maxQuantity">
        <el-input
          v-model="dataForm.maxQuantity"
          placeholder="最大申购数量"
        ></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
    <!-- 确认弹窗-end -->
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
      dialogFormVisible: true,
      formLabelWidth: "120px",
      menuList: [],
      menuListTreeProps: {
        label: "name",
        children: "children",
      },
      options: [],
      optionsTwo: [
        {
          label: "机器人刷单",
          value: 1,
        },
        {
          label: "第三方数据采集",
          value: 2,
        },
      ],
      row: "",
      dataForm: {
        id: "",
        idCode: "",
        projectName: "",
        issuePrice: "",
        currency: "usdt",
        expectedLaunchTime: "",
        subscriptionEndTime: "",
        subscriptionStartTime: "",
        publishTime: "",
        minQuantity: "",
        maxQuantity: "",
        whitePagerAddress: "",
      },
      dataRule: {
        whitePagerAddress: [
          { required: true, message: "白皮书地址不能为空", trigger: "blur" },
        ],
        idCode: [{ required: true, message: "ID不能为空", trigger: "blur" }],
        projectName: [
          { required: true, message: "申购项目名称不能为空", trigger: "blur" },
        ],
        issuePrice: [
          { required: true, message: "发行价不能为空", trigger: "blur" },
        ],
        minQuantity: [
          { required: true, message: "最小申购数量不能为空", trigger: "blur" },
        ],
        maxQuantity: [
          { required: true, message: "最大申购数量不能为空", trigger: "blur" },
        ],
      },
    };
  },
  methods: {
    init(arr, row) {
      this.options = arr || [];
      this.row = row || "";
      console.log(row);
      if (row) {
        (this.dataForm.id = row.uuid),
          (this.dataForm.idCode = row.idCode),
          (this.dataForm.projectName = row.projectName),
          (this.dataForm.issuePrice = row.issuePrice),
          (this.dataForm.currency = row.currency),
          (this.dataForm.expectedLaunchTime = row.expectedLaunchTime),
          (this.dataForm.subscriptionEndTime = row.subscriptionEndTime),
          (this.dataForm.subscriptionStartTime = row.subscriptionStartTime),
          (this.dataForm.publishTime = row.publishTime),
          (this.dataForm.minQuantity = row.minQuantity),
          (this.dataForm.maxQuantity = row.maxQuantity);
        this.optionsTwo.value = row.dataType * 1;
        this.options.value = row.projectTypeSymbol;
        this.dataForm.whitePagerAddress = row.whitePagerAddress;
      } else {
        this.optionsTwo.value = this.optionsTwo[0].value;
        this.options.value = this.options[0].value;
      }
      this.visible = true;
      // this.dialogFormVisible = false
    },
    handClose() {
      this.$data.dataForm = JSON.parse(
        JSON.stringify(this.$options.data().dataForm)
      );
      this.$nextTick(() => {
        this.$refs["dataForm"].clearValidate(); // 清除表单验证
      });
      this.optionsTwo.value = "";
      this.options.value = "";
    },
    changeVal(val) {
      this.$forceUpdate();
    },
    dataFormSubmit: Debounce(function () {
      if (this.row) {
        //更新修改
        //start
        this.$refs["dataForm"].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/purchasing/update`),
              method: "post",
              data: this.$http.adornData({
                id: this.dataForm.id,
                currency: this.dataForm.currency, //接受申购币种
                dataType: this.optionsTwo.value, //数据源类别
                expectedLaunchTime: this.dataForm.expectedLaunchTime, //预计上线时间
                idCode: this.dataForm.idCode, //	项目ID
                issuePrice: this.dataForm.issuePrice, //发行价
                maxQuantity: this.dataForm.maxQuantity, //最大申购数量
                minQuantity: this.dataForm.minQuantity, //最小申购数量
                projectName: this.dataForm.projectName, //申购项目名称
                projectTypeSymbol: this.options.value, //项目总类 1 全球ETF 2 能源ETF 3 黄金ETF 4.人工智能ETF
                //'projectTypeName': this.options.label,    //项目总类 1 全球ETF 2 能源ETF 3 黄金ETF 4.人工智能ETF
                publishTime: this.dataForm.publishTime, //公布时间
                subscriptionEndTime: this.dataForm.subscriptionEndTime, //结束申购时间
                subscriptionStartTime: this.dataForm.subscriptionStartTime, //开始申购时间
                whitePagerAddress: this.dataForm.whitePagerAddress, //白皮书地址
              }),
            }).then(({ data }) => {
              if (data.code == 0) {
                this.$message({
                  message: "操作成功",
                  type: "success",
                  duration: 1500,
                  onClose: () => {
                    this.dialogFormVisible = false;
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
                    this.dialogFormVisible = false;
                    this.visible = false;
                  },
                });
              }
            });
          }
        });
        //end
      } else {
        //新增
        //start
        this.$refs["dataForm"].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/purchasing/add`),
              method: "post",
              data: this.$http.adornData({
                currency: this.dataForm.currency, //接受申购币种
                dataType: this.optionsTwo.value, //数据源类别
                expectedLaunchTime: this.dataForm.expectedLaunchTime, //预计上线时间
                idCode: this.dataForm.idCode, //	项目ID
                issuePrice: this.dataForm.issuePrice, //发行价
                maxQuantity: this.dataForm.maxQuantity, //最大申购数量
                minQuantity: this.dataForm.minQuantity, //最小申购数量
                projectName: this.dataForm.projectName, //申购项目名称
                projectTypeSymbol: this.options.value, //项目总类 1 全球ETF 2 能源ETF 3 黄金ETF 4.人工智能ETF
                //'projectTypeName': this.options.label,    //项目总类 1 全球ETF 2 能源ETF 3 黄金ETF 4.人工智能ETF
                publishTime: this.dataForm.publishTime, //公布时间
                subscriptionEndTime: this.dataForm.subscriptionEndTime, //结束申购时间
                subscriptionStartTime: this.dataForm.subscriptionStartTime, //开始申购时间
                whitePagerAddress: this.dataForm.whitePagerAddress, //白皮书地址
              }),
            }).then(({ data }) => {
              if (data.code == 0) {
                this.$message({
                  message: "操作成功",
                  type: "success",
                  duration: 1500,
                  onClose: () => {
                    this.dialogFormVisible = false;
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
                    this.dialogFormVisible = false;
                    this.visible = false;
                  },
                });
              }
            });
          }
        });
        //end
      }
    }),
    handleAvatarSuccess(res, file) {
      // this.dataForm.methodImg = res.data.path
      //{"data":{"path":"null/2023-04-29/d3f084ea-391f-4ec9-a2dd-f9393221f58f.png",
      //"httpUrl":"https://trading-order-test.s3.amazonaws.com/null/2023-04-29/d3f084ea-391f-4ec9-a2dd-f9393221f58f.png"},"code":0,"msg":"","succeed":false}
      console.log(file);
      this.dataForm.imageUrl = URL.createObjectURL(file.raw); //显示地址
      this.dataForm.imgUrl = res.data.path; //接口传递
      console.log(this.dataForm.imageUrl);
      // if(res.code == 0){
      //   this.dataForm.image = res.data.httpUrl
      // }
    },
    beforeAvatarUpload(file) {
      // const isJPG = file.type === 'image/jpeg';
      const isLt2M = file.size / 1024 / 1024 < 10;
      if (!isLt2M) {
        this.$message.error("上传图片大小不能超过 10MB!");
      }
      return isLt2M;
    },
  },
};
</script>

<style scoped>
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
</style>
