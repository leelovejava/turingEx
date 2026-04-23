<template>
  <div id="advertiserDetailPage">
    <div v-if="userInfo" id="wantBuy" class="bg-blue " style="overflow: auto">
      <div class="w-full h-32 pl-10 pr-8 box-border bg-blue flex justify-between text-white items-center">
        <van-icon class="flex-1 text-32 font-bold" name="arrow-left" @click.native="$router.back()" />
      </div>

      <div class="main">
        <div class="user">
          <img :src="c2cUser.headImg" class="avag" alt="" />
          <!--        <div class="avag">B</div>-->
          <div>
            <p class="name">{{ c2cUser.nickName }}</p>
            <div class="advertisement flex items-center">
              <img class="succ" src="@/assets/image/icon-success.png" alt="" />
              <span>{{ $t('认证广告方') }}</span>
              <span class="shu">|</span>
              <span>{{ $t('保证金') }} {{ $t(`${c2cUser.deposit}USDT`) }}</span>
            </div>
            <div class="e-li">
              <div class="email">
                <span>{{ $t('邮箱') }}</span>
                <img v-if="party.emailAuthority" class="relative" src="@/assets/image/success.png" alt="" />
                <img class="w-5 h-5 relative" v-else src="~@/assets/image/c2c/no.png" alt="">
              </div>
              <div class="email">
                <span>{{ $t('手机') }}</span>
                <img v-if="party.phoneAuthority" class="relative" src="@/assets/image/success.png" alt="" />
                <img class="w-5 h-5 relative" v-else src="~@/assets/image/c2c/no.png" alt="">
              </div>
              <div class="email">
                <span>{{ $t('身份') }}</span>
                <img v-if="party.kycAuthority" class="relative" src="@/assets/image/success.png" alt="" />
                <img class="w-5 h-55 relative" v-else src="~@/assets/image/c2c/no.png" alt="">
              </div>
              <div class="email">
                <span>{{ $t('高级认证') }}</span>
                <img v-if="party.kycHighlevelAuthority" class="relative " src="@/assets/image/success.png" alt="" />
                <img class="w-5 h-5 relative " v-else src="~@/assets/image/c2c/no.png" alt="">
              </div>
            </div>
          </div>
        </div>

        <div class="main-list" ref="main" :style="fullHeight">
          <div class="main-box">
            <div class="main-nav ">
              <span class="info">{{ $t('信息') }}</span>
              <span class="gen flex  items-center" style="color: #868d9a" @click="linkToDetail">
                {{ $t('更多数据') }}
                <img class="right" src="@/assets/image/icon-right.png" alt="" />
              </span>
            </div>
            <div class="shuju">
              <div>
                <p class="num">{{ c2cUser.thirtyDaysOrder }}</p>
                <p class="time">{{ $t('30日成单数') }}</p>
              </div>
              <div>
                <p class="num">{{ c2cUser.thirtyDaysOrderRatio }}%</p>
                <p class="time">{{ $t('30日成单率') }}</p>
              </div>
            </div>
            <p class="flex justify-between ping" @click="show = true">
              <span class="time">{{ $t('平均放行') }}</span>
              <span class="textColor">{{ c2cUser.thirtyDaysPassAverageTime }}{{ $t('分钟') }}</span>
            </p>
            <p class="flex justify-between ping" @click="show = true">
              <span class="time">{{ $t('平均付款') }}</span>
              <span class="textColor">{{ c2cUser.thirtyDaysPayAverageTime }}{{ $t('分钟') }}</span>
            </p>
          </div>

          <div class="guang">
            <p class="g-title">{{ $t('在线广告') }}</p>
            <!-- 
          
          <div v-show="buy.length === 0" class="g-box"></div> -->
            <div class="g-box">
              <p class="g-zai">{{ $t('在线出售广告') }}</p>
              <div v-if="advert.length === 0" class="g-data flex justify-center items-center">
                <div class="text-align: center;">
                  <img class="zanwu" :src="require('@/assets/image/c2c/Group1942.png')" alt="" />
                </div>
              </div>
              <template v-else>
                <div class="g-data" v-for="(item, index) in advert" :key="index">
                  <div class="usdt ">
                    <img src="@/assets/image/USDT.png" alt="" />
                    <span class="textColor">{{ item.symbol.toLocaleUpperCase() }}</span>
                  </div>
                  <p>{{ $t('单价') }}</p>
                  <p class="g-dan textColor">
                    <span class="textColor">$</span><span class="textColor">{{ item.symbolValue }}</span>
                  </p>
                  <p class="mar-9">
                    {{ $t('数量') }}<span style="color: #fff">{{ item.coinAmount }} USDT</span>
                  </p>
                  <p class="mar-30">
                    {{ $t('限额') }}
                    <span style="color: #fff">${{ item.investmentMin }} - ${{ item.investmentMax }}</span>
                  </p>
                  <div class="flex">
                    <div class="flex items-center mar-23">
                      <div class="shu-h"></div>
                      <span>{{ $t('银行卡') }}</span>
                    </div>
                    <div class="flex items-center">
                      <div class="shu-h" style="background: #4ba6eb"></div>
                      <span>{{ $t('币交易') }}</span>
                    </div>
                  </div>

                  <div v-if="item.direction === 'sell'" class="buy" style="background: #e35461" @click="trade(item)">{{
                    $t('向Ta出售') }}</div>
                  <div v-else class="buy" @click="trade(item)">{{ $t('向Ta购买') }}</div>
                </div>
              </template>

              <!-- <div class="g-bottom"></div> -->
            </div>

            <p class="g-feng">
              {{ $t(' 风控提示：为了降低您的交易风险，认证广告方已向平台缴纳保证金，请放心交易。') }}
            </p>
          </div>
        </div>
      </div>

      <van-dialog v-model="show" :showConfirmButton="false" :closeOnClickOverlay="true">
        <div class="dia-main">
          <p>{{ $t('30日平均放行: 近30日卖币时收款后放币的平均确认时间。') }}</p>
          <p class="mar-70">{{ $t('30日平均放行: 近30日购买数字货币平均付款的时间。') }}</p>
          <div class="dia-btn" @click="show = false">{{ $t('好的') }}</div>
        </div>
      </van-dialog>

      <van-popup class="w-full h-full" v-model:show="showDetail" position="right">
        <detail :detail="c2cUser" @back="back" />
      </van-popup>
    </div>
  </div>
</template>

<script>
import { Icon, Dialog, Popup } from 'vant';
import detail from "@/views/cryptos/advertiserDetail/detail.vue";
import otcApi from "@/service/otc.api.js";

export default {
  name: 'advertiserDetail',
  props: ['uid'],
  components: {
    [Icon.name]: Icon,
    [Dialog.name]: Dialog,
    [Popup.name]: Popup,
    detail,
  },
  data() {
    return {
      top: 0, // main-list距离顶部的高度
      show: false,
      showDetail: false,
      userInfo: null,
      info: {
        singularNumber: 1045,
        singularBai: '99.24%',
        release: 1.87,
        payment: 0.93,
      },
      sell: [
        {
          name: 'USDT',
          price: '0.970',
          count: '498.60',
          minCount: '200.00',
          maxCount: '483.64',
        },
      ],
      buy: [
        {
          name: 'USDT',
          price: '0.970',
          count: '498.60',
          minCount: '200.00',
          maxCount: '483.64',
        },
      ],
    }
  },
  created() {
    console.log(this.uid);
    otcApi.ctcUserGet({ c2c_user_id: this.uid, language: this.$i18n.locale }).then(res => {
      this.userInfo = res;
    })
  },
  mounted() {
    this.$nextTick(() => {
      setTimeout(() => {
        this.top = this.$refs.main.getBoundingClientRect().top;
      }, 1000)
    })
  },
  methods: {
    linkToDetail() {
      this.showDetail = true;
      // this.$router.push('/advertiserDetail/detail')
    },
    back() {
      this.showDetail = false;
    },
    trade(item) {
      console.log(item);
      this.$store.commit('c2c/SET_DIRECTION', item.direction)
      this.$router.push({
        name: 'c2cTrade',
        query: {
          id: item.id,
          type: item.direction,
        }
      })
    }
  },
  computed: {
    c2cUser() {
      return this.userInfo.c2c_user;
    },
    advert() {
      return this.userInfo.advert;
    },
    party() {
      return this.userInfo.party;
    },
    fullHeight() {
      return {
        'height': `calc(100vh - ${this.top}px)`
      }
    }
  }
}
</script>

<style lang="scss" scoped>
#advertiserDetailPage {
  font-size: 30px;

  .box-radius {
    border-radius: 40px;
  }

  .list-img {
    top: -6px;
  }

  .main {
    min-height: 100%;
  }

  .user {
    display: flex;
    color: #fff;
    background: $btn_main;
  }

  .avag {
    width: 64px;
    height: 64px;
    background: #fff;
    border-radius: 50%;
    font-weight: bold;
    font-size: 32px;
    text-align: center;
    line-height: 64px;
    margin: 10px 25px 0 32px;
  }

  .name {
    font-size: 33px;
  }

  .succ {
    width: 23px;
    height: 23px;
    margin-right: 10px;
  }

  .shu {
    margin: 0 15px;
  }

  .advertisement {
    display: flex;
    align-items: center;
    font-size: 20px;
    margin-top: 10px;
    margin-bottom: 24px;
  }

  .e-li {
    display: flex;
    font-size: 26px;
  }

  .email {
    display: flex;
    align-items: center;
    //width: 100px;
    justify-content: space-between;
    margin-right: 48px;

    span {
      margin-right: 14px;
    }
  }

  .email>img {
    width: 23px;
    height: 23px;
    border-radius: 50%;
  }

  .main-list {
    overflow: auto;
    margin-top: 44px;
    background: $main_background;

    border-radius: 80px 80px 0px 0px;
    // padding-bottom: 300px;
  }

  .main-box {
    padding: 45px 32px 60px;

    border-bottom: 1px solid $divi_line;
  }

  .main-nav {
    display: flex;
    justify-content: space-between;
    margin-bottom: 48px;
  }

  .info {
    color: $text_color;

    font-size: 30px;
    font-weight: bold;
  }

  .gen {
    font-size: 22px;
  }

  .right {
    width: 13px;
    height: 18px;
    margin-left: 15px;
  }

  .shuju {
    display: flex;
    padding-right: 244px;
    justify-content: space-between;
    margin-bottom: 53px;
  }

  .num {
    font-size: 40px;

    color: $text_color;

    font-weight: bold;
    margin-bottom: 18px;
  }

  .time {
    color: #868d9a;
    font-size: 22px;
  }

  .ping {
    margin-right: 47px;
    font-size: 22px;
    margin-top: 26px;
  }

  .g-title {
    color: $text_color;
    font-size: 30px;
    font-weight: bold;
    margin: 60px 32px;
  }

  .g-zai {
    color: #868d9a;
    font-size: 28px;
    margin-left: 32px;
    padding-bottom: 32px;

    border-bottom: 1px solid $divi_line;
  }

  .g-data {
    min-height: 380px;

    background: $main_background;

    box-sizing: border-box;
    font-size: 24px;
    color: #868d9a;
    padding: 45px 0 41px 32px;
    position: relative;
    border-bottom: 1px solid $divi_line;

    .g-dan {
      margin: 8px 0 22px;
      color: $text_color;
      font-weight: bold;

      span {
        font-size: 40px;
      }
    }

    .mar-9 {
      margin-bottom: 9px;
    }

    .mar-30 {
      margin-bottom: 30px;
    }

    .buy {
      width: 160px;
      height: 56px;
      background: #2EBD85;
      color: #fff;
      font-size: 26px;
      text-align: center;
      line-height: 56px;
      border-radius: 10px;
      position: absolute;
      right: 32px;
      bottom: 95px;
    }
  }

  .g-bottom {
    margin: 60px 0;
    height: 1px;
    border-top: 1px solid #484756;
  }

  .usdt {
    display: flex;
    align-items: center;
    margin-bottom: 32px;

    img {
      width: 32px;
      height: 32px;
      margin-right: 15px;
    }

    span {
      font-size: 30px;
    }
  }

  .shu-h {
    width: 5px;
    height: 20px;
    background: #e7bb41;
    border-radius: 10px;
    margin-right: 9px;
  }

  .mar-23 {
    margin-right: 23px;
  }

  .g-feng {
    color: #868d9a;
    font-size: 18px;
    margin: 0 32px;
    padding-top: 80px;
    padding-bottom: 80px;
  }

  .zanwu {
    width: 150px;
    height: 180px;
  }

  .dia-main {
    padding: 60px 64px 44px 48px;
    color: $text_color;

    font-size: 28px;

    .mar-70 {
      margin: 70px 0 46px;
    }
  }

  .dia-btn {
    height: 80px;
    line-height: 80px;
    text-align: center;
    color: $text_color;

    background: #1d91ff;
  }

  .bg-blue {
    background: $btn_main;
  }

  // .textColor {
  //   color: #fff;
  // }

  .appContent {
    padding-bottom: 0 !important;
  }
}
</style>
