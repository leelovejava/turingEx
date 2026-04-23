<template>
  <div>
    <p class="mt-24 font-semibold" v-if="needLabel">
      {{ $t("message.user.zhuangtai") }}
    </p>
    <div class="total">
      <div class="t-box">
        <p class="label" v-if="needLabel">
          {{ $t(`message.user.${isWithdraw ? "tikuan" : "chongzhi"}`)
          }}{{ $t("message.user.zhuangtai") }}
        </p>
        <!-- crypto   接口status<number> -->
        <div v-if="type === 'crypto'">
          <div v-if="state === 0" class="flex">
            <img src="@/assets/images/wallet/pending.svg" class="mr-2" />
            {{ $t("message.user.chulizhong") }}
          </div>
          <div v-if="state === 1" class="flex">
            <img src="@/assets/images/wallet/success.svg" class="mr-2" />
            {{ $t("message.user.chongzhi") }}{{ $t("message.user.chenggong") }}
          </div>
          <div v-if="state === 2" class="flex">
            <img src="@/assets/images/wallet/failed.svg" class="mr-2" />
            {{ $t("message.user.chongzhi") }}{{ $t("message.user.shibai") }}
          </div>
        </div>
         <!-- bank   接口state<string> -->
        <div v-else>
          <div v-if="state == 0" class="flex">
            <img src="@/assets/images/wallet/pending.svg" class="mr-2" />
            {{ $t("message.c2c.weifukuan") }}
          </div>
          <div v-if="state == 1" class="flex">
            <img src="@/assets/images/wallet/pending.svg" class="mr-2" />
            {{ $t("message.c2c.weifukuan") }}
          </div>
          <div v-if="state == 2" class="flex">
            <img src="@/assets/images/wallet/pending.svg" class="mr-2" />
            {{ $t("message.c2c.shensuzhong") }}
          </div>
          <div v-if="state == 3" class="flex">
            <img src="@/assets/images/wallet/success.svg" class="mr-2" />
            {{ $t("message.user.chongzhi") }}{{ $t("message.user.chenggong") }}
          </div>
          <div v-if="state == 4" class="flex">
            <img src="@/assets/images/wallet/failed.svg" class="mr-2" />
            {{ $t("message.c2c.yiquxiao") }}
          </div>
          <div v-if="state == 5" class="flex">
            <img src="@/assets/images/wallet/failed.svg" class="mr-2" />
            {{ $t("message.c2c.yichaoshi") }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
const props = defineProps({
  state: {
    type: [String,Number],
  },
  type: {
    type: String,
    default: "crypto",
  },
  needLabel: {
    type: Boolean,
    default: false,
  },
  isWithdraw: {
    type: Boolean,
    default: false,
  },
});
</script>
