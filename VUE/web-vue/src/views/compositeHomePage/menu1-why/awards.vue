<template>
  <div>
    <pc-section class="index-section1 py-12">
      <h1>{{ t("m1-award-s0-t1") }}</h1>
      <h5 class="w-500">{{ t("m1-award-s0-d1") }}</h5>
    </pc-section>
    <menu-layout :collapseList="menu1List">
      <template v-slot:content>
        <section v-for="(item, _index) in contentList" :key="_index">
          <div>
            <h2 v-if="item.time">{{ item?.time }}</h2>
            <h2 class="mb-4 fs-32">{{ t(`m1-award-s${_index + 1}-t1`) }}</h2>
            <h5 class="mb-4">{{ t(`m1-award-s${_index + 1}-d1`) }}</h5>
          </div>
          <div class="flex flex-wrap">
            <card-why
              v-for="(item, index) in item.awardNum"
              :index="index + 1"
              :key="index"
              :desc="`${t(`m1-award-s${_index + 1}-a${index + 1}`)}`"
            ></card-why>
          </div>
        </section>
        <ready class="pb-8"></ready>
      </template>
    </menu-layout>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import cardWhy from "@comCompositeHome/cardCommon.vue";
import { menu1List } from "@/utils/path";
import ready from "@comCompositeHome/ready.vue";
const route = useRoute();
const { t } = useI18n();

const contentList = [
  {
    time: 2019,
    awardNum: 2,
  },
  {
    time: 2019,
    awardNum: 2,
  },
  {
    time: 2018,
    awardNum: 1,
  },
  {
    awardNum: 2,
  },
  {
    time: 2018,
    awardNum: 3,
  },
  {
    time: 2017,
    awardNum: 6,
  },
  {
    awardNum: 2,
  },
  {
    awardNum: 2,
  },
  {
    time: 2016,
    awardNum: 8,
  },
];

const gotoPage = () => {};
</script>
<style lang="scss" scoped>
.w-500 {
  width: 500px;
}
</style>
