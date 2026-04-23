<template>
    <div class="finish">
        <Step :step="4"></Step>
        <div class="title textColor">{{ $t('registerSuccess') }}</div>
        <p class="mt-8 pl-9">
            <i @click="agreeInfo">
                <img v-show="agree1" src="../../assets/image/login/prot2.png" alt="" />
                <img v-show="!agree1" src="../../assets/image/login/prot1.png" alt="" />
            </i>
            {{ $t('agreeNoticeCompet') }}
        </p>
        <p class="mt-6 pl-9">
            <i @click="agreeAddress">
                <img v-show="agree2" src="../../assets/image/login/prot2.png" alt="" />
                <img v-show="!agree2" src="../../assets/image/login/prot1.png" alt="" />
            </i>
            {{ $t('agreeAdderssCompet') }}
        </p>
        <div class="content mt-4" @click="$router.push('/exchange/channel-in')">
            <div class="left">
                <div class="text-40">{{ $t('wangtRecharge') }}</div>
                <div class="text-32 mt-7">{{ $t('torecharge') }}<img src="../../assets/image/login/Vector.png" alt="" />
                </div>
            </div>
            <div class="right pic2">
                <img src="./img/rechargePic.png" alt="">
            </div>
        </div>
        <van-button class="w-full" style="margin-top:10px;" type="primary" @click="$router.push('/quotes/index')">{{
            $t('sure')
        }}</van-button>
    </div>
</template>

<script setup>
import Step from "./step.vue";
import { ref } from 'vue';
const agree1 = ref(true)
const agree2 = ref(true)

const agreeInfo = () => {
    agree1.value = !agree1.value
}

const agreeAddress = () => {
    agree2.value = !agree2.value
}

</script>

<style lang="scss" scoped>
.finish {
    padding: 16px;
    font-size: 13px;
    width: 100%;
    box-sizing: border-box;
}

.title {
    font-weight: 700;
    font-size: 26px;
    margin-top: 33px;
    text-align: center;
}

.title1 {
    font-weight: 700;
    font-size: 22px;
    text-align: center;
    margin-top: 22px;
}

p {
    display: flex;
    align-items: center;
    color: $text_color1;

    i {
        display: block;
        margin-right: 9px;
        box-sizing: border-box;

        img {
            width: 15px;
            height: 15px;
        }
    }
}

.content {
    background: $selectSymbol_background;
    border-radius: 4px;
    padding: 0 18px;
    display: flex;
    justify-content: space-between;
    height: 110px;
    align-items: center;
    color: $text_color;

    .left {
        img {
            width: 11px;
            height: 11px;
            margin-left: 6px;
        }
    }

    .pic1 {
        width: 70px;
        height: 76px;

        img {
            width: 100%;
            height: 100%;
        }
    }

    .pic2 {
        width: 76px;
        height: 66px;

        img {
            width: 100%;
            height: 100%;
        }
    }
}
</style>