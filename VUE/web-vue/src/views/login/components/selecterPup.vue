<!-- 选择区号组件 -->
<template>
  <div v-if="show">
    <div class="css-vp41bv">
      <div class="css-snh7a0">
        <div class="css-4pkril">
          <div class="css-1dcbbbv">
            <!-- 选择区号 -->
            <div class="modaltitle css-4cffwv">
              <div class="css-e6jk6i">
                {{ t("xuanzequhao") }}
              </div>
            </div>
            <svg @click="isClose" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" class="css-10uq0b5">
              <path
                d="M6.697 4.575L4.575 6.697 9.88 12l-5.304 5.303 2.122 2.122L12 14.12l5.303 5.304 2.122-2.122L14.12 12l5.304-5.303-2.122-2.122L12 9.88 6.697 4.575z"
                fill="currentColor"></path>
            </svg>
          </div>
          <!-- 下拉框 -->
          <div class="css-1ro0y7z">
            <vue3-country-intl v-model="countryCodes" placeholder="" :iso2="countryCodestore.iso2"
              @onChange="handleChange" :noDataText="$t('message.user.nationNodata')"></vue3-country-intl>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
// https://github.com/941477276/vue3-country-intl
import Vue3CountryIntl from "vue3-country-intl";
import { useCountryCodeStore } from "@/store/countryCode";
import { useI18n } from "vue-i18n";
const { t } = useI18n();
const countryCodestore = useCountryCodeStore();
const show = ref(false);
const countryCodes = computed({
  get() {
    return countryCodestore.code;
  },
  set(val) {
    countryCodestore.updateCountry(val);
  },
});

const handleChange = (obj) => {
  countryCodestore.updateIso2(obj.iso2);
};

const isShow = () => {
  show.value = true;
};
const isClose = () => {
  show.value = false;
};
//
watch(countryCodes, (newval) => {
  if (newval) {
    show.value = false;
  }
});

defineExpose({
  isShow,
});
</script>
<style scoped>
@import url("@/assets/css/login/selector.css");
</style>
