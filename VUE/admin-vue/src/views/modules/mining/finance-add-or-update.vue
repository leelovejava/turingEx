<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
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
      <el-row>
        <el-col :span="8">
          <el-form-item label="产品名称(简体中文)" prop="name">
            <el-input
              v-model="dataForm.name"
              placeholder="产品名称(简体中文)"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="产品名称(繁体中文)" prop="name_cn">
            <el-input
              v-model="dataForm.name_cn"
              placeholder="产品名称(繁体中文)"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="产品名称(英文)" prop="name_en">
            <el-input
              v-model="dataForm.name_en"
              placeholder="产品名称(英文)"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="8">
          <el-form-item label="产品名称(韩语)" prop="name_kn">
            <el-input
              v-model="dataForm.name_kn"
              placeholder="产品名称(韩语)"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="产品名称(日语)" prop="name_jn">
            <el-input
              v-model="dataForm.name_jn"
              placeholder="产品名称(日语)"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="支付方式图片" prop="img">
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
      <el-form-item class="titleDivTwo" label="交易信息" prop="">
      </el-form-item>
      <el-row>
        <el-col :span="8">
          <el-form-item label="状态" prop="state">
            <el-select
              class="speaInputTwo"
              v-model="dataForm.state"
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
          <el-form-item label="周期" prop="cycle">
            <el-input
              class="speaInputTwo"
              v-model="dataForm.cycle"
              placeholder="周期"
            >
              <template v-slot:append>
                <span>天</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="11">
          <el-form-item label="日利率" prop="daily_rate">
            <el-input v-model="dataForm.daily_rate" placeholder="">
              <template v-slot:append>
                <span>%</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="2">          
          <el-form-item label="— —" prop="">
          </el-form-item></el-col>
        <el-col :span="11">
          <el-form-item label="" prop="daily_rate_max">
            <el-input v-model="dataForm.daily_rate_max"  placeholder="">
              <template v-slot:append>
                <span>%</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="8">
          <el-form-item label="今日利率" prop="today_rate">
            <el-input
              v-model="dataForm.today_rate"
              placeholder="今日利率"
            ><template v-slot:append>
                <span>%</span>
              </template></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="违约结算比例" prop="default_ratio">
            <el-input
              v-model="dataForm.default_ratio"
              placeholder="违约结算比例"
            ><template v-slot:append>
                <span>%</span>
              </template></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="11">
          <el-form-item label="投资金额区间" prop="investment_min">
            <el-input v-model="dataForm.investment_min" placeholder="">
              <template v-slot:append>
                <span>USDT</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="2">          
          <el-form-item label="— —" prop="">
          </el-form-item></el-col>
        <el-col :span="11">
          <el-form-item label="" prop="investment_max">
            <el-input v-model="dataForm.investment_max"  placeholder="">
              <template v-slot:append>
                <span>USDT</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="11">
          <el-form-item  label="登录人资金密码"  prop="login_safeword">
        <el-input v-model="dataForm.login_safeword" type="password" placeholder="请输入登录人资金密码"></el-input>
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
    var validateindex = (rule, value, callback) => {
      if (this.dataForm.cycle < 0 || this.dataForm.cycle == 0) {
        callback(new Error("周期不能小于等于0"));
      } else {
        callback();
      }
    };
    var validatedaily_rate = (rule, value, callback) => {
      if (this.dataForm.daily_rate < 0 ) {
        callback(new Error("日利率不能小于0"));
      } else if(this.dataForm.daily_rate > this.dataForm.daily_rate_max) {
        callback(new Error("日利率初始值不能大于结算值"));
      }else{
        callback();
      }
    };
    var validatedaily_rate_max = (rule, value, callback) => {
      if (this.dataForm.daily_rate_max < this.dataForm.daily_rate) {
        callback(new Error("日利率结算值不能小于初始值"));
      }else if(this.dataForm.daily_rate_max <= 0){
        callback(new Error("日利率结算值不能为0"));
      } else {
        callback();
      }
    };
    var validatetoday_rate = (rule, value, callback) => {
      if (this.dataForm.today_rate < 0 ) {
        callback(new Error("今日利率不能小于0"));
      } else {
        callback();
      }
    };
    var validatetodefault_ratio = (rule, value, callback) => {
      if (this.dataForm.default_ratio < 0 ) {
        callback(new Error("违约结算比例不能小于0"));
      } else {
        callback();
      }
    };
    var validatetodeinvestment_min = (rule, value, callback) => {
      if (this.dataForm.investment_min <= 0 ) {
        callback(new Error("投资金额区间不能小于0"));
      } else {
        callback();
      }
    };
    var validatetodeinvestment_max = (rule, value, callback) => {
      if (this.dataForm.investment_max <= this.dataForm.investment_min) {
        callback(new Error("区间结算值不能小于等于初始值"));
      }else if(this.dataForm.investment_max <= 0){
        callback(new Error("区间结算值不能小于等于初始值"));
      } else {
        callback();
      }
    };
    return {
      visible: false,
      imageUrl: "",
      dataForm: {
        id: 0,
        roleName: "",
        remark: "",
        name: "",
        state: "",
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
        name: [
          { required: true, message: "名称不能为空", trigger: "blur" },
        ],
        name_cn: [
          { required: true, message: "名称不能为空", trigger: "blur" },
        ],
        name_en: [
          { required: true, message: "名称不能为空", trigger: "blur" },
        ],
        state: [
          { required: true, message: "状态不能为空", trigger: "blur" },
        ],
        cycle: [
          { validator: validateindex, trigger: "blur" },
          { required: true, message: "周期不能为空", trigger: "blur" },
        ],
        daily_rate: [
          { validator: validatedaily_rate, trigger: "blur" },
          { required: true, message: "日利率不能为空", trigger: "blur" },
        ],
        daily_rate_max: [
          { validator: validatedaily_rate_max, trigger: "blur" },
          { required: true, message: "日利率不能为空", trigger: "blur" },
        ],
        today_rate: [
          { validator: validatetoday_rate, trigger: "blur" },
          { required: true, message: "今日利率不能为空", trigger: "blur" },
        ],
        default_ratio: [
          { validator: validatetodefault_ratio, trigger: "blur" },
          { required: true, message: "违约结算比例不能为空", trigger: "blur" },
        ],
        investment_min: [
          { validator: validatetodeinvestment_min, trigger: "blur" },
          { required: true, message: "投资金额区间不能为空", trigger: "blur" },
        ],
        investment_max: [
          { validator: validatetodeinvestment_max, trigger: "blur" },
          { required: true, message: "投资金额区间不能为空", trigger: "blur" },
        ],
        login_safeword: [
            { required: true, message: '登录人资金密码不能为空', trigger: 'blur' },
        ],
        img: [
            { required: true, message: '上传图片不能为空', trigger: 'blur' },
        ],
      },
      tempKey: -666666, // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
    };
  },
  methods: {
    init(row) {
      this.dataForm = {...row};
      if(this.dataForm.img){
        this.imageUrl = this.dataForm.img
      }
      this.visible = true;
    },
    handleAvatarSuccess(res, file) {
      this.dataForm.img = res.data.path;
      console.log(file);
      this.imageUrl = URL.createObjectURL(file.raw);
      console.log(this.imageUrl);
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
     this.$nextTick(() => {
            this.$refs['dataForm'].clearValidate() // 清除表单验证
          })
      },
    changeVal(val) {
      this.$forceUpdate();
    },
    // 表单提交
    dataFormSubmit: Debounce(function () {

      if (!this.dataForm.img) {
        this.$message.error("上传图片不能为空");
        return;
      }

      let url = `/normal/adminFinanceAction!update.action`;
      if(!this.dataForm.id){
        url = `/normal/adminFinanceAction!add.action`;
      }
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(url),
            method: "get",
            params: this.$http.adornParams({
              ...this.dataForm,
              login_safeword:encrypt(this.dataForm.login_safeword)
            }),
          }).then(({ data }) => {
            console.log("data => " + JSON.stringify(data));
            this.$message({
              message: "操作成功",
              type: "success",
              duration: 1500,
              onClose: () => {
                this.visible = false;
                this.$emit("refreshDataList");
              },
            });
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
</style>
