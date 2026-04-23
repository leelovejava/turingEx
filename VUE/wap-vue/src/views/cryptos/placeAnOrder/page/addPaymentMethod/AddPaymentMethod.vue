<template>
  <div id="AddPaymentMethodPage">
    <div class="AddPaymentMethod"
      style="display: flex; flex-direction: column;align-items: center; width: 100%;height: 100%">
      <div class="top w-full">
        <order-nav class="mb-55">
          <template #title>
            {{ $t('全部收款方式') }}
          </template>
        </order-nav>
        <div class="pl-8 pr-8 pb-6">
          <buy-input v-model="select" @input="inputValue" :placeholder="$t('请输入收款方式')" />
        </div>
      </div>
      <div style="width: 100%;flex: 1; overflow: auto">
        <van-index-bar :sticky-offset-top="100" :sticky="false" :index-list="indexList">
          <div v-for="(item, index) in list" :key="index">
            <van-index-anchor class="pt-2 pb-2 tabBackground" :index="item.title" />
            <div class="payment_method_cell flex items-center ml-12 pt-2 pb-2" v-for="(label, labelIndex) in item.labels"
              :key="label.id" @click="addBankCar(label)">
              <div class="w-3 h-10 mr-5 rounded-xl" :style="{ 'background': color(labelIndex) }"></div>
              <van-cell :title="label.name" />
            </div>
          </div>
        </van-index-bar>
      </div>
    </div>
  </div>
</template>

<script>
import otcApi from "@/service/otc.api";
import { pinyin } from 'pinyin-pro';

import {
  getRandom
} from "@/utils/utis";
import { IndexBar, IndexAnchor, Cell, Sticky, showLoadingToast, closeToast } from 'vant';
import OrderNav from "@/components/Transform/order-nav/OrderNav.vue";
import BuyInput from "@/views/cryptos/placeAnOrder/components/buyInput/BuyInput.vue";

export default {
  name: "AddPaymentMethod",
  data() {
    return {
      select: '',
      indexList: [],
      list: [],
      allList: []
    }
  },
  created() {
    this.getPaymentMethodConfig();
  },
  methods: {
    //搜索
    inputValue() {
      if (this.select) {
        this.list = this.list.filter((item) => {
          return item.labels[0].name.indexOf(this.select) != -1
        })
      } else {
        this.list = this.allList
      }
    },
    addBankCar(data) {
      this.$router.push({
        path: '/cryptos/wantBuy/bankCard',
        query: {
          type: "CN",
          id: data.id,
          configType: 'add',
        }
      })
    },
    async getPaymentMethodConfig() {
      const titleArr = []; // 右侧索引
      showLoadingToast({
        duration: 0,
        forbidClick: true,
        message: this.$t('加载中')
      })
      const res = await otcApi.ctcPaymentMethodConfig({ language: this.$i18n.locale })
      closeToast();
      // format数据
      Object.keys(res).forEach(key => {
        // 换成拼音
        const title = pinyin(res[key], {
          pattern: 'first',
          type: 'array'
        })[0].toLocaleUpperCase()

        titleArr.push(title)

        // 查找是否有相同
        const index = this.list.findIndex(item => item.title === title)

        if (index === -1) {
          this.list.push({
            title,
            labels: [{
              id: key,
              name: res[key],
            }]
          })
        } else {
          this.list[index].labels.push({
            id: key,
            name: res[key],
          })
        }

      })
      console.log(this.list)
      this.allList = this.list
      // 数组去重
      this.indexList = [...new Set(titleArr)]
    },
    getRandom(min, max) {
      return Math.floor(Math.random() * (max - min) + min)
    },
    color(index) {
      return `rgb(${this.getRandom(0, 250) + index},${this.getRandom(0, 250) + index},${this.getRandom(0, 250) + index})`
    }
  },
  components: {
    [IndexBar.name]: IndexBar,
    [IndexAnchor.name]: IndexAnchor,
    [Cell.name]: Cell,
    OrderNav,
    BuyInput,
  }
}
</script>

<style lang="scss" scoped>
#AddPaymentMethodPage {
  font-size: 30px;

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
      -webkit-transform: scaleY(.5);
      transform: scaleY(.5);
      border-bottom: 1px solid $border_color;
    }
  }

  .AddPaymentMethod {

    :deep(.van-index-anchor) {
      font-size: 36px;
      font-weight: 500;
      color: #868D9A;
    }

    :deep(.van-cell__title) {
      font-size: 32px;
      color: $text_color2;
    }

    :deep(.van-cell) {
      padding-left: 8px;
      background: $main_background;
    }

    :deep(.van-index-bar__sidebar) {
      display: none;
      color: #7CBFFF;

      span {
        margin-bottom: 4px;
        font-size: 26px;
      }
    }
  }
}
</style>
