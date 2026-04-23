<template>
  <div id="cryptos">
    <div id="MiningPoolFinanceHistory" class="MiningPoolFinanceHistory">
      <assets-head :title="$t('历史')" :back-func="() => {
        $router.push({
          path: '/cryptos/funds',
          query: {
            tab: 3,
            index: type, // 0: 查看理财订单 1: 矿机
            type: 'cryptos'
          }
        })
      }"></assets-head>
      <van-tabs v-model:active="active" ref="tabs" @change="onchange">
        <van-tab :title="$t('购买')">
          <div class="mainBackground h-2"></div>
          <van-pull-refresh :pulling-text="$t('下拉即可刷新')" :loosing-text="$t('释放即可刷新')" :loading-text="$t('加载中')"
            v-model="isLoading" @refresh="onRefresh">
            <van-list v-model:loading="loading" :finished="finished" @load="onLoad" :loading-text="$t('加载中')">
              <div v-for="(item, index) in list" :key="'buy' + index">
                <div class="box-border px-8">
                  <div class="flex justify-between items-center h-28 text-32 font-normal">
                    <div class="text-grey">{{ $t('时间') }}</div>
                    <div class="textColor"> {{ item.create_time }}</div>
                  </div>
                  <div class="flex justify-between items-center h-28 text-32 font-normal">
                    <div class="text-grey">{{ $t('数量') }}</div>
                    <div class="textColor">{{ item.amount }}&nbsp;USDT</div>
                  </div>
                  <div class="flex justify-between items-center h-28 text-32 font-normal">
                    <div class="text-grey">{{ $t('周期') }}</div>
                    <div class="textColor"> {{ item.cycle == 0 ? $t('无限期') : item.cycle + $t('天') }}</div>
                  </div>
                  <div class="flex justify-between items-center h-28 text-32 font-normal">
                    <div class="text-grey">{{ $t('累计收益') }}</div>
                    <div class="textColor" :class="item.profit / 1 > 0 ? 'text-green' : 'text-red'"> {{ item.profit }}
                    </div>
                  </div>

                </div>
                <div class="diviLine h-3"></div>
              </div>
            </van-list>
          </van-pull-refresh>
        </van-tab>
        <van-tab :title="$t('赎回')">
          <div class="mainBackground h-2"></div>
          <van-pull-refresh :pulling-text="$t('下拉即可刷新')" :loosing-text="$t('释放即可刷新')" :loading-text="$t('加载中')"
            v-model:loading="isLoading" @refresh="onRefresh">
            <van-list v-model:loading="loading" :finished="finished" :finished-text="$t('没有更多了')" @load="onLoad">
              <div class="flex justify-center">
                <financial-list :list="list" :btnShow="false" :type="type" :goBack="true"></financial-list>
              </div>
            </van-list>
          </van-pull-refresh>

        </van-tab>
        <van-tab :title="$t('利息')">
          <div class="mainBackground h-2"></div>
          <van-pull-refresh :pulling-text="$t('下拉即可刷新')" :loosing-text="$t('释放即可刷新')" :loading-text="$t('加载中')"
            v-model="isLoading" @refresh="onRefresh">
            <van-list v-model:loading="loading" :finished="finished" :finished-text="$t('没有更多了')" @load="onLoad">
              <div v-for="(item, index) in list" :key="'interest' + index">
                <div class="flex justify-between items-center h-36 px-8">
                  <div class="flex items-center">
                    <img v-if="item.amount / 1 > 0 ? false : true" src="@/assets/image/assets-center/out.png" alt=""
                      class="w-11 h-11 mr-8">
                    <img v-else src="@/assets/image/assets-center/in.png" alt="" class="w-11 h-11 mr-8">
                    <div class="flex flex-col">
                      <div class="mb-4 text-32 font-normal textColor">{{ $t('利息数量') }}（USDT）</div>
                      <div class="text-grey text-28">{{ item.createTimeStr }}</div>
                    </div>
                  </div>
                  <div class="text-32" :class="item.amount / 1 > 0 ? 'text-green' : 'text-red'">{{ item.amount }}</div>
                </div>
                <div class="diviLine h-2"></div>
              </div>
            </van-list>
          </van-pull-refresh>
        </van-tab>
      </van-tabs>
    </div>
  </div>
</template>

<script>
import { Tab, Tabs, Icon, List, PullRefresh, Button } from 'vant';
import assetsHead from '@/components/Transform/assets-head/index.vue';

import { getfinacialProductsBought, getMachineBought } from '@/service/financialManagement.api.js';
import { _fundRecord } from '@/service/fund.api';
import financialList from "@/components/Transform/assetsCenter/financialList.vue";
export default {
  name: "FinancialHistory",
  components: {
    [Tab.name]: Tab,
    [Tabs.name]: Tabs,
    [Icon.name]: Icon,
    [List.name]: List,
    [Button.name]: Button,
    [PullRefresh.name]: PullRefresh,
    assetsHead,
    financialList
  },
  data() {
    return {
      type: 0, // 是普通理财历史  1 是矿池理财历史
      isLoading: false, // 下拉刷新LOADING
      loading: false,
      finished: false,
      page: 1,
      active: 0,
      list: [],
    }
  },
  methods: {
    handleGoOrderDetail(item) {
      if (this.type === 0) {
        this.$router.push({
          path: '/cryptos/financialOrder',
          query: {
            ...item,
            showBtn: this.active === 0,
            goBack: true
          }
        })
      } else {
        this.$router.push({
          path: '/cryptos/miningMachineOrder',
          query: {
            ...item,
            showBtn: this.active === 0,
            goBack: true
          }
        })
      }
    },
    onRefresh() {
      console.log('refresh')
      this.init()
    },
    onLoad() {
      this.get()
    },
    onchange() {
      this.init()
    },
    init() {
      this.page = 1
      this.list = []
      this.get()
    },
    get() {
      if (this.type === 0) { // 普通理财历史
        if (this.active === 0) {
          getfinacialProductsBought({
            page_no: this.page,
            state: '1'
          }).then(res => { // 已购理财产品
            this.handleData(res)
          })
        } else if (this.active === 1) {
          getfinacialProductsBought({
            page_no: this.page,
            state: '2'
          }).then(res => {  // 已赎回理财产品
            this.handleData(res)
          })
        } else {
          _fundRecord('finance', this.page, 'finance_profit').then(res => { // 利息
            this.handleData(res)
          })
        }
      } else {  // 矿机理财历史
        if (this.active === 0) {
          getMachineBought({
            page_no: this.page,
            state: '1'
          }).then(res => { // 已购理财产品
            this.handleData(res)
          })
        } else if (this.active === 1) {
          getMachineBought({
            page_no: this.page,
            state: '2'
          }).then(res => {  // 已赎回理财产品
            this.handleData(res)
          })
        } else {
          _fundRecord('miner', this.page, 'miner_profit').then(res => { // 利息
            this.handleData(res)
          })
        }
      }
    },
    handleData(data) {
      this.isLoading = false;
      console.log(data)
      this.list = this.list.concat(data)
      this.loading = false
      if (data.length < 10) {
        this.finished = true
      }
      this.page++
    }
  },
  created() {
    if (this.$route.query.tab) {
      this.active = this.$route.query.tab
    }
    if (this.$route.query.type) {
      this.type = this.$route.query.type / 1
    }
  },
  mounted() {
  },
}
</script>

<style lang="scss" scoped>
#cryptos {
  font-size: 30px;

  .MiningPoolFinanceHistory {
    width: 100%;
    box-sizing: border-box;

    :deep(.van-tabs__nav) {
      background-color: $main_background;
    }

    :deep(.van-tab--active) {
      color: $text_color;
    }
  }
}
</style>
