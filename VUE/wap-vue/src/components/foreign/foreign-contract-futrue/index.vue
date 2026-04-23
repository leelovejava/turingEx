<template>
    <div class="contact-futrue">
        <div class="text-24 text-grey mb-24">{{ $t('交割时间') }}</div>
        <ul class="flex flex-wrap w-full">
            <li v-for="(item, index) in initFutrue.para" :key="item.para_id" class="h-92 flex items-center mb-22"
                @click="onSelect(item, index)">
                <p class="w-95 h-full flex justify-center items-center text-22 flex-1"
                    :class="active === item.para_id ? 'bg-light-blue text-white' : 'bgDark textColor'">{{ item.time_num +
                        item.time_unit.substr(0, 1) }}</p>
                <p class="w-125 h-full flex justify-center items-center text-22 flex-1"
                    :class="active === item.para_id ? 'bg-dark-blue text-white' : 'contBackground textColor'">{{
                        item.profit_ratio }}%</p>
            </li>
        </ul>
    </div>
</template>

<script>
export default {
    name: 'ContractFutrue',
    props: {
        initFutrue: {
            type: Object,
            default() {
                return {}
            }
        }
    },
    data() {
        return {
            active: '',
            initParam: [] // 初始化参数
        }
    },
    created() {
        this.active = this.initFutrue && this.initFutrue.para ? this.initFutrue.para[0].para_id : ''
        this.$emit('paraId', { id: this.active, index: 0 })
    },
    methods: {
        onSelect(item, index) { // 选中
            this.active = item.para_id
            this.$emit('paraId', { id: this.active, index })
        }
    }
}
</script>
