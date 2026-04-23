<template>
  <div class="charge-bank pb-20">
    <Headers :title="t('message.user.BankCarddeposit')" />
    <div class="px-30 content">
      <div class="pt-6">
        <ul class="flex flex-col">
          <!-- 国家 -->
          <li class="flex flex-col mt-6">
            <p class="mb-2">{{ $t("message.user.guoji") }}</p>
            <vue3-country-intl
              v-model="nationality"
              :disabled="disabled"
              :showAreaCode="false"
              type="country"
              :useChinese="isUseChinese()"
              :placeholder="$t('message.user.qingxuanzeguoji')"
              :noDataText="$t('message.user.nationNodata')"
            >
            </vue3-country-intl>
          </li>
          <!-- 法币 -->
          <li class="flex flex-col mt-6">
            <p>{{ $t("message.user.FrenchCurrency") }}</p>
            <el-select
              @change="handleFiatSelect"
              v-model="fiatValue"
              value-key="name"
              class="my-2"
              :placeholder="t('message.user.selectFrenchCurrency')"
              size="large"
            >
              <el-option
                v-for="item in currencyList"
                :key="item.name"
                :label="item.name"
                :value="item"
              />
            </el-select>
          </li>

          <!-- 充值数量 -->
          <li class="flex flex-col mt-6">
            <p>{{ $t("message.user.RechargeAmount") }}</p>
            <el-input
              :placeholder="$t('message.user.RechargeAmount') + '10-999999'"
              class="mt-2 usd-input"
              type="number"
              v-model="amount"
            />
          </li>
        </ul>
      </div>
      <!-- 充值说明 -->
      <div class="mt-4 centent">
        <h3 class="mt-2 text-balck">
          {{ $t("message.user.RechargeInstructions") }}
        </h3>
        <p class="mt-2 text-xs">{{ $t("message.user.desc1") }}</p>
        <p class="mt-2 text-xs">{{ $t("message.user.desc2") }}</p>
        <p class="mt-2 text-xs">{{ $t("message.user.desc3") }}</p>
        <p class="mt-2 text-xs">{{ $t("message.user.desc4") }}</p>
        <p class="mt-2 text-xs">{{ $t("message.user.desc5") }}</p>
      </div>
      <div class="mt-6">
        <el-button class="w-full btn" type="primary" @click="handleSubmit">{{
          $t("message.user.tijiao")
        }}</el-button>
      </div>
    </div>
    <RechargeDetail
      :showPopup="showPopup"
      @close="handleCloseDetail"
      :payInfo="payInfo"
      @getToken="getToken"
    />
  </div>
</template>

<script setup>
import RechargeDetail from "./confirmDialog.vue";
import Headers from "../components/headers.vue";
import { ref, onBeforeMount } from "vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
import Axios2 from "@/api/wallet.js";
import { _uploadImage } from "@/api/chat.js";
import { getStorage } from "@/utils/index";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
const router = useRouter();
const nationality = ref("");
const showPopup = ref(false); //展示充值弹窗
const disabled = ref(false);
const currencyList = ref([]); //法币列表
const rate = ref(1);
const amount = ref("");
const fiatValue = ref("");
const payInfo = ref({}); //支付信息

const isUseChinese = () => {
  return getStorage("lang") === "zh-CN";
};

onBeforeMount(() => {
  getBankList();
  // 获取法币列表
  Axios2.getRechargeCurrencyList().then((res) => {
    currencyList.value = res.data.map((item) => {
      return {
        name: item.currency,
        rate: item.rate,
      };
    });
  });
});

//获取银行卡列表
const getBankList = () => {
  Axios2.getMyPaymentMethodList().then((res) => {
    if (res.data.length === 0) {
      // 如果没有银行卡，请先绑定银行卡
      ElMessageBox.confirm(t("message.user.Bindbankcard"), {
        confirmButtonText: t("message.user.queren"),
        cancelButtonText: t("message.user.quxiao"),
        type: "warning",
      })
        .then(() => {
          router.push("/addressma");
        })
        .catch(() => {
          router.push("/recharge");
        });
    } else {
      getToken(); //不用一开始就获取token
    }
  });
};

// 获取token
const getToken = () => {
  Axios2.getBankRechargeToken().then((res) => {
    const { gf_payment_method_id, session_token } = res.data;
    payInfo.value.payment_method_id = gf_payment_method_id;
    payInfo.value.session_token = session_token;
  });
};

const handleFiatSelect = (val) => {
  fiatValue.value = val.name;
  rate.value = val.rate;
};

const handleSubmit = () => {
  if (!nationality.value) {
    ElMessage.error(t("message.user.qingxuanzeguoji"));
    return;
  }
  if (!fiatValue.value) {
    ElMessage.error(t("message.user.selectFrenchCurrency"));
    return;
  }

  if (!amount.value) {
    ElMessage.error(t("message.user.enterRechargeAmount"));
    return;
  }

  let numReg = /^[0-9]+([.]{1}[0-9]+){0,1}$/;
  if (!numReg.test(amount.value)) {
    ElMessage.error(t("message.user.amountNumber"));
    return;
  }
  payInfo.value.nationality = nationality.value;
  payInfo.value.direction = "recharge";
  payInfo.value.currency = fiatValue.value;
  payInfo.value.rate = rate.value;
  payInfo.value.coin_amount =
    Math.floor((amount.value / rate.value) * 100) / 100;
  payInfo.value.fa_amount = amount.value;
  showPopup.value = true;
};
const handleCloseDetail = () => {
  showPopup.value = false;
};
</script>
<style lang="scss" scoped>
$input_background: #27293b;
$text_color: #fff;
$text_color1: #868d9a; //文字浅色

.charge-bank {
  font-size: 15px;
  .content {
    width: 370px;
    margin: 0 auto;

    // label的样式
    p {
    }
  }
}

.select-item {
  background: $input_background;
  padding: 0 15px;
  align-items: center;
  height: 50px;
  border-radius: 3px;
  margin-top: 10px;
  color: $text_color;
}

.title {
  font-size: 26px;
  color: $text_color;
  font-weight: bold;
}

.usd-input {
  height: 50px;
  font-size: 14px;
}
h3 {
  font-size: 20px;
}

.centent {
  h2 {
    color: $text_color;
    font-size: 16px;
  }

  p {
    color: $text_color1;
    line-height: 22px;
  }
}

.tips {
  color: $text_color1;
  padding: 10px 0;
  font-size: 14px;
}

.text-xs {
  font-size: 14px;
}

.btn {
  padding: 20px;
}
</style>
