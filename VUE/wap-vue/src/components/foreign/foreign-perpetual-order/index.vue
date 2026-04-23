<template>
    <!-- 永续合约订单列表页 -->
    <div class="perpetual-order">
        <div class="items-center mt-18">
            <div class="flex justify-between  items-center border-b-color">
                <div class="flex items-center">
                    <div class="w-60 py-5 flex flex-col items-center textColor1" @click="tabClick('3')"
                        :class="type == '3' ? 'active-line' : ''">{{ $t('持有仓位') }}</div>
                    <div class="w-60 ml-15 py-5 text-grey flex flex-col items-center textColor1" @click="tabClick('4')"
                        :class="type == '4' ? 'active-line' : ''">{{ $t('历史仓位') }}</div>
                </div>
                <img src="@/assets/image/public/record.png" alt="record-img" class="w-8 h-8 mr-8 " @click="goHistory" />
            </div>
            <!-- 交割-->
            <div v-if="type == '3'">
                <futrue-hold-list :price="price" :list-data="futrueHold" />
            </div>
            <div v-if="type == '4'">
                <futrue-histroy-position :list-data="futrueHistroy" />
            </div>
        </div>
    </div>
</template>

<script setup>
import futrueHoldList from '@/views/foreign/deliveryContract/hold.vue'
import futrueHistroyPosition from '@/views/foreign/deliveryContract/position.vue'
import { ref, onMounted, onActivated, onDeactivated, onBeforeUnmount } from 'vue';
import { useRouter } from "vue-router";
const router = useRouter()
const emits = defineEmits(['tab'])

let type = ref('')
const props = defineProps({
    symbol: {
        type: String,
        default: ''
    },
    orderCur: { //
        type: Array,
        default() {
            return []
        }
    },
    orderHold: {
        type: Array,
        default() {
            return []
        }
    },
    futrueHold: {
        type: Array,
        default() {
            return []
        }
    },
    futrueHistroy: {
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

onMounted(() => {
    type.value = '3'
    tabClick('3')
})

onActivated(() => {
    type.value = '3'
    this.tabClick('3')
})

const tabClick = (types) => {
    type.value = types;
    if (types == '1') { //  && !this.orderCur.length
        emits('tab', 'fetchOrderListHold')
    }
    if (types === '2') { //  && !this.orderHold.length
        //emits('tab', 'fetchOrderListHold')
        emits('tab', 'fetchOrderListCur')
    }
    if (types === '3') {
        emits('tab', 'fetchFutrueHoldList')
    }
    if (types === '4') {
        emits('tab', 'fetchFutrueHistory')
    }
}
const goHistory = () => {
    router.push({
        path: `/foreign/ForexDeliveryContractHistory`,
        query: {
            symbol: props.symbol
        }
    });
}

</script>

<style lang="scss" scoped>
.perpetual-order {
    font-size: 14px;
}

.all-cancel-btn {
    background-color: #EAEBEF;
}

.cancel-btn {
    background-color: #EAEBEF;
}

.active-line {
    position: relative;
    // padding: 15px 0;
    color: $main-btn-color;
    background-color: $color_main;
    border-radius: 4px;
}

// .active-line::after {
//     content: '';
//     position: absolute;
//     left: 50%;
//     transform: translateX(-50%);
//     bottom: 0;
//     right: 0;
//     width: 140px;
//     height: 8px;
//     background-color: $color_main;

// }

.border-b-color {
    border-bottom: 1px solid $border_color;
}
</style>
