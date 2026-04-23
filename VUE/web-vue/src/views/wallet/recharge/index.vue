<template>
  <div class="page">
    <div class="headbg flex justify-between">
      <div>{{ t("message.user.qingxuanzerujinfangshi") }}</div>
      <p class="fs-12 blue-color cursor-pointer" @click="gotoRecord">
        {{ $t("message.user.chongzhi") }} {{ $t("message.user.jilu") }}
      </p>
    </div>
    <div class="xcenter">
      <div class="tipbg mt-12 mb-16">{{ t("message.user.chongzhitishi") }}</div>
      <div class="mb-4">
        {{ t("message.user.qingxuanzerujinfangshi") }}
      </div>
      <div class="flex">
        <div
          class="item"
          :class="activeType == 'bank' ? 'item-active' : ''"
          @click="handleBank"
        >
          <img src="@/assets/images/wallet/recharge/bank.png" />
          <div>{{ t("message.user.yinhangkachongzhi") }}</div>
        </div>
        <div
          class="item"
          :class="activeType == 'usdt' ? 'item-active' : ''"
          @click="handleUsdt"
        >
          <img src="@/assets/images/wallet/recharge/usdt.png" />
          <div>{{ t("message.user.usdtchongzhi") }}</div>
        </div>
      </div>
      <div class="btn-wrapper">
        <el-button @click="goBack">{{ t("message.user.quxiao") }}</el-button>
        <el-button type="primary" @click="goPage">{{
          t("message.user.queren")
        }}</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { useI18n } from "vue-i18n";

const { t } = useI18n();

const router = useRouter();
const activeType = ref("bank");

const handleBank = () => {
  activeType.value = "bank";
};
const handleUsdt = () => {
  activeType.value = "usdt";
};
const goPage = () => {
  router.push(`/recharge/${activeType.value}`);
};

const gotoRecord = () => {
  router.push(`/recharge/record`);
};

const goBack = ()=>{
  router.go(-1)
}
</script>
<style lang="scss" scoped>
.page {
  width: 100vw;
  position: relative;
  .headbg {
    padding: 24px 360px;
    margin: 0 auto;
    background: #fafafb;
    font-weight: 600;
    font-size: 24px;
  }
  .xcenter {
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
    margin: 0 auto;
    .tipbg {
      background: #eff7ff;
      border-radius: 5px;
      padding: 20px;
      min-width: 506px;
    }
    .item {
      border-radius: 8px;
      border: 1px solid #dfe1e2;

      padding: 0 24px;
      display: flex;
      justify-content: center;
      align-items: center;
      height: 62px;
      margin-right: 24px;
      color: #727a89;
      font-size: 14px;
      cursor: pointer;
      img {
        margin-right: 8px;
        width: 24px;
        height: 24px;
      }
    }
    .item:hover,
    .item-active {
      border: 1.6px solid #2465f1;
      color: #000;
    }
    .btn-wrapper {
      margin-top: 240px;
      .el-button {
        width: 186px;
        height: 48px;
      }
      .el-button:first-child {
      }
      .el-button:nth-child(2) {
        background: #2465f1;
      }
    }
  }
}
</style>
