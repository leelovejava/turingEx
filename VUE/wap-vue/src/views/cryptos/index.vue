<template>
  <div class="home" id="cryptos">
    <section class="news-banner-container">
      <van-swipe class="swipe-box" :autoplay="5000" :indicator-color="swipeIndicatorColor" v-if="!isZh">
        <van-swipe-item>
          <img src="@/assets/image/usStock/enbanner1.png" alt="">
        </van-swipe-item>
        <van-swipe-item>
          <img src="@/assets/image/usStock/enbanner2.png" alt="">
        </van-swipe-item>
      </van-swipe>
      <van-swipe class="swipe-box" :autoplay="5000" :indicator-color="swipeIndicatorColor" v-else>
        <van-swipe-item>
          <img src="@/assets/image/usStock/cnbanner1.png" alt="">
        </van-swipe-item>
        <van-swipe-item>
          <img src="@/assets/image/usStock/cnbanner2.png" alt="">
        </van-swipe-item>
      </van-swipe>
    </section>
    <div class="quotes-notice-outer">
      <van-notice-bar
        class="quotes-notice-bar text-26"
        left-icon=""
        :scrollable="false"
        background="transparent"
        :color="noticeBarColor"
      >
        <template #left-icon>
          <span class="quotes-notice-bell" aria-hidden="true">
            <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.6">
              <path d="M18 8a6 6 0 10-12 0c0 7-3 7-3 7h18s-3 0-3-7" stroke-linecap="round" stroke-linejoin="round" />
              <path d="M13.73 21a2 2 0 01-3.46 0" stroke-linecap="round" />
            </svg>
          </span>
        </template>
        <van-swipe vertical class="notice-swipe" :autoplay="2000" :show-indicators="false">
          <van-swipe-item
            v-for="item in announceList"
            :key="item.id"
            class="notice-swipe-item"
            @click="toAnnounceDetail(item.uuid)"
          >
            {{ item.title }}
          </van-swipe-item>
        </van-swipe>
        <template #right-icon>
          <span class="quotes-notice-more" @click.stop="$router.push('/cryptos/announce')">
            <img class="more-img" src="../../assets/more.png" alt="">
          </span>
        </template>
      </van-notice-bar>
    </div>
    <cry-nav @open-ai-quant-sheet="showAiQuantSheet = true" />
    <div class="quickly">
      <div class="quickBox chongbi" :class="currentTheme === 'dark' ? 'dark' : 'white'"
        @click="$router.push('/cryptos/recharge/rechargeList')">
        <div class="left">
          <div class="leftBox">
            <img src="@/assets/theme/dark/image/chongicon.png" alt="">
          </div>
          <div class="leftCont">
            <p style="max-width:100px;">{{ $t("快捷充币") }}</p>
          </div>
        </div>
        <div class="right">
          <img v-if="currentTheme === 'dark'" src="@/assets/theme/dark/image/goto.png" alt="">
          <img v-else src="@/assets/theme/white/image/goto.png" alt="">
        </div>
      </div>
      <div class="quickBox tibi" :class="currentTheme === 'dark' ? 'dark' : 'white'"
        @click="$router.push('/cryptos/withdraw/withdrawPage')">
        <div class="left">
          <div class="leftBox"><img src="@/assets/theme/dark/image/tiicon.png" alt=""></div>
          <div class="leftCont">
            <p style="max-width:100px;">{{ $t("快速提币") }}</p>
          </div>
        </div>
        <div class="right">
          <img v-if="currentTheme === 'dark'" src="@/assets/theme/dark/image/goto.png" alt="">
          <img v-else src="@/assets/theme/white/image/goto.png" alt="">
        </div>
      </div>
    </div>
    <!-- <div
      class="ai-quant-entry"
      :class="currentTheme === 'dark' ? 'dark' : 'white'"
      @click="showAiQuantSheet = true"
    >
      <div class="ai-quant-entry-inner">
        <span class="ai-quant-entry-icon" aria-hidden="true">
          <svg viewBox="0 0 24 24" width="28" height="28" fill="none" stroke="currentColor" stroke-width="1.6">
            <path d="M12 3v3M12 18v3M4.2 4.2l2.1 2.1M17.7 17.7l2.1 2.1M3 12h3M18 12h3M4.2 19.8l2.1-2.1M17.7 6.3l2.1-2.1" stroke-linecap="round" />
            <circle cx="12" cy="12" r="3.5" />
          </svg>
        </span>
        <span class="ai-quant-entry-label">{{ $t('aiQuantEntry') }}</span>
        <img
          v-if="currentTheme === 'dark'"
          class="ai-quant-entry-arrow"
          src="@/assets/theme/dark/image/goto.png"
          alt=""
        />
        <img v-else class="ai-quant-entry-arrow" src="@/assets/theme/white/image/goto.png" alt="" />
      </div>
    </div> -->
    <ex-hot :listData="hList"></ex-hot>
    <list-quatation :listData="qList" />
    <van-popup
      v-model:show="showAiQuantSheet"
      position="bottom"
      round
      teleport="body"
      class="ai-quant-bottom-sheet-popup"
      :safe-area-inset-bottom="true"
    >
      <div class="ai-quant-bottom-sheet">
        <div class="ai-quant-sheet-handle" />
        <div class="ai-quant-choose-title">{{ $t('aiQuantChooseTitle') }}</div>

        <button type="button" class="ai-quant-choose-card" @click="openSpotQuestionnaire">
          <span class="ai-quant-choose-icon ai-quant-choose-icon--chart" aria-hidden="true">
            <svg viewBox="0 0 24 24" width="30" height="30" fill="none">
              <path
                d="M4 18V6m4 12V10m4 8V8m4 10v-6m4 6V4"
                stroke="currentColor"
                stroke-width="2.2"
                stroke-linecap="round"
              />
            </svg>
          </span>
          <span class="ai-quant-choose-body">
            <span class="ai-quant-choose-card-title">{{ $t('aiQuantSpotCopyTitle') }}</span>
            <span class="ai-quant-choose-card-desc">{{ $t('aiQuantSpotCopyDesc') }}</span>
          </span>
          <span class="ai-quant-choose-chevron" aria-hidden="true">›</span>
        </button>

        <button type="button" class="ai-quant-choose-card" @click="openBotAiQuant">
          <span class="ai-quant-choose-icon ai-quant-choose-icon--bot" aria-hidden="true">
            <svg viewBox="0 0 24 24" width="30" height="30" fill="none">
              <rect x="6" y="7" width="12" height="11" rx="2" stroke="currentColor" stroke-width="1.9" />
              <circle cx="10" cy="12" r="1.3" fill="currentColor" />
              <circle cx="14" cy="12" r="1.3" fill="currentColor" />
              <path d="M9 4h6l1 3H8l1-3z" stroke="currentColor" stroke-width="1.9" stroke-linejoin="round" />
            </svg>
          </span>
          <span class="ai-quant-choose-body">
            <span class="ai-quant-choose-card-title">{{ $t('aiQuantBotCopyTitle') }}</span>
            <span class="ai-quant-choose-card-desc">{{ $t('aiQuantBotCopyDesc') }}</span>
          </span>
          <span class="ai-quant-choose-chevron" aria-hidden="true">›</span>
        </button>
      </div>
    </van-popup>
    <van-popup v-model:show="item.showPopUp" style="border-radius:10px;" :close-on-click-overlay="false"
      v-for="item in popupNewsList" :key="item.id">
      <div class="w-350 p-20 box-border">
        <div class="font-bold text-center text-28 textColor">{{ item.title }}</div>
        <div class="flex justify-center mt-30" v-if="item.imgUrl"><img :src="`${item.imgUrl}`" class="w-200 h-200"
            alt="" /></div>
        <div class="py-10 textColor   content-title" v-html="item.content"></div>
        <van-button color="#1194F7" class="w-full h-40 rounded-full" type="info" @click="closePopNotice(item)">
          {{ $t('我知道了') }}
        </van-button>
      </div>
    </van-popup>
  </div>
</template>
<script>
import { Popup, Swipe, SwipeItem, showToast } from "vant";
import ListQuatation from "@/components/Transform/list-quotation/index.vue";
import { mapGetters, mapActions } from "vuex";
import { SET_COIN_LIST } from '@/store/const.store'
import CryNav from "@/components/Transform/cry-nav/index.vue";
import ExHot from "@/components/Transform/ex-hot/index.vue";
import { setStorage, getStorage } from '@/utils'
import { _getUnreadMsg } from '@/service/im.api'
import { _getNewsList1, _getPopupNews } from '@/service/user.api'
import { BASE_URL } from "@/config";
import { themeStore } from '@/store/theme';
import { _getRealtimeByType, _publicRealtimeTop } from '@/service/quotes.api'
import { _getQuantQuestionExist } from '@/service/cryptos.api'

export default {
  name: "HomePage",
  props: {
    index: {
      type: Number,
      default: 0
    },
    tabActive: {
      type: Number,
      default: 0
    }
  },
  watch: {
    tabActive: {
      handler() {
        console.log('watch cryptos', this.index, this.tabActive)
        if (this.index === this.tabActive) {
          this.letMeGo()
        }
      },
    },
  },
  components: {
    ListQuatation,
    CryNav,
    ExHot,
    [Popup.name]: Popup,
    [Swipe.name]: Swipe,
    [SwipeItem.name]: SwipeItem,
  },
  computed: {
    ...mapGetters({
      coinList: 'home/coinList',
      currency: 'home/currency',
      coinArr: 'home/coinArr',
      hotArr: 'home/hotArr',
      userInfo: 'user/userInfo'
    }),
    currentTheme() {
      return themeStore().theme
    },
    noticeBarColor() {
      return this.currentTheme === 'dark' ? '#ffffff' : '#333333'
    },
    swipeIndicatorColor() {
      return this.currentTheme === 'dark' ? '#ffffff' : '#1678FF'
    },
  },
  data() {
    const arr = [] // 初始化数据
    for (let i = 0; i < 10; i++) {
      arr.push({ id: i })
    }
    return {
      BASE_URL,
      account: "",
      hList: arr.slice(0, 3), // 热门
      qList: arr, // 行情列表
      active: 0,
      timer: null,
      loading: false,
      announceTitle: '',
      announceId: '',
      announceList: [],
      socket: null,
      popupNewsList: [],
      language: '',
      isZh: false,
      tabActiveIndex: 0,
      showAiQuantSheet: false
    }
  },
  methods: {
    ...mapActions('home', [SET_COIN_LIST]),
    async fetchQList() { // 获取行情
      const list = await _getRealtimeByType({
        type: 'cryptos',
        pageNo: 1
      }).catch(() => {

        //TODO: 轮询
        // this.timer = setTimeout(() => {
        //   this.fetchQList()
        // }, 1000)
      })
      if (!(list instanceof Array)) {
        return
      }
      // this.qList = list.slice(0,10);
      this.qList = list;
    },
    publicRealtimeTop() {
      _publicRealtimeTop({
        type: 'cryptos',
      }).then(data => {
        this.hList = data.slice(0, 3) || []
      }).catch((e) => {

      })
    },
    getNews() {
      _getNewsList1({
        language: this.$i18n.locale,
      }).then(res => {
        this.announceList = res
      })
    },
    getPopupNews() {
      _getPopupNews({
        token: this.userInfo.token,
        language: this.$i18n.locale,
      }).then(res => {
        if (res.length) {
          if (!getStorage('popNotice')) {
            let list = res
            list.forEach(item => {
              item.showPopUp = true
            })
            this.popupNewsList = list
          }
        }
      })
    },
    closePopNotice(item) {
      item.showPopUp = false
      setStorage('popNotice', true)
    },
    toAnnounceDetail(announceId) {
      if (announceId) {
        this.$router.push({ path: '/cryptos/AnnounceDetail', query: { id: announceId } })
      }
    },
    onClickLeft() { },
    onClickRight() { },
    async openSpotQuestionnaire() {
      this.showAiQuantSheet = false
      const res = await _getQuantQuestionExist()
      if (res?.exist) {
        showToast({ message: this.$t('traderAlreadySubmitted'), position: 'middle' })
        return
      }
      this.$router.push('/cryptos/aiQuant/questionnaire')
    },
    openBotAiQuant() {
      this.showAiQuantSheet = false
      this.$router.push('/cryptos/aiQuant')
    },
    letMeGo() {
      this.$emit('changeLetMego', () => {
        this.fetchQList()
        this.publicRealtimeTop()
      })
    },
    // clearTimer() {
    //   if (this.timer) {
    //     clearInterval(this.timer)
    //     this.timer = null
    //   }
    // },
    getTabName() {
      return '1'
    },
  },
  async created() {
    console.log('onMounted');
    console.log('this.type', this.type);
    this.getNews();
    this.getPopupNews()
    await this.SET_COIN_LIST('cryptos')
    this.language = JSON.parse(localStorage.getItem('lang'))
    this.isZh = this.language == "zh-CN" || this.language == "CN"
    // this.fetchQList()
    if (this.index === this.tabActive) {
      this.letMeGo()
    }
  },
  unmounted() {
    // this.clearTimer()
  }
};
</script>
<style lang="scss" scoped>
:deep(.van-notice-bar__content) {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.home {
  width: 100%;
  box-sizing: border-box;
  padding: 8px 12px 108px;
  background: $main_background;
}

.news-banner-container {
  margin: 8px 0 12px;
  height: 204px;
  padding: 0;

  .swipe-box {
    border-radius: 20px;
    overflow: hidden;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.35);
  }

  .van-swipe-item {
    color: $text_color;
    font-size: 20px;
    line-height: 204px;
    text-align: center;
    background: $tab_background;

    img {
      display: block;
      height: 204px;
      width: 100%;
      object-fit: cover;
    }
  }
}

.quotes-notice-outer {
  margin-bottom: 8px;
  padding: 0;
  border-radius: 16px;
  background: $main2_background;
  border: 1px solid $line_color;
  overflow: hidden;
}

.quotes-notice-bell {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 80px;
  color: $text_color;
  flex-shrink: 0;
}

.quotes-notice-more {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 80px;
  flex-shrink: 0;
  opacity: 0.75;
}

:deep(.quotes-notice-bar.van-notice-bar) {
  padding: 0 4px 0 8px;
  height: 80px;
  align-items: center;
  color: $text_color;
  font-size: 28px;
}

:deep(.quotes-notice-bar .van-notice-bar__left-icon) {
  margin-right: 8px;
}

:deep(.quotes-notice-bar .van-notice-bar__right-icon) {
  margin-left: 8px;
}

.box {
  width: 100px;
  height: 100px;
  background: red;
  font-size: 25px;
}

.van-notice-bar {
  padding: 0;
}

.notice-swipe {
  flex: 1;
  height: 80px;
  line-height: 80px;
  margin-top: 0;
  margin-left: 10px;
}

.buyBox {
  background: $cont_background;
  width: 100%;
  height: 132px;
  border-radius: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-left: 43px;
  padding-right: 40px;
  margin-bottom: 44px;
  box-sizing: border-box;

  .left {
    display: flex;
    align-items: center;
    flex: 1;

    .leftBox {
      width: 104px;
      height: 88px;

      img {
        width: 100%;
        height: 100%;
      }
    }
  }

  .leftCont {
    margin-left: 40px;

    p {
      font-size: 30px;
      color: $text_color;
      font-weight: 600;
      line-height: 14px;
    }

    span {
      color: $text_color1;
      ;
      font-size: 22px;
      line-height: 30px;
    }
  }

  .right {
    width: 53px;
    height: 53px;

    img {
      width: 100%;
      height: 100%;
    }
  }
}


#cryptos .quickly {
  width: 100%;
  min-height: 110px;
  display: flex;
  justify-content: space-between;
  gap: 8px;
  margin: 16px 0 20px;

  .quickBox {
    flex: 1;
    min-height: 110px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 18px;
    border-radius: 20px;
    box-sizing: border-box;

    .left {
      display: flex;
      align-items: center;
      flex: 1;
      min-width: 0;

      .leftBox {
        width: 75px;
        height: 75px;
        flex-shrink: 0;
        display: flex;
        align-items: center;
        justify-content: center;

        img {
          width: 70px;
          height: 70px;
          object-fit: contain;
        }
      }
    }

    .leftCont {
      margin-left: 12px;

      p {
        font-size: 30px;
        font-weight: 600;
        line-height: 1.3;
      }
    }

    .right {
      width: 36px;
      height: 36px;
      opacity: 0.55;
      flex-shrink: 0;

      img {
        width: 100%;
        height: 100%;
      }
    }

    &.dark {
      border: 1px solid rgba(255, 255, 255, 0.08);
      background: linear-gradient(
        145deg,
        rgba(40, 42, 55, 0.95) 0%,
        rgba(28, 28, 32, 0.98) 100%
      );
      box-shadow:
        inset 0 1px 0 rgba(255, 255, 255, 0.06),
        0 4px 16px rgba(0, 0, 0, 0.25);

      .leftCont p {
        color: #f3f4f6;
      }
    }

    &.white {
      border: 1px solid $line_color;
      background: linear-gradient(145deg, $mainBgColor 0%, $tab_background 100%);
      box-shadow: 0 4px 14px rgba(0, 0, 0, 0.06);

      .leftCont p {
        color: $text_color;
      }
    }
  }

  .chongbi {}

  .tibi {}
}

#cryptos .ai-quant-entry {
  width: 100%;
  margin: 0 0 20px;
  border-radius: 20px;
  border: 1px solid rgba(0, 145, 255, 0.28);
  background: linear-gradient(
    135deg,
    rgba(17, 120, 247, 0.22) 0%,
    rgba(40, 42, 55, 0.95) 50%,
    rgba(28, 28, 32, 0.98) 100%
  );
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.06),
    0 4px 16px rgba(0, 0, 0, 0.25);
  cursor: pointer;
}

#cryptos .ai-quant-entry-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 96px;
  padding: 16px 20px;
  box-sizing: border-box;
}

#cryptos .ai-quant-entry-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  flex-shrink: 0;
  color: $btn_main;
}

#cryptos .ai-quant-entry-label {
  flex: 1;
  margin-left: 14px;
  font-size: 30px;
  font-weight: 600;
  color: $text_color;
  text-align: left;
}

#cryptos .ai-quant-entry-arrow {
  width: 36px;
  height: 36px;
  opacity: 0.55;
  flex-shrink: 0;
}

.ai-quant-bottom-sheet-popup.van-popup {
  background: $main_background;
}

@media (min-width: 900px) {
  .ai-quant-bottom-sheet-popup.van-popup.van-popup--bottom {
    width: 92% !important;
    max-width: 520px !important;
    left: 50% !important;
    right: auto !important;
    margin: 0 !important;
    transform: translateX(-50%) !important;
  }
}

.ai-quant-bottom-sheet {
  padding: 6px 20px calc(28px + env(safe-area-inset-bottom, 0px));
  min-height: 280px;
  box-sizing: border-box;
}

.ai-quant-sheet-handle {
  width: 40px;
  height: 5px;
  margin: 10px auto 22px;
  border-radius: 3px;
  background: $line_color;
}

.ai-quant-choose-title {
  margin: 0 0 22px;
  padding: 0 4px;
  font-size: 32px;
  font-weight: 600;
  color: $text_color;
  text-align: left;
}

.ai-quant-choose-card {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 22px 18px;
  min-height: 108px;
  margin-bottom: 14px;
  border: 1px solid $border_color;
  border-radius: 16px;
  background: $tab_background;
  color: inherit;
  cursor: pointer;
  text-align: left;
  box-sizing: border-box;
  transition: opacity 0.12s ease;
}

.ai-quant-choose-card:last-of-type {
  margin-bottom: 6px;
}

.ai-quant-choose-card:active {
  opacity: 0.88;
}

.ai-quant-choose-icon {
  flex-shrink: 0;
  width: 58px;
  height: 58px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 14px;
  color: $btn_main;
  background: rgba(22, 120, 255, 0.14);
}

.ai-quant-choose-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.ai-quant-choose-card-title {
  font-size: 30px;
  font-weight: 700;
  color: $text_color;
  line-height: 1.25;
}

.ai-quant-choose-card-desc {
  font-size: 24px;
  color: $text_color1;
  line-height: 1.35;
}

.ai-quant-choose-chevron {
  flex-shrink: 0;
  font-size: 42px;
  font-weight: 300;
  color: $text_color1;
  line-height: 1;
  margin-left: 4px;
}

.notice-swipe-item {
  font-size: 28px;
  line-height: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: $text_color;
}

.more-img {
  width: 18px !important;
  height: 18px !important;
  object-fit: contain;
}

.content-title {
  font-size: 28px;
}
</style>
