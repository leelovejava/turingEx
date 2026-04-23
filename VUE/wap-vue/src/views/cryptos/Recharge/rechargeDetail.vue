<template>
    <div class="rechargeDetail">
        <van-nav-bar :title="type2 + $t('详情')" left-arrow @click-left="onClickLeft" />
        <div class="flex flex-col items-center justify-center">
            <div class="text-grey text-32 num-text">{{ $t('数量') }}</div>
            <div class="text-50 textColor">
                <span class="font-bold">{{ info.amount }}</span>
                <span class="text-44 ml10">{{ info.coin }}</span>
            </div>
            <div class="flex items-center justify-center mt53 mb16">


                <div class="status-icon status-bg3" v-if="info.state == 0"></div>
                <div class="status-icon status-bg1" v-if="info.state == 3"></div>
                <div class="status-icon status-bg4" v-if="info.state == 4"></div>
                <span class="ml10 text-32 textColor">{{ type2 }}{{ status[info.state] && status[info.state].split('_')[0]
                }}</span>
            </div>
            <div class="text-grey text-26 pl-30 pr-30">
                {{ $t('数字币已经') }}{{ $t(type2) }}{{ status[info.state] &&
                    status[info.state].split('_')[0] }}{{ $t('。您可以在钱包账户中查看详情。') }}
            </div>
        </div>
        <div class="border-light-grey mt42"></div>
        <div class="text-30 pl-30 pr-30 mt53">
            <div class="flex justify-between mb74">
                <div class="text-grey">{{ $t('确认数') }}</div>
                <div class="textColor">12/12</div>
            </div>
            <div class="flex justify-between mb74">
                <div class="text-grey">{{ type2 }}{{ $t('account') }}</div>
                <div class="textColor">{{ $t('钱包账户') }}</div>
            </div>
            <div class="flex justify-between mb74">
                <div class="text-grey">{{ $t('转账网络') }}</div>
                <div class="textColor">{{ info.coin_blockchain }}</div>
            </div>
            <div class="flex justify-between mb74">
                <div class="text-grey">{{ $t('地址') }}</div>
                <div class="flex items-center">
                    <div class="text-30 text-underline textColor">{{ info.from || info.to }}</div>
                    <img src="../../../assets/image/assets-center/copy.png" width="15" height="15" class="ml10 ml-20"
                        @click="copy(info.from || info.to)" />
                </div>
            </div>
            <!--             <div class="flex justify-between mb74">-->
            <!--                <div class="text-grey">{{ $t('交易哈希') }}</div>-->
            <!--                <div class="flex">-->
            <!--                    <div class="text-30 text-underline">{{info.tx || '-'}}</div>-->
            <!--                    <img src="../../assets/image/assets-center/copy.png" width="15" height="15" class="ml10 ml-20" @click="copy(info.tx)"/>-->
            <!--                </div>-->
            <!--             </div>-->
            <div class="flex justify-between mb74">
                <div class="text-grey">{{ $t('日期') }}</div>
                <div class="textColor">{{ info.create_time }}</div>
            </div>
            <div class="flex justify-between mb74">
                <div class="text-grey">{{ $t('备注') }}</div>
                <div class="textColor">{{ info.failure_msg || '-' }}</div>
            </div>
        </div>

    </div>
</template>

<script>
import { _rechargeDetail, _withdrawDetail } from '@/service/fund.api'
import { showSuccessToast } from 'vant';
export default {
    name: "rechargeDetail",
    props: {
        type: {
            type: String,
            default: '充值'
        }
    },
    computed: {
        type2() {
            let res
            if (this.type === '充值') {
                res = this.$t('充值') + ' '
            } else {
                res = this.$t('提现') + ' '
            }
            return res
        },
    },
    data() {
        return {
            order_no: '',
            info: {
                state: 0
            },
            status: [this.$t('确定中') + '_identifing', this.$t('成功') + '_small-success', this.$t('失败') + '_icon-close']
        }
    },
    created() {
        this.order_no = this.$route.query.order_no
        const _api = this.type === '充值' ? _rechargeDetail : _withdrawDetail
        _api(this.order_no).then(data => {
            this.info = data
        })
    },
    methods: {
        copy(val) {
            if (!val) {
                return
            }
            navigator.clipboard.writeText(val).then(() => {
                showSuccessToast(this.$t('已复制'))
            })
        },
        onClickLeft() {
            this.$router.go(-1);
        },
    }
}
</script>
<style lang="scss" scoped>
.rechargeDetail {
    font-size: 30px;
    width: 100%;
    box-sizing: border-box;

    :deep(.van-hairline--bottom) {
        &::after {
            border-color: $border_color;
        }
    }
}

.num-text {
    margin-top: 20px;
    margin-bottom: 20px;
}

.mb53 {
    margin-bottom: 20px;
}

.ml10 {
    margin-left: 10px;
}

.mt53 {
    margin-top: 10px;
}

.mb16 {
    margin-bottom: 16px;
}

.mt42 {
    margin-top: 42px;
}

.mb74 {
    margin-bottom: 74px;
}

.text-underline {
    text-decoration: underline;
}

.status-icon {
    width: 40px;
    height: 40px;
}

.status-bg1 {
    background: url('@/assets/image/Record/icon1.png') no-repeat center;
    background-size: 100% 100%;
}

.status-bg2 {
    background: url('@/assets/image/Record/icon2.png') no-repeat center;
    background-size: 100% 100%;
}

.status-bg3 {
    background: url('@/assets/image/Record/icon3.png') no-repeat center;
    background-size: 100% 100%;
}

.status-bg4 {
    background: url('@/assets/image/Record/icon4.png') no-repeat center;
    background-size: 100% 100%;
}

.text-underline {
    width: 400px;
    overflow: hidden;
    word-break: break-all;
    word-wrap: break-word;
    text-align: right;
}
</style>
