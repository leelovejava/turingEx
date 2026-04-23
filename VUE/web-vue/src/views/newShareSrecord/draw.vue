<template>
  <!-- 抽签 -->
  <div class="draw">
    <div class="draw-box">
      <header class="draw-box-header">
        <p class="fs-24" style="margin-bottom: 1px">{{ dataList.name }}</p>
        <p class="fs-16" style="margin-bottom: 8px; color: #747a8f">
          ({{ dataList.productName }})
        </p>
        <!-- <p class="fs-24" style="color: #F33368;">{{dataList.marketPrice}}</p> -->
      </header>
      <main class="draw-box-main">
        <div
          class="draw-box-main-text"
          v-for="(value, key, i) in valueList"
          :key="i"
        >
          <p style="color: #b7bdd1">{{ t(`message.user.${value}`) }}</p>
          <p>{{ dataList[key] }}</p>
        </div>
      </main>
      <footer class="draw-box-footer">
        <div class="draw-box-footer-number">
          <div class="flex-space-between" style="margin-bottom: 12px">
            <p class="fs-16 draw-title-color">
              {{ t(`message.user.shuliangzhang`) }}
            </p>
            <p class="fs-14 draw-text-color">
              {{ t(`message.user.yizhangdengyuyiqian`) }}
            </p>
          </div>
          <el-input
            class="draw-box-footer-number-input"
            v-model="amountValue"
            :placeholder="t(`message.user.qingshuruchouqian`)"
            @input="(value) => (amountValue = value.replace(/^(0+)|[^\d]+/g, ''))"
          />
        </div>
        <div class="draw-box-footer-number">
          <div class="flex-space-between" style="margin-bottom: 12px">
            <p class="fs-16 draw-title-color">
              {{ t(`message.user.keyongedu`) }}
            </p>
            <p class="fs-14 draw-text-color">{{ t(`message.user.yuan10`) }}</p>
          </div>
          <el-input
            class="draw-box-footer-number-input"
            v-model="dataList.defaultLimit"
            readonly
          />
        </div>
        <div class="draw-box-footer-button">
          <button class="draw-button" v-click="drawButton">
            {{ t(`message.user.yijianchouqian`) }}
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
  startSubscribeDate: "chouqiankaishiri",
  endSubscribeDate: "chouqianjiezhiri",
  drawDate: "chouqianri",
  issuanceDate: "faquanri",
};

const amountValue = ref("");

const drawButton = () => {
  if (!amountValue.value) {
    ElMessage({
      message: t(`message.user.qingshuruchouqian`),
      type: "warning",
    });
    return;
  }
  const params = {
    code: dataList.value.productCode,
    amount: amountValue.value,
  };
  Axios.setDrawMessage(params).then((res) => {
    if (res.code === 0) {
      ElMessage({
        message: t(`message.user.choyqianchenggong`),
        type: "success",
      });
      router.replace({
        path: "/newShareSrecord/drawRecord",
        query: {
          type: 1,
        },
      });
    }
  });
};
</script>

<style lang="scss" scoped>
.draw {
  display: flex;
  justify-content: center;
  .draw-box {
    margin-top: 30px;
    width: 614px;
    min-height: 700px;
    background: #27293b;
    color: #fff;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0px 0px 12.85863971710205px 0px #00000033;
    .draw-box-header {
      background: #192137;
      height: 137px;
      padding: 18px;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
    }
  }
  .draw-box-main {
    padding: 24px 37px 30px;
    .draw-box-main-text {
      display: flex;
      justify-content: space-between;
      margin-bottom: 15px;
      &:nth-last-of-type(1) {
        margin-bottom: 0;
      }
    }
  }
  .draw-box-footer {
    padding: 0px 37px 30px;
    :deep(.draw-box-footer-number) {
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
    }
    .draw-box-footer-button {
      .draw-button {
        width: 100%;
        height: 60px;
        background: linear-gradient(90deg, #2c64d4 0%, #38aeea 100%);
        font-size: 18px;
      }
    }
  }
}
</style>
