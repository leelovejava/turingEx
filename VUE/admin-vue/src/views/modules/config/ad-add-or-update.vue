<template>
  <el-dialog
    :title="!dataForm.id ? '新增横幅' : '修改横幅'"
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
      <el-form-item  label="业务代码" prop="contentCode">
        <el-input
          v-model="dataForm.contentCode"
          placeholder="业务代码(同种内容不同语言代码相同)"
        ></el-input>
      </el-form-item>
      <el-form-item label="资金密码" prop="loginSafeword">
        <el-input
          v-model="dataForm.loginSafeword"
          type="password"
          placeholder="资金密码"
        ></el-input>
      </el-form-item>
      <el-form-item label="图片" prop="methodImg">
        <el-upload
          class="avatar-uploader"
          :action="$http.adornUrl('/api/uploadFile')"
          :headers="{ Authorization: $cookie.get('Authorization') }"
          :show-file-list="true"
          :on-success="handleAvatarSuccess"
          :before-upload="beforeAvatarUpload"
        >
          <img v-if="dataForm.image" :src="dataForm.image" class="avatar" />
          <i v-else class="el-icon-plus avatar-uploader-icon"></i>
        </el-upload>
      </el-form-item>

      <el-form-item  label="路径">
        <el-input v-model="dataForm.url" placeholder="路径"></el-input>
      </el-form-item>
      <el-form-item>
        <div class="green">排序索引，数字越小越靠前</div>
      </el-form-item>
      <el-form-item label="排序索引" prop="sortIndex">
        <el-input
          v-model="dataForm.sortIndex"
          placeholder="排序索引,数字越小越靠前"
        ></el-input>
      </el-form-item>

      <el-form-item  label="模块">
        <el-select
          v-model="model.value"
          placeholder="请选择"
          @change="changeVal()"
        >
          <el-option
            v-for="item in model"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item  label="语言">
        <el-select
          v-model="langug.value"
          clearable
          placeholder="请选择"
          @change="changeVal()"
        >
          <el-option
            v-for="item in langug"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="是否可点击">
        <el-select
          v-model="click.value"
          placeholder="请选择"
          @change="changeVal()"
        >
          <el-option
            v-for="item in click"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="是否展示">
        <el-select
          v-model="onShow.value"
          placeholder="请选择"
          @change="changeVal()"
        >
          <el-option
            v-for="item in onShow"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
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
    var validateindex = (rule, value, callback) => {
      if (this.dataForm.sortIndex < 0) {
        callback(new Error("排序不能小于0"));
      } else {
        callback();
      }
    };
    return {
      imageUrl: "",
      dialogImageUrl: "",
      dialogVisible: false,
      visible: false,
      row: "",
      langug: [], // 语言
      menuList: [],
      menuListTreeProps: {
        label: "name",
        children: "children",
      },
      model: [
        {
          label: "顶部展示",
          value: "top",
        },
        {
          label: "其他地方展示",
          value: "other",
        },
        {
          label: "弹窗海报",
          value: "poster",
        },
      ],
      onShow: [
        {
          label: "是",
          value: 1,
        },
        {
          label: "否",
          value: 0,
        },
      ],
      click: [
        {
          label: "是",
          value: 1,
        },
        {
          label: "否",
          value: 0,
        },
      ],
      dataForm: {
        id: "",
        contentCode: "",
        image: "",
        loginSafeword: "",
        sortIndex: "",
        url: "",
        langCode:'',
      },
      dataRule: {
        roleName: [
          { required: true, message: "角色名称不能为空", trigger: "blur" },
          {
            pattern: /\s\S+|S+\s|\S/,
            message: "请输入正确的角色名称",
            trigger: "blur",
          },
        ],
        sortIndex: [
          { validator: validateindex, trigger: "blur" },
          { required: true, message: "排序不能为空", trigger: "blur" },
        ],
        contentCode: [
          { required: true, message: "业务代码不能为空", trigger: "blur" },
        ],
        loginSafeword: [
          { required: true, message: "资金密码不能为空", trigger: "blur" },
        ],
        image: [{ required: true, message: "图片不能为空", trigger: "blur" }],
        remark: [
          {
            required: false,
            pattern: /\s\S+|S+\s|\S/,
            message: "输入格式有误",
            trigger: "blur",
          },
        ],
      },
      tempKey: -666666, // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
    };
  },
  methods: {
    init(arr, row) {
      this.langug = arr;
      this.row = row || "";
      console.log(arr)
      if (this.row) {
        this.click.value = row.click;
        this.onShow.value = row.onShow;
        this.dataForm.sortIndex = row.sortIndex;
        this.dataForm.contentCode = row.contentCode;
        this.dataForm.url = row.url;
        this.dataForm.image = row.image;
        this.dataForm.id = row.uuid;
        this.langug.value = row.language
        this.model.value = row.model
      } else {
        this.click.value = this.click[0].value;
        this.onShow.value = this.onShow[0].value;
        this.model.value = this.model[0].value;
        this.langug.value = this.langug[0].value;
      }
      this.$nextTick(() => {
        //this.$refs.dataForm.resetFields()
      });
      this.visible = true;
    },
    // 表单提交
    handClose() {
      this.$data.dataForm = JSON.parse(
        JSON.stringify(this.$options.data().dataForm)
      ); //清除表单数据 拷贝数据
      this.$nextTick(() => {
        this.$refs["dataForm"].clearValidate(); // 清除表单验证
      });
    },
    dataFormSubmit: Debounce(function () {
      if (!this.dataForm.image) {
        this.$message({
          message: "上传图片不能为空",
          type: "error",
          duration: 1500,
          onClose: () => {},
        });
        return;
      }

      if (this.row) {
        //start
        this.$refs["dataForm"].validate((valid) => {
          // 修改
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/banner/update`),
              method: "post",
              data: this.$http.adornData({
                click: this.click.value, //是否可以点击
                contentCode: this.dataForm.contentCode, //业务代码， 同种内容 不同语言下的code相同
                id: this.dataForm.id, //修改传id 新增不传
                image: this.dataForm.image, //展示图片
                language: this.langug.value, //语言
                loginSafeword: encrypt(this.dataForm.loginSafeword), //资金密码
                model: this.model.value, //类型，top:顶部展示，other:其他地方展示,poster:弹窗海报
                onShow: this.onShow.value, //是否展示
                sortIndex: this.dataForm.sortIndex, //	排列顺序（数字相同按时间排，越小排越前）
                url: this.dataForm.url, //访问路径
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
        //start
        this.$refs["dataForm"].validate((valid) => {
          // 新增
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/banner/add`),
              method: "post",
              data: this.$http.adornData({
                click: this.click.value, //是否可以点击
                contentCode: this.dataForm.contentCode, //业务代码， 同种内容 不同语言下的code相同
                image: this.dataForm.image, //展示图片
                language: this.langug.value, //语言
                loginSafeword: encrypt(this.dataForm.loginSafeword), //资金密码
                model: this.model.value, //类型，top:顶部展示，other:其他地方展示,poster:弹窗海报
                onShow: this.onShow.value, //是否展示
                sortIndex: this.dataForm.sortIndex, //	排列顺序（数字相同按时间排，越小排越前）
                url: this.dataForm.url, //访问路径
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
                    //this.visible = false;
                  },
                });
              }
            });
          }
        });
      }
    }),
    changeVal(val) {
      this.$forceUpdate();
    },
    handleAvatarSuccess(res, file) {
      // this.dataForm.methodImg = res.data.path
      //{"data":{"path":"null/2023-04-29/d3f084ea-391f-4ec9-a2dd-f9393221f58f.png",
      //"httpUrl":"https://trading-order-test.s3.amazonaws.com/null/2023-04-29/d3f084ea-391f-4ec9-a2dd-f9393221f58f.png"},"code":0,"msg":"","succeed":false}
      console.log(res);
      console.log(file);
      this.imageUrl = URL.createObjectURL(file.raw); //显示地址
      this.dataForm.image = res.data.path; //接口传递
      console.log(this.imageUrl);
      if (res.code == 0) {
        this.dataForm.image = res.data.httpUrl;
      }
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
