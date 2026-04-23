<template>
  <!-- 认缴 -->
  <div class="pay">
    <div class="pay-box">
      <header class="pay-box-header">
        <p class="fs-24" style="margin-bottom: 1px">{{ dataList.name }}</p>
        <p class="fs-16" style="margin-bottom: 8px; color: #747a8f">
          ({{ dataList.productName }})
        </p>
        <!-- <p class="fs-24" style="color: #F33368;">{{dataList.marketPrice}}</p> -->
      </header>
      <main class="pay-box-main">
        <div
          class="pay-box-main-text"
          v-for="(value, key, i) in valueList"
          :key="i"
        >
          <p style="color: #b7bdd1">{{ t(`message.user.${value}`) }}</p>
          <p>{{ dataList[key] }}</p>
        </div>
      </main>
      <footer class="pay-box-footer">
        <div class="pay-box-footer-number">
          <div class="flex-space-between" style="margin-bottom: 12px">
            <p class="fs-16 draw-title-color">
              {{ t(`message.user.zhongqianedu`) }}
            </p>
            <p class="fs-14 draw-text-color">{{ t(`message.user.gu`) }}</p>
          </div>
          <el-input
            class="pay-box-footer-number-input limitColor"
            v-model="dataList.winningNumber"
            readonly
          />
        </div>
        <div class="pay-box-footer-number">
          <div class="flex-space-between" style="margin-bottom: 12px">
            <p class="fs-16 draw-title-color">
              {{ t(`message.user.renjiaojine`) }}
            </p>
            <p class="fs-14 draw-text-color">USD</p>
          </div>
          <el-input
            class="pay-box-footer-number-input"
            v-model="amountValue"
            :placeholder="t(`message.user.qingshururenjiaojine`)"
            @input="(value) => (amountValue = value.replace(/^(0+)|[^\d]+/g, ''))"
          />
        </div>
        <div class="pay-box-footer-number">
          <div class="flex-space-between" style="margin-bottom: 12px">
            <p class="fs-16 draw-title-color">
              {{ t(`message.user.dangqiankeyongyue`) }}
            </p>
            <p class="fs-14 draw-text-color">USD</p>
          </div>
          <el-input
            class="pay-box-footer-number-input"
            v-model="dataList.defaultLimit"
            readonly
          />
        </div>
        <div class="pay-box-footer-button">
          <button class="pay-button" v-click="payButton">
            {{ t(`message.user.yijianrenjiao`) }}
          </button>
        </div>
      </footer>
    </div>
  </div>
</template>

<script setup>
import Axios from "@/api/newShares";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
const route = useRoute();
const router = useRouter();

const dataList = ref({});
onMounted(() => {
  const { id } = route.query;
  Axios.getDrawMessage({ id }).then((res) => {
    if (res.code === 0) {
      dataList.value = res.data;
    }
  });
});

const valueList = {
  subscribeTotalNumber: "faxingzongzhangshu",
  startSubscribeDate: "renjiaokaishiri",
  endSubscribeDate: "renjiaojiezhiri",
  drawDate: "renjiaori",
  issuanceDate: "faquanri",
};

const amountValue = ref("");

const payButton = () => {
  if (!amountValue.value) {
    ElMessage({
      message: t(`message.user.qingshururenjiaojine`),
      type: "warning",
    });
    return;
  }
  const params = {
    orderNo: dataList.value.orderNo,
    deductNumber: amountValue.value,
  };
  Axios.setPayMessage(params).then((res) => {
    if (res.code === 0) {
      ElMessage({
        message: t(`message.user.renjiaochenggong`),
        type: "success",
      });
      router.replace({
        path: "/newShareSrecord/payRecord",
        query: {
          type: 2,
        },
      });
    }
  });
};
</script>

<style lang="scss" scoped>
.pay {
  display: flex;
  justify-content: center;
  .pay-box {
    margin-top: 30px;
    width: 614px;
    min-height: 700px;
    background: #27293b;
    color: #fff;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0px 0px 12.85863971710205px 0px #00000033;
    .pay-box-header {
      background: #192137;
      height: 137px;
      padding: 18px;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
    }
  }
  .pay-box-main {
    padding: 24px 37px 30px;
    .pay-box-main-text {
      display: flex;
      justify-content: space-between;
      margin-bottom: 15px;
      &:nth-last-of-type(1) {
        margin-bottom: 0;
      }
    }
  }
  .pay-box-footer {
    padding: 0px 37px 30px;
    :deep(.pay-box-footer-number) {
      margin-bottom: 20px;
      .el-input__wrapper {
        height: 60px;
        background: #1b2134;
        box-shadow: none;
        .el-input__inner {
          text-align: center;
          font-size: 18px;
          color: #fff;
        }
      }
      &:nth-last-of-type(1) {
        margin-bottom: 0;
      }
      .limitColor {
        .el-input__wrapper {
          background: #2d344b;
        }
      }
    }
    .pay-box-footer-button {
      .pay-button {
        width: 100%;
        height: 60px;
        background: linear-gradient(90deg, #2c64d4 0%, #38aeea 100%);
        font-size: 18px;
      }
    }
  }
}
</style>
