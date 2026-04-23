<template>
    <div id="cryptos">
        <div class="exchangeSubmit">
            <assets-head title="" :goHome="true" />
            <div class="flex flex-col items-center">
                <img src="../../../assets/image/exchange/icon_9.png" v-if="!isLoading" alt="logo" class="w-40 h-40 mt-28" />
                <img src="../../../assets/image/exchange/icon_8.png" v-if="isLoading" alt="logo"
                    class="w-100 h-150 mt-28" />
                <p class="text-38 text-center pt-14 textColor" v-if="!isLoading">{{ $t('闪兑成功') }}</p>
                <!-- <p class="text-50 mt-23 text-center textColor">{{ detail.rate * detail.volume }}<span class="text-grey text-35">
                    {{ detail.symbol_to && detail.symbol_to.toUpperCase() }}</span></p> -->
                <span class="text-grey text-35 pt-60" v-if="isLoading">
                    {{ $t('结算中') }}
                </span>

                <ul class="px-8 pt-5 pb-8 mt-14 tabBackground rounded flex-1 w-full box-border" style="width: 90%;">
                    <li class="flex items-center">
                        <div class="flex-1 textColor text-left">
                            <div class="text-grey text-28 mt-14">{{ $t('从') }}</div>
                            <div class="text-30 mt-7">{{ detail.volume }}{{ detail.symbol && detail.symbol.toUpperCase() }}
                            </div>
                        </div>
                        <img src="../../../assets/image/exchange/icon_6.png" class="w-8 h-5" style="margin-top:-28px" />

                        <div class="flex-1 textColor text-right">
                            <div class="text-grey text-28 mt-14">{{ $t('至') }}</div>
                            <div class="text-30 mt-7" v-if="detail.volume">{{ (detail.volume * detail.rate).toFixed(5) }}
                                {{ detail.symbol_to && detail.symbol_to.toUpperCase() }}</div>
                        </div>
                    </li>
                    <!-- <li class="flex justify-between text-28 pt-10  mt-40  textColor">
                        <span class="text-grey">{{ $t('交易手续费') }}</span>
                        <span class="color-green">1</span>
                    </li> -->
                    <li class="flex justify-between text-28 pt-10  textColor">
                        <span class="text-grey">{{ $t('类型') }}</span>
                        <span class="textColor">{{ $t('市价') }}</span>
                    </li>
                    <!-- <li class="flex justify-between text-28 pt-25 mt-40">
                    <span class="text-grey">{{ $t('闪兑自') }}</span>
                    <span class="textColor">{{ detail.volume }} {{ detail.symbol && detail.symbol.toUpperCase() }}</span>
                </li> -->
                    <li class="flex justify-between text-28 pt-10  textColor">
                        <span class="text-grey">{{ $t('汇率') }}</span>
                        <div class="textColor flex items-center">{{ toValue }}≈{{ formValue }}
                            <img src="../../../assets/image/exchange/icon_4.png" @click="convert" class="w-11 h-11 ml-5" />
                        </div>
                    </li>

                </ul>
                <p class="tips px-9 mt-5" v-if="isLoading">
                    <van-icon name="warning-o" />{{ $t('您的订单正在处理中，可能需要5分钟才能完成。感谢您的耐心等待。您可以在订单历史中查看状态。') }}
                </p>
                <div class="h-24 rounded flex justify-between px-8 box-border items-center mt-24 mb-12 text-white w-full">
                    <p class="but rounded flex justify-center items-center bgDark"
                        @click="route('/quotes/index?tabActive=1')">
                        {{ $t('返回首页') }}</p>
                    <p class="but rounded btnMain text-white flex justify-center items-center"
                        @click="route('/cryptos/exchangeHistory')">
                        {{ $t('查看历史') }}</p>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import assetsHead from "@/components/Transform/assets-head/index.vue";
export default {
    data() {
        return {
            detail: {},
            toValue: '',
            formValue: '',
            isLoading: true
        }
    },
    components: {
        assetsHead,
    },
    created() {
        setTimeout(() => {
            this.isLoading = false
        }, 2000);
    },
    methods: {
        onClickLeft() {
            this.$router.go(-1)
        },
        route(path) {
            this.$router.push(path)
        },
        convert() {
            [this.toValue, this.formValue] = [this.formValue, this.toValue]
        }
    },
    beforeRouteEnter(to, from, next) {
        const { query: { data } } = to
        next(vm => {
            vm.detail = JSON.parse(data)
            vm.toValue = 1 + vm.detail.symbol.toUpperCase()
            vm.formValue = vm.detail.rate + vm.detail.symbol_to.toUpperCase()
        })
    }
}
</script>
<style lang="scss" scoped>
#cryptos {
    font-size: 30px;

    .exchangeSubmit {
        width: 100%;
        box-sizing: border-box;

        .but {
            width: 340px;
            height: 100px;
        }
    }

    .bg-grey-light {
        background: $light-grey;
    }

    .bg-grey-dark {
        background: $light-grey;
    }
}
</style>
