<template>
  <div>
    <div class="page-header-background">
      <div class="page-header-content page-header-box">
        <!-- 通用左边菜单 -->
        <div class="left-page-header">
          <div @click="goRouter('/')" class="logo">
            <img
              src="@/assets/images/compositeHome/logo.png"
              width="24"
              height="24"
            />
            <span class="logoName2"> {{ $title }} </span>
          </div>
          <menu-item
            v-for="(_, i) in leftMenuList"
            :key="i"
            :title="_.title"
            :menuList="_.menuList"
            :url="_.url"
            :tips="_.tips"
          >
          </menu-item>
        </div>
        <!-- 登录前的右边菜单-->
        <div class="right-page-header-pre-login" v-if="!userStore.existToken">
          <div @click="goRouter('/login')">{{ $t("message.home.denglu") }}</div>
          <div @click="goRouter('/register')">
            {{ $t("message.home.zhuce") }}
          </div>

          <div @click="handleChangeLang('lang')">
            {{ $t("message.home.language") }}
          </div>
          <!-- <div @click="handleChangeLang('exchange')">{{ rate }}</div> -->
        </div>
        <!-- 登录后的右边菜单 -->
        <div class="right-page-header" v-else>
          <!-- 充值 -->
          <div style="margin: auto; top: 0; left: 0; bottom: 0; right: 0">
            <div @click="goRouter('/recharge')" class="recharge-btn">
              <img
                src="@/assets/images/headIcon/right-menu/material-symbols.png"
              />
              {{ $t("message.user.chongzhi") }}
            </div>
          </div>
          <!-- 钱包、订单、账户 -->
          <menu-item
            v-for="(_, i) in rightMenuList"
            :key="i"
            :title="_.title"
            :menuList="_.menuList"
            :isRight="true"
          >
          </menu-item>
          <!-- 选择的语言 -->
          <div @click="handleChangeLang('lang')" style="margin-left: 16px">
            {{ $t("message.home.language") }}
          </div>
          <!-- <div @click="handleChangeLang('exchange')">{{ rate }}</div> -->
        </div>
      </div>
    </div>
    <!-- 语言和汇率弹窗 -->
    <lang-select ref="langSelectRef" />
  </div>
</template>
<script setup>
import { mapState } from "pinia";
import { useRoute, useRouter } from "vue-router";
import { useUserStore } from "@/store/user";
import { useCurrencyStore } from "@/store/currency";
import LangSelect from "./langSelect.vue";
import MenuItem from "./headerMenuItem.vue";
import { getImages, nowUrl } from "@/utils/index";
import { qianbaoList, dingdanList } from "@/utils/menuConfig";
import {computed} from 'vue';
const router = useRouter();

const currencyStore = useCurrencyStore();
const userStore = useUserStore();

const langSelectRef = ref(null);

let leftMenuList = computed(()=>[
  {
    title: "shichang", //市场
    url: "/market",
  },
  {
    title: "shuzihuobi", // 数字货币= 现货交易 永续合约 交割合约
    menuList: [
      ...contractList("/coin/constract/btc"),
      ...spotList("/coin/spot/btc"),
    ],
  },
  {
    title: "meigu", // 美股 = 美股交易 永续合约 交割合约
    menuList: [
      ...contractList(
        `/usStocks/constract/${currencyStore.usStocksCurrency[0]?.symbol}`
      ),
      ...spotList(
        `/usStocks/spot/${currencyStore.usStocksCurrency[0]?.symbol}`,
        "meigujiaoyi"
      ),
    ],
  },

  {
    title: "waihui", // 外汇=永续合约 交割合约
    menuList: contractList(
      `/forex/constract/${currencyStore.forexCurrency[0]?.symbol}`
    ),
  },
  {
    title: "etf", // ETF= etf交易 永续合约 交割合约
    menuList: [
      ...contractList(`/etf/constract/${currencyStore.etfCurrency[0]?.symbol}`),
      ...spotList(
        `/etf/spot/${currencyStore.etfCurrency[0]?.symbol}`,
        "etfjiaoyi"
      ),
    ],
  },
  // {
  //   title: "Agu", // A股
  //   menuList: [
  //     ...contractList(
  //       `/cnStocks/constract/${currencyStore.cnStocksCurrency[0]?.symbol}`
  //     ),
  //     ...spotList(
  //       `/cnStocks/spot/${currencyStore.cnStocksCurrency[0]?.symbol}`,
  //       "Agujiaoyi"
  //     ),
  //   ],
  // },
  // {
  //   title: "taigu", // 台股和美股一样
  //   menuList: [
  //     ...contractList(
  //       `/twStocks/constract/${currencyStore.twStocksCurrency[0]?.symbol}`
  //     ),
  //     ...spotList(
  //       `/twStocks/spot/${currencyStore.twStocksCurrency[0]?.symbol}`,
  //       "taigujiaoyi"
  //     ),
  //   ],
  // },
  {
    title: "jinrongyewu", // 金融业务=基金理财+矿池锁仓+C2C
    menuList: [
      {
        iconPath: getImages("headIcon/left-menu/pig.png"),
        urlPath: "/fundMa",
        title: "licaijijin",
        desc: "jijinlicai_1",
      },
      {
        iconPath: getImages("headIcon/left-menu/min.png"),
        urlPath: "/fundMakc",
        title: "kuangchisuocang",
        desc: "kuangchisuocang_1",
      },
    ],
  },
])


const contractList = (urlPath) => {
  return [
    {
      iconPath: getImages("headIcon/left-menu/constract-icon.png"),
      urlPath,
      urlQuery: { timestamp: Date.now(), RouterName: "sustainable" },
      title: "yongxuheyue",
      desc: "yongxuheyue_1",
    },
    {
      iconPath: getImages("headIcon/left-menu/delivery-icon.png"),
      urlPath,
      urlQuery: { timestamp: Date.now(), RouterName: "delivery" },
      title: "jiaogeheyue",
      desc: "jiaogeheyue_1",
    },
  ];
};

const spotList = (urlPath, tle) => {
  return [
    {
      iconPath: getImages("headIcon/left-menu/spot-icon.png"),
      urlPath,
      title: tle || "xianhuojiaoyishouye",
      desc: "xianhuojiaoyi_1",
    },
  ];
};


const rightMenuList = [
  // {
  //   title: "新股认购",
  //   menuList: [
  //     {
  //       urlPath: "/newShares/newSharesBox",
  //       title: "xingurengou",
  //       iconPath: getImages("headIcon/newShares/6123.png"),
  //     },
  //     {
  //       urlPath: "/newShareSrecord/drawRecord",
  //       title: "chouqianjilu",
  //       iconPath: getImages("headIcon/newShares/6137.png"),
  //     },
  //     {
  //       urlPath: "/newShareSrecord/payRecord",
  //       title: "renjiaojilu",
  //       iconPath: getImages("headIcon/newShares/6138.png"),
  //     },
  //     {
  //       urlPath: "/newShares/newSharesInventory",
  //       title: "xingukucun",
  //       iconPath: getImages("headIcon/newShares/6139.png"),
  //     },
  //     {
  //       urlPath: "/newShares/nowSharesInventory",
  //       title: "xiangukucun",
  //       iconPath: getImages("headIcon/newShares/6140.png"),
  //     },
  //   ],
  // },
  {
    title: "qianbao",
    menuList: qianbaoList,
  },
  {
    title: "dingdan", //订单历史
    menuList: dingdanList,
  },
  {
    title: "zhanghu",
    menuList: [
      {
        urlPath: "/my/security",
        title: "zhanghuanquan",
        iconPath: getImages("headIcon/personal-menu/account-security.png"),
      },
      {
        urlPath: "/my/universal",
        title: "tongyong",
        iconPath: getImages("headIcon/personal-menu/universal.png"),
      },
      {
        urlPath: "/my/helpCenter",
        title: "bangzhuzhongxin",
        iconPath: getImages("headIcon/personal-menu/help-center.png"),
      },
      {
        urlPath: "/my/workerOrder",
        title: "gongdanzhongxin",
        iconPath: getImages("headIcon/personal-menu/help-center.png"),
      },
      {
        urlPath: "/promote",
        title: "tuiguangzhouxin",
        iconPath: getImages("headIcon/personal-menu/contract-account.png"),
      },
      {
        urlPath: "/my/announcement",
        title: "gonggaozhongxin",
        iconPath: getImages("headIcon/personal-menu/notice.png"),
      },
      {
        urlPath: "/loginOut",
        title: "tuichu",
        iconPath: getImages("headIcon/personal-menu/quit.png"),
      },
    ],
  },
];

const goRouter = (parmas) => {
  router.push(parmas);
};

const handleChangeLang = (type) => {
  langSelectRef.value.isShowClick(type);
};

const goApp = () => {
  window.location.href = "/app.html";
};
</script>
<style scoped>
.page-header-background {
  background-color: #171a1e;
  padding: 0 16px;
}

.page-header-content {
  display: flex;
  justify-content: space-between;
  height: 55px;
  line-height: 55px;
  color: white;
}

.left-page-header > div:nth-child(1) > img {
  display: inline-block;
}

.right-page-header,
.left-page-header {
  display: flex;
  flex-wrap: wrap;
}
.right-page-header-pre-login {
  display: flex;
}

.right-page-header > div {
  cursor: pointer;
  color: #fff;
}

.right-page-header-pre-login > div {
  padding: 0 16px;
  cursor: pointer;
  color: #fff;
}

.left-page-header > div {
  padding: 0 0px;
  cursor: pointer;
  color: #fff;
}

.left-page-header > div:hover,
.right-page-header > div:hover {
  color: #00d6ca;
}

.logo {
  display: flex;
  align-items: center;
}

.logoName2 {
  margin-left: 14px;
  margin-right: 30px;
  display: flex;
  align-items: center;
  font-weight: 600;
  font-size: 20px;
  color: #ffffff;
}

.el-dropdown-link {
  cursor: pointer;
  font-size: 16px;
}

.el-icon-arrow-down {
  font-size: 12px;
}

.recharge-btn {
  background-color: #1d91ff;
  padding: 4px;
  height: 30px;
  min-width: 98px;
  border-radius: 5px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}
</style>
