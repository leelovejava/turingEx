<template>
  <div id="cryptos">
    <div id="full" class="adScreening">
      <div class="flex box-border items-center justify-between px-8 w-full mt-6 text-36 font-bold text-center">
        <div></div>
        <span class="c2cColor">{{ $t('广告筛选') }}</span>
        <van-icon class="c2cColor" name="cross" @click="handlerBack" />
      </div>
      <div class="text-28 mb-9 text-grey ml-8 mt-10">{{ $t('总额') }}</div>
      <div class="px-8">
        <van-field v-model="amount" :size="large" :placeholder="$t('请输入总额')">
          <template #extra>
            <div class="tabBackground pr-8 box-border textColor">
              {{ exchangeCurrency }}
            </div>
          </template>
        </van-field>
      </div>
      <select-item class="mt-16" :title="$t('交易方式')" :options="fullPayMethods" :value="method_type"
        @input="changeMethod_type" />
      <select-item class="mt-16" :title="$t('国家/地区')" :options="countries" :value="country_code"
        @input="changeCountry_code" />
      <div class="px-8">
        <van-cell class="c2cColor" center :title="$t('仅显示商家发布的广告')">
          <template #right-icon>
            <van-switch v-model="checked" size="24" @click="showAd = !showAd" />
          </template>
        </van-cell>
        <van-collapse v-model="activeNames" v-show="showAd">
          <van-collapse-item>
            <template #title>
              <div class="flex items-center">
                <img class="w-6 h-6 mr-4" src="~@/assets/image/otc/wantBuyHead/tip_ico.png" alt="">
                <span class="mr-4 text-24 text-grey">{{ $t('广告筛选说明') }}</span>
                <van-icon name="arrow-down" class="block relative top-1 text-24 font-bold"
                  :class="{ 'rotateZ': activeNames.length > 0 }" />
              </div>
            </template>
            <template #right-icon><i></i></template>
            <template #default>
              <div class="text-28 c2cColor">
                <div><span>{{ $t('交易方式：') }}</span><span class="font-bold">{{ $t('仅显示可用的交易方式') }}</span></div>
                <div class="mt-2"><span>{{ $t('国家/地区') }}</span><span class="font-bold">{{ $t('仅显示可用的国家 / 地区') }}</span>
                </div>
              </div>
            </template>
          </van-collapse-item>
        </van-collapse>
        <div class="flex mt-14 mb-9 justify-center ">
          <button class="w-80 h-20 tabBackground c2cColor rounded-lg font-normal text-30 border-none mr-5"
            @click="onReset">{{ $t('重置') }}
          </button>
          <button class="w-80 h-20 btnMain rounded-lg font-normal text-30 border-none"
            :class="[{ 'btnMain': tabindex === 1 }, { 'text-white': tabindex === 1 }]" @click="onConfirm">{{ $t('确认') }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import SelectItem from "../components/selectItem/SelectItem.vue";
import { Cell, Icon, Popup, DropdownMenu, DropdownItem, Field, Switch, Collapse, CollapseItem } from 'vant';
import { mapGetters } from 'vuex'
import countries from '@/components/Transform/country-list/countryList.js'

export default {
  name: "adScreening",
  components: {
    [Icon.name]: Icon,
    [Field.name]: Field,
    [Popup.name]: Popup,
    [Cell.name]: Cell,
    [Switch.name]: Switch,
    [DropdownMenu.name]: DropdownMenu,
    [DropdownItem.name]: DropdownItem,
    [Collapse.name]: Collapse,
    [CollapseItem.name]: CollapseItem,
    SelectItem,
  },
  computed: {
    ...mapGetters('c2c', ['payMethods']),
    ...mapGetters('c2c', ['symbolList', 'payMethods', 'exchangeCurrency']),
    // 处理交易方式对象
    fullPayMethods() {
      const payMethods = [];
      Object.keys(this.payMethods).forEach((key) => {
        payMethods.push({
          method_type: key,
          title: this.payMethods[key]
        })
      })
      payMethods.unshift({
        method_type: '',
        title: this.$t('全部')
      })
      return payMethods;
    }
  },
  created() {
  },
  data() {
    return {
      countries, // 国家列表
      activeNames: [],
      amount: '',
      // 0 代表全部
      method_type: '', // 支付方式
      country_code: 99, // 国家
      tradeList: [
        {
          '': this.$t('全部'),
          // Bank_card: '银行卡',
          // Coin_collection: '币收款',
        }
      ],
      large: '80',
      checked: false,
      tabindex: 1,
      showAd: false,
    }
  },
  methods: {
    onChooseType(key) { // 选择支付方式
      this.method_type = key
      this.$refs.paymentMethod[0].toggle()
    },
    onSelectCountry(item) {
      this.country_code = item.code;
      this.$refs.country.toggle()
    },
    onReset() { // 重置
      this.amount = ''
      this.method_type = 99
      this.country_code = 99
    },
    onConfirm() { // 确认
      console.log(this.amount, this.method_type, this.country_code, this.checked)
      this.$emit('confirm', {
        amount: this.amount,
        method_type: this.method_type,
        country_code: this.country_code,
      })

      this.$emit('back')
    },
    handlerBack() {
      this.$emit('back')
    },
    changeMethod_type(val) {
      this.method_type = val
    },
    changeCountry_code(val) {
      this.country_code = val
    }
  }
}
</script>

<style lang="scss" scoped>
#cryptos {
  .active_trade {
    color: $main-btn-color;
    background: $color_main;
  }

  .rotateZ {
    transform: rotateZ(180deg);
  }


  :deep(.van-dropdown-menu__bar) {
    background: none;
    box-shadow: none;
  }

  :deep(.van-collapse-item__content) {
    padding: 0;
  }

  #full {
    :deep(.van-cell) {
      background-color: $main_background;

      &::after {
        border-bottom: 1px solid $divi_line;
      }
    }

    .van-switch {
      background: $btn_main;
    }

    :deep(.van-collapse-item__content) {
      background: $main_background;
    }

    :deep(.van-field__control) {
      color: $text_color;
    }
  }
}
</style>
