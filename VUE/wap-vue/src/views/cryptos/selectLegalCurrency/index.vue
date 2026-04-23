<template>
  <div id="cryptos">
    <div style="display: flex;
        flex-direction: column;
        align-items: center;
        width: 100%;
        height: 100%; 
      ">
      <div class="top w-full">
        <order-nav class="mb-5">
          <template #title>
            {{ $t('选择法币') }}
          </template>
        </order-nav>
        <div class="pl-8 pr-8 pb-26" @click="showSearch = true">
          <buy-input :isReadonly="true" :placeholder="$t('请输入法币')" />
        </div>
      </div>
      <!-- <div style="width: 100%">
      <p class="jiao">
        <span>{{$t('您可交易的法币')}}</span>
        <img class="tan" src="@/assets/image/tan.png" alt="" />
      </p>

      <div class="ke">
        <p class="ke-li">
          <span class="yang">￥</span>
          <span>CNY</span>
        </p>
      </div>
    </div> -->
      <div style="width: 100%">
        <p class="jiao">{{ $t('全部法币') }}</p>
      </div>
      <div style="width: 100%; flex: 1; overflow: auto">
        <van-index-bar :sticky-offset-top="100" :sticky="false">
          <div v-for="(item, index) in items" :key="index">
            <van-index-anchor class="pt-2 pb-2" :index="item.title" />
            <div class="payment_method_cell flex items-center ml-10 pt-2 pb-2" v-for="(label, labelIndex) in item.labels"
              :key="labelIndex" @click="onSelectCurrency(label)">
              <span class="first">{{ label.fLabel }}</span>
              <van-cell :title="label.label" />
            </div>
          </div>
        </van-index-bar>
      </div>

      <div v-show="showSearch" class="fixed-search">
        <div class="search pb-6 flex justify-between items-center">
          <buy-input v-model="searchValue" :placeholder="$t('请输入法币')" @input="inputChange" />
          <span class="close-q" @click="showSearch = false">{{ $t('取消') }}</span>
        </div>
        <div class="search-main">
          <div v-if="searchList.length === 0" class="clear">
            <p>{{ $t('历史搜索') }}</p>
            <p>{{ $t('没有历史记录') }}</p>
          </div>
          <div v-else class="search-li">
            <p v-html="filterStr(item.label)" v-for="(item, index) in searchList" :key="index"
              @click="onSelectCurrency(item)"></p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { IndexBar, IndexAnchor, Cell } from 'vant'
import OrderNav from '@/components/Transform/order-nav/OrderNav.vue'
import BuyInput from '../placeAnOrder/components/buyInput/BuyInput.vue'
import { mapActions, mapGetters, mapMutations } from 'vuex'

export default {
  name: 'SelectLegalCurrency',
  data() {
    return {
      showSearch: false,
      searchValue: '',
      list: [
        'A',
        'B',
        'C',
        'D',
        'E',
        'F',
        'G',
        'H',
        'I',
        'J',
        'K',
        'L',
        'M',
        'N',
        'O',
        'P',
        'Q',
        'R',
        'S',
        'T',
        'U',
        'V',
        'W',
        'X',
        'Y',
        'Z',
        '#',
      ],
      // items: [
      //   {
      //     title: 'A',
      //     labels: [
      //       { fLabel: 'ARS', label: 'ARS', type: 'EN' }, // type 国外、国内标识
      //       { fLabel: 'A', label: 'AUD', type: 'EN' },
      //     ],
      //   },
      //   {
      //     title: 'B',
      //     labels: [
      //       { fLabel: 'B', label: 'BOB', type: 'EN' }, // type 国外、国内标识
      //     ],
      //   },
      // ],
      searchList: [],
    }
  },
  async created() {
    await this.SET_FIAT_LIST()
    // console.log(this.fiatList)
  },
  computed: {
    ...mapGetters('c2c', ['fiatList']),
    items() {
      return this.fiatList
    }
  },
  methods: {
    ...mapActions('c2c', ['SET_FIAT_LIST']),
    ...mapMutations('c2c', ['SET_CURRENCY', 'SET_CURRENCY_SYMBOL']),
    filterStr(val) {
      return this.heightLight(val, this.searchValue)
    },
    heightLight(str, key) {
      const reg = new RegExp(key, 'ig')
      return str.replace(reg, (val) => {
        return `<span style="color:#1D91FF">${val}</span>`
      })
    },
    inputChange(val) {
      let arr = []
      this.items.forEach((item) => {
        arr = arr.concat(item.labels)
      })
      console.log('arr', arr, val)
      if (val) {
        // this.searchList = arr.filter((item) =>  item.label.indexOf('C') !== -1)
        this.searchList = arr.filter((item) => {
          return item.label.indexOf(val.toUpperCase()) != -1
        })
      } else {
        this.searchList = []
      }
      console.log('searchList', this.searchList)
    },
    onSelectCurrency(item) {
      console.log('item', item)
      this.SET_CURRENCY(item.currency)
      this.SET_CURRENCY_SYMBOL(item.currency_symbol)
      this.$router.go(-1)
    }
  },
  components: {
    [IndexBar.name]: IndexBar,
    [IndexAnchor.name]: IndexAnchor,
    [Cell.name]: Cell,
    OrderNav,
    BuyInput,
  },
}
</script>

<style lang="scss" scoped>
#cryptos {

  :deep(.van-index-bar__sidebar) {
    display: none;
  }

  .top {
    position: relative;
    top: 0;
    left: 0;
  }

  .payment_method_cell {
    position: relative;

    &:after {
      position: absolute;
      box-sizing: border-box;
      content: ' ';
      pointer-events: none;
      right: 16px;
      bottom: 0;
      left: 16px;
      border-bottom: 1px solid $line_color;

      -webkit-transform: scaleY(0.5);
      transform: scaleY(0.5);
    }

    :deep(.van-index-anchor) {
      font-size: 36px;
      font-weight: 500;
      color: #868d9a;
    }

    :deep(.van-cell__title) {
      font-size: 32px;
    }

    :deep(.van-cell) {
      padding-left: 8px;
    }

    :deep(.van-cell) {
      padding-left: 8px;
      color: $text_color2;
      background: $tab_background;
    }

    :deep(.van-index-bar__sidebar) {
      color: #7cbfff;

      span {
        margin-bottom: 4px;
        font-size: 26px;
      }
    }
  }



  .jiao {
    color: #868d9a;
    font-size: 32px;
    padding: 40px 0 20px 0;
    margin-left: 30px;
    border-bottom: 1px solid $line_color;
    text-align: left;
    display: flex;
    align-items: center;

    .tan {
      margin-left: 12px;
      width: 24px;
      height: 24px;
    }
  }

  .ke {
    padding: 30px;

    .ke-li {
      padding: 30px 70px;
      font-size: 28px;
      color: #21262f;

      .yang {
        margin-right: 38px;
        color: #868d9a;
      }
    }
  }

  .first {
    color: #868d9a;
    margin-right: 40px;
    width: 80px;
    text-align: right;
    font-size: 30px;
  }

  .fixed-search {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 100000;

    .search {
      padding: 24px 30px;
    }

    .search-main {
      min-height: calc(100% - 72px);
      background: $main_background;
      padding: 0 30px;

      .search-li {
        padding-left: 70px;
        color: $text_color;

        p {
          height: 100px;
          line-height: 100px;
          border-bottom: 1px solid #484756;
        }
      }
    }
  }

  .close-q {
    display: inline-block;
    width: 80px;
    margin-left: 29px;
    color: #9ca2ad;
    font-size: 30px;
  }

  .clear {}

  .clear> :first-child {
    font-size: 26.5px;
    margin-bottom: 116px;
    color: $text_color;
  }

  .clear> :last-child {
    text-align: center;
    color: #868d9a;
    font-size: 28px;
  }
}
</style>
