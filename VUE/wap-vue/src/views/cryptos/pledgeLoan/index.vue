<template>
    <div id="cryptos">
        <div class="pledgeLoan">
            <assets-head :title="$t('质押借币')" :backFunc="() => $router.push('/quotes/index?tabActive=1')">
                <div class="custom" @click="$router.push('/cryptos/pledgeLoanOrder')">

                    <img :src="handleImage(orderIcon)" class="record-img " alt="record-img" />
                </div>
            </assets-head>
            <div class="contentBox">
                <div class="imgBox"><img src="@/assets/image/pledgeLoanBg.png" alt="" /></div>
                <div class="content mt-64 box-shad tabBackground">
                    <div class="mb-9">
                        <div class="text-32 textColor">{{ $t('借款') }}</div>
                        <div class="flex mt-5 h-24 items-center inputBox inputBackground1 textColor">
                            <input class="h-full pl-5 inputBackground1" type="number" v-model="loanAmount"
                                @input="changeAmount" :placeholder="$t('借款数量') + `>=${loanAmountMin}`">
                            <div class="right w-64 h-16 flex items-center pl-6 box-border relative" @click="openSelect">
                                <img src="@/assets/image/USD.png" class="w-12 h-12" alt="">
                                <span class="ml-1 mr-2">USD</span>
                                <img src="@/assets/image/icon-more.png" alt="logo" class="w-6 h-6" />
                                <div class="slectBox" v-show="isShow" @click.stop="isShow = false;">
                                    <div class="h-16 lh-16 border-b-color">Select</div>
                                    <div class="flex items-center h-20">
                                        <img src="@/assets/image/USD.png" class="w-10 h-10" alt="">
                                        <span class="ml-3 text-24 text-grey">USD</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="errorInfo text-24 h-7 mt-5 mb-7" style="color:#F43368;">{{ errorMsg ? errorMsg
                            : ''
                        }}
                        </div>
                        <div class="h-24 pledgeLoan_background rounded-lg text-center text-black text-34 flex items-center justify-center"
                            @click="getLoanParamFn">
                            {{ $t('质押股票') }}</div>
                    </div>
                </div>
                <div class="content mt-9 tabBackground">
                    <div class="text-32 textColor">{{ $t('质押') }}</div>
                    <div class="flex mt-5 h-24 items-center inputBox inputBackground1 textColor relative">
                        <input class="h-full pl-5 inputBackground1" type="number" v-model="pledgeAmount"
                            @input="changeAmount" :placeholder="$t('请输入质押数量')">
                        <div class="right w-63 h-16 flex items-center pl-7 box-border" @click="openSelectBorrow">
                            <img :src="`${HOST_URL}/symbol/${pledgeCurrency.toLowerCase()}.png`" class="w-12 h-12" alt="" />
                            <span class="ml-3 mr-7 w-22 currency-title">{{ pledgeCurrency.toUpperCase() }}</span>
                            <img src="@/assets/image/icon-more.png" alt="logo" class="w-6 h-6" />
                        </div>
                        <div class="slectBox slectBoxMax" v-show="isShow1">
                            <div>
                                <div class="h-16 lh-16 border-b-color">Select</div>
                                <div class="flex items-center border-b-color justify-between" v-for="item in walletList"
                                    :key="item.symbol" @click.stop="selectCoin(item)">
                                    <div class="flex items-center">
                                        <img :src="`${HOST_URL}/symbol/${item.symbol.toLowerCase()}.png`" class="w-10 h-10" alt="">
                                        <span class="ml-3 text-24 text-grey">{{ item.symbol.toUpperCase() }}</span>
                                    </div>
                                    <div class="py-2.5">
                                        <div class="text-grey text-right">{{ $t('总资产') }}:{{ item.volume }}</div>
                                        <div class="text-grey text-right">{{ $t('可用') }}:{{ item.usable }}</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="flex items-center text-24 mt-4 text-grey">
                        {{ $t('可用余额') }}：<span class="mr-3.5">{{ volume || '--' }}&nbsp;
                            {{ pledgeCurrency.toUpperCase() }}
                        </span><img @click="$router.push('/cryptos/exchangePage')" src="@/assets/image/exchangeIcon.png"
                            class="w-7 h-7" alt="">
                    </div>
                    <div class="mt-12 text-32 textColor">{{ $t('借币期限') }}</div>
                    <div class="mt-5 text-24 text-grey">{{ $t('提前还款不罚息') }}</div>
                    <div class="flex items-center h-24 mt-5 inputBox inputBackground1 textColor"
                        @click="showTerm = !showTerm">
                        <div class="pl-5 h-full inputBackground1 selectedCon">{{ loanCycle + $t('天') }}</div>
                        <div class="iconBox mr-8"><img src="@/assets/image/icon-more.png" alt="logo" class="w-6 h-6" />
                        </div>
                    </div>
                    <p class="mt-6 text-grey text-24 flex justify-between">
                        <span>{{ $t('强平价格') }}USD</span>
                        <span class="textColor">{{ closeOut || '--' }}</span>
                    </p>
                    <p class="mt-6 text-grey text-24 flex justify-between">
                        <span>{{ $t('质押率') }}</span>
                        <span class="textColor">{{ pledgeRate !== '' ? (pledgeRate * 10000 / 100).toFixed(2) : '--'
                        }}%</span>
                    </p>
                    <p class="mt-6 text-grey text-24 flex justify-between">
                        <span>{{ $t('小时利率') }}</span>
                        <span class="textColor">{{ hourlyRate || '--' }}</span>
                    </p>
                    <p class="mt-6 text-grey text-24 flex justify-between">
                        <span>{{ $t('日利率') }}</span>
                        <span class="textColor">{{ isNaN(hourlyRate) ? '--' : hourlyRate * 24 }}</span>
                    </p>
                    <div class="queIcon mt-2" @click="showMask = true"><img src="@/assets/image/skyQuestion.png"
                            class="w-6 h-6" alt=""></div>
                    <p class="mt-12 text-32 text-grey flex justify-between">
                        <span>{{ $t('总利息') }}</span>
                        <span class="textColor">{{ interestAmount || '--' }}&nbsp;USD</span>
                    </p>
                    <p class="mt-12 text-32 text-grey flex justify-between">
                        <span>{{ $t('预计还款') }}</span>
                        <span class="textColor">{{ debtAmount || '--' }}&nbsp;USD</span>
                    </p>
                    <div class="mt-19 h-24 pledgeLoan_background rounded-lg text-center text-black text-34 mt-12 flex items-center justify-center"
                        @click="getAddOrder">
                        {{ $t('借款') }}</div>
                </div>
            </div>
            <van-action-sheet v-model:show="showTerm" :title="$t('选择期限')">
                <div class="day-select">
                    <van-radio-group v-model="radio" @change="changeDay">
                        <van-cell-group>
                            <van-cell :title="item + $t('天')" clickable @click="radio = index + 1"
                                v-for="(item, index) in dayList" :key="index">
                                <template #right-icon>
                                    <van-radio :name="index + 1" />
                                </template>
                            </van-cell>
                        </van-cell-group>
                    </van-radio-group>
                </div>
            </van-action-sheet>
            <rule-mask v-model="showMask"></rule-mask>
        </div>
    </div>
</template>

<script>
import assetsHead from "@/components/Transform/assets-head/index.vue";
import { ActionSheet, RadioGroup, Radio, Cell, CellGroup, showToast } from "vant";
import { _getLoanConfig, _addOrder, _getLoanParam } from "@/service/pledgeLoan.js";
import { _getAllWallet } from '@/service/fund.api';
import { HOST_URL } from '@/config'
import { debounce } from '@/utils/utis'
import ruleMask from "./ruleMask.vue";
import { mapGetters } from 'vuex';
import { themeStore } from '@/store/theme';
const thStore = themeStore()
export default {
    props: {

    },
    components: {
        assetsHead,
        [ActionSheet.name]: ActionSheet,
        [RadioGroup.name]: RadioGroup,
        [Radio.name]: Radio,
        [Cell.name]: Cell,
        [CellGroup.name]: CellGroup,
        ruleMask
    },
    data() {
        return {
            HOST_URL,
            showMask: false,
            errorMsg: '',
            radio: 1,
            isShow: false,
            isShow1: false,
            showTerm: false,
            loanCycle: '',
            walletList: [],
            dayList: [],
            hourlyRate: '', //时利率
            loanAmountMin: '',//借款最小值
            loanAmount: '', //借款金额
            pledgeAmount: '', //质押金额
            pledgeCurrency: '', //质押币种
            volume: '', //可用余额
            pledgeRate: '', //质押率 
            closeOut: '', //强平价格
            debtAmount: '',//预计还款
            interestAmount: '--',//总利息
            orderIcon: new URL(`../../../assets/theme/${thStore.theme}/image/order.png`, import.meta.url)
        }
    },
    mounted() {
        this.init()
    },
    computed: {
        ...mapGetters({
            theme: 'home/theme'
        }),
    },
    methods: {
        handleImage(url) {
            return new URL(url, import.meta.url).href
        },
        init() {
            this.getLoanConfig()
            this.getList()
        },
        openSelect() {
            this.isShow = !this.isShow
        },
        openSelectBorrow() {
            this.isShow1 = !this.isShow1
        },
        selectCoin(item) {
            this.pledgeCurrency = item.symbol
            this.volume = item.usable
            this.isShow1 = false
        },
        changeDay(index) {
            this.loanCycle = this.dayList[index * 1 - 1]
            this.getLoanParamFn()
        },
        changeAmount() {
            if (this.pledgeAmount >= this.volume) {
                this.pledgeAmount = this.volume
            }
            this.debounceFn()
        },
        getLoanConfig() {
            _getLoanConfig().then(res => {
                this.dayList = res.loanCycle
                this.loanCycle = this.dayList[0]
                this.hourlyRate = res.hourlyRate
                this.loanAmountMin = res.loanAmountMin
            })
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
        },
        getList() {
            _getAllWallet().then((res) => {
                let walletList = res.extends.filter(item => {
                    return item.symbol.toLowerCase() != 'usdt'
                });
                this.walletList = [...walletList].sort(this.compare("usdt", 'up'))
                let initObj = this.walletList.find(item => {
                    return item.symbol.toLowerCase() == 'eth'
                })
                if (!initObj) {
                    initObj = this.walletList[0]
                }
                this.volume = initObj.usable
                this.pledgeCurrency = initObj.symbol
            });
        },
        debounceFn: debounce(function () {
            this.getLoanParamFn()
        }, 500),
        getLoanParamFn() {
            this.errorMsg = ''
            if (this.loanAmount == '') {
                showToast(this.$t('请输入借款数量'))
                return false
            }
            if (this.pledgeAmount == '') {
                showToast(this.$t('请输入质押数量'))
                return false
            }
            if (this.loanAmount * 1 < this.loanAmountMin * 1) {
                showToast(this.$t('最小借款数量为', { 'mount': this.loanAmountMin }))
                return false;
            }
            _getLoanParam({
                loanAmount: this.loanAmount,
                pledgeAmount: this.pledgeAmount,
                pledgeCurrency: this.pledgeCurrency,
                loanCycle: this.loanCycle
            }).then(res => {
                this.closeOut = res.closeOut
                this.pledgeRate = res.pledgeRate
                this.debtAmount = res.debtAmount
                this.interestAmount = res.interestAmount
            }).catch(error => {
                if (error.code == 10) {
                    this.loanAmount = ''
                    this.pledgeAmount = ''
                    this.closeOut = ''
                    this.pledgeRate = ''
                    this.interestAmount = ''
                    this.debtAmount = ''
                    this.errorMsg = this.$t('质押率过高，质押金额不得低于:') + error.msg
                } else if (error.code === 'ECONNABORTED') { showToast(this.$t('网络超时！')); }
                else if (error.msg !== undefined) { showToast(this.$t(error.msg)); }
            })
        },
        getAddOrder() {
            if (this.loanAmount == '') {
                showToast(this.$t('请输入借款数量'))
                return false
            }
            if (this.pledgeAmount == '') {
                showToast(this.$t('请输入质押数量'))
                return false
            }
            if (this.loanAmount * 1 < this.loanAmountMin * 1) {
                showToast(this.$t('最小借款数量为', { 'mount': this.loanAmountMin }))
                return false;
            }
            _addOrder({
                loanAmount: this.loanAmount,
                pledgeAmount: this.pledgeAmount,
                pledgeCurrency: this.pledgeCurrency,
                loanCycle: this.loanCycle
            }).then(res => {
                this.loanAmount = ''
                this.pledgeAmount = ''
                this.closeOut = ''
                this.pledgeRate = ''
                this.interestAmount = ''
                this.debtAmount = ''
                showToast(this.$t('借贷数量已发放,请至现货账号查看'));
                this.getList();
            }).catch(error => {
                if (error.code === 'ECONNABORTED') { showToast(this.$t('网络超时！')); }
                else if (error.msg !== undefined) { showToast(this.$t(error.msg)); }
            })
        }
    }
}
</script>

<style lang="scss" scoped>
#cryptos {
    font-size: 30px;

    .pledgeLoan {
        width: 100%;
        box-sizing: border-box;

        .van-popup {
            background: $main2_background;
        }

        .van-action-sheet__header {
            color: $text_color;
        }

        .van-cell {
            background: $main2_background;
            color: $text_color;

            font-size: 32px;
        }

        .van-cell-group {
            background: $main2_background;
        }
    }

    .contentBox {
        padding: 0 32px;
        position: relative;
        overflow: auto;
    }

    .imgBox {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 300px;

        img {
            width: 100%;
            height: 100%;
        }
    }

    .content {
        border-radius: 8px;
        padding: 36px 30px 36px 34px;
        position: relative;

        .inputBox {
            .selectedCon {
                flex: 1;
                display: flex;
                align-items: center;
            }

            input {
                flex: 1;
                border: none;
            }

            .right {
                border-left: 1px solid #B8BCC5;
            }
        }

    }

    .slectBox {
        position: absolute;
        left: 0;
        top: 114px;
        width: 100%;

        background: $main_background;
        border-radius: 4px;
        padding: 0px 20px 76px 20px;
        box-sizing: border-box;

        border: 1px solid $border_color;

        z-index: 2;

        &.slectBoxMax {
            max-height: 574px;
            overflow-y: auto;
        }
    }

    .item {
        height: 98px;
        display: flex;
        justify-content: space-between;
        align-items: center;

        color: $text_color;
    }

    .custom {
        width: 44px;
        height: 44px;

        img {
            width: 100%;
            height: 100%;
        }
    }

    .record-img {
        width: 30px !important;
        height: 35px !important;
        display: block;
    }

    .currency-title {
        overflow: hidden;
    }

    .day-select {
        padding: 30px;

        :deep(.van-cell) {
            padding: 20px 0;
        }
    }
}
</style>