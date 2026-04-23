<template>
    <div class="contact-futrue">
        <div class="h-20 textColor3 border-main rounded-xl 0 pl-10 relative flex items-center"
            @click="showSelect = !showSelect">
            <span v-if="obj.profit_ratio">
                {{ obj.time_num + (obj.time_unit ? obj.time_unit.substr(0, 1) : '') }}
                &nbsp;-&nbsp;{{ obj.profit_ratio ? obj.profit_ratio.split('~')[0] : '' }}%
            </span>
            <ul class="w-full border-main tabBackground h-336 absolute top-0 left-0" style="overflow-y:auto;z-index:2;"
                v-show="showSelect">
                <li v-for="(item, index) in initFutrue.para" :key="item.para_id" class="h-20 flex items-center"
                    @click.stop="onSelect(item, index)">
                    <p class="w-95 h-full flex justify-center items-center text-26 flex-1"
                        :class="active === item.para_id ? 'contBackground colorMain' : 'textColor'">
                        {{ item.time_num + item.time_unit.substr(0, 1) }}&nbsp;-&nbsp;{{
                            item.profit_ratio ? item.profit_ratio.split('~')[0] : ''
                        }}%
                    </p>
                </li>
            </ul>
        </div>
    </div>
</template>

<script setup>
import { DropdownMenu, DropdownItem } from 'vant';
import { ref, onMounted } from "vue";
const emits = defineEmits(['paraId'])
const props = defineProps({
    initFutrue: {
        type: Object,
        default() {
            return {}
        }
    }
})
let value = ref(0)
let showSelect = ref(false)
let active = ref('')
let obj = ref({})
onMounted(() => {
    if (props.initFutrue && props.initFutrue.para.length > 0) {
        obj.value = props.initFutrue.para && props.initFutrue.para[0] ? props.initFutrue.para[0] : {};
        active.value = obj.value.para_id ? obj.value.para_id : ''
        emits('paraId', { id: active.value, index: 0 })
    }
})
const onSelect = (item, index) => { // 选中
    showSelect.value = false
    obj.value = item;
    active.value = item.para_id
    emits('paraId', { id: active.value, index })
}
</script>

<style lang="scss" scoped>
.border-main {
    border: 1px solid $border_color;
    border-radius: 4px;
}

.lh-10 {
    line-height: 10px;
}
</style>
