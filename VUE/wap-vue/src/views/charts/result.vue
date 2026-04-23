<template lang="pug">
div.reuslt.flex.h-screen.w-full.flex.justify-center.items-center.relative
    div
        p.flex.justify-center.my-2
            img.w-12(src="@/assets/image/order/orderWait.png", alt="" v-if="status==0")
            img.w-12(src="@/assets/image/order/orderComplete.png", alt="" v-else)
        template(v-if="!status")    
            h2.text-primary.text-lg.font-bold.my-2.text-center {{$t('orderProcessing')}}...
            p.text-center {{$t('buylimit')}} {{symbol}} {{$t('exist')}} {{chartData.ask}}
        template(v-else)
            p.finish {{$t('Finish')}}
            p.text-center.buy {{$t('buylimit')}} {{chartData.close}}/{{chartData.open}} {{symbol}} at {{chartData.bid}}   
    div.fixed.bottom-0.left-0.flex.w-full.safe-area-pb
        van-button.flex-1(@click="onButton") {{!status  ? $t('hide') : $t('Finish')}}    
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from 'vue-router';
import {
    showToast
} from 'vant';
import { _initOpen, _initClose, _orderOpen } from "@/service/trade.api.js";
import { useI18n } from "vue-i18n";
const { t } = useI18n()
const router = useRouter()
const route = useRoute()

const status = ref(0)
const initOpen = ref({})
const initClose = ref({})
const direction = ref('buy')
const symbol = ref('')
const deviation = ref(1) //1个单位
const formData = ref({
    symbol: '', // 币种
    session_token: '',
    direction: 'buy', // 买or卖
    price_type: 'opponent', // 市价or限价
    lever_rate: 1, // 杠杆
    price: '',
    amount: '', // 数量
})
const chartData = ref({})
const onButton = () => {
    // router.back()
    // router.push('/exchange/list')
    router.push('/trade/index')
}
onMounted(async () => {
    chartData.value = JSON.parse(route.query.chartData)
    direction.value = route.query.direction
    symbol.value = route.query.symbol
    deviation.value = route.query.deviation * 1
    await initParams()
    order()
})
const initParams = async () => {
    if (direction.value == 'buy') {
        initOpen.value = await _initOpen({ symbol: symbol.value })
    } else {
        initClose.value = await _initClose({ symbol: symbol.value })

    }
}
const order = () => {
    let _order = null
    formData.value.symbol = symbol.value
    formData.value.direction = direction.value
    formData.value.lever_rate = route.query.lever_rate
    formData.value.price_type = route.query.price_type == 0 ? 'opponent' : 'limit'
    if (direction.value === 'buy') {
        formData.value.session_token = initOpen.value.session_token
        formData.value.price = formData.value.price_type == 'opponent' ? chartData.value.ask : route.query.price
        formData.value.amount = deviation.value
        _order = _orderOpen
    } else {
        formData.value.session_token = initClose.value.session_token
        formData.value.price = formData.value.price_type == 'opponent' ? chartData.value.ask : route.query.price
        // formData.value.amount = initClose.value.amount
        formData.value.amount = deviation.value
        _order = _orderOpen
    }
    _order(formData.value).then(res => {
        showToast(t('SuccessfulOperation'));
        status.value = 1
        // setTimeout(() => {
        //     router.push('/exchange/list')
        // }, 1000)
    }).catch(error => {
        router.go(-1)
    })
}
</script>
<style lang="scss">
.buy {
    width: 60%;
    text-align: center;
    margin: 0 auto;
}

.finish {
    font-size: 18px;
    font-weight: bold;
    text-align: center;
    padding: 10px 0;
}
</style>