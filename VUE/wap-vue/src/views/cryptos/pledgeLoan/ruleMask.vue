<template>
    <div id="cryptos">
        <van-popup :value="value" @input="val => this.$emit('input', val)" round :closeable="true">
            <div class="text-40 textColor mb-14 text-center">{{ $t('规则说明') }}</div>
            <div class="mb-8">
                <div class="text-32 flex justify-between">
                    <span class="textColor">{{ $t('规则说明') }}</span>
                    <span class="skyColor">75%</span>
                </div>
                <div class="text-grey text-28 mt-4">{{ $t('可借金额等于质押金额*初始质押率') }}</div>
            </div>
            <div class="mb-8">
                <div class="text-32 flex justify-between">
                    <span class="textColor">{{ $t('补仓质押率') }}</span>
                    <span class="skyColor">60%</span>
                </div>
                <div class="text-grey text-28 mt-4">
                    {{ $t('(订单借贷资产+累计利息)转化为质押资产价格/质押资产价值≥60%，系统会提醒您补充质押资产或提前还款*初始质押率') }}
                </div>
            </div>
            <div class="mb-8">
                <div class="text-32 flex justify-between">
                    <span class="textColor">{{ $t('平仓质押率') }}</span>
                    <span class="skyColor">85%</span>
                </div>
                <div class="text-grey text-28 mt-4">
                    {{ $t('(订单借贷资产+累计利息)转化为质押资产价格/质押资产价值≥85%，系统会自动卖出/扣除抵押资产以尝还到款。请及时补充抵押资金') }}
                </div>
            </div>
            <div class="mb-8">
                <div class="text-32 flex justify-between">
                    <span class="textColor">{{ $t('利率费用') }}</span>
                    <span class="skyColor"></span>
                </div>
                <div class="text-grey text-28 mt-4">{{ $t('小时利率0.2%;日利率4.8%;提前还贷手续费,到款以小时计算,不足1小时按1小时计算') }}</div>
            </div>
        </van-popup>
    </div>
</template>

<script>
import { Popup } from "vant";
export default {
    props: {
        value: {
            type: Boolean,
            default: false,
        },
    },
    components: {
        [Popup.name]: Popup,
    },
}
</script>

<style lang="scss" scoped>
#cryptos {
    .van-popup {
        width: 700px;
        padding: 38px 32px 64px 32px;
    }

    .skyColor {
        color: #13D3EB;
    }
}
</style>