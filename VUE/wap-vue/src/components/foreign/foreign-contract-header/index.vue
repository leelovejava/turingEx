<template>
    <!-- 永续合约，交割合约公共头部 -->
    <div>
        <div class="contract-header">
            <div class="pl-4 pr-4">
                <div class="flex justify-start pt-8 pb-4 before">
                    <div class="icon icon_back_1" @click="jump()">
                        <van-icon name="arrow-left" size="20" />
                    </div>
                    <!-- <img src="@/assets/image/icon_back_1.png" class="icon_back_1" alt="" @click="jump()"> -->
                    <div class="flex items-center text-32">
                        <img :src="handleImage(leftIcon)" alt="convert-img" class="w-10 h-10" @click="onSidebar">
                        <div class="flex pl-2 textColor font-bold" @click="onSidebar">
                            <div class="text-32">{{ symbol.toUpperCase() || '--' }}</div>
                            <div class="ml-4">{{ $t('delivery') }}</div>
                        </div>
                        <div class="pl-4 w-20" :class="{ 'text-green': range > 0, 'text-red': range <= 0 }">{{
                            range > 0 ?
                            '+' : '' }}{{ range || '--' }}%</div>
                        <!-- <img src="@/assets/image/kline.png" class="w-22 h-22 right" alt=""
                            @click="$router.push(`/trendDetails/${symbol}?page=${page}`)"> -->
                    </div>
                </div>
            </div>
        </div>
        <!-- 左侧边弹出菜单 -->
        <van-popup class="popup" round v-model:show="show" close-icon-position="top-left" position="left" @closed="onClose">
            <div class="pl-10 border-b-color pt-12 pb-12">
                <div class="textColor">
                    <span class="font-bold text-36 mr-3">{{ $t('交割合约') }} </span>
                    <span class="text-24">/ USD</span>
                </div>
            </div>
            <div class="pl-10 pr-10">
                <div class="flex justify-between mb-21 mt-8 text-30">
                    <div class="flex items-center text-grey">
                        <div class="mr-3">{{ $t('name') }}</div>
                    </div>
                    <div class="flex text-grey">
                        <div class="flex items-center">
                            <div class="">{{ $t('newPrice') }}</div>
                        </div>
                        <div class="flex items-center">
                            <div class="mr-3">/24H{{ $t('change') }}</div>
                        </div>
                    </div>
                </div>
                <div class="flex justify-between mb-7" v-for="item in list" :key="item.name" @click="onRoute(item)">
                    <div>
                        <div class="textColor text-32">{{ item.symbol }}</div>
                        <div class="text-grey mt-1 text-24">{{ selectIndex == 1 ? $t('永续') : $t('delivery') }}</div>
                    </div>
                    <div class="text-right">
                        <div class="textColor">{{ item.close }}</div>
                        <div class="mt-1" :class="item.change_ratio > 0 ? 'text-green' : 'text-red'">{{ item.change_ratio
                        }}%</div>
                    </div>
                </div>
            </div>
        </van-popup>
    </div>
</template>

<script setup>
import { ref, } from 'vue';
import { Popup } from "vant";
import { _getQuotes } from '@/service/quotes.api'
import { setStorage } from '@/utils/index'
import { useQuotesStore } from '@/store/quotes.store';
import { useRouter, useRoute } from "vue-router";
import { themeStore } from '@/store/theme';
const thStore = themeStore()
const router = useRouter()
const route = useRoute()
const emits = defineEmits(['update-coin', 'tab'])
const quotesStore = useQuotesStore()

let show = ref(false)
let timeout = ref(null)
let list = ref([])
const props = defineProps({
    backFunc: {
        type: Function,
        default: null
    },
    balance: { // 余额
        type: [String, Number],
        default: 0.00
    },
    symbol: {
        type: String,
        default: ''
    },
    range: {
        type: String,
        defalult: ''
    },
    selectIndex: {
        type: [String, Number],
        defalult: ''
    },
})

const leftIcon = new URL(`../../../assets/theme/${thStore.theme}/image/black-convert.png`, import.meta.url)

const onRoute = (item) => {
    if (route.params.symbol !== item.symbol) {
        let routeName = route.name
        router.push(`/foreign/${routeName}/${item.symbol}`)
        if (routeName == 'deliveryContract') {
            setStorage('jgSymbol', item.symbol)
        } else {
            setStorage('symbol', item.symbol)
        }
        onClose()
        emits('update-coin', item.symbol)
    }
    show.value = false
}

const onSidebar = () => { // 侧边栏打开
    show.value = true
    fetchList()
}
const fetchList = () => { // 获取行情
    _getQuotes(quotesStore.coins).then(data => {
        list.value = data
        if (timeout.value) {
            clearTimeout(timeout.value)
            timeout.value = null
        }
        timeout.value = setTimeout(() => {
            fetchList()
        }, 1000)
    })
}
const onClose = () => {
    if (timeout.value) {
        clearTimeout(timeout.value)
        timeout.value = null
    }
}
const jump = () => {
    router.push('/quotes/index?tabActive=2')

}
const handleImage = (url) => {
    return new URL(url, import.meta.url).href
}

</script>

<style lang="scss" scoped>
.contract-header {
    font-size: 15px;
}

.before {
    position: relative;

    .back {
        position: absolute;
        left: 0;
    }

    .right {
        position: absolute;
        right: 0;
    }
}


.wallet-background {
    background-color: #E8E8E8;
}

.tabBtn {
    border-radius: 8px;
    border: 1px solid #EAEDF2;
    color: $text_color1;
    background: $light-grey;
}

.select-active {
    background-color: $btn_main;
    color: $text_color;
    border: none;
}

.no-select {}

// 弹出层样式
:deep(.van-popup) {
    height: 100% !important;
    width: 300px;
    background: $main_background;
    font-size: 14px;
}

.border-b-color {
    border-bottom: 1px solid $border_color;
}

.icon_back_1 {
    width: 20px;
    height: 20px;
    margin-right: 30px;
}
</style>
