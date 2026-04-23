<template>
  <div class="list-quatation">
    <mg-tabs @tabs="onTabs"></mg-tabs>
    <van-list>
      <van-cell class="tab">
        <div class="flex items-center w-full text-grey text-26">
          <p class="left text-left">
            <span>{{ $t('名称代码') }}
            </span>
          </p>
          <p class="mid text-right">
            {{ $t('盘前涨跌幅') }}
          </p>
          <p class="right text-right">
            {{ $t('盘前价') }}
          </p>
        </div>
      </van-cell>
      <transition-group :name="type" tag="div">
        <div class="groupItem" :key="active">
          <van-cell v-for="item in showList" :key="item.id">
            <ul class="flex justify-evenly w-full items-center" @click="onItemClick(item)">
              <li class="flex items-center mid">
                <p class="flex flex-col items-start">
                  <span class="text-28 text-left text-color name"> {{ item.name || '--' }} </span>
                  <span class="flex text-24 flex items-center text-left leading-0.2 fullname">{{ item.symbolFullName ||
                    '--' }}</span>
                </p>
              </li>
              <li class="flex flex-col mid">
                <p class="w-40 text-32 h-18 text-color bg-green border-0 text-center" v-if="item.changeRatio > 0">
                  +{{ item.changeRatio }}%</p>
                <p class="w-40 text-32 h-18 text-color bg-red border-0 text-center btn" v-else>
                  {{ item.changeRatio || (item.changeRatio === 0 ? 0 : '--') }}%</p>
              </li>
              <li class="right flex items-center justify-end">
                <p class="text-color">
                  {{ item.close || '--' }}
                </p>
              </li>
            </ul>
          </van-cell>
        </div>
      </transition-group>
    </van-list>
  </div>
</template>
  
<script>
import { List, Cell } from 'vant'
import { mapGetters, mapActions } from 'vuex'
import { fixDate, setStorage } from "@/utils";
import MgTabs from "@/components/Transform/mg-tabs/index.vue";
import { HOST_URL } from '@/config'
import { SET_CURRENCY } from "@/store/const.store";
export default {
  name: 'MgQuotation',
  data() {
    return {
      fixDate,
      HOST_URL,
      active: 0,
      type: 'left' //left 从左往右 right 从有王座
    }
  },
  props: {
    showMore: {
      type: Boolean,
      default: true
    },
    listData: {
      type: Array,
      default() {
        return []
      }
    },
    tabActive: {
      type: Number,
      default: 0
    },
  },
  computed: {
    ...mapGetters({ currency: 'home/currency' }),
  },
  components: {
    [List.name]: List,
    [Cell.name]: Cell,
    MgTabs
  },
  mounted() {
    this.SET_CURRENCY()
  },
  methods: {
    ...mapActions('home', [SET_CURRENCY]),
    onItemClick(item) {
      if (this.tabActive == 2) { //现货
        this.$router.push({
          path: `/cryptos/trade/${item.symbol}`
        });
      } else {
        setStorage('symbol', item.symbol)
        this.$router.push({
          path: `/cryptos/perpetualContract/${item.symbol}`,
          query: { type: 'US-stocks' }
        });
      }
    },
    handleImage(url) {
      return new URL(url, import.meta.url).href
    },
    onTabs(val) {
      if (this.active < val) {
        this.type = 'right'
      } else {
        this.type = 'left'
      }
      this.active = val
      if (val == 0) {
        this.showList = [...this.listData];
      } else if (val == 1) {
        this.showList = [...this.listData].sort(this.compare("changeRatio", 'up'))
      } else if (val == 2) {
        this.showList = [...this.listData].sort(this.compare("changeRatio", 'down'))
      }
    },
    compare(p, type) { //这是比较函数
      return function (m, n) {
        var a = m[p];
        var b = n[p];
        if (a == b) {
          return
        }
        if (type == 'up') {
          return b - a; //升序
        } else if (type == 'down') {
          return a - b; //降序
        } else {
          return a - b;
        }
      }
    }
  },
  watch: {
    listData() {
      if (this.active == 0) {
        this.showList = [...this.listData];
      } else if (this.active == 1) {
        this.showList = [...this.listData].sort(this.compare("changeRatio", 'up'))
      } else if (this.active == 2) {
        this.showList = [...this.listData].sort(this.compare("changeRatio", 'down'))
      }
      this.$forceUpdate()
    }
  }
}
</script>
<style lang="scss" scoped>
.text-color {
  color: $text_color;
}

.left-enter-active,
.left-leave-active,
.right-enter-active,
.right-leave-active {
  will-change: transform;
  transition: all 250ms;
}

.left-leave-active,
.right-leave-active {
  display: none;
}

.left-enter {
  opacity: 0;
  transform: translate3d(-100%, 0, 0);
}

.left-leave {
  opacity: 0;
  transform: translate3d(0%, 0, 0)
}

.right-enter {
  opacity: 0;
  transform: translate3d(100%, 0, 0);
}

.right-leave {
  opacity: 0;
  transform: translate3d(0%, 0, 0)
}

.btn {
  border-radius: 9px;
  line-height: 71px;
}

.left {
  flex: 1;
}

.mid {
  flex: 1;
  text-align: center;
}

.right {
  flex: 1;
  // margin-left: 38px;
}

.tab {
  background-color: $mainBgColor;
}

.groupItem {
  :deep(.van-cell) {
    height: 120px;
    background-color: $mainBgColor;

    ul {
      align-items: center;
      height: 100%;
    }

    li p {
      line-height: 34px;
    }

    .name,
    .fullname {
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      max-width: 260px;
    }
  }

  .bg-green {
    color: #06CDA5;
  }

  .bg-red {
    color: #F43368;
  }


}

:deep(.van-cell::after) {
  border-bottom: 1px solid $border_color !important;
}
</style>
  