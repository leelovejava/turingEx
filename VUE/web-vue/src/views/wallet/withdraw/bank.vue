<template>
  <div class="withdraw-bank pb-20">
    <Headers :title="t('message.user.yinhangkatikuan')" />

    <div class="pt-6 content">
      <!-- 法币 -->
      <li class="flex flex-col mt-6">
        <p class="mb-2">{{ $t("message.user.FrenchCurrency") }}</p>
        <el-select
          @change="handleFiatSelect"
          v-model="fiatValue"
          :placeholder="t('message.user.selectFrenchCurrency')"
          size="large"
        >
          <el-option
            v-for="item in currencyList"
            :key="item.name"
            :label="item.name"
            :value="item.name"
          />
        </el-select>
      </li>
      <!-- 提款方式 -->
      <li class="flex flex-col mt-6">
        <p class="mb-2">{{ $t("message.user.tikuanfangshi") }}</p>
        <el-select
          @change="handleBankSelect"
          v-model="payType"
          value-key="name"
          :placeholder="t('message.user.qingxuanzetikuanfangshi')"
          size="large"
        >
          <el-option
            v-for="item in withdrawBankList"
            :key="item.name"
            :label="item.name"
            :value="item"
          />
        </el-select>
      </li>
      <!-- 提款数量 -->
      <li class="flex flex-col mt-6">
        <p class="mb-2">{{ $t("message.user.tixianshuliang") }}</p>
        <el-input
          :placeholder="$t('message.user.tixianshuliang') + '10-999999'"
          class="usd-input"
          type="number"
          v-model="amount"
          @input="changeInput"
        />
        <!-- 可用 -->
        <div class="flex mt-2 justify-between">
          <div class="text-slate-400">
            {{ $t("message.user.keyong") }}
          </div>
          <div class="money text-base">
            <span class="mr-1 font-semibold">{{ balance }}</span>
            <span>USD</span>
          </div>
        </div>
      </li>
      <!-- 可用到账余额 -->
      <div class="quantity-wrap">
        <div class="flex justify-between items-center">
          <div class="text-slate-400">
            {{ $t("message.user.keyongdaozhangshuliang") }}
          </div>
          <div class="money text-base">
            <span class="mr-1 font-semibold">{{ balance }}</span>
            <span>USD</span>
          </div>
        </div>
        <!-- 提现手续费 -->
        <div class="flex justify-between">
          <div class="text-slate-400">
            {{ $t("message.user.tixianfeiyong") }}
          </div>
          <div class="money text-base">
            <span class="mr-1 font-semibold">{{ fee || "0.00" }}</span>
            <span>USD</span>
          </div>
        </div>
      </div>
      <!-- 提现说明 -->
      <div class="mt-4 centent">
        <h2>{{ $t("message.user.tikuanshuoming") }}</h2>
        <p class="mt-2 text-slate-400 text-xs">
          {{ $t("message.user.bankTips1") }}
        </p>
        <p class="mt-2 text-slate-400 text-xs">
          {{ $t("message.user.bankTips2") }}
        </p>
        <p class="mt-2 text-slate-400 text-xs">
          {{ $t("message.user.bankTips3") }}
        </p>
        <p class="mt-2 text-slate-400 text-xs">
          {{ $t("message.user.bankTips4") }}
        </p>
      </div>
      <div class="mt-6">
        <el-button class="w-full" type="primary" @click="handleSubmit">{{
          $t("message.user.tijiao")
        }}</el-button>
      </div>
    </div>

    <ConfirmDialog
      :isShow="showDetail"
      :payInfo="payInfo"
      @close="handleCloseDetail"
    />
    <FundPwdVerify
      :isShow="showFundVerify"
      :payInfo="payInfo"
      @close="handleCloseVerify"
    />
    <SuccessPage :isShow="showSuccess" @close="handleCloseSuccess" />
  </div>
</template>

<script setup>
import Headers from "../components/headers.vue";
import ConfirmDialog from "./confirmDialog.vue";
import FundPwdVerify from "./fundPwdDialog.vue";
import SuccessPage from "./successDialog.vue";
import { ElMessage } from "element-plus";
import { ref, onBeforeMount } from "vue";
import { useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import Axios2 from "@/api/wallet.js";

const { t } = useI18n();
const router = useRouter();

onBeforeMount(() => {
  getBankList();
  // 获取法币列表
  Axios2.getRechargeCurrencyList().then((res) => {
    currencyList.value = res.data.map((item) => {
      return {
        name: item.currency,
      };
    });
  });

  Axios2.getBalanceUsdt().then((res) => {
    balance.value = res.data.money;
  });
});
const currencyList = ref([]); //法币
const withdrawBankList = ref([]); //提款方式
const payInfo = ref({});
const balance = ref(0);
const fiatValue = ref("");
const payType = ref("");
const amount = ref("");
const fee = ref(0);

const showDetail = ref(false); //控制订单详情
const showFundVerify = ref(false);
const showSuccess = ref(false);

//获取银行卡列表
const getBankList = () => {
  Axios2.getMyPaymentMethodList().then((res) => {
    if (res.data.length === 0) {
      // 如果没有银行卡，请先绑定银行卡
      ElMessageBox.confirm(t("message.user.Bindbankcard"), {
        confirmButtonText: t("message.user.queren"),
        cancelButtonText: t("message.user.quxiao"),
        type: "warning",
      }).then(() => {
        router.push("/addressma");
      });
    } else {
      res.data.map((item) => {
        let obj = {
          name: item.methodName + " " + item.paramValue1,
          value: item.uuid,
        };
        withdrawBankList.value.push(obj);
      });
      getToken(); //不用一开始就获取token
    }
  });
};

// 获取token
const getToken = () => {
  Axios2.getBankRechargeToken().then((res) => {
    payInfo.value.session_token = res.data.session_token;
  });
};

// 根据提款数量得到手续费
const changeInput = () => {
  if (amount.value === "") {
    fee.value = "";
    return;
  }
  Axios2.getWithdrawFee({
    amount: amount.value,
    channel: "USDT_",
  }).then((res) => {
    fee.value = res.data.fee;
    payInfo.value.fee = fee.value;
    payInfo.value.volume_last = res.data.volume_last;
  });
};

const handleFiatSelect = (val) => {
  fiatValue.value = val;
};

const handleBankSelect = (value, index) => {
  payInfo.value.payment_method_id = value.value;
  payType.value = value.name;
};

const handleSubmit = () => {
  if (!fiatValue.value) {
    ElMessage.error(t("message.user.selectFrenchCurrency"));
    return;
  }
  if (!amount.value) {
    ElMessage.error(t("message.user.tixianshuliang"));
    return;
  }
  if (!payType.value) {
    ElMessage.error(t("message.user.qingxuanzetikuanfangshi"));
    return;
  }
  let numReg = /^[0-9]+([.]{1}[0-9]+){0,1}$/;
  if (!numReg.test(amount.value)) {
    ElMessage.error(t("enterWithdrawalAmountNumber"));
    return;
  }
  payInfo.value.direction = "withdraw";
  payInfo.value.currency = fiatValue.value;
  payInfo.value.coin_amount = amount.value;
  payInfo.value.fa_amount = amount.value;

  showDetail.value = true; //展示detail
};

const handleCloseDetail = (isConfirm) => {
  showDetail.value = false;
  if (isConfirm) {
    showFundVerify.value = true;
  }
};

const handleCloseVerify = (isConfirm) => {
  showFundVerify.value = false;
  if (isConfirm) {
    showSuccess.value = true; //资金密码输入成功
  }
};

const handleCloseSuccess = () => {
  showSuccess.value = false;
};
</script>
<style lang="scss" scoped>
$input_background: #27293b;
$text_color: #fff;
$text_color1: #868d9a; //文字浅色
.withdraw-bank {
  font-size: 14px;

  .content {
    width: 370px;
    margin: 0 auto;
  }
}
.select-item {
  padding: 0 15px;
  align-items: center;
  height: 50px;
  border-radius: 3px;
}

.title {
  font-size: 16px;
  font-weight: bold;
}

.usd-input {
  height: 50px;
  font-size: 16px;
}

.centent {
  h2 {
    font-size: 16px;
  }

  p {
    line-height: 22px;
    font-size: 14px;
  }
}
</style>
