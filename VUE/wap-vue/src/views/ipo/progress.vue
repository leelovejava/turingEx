<template>
    <div class="progress">
        <div class="header-box text-white">
            <van-nav-bar @click-left="onRoute(-1)">
                <template #left>
                    <div class="flex items-center">
                        <van-icon name="arrow-left" class="left-icon" />
                        <div class="left-div text-left ml-2">
                            <div class="title">{{ route.query?.name }}</div>
                            <div class="text">{{ route.query?.id }}</div>
                        </div>
                    </div>
                </template>
                <!-- <template #right>
                    <img class="search-icon" src="@/assets/image/optional/search.png" alt="">
                </template> -->
            </van-nav-bar>
            <div class="progress-div">
                <div class="title">{{t('预计开始交易时间')}}</div>
                <div class="text">{{t('待定')}}</div>
            </div>
        </div>
        <div class="px-5">
            <div class="flex py-5 justify-between ">
                <div class="flex-1">
                    <div class="textColor1">{{t('市盈率(净)')}}</div>
                    <div>{{ topData.peTtm || '--'  }}</div>
                </div>
                <div class="flex-1 text-center">
                    <div class="textColor1">{{t('净利率')}}</div>
                    <div class="">{{ topData.netProfitRatio || '--' }}</div>
                </div>
                <div class="flex-1 text-right">
                    <div class="textColor1">{{t('市销率')}}</div>
                    <div>{{ Number(topData.pbTtm).toFixed(2) || '--' }}</div>
                </div>
            </div>
            <div class="flex py-5 justify-between ">
                <div class="flex-1">
                    <div class="textColor1">{{t('市值')}}</div>
                    <div>{{ topData.skMarketCap || '--'  }}</div>
                </div>
                <div class="flex-1 text-center">
                    <div class="textColor1">{{t('所属行业')}}</div>
                    <div class="">{{ topData.belongIndustry || '--'  }}</div>
                </div>
                <div class="flex-1"></div>
            </div>
            <div class="steps">
              <van-steps direction="vertical" :active="0">
                <van-step>
                 <div class="p-l">{{ topData.foundDate }}</div>
                 <div>{{t('递交招股书')}}</div>
                 <div class="examine-but mt-5">{{t('查看招股书')}}></div>
                </van-step>
                <van-step>
                  <div class="p-l">{{t('待定')}}</div>
                    <div>{{t('开始申购')}}</div>
                    <div class="tip mt-3">{{t('申购区间价')}}: {{ Number(topData.bps).toFixed(2) + '~' + Number(topData.bps * 2).toFixed(2)}}</div>
                    <div class="tip mt-3">{{t('预计发行股本')}}：{{t('待定')}}</div>
                </van-step>
                <van-step>
                  <div class="p-l">{{t('待定')}}</div>
                    <div>{{t('开始申购')}}</div>
                    <div class="tip mt-3">{{t('发行价')}}：{{ Number(topData.bps * 1.5).toFixed(2) }}</div>
                    <div class="tip mt-3">{{t('发行后总股本')}}：{{t('待定')}}</div>
                </van-step>
                <van-step>
                    <div class="p-l">{{t('待定')}}</div>
                    <div>{{t('公布中签,上市')}}</div>
                </van-step>
            </van-steps>
            </div>
        </div>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from "vue-i18n";
import { itemSummary } from '@/service/ipo.api.js'
const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const topData = ref({})
onMounted(() => {
  getTopData()
})

const getTopData = () => {
  itemSummary({symbol: route.query?.id}).then((res )=> {
    topData.value = res
  })
}
const onRoute = (path) => {
  if (path === -1) {
    router.go(-1)
    return
  }
  router.push(path)
}
</script>
<style lang="scss" scoped>
:deep(.van-icon-arrow-left) {
  color: #fff !important;
}
.pt-3 {
    padding-top: 0 !important;
}

.progress {
    font-size: 14px;

    .header-box {
        height: 200px;
        padding-top: 10px;
        // background: linear-gradient(to bottom right, #6352BC, #2F74A9);
        background: linear-gradient(to bottom right, #6352BC, #2F74A9);
    }

    .search-icon {
        width: 23px;
        height: 23px;
    }

    .left-icon {
        font-size: 22px;
    }

    .left-div {
        .title {
            font-weight: bold;
            font-size: 16px;
        }

        .text {
            font-size: 13px;
        }
    }

    .progress-div {
        text-align: center;
        padding-top: 50px;

        .title {
            font-size: 13px;
        }

        .text {
            font-weight: bold;
            font-size: 16px;
        }
    }

    .steps{
      border-top: 1px solid $border_color;
      padding-top: 12px;
      padding-left: 75px;
    }

    :deep(.van-steps) {
        background: $van-step-g;
        overflow: inherit;

        .van-step__title {
            color: $text_color;

            .time {
                color: #747A8F;
                margin: 5px 0;
            }

        }

        .van-step__line {
            background-color: $step-border;
        }

        .van-icon-checked::before {
            content: '';
            background-color: $mainBgColor;
            width: 6px;
            height: 6px;
            box-shadow: 0 0 0px 4px $color_main;
            border-radius: 50%;
        }
        .van-step__circle {
            background-color: $van-step-b;
            width: 6px;
            height: 6px;
            box-shadow: 0 0 0px 4px $van-step-s;
        }

        .van-step--vertical:not(:last-child):after {
            border-bottom-width: 0px;
            border-color: $border_color;
        }

        .p-l{
          width: 83px;
          position: absolute;
          left: -110px;
          text-align: right;
        }
    }
    .examine-but{
        padding: 5px 10px;
        border: 1px solid $color_main;
        max-width: 100px;
        text-align: center;
        border-radius: 5px;
        color:  $color_main;
        font-size: 13px;
    }
    .tip{
        font-size: 13px;
        color: $text_color6;
    }
}
</style>