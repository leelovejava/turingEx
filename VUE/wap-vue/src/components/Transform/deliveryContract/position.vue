<template>
  <div id="#cryptos">
    <div class="position text-28">
      <div class="position-padding" v-for="item in listData" :key="item.order_no">
        <div class="position-tag ">
          <div class="position-tag-style" :class="item.direction === 'buy' ? 'green' : 'red'">
            {{ item.direction === 'buy' ? $t('开多') : $t('开空') }}</div>
          <div class="position-tag-title textColor">{{ item.name }} {{ $t('交割') }}</div>
          <div class="position-tag-title2">{{ $t('全仓') }}</div>
          <img class="position-tag-img" src="@/assets/image/deliveryContract/Group1042.png" />
        </div>
        <div class="position-div1">
          <span class="position-text1">{{ $t('数量') }}</span>
          <span class="position-text2 textColor">{{ item.volume }}</span>
        </div>
        <div class="position-div1">
          <span class="position-text1">{{ $t('方向') }}</span>
          <span class="position-text2" :class="item.direction === 'buy' ? 'color-green' : 'color-red'">
            {{ item.direction === 'buy' ? $t('开多') : $t('开空') }}</span>
        </div>
        <div class="position-div1">
          <span class="position-text1">{{ $t('交割时间') }}</span>
          <span class="position-text2 textColor">{{ item.time_num + item.time_unit }}</span>
        </div>
        <div class="position-div1">
          <span class="position-text1">{{ $t('购买价') }}( {{ routeType == 'cryptos' ? 'USDT' : 'USD' }})</span>
          <span class="position-text2 textColor">{{ item.open_price }}</span>
        </div>
        <div class="position-div1">
          <span class="position-text1">{{ $t('结算价') }}( {{ routeType == 'cryptos' ? 'USDT' : 'USD' }})</span>
          <span class="position-text2 textColor">{{ item.close_price }}</span>
        </div>
        <div class="position-div1">
          <span class="position-text1">{{ $t('盈亏') }}</span>
          <span class="position-text2" :class="item.profit > 0 ? 'color-green' : 'color-red'">
            {{ item.profit > 0 ? '+' + item.profit : item.profit }}
          </span>
        </div>
        <div class="position-div1">
          <span class="position-text1">{{ $t('到期时间') }}</span>
          <span class="position-text2 textColor"> {{ dayjs(item.close_time * 1000).format('YYYY-MM-DD HH:mm:ss') }}</span>
        </div>
        <div class="position-div1">
          <span class="position-text1">{{ $t('操作') }}</span>
          <span class="position-text2 colorMain" @click="onDetail(item)">{{ $t('详情') }}</span>
        </div>
        <div style="margin-top: 33px;width: 100%;height: 1px;float: left;" class="bgDark"></div>
      </div>

    </div>
    <van-popup v-model:show="show">
      <popup-delivery showBtns :detailData="detailData" :key="detailData.order_no" @close="show = false" :disabled="true"
        :price="price" />
    </van-popup>
  </div>
</template>
<script>
import { Tab, Tabs, Popup } from 'vant';
import PopupDelivery from '@/components/Transform/popup-delivery/index.vue'
import dayjs from 'dayjs'
export default {
  data() {
    return {
      active: 2,
      show: false,
      detailData: {},
      routeType: 'cryptos'
    }
  },
  props: {
    listData: {
      type: Array,
      default() {
        return []
      }
    },
    price: {
      type: [Number, String],
      default: '0.00'
    },
  },
  components: {
    [Tab.name]: Tab,
    [Tabs.name]: Tabs,
    [Popup.name]: Popup,
    PopupDelivery
  },
  mounted() {
    this.routeType = this.$route.query.type
  },
  methods: {
    onDetail(item) { // 详细界面
      this.show = true
      this.detailData = item
    },
    dayjs
  }
}
</script>
<style lang="scss" scoped >
#cryptos {
  font-size: 40px;

  // .van-popup {
  //   position: fixed;
  //   top: 30%;
  //   z-index: 10000;
  // }

  .position {
    width: 100%;
    overflow: auto;

    .position-tab-flex {
      width: 100%;
      display: flex;

      .position-tab {
        flex: 1;
      }

      .position-tab-icon {
        width: 38px;
        height: 38px;
        margin-right: 30px;
        margin-top: 22px;
      }
    }

    .position-padding {
      //border-bottom: 1px solid #F2F4F9;
      padding: 42px 0 0 0;
      box-sizing: border-box;
      width: 100%;
      overflow: auto;

      .position-tag {
        display: flex;
        align-items: center;

        .position-tag-style {
          padding: 11px 28px;
          margin-right: 22px;
          font-style: normal;
          font-weight: 400;
          font-size: 26.4981px;
          color: $main-btn-color;
        }

        .position-tag-title {
          font-style: normal;
          font-weight: 600;
          font-size: 30.9145px;
          margin-right: 13px;
        }

        .position-tag-title2 {
          font-style: normal;
          font-weight: 400;
          font-size: 30.9145px;
          color: $text_color5;
        }

        .position-tag-img {
          margin-left: 11px;
          width: 40px;
          height: 31px;
        }
      }

      .red {
        background: $red;
        border-radius: 6.6266px;
      }

      .green {
        background: $green;
        border-radius: 6.62453px;
      }

      .position-div1 {
        margin-top: 20px;
        width: 100%;
        float: left;

        .position-text1 {
          float: left;
          font-style: normal;
          font-weight: 400;
          font-size: 30px;
          color: $text_color5;
        }

        .position-text2 {
          float: right;
          font-style: normal;
          font-weight: 400;
          font-size: 30px;
        }

        .position-text3 {
          font-style: normal;
          font-weight: 400;
          font-size: 30px;
        }
      }
    }

    .color-red {
      color: $red !important;
    }

    .color-green {
      color: $green !important;
    }

    .color-blue {
      color: $color_main !important;
    }
  }
}
</style>
