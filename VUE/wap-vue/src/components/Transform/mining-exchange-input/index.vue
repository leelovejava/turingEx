<template>
  <div id="cryptos">
    <div class="flex mt-8 items-center">
      <p class="text-28 flex-1 textColor">{{ title }}</p>
      <!-- <div class="money-bag" v-if="isMoney">
        现货钱包
        <img src="@/assets/image/exchange/icon_4.png" alt="" class="w-25 h-25" />
      </div> -->
    </div>
    <div style="box-sizing: border-box"
      class="w-full rounded mt-5 px-3 flex items-center box-border py-3 inputBackground">
      <div class="flex border-r-gray border-white items-center" @click="onShowActionSheet">
        <img :src="`${HOST_URL}/symbol/${type == 1 ? iconImg1 : iconImg2}.png`" alt="logo"
          class="rounded-full mr-5 currency-icon" />
        <span class="text-26 mr-5 w-16 textColor">{{ type == 1 ? coin.toUpperCase() : coin1.toUpperCase() }}</span>
        <img src="./icon-arrow.png" alt="logo" class="icon-arrow" />
      </div>
      <div class="input-wrap flex justify-between flex-1 items-center ml-3">
        <input placeholder="" v-if="type == 1" type="number" class="h-14 pl-3 border-none inputBackground textColor"
          v-model="oneValue" @input="onInput" :disabled="disabled" />
        <input placeholder="" v-if="type == 2" type="number" class="h-14 pl-3 border-none inputBackground textColor"
          :value="value" @input="onInput" :disabled="disabled" />
      </div>
      <div v-if="showMax" class="text-blue max-title pr-5" @click="onMax">{{ $t('最大') }}</div>
    </div>
    <div class="flex justify-between text-26 mt-4 text-grey">
      <p v-for="(tip, index) in tips" :key="index">{{ tip }}</p>
    </div>
    <!-- <van-action-sheet  v-model="show" :actions="actions" @select="onSelect" /> -->
    <van-popup v-model:show="show" position="bottom" class="mainBackground" :style="{ height: '80%' }">
      <div class="px-8 py-8 exchange-pop">
        <div class="tab-list flex">
          <div class="tab-item flex-1 textColor" :class="[activeIndex == 0 ? 'active' : '']" @click="checkActive(0)">
            <div class="mt-5 text-30">{{ $t('从') }}</div>
            <div class="flex items-center justify-center mt-5">
              <img :src="`${HOST_URL}/symbol/${iconImg1}.png`" alt="logo"
                class="w-8 h-8 rounded-full mr-5 currency-icon" />

              {{ coin.toUpperCase() }}
            </div>
          </div>
          <div class="tab-item flex-1 textColor" :class="[activeIndex == 1 ? 'active' : '']" @click="checkActive(1)">
            <div class="mt-5 text-30">{{ $t('至') }}</div>
            <div class="flex items-center justify-center  mt-5">
              <img :src="`${HOST_URL}/symbol/${iconImg2}.png`" alt="logo"
                class="w-8 h-8 rounded-full mr-5 currency-icon" />
              {{ coin1.toUpperCase() }}
            </div>
          </div>
        </div>
        <div class="title">{{ $t('闪兑至') }}</div>
        <div>
          <input v-model="searchValue" @input="searchFun" class="search-input" :placeholder="$t('搜索')" />
          <!-- <template slot="left-icon">
              <img src="@/assets/image/exchange/icon_5.png" alt="" class="w-40 h-40 mt-5" />
            </template> -->
          <!-- </van-field> -->
        </div>
        <div class="list mt-10">

          <div class="list-item flex mb-8" v-for="(item, index) in currencyList" :key="index" @click="onSelect(item)">
            <div class="flex-1 flex items-center">
              <div>

                <img :src="`${HOST_URL}/symbol/${item.symbol.toLowerCase()}.png`" alt="logo"
                  class="w-8 h-9 rounded-full mr-5 currency-icon" />
              </div>
              <div>
                <div class="item-title textColor">{{ item.symbol.toUpperCase() }}</div>
              </div>
            </div>
            <div class="text-right" v-if="activeIndex == 0">
              <div class="item-title textColor">{{ item.usable }}</div>
              <div class="item-text">${{ item.usdt }}</div>
            </div>
          </div>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script>
import { ActionSheet, Popup, Field } from "vant";
import { HOST_URL } from '@/config'
// import { mapGetters } from "vuex";
export default {
  name: "MiningExchangeInput",
  components: {
    [ActionSheet.name]: ActionSheet,
    [Popup.name]: Popup,
    [Field.name]: Field,
  },
  computed: {
    // ...mapGetters({coinList: 'home/coinList'}),
    // actions() { // 下拉列表
    //   const list = this.coinList.slice(0)
    //   list.map(item => {
    //     item.name = item.symbol.toUpperCase()
    //   })
    //   list.unshift({symbol: 'usdt', name: 'USDT'})
    //   return list
    // }
  },
  created() {
    this.currencyList = this.actions
    this.allCurrencyList = this.actions
    console.log(this.iconImg1, this.iconImg2)
  },
  props: {
    actions: {
      type: Array,
      default() {
        return []
      }
    },
    title: {
      type: String,
      default: "",
    },
    disabled: {
      type: Boolean,
      default: false
    },
    value: {
      type: [Number, String],
      default: 0,
    },
    getval: {
      type: String,
      default: ''
    },
    coin: {
      type: String,
      default: "user",
    },
    coin1: {
      type: String,
      default: "AAPL",
    },
    showMax: {
      type: Boolean,
      default: true,
    },
    tips: {
      type: Array,
      default() {
        return [];
      },
    },
    type: {
      type: Number,
      default: 1,
    },
    value1: {
      type: [Number, String],
      default: 0,
    },
    iconImg2: {
      type: String,
      default: "",
    },
    iconImg1: {
      type: String,
      default: "",
    },
  },
  watch: {
    actions(val) {
      this.currencyList = val
      this.allCurrencyList = val
    },
    value1(val) {
      this.oneValue = val
    }
  },
  data() {
    return {
      HOST_URL,
      show: false,
      searchValue: '',
      activeIndex: 0,
      currencyList: [],
      allCurrencyList: [],
      oneValue: ''
    };
  },
  methods: {
    onInput(e) { // 响应外层v-models
      // console.log(e.target.value)
      this.$emit('input', e)
    },
    onMax() {
      this.$emit('max')
    },
    onShowActionSheet() {
      if (this.type == 1) {
        this.activeIndex = 0
      } else {
        this.activeIndex = 1
      }
      if (this.actions.length) {
        this.show = true;
      }
    },
    onSelect(e) { // 选中
      let obj = {
        item: e,
        type: this.activeIndex
      }
      this.searchValue = ''
      this.$emit('select', obj)
      this.show = false;
    },
    checkActive(val) {
      this.activeIndex = val
    },
    //搜索
    searchFun() {
      if (this.searchValue) {
        this.currencyList = this.currencyList.filter((item) => {
          return item.symbol.toUpperCase().indexOf(this.searchValue.toUpperCase()) != -1
        })
      } else {
        this.currencyList = this.allCurrencyList
      }
    }
  },
};
</script>
<style lang="scss" scoped>
#cryptos {

  font-size: 30px;

  .input-wrap {
    border-left: 1px solid $input-border;
    padding-left: 30px;
  }

  .van-action-sheet__item {
    background: $input_background;
    color: $text_color;
  }



  .money-bag {
    color: #818181;
  }


  .exchange-pop {
    .tab-list {
      background: $exchange-bg;

      border-radius: 140px;
      height: 180px;
      color: $text_color;

      .tab-item {
        text-align: center;
      }

      .active {
        background: $tab_active;

        border-radius: 140px;
      }
    }

    .title {
      color: $text_color;
      padding: 30px 0 !important
    }

    .search-input {
      width: 100%;
      color: $text_color;
      background: $input_background;
      border-radius: 80px;
      height: 100px !important;
      display: flex;
      align-items: center;
      padding-left: 30px !important;

      :deep(.van-field__control) {
        color: $text_color;
      }
    }

    .list {
      color: $text_color;

      .list-item {

        .item-title {
          font-size: 30px;
          font-weight: bold;
        }

        .item-text {
          color: $text_color1;
          font-size: 24px;
          margin-top: 5px;
        }
      }
    }
  }

  // :deep(.van-field__control) {
  //   color: #747A8F;
  // }

  .currency-icon {
    width: 60px !important;
    height: 60px !important;
  }

  .icon-arrow {
    width: 30px !important;
    height: 20px !important;
    margin-right: 10px !important;
  }

  .h-40 {
    height: 100px !important;
  }

  .w-62 {
    width: 150px !important;
  }

  .max-title {
    min-width: 100px;
    text-align: right;
  }
}
</style>
