<template>
  <el-dialog
    :title="!id ? '新增' : '修改'"
    :close-on-click-modal="false"
    width="1200px"
    @close="handClose"
    :visible.sync="visible"
  >
    <el-scrollbar class="vertical-scrollbar">
      <div class="scroll-content">
        <el-form
          :model="dataForm"
          :rules="dataRule"
          ref="dataForm"
          @keyup.enter.native="dataFormSubmit()"
          label-width="220px"
        >
          <el-form-item class="titleDivTwo" label="基础信息" prop="">
          </el-form-item>
          <el-row>
            <el-col :span="12">
              <el-form-item label="交易员UID" prop="traderUsercode">
                <el-input
                  :disabled="dataForm.uuid ? true : false"
                  v-model="dataForm.traderUsercode"
                  placeholder="交易员UID"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12" v-if="!id">
              <el-form-item label="添加用户类型" prop="userType">
                <el-select
                  v-model="dataForm.userType"
                  class="spainut"
                  placeholder="请选择"
                  @change="changeVal()"
                >
                  <el-option
                    v-for="item in userTypeList"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="(真实/演示)用户UID" prop="usercode">
                <el-input
                  :disabled="dataForm.uuid ? true : false"
                  v-model="dataForm.usercode"
                  placeholder="(真实/演示)用户UID"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="虚假用户用户名" prop="username">
                <el-input
                :disabled="dataForm.uuid ? true : false"
                  v-model="dataForm.username"
                  placeholder="虚假用户用户名"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="跟单方式" prop="followType">
                <el-select
                  class="spainut"
                  v-model="dataForm.followType"
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
            <el-col :span="12">
              <el-form-item label="固定张数值或固定比例值(整数)" prop="volume">
                <el-input
                  type="number"
                  v-model="dataForm.volume"
                  placeholder="固定张数值或固定比例值(整数)"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="最大持仓张数（整数）" prop="volumeMax">
                <el-input
                  type="number"
                  v-model="dataForm.volumeMax"
                  placeholder="最大持仓张数（整数）"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="跟随收益" prop="profit">
                <el-input
                  type="number"
                  v-model="dataForm.profit"
                  placeholder="跟随收益"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="止盈百分比" prop="stopProfit">
                <el-input
                  type="number"
                  v-model="dataForm.stopProfit"
                  placeholder="止盈百分比"
                >
                  <template v-slot:append>
                    <span>%</span>
                  </template></el-input
                >
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="止损百分比" prop="stopLoss">
                <el-input
                  type="number"
                  v-model="dataForm.stopLoss"
                  placeholder="止损百分比"
                >
                  <template v-slot:append>
                    <span>%</span>
                  </template></el-input
                >
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="跟随本金" prop="amountSum">
                <el-input
                  type="number"
                  v-model="dataForm.amountSum"
                  placeholder="跟随本金"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
    </el-scrollbar>
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
      dataForm: {
        id: "",
        uuid:"",
        traderUsercode: "",
        userType: "",
        usercode: "",
        followType: "",
        username: "",
        volume: "",
        volumeMax: "",
        profit: "",
        amountSum: "",
        stopProfit: "",
        stopLoss: "",
      },
      id: "",
      userTypeList: [
        {
          label: "(真实/演示)用户",
          value: "1",
        },
        {
          label: "虚假用户",
          value: "2",
        },
      ],
      direction: [
        {
          label: "固定张数",
          value: "1",
        },
        {
          label: "固定比例",
          value: "2",
        },
      ],
      dataRule: {
        //{ validator: validateindex, trigger: "blur" },
        traderUsercode: [
          { required: true, message: "交易员UID不能为空", trigger: "blur" },
        ],
        usercode: [{ required: true, message: "UID不能为空", trigger: "blur" }],
        username: [
          { required: true, message: "名称不能为空", trigger: "blur" },
        ],
        volume: [
          {
            required: true,
            message: "跟单张数或比例不能为空",
            trigger: "blur",
          },
        ],
        volumeMax: [
          { required: true, message: "最大持仓张数不能为空", trigger: "blur" },
        ],
        profit: [
          { required: true, message: "跟随收益不能为空", trigger: "blur" },
        ],
        amountSum: [
          { required: true, message: "跟随本金不能为空", trigger: "blur" },
        ],
        stopProfit: [
          { required: true, message: "止盈百分比不能为空", trigger: "blur" },
        ],
        stopLoss: [
          { required: true, message: "止损百分比不能为空", trigger: "blur" },
        ],
      },
      tempKey: -666666, // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
    };
  },
  methods: {
    init(id) {
      if (id) {
        this.id = id;
        this.getMassage();
      } else {
        this.dataForm.userType = this.userTypeList[0].value;
        this.dataForm.followType = this.direction[0].value;
      }
      if (this.dataForm.img) {
        // this.imageUrl =  this.dataForm.path
      }
      this.visible = true;
    },
    handleAvatarSuccess(res, file) {
      this.dataForm.img = res.data.path;
      this.imageUrl = URL.createObjectURL(file.raw);
    },
    beforeAvatarUpload(file) {
      // const isJPG = file.type === 'image/jpeg';
      const isLt2M = file.size / 1024 / 1024 < 10;

      // if (!isJPG) {
      //   this.$message.error('上传头像图片只能是 JPG 格式!');
      // }
      if (!isLt2M) {
        this.$message.error("上传图片大小不能超过 10MB!");
      }
      // return isJPG && isLt2M;
      return isLt2M;
    },
    handClose() {
      this.id = "";
      this.$data.dataForm = JSON.parse(
        JSON.stringify(this.$options.data().dataForm)
      );
      this.imageUrl = "";
      this.$nextTick(() => {
        this.$refs["dataForm"].clearValidate(); // 清除表单验证
      });
    },
    changeVal(val) {
      this.$forceUpdate();
    },

    dataFormSubmit: Debounce(function () {
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          let url = "";
          if (this.id) {
            //更新
            url = `/normal/adminTraderFollowUser!update.action`;
          } else {
            //新增
            url = `/normal/adminTraderFollowUser!add.action`;
          }
          // 添加需要提交的表单字段及其值到 FormData 对象
          this.$http({
            url: this.$http.adornUrl(url),
            method: "post",
            data: this.$http.adornData(this.dataForm),
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
    getMassage() {
      this.$http({
        url: this.$http.adornUrl(
          `/normal/adminTraderFollowUser!toUpdate.action`
        ),
        method: "get",
        params: this.$http.adornParams({
          uuid: this.id,
        }),
      }).then(({ data }) => {
        console.log("data => " + JSON.stringify(data));
        if (data.code == 0) {
          //this.dataForm = { ...data.data };
          this.dataForm.uuid=data.data.uuid
          this.dataForm.traderUsercode = data.data.traderUserCode;
          // this.dataForm.userType=data.data.,
          this.dataForm.usercode = data.data.userCode;
          this.dataForm.followType = data.data.followType;
          this.dataForm.username = data.data.username;
          this.dataForm.volume = data.data.volume;
          this.dataForm.volumeMax = data.data.volumeMax;
          this.dataForm.profit = data.data.profit;
          this.dataForm.amountSum = data.data.amountSum;
          this.dataForm.stopProfit = data.data.stopProfit;
          this.dataForm.stopLoss = data.data.stopLoss;
          //   this.imageUrl = this.dataForm.img;
          // console.log(this.imageUrl)
        } else {
          this.$message({
            message: data.msg,
            type: "error",
            duration: 1500,
            onClose: () => {},
          });
        }
      });
    },
    // 表单提交
    // dataFormSubmit: Debounce(function () {
    //   if (!this.dataForm.img) {
    //     this.$message.error("上传图片不能为空");
    //     return;
    //   }

    //   let url = `/normal/adminFinanceAction!update.action`;
    //   if (!this.dataForm.id) {
    //     url = `/normal/adminFinanceAction!add.action`;
    //   }
    //   this.$refs["dataForm"].validate((valid) => {
    //     if (valid) {
    //       this.$http({
    //         url: this.$http.adornUrl(url),
    //         method: "get",
    //         params: this.$http.adornParams({
    //           ...this.dataForm,
    //           login_safeword: encrypt(this.dataForm.login_safeword),
    //         }),
    //       }).then(({ data }) => {
    //         console.log("data => " + JSON.stringify(data));
    //         this.$message({
    //           message: "操作成功",
    //           type: "success",
    //           duration: 1500,
    //           onClose: () => {
    //             this.visible = false;
    //             this.$emit("refreshDataList");
    //           },
    //         });
    //       });
    //     }
    //   });
    // }),
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
.vertical-scrollbar .scroll-content {
  max-height: 600px;
  overflow-y: auto;
  overflow-x: hidden;
}
.vertical-scrollbar .scroll-content::-webkit-scrollbar {
  width: 4px;
}

.vertical-scrollbar .scroll-content::-webkit-scrollbar-track {
  background-color: #f1f1f1;
}

.vertical-scrollbar .scroll-content::-webkit-scrollbar-thumb {
  background-color: #888;
  border-radius: 4px;
}

.vertical-scrollbar .scroll-content::-webkit-scrollbar-thumb:hover {
  background-color: #555;
}
.spainut {
  width: 360px;
}
</style>
