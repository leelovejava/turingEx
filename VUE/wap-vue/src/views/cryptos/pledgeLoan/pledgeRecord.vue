<template>
    <div id="cryptos">
        <div class="pledgeRecord">
            <assets-head :title="$t('质押记录')"></assets-head>
            <div class="content px-8">
                <van-list v-model:loading="loading" :loading-text="$t('加载中...')" :finished="finished"
                    :finished-text="orderList.length ? $t('已经全部加载完毕') : ''" @load="onLoad" :offset="130">
                    <div class="item mb-10 contBackground1 rounded-lg pl-6 pr-6 pb-8" v-for="(item, index) in orderList"
                        :key="index">
                        <div class="flex justify-between border-b-color h-24 box-border text items-center mb-7">
                            <div class="textColor text-36"
                                :class="{ 'redColor': item.orderType == 5, 'skyColor': item.orderType == 2 }">
                                {{ fixStr(item.orderType) }}</div>
                            <div class="text-grey text-32">{{ item.createTime }}</div>
                        </div>
                        <div class="flex">
                            <div class="mr-100 flex-1"
                                v-if="item.orderType == 1 || item.orderType == 3 || item.orderType == 4">
                                <div class="text-grey text-32">{{ item.orderType == 4 ? $t('还款数量') : $t('借款') }}</div>
                                <div class="textColor mt-5 text-32">{{ item.loanAmount }}&nbsp;USD</div>
                            </div>
                            <div class="mr-100 flex-1" v-if="item.orderType == 1 || item.orderType == 2">
                                <div class="text-grey text-24">{{ $t('质押类型') }}</div>
                                <div class="textColor mt-5 text-24">{{ $t('币') }}</div>
                            </div>
                            <div class="flex-1" v-if="item.orderType == 1 || item.orderType == 2">
                                <div class="text-grey text-24">{{ $t('质押金额') }}</div>
                                <div class="textColor mt-5 text-24">
                                    {{ item.pledgeAmount }}&nbsp;{{ item.pledgeCurrency.toUpperCase() }}</div>
                            </div>
                        </div>
                    </div>
                </van-list>
            </div>
        </div>
    </div>
</template>

<script>
import assetsHead from "@/components/Transform/assets-head/index.vue";
import { _relationOrderList } from "@/service/pledgeLoan.js";
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
            id: '',
            page_no: 1,
            orderList: [],
            loading: false,
            finished: false,
        }
    },
    created() {
        this.id = this.$route.query.id
    },
    methods: {
        onLoad() {
            this.getRelationOrderList()
        },
        getRelationOrderList() {
            _relationOrderList({
                loanOrderId: this.id,
                page_no: this.page_no
            }).then(res => {
                this.orderList = [...this.orderList, ...res]
                this.loading = false
                if (res.length < 10) {
                    this.finished = true
                } else {
                    this.page++
                }
            })
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
    }
}
</script>

<style lang="scss" scoped>
#cryptos {
    .pledgeRecord {
        width: 100%;
        box-sizing: border-box;
    }

    .redColor {
        color: $red;
    }

    .skyColor {
        color: #13D3EB;
    }
}
</style>