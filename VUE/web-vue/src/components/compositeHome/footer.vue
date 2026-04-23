<template>
  <pc-section class="py-8" :class="theme" innerclass="px-0">
    <!-- 菜单 -->
    <div class="flex footer-menu">
      <div class="w-300">
        <img
          src="@/assets/images/compositeHome/logo.png"
          width="72"
          height="72"
        />
      </div>
      <div class="w-1000 grid grid-cols-3 gap-x-4">
        <div v-for="(_, _index) in footerList" :key="_index">
          <div
            class="menu h-12 font-semibold text-sm"
            :class="activeLabel === _.lable ? 'active' : ''"
            @click="gotoPage(_.path)"
          >
            {{ t(`footer-s1-${_index + 1}-t1`) }}
          </div>
          <!-- 二级菜单 -->
          <div v-for="(item, i) in _.list" :key="i">
            <div
              class="h-12 text-sm menu"
              :class="activeLabel === item.title ? 'active' : ''"
              @click="gotoPage(item.path)"
            >
              {{ t(item.title) }}
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 隐私政策 -->
    <div class="footer-privacy pt-8">
      <div class="flex mb-8">
        <p
          v-for="(_, i) in list5"
          :key="i"
          class="text-sm mr-12 menu"
          @click="gotoPage(_.url)"
        >
          {{ t(`footer-s2-${i + 1}-t1`) }}
        </p>
      </div>
      <div class="text text-sm" v-html="t('privacyText')"></div>
    </div>
  </pc-section>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import { getCurrentInstance } from "vue";
const activeLabel = ref();
const { t } = useI18n();
const router = useRouter();
const props = defineProps({
  theme: {
    type: String,
    default: "light",
  },
});

const { proxy } = getCurrentInstance();
const firstList = [
  {
    // title: t("footer-s1-1-d1", { TITLE: proxy.$title }), //切换有问题
    title: "footer-s1-1-d1",
    path: "/why-demo/",
  },
  // {
  //   title: t("footer-s1-1-d2"),
  //   path: "/market-analysis",
  // },
  {
    title: "footer-s1-1-d3",
    path: "/trading/",
  },
  {
    title: "footer-s1-1-d4",
    path: "/trading-platforms/",
  },
  {
    title: "footer-s1-1-d5",
    path: "/trading-platforms/tools",
  },

  // {
  //   title: "footer-s1-1-d6",
  //   path: "/market-analysis/",
  // },
  {
    title: "footer-s1-1-d7",
    path: "/support",
  },
];

const secondList = [
  {
    title: "footer-s1-2-d1",
    path: "/market-analysis/market-news/",
  },
  // {
  //   title: t("市场工具"),
  //   path: "/trading-platforms/tools/",
  // },
  {
    title: "footer-s1-2-d2",
    path: "/market-analysis/analysts/",
  },
];
const thirdList = [
  {
    title: "footer-s1-3-d1",
    path: "/education/learn-forex/",
  },
  {
    title: "footer-s1-3-d2",
    path: "/education/learn-trade-cfds/",
  },
  {
    title: "footer-s1-3-d4",
    path: "/education/trading-guides/",
  },
];
const fourthList = [
  {
    title: proxy.$email,
    path: "mail",
  },
  // {
  //   title: "+1786 628 1209",
  //   path: "tel",
  // },
];
const footerList = [
  {
    list: firstList,
    path: "/",
  },
  {
    list: secondList,
    path: "/market-analysis",
  },
  {
    list: thirdList,
    path: "/education",
  },
  {
    list: fourthList,
    path: "/contact-us",
  },
];

const list5 = [
  {
    url: "/legal-documentation",
  },
  {
    url: "pdf.js/SCB_Privacy_Policy_ROW.pdf",
  },
  {
    url: "pdf.js/SCB_Client_TCs_ROW.pdf",
  },
  {
    url: "pdf.js/SCB_Cookie_Policy_ROW.pdf",
  },
  {
    url: "pdf.js/SCB_Complaints_Notice_ROW.pdf",
  },
];

const gotoPage = (path) => {
  if (path === "mail") {
    window.location.href = `mailto:${import.meta.env.VITE_APP__EMAIL}`;
    return;
  }
  // if (path === "tel") {
  //   window.location.href = "tel:+1786 628 1209";
  //   return;
  // }
  if (!path.startsWith("/")) {
    window.open(path);
    return;
  }
  router.push(path);
};
</script>

<style lang="scss" scoped>
.light {
  background: #fff;
  color: #000;
}

.black {
  background: #000000;
  color: #fff;
}

.w-300 {
  width: 300px;
}

.w-1000 {
  width: 1000px;
}

.footer-menu {
  border-bottom: 1px solid #dbdbdb;
}

.menu {
  cursor: pointer;
}

.menu:hover {
  color: #233ad4;
}

.footer-privacy {
  .text {
    color: #5c5c5c;
  }
}
</style>
