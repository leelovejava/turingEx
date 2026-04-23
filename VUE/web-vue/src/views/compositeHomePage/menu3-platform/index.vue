<!-- menu3入口 -->
<template>
  <div>
    <pc-section class="section1" innerclass="flex">
      <div class="flex1 pt-12 px-24">
        <h2>{{ t("m3-i-s0-t1") }}</h2>
        <h5 class="desc">{{ t("m3-i-s0-d1") }}</h5>
      </div>
      <img
        src="@/assets/images/compositeHome/menu3/home/h0.png"
        width="665"
        height="232"
      />
    </pc-section>
    <!-- 导航 -->
    <pc-section>
      <navigator></navigator>
    </pc-section>
    <!-- 我们的平台 -->
    <div class="py-20 pb-16 text-center">
      <h2>{{ t("m3-i-s1-t1") }}</h2>
      <h5>{{ t("m3-i-s1-d1") }}</h5>
    </div>
    <!--  -->
    <block-card :num="1" :list="list6" :needBtn="false"></block-card>
    <block-card :num="2" :list="list4"></block-card>
    <block-card :num="3" :list="list3" :bg="'white'"></block-card>
    <block-card :num="4" :list="list3"></block-card>

    <!-- 比较我们的平台 -->
    <pc-section innerclass="flex justify-center flex-col items-center py-16">
      <h2>{{ t("m3-i-s3-t1") }}</h2>
      <el-table :data="tableData" class="self-table w-1000">
        <el-table-column
          v-for="(item, index) in tableList"
          :key="index"
          :prop="item.prop"
          :label="item.label"
        />
      </el-table>
    </pc-section>
    <!-- 我们的交易工具 -->
    <pc-section class="section1 text-center" innerclass="py-12">
      <h2 class="mb-12">{{ t("m3-i-s4-t1") }}</h2>
      <div class="flex justify-center">
        <div class="blue-border mr-16 w-370 text-left">
          <img
            src="@/assets/images/compositeHome/menu3/home/icon1.png"
            width="40"
            height="40"
          />
          <h2 class="fs-24 mt-4">{{ t("m3-i-s4-1-t1") }}</h2>
          <h5>{{ t("m3-i-s4-1-d1") }}</h5>
        </div>
        <div class="blue-border w-370 text-left">
          <img
            src="@/assets/images/compositeHome/menu3/home/icon2.png"
            width="40"
            height="40"
          />
          <h2 class="fs-24 mt-4">{{ t("m3-i-s4-2-t1") }}</h2>
          <h5>{{ t("m3-i-s4-2-d1") }}</h5>
        </div>
      </div>
    </pc-section>

    <!-- 我们的社交交易伙伴 -->
    <pc-section class="text-center" innerclass="py-12">
      <h2 class="mb-12">{{ t("m3-i-s5-t1") }}</h2>
      <div class="grid grid-cols-3 gap-x-16">
        <div class="blue-border" v-for="(item, i) in list3" :key="i">
          <h2 class="fs-24">{{ t(`m3-i-s5-${i + 1}-t1`) }}</h2>
          <h5>{{ t(`m3-i-s5-${i + 1}-d1`) }}</h5>
        </div>
      </div>
    </pc-section>
    <ready-bg></ready-bg>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import pcSection from "@comCompositeHome/pcSection.vue";
import blockCard from "@comCompositeHome/menu3/blockCard.vue";
import readyBg from "@comCompositeHome/readyWithBg.vue";
import navigator from "@comCompositeHome/navigator.vue";
const list3 = Array(3);
const list4 = Array(4);
const list6 = Array(6);
const list15 = Array.from({ length: 15 });
const route = useRoute();
const { t } = useI18n();

const tableList = [
  {
    prop: "v1",
  },
  {
    label: t("m3-i-s2-4-t1"),
    prop: "v2",
  },
  {
    label: t("m3-i-s2-2-t1"),
    prop: "v3",
  },
  {
    label: t("m3-i-s2-3-t1"),
    prop: "v4",
  },
];

const tableData = list15.map((_, i) => {
  let v1 = t(`m3-i-s3-r${i + 1}`);
  if (i === 0) {
    return {
      v1,
      v2: t("Windows"),
      v3: t("Windows"),
      v4: t("Windows"),
    };
  }
  const yes = t("m3-i-s3-y");
  const no = t("m3-i-s3-n");
  //是否否
  if ([3, 8, 10, 12, 14].includes(i)) {
    return {
      v1,
      v2: yes,
      v3: no,
      v4: no,
    };
  }
  // 是是否
  if ([1].includes(i)) {
    return {
      v1,
      v2: yes,
      v3: no,
      v4: no,
    };
  }
  // 否是是
  if ([11].includes(i)) {
    return {
      v1,
      v2: no,
      v3: yes,
      v4: yes,
    };
  }
  // 是否是
  if ([9].includes(i)) {
    return {
      v1,
      v2: yes,
      v3: no,
      v4: yes,
    };
  }
  //默认是是是
  return {
    v1,
    v2: yes,
    v3: yes,
    v4: yes,
  };
});
</script>
<style lang="scss" scoped>
.section1 {
  background: #f2f4f6;
  .desc {
    width: 532px;
  }
}

.w-1000 {
  width: 1000px;
}
.w-370 {
  width: 370px;
}
</style>
