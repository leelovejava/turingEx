<template>
  <div class="lang">
    <fx-header>
      <template #title>
        {{ $t('setLanguage') }}
      </template>
    </fx-header>
    <div v-for="(item, index) in lang" :key="index" class="lang-padding" @click="handleSetLang(item.key)">
      <div class="lang-title flex items-center text-28 textColor">
        <img class="h-8 mr-5" :src="item.image" alt="">
        {{ item.title }}
      </div>
      <div class="lang-flex"></div>
      <img v-if="item.key == locale" src="../../assets/image/public/checked.png" style="width: 20px;height: 20px;" />
    </div>
  </div>
</template>
<script setup>
import { onMounted, ref } from "vue";
import { useI18n } from "vue-i18n";
import { SET_LANGUAGE } from '@/store/types.store'
import { useLanguageStore } from '@/store/language.store'

const { locale, t } = useI18n()

const languageStore = useLanguageStore()

let lang = ref([
  { title: 'Deutsch', key: 'de', image: new URL('../../assets/image/lang/de.png', import.meta.url) }, //德语
  { title: 'English', key: 'en', image: new URL('../../assets/image/lang/en-US.png', import.meta.url) },
  { title: 'español', key: 'es', image: new URL('../../assets/image/lang/Spanish.png', import.meta.url) }, //西班牙
  { title: 'Français', key: 'fr', image: new URL('../../assets/image/lang/French.png', import.meta.url) }, //法语
  { title: 'Italiano', key: 'Italy', image: new URL('../../assets/image/lang/Italy.png', import.meta.url) }, //意大利语
  { title: '日本語', key: 'Japanese', image: new URL('../../assets/image/lang/Japanese.png', import.meta.url) },
  { title: '한국인', key: 'Korean', image: new URL('../../assets/image/lang/Korean.png', import.meta.url) },
  { title: 'Portugal', key: 'pt', image: new URL('../../assets/image/lang/pt.png', import.meta.url) }, //葡萄牙
  { title: 'Tiếng Việt', key: 'vi', image: new URL('../../assets/image/lang/vi.png', import.meta.url) },//越南语
  { title: '繁体中文', key: 'CN', image: new URL('../../assets/image/lang/taiwan.png', import.meta.url) },
  { title: '简体中文', key: 'zh-CN', image: new URL('../../assets/image/lang/zh-CN.png', import.meta.url) },
  { title: 'Ελληνικά', key: 'gr', image: new URL('../../assets/image/lang/Greek.png', import.meta.url) }, //希腊语
  { title: 'ไทย', key: 'th', image: new URL('../../assets/image/lang/Thai.png', import.meta.url) }, //泰语
  // { title: 'Română', key: 'ro', image: new URL('../../assets/image/lang/Romanian.jpg', import.meta.url) }, //罗马尼亚
  // { title: 'Türkçe', key: 'tur', image: new URL('../../assets/image/lang/Turkish.png', import.meta.url) }, //土耳其
]
)

onMounted(() => {
  console.log(locale.value)
})
const handleSetLang = (lang) => {
  locale.value = lang
  languageStore[SET_LANGUAGE](lang)
}

</script>
<style lang="scss" scoped>
.lang {
  width: 100%;
  box-sizing: border-box;
}

.CommonProblem-padding {
  padding-left: 25px;
  padding-right: 25px;
}

.lang-padding {
  padding: 15px 18px 22px 18px;
  box-sizing: border-box;
  border-bottom: 1px solid $border_color;
  font-weight: 400;
  font-size: 18px;
  color: $text_color;
  display: flex;
}

.lang-flex {
  flex: 1;
}
</style>
