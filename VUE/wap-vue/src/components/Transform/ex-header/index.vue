<template>
  <div class="header flex text-28">
    <div class="user" @click="$router.push('/userCenter')"><img src="@/assets/theme/dark/image/Group.png" alt=""></div>
    <div class="styleWh tabBackground rounded-full flex items-center input-container">
      <img src="@/assets/image/icon-search.png" alt="logo" />
      <input type="text" v-model="keywords" :placeholder="$t('搜索币种')" class="h-full flex-1 border-none bg-none textColor"
        @input="onInput" />
    </div>
    <div class="custom" @click="tokefu">
      <img src="@/assets/image/customer.png" alt="">
    </div>
  </div>
</template>

<script>
import { themeStore } from '@/store/theme';
const thStore = themeStore()
const THEME = thStore.theme
export default {
  props: {
    unread_num: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      THEME,
      keywords: '',
      inputTimeout: null,
      active: 1,
      tabList: [
        { id: 1, text: this.$t('自选'), data: [], loading: true },
        { id: 2, text: this.$t('现货'), data: [], loading: true },
        { id: 3, text: this.$t('合约'), data: [], loading: true }
      ],
    }
  },
  methods: {
    onInput() { // 输入
      if (this.inputTimeout) {
        clearTimeout(this.inputTimeout)
        this.inputTimeout = null
      }
      this.inputTimeout = setTimeout(() => {
        this.getFilterData()
      }, 50)
    },
    getFilterData() { // 过滤后的数据
      const index = this.active / 1 - 1
      //console.log(index)
      this.tabList[index]['data'] = this.tabList[index]['data'].filter(item => item.symbol.includes(this.keywords))
    },
    tokefu() {
      this.$router.push('/customerService')
    }
  }
}
</script>

<style lang="scss" scoped>
#cryptos {
  .header {
    height: 72px;
    display: flex;
    justify-content: center;
    align-items: center;
    padding-top: 10px;
    margin-bottom: 10px;

    .user {
      width: 66px;
      height: 66px;

      img {
        width: 100%;
        height: 100%;
      }
    }

    .input-container {
      height: 64px;

      img {
        margin: 0 20px;
        width: 2rem;
        height: 2rem;
      }
    }

    .custom {
      width: 44px;
      height: 44px;

      img {
        width: 100%;
        height: 100%;
      }
    }
  }

  .styleWh {
    margin-left: 22px;
    margin-right: 26px;
    flex: 1;
  }
}
</style>