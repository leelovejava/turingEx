<template>
    <div class="success">
        <!-- <fx-header @back="openUrl(1)">
        </fx-header> -->
        <img class="success-img" src="../../assets/image/order/success-bg.png" />
        <p class="money mt-5">{{ t('交易完成') }}</p>
        <!-- <p class="money mt-5">{{ payInfo.currency }} {{ payInfo.amount }}</p>
        <p class="mt-5 text">{{ $t('successfulPurchase') }} {{ payInfo.amount }} {{ payInfo.currency }}</p> -->
        <div class="px-4 mt-20">
            <van-button class="w-full" type="primary" @click="openUrl(1)">{{ $t('Finish') }} </van-button>
        </div>
        <p class="mt-5 assets" @click="openUrl(2)">{{ $t('viewAssets') }}</p>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from "vue-i18n";
const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const payInfo = ref({})
onMounted(() => {
    payInfo.value = JSON.parse(route.query.payInfo)
    console.log(payInfo.value)

})
const openUrl = (index) => {
    router.push('/trade/index')
}

</script>
<style lang="scss" scoped>
.success {
    text-align: center;

    .success-img {
        width: 73px;
        height: 73px;
        display: block;
        margin: 0 auto;
        margin-top: 50px;
    }

    .money {
        font-size: 28px;
        font-weight: bold;
        color: $text_color3;
        margin: 20px 0;
    }

    .text {
        color: $dark-grey;
        font-size: 16px;
    }

    .w-full {
        width: 180px;
    }

    .assets {
        color: $active_line;
        font-size: 14px;
    }
}
</style>