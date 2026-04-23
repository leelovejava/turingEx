<template>
    <div id="cryptos">
        <div class="pledgeLoanOrder">
            <assets-head :title="$t('质押借币订单')" :backFunc="() => $router.push('/cryptos/pledgeLoan')"></assets-head>
            <template v-if="noticeList.length">
                <div class="h-25 flex items-center justify-between pl-8 pr-7 mb-10"
                    style="color:#F5C425;background:#56481B;" v-for="(item, index) in noticeList" :key="index"
                    @click="toOrderDetail(item)">
                    <div class="flex text-28">
                        <img src="@/assets/image/waring.png" alt="" class="w-9 h-9" />
                        <span class="ml-22">{{ $t('有笔订单质押率高于60%有平仓风险') }}</span>
                    </div>
                    <img src="@/assets/image/warningTo.png" alt="" class="w-9 h-9" />
                </div>
            </template>
            <div class="content px-4">
                <van-list v-model:loading="loading" :loading-text="$t('加载中...')" :finished="finished"
                    :finished-text="orderList.length ? $t('已经全部加载完毕') : ''" @load="onLoad" :offset="130">
                    <div class="item mb-10 contBackground1 rounded-lg pl-6 pr-6 pb-7" v-for="(item, index) in orderList"
                        :key="index" @click="toOrderDetail(item.id)">
                        <div class="flex justify-between border-b-color h-24 box-border text items-center mb-10">
                            <div class="textColor"><span class="skyColor mr-10">{{ fixStr(item.orderType) }}</span>{{
                                item.loanAmount }}&nbsp;USD
                            </div>
                            <div
                                :class="{ 'redColor': item.state == 3, 'red': item.state == 4, 'text-grey': item.state == 2, 'skyColor': item.state == 1 }">
                                {{ fixStatus(item.state) }}</div>
                        </div>
                        <div class="flex">
                            <div class="mr-24">
                                <div class="text-grey">{{ $t('贷款币种') }}</div>
                                <div class="textColor mt-5">{{ item.loanCurrency.toUpperCase() }}</div>
                            </div>
                            <div class="mr-24">
                                <div class="text-grey">{{ $t('质押率') }}</div>
                                <div class="textColor mt-5">
                                    {{ item.pledgeRate !== '' ? (item.pledgeRate * 10000 / 100).toFixed(2) : '--' }}%</div>
                            </div>
                            <div>
                                <div class="text-grey">{{ $t('总负债') }}</div>
                                <div class="textColor mt-5">{{ item.debtAmount }}&nbsp;{{ item.loanCurrency.toUpperCase()
                                }}
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="flex flex-col justify-center pt-50 pb-20 items-center" v-if="!orderList.length && !loading">
                        <img src="@/assets/image/assets-center/no-data.png" alt="" class="w-180 h-180" />
                        <p class="text-grey mt-10 text-24">{{ $t('暂无记录') }}</p>
                    </div>
                </van-list>
            </div>
        </div>
    </div>
</template>

<script>
import assetsHead from "@/components/Transform/assets-head/index.vue";
import { _orderList } from "@/service/pledgeLoan.js";
import { List } from 'vant'
export default {
    props: {

    },
    components: {
        assetsHead,
        [List.name]: List,
    },
    data() {
        return {
            page: 1,
            orderList: [],
            noticeList: [],
            loading: false,
            finished: false,
        }
    },

    methods: {
        onLoad() {
            this.getOrderList()
        },
        toOrderDetail(id) {
            this.$router.push({ path: '/cryptos/pledgeLoanOrderDetail', query: { id } })
        },
        fixStr(orderType) {
            let str = ''
            if (orderType == 1) {
                str = this.$t('借款')
            } else if (orderType == 2) {
                str = this.$t('新增质押')
            } else if (orderType == 3) {
                str = this.$t('续借')
            } else if (orderType == 4) {
                str = this.$t('还款')
            } else if (orderType == 5) {
                str = this.$t('强平结清')
            }
            return str;
        },
        fixStatus(state) {
            let string = ''
            if (state == 1) {
                string = this.$t('计息中')
            } else if (state == 2) {
                string = this.$t('已结清')
            } else if (state == 3) {
                string = this.$t('强平结清')
            } else if (state == 4) {
                string = this.$t('已逾期')
            }
            return string
        },
        getOrderList() {
            _orderList({
                page_no: this.page
            }).then(res => {
                this.orderList = [...this.orderList, ...res.list]
                // console.log(logs)
                this.loading = false
                if (res.list.length < 10) {
                    this.finished = true
                } else {
                    this.page++
                }
                this.noticeList = res.noticeList
            })
        }
    }
}
</script>

<style lang="scss" scoped>
#cryptos {
    font-size: 30px;

    .pledgeLoanOrder {
        width: 100%;
        box-sizing: border-box;
    }

    .skyColor {
        color: #13D3EB;
    }

    .redColor {
        color: $red;
    }

    .red {
        color: #FF0000;
    }
}
</style>