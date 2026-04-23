<template>
  <el-dialog
    :title="'请重新验证谷歌验证码'"
    :close-on-click-modal="false"
    :show-close="false"
    :visible.sync="visible"
    width="600px"
    ref="ipPop"
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="100px"
    >
      <el-form-item label="" prop="rata">
        <div class="red">此次操作ip与上次登录ip不相符</div>
      </el-form-item>
      <!-- <el-form-item label="登陆人账户" prop="rata">
        <el-input
          v-model="userName"
          disabled
          placeholder="登陆人账户"
        ></el-input>
      </el-form-item> -->
      <el-form-item label="谷歌验证码" prop="gooleAuthCode">
        <el-input
          v-model="dataForm.gooleAuthCode"
          placeholder="请输入"
        ></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { treeDataTranslate } from "@/utils";
import { Debounce } from "@/utils/debounce";
export default {
  data() {
    return {
      visible: true,
      menuList: [],
      menuListTreeProps: {
        label: "name",
        children: "children",
      },
      dataForm: {
        id: 0,
        roleName: "",
        remark: "",
        gooleAuthCode: "",
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
        gooleAuthCode: [
          { required: true, message: "谷歌验证码不能为空", trigger: "blur" },
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
  computed: {
    // userName: {
    //   // 获取到登录用户名
    //   get() {
    //     return this.$store.state.user.name;
    //   },
    //   set(val) {
    //     this.$store.commit("user/updateName", val);
    //   },
    // },
  },
  mounted() {},
  methods: {
    // 表单提交
    dataFormSubmit: Debounce(function () {
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(`/updateCheckIp`),
            method: "post",
            data: this.$http.adornData({
              gooleAuthCode: this.dataForm.gooleAuthCode,
            }),
          }).then(({ data }) => {
            if (data.code == 0) {
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.removeComponent()
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
    removeComponent() {
      // 销毁当前组件实例
      this.$destroy();

      // 获取当前组件的根元素
      const element = this.$el;

      // 从 DOM 中移除当前组件的根元素
      if (element && element.parentNode) {
        element.parentNode.removeChild(element);
      }
    }
  
  },
};
</script>
