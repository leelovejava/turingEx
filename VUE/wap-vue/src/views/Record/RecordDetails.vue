<template>
    <div class="RecordDetails pb-12">
        <fx-header>
            <template #title>{{ title }}&nbsp;{{ $t('详情') }}</template>
        </fx-header>
        <div class="quantity mt-8">{{ $t('金额') }}</div>
        <div class="money mt-8" v-if="type == 0">
            <span>{{ payInfo.amount }}</span> {{ payInfo.currency }}
        </div>
        <div class="money mt-8" v-else>
            <span>{{ payInfo.volume }}</span> {{ payInfo.coin }}
        </div>
        <div class="status flex mt-9" v-if="type == 0">
            <div class="status-icon status-bg3" v-if="payInfo.state == 0"></div>
            <div class="status-icon status-bg1" v-if="payInfo.state == 3"></div>
            <div class="status-icon status-bg4" v-if="payInfo.state == 4"></div>
            <template v-if="payInfo.state == 0">
                <div class="status-icon status-icon-color3 mr-2"></div>
                {{ $t('confirming') }}
            </template>
            <template v-if="payInfo.state == 3">
                <div class="status-icon status-icon-color1 mr-2"></div>
                {{ $t('success') }}
            </template>
            <template v-if="payInfo.state == 4">
                <div class="status-icon status-icon-color2 mr-2"></div>
                {{ $t('Cancelled') }}
            </template>
        </div>
        <div class="status flex mt-9" v-else>
            <div class="status-icon status-bg3" v-if="payInfo.state == 0"></div>
            <div class="status-icon status-bg1" v-if="payInfo.state == 1"></div>
            <div class="status-icon status-bg4" v-if="payInfo.state == 2"></div>
            <template v-if="payInfo.state == 0">
                <div class="status-icon status-icon-color3 mr-2"></div>
                {{ $t('confirming') }}
            </template>
            <template v-if="payInfo.state == 1">
                <div class="status-icon status-icon-color1 mr-2"></div>
                {{ $t('success') }}
            </template>
            <template v-if="payInfo.state == 2">
                <div class="status-icon status-icon-color2 mr-2"></div>
                {{ $t('fail') }}
            </template>
        </div>
        <div class="text mt-3" v-if="type == 0">
            <span v-if="payInfo.state == 0">{{ type == 0 ? payInfo.currency :
                payInfo.coin }}&nbsp;{{ title }}&nbsp;{{ $t('processing') }}...</span>
            <span v-if="payInfo.state == 3">{{ type == 0 ? payInfo.currency :
                payInfo.coin }}&nbsp;{{ $t('already') }}{{ title }}&nbsp;{{ $t('checkTheDetails') }}</span>
            <span v-if="payInfo.state == 4">{{ type == 0 ? payInfo.currency :
                payInfo.coin }}{{ title }}{{ $t('Cancelled') }}，{{ $t('contactPlatform') }} </span>
        </div>
        <div class="text mt-3" v-else>
            <span v-if="payInfo.state == 0">{{ type == 0 ? payInfo.currency :
                payInfo.coin }}&nbsp;{{ title }}&nbsp;{{ $t('processing') }}...</span>
            <span v-if="payInfo.state == 1">{{ type == 0 ? payInfo.currency :
                payInfo.coin }}&nbsp;{{ $t('already') }}{{ title }}{{ $t('checkTheDetails') }}</span>
            <span v-if="payInfo.state == 2">{{ type == 0 ? payInfo.currency :
                payInfo.coin }}&nbsp;{{ title }}{{ $t('Cancelled') }}，{{ $t('contactPlatform') }} </span>
        </div>
        <ul class="px-4" v-if="type == 0">
            <!-- <li class="flex mt-10 justify-between">
                <div class="ash">确认数</div>
                <div>12/12</div>
            </li> -->
            <li class="flex mt-10 justify-between">
                <div class="ash">{{ title }}{{ $t('account') }}</div>
                <div>{{ payInfo.paramName1 }}</div>
            </li>

            <li class="flex mt-10 justify-between">
                <div class="ash">{{ $t('type') }}</div>
                <div>{{ title }}</div>
            </li>
            <li class="flex mt-10 justify-between">
                <div class="ash"> {{ $t('BankAccount') }}</div>
                <div>{{ payInfo.paramValue1 }}</div>
            </li>
            <li class="flex mt-10 justify-between">
                <div class="ash">{{ $t('bankTitle') }}</div>
                <div>{{ payInfo.paramValue2 }}</div>
            </li>
            <li class="flex mt-10 justify-between">
                <div class="ash">{{ $t('orderNumber') }}</div>
                <div>{{ payInfo.orderNo }}</div>
            </li>
            <li class="flex mt-10 justify-between">
                <div class="ash">{{ $t('date') }}</div>
                <div>{{ payInfo.createTime ? dayjs(payInfo.createTime).format('YYYY-MM-DD HH:mm:ss') : '' }}</div>
            </li>
            <li class="flex mt-10 justify-between">
                <div class="ash">{{ $t('Remarks') }}</div>
                <div>{{ payInfo.remark }}</div>
            </li>
        </ul>
        <ul class="px-4" v-else>
            <li class="flex mt-10 justify-between">
                <div class="ash">{{ $t('TheInternet') }}</div>
                <div>{{ payInfo.coin_blockchain }}</div>
            </li>
            <li class="flex mt-10 justify-between">
                <div class="ash">{{ $t('free') }}</div>
                <div>{{ payInfo.fee }}</div>
            </li>
            <li class="flex mt-10 justify-between">
                <div class="ash">{{ isCZ == 0 ? $t('paymentAddress') : $t('paymentReceivingAddressMethod') }}</div>
                <div class="flex">
                    <div class="address" v-if="payInfo.from || payInfo.to">{{ isCZ == 0 ? payInfo.from : payInfo.to }}
                    </div>
                    <div v-else>--</div>
                    <span class="copy" @click="copy(isCZ == 0 ? payInfo.from : payInfo.to)"
                        v-if="payInfo.from || payInfo.to"></span>

                </div>
            </li>
            <li class="flex mt-10 justify-between">
                <div class="ash">{{ $t('hash') }}</div>
                <div>{{ payInfo.tx ? payInfo.tx : '--' }}<span class="copy" v-if="payInfo.tx"
                        @click="copy(payInfo.tx)"></span></div>
            </li>
            <li class="flex mt-10 justify-between">
                <div class="ash">{{ $t('date') }}</div>
                <div>{{ payInfo.create_time }}</div>
            </li>
            <li class="flex mt-10 justify-between">
                <div class="ash">{{ $t('orderNumber') }}</div>
                <div>{{ payInfo.order_no }}</div>
            </li>
            <li class="flex mt-10 justify-between">
                <div class="ash">{{ $t('Remarks') }}</div>
                <div>{{ payInfo.remark }}</div>
            </li>
        </ul>
    </div>
</template>

<script setup>
import { onBeforeMount, ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getc2cOrder, _getRechargeBlockchain, _getWithdraw } from "@/service/recharge.api.js";
import useClipboard from "vue-clipboard3";
import { showToast } from 'vant'
import { useI18n } from "vue-i18n";
import dayjs from 'dayjs';
const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const { toClipboard } = useClipboard();
const type = ref(0)
const orderNo = ref(null)
const payInfo = ref({})
const isCZ = ref(0)
const title = ref('充值')
const copy = async (val) => {
    console.log(val)
    try {
        await toClipboard(val);
        showToast(t('copySuccess'));
    } catch (e) {
        console.error(e);
    }
}
onMounted(async () => {
    console.log(route.query)
    type.value = route.query.type
    isCZ.value = route.query.isCZ
    orderNo.value = route.query.orderNo
    if (type.value == 0) {
        getc2cOrderDetails(orderNo.value)
    } else {
        if (isCZ.value == 0) {
            getRechargeBlockchain(orderNo.value)
            title.value = t('recharge')
        } else {
            getWithdraw(orderNo.value)
            title.value = t('withdraw')
        }
    }
})
//银行卡订单详情
const getc2cOrderDetails = (orderNo) => {
    getc2cOrder({ order_no: orderNo }).then((res) => {
        payInfo.value = res
        console.log(payInfo.value)
        title.value = payInfo.value.direction == 'recharge' ? t('recharge') : t('withdraw')
    })

}
//数字货币充值
const getRechargeBlockchain = (orderNo) => {
    _getRechargeBlockchain({ order_no: orderNo }).then((res) => {
        payInfo.value = res
    })

}
//数字货币提现
const getWithdraw = (orderNo) => {
    _getWithdraw({ order_no: orderNo }).then((res) => {
        payInfo.value = res
    })

}
</script>
<style lang="scss" scoped>
.RecordDetails {
    font-size: 14px;

    .quantity {
        color: $text_color1;
        text-align: center;
    }

    .money {
        font-size: 26px;
        text-align: center;

        span {
            font-weight: bold;
        }
    }

    .status {
        align-items: center;
        justify-content: center;
        font-size: 16px;
        color: $text_color1;

        .status-icon {
            width: 20px;
            height: 20px;
        }
    }

    .text {
        text-align: center;
        font-size: 14px;
        color: $text_color1;
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

    ul {
        li {
            .address {
                max-width: 200px;
                word-break: break-all;
                word-wrap: break-word;
                padding-right: 10px;
            }

            .copy {
                display: block;
                width: 13px;
                height: 15px;
                background: url('@/assets/image/order/copy.png') no-repeat center;
                background-size: 100% 100%;
                margin-top: 4px;
            }
        }
    }

    .ash {
        color: $dark-grey;
    }
}
</style>