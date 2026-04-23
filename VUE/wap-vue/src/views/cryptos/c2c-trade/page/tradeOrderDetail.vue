<template>
    <div class="w-full h-full">
        <trade-success-detail :back="back">
          
        </trade-success-detail>
    </div>
</template>

<script>
import {
    mapState
} from "vuex";
import TradeSuccessDetail from "@/views/cryptos/c2c-trade/components/TradeSuccessDetail.vue";
export default {
    name: "TradeSuccessDetailSell",
    computed: {
        ...mapState('c2cSell', ['orderInfo'])
    },
    components: {
        TradeSuccessDetail,
    },
    data(){
        return {
            back:true
        }
    },
    created(){
        if (this.$route.query.back){
            this.back = false
        }
    },
}
</script>

<style lang="scss" scoped>
::deep(.order-data) {
    .title {
        padding-left: 32px;
    }
}
</style>
