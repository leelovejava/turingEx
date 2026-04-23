<template>
  <el-dialog
    :title="''"
    :close-on-click-modal="false"
    :visible.sync="visible"
    @close="handClose"
    width="600px"
  >
    <el-form
      :model="dataForm"
      ref="dataForm"
      :rules="dataRule"
      @keyup.enter.native="dataFormSubmit()"
      label-width="120px"
      label-position="top"
      style="width: auto"
      size="mini"
    >
      <span>详细信息</span>
      <el-divider></el-divider>

      <el-form-item label="实名姓名" prop="name" style="width: auto">
        <el-input
          v-model="dataForm.name"
          placeholder=""
          disabled
          style="width: 510px"
        ></el-input>
      </el-form-item>

      <el-form-item label="证件名称" prop="idName" style="width: auto">
        <el-input
          v-model="dataForm.idName"
          placeholder=""
          disabled
          style="width: 510px"
        ></el-input>
      </el-form-item>

      <el-form-item label="证件号码" prop="idNumber" style="width: auto">
        <el-input
          v-model="dataForm.idNumber"
          placeholder=""
          disabled
          style="width: 510px"
        ></el-input>
      </el-form-item>

      <el-form-item label="国籍" prop="nationality" style="width: auto">
        <el-input
          v-model="dataForm.natialName"
          placeholder=""
          disabled
          style="width: 510px"
        ></el-input>
      </el-form-item>

      <span>证件照</span>
      <el-divider></el-divider>
      <el-row>
        <el-col :span="8">
          <el-form-item label="证件正面照" prop="methodImg">
            <el-upload
              class="avatar-uploader"
              :action="$http.adornUrl('/api/uploadFile')"
              :headers="{ Authorization: $cookie.get('Authorization') }"
              :show-file-list="false"
              :on-success="handleAvatarSuccess0"
              :before-upload="beforeAvatarUpload0"
            >
              <img v-if="idFrontImg" :src="idFrontImg" class="avatar" />
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
            <el-button
              v-if="idFrontImg"
              type="primary"
              @click="open(idFrontImg)"
              plain
              size="small"
            >
              查看
            </el-button>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="证件背面照" prop="methodImg">
            <el-upload
              class="avatar-uploader"
              :action="$http.adornUrl('/api/uploadFile')"
              :headers="{ Authorization: $cookie.get('Authorization') }"
              :show-file-list="false"
              :on-success="handleAvatarSuccess1"
              :before-upload="beforeAvatarUpload1"
            >
              <img v-if="idBackImg" :src="idBackImg" class="avatar" />
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
            <el-button
              v-if="idBackImg"
              type="primary"
              @click="open(idBackImg)"
              plain
              size="small"
            >
              查看
            </el-button>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="手持正面照" prop="methodImg">
            <el-upload
              class="avatar-uploader"
              :action="$http.adornUrl('/api/uploadFile')"
              :headers="{ Authorization: $cookie.get('Authorization') }"
              :show-file-list="false"
              :on-success="handleAvatarSuccess2"
              :before-upload="beforeAvatarUpload2"
            >
              <img v-if="handheldPhoto" :src="handheldPhoto" class="avatar" />
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
            <el-button
              v-if="handheldPhoto"
              type="primary"
              @click="open(handheldPhoto)"
              plain
              size="small"
            >
              查看
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
      <div class="mainCant" v-if="imgViewerVisible">
        <div>
          <el-image-viewer
            :on-close="closeImgViewer"
            class="picLoad"
            :url-list="imgList"
          />
        </div>
        <el-button  @click="closeImgViewer">关闭</el-button>
      </div>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button  @click="visible = false">关闭</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
    <!-- 确认弹窗-end -->
  </el-dialog>
</template>

<script>
import { Debounce } from "@/utils/debounce";
import { countries } from "@/utils/countryList";
export default {
  data() {
    return {
      imgViewerVisible: false,
      imgList: "",
      idFrontImg1: "",
      idBackImg1: "",
      handheldPhoto1: "",
      idFrontImg: "",
      idBackImg: "",
      handheldPhoto: "",
      visible: false,
      dialogFormVisible: true,
      formLabelWidth: "120px",
      menuList: [],
      menuListTreeProps: {
        label: "name",
        children: "children",
      },
      options: [],
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
        relatedStockName: [],
        name: "",
        natialName: "",
      },
      fixstork: [], //返回的相关股票品种
      stocks: [],
      dataRule: {
        relatedStockName: [
          { required: true, message: "成分股不能为空", trigger: "blur" },
        ],
        projectName: [{ required: true, message: "不能为空", trigger: "blur" }],
        turnover: [
          { required: true, message: "成交量不能为空", trigger: "blur" },
        ],
        initPrice: [
          { required: true, message: "初始化价格不能为空", trigger: "blur" },
        ],
        minProfitLoss: [
          {
            required: true,
            message: "最小变动单位的盈亏金额不能为空",
            trigger: "blur",
          },
        ],
        minUnit: [
          { required: true, message: "最小变动单位不能为空", trigger: "blur" },
        ],
        fee: [
          { required: true, message: "每张手续费不能为空", trigger: "blur" },
        ],
        amount: [
          { required: true, message: "每张金额不能为空", trigger: "blur" },
        ],
      },
    };
  },
  mounted() {},
  components: {
    "el-image-viewer": () =>
      import("element-ui/packages/image/src/image-viewer"),
  },
  methods: {
    init(row) {
      //console.log(row);
      let id = row.uuid;
      this.dataForm = {};
      this.visible = true;
      this.getDesc(id);
    },
    handClose() {
      this.$data.dataForm = JSON.parse(
        JSON.stringify(this.$options.data().dataForm)
      );
      this.$nextTick(() => {
        this.$refs["dataForm"].clearValidate(); // 清除表单验证
      });
    },
    changeVal(val) {
      this.$forceUpdate();
    },
    dataFormSubmit: Debounce(function () {
      this.UpdateProjectBreed();
    }),
    open(img) {
      // console.log(img)
      // const image = new Image();
      // image.src = img;
      // const newWindow = window.open();
      // newWindow.document.body.appendChild(image);
      this.imgViewerVisible = true;
      this.imgList = [img];
      const m = (e) => {
        e.preventDefault();
      };
      document.body.style.overflow = "hidden";
      document.addEventListener("touchmove", m, false); // 禁止页面滑动
    },
    closeImgViewer() {
      this.imgViewerVisible = false;
      const m = (e) => {
        e.preventDefault();
      };
      document.body.style.overflow = "auto";
      document.removeEventListener("touchmove", m, true);
    },
    handleAvatarSuccess0(res, file) {
      if (res.code == 0) {
        this.dataForm.idFrontImg = URL.createObjectURL(file.raw); //显示地址
        this.dataForm.imgUrl = res.data.path; //接口传递
        this.idFrontImg = this.dataForm.idFrontImg;
        this.idFrontImg1 = this.dataForm.imgUrl;
      } else {
        this.$message({
          message: res.msg,
          type: "error",
          duration: 1500,
          onClose: () => {},
        });
      }
    },
    beforeAvatarUpload0(file) {
      // const isJPG = file.type === 'image/jpeg';
      const isLt2M = file.size / 1024 / 1024 < 10;
      if (!isLt2M) {
        this.$message.error("上传图片大小不能超过 10MB!");
      }
      return isLt2M;
    },
    handleAvatarSuccess1(res, file) {
      if (res.code == 0) {
        this.dataForm.idBackImg = URL.createObjectURL(file.raw); //显示地址
        this.dataForm.imgUrl = res.data.path; //接口传递
        this.idBackImg = this.dataForm.idBackImg || "";
        this.idBackImg1 = this.dataForm.imgUrl;
      } else {
        this.$message({
          message: res.msg,
          type: "error",
          duration: 1500,
          onClose: () => {},
        });
      }
    },
    beforeAvatarUpload1(file) {
      // const isJPG = file.type === 'image/jpeg';
      const isLt2M = file.size / 1024 / 1024 < 10;
      if (!isLt2M) {
        this.$message.error("上传图片大小不能超过 10MB!");
      }
      return isLt2M;
    },
    handleAvatarSuccess2(res, file) {
      if (res.code == 0) {
        this.dataForm.handheldPhoto = URL.createObjectURL(file.raw); //显示地址
        this.dataForm.imgUrl = res.data.path; //接口传递
        this.handheldPhoto = this.dataForm.handheldPhoto || "";
        this.handheldPhoto1 = this.dataForm.imgUrl;
      } else {
        this.$message({
          message: res.msg,
          type: "error",
          duration: 1500,
          onClose: () => {},
        });
      }
    },
    beforeAvatarUpload2(file) {
      // const isJPG = file.type === 'image/jpeg';
      const isLt2M = file.size / 1024 / 1024 < 10;
      if (!isLt2M) {
        this.$message.error("上传图片大小不能超过 10MB!");
      }
      return isLt2M;
    },
    UpdateProjectBreed() {
      //start
      this.$refs["dataForm"].validate((valid) => {
        //修改
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(`/edit`),
            method: "put",
            data: this.$http.adornData({
              uuid: this.dataForm.uuid,
              idFrontImg: this.idFrontImg1,
              idBackImg: this.idBackImg1,
              handheldPhoto: this.handheldPhoto1,
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
    },
    //获取详情
    getDesc(uuid) {
      this.$http({
        url: this.$http.adornUrl("/getById/" + uuid),
        method: "get",
      }).then(({ data }) => {
        if (data.code == 0) {
          this.dataForm = data.data;
          this.dataForm.natialName =
            countries["zh-CN"][this.dataForm.nationality]["name"];
          this.idFrontImg = this.dataForm.idFrontImg || "";
          this.idBackImg = this.dataForm.idBackImg || "";
          this.handheldPhoto = this.dataForm.handheldPhoto || "";
        } else {
          this.$message({
            message: data.msg,
            type: "error",
            duration: 1500,
          });
        }
      });
    },
  },
};
</script>

<style scoped>
.mainCant{
  position: fixed;
    z-index: 9998;
    width: 1200px;
    top: 0;
    left: 20%;
    height: 100%;
    background: rgba(10, 10, 10, 0.7);
    /* background: red; */
}
.picLoad {
  position: absolute;
  width: 600px;
  height: 100%;
  left: 35%;
  margin: 0 auto;
  top: 100px;
  z-index: 9999 !important;
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

.frame {
  width: 700px;
}

.customWidth1 {
  position: relative;
  margin: 0 auto 50px;
  border-radius: 2px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
  box-sizing: border-box;
  width: 60%;
}
/* 
  ::deep .customWidth {
     width: 45%;
  }  */
::v-deep .el-image-viewer__actions {
  position: relative;
    left: 30%;
    z-index: 9999;
    top: 70%;
    font-size: 25px;
    color: #8a9093;
    background: rgba(10, 10, 10, 0.7);
    width: 144px;
    /* margin: 0; */
    padding: 0 0 0 17px;
    border-radius: 20px;
}
::v-deep .el-icon-close{
  /* position: relative;
    left: 100%;
    z-index: 9999;
    top: 8%;
    font-size: 20px;
    color: #02a1e9; */
}
</style>
