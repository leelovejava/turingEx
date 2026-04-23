<template>
  <el-dialog
    :title="!row ? '新增' : '修改'"
    :close-on-click-modal="false"
    width="1100px"
    @close = 'handClose'
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
      <el-form-item  label="注意事项：">
        <div class="green">
          代理商(UID)</br>
全局只设置代理商,表示代理线下所有用户质押配置。</br>
优先级为个人>代理>全局</div>
      </el-form-item>
      <el-row>
        <el-col :span="8">
          <el-form-item label="代理商UID" prop="userCode">
            <el-input
              v-model="dataForm.userCode"
              :disabled = "row?true:false"
              placeholder="代理商UID"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="限制天数" prop="limitDays">
            <el-input
              v-model="dataForm.limitDays"
              placeholder="限制天数"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="标题文本" prop="title">
            <el-input
              v-model="dataForm.title"
              placeholder="标题文本"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      
      <el-row>
        <el-col :span="8">
          <el-form-item label="标题图片" prop="titleImg">
        <el-upload
          class="avatar-uploader"
          :action="$http.adornUrl('/api/uploadFile')"
          :headers="{ Authorization: $cookie.get('Authorization') }"
          :show-file-list="false"
          :on-success="handleAvatarSuccess"
          :before-upload="beforeAvatarUpload"
        >
          <img v-if="imageUrl" :src="imageUrl" class="avatar" />
          <i v-else class="el-icon-plus avatar-uploader-icon"></i>
        </el-upload>
      </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="内容文本" prop="content">
        <el-input
          v-model="dataForm.content"
          type="textarea"
          placeholder="内容文本"
        ></el-input>
      </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="内容图片" prop="contentImg">
        <el-upload
          class="avatar-uploader"
          :action="$http.adornUrl('/api/uploadFile')"
          :headers="{ Authorization: $cookie.get('Authorization') }"
          :show-file-list="false"
          :on-success="handleAvatarSuccess2"
          :before-upload="beforeAvatarUpload"
        >
          <img v-if="imageUrl2" :src="imageUrl2" class="avatar" />
          <i v-else class="el-icon-plus avatar-uploader-icon"></i>
        </el-upload>
      </el-form-item>
        </el-col>
      </el-row>
      <el-form-item class="titleDivTwo" label="交易信息" prop="">
      </el-form-item>
      <el-form-item  label="注意事项：">
        <div class="green">
          收益费率格式示范：100-5000;0.0025-0.003|5000-20000;0.005-0.0055|20000-50000;0.0055-0.0065|50000-9999999;0.0065-0.0075</br>
          ⻔槛说明举例：100-5000;0.0025-0.003 表示:如果客户的钱包USDT余额在100到5000USDT之间，每次结算可以获得0.25%到0.3%之间的利润</br>
          ⼀天有4次结算。 有可能是0.26%或者 0.29%，是随机区间</div>
      </el-form-item>
      <el-form-item label="收益费率" prop="config">
            <el-input
              v-model="dataForm.config"
              placeholder="收益费率"
            ></el-input>
          </el-form-item>
      <el-row>
        <el-col :span="12">
          <el-form-item label="用户USDT达标数量" prop="usdt">
            <el-input
              v-model="dataForm.usdt"
              placeholder="用户USDT达标数量"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="奖励ETH数量" prop="eth">
            <el-input
              v-model="dataForm.eth"
              placeholder="奖励ETH数量"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="12">
          <el-form-item  label="登录人资金密码"  prop="loginSafeword">
        <el-input v-model="dataForm.loginSafeword" type="password" placeholder="请输入登录人资金密码"></el-input>
      </el-form-item></el-col>
      </el-row>
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
      row:'',
      imageUrl: "",
      imageUrl2:'',
      dataForm: {
        id: '',
        config: "",
        content: "",
        contentImg: "",
        eth: "",
        limitDays: "",
        loginSafeword: "",
        title: "",
        titleImg: "",
        usdt: "",
        userCode: "",
      },
      direction: [
        {
          label: "启用",
          value: "1",
        },
        {
          label: "停用",
          value: "0",
        },
      ],
      dataRule: {
        //{ validator: validateindex, trigger: "blur" }, 
        titleImg: [
          { required: true, message: "标题图片不能为空", trigger: "blur" },
        ],
        contentImg: [
          { required: true, message: "内容图片不能为空", trigger: "blur" },
        ],
        userCode: [
          { required: true, message: "代理商UID不能为空", trigger: "blur" },
        ],
        limitDays: [
          { required: true, message: "限制天数不能为空", trigger: "blur" },
        ],
        title: [
          { required: true, message: "标题文本不能为空", trigger: "blur" },
        ],
        config: [
          { required: true, message: "收益费率不能为空", trigger: "blur" },
        ],
        usdt: [
          { required: true, message: "用户USDT达标数量不能为空", trigger: "blur" },
        ],
        eth: [
          { required: true, message: "奖励ETH数量不能为空", trigger: "blur" },
        ],
        loginSafeword: [
            { required: true, message: '登录人资金密码不能为空', trigger: 'blur' },
        ],
      },
      tempKey: -666666, // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
    };
  },
  methods: {
    init(row) {
      console.log(row)
      if(row){
        this.row = row,
        this.dataForm = row; 
        this.imageUrl = this.dataForm.titleImgUrl
        this.imageUrl2 = this.dataForm.contentImgUrl
      }else{
        this.dataForm.config = '100-5000;0.0025-0.003|5000-20000;0.005-0.0055|20000-50000;0.0055-0.0065|50000-9999999;0.0065-0.0075'
      }
      
      this.visible = true;
    },
    handleAvatarSuccess(res, file) {
      if(res.code == 0){
        this.dataForm.titleImg = res.data.path;
        this.imageUrl = URL.createObjectURL(file.raw);
        this.$refs["dataForm"].validateField('titleImg')
      }else{
        this.$message({
          type: 'error',
          message: data.msg
        }); 
      }


    },
    handleAvatarSuccess2(res, file) {
      if(res.code == 0){
        this.dataForm.contentImg = res.data.path;
        this.imageUrl2 = URL.createObjectURL(file.raw);
        this.$refs["dataForm"].validateField('contentImg')
      }else{
        this.$message({
          type: 'error',
          message: data.msg
        }); 
      }


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
    handClose(){
        this.$data.dataForm=JSON.parse(JSON.stringify(this.$options.data().dataForm))
        this.imageUrl = ''
        this.imageUrl2 = ''
        this.row = ''
     this.$nextTick(() => {
            this.$refs['dataForm'].clearValidate() // 清除表单验证
          })
      },
    changeVal(val) {
      this.$forceUpdate();
    },
    // 表单提交
    dataFormSubmit: Debounce(function () {
      let url = '';
      let data = {}
      if(!this.row){ // 新增
        url = `/pledgeConfig/add`;
        data = {
          config :this.dataForm.config,
          content:this.dataForm.content,
          contentImg:this.dataForm.contentImg,
          eth :this.dataForm.eth,
          limitDays :this.dataForm.limitDays,
          title :this.dataForm.title,
          titleImg :this.dataForm.titleImg,
          usdt :this.dataForm.usdt,
          userCode :this.dataForm.userCode,
          loginSafeword:encrypt(this.dataForm.loginSafeword)
        }
      }else{
        url = `/pledgeConfig/update`;
        data = {
          config :this.dataForm.config,
          content:this.dataForm.content,
          contentImg:this.dataForm.contentImg,
          eth :this.dataForm.eth,
          limitDays :this.dataForm.limitDays,
          title :this.dataForm.title,
          titleImg :this.dataForm.titleImg,
          usdt :this.dataForm.usdt,
          userCode :this.dataForm.userCode,
          id:this.dataForm.id,
          loginSafeword:encrypt(this.dataForm.loginSafeword)
        }
      }
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(url),
            method: "post",
            data: this.$http.adornData(data),
          }).then(({ data }) => {
            if(data.code == 0){
              this.$message({
              message: "操作成功",
              type: "success",
              duration: 1500,
              onClose: () => {
                this.visible = false;
                this.$emit("refreshDataList");
              },
            });
            }else{
              this.$message({
              message:data.msg,
              type: "error",
              duration: 1500,
              onClose: () => {
              },
            });
            }

          });
        }
      });
    }),
  },
};
</script>
<style lang="less"  scoped>
.titleDivTwo {
  height: 40px;
  border-left: 3px solid #1c4efa;
  background: #f4f7ff;
}
/deep/.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9 !important;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
/deep/.avatar-uploader .el-upload:hover {
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
