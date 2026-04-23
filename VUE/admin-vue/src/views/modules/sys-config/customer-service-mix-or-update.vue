<template>
  <el-dialog
    :title="m"
    :close-on-click-modal="false"
    @close="handClose"
    :visible.sync="visible"
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="120px"
    >
      <el-form-item v-if="val == 2 || val == 3" label="用户名" prop="userName">
        <el-input
          v-model="dataForm.userName"
          :disabled="row ? true : false"
          placeholder="请输入后台登录用户名"
        ></el-input>
      </el-form-item>
      <el-form-item v-if="val == 2" label="新密码" prop="password">
        <el-input
          v-model="dataForm.password"
          type="password"
          placeholder="请输入密码"
        ></el-input>
      </el-form-item>
      <el-form-item v-if="val == 3" label="新资金密码" prop="safePassword">
        <el-input
          v-model="dataForm.safePassword"
          type="password"
          placeholder="请输入资金密码"
        ></el-input>
      </el-form-item>
      <el-form-item
        v-if="val == 2 || val == 3 || val == 4"
        label="登录人资金密码"
        :label-width="formLabelWidth"
        prop="loginSafeword"
      >
        <el-input
          v-model="dataForm.loginSafeword"
          type="password"
          placeholder="登录人资金密码"
          autocomplete="off"
        ></el-input>
      </el-form-item>
      <el-form-item
        v-if="val == 2 || val == 3"
        label="超级谷歌验证码"
        :label-width="formLabelWidth"
        prop="superGoogleAuthCode"
      >
        <el-input
          v-model="dataForm.superGoogleAuthCode"
          placeholder="超级谷歌验证码"
          autocomplete="off"
        ></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
    <!-- 确认弹窗-start -->
    <!-- <el-dialog title="确认增加" :visible.sync="dialogFormVisible" :append-to-body="true">
      <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="open()" label-width="80px">
        <el-form-item label="登录人资金密码" :label-width="formLabelWidth" prop="loginSafeword">
          <el-input v-model="dataForm.loginSafeword" type="password" placeholder="登录人资金密码" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item v-if="!row" label="超级谷歌验证码" :label-width="formLabelWidth" prop="superGoogleAuthCode">
          <el-input v-model="dataForm.superGoogleAuthCode" placeholder="超级谷歌验证码" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="dataFormSubmit()">确 定</el-button>
      </div>
    </el-dialog> -->
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
      // form: {
      //   loginSafeword	: '',     //登录人资金密码
      //   superGoogleAuthCode: '' //超级管理员谷歌验证码
      // },
      m: "",
      formLabelWidth: "120px",
      dialogFormVisible: true,
      visible: false,
      row: "",
      val: "",
      menuList: [],
      enabled: [
        {
          value: true,
          label: "开启",
        },
        {
          value: false,
          label: "关闭",
        },
      ],
      menuListTreeProps: {
        label: "name",
        children: "children",
      },
      dataForm: {
        id: "",
        userName: "",
        password: "",
        safePassword: "",
        loginSafeword: "",
        superGoogleAuthCode: "",
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
        userName: [
          { required: true, message: "用户名不能为空", trigger: "blur" },
        ],
        safePassword: [
          { required: true, message: "资金密码不能为空", trigger: "blur" },
        ],
        password: [
          { required: true, message: "密码不能为空", trigger: "blur" },
        ],
        loginSafeword: [
          { required: true, message: "登录人资金密码不能为空", trigger: "blur" },
        ],
        superGoogleAuthCode: [
          { required: true, message: "超级谷歌验证码不能为空", trigger: "blur" },
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
      tempKey: -666666, // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
    };
  },
  methods: {
    init(val, row, m) {
      this.val = val || "";
      this.row = row || "";
      this.m = m || "";
      this.visible = true;
      if (this.row) {
        this.dataForm.userName = row.userName;
        this.dataForm.remarks = row.remarks;
        this.dataForm.autoAnswer = row.autoAnswer;
        this.dataForm.id = row.id;
      } else {
        this.enabled.value = this.enabled[0].value;
      }
      this.dialogFormVisible = false;
    },
    open() {
      this.dialogFormVisible = true;
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
    // 表单提交
    dataFormSubmit: Debounce(function () {
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          let ur = "";
          let dat = "";
          let a = {
            // 修改密码
            id: this.dataForm.id,
            loginSafeword: encrypt(this.dataForm.loginSafeword),
            password: encrypt(this.dataForm.password),
            superGoogleAuthCode: this.dataForm.superGoogleAuthCode,
          };
          let aa = `/customer/updatePassword`;

          let b = {
            // 修改资金密码
            id: this.dataForm.id,
            loginSafeword: encrypt(this.dataForm.loginSafeword),
            safePassword: encrypt(this.dataForm.safePassword),
            superGoogleAuthCode: this.dataForm.superGoogleAuthCode,
          };
          let bb = `/customer/updateSafePassword`;

          let c = {
            // 强制下线
            id: this.dataForm.id,
            loginSafeword: encrypt(this.dataForm.loginSafeword),
          };
          let cc = `/customer/forceOffline`;

          if (this.val == 2) {
            ur = aa;
            dat = a;
          } else if (this.val == 3) {
            ur = bb;
            dat = b;
          } else if (this.val == 4) {
            ur = cc;
            dat = c;
          }
          this.$http({
            url: this.$http.adornUrl(ur),
            method: "post",
            // data:data1
            data: this.$http.adornData(dat),
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
                  
                },
              });
            }
          });
        }
      });

      //end
    }),
  },
};
</script>
