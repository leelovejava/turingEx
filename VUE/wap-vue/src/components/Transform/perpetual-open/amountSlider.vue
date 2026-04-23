<!-- 张数选择器 -->
<template>
    <div class="slider-index">
        <div class="amount-slider">
            <div class="amount-slider-clickable">
                <vue-slider @change="sliderAmountChange" class="mainBox" :marks="marks" v-model="sliderAmount"
                    :hide-label="true" width="92%" tooltip="hover" :tooltip-formatter="'{value}%'"
                    :railStyle="{ background: '#404040', height: '2px' }"
                    :processStyle="{ background: '#266BFF', height: '2px' }">
                    <template v-slot:step="{ active }">
                        <div :class="['custom-step', { active }]"></div>
                    </template>
                </vue-slider>
            </div>
            <div class="poecs">
                <span>0%</span>
                <span class="lins">25%</span>
                <span class="lins">50%</span>
                <span class="lins">75%</span>
                <span class="lins">100%</span>
            </div>
        </div>
    </div>
</template>
<script>
import { mapGetters } from "vuex";
import VueSlider from "vue-slider-component";

export default {
    name: "amountSlider",
    components: {
        VueSlider,
    },
    props: {
        maxAmount: {
            type: Number,
            default: 0, //可开或者可平的总数
        },
    },
    data() {
        return {
            amount: undefined, //用户输入的张数
            sliderAmount: "", //滑块的数量
            marks: (val) => val % 25 === 0,
        };
    },
    computed: {
        ...mapGetters(["existToken"]),
    },
    methods: {
        //输入框事件
        inputChange(val) {
            this.$emit("getAmount", val);
        },
        // 清除输入框
        cleanAmount() {
            this.amount = undefined;
        },
        //滑块事件
        sliderAmountChange(val) {
            console.log("滑块的值", val, this.maxAmount);
            let data;
            if (this.maxAmount) {
                if (val == 0) {
                    this.amount = undefined;
                } else {
                    const rate = val / 100; //如0.25
                    data = this.maxAmount * rate;
                    this.amount = parseInt(data);
                }
                this.$emit("getAmount", this.amount);
            }
        },
        //输入框amount变化
        amountChange(amount) {
            this.sliderAmount = ((amount / this.maxAmount) * 100).toFixed(2);
        },
        emptyValue() {
            this.sliderAmount = "";
        },
    },
};
</script>
<style scoped lang="scss">
/* 滑块 */
.mainBox {
    :deep(.vue-slider-dot-tooltip-inner) {
        background: #404040 !important;
        color: #fafafa !important;
    }

    :deep(.vue-slider-dot-tooltip-inner-top::after) {
        display: none;
    }

    :deep(.vue-slider-marks) {
        background: $border-grey;
    }

    :deep(.custom-step) {
        width: 16px;
        height: 16px;
        border-radius: 50%;
        /* 没有选中时候圈圈的颜色 */
        background: $mainbgWhiteColor;
        border: 1px solid #dddddd;
        margin-top: -6px;
    }
}

.custom-step.active {
    box-shadow: 0 0 0 3px $color_main;
    background-color: $color_main;
}

.poecs {
    margin-top: 16px;
    color: $text_color;
}

.poecs span.lins {
    display: inline-block;
    width: 22%;
    text-align: right;
}
</style>
  