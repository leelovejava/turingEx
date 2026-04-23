<template>
  <div id="EnterTradePagePop">
    <div class="py-12 px-14">
      <order-nav>
        <template #left><i></i></template>
        <template #title>
          <div class="header-box">
            <span v-if="type === 'buy'" class="text-36 header-title">{{ $t('确认购买') }}</span>
            <span v-else class="text-36 header-title">{{ $t('确认出售') }}</span>
          </div>
        </template>
        <template #default>
          <van-icon @click="$emit('close')" name="cross" class="font-semibold" color="#868D9A" />
        </template>
      </order-nav>
      <div class="text-center">
        <div v-if="type === 'buy'">
          <div v-if="info.type === 'Amount'">
            <div class="flex items-end justify-center font-bold">
              <span class="relative mr-4 text-40">{{ currencySymbol }}</span>
              <span class="text-50">{{ fullTotalPrice }}</span>
            </div>
            <div class="mt-4 text-28">{{ $t('我将收到') }}&nbsp;<span style="color:#2C78F8;">{{ fullNum
            }}</span>&nbsp;{{ symbol }}</div>
          </div>
          <div v-else>
            <div class="flex items-end justify-center font-bold">
              <span class="relative mr-4 text-40">{{ currencySymbol }}</span>
              <span class="text-50">{{ fullTotalPrice }}</span>
            </div>
            <div class="mt-4 text-28">{{ $t('我将收到') }}&nbsp;<span style="color:#2C78F8;">{{ fullNum
            }}</span>&nbsp;{{ symbol }}</div>
          </div>
        </div>
        <div v-else>
          <div v-if="info.type === 'Amount'">
            <div class="flex items-end justify-center font-bold">
              <span class="relative mr-4 text-40">{{ currencySymbol }}</span>
              <span class="text-52">{{ fullTotalPrice }}</span>
            </div>
            <div class="mt-4 text-28">{{ $t('我将出售') }}&nbsp;<span style="color:#2C78F8;">{{ fullNum
            }}</span>&nbsp;{{ symbol }}</div>
          </div>
          <div v-else>
            <div class="flex items-end justify-center font-bold">
              <span class="relative mr-4 text-40">{{ currencySymbol }}</span>
              <span class="text-52">{{ fullTotalPrice }}</span>
            </div>
            <div class="mt-4 text-28">{{ $t('我将出售') }}&nbsp;<span style="color:#2C78F8;">{{ fullNum
            }}</span>&nbsp;{{ symbol }}</div>
          </div>
        </div>
      </div>
      <div class="mt-16">
        <div class="text-grey">{{ $t('选择支付方式') }}</div>
        <div class="mt-4">
          <div
            class="relative flex items-center justify-between mb-5 pl-9 pr-10 py-7 bg-f5 rounded-xl text-28 box-border bank-item"
            :class="{ 'active': active === index }" @click="handleClick(index, item)"
            v-for="(item, index) in paymentMethod" :key="index">
            <div class="flex items-center h-8">
              <img :src="item.method_img_path" alt="" class="w-10 h-10 mr-5" />
              <span>{{ $t(item.method_name) }}</span>
            </div>
            <div>
              <p>{{ currencySymbol }} {{ item.advert_price }}</p>
              <p class="mt-3 text-24" style="color: #1A6EBD" v-if="goodPrice">{{ $t('价格最优') }}</p>
            </div>
            <img v-show="active === index" class="absolute right-0 top-0 w-80 h-28" src="@/assets/image/c2c/Group317.png"
              alt="">
          </div>
          <!-- <div
            class="relative flex justify-between pl-36 pr-62 py-28 bg-f5 rounded-xl text-28 box-border bank-item"
            :class="{'active': active === 1}"
            @click="handleClick(1)"
        >
          <div class="flex items-center">
            <span class="w-5 h-21 mr-13 rounded-2xl" style="background: #4BA6EB"></span>
            <span>{{$t(paymentMethod[1].methodName)}}</span>
          </div>
          <div>
            <p>¥ 6.73</p>
          </div>
          <img v-show="active === 1" class="absolute right-0 top-0 w-310 h-110"
               :src="require('@/assets/image/c2c/Group317.png')" alt="">
        </div> -->
        </div>
      </div>
      <div>
        <van-button v-if="type === 'buy'" class="w-full h-20 mt-14 text-32 rounded-xl" type="primary" color="#2EBD85"
          @click="trade">{{ $t('确认购买') }}
        </van-button>
        <van-button v-else class="w-full h-20 mt-14 text-32 rounded-xl" type="primary" color="#E35461" @click="trade">{{
          $t('确认出售') }}
        </van-button>
      </div>
    </div>
  </div>
</template>

<script>
import {
  Icon,
  Button,
} from "vant";
import OrderNav from "@/components/Transform/order-nav/OrderNav.vue";
import otcApi from "@/service/otc.api.js";
import { mapGetters } from "vuex";

export default {
  name: "EnterTrade",
  props: ['type', 'paymentMethod', 'info', 'sessionToken', 'symbol', 'exchangeCurrency'],
  data() {
    return {
      advert_price: '', // 测试单价
      active: 0,
      goodPrice: true,
      advert_id: ''
    }
  },
  watch: {
    paymentMethod: {
      handler: function (val) {
        this.advert_price = this.paymentMethod[this.active].advert_price
        this.advert_id = this.paymentMethod[this.active].advert_id
      },
      immediate: true
    }
  },
  methods: {
    handleClick(num, item) {
      this.active = num;
      this.advert_price = item.advert_price
      this.advert_id = item.advert_id
    },
    async trade() {
      const params = {
        session_token: this.sessionToken,
        payment_method_id: this.paymentMethod[this.active].payment_method_id,
        order_type: this.info.type === 'Amount' ? 'by_amount' : 'by_num',
        amount: this.fullTotalPrice,
        coin_amount: this.fullNum,
        direction: this.type,
        currency: this.exchangeCurrency,
        symbol: this.symbol.toLowerCase(),
        remark: '',
        c2c_advert_id: this.advert_id
      }

      otcApi.ctcOrderOpenQuick(params).then(res => {

        this.$store.commit('c2c/SET_ORDER_NO', res.order_no)
        if (this.type == 'buy') {
          this.$router.push('/cryptos/orderGeneration')
        } else {

          this.$router.push('/cryptos/sellGenerate')
        }
      }).catch(err => {
        // if(err.code==1){
        //   this.$emit('updataToken',true)
        // }
      })
    }
  },
  computed: {
    ...mapGetters("c2c", ["currencySymbol"]),
    fullTotalPrice() {
      if (this.info.type === 'Amount') {
        return this.info.num
      } else {
        console.log((this.info.num * 1))
        console.log(this.advert_price)
        return ((this.info.num * 1) * this.advert_price).toFixed(2)
      }
    },
    fullNum() {
      if (this.info.type === 'Amount') {
        return ((this.info.num * 1) / this.advert_price).toFixed(4)
      } else {
        return this.info.num
      }
    }
  },
  components: {
    [Icon.name]: Icon,
    [Button.name]: Button,
    OrderNav,
  }
}
</script>

<style lang="scss" scoped>
#EnterTradePagePop {
  font-size: 30px;

  .header-box {
    color: $text_color;
  }

  .bank-item {
    border-width: 1px;
    border-style: solid;
    border-color: #F5F5F5;
    background: $main_background;
    color: $text_color;
    border-color: $line_color;

    img {
      right: -1px;
      top: -1px;
    }
  }

  .active {
    border-color: #1D91FF !important;
  }

  .text-center {
    color: $text_color;
  }
}
</style>
