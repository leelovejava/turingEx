<template>
    <div :key="currentSymbol" class="pb-108 ">
        <!-- 头部区 -->
        <ContractHeader :symbol="currentSymbol" :range="range" :selectIndex="selectIndex"
            :balance="userStore.userInfo.balance" @update-coin="onUpdate" page="perpetualContract">
        </ContractHeader>
        <div>
            <div class="mainBackground rounded-view">
                <PerpetualOpen class="pl-8 pr-8" :key="keyIndex + 'c'" :selectIndex="selectIndex" :symbol="currentSymbol"
                    :green-data="bids" :red-data="asks" :price="price" :init-open="initOpen" :init-futrue="initFutrue"
                    @ordered="onOrdered" :currentType="currentType" @changeCurrentType="changeCurrentType">
                </PerpetualOpen>
                <div class="line"></div>
                <PerpetualOrder class="pl-8 pr-8" :key="keyIndex + 'd'" :symbol="currentSymbol" :order-cur="orderCur"
                    :order-hold="orderHold" :price="price" :topIndex="selectIndex" :futrue-hold="futrueHold"
                    :futrue-histroy="futrueHistroy" @tab="onTab" @recall="onRecall">
                </PerpetualOrder>
            </div>
        </div>

    </div>
</template>

<script setup>
import PerpetualOpen from '@/components/foreign/foreign-perpetual-open/index.vue'
import PerpetualOrder from '@/components/foreign/foreign-perpetual-order/index.vue'
import { ref, onMounted, onActivated, onDeactivated, onBeforeUnmount } from 'vue';
import ContractHeader from '@/components/foreign/foreign-contract-header/index.vue'
import { _getRealtime } from '@/service/quotes.api'
import { useUserStore } from '@/store/user';
import { useQuotesStore } from '@/store/quotes.store';
import { useRoute } from 'vue-router'
import { WS_URL } from '@/config'
import { SET_COIN_LIST, SET_USERINFO } from '@/store/types.store'
import { _getBalance } from '@/service/user.api.js'
import { _futrueOrderInit, _futrueOrderList, _getDeepData, contractOrder } from "@/service/trade.api.js";
const userStore = useUserStore()
const quotesStore = useQuotesStore()
const route = useRoute()


const initTimer = ref(null)
let keyIndex = ref(0)
let timeout = ref(null)
let timer = ref(null) // 底部持仓等的公用定时器
let timer2 = ref(null) // 交割合约底部持仓等的公用定时器
let balance = ref(0)
let currentSymbol = ref('')
let price = ref('')
let range = ref('')
let initOpen = ref({})
const initArr = []
for (let i = 0; i < 6; i++) {
    initArr.push({
        amount: '--',
        price: '--'
    })
}
let asks = ref(initArr)  // 卖单
let bids = ref(initArr) // 买单
let orderCur = ref([]) // 当前委托
let orderHold = ref([]) // 持有仓位
let futrueHold = ref([]) // 交割持仓
let futrueHistroy = ref([]) // 交割历史
let sockets = ref({
    quotes: null, // 行情
    deep: null /// 深度
})
let curTab = ref('fetchFutrueHoldList')
let selectIndex = ref(2) // 当前tab
let initFutrue = ref({}) /// 交割合约
let show = ref(false) // popup
let animated1 = ref(false)
let animated2 = ref(false)
let currentType = ref('')

const onUpdate = (symbol) => { // 更新
    currentSymbol.value = symbol
    closeSocket()
    init(symbol)
}

const onRecall = () => { // 撤单or 平仓 evt
    clearTimer()
    contractOrder({
        symbol: currentSymbol.value,
        type: 'orders',
        page_no: 1,
        symbolType: 'cryptos'
    }).then(data => {
        orderHold.value = data
    })
    if (curTab.value == 'fetchFutrueHoldList') {
        fetchFutrueHoldList(currentSymbol.value)
    } else {
        fetchFutrueHistory(currentSymbol.value)
    }
}

const onOrdered = (evt) => { // 下单过后的回调
    clearTimer()
    // this.clearTimeout()
    console.log(evt)
    initParam(currentSymbol.value, evt) // 重新初始化
    // TODO: 这里要做判断
    if (curTab.value == 'fetchFutrueHoldList') {
        fetchFutrueHoldList(currentSymbol.value)
    } else {
        fetchFutrueHistory(currentSymbol.value)
    }
}

const onTab = (evt) => { // 点击tab后的回调
    console.log('evt', evt)
    clearTimer()
    curTab.value = evt
    if (curTab.value == 'fetchFutrueHoldList') {
        fetchFutrueHoldList(currentSymbol.value)
    } else {
        fetchFutrueHistory(currentSymbol.value)
    }
}

const fetchQoutes = (symbol) => { // 获取当前行情
    _getRealtime(symbol).then(data => { // 获取行情
        handleQoutes(data)
        startQuoteSocket() // socket
    })
}

const handleQoutes = (data) => {
    if (data && data.length) {
        const cur = data[0]
        price.value = cur.close
        range.value = cur.change_ratio + ''
    }
}

const fetchDeepData = (symbol) => {
    console.log('sd')
    _getDeepData(symbol).then(data => { // 获取深度
        handleDeep(data)
        startDeepSocket() // socket
    })
}

const handleDeep = (data) => {
    deepData.value = data
    const { asks, bids } = data
    asks.value = asks.sort((a, b) => a.price - b.price).slice(0, 6)
    bids.value = bids.sort((a, b) => a.price - b.price).slice(-6)
}

const startQuoteSocket = () => { // 行情socket
    sockets.value.quotes = new WebSocket(`${WS_URL}/1/${currentSymbol.value}`)
    sockets.value.quotes.onmessage = (evt) => {
        const { data } = evt
        const { code, data: _data } = JSON.parse(data)
        if (code / 1 === 0) {
            handleQoutes(_data)
        }
    }
}

const startDeepSocket = () => {
    sockets.value.deep = new WebSocket(`${WS_URL}/3/${currentSymbol.value}`)
    sockets.value.deep.onmessage = (evt) => {
        const { data } = evt
        const { code, data: _data } = JSON.parse(data)
        if (code / 1 === 0) {
            handleDeep(_data)
        }
    }
}
const initParam = (symbol, type) => { // 初始化参数
    if (type === 'futrue' || !type) {
        _futrueOrderInit(symbol).then(data => {
            initFutrue.value = data
        })
    }
}

const fetchOrderListCur = (symbol) => { // 当前委托
    console.log('当前委托')
    if (userStore.userInfo.token) {
        _contractApplyOrderList({
            symbol,
            type: 'orders',
            page_no: 1
        }).then(data => {
            orderCur.value = data
        })
        clearTimer()
        timer.value = setInterval(() => {
            _contractApplyOrderList({
                symbol,
                type: 'orders',
                page_no: 1
            }).then(data => {
                orderCur.value = data
            })
        }, 1000)
    }
}

const fetchFutrueHoldList = (symbol) => { // 交割持仓
    if (userStore.userInfo.token) {
        _futrueOrderList(symbol).then(data => {
            futrueHold.value = data
        })
        timer.value = setInterval(() => {
            _futrueOrderList(symbol).then(data => {
                futrueHold.value = data
            })
        }, 1000)
    }
}

const fetchFutrueHistory = (symbol) => { // 交割历史持仓
    _futrueOrderList(symbol, 'hisorders').then(data => {
        futrueHistroy.value = data
    })
}
const init = (symbol) => { // 初始化页面
    currentSymbol.value = symbol
    fetchQoutes(symbol)
    // fetchDeepData(symbol)
    initParam(symbol) // 'open'
    clearTimer()
    fetchFutrueHoldList(symbol)
}

const closeSocket = () => {
    sockets.value.quotes && sockets.value.quotes.close()
    sockets.value.deep && sockets.value.deep.close()
    sockets.value.quotes = null
    sockets.value.deep = null
}

const clearTimer = () => {
    clearInterval(timer.value)
    timer.value = null
}
const changeCurrentType = (type) => {
    currentType.value = type
}

onMounted(async () => {
    let symbol = route.params.symbol
    if (symbol) {
        currentSymbol.value = symbol
        init(currentSymbol.value)
    }
    if (!quotesStore.coinList.length) {
        await quotesStore[SET_COIN_LIST]()
    }
    _getBalance().then(data => { // 获取用户余额
        quotesStore[SET_USERINFO, { balance: data.money }]
    })
    currentType.value = route.query.currentType ? route.query.currentType : 'long'
})


onDeactivated(() => {
    closeSocket()
    clearTimer()
})

onActivated(() => {
    currentType.value = route.query.currentType ? route.query.currentType : 'long'
})

onBeforeUnmount(() => {
    closeSocket()
    clearTimer()
})









</script>

<style lang="scss" scoped>
//.list-enter-active, .list-leave-active {
//  transition: all .5s;
//  transform: translateY(30px)
//}
//.list-enter, .list-leave-to
//  /* .list-leave-active for below version 2.1.8 */ {
//  transform: translateY(0)
//}

.list-enter-active,
.list-leave-active {
    will-change: transform;
    transition: all 250ms;
}

.list-enter {
    opacity: 0;
    transform: translate3d(-100%, 0, 0);
}

.list-leave {
    opacity: 0;
    transform: translate3d(100%, 0, 0);
}

.rounded-view {
    border-top-left-radius: 20px;
    border-top-right-radius: 20px;
    box-sizing: border-box;
}

.my-swipe {
    width: 100%;
}

.my-swipe .van-swipe-item {}

.line {
    height: 6px;
    background: $selectSymbol_background;
}

@keyframes animate1 {
    0% {
        opacity: 1;
        transform: translate3d(100%, 0, 0);
    }

    //   40% {
    //      opacity: 1;
    //     transform: translate3d(50%, 0, 0);
    //   }
    100% {
        opacity: 1;
        transform: translate3d(0%, 0, 0);
    }
}

@keyframes animate2 {
    0% {
        opacity: 1;
        transform: translate3d(-100%, 0, 0);
    }

    //   40% {
    //      opacity: 1;
    //     transform: translate3d(50%, 0, 0);
    //   }
    100% {
        opacity: 1;
        transform: translate3d(0%, 0, 0);
    }
}


.slide2 {
    animation: animate2 200ms linear;
}

.pb-108 {
    padding-bottom: 54px;
}
</style>
