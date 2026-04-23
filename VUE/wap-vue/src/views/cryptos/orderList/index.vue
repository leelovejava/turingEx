<template>
  <div class="main flex flex-col">
    <div class="nav flex items-center">
      <van-icon name="arrow-left" />
      <div class="top-nav flex">
        <div :class="active ? 'active' : ''" @click="active = true">{{ $t('进行中') }}</div>
        <div :class="!active ? 'active' : ''" @click="active = false">
          {{ $t('已完成') }}
        </div>
      </div>
    </div>
    <van-tabs @change="handleChange" v-model="activeName">
      <van-tab v-for="(item, index) in tabList" :key="index" :title="item.label">
      </van-tab>
    </van-tabs>
    <div class="btp flex-1 bg-white">
      <van-pull-refresh :pulling-text="$t('下拉即可刷新')" :loosing-text="$t('释放即可刷新')" :loading-text="$t('加载中')"
        v-model="isLoading" @refresh="onRefresh">
        <van-list :immediate-check="false" v-model="loading" :finishedArr="finishedArr" :finishedArr-text="$t('没有更多了')"
          @load="onLoad">
          <div v-if="list.length === 0" class="zanwu">
            <img src="@/assets/image/zanwu.png" alt="" />
            <p>{{ $t('暂无订单') }}</p>
          </div>
          <div v-else>
            <Items v-for="(el, eIndex) in list" :key="eIndex" :items="el" />
          </div>
        </van-list>
      </van-pull-refresh>
    </div>
  </div>
</template>

<script>
import { Icon, Tab, Tabs, List, PullRefresh, showLoadingToast, closeToast } from 'vant'
import listLoadMixins from '@/utils/list-load-mixins.js'
import Items from './items.vue'
import { getMerchantOrdersList } from '@/service/otc.api.js'
export default {
  mixins: [listLoadMixins],
  components: {
    [Icon.name]: Icon,
    [Tab.name]: Tab,
    [Tabs.name]: Tabs,
    Items,
    [List.name]: List,
    [Toast.name]: Toast,
    [PullRefresh.name]: PullRefresh,
  },
  created() {
    this.get()
  },
  methods: {
    get() { // 获取数据的方法需要自定义
      showLoadingToast({
        duration: 0,
        forbidClick: true,
        message: this.$t('加载中')
      })
      getMerchantOrdersList(this.form).then(res => {
        closeToast();
        // console.log('承兑商订单列表', res)
        this.handleData(res)
      })
    },
    handleChange() {
      this.form.state = this.tabList[this.activeName].value
      this.onRefresh()
    }
  },
  data() {
    return {
      form: {
        direction: '', // buy sell
        state: '' // 0未付款/1已付款/2申诉中/3已完成/4已取消/5已超时
      },
      active: true,
      activeName: 0,
      finishedArr: [
        {
          label: this.$t('已完成'),
          value: 3
        },
        {
          label: this.$t('已取消'),
          value: 4
        },
        {
          label: this.$t('已超时'),
          value: 5
        },
      ],
      runningArr: [
        {
          label: this.$t('全部'),
          value: ''
        },
        {
          label: this.$t('已付款'),
          value: 1
        },
        {
          label: this.$t('申诉中'),
          value: 2
        },
      ],
    }
  },

  watch: {
    active(val) {
      this.activeName = 0
      if (val === true) {
        this.form.state = ''
      } else {
        this.form.state = 3
      }
      this.onRefresh()
    }
  },
  computed: {
    tabList() {
      return this.active ? this.runningArr : this.finishedArr
    },
  },
}
</script>

<style lang="scss" scoped>
.main {
  background: rgb(245, 245, 245);
  height: calc(100% - 160px);

  .nav {
    padding: 22px 40px;

    .top-nav {
      width: 372px;
      height: 80px;
      border: 1px solid #e0e0e0;
      border-radius: 6px;
      margin-left: 160px;

      div {
        margin: 8px;
        height: 64px;
        line-height: 64px;
        text-align: center;
        width: 48%;
      }
    }
  }



  :deep(.van-tabs__line) {
    width: 52px;
    height: 6px;
  }

  :deep(.van-tabs__nav) {
    border-radius: 70px 70px 0 0 !important;
  }

  .main-c {
    height: 100%;
    background: #fff;
    border-radius: 80px 80px 0px 0px;
    overflow: auto;
  }
}

.zanwu {
  text-align: center;
  font-size: 28px;
  color: #868d9a;

  img {
    margin-top: 206px;
    width: 157px;
    height: 152px;
  }
}

.btp {
  border-top: 1px solid #484756;
  overflow-y: auto;
}

.active {
  background: #fff;
}
</style>
