<template>
  <section class="my-index">
    <fx-header :title="$t('my')" :showLeft="false">
      <template v-slot:right>
        <van-badge v-if="userStore.userInfo && userStore.userInfo.token" class="w-35 h-33 mr-22" :content="unreadMsg">
          <van-icon name="service-o" size="18" @click="$router.push('/workerOrder')" />
        </van-badge>
      </template>
    </fx-header>

    <div class="px-8 mt-4 mb-10">
      <h1 class="text-2xl font-bold title" v-if="!(userStore.userInfo && userStore.userInfo.token)">
        {{ $t("welcome") }}&nbsp;{{ $title }}!
      </h1>
      <p class="sub-text" v-if="!(userStore.userInfo && userStore.userInfo.token)">
        {{ $t("全球最大的区块链资产平台") }}
      </p>
      <p class="w-full flex mt-4 gap-x-4" v-if="!(userStore.userInfo && userStore.userInfo.token)">
        <van-button class="flex-1" @click="onRoute('/register')">{{
          $t("register")
        }}</van-button>
        <van-button class="flex-1" type="primary" @click="onRoute('/login')">{{
          $t("login")
        }}</van-button>
      </p>
      <div class="mt-4 flex" v-else>
        <img class="w-24 h-24" src="@/assets/image/avatar.png" alt="avatar" />
        <div class="ml-4 flex flex-col justify-center">
          <div class="font-bold text-lg name">
            {{ userStore.userInfo && userStore.userInfo.username }}
          </div>
          <div class="text-sm text-gray-400 mt-1 flex items-center id-text">
            ID：{{ userStore.userInfo && userStore.userInfo.usercode
            }}<img class="w-8 h-8 ml-8" src="@/assets/image/idcopy.png" alt="id" @click="copy" />
          </div>
        </div>
      </div>
    </div>
    <!-- <div class="divider"></div> -->
    <div class="mt-4">
      <van-cell-group :border="false">
        <van-cell center :title="t('darkMode')" :border="false">
          <template #right-icon>
            <van-switch :model-value="isDarkTheme" @update:model-value="onDarkModeChange" />
          </template>
        </van-cell>
      </van-cell-group>
    </div>
    <div v-if="userStore.userInfo && userStore.userInfo.token">
      <van-cell-group :title="t('快捷入口')" :border="false"></van-cell-group>
      <van-grid class="van-grid-main" :column-num="4" :border="false">
        <van-grid-item v-for="(item, index) in quickList" :key="index" icon="photo-o" :to="item.path"
          :text="t(item.name)">
          <template #icon>
            <img class="grid-item-img" :src="item.icon" />
          </template>
        </van-grid-item>
      </van-grid>
    </div>
    <!-- <div class="divider"></div> -->
    <div class="mt-4">
      <van-cell-group v-for="(item, index) in cellList" :key="index" :title="item.title" :border="false">
        <van-cell 
          v-for="(_item, _index) in item.list" 
          :key="_index" 
          is-link="is-link" 
          center="center"
          :title="_item.title" 
          @click="onRoute(_item.path)"
          v-show="!_item.requireLogin || (userStore.userInfo && userStore.userInfo.token)"
        >
          <template #icon>
            <img class="cell-img" :src="_item.icon" />
          </template>
          <div v-if="_item.path === '/certificationCenter' && _item.show === true">
            <span :class="{
              red: status == 0 || status == 3,
              yellow: status == 1,
              green: status == 2,
            }">{{
  status == 0
  ? $t("notCertified")
  : status == 1
    ? $t("reviewing")
    : status == 2
      ? $t("verified")
      : status == 3
        ? $t("noPassView")
        : ""
}}</span>
          </div>
          <div v-if="_item.path === '/advancedCtf' && _item.show === true">
            <span :class="{
              red: kycHighStatus == 0 || kycHighStatus == 3,
              yellow: kycHighStatus == 1,
              green: kycHighStatus == 2,
            }">{{
  kycHighStatus == 0
  ? $t("notCertified")
  : kycHighStatus == 1
    ? $t("reviewing")
    : kycHighStatus == 2
      ? $t("verified")
      : kycHighStatus == 3
        ? $t("noPassView")
        : ""
}}</span>
          </div>
        </van-cell>
      </van-cell-group>
    </div>
    <div class="px-4 mt-4" v-if="userStore.userInfo && userStore.userInfo.token">
      <p class="w-full flex mt-4 gap-x-4">
        <van-button type="primary" class="flex-1" @click="loginOut">{{
          $t("loginOut")
        }}</van-button>
      </p>
    </div>
    <!-- <button style="height: 200px;" @click="changeTheme">哈哈</button> -->
  </section>
</template>

<script setup>
import { reactive, onMounted, ref, computed } from "vue";
import { useRouter } from "vue-router";
import { _getIdentify, _getKycHighLevel, _logOut } from "@/service/user.api.js";
import { useUserStore } from "@/store/user";
import { useI18n } from "vue-i18n";
import useClipboard from "vue-clipboard3";
import { showToast } from "vant";
import addBankIcon from "@/assets/image/userCenter/addBank.png";
import kycHighStatusIcon from "@/assets/image/userCenter/kycHighStatus.png";
import store from "@/store/store";
import { _getUnreadMsg } from '@/service/im.api';
import { themeStore } from "@/store/theme";
import { SET_THEME } from "@/store/types.store";
const { t } = useI18n();
const { toClipboard } = useClipboard();

const thStore = themeStore();

const isDarkTheme = computed(() => thStore.theme === "dark");

const onDarkModeChange = (dark) => {
  const next = dark ? "dark" : "white";
  if (next === thStore.theme) return;
  thStore[SET_THEME](next, true);
};

const router = useRouter();
const userStore = useUserStore();
const status = ref(null);
const kycHighStatus = ref(null);
const unreadMsg = ref('');
const state = reactive({
  cellList: [
    {
      title: t("safe"),
      list: [
        { icon: "shield-o", title: t("safe"), path: "/safety" },
        { icon: "setting-o", title: t("changePassword"), path: "/changePassword" },
        { icon: addBankIcon, title: t("AddPaymentMethod"), path: "/payMentMethod/list" },
      ],
    },
    {
      title: t("universal"),
      list: [
        { icon: "font-o", title: t("language"), path: "/language" },
        { icon: "service-o", title: t("workerOrder.center"), path: "/workerOrder", requireLogin: true },
        {
          icon: "idcard",
          title: t("authVerify"),
          path: "/certificationCenter",
          show: true,
        },
        { icon: "todo-list-o", title: t("账变记录"), path: "/cryptos/accountChange" },
        { icon: "gold-coin-o", title: t("计价方式"), path: "/cryptos/exchangeRate" },
      ],
    },
  ],
});
const quickList = reactive([
  {
    name: "safe",
    path: "/safety",
    icon: new URL(
      `../../assets/theme/${thStore.theme}/image/assets-center/fast-icon2.png`,
      import.meta.url
    ),
  },
  {
    name: "\u8d26\u53d8\u8bb0\u5f55",
    path: "/cryptos/accountChange",
    icon: new URL(
      `../../assets/theme/${thStore.theme}/image/assets-center/fast-icon1.png`,
      import.meta.url
    ),
  },
  {
    name: "\u9080\u8bf7\u63a8\u5e7f",
    path: "/promote",
    icon: new URL(
      `../../assets/theme/${thStore.theme}/image/assets-center/fast-icon4.png`,
      import.meta.url
    ),
  },
  {
    name: "authVerify",
    path: "/certificationCenter",
    icon: new URL(
      `../../assets/theme/${thStore.theme}/image/assets-center/fast-icon5.png`,
      import.meta.url
    ),
  },
]);
const onRoute = (path) => {
  console.log(path);
  router.push(path);
};

onMounted(() => {
  if (userStore.userInfo && userStore.userInfo.token) {
    getIdentify();
    getKycHighLevel();
    fetchUnread();
  }
});

const cellList = computed(() => {
  if (userStore.userInfo && userStore.userInfo.token) {
    return [
      // {
      //   title: t('safe'), list: [
      //     // { icon: 'shield-o', title: t('safe'), path: '/safety' },
      //     // { icon: 'setting-o', title: t('changePassword'), path: '/changePassword' },
      //     { icon: addBankIcon, title: t('AddPaymentMethod'), path: '/payMentMethod/list' }
      //   ]
      // },
      {
        title: t("universal"),
        list: [
          {
            icon: new URL(
              "../../assets/image/assets-center/language.png",
              import.meta.url
            ),
            title: t("language"),
            path: "/language",
          },
          {
            icon: new URL(
              "../../assets/image/assets-center/onLineService.png",
              import.meta.url
            ),
            title: t("onLineService"),
            path: "/workerOrder",
          },
          {
            icon: new URL(
              "../../assets/image/assets-center/authVerify.png",
              import.meta.url
            ),
            title: t("authVerify"),
            path: "/certificationCenter",
            show: true,
          },
          {
            icon: new URL(
              "../../assets/image/assets-center/AdvancedCertification.png",
              import.meta.url
            ),
            title: t("高级认证"),
            path: "/advancedCtf",
            show: true,
          },
          // { icon: 'todo-list-o', title: t('账变记录'), path: '/cryptos/accountChange' },
          {
            icon: new URL(
              "../../assets/image/assets-center/valuation.png",
              import.meta.url
            ),
            title: t("计价方式"),
            path: "/cryptos/exchangeRate",
          },
          {
            icon: new URL(
              "../../assets/image/assets-center/AddPaymentMethod.png",
              import.meta.url
            ),
            title: t("AddPaymentMethod"),
            path: "/payMentMethod/list",
          },
        ],
      },
      // {
      //   title: t('用户'), list: [
      //     { icon: 'link-o', title: t('邀请推广'), path: '/promote' },
      //   ]
      // },
      {
        title: t("更多"),
        list: [
          {
            icon: new URL("../../assets/image/assets-center/help.png", import.meta.url),
            title: t("帮助中心"),
            path: "/helpCenter",
          },
          {
            icon: new URL(
              "../../assets/image/assets-center/aboutUs.png",
              import.meta.url
            ),
            title: t("关于我们"),
            path: "/aboutUs",
          },
        ],
      },
    ];
  } else {
    return [
      {
        title: t("universal"),
        list: [
          {
            icon: new URL("../../assets/image/assets-center/help.png", import.meta.url),
            title: t("language"),
            path: "/language",
          },
          // { icon: 'gold-coin-o', title: t('计价方式'), path: '/cryptos/exchangeRate' },
        ],
      },
      // {
      //   title: t('更多'), list: [
      //     { icon: 'question-o', title: t('帮助中心'), path: '/helpCenter' },
      //     { icon: 'user-o', title: t('关于我们'), path: '/aboutUs' }
      //   ]
      // }
    ];
  }
});
const loginOut = () => {
  _logOut({
    token: userStore.userInfo.token,
  }).then((res) => {
    userStore.userInfo = {};
    store.state.user.userInfo = {};
  });
};
const getIdentify = () => {
  _getIdentify().then((data) => {
    status.value = data.status;
  });
};
const getKycHighLevel = () => {
  _getKycHighLevel().then((data) => {
    kycHighStatus.value = data.status;
  });
};
const copy = async () => {
  try {
    await toClipboard(userStore.userInfo && userStore.userInfo.usercode);
    showToast(t("copySuccess"));
  } catch (e) {
    console.error(e);
  }
};

//TODO: 获取未读消息,接口返回数据结构有问题
const fetchUnread = () => { // 获取未读
  _getUnreadMsg().then(res => {
    unreadMsg.value = (res * 1 > 0) ? res * 1 : '';
  })
}
</script>

<style lang="scss" scoped>
:deep(.van-cell-group__title) {
  background: $main2_background !important;
  padding: 12px 16px;
}

:deep(.van-cell-group) {
  .van-icon {
    display: flex;
    align-items: center;
  }
}

:deep(.van-cell) {
  background: $mainBgColor;
  border-bottom: 1px solid $border_color;

  &:hover {
    background: $mainBgColor;
  }
}

:deep(.van-cell-group__title) {
  background: $mainBgColor;
}

:deep(.van-nav-bar__content) {
  background: $mainBgColor;
}

:deep(.van-icon) {
  color: $text_color;
}

:deep(.van-cell__title) {
  color: $text_color;
}

:deep(.van-cell:after) {
  border: none;
}

:deep(.van-grid-item__content) {
  background: $mainBgColor;
}

:deep(.van-grid-item__text) {
  color: $text_color;
  font-size: 13px;
  height: 30px;
}

.my-index {
    padding-bottom: calc(80px + constant(safe-area-inset-bottom)) !important;
    padding-bottom: calc(80px + env(safe-area-inset-bottom)) !important;
}

h1.title {
  font-size: 22px;
  line-height: 26px;
}

.sub-text {
  padding: 10px 0;
  color: $text_color1;
  font-size: 12px;
}

.name {
  font-size: 16px;
  line-height: 20px;
}

.yellow {
  color: #f5c421;
}

.red {
  color: $red;
}

.green {
  color: $green;
}

.id-text {
  font-size: 14px;
  font-weight: 700;
}

.grid-item-img {
  width: 48px;
  height: 48px;
  margin-bottom: 5px;
}

.van-grid-main {
  padding: 5px 0;
}

.cell-img {
  width: 20px;
  height: 20px;
  margin-right: 10px;
}
</style>

