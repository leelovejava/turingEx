<template>
  <div id="cryptos">
    <assets-head :title="$t('资金账户')" :back-func="backFunc">
    </assets-head>
    <div class="pb-32 assetsCenter px-3">
      <div class="flex justify-between mx-14 mt-8  box-border">
        <div class=" items-center text-center textColor1" v-for="(item, index) in typeList " :key="'type' + index"
          @click="onTabs(index)">
          <div class="type">{{ item.type }}</div>
        </div>
      </div>
      <div class="flex mt-4">
        <span class="flex-1 border-b-color " :class="{ active_color: tab === 0 }"></span>
        <span class="flex-1 border-b-color" :class="{ active_color: tab === 1 }"></span>
        <span class="flex-1 border-b-color" :class="{ active_color: tab === 2 }"></span>
        <span class="flex-1 border-b-color" :class="{ active_color: tab === 3 }"></span>
      </div>
      <over-view v-if="tab === 0" :funds="funds" :key="tab"></over-view>
      <as-sets v-if="tab === 1" :funds="funds" :key="tab"></as-sets>
      <contract v-if="tab === 2" :funds="funds" :index="index" :key="tab"></contract>
      <financial v-if="tab === 3" :funds="funds" :index="index" :key="tab"></financial>
    </div>
  </div>
</template>

<script>

import AsSets from "@/components/Transform/assetsCenter/assets.vue"
import OverView from "@/components/Transform/assetsCenter/overview.vue"
import Contract from "@/components/Transform/assetsCenter/contract.vue"
import Financial from "@/components/Transform/assetsCenter/financial.vue"
import { _getAllAssets } from "@/service/user.api.js";
export default {
  name: "assets-index",
  components: {
    AsSets,
    OverView,
    Contract,
    Financial
  },
  data() {
    return {
      type: 'left', //left 从左往右 right 从有王座
      list: [],
      timer: null,
      tab: 0,
      index: 0, // 每个组件的二级tab
      funds: {},
      typeList: [
        {
          type: this.$t('总览'),
        },
        {
          type: this.$t('现货账户'),
        },
        {
          type: this.$t('合约'),

        },
        {
          type: this.$t('理财'),
        },
      ]
    }
  },
  methods: {
    backFunc() {
      this.$router.push({
        path: '/quotes/index',
        query: {
          tabActive: 1
        }
      })
    },
    onTabs(val) {
      if (this.tab < val) {
        this.type = 'right'
      } else {
        this.type = 'left'
      }
      console.log(val)
      this.tab = val
    },
    getAssets() {
      _getAllAssets().then((data) => {
        this.funds = data
      })
    },
  },
  created() {
    this.getAssets()
    this.timer = setInterval(() => {
      this.getAssets()
    }, 5000)
    if (Object.hasOwnProperty.call(this.$route.query, 'tab')) {
      this.tab = this.$route.query.tab / 1
      this.index = this.$route.query.index ? this.$route.query.index * 1 : 0
    }
  },
  activated() {
    clearInterval(this.timer)
    this.timer = setInterval(() => {
      this.getAssets()
    }, 5000)
    this.index = 0
  },
  deactivated() {
    clearInterval(this.timer)
    this.timer = null
  },
  beforeUnmount() {
    clearInterval(this.timer)
    this.timer = null
  }
}
</script>

<style lang="scss" scoped>
#cryptos {
  .assetsCenter {
    width: 100%;
    box-sizing: border-box;
  }

  .header-title {
    padding: 20px 0;
    font-size: 2.25rem;
    text-align: center;
  }

  .type {
    font-size: 36px;
    line-height: 24px;
  }

  .active_color {
    border-bottom: 5px solid $color_main ;
    border-radius: 2.208px;
  }

  .left-enter-active,
  .left-leave-active,
  .right-enter-active,
  .right-leave-active {
    will-change: transform;
    transition: all 500ms;
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
    transform: translate3d(100%, 0, 0)
  }

  .right-enter {
    opacity: 0;
    transform: translate3d(100%, 0, 0);
  }

  .right-leave {
    opacity: 0;
    transform: translate3d(-100%, 0, 0)
  }
}
</style>
