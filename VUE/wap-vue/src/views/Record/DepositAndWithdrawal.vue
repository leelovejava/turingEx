<template>
    <div class="Record pb-12">
        <fx-header fixed>
            <template #title>{{ $t("rechargeWithRecord") }}</template>
        </fx-header>
        <div class="tab-fixed">
            <van-tabs v-model:active="active" @click-tab="onClickTab">
                <van-tab v-for="(item, index) in tabList" :key="index" :title="item.name">
                </van-tab>
            </van-tabs>
        </div>
        <div class="list-wrap px-8 pt-8">
            <div class="tab-wrap flex px-4 mt-5">
                <div class="tab-item mr-4" :class="[selectIndexActive === index ? 'active' : '']"
                    v-for="(item, index) in tabListTwo" :key="index" @click="changeCoin(index)">{{ item }}</div>
            </div>
            <van-pull-refresh v-model="refreshing" @refresh="onRefresh" :pulling-text="t('下拉即可刷新')"
                :loosing-text="t('释放即可刷新')" :loading-text="t('加载中...')">
                <div v-if="noData" class="pt-20 text-center">
                    {{ $t('noData') }}
                </div>
                <template v-else>
                    <van-list v-model:loading="loading" :finished="finished" :loading-text="$t('loading')"
                        :finished-text="$t('noMore')" @load="onLoad">
                        <ul>
                            <li class="px-4 mt-10" v-for="(item, _index) in list" :key="_index" @click="openDetails(item)">
                                <div class="flex  justify-between">
                                    <div class="type">{{ selectIndexActive == 0 ? item.currency : item.coin }}</div>
                                    <div class="money"> {{ item.amount }}</div>
                                </div>
                                <div class="flex mt-1 justify-between">
                                    <div class="time">{{ selectIndexActive == 0 ? item.create_time : item.createtime }}
                                    </div>
                                    <div class="status flex" v-if="selectIndexActive == 1">
                                        <template v-if="item.status == 0">
                                            <div class="status-icon status-icon-color3 mr-2"></div>
                                            {{ $t('confirming') }}
                                        </template>
                                        <template v-if="item.status == 1">
                                            <div class="status-icon status-icon-color1 mr-2"></div>
                                            {{ $t('success') }}
                                        </template>
                                        <template v-if="item.status == 2">
                                            <div class="status-icon status-icon-color2 mr-2"></div>
                                            {{ $t('fail') }}
                                        </template>
                                    </div>
                                    <div class="status flex" v-else>
                                        <template v-if="item.state == 0">
                                            <div class="status-icon status-icon-color3 mr-2"></div>
                                            {{ $t('confirming') }}
                                        </template>
                                        <template v-if="item.state == 3">
                                            <div class="status-icon status-icon-color1 mr-2"></div>
                                            {{ $t('success') }}
                                        </template>
                                        <template v-if="item.state == 4">
                                            <div class="status-icon status-icon-color2 mr-2"></div>
                                            {{ $t('fail') }}
                                        </template>
                                    </div>
                                </div>
                            </li>
                        </ul>
                    </van-list>
                </template>

            </van-pull-refresh>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { _foreignRechargeWith, _getRechargeList } from "@/service/recharge.api";
import { _withdrawList } from "@/service/withdraw.api";
import { useI18n } from "vue-i18n";
import { getStorage, setStorage } from '@/utils/index'
const { t } = useI18n()

const router = useRouter()
const route = useRoute()
let active = ref(0)
let selectIndexActive = ref(0)
const list = ref([]);
const loading = ref(false);
const refreshing = ref(false)
const noData = ref(false)
const finished = ref(false);
const id = ref(1)
const tabList = ref([
    {
        name: t('recharge'),
        direction: 'recharge'
    },
    {
        name: t('withdraw'),
        direction: 'withdraw'
    }
])

onMounted(() => {
    // console.log(getters)
    let recordId = getStorage('recordId')
    id.value = recordId
    if (id.value == 1 || id.value == 2) {
        active.value = 0
        if (id.value == 2) {
            selectIndexActive.value = 1
        } else {
            selectIndexActive.value = 0
        }
    } else {
        active.value = 1
        if (id.value == 4) {
            selectIndexActive.value = 1
        } else {
            selectIndexActive.value = 0
        }
    }
})
let currentTab = ref({
    name: t('recharge'),
    direction: 'recharge'
})
// const tabListTwo = [t('foreignCurrency'), t('digitalCurrency')]
const tabListTwo = [t('foreignCurrency')]
const page = ref(1)
const onClickTab = () => {
    currentTab.value = tabList.value[active.value]
    id.value = active.value
    if (active.value == 0) {
        if (selectIndexActive.value == 0) {
            setStorage('recordId', 1)
        } else {
            setStorage('recordId', 2)
        }
    } else {
        if (selectIndexActive.value == 0) {
            setStorage('recordId', 3)
        } else {
            setStorage('recordId', 4)
        }
    }
    onRefresh()
}
const changeCoin = (index) => {
    selectIndexActive.value = index
    if (active.value == 0) {
        if (selectIndexActive.value == 0) {
            setStorage('recordId', 1)
        } else {
            setStorage('recordId', 2)
        }
    } else {
        if (selectIndexActive.value == 0) {
            setStorage('recordId', 3)
        } else {
            setStorage('recordId', 4)
        }
    }
    onRefresh()
}
const onLoad = () => {
    // 异步更新数据

    if (selectIndexActive.value == 0) {
        foreignRechargeWith()
    } else {
        if (active.value == 0) {
            getRechargeList()
        } else {
            withdrawList()
        }
    }

}
const openDetails = (item) => {
    router.push('RecordDetails?orderNo=' + item.order_no + '&type=' + selectIndexActive.value +
        '&isCZ=' + active.value)
}
// 银行卡充值
const foreignRechargeWith = () => {
    _foreignRechargeWith({
        page_no: page.value,
        direction: tabList.value[active.value].direction
    }).then((res) => {
        list.value = []
        refreshing.value = false;
        // if (res.length < 8) {
        //     finished.value = true
        // }

        list.value = list.value.concat(res);
        loading.value = false;
        finished.value = true
        // 如果没有数据，显示暂无数据
        if (list.value.length === 0 && page.value === 1) {
            noData.value = true
        }
        page.value++;
    })
}
// usdt充值
const getRechargeList = () => {
    _getRechargeList({
        page_no: page.value
    }).then((res) => {

        list.value = []
        refreshing.value = false;
        // if (res.length < 8) {
        //     finished.value = true
        // }
        loading.value = false;
        finished.value = true
        list.value = list.value.concat(res);
        // 如果没有数据，显示暂无数据
        if (list.value.length === 0 && page.value === 1) {
            noData.value = true
        }
        page.value++;
    });
}
// usdt提现记录
const withdrawList = () => {
    _withdrawList({
        page_no: page.value
    }).then((res) => {
        refreshing.value = false;
        loading.value = false;
        list.value = list.value.concat(res);
        finished.value = true
        // 如果没有数据，显示暂无数据
        if (list.value.length === 0 && page.value === 1) {
            noData.value = true
        }
        // 如果加载完毕，显示没有更多了
        // if (res.length < 8) {
        //     finished.value = true
        // }
        page.value++;
    });
}
const onRefresh = () => {
    finished.value = false
    loading.value = true
    list.value = []
    page.value = 1
    noData.value = false
    onLoad()

}
</script>
<style lang="scss" scoped>
:deep(.van-tabs__line) {
    background: $btn_main;
}

:deep(.van-tab) {
    font-size: 16px;
}

:deep(.van-tabs__nav) {
    background: $select-bg;
}

:deep(.van-tab__text) {
    color: $text_color;
}

.Record {
    padding-top: var(--van-nav-bar-height);
    font-size: 14px;
}

.tab-fixed {
    // position: fixed;
    // top: var(--van-nav-bar-height);
    width: 100%;
    z-index: 2;
}

.tab-wrap {
    .tab-item {
        padding: 0 20px;
        height: 32px;
        text-align: center;
        line-height: 31px;
        color: $text_color1;
        border-radius: 20px;
        font-size: 14px;
    }

    .active {
        background: $btn_main;
        color: $main-btn-color;
    }
}

ul {
    li {
        .type {
            font-size: 18px;
        }

        .money {
            font-weight: bold;
            font-size: 16px;
        }

        .time {
            color: $dark-grey;
            font-size: 14px;
        }

        .status {
            color: $dark-grey;
            align-items: center;
            font-size: 14px;
        }
    }
}


.status-icon {
    width: 8px;
    height: 8px;
    border-radius: 50%;
}

.status-icon-color1 {
    background: $green;
}

.status-icon-color2 {
    background: #EA0F0F;
}

.status-icon-color3 {
    background: #F5D658;
}
</style>