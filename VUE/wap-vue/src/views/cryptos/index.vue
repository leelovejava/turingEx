<template>
  <div class="home" id="cryptos">
    <section class="news-banner-container">
      <van-swipe class="swipe-box" :autoplay="5000" indicator-color="white" v-if="!isZh">
        <van-swipe-item>
          <img src="@/assets/image/usStock/enbanner1.png" alt="">
        </van-swipe-item>
        <van-swipe-item>
          <img src="@/assets/image/usStock/enbanner2.png" alt="">
        </van-swipe-item>
      </van-swipe>
      <van-swipe class="swipe-box" :autoplay="5000" indicator-color="white" v-else>
        <van-swipe-item>
          <img src="@/assets/image/usStock/cnbanner1.png" alt="">
        </van-swipe-item>
        <van-swipe-item>
          <img src="@/assets/image/usStock/cnbanner2.png" alt="">
        </van-swipe-item>
      </van-swipe>
    </section>
    <div class="py-4">
      <van-notice-bar class="text-26 textColor" left-icon="" :scrollable="false" background="transparent">
        <div slot="left-icon" class=" flex items-center more-img"><img class="w-10 h-10 more-img"
            src="../../assets/Horn.png" alt="">
        </div>
        <van-swipe vertical class="notice-swipe" :autoplay="2000" :show-indicators="false">
          <van-swipe-item v-for="item in announceList" :key="item.id" @click="toAnnounceDetail(item.uuid)">{{ item.title
          }}
          </van-swipe-item>
        </van-swipe>
        <div class="ml-20 flex items-center " slot="right-icon" @click.stop="$router.push('/cryptos/announce')"><img
            class="w-10 h-10 more-img" src="../../assets/more.png" alt="">
        </div>
      </van-notice-bar>
    </div>
    <cry-nav />
    <div class="quickly">
      <div class="quickBox chongbi" :class="THEME == 'dark' ? 'dark' : 'white'"
        @click="$router.push('/cryptos/recharge/rechargeList')">
        <div class="left">
          <div class="leftBox">
            <img src="@/assets/theme/dark/image/chongicon.png" alt="">
          </div>
          <div class="leftCont">
            <p style="max-width:100px;" class="color-white">{{ $t("快捷充币") }}</p>
          </div>
        </div>
        <div class="right">
          <img v-if="THEME == 'dark'" src="@/assets/theme/dark/image/goto.png" alt="">
          <img v-else src="@/assets/theme/white/image/goto.png" alt="">
        </div>
      </div>
      <div class="quickBox tibi ml-10" :class="THEME == 'dark' ? 'dark' : 'white'"
        @click="$router.push('/cryptos/withdraw/withdrawPage')">
        <div class="left">
          <div class="leftBox"><img src="@/assets/theme/dark/image/tiicon.png" alt=""></div>
          <div class="leftCont">
            <p style="max-width:100px;">{{ $t("快速提币") }}</p>
          </div>
        </div>
        <div class="right">
          <img v-if="THEME == 'dark'" src="@/assets/theme/dark/image/goto.png" alt="">
          <img v-else src="@/assets/theme/white/image/goto.png" alt="">
        </div>
      </div>
    </div>
    <ex-hot :listData="hList"></ex-hot>
    <list-quatation :listData="qList" />
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
import { Popup, Swipe, SwipeItem } from "vant";
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
const thStore = themeStore()
const THEME = thStore.theme

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
  },
  data() {
    const arr = [] // 初始化数据
    for (let i = 0; i < 10; i++) {
      arr.push({ id: i })
    }
    return {
      BASE_URL,
      THEME,
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
      tabActiveIndex: 0
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
  padding: 16px 16px 108px;
}

.news-banner-container {
  margin: 20px 0;
  height: 320px;
  padding: 0 12px;

  .swipe-box {
    border-radius: 8px;
  }

  .van-swipe-item {
    color: $text_color;
    font-size: 20px;
    line-height: 320px;
    text-align: center;
    background-color: $selectSymbol_background;

    img {
      display: block;
      height: 320px;
      width: 100%;
      object-fit: cover;
    }
  }
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
  height: 82px;
  line-height: 82px;
  margin-top: 20px;
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
      color: #21262F;
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
  height: 144px;
  display: flex;
  justify-content: space-between;
  margin-bottom: 32px;

  .quickBox {
    width: 370px;
    height: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-right: 40px;
    padding-left: 16px;

    .left {
      display: flex;
      align-items: center;
      flex: 1;

      .leftBox {
        width: 100px;
        height: 100px;

        img {
          width: 100%;
          height: 100%;
        }
      }
    }

    .leftCont {
      margin-left: 24px;

      p {
        font-size: 24px;
        color: $text_color;
        font-weight: 600;
        line-height: 14px;
      }
    }

    .right {
      width: 48px;
      height: 48px;

      img {
        width: 100%;
        height: 100%;
      }
    }
  }

  .chongbi {
    background: url('@/assets/theme/white/image/chongb.png');
    background-size: cover;

    &.dark {
      background: url('@/assets/theme/dark/image/chongb.png');
      background-size: cover;
    }
  }

  .tibi {
    background: url('@/assets/theme/white/image/tib.png');
    background-size: cover;

    &.dark {
      background: url('@/assets/theme/dark/image/tib.png');
      background-size: cover;
    }
  }
}



.van-notice-bar {
  height: 60px;
}

.notice-swipe {
  height: 60px;
  line-height: 60px;
}

.van-swipe-item {
  font-size: 30px;
}

.more-img {
  width: 40px !important;
  height: 40px !important;
  margin-top: 8px;
}

.content-title {
  font-size: 28px;
}
</style>
