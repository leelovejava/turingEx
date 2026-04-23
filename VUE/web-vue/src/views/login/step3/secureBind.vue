<!-- 安全绑定 -->
<template>
  <div>
    <div style="margin-top: 50px">
      <Steps :stepIndex="3" />
      <main class="main css-6q12pm">
        <div class="css-108ucgm">
          <div class="binance-row css-16kn2us">
            <div class="binance-col css-1wz0uwi" style="margin-left: -12px">
              <div class="css-cz4mv2">
                <div class="css-6x3lo1">
                  <div class="css-vurnku">
                    <div class="css-ojbpe7">
                      {{ $t("anquanbangding") }}
                    </div>

                    <div class="css-vurnku">
                      <!-- 列表 -->
                      <div class="css-15651n7">
                        <div class="list-item" @click="jumpOver('/googleSet')">
                          <div class="list-item-left">
                            <img
                              style="
                                width: 24px;
                                height: 24px;
                                margin-right: 5px;
                              "
                              src="@/assets/images/login/google.png"
                            />
                            {{ $t("gugeyanzhengqibangding") }}
                          </div>
                          <div>
                            <i class="el-icon-arrow-right"></i>
                          </div>
                        </div>
                        <!-- <div class="list-item" @click="jumpOver('/emailSet')">
                          <div class="list-item-left">
                            <img style="width: 24px;height: 24px;margin-right: 5px;" src="@/assets/newImages/email.png" />
                            邮箱验证
                          </div>
                          <div>
                            <i class="el-icon-arrow-right"></i>
                          </div>
                        </div>
                        <div class="list-item" @click="jumpOver('/phoneSet')">
                          <div class="list-item-left">
                            <img style="width: 24px;height: 24px;margin-right: 5px;" src="@/assets/newImages/phone.png" />
                            手机号验证
                          </div>
                          <div>
                            <i class="el-icon-arrow-right"></i>
                          </div>
                        </div> -->
                      </div>

                      <button
                        data-bn-type="button"
                        @click="jumpOver('/setDone')"
                        class="css-1bsmpdm"
                      >
                        {{ $t("xiayibu") }}
                      </button>
                      <div
                        @click="jumpOver('/setDone')"
                        style="
                          text-align: center;
                          padding: 20px 0;
                          color: #868d9a;
                          cursor: pointer;
                        "
                      >
                        {{ $t("tiaoguo") }}
                      </div>
                    </div>
                    <div class="css-vurnku"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>
<script>
import Steps from "../components/steps.vue";
export default {
  name: "secureBind",
  components: {
    Steps,
  },

  created() {},
  computed: {},
  methods: {
    jumpOver(url) {
      this.$router.push(url);
    },
  },
};
</script>
<style scoped>
@import url("@/assets/css/login/register.css");

.el-input--suffix >>> .el-input__inner {
  padding-right: 30px;
  outline: none;
  border: none;
}

.list-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  margin-bottom: 20px;
  border: 1px solid #e9ecef;
  height: 50px;
  padding: 0 15px;
}

.list-item-left {
  display: flex;
  align-items: center;
}

.list-item-left img {
  display: block;
}
</style>
