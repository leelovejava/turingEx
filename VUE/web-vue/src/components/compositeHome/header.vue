<template>
  <section class="header-index">
    <div class="header-index-w">
      <!-- 个人 -->
      <div class="flex justify-between text-sm">
        <div class="flex">
          <p
            class="menu"
            v-for="(_, i) in left3"
            :key="i"
            @click="goPage(_.url)"
          >
            {{ t(_.title) }}
          </p>
        </div>
        <div class="flex">
          <p class="menu" @click="goPage('/support')">
            {{ t("header-s1-4") }}
          </p>
          <p class="menu" @click="goPage('/refer-friend')">
            {{ t("header-s1-5") }}
          </p>
          <!-- 多语言 -->
          <el-select
            v-model="langValue"
            class="self-select"
            :class="needTransparent ? 'transparent-bg' : 'black-bg'"
            @change="handleLangChange"
          >
            <el-option
              v-for="item in langOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <!-- <p class="cursor-pointer">{{ "header-s1-6") }}</p> -->
        </div>
      </div>
      <!-- 第二行  -->
      <div class="flex items-center mt-4">
        <div
          class="flex flex-1 items-center"
          style="cursor: pointer"
          @click="goPage('/home')"
        >
          <img
            class="mr-4"
            src="@/assets/images/compositeHome/logo.png"
            width="46"
            height="25"
          />
          <div class="text-2xl">{{ $title }}</div>
        </div>

        <div class="flex flex-2">
          <el-dropdown
            v-for="(_item, _index) in headerList"
            :key="_index"
            size="large"
          >
            <div class="text-base mr-8 text-white" @click="goPage(_item.path)">
              {{ t(_item.label) }}
            </div>
            <template #dropdown>
              <el-dropdown-menu class="self-header-drop">
                <el-dropdown-item
                  v-for="(item, index) in _item.option"
                  :key="index"
                  @click="goPage(item.path)"
                >
                  {{ t(item?.label) }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
        <div class="flex flex-1 justify-end items-center">
          <el-button
            v-if="userInfo.token"
            class="blue-small-btn"
            style="margin-right: 16px"
            @click="gotoPage"
          >
            {{ t("header-s2-b1") }}
          </el-button>
          <h6
            v-else
            class="text-white font-normal mr-6 cursor-pointer"
            @click="goPage('/login')"
          >
            {{ t("header-s2-b2") }}
          </h6>

          <el-button
            v-if="userInfo.token"
            class="blue-small-btn"
            @click="handleLogout"
          >
            {{ t("tuichu") }}
          </el-button>
          <el-button v-else class="blue-small-btn" @click="goPage('/login')">
            {{ t("ready-b1") }}
          </el-button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { useRouter } from "vue-router";
import Axios from "@/api/login.js";
import { storeToRefs } from "pinia";
import { useI18n } from "vue-i18n";
import { useUserStore } from "@/store/user";
import { useLanguageStore } from "@/store/lang";
import { removeStorage, langOptions } from "@/utils";
import { gotoPage } from "@/utils/login";

const { t, locale } = useI18n();
const { userInfo } = storeToRefs(useUserStore());
const langStore = useLanguageStore();

const props = defineProps({
  needTransparent: {
    type: Boolean,
    default: false,
  },
});

const router = useRouter();
const langValue = ref(langStore.language);

const left3 = [
  // {
  //   url: "/home",
  //   title:'header-s1-1'
  // },
  {
    url: "/partners",
    title:'header-s1-2'
  },
  {
    url: "/group",
    title:'header-s1-3'
  },
];

const handleLangChange = (val) => {
  locale.value = val; // 要切换的语言
  langStore.updateLang(val);
};

// 退出登录
const handleLogout = () => {
  Axios.loginOut().then((res) => {
    if (res.code == "0") {
      const store = useUserStore();
      store.resetUserInfo();
      removeStorage("spToken");
      removeStorage("username");
    }
  });
};
const whyList = [
  {
    label: "m1-1",
    path: "/why-demo/about",
  },
  {
    label: "m1-2",
    path: "/why-demo/awards",
  },
  {
    label: "m1-3",
    path: "/why-demo/premium-clients",
  },
  {
    label: "m1-4",
    path: "/why-demo/active-trader-program",
  },
  {
    label: "m1-5",
    path: "/why-demo/legal-entity-identifier",
  },
];
const marketList = [
  {
    label: "m2-product-s1-t1",
    path: "/trading/instruments",
  },
  {
    label: "m2-2",
    path: "/trading/spreads-swaps-commissions",
  },
  {
    label: "m2-3",
    path: "/trading/trading-hours",
  },
  {
    label: "m2-5",
    path: "/trading/accounts",
  },
  {
    label: "m2-6",
    path: "/trading/funding-withdrawals",
  },
  {
    label: "m2-7",
    path: "/trading/get-started",
  },
  {
    label: "m2-8",
    path: "/trading/trading-hours",
  },
];
const platformList = [
  {
    label: "m3-1",
    path: "/trading-platforms/platforms",
  },
  {
    label: "m3-st-h-s0-t1",
    path: "/trading-platforms/social-trading",
  },
  {
    label: "capitalise-ai",
    path: "/trading-platforms/tools/capitalise-ai",
  },
  {
    label: "m2-4",
    path: "/trading-platforms/maintenance-schedule",
  },
];
const analyzeList = [
  {
    label: "m4-1",
    path: "/market-analysis/market-news",
  },
  {
    label: "m4-2",
    path: "/trading-platforms/tools",
  },
  // {
  //   label: "经济日历",
  //   path: "/market-analysis/economic-calendar",
  // },
  {
    label: "m4-3",
    path: "/market-analysis/analysts",
  },
];
const educationList = [
  {
    label: "m5-1",
    path: "/education/learn-forex",
  },
  {
    label: "m5-2",
    path: "/education/learn-trade-cfds",
  },
  {
    label: "m5-3",
    path: "/education/trading-guides",
  },
  // {
  //   label: "线上讲座",
  //   path: "/education/webinars",
  // },
];
const headerList = [
  {
    label: "header-s2-1",
    option: whyList,
    path: "/why-demo",
  },
  {
    label: "header-s2-2",
    option: marketList,
    path: "/trading",
  },
  {
    label: "header-s2-3",
    option: platformList,
    path: "/trading-platforms",
  },
  {
    label: "header-s2-4",
    option: analyzeList,
    path: "/market-analysis",
  },
  {
    label: "header-s2-5",
    option: educationList,
    path: "/education",
  },
];

const goPage = (path) => {
  router.push(path);
};
</script>

<style lang="scss" scoped>
.self-select :deep {
  width: 80px;
  margin-top: -4px;

  .el-input__wrapper {
    padding: 0;
  }
}

.transparent-bg:deep {
  .el-input__inner {
    background: #1c1b1a;
    color: #fff;
  }

  .el-input__suffix {
    background: #1c1b1a;
    // background: #3c454d;
  }
}

.black-bg:deep {
  .el-input__inner {
    background: #000;
    color: #fff;
  }

  .el-input__suffix {
    background: #000;
  }
}

// 以上为下拉选择框样式覆盖
.self-header-drop {
  .el-popper__arrow {
    display: none;
  }

  .el-scrollbar__wrap {
    min-width: 360px;
  }

  .el-dropdown-menu__item {
    padding: 10px 20px !important;
    line-height: 28px !important;
    font-size: 18px !important;
    color: #000 !important;
  }
}

.header-index {
  background: #000000;
  color: #fff;
  width: 100%;
  display: flex;
  justify-content: center;
  padding: 18px 0 16px 0;
  position: sticky;
  top: 0px;
  z-index: 1000;

  &-w {
    width: 1300px; // 核心区域1300
  }
}

.btn {
  background: #2e64f1;
  border-radius: 50px;
  padding: 12px 20px;
}

.menu {
  cursor: pointer;
  margin-right: 16px;
}

.menu:hover {
  color: #233ad4;
}
</style>
