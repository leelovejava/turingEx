<template>
  <el-dialog
    :title="!dataForm ? '新增' : '修改'"
    :close-on-click-modal="false"
    width="1100px"
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
          <el-form-item label="用户账号" prop="userName">
            <el-input disabled v-model="dataForm.userName" placeholder="用户名称"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="借贷币种" prop="symbol">
            <el-input v-model="dataForm.symbol" placeholder="借贷币种"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="借贷额度" prop="quota">
            <el-input v-model="dataForm.quota" placeholder="借贷额度"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="8">
          <el-form-item label="审核状态" prop="state[1]" >
            <el-select
              class="speaInputTwo"
              v-model="dataForm.state[1]"
              :disabled = 'dataForm?true:false'
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
          <el-form-item label="借贷期限(天)" prop="term">
            <el-input disabled v-model="dataForm.term" placeholder="天"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="申请时间" prop="createTime">
            <el-input v-model="dataForm.createTime" :disabled = 'dataForm?true:false' placeholder="申请时间"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="8">
          <el-form-item label="还款周期(天)" prop="repayCycle">
            <el-input disabled v-model="dataForm.repayCycle" placeholder="天"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="日利率" prop="dailyRate">
            <el-input disabled v-model="dataForm.dailyRate" placeholder=""></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="放款机构" prop="lendingName">
            <el-input disabled v-model="dataForm.lendingName" placeholder=""></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="8">
          <el-form-item label="证件照正面" prop="methodImg">
            <el-upload
              class="avatar-uploader"
              :action="$http.adornUrl('/api/uploadFile')"
              :headers="{ Authorization: $cookie.get('Authorization') }"
              :show-file-list="true"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
            >
              <img v-if="dataForm.houseImgs && dataForm.houseImgs[0]" :src="dataForm.houseImgs[0]" class="avatar" />
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="证件照背面" prop="methodImg">
            <el-upload
              class="avatar-uploader"
              :action="$http.adornUrl('/api/uploadFile')"
              :headers="{ Authorization: $cookie.get('Authorization') }"
              :show-file-list="true"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
            >
              <img v-if="dataForm.houseImgs && dataForm.houseImgs[1]" :src="dataForm.houseImgs[1]" class="avatar" />
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="手持正面照" prop="methodImg">
            <el-upload
              class="avatar-uploader"
              :action="$http.adornUrl('/api/uploadFile')"
              :headers="{ Authorization: $cookie.get('Authorization') }"
              :show-file-list="true"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
            >
              <img v-if="dataForm.houseImgs && dataForm.houseImgs[2]" :src="dataForm.houseImgs[2]" class="avatar" />
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
          </el-form-item>
        </el-col>
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
export default {
  data() {
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
          label: "未审",
          value: "1",
        },
        {
          label: "通过",
          value: "2",
        },
        {
          label: "驳回",
          value: "3",
        },
      ],
      dataRule: {
        roleName: [
          { required: true, message: "角色名称不能为空", trigger: "blur" },
        ],
      },
      tempKey: -666666, // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
    };
  },
  methods: {
    init(id) {   
      this.dataForm = id || {};
      this.visible = true;
      console.log("this.dataForm = " + JSON.stringify(this.dataForm));
    },
    changeVal(val) {
      this.$forceUpdate();
    },
    handleAvatarSuccess(res, file) {
      this.dataForm.methodImg = res.data.path;
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
    // 表单提交
    dataFormSubmit: Debounce(function () {
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(`/normal/loanadmin!modify.action`),
            method: "post",
            data: this.$http.adornData({
              orderNo: this.dataForm.uuid,
              img_idimg_1: this.dataForm.houseImgs[0],
              img_idimg_2: this.dataForm.houseImgs[1],
              img_idimg_3: this.dataForm.houseImgs[2],
              partyId: this.dataForm.userName,
              symbol: this.dataForm.symbol,
              quota: this.dataForm.quota,
              state: this.dataForm.state,
              term: this.dataForm.term,
              createTime: this.dataForm.createTime,
              repayCycle: this.dataForm.repayCycle,
              dailyRate: this.dataForm.dailyRate,

            }),
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
                message: data.msg,
                type: "error",
                duration: 1500,
                onClose: () => {
                  this.visible = false;
                  this.$emit("refreshDataList");
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
