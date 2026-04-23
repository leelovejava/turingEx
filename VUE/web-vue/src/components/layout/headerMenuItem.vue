<template>
  <div class="dropdown">
    <!-- 港股等 -->
    <el-tooltip v-if="tips" :content="t('message.user.dataNotReady')">
      <button class="dropbtn">
        {{ t(`message.user.${title}`) }}
      </button>
    </el-tooltip>
    <!-- 市场 -->
    <button v-else class="dropbtn" @click="gotoPage(url)">
      {{ t(`message.user.${title}`) }}
      <img
        v-if="menuList.length"
        class="icon-arrow-down"
        src="@/assets/images/headIcon/right-menu/icon_arrow_down.png"
      />
    </button>
    <!-- 下拉框 -->
    <div class="dropdown-content" v-if="menuList.length">
      <div
        class="dropdown-item dropdown-right-item pl-20"
        v-if="title == 'zhanghu' && store.existToken"
      >
        {{ store.userInfo.username || userName }}
      </div>
      <div
        :class="`${
          isRight ? 'dropdown-item dropdown-right-item' : 'dropdown-item'
        }`"
        @click="gotoRoute(item)"
        v-for="(item, i) in menuList"
        :key="i"
      >
        <div v-if="isRight">
          <img
            :src="item.iconPath"
            :class="
              title == 'qianbao' ? 'wallet-img' : 'dropdown-item-right-icon'
            "
          />
          <p class="dropdown-item-title">
            {{ t(`message.user.${item.title}`) }}
          </p>
        </div>
        <div v-else>
          <img :src="item.iconPath" class="dropdown-item-icon" />
          <p class="dropdown-item-title">
            {{ t(`message.home.${item.title}`) }}
          </p>
          <p class="dropdown-item-content">
            {{ t(`message.home.${item.desc}`) }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from "vue-router";
import { useUserStore } from "@/store/user";
import { useI18n } from "vue-i18n";
import { removeStorage } from "@/utils";
import Axios from "@/api/login.js";
import { ElTooltip } from "element-plus";
const store = useUserStore();
const router = useRouter();
const { t } = useI18n();
const userName = JSON.parse(localStorage.getItem("username"));

const props = defineProps({
  title: {
    type: String,
    default: "",
  },
  url: {
    type: String,
    default: "",
  },
  menuList: {
    type: Array,
    default: [],
  },
  isRight: {
    type: Boolean,
    default: false,
  },
  tips: {
    type: Boolean,
    default: false,
  },
});

const gotoPage = (path) => {
  if (!path) {
    return;
  }
  router.push(path);
};

const gotoRoute = (item) => {
  const { urlPath, urlQuery } = item || {};
  if (!urlPath) {
    return;
  }
  if (urlPath.includes("loginOut")) {
    Axios.loginOut().then((res) => {
      if (res.code == "0") {
        store.resetUserInfo();
        removeStorage("spToken");
        removeStorage("username");
        router.push("/");
        return;
      }
    });
    return;
  }
  let route = urlPath;
  if (urlQuery) {
    route = {
      path: urlPath,
      query: urlQuery,
    };
  }
  router.push(route);
};
</script>

<style scoped>
.pl-20 {
  padding-left: 20px;
}

.dropbtn {
  color: white;
  padding: 0 14px;
  font-size: 14px;
  border: none;
  cursor: pointer;
}

.dropdown {
  position: relative;
  display: inline-block;
}

.dropdown-content {
  display: none;
  position: absolute;
  background-color: #f9f9f9;
  min-width: 160px;
  box-shadow: 0px 8px 16px 0px rgba(0, 0, 0, 0.2);
  z-index: 100;
}

/* 账户 */
.dropdown-content:last-child {
  left: -80px;
}

.dropdown-content a {
  color: black;
  padding: 12px 16px;
  text-decoration: none;
  display: block;
}

.dropdown:hover .dropdown-content {
  display: block;
}

.dropdown:hover .dropbtn {
  color: #1d91ff;
}

.dropdown-item {
  height: 70px;
  width: 463px;
  left: 0px;
  top: 0px;
  border-radius: 0px;
  background-color: #333;
  position: relative;
}

.dropdown-item:hover {
  background-color: #333;
  opacity: 0.9;
}

.dropdown-right-item,
.dropdown-right-item:hover {
  height: 40px;
  width: 260px;
}

.dropdown-item a {
  color: #ffffff;
}

.dropdown-item-icon {
  width: 20px;
  height: 20px;
  position: absolute;
  left: 5%;
  top: 40%;
}
.dropdown-item-right-icon {
  width: 32px;
  height: 32px;
  position: absolute;
  left: 5%;
}
.wallet-img {
  width: 20px;
  height: 20px;
  position: absolute;
  left: 5%;
  top: 5px;
}

.dropdown-item-title {
  position: absolute;
  left: 12%;
  top: 16%;
  color: #ffffff;
  line-height: 16px;
  font-size: 16px;
}
.dropdown-right-item .dropdown-item-title {
  left: 18%;
}

.dropdown-item-content {
  position: absolute;
  left: 12%;
  top: 53%;
  color: #b9c2d1;
  line-height: 14px;
  font-size: 14px;
}

.icon-arrow-down {
  display: inline-block;
}

.dropbtn:hover > img {
  transform: rotate(180deg);
}
.xianhuo-img {
  width: 20px !important;
  height: 20px !important;
  margin: 0 8px !important;
}
</style>
