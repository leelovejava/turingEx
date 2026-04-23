<template>
    <van-popup round :value="props.showPopup" style="{background: red !important;}" @input="val => this.$emit('input', val)"
        position="bottom" :close-on-click-overlay="false">
        <div class="relative px-15 pt-23 px-4 pb-10">
            <div class="flex justify-end pt-4 items-center">
                <img @click="onClose" class="z-20 w-8 h-8" src="./close.png" alt="" />
            </div>
            <h4 class="text-24 font-medium text-black title">{{ $t('confirmOrder') }}</h4>
            <div class="flex flex-col justify-center pay-info">
                <p class="text-center mt-4">{{ $t('Actual') }}</p>
                <h2 class="font-bold text-base text-center mt-2 money-title mb-2">{{ volume_last }}</h2>
                <section>
                    <ul class="mt-2 mb-4 border-b-1">
                        <li class="flex justify-between pt-1 pb-1">
                            <span class="ash">{{ $t('WithdrawalAddress') }}</span>
                            <span>{{ address }}</span>
                        </li>
                        <li class="flex justify-between pt-1 pb-1">
                            <span class="ash">{{ $t('mainNetwork') }}</span>
                            <span>{{ blockchain_name }}</span>
                        </li>
                        <li class="flex justify-between pt-1 pb-1">
                            <span class="ash">{{ $t('WithdrawalSource') }}</span>
                            <span>{{ $t('walletAccount') }}</span>
                        </li>
                    </ul>
                </section>
                <section>
                    <ul class="mt-2 mb-4 border-b-1">
                        <li class="flex justify-between pt-1 pb-1">
                            <span class="ash">{{ $t('Currency') }}</span>
                            <span>USDT</span>
                        </li>
                        <li class="flex justify-between pt-1 pb-1">
                            <span class="ash">{{ $t('amount') }}</span>
                            <span>{{ amount }}</span>
                        </li>
                        <li class="flex justify-between pt-1 pb-1">
                            <span class="ash">{{ $t('NetworkFee') }}</span>
                            <span>{{ fee }}</span>
                        </li>
                    </ul>
                </section>
                <p class="my-5 text-24" style="color:#535862">{{ $t('illustrate1') }}</p>
                <van-button type="primary" @click="onConfirm">{{ $t('confirmOrder') }}</van-button>
            </div>
        </div>
    </van-popup>
</template>

<script setup>
import { useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
const { t } = useI18n()
const router = useRouter()
const props = defineProps({
    showPopup: {
        type: Boolean,
        default: false
    },
    blockchain_name: {
        type: String,
        default: ''
    },
    address: {
        type: String,
        default: ''
    },
    volume_last: {
        default: 0
    },
    amount: {
        type: String,
        default: ''
    },
    fee: {
        default: ''
    },
    session_token: {
        type: String,
        default: ''
    },
    coin: {
        type: String,
        default: ''
    }
})


const emits = defineEmits(['close'])

const onClose = () => {
    emits('close')
}

const onConfirm = () => {
    onClose()
    router.push({
        path: '/exchange/fund-password-verify',
        query: {
            type: 'bankWithdraw',
            session_token: props.session_token,
            amount: props.amount,
            from: props.address,
            channel: props.coin + '_' + props.blockchain_name
        }
    })
}

</script>
<style lang="scss" scoped>
.title {
    text-align: center;
    color: #1F2025;
    font-weight: bold;
    font-size: 20px;
}

.pay-info {
    color: $black;
    font-size: 14px;
}


.ash {
    color: $text_color1;
}

.money-title {
    font-size: 24px;
    font-weight: bold
}

.returnleft {
    position: absolute;
    left: 0;
    top: 4px;
}
</style>