<template>
  <el-dialog
    :title="!dataForm.id ? 'Add' : 'Edit'"
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
      <el-form-item label="Phone" prop="phone">
        <el-input v-model="dataForm.phone" placeholder="Phone" clearable />
      </el-form-item>
      <el-form-item label="Phone All" prop="phoneAll">
        <el-input v-model="dataForm.phoneAll" placeholder="Phone All" clearable />
      </el-form-item>
      <el-form-item label="Email" prop="email">
        <el-input v-model="dataForm.email" placeholder="Email" clearable />
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">Cancel</el-button>
      <el-button type="primary" @click="dataFormSubmit()">OK</el-button>
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
            message: "Invalid phone number",
            trigger: "blur",
          },
        ],
        email: [
          {
            type: "email",
            message: "Invalid email format",
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
          this.dataForm.id = 0;
          this.dataForm.phone = "";
          this.dataForm.phoneAll = "";
          this.dataForm.email = "";
        }
      });
    },
    dataFormSubmit() {
      this.$refs.dataForm.validate((valid) => {
        if (valid) {
          if (!this.dataForm.phone && !this.dataForm.email) {
            this.$message.error("Phone and email cannot both be empty");
            return;
          }

          const url = this.dataForm.id
            ? "/admin/userOld/update"
            : "/admin/userOld/save";

          this.$http({
            url: this.$http.adornUrl(url),
            method: "post",
            data: this.$http.adornData({
              id: this.dataForm.id || undefined,
              phone: this.dataForm.phone || undefined,
              phoneAll: this.dataForm.phoneAll || undefined,
              email: this.dataForm.email || undefined,
            }),
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$message.success("Success");
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
