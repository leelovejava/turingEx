<template>
    <div>
        <div class="steps">
            <div class="step" :class="step == 1 || step == 2 || step == 3 || step == 4 ?'green':'' ">1</div>
            <div class="line" :class="step == 1 || step == 2 || step == 3 || step == 4 ?'green':''"></div>
            <div class="step" :class="step == 2 || step == 3 || step == 4 ?'green':''">2</div>
            <div class="line" :class="step == 3 || step == 4 ?'green':''"></div>
            <div class="step" :class="step == 3 || step == 4 ?'green':''">3</div>
            <div class="line" :class="step == 4 ?'green':''"></div>
            <div class="step" :class="step == 4 ?'green':''">4</div>
        </div>
        <div class="stepsText textColor">
            <div>{{ $t('accountRegister') }}</div>
            <div>{{ $t('realNameVertify') }}</div>
            <div>{{ $t('safeBind') }}</div>
            <div>{{ $t('toExchange') }}</div>
        </div>
    </div>
</template>

<script setup>
const props = defineProps({
    step: {
        type: Number ,
        default: 1
    }
})
</script>

<style lang="scss" scoped>
.steps {
    display: flex;
    align-items: center;
    margin-top: 35px;
    padding-left: 10px;

    .step {
        width: 40px;
        height: 40px;
        line-height: 40px;
        text-align: center;
        font-size: 20px;
        color: $text_color;
        background:  $dark-grey;
        border-radius: 50%;
    }

    .line {
        width: 64px;
        height: 5px;
        background: $light-grey;
    }

    .green {
        background: $green;
    }
}

.stepsText {
    display: flex;
    padding-left: 10px;
    margin-top: 10px;

    div {
        text-align: center;
        width: 104px;

        &:nth-child(1) {
            width: 72px;
            text-align: left;
        }
        &:nth-child(4){
            width: 72px;
            text-align: right;
        }
    }
}
</style>