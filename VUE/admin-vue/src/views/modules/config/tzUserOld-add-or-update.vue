<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '编辑'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    append-to-body
  >
    <el-form
      ref="dataForm"
      :model="dataForm"
      :rules="dataRule"
      label-width="140px"
    >
      <el-form-item label="手机号码" prop="phone">
        <el-input
          v-model="dataForm.phone"
          placeholder="手机号码"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="手机号码(备用)" prop="phoneAll">
        <el-input
          v-model="dataForm.phoneAll"
          placeholder="手机号码(备用)"
          clearable
        ></el-input>
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input
          v-model="dataForm.email"
          placeholder="邮箱"
          clearable
        ></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  data() {
    return {
      visible: false,
      dataForm: {
        id: 0,
        phone: "",
        phoneAll: "",
        email: "",
      },
      dataRule: {
        phone: [
          {
            pattern: /^1[3-9]\d{9}$/,
            message: "手机号格式不正确",
            trigger: "blur",
          },
        ],
        email: [
          {
            type: "email",
            message: "邮箱格式不正确",
            trigger: "blur",
          },
        ],
      },
    };
  },
  methods: {
    init(row) {
      this.visible = true;
      this.$nextTick(() => {
        this.$refs.dataForm.resetFields();
        if (row && row.id) {
          // 编辑模式
          this.dataForm.id = row.id;
          this.$http({
            url: this.$http.adornUrl("/admin/userOld/getById"),
            method: "get",
            params: this.$http.adornParams({ id: row.id }),
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.dataForm.phone = data.data.phone;
              this.dataForm.phoneAll = data.data.phoneAll;
              this.dataForm.email = data.data.email;
            } else {
              this.$message.error(data.msg);
            }
          });
        } else {
          // 新增模式
          this.dataForm.id = 0;
          this.dataForm.phone = "";
          this.dataForm.phoneAll = "";
          this.dataForm.email = "";
        }
      });
    },
    // 表单提交
    dataFormSubmit() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          // 验证手机号和邮箱不能同时为空
          if (!this.dataForm.phone && !this.dataForm.email) {
            this.$message.error("手机号和邮箱不能同时为空");
            return;
          }

          const url = this.dataForm.id
            ? "/admin/userOld/update"
            : "/admin/userOld/save";
          const method = "post";

          this.$http({
            url: this.$http.adornUrl(url),
            method: method,
            data: this.$http.adornData({
              id: this.dataForm.id || undefined,
              phone: this.dataForm.phone || undefined,
              phoneAll: this.dataForm.phoneAll || undefined,
              email: this.dataForm.email || undefined,
            }),
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$message.success("操作成功");
              this.visible = false;
              this.$emit("refreshDataList");
            } else {
              this.$message.error(data.msg);
            }
          });
        }
      });
    },
  },
};
</script>