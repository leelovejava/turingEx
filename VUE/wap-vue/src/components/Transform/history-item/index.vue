<template>
    <div class="history-item py-20 text-28">
        <div class="flex justify-between header-info">
            <div class="title">
                <span :class="entrust.offset == 'open' ? 'text-green' : 'text-red'"> {{
                    handleWordType(entrust.order_price_type) }}</span>
                <span :class="entrust.offset == 'open' ? 'text-green' : 'text-red'"> / </span>
                <span :class="entrust.offset == 'open' ? 'text-green' : 'text-red'">{{ handleWordOffset(entrust.offset)
                }}</span>&nbsp;
                <span>{{ entrust.name }}</span>
            </div>
            <div class="text" @click.stop="goDetail(entrust.order_no)">
                <span v-if="state == 'submitted'" @click.stop="cancelSingle(entrust.order_no)">{{ $t('撤单') }}</span>
                <span v-if="state == 'created'">{{ $t('已完成') }}</span>
                <span v-if="state == 'canceled'">{{ $t('canceled') }}</span>
                <van-icon name="arrow" />
            </div>
        </div>
        <div class="flex info">
            <div class="data-item ">
                <div class="title">{{ $t('时间') }}</div>
                <div class="text">{{ entrust.create_time ? entrust.create_time.substring(5, entrust.create_time.length) :
                    '--' }}</div>
            </div>
            <div class="data-item right-text">
                <div class="title">{{ $t('委托价') }}（{{ unit }}）</div>
                <div class="text">{{ entrust.price }}</div>
            </div>

        </div>
        <div class="flex info">
            <div class="data-item">
                <div class="title">{{ $t('交易额') }}（{{ unit }}）</div>
                <div class="text">{{ entrust.total }}</div>
            </div>
            <div class="data-item right-text">
                <div class="title">{{ $t('成交总额') }}（{{ unit }}）</div>
                <div class="text">{{ entrust.total }}</div>
            </div>
        </div>
        <div class="flex info">
            <div class="data-item">
                <div class="title">{{ $t('成交均价') }}（{{ unit }}）</div>
                <div class="text">{{ entrust.price }}</div>
            </div>
            <div class="data-item right-text">
                <div class="title">{{ $t('成交量') }}（{{ entrust.symbol }}）</div>
                <div class="text">{{  entrust.volume }}</div>
            </div>
        </div>
    </div>
</template>

<script>
import { Circle } from 'vant'
export default {
    name: 'history-item', // 订单委托项
    components: {
        [Circle.name]: Circle
    },
    props: {
        entrust: {
            type: Object,
            default() {
                return {}
            }
        },
        state: {
            type: String,
            default: ''
        },
        unit: {
            type: String,
            default: 'USDT'
        },

    },
    data() {
        return {
            text: '0',
            currentRate: 0,
            finishRate: 100,
            finishText: '100%'
        }
    },
    watch: {
    },
    methods: {
        handleWordType(type) {
            return type === 'limit' ? this.$t('限价') : this.$t('市价');
        },
        handleWordOffset(offset) {
            return offset === 'open' ? this.$t('买入') : this.$t('卖出');
        },
        goDetail(order_no) { // 详情
            this.$router.push({ path: '/cryptos/symbolOrderDetail', query: { 'order_no': order_no } })
        },
        cancelSingle(order_no) { // 撤单
            this.$emit('cancelOrder', order_no)
        }
    }
}
</script>
<style lang="scss"  scoped>
.history-item {
    border-bottom: 1px solid $border_color;

    padding: 40px 0;

    :deep(.van-circle__text) {
        color: $text_color;
    }

    :deep(.van-circle__hover) {
        color: $text_color;
    }

    .header-info {
        align-items: center;
        padding-bottom: 30px;

        .title {
            font-size: 32px;
            font-weight: 500;
            display: flex;
            align-items: center;

            span {
                color: $text_color;
            }

            .text-green {
                color: #00C48C;
            }

            .text-red {
                color: #FF4D4F;
            }
        }

        .text {
            font-size: 28px;
            color: $text_color6;
        }
    }

    .data-item {
        flex: 1;

        .title {
            color: $dark-grey;
            font-size: 24px;
        }

        .text {
            font-size: 28px;

            color: $text_color;

            margin-top: 10px;
        }
    }

    .right-text {
        text-align: right;
    }

    .center-text {
        text-align: center;
    }

    .info {
        margin-bottom: 20px;
    }
}

.btn-wrap {
    button {
        padding: 0 26px;
        border-radius: 6px;
        font-size: 26px;

    }

    .detailBtn {
        border: 1px solid #fff;
        color: $text_color;
        justify-content: space-between;
    }

}

.text-red {
    color: $red !important;
}

.text-blue {
    color: $color_main !important;
}
</style>
