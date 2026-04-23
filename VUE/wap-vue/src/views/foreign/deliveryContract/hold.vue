<template>
    <!-- 持有仓位列表 -->
    <div class="positionList">
        <div class="border-b-color" v-for="item in listData" :key="item.order_no">
            <div class="flex justify-between pt-5 pb-5">
                <div class="flex flex-col">
                    <div class="flex items-center">
                        <div class="pl-6 pr-6 pt-2 pb-2 text-white open-btn"
                            :class="item.direction == 'buy' ? ' bg-green' : 'bg-red'">
                            {{ item.direction == 'buy' ? $t('开多') : $t('开空') }}
                        </div>
                        <div class="ml-2 text-28 font-semibold">
                            <span class="textColor">{{ item.name }} {{ $t('delivery') }}</span>
                            <span class="text-grey1 font-normal ml-2 mr-2">{{ $t('全仓') }} {{ item.lever_rate
                            }}x</span>
                        </div>
                        <img v-if="item.direction == 'buy'" src="@/assets/image/public/green-leverage.png" alt=""
                            class="w-8 h-8" />
                        <img v-else src="@/assets/image/public/red-leverage.png" alt="" class="w-8 h-8" />
                    </div>
                </div>
            </div>
            <div class="flex justify-between">
                <div>
                    <div class="text-grey1">{{ $t('buyPrice') }}(USD)</div>
                    <div class="mt-2 textColor">{{ item.open_price }}</div>
                </div>
                <div>
                    <div class="text-grey1 text-right">{{ $t('现价') }}</div>
                    <div class="mt-2" :class="item.close_price > item.open_price ? 'text-green' : 'text-red'">
                        {{ item.close_price }}</div>
                </div>
            </div>
            <div class="flex pt-5 pb-5">
                <div class="flex-1">
                    <div class="text-grey1">{{ $t('方向') }}</div>
                    <div class="mt-2" :class="item.direction === 'buy' ? 'text-green' : 'text-red'">{{ item.direction ===
                        'buy' ? $t('开多') : $t('开空') }}</div>
                </div>
                <div class="flex-1">
                    <div class="text-grey1 text-center">{{ $t('number') }}</div>
                    <div class="mt-2 text-center textColor">{{ item.volume }}</div>
                </div>
                <div class="flex-1 flex flex-col items-end">
                    <div class="text-grey1">{{ $t('盈亏') }}</div>
                    <div class="mt-2" :class="item.profit / 1 > 0 ? 'text-green' : 'text-red'">
                        {{ item.profit / 1 > 0 ? '+' + item.profit : item.profit }}
                    </div>
                </div>
            </div>
            <div class="flex pb-4">
                <div class="flex-1">
                    <div class="text-grey1">{{ $t('剩余时间') }}</div>
                    <div class="mt-2 textColor">{{ fomatTime(item.remain_time) }}</div>
                </div>
                <div class="flex-1">
                    <div class="text-grey1  text-center">{{ $t('交割时间') }}</div>
                    <div class="mt-2  text-center textColor">{{ item.time_num + item.time_unit }}</div>
                </div>
                <div class="flex-1">
                    <div class="text-grey1 text-right">{{ $t('操作') }}</div>
                    <div class="mt-2 colorMain text-right" @click="goDetail(item)">{{ $t('详情') }}</div>
                </div>
            </div>
        </div>
        <van-popup round v-model:show="show" teleport="body">
            <popup-delivery :showBtns="true" :detailData="detailData" :price="price" @timeEnd="handleTimeEnd"
                :key="detailData.order_no" @close="onClose" />
        </van-popup>
    </div>
</template>

<script setup>
import { Popup } from 'vant';
import PopupDelivery from '@/components/foreign/foreign-popup-delivery/index.vue'
import { _futrueOrderDetail } from "@/service/trade.api.js";
import { ref, computed } from 'vue';


let show = ref(false)
let detailData = ref({})
let timeout = ref(null)
const props = defineProps({
    listData: {
        type: Array,
        default() {
            return []
        }
    },
    price: {
        type: [Number, String],
        default: '0.00'
    },
})

const clearTimeoutFn = () => {
    clearTimeout(timeout.value)
    timeout.value = null
}
//合约时间结束后显示详情
const handleTimeEnd = (order) => {
    _futrueOrderDetail(order).then(data => {
        clearTimeoutFn()
        detailData.value = data
        if (data.state !== 'created') {
            timeout.value = setTimeout(() => {
                handleTimeEnd(order)
            }, 1000)
        }
    })
}
const fomatTime = computed(() => (time) => {
    let arr = time.split(':')
    let day = Math.floor(arr[0] / 24)
    let hour = arr[0] % 24 >= 10 ? arr[0] % 24 : '0' + arr[0] % 24
    console.log(hour)
    let min = arr[1] >= 10 ? arr[1] : '0' + arr[1]
    let sec = arr[2] >= 10 ? arr[2] : '0' + arr[2]
    if (day >= 1) {
        return day + this.$t('天') + ' ' + hour + ':' + min + ':' + sec
    } else {
        return hour + ':' + min + ':' + sec
    }

})

const goDetail = (item) => {
    detailData.value = item
    show.value = true
}

const onClose = () => { // 关闭
    show.value = false
}
</script>
<style lang="scss" scoped>
.positionList {
    font-size: 14px
}
</style>
