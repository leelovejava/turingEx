<template>
  <div id="cryptos">
    <p class="header-title">{{ $t('现货账户') }}</p>
    <div class="pb-32 assetsCenter px-3">
      <as-sets :funds="funds" key="0" tab="funds"></as-sets>
    </div>
  </div>
</template>

<script>

import AsSets from "@/components/Transform/assetsCenter/assets.vue"
import { _getAllAssets } from "@/service/user.api.js";
export default {
  name: "assets-index",
  components: {
    AsSets,
  },
  data() {
    return {
      type: 'left', //left 从左往右 right 从右往左
      list: [],
      timer: null,
      funds: {},
      typeList: [
        {
          type: this.$t('现货账户'),
        },
      ]
    }
  },
  methods: {
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
  },
  activated() {
    clearInterval(this.timer)
    this.timer = setInterval(() => {
      this.getAssets()
    }, 5000)
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
