<!-- 安全列表 -->
<template>
  <div>
    <div style="margin-top: 50px">
      <Steps :stepIndex="4" />
      <main class="main css-6q12pm">
        <div class="css-108ucgm">
          <div class="binance-row css-16kn2us">
            <div class="binance-col css-1wz0uwi" style="margin-left: -12px">
              <div class="css-cz4mv2">
                <div class="css-6x3lo1">
                  <div class="css-vurnku">
                    <div class="css-ojbpe7">
                      {{ $t("message.home.zhucechenggong") }}
                    </div>

                    <div class="css-vurnku">
                      <!-- 列表 -->
                      <div class="css-15651n7">
                        <!-- 行情页 -->
                        <div class="list-item" @click="jumpOver('/market')">
                          <div class="list-item-left">
                            <p class="item-name">
                              {{ $t("woxiangyaomaibi") }}
                            </p>
                            <p style="margin-top: 30px">
                              {{ $t("message.home.home6")
                              }}<i class="el-icon-right"></i>
                            </p>
                          </div>
                          <div>
                            <img
                              style="width: 64px; height: 70px"
                              src="@/assets/images/login/market.png"
                            />
                          </div>
                        </div>
                        <!-- 充值页 -->
                        <div class="list-item" @click="jumpOver('/recharge')">
                          <div class="list-item-left">
                            <p class="item-name">
                              {{ $t("woxiangyaochongzhi") }}
                            </p>
                            <p style="margin-top: 30px">
                              {{ $t("quchongzhi")
                              }}<i class="el-icon-right"></i>
                            </p>
                          </div>
                          <div>
                            <img
                              style="width: 69px; height: 60px"
                              src="@/assets/images/login/recharge.png"
                            />
                          </div>
                        </div>
                      </div>
                      <!-- 完成 -->
                      <button
                        @click="jumpOver('/')"
                        data-bn-type="button"
                        class="css-1bsmpdm"
                      >
                        {{ $t("wancheng") }}
                      </button>
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
  name: "secureList",
  data() {
    return {
      stepIndex: 4,
    };
  },
  components: {
    Steps,
  },
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
  height: 100px;
  padding: 0 15px;
  background: #f5f5f5;
  border-radius: 4px;
}

.list-item-left {
}

.list-item-left img {
  display: block;
}

.item-name {
  font-weight: bold;
  font-size: 18px;
  color: #333;
}
</style>
