<template>
    <div class="rechargeWithdrawRecord">
        <assets-head :title="$t('历史记录')" :goAssetsCenter="goAssetsCenter" />
        <div>
            <div class="flex justify-around mb-33 border-b-color mt-50 text-36">
                <div class="width-170 text-grey flex flex-col items-center" @click="tabClick('1')"
                    :class="type == '1' ? 'active-line' : ''">{{ $t('充值') }}</div>
                <div class="ml-100 text-grey width-170 flex flex-col items-center" @click="tabClick('2')"
                    :class="type == '2' ? 'active-line' : ''">{{ $t('提现') }}</div>
            </div>
            <recharge-history v-if="type == 1"></recharge-history>
            <withdraw-history v-if="type == 2"></withdraw-history>
        </div>
    </div>
</template>

<script>
import RechargeHistory from './components/rechargeHistory.vue';
import WithdrawHistory from './components/withdrawHistory.vue';
import assetsHead from "@/components/Transform/assets-head/index.vue";
export default {
    name: "rechargeWithdrawRecord",
    components: {
        assetsHead,
        RechargeHistory,
        WithdrawHistory,
    },
    data() {
        return {
            type: "1",
            goAssetsCenter: true,
        }
    },
    created() {
        console.log('this.$route', this.$route)
        if (this.$route.query.type) {
            this.type = this.$route.query.type;
        }
        if (this.$route.query.back) { // 回到上一页
            this.goAssetsCenter = false;
        }
    },
    methods: {
        tabClick(type) {
            this.type = type;
        },
    }
}
</script>
<style lang="scss" scoped>
.rechargeWithdrawRecord {
    width: 100%;
    box-sizing: border-box;
    font-size: 30px;
}

.active-line {
    position: relative;
    padding-bottom: 30px;
    color: $text_color !important;
}

.active-line::after {
    content: '';
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
    bottom: 0;
    right: 0;
    width: 140px;
    height: 8px;
    background-color: $color_main;
}
</style>
