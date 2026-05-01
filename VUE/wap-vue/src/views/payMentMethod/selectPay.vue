<template>
  <div class="selectPay pb-10">
    <fx-header>
      <template #title>{{ $t('allPay') }}</template>
    </fx-header>
    <van-index-bar v-for="(item, index) in keyList" :key="index" :index-list="newLetter">
      <van-index-anchor class="index-anchor" :index="item.name" />
      <div
        v-for="(items, itemIndex) in item.list"
        :key="itemIndex"
        class="item-cell ml-4 py-4"
        @click="openAdd(items)"
      >
        {{ items }}
      </div>
    </van-index-bar>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { _getBankPaymentMethodConfig } from "@/service/user.api.js";

const router = useRouter();

const ALLOWED_METHODS = ["USDT", "USDC", "BTC", "ETH"];

const keysList = ref({});
const keyList = ref([]);
const newLetter = ref([]);

const normalizeMethodName = (value) => {
  return String(value || "").trim().toUpperCase();
};

const extractAllowedToken = (name) => {
  const upperName = normalizeMethodName(name);
  return ALLOWED_METHODS.find((token) => upperName.includes(token)) || "";
};

const buildMethodList = (methodMap) => {
  const filteredEntries = Object.entries(methodMap || {})
    .map(([id, name]) => {
      const token = extractAllowedToken(name);
      return token ? [id, token] : null;
    })
    .filter(Boolean)
    .sort((a, b) => ALLOWED_METHODS.indexOf(a[1]) - ALLOWED_METHODS.indexOf(b[1]));

  keysList.value = Object.fromEntries(filteredEntries);
  keyList.value = [];
  newLetter.value = [];

  const groups = {};
  filteredEntries.forEach(([, name]) => {
    const letter = name.charAt(0);
    if (!groups[letter]) {
      groups[letter] = [];
    }
    groups[letter].push(name);
  });

  newLetter.value = Object.keys(groups);
  keyList.value = newLetter.value.map((letter) => ({
    name: letter,
    list: groups[letter],
  }));
};

const openAdd = (val) => {
  let id = '';
  for (const key in keysList.value) {
    if (Object.prototype.hasOwnProperty.call(keysList.value, key) && keysList.value[key] === val) {
      id = key;
      break;
    }
  }
  sessionStorage.setItem("editAdd", JSON.stringify({ id, name: val, type: 'add' }));
  router.push('/payMentMethod/add');
};

const getC2cPaymentMethodConfig = () => {
  _getBankPaymentMethodConfig().then((res) => {
    buildMethodList(res);
  });
};

onMounted(() => {
  getC2cPaymentMethodConfig();
});
</script>

<style lang="scss" scoped>
.selectPay {
  .index-anchor {
    background: $recommend_bg;
  }
}

:deep(.van-index-anchor) {
  font-size: 16px;
  color: $text-color;
}

:deep(.van-index-bar__index) {
  color: $active_line;
  font-size: 14px;
  margin-top: 6px;
  display: none;
}

.item-cell {
  border-bottom: 1px solid $grey_bg;
  font-size: 15px;
}
</style>
