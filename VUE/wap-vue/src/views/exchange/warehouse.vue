<template>
    <div>
        <van-nav-bar :title="$t('closePosition')">
            <template #left>
            </template>
            <template #right>
                <van-icon class="cross-icon" name="cross" @click="$router.back()" />
            </template>
        </van-nav-bar>
        <div class="cell-item flex mt-20" v-if="dataInfo.item">
            <div class="cell-item-left flex-1">{{ t('Symbol') }}</div>
            <div class="cell-item-right">{{ dataInfo.item.symbol }}</div>
        </div>
        <div class="cell-item flex" v-if="dataInfo.item">
            <div class="cell-item-left flex-1">{{ t('position') }}</div>
            <div class="cell-item-right">
                {{ detail.volume / (detail.lever_rate ? detail.lever_rate : 1) }}*{{ detail.lever_rate ? detail.lever_rate :
                    1
                }}x
            </div>
        </div>
        <div class="cell-item flex" v-if="dataInfo.item">
            <div class="cell-item-left flex-1">{{ t('exchangeDirection') }}</div>
            <div class="cell-item-right">{{ dataInfo.item.direction == 'buy' ? $t('buy') : $t('sell') }}</div>
        </div>
        <div class="cell-item flex" v-if="dataInfo.item">
            <div class="cell-item-left flex-1">{{ t('执行点位') }}</div>
            <div class="cell-item-right">{{ detail.trade_avg_price }}</div>
        </div>
        <div class="cell-item flex" v-if="dataInfo.item">
            <!-- 实时点位取平仓价格字段，执行点位取建仓成本 -->
            <div class="cell-item-left flex-1">{{ t('实时点位') }}</div>
            <div class="cell-item-right">{{ detail.close_avg_price }}</div>
        </div>
        <div class="cell-item flex" v-if="dataInfo.item">
            <div class="cell-item-left flex-1">{{ t('盈利') }}</div>
            <div :class="{ 'cell-item-right': true, 'red': +detail.profit < 0, 'green': +detail.profit > 0 }">{{
                detail.profit }}</div>
        </div>
        <div class="fixed-bottom ">
            <van-button @click="$router.back()">{{ $t('Cancel') }}</van-button>
            <van-button type="primary" @click="cancelOrder">{{ $t('closePosition') }}</van-button>
        </div>
    </div>
</template>
<script setup>
import {
    _contractOrderClose,
    _orderHoldDetail
} from "@/service/trade.api.js";
import { onMounted, onBeforeUnmount, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router';
import {
    showToast
} from 'vant';
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const year = ref('')
const detail = ref({})
const dataInfo = ref({})
const timer = ref(null)

onMounted(() => {
    let date = new Date()
    year.value = date.getFullYear()
    dataInfo.value = JSON.parse(route.query.checkInfo)
    fetchDetail(dataInfo.value.item.order_no);
    // timer.value = setInterval(()=>{
    //     fetchDetail(dataInfo.value.item.order_no);
    // },1000);  

})

onBeforeUnmount(() => {
    clearInterval(timer.value)
    timer.value = null
})

const cancelOrder = () => {
    let obj = {
        order_no: dataInfo.value.item.order_no
    }
    console.log(obj)
    _contractOrderClose(obj).then((res) => {
        showToast(t('SuccessfulOperation'))
        setTimeout(() => {
            router.back()
        }, 1000)
    })
}

const fetchDetail = (order_no) => {
    _orderHoldDetail(order_no).then(data => {
        detail.value = data
    })
}
</script>
<style lang="scss" scoped>
.green {
    color: $green !important;
}

.red {
    color: $red !important;
}

.cross-icon {
    font-size: 18px;
    color: #494A50;
}

.mt-20 {
    margin-top: 20px;
}

.cell-item {
    padding: 0 15px;
    height: 45px;
    align-items: center;
    justify-content: center;

    .cell-item-left {
        font-size: 16px;
        color: $text_color5;
    }

    .cell-item-right {
        font-size: 18px;
        color: #1F2025;
    }
}

.fixed-bottom {
    position: fixed;
    bottom: constant(safe-area-inset-bottom);
    bottom: env(safe-area-inset-bottom);
    left: 0;
    width: 100%;
    display: flex;
    justify-content: space-between;
    border-top: 1px solid $border-grey;
    padding: 20px 15px;
}

:deep(.van-button--primary) {
    height: 50px;
    width: 165px;
    border-radius: 5px;
    background: $btn_main;
}

:deep(.van-button--default) {
    height: 50px;
    width: 165px;
    border-radius: 5px;
    border: 1px solid $btn_main;
    color: $active_line;
}
</style>