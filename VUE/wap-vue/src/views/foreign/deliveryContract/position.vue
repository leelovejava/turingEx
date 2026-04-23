<template>
  <div class="position">
    <div class="position-padding" v-for="item in listData" :key="item.order_no">
      <div class="position-tag ">
        <div class="position-tag-style" :class="item.direction === 'buy' ? 'green' : 'red'">
          {{ item.direction === 'buy' ? $t('开多') : $t('开空') }}</div>
        <div class="position-tag-title textColor">{{ item.name }} {{ $t('delivery') }}</div>
        <div class="position-tag-title2">{{ $t('全仓') }}</div>
        <img class="position-tag-img" src="@/assets/image/public/Group1042.png" />
      </div>
      <div class="position-div1">
        <span class="position-text1">{{ $t('number') }}</span>
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
        <span class="position-text1">{{ $t('buyPrice') }}（USD）</span>
        <span class="position-text2 textColor">{{ item.open_price }}</span>
      </div>
      <div class="position-div1">
        <span class="position-text1">{{ $t('结算价格') }}（USD）</span>
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
        <span class="position-text2 textColor">{{ item.close_time ? dayjs(item.close_time * 1000).format('YYYY-MM-DD HH:mm:ss') : '--' }}</span>
      </div>
      <div class="position-div1">
        <span class="position-text1">{{ $t('操作') }}</span>
        <span class="position-text2 colorMain" @click="onDetail(item)">{{ $t('详情') }}</span>
      </div>
      <div style="margin-top: 17px;width: 100%;height: 1px;float: left;" class="bgDark"></div>
    </div>
    <van-popup round v-model:show="show" teleport="body">
      <popup-delivery showBtns :detailData="detailData" :key="detailData.order_no" @close="show = false"
        :disabled="true" />
    </van-popup>
  </div>
</template>
<script setup>
import { Tab, Tabs, Popup } from 'vant';
import PopupDelivery from '@/components/foreign/foreign-popup-delivery/index.vue'
import { ref } from 'vue';
import dayjs from 'dayjs'
const props = defineProps({
  listData: {
    type: Array,
    default() {
      return []
    }
  }
})
let active = ref(2)
let show = ref(false)
let detailData = ref({})

const onDetail = (item) => { // 详细界面
  show.value = true
  detailData.value = item
}
</script>
<style lang="scss" scoped>
.position {
  width: 100%;
  overflow: auto;
  font-size: 14px;

  .position-tab-flex {
    width: 100%;
    display: flex;

    .position-tab {
      flex: 1;
    }

    .position-tab-icon {
      width: 19px;
      height: 19px;
      margin-right: 15px;
      margin-top: 11px;
    }
  }

  .position-padding {
    //border-bottom: 1px solid #F2F4F9;
    padding: 21px 0 0 0;
    box-sizing: border-box;
    width: 100%;
    overflow: auto;

    .position-tag {
      display: flex;
      align-items: center;

      .position-tag-style {
        padding: 4px 14px;
        margin-right: 11px;
        font-style: normal;
        font-weight: 400;
        font-size: 12px;
        color: $main-btn-color;
      }

      .position-tag-title {
        font-style: normal;
        font-weight: 600;
        font-size: 15px;
        margin-right: 7px;
      }

      .position-tag-title2 {
        font-style: normal;
        font-weight: 400;
        font-size: 15px;
        color: $text_color5;
      }

      .position-tag-img {
        margin-left: 11px;
        width: 20px;
        height: 15px;
      }
    }

    .red {
      background: $red;
      border-radius: 4.6266px;
    }

    .green {
      background: $green;
      border-radius: 4.62453px;
    }

    .position-div1 {
      margin-top: 17px;
      width: 100%;
      float: left;

      .position-text1 {
        float: left;
        font-style: normal;
        font-weight: 400;
        font-size: 15px;
        color: $text_color5;
      }

      .position-text2 {
        float: right;
        font-style: normal;
        font-weight: 400;
        font-size: 15px;
      }

      .position-text3 {
        font-style: normal;
        font-weight: 400;
        font-size: 15px;
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

.bgDark {
  background: $border_color !important;
}
</style>
