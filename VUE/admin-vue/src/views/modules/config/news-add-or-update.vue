<template>
  <el-dialog
    :title="!dataForm.id ? '新增新闻' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    @close="handClose"
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="100px"
    >
      <el-form-item label="玩家/代理UID（空是全局）">
        <el-input
          v-model="dataForm.userCode"
          placeholder="请输入UID"
        ></el-input>
      </el-form-item>
      <el-form-item label="标题" prop="title">
        <el-input v-model="dataForm.title" placeholder="标题"></el-input>
      </el-form-item>
      <el-form-item label="图片" prop="imageUrl">
        <el-upload
          class="avatar-uploader"
          :action="$http.adornUrl('/api/uploadFile')"
          :headers="{ Authorization: $cookie.get('Authorization') }"
          :show-file-list="false"
          :on-success="handleAvatarSuccess"
          :before-upload="beforeAvatarUpload"
        >
          <img
            v-if="dataForm.imageUrlhttp"
            :src="dataForm.imageUrlhttp"
            class="avatar"
          />
          <i v-else class="el-icon-plus avatar-uploader-icon"></i>
        </el-upload>
      </el-form-item>

      <!-- <el-form-item label="图片">
        <el-upload
          action="#"
          :auto-upload="false" 
          list-type="picture-card"
          :on-change="handleChange"
          :on-preview="handlePictureCardPreview"
          :on-remove="handleRemove">
          <i class="el-icon-plus"></i>
        </el-upload>
        <el-dialog :visible.sync="dialogVisible">
          <img width="100%" :src="dialogImageUrl" alt="">
        </el-dialog>
      </el-form-item> -->

      <el-form-item label="图片跳转链接" prop="">
        <el-input
          v-model="dataForm.imgJumpUrl"
          placeholder="图片跳转链接"
        ></el-input>
      </el-form-item>

      <el-form-item label="可否点击">
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
      <el-form-item>
        <div class="green">
          【是否弹出】和【滚动新闻】只能配一个为是，否则新闻将不显示
        </div>
      </el-form-item>
      <el-form-item label="是否弹出">
        <el-select
          v-model="optionsThree.value"
          placeholder="请选择"
          @change="changeVal()"
        >
          <el-option
            v-for="item in optionsThree"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="滚动新闻">
        <el-select
          v-model="optionsFour.value"
          placeholder="请选择"
          @change="changeVal()"
        >
          <el-option
            v-for="item in optionsFour"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="语言">
        <el-select
          v-model="options.value"
          placeholder="请选择语言"
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
        <!-- <el-select v-model="dataForm.language" placeholder="英文">         
        </el-select> -->
      </el-form-item>
      <el-form-item>
        <div class="green">
          当前时间不在【开始时间】和【结束时间】之内时，新闻将不显示
        </div>
      </el-form-item>
      <el-form-item label="开始时间">
        <el-date-picker
          v-model="dataForm.startTime"
          type="datetime"
          value-format="yyyy-MM-dd HH:mm:ss"
          placeholder="选择日期时间"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item label="结束时间">
        <el-date-picker
          v-model="dataForm.endTime"
          type="datetime"
          value-format="yyyy-MM-dd HH:mm:ss"
          placeholder="选择日期时间"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item label="内容" prop="content">
        <el-input
          type="textarea"
          placeholder="请输入内容"
          v-model="dataForm.content"
          :autosize="{ minRows: 8, maxRows: 8 }"
          show-word-limit
        >
        </el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="open()">确定</el-button>
    </span>
    <!-- 确认弹窗-start -->
    <el-dialog
      title="确认增加"
      :visible.sync="dialogFormVisible"
      :append-to-body="true"
    >
      <el-form
        :model="dataForm2"
        :rules="dataRule2"
        ref="dataForm2"
        @keyup.enter.native="open()"
        label-width="80px"
      >
        <el-form-item
          label="登录人资金密码"
          :label-width="formLabelWidth"
          prop="loginSafeword"
        >
          <el-input
            v-model="dataForm2.loginSafeword"
            type="password"
            placeholder="登录人资金密码"
            autocomplete="off"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="dataFormSubmit()">确 定</el-button>
      </div>
    </el-dialog>
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
          label: "是",
          value: true,
        },
        {
          label: "否",
          value: false,
        },
      ],
      optionsThree: [
        {
          label: "是",
          value: true,
        },
        {
          label: "否",
          value: false,
        },
      ],
      optionsFour: [
        {
          label: "是",
          value: true,
        },
        {
          label: "否",
          value: false,
        },
      ],
      row: "",
      dataForm: {
        id: 0,
        userCode: "",
        roleName: "",
        remark: "",
        imgJumpUrl: "",
        imageUrl: "",
        imageUrlhttp: "",
        imgUrl: "",
        title: "",
        content: "",
        startTime: "",
        endTime: "",
      },
      dataForm2: {
        loginSafeword: "",
      },
      dataRule: {
        //imageUrl:[{ required: true, message: '资金密码不能为空', trigger: 'blur' },],

        title: [{ required: true, message: "标题不能为空", trigger: "blur" }],
        content: [{ required: true, message: "内容不能为空", trigger: "blur" }],
        imgJumpUrl: [
          { required: true, message: "图片跳转链接不能为空", trigger: "blur" },
        ],
        // imageUrl:[{ required: true, message: '图片不能为空', trigger: 'blur' },],
        roleName: [
          { required: true, message: "角色名称不能为空", trigger: "blur" },
          {
            pattern: /\s\S+|S+\s|\S/,
            message: "请输入正确的角色名称",
            trigger: "blur",
          },
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
      dataRule2: {
        loginSafeword: [
          { required: true, message: "资金密码不能为空", trigger: "blur" },
        ],
      },
      dialogVisible: false,
      dialogImageUrl: "",
      tempKey: -666666, // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
    };
  },
  methods: {
    init(row, arr) {
      this.row =  row  || "";
      this.options = arr;
      if (row) {
        this.dataForm.title = row.title;
        this.dataForm.imageUrl = row.imgUrl;
        this.dataForm.imageUrlhttp = row.httpImgUrl;
        this.dataForm.userCode = row.userCode || "";
        this.dataForm.imgJumpUrl = row.imgJumpUrl;
        this.dataForm.imgUrl = row.imgUrl;
        this.optionsTwo.value = row.click;
        this.optionsThree.value = row.popUp;
        this.optionsFour.value = row.indexTop;
        this.options.value = row.language;
        this.dataForm.startTime = row.startTime;
        this.dataForm.endTime = row.endTime;
        this.dataForm.content = row.content;
        this.dataForm.id = row.uuid;
      } else {
        this.optionsTwo.value = this.optionsTwo[0].value;
        this.optionsThree.value = this.optionsThree[0].value;
        this.optionsFour.value = this.optionsFour[1].value;
        this.options.value = this.options[0].value;
      }
      this.visible = true;
      this.dialogFormVisible = false;
    },
    open() {
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          this.dialogFormVisible = true;
        } else {
          return false;
        }
      });
    },
    handClose() {
      this.$data.dataForm = JSON.parse(
        JSON.stringify(this.$options.data().dataForm)
      );
      this.$data.dataForm2 = JSON.parse(
        JSON.stringify(this.$options.data().dataForm2)
      );
      this.$nextTick(() => {
        if (this.$refs["dataForm"]) this.$refs["dataForm"].clearValidate(); // 清除表单验证
        if (this.$refs["dataForm2"]) this.$refs["dataForm2"].clearValidate(); // 清除表单验证
      });
      this.optionsTwo.value = "";
      this.optionsThree.value = "";
      this.optionsFour.value = "";
      this.options.value = "";
    },
    changeVal(val) {
      this.$forceUpdate();
    },
    // Open(call){
    //   this.$prompt('登录人资金密码', '提示', {
    //     confirmButtonText: '确定',
    //     cancelButtonText: '取消',
    //   }).then(({ value }) => {
    //     this.dataForm.loginSafeword = value;
    //     if(call){ call() }
    //   }).catch((e) => {
    //     console.log("error = " +e);
    //     this.$message({
    //       type: 'info',
    //       message: '取消输入'
    //     });
    //   });
    // },
    // 表单提交
    dataFormSubmit: Debounce(function () {
      // let click = false;
      // let index = false;
      // let popUp = false;
      // try{
      //   click = parseInt(this.dataForm.click)==1 ? true : false;
      //   index = parseInt(this.dataForm.index)==1 ? true : false;
      //   popUp = parseInt(this.dataForm.popUp)==1 ? true : false;
      // }catch{
      // }
      if (this.row) {
        //更新修改
        //start
        this.$refs["dataForm2"].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/news/update`),
              method: "post",
              data: this.$http.adornData({
                click: this.optionsTwo.value, //是否可点击
                content: this.dataForm.content, //内容
                endTime: this.dataForm.endTime, //this.dataForm.endTime,    //结束时间
                id: this.dataForm.id, //修改传id,新增不传
                imgJumpUrl: this.dataForm.imgJumpUrl, //图片跳转链接
                imgUrl: this.dataForm.imgUrl, //图片地址
                index: this.optionsFour.value, //index
                language: this.options.value, //语言
                loginSafeword: encrypt(this.dataForm2.loginSafeword), //资金密码
                popUp: this.optionsThree.value, //是否弹出
                startTime: this.dataForm.startTime, //this.dataForm.startTime,  //开始时间 2023-03-22 00:00:00
                title: this.dataForm.title, //标题
                userCode: this.dataForm.userCode, //玩家/代理UID(空是全局)
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
        console.log(222222222)
        //新增
        //start
        this.$refs["dataForm2"].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/news/add`),
              method: "post",
              data: this.$http.adornData({
                click: this.optionsTwo.value, //是否可点击
                content: this.dataForm.content, //内容
                endTime: this.dataForm.endTime, //this.dataForm.endTime,    //结束时间
                imgJumpUrl: this.dataForm.imgJumpUrl, //图片跳转链接
                imgUrl: this.dataForm.imgUrl, //图片地址
                index: this.optionsFour.value, //index
                language: this.options.value, //语言
                loginSafeword: encrypt(this.dataForm2.loginSafeword), //资金密码
                popUp: this.optionsThree.value, //是否弹出
                startTime: this.dataForm.startTime, //this.dataForm.startTime,  //开始时间 2023-03-22 00:00:00
                title: this.dataForm.title, //标题
                userCode: this.dataForm.userCode, //玩家/代理UID(空是全局)
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
                    //this.visible = false
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
      console.log("handleAvatarSuccess = " + JSON.stringify(res));
      //this.dataForm.imageUrlhttp = row.httpImgUrl
      this.dataForm.imageUrlhttp = URL.createObjectURL(file.raw); //显示地址

      if (res.code == 0) {
        this.dataForm.imgUrl = res.data.path; //接口传递
        console.log(this.dataForm.imageUrl);
        console.log(this.dataForm.imgUrl);
      } else {
        this.$message.error(res.msg);
      }

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
