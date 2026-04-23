<template>
  <section class="pb-fix">
    <div class="hk-stocks-container-box">
      <van-loading color="#1194F7" class="loading-box-us" v-if="isLoading" />
      <section class="hk-stocks-tab-container">
        <section class="hk-stocks-container">
          <div class="indicator-container flex">
            <div :class="{
              'indicator-item': true,
              'up-bg': item.changeRatio > 0,
              'down-bg': item.changeRatio <= 0,
            }" v-for="(item, index) in indexCardInfo" :key="item.symbol" @click="handleIndexClick(item, index)">
              <div class="title-box">
                <div class="point"></div>
                <p class="title">{{ t(item.name) }}</p>
              </div>
              <p class="num" :class="{
                'text-up': item.changeRatio > 0,
                'text-down': item.changeRatio <= 0,
              }">
                {{ item.close ?? "--" }}
              </p>
              <p class="value" :class="{
                'text-up': item.changeRatio > 0,
                'text-down': item.changeRatio <= 0,
              }">
                {{
                  item.changeRatio
                  ? `${item.changeRatio}%`
                  : item.changeRatio === 0
                    ? 0
                    : "--"
                }}
              </p>
              <div class="trend">
                <m-echarts :dataObj="item" :ratio="Number(item.changeRatio)" :index="item.symbol" />
              </div>
            </div>
          </div>
          <section class="news-banner-container">
            <van-swipe class="swipe-box" :autoplay="5000" indicator-color="white" v-if="!isZh">
              <van-swipe-item>
                <img src="@/assets/image/hkStock/banner4.png" alt="" />
              </van-swipe-item>
              <van-swipe-item>
                <img src="@/assets/image/hkStock/banner5.png" alt="" />
              </van-swipe-item>
              <van-swipe-item>
                <img src="@/assets/image/hkStock/banner6.png" alt="" />
              </van-swipe-item>
            </van-swipe>
            <van-swipe class="swipe-box" :autoplay="5000" indicator-color="white" v-else>
              <van-swipe-item>
                <img src="@/assets/image/hkStock/banner1.png" alt="" />
              </van-swipe-item>
              <van-swipe-item>
                <img src="@/assets/image/hkStock/banner2.png" alt="" />
              </van-swipe-item>
              <van-swipe-item>
                <img src="@/assets/image/hkStock/banner3.png" alt="" />
              </van-swipe-item>
            </van-swipe>
          </section>
          <section class="notice-box">
            <div class="px-12">
              <van-notice-bar class="text-26 textColor" left-icon="" :scrollable="false" background="transparent">
                <div slot="left-icon" class="flex items-center more-img">
                  <img class="w-10 h-10 more-img" src="../../assets/Horn.png" alt="" />
                </div>
                <van-swipe vertical class="notice-swipe" :autoplay="2000" :show-indicators="false">
                  <van-swipe-item v-for="item in newsList" :key="item.id" @click="toAnnounceDetail(item.uuid)">{{
                    item.title }}
                  </van-swipe-item>
                </van-swipe>
                <div class="ml-20 flex items-center" slot="right-icon" @click.stop="$router.push('/cryptos/announce')">
                  <img class="w-10 h-10 more-img" src="../../assets/more.png" alt="" />
                </div>
              </van-notice-bar>
            </div>
          </section>
          <div class="divider mt-4"></div>
          <div class="nav">
            <hk-stock-ex-nav :prominentListData="prominentListData" />
          </div>
          <div class="divider"></div>
          <p class="hk-title">{{ t("港股板块") }}</p>
          <div class="flex hot-industry">
            <div class="indicator-item" v-for="(item, index) in hkHotModule" :key="item.symbol" @click="itemClick(item)">
              <div class="title-box">
                <div class="point"></div>
                <p class="title">{{ item?.name }}</p>
              </div>
              <p class="num">{{ item?.close }}</p>
              <p class="value" :class="{ 'text-up': item?.changeRatio > 0, 'text-down': item?.changeRatio <= 0 }">
                {{ item.changeRatio }}%</p>
            </div>
          </div>
          <div class="divider"></div>
          <div class="all-etf-ranking">
            <p class="title">{{ t("市场表现") }}</p>
            <div class="tabs flex">
              <div class="tab-item flex-1" v-for="(item, index) in tabList" @click="selectIndex(index, item.value)"
                :class="[tabIndex === index ? 'active' : '']" :key="item">
                {{ item.title }}
              </div>
            </div>
            <div class="etf-table">
              <ul>
                <li class="title-line">
                  <div class="flex-l">
                    <p>{{ t("nameCode") }}</p>
                  </div>
                  <div class="flex-r">
                    <div class="flex-r-item">
                      <p>{{ t("uptodate") }}</p>
                    </div>
                    <div class="flex-r-item">
                      <p>{{ t("increase") }}</p>
                    </div>
                    <div class="flex-r-item right">
                      <p>{{ t("Amount") }}</p>
                    </div>
                  </div>
                </li>
                <li v-for="(item, index) in hkStocksListData.slice(0, 5)" :key="item.symbol" @click="itemClick(item)">
                  <div class="flex-l">
                    <p>{{ item.name }}</p>
                    <p class="gray-text">
                      {{ item.symbol }}
                    </p>
                  </div>
                  <div class="flex-r">
                    <div class="flex-r-item">
                      <p :class="item.close < 1 ? 'text-up' : 'text-down'">
                        {{ item.close }}
                      </p>
                    </div>
                    <div class="flex-r-item">
                      <p :class="item.changeRatio > 0 ? 'text-up' : 'text-down'">
                        <span>{{ item.changeRatio }}%</span>
                      </p>
                    </div>
                    <div class="flex-r-item">
                      <div class="last-item right">
                        <p>
                          <span>{{ fixDate(item.amount) }}</span>
                        </p>
                      </div>
                    </div>
                  </div>
                </li>
              </ul>
            </div>
            <div class="more-box" @click="moreOpen(0, t('市场表现'))">
              <div class="icon-group">
                <p class="">{{ t('更多') }}</p>
                <img src="@/assets/image/quotes/right-arrow-grey.png" alt="" class="icon arrow">
              </div>
            </div>
          </div>

          <div class="technology" v-if="technologyListData && technologyListData.length > 0">
            <div class="divider"></div>
            <div class="other-etf-ranking">
              <p class="title">{{ t("Technology") }}</p>
              <div class="etf-table">
                <ul>
                  <li class="title-line">
                    <div class="flex-l">
                      <p>{{ t("nameCode") }}</p>
                    </div>
                    <div class="flex-r">
                      <div class="flex-r-item">
                        <p>{{ t("Premarketfall") }}</p>
                      </div>
                      <div class="flex-r-item">
                        <p>{{ t("premarketPrice") }}</p>
                      </div>
                    </div>
                  </li>

                  <li v-for="(item, index) in technologyListData.slice(0, 5)" :key="item.symbol" @click="itemClick(item)">
                    <div class="flex-l">
                      <p>{{ item.name }}</p>
                      <p class="gray-text">
                        {{ item.symbol }}
                      </p>
                    </div>
                    <div class="flex-r">
                      <div class="flex-r-item">
                        <p :class="item.changeRatio > 0 ? 'text-up' : 'text-down'">
                          <span>{{ item.changeRatio }}%</span>
                        </p>
                      </div>
                      <div class="flex-r-item">
                        <p>{{ item.close }}</p>
                      </div>
                    </div>
                  </li>
                </ul>
              </div>
              <div class="more-box" @click="moreOpen(1, t('Technology'),'technology')">
                <div class="icon-group">
                  <p class="">{{ t("更多") }}</p>
                  <img src="@/assets/image/quotes/right-arrow-grey.png" alt="" class="icon arrow" />
                </div>
              </div>
            </div>
          </div>

          <div class="financial" v-if="dinancialListData && dinancialListData.length > 0">
            <div class="divider"></div>
            <div class="other-etf-ranking">
              <p class="title">{{ t("Financial") }}</p>
              <div class="etf-table">
                <ul>
                  <li class="title-line">
                    <div class="flex-l">
                      <p>{{ t("nameCode") }}</p>
                    </div>
                    <div class="flex-r">
                      <div class="flex-r-item">
                        <p>{{ t("Premarketfall") }}</p>
                      </div>
                      <div class="flex-r-item">
                        <p>{{ t("premarketPrice") }}</p>
                      </div>
                    </div>
                  </li>
                  <li v-for="(item, index) in dinancialListData.slice(0, 5)" :key="item.symbol" @click="itemClick(item)">
                    <div class="flex-l">
                      <p>{{ item.name }}</p>
                      <p class="gray-text">
                        {{ item.symbol }}
                      </p>
                    </div>
                    <div class="flex-r">
                      <div class="flex-r-item">
                        <p :class="item.changeRatio > 0 ? 'text-up' : 'text-down'">
                          <span>{{ item.changeRatio }}%</span>
                        </p>
                      </div>
                      <div class="flex-r-item">
                        <p>{{ item.close }}</p>
                      </div>
                    </div>
                  </li>
                </ul>
              </div>
              <div class="more-box" @click="moreOpen(2, t('Financial'),'dinancial')">
                <div class="icon-group">
                  <p class="">{{ t("更多") }}</p>
                  <img src="@/assets/image/quotes/right-arrow-grey.png" alt="" class="icon arrow" />
                </div>
              </div>
            </div>
          </div>

          <div class="medicineAndfood" v-if="healthcareListData && healthcareListData.length > 0">
            <div class="divider"></div>
            <div class="other-etf-ranking">
              <p class="title">{{ t("MedicineAndfood") }}</p>
              <div class="etf-table">
                <ul>
                  <li class="title-line">
                    <div class="flex-l">
                      <p>{{ t("nameCode") }}</p>
                    </div>
                    <div class="flex-r">
                      <div class="flex-r-item">
                        <p>{{ t("Premarketfall") }}</p>
                      </div>
                      <div class="flex-r-item">
                        <p>{{ t("premarketPrice") }}</p>
                      </div>
                    </div>
                  </li>
                  <li v-for="(item, index) in healthcareListData.slice(0, 5)" :key="item.symbol" @click="itemClick(item)">
                    <div class="flex-l">
                      <p>{{ item.name }}</p>
                      <p class="gray-text">
                        {{ item.symbol }}
                      </p>
                    </div>
                    <div class="flex-r">
                      <div class="flex-r-item">
                        <p :class="item.changeRatio > 0 ? 'text-up' : 'text-down'">
                          <span>{{ item.changeRatio }}%</span>
                        </p>
                      </div>
                      <div class="flex-r-item">
                        <p>{{ item.close }}</p>
                      </div>
                    </div>
                  </li>
                </ul>
              </div>
              <div class="more-box" @click="moreOpen(3, t('MedicineAndfood'),'healthcare')">
                <div class="icon-group">
                  <p class="">{{ t("更多") }}</p>
                  <img src="@/assets/image/quotes/right-arrow-grey.png" alt="" class="icon arrow" />
                </div>
              </div>
            </div>
          </div>

          <div class="autoEnergy" v-if="energyListData && energyListData.length > 0">
            <div class="divider"></div>
            <div class="other-etf-ranking">
              <p class="title">{{ t("AutoEnergy") }}</p>
              <div class="etf-table">
                <ul>
                  <li class="title-line">
                    <div class="flex-l">
                      <p>{{ t("nameCode") }}</p>
                    </div>
                    <div class="flex-r">
                      <div class="flex-r-item">
                        <p>{{ t("Premarketfall") }}</p>
                      </div>
                      <div class="flex-r-item">
                        <p>{{ t("premarketPrice") }}</p>
                      </div>
                    </div>
                  </li>
                  <li v-for="(item, index) in energyListData.slice(0, 5)" :key="item.symbol" @click="itemClick(item)">
                    <div class="flex-l">
                      <p>{{ item.name }}</p>
                      <p class="gray-text">
                        {{ item.symbol }}
                      </p>
                    </div>
                    <div class="flex-r">
                      <div class="flex-r-item">
                        <p :class="item.changeRatio > 0 ? 'text-up' : 'text-down'">
                          <span>{{ item.changeRatio }}%</span>
                        </p>
                      </div>
                      <div class="flex-r-item">
                        <p>{{ item.close }}</p>
                      </div>
                    </div>
                  </li>
                </ul>
              </div>
              <div class="more-box" @click="moreOpen(4, t('AutoEnergy'),'energy')">
                <div class="icon-group">
                  <p class="">{{ t("更多") }}</p>
                  <img src="@/assets/image/quotes/right-arrow-grey.png" alt="" class="icon arrow" />
                </div>
              </div>
            </div>
          </div>

          <div class="manufacturingRetaiSales" v-if="manufacturingListData && manufacturingListData.length > 0">
            <div class="divider"></div>
            <div class="other-etf-ranking">
              <p class="title">{{ t("ManufacturingRetaiSales") }}</p>
              <div class="etf-table">
                <ul>
                  <li class="title-line">
                    <div class="flex-l">
                      <p>{{ t("nameCode") }}</p>
                    </div>
                    <div class="flex-r">
                      <div class="flex-r-item">
                        <p>{{ t("Premarketfall") }}</p>
                      </div>
                      <div class="flex-r-item">
                        <p>{{ t("premarketPrice") }}</p>
                      </div>
                    </div>
                  </li>
                  <li v-for="(item, index) in manufacturingListData.slice(0, 5)" :key="item.symbol"
                    @click="itemClick(item)">
                    <div class="flex-l">
                      <p>{{ item.name }}</p>
                      <p class="gray-text">
                        {{ item.symbol }}
                      </p>
                    </div>
                    <div class="flex-r">
                      <div class="flex-r-item">
                        <p :class="item.changeRatio > 0 ? 'text-up' : 'text-down'">
                          <span>{{ item.changeRatio }}%</span>
                        </p>
                      </div>
                      <div class="flex-r-item">
                        <p>{{ item.close }}</p>
                      </div>
                    </div>
                  </li>
                </ul>
              </div>
              <div class="more-box" @click="moreOpen(5, t('ManufacturingRetaiSales'),'manufacturing')">
                <div class="icon-group">
                  <p class="">{{ t("更多") }}</p>
                  <img src="@/assets/image/quotes/right-arrow-grey.png" alt="" class="icon arrow" />
                </div>
              </div>
            </div>
          </div>

          <div class="chineseStocks" v-if="chineseListData && chineseListData.length > 0">
            <div class="divider"></div>
            <div class="other-etf-ranking">
              <p class="title">{{ t("ChineseStocks") }}</p>
              <div class="etf-table">
                <ul>
                  <li class="title-line">
                    <div class="flex-l">
                      <p>{{ t("nameCode") }}</p>
                    </div>
                    <div class="flex-r">
                      <div class="flex-r-item">
                        <p>{{ t("Premarketfall") }}</p>
                      </div>
                      <div class="flex-r-item">
                        <p>{{ t("premarketPrice") }}</p>
                      </div>
                    </div>
                  </li>
                  <li v-for="(item, index) in chineseListData.slice(0, 5)" :key="item.symbol" @click="itemClick(item)">
                    <div class="flex-l">
                      <p>{{ item.name }}</p>
                      <p class="gray-text">
                        {{ item.symbol }}
                      </p>
                    </div>
                    <div class="flex-r">
                      <div class="flex-r-item">
                        <p :class="item.changeRatio > 0 ? 'text-up' : 'text-down'">
                          <span>{{ item.changeRatio }}%</span>
                        </p>
                      </div>
                      <div class="flex-r-item">
                        <p>{{ item.close }}</p>
                      </div>
                    </div>
                  </li>
                </ul>
              </div>
              <div class="more-box" @click="moreOpen(6, t('ChineseStocks'),'chinese')">
                <div class="icon-group">
                  <p class="">{{ t("更多") }}</p>
                  <img src="@/assets/image/quotes/right-arrow-grey.png" alt="" class="icon arrow" />
                </div>
              </div>
            </div>
          </div>

          <div v-for="listData in newTypeArr" v-if="newTypeArr.length > 0">
            <div :class="listData.name" v-if="listData.value && listData.value.length > 0">
              <div class="divider"></div>
              <div class="other-etf-ranking">
                <p class="title">{{ listData.name }}</p>
                <div class="etf-table">
                  <ul>
                    <li class="title-line">
                      <div class="flex-l">
                        <p>{{ t("nameCode") }}</p>
                      </div>
                      <div class="flex-r">
                        <div class="flex-r-item">
                          <p>{{ t("Premarketfall") }}</p>
                        </div>
                        <div class="flex-r-item">
                          <p>{{ t("premarketPrice") }}</p>
                        </div>
                      </div>
                    </li>
                    <li v-for="(item, index) in listData.value.slice(0, 5)" :key="item.symbol" @click="itemClick(item)">
                      <div class="flex-l">
                        <p>{{ item.name }}</p>
                        <p class="gray-text">
                          {{ item.symbol }}
                        </p>
                      </div>
                      <div class="flex-r">
                        <div class="flex-r-item">
                          <p :class="item.changeRatio > 0 ? 'text-up' : 'text-down'">
                            <span>{{ item.changeRatio }}%</span>
                          </p>
                        </div>
                        <div class="flex-r-item">
                          <p>{{ item.close }}</p>
                        </div>
                      </div>
                    </li>
                  </ul>
                </div>
                <div class="more-box" @click="moreOpen(listData.index, listData.name,listData.category)">
                  <div class="icon-group">
                    <p class="">{{ t("更多") }}</p>
                    <img src="@/assets/image/quotes/right-arrow-grey.png" alt="" class="icon arrow" />
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="divider"></div>
        </section>
      </section>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import HkStockExNav from "@/components/trade/hk-stock-ex-nav/index.vue";
import { useI18n } from "vue-i18n";

import { _getNewsList1 } from "@/service/user.api";
import MEcharts from "@/components/ex-echarts/index.vue";
import { fixDate } from "@/utils";
import { _getRealtimeByType, _publicRealtimeTop } from '@/service/quotes.api'


const { t } = useI18n();
const newsList = ref([]);
const hkStocksListData = ref([]);
const indexCardInfo = ref([]);
const prominentListData = ref([]); //知名美股
const technologyListData = ref([]); // 科技类
const dinancialListData = ref([]); // 金融类
const healthcareListData = ref([]); // 医疗类
const energyListData = ref([]); // 能源类
const manufacturingListData = ref([]); // 制造业
const chineseListData = ref([]); // 中概股
const rawMaterialsListData = ref([]); // 原材料业
const industryListData = ref([]); // 工业
const consumerServicesListData = ref([]); // 消费者服务业
const utilityListData = ref([]); // 公用事业
const estateListData = ref([]); // 地产建筑业
const informationListData = ref([]); // 资讯科技业
const conglomeratesListData = ref([]); //  综合企业
const newTypeArr = ref([]);
const hkHotModule = ref([])
const typeIndex = ref(0);
const tabIndex = ref(0);
const router = useRouter();
const route = useRoute();


const language = JSON.parse(localStorage.getItem("lang"));
const isZh = language == "zh-CN" || language == "CN";
const isLoading = ref(false);
const tabList = ref([
  { title: t("涨幅榜"), value: "changeRatio" },
  { title: t("跌幅榜"), value: "decrease" },
  { title: t("成交量"), value: "volume" },
  { title: t("成交额"), value: "amount" },
]);
const defaultListData = ref([])
const sortType = ref("changeRatio");

const props = defineProps({
  index: {
    type: Number,
    default: 0
  },
  tabActive: {
    type: Number,
    default: 0
  }
})

onMounted(async () => {
  getNewsList();
  getRealtimeByType();
  publicRealtimeTop()
  letMeGo()
});

const emit = defineEmits(['changeLetMego'])
const letMeGo = () => {
  emit('changeLetMego', () => {
    getRealtimeByType()
    publicRealtimeTop()
  })
}


const handleIndexClick = (item, index) => {
  typeIndex.value = index;
  router.push(`/quotes/usStockIndexDetail?symbol=${item.symbol}&symbolType=HK-stocks`);
};

const getNewsList = () => {
  _getNewsList1({
    language: JSON.parse(localStorage.getItem("lang")) || "en",
  }).then((res) => {
    const result = res.filter((item) => item.show);
    newsList.value = result.slice(0, 3);
  });
};



const publicRealtimeTop = () => {
  _publicRealtimeTop({
    type: 'HK-stocks',
  }).then(data => {
    indexCardInfo.value = data.slice(0, 3)
    hkHotModule.value = data.slice(3)
  }).catch((e) => {

  })
}


const getRealtimeByType = () => {
  _getRealtimeByType({
    type: "HK-stocks",
    pageNo: 1
  }).then((data) => {
    if (data === null) {
      data = [];
    }
    defaultListData.value = data
    let result = []
    if (sortType.value === 'decrease') {
      result = defaultListData.value.sort((a, b) => a.changeRatio - b.changeRatio)
    } else {
      result = defaultListData.value.sort((a, b) => b[sortType.value] - a[sortType.value])
    }
    hkStocksListData.value = result

    prominentListData.value = data;
    technologyListData.value =
      data.filter((item) => item.category === "technology") || [];
    dinancialListData.value = data.filter((item) => item.category === "dinancial") || [];
    healthcareListData.value =
      data.filter((item) => item.category === "healthcare") || [];
    energyListData.value = data.filter((item) => item.category === "energy") || [];
    manufacturingListData.value =
      data.filter((item) => item.category === "manufacturing") || [];
    chineseListData.value = data.filter((item) => item.category === "chinese") || [];
    rawMaterialsListData.value =
      data.filter((item) => item.category === "rawMaterials") || []; // 原材料业
    industryListData.value = data.filter((item) => item.category === "industry") || []; // 工业
    consumerServicesListData.value =
      data.filter((item) => item.category === "consumerServices") || []; // 消费者服务业
    utilityListData.value = data.filter((item) => item.category === "utility") || []; // 公用事业
    estateListData.value = data.filter((item) => item.category === "estate") || []; // 地产建筑业
    informationListData.value =
      data.filter((item) => item.category === "information") || []; // 资讯科技业
    conglomeratesListData.value =
      data.filter((item) => item.category === "conglomerates") || []; //  综合企业
    newTypeArr.value = [
      {
        name: t("rawMaterials"),
        index: 7,
        value: rawMaterialsListData.value,
        category:'rawMaterials'
      },
      {
        name: t("industry"),
        index: 8,
        value: industryListData.value,
        category:'industry'
      },
      {
        name: t("consumer"),
        index: 9,
        value: consumerServicesListData.value,
        category:'consumerServices'
      },
      {
        name: t("utility"),
        index: 10,
        value: utilityListData.value,
        category:'utility'
      },
      {
        name: t("estate"),
        index: 11,
        value: estateListData.value,
        category:'estate'
      },
      {
        name: t("information"),
        index: 12,
        value: informationListData.value,
        category:'information'
      },
      {
        name: t("conglomerates"),
        index: 13,
        value: conglomeratesListData.value,
        category:'conglomerates'
      },
    ];
  });
};


const selectIndex = (index, value) => {
  tabIndex.value = index;
  sortType.value = value
  let result = []
  if (sortType.value === 'decrease') {
    result = defaultListData.value.sort((a, b) => a.changeRatio - b.changeRatio)
  } else {
    result = defaultListData.value.sort((a, b) => b[sortType.value] - a[sortType.value])
  }
  hkStocksListData.value = result
};

const onRoute = (path) => {
  router.push(path);
};

const itemClick = (item) => {
  router.push(
    `/quotes/usStockDetail?symbol=${item.symbol}&symbolType=HK-stocks&enName=${item.enName}&tabIndex=${props.tabActive}`
  );
};
//打开更多
const moreOpen = (index, name,category) => {
    if(category){
        router.push(`/quotes/UsStockMore?index=${index}&typName=${name}&symbolType=HK-stocks&category=${category}&tabIndex=${props.tabActive}`);
    }else{
        router.push(`/quotes/UsStockMore?index=${index}&typName=${name}&symbolType=HK-stocks&tabIndex=${props.tabActive}`);
    }

};


watch(() => props.tabActive, (val) => {
  console.log('watch etf', val)
  if (props.index === val) {
    letMeGo()
  }
})
</script>
<style lang="scss" scoped>
.loading-box-us {
  position: absolute;
  top: 8% !important;
  left: 50%;
  transform: translate(-50%, -50%);
}

:deep(.van-tabs__nav) {
  background: $selectSymbol_background;
}

:deep(.van-tab) {
  font-size: 14px;
  color: $text_color;
  font-weight: 400;
}

:deep(.van-tab.van-tab--active) {
  font-weight: 700;
}

.hk-stocks-container-box {
  position: relative;

  .green {
    color: $green;
  }

  .red {
    color: $red;
  }

  .header {
    display: flex;
    height: 28px;
    padding: 0 12px;

    .flex-l {
      flex: 1;
      display: inline-flex;

      .icon {
        display: inline-block;
        width: 24px;
        height: 28px;
        padding: 6px 4px;

        img {
          height: 16px;
          width: 12px;
        }
      }
    }

    .title {
      font-weight: 700;
      font-size: 20px;
      line-height: 28px;
      color: $mainTextColor;
    }

    .icon-group {
      width: 100px;
      text-align: right;

      .icon {
        display: inline-block;
        width: 28px;
        height: 28px;
        padding: 4px;
        margin-left: 16px;
      }
    }
  }

  .notice-box {
    :deep(.van-notice-bar__content) {
      width: 100%;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .van-notice-bar {
      padding: 0;
      height: 30px;
    }

    .notice-swipe {
      flex: 1;
      margin-left: 10px;
      height: 30px;
      line-height: 30px;
      font-size: 13px;
    }

    .ml-20 {
      margin-left: 20px;
    }
  }

  .hk-stocks-tab-container {
    margin-top: 18px;
  }

  .chart-title {
    padding: 12px;
    font-weight: 700;
    font-size: 16px;
  }

  .hk-stocks-container {
    padding: 0;

    .kline-chart-container {
      position: relative;

      .kline-chart {
        width: 100%;
        height: 200px;
        border-bottom: 1px dashed $border_color;
      }

      .max {
        position: absolute;
        top: 5px;
        right: 10px;
        z-index: 2;
        font-size: 12px;
        color: #b8bdd1;
      }

      .min {
        position: absolute;
        z-index: 2;
        bottom: 35px;
        right: 10px;
        font-size: 12px;
        color: #b8bdd1;
      }
    }

    .time-x {
      padding: 0 12px 8px;
      font-size: 10px;
      color: #747a8f;
      display: flex;
      justify-content: space-between;
    }

    .indicator-container {
      padding: 0 12px;

      .indicator-item {
        flex: 1;
        max-width: 33.33%;
        padding-left: 6px;
        text-align: left;
        margin: 0 4px;
        border-radius: 10px;
        background: #1a2136;

        .trend {
          height: 65px;
        }

        .title-box {
          display: inline-flex;
          align-items: center;
        }

        .title {
          position: relative;
          font-size: 12px;
          line-height: 16px;
          color: $text_color;
        }

        .point {
          margin-right: 4px;
          width: 6px;
          height: 6px;
          border-radius: 50%;
        }

        .num {
          margin-left: 10px;
          font-weight: 700;
          font-size: 16px;
          line-height: 24px;
          color: $text_color;
        }

        .value {
          margin-left: 10px;
          font-size: 12px;
          font-weight: 400;
        }
      }

      .up-bg {
        background: $up-bg;
      }

      .down-bg {
        background: $down-bg;
      }

      .text-down {
        color: $red !important;
      }

      .text-up {
        color: $green !important;
      }
    }

    .hk-title {
      margin: 10px 0;
      padding: 0 12px;
      font-weight: 700;
      font-size: 16px;
      color: $mainTextColor;
    }

    .hot-industry {
      margin-bottom: 10px;

      .indicator-item {
        flex: 1;
        text-align: center;

        .title-box {
          display: inline-flex;
          align-items: center;
        }

        .title {
          position: relative;
          font-size: 12px;
          line-height: 16px;
          color: $lower-text-color;
        }

        .point {
          margin-right: 4px;
          width: 6px;
          height: 6px;
          border-radius: 50%;
        }

        .num {
          margin-left: 10px;
          font-weight: 700;
          font-size: 16px;
          line-height: 24px;
          color: $text_color;
        }

        .value {
          margin-left: 10px;
          font-size: 12px;
          font-weight: 400;
        }
      }
    }

    .news-banner-container {
      margin: 10px 0;
      height: 160px;
      padding: 0 12px;

      .swipe-box {
        border-radius: 8px;
      }

      .van-swipe-item {
        color: $text_color;
        font-size: 20px;
        line-height: 160px;
        text-align: center;
        background-color: $selectSymbol_background;

        img {
          display: block;
          height: 160px;
          width: 100%;
          object-fit: cover;
        }
      }
    }

    .banner-container {
      margin: 20px 0;
      padding: 0 12px;

      .swipe-box {
        border-radius: 10px;
      }

      .van-swipe-item {
        height: 110px;
        padding: 10px 14px;
        color: $text_color;
        font-size: 14px;
        background: linear-gradient(180deg, #332d2e 0%, #24242e 100%);

        .header {
          padding: 0;
          display: flex;
          justify-content: space-between;
          align-items: center;

          .title {
            font-size: 14px;
            text-overflow: ellipsis;
            overflow: hidden;
            word-break: break-all;
            white-space: nowrap;
            max-width: 160px;
          }

          .title:first-letter {
            color: #e69a38;
          }

          .date {
            font-size: 12px;
            color: #b8bdd1;
            width: 130px;
            text-align: right;
          }
        }
      }
    }

    .hot-container {
      margin: 20px 0;
      padding: 0 8px;

      .header-box {
        display: flex;

        .title {
          flex: 1;
          font-size: 16px;
          padding: 0 8px;
          font-weight: 700;
        }

        .icon-group {
          display: flex;
          align-items: center;
          font-size: 14px;

          .icon.arrow {
            margin-left: 10px;
            width: 15px;
            height: 15px;
          }
        }
      }

      .hot-box {
        display: grid;
        grid-template-columns: 33.33% 33.33% 33.33%;
        grid-template-rows: repeat(2, 100px);
      }

      .hot-item {
        padding: 6px 2px;
        margin: 4px;
        text-align: center;
        font-size: 12px;
        line-height: 18px;
        color: #b8bdd1;
        background: #1b2134;
        border-radius: 4px;

        .value {
          font-weight: 700;
          color: $text_color;
          line-height: 24px;
        }

        .num {
          .num-left {
            margin-right: 6px;
          }
        }
      }
    }

    .all-etf-ranking,
    .other-etf-ranking {
      margin-top: 10px;

      .title {
        font-size: 16px;
        font-weight: 700;
        padding: 0 12px;
      }

      .tabs {
        padding: 0 12px;
        margin-top: 10px;
        height: 40px;
        line-height: 24px;
        color: #bbbcbd;

        .tab-item {
          margin: 4px;
          text-align: center;
          padding: 4px 6px;
          font-size: 12px;
          color: $text_color5;
          background: $US_tab_background;
          border-radius: 10px;
          background-size: cover;
        }

        .active {
          font-weight: 700;
          color: $color_main !important;
          background: $US_tabActice_background;
          border-radius: 10px;
          background-size: cover;
        }
      }

      .etf-table {
        .right {
          text-align: right;
        }

        ul {
          margin-top: 10px;
        }

        .title-line {
          color: #747a8f;
          font-size: 12px;
          font-weight: 400;
          padding: 0 12px;
          border: none;
        }

        li {
          padding: 14px 12px;
          display: flex;
          justify-content: space-between;
          align-items: center;
          font-size: 12px;
          line-height: 18px;
          border-bottom: 1px solid $border_color;

          .gray-text {
            color: #bcbdc2;
            font-size: 12px;
          }

          .flex-l {
            width: 40%;
          }

          .flex-r {
            display: inline-flex;
            flex: 1;

            .flex-r-item {
              flex: 1;
              align-self: center;
              text-align: center;

              &:last-child {
                text-align: right;
              }
            }
          }
        }
      }

      .more-box {
        height: 40px;

        .icon-group {
          display: flex;
          justify-content: center;
          align-items: center;
          height: 40px;
          line-height: 40px;
          font-size: 14px;
          color: $text_color6;

          .icon.arrow {
            margin-left: 10px;
            width: 15px;
            height: 15px;
            color: $text_color6;
          }
        }
      }
    }
  }

  .bar-chart-wrap {
    overflow: hidden;
    display: flex;
    justify-content: center;
  }

  .top-img {
    width: 20px;
    height: 20px;
  }

  .chart-bottom {
    padding: 0 10px;

    .rect {
      position: relative;
      display: flex;
      height: 8px;
      width: 100%;

      .left {
        display: inline-block;
        width: 60%;
        background-color: #06cda5;
        height: 100%;
        border-radius: 4px 0 0 4px;
      }

      .right {
        display: inline-block;
        width: 40%;
        background-color: #f43368;
        height: 100%;
        border-radius: 0 4px 4px 0;
      }
    }

    .shape {
      position: absolute;
      left: 180px;
      top: 0;
      width: 60px;
      height: 100%;
      background-color: #747a8f;
      transform: skew(-20deg);
    }

    .params {
      margin-top: 4px;
      margin-bottom: 10px;
      display: flex;
      justify-content: space-between;
      font-size: 14px;

      .down {
        color: #06cda5;
      }

      .up {
        color: #f43368;
      }
    }
  }
}
</style>
