<template>
  <div>
    <pc-section class="index-section1 py-12">
      <h1>{{ t("m1-vip-s0-t1") }}</h1>
      <h5 class="w-500">{{ t("m1-vip-s0-d1") }}</h5>
    </pc-section>
    <menu-layout :collapseList="menu1List">
      <template v-slot:content>
        <section>
          <!-- 高级客户福利 -->
          <h2>{{ t("m1-vip-s1-t1") }}</h2>
          <h5 class="mb-4">{{ t("m1-vip-s1-d1") }}</h5>
          <div class="flex flex-wrap">
            <card
              v-for="(item, index) in list4"
              :key="index"
              :desc="t(`m1-vip-s1-${index + 1}`)"
              :lineWidth="'360px'"
            ></card>
          </div>
        </section>
        <section>
          <!-- 量身定制的服务和支持 -->
          <h2>{{ t("m1-vip-s2-t1") }}</h2>
          <h5 class="mb-4">{{ t("m1-vip-s2-d1") }}</h5>
          <!-- 如何获得高级客户资格 -->
          <h2>{{ t("m1-vip-s3-t1") }}</h2>
          <h5 class="mb-4">{{ t("m1-vip-s3-d1") }}</h5>
          <el-table :data="tableData" class="self-table" :show-header="false">
            <el-table-column
              v-for="(item, index) in tableList"
              :key="index"
              :prop="item.prop"
            />
          </el-table>
          <div></div>
        </section>
        <section>
          <!-- 独家福利 -->
          <h2 class="my-4">{{ t("m1-vip-s4-t1") }}</h2>
          <div class="grid grid-cols-2 gap-8">
            <div class="card p-8" v-for="(_, i) in list3" :key="i">
              <img
                :src="
                  getImageUrl(
                    `/src/assets/images/compositeHome/menu1/vip/icon${
                      i + 1
                    }.png`
                  )
                "
                width="40"
                height="40"
              />
              <h5 class="my-4 text-left fs-24 font-normal">
                {{ t(`m1-vip-s4-${i + 1}-t1`) }}
              </h5>
              <h5 class="text-base text-left">
                {{ t(`m1-vip-s4-${i + 1}-d1`) }}
              </h5>
            </div>
          </div>
          <!-- 立即咨询 -->
          <h2 class="my-4">{{ t("m1-vip-s5-t1") }}</h2>
          <h5 class="mb-4">{{ t("m1-vip-s5-d1") }}</h5>
          <h5 class="mb-4">{{ t("m1-vip-s5-d2") }}</h5>
        </section>
      </template>
    </menu-layout>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
// import cardWhy from "@comCompositeHome/cardCommon.vue";
import { getImageUrl } from "@/utils/index";
import card from "@comCompositeHome/menu2/card.vue";
import { menu1List } from "@/utils/path";
const route = useRoute();
const { t } = useI18n();
const list4 = Array.from({ length: 4 });
const list3 = Array(3);
const tableList = [
  {
    prop: "v1",
  },
  {
    prop: "v2",
  },
];
const tableData = list4.map((_, i) => ({
  v1: t(`m1-vip-s3-${i + 1}-t1`),
  v2: t(`m1-vip-s3-${i + 1}-t2`),
}));

const gotoPage = () => {};
</script>
<style lang="scss" scoped>
.w-500 {
  width: 500px;
}

.self-table :deep {
  td.el-table__cell div {
    text-align: center;
  }
}

.card {
  background: #ffffff;
  box-shadow: 0px 3px 10px 9px rgba(0, 0, 0, 0.05);
  border-top: 2px solid #2038d3;
}
</style>
