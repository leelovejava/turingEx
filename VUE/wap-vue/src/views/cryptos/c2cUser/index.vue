<template>
  <div id="c2c_user" class="absolute left-0 top-0 bottom-160 w-full c2c_user">
    <div class="w-full h-full top">
      <order-nav style="background: var(--theme-color)" />
      <div class="flex pl-32 pr-32 mt-20 overflow-auto">
        <div class="avatar w-70 h-70 flex items-center font-bold text-34 justify-center" @click="handleGoUserHome">
          <img class="w-full h-full rounded-full" :src="ctc_user.headImg" alt="">
        </div>
        <div class="ml-25 text-white">
          <div class="text-34">{{ ctc_user.nickName }}</div>
          <div class="mt-17 text-24">{{ ctc_user.c2cUserType ? $t('认证用户') : $t('未认证用户') }}</div>
          <div class="flex items-center mt-28 text-26">
            <div class="flex items-center h-30 mr-48">
              <span class="mr-12">{{ $t('邮箱') }}</span>
              <img class="w-20 h-20 relative top-4" v-if="party.email_authority" src="~@/assets/image/c2c/gou.png" alt="">
              <img class="w-20 h-20 relative top-4" v-else src="~@/assets/image/c2c/no.png" alt="">
            </div>
            <div class="flex items-center h-30 mr-48">
              <span class="mr-12">{{ $t('手机') }}</span>
              <img class="w-20 h-20 relative top-4" v-if="party.phone_authority" src="~@/assets/image/c2c/gou.png" alt="">
              <img class="w-20 h-20 relative top-4" v-else src="~@/assets/image/c2c/no.png" alt="">
            </div>
            <div class="flex items-center h-30 mr-48">
              <span class="mr-12">{{ $t('身份') }}</span>
              <img class="w-20 h-20 relative top-4" v-if="party.kyc_authority" src="~@/assets/image/c2c/gou.png" alt="">
              <img class="w-20 h-20 relative top-4" v-else src="~@/assets/image/c2c/no.png" alt="">
            </div>
            <div class="flex items-center h-30 mr-48">
              <span class="mr-12">{{ $t('高级认证') }}</span>
              <img class="w-20 h-20 relative top-4" v-if="party.kyc_highlevel_authority" src="~@/assets/image/c2c/gou.png"
                alt="">
              <img class="w-20 h-20 relative top-4" v-else src="~@/assets/image/c2c/no.png" alt="">
            </div>
          </div>
        </div>
      </div>
    </div>
    <div ref="content" class="content absolute left-0 bottom-0 w-full bg-white pt-40 box-border overflow-auto">
      <div class="pr-50 pl-50">
        <div class="flex" style="color: #868D9A">
          <div class="flex-1">
            <h2 class="mb-18 font-bold text-40 text-black">{{ ctc_user.thirtyDaysOrder }}</h2>
            <div class="text-28">{{ $t('30日成单数') }}</div>
          </div>
          <div class="flex-1">
            <h2 class="mb-18 font-bold text-40 text-black">{{ ctc_user.thirtyDaysOrderRatio }}%</h2>
            <div class="text-28">{{ $t('30日成单率') }}</div>
          </div>
        </div>
        <div class="w-full mt-54">
          <van-cell-group class="w-full text-24 box-border">
            <van-cell class="mb-28" title="平均放行" :value="ctc_user.thirtyDaysPassAverageTime" />
            <van-cell title="平均付款" :value="ctc_user.thirtyDaysPayAverageTime" />
          </van-cell-group>
        </div>
      </div>

      <div class="flex items-center justify-center mt-30 more pb-32 mb-48" @click="enterMore">
        <span class="text-26 mr-12">{{ $t('更多数据') }}</span>
        <span><van-icon name="arrow" color="#868D9A" /></span>
      </div>

      <div class="text-28">
        <van-cell-group class="w-full text-24 box-border pr-50 pl-50">
          <user-cell :left-img="require('@/assets/image/c2c/group287.png')" title="收款方式" :value="payMethodNum || '--'"
            @cell-click="$router.push({
              path: '/cryptos/paymentMethod',
              query: {
                ctc_user: JSON.stringify(ctc_user)
              }
            })" />
          <user-cell class="mt-100" :left-img="require('@/assets/image/c2c/Subtract.png')" title="C2C帮助中心" @cell-click="$router.push({
            path: '/cryptos/c2cHelpCenter'
          })" />
          <user-cell class="mt-100" :left-img="require('@/assets/image/c2c/group288.png')" title="C2C通知设置"
            @cell-click="enterSettings" />
          <!--是接单模式的情况下-->
          <template v-if="orderMode">
            <user-cell class="mt-100" :left-img="require('@/assets/image/c2c/Subtract-2.png')" title="成为认证广告方"
              @cell-click="$router.push({
                path: '/cryptos/CertifiedAdvertiser'
              })" />
            <user-cell class="mt-100" :left-img="require('@/assets/image/c2c/Subtract-3.png')" title="添加C2C到桌面" />
          </template>
        </van-cell-group>
      </div>
      <div class="mt-40 px-34 mb-74" v-if="orderMode">
        <van-button class="w-full rounded-lg" @click="switchPlaceAnOrder" type="info"
          color="#1D91FF">切换到“下单模式”</van-button>
      </div>
    </div>
  </div>
</template>

<script>
import {
  mapState
} from "vuex";
import { SET_ORDER_MODE } from "@/store/const.store";
import { Cell, CellGroup, Icon, Button } from 'vant';
import OrderNav from "@/components/Transform/order-nav/OrderNav.vue";
import UserCell from "@/views/cryptos/c2cUser/components/UserCell.vue";
import otcApi from "@/service/otc.api";

export default {
  name: "c2cUser",
  data() {
    return {
      email: "cn*****.com",
      ctc_user: {},
      party: {},
      payMethodNum: '',
    }
  },
  created() {
    this.ctcUserGetUserCenter()
  },
  methods: {
    ctcUserGetUserCenter() { // 获取页面信息
      otcApi.ctcUserGetUserCenter({ language: this.$i18n.locale }).then(res => {
        const { c2c_user, party } = res || {}
        this.ctc_user = c2c_user // 用户信息
        this.party = party // 验证信息
        otcApi.ctcPaymentMethodType({ party_id: this.ctc_user.c2cUserPartyId, language: this.$i18n.locale }).then(res => {
          console.log(res)
          this.payMethodNum = Object.keys(res).length
        })
      })
    },
    enterMore() {
      let ctc_user = JSON.stringify(this.ctc_user)
      this.$router.push({ path: '/c2cUser/c2cUserMore', query: { ctc_user } })
    },
    // 进入通知设置
    enterSettings() {
      let ctc_user = JSON.stringify(this.ctc_user)
      this.$router.push({ path: '/c2cNoticeSettings', query: { ctc_user } })
    },
    switchPlaceAnOrder() {
      this.$router.replace({
        path: '/cryptos/wantBuy'
      })

      this.$store.commit(`c2c/${SET_ORDER_MODE}`, {
        state: false,
      })
    },
    handleGoUserHome() {
      console.log(this.ctc_user.id)
      this.$router.push({
        path: '/cryptos/advertiserDetail',
        query: {
          uid: this.ctc_user.id
        }
      })
    },
  },
  computed: {
    ...mapState('c2c', ['orderMode'])
  },
  components: {
    [Cell.name]: Cell,
    [CellGroup.name]: CellGroup,
    [Icon.name]: Icon,
    [Button.name]: Button,
    OrderNav,
    UserCell,
  }
}
</script>

<style lang="scss" scoped>
.c2c_user {
  width: 100%;
  box-sizing: border-box;

  :deep(.van-nav-bar) {
    background: #1D91FF;

    &::after {
      content: none;
    }

    .van-icon {
      color: #fff;
    }
  }
}

.top {
  height: 370px;
  background: #1D91FF;


  .avatar {
    border-radius: 50%;
    background: #21262F;
    color: #fff;
  }
}

.content {
  top: 300px;
  border-radius: 70px 70px 0 0;

  .more {
    position: relative;
    border-bottom: 1px solid #484756;
    color: #868D9A;

    .van-icon {
      font-size: 20px;
      font-weight: 600;
    }
  }
}
</style>
