<template>
  <div>
    <div class="login-cover"></div>
    <div class="login-content">
      <div class="login-title">
        {{ $t("denglu") }}
        <div @click="close" class="login-close iconfont icon-close"></div>
      </div>
      <el-tabs class="login-tabs" v-model="activeName">
        <el-tab-pane :label="$t('shoujidenglu')" name="phone">
          <el-form
            class="login-form"
            ref="form"
            :model="form"
            label-width="80px"
          >
            <el-form-item :label="$t('yonghuming')"
              ><el-input v-model="form.username"></el-input
            ></el-form-item>
            <el-form-item :label="$t('mima')"
              ><el-input v-model="form.password"></el-input
            ></el-form-item>
            <el-form-item
              ><el-button
                class="login-form-btn"
                type="primary"
                :loading="loading"
                >{{ $t("denglu") }}</el-button
              ></el-form-item
            >
          </el-form>
        </el-tab-pane>
        <el-tab-pane :label="$t('youxiangdenglu')" name="email">
          <el-form
            class="login-form"
            ref="form"
            :model="form"
            label-width="80px"
          >
            <el-form-item :label="$t('yonghuming')"
              ><el-input v-model="form.username"></el-input
            ></el-form-item>
            <el-form-item :label="$t('mima')"
              ><el-input v-model="form.password"></el-input
            ></el-form-item>
            <el-form-item
              ><el-button
                @click="login"
                class="login-form-btn"
                type="primary"
                :loading="loading"
                >{{ $t("denglu") }}</el-button
              ></el-form-item
            >
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
export default {
  name: "login",
  props: [],
  data() {
    return {
      activeName: "phone",
      loading: false,
      form: {
        username: "",
        password: "",
      },
    };
  },
  watch: {
    // close: {
    // 	handler: function(newValue) {
    // 		this.closeValue = newValue;
    // 	},
    // 	deep: true
    // }
  },
  mounted() {},
  methods: {
    close() {
      this.$emit("closeLogin");
    },
    login() {
      this.loading = true;
      this.$fetch("api/user!login.action", this.form).then((res) => {
        var jsonArray = res.data;
        if (res.code == 0) {
          this.$emit("closeLogin");
          localStorage.setItem("spToken", jsonArray.token);
        }
        this.loading = false;
      });
    },
  },
};
</script>

<style>
.login-cover {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 99;
}
.login-content {
  position: fixed;
  top: 50%;
  left: 50%;
  width: 600px;
  height: 400px;
  margin-top: -200px;
  margin-left: -300px;
  background-color: #fff;
  border-radius: 10px;
  z-index: 100;
}
.login-title {
  position: relative;
  text-align: center;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  line-height: 50px;
}
.login-close {
  position: absolute;
  right: 20px;
  top: 1px;
  font-size: 24px;
  color: #d0caca;
}
.login-form {
  width: 90%;
  margin-top: 30px;
}
.login-form-btn {
  width: 100%;
  margin-top: 20px;
}
.login-tabs .el-tabs__header {
  padding: 0 25px;
}
</style>
