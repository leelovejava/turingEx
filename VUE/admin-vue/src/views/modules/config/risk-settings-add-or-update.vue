<template>
  <el-dialog
    :title="!id ? '新增' : '修改'"
    :close-on-click-modal="false"
    @close="handClose"
    width="700px"
    :visible.sync="visible"
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="90px"
    >
      <el-form-item v-if="id" label="用户名" prop="clientName">
        <el-input
          v-model="dataForm.clientName"
          disabled
          placeholder="用户名"
        ></el-input>
      </el-form-item>
      <el-form-item label="客户值类型">
        <el-select
          v-model="dataForm.clientType"
          @change="changeVal1()"
          class="inpspase"
          placeholder="请选择"
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
      <el-form-item
        label="UID"
        v-if="dataForm.clientType == 'userCode'"
        prop="userCode"
      >
        <el-input
          v-model="dataForm.userCode"
          clearable
          placeholder="UID"
        ></el-input>
      </el-form-item>
      <el-form-item label="IP" v-if="dataForm.clientType == 'ip'" prop="ip">
        <el-input v-model="dataForm.ip" clearable placeholder="ip"></el-input>
      </el-form-item>
      <el-form-item label="风控类型">
        <el-select
          v-model="dataForm.type"
          @change="changeVal()"
          class="inpspase"
          placeholder="请选择"
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
      <el-form-item label="状态">
        <el-select
          v-model="dataForm.status"
          @change="changeVal()"
          class="inpspase"
          placeholder="请选择"
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
      <el-form-item label="开始时间" prop="beginTime">
        <el-date-picker
          class="inpspase"
          v-model="dataForm.beginTime"
          type="datetime"
          clearable
          value-format="yyyy-MM-dd HH:mm:ss"
          placeholder="选择日期时间"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item label="结束时间" prop="endTime">
        <el-date-picker
          class="inpspase"
          v-model="dataForm.endTime"
          type="datetime"
          clearable
          value-format="yyyy-MM-dd HH:mm:ss"
          placeholder="选择日期时间"
        >
        </el-date-picker>
      </el-form-item>
      <!-- <el-form-item v-if="id" label="提现权限" label-width="100px">
        <el-select v-model="optionsThree.value3" @change="changeVal()" class="inpspase"                     
                     placeholder="请选择">
            <el-option v-for="item in optionsThree"
                       :key="item.value3"
                       :label="item.label3"
                       :value="item.value3">
            </el-option>
          </el-select>
      </el-form-item>
      <div v-if="id" style="margin:20px 0;color: rgb(124, 126, 124);padding-left: 100px">演示账号该设置不生效，默认无提现权限</div>
      <el-form-item label="是否业务锁定" label-width="100px">
        <el-select v-model="optionsTwo.value2" @change="changeVal()" class="inpspase"  
                     placeholder="请选择">
            <el-option v-for="item in optionsTwo"
                       :key="item.value2"
                       :label="item.label2"
                       :value="item.value2">
            </el-option>
          </el-select>
      </el-form-item>
      <el-form-item label="备注" label-width="100px" prop="remarks">
        <el-input type="textarea"  :autosize="{ minRows: 5, maxRows: 8}" show-word-limit  v-model="dataForm.remarks"></el-input>
      </el-form-item> -->
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { isEmail, isMobile } from "@/utils/validate";
import { Debounce } from "@/utils/debounce";
import { encrypt } from "@/utils/crypto";
export default {
  data() {
    var validatePassword = (rule, value, callback) => {
      if (!this.dataForm.id && !/\S/.test(value)) {
        callback(new Error("密码不能为空"));
      } else {
        callback();
      }
    };
    var validateEmail = (rule, value, callback) => {
      if (!isEmail(value)) {
        callback(new Error("邮箱格式错误"));
      } else {
        callback();
      }
    };
    var validateMobile = (rule, value, callback) => {
      if (!isMobile(value)) {
        callback(new Error("手机号格式错误"));
      } else {
        callback();
      }
    };
    return {
      visible: false,
      roleList: {},
      id: "",
      dataForm: {
        userCode: "",
        clientName: "",
        ip: "",
        type: "",
        beginTime: "",
        endTime: "",
        clientType: "",
        status: "",
      },
      options: [
        {
          label: "断网",
          value: "badnetwork",
        },
        {
          label: "黑名单",
          value: "black",
        },
      ],
      optionsTwo: [
        {
          label: "启用",
          value: 1,
        },
        {
          label: "未启用",
          value: 0,
        },
      ],
      optionsThree: [
        {
          label: "用户UID",
          value: "userCode",
        },
        {
          label: "ip地址",
          value: "ip",
        },
      ],
      dataRule: {
        username: [
          { required: true, message: "用户名不能为空", trigger: "blur" },
          // { pattern: /\s\S+|S+\s|\S/, message: '请输入正确的用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: "密码不能为空", trigger: "blur" },
        ],
        userCode: [{ required: true, message: "UID不能为空", trigger: "blur" }],
        ip: [
          { required: true, message: "ip不能为空", trigger: "blur" },
          { validator: this.validateIPAddress, trigger: "blur" },
        ],
      },
    };
  },
  methods: {
    init(row) {
      if (row) {
        this.dataForm = {...row};
        if (row.clientType === "userCode") {
        this.$set(this.dataForm, "userCode", row.clientKey);
        this.$set(this.dataForm, "ip", "");
      } else {
        this.$set(this.dataForm, "ip", row.clientKey);
        this.$set(this.dataForm, "userCode", "");
      }
        this.id = row.id;
      } else {
        this.dataForm.type = this.options[0].value;
        this.dataForm.status = this.optionsTwo[0].value;
        this.dataForm.clientType = this.optionsThree[0].value;
      }

      this.visible = true;
      this.$nextTick(() => {
        //this.$refs.dataForm.resetFields()
      });
    },
    validateIPAddress(rule, value, callback) {
      const parts = value.split('.');
      // console.log(parts)
      //   console.log(parts.length)
      if (parts.length !== 4) {
        // console.log(parts)
        // console.log(parts.length)
        return callback(new Error('请输入正确的 IP 地址'));
      }

      // for (let i = 0; i < parts.length; i++) {
      //   if (parts[i] !== '*' || !(/^\d+$/.test(parts[i]))) {
      //     console.log(2)
      //     console.log(parts)
      //     return callback(new Error('请输入正确的 IP 地址'));
      //   }
      // }

      callback();
    },
    changeVal(val) {
      this.$forceUpdate();
    },
    changeVal1(val) {
      this.$forceUpdate();
      this.$refs["dataForm"].clearValidate(); // 清除表单验证
      this.dataForm.userCode = "";
      this.dataForm.ip = "";
    },
    handClose() {
      this.id = "";
      this.$data.dataForm = JSON.parse(
        JSON.stringify(this.$options.data().dataForm)
      );
      this.$nextTick(() => {
        this.$refs["dataForm"].clearValidate(); // 清除表单验证
      });
    },
    // 表单提交
    dataFormSubmit: Debounce(function () {
      if (this.dataForm.userCode == "" && this.dataForm.ip == "") {
        this.$message({
          message: "UID和IP必须填一项",
          type: "error",
          duration: 1500,
          onClose: () => {
          },
        });
      } else {
        this.$refs["dataForm"].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/riskclient/save`), //修改
              method: "post",
              data: this.$http.adornData({
                userCode: this.dataForm.userCode,
                ip: this.dataForm.ip,
                type: this.dataForm.type,
                status: this.dataForm.status,
                beginTime: this.dataForm.beginTime,
                endTime: this.dataForm.endTime,
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
                });
              }
            });
          }
        });
      }
    }),
  },
};
</script>
<style scoped>
.inpspase {
  width: 570px;
}
</style>
