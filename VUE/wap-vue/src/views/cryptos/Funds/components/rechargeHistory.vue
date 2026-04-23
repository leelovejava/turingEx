<template>
    <!-- 充值历史 -->
    <div class="pl-30 pr-30 wrap">
        <div class="text-center recharge-text ">{{ $t('数字货币') }}</div>
        <van-pull-refresh v-model="refreshing" @refresh="onRefresh" :pulling-text="$t('下拉即可刷新')"
            :loosing-text="$t('释放即可刷新')" :loading-text="$t('加载中')">
            <div>
                <div v-if='noData' class="textColor">
                    {{ $t('暂无数据') }}
                </div>
                <template v-else>
                    <van-list ref="tabs" :loading-text="$t('加载中')" v-model:loading="loading" :finished="finished" :finished-text="$t('已经全部加载完毕')" :offset="130"
                        @load="onLoad">
                        <div class="flex justify-between mb-10" v-for="(item, index) in list" :key="index"
                            @click="onDetail(item)">
                            <div>
                                <div class=" textColor">{{ item.coin }}</div>
                                <div class="text-grey mt20">{{ item.createTime }}</div>
                            </div>
                            <div>
                                <div class="text-right textColor">{{ item.amount }}</div>
                                <div class="mt20">
                                    <div class="flex justify-end" v-if="item.status == 0">
                                        <div class="common-round yellow-round"></div>
                                        <div class="text-grey ">{{ $t('确定中') }}</div>
                                    </div>
                                    <div class="flex justify-end" v-if="item.status == 1">
                                        <div class="common-round green-round"></div>
                                        <div class="text-grey ">{{ $t('成功') }}</div>
                                    </div>
                                    <div class="flex" v-if="item.status == 2">
                                        <div class="common-round red-round"></div>
                                        <div class="text-grey ">{{ $t('失败') }}</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </van-list>
                </template>
            </div>
        </van-pull-refresh>
    </div>
</template>

<script>
import Axios from "@/service/recharge.js"
import { List, PullRefresh } from 'vant';
export default {
    name: "rechargeHistory",
    components: {
        [List.name]: List,
        [PullRefresh.name]: PullRefresh
    },
    data() {
        return {
            list: [],
            page: 1,
            loading: false, // 当loading为true时，转圈圈
            finished: false,  // 数据是否请求结束，结束会先显示'已经全部加载完毕'
            noData: false,// 如果没有数据，显示暂无数据
            refreshing: false,   // 下拉的加载图案

        }
    },
    mounted() {
        this.list = []
        // this.getList();
    },
    methods: {
        onDetail(item) { // 充值详情
            // this.$router.push('/recharge/rechargeDetail?id='+ item.order_no)
            this.$router.push('/cryptos/recharge/rechargeDetail?order_no=' + item.order_no)
        },
        getList() {
            Axios.getRechargeList({
                page_no: this.page
            }).then((res) => {
                // 如果加载完毕，显示没有更多了
                this.refreshing = false;
                if (res.length < 8) {
                    this.finished = true
                }

                this.loading = false;
                this.list = this.list.concat(res);
                // 如果没有数据，显示暂无数据
                if (this.list.length === 0 && this.page === 1) {
                    this.noData = true
                }

                this.page++;

            });
        },
        onLoad() {
            console.log('onLoad')
            setTimeout(() => {
                this.getList();
            }, 500)
        },
        onRefresh() {
            this.list = []
            this.page = 1
            this.loading = true
            this.finished = false
            this.noData = false
            this.onLoad()
        }
    }
}
</script>

<style lang="scss" scoped>
@import "./history.scss";

.wrap {
    font-size: 30px;
}
</style>
