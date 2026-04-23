<template>
    <van-popup round :value="props.showPopup" @input="val => this.$emit('input', val)" :close-on-click-overlay="false"
        position="bottom">
        <div class="relative px-15 pt-23 px-4 pb-10">
            <div class="flex justify-between py-4 items-center">
                <h4 class="text-20 font-medium text-black">{{ $t('cycle') }}</h4>
                <img @click="onClose" class="z-20 w-8 h-8" src="./close.png" alt="" />
            </div>
            <div class="flex flex-col justify-center">
                <section v-for="(item, index) in infoList" :key="index">
                    <p class="mt-2">{{ item.title }}</p>
                    <ul class="mt-2 mb-4 border-b-1 flex flex-wrap">
                        <li v-for="(_item, _index) in item.list" :key="_index" :class="{
                            'active':
                                _item.value === quotesStore.stage
                        }" class="bg-btn-gray mr-4 rounded px-4 py-1 mb-4" @click="onItemClick(_item)">
                            {{ _item.label }}
                        </li>
                    </ul>
                </section>
            </div>
        </div>
    </van-popup>
</template>

<script setup>
import { useQuotesStore } from '@/store/quotes.store';
import { SET_STAGE } from '@/store/types.store';
import { useI18n } from "vue-i18n";
const { t } = useI18n()
const props = defineProps({
    showPopup: {
        type: Boolean,
        default: false
    }
})

const quotesStore = useQuotesStore()
console.log(quotesStore.stage)

const emits = defineEmits(['close', 'select'])

const infoList = [
    // {
    //     title: '秒', list: [
    //         { label: '分时', value: '1sec' },
    //     ]
    // },
    {
        title: t('minute'), list: [
            { label: '1' + t('minute'), value: '1min', seconds: 5 * 60 * 1000 },
            // { label: '3分钟', value: '3min' },
            { label: '5' + t('minute'), value: '5min', seconds: 5 * 60 * 1000 },
            { label: '15' + t('minute'), value: '15min', seconds: 15 * 60 * 1000 },
            { label: '30' + t('minute'), value: '30min', seconds: 30 * 60 * 1000 },
            // { label: '45分钟', value: '45min' },
        ]
    },
    {
        title: t('Hour'), list: [
            { label: '1' + t('Hour'), value: '60min', seconds: 1 * 60 * 60 * 1000 },
            // { label: '2小时', value: '2hour' },
            // { label: '3小时', value: '3hour' },
            { label: '4' + t('Hour'), value: '4hour', seconds: 4 * 60 * 60 * 1000 }
        ]
    },
    {
        title: t('day'), list: [
            { label: '1' + t('day'), value: '1day', seconds: 1 * 24 * 60 * 60 * 1000 },
            { label: '1' + t('week'), value: '1week', seconds: 7 * 24 * 60 * 60 * 1000 },
            { label: '1' + t('moon'), value: '1mon', seconds: 30 * 24 * 60 * 60 * 1000 },
            // { label: '1年', value: '1year' }
        ]
    }
]


const onClose = () => {
    emits('close')
}

const onItemClick = (item) => {
    let timeValue = ''
    timeValue = item.value
    quotesStore[SET_STAGE]({ stage: item.value, seconds: item.seconds })
    emits('select', item.value)
    onClose()
}

</script>
<style lang="scss" scoped>
.active {
    background: $btn_main !important;
    color: $text_color !important;
}
</style>