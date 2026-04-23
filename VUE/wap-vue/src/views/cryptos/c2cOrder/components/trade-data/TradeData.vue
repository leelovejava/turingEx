<template>
  <div id="cryptos">
    <div>
      <div class="relative px-8 order-msg mainBackground" :class="{ 'hide': !isShow && detail.sellName }">
        <order-data class="w-full" :detail="detail">
          <slot name="divider"></slot>
        </order-data>
        <van-icon v-if="detail.sellName" @click="show" name="arrow-down" class="absolute bottom-3 font-bold text-grey"
          :class="{ 'rotateZ': isShow }" />
      </div>
      <div class="h-4 diviLine"></div>
      <div class="px-8 pt-9 mainBackground">
        <van-collapse v-model="activeNames">
          <van-collapse-item name="1">
            <template #title>
              <div>
                <p class="text-30 c2cColor">
                  <slot name="trade-title"></slot>
                </p>
                <van-divider />
                <div class="flex items-center mb-7">
                  <div class="w-2 h-8 rounded-xl mr-5" style="background: #E7BB41;"></div>
                  <span class="ml-2 text-30 c2cColor">{{ $t(detail.methodName) }}</span>
                </div>
              </div>
            </template>
            <van-cell-group class="payment-method">
              <van-cell v-for="(item, index) in detail.tradeMethod" :key="index" :title="item.label"
                :value="item.value" />
            </van-cell-group>
          </van-collapse-item>
        </van-collapse>
      </div>
      <div class="h-4 diviLine"></div>
      <div class="p-8 mainBackground">
        <slot name="terms"></slot>
      </div>
    </div>
  </div>
</template>

<script>
import { Icon, Collapse, CollapseItem, Cell, CellGroup, Divider } from "vant"
import OrderData from "@/views/cryptos/c2cOrder/components/order-data/OrderData.vue";
export default {
  name: "TradeData",
  // props: ['title', 'count', 'totalPrice', 'createOrderTime', 'orderNumber', 'sellName', 'clientType','tradeMethod','methodName','unitPrice'],
  props: {
    detail: {
      type: Object,
      default() {
        return {}
      }
    }
  },
  data() {
    return {
      isShow: false,
      activeNames: [],
    }
  },
  methods: {
    show() {
      this.isShow = !this.isShow;
    }
  },
  components: {
    [Icon.name]: Icon,
    [Collapse.name]: Collapse,
    [CollapseItem.name]: CollapseItem,
    [Cell.name]: Cell,
    [CellGroup.name]: CellGroup,
    [Divider.name]: Divider,
    OrderData,
  }
}
</script>

<style lang="scss" scoped>
#cryptos {
  font-size: 30px;

  :deep(.mainBackground) {


    .van-cell-group,
    .van-cell {
      color: $text_color1;
      background: $main_background;
    }

    .van-cell__value {
      color: $text_color;
    }

    .order-msg {
      transition: all .5s ease;
      overflow: hidden;

      .van-icon {
        left: 50%;
        transform: translateX(-50%);
        transition: all .5s ease;
      }
    }

    .payment-method {
      .van-cell {
        margin-bottom: 50px;
        font-size: 28px;

        &:last-child {
          margin-bottom: 30px;
        }

        .van-cell__title {
          color: #8A919E;
        }
      }
    }



    .rotateZ {
      transform: translateX(-50%) rotateZ(180deg) !important;
    }

    .hide {
      height: 365px;
    }

    .van-collapse-item__content {
      padding: 0;
      font-size: 28px;
      color: $c2c_color;
      background: $main_background;
    }

    .van-collapse-item {
      .van-cell__title {
        font-size: 28px;
        color: $c2c_color;
      }

      .van-divider {
        margin: 28px 0;
        border-color: transparent !important;
      }
    }
  }
}
</style>