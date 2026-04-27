<template>
  <div class="lang">
    <fx-header>
      <template #title>
        {{ $t('setLanguage') }}
      </template>
    </fx-header>
    <div
      v-for="item in langList"
      :key="item.key"
      class="lang-padding"
      @click="handleSetLang(item.key)"
    >
      <div class="lang-title flex items-center text-28 textColor">
        <img class="lang-icon mr-5" :src="item.image" :alt="item.title">
        {{ item.title }}
      </div>
      <div class="lang-flex"></div>
      <img
        v-if="item.key === currentLocale"
        class="lang-checked"
        src="../../assets/image/public/checked.png"
        alt="checked"
      >
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from "vue";
import { useI18n } from "vue-i18n";
import { SET_LANGUAGE } from "@/store/types.store";
import { useLanguageStore } from "@/store/language.store";
import { getStorage } from "@/utils";

const { locale } = useI18n();
const languageStore = useLanguageStore();

const langList = ref([
  { title: "Deutsch", key: "de", image: new URL("../../assets/image/lang/de.png", import.meta.url).href },
  { title: "English", key: "en", image: new URL("../../assets/image/lang/en-US.png", import.meta.url).href },
  { title: "Espanol", key: "es", image: new URL("../../assets/image/lang/Spanish.png", import.meta.url).href },
  { title: "Francais", key: "fr", image: new URL("../../assets/image/lang/French.png", import.meta.url).href },
  { title: "Italiano", key: "Italy", image: new URL("../../assets/image/lang/Italy.png", import.meta.url).href },
  { title: "Japanese", key: "Japanese", image: new URL("../../assets/image/lang/Japanese.png", import.meta.url).href },
  { title: "Korean", key: "Korean", image: new URL("../../assets/image/lang/Korean.png", import.meta.url).href },
  { title: "Portuguese", key: "pt", image: new URL("../../assets/image/lang/pt.png", import.meta.url).href },
  { title: "Tieng Viet", key: "vi", image: new URL("../../assets/image/lang/vi.png", import.meta.url).href },
  { title: "Traditional Chinese", key: "CN", image: new URL("../../assets/image/lang/taiwan.png", import.meta.url).href },
  { title: "Simplified Chinese", key: "zh-CN", image: new URL("../../assets/image/lang/zh-CN.png", import.meta.url).href },
  { title: "Greek", key: "gr", image: new URL("../../assets/image/lang/Greek.png", import.meta.url).href },
  { title: "Thai", key: "th", image: new URL("../../assets/image/lang/Thai.png", import.meta.url).href },
]);

const currentLocale = computed(() => {
  return locale.value || languageStore.language || getStorage("lang") || "en";
});

const handleSetLang = (lang) => {
  if (!lang || lang === currentLocale.value) {
    return;
  }
  locale.value = lang;
  languageStore[SET_LANGUAGE](lang);
  document.documentElement.lang = lang;
  window.location.reload();
};
</script>

<style lang="scss" scoped>
.lang {
  width: 100%;
  box-sizing: border-box;
}

.lang-padding {
  padding: 15px 18px 22px 18px;
  box-sizing: border-box;
  border-bottom: 1px solid $border_color;
  font-weight: 400;
  font-size: 18px;
  color: $text_color;
  display: flex;
  align-items: center;
}

.lang-flex {
  flex: 1;
}

.lang-icon {
  width: 32px;
  height: 32px;
}

.lang-checked {
  width: 20px;
  height: 20px;
}
</style>
