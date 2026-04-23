<template>
    <div class="morePage">
        <fx-header>
            <template #title>
                {{ $t('更多') }}
            </template>
        </fx-header>
        <div class="main">
            <div class="search-container flex items-center ">
                <van-search class="search-input" v-model="searchVal" :placeholder="$t('搜索更多服务')" shape="round"
                    :background="thStore.theme == 'dark' ? '#131A2E' : '#fff'">
                </van-search>
            </div>
            <div v-if="searchList.length == 0">
                <div class="nav-list ">
                    <div class="title px-5">{{ t('推荐') }}</div>
                    <van-grid class="van-grid-main" :column-num="4" :border="false">
                        <van-grid-item v-for="(item, index) in navList.recommend" @click="openUrl(item)" :key="index"
                            :text="item.name">
                            <template #icon>
                                <img class="grid-item-img" :src="item.url" />
                            </template>
                        </van-grid-item>
                    </van-grid>
                </div>
                <div class="line-div"></div>
                <div class="nav-list ">
                    <div class="title px-5">{{ t('股票') }}</div>
                    <van-grid class="van-grid-main" :column-num="4" :border="false">
                        <van-grid-item v-for="(item, index) in navList.stock" @click="openUrl(item)" :key="index"
                            :text="item.name">
                            <template #icon>
                                <img class="grid-item-img" :src="item.url" />
                            </template>
                        </van-grid-item>
                    </van-grid>
                </div>
                <div class="line-div"></div>
                <div class="nav-list ">
                    <div class="title px-5">{{ t('ETF') }}</div>
                    <van-grid class="van-grid-main" :column-num="4" :border="false">
                        <van-grid-item v-for="(item, index) in navList.etf" @click="openUrl(item)" :key="index"
                            :text="item.name">
                            <template #icon>
                                <img class="grid-item-img" :src="item.url" />
                            </template>
                        </van-grid-item>
                    </van-grid>
                </div>
                <div class="line-div"></div>
                <div class="nav-list ">
                    <div class="title px-5">{{ t('外汇') }}</div>
                    <van-grid class="van-grid-main" :column-num="4" :border="false">
                        <van-grid-item v-for="(item, index) in navList.foreign" @click="openUrl(item)" :key="index"
                            :text="item.name">
                            <template #icon>
                                <img class="grid-item-img" :src="item.url" />
                            </template>
                        </van-grid-item>
                    </van-grid>
                </div>
            </div>
            <div class="search" v-else>
                <div class="nav-list ">
                    <van-grid class="van-grid-main" :column-num="4" :border="false">
                        <van-grid-item v-for="(item, index) in searchList" @click="openUrl(item)" :key="index"
                            :text="item.name">
                            <template #icon>
                                <img class="grid-item-img" :src="item.url" />
                            </template>
                        </van-grid-item>
                    </van-grid>
                </div>
            </div>
        </div>

    </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { showToast } from "vant";
import { useI18n } from "vue-i18n";
import { useUserStore } from '@/store/user';
import { themeStore } from '@/store/theme';
const thStore = themeStore()
const useStore = useUserStore();
const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const searchVal = ref('')
const navList = ref({
    recommend: [
        { name: t('公告中心'), url: new URL(`../../assets/theme/${thStore.theme}/image/nav/announcement.png`, import.meta.url), path: '/cryptos/announce', isLogin: false },
        { name: t('充值'), url: new URL(`../../assets/theme/${thStore.theme}/image/nav/recharge.png`, import.meta.url), path: '/cryptos/recharge/rechargeList', isLogin: true },
        { name: t('提现'), url: new URL(`../../assets/theme/${thStore.theme}/image/nav/withdraw.png`, import.meta.url), path: '/cryptos/withdraw/withdrawPage', isLogin: true },
        { name: t('账变记录'), url: new URL(`../../assets/theme/${thStore.theme}/image/nav/record.png`, import.meta.url), path: '/cryptos/accountChange?type=cryptos', isLogin: true },
        { name: t('质押借币'), url: new URL(`../../assets/theme/${thStore.theme}/image/nav/PledgeLoan.png`, import.meta.url), path: '/cryptos/pledgeLoan', isLogin: false },
        { name: t('助力贷'), url: new URL(`../../assets/theme/${thStore.theme}/image/nav/Helpoan.png`, import.meta.url), path: '/cryptos/loan', isLogin: true },
        { name: t('news'), url: new URL(`../../assets/theme/${thStore.theme}/image/nav/Information.png`, import.meta.url), path: '/news', isLogin: false },
        { name: t('理财'), url: new URL(`../../assets/theme/${thStore.theme}/image/nav/financialmanagement.png`, import.meta.url), path: '/cryptos/fm-home', isLogin: false },
    ],
    stock: [
        { name: t('美股指数'), url: new URL(`../../assets/theme/${thStore.theme}/image/nav/trade.png`, import.meta.url), path: '/quotes/openTrade?tabActive=0&symbol=AAPL&type=US-stocks', isLogin: false },
        { name: t('美股合约'), url: new URL(`../../assets/theme/${thStore.theme}/image/nav/contract.png`, import.meta.url), path: '/cryptos/perpetualContract/AAPL?type=US-stocks', isLogin: false },
        { name: t('美股交割'), url: new URL(`../../assets/theme/${thStore.theme}/image/nav/delivery1.png`, import.meta.url), path: '/cryptos/perpetualContract/AAPL?type=US-stocks&selectIndex=2', isLogin: false },
        { name: t('港股指数'), url: new URL(`../../assets/theme/${thStore.theme}/image/hkNav/icon01.png`, import.meta.url), path: '/quotes/openTrade?tabActive=0&symbol=00139&type=HK-stocks', isLogin: false },
        { name: t('港股合约'), url: new URL(`../../assets/theme/${thStore.theme}/image/hkNav/icon02.png`, import.meta.url), path: '/cryptos/perpetualContract/00139?type=HK-stocks', isLogin: false },
        { name: t('港股交割'), url: new URL(`../../assets/theme/${thStore.theme}/image/hkNav/icon03.png`, import.meta.url), path: '/cryptos/perpetualContract/00139?type=HK-stocks&selectIndex=2', isLogin: false },
        { name: t('台股指数'), url: new URL(`../../assets/theme/${thStore.theme}/image/twNav/icon01.png`, import.meta.url), path: '/quotes/openTrade?tabActive=0&symbol=2002&type=TW-stocks', isLogin: false },
        { name: t('台股合约'), url: new URL(`../../assets/theme/${thStore.theme}/image/twNav/icon02.png`, import.meta.url), path: '/cryptos/perpetualContract/2002?type=TW-stocks', isLogin: false },
        { name: t('台股交割'), url: new URL(`../../assets/theme/${thStore.theme}/image/twNav/icon03.png`, import.meta.url), path: '/cryptos/perpetualContract/2002?type=TW-stocks&selectIndex=2', isLogin: false },
        // { name: t('A股指数'), url: new URL(`../../assets/theme/${thStore.theme}/image/aNav/icon01.png`, import.meta.url), path: '/quotes/openTrade?tabActive=0&symbol=SH688981&type=A-stocks', isLogin: false },
        // { name: t('A股合约'), url: new URL(`../../assets/theme/${thStore.theme}/image/aNav/icon02.png`, import.meta.url), path: '/cryptos/perpetualContract/SH688981?type=A-stocks', isLogin: false },
        // { name: t('A股交割'), url: new URL(`../../assets/theme/${thStore.theme}/image/aNav/icon03.png`, import.meta.url), path: '/cryptos/perpetualContract/SH688981?type=A-stocks&selectIndex=2', isLogin: false },
    ],
    etf: [
        { name: t('ETF交易'), url: new URL(`../../assets/theme/${thStore.theme}/image/nav/trade.png`, import.meta.url), path: '/quotes/openTrade?tabActive=0&symbol=GlobalETF500&type=indices', isLogin: false },
        { name: t('ETF合约'), url: new URL(`../../assets/theme/${thStore.theme}/image/nav/contract.png`, import.meta.url), path: '/cryptos/perpetualContract/GlobalETF500?type=indices', isLogin: false },
        { name: t('ETF交割'), url: new URL(`../../assets/theme/${thStore.theme}/image/nav/delivery1.png`, import.meta.url), path: '/cryptos/perpetualContract/GlobalETF500?type=indices&selectIndex=2', isLogin: false },
    ],
    foreign: [
        // { name: t('外汇交易'), url: new URL('@/assets/theme/dark/image/nav/trade.png', import.meta.url), path: '/foreign/coinChart?symbol=USDSGD', isLogin: false },
        { name: t('外汇合约'), url: new URL(`../../assets/theme/${thStore.theme}/image/nav/contract.png`, import.meta.url), path: '/foreign/coinChart?symbol=USDSGD', isLogin: false },
        { name: t('外汇交割'), url: new URL(`../../assets/theme/${thStore.theme}/image/nav/delivery1.png`, import.meta.url), path: '/foreign/deliveryContract/USDSGD', isLogin: false },
    ]
})
const searchList = ref([])
const allList = ref([]) //全部数据
onMounted(async () => {
    allList.value = [].concat(navList.value.recommend, navList.value.stock, navList.value.etf, navList.value.foreign)
})
const onClickButton = () => {
    if (searchVal.value) {
        searchList.value = allList.value.filter((item) => {
            console.log(item.name.indexOf(searchVal.value))
            return item.name.indexOf(searchVal.value) != -1
        })
        console.log(searchList.value)
    } else {
        searchList.value = []
    }
}
const openUrl = (item) => {
    if (item.isLogin) {
        if (!useStore.userInfo.token) {
            router.push('/login')
            return
        } else {
            router.push(item.path)
        }
    } else {
        router.push(item.path)
    }
}
watch(searchVal, (val, oldVal) => {
    if (val) {
        searchList.value = allList.value.filter((item) => {
            console.log(item.name.indexOf(val))
            return item.name.indexOf(searchVal.value) != -1
        })
        console.log(searchList.value)
    } else {
        searchList.value = []
    }
})


</script>
<style lang="scss" scoped>
.morePage {
    padding-bottom: 30px;
    font-size: 14px;

    .search-container {

        .search-input {
            height: 42px;
            display: flex;
            align-items: center;
            width: 100%;
        }

        :deep(.van-search__field) {
            background: $search_background !important;
        }

        :deep(.van-search__content) {
            overflow: hidden;
            background: $search_background;
        }

        :deep(.van-field__control) {
            background: transparent !important;
            height: 100% !important;
            color: $text_color;
        }
    }

    .nav-list {
        .title {
            font-size: 16px;
            padding: 20px 15px 10px 15px;
        }

        .grid-item-img {
            width: 48px;
            height: 48px;
            margin-bottom: 5px;
        }

        :deep(.van-grid-item__content) {
            background: $more-bg;
            padding: 10px 10px;
        }

        :deep(.van-grid-item__text) {
            color: $text_color;
            font-size: 12px;
            height: 30px;
            display: flex;

        }
    }

    .line-div {
        background: $divi_line;
        height: 10px;
    }

}
</style>